package jp.sourceforge.acerola3d.a3;

import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

class SubCameraBehavior extends Behavior {
    TransformGroup tg;
    Transform3D t = new Transform3D();
    Vector3d cameraNowV = new Vector3d();
    Vector3d cameraNextV = new Vector3d();
    Quat4d cameraNowQ = new Quat4d();
    Quat4d cameraNextQ = new Quat4d();
    double cameraNowS = 1.0;
    double cameraNextS = 1.0;
    Vector3d cameraDefaultV = new Vector3d();
    Quat4d cameraDefaultQ = new Quat4d();
    double cameraDefaultS = 1.0;
    A3VirtualUniverse virtualUniverse = null;
    double interpolateRatio = 0.1;

    SubCameraBehavior(TransformGroup tg) {
        this.tg = tg;
    }
    void setA3VirtualUniverse(A3VirtualUniverse u) {
        virtualUniverse = u;
    }
    public void initialize() {
        WakeupOnBehaviorPost w = new WakeupOnBehaviorPost(null,1);
        wakeupOn(w);
    }
    @SuppressWarnings("unchecked")
    public void processStimulus(Enumeration criteria) {
        //WakeupOnElapsedTime w = new WakeupOnElapsedTime(100);
        WakeupOnBehaviorPost w = new WakeupOnBehaviorPost(null,1);
        wakeupOn(w);
        double ratio= 1.0-Math.pow(interpolateRatio,((double)virtualUniverse.elapsedTime)/1000.0);
        cameraNowS = cameraNowS + ratio*(cameraNextS - cameraNowS);
        cameraNowQ.normalize();
        cameraNowQ.interpolate(cameraNextQ,ratio);
        cameraNowQ.normalize();
        cameraNowV.interpolate(cameraNextV,ratio);
        t.set(cameraNowQ,cameraNowV,cameraNowS);
        try {
            tg.setTransform(t);
        } catch (BadTransformException e) {
            System.out.println("BadTransformException in SubCameraBehavior.processStimulus().");
            cameraNowS=1.0;cameraNowQ.set(0,0,0,1);cameraNowV.set(0,0,0);
            //e.printStackTrace();
        }
    }

    /**
     * カメラのデフォルトの位置を指定します。
     */
    public void setDefaultCameraLoc(double x,double y,double z) {
        cameraDefaultV.set(x,y,z);
    }

    /**
     * カメラのデフォルトの位置を指定します。
     */
    public void setDefaultCameraLoc(Vector3d loc) {
        cameraDefaultV.set(loc);
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraQuat(double x,double y,double z,double w) {
        cameraDefaultQ.set(x,y,z,w);
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraQuat(Quat4d quat) {
        cameraDefaultQ.set(quat);
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraRot(double x,double y,double z) {
        cameraDefaultQ.set(Util.euler2quat(x,y,z));
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraRot(Vector3d rot) {
        cameraDefaultQ.set(Util.euler2quat(rot));
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraRev(double x,double y,double z) {
        cameraDefaultQ.set(Util.euler2quat(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z));
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraRev(Vector3d rev) {
        cameraDefaultQ.set(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
    }

    /**
     * カメラのデフォルトの拡大率を指定します。
     */
    public void setDefaultCameraScale(double s) {
        cameraDefaultS = s;
    }

    /**
     * カメラの位置、回転、拡大率をリセットしてデフォルトに戻します。
     */
    public void resetCamera() {
        cameraNowV.set(cameraDefaultV);
        cameraNextV.set(cameraDefaultV);
        cameraNowQ.set(cameraDefaultQ);
        cameraNextQ.set(cameraDefaultQ);
        cameraNowS = cameraDefaultS;
        cameraNextS = cameraDefaultS;
    }

    /**
     * カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(double x,double y,double z) {
        cameraNextV.set(x,y,z);
    }

    /**
     * カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(Vector3d loc) {
        cameraNextV.set(loc);
    }

    /**
     * カメラの位置を即時に指定します。
     */
    public void setCameraLocImmediately(double x,double y,double z) {
        cameraNowV.set(x,y,z);
        cameraNextV.set(x,y,z);
    }

    /**
     * カメラの位置を即時に指定します。
     */
    public void setCameraLocImmediately(Vector3d loc) {
        cameraNowV.set(loc);
        cameraNextV.set(loc);
    }

    /**
     * 現在のカメラ位置に引数のベクトルを加えたものを新しカメラ位置に指定します。
     * 自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void addCameraLoc(double x,double y,double z) {
        cameraNextV.x += x;
        cameraNextV.y += y;
        cameraNextV.z += z;
    }

    /**
     * 現在のカメラ位置に引数のベクトルを加えた物を新しカメラの位置に指定します。
     * 自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void addCameraLoc(Vector3d loc) {
        cameraNextV.add(loc);
    }

    /**
     * 現在のカメラ位置に引数のベクトルを加えた物を新しカメラの位置に即時に指定します。
     */
    public void addCameraLocImmediately(double x,double y,double z) {
        cameraNextV.x += x;
        cameraNextV.y += y;
        cameraNextV.z += z;
        cameraNowV.set(cameraNextV);
    }

    /**
     * 現在のカメラ位置に引数のベクトルを加えた物を新しカメラの位置に即時に指定します。
     */
    public void addCameraLocImmediately(Vector3d loc) {
        cameraNextV.add(loc);
        cameraNowV.set(cameraNextV);
    }

    public void moveCameraForward(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,0,1));
        v.scale(-l);
        addCameraLoc(v);
    }

    public void moveCameraForwardImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,0,1));
        v.scale(-l);
        addCameraLocImmediately(v);
    }

    public void moveCameraBackward(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,0,1));
        v.scale(l);
        addCameraLoc(v);
    }

    public void moveCameraBackwardImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,0,1));
        v.scale(l);
        addCameraLocImmediately(v);
    }

    public void moveCameraRight(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(1,0,0));
        v.scale(l);
        addCameraLoc(v);
    }

    public void moveCameraRightImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(1,0,0));
        v.scale(l);
        addCameraLocImmediately(v);
    }

    public void moveCameraLeft(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(1,0,0));
        v.scale(-l);
        addCameraLoc(v);
    }

    public void moveCameraLeftImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(1,0,0));
        v.scale(-l);
        addCameraLocImmediately(v);
    }

    public void moveCameraUp(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,1,0));
        v.scale(l);
        addCameraLoc(v);
    }

    public void moveCameraUpImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,1,0));
        v.scale(l);
        addCameraLocImmediately(v);
    }

    public void moveCameraDown(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,1,0));
        v.scale(-l);
        addCameraLoc(v);
    }

    public void moveCameraDownImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,1,0));
        v.scale(-l);
        addCameraLocImmediately(v);
    }

    public void moveCameraTo(Vector3d v, double l) {
        Vector3d vv = new Vector3d();
        vv.sub(v,cameraNowV);
        addCameraLoc(vv);
    }

    public void moveCameraTo(double x, double y, double z, double l) {
        Vector3d vv = new Vector3d(x,y,z);
        vv.sub(cameraNowV);
        addCameraLoc(vv);
    }

    public void moveCameraToImmediately(Vector3d v, double l) {
        Vector3d vv = new Vector3d();
        vv.sub(v,cameraNowV);
        addCameraLocImmediately(vv);
    }

    public void moveCameraToImmediately(double x, double y, double z, double l) {
        Vector3d vv = new Vector3d(x,y,z);
        vv.sub(cameraNowV);
        addCameraLocImmediately(vv);
    }

    /**
     * カメラの現在位置を返します。
     */
    public Vector3d getCameraLoc() {
        return new Vector3d(cameraNowV);
    }

    /**
     * カメラの目標位置を返します。
     */
    public Vector3d getCameraTargetLoc() {
        return new Vector3d(cameraNextV);
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(double x,double y,double z,double w) {
        cameraNextQ.set(x,y,z,w);
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(Quat4d quat) {
        cameraNextQ.set(quat);
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraQuatImmediately(double x,double y,double z,double w) {
        cameraNowQ.set(x,y,z,w);
        cameraNextQ.set(x,y,z,w);
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraQuatImmediately(Quat4d quat) {
        cameraNowQ.set(quat);
        cameraNextQ.set(quat);
    }

    public void mulCameraQuat(double x,double y,double z,double w) {
        cameraNextQ.mul(new Quat4d(x,y,z,w));
    }

    public void mulCameraQuat(Quat4d quat) {
        cameraNextQ.mul(quat);
    }

    public void mulCameraQuatImmediately(double x,double y,double z,double w) {
        cameraNextQ.mul(new Quat4d(x,y,z,w));
        cameraNowQ.set(cameraNextQ);
    }

    public void mulCameraQuatImmediately(Quat4d quat) {
        cameraNextQ.mul(quat);
        cameraNowQ.set(cameraNextQ);
    }

    /**
     * カメラの現在の回転を返します。
     */
    public Quat4d getCameraQuat() {
        return new Quat4d(cameraNowQ);
    }

    public Quat4d getCameraTargetQuat() {
        return new Quat4d(cameraNextQ);
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraRot(double x,double y,double z) {
        cameraNextQ.set(Util.euler2quat(x,y,z));
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraRot(Vector3d rot) {
        cameraNextQ.set(Util.euler2quat(rot));
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraRotImmediately(double x,double y,double z) {
        cameraNowQ.set(Util.euler2quat(x,y,z));
        cameraNextQ.set(cameraNowQ);
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraRotImmediately(Vector3d rot) {
        cameraNowQ.set(Util.euler2quat(rot));
        cameraNextQ.set(cameraNowQ);
    }

    public void mulCameraRot(double x,double y,double z) {
        cameraNextQ.mul(Util.euler2quat(x,y,z));
    }

    public void mulCameraRot(Vector3d rot) {
        cameraNextQ.mul(Util.euler2quat(rot));
    }

    public void mulCameraRotImmediately(double x,double y,double z) {
        cameraNextQ.mul(Util.euler2quat(x,y,z));
        cameraNowQ.set(cameraNextQ);
    }

    public void mulCameraRotImmediately(Vector3d rot) {
        cameraNextQ.mul(Util.euler2quat(rot));
        cameraNowQ.set(cameraNextQ);
    }

    public Vector3d getCameraRot() {
        return Util.quat2euler(cameraNowQ);
    }

    public Vector3d getCameraTargetRot() {
        return Util.quat2euler(cameraNextQ);
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraRev(double x,double y,double z) {
        cameraNextQ.set(Util.euler2quat(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z));
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraRev(Vector3d rev) {
        cameraNextQ.set(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraRevImmediately(double x,double y,double z) {
        cameraNowQ.set(Util.euler2quat(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z));
        cameraNextQ.set(cameraNowQ);
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraRevImmediately(Vector3d rev) {
        cameraNowQ.set(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
        cameraNextQ.set(cameraNowQ);
    }

    public void mulCameraRev(double x,double y,double z) {
        cameraNextQ.mul(Util.euler2quat(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z));
    }

    public void mulCameraRev(Vector3d rev) {
        cameraNextQ.mul(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
    }

    public void mulCameraRevImmediately(double x,double y,double z) {
        cameraNextQ.mul(Util.euler2quat(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z));
        cameraNowQ.set(cameraNextQ);
    }

    public void mulCameraRevImmediately(Vector3d rev) {
        cameraNextQ.mul(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
        cameraNowQ.set(cameraNextQ);
    }

    public Vector3d getCameraRev() {
        Vector3d ret = Util.quat2euler(cameraNowQ);
        ret.scale(180.0/Math.PI);
        return ret;
    }

    public Vector3d getCameraTargetRev() {
        Vector3d ret = Util.quat2euler(cameraNextQ);
        ret.scale(180.0/Math.PI);
        return ret;
    }

    /**
     * カメラの拡大率を指定します。自動的に補完が働き滑らかにカメラの拡大率が
     * 変ります。拡大率がデフォルトの1.0の時は10cmより手前と100mより奥はクリッピングされて
     * 表示されません。拡大率を0.1にすれば1cmから10mの間を表示できるようになり、
     * 10.0にすれば1mから1kmの間を表示できるようになります。
     */
    public void setCameraScale(double s) {
        cameraNextS = s;
    }

    /**
     * カメラの拡大率を即時に指定します。拡大率がデフォルトの1.0の時は10cmより
     * 手前と100mより奥はクリッピングされて表示されません。拡大率を0.1にすれば
     * 1cmから10mの間を表示できるようになり、10.0にすれば1mから1kmの間を
     * 表示できるようになります。
     */
    public void setCameraScaleImmediately(double s) {
        cameraNowS = s;
        cameraNextS = s;
    }

    public void mulCameraScale(double s) {
        cameraNextS *= s;
    }

    public void mulCameraScaleImmediately(double s) {
        cameraNextS *= s;
        cameraNowS = cameraNextS;
    }

    /**
     * カメラの拡大率を返します。
     */
    public double getCameraScale() {
        return cameraNowS;
    }

    public double getCameraTargetScale() {
        return cameraNextS;
    }

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up) {
        Vector3d front = new Vector3d(lookAt);
        front.sub(cameraNowV);
        cameraNextQ.set(Util.frontFacingQuat_CAMERA(front,up));
    }

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up) {
        setCameraLookAtPoint(lookAt,up);
        cameraNowQ.set(cameraNextQ);
    }

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up) {
        Vector3d lookAt = new Vector3d(x,y,z);
        setCameraLookAtPoint(lookAt,up);
    }

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up) {
        setCameraLookAtPoint(x,y,z,up);
        cameraNowQ.set(cameraNextQ);
    }
    void setInterpolateRatio(double ir) {
        interpolateRatio = ir;
    }






    public final void turnCameraUp(double deg) {
        mulCameraQuat(Util.euler2quat(deg/180.0*Math.PI,0,0));
    }
    public final void turnCameraUpNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(deg/180.0*Math.PI,0,0));
    }
    public final void turnCameraDown(double deg) {
        mulCameraQuat(Util.euler2quat(-deg/180.0*Math.PI,0,0));
    }
    public final void turnCameraDownNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(-deg/180.0*Math.PI,0,0));
    }
    public final void turnCameraRight(double deg) {
        mulCameraQuat(Util.euler2quat(0,-deg/180.0*Math.PI,0));
    }
    public final void turnCameraRightNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(0,-deg/180.0*Math.PI,0));
    }
    public final void turnCameraLeft(double deg) {
        mulCameraQuat(Util.euler2quat(0,deg/180.0*Math.PI,0));
    }
    public final void turnCameraLeftNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(0,deg/180.0*Math.PI,0));
    }
    public final void rollCameraRight(double deg) {
        mulCameraQuat(Util.euler2quat(0,0,-deg/180.0*Math.PI));
    }
    public final void rollCameraRightNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(0,0,-deg/180.0*Math.PI));
    }
    public final void rollCameraLeft(double deg) {
        mulCameraQuat(Util.euler2quat(0,0,deg/180.0*Math.PI));
    }
    public final void rollCameraLeftNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(0,0,deg/180.0*Math.PI));
    }
    //****************************************
    public void setCameraLookAtPoint(A3Object a,Vector3d up) {
        this.setCameraLookAtPoint(a.getLoc(),up);
    }
    public void setCameraLookAtPointNow(A3Object a,Vector3d up) {
        this.setCameraLookAtPointImmediately(a.getLoc(),up);
    }
    public ArrayList<A3Object> getAll() {
        return null;
    }
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c) {
        return null;
    }
    public void turnCameraTo(double dirX,double dirY,double dirZ,Vector3d up) {
        turnCameraTo(new Vector3d(dirX,dirY,dirZ),up);
    }
    public void turnCameraTo(Vector3d dir,Vector3d up) {
        Vector3d lookAtPoint = new Vector3d();
        lookAtPoint.add(cameraNowV,dir);
        setCameraLookAtPoint(lookAtPoint,up);
    }
    public void turnCameraToNow(double dirX,double dirY,double dirZ,Vector3d up) {
        turnCameraToNow(new Vector3d(dirX,dirY,dirZ),up);
    }
    public void turnCameraToNow(Vector3d dir,Vector3d up) {
        Vector3d lookAtPoint = new Vector3d();
        lookAtPoint.add(cameraNowV,dir);
        setCameraLookAtPointImmediately(lookAtPoint,up);
    }
}
