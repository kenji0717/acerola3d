package jp.sourceforge.acerola3d.a3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.vecmath.*;
import jp.sourceforge.acerola3d.a3.A3Object.UpperDirection;
import com.sun.j3d.exp.swing.JCanvas3D;

/**
 * A3SubCanvasのLightWeightバージョンで、既存の仮想空間に
 * 挿入することで、既存のカメラとは別の視点からの映像を
 * 描画できる3Dキャンバスです。
 * このクラスはA3CanvasInterfaceを実装しており，
 * A3CanvasInterfaceの多くのメソッドは現在アクティブな
 * シーンのカメラやナビゲーションに対して動作するべきものですが，
 * このJA3SubCanvasの場合は独自のカメラやナビゲーションモードを
 * 持っており，それに対する操作となる点に注意して下さい．
 */
public class JA3SubCanvas extends JCanvas3D implements A3CanvasInterface, Component2DContainerInterface {
    private static final long serialVersionUID = 1L;

    BranchGroup viewPlatformBG;
    SubCameraBehavior subCamBehavior;
    View view;
    Transform3D t;
    TransformGroup tg;
    Light headLight;
    Canvas3D canvas3d;
    PickingBehavior pickingBehavior;
    A3VirtualUniverse virtualUniverse;
    Hashtable<A3Object,BranchGroup> lockedA3Hash = new Hashtable<A3Object,BranchGroup>();
    BranchGroup cameraGroup;
    ArrayList<Runnable> renderingLoopTasks = new ArrayList<Runnable>();

    /**
     * (500,500)の大きさのJA3SubCanvasを作成します。
     */
    public static JA3SubCanvas createJA3SubCanvas() {
        return createJA3SubCanvas(500,500);
    }

    /**
     * (w,h)の大きさのJA3SubCanvasを作ります。
     */
    public static JA3SubCanvas createJA3SubCanvas(int w,int h) {
        GraphicsDevice gd = createGraphicsDevice();
        GraphicsConfigTemplate3D gct3d = createGraphicsConfigTemplate3D();
        return createJA3SubCanvas(gct3d,gd,w,h);
    }

    static GraphicsDevice createGraphicsDevice() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd;
    }
    static GraphicsConfigTemplate3D createGraphicsConfigTemplate3D() {
        GraphicsConfigTemplate3D gct3d = new GraphicsConfigTemplate3D();
        return gct3d;
    }

    /**
     * GraphicsConfigTemplate3DとGraphicsDeviceオブジェクトを指定して
     * (w,h)の大きさのA3SubCanvasを作ります。
     */
    public static JA3SubCanvas createJA3SubCanvas(GraphicsConfigTemplate3D t,GraphicsDevice d,int w,int h) {
        return new JA3SubCanvas(t,d,w,h);
    }

    /**
     * (500,500)の大きさのJA3SubCanvasを作ります。
     */
    public JA3SubCanvas() {
        this(500,500);
    }

    /**
     * (w,h)の大きさのJA3SubCanvasを作ります。
     */
    public JA3SubCanvas(int w,int h) {
        this(createGraphicsConfigTemplate3D(),createGraphicsDevice(),w,h);
    }

    static GraphicsConfigTemplate3D forceA23init(GraphicsConfigTemplate3D t)  {
        jp.sourceforge.acerola3d.A23.initA23();//2014,11/10追加(FrustumCulling=falseのためだけに)
        return t;
    }

    /**
     * GraphicsConfigTemplate3DとGraphicsDeviceオブジェクトを指定して
     * (w,h)の大きさのA3SubCanvasを作ります。
     */
    public JA3SubCanvas(GraphicsConfigTemplate3D t,GraphicsDevice d,int w,int h) {
        super(forceA23init(t),d);
        setPreferredSize(new Dimension(w,h));
        setSize(w,h);
        //本当は、以下の処理はここでやりたいところだけど、
        //NullPointerExceptionが出るので、addNotifyメソッドに
        //入れてあげたらうまくいった。でも場合によってはまずいかも。
        //prepareVirtualUniverse();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        canvas3d = this.getOffscreenCanvas3D();
        prepareVirtualUniverse();
        enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK);
        enableEvents(java.awt.AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        //enableEvents(java.awt.AWTEvent.KEY_EVENT_MASK);
    }

    void prepareVirtualUniverse() {
        PhysicalBody pBody = new PhysicalBody();
        PhysicalEnvironment pEnv = new PhysicalEnvironment();

        viewPlatformBG = new BranchGroup();
        t = new Transform3D();
        ViewPlatform viewPlatform = new ViewPlatform();
        tg = new TransformGroup(t);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.addChild(viewPlatform);
        viewPlatformBG.addChild(tg);
        cameraGroup = new BranchGroup();
        cameraGroup.setCapability(BranchGroup.ALLOW_DETACH);
        cameraGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        cameraGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        cameraGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        tg.addChild(cameraGroup);

        headLight = new DirectionalLight();
        headLight.setCapability(Light.ALLOW_STATE_WRITE);
        headLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE));
        headLight.setEnable(false);
        tg.addChild(headLight);

        view = new View();
        view.addCanvas3D(canvas3d);
        view.setPhysicalBody(pBody);
        view.setPhysicalEnvironment(pEnv);
        view.attachViewPlatform(viewPlatform);

        subCamBehavior = new SubCameraBehavior(tg);
        viewPlatformBG.addChild(subCamBehavior);
        subCamBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE));
    }

    // SubCanvas用インタフェース
    @Override
    public BranchGroup getBranchGroupForViewPlatform() {
        return viewPlatformBG;
    }

    @Override
    public void setPickingBehavior(PickingBehavior pb) {
        pickingBehavior = pb;
        viewPlatformBG.addChild(pb);
    }

    @Override
    public void setVirtualUniverse(A3VirtualUniverse vu) {
        virtualUniverse = vu;
        subCamBehavior.setA3VirtualUniverse(vu);
    }

    @Override
    public A3VirtualUniverse getVirtualUniverse() {
        return virtualUniverse;
    }

    //カメラ制御
    @Override
    public void setDefaultCameraLoc(double x,double y,double z) {
        subCamBehavior.setDefaultCameraLoc(x,y,z);
    }

    @Override
    public void setDefaultCameraLoc(Vector3d loc) {
        subCamBehavior.setDefaultCameraLoc(loc);
    }

    @Override
    public void setDefaultCameraQuat(double x,double y,double z,double w) {
        subCamBehavior.setDefaultCameraQuat(x,y,z,w);
    }

    @Override
    public void setDefaultCameraQuat(Quat4d quat) {
        subCamBehavior.setDefaultCameraQuat(quat);
    }

    @Override
    public void setDefaultCameraRot(double x,double y,double z) {
        subCamBehavior.setDefaultCameraRot(x,y,z);
    }

    @Override
    public void setDefaultCameraRot(Vector3d rot) {
        subCamBehavior.setDefaultCameraRot(rot);
    }

    @Override
    public void setDefaultCameraRev(double x,double y,double z) {
        subCamBehavior.setDefaultCameraRev(x,y,z);
    }

    @Override
    public void setDefaultCameraRev(Vector3d rev) {
        subCamBehavior.setDefaultCameraRev(rev);
    }

    @Override
    public void setDefaultCameraScale(double s) {
        subCamBehavior.setDefaultCameraScale(s);
    }

    @Override
    public void resetCamera() {
        subCamBehavior.resetCamera();
    }

    @Override
    public void setCameraLoc(double x,double y,double z) {
        subCamBehavior.setCameraLoc(x,y,z);
    }

    @Override
    public void setCameraLoc(Vector3d loc) {
        subCamBehavior.setCameraLoc(loc);
    }

    @Override
    public void setCameraLocImmediately(double x,double y,double z) {
        subCamBehavior.setCameraLocImmediately(x,y,z);
    }

    @Override
    public void setCameraLocImmediately(Vector3d loc) {
        subCamBehavior.setCameraLocImmediately(loc);
    }

    @Override
    public void addCameraLoc(double x,double y,double z) {
        subCamBehavior.addCameraLoc(x,y,z);
    }

    @Override
    public void addCameraLoc(Vector3d loc) {
        subCamBehavior.addCameraLoc(loc);
    }

    @Override
    public void addCameraLocImmediately(double x,double y,double z) {
        subCamBehavior.addCameraLocImmediately(x,y,z);
    }

    @Override
    public void addCameraLocImmediately(Vector3d loc) {
        subCamBehavior.addCameraLocImmediately(loc);
    }

    @Override
    public void moveCameraForward(double l) {
        subCamBehavior.moveCameraForward(l);
    }

    @Override
    public void moveCameraForwardImmediately(double l) {
        subCamBehavior.moveCameraForwardImmediately(l);
    }

    @Override
    public void moveCameraBackward(double l) {
        subCamBehavior.moveCameraBackward(l);
    }

    @Override
    public void moveCameraBackwardImmediately(double l) {
        subCamBehavior.moveCameraBackwardImmediately(l);
    }

    @Override
    public void moveCameraRight(double l) {
        subCamBehavior.moveCameraRight(l);
    }

    @Override
    public void moveCameraRightImmediately(double l) {
        subCamBehavior.moveCameraRightImmediately(l);
    }

    @Override
    public void moveCameraLeft(double l) {
        subCamBehavior.moveCameraLeft(l);
    }

    @Override
    public void moveCameraLeftImmediately(double l) {
        subCamBehavior.moveCameraLeftImmediately(l);
    }

    @Override
    public void moveCameraUp(double l) {
        subCamBehavior.moveCameraUp(l);
    }

    @Override
    public void moveCameraUpImmediately(double l) {
        subCamBehavior.moveCameraUpImmediately(l);
    }

    @Override
    public void moveCameraDown(double l) {
        subCamBehavior.moveCameraDown(l);
    }

    @Override
    public void moveCameraDownImmediately(double l) {
        subCamBehavior.moveCameraDownImmediately(l);
    }

    @Override
    public void moveCameraTo(Vector3d v, double l) {
        subCamBehavior.moveCameraTo(v,l);
    }

    @Override
    public void moveCameraTo(double x, double y, double z, double l) {
        subCamBehavior.moveCameraTo(x,y,z,l);
    }

    @Override
    public void moveCameraTo(A3Object a, double l) {
        subCamBehavior.moveCameraTo(a.getLoc(),l);
    }

    @Override
    public void moveCameraToImmediately(Vector3d v, double l) {
        subCamBehavior.moveCameraToImmediately(v,l);
    }

    @Override
    public void moveCameraToImmediately(double x, double y, double z, double l) {
        subCamBehavior.moveCameraToImmediately(x,y,z,l);
    }

    @Override
    public void moveCameraToImmediately(A3Object a, double l) {
        subCamBehavior.moveCameraToImmediately(a.getLoc(),l);
    }

    @Override
    public void moveCameraForward(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraForwardImmediately(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraBackward(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraBackwardImmediately(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraRight(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraRightImmediately(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraLeft(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraLeftImmediately(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraUp(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraUpImmediately(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraDown(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraDownImmediately(double l,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraTo(Vector3d v, double l, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraTo(double x, double y, double z, double l, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraTo(A3Object a, double l, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraToImmediately(Vector3d v, double l, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraToImmediately(double x, double y, double z, double l, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public void moveCameraToImmediately(A3Object a, double l, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    @Override
    public Vector3d getCameraLoc() {
        return subCamBehavior.getCameraLoc();
    }

    @Override
    public Vector3d getCameraTargetLoc() {
        return subCamBehavior.getCameraTargetLoc();
    }

    @Override
    public void setCameraQuat(double x,double y,double z,double w) {
        subCamBehavior.setCameraQuat(x,y,z,w);
    }

    @Override
    public void setCameraQuat(Quat4d quat) {
        subCamBehavior.setCameraQuat(quat);
    }

    @Override
    public void setCameraQuatImmediately(double x,double y,double z,double w) {
        subCamBehavior.setCameraQuatImmediately(x,y,z,w);
    }

    @Override
    public void setCameraQuatImmediately(Quat4d quat) {
        subCamBehavior.setCameraQuatImmediately(quat);
    }

    @Override
    public void mulCameraQuat(double x,double y,double z,double w) {
        subCamBehavior.mulCameraQuat(x,y,z,w);
    }

    @Override
    public void mulCameraQuat(Quat4d quat) {
        subCamBehavior.mulCameraQuat(quat);
    }

    @Override
    public void mulCameraQuatImmediately(double x,double y,double z,double w) {
        subCamBehavior.mulCameraQuatImmediately(x,y,z,w);
    }

    @Override
    public void mulCameraQuatImmediately(Quat4d quat) {
        subCamBehavior.mulCameraQuatImmediately(quat);
    }

    @Override
    public Quat4d getCameraQuat() {
        return subCamBehavior.getCameraQuat();
    }

    @Override
    public Quat4d getCameraTargetQuat() {
        return subCamBehavior.getCameraTargetQuat();
    }

    @Override
    public void setCameraRot(double x,double y,double z) {
        subCamBehavior.setCameraRot(x,y,z);
    }

    @Override
    public void setCameraRot(Vector3d rot) {
        subCamBehavior.setCameraRot(rot);
    }

    @Override
    public void setCameraRotImmediately(double x,double y,double z) {
        subCamBehavior.setCameraRotImmediately(x,y,z);
    }

    @Override
    public void setCameraRotImmediately(Vector3d rot) {
        subCamBehavior.setCameraRotImmediately(rot);
    }

    @Override
    public void mulCameraRot(double x,double y,double z) {
        subCamBehavior.mulCameraRot(x,y,z);
    }

    @Override
    public void mulCameraRot(Vector3d rot) {
        subCamBehavior.mulCameraRot(rot);
    }

    @Override
    public void mulCameraRotImmediately(double x,double y,double z) {
        subCamBehavior.mulCameraRotImmediately(x,y,z);
    }

    @Override
    public void mulCameraRotImmediately(Vector3d rot) {
        subCamBehavior.mulCameraRotImmediately(rot);
    }

    @Override
    public Vector3d getCameraRot() {
        return subCamBehavior.getCameraRot();
    }

    @Override
    public Vector3d getCameraTargetRot() {
        return subCamBehavior.getCameraTargetRot();
    }

    @Override
    public void setCameraRev(double x,double y,double z) {
        subCamBehavior.setCameraRev(x,y,z);
    }

    @Override
    public void setCameraRev(Vector3d rev) {
        subCamBehavior.setCameraRev(rev);
    }

    @Override
    public void setCameraRevImmediately(double x,double y,double z) {
        subCamBehavior.setCameraRevImmediately(x,y,z);
    }

    @Override
    public void setCameraRevImmediately(Vector3d rev) {
        subCamBehavior.setCameraRevImmediately(rev);
    }

    @Override
    public void mulCameraRev(double x,double y,double z) {
        subCamBehavior.mulCameraRev(x,y,z);
    }

    @Override
    public void mulCameraRev(Vector3d rev) {
        subCamBehavior.mulCameraRev(rev);
    }

    @Override
    public void mulCameraRevImmediately(double x,double y,double z) {
        subCamBehavior.mulCameraRevImmediately(x,y,z);
    }

    @Override
    public void mulCameraRevImmediately(Vector3d rev) {
        subCamBehavior.mulCameraRevImmediately(rev);
    }

    @Override
    public Vector3d getCameraRev() {
        return subCamBehavior.getCameraRev();
    }

    @Override
    public Vector3d getCameraTargetRev() {
        return subCamBehavior.getCameraTargetRev();
    }

    @Override
    public void setCameraScale(double s) {
        subCamBehavior.setCameraScale(s);
    }

    @Override
    public void setCameraScaleImmediately(double s) {
        subCamBehavior.setCameraScaleImmediately(s);
    }

    @Override
    public void mulCameraScale(double s) {
        subCamBehavior.mulCameraScale(s);
    }

    @Override
    public void mulCameraScaleImmediately(double s) {
        subCamBehavior.mulCameraScaleImmediately(s);
    }

    @Override
    public double getCameraScale() {
        return subCamBehavior.getCameraScale();
    }

    @Override
    public double getCameraTargetScale() {
        return subCamBehavior.getCameraTargetScale();
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up) {
        subCamBehavior.setCameraLookAtPoint(lookAt,up);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up) {
        subCamBehavior.setCameraLookAtPointImmediately(lookAt,up);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up) {
        subCamBehavior.setCameraLookAtPoint(x,y,z,up);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up) {
        subCamBehavior.setCameraLookAtPointImmediately(x,y,z,up);
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.setCameraLookAtPoint(lookAt,up);
    }

    @Override
    public void setCameraLookAtPoint(double x, double y, double z) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.setCameraLookAtPoint(x,y,z,up);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.setCameraLookAtPointImmediately(lookAt,up);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x, double y, double z) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.setCameraLookAtPointImmediately(x,y,z,up);
    }


//  ----------座標変換のラッパーメソッド---------
    @Override
    public Point3d canvasToVirtualCS(int x, int y) {
        if (avatar==null)
            return null;
        Vector3d v1 = avatar.getLoc();
        Vector3d v2 = subCamBehavior.getCameraLoc();
        Vector3d v = new Vector3d();
        v.sub(v1,v2);
        Vector3d p = virtualCSToPhysicalCS(v);
        return canvasToVirtualCS(x,y,p.z);
    }

    @Override
    public Point3d canvasToVirtualCS(int x,int y,double dis) {
        Point3d p = canvasToPhysicalCS(x,y,dis);
        tg.getTransform(t);
        t.transform(p);
        return p;
    }

    @Override
    public Point3d canvasToPhysicalCS(int x, int y) {
        if (avatar==null)
            return null;
        Vector3d v1 = avatar.getLoc();
        Vector3d v2 = subCamBehavior.getCameraLoc();
        Vector3d v = new Vector3d();
        v.sub(v1,v2);
        Vector3d p = virtualCSToPhysicalCS(v);
        return canvasToPhysicalCS(x,y,p.z);
    }

    @Override
    public Point3d canvasToPhysicalCS(int x,int y,double dis) {
        int dw = canvas3d.getWidth();
        int dh = canvas3d.getHeight();
        double ww = canvas3d.getPhysicalWidth();
        double hh = canvas3d.getPhysicalHeight();
        double tt = view.getFieldOfView();//スクリーンの左右の画角

        // dx,dy,dz:スクリーン上での座標(物理座標系で)
        double dx =  ((double)x)*ww/((double)dw)-ww/2.0;
        double dy = -((double)y)*hh/((double)dh)+hh/2.0;
        double dz = -ww/2.0/Math.tan(tt/2.0);

        if (view.getProjectionPolicy()==View.PARALLEL_PROJECTION) {
            double s = view.getScreenScale();
            double sw = canvas3d.getScreen3D().getPhysicalScreenWidth();
            //double sh = this.getScreen3D().getPhysicalScreenHeight();
            //return new Point3d(dx/s*(sw/ww),dy/s*(sh/hh),-dis); //どうやらこっちじゃないみたい
            return new Point3d(dx/s*(sw/ww),dy/s*(sw/ww),-dis);
        }

        double s = dis/(-dz);
        return new Point3d(s*dx,s*dy,s*dz);
    }

    @Override
    public Vector3d physicalCSToVirtualCS(Vector3d v) {
        Point3d p = new Point3d(v);
        tg.getTransform(t);
        t.transform(p);
        return new Vector3d(p);
    }

    @Override
    public Point physicalCSToCanvas(Point3d p) {
        int dw = canvas3d.getWidth();
        int dh = canvas3d.getHeight();
        double ww = canvas3d.getPhysicalWidth();
        double hh = canvas3d.getPhysicalHeight();

        double tt = view.getFieldOfView();
        tt = ww/2.0/Math.tan(tt/2.0); // 視点とスクリーンの距離
        double zz = -p.z;

        Point ret = new Point();
        if (view.getProjectionPolicy()==View.PARALLEL_PROJECTION) {
            double s = view.getScreenScale();
            double sw = canvas3d.getScreen3D().getPhysicalScreenWidth();
            //double sh = virtualUniverse.canvas3d.getScreen3D().getPhysicalScreenHeight();
            ret.x = (int)(( p.x*(dw/((double)ww)))*s*ww/sw+dw/2);
            ret.y = (int)((-p.y*(dh/((double)hh)))*s*ww/sw+dh/2);
        } else {
            ret.x = (int)( p.x*(dw/((double)ww))/(zz/tt))+dw/2;
            ret.y = (int)(-p.y*(dh/((double)hh))/(zz/tt))+dh/2;
        }
        return ret;
    }

    @Override
    public Point virtualCSToCanvas(Point3d p) {
        tg.getTransform(t);
        t.invert();
        t.transform(p);
        return physicalCSToCanvas(p);
    }

    @Override
    public Vector3d virtualCSToPhysicalCS(Vector3d v) {
        Point3d p = new Point3d(v);
        tg.getTransform(t);
        t.invert();
        t.transform(p);
        return new Vector3d(p);
    }

    @Override
    public Vector3d getCameraUnitVecX() {
        Vector3d v = physicalCSToVirtualCS(new Vector3d(1.0,0.0,0.0));
        v.sub(subCamBehavior.cameraNowV);
        return v;
    }

    @Override
    public Vector3d getCameraUnitVecY() {
        Vector3d v = physicalCSToVirtualCS(new Vector3d(0.0,1.0,0.0));
        v.sub(subCamBehavior.cameraNowV);
        return v;
    }

    @Override
    public Vector3d getCameraUnitVecZ() {
        Vector3d v = physicalCSToVirtualCS(new Vector3d(0.0,0.0,1.0));
        v.sub(subCamBehavior.cameraNowV);
        return v;
    }

    @Override
    public Dimension getCanvasSize() {
        return this.getSize();
    }

    @Override
    public void cleanUp() {
        ;
    }

    @Override
    public void setProjectionMode(ProjectionMode m) {
        if (m==ProjectionMode.PERSPECTIVE) {
            view.setProjectionPolicy(javax.media.j3d.View.PERSPECTIVE_PROJECTION );
            view.setScreenScalePolicy(View.SCALE_SCREEN_SIZE);
        } else if (m==ProjectionMode.PARALLEL) {
            view.setProjectionPolicy(javax.media.j3d.View.PARALLEL_PROJECTION);
            view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        }
    }

    @Override
    public void setCanvasWidthInPWorld(double s) {
        double psw = canvas3d.getScreen3D().getPhysicalScreenWidth();
        view.setScreenScale(psw/s);
    }

    @Override
    public void setFieldOfView(double f) {
        view.setFieldOfView(f);
    }

    @Override
    public A3Object pickA3(int x,int y) {
        return pickingBehavior.pickA3(x,y);
    }

    @Override
    public A3Object pick(Vector3d origin,Vector3d dir) {
        return pickingBehavior.pickA3(origin,dir);
    }

    @Override
    public TransformGroup getTransformGroupForViewPlatform() {
        return tg;
    }

    @Override
    public Canvas3D getCanvas3D() {
        return canvas3d;
    }

    A3Object avatar;
    @Override
    public void setAvatar(A3Object a3) {
        avatar = a3;
    }

    @Override
    public A3Object getAvatar() {
        return avatar;
    }

    A3Controller controller = null;
    void setA3Controller0(A3Controller c) {
        if (c!=null)
            c.setA3CanvasInterface(this);
        if (controller!=null) {
            this.removeA3Listener(controller);
            this.removeKeyListener(controller);
            controller.stop();
        }
        controller = c;
        if (controller!=null) {
            this.addA3Listener(controller);
            this.addKeyListener(controller);
            controller.init();
        }
    }

    @Override
    public void setA3Controller(A3Controller c) {
        naviMode = NaviMode.USER;
        setA3Controller0(c);
    }

    @Override
    public void addA3Listener(A3Listener l) {
        pickingBehavior.addA3Listener(l);
    }

    double naviSpeed = 1.0;
    @Override
    public double getNavigationSpeed() {
        return naviSpeed;
    }

    UpperDirection upperDirection = UpperDirection.Y;
    @Override
    public UpperDirection getUpperDirection() {
        return upperDirection;
    }

    @Override
    public void removeA3Listener(A3Listener l) {
        pickingBehavior.removeA3Listener(l);
    }

    @Override
    public void setHeadLightEnable(boolean b) {
        headLight.setEnable(b);
    }

    NaviMode naviMode = NaviMode.NONE;
    @Override
    public void setNavigationMode(NaviMode m,Object...params) {
        naviMode = m;
        if (naviMode == NaviMode.NONE)
            setA3Controller0(new NoneController());
        else if (naviMode == NaviMode.WALK)
            setA3Controller0(new WalkController());
        else if (naviMode == NaviMode.FLY)
            setA3Controller0(new FlyController());
        else if (naviMode == NaviMode.EXAMINE)
            setA3Controller0(new ExamController());
        else if (naviMode == NaviMode.EDIT)
            setA3Controller0(new EditController());
        else if (naviMode == NaviMode.SIMPLE)
            setA3Controller0(new SimpleController(params));
        else if (naviMode == NaviMode.CHASE)
            setA3Controller0(new ChaseController(params));
        
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Component2D cs[] = components2D.toArray(new Component2D[0]);
        for (Component2D c: cs) {
            c.calPhysicalZ(this);
        }
        Arrays.sort(cs,Component2DComparator.comparator);
        Graphics2D gg = (Graphics2D)g;
        for (Component2D c : cs) {
            if (c.z>0.0)
                continue;
            c.paint(gg,this);
        }
        if (Component2D.advertisement!=null) {
            Component2D.advertisement.paint(gg,this);
        }
        //gg.flush(true);

        //FPSのための計算
        fpsCounter++;
        long l = System.currentTimeMillis()-fpsStartTime;
        if (l>1000) {
            fps = (int)((1000*fpsCounter)/l);
            fpsCounter=0;
            fpsStartTime = System.currentTimeMillis();
        }
    }

    long fpsStartTime;
    int fpsCounter=0;
    int fps;
    @Override
    public int getFPS() {
        return fps;
    }

    @Override
    public void setNavigationSpeed(double s) {
        naviSpeed = s;
    }

    @Override
    public void setUpperDirection(UpperDirection d) {
        upperDirection = d;
    }

//  ----------おまけ機能---------
    volatile boolean check = false;
    GraphicsContext3D gc;
    Raster readRaster;

    public void postSwap() {
        //super.postSwap();
        if (check) {
            gc.readRaster(readRaster);
            check = false;
        }
        synchronized(renderingLoopTasks) {
            for (Runnable r:renderingLoopTasks) {
                r.run();
            }
        }
    }

    @Override
    public void saveImage(File file) throws IOException {
        int width = getWidth();
        int height = getHeight();
        BufferedImage bImage = new BufferedImage(
                               width,height,BufferedImage.TYPE_INT_RGB);
        ImageComponent2D ic2d = new ImageComponent2D(
                                ImageComponent.FORMAT_RGB,bImage);
        //DepthComponentFloat dcf = new DepthComponentFloat(width,height);
        readRaster = new Raster(new Point3f(0.0f,0.0f,0.0f),
                            Raster.RASTER_COLOR,0,0,width,height,
                            ic2d,null);
        check = true;
        while(check) {
            try{Thread.sleep(300);}catch(Exception e){;}
        }

        ImageComponent2D ic = readRaster.getImage();
        BufferedImage image = ic.getImage();

        ImageIO.write(image,"png",file);
//        FileOutputStream out = new FileOutputStream(file);
//        JPEGImageEncoder e = JPEGCodec.createJPEGEncoder(out);
//        e.encode(image);
//        out.close();
    }

    @Override
    public BufferedImage snapshot() {
        int width = getWidth();
        int height = getHeight();
        BufferedImage bImage = new BufferedImage(
                               width,height,BufferedImage.TYPE_INT_RGB);
        ImageComponent2D ic2d = new ImageComponent2D(
                                ImageComponent.FORMAT_RGB,bImage);
        //DepthComponentFloat dcf = new DepthComponentFloat(width,height);
        readRaster = new Raster(new Point3f(0.0f,0.0f,0.0f),
                            Raster.RASTER_COLOR,0,0,width,height,
                            ic2d,null);
        check = true;
        while(check) {
            try{Thread.sleep(300);}catch(Exception e){;}
        }

        ImageComponent2D ic = readRaster.getImage();
        BufferedImage image = ic.getImage();

        return image;
    }

    @Override
    public void addLockedA3(A3Object a) {
        if (lockedA3Hash.containsKey(a))
            return;
        a.setComponent2DContainerInterface(this);
        a.lockedA3=true;
        a.init();
        A3BranchGroup bg = a.getA3BranchGroup();
        cameraGroup.addChild(bg);
        lockedA3Hash.put(a,bg);
    }

    @Override
    public void delAllLockedA3() {
        Enumeration<A3Object> e = lockedA3Hash.keys();
        while (e.hasMoreElements()) {
            A3Object a3 = e.nextElement();
            BranchGroup bg = lockedA3Hash.get(a3);
            bg.detach();
            a3.dispose();
        }
        lockedA3Hash.clear();
    }

    @Override
    public void delLockedA3(A3Object a) {
        BranchGroup bg = lockedA3Hash.get(a);
        if (bg==null)
            return;
        bg.detach();
        a.dispose();
        lockedA3Hash.remove(a);
    }

    @Override
    public A3VirtualUniverse getA3VirtualUniverse() {
        return virtualUniverse;
    }

    ArrayList<Component2D> components2D = new ArrayList<Component2D>();
    @Override
    public void add(Component2D c) {
        synchronized (components2D) {
            if (!components2D.contains(c))
                components2D.add(c);
        }
    }

    @Override
    public void del(Component2D c) {
        synchronized (components2D) {
            components2D.remove(c);
        }
    }

    @Override
    public void setSoundGain(double g) {
        virtualUniverse.setSoundGain(g);
    }

    @Override
    public double getSoundGain() {
        return virtualUniverse.getSoundGain();
    }

    //################################################################################
    //ここから下はダミーのメソッド
    //################################################################################
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void add(A3Object a) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void add(Component2D c, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void add(A3Object a, int s) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void addA3SubCanvas(A3CanvasInterface sc) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void addLockedA3(A3Object a, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void changeActiveScene(int s) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void del(A3Object a) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void del(Component2D c, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void del(A3Object a, int s) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void delAll() {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void delAll(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void delAllLockedA3(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void delBackground() {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void delBackground(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void delLockedA3(A3Object a, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public A3Object getAvatar(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public Vector3d getCameraLoc(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public Vector3d getCameraTargetLoc(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public Quat4d getCameraQuat(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public Quat4d getCameraTargetQuat(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public double getCameraScale(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return 0;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public double getCameraTargetScale(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return 0;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public double getNavigationSpeed(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return 0;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public UpperDirection getUpperDirection(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void prepareScene(int s) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void resetCamera(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setA3Controller(A3Controller c, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setAvatar(A3Object a, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setBackground(A3Object a) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setBackground(A3Object a, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLoc(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLoc(Vector3d loc, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLocImmediately(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLocImmediately(Vector3d loc, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void addCameraLoc(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void addCameraLoc(Vector3d loc, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void addCameraLocImmediately(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void addCameraLocImmediately(Vector3d loc, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPoint(Vector3d lookAt, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPoint(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPoint(Vector3d lookAt, Vector3d up, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPoint(double x, double y, double z, Vector3d up,
            int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPointImmediately(double x, double y, double z,
            int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt, Vector3d up,
            int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPointImmediately(double x, double y, double z,
            Vector3d up, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraQuat(double x, double y, double z, double w, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraQuat(Quat4d quat, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraQuatImmediately(double x, double y, double z,
            double w, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraQuatImmediately(Quat4d quat, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraQuat(double x, double y, double z, double w, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraQuat(Quat4d quat, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraQuatImmediately(double x, double y, double z,
            double w, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraQuatImmediately(Quat4d quat, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraRot(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraRot(Vector3d rot, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraRotImmediately(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraRotImmediately(Vector3d rot, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraRot(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraRot(Vector3d rot, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraRotImmediately(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraRotImmediately(Vector3d rot, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public Vector3d getCameraRot(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public Vector3d getCameraTargetRot(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraRev(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraRev(Vector3d rev, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraRevImmediately(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraRevImmediately(Vector3d rev, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraRev(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraRev(Vector3d rev, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraRevImmediately(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraRevImmediately(Vector3d rev, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public Vector3d getCameraRev(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public Vector3d getCameraTargetRev(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraScale(double s, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraScaleImmediately(double s, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraScale(double s, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void mulCameraScaleImmediately(double s, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setDefaultCameraLoc(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setDefaultCameraLoc(Vector3d loc, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setDefaultCameraQuat(double x, double y, double z, double w,
            int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setDefaultCameraQuat(Quat4d quat, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setDefaultCameraRot(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setDefaultCameraRot(Vector3d rot, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setDefaultCameraRev(double x, double y, double z, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setDefaultCameraRev(Vector3d rev, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setDefaultCameraScale(double s, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setNavigationMode(int scene,NaviMode m,Object...params) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setNavigationSpeed(double s, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setUpperDirection(UpperDirection d, int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setUpdateInterval(long l) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    @Override
    public long getUpdateInterval() {
        return virtualUniverse.getUpdateInterval();
    }

    @Override
    public void waitForUpdate(long timeout) {
        virtualUniverse.waitForUpdate(timeout);
    }

    @Override
    public void insertTaskIntoRenderingLoop(Runnable task) {
        synchronized(renderingLoopTasks) {
            renderingLoopTasks.add(task);
        }
    }

    @Override
    public void removeTaskFromRenderingLoop(Runnable task) {
        synchronized(renderingLoopTasks) {
            renderingLoopTasks.remove(task);
        }
    }

    @Override
    public void insertTaskIntoTimerLoop(Runnable task) {
        virtualUniverse.insertTaskIntoTimerLoop(task);
    }

    @Override
    public void removeTaskFromTimerLoop(Runnable task) {
        virtualUniverse.removeTaskFromTimerLoop(task);
    }

    @Override
    public void setCameraInterpolateRatio(double ir) {
        subCamBehavior.setInterpolateRatio(ir);
    }

    @Override
    public void setDepthBufferFreezeTransparent(boolean b) {
        view.setDepthBufferFreezeTransparent(b);
    }

    //################################################################################
    //Now系メソッド．Immediatelyを呼ぶだけでOK．
    //################################################################################
    @Override
    public void setCameraLocNow(double x,double y,double z) {
        setCameraLocImmediately(x,y,z);
    }
    @Override
    public void setCameraLocNow(Vector3d loc) {
        setCameraLocImmediately(loc);
    }
    @Override
    public void addCameraLocNow(double x,double y,double z) {
        addCameraLocImmediately(x,y,z);
    }
    @Override
    public void addCameraLocNow(Vector3d loc) {
        addCameraLocImmediately(loc);
    }
    @Override
    public void moveCameraForwardNow(double l) {
        moveCameraForwardImmediately(l);
    }
    @Override
    public void moveCameraBackwardNow(double l) {
        moveCameraBackwardImmediately(l);
    }
    @Override
    public void moveCameraRightNow(double l) {
        moveCameraRightImmediately(l);
    }
    @Override
    public void moveCameraLeftNow(double l) {
        moveCameraLeftImmediately(l);
    }
    @Override
    public void moveCameraUpNow(double l) {
        moveCameraUpImmediately(l);
    }
    @Override
    public void moveCameraDownNow(double l) {
        moveCameraDownImmediately(l);
    }
    @Override
    public void moveCameraToNow(Vector3d v, double l) {
        moveCameraToImmediately(v, l);
    }
    @Override
    public void moveCameraToNow(double x, double y, double z, double l) {
        moveCameraToImmediately(x, y, z, l);
    }
    @Override
    public void moveCameraToNow(A3Object a, double l) {
        moveCameraToImmediately(a, l);
    }
    @Override
    public void moveCameraForwardNow(double l,int scene) {
        moveCameraForwardImmediately(l,scene);
    }
    @Override
    public void moveCameraBackwardNow(double l,int scene) {
        moveCameraBackwardImmediately(l,scene);
    }
    @Override
    public void moveCameraRightNow(double l,int scene) {
        moveCameraRightImmediately(l,scene);
    }
    @Override
    public void moveCameraLeftNow(double l,int scene) {
        moveCameraLeftImmediately(l,scene);
    }
    @Override
    public void moveCameraUpNow(double l,int scene) {
        moveCameraUpImmediately(l,scene);
    }
    @Override
    public void moveCameraDownNow(double l,int scene) {
        moveCameraDownImmediately(l,scene);
    }
    @Override
    public void moveCameraToNow(Vector3d v, double l,int scene) {
        moveCameraToImmediately(v, l,scene);
    }
    @Override
    public void moveCameraToNow(double x, double y, double z, double l,int scene) {
        moveCameraToImmediately(x, y, z, l,scene);
    }
    @Override
    public void moveCameraToNow(A3Object a, double l,int scene) {
        moveCameraToImmediately(a, l,scene);
    }
    @Override
    public void setCameraQuatNow(double x,double y,double z,double w) {
        setCameraQuatImmediately(x,y,z,w);
    }
    @Override
    public void setCameraQuatNow(Quat4d quat) {
        setCameraQuatImmediately(quat);
    }
    @Override
    public void mulCameraQuatNow(double x,double y,double z,double w) {
        mulCameraQuatImmediately(x,y,z,w);
    }
    @Override
    public void mulCameraQuatNow(Quat4d quat) {
        mulCameraQuatImmediately(quat);
    }
    @Override
    public void setCameraRotNow(double x,double y,double z) {
        setCameraRotImmediately(x,y,z);
    }
    @Override
    public void setCameraRotNow(Vector3d rot) {
        setCameraRotImmediately(rot);
    }
    @Override
    public void mulCameraRotNow(double x,double y,double z) {
        mulCameraRotImmediately(x,y,z);
    }
    @Override
    public void mulCameraRotNow(Vector3d rot) {
        mulCameraRotImmediately(rot);
    }
    @Override
    public void setCameraRevNow(double x,double y,double z) {
        setCameraRevImmediately(x,y,z);
    }
    @Override
    public void setCameraRevNow(Vector3d rev) {
        setCameraRevImmediately(rev);
    }
    @Override
    public void mulCameraRevNow(double x,double y,double z) {
        mulCameraRevImmediately(x,y,z);
    }
    @Override
    public void mulCameraRevNow(Vector3d rev) {
        mulCameraRevImmediately(rev);
    }
    @Override
    public void setCameraScaleNow(double s) {
        setCameraScaleImmediately(s);
    }
    @Override
    public void mulCameraScaleNow(double s) {
        mulCameraScaleImmediately(s);
    }
    @Override
    public void setCameraLookAtPointNow(Vector3d lookAt) {
        setCameraLookAtPointImmediately(lookAt);
    }
    @Override
    public void setCameraLookAtPointNow(double x,double y,double z) {
        setCameraLookAtPointImmediately(x,y,z);
    }
    @Override
    public void setCameraLookAtPointNow(Vector3d lookAt,Vector3d up) {
        setCameraLookAtPointImmediately(lookAt,up);
    }
    @Override
    public void setCameraLookAtPointNow(double x,double y,double z,Vector3d up) {
        setCameraLookAtPointImmediately(x,y,z,up);
    }
    @Override
    public void setCameraLocNow(double x,double y,double z,int scene) {
        setCameraLocImmediately(x,y,z,scene);
    }
    @Override
    public void setCameraLocNow(Vector3d loc,int scene) {
        setCameraLocImmediately(loc,scene);
    }
    @Override
    public void addCameraLocNow(double x,double y,double z,int scene) {
        addCameraLocImmediately(x,y,z,scene);
    }
    @Override
    public void addCameraLocNow(Vector3d loc,int scene) {
        addCameraLocImmediately(loc,scene);
    }
    @Override
    public void setCameraQuatNow(double x,double y,double z,double w,int scene) {
        setCameraQuatImmediately(x,y,z,w,scene);
    }
    @Override
    public void setCameraQuatNow(Quat4d quat,int scene) {
        setCameraQuatImmediately(quat,scene);
    }
    @Override
    public void mulCameraQuatNow(double x,double y,double z,double w,int scene) {
        mulCameraQuatImmediately(x,y,z,w,scene);
    }
    @Override
    public void mulCameraQuatNow(Quat4d quat,int scene) {
        mulCameraQuatImmediately(quat,scene);
    }
    @Override
    public void setCameraRotNow(double x,double y,double z,int scene) {
        setCameraRotImmediately(x,y,z,scene);
    }
    @Override
    public void setCameraRotNow(Vector3d rot,int scene) {
        setCameraRotImmediately(rot,scene);
    }
    @Override
    public void mulCameraRotNow(double x,double y,double z,int scene) {
        mulCameraRotImmediately(x,y,z,scene);
    }
    @Override
    public void mulCameraRotNow(Vector3d rot,int scene) {
        mulCameraRotImmediately(rot,scene);
    }
    @Override
    public void setCameraRevNow(double x,double y,double z,int scene) {
        setCameraRevImmediately(x,y,z,scene);
    }
    @Override
    public void setCameraRevNow(Vector3d rev,int scene) {
        setCameraRevImmediately(rev,scene);
    }
    @Override
    public void mulCameraRevNow(double x,double y,double z,int scene) {
        mulCameraRevImmediately(x,y,z,scene);
    }
    @Override
    public void mulCameraRevNow(Vector3d rev,int scene) {
        mulCameraRevImmediately(rev,scene);
    }
    @Override
    public void setCameraScaleNow(double s,int scene) {
        setCameraScaleImmediately(s,scene);
    }
    @Override
    public void mulCameraScaleNow(double s,int scene) {
        mulCameraScaleImmediately(s,scene);
    }
    @Override
    public void setCameraLookAtPointNow(Vector3d lookAt,int scene) {
        setCameraLookAtPointImmediately(lookAt,scene);
    }
    @Override
    public void setCameraLookAtPointNow(double x,double y,double z,int scene) {
        setCameraLookAtPointImmediately(x,y,z,scene);
    }
    @Override
    public void setCameraLookAtPointNow(Vector3d lookAt,Vector3d up,int scene) {
        setCameraLookAtPointImmediately(lookAt,up,scene);
    }
    @Override
    public void setCameraLookAtPointNow(double x,double y,double z,Vector3d up,int scene) {
        setCameraLookAtPointImmediately(x,y,z,up,scene);
    }






    //################################################################################
    //turn系メソッド．
    //################################################################################
    @Override
    public void turnCameraUp(double deg) {
        subCamBehavior.turnCameraUp(deg);
    }
    @Override
    public void turnCameraUpNow(double deg) {
        subCamBehavior.turnCameraUpNow(deg);
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraUp(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraUpNow(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    @Override
    public void turnCameraDown(double deg) {
        subCamBehavior.turnCameraDown(deg);
    }
    @Override
    public void turnCameraDownNow(double deg) {
        subCamBehavior.turnCameraDownNow(deg);
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraDown(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraDownNow(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    @Override
    public void turnCameraRight(double deg) {
        subCamBehavior.turnCameraRight(deg);
    }
    @Override
    public void turnCameraRightNow(double deg) {
        subCamBehavior.turnCameraRightNow(deg);
    }
    @Override
    public void turnCameraRight(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraRightNow(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    @Override
    public void turnCameraLeft(double deg) {
        subCamBehavior.turnCameraLeft(deg);
    }
    @Override
    public void turnCameraLeftNow(double deg) {
        subCamBehavior.turnCameraLeftNow(deg);
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraLeft(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraLeftNow(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    @Override
    public void rollCameraRight(double deg) {
        subCamBehavior.rollCameraRight(deg);
    }
    @Override
    public void rollCameraRightNow(double deg) {
        subCamBehavior.rollCameraRightNow(deg);
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void rollCameraRight(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void rollCameraRightNow(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    @Override
    public void rollCameraLeft(double deg) {
        subCamBehavior.rollCameraLeft(deg);
    }
    @Override
    public void rollCameraLeftNow(double deg) {
        subCamBehavior.rollCameraLeftNow(deg);
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void rollCameraLeft(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void rollCameraLeftNow(double deg,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    //****************************************
    @Override
    public void setCameraLookAtPoint(A3Object a) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.setCameraLookAtPoint(a,up);
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,Vector3d up) {
        subCamBehavior.setCameraLookAtPoint(a,up);
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPoint(A3Object a,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPoint(A3Object a,Vector3d up,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.setCameraLookAtPointNow(a,up);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,Vector3d up) {
        subCamBehavior.setCameraLookAtPointNow(a,up);
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPointNow(A3Object a,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setCameraLookAtPointNow(A3Object a,Vector3d up,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }
    @Override
    public ArrayList<A3Object> getAll() {
        return subCamBehavior.getAll();
    }
    @Override
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c) {
        return subCamBehavior.getAll(c);
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public ArrayList<A3Object> getAll(int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }
    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }
//-------
    @Override
    public void turnCameraTo(double dirX,double dirY,double dirZ) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.turnCameraTo(dirX,dirY,dirZ,up);
    }
    @Override
    public void turnCameraTo(Vector3d dir) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.turnCameraTo(dir,up);
    }
    @Override
    public void turnCameraToNow(double dirX,double dirY,double dirZ) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.turnCameraToNow(dirX,dirY,dirZ,up);
    }
    @Override
    public void turnCameraToNow(Vector3d dir) {
        Vector3d up = new Vector3d();
        if (upperDirection==UpperDirection.Y)
            up.y = 1.0;
        else if (upperDirection==UpperDirection.Z)
            up.z = 1.0;
        subCamBehavior.turnCameraToNow(dir,up);
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraTo(double dirX,double dirY,double dirZ,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraTo(Vector3d dir,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraToNow(double dirX,double dirY,double dirZ,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void turnCameraToNow(Vector3d dir,int scene) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
    }

    //2015,12/06: 指定したA3Objectの情報をもとにカメラを
    //コントロールするメソッド達
    @Override public void setCameraLoc(A3Object a)
    {setCameraLoc(a.getLoc());}
    @Override public void setCameraQuat(A3Object a)
    {setCameraQuat(a.getQuat());}
    @Override public void setCameraRev(A3Object a)
    {setCameraQuat(a.getQuat());}
    @Override public void setCameraScale(A3Object a)
    {setCameraScale(a.getScale());}
    @Override public void setCameraLocRevScale(A3Object a)
    {setCameraLoc(a.getLoc());
     setCameraQuat(a.getQuat());
     setCameraScale(a.getScale());}
    @Override public void setCameraLocNow(A3Object a)
    {setCameraLocNow(a.getLoc());}
    @Override public void setCameraQuatNow(A3Object a)
    {setCameraQuatNow(a.getQuat());}
    @Override public void setCameraRevNow(A3Object a)
    {setCameraQuatNow(a.getQuat());}
    @Override public void setCameraScaleNow(A3Object a)
    {setCameraScaleNow(a.getScale());}
    @Override public void setCameraLocRevScaleNow(A3Object a)
    {setCameraLocNow(a.getLoc());
     setCameraQuatNow(a.getQuat());
     setCameraScaleNow(a.getScale());}
    @Override public void setCameraLoc(A3Object a,int scene)
    {setCameraLoc(a.getLoc(),scene);}
    @Override public void setCameraQuat(A3Object a,int scene)
    {setCameraQuat(a.getQuat(),scene);}
    @Override public void setCameraRev(A3Object a,int scene)
    {setCameraQuat(a.getQuat(),scene);}
    @Override public void setCameraScale(A3Object a,int scene)
    {setCameraScale(a.getScale(),scene);}
    @Override public void setCameraLocRevScale(A3Object a,int scene)
    {setCameraLoc(a.getLoc(),scene);
     setCameraQuat(a.getQuat(),scene);
     setCameraScale(a.getScale(),scene);}
    @Override public void setCameraLocNow(A3Object a,int scene)
    {setCameraLocNow(a.getLoc(),scene);}
    @Override public void setCameraQuatNow(A3Object a,int scene)
    {setCameraQuatNow(a.getQuat(),scene);}
    @Override public void setCameraRevNow(A3Object a,int scene)
    {setCameraQuatNow(a.getQuat(),scene);}
    @Override public void setCameraScaleNow(A3Object a,int scene)
    {setCameraScaleNow(a.getScale(),scene);}
    @Override public void setCameraLocRevScaleNow(A3Object a,int scene)
    {setCameraLocNow(a.getLoc(),scene);
     setCameraQuatNow(a.getQuat(),scene);
     setCameraScaleNow(a.getScale(),scene);}

}
