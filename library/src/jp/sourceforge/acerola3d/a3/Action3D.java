package jp.sourceforge.acerola3d.a3;

import java.io.*;
import java.net.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;
import jp.sourceforge.acerola3d.A23;
import jp.sourceforge.acerola3d.SoftRefMap;


/**
 * アクションデータを含む3Dオブジェクトのクラス。
 * ユーザはこのクラスを介してアクションの切り替えなどの操作ができます。
 * 通常はコンストラクタを使用せずに、Action3D.load()からインスタンスを
 * 生成して使います。
 * 
 */
public class Action3D extends A3Object implements ActionObject {
    static SoftRefMap<URL,Action3DData> action3DDataCash = new SoftRefMap<URL,Action3DData>();

    static void initAction3D() {
        Action3DData.initAction3DData();
    }

    /**
     * URL文字列が示すAcerola3Dファイルを読み込みAction3D
     * のインスタンスを生成します。
     */
    public synchronized static Action3D load(String a3URL) {
        A23.initA23();
        Action3D tmpA3 = null;
        try {
            tmpA3 = load(new URL(a3URL));
        } catch(Exception e) {
            e.printStackTrace();
            tmpA3 = load("x-res:///jp/sourceforge/acerola3d/resources/error.a3");
        }
        return tmpA3;
    }

    /**
     * URLオブジェクトが示すAcerola3Dファイルを読み込みAction3D
     * のインスタンスを生成します。
     */
    public synchronized static Action3D load(URL a3URL) {
        A23.initA23();
        initAction3D();

        A3InitData initData = new A3InitData("jp.sourceforge.acerola3d.a3.Action3D");
        initData.set(a3URL,-1);
        Action3D a3 = new Action3D(initData);
        return a3;
    }

    
//  --------------------------------------------------------------------------------
//  --------------------------------------------------------------------------------
//  --------------------------------------------------------------------------------
    
    ActionBehavior actionBehavior;

    /**
     * A3InitDataをもとにAction3Dオブジェクトを生成するコンストラクタ。
     * A3InitDataの引数の数は、2か5か7か8でなければなりません。
     * 引数が5か7の時は自動的にautoActionControl=trueとなります。
     *
     * <table border="1" summary="required data in A3InitData">
     * <tr><td>0:String      </td><td>URL of Acerola3D</td><td>必須</td></tr>
     * <tr><td>1:Serializable</td><td>ActionID        </td><td>必須</td></tr>
     * <tr><td>2:Serializable</td><td>haltActionID </td><td>任意</td></tr>
     * <tr><td>3:Serializable</td><td>walkActionID    </td><td>任意</td></tr>
     * <tr><td>4:Serializable</td><td>runActionID     </td><td>任意</td></tr>
     * <tr><td>5:double      </td><td>walkSpeed       </td><td>任意</td></tr>
     * <tr><td>6:double      </td><td>runSpeed        </td><td>任意</td></tr>
     * <tr><td>7:boolean     </td><td>billboard       </td><td>任意</td></tr>
     * </table>
     */
    public Action3D(A3InitData d) {
        super(d);
        realConstructor(d);
    }
    /**
     * Action3DファイルのURL文字列を引数とするAction3Dオブジェクトの
     * コンストラクタ。このコンストラクタで指定できるのはURLだけなので
     * 他の設定はオブジェクト生成後に各種メソッドで指定して下さい。
     */
    public Action3D(String a3URL) {
        super(initHack());
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.Action3D");
        d.set(a3URL,-1);
        realConstructor(d);
    }
    /**
     * Action3Dファイルを指すURLオブジェクトを引数とするAction3Dオブジェクトの
     * コンストラクタ。このコンストラクタで指定できるのはURLだけなので
     * 他の設定はオブジェクト生成後に各種メソッドで指定して下さい。
     */
    public Action3D(URL a3URL) {
        super(initHack());
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.Action3D");
        d.set(a3URL,-1);
        realConstructor(d);
    }
    static A3InitData initHack() {
        return new A3InitData("jp.sourceforge.acerola3d.a3.Action3D");
    }
    void realConstructor(A3InitData d) {
        try {
            A23.initA23();
            initAction3D();
            URL url = null;
            if (d.get(0) instanceof String)
                url = new URL(d.getString(0));
            else if (d.get(0) instanceof URL)
                url = d.getURL(0);
            Action3DData action3DData = action3DDataCash.get(url);
            if (action3DData==null) {
                action3DData = new Action3DData(url);
                action3DDataCash.put(url,action3DData);
            }
            if (action3DData.billboardControl) {
                setEnableBehavior(true);
                setBillboardControl(true);
            }
            actionBehavior = new ActionBehavior(this,action3DData.copy());
            setNode(actionBehavior.getNode());

            actionBehavior.initAction(d.get(1));
            if (d.getDataCount()>=5) {
                setHaltAction(d.get(2));
                setWalkAction(d.get(3));
                setRunAction(d.get(4));
                setAutoActionControl(true);
            }
            if (d.getDataCount()>=7) {
                setMinWalkSpeed(d.getDouble(5));
                setMinRunSpeed(d.getDouble(6));
            }
            if (d.getDataCount()>=8) {
                setBillboardControl(d.getBoolean(7));
            }
        } catch (Exception e) {
            e.printStackTrace();
            d = new A3InitData("jp.sourceforge.acerola3d.a3.Action3D");
            d.set("x-res:///jp/sourceforge/acerola3d/resources/error.a3",-1);
            realConstructor(d);
        }
    }

    protected void init() {
        super.init();
        actionBehavior.init();
    }

    /**
     * A3UpdateDataの情報をもとに現在の3Dオブジェクトの
     * 状態を更新します。
     *
     * A3UpdateDataにセットするデータは以下のようになります。
     *
     * <table border="1" summary="required data in A3UpdateData">
     * <tr><td>0:Serializable</td><td>ActionNo   </td><td>必須(変更なしの時は-1にする)</td></tr>
     * <tr><td>1:boolean     </td><td>immediately</td><td>必須</td></tr>
     * </table>
     */
    public void update(A3UpdateData d) {
        super.update(d);
        if (d.getBoolean(1))
            changeImmediately(d.get(0));
        else
            change(d.get(0));
    }

    /**
     * 指定したアクションナンバーのアクションに切りかえます。
     * 実際には、アクションの切りかえ要求を待ち行列に追加し、
     * 順番がまわってきたら、指定したアクションが実行されます。
     */
    public void change(int newActionNo) {
        actionBehavior.change(newActionNo);
    }

    /**
     * 指定したアクションに切りかえます。実際には、
     * アクションの切りかえ要求を待ち行列に追加し、順番が
     * まわってきたら、指定したアクションが実行されます。
     * 引数がStringであれば、アクション名と見なし、
     * Integerである場合はアクションナンバーであると見なし、
     * アクションを切り替えます。
     */
    public void change(Serializable s) {
        actionBehavior.change(s);
    }

    /**
     * 指定したアクションナンバーのアクションに即座に
     * 切りかえます。待ち行列に他のアクションの切りかえ要求が
     * 残っていても、それらを無視して指定したアクションに切りかえます。
     */
    public void changeImmediately(int newActionNo) {
        actionBehavior.changeImmediately(newActionNo);
    }

    /**
     * 指定したアクションに即座に切りかえます。待ち行列に
     * 他のアクションの切りかえ要求が残っていても、それらを
     * 無視して指定したアクションに切りかえます。
     * 引数がStringであれば、アクション名と見なし、
     * Integerである場合はアクションナンバーであると見なし、
     * アクションを切り替えます。
     */
    public void changeImmediately(Serializable s) {
        actionBehavior.changeImmediately(s);
    }

    /**
     * 指定したアクションナンバーのアクションに即座に
     * 切りかえます。待ち行列に他のアクションの切りかえ要求が
     * 残っていても、それらを無視して指定したアクションに切りかえます。
     */
    public void changeNow(int newActionNo) {
        actionBehavior.changeImmediately(newActionNo);
    }

    /**
     * 指定したアクションに即座に切りかえます。待ち行列に
     * 他のアクションの切りかえ要求が残っていても、それらを
     * 無視して指定したアクションに切りかえます。
     * 引数がStringであれば、アクション名と見なし、
     * Integerである場合はアクションナンバーであると見なし、
     * アクションを切り替えます。
     */
    public void changeNow(Serializable s) {
        actionBehavior.changeImmediately(s);
    }

    /**
     * このAction3Dに含まれるアクションの個数を返します。
     */
    public int getActionCount() {
        return actionBehavior.getActionCount();
    }

    /**
     * このAction3Dに含まれる全てのアクション
     * アクション名の配列を返します。
     */
    public String[] getActionNames() {
        return actionBehavior.getActionNames();
    }

    /**
     * 指定したアクションナンバーのアクションの
     * アクション名を返します。
     */
    public String getActionName(int i) {
        return actionBehavior.getActionName(i);
    }

    /**
     * 指定したアクション名に対応するアクションナンバーを返します。
     */
    public int getActionNo(String an) {
        return actionBehavior.getActionNoFromActionName(an);
    }

    /**
     * 現在設定されているアクションナンバーを返します。
     */
    public int getActionNo() {
        return actionBehavior.getActionNo();
    }

    /**
     * 現在設定されているアクション名を返します。
     */
    public String getActionName() {
        return actionBehavior.getActionName();
    }

    /**
     * このAction3Dを作成した時に読み込んだAcerola3D
     * ファイル中に記述されたコメント文字列を返します。
     */
    public String getComment() {
        return actionBehavior.getComment();
    }

    /**
     * 指定したアクションナンバーのアクションの再生
     * 時間を秒数で返します。
     */
    public double getMotionLength(int actionNo) {
        return actionBehavior.getMotionLength(actionNo);
    }

    /**
     * 指定したアクション名のアクションの再生
     * 時間を秒数で返します。
     */
    public double getMotionLength(String actionName) {
        return actionBehavior.getMotionLength(actionName);
    }

    /**
     * プレイモード(再生か停止か)を設定します。
     */
    public void setMode(Motion.Mode m) {
        actionBehavior.setMode(m);
    }

    /**
     * 停止している時に、時間を指定してその時の
     * 状態にする。
     */
    public void setPauseTime(double t) {
        actionBehavior.setPauseTime(t);
    }

    /**
     * 現在再生中のアクションの再生開始からの時間を
     * 取得します。
     */
    public double getTime() {
        return actionBehavior.getTime();
    }

    /**
     * オートアクションコントロールの機能の
     * ON、OFFを設定する。ONにすると移動の
     * スピードに応じてアクションが自動的に
     * 設定されるようになります。
     */
    public void setAutoActionControl(boolean b) {
        actionBehavior.setAutoActionControl(b);
    }

    /**
     * オートアクションコントロールが設定されているか
     * どうかを返します。
     */
    public boolean autoActionControl() {
        return actionBehavior.autoActionControl();
    }

    /**
     * 停止している時のアクションを設定します。
     * オートアクションコントロールが有効である時のみ意味を持ちます。
     */
    public void setHaltAction(int i) {
        actionBehavior.setHaltAction(i);
    }

    /**
     * 停止している時のアクションを設定します。
     * オートアクションコントロールが有効である時のみ意味を持ちます。
     */
    public void setHaltAction(Serializable s) {
        actionBehavior.setHaltAction(s);
    }

    /**
     * 停止している時のアクションのアクションナンバーを返します。
     */
    public int getHaltActionNo() {
        return actionBehavior.getHaltActionNo();
    }

    /**
     * 歩くアクションを設定します。
     * オートアクションコントロールが有効である時のみ意味を持ちます。
     */
    public void setWalkAction(int i) {
        actionBehavior.setWalkAction(i);
    }

    /**
     * 歩くアクションを設定します。
     * オートアクションコントロールが有効である時のみ意味を持ちます。
     */
    public void setWalkAction(Serializable s) {
        actionBehavior.setWalkAction(s);
    }

    /**
     * 歩くアクションのアクションナンバーを返します。
     */
    public int getWalkActionNo() {
        return actionBehavior.getWalkActionNo();
    }

    /**
     * 最低歩行速度を設定します。
     * オートアクションコントロールが有効で、かつ、
     * ここで指定した速度より小さい速度である時は
     * 止っていると判定して止っている時のアクションが
     * 自動的にセットされます。この最低歩行速度と、
     * 最低走行速度の間の速度の時には歩いていると
     * 判定して、歩いている時のアクションが自動的に
     * セットされます。
     * 単位はメートル毎秒(m/s)とします。
     */
    public void setMinWalkSpeed(double d) {
        actionBehavior.setMinWalkSpeed(d);
    }

    /**
     * 最低歩行速度を返します。
     * 単位はメートル毎秒(m/s)です。
     */
    public double getMinWalkSpeed() {
        return actionBehavior.getMinWalkSpeed();
    }

    /**
     * 走るアクションを設定します。
     * オートアクションコントロールが有効である時のみ意味を持ちます。
     */
    public void setRunAction(int i) {
        actionBehavior.setRunAction(i);
    }

    /**
     * 走るアクションを設定します。
     * オートアクションコントロールが有効である時のみ意味を持ちます。
     */
    public void setRunAction(Serializable s) {
        actionBehavior.setRunAction(s);
    }

    /**
     * 走るアクションのアクションナンバーを返します。
     */
    public int getRunActionNo() {
        return actionBehavior.getRunActionNo();
    }

    /**
     * 最低走行速度を設定します。
     * オートアクションコントロールが有効で、かつ、
     * ここで指定した速度より大きい速度である時は
     * 走っていると判定して走っている時のアクションが
     * 自動的にセットされます。最低歩行速度と、この
     * 最低走行速度の間の速度の時には歩いていると
     * 判定して、歩いている時のアクションが自動的に
     * セットされます。
     * 単位はメートル毎秒(m/s)とします。
     */
    public void setMinRunSpeed(double d) {
        actionBehavior.setMinRunSpeed(d);
    }

    /**
     * 最低走行速度を返します。
     * 単位はメートル毎秒(m/s)です。
     */
    public double getMinRunSpeed() {
        return actionBehavior.getMinRunSpeed();
    }

    /**
     * RDFの情報をStringで返します。
     */
    public String getRDF() {
        return actionBehavior.getRDF();
    }
    /**
     * タグの情報をStringの配列で返します。
     */
    public String[] getTags() {
        return actionBehavior.getTags();
    }
    /**
     * プロファイルのURI識別子の情報を
     * Stringの配列で返します。
     */
    public String[] getProfiles() {
        return actionBehavior.getProfiles();
    }
    /**
     * サムネール画像のURLを配列で返します。
     */
    public URL[] getThumbnails() {
        try {
            String u[] = actionBehavior.getThumbnails();
            if (u!=null) {
                URL ret[] = new URL[u.length];
                for (int i=0;i<u.length;i++)
                    ret[i] = new URL(u[i]);
                return ret;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Acerola3Dファイルの中に保存されている
     * HTMLファイルのURLを返します。
     */
    public URL getHTML() {
        try {
            String u = actionBehavior.getHTML();
            if (u!=null)
                return new URL(u);
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * モーションデータをアクションに設定。
     */
    public void setMotion(int actionNo,Motion motion) {
        actionBehavior.setMotion(actionNo,motion);
    }

    /**
     * モーションデータをアクションに設定。
     */
    public void setMotion(Serializable action,Motion motion) {
        actionBehavior.setMotion(action,motion);
    }

    /**
     * アクションに設定されているモーションデータを取得。
     */
    public Motion getMotion(int actionNo) {
        return actionBehavior.getMotion(actionNo);
    }

    /**
     * アクションに設定されているモーションデータを取得。
     */
    public Motion getMotion(Serializable action) {
        return actionBehavior.getMotion(action);
    }

    /**
     * CATALOG.XMLで指定されているアクションのスケール(scale)を
     * 返します．
     */
    public double getActionScale(int actionNo) {
        return actionBehavior.getActionScale(actionNo);
    }

    /**
     * CATALOG.XMLで指定されているアクションのスケール(scale)を
     * 返します．
     */
    public double getActionScale(Serializable action) {
        return actionBehavior.getActionScale(action);
    }

    /**
     * CATALOG.XMLで指定されているアクションのオフセット(offset)を
     * 返します．
     */
    public Vector3d getActionOffset(int actionNo) {
        return actionBehavior.getActionOffset(actionNo);
    }

    /**
     * CATALOG.XMLで指定されているアクションのオフセット(offset)を
     * 返します．
     */
    public Vector3d getActionOffset(Serializable action) {
        return actionBehavior.getActionOffset(action);
    }

    /**
     * CATALOG.XMLで指定されているアクションの回転(rot)を
     * 返します．
     */
    public Vector3d getActionRot(int actionNo) {
        return actionBehavior.getActionRot(actionNo);
    }

    /**
     * CATALOG.XMLで指定されているアクションの回転(rot)を
     * 返します．
     */
    public Vector3d getActionRot(Serializable action) {
        return actionBehavior.getActionRot(action);
    }

    /**
     * 現在再生中のアクションに含まれるBoneが世界座標系で
     * 見て，どのような変換で表示されているかを示すTransform3Dを
     * 返すメソッドです．
     */
    public Transform3D getWorldTransOfBone(String bone) {
        String actionName = this.getActionName();

        double actionScale = this.getActionScale(actionName);
        Vector3d actionOffset = this.getActionOffset(actionName);
        Vector3d actionRot = this.getActionRot(actionName);

        Motion motion = this.getMotion(actionName);
        ArrayList<String> al = new ArrayList<String>();
        String b = bone;
        while (b!=null) {
            al.add(0,b);
            b = motion.getParentBone(b);
        }
        String[] bones = al.toArray(new String[0]);

        Quat4d quat = new Quat4d();
        Vector3d loc = new Vector3d();
        double scale;

        Transform3D t2 = new Transform3D();//CATALOG.XMLの修正分
        t2.set(Util.euler2quat(actionRot),actionOffset,actionScale);

        Transform3D t1 = new Transform3D(this.getQuat(),this.getLoc(),this.getScale());
        Transform3D t3 = new Transform3D();
        double time = getTime();
        for (int i=0;i<bones.length;i++) {
            t3.mul(motion.getTransform3D(bones[i],time));
        }
        scale = t3.get(quat,loc);
        t3.set(quat,loc,scale/actionScale);//美しくない．．．
        t1.mul(t2);
        t1.mul(t3);

        return t1;
    }

    /**
     * Shapeをあるアクションのボーンに設定。
     * このメソッドが上手く機能するためには、このAction3Dオブジェクトが
     * まだA3CanvasInterfaceに追加されていないことと、引数で指定した
     * boneNameがCATALOG.XMLできちんと指定されていることが必要。
     */
    public void setShape(int actionNo,String boneName,Node shape) {
        actionBehavior.setShape(actionNo,boneName,shape);
    }

    /**
     * Shapeをあるアクションのボーンに設定。
     * このメソッドが上手く機能するためには、このAction3Dオブジェクトが
     * まだA3CanvasInterfaceに追加されていないことと、引数で指定した
     * boneNameがCATALOG.XMLできちんと指定されていることが必要。
     */
    public void setShape(Serializable action,String boneName,Node shape) {
        actionBehavior.setShape(action,boneName,shape);
    }

    /**
     * このAction3Dで使用されている全てのShapeのファイル名を
     * 返す．同じShapeが異なるBoneやActionで共有されている場合でも，
     * 返り値は重複しないようになっている．
     */
    public String[] getShapeFilenames() {
        return actionBehavior.getShapeFilenames();
    }

    /**
     * このAction3Dで使用されていShapeをファイル名で指定し，
     * そのShapeを引数で指定されたJava3DのNodeに変更します．
     * 変更前のShapeが異なるBoneやActionで共有されている場合には
     * その全てをnodeに置き換えます．この置き換えにはSharedGroupと
     * Linkを使った実装が使われます．なのでVRML.getNode()や
     * Action3D.getNode()は使えません。Util.loadVRML_B()を
     * 使用して下さ。
     */
    public void replaceShape(String shapeFilename,Node node) {
        actionBehavior.replaceShape(shapeFilename,node);
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
    public void replaceShapeWithA3(String shapeFilename,String a3url) throws Exception {
        actionBehavior.replaceShapeWithA3(shapeFilename,a3url);
    }

    /**
     * ファイル名で指定したShapeがreplaceShapeWithA3()で変更されている
     * 場合に，そのメソッドの中で生成される全てのAction3Dのアクションを
     * 一括で変更します．
     */
    public void shapeChange(String shapeFilename,String actionName) {
        actionBehavior.shapeChange(shapeFilename,actionName);
    }

    /**
     * Soundをアクションに設定。
     * まだ未実装。
     */
    public void setSound(int actionNo,MediaContainer sound) {
        actionBehavior.setSound(actionNo,sound);
    }

    /**
     * Soundをアクションに設定。
     * まだ未実装。
     */
    public void setSound(Serializable action,MediaContainer sound) {
        actionBehavior.setSound(action,sound);
    }

    /**
     * 現在選択されているアクションのサウンドの音量を設定します。
     */
    public void setSoundGain(double g) {
        actionBehavior.setSoundGain(g);
    }

    /**
     * actionNoで指定したアクションのサウンドの音量を設定します。
     */
    public void setSoundGain(int actionNo,double g) {
        actionBehavior.setSoundGain(actionNo,g);
    }

    /**
     * actionで指定したアクションのサウンドの音量を設定します。
     */
    public void setSoundGain(Serializable action,double g) {
        actionBehavior.setSoundGain(action,g);
    }

    /**
     * 全てのアクションのサウンドの音量を設定します。
     */
    public void setAllSoundGain(double g) {
        actionBehavior.setAllSoundGain(g);
    }

    /**
     * 現在選択されているアクションのサウンドの音量を取得します。
     */
    public double getSoundGain() {
        return actionBehavior.getSoundGain();
    }

    /**
     * actionNoで指定したアクションのサウンドの音量を取得します。
     */
    public double getSoundGain(int actionNo) {
        return actionBehavior.getSoundGain(actionNo);
    }

    /**
     * actionで指定したアクションのサウンドの音量を取得します。
     */
    public double getSoundGain(Serializable action) {
        return actionBehavior.getSoundGain(action);
    }

    /**
     * キャッシュに保存されたAction3Dデータを全て
     * 削除します。
     */
    public static void clearCash() {
        action3DDataCash.clear();
    }
    /**
     * 引数のURLから読み込まれたAction3Dデータの
     * キャッシュを削除します。
     */
    public static void clearCash(URL url) {
        action3DDataCash.remove(url);
    }
    /**
     * 引数のURL文字列から読み込まれたAction3Dデータの
     * キャッシュを削除します。
     */
    public static void clearCash(String url) {
        try {
            action3DDataCash.remove(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void dispose() {
        super.dispose();
        actionBehavior.dispose();
    }
//----------------------------------------
    /**
     * ラベルをセットします。(デスクトップのアイコンの
     * ファイル名のような感じで)表示されているラベルを
     * 消去したい場合には，引数にnullをわたして下さい．
     */
    public void setLabel(String l) {
        if (label==null) {
            label = new A3Label(l,this);
            actionBehavior.resetLabelParameters();//ポイント
        }
        if (l!=null) {
            label.setString(l);
            if (component2DContainer!=null)
                component2DContainer.add(label);
        } else {
            if (component2DContainer!=null)
                component2DContainer.del(label);
        }
    }

    /**
     * ラベルを表示する場合のオフセットを設定します。
     */
    public void setLabelLoc(double x,double y) {
        super.setLabelLoc(x,y);
        actionBehavior.setLabelLoc(x,y);
    }
    /**
     * アクションを指定してラベルを表示する場合のオフセットを設定します。
     */
    public void setLabelLoc(int actionNo,double x,double y) {
        if (this.getActionNo()==actionNo)
            super.setLabelLoc(x,y);
        actionBehavior.setLabelLoc(actionNo,x,y);
    }
    /**
     * アクションを指定してラベルを表示する場合のオフセットを設定します。
     */
    public void setLabelLoc(Serializable action,double x,double y) {
        int actionNo;
        if (action instanceof Integer) {
            actionNo = ((Integer)action).intValue();
        } else if (action instanceof String) {
            actionNo = this.getActionNo((String)action);
        } else {
            return;
        }
        if (this.getActionNo()==actionNo)
            super.setLabelLoc(x,y);
        actionBehavior.setLabelLoc(actionNo,x,y);
    }
    /**
     * ラベルを表示する場合のオフセットを返します。
     */
    public void getLabelLoc(double ret[]) {
        super.getLabelLoc(ret);
    }
    /**
     * アクションを指定してラベルを表示する場合のオフセットを返します。
     */
    public void getLabelLoc(int actionNo,double ret[]) {
        actionBehavior.getLabelLoc(actionNo,ret);
    }
    /**
     * アクションを指定してラベルを表示する場合のオフセットを返します。
     */
    public void getLabelLoc(Serializable action,double ret[]) {
        int actionNo = 0;
        if (action instanceof Integer) {
            actionNo = ((Integer)action).intValue();
        } else if (action instanceof String) {
            actionNo = this.getActionNo((String)action);
        }
        actionBehavior.getLabelLoc(actionNo,ret);
    }
    /**
     * 吹き出しをセットします。表示されている吹き出しを
     * 消去したい場合には，引数にnullをわたして下さい．
     */
    public void setBalloon(String s) {
        if (balloon==null) {
            balloon = new A3Balloon(s,this);
            actionBehavior.resetBalloonParameters();//ポイント
        }
        if (s!=null) {
            balloon.setString(s);
            if (component2DContainer!=null)
                component2DContainer.add(balloon);
        } else {
            if (component2DContainer!=null)
                component2DContainer.del(balloon);
        }
    }

    /**
     * 向きを指定して吹き出しを表示する場合のオフセットを設定します。
     */
    public void setBalloonLoc(A3Object.BalloonDir d,double x,double y) {
        if (balloon==null) {
            balloon = new A3Balloon("",this);
            actionBehavior.resetBalloonParameters();
        }
        balloon.setOffset(d,x,y);
        actionBehavior.setBalloonLoc(d,x,y);
    }

    /**
     * アクションと向きを指定して吹き出しを表示する場合のオフセットを設定します。
     */
    public void setBalloonLoc(int actionNo,A3Object.BalloonDir d,double x,double y) {
        if (this.getActionNo()==actionNo) {
            if (balloon==null) {
                balloon = new A3Balloon("",this);
                actionBehavior.resetBalloonParameters();
            }
            balloon.setOffset(d,x,y);
        }
        actionBehavior.setBalloonLoc(actionNo,d,x,y);
    }

    /**
     * アクションと向きを指定して吹き出しを表示する場合のオフセットを設定します。
     */
    public void setBalloonLoc(Serializable action,A3Object.BalloonDir d,double x,double y) {
        int actionNo;
        if (action instanceof Integer) {
            actionNo = ((Integer)action).intValue();
        } else if (action instanceof String) {
            actionNo = this.getActionNo((String)action);
        } else {
            return;
        }
        setBalloonLoc(actionNo,d,x,y);
    }
    /**
     * 全てのアクションにおいて、指定した向きの吹き出しを表示する場合のオフセットを設定します。
     */
    public void setBalloonLocAll(A3Object.BalloonDir d,double x,double y) {
        setBalloonLoc(d,x,y);
        actionBehavior.setBalloonLocAll(d,x,y);
    }
    /**
     * 向きを指定して吹き出しを表示する場合のオフセットを返します。
     */
    public void getBalloonLoc(A3Object.BalloonDir d,double ret[]) {
        actionBehavior.getBalloonLoc(d,ret);
    }
    /**
     * アクションと向きを指定して吹き出しを表示する場合のオフセットを返します。
     */
    public void getBalloonLoc(int actionNo,A3Object.BalloonDir d,double ret[]) {
        actionBehavior.getBalloonLoc(actionNo,d,ret);
    }
    /**
     * アクションと向きを指定して吹き出しを表示する場合のオフセットを返します。
     */
    public void getBalloonLoc(Serializable action,A3Object.BalloonDir d,double ret[]) {
        int actionNo = 0;
        if (action instanceof Integer) {
            actionNo = ((Integer)action).intValue();
        } else if (action instanceof String) {
            actionNo = this.getActionNo((String)action);
        }
        actionBehavior.getBalloonLoc(actionNo,d,ret);
    }

    /**
     * 吹き出しを表示する場合の向きを設定します。
     */
    public void setBalloonDir(BalloonDir d) {
        if (balloon==null) {
            balloon = new A3Balloon("",this);
            actionBehavior.resetBalloonParameters();
        }
        balloon.setDir(d);
        actionBehavior.setBalloonDir(d);
    }
    /**
     * アクションを指定して吹き出しを表示する場合の向きを設定します。
     */
    public void setBalloonDir(int actionNo,BalloonDir d) {
        if (this.getActionNo()==actionNo) {
            if (balloon==null) {
                balloon = new A3Balloon("",this);
                actionBehavior.resetBalloonParameters();
            }
            balloon.setDir(d);
        }
        actionBehavior.setBalloonDir(actionNo,d);
    }
    /**
     * アクションを指定して吹き出しを表示する場合の向きを設定します。
     */
    public void setBalloonDir(Serializable action,BalloonDir d) {
        int actionNo;
        if (action instanceof Integer) {
            actionNo = ((Integer)action);
        } else if (action instanceof String) {
            actionNo = this.getActionNo((String)action);
        } else {
            return;
        }
        setBalloonDir(actionNo,d);
    }
    /**
     * 全てのアクションの吹き出しを表示する場合の向きを設定します。
     */
    public void setBalloonDirAll(BalloonDir d) {
        setBalloonDir(d);
        actionBehavior.setBalloonDirAll(d);
    }
    //----------------------------------------
    void setSoundLocQuat(Vector3d v,Quat4d q) {
        if (actionBehavior!=null)
            actionBehavior.setSoundLocQuat(v,q);
    }
    void setSoundTypeLocked() {
        if (actionBehavior!=null)
            actionBehavior.setSoundTypeLocked();
    }

    void setTransformImmediately(Transform3D t) {
        behavior.setTransformImmediately(t);
    }

    /**
     * このAction3Dオブジェクトの座標・回転・拡大率を
     * BVHのルートの骨の情報で制御するかどうかを指定します．
     * trueは指定する，falseは指定しないことを表します．
     * デフォルトはfalseです．
     */
    public void transControlUsingRootBone(boolean b) {
        actionBehavior.transControlUsingRootBone(b);
    }
}
