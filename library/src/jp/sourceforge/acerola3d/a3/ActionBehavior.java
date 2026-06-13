package jp.sourceforge.acerola3d.a3;

import java.io.Serializable;
import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import jp.sourceforge.acerola3d.a3.A3Object.BalloonDir;
import jp.sourceforge.acerola3d.sound.*;

/*
 * Action3DのためのBehavior。かなりのコードをこのBehavior
 * に移動して一括して管理することにした。スレッド関係のなんだかんだも、
 * このクラスで全て受け持つことにする。
 */
class ActionBehavior extends Behavior implements Cloneable {
    static BoundingSphere bounding = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);
    //playStatusに入れる状態
    static final int S0=0;//まだスタートしてない
    static final int S1=1;//スタートしてセーニョ前
    static final int S2=2;//セーニョ後のメインループ中
    static final int S3=3;//ダルセーニョ後だがストップ前
    static final int S4=4;//ストップ状態
    //static final int ACTION_QUEUE_SIZE = 3;
    static final int ACTION_QUEUE_SIZE = 10;

    //  現在のアクションNO
    int actionNo = 0;
    boolean immediatelyFlag = false;
    int playCount = 0;
    int playStatus = S0;
    boolean loopFlag;
    ArrayList<Integer> actionQueue = new ArrayList<Integer>();
    Action3DData a3Data;
    BranchGroup allBranchGroup = null;
    A3VirtualUniverse universe = null;

    //BVHのrootの骨の値をTransformGroupではなく
    //Action3Dオブジェクトそのものの変換として設定する場合true
    boolean transControlUsingRootBone = false;
    
    long motionLength;
    int elapsedTime;
    Motion.Mode mode = Motion.Mode.PLAY;
    long timeOffset = 0;
    long pauseTime;
    Action3D a3;
    long segnoTime;
    long dalsegnoTime;
    long epilogueStartTime;//最後のフレーズを開始する時間
    long epilogueEndTime;//最終的にアクションが終了する時間

    double timeD;//現在再生中のモーションの開始からの時間(segnoとかも考慮済みの時間)

    public ActionBehavior(Action3D action3d,Action3DData action3DData) {
        a3 = action3d;
        a3Data = action3DData;
        a3Data.construct3DNode(a3);

        actionNo = 0;
        resetParameters();

        this.setSchedulingBounds(bounding);
        allBranchGroup = new BranchGroup();
        allBranchGroup.addChild(a3Data.getNode());
        allBranchGroup.addChild(this);
    }
    void setA3VirtualUniverse(A3VirtualUniverse u) {
        universe = u;
    }
    void resetBalloonParameters() {
        if (a3.balloon!=null) {
            double x = a3Data.actions[actionNo].rightBalloonOffset[0];
            double y = a3Data.actions[actionNo].rightBalloonOffset[1];
            a3.balloon.setOffset(A3Object.BalloonDir.RIGHT,x,y);
            x = a3Data.actions[actionNo].leftBalloonOffset[0];
            y = a3Data.actions[actionNo].leftBalloonOffset[1];
            a3.balloon.setOffset(A3Object.BalloonDir.LEFT,x,y);
            x = a3Data.actions[actionNo].topBalloonOffset[0];
            y = a3Data.actions[actionNo].topBalloonOffset[1];
            a3.balloon.setOffset(A3Object.BalloonDir.TOP,x,y);
            x = a3Data.actions[actionNo].bottomBalloonOffset[0];
            y = a3Data.actions[actionNo].bottomBalloonOffset[1];
            a3.balloon.setOffset(A3Object.BalloonDir.BOTTOM,x,y);
            a3.balloon.direction = a3Data.actions[actionNo].balloonDirection;
        }        
    }
    void resetLabelParameters() {
        if (a3.label!=null) {
            double x = a3Data.actions[actionNo].labelOffset[0];
            double y = a3Data.actions[actionNo].labelOffset[1];
            a3.label.setOffset(x,y);
        }
    }
    void resetSoundLocAndQuat() {
        Vector3d loc = a3.getTargetLoc();
        Quat4d quat = a3.getTargetQuat();
        a3Data.actions[actionNo].setSoundLocQuat(loc,quat);
    }
    void resetParameters() {
        playCount = 0;
        playStatus = S0;
        immediatelyFlag = false;
        timeOffset = System.currentTimeMillis();
        loopFlag = a3Data.actions[actionNo].loopFlag;
        motionLength = (long)(a3Data.actions[actionNo].getMotionLength()*1000.0);
        elapsedTime = (int)(a3Data.actions[actionNo].motion.getDefaultFrameTime()*1000.0);
        segnoTime = (long)(a3Data.actions[actionNo].segno*1000.0);
        dalsegnoTime = (long)(a3Data.actions[actionNo].dalsegno*1000.0);
        if (dalsegnoTime<0)
            dalsegnoTime=motionLength;
        if (loopFlag) {
            epilogueStartTime=Long.MAX_VALUE;
            epilogueEndTime=Long.MAX_VALUE;
        } else {
            epilogueStartTime=dalsegnoTime;
            epilogueEndTime=motionLength;
        }
        resetBalloonParameters();
        resetLabelParameters();
        resetSoundLocAndQuat();
    }
    void doChangeAction(int newActionNo) {
        a3Data.actions[actionNo].stop();
        actionNo = newActionNo;
        a3Data.change(actionNo);
        a3Data.actions[actionNo].start();
        resetParameters();
        if (a3.isSelected3D()) {
            a3.behavior.adjustSelected3D();
        }
    }
    void init() {
        synchronized (actionQueue) {
            if (actionQueue.size()>0) {
                int newActionNo = actionQueue.remove(0);
                doChangeAction(newActionNo);
            }
        }
    }
    public void initialize() {
        if (elapsedTime==0) //<-- なんでこれが必要なのか
            return;         //<-- わからない
        timeOffset = System.currentTimeMillis();
        WakeupOnElapsedTime w = new WakeupOnElapsedTime(1);
        wakeupOn(w);
        //processStimulus(null);//これをやっちゃうと着せ替え機能で問題あり
    }
    @SuppressWarnings("unchecked")
    public void processStimulus(Enumeration criteria) {
        synchronized (actionQueue) {
            if (universe==null) {
                WakeupOnElapsedTime w = new WakeupOnElapsedTime(elapsedTime);
                wakeupOn(w);
            } else {
                WakeupOnBehaviorPost w = null;
                w = new WakeupOnBehaviorPost(universe.getTimerBehavior(),1);
                wakeupOn(w);
            }

            //autoActionControlの処理
            if (a3Data.autoActionControl==true) {
                double ls = a3.getSpeed();
                int i = -1;
                if (ls < a3Data.getMinWalkSpeed()) {
                    i = a3Data.getHaltActionNo();
                } else if (ls<a3Data.getMinRunSpeed()) {
                    i = a3Data.getWalkActionNo();
                } else {
                    i = a3Data.getRunActionNo();
                }
                if ((i!=actionNo)&&(actionQueue.size()==0)) {
                    actionQueue.add(i);
                }
            }
            //アクション切り替えの処理
            if (immediatelyFlag==true) {
                if (actionQueue.size()>0) {
                    int newActionNo = actionQueue.remove(0);
                    doChangeAction(newActionNo);
                }
            } else {
                if ((playStatus==S4)&&(actionQueue.size()>0)) {
                    int newActionNo = actionQueue.remove(0);
                    doChangeAction(newActionNo);
                }
            }

            //アクションがはじまってからの経過時間を計算する
            //ポーズモードの時は特別扱い
            long time;
            long nowTime = System.currentTimeMillis();
            if (mode == Motion.Mode.PAUSE) {
                epilogueStartTime=dalsegnoTime;
                epilogueEndTime=motionLength;
                time = pauseTime;
            } else {
                time = nowTime - timeOffset;
            }

            //actionQueueにデータが入ってきた時点で終了のタイミングが計算される
            if ((epilogueStartTime==Long.MAX_VALUE)&&(actionQueue.size()>0)) {
                epilogueStartTime = time + (dalsegnoTime - segnoTime) - ((time -segnoTime) % (dalsegnoTime - segnoTime));
                epilogueEndTime = epilogueStartTime+(motionLength-dalsegnoTime);
            }

            //時間から現在の状態を決定
            int newPlayStatus;
            if (time<segnoTime)
                newPlayStatus=S1;
            else if (time<epilogueStartTime)
                newPlayStatus=S2;
            else if (time<epilogueEndTime)
                newPlayStatus=S3;
            else
                newPlayStatus=S4;

            //終了状態が2回続いていれば以下の処理は省略する
            //ただし、Marionetteのようなやつは省略不可
            if ((playStatus==S4)&&(newPlayStatus==S4)&&(motionLength>0))
                    return;

            //現在の再生回数を計算
            int newPlayCount = (int)((time-segnoTime)/(dalsegnoTime-segnoTime));

            //モーションキャプチャデータにおける時間に変換
            if (newPlayStatus==S1) {
                ;
            } else if (newPlayStatus==S2) {
                time = segnoTime + (time-segnoTime)%(dalsegnoTime-segnoTime);
            } else if (newPlayStatus==S3) {
                time = dalsegnoTime + (time-epilogueStartTime);
            } else if (newPlayStatus==S4) {
                time = motionLength-1;
            }

            //実際に骨を動かす
            timeD = ((double)time)/1000.0;
            Action3DData.Action a = a3Data.actions[actionNo];
            String rootBoneName = a.motion.getRootBone();
            for (String boneName : a.tgMap.keySet()) {
                if ((transControlUsingRootBone)&&(boneName.equals(rootBoneName))) {
                    Transform3D t = a.motion.getTransform3D(boneName,timeD);
                    if (t!=null) {
                        a3.setTransformImmediately(t);
                        //下の1行、設計的に非常に良くないけどどうしてもここで同期を取っておきたいので入れてある
                        a3.behavior.setNow();
                    }
                } else {
                    TransformGroup tg = a.tgMap.get(boneName);
                    Transform3D t = a.motion.getTransform3D(boneName,timeD);
                    if (t!=null) {
                        try {
                            tg.setTransform(t);
                        } catch(BadTransformException e) {
                            System.out.println("BadTransformException in ActionBehavior.processStimulus().");
                            //e.printStackTrace();
                        }
                    }
                }
            }
            //3Dセレクトマーカーのアジャストメント
            if (a3.isSelected3D()) {
                a3.behavior.adjustSelected3D();
            }

            //再生後初めてのサウンド再生
            //((newPlayStatus==4)&&(playStatus==0))==trueのような場合も再生する
            if ((newPlayStatus>=S2)&&(playStatus<S2)) {
                if (a3Data.actions[actionNo].sound!=null) {
                    //a3Data.actions[actionNo].sound.setEnable(false);
                    a3Data.actions[actionNo].soundStart();
                }
            }
            //S2状態で2回目以降のサウンド再生
            if ((newPlayStatus==S2)&&(playCount!=newPlayCount)) {
                if (a3Data.actions[actionNo].sound!=null) {
                    //a3Data.actions[actionNo].sound.setEnable(false);
                    a3Data.actions[actionNo].soundStart();
                }
            }
            //soundContinue==falseの場合はアクション終了でサウンドも終了
            if ((newPlayStatus==S4)&&(!a3Data.actions[actionNo].soundContinue)) {
                if (a3Data.actions[actionNo].sound!=null) {
                    a3Data.actions[actionNo].soundStop();
                }
            }

            //状態と再生回数の保存
            playStatus = newPlayStatus;
            playCount = newPlayCount;
        }
    }

    void initAction(int initActionNo) {
        synchronized (actionQueue) {
            //gaha?
            if ((initActionNo<0)||(initActionNo>=a3Data.actions.length))
                initActionNo=0;
            actionQueue.clear();
            actionQueue.add(initActionNo);
        }
    }
    void initAction(Serializable s) {
        if (s instanceof Integer)
            initAction(((Integer)s).intValue());
        if (s instanceof String)
            initAction(a3Data.getActionNoFromActionName((String)s));
    }
    void change(int newActionNo) {
        synchronized (actionQueue) {
            //gaha?
            if ((newActionNo<0)||(newActionNo>=a3Data.actions.length))
                return;
            if (a3.component2DContainer==null)
                doChangeAction(newActionNo);
            if (actionQueue.size()<ACTION_QUEUE_SIZE)
                actionQueue.add(newActionNo);
            else
                actionQueue.set(ACTION_QUEUE_SIZE-1,newActionNo);
        }
    }
    void change(Serializable s) {
        if (s instanceof Integer)
            change(((Integer)s).intValue());
        if (s instanceof String)
            change(a3Data.getActionNoFromActionName((String)s));
    }
    void changeImmediately(int newActionNo) {
        synchronized (actionQueue) {
            //gaha?
            if ((newActionNo<0)||(newActionNo>=a3Data.actions.length))
                return;
            if (a3.component2DContainer==null)
                doChangeAction(newActionNo);
            actionQueue.clear();
            actionQueue.add(newActionNo);
            immediatelyFlag = true;
        }
    }
    void changeImmediately(Serializable s) {
        if (s instanceof Integer)
            changeImmediately(((Integer)s).intValue());
        if (s instanceof String)
            changeImmediately(a3Data.getActionNoFromActionName((String)s));
    }
    

    public void setFrameTime(double d) {
        elapsedTime = (int)(1000.0*d);
    }

    public void setPauseTime(double t) {
        pauseTime = (long)(1000.0*t);
    }

    public double getTime() {
        return timeD;
    }

    public void setMode(Motion.Mode m) {
        mode = m;
    }
    public boolean isStoped() {
        return ((loopFlag==false)&&(playCount>0));
    }
    public void dispose() {
        a3Data.actions[actionNo].soundStop();
    }

//-------------------------------
    Node getNode() {
        return allBranchGroup;
    }

    int getActionNo() {
        return actionNo;
    }
    String getActionName() {
        return a3Data.actions[actionNo].actionName;
    }

/* GAHA
    String getActionNameFromActionNo(int an) {
        return a3Data.actions[an].actionName;
    }
*/
    int getActionNoFromActionName(String an) {
        return a3Data.getActionNoFromActionName(an);
    }

    int getActionCount() {
        return a3Data.actions.length;
    }

    String[] getActionNames() {
        return (String[])a3Data.actionNames.clone();
    }

    boolean isStoped(int actionNo) {
        return a3Data.actions[actionNo].isStoped();
    }

    double getMotionLength(int actionNo) {
        return a3Data.actions[actionNo].getMotionLength();
    }

    double getMotionLength(String actionName) {
        int actionNo = getActionNoFromActionName(actionName);
        return a3Data.actions[actionNo].getMotionLength();
    }

    String getActionName(int i) {
        return a3Data.actions[i].actionName;
    }

    String getComment() {
        return a3Data.comment;
    }

    int getFrameCount() {
        return a3Data.actionNames.length;
    }

    void setAutoActionControl(boolean b) {
        a3Data.autoActionControl = b;
    }
    boolean autoActionControl() {
        return a3Data.autoActionControl;
    }

    void setHaltAction(int i) {
        if ((i<0)||(i>=a3Data.actions.length))
            return;
        a3Data.haltActionNo = i;
    }
    void setHaltAction(Serializable s) {
        if (s instanceof String) {
            setHaltAction(getActionNoFromActionName((String)s));
        } else if (s instanceof Integer) {
            setHaltAction(((Integer)s).intValue());
        }
    }
    int getHaltActionNo() {
        return a3Data.haltActionNo;
    }

    void setWalkAction(int i) {
        if ((i<0)||(i>=a3Data.actions.length))
            return;
        a3Data.walkActionNo = i;
    }
    void setWalkAction(Serializable s) {
        if (s instanceof String) {
            setWalkAction(getActionNoFromActionName((String)s));
        } else if (s instanceof Integer) {
            setWalkAction(((Integer)s).intValue());
        }
    }
    int getWalkActionNo() {
        return a3Data.walkActionNo;
    }

    void setMinWalkSpeed(double d) {
        a3Data.minWalkSpeed = d;
    }
    double getMinWalkSpeed() {
        return a3Data.minWalkSpeed;
    }
    String getRDF() {
        return a3Data.rdf;
    }
    String[] getTags() {
        return a3Data.tags.clone();
    }
    String[] getProfiles() {
        return a3Data.profiles.clone();
    }
    String[] getThumbnails() {
        return a3Data.thumbnails.clone();
    }
    String getHTML () {
        return a3Data.html;
    }

    void setRunAction(int i) {
        if ((i<0)||(i>=a3Data.actions.length))
            return;
        a3Data.runActionNo = i;
    }
    void setRunAction(Serializable s) {
        if (s instanceof String) {
            setRunAction(getActionNoFromActionName((String)s));
        } else if (s instanceof Integer) {
            setRunAction(((Integer)s).intValue());
        }
    }
    int getRunActionNo() {
        return a3Data.runActionNo;
    }

    void setMinRunSpeed(double d) {
        a3Data.minRunSpeed = d;
    }
    double getMinRunSpeed() {
        return a3Data.minRunSpeed;
    }

//-------------------------------

    void setMotion(int actionNo,Motion motion) {
        a3Data.actions[actionNo].motion = motion;
    }
    void setMotion(Serializable action,Motion motion) {
        int actionNo=0;
        if (action instanceof String)
            actionNo = a3Data.getActionNoFromActionName((String)action);
        if (action instanceof Integer)
            actionNo = ((Integer)action).intValue();
        setMotion(actionNo,motion);
    }
    Motion getMotion(int actionNo) {
        return a3Data.actions[actionNo].motion;
    }
    Motion getMotion(Serializable action) {
        int actionNo=0;
        if (action instanceof String)
            actionNo = a3Data.getActionNoFromActionName((String)action);
        if (action instanceof Integer)
            actionNo = ((Integer)action).intValue();
        return getMotion(actionNo);
    }
    double getActionScale(int actionNo) {
        return a3Data.actions[actionNo].scale;
    }
    double getActionScale(Serializable action) {
        int actionNo=0;
        if (action instanceof String)
            actionNo = a3Data.getActionNoFromActionName((String)action);
        if (action instanceof Integer)
            actionNo = ((Integer)action).intValue();
        return getActionScale(actionNo);
    }
    Vector3d getActionOffset(int actionNo) {
        if (a3Data.actions[actionNo].offsetXYZ == null)
            return new Vector3d();
        else
            return new Vector3d(a3Data.actions[actionNo].offsetXYZ);
    }
    Vector3d getActionOffset(Serializable action) {
        int actionNo=0;
        if (action instanceof String)
            actionNo = a3Data.getActionNoFromActionName((String)action);
        if (action instanceof Integer)
            actionNo = ((Integer)action).intValue();
        return getActionOffset(actionNo);
    }
    Vector3d getActionRot(int actionNo) {
        if (a3Data.actions[actionNo].rotationXYZ == null)
            return new Vector3d();
        else
            return new Vector3d(a3Data.actions[actionNo].rotationXYZ);
    }
    Vector3d getActionRot(Serializable action) {
        int actionNo=0;
        if (action instanceof String)
            actionNo = a3Data.getActionNoFromActionName((String)action);
        if (action instanceof Integer)
            actionNo = ((Integer)action).intValue();
        return getActionRot(actionNo);
    }
    void setShape(int actionNo,String boneName,Node shape) {
        a3Data.setShape(actionNo,boneName,shape);
    }
    void setShape(Serializable action,String boneName,Node shape) {
        a3Data.setShape(action,boneName,shape);
    }
    /**
     * このAction3Dで使用されている全てのShapeのファイル名を
     * 返す．同じShapeが異なるBoneやActionで共有されている場合でも，
     * 返り値は重複しないようになっている．
     */
    String[] getShapeFilenames() {
        return a3Data.getShapeFilenames();
    }

    /**
     * このAction3Dで使用されていShapeをファイル名で指定し，
     * そのShapeを引数で指定されたJava3DのNodeに変更します．
     * 変更前のShapeが異なるBoneやActionで共有されている場合には
     * その全てをnodeに置き換えます．この置き換えにはSharedGroupと
     * Linkを使った実装が使われます．
     */
    void replaceShape(String shapeFilename,Node node) {
        a3Data.replaceShape(shapeFilename,node);
    }

    /**
     * このAction3Dで使用されていShapeをファイル名で指定し，
     * そのShapeを引数で指定されたURLから生成されるAction3Dの
     * getNode()メソッドで得られるNodeで置き換えます．
     * 変更前のShapeが異なるBoneやActionで共有されている場合には
     * その全てを置き換えます．この置き換えの実装では，内部で
     * 置き換えが必要なShapeの数だけAction3DがnewされてShapeごとに
     * 独立したAction3Dがセットされます．これらの複数のAction3Dの
     * Actionのchangeを一括して行うためにはshapeChange()メソッドを
     * 使用して下さい．
     */
    void replaceShapeWithA3(String shapeFilename,String a3url) throws Exception {
        a3Data.replaceShapeWithA3(shapeFilename,a3url);
    }

    /**
     * ファイル名で指定したShapeがreplaceShapeWithA3()で変更されている
     * 場合に，そのメソッドの中で生成される全てのAction3Dのアクションを
     * 一括で変更します．
     */
    void shapeChange(String shapeFilename,String actionName) {
        a3Data.shapeChange(shapeFilename,actionName);
    }
    void setSound(int actionNo,MediaContainer sound) {
        ;
    }
    void setSound(Serializable action,MediaContainer sound) {
        ;
    }
    /**
     * 現在選択されているアクションのサウンドの音量を設定します。
     */
    public void setSoundGain(double g) {
        a3Data.actions[actionNo].setSoundGain(g);
    }

    /**
     * actionNoで指定したアクションのサウンドの音量を設定します。
     */
    public void setSoundGain(int an,double g) {
        a3Data.actions[an].setSoundGain(g);
    }

    /**
     * actionで指定したアクションのサウンドの音量を設定します。
     */
    public void setSoundGain(Serializable action,double g) {
        int actionNo=0;
        if (action instanceof String)
            actionNo = a3Data.getActionNoFromActionName((String)action);
        if (action instanceof Integer)
            actionNo = ((Integer)action).intValue();
        a3Data.actions[actionNo].setSoundGain(g);
    }

    /**
     * 全てのアクションのサウンドの音量を設定します。
     */
    public void setAllSoundGain(double g) {
        for (Action3DData.Action a : a3Data.actions) {
            a.setSoundGain(g);
        }
    }

    /**
     * 現在選択されているアクションのサウンドの音量を取得します。
     */
    public double getSoundGain() {
        return a3Data.actions[actionNo].getSoundGain();
    }

    /**
     * actionNoで指定したアクションのサウンドの音量を取得します。
     */
    public double getSoundGain(int an) {
        return a3Data.actions[an].getSoundGain();
    }

    /**
     * actionで指定したアクションのサウンドの音量を取得します。
     */
    public double getSoundGain(Serializable action) {
        int actionNo=0;
        if (action instanceof String)
            actionNo = a3Data.getActionNoFromActionName((String)action);
        if (action instanceof Integer)
            actionNo = ((Integer)action).intValue();
        return a3Data.actions[actionNo].getSoundGain();
    }
//----------------------------------------
    public void setLabelLoc(double x,double y) {
        a3Data.actions[actionNo].labelOffset[0] = x;
        a3Data.actions[actionNo].labelOffset[1] = y;
    }
    public void setLabelLoc(int actionNo,double x,double y) {
        a3Data.actions[actionNo].labelOffset[0] = x;
        a3Data.actions[actionNo].labelOffset[1] = y;
    }
    public void getLabelLoc(int actionNo,double ret[]) {
        ret[0] = a3Data.actions[actionNo].labelOffset[0];
        ret[1] = a3Data.actions[actionNo].labelOffset[1];
    }
    public void setBalloonLoc(BalloonDir d,double x,double y) {
        a3Data.actions[actionNo].balloonDirection = d;
        if (d==A3Object.BalloonDir.RIGHT) {
            a3Data.actions[actionNo].rightBalloonOffset[0] = x;
            a3Data.actions[actionNo].rightBalloonOffset[1] = y;
        } else if (d==A3Object.BalloonDir.LEFT) {
            a3Data.actions[actionNo].leftBalloonOffset[0] = x;
            a3Data.actions[actionNo].leftBalloonOffset[1] = y;
        } else if (d==A3Object.BalloonDir.TOP) {
            a3Data.actions[actionNo].topBalloonOffset[0] = x;
            a3Data.actions[actionNo].topBalloonOffset[1] = y;
        } else if (d==A3Object.BalloonDir.BOTTOM) {
            a3Data.actions[actionNo].bottomBalloonOffset[0] = x;
            a3Data.actions[actionNo].bottomBalloonOffset[1] = y;
        }
    }
    public void setBalloonLoc(int actionNo,BalloonDir d,double x,double y) {
        a3Data.actions[actionNo].balloonDirection = d;
        if (d==A3Object.BalloonDir.RIGHT) {
            a3Data.actions[actionNo].rightBalloonOffset[0] = x;
            a3Data.actions[actionNo].rightBalloonOffset[1] = y;
        } else if (d==A3Object.BalloonDir.LEFT) {
            a3Data.actions[actionNo].leftBalloonOffset[0] = x;
            a3Data.actions[actionNo].leftBalloonOffset[1] = y;
        } else if (d==A3Object.BalloonDir.TOP) {
            a3Data.actions[actionNo].topBalloonOffset[0] = x;
            a3Data.actions[actionNo].topBalloonOffset[1] = y;
        } else if (d==A3Object.BalloonDir.BOTTOM) {
            a3Data.actions[actionNo].bottomBalloonOffset[0] = x;
            a3Data.actions[actionNo].bottomBalloonOffset[1] = y;
        }
    }
    public void setBalloonLoc(Serializable action,BalloonDir d,double x,double y) {
        if (action instanceof Integer) {
            int actionNo = ((Integer)action).intValue();
            setBalloonLoc(actionNo,d,x,y);
        } else if (action instanceof String) {
            int actionNo = a3Data.getActionNoFromActionName((String)action);
            setBalloonLoc(actionNo,d,x,y);
        }
    }
    public void setBalloonLocAll(BalloonDir d,double x,double y) {
        for (int actionNo=0;actionNo<a3Data.actions.length;actionNo++) {
            if (d==A3Object.BalloonDir.RIGHT) {
                a3Data.actions[actionNo].rightBalloonOffset[0] = x;
                a3Data.actions[actionNo].rightBalloonOffset[1] = y;
            } else if (d==A3Object.BalloonDir.LEFT) {
                a3Data.actions[actionNo].leftBalloonOffset[0] = x;
                a3Data.actions[actionNo].leftBalloonOffset[1] = y;
            } else if (d==A3Object.BalloonDir.TOP) {
                a3Data.actions[actionNo].topBalloonOffset[0] = x;
                a3Data.actions[actionNo].topBalloonOffset[1] = y;
            } else if (d==A3Object.BalloonDir.BOTTOM) {
                a3Data.actions[actionNo].bottomBalloonOffset[0] = x;
                a3Data.actions[actionNo].bottomBalloonOffset[1] = y;
            }
        }
    }
    public void getBalloonLoc(BalloonDir d,double ret[]) {
        getBalloonLoc(actionNo,d,ret);
    }
    public void getBalloonLoc(int actionNo,BalloonDir d,double ret[]) {
        if (d==A3Object.BalloonDir.RIGHT) {
            ret[0] = a3Data.actions[actionNo].rightBalloonOffset[0];
            ret[1] = a3Data.actions[actionNo].rightBalloonOffset[1];
        } else if (d==A3Object.BalloonDir.LEFT) {
            ret[0] = a3Data.actions[actionNo].leftBalloonOffset[0];
            ret[1] = a3Data.actions[actionNo].leftBalloonOffset[1];
        } else if (d==A3Object.BalloonDir.TOP) {
            ret[0] = a3Data.actions[actionNo].topBalloonOffset[0];
            ret[1] = a3Data.actions[actionNo].topBalloonOffset[1];
        } else if (d==A3Object.BalloonDir.BOTTOM) {
            ret[0] = a3Data.actions[actionNo].bottomBalloonOffset[0];
            ret[1] = a3Data.actions[actionNo].bottomBalloonOffset[1];
        }
    }
    public void setBalloonDir(BalloonDir d) {
        a3Data.actions[actionNo].balloonDirection = d;
    }
    public void setBalloonDir(int actionNo,BalloonDir d) {
        a3Data.actions[actionNo].balloonDirection = d;
    }
    public void setBalloonDir(Serializable action,BalloonDir d) {
        if (action instanceof Integer) {
            int actionNo = ((Integer)action).intValue();
            setBalloonDir(actionNo,d);
        } else if (action instanceof String) {
            int actionNo = a3Data.getActionNoFromActionName((String)action);
            setBalloonDir(actionNo,d);
        }
    }
    public void setBalloonDirAll(BalloonDir d) {
        for (int i=0;i<a3Data.actions.length;i++) {
            a3Data.actions[i].balloonDirection = d;
        }
    }
    void setSoundLocQuat(Vector3d v,Quat4d q) {
        a3Data.actions[actionNo].setSoundLocQuat(v,q);
    }
    void setSoundTypeLocked() {
        for (Action3DData.Action a : a3Data.actions) {
            if (a.sound instanceof A3Sound) {
                ((A3Sound)(a.sound)).setType(A3SoundType.BackgroundSound);
            }
        }
    }

    void transControlUsingRootBone(boolean b) {
        transControlUsingRootBone = b;
    }
}
