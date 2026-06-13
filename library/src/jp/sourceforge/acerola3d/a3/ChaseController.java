package jp.sourceforge.acerola3d.a3;

import javax.vecmath.*;

/**
 * A3CanvasやA3Windowなどに登録されたアバターを
 * 追跡するようにカメラを自動制御するコントローラです。
 * A3CanvasやA3WindowなどのsetA3Controllerメソッドで
 * 有効化します。
 */
public class ChaseController extends A3Controller implements Runnable {
    Quat4d q;
    Vector3d v;
    double s;

    /**
     * アバタ後方5メートルから追跡するように
     * カメラを自動制御するコントローラを生成
     * します。A23.setDefaultUpperDirection()などで
     * Z軸が上になる座標系に設定している場合は正常に機能しない
     * ので他のコンストラクタを使って下さい。
     */
    public ChaseController() {
        q = new Quat4d(0.0,1.0,0.0,0.0);
        v = new Vector3d(0.0,0.0,-5.0);
        s = 1.0;
    }

    /**
     * アバタを追跡するようにカメラを自動制御する
     * コントローラを生成します。引数はアバタの
     * 座標系をカメラの座標系へ写像するアフィン変換。
     */
    public ChaseController(Matrix4d mc) {
        Matrix3d m = new Matrix3d();
        m.m00 = mc.m00;m.m01 = mc.m01;m.m02 = mc.m02;
        m.m10 = mc.m10;m.m11 = mc.m11;m.m12 = mc.m12;
        m.m20 = mc.m20;m.m21 = mc.m21;m.m22 = mc.m22;
        q = Util.matrix2quat(m);
        v = new Vector3d();
        mc.get(v);
        s = mc.getScale();
    }
    /**
     * アバタを追跡するようにカメラを自動制御する
     * コントローラを生成します。引数はアバタの
     * 座標系をカメラの座標系へ変換するための回転と
     * 平行移動と拡大率。
     */
    public ChaseController(Quat4d q,Vector3d v,double s) {
        this.q = new Quat4d(q);
        this.v = new Vector3d(v);
        this.s = s;
    }

    /**
     * アバタを追跡するようにカメラを自動制御する
     * コントローラを生成します。引数は、アバタを原点とする座標系で、
     * 注視点とカメラの位置と上方向ベクトルと拡大率。
     */
    public ChaseController(Vector3d lookAt,Vector3d camera,Vector3d up,double s) {
        Vector3d front = new Vector3d();
        front.sub(lookAt,camera);
        Quat4d quat = Util.frontFacingQuat_CAMERA(front,up);
        this.q = quat;
        this.v = new Vector3d(camera);
        this.s = s;
    }

    /**
     * アバタを追跡するようにカメラを自動制御する
     * コントローラを生成します。引数は、アバタを原点とする座標系で、
     * 注視点(Vector3d)とカメラの位置(Vector3d)と上方向ベクトル(Vector3d)と拡大率(Double)。
     */
    public ChaseController(Object...params) {
        if (params.length!=4) {
            //System.out.println("ChaseController not initialized!!!");
            q = new Quat4d(0.0,1.0,0.0,0.0);
            v = new Vector3d(0.0,0.0,-5.0);
            s = 1.0;
            return;
        }
        Vector3d lookAt = (Vector3d)params[0];
        Vector3d camera = (Vector3d)params[1];
        Vector3d up = (Vector3d)params[2];
        double s = ((Double)params[3]);
        Vector3d front = new Vector3d();
        front.sub(lookAt,camera);
        Quat4d quat = Util.frontFacingQuat_CAMERA(front,up);
        this.q = quat;
        this.v = new Vector3d(camera);
        this.s = s;
    }

    /**
     * 初期化処理をします。Acerola3Dパッケージの内部で
     * 使用されるメソッドなので直接呼び出さないで下さい。
     */
    public void init() {
        a3canvas.insertTaskIntoTimerLoop(this);
    }

    /**
     * 終了処理をします。Acerola3Dパッケージの内部で
     * 使用されるメソッドなので直接呼び出さないで下さい。
     */
    public void stop() {
        a3canvas.removeTaskFromTimerLoop(this);
    }

    /**
     * 処理をします。Acerola3Dパッケージの内部で
     * 使用されるメソッドなので直接呼び出さないで下さい。
     */
    public void run() {
        A3Object avatar = a3canvas.getAvatar();
        if (avatar==null) {
            return;
        }
        Quat4d aq = avatar.getQuat();
        Vector3d av = avatar.getLoc();
        double as = avatar.getScale();

        Quat4d cq = new Quat4d();
        cq.mul(aq,q);
        Vector3d cv = Util.trans(aq,v);
        cv.add(av);
        double cs = s*as;

        //a3canvas.setCameraLocImmediately(cv);
        //a3canvas.setCameraQuatImmediately(cq);
        //a3canvas.setCameraScaleImmediately(cs);
        a3canvas.setCameraLoc(cv);
        a3canvas.setCameraQuat(cq);
        a3canvas.setCameraScale(cs);
    }
}
