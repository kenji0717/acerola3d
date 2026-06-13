package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.Transform3D;
import javax.vecmath.*;

/**
 * モーションキャプチャデータのデターに従って
 * カメラをコントロールするコントローラ。
 */
public class MotionCameraController extends A3Controller implements Runnable {
    Thread t;
    volatile boolean stopRequest = false;
    Motion motion;
    long motionLength;
    String bone;
    long offsetTime;
    boolean pause = true;
    boolean loop = false;

    /**
     * モーションキャプチャのデータと、そのモーションに含まれる
     * boneを指定してカメラをコントロールするためのコントローラを
     * 生成します。
     */
    public MotionCameraController(Motion m,String b) {
        motion = m;
        bone = b;
        motionLength = (long)(motion.getMotionLength()*1000.0);
    }

    /**
     * 初期化処理をします。Acerola3Dパッケージの内部で
     * 使用されるメソッドなので直接呼び出さないで下さい。
     */
    public void init() {
        stopRequest = false;
        pause = true;
        t = new Thread(this);
        t.start();
    }

    /**
     * 終了処理をします。Acerola3Dパッケージの内部で
     * 使用されるメソッドなので直接呼び出さないで下さい。
     */
    public void stop() {
        stopRequest = true;
    }

    /**
     * カメラのモーションをスタートさせます。
     */
    public void cue() {
        offsetTime = System.currentTimeMillis();
        pause = false;
    }

    /**
     * カメラに新しいモーションをセットしてリセットします。
     */
    public void changeMotion(Motion m) {
        pause = true;
        offsetTime = System.currentTimeMillis();
        motion = m;
        motionLength = (long)(motion.getMotionLength()*1000.0);
    }

    /**
     * カメラのモーションをストップしてリセットします。
     */
    public void reset() {
        pause = true;
        offsetTime = System.currentTimeMillis();
    }

    /**
     * カメラのモーションをループするかどうかをセットします。
     * デフォルトはfalseです。
     */
    public void setLoop(boolean l) {
        loop = l;
    }
    /**
     * 処理をします。Acerola3Dパッケージの内部で
     * 使用されるメソッドなので直接呼び出さないで下さい。
     */
    public void run() {
        while (!stopRequest) {
            if (pause==true)
                continue;
            long time = System.currentTimeMillis()-offsetTime;
            if ((loop==false)&&(time>motionLength))
                continue;
            time = time % motionLength;
            double t = (double)time/1000.0;
            Transform3D trans = new Transform3D();
            calTrans(trans,bone,t);
            Vector3d cameraV = new Vector3d();
            Quat4d cameraQ = new Quat4d();
            double cameraS = trans.get(cameraQ,cameraV);
            a3canvas.setCameraLocImmediately(cameraV);
            a3canvas.setCameraQuatImmediately(cameraQ);
            a3canvas.setCameraScaleImmediately(cameraS);
            long elapsedTime = a3canvas.getUpdateInterval();
            try{Thread.sleep(elapsedTime);}catch(Exception e){;}
        }
    }
    void calTrans(Transform3D trans,String bone,double time) {
        Transform3D t = motion.getTransform3D(bone, time);
        trans.mul(t,trans);
        String nextBone = motion.getParentBone(bone);
        if (nextBone==null)
            return;
        calTrans(trans,nextBone,time);
    }
}
