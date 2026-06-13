package jp.sourceforge.acerola3d.a3;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.media.j3d.*;
import javax.vecmath.*;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * 
 */
public class JA3Canvas2 extends TransCanvas3D implements A3CanvasInterface {
    private static final long serialVersionUID = 1L;

    A3VirtualUniverse universe;

    /**
     * (500,500)の大きさのJA3Canvasを作成します。
     */
    public static JA3Canvas2 createJA3Canvas() {
        return createJA3Canvas(500,500);
    }

    /**
     * (w,h)の大きさのJA3Canvasを作ります。
     */
    public static JA3Canvas2 createJA3Canvas(int w,int h) {
        GraphicsDevice gd = createGraphicsDevice();
        GraphicsConfigTemplate3D gct3d = createGraphicsConfigTemplate3D();
        return createJA3Canvas(gct3d,gd,w,h);
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
     * (w,h)の大きさのA3Canvasを作ります。
     */
    public static JA3Canvas2 createJA3Canvas(GraphicsConfigTemplate3D t,GraphicsDevice d,int w,int h) {
        return new JA3Canvas2(t,d,w,h);
    }

    /**
     * (500,500)の大きさのA3Canvasを作ります。
     */
    public JA3Canvas2() {
        this(500,500);
    }

    /**
     * (w,h)の大きさのA3Canvasを作ります。
     */
    public JA3Canvas2(int w,int h) {
        this(createGraphicsConfigTemplate3D(),createGraphicsDevice(),w,h);
    }

    static GraphicsConfigTemplate3D forceA23init(GraphicsConfigTemplate3D t) {
        jp.sourceforge.acerola3d.A23.initA23();//2014,11/10追加(FrustumCulling=falseのためだけに)
        return t;
    }

    /**
     * GraphicsConfigTemplate3DとGraphicsDeviceオブジェクトを指定して
     * (w,h)の大きさのA3Canvasを作ります。
     */
    public JA3Canvas2(GraphicsConfigTemplate3D t,GraphicsDevice d,int w,int h) {
        super(forceA23init(t),d,false);
        setPreferredSize(new Dimension(w,h));
        setSize(w,h);
        this.canvasIF = this;
        //本当は、以下の処理はここでやりたいところだけど、
        //NullPointerExceptionが出るので、addNotifyメソッドに
        //入れてあげたらうまくいった。でも場合によってはまずいかも。
        //universe = new A3VirtualUniverse(this);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        universe = new A3VirtualUniverse(this);
        enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK);
        enableEvents(java.awt.AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }
    public TimerBehavior getTimerBehavior() {
        return universe.getTimerBehavior();
    }

    // A3Objectの追加と削除
    @Override
    public void add(A3Object a) {
        universe.add(a);
    }

    @Override
    public void del(A3Object a) {
        universe.del(a);
    }

    @Override
    public void delAll() {
        universe.delAll();
    }

    @Override
    public void delAll(int scene) {
        universe.delAll(scene);
    }

    @Override
    public void setBackground(A3Object a) {
        universe.setBackground(a);
    }

    @Override
    public void delBackground() {
        universe.delBackground();
    }

    @Override
    public void setAvatar(A3Object a) {
        universe.setAvatar(a);
    }

    @Override
    public A3Object getAvatar() {
        return universe.getAvatar();
    }

    // リスナ設定のラッパーメソッド
    @Override
    public void addA3Listener(A3Listener l) {
        universe.addA3Listener(l);
    }

    @Override
    public void removeA3Listener(A3Listener l) {
        universe.addA3Listener(l);
    }

    @Override
    public void setDefaultCameraLoc(double x,double y,double z) {
        universe.setDefaultCameraLoc(x,y,z);
    }

    @Override
    public void setDefaultCameraLoc(Vector3d loc) {
        universe.setDefaultCameraLoc(loc);
    }

    @Override
    public void setDefaultCameraQuat(double x,double y,double z,double w) {
        universe.setDefaultCameraQuat(x,y,z,w);
    }

    @Override
    public void setDefaultCameraQuat(Quat4d quat) {
        universe.setDefaultCameraQuat(quat);
    }

    @Override
    public void setDefaultCameraRot(double x,double y,double z) {
        universe.setDefaultCameraRot(x,y,z);
    }

    @Override
    public void setDefaultCameraRot(Vector3d rot) {
        universe.setDefaultCameraRot(rot);
    }

    @Override
    public void setDefaultCameraRev(double x,double y,double z) {
        universe.setDefaultCameraRev(x,y,z);
    }

    @Override
    public void setDefaultCameraRev(Vector3d rev) {
        universe.setDefaultCameraRev(rev);
    }

    @Override
    public void setDefaultCameraScale(double s) {
        universe.setDefaultCameraScale(s);
    }

    @Override
    public void resetCamera() {
        universe.resetCamera();
    }

    @Override
    public void setCameraLoc(double x,double y,double z) {
        universe.setCameraLoc(x,y,z);
    }

    @Override
    public void setCameraLoc(Vector3d loc) {
        universe.setCameraLoc(loc);
    }

    @Override
    public void setCameraLocImmediately(double x,double y,double z) {
        universe.setCameraLocImmediately(x,y,z);
    }

    @Override
    public void setCameraLocImmediately(Vector3d loc) {
        universe.setCameraLocImmediately(loc);
    }

    @Override
    public void addCameraLoc(double x,double y,double z) {
        universe.addCameraLoc(x,y,z);
    }

    @Override
    public void addCameraLoc(Vector3d loc) {
        universe.addCameraLoc(loc);
    }

    @Override
    public void addCameraLocImmediately(double x,double y,double z) {
        universe.addCameraLocImmediately(x,y,z);
    }

    @Override
    public void addCameraLocImmediately(Vector3d loc) {
        universe.addCameraLocImmediately(loc);
    }

    @Override
    public void moveCameraForward(double l) {
        universe.moveCameraForward(l);
    }

    @Override
    public void moveCameraForwardImmediately(double l) {
        universe.moveCameraForwardImmediately(l);
    }

    @Override
    public void moveCameraBackward(double l) {
        universe.moveCameraBackward(l);
    }

    @Override
    public void moveCameraBackwardImmediately(double l) {
        universe.moveCameraBackwardImmediately(l);
    }

    @Override
    public void moveCameraRight(double l) {
        universe.moveCameraRight(l);
    }

    @Override
    public void moveCameraRightImmediately(double l) {
        universe.moveCameraRightImmediately(l);
    }

    @Override
    public void moveCameraLeft(double l) {
        universe.moveCameraLeft(l);
    }

    @Override
    public void moveCameraLeftImmediately(double l) {
        universe.moveCameraLeftImmediately(l);
    }

    @Override
    public void moveCameraUp(double l) {
        universe.moveCameraUp(l);
    }

    @Override
    public void moveCameraUpImmediately(double l) {
        universe.moveCameraUpImmediately(l);
    }

    @Override
    public void moveCameraDown(double l) {
        universe.moveCameraDown(l);
    }

    @Override
    public void moveCameraDownImmediately(double l) {
        universe.moveCameraDownImmediately(l);
    }

    @Override
    public void moveCameraTo(Vector3d v, double l) {
        universe.moveCameraTo(v,l);
    }

    @Override
    public void moveCameraTo(double x, double y, double z, double l) {
        universe.moveCameraTo(x,y,z,l);
    }

    @Override
    public void moveCameraTo(A3Object a, double l) {
        universe.moveCameraTo(a,l);
    }

    @Override
    public void moveCameraToImmediately(Vector3d v, double l) {
        universe.moveCameraToImmediately(v,l);
    }

    @Override
    public void moveCameraToImmediately(double x, double y, double z, double l) {
        universe.moveCameraToImmediately(x,y,z,l);
    }

    @Override
    public void moveCameraToImmediately(A3Object a, double l) {
        universe.moveCameraToImmediately(a,l);
    }

    @Override
    public void moveCameraForward(double l,int scene) {
        universe.moveCameraForward(l,scene);
    }

    @Override
    public void moveCameraForwardImmediately(double l,int scene) {
        universe.moveCameraForwardImmediately(l,scene);
    }

    @Override
    public void moveCameraBackward(double l,int scene) {
        universe.moveCameraBackward(l,scene);
    }

    @Override
    public void moveCameraBackwardImmediately(double l,int scene) {
        universe.moveCameraBackwardImmediately(l,scene);
    }

    @Override
    public void moveCameraRight(double l,int scene) {
        universe.moveCameraRight(l,scene);
    }

    @Override
    public void moveCameraRightImmediately(double l,int scene) {
        universe.moveCameraRightImmediately(l,scene);
    }

    @Override
    public void moveCameraLeft(double l,int scene) {
        universe.moveCameraLeft(l,scene);
    }

    @Override
    public void moveCameraLeftImmediately(double l,int scene) {
        universe.moveCameraLeftImmediately(l,scene);
    }

    @Override
    public void moveCameraUp(double l,int scene) {
        universe.moveCameraUp(l,scene);
    }

    @Override
    public void moveCameraUpImmediately(double l,int scene) {
        universe.moveCameraUpImmediately(l,scene);
    }

    @Override
    public void moveCameraDown(double l,int scene) {
        universe.moveCameraDown(l,scene);
    }

    @Override
    public void moveCameraDownImmediately(double l,int scene) {
        universe.moveCameraDownImmediately(l,scene);
    }

    @Override
    public void moveCameraTo(Vector3d v, double l, int scene) {
        universe.moveCameraTo(v,l,scene);
    }

    @Override
    public void moveCameraTo(double x, double y, double z, double l, int scene) {
        universe.moveCameraTo(x,y,z,l,scene);
    }

    @Override
    public void moveCameraTo(A3Object a, double l, int scene) {
        universe.moveCameraTo(a,l,scene);
    }

    @Override
    public void moveCameraToImmediately(Vector3d v, double l, int scene) {
        universe.moveCameraToImmediately(v,l,scene);
    }

    @Override
    public void moveCameraToImmediately(double x, double y, double z, double l, int scene) {
        universe.moveCameraToImmediately(x,y,z,l,scene);
    }

    @Override
    public void moveCameraToImmediately(A3Object a, double l, int scene) {
        universe.moveCameraToImmediately(a,l,scene);
    }

    @Override
    public Vector3d getCameraLoc() {
        return universe.getCameraLoc();
    }

    @Override
    public Vector3d getCameraTargetLoc() {
        return universe.getCameraTargetLoc();
    }

    @Override
    public void setCameraQuat(double x,double y,double z,double w) {
        universe.setCameraQuat(x,y,z,w);
    }

    @Override
    public void setCameraQuat(Quat4d quat) {
        universe.setCameraQuat(quat);
    }

    @Override
    public void setCameraQuatImmediately(double x,double y,double z,double w) {
        universe.setCameraQuatImmediately(x,y,z,w);
    }

    @Override
    public void setCameraQuatImmediately(Quat4d quat) {
        universe.setCameraQuatImmediately(quat);
    }

    @Override
    public void mulCameraQuat(double x,double y,double z,double w) {
        universe.mulCameraQuat(x,y,z,w);
    }

    @Override
    public void mulCameraQuat(Quat4d quat) {
        universe.mulCameraQuat(quat);
    }

    @Override
    public void mulCameraQuatImmediately(double x,double y,double z,double w) {
        universe.mulCameraQuatImmediately(x,y,z,w);
    }

    @Override
    public void mulCameraQuatImmediately(Quat4d quat) {
        universe.mulCameraQuatImmediately(quat);
    }

    @Override
    public Quat4d getCameraQuat() {
        return universe.getCameraQuat();
    }

    @Override
    public Quat4d getCameraTargetQuat() {
        return universe.getCameraTargetQuat();
    }

    @Override
    public void setCameraRot(double x,double y,double z) {
        universe.setCameraRot(x,y,z);
    }

    @Override
    public void setCameraRot(Vector3d rot) {
        universe.setCameraRot(rot);
    }

    @Override
    public void setCameraRotImmediately(double x,double y,double z) {
        universe.setCameraRotImmediately(x,y,z);
    }

    @Override
    public void setCameraRotImmediately(Vector3d rot) {
        universe.setCameraRotImmediately(rot);
    }

    @Override
    public void mulCameraRot(double x,double y,double z) {
        universe.mulCameraRot(x,y,z);
    }

    @Override
    public void mulCameraRot(Vector3d rot) {
        universe.mulCameraRot(rot);
    }

    @Override
    public void mulCameraRotImmediately(double x,double y,double z) {
        universe.mulCameraRotImmediately(x,y,z);
    }

    @Override
    public void mulCameraRotImmediately(Vector3d rot) {
        universe.mulCameraRotImmediately(rot);
    }

    @Override
    public Vector3d getCameraRot() {
        return universe.getCameraRot();
    }

    @Override
    public Vector3d getCameraTargetRot() {
        return universe.getCameraTargetRot();
    }

    @Override
    public void setCameraRev(double x,double y,double z) {
        universe.setCameraRev(x,y,z);
    }

    @Override
    public void setCameraRev(Vector3d rev) {
        universe.setCameraRev(rev);
    }

    @Override
    public void setCameraRevImmediately(double x,double y,double z) {
        universe.setCameraRevImmediately(x,y,z);
    }

    @Override
    public void setCameraRevImmediately(Vector3d rev) {
        universe.setCameraRevImmediately(rev);
    }

    @Override
    public void mulCameraRev(double x,double y,double z) {
        universe.mulCameraRev(x,y,z);
    }

    @Override
    public void mulCameraRev(Vector3d rev) {
        universe.mulCameraRev(rev);
    }

    @Override
    public void mulCameraRevImmediately(double x,double y,double z) {
        universe.mulCameraRevImmediately(x,y,z);
    }

    @Override
    public void mulCameraRevImmediately(Vector3d rev) {
        universe.mulCameraRevImmediately(rev);
    }

    @Override
    public Vector3d getCameraRev() {
        return universe.getCameraRev();
    }

    @Override
    public Vector3d getCameraTargetRev() {
        return universe.getCameraTargetRev();
    }

    @Override
    public void setCameraScale(double s) {
        universe.setCameraScale(s);
    }

    @Override
    public void setCameraScaleImmediately(double s) {
        universe.setCameraScaleImmediately(s);
    }

    @Override
    public void mulCameraScale(double s) {
        universe.mulCameraScale(s);
    }

    @Override
    public void mulCameraScaleImmediately(double s) {
        universe.mulCameraScaleImmediately(s);
    }

    @Override
    public double getCameraScale() {
        return universe.getCameraScale();
    }

    @Override
    public double getCameraTargetScale() {
        return universe.getCameraTargetScale();
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt) {
        universe.setCameraLookAtPoint(lookAt);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt) {
        universe.setCameraLookAtPointImmediately(lookAt);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z) {
        universe.setCameraLookAtPoint(x,y,z);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z) {
        universe.setCameraLookAtPointImmediately(x,y,z);
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up) {
        universe.setCameraLookAtPoint(lookAt,up);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up) {
        universe.setCameraLookAtPointImmediately(lookAt,up);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up) {
        universe.setCameraLookAtPoint(x,y,z,up);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up) {
        universe.setCameraLookAtPointImmediately(x,y,z,up);
    }


    @Override
    public void setHeadLightEnable(boolean b) {
        universe.setHeadLightEnable(b);
    }
    // マウスナビゲーションのモード設定
    @Override
    public void setNavigationMode(NaviMode m,Object...params) {
        universe.setNavigationMode(m,params);
    }

    @Override
    public void setNavigationSpeed(double s) {
        universe.setNavigationSpeed(s);
    }

    @Override
    public double getNavigationSpeed() {
        return universe.getNavigationSpeed();
    }

    @Override
    public void setA3Controller(A3Controller c) {
        universe.setA3Controller(c);
    }
//  ----------座標変換とピッキングのためのラッパーメソッド---------
    @Override
    public Point3d canvasToVirtualCS(int x,int y) {
        return universe.canvasToVirtualCS(x,y);
    }

    @Override
    public Point3d canvasToVirtualCS(int x,int y,double dis) {
        return universe.canvasToVirtualCS(x,y,dis);
    }

    @Override
    public Point3d canvasToPhysicalCS(int x,int y) {
        return universe.canvasToPhysicalCS(x,y);
    }

    @Override
    public Point3d canvasToPhysicalCS(int x,int y,double dis) {
        return universe.canvasToPhysicalCS(x,y,dis);
    }

    @Override
    public Vector3d physicalCSToVirtualCS(Vector3d v) {
        return universe.physicalCSToVirtualCS(v);
    }

    @Override
    public Point physicalCSToCanvas(Point3d p) {
        return universe.physicalCSToCanvas(p);
    }

    @Override
    public Point virtualCSToCanvas(Point3d p) {
        return universe.virtualCSToCanvas(p);
    }

    @Override
    public Vector3d virtualCSToPhysicalCS(Vector3d v) {
        return universe.virtualCSToPhysicalCS(v);
    }

    @Override
    public Vector3d getCameraUnitVecX() {
        return universe.getCameraUnitVecX();
    }

    @Override
    public Vector3d getCameraUnitVecY() {
        return universe.getCameraUnitVecY();
    }

    @Override
    public Vector3d getCameraUnitVecZ() {
        return universe.getCameraUnitVecZ();
    }

    @Override
    public A3Object pickA3(int x,int y) {
        return universe.pickingBehavior.pickA3(x,y);
    }

    @Override
    public A3Object pick(Vector3d origin,Vector3d dir) {
        return universe.pickingBehavior.pickA3(origin,dir);
    }
//  ----------J3DGraphics2D(文字描画など)---------
    @Override
    public void add(Component2D c) {
        universe.add(c);
    }

    @Override
    public void del(Component2D c) {
        universe.del(c);
    }

    @Override
    public void add(Component2D c,int scene) {
        universe.add(c,scene);
    }

    @Override
    public void del(Component2D c,int scene) {
        universe.del(c,scene);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Component2D cs[] = universe.getComponents2D().toArray(new Component2D[0]);
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
    public void setUpdateInterval(long l) {
        universe.setUpdateInterval(l);
    }

    @Override
    public long getUpdateInterval() {
        return universe.getUpdateInterval();
    }

    @Override
    public void waitForUpdate(long timeout) {
        universe.waitForUpdate(timeout);
    }

    @Override
    public void insertTaskIntoRenderingLoop(Runnable task) {
        super.insertTaskIntoRenderingLoop(task);
    }

    @Override
    public void removeTaskFromRenderingLoop(Runnable task) {
        super.removeTaskFromRenderingLoop(task);
    }

    @Override
    public void insertTaskIntoTimerLoop(Runnable task) {
        universe.insertTaskIntoTimerLoop(task);
    }

    @Override
    public void removeTaskFromTimerLoop(Runnable task) {
        universe.removeTaskFromTimerLoop(task);
    }

    @Override
    public void setCameraInterpolateRatio(double ir) {
        universe.setCameraInterpolateRatio(ir);
    }

    @Override
    public void setDepthBufferFreezeTransparent(boolean b) {
        universe.setDepthBufferFreezeTransparent(b);
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

//  ----------シーン関係のメソッド---------
    @Override
    public void prepareScene(int scene) {
        universe.prepareScene(scene);
    }

    @Override
    public void changeActiveScene(int s) {
        universe.changeActiveScene(s);
    }

    @Override
    public void add(A3Object a,int s) {
        universe.add(a,s);
    }

    @Override
    public void del(A3Object a,int s) {
        universe.del(a,s);
    }

    @Override
    public void setDefaultCameraLoc(double x,double y,double z,int scene) {
        universe.setDefaultCameraLoc(x,y,z);
    }

    @Override
    public void setDefaultCameraLoc(Vector3d loc,int scene) {
        universe.setDefaultCameraLoc(loc,scene);
    }

    @Override
    public void setDefaultCameraQuat(double x,double y,double z,double w,int scene) {
        universe.setDefaultCameraQuat(x,y,z,w,scene);
    }

    @Override
    public void setDefaultCameraQuat(Quat4d quat,int scene) {
        universe.setDefaultCameraQuat(quat,scene);
    }

    @Override
    public void setDefaultCameraRot(double x,double y,double z,int scene) {
        universe.setDefaultCameraRot(x,y,z,scene);
    }

    @Override
    public void setDefaultCameraRot(Vector3d rot,int scene) {
        universe.setDefaultCameraRot(rot,scene);
    }

    @Override
    public void setDefaultCameraRev(double x,double y,double z,int scene) {
        universe.setDefaultCameraRev(x,y,z,scene);
    }

    @Override
    public void setDefaultCameraRev(Vector3d rev,int scene) {
        universe.setDefaultCameraRev(rev,scene);
    }

    @Override
    public void setDefaultCameraScale(double s,int scene) {
        universe.setDefaultCameraScale(s,scene);
    }

    @Override
    public void resetCamera(int scene) {
        universe.resetCamera(scene);
    }

    @Override
    public void setCameraLoc(double x,double y,double z,int scene) {
        universe.setCameraLoc(x,y,z,scene);
    }

    @Override
    public void setCameraLoc(Vector3d loc,int scene) {
        universe.setCameraLoc(loc,scene);
    }

    @Override
    public void setCameraLocImmediately(double x,double y,double z,int scene) {
        universe.setCameraLocImmediately(x,y,z,scene);
    }

    @Override
    public void setCameraLocImmediately(Vector3d loc,int scene) {
        universe.setCameraLocImmediately(loc,scene);
    }

    @Override
    public void addCameraLoc(double x,double y,double z,int scene) {
        universe.addCameraLoc(x,y,z,scene);
    }

    @Override
    public void addCameraLoc(Vector3d loc,int scene) {
        universe.addCameraLoc(loc,scene);
    }

    @Override
    public void addCameraLocImmediately(double x,double y,double z,int scene) {
        universe.addCameraLocImmediately(x,y,z,scene);
    }

    @Override
    public void addCameraLocImmediately(Vector3d loc,int scene) {
        universe.setCameraLocImmediately(loc,scene);
    }

    @Override
    public Vector3d getCameraLoc(int scene) {
        return universe.getCameraLoc(scene);
    }

    @Override
    public Vector3d getCameraTargetLoc(int scene) {
        return universe.getCameraTargetLoc(scene);
    }

    @Override
    public void setCameraQuat(double x,double y,double z,double w,int scene) {
        universe.setCameraQuat(x,y,z,w,scene);
    }

    @Override
    public void setCameraQuat(Quat4d quat,int scene) {
        universe.setCameraQuat(quat,scene);
    }

    @Override
    public void setCameraQuatImmediately(double x,double y,double z,double w,int scene) {
        universe.setCameraQuatImmediately(x,y,z,w,scene);
    }

    @Override
    public void setCameraQuatImmediately(Quat4d quat,int scene) {
        universe.setCameraQuatImmediately(quat,scene);
    }

    @Override
    public void mulCameraQuat(double x,double y,double z,double w,int scene) {
        universe.mulCameraQuat(x,y,z,w,scene);
    }

    @Override
    public void mulCameraQuat(Quat4d quat,int scene) {
        universe.mulCameraQuat(quat,scene);
    }

    @Override
    public void mulCameraQuatImmediately(double x,double y,double z,double w,int scene) {
        universe.mulCameraQuatImmediately(x,y,z,w,scene);
    }

    @Override
    public void mulCameraQuatImmediately(Quat4d quat,int scene) {
        universe.mulCameraQuatImmediately(quat,scene);
    }

    @Override
    public Quat4d getCameraQuat(int scene) {
        return universe.getCameraQuat(scene);
    }

    @Override
    public Quat4d getCameraTargetQuat(int scene) {
        return universe.getCameraTargetQuat(scene);
    }

    @Override
    public void setCameraRot(double x,double y,double z,int scene) {
        universe.setCameraRot(x,y,z,scene);
    }

    @Override
    public void setCameraRot(Vector3d rot,int scene) {
        universe.setCameraRot(rot,scene);
    }

    @Override
    public void setCameraRotImmediately(double x,double y,double z,int scene) {
        universe.setCameraRotImmediately(x,y,z,scene);
    }

    @Override
    public void setCameraRotImmediately(Vector3d rot,int scene) {
        universe.setCameraRotImmediately(rot,scene);
    }

    @Override
    public void mulCameraRot(double x,double y,double z,int scene) {
        universe.mulCameraRot(x,y,z,scene);
    }

    @Override
    public void mulCameraRot(Vector3d rot,int scene) {
        universe.mulCameraRot(rot,scene);
    }

    @Override
    public void mulCameraRotImmediately(double x,double y,double z,int scene) {
        universe.mulCameraRotImmediately(x,y,z,scene);
    }

    @Override
    public void mulCameraRotImmediately(Vector3d rot,int scene) {
        universe.setCameraRotImmediately(rot,scene);
    }

    @Override
    public Vector3d getCameraRot(int scene) {
        return universe.getCameraRot();
    }

    @Override
    public Vector3d getCameraTargetRot(int scene) {
        return universe.getCameraTargetRot();
    }

    @Override
    public void setCameraRev(double x,double y,double z,int scene) {
        universe.setCameraRev(x,y,z,scene);
    }

    @Override
    public void setCameraRev(Vector3d rev,int scene) {
        universe.setCameraRev(rev,scene);
    }

    @Override
    public void setCameraRevImmediately(double x,double y,double z,int scene) {
        universe.setCameraRevImmediately(x,y,z,scene);
    }

    @Override
    public void setCameraRevImmediately(Vector3d rev,int scene) {
        universe.setCameraRevImmediately(rev,scene);
    }

    @Override
    public void mulCameraRev(double x,double y,double z,int scene) {
        universe.mulCameraRev(x,y,z,scene);
    }

    @Override
    public void mulCameraRev(Vector3d rev,int scene) {
        universe.mulCameraRev(rev,scene);
    }

    @Override
    public void mulCameraRevImmediately(double x,double y,double z,int scene) {
        universe.mulCameraRevImmediately(x,y,z,scene);
    }

    @Override
    public void mulCameraRevImmediately(Vector3d rev,int scene) {
        universe.setCameraRevImmediately(rev,scene);
    }

    @Override
    public Vector3d getCameraRev(int scene) {
        return universe.getCameraRev();
    }

    @Override
    public Vector3d getCameraTargetRev(int scene) {
        return universe.getCameraTargetRev();
    }

    @Override
    public void setCameraScale(double s,int scene) {
        universe.setCameraScale(s,scene);
    }

    @Override
    public void setCameraScaleImmediately(double s,int scene) {
        universe.setCameraScaleImmediately(s,scene);
    }

    @Override
    public void mulCameraScale(double s,int scene) {
        universe.mulCameraScale(s,scene);
    }

    @Override
    public void mulCameraScaleImmediately(double s,int scene) {
        universe.mulCameraScaleImmediately(s,scene);
    }

    @Override
    public double getCameraScale(int scene) {
        return universe.getCameraScale(scene);
    }

    @Override
    public double getCameraTargetScale(int scene) {
        return universe.getCameraTargetScale(scene);
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,int scene) {
        universe.setCameraLookAtPoint(lookAt,scene);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt,int scene) {
        universe.setCameraLookAtPointImmediately(lookAt,scene);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,int scene) {
        universe.setCameraLookAtPoint(x,y,z,scene);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,int scene) {
        universe.setCameraLookAtPointImmediately(x,y,z,scene);
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up,int scene) {
        universe.setCameraLookAtPoint(lookAt,up,scene);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up,int scene) {
        universe.setCameraLookAtPointImmediately(lookAt,up,scene);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up,int scene) {
        universe.setCameraLookAtPoint(x,y,z,up,scene);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up,int scene) {
        universe.setCameraLookAtPointImmediately(x,y,z,up,scene);
    }

    @Override
    public void setNavigationMode(int scene,NaviMode m,Object...params) {
        universe.setNavigationMode(scene,m,params);
    }

    @Override
    public void setNavigationSpeed(double s,int scene) {
        universe.setNavigationSpeed(s,scene);
    }

    @Override
    public double getNavigationSpeed(int scene) {
        return universe.getNavigationSpeed(scene);
    }

    @Override
    public void setA3Controller(A3Controller c,int scene) {
        universe.setA3Controller(c,scene);
    }

    @Override
    public void setBackground(A3Object a,int scene) {
        universe.setBackground(a,scene);
    }

    @Override
    public void delBackground(int scene) {
        universe.delBackground(scene);
    }

    @Override
    public void setAvatar(A3Object a,int scene) {
        universe.setAvatar(a,scene);
    }

    @Override
    public A3Object getAvatar(int scene) {
        return universe.getAvatar(scene);
    }
//  ---------- LockedA3の処理 ----------
    @Override
    public void addLockedA3(A3Object a) {
        universe.addLockedA3(a);
    }

    @Override
    public void delLockedA3(A3Object a) {
        universe.delLockedA3(a);
    }

    @Override
    public void delAllLockedA3() {
        universe.delAllLockedA3();
    }

    @Override
    public void addLockedA3(A3Object a,int scene) {
        universe.addLockedA3(a,scene);
    }

    @Override
    public void delLockedA3(A3Object a,int scene) {
        universe.delLockedA3(a,scene);
    }

    @Override
    public void delAllLockedA3(int scene) {
        universe.delAllLockedA3(scene);
    }

    @Override
    public void setUpperDirection(A3Object.UpperDirection d) {
        universe.setUpperDirection(d);
    }

    @Override
    public void setUpperDirection(A3Object.UpperDirection d,int scene) {
        universe.setUpperDirection(d,scene);
    }

    @Override
    public A3Object.UpperDirection getUpperDirection() {
        return universe.getUpperDirection();
    }

    @Override
    public A3Object.UpperDirection getUpperDirection(int scene) {
        return universe.getUpperDirection(scene);
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
    public void addA3SubCanvas(A3CanvasInterface sc) {
        universe.addA3SubCanvas(sc);
    }

    @Override
    public void setProjectionMode(ProjectionMode m) {
        universe.setProjectionMode(m);
    }

    @Override
    public void setCanvasWidthInPWorld(double s) {
        universe.setCanvasWidthInPWorld(s);
    }

    @Override
    public void setFieldOfView(double f) {
        universe.setFieldOfView(f);
    }

    @Override
    public TransformGroup getTransformGroupForViewPlatform() {
        return universe.getTransformGroupForViewPlatform();
    }

    @Override
    public Canvas3D getCanvas3D() {
        return super.getOffscreenCanvas3D();
    }

    @Override
    public void setSoundGain(double g) {
        universe.setSoundGain(g);
    }

    @Override
    public double getSoundGain() {
        return universe.getSoundGain();
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public BranchGroup getBranchGroupForViewPlatform() {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        return null;
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setPickingBehavior(PickingBehavior pb) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    /**
     * このメソッドはダミーのメソッドなので使用しないで下さい。
     * アプリケーション側のプログラム互換性のために導入されましたが、
     * 後のバージョンでは廃止する予定です。
     */
    @Override
    public void setVirtualUniverse(A3VirtualUniverse vu) {
        // TODO Version3系列ではこのメソッドが削除できるようにすべし。
        
    }

    @Override
    public A3VirtualUniverse getVirtualUniverse() {
        return universe;
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
        universe.turnCameraUp(deg);
    }
    @Override
    public void turnCameraUpNow(double deg) {
        universe.turnCameraUpNow(deg);
    }
    @Override
    public void turnCameraUp(double deg,int scene) {
        universe.turnCameraUp(deg,scene);
    }
    @Override
    public void turnCameraUpNow(double deg,int scene) {
        universe.turnCameraUpNow(deg,scene);
    }
    @Override
    public void turnCameraDown(double deg) {
        universe.turnCameraDown(deg);
    }
    @Override
    public void turnCameraDownNow(double deg) {
        universe.turnCameraDownNow(deg);
    }
    @Override
    public void turnCameraDown(double deg,int scene) {
        universe.turnCameraDown(deg,scene);
    }
    @Override
    public void turnCameraDownNow(double deg,int scene) {
        universe.turnCameraDownNow(deg,scene);
    }
    @Override
    public void turnCameraRight(double deg) {
        universe.turnCameraRight(deg);
    }
    @Override
    public void turnCameraRightNow(double deg) {
        universe.turnCameraRightNow(deg);
    }
    @Override
    public void turnCameraRight(double deg,int scene) {
        universe.turnCameraRight(deg,scene);
    }
    @Override
    public void turnCameraRightNow(double deg,int scene) {
        universe.turnCameraRightNow(deg,scene);
    }
    @Override
    public void turnCameraLeft(double deg) {
        universe.turnCameraLeft(deg);
    }
    @Override
    public void turnCameraLeftNow(double deg) {
        universe.turnCameraLeftNow(deg);
    }
    @Override
    public void turnCameraLeft(double deg,int scene) {
        universe.turnCameraLeft(deg,scene);
    }
    @Override
    public void turnCameraLeftNow(double deg,int scene) {
        universe.turnCameraLeftNow(deg,scene);
    }
    @Override
    public void rollCameraRight(double deg) {
        universe.rollCameraRight(deg);
    }
    @Override
    public void rollCameraRightNow(double deg) {
        universe.rollCameraRightNow(deg);
    }
    @Override
    public void rollCameraRight(double deg,int scene) {
        universe.rollCameraRight(deg,scene);
    }
    @Override
    public void rollCameraRightNow(double deg,int scene) {
        universe.rollCameraRightNow(deg,scene);
    }
    @Override
    public void rollCameraLeft(double deg) {
        universe.rollCameraLeft(deg);
    }
    @Override
    public void rollCameraLeftNow(double deg) {
        universe.rollCameraLeftNow(deg);
    }
    @Override
    public void rollCameraLeft(double deg,int scene) {
        universe.rollCameraLeft(deg,scene);
    }
    @Override
    public void rollCameraLeftNow(double deg,int scene) {
        universe.rollCameraLeftNow(deg,scene);
    }
    //****************************************
    @Override
    public void setCameraLookAtPoint(A3Object a) {
        universe.setCameraLookAtPoint(a);
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,Vector3d up) {
        universe.setCameraLookAtPoint(a,up);
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,int scene) {
        universe.setCameraLookAtPoint(a,scene);
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,Vector3d up,int scene) {
        universe.setCameraLookAtPoint(a,up,scene);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a) {
        universe.setCameraLookAtPointNow(a);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,Vector3d up) {
        universe.setCameraLookAtPointNow(a,up);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,int scene) {
        universe.setCameraLookAtPointNow(a,scene);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,Vector3d up,int scene) {
        universe.setCameraLookAtPointNow(a,up);
    }
    @Override
    public ArrayList<A3Object> getAll() {
        return universe.getAll();
    }
    @Override
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c) {
        return universe.getAll(c);
    }
    @Override
    public ArrayList<A3Object> getAll(int scene) {
        return universe.getAll(scene);
    }
    @Override
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c,int scene) {
        return universe.getAll(c,scene);
    }
//-------
    @Override
    public void turnCameraTo(double dirX,double dirY,double dirZ) {
        universe.turnCameraTo(dirX,dirY,dirZ);
    }
    @Override
    public void turnCameraTo(Vector3d dir) {
        universe.turnCameraTo(dir);
    }
    @Override
    public void turnCameraToNow(double dirX,double dirY,double dirZ) {
        universe.turnCameraToNow(dirX,dirY,dirZ);
    }
    @Override
    public void turnCameraToNow(Vector3d dir) {
        universe.turnCameraToNow(dir);
    }
    @Override
    public void turnCameraTo(double dirX,double dirY,double dirZ,int scene) {
        universe.turnCameraTo(dirX,dirY,dirZ,scene);
    }
    @Override
    public void turnCameraTo(Vector3d dir,int scene) {
        universe.turnCameraTo(dir,scene);
    }
    @Override
    public void turnCameraToNow(double dirX,double dirY,double dirZ,int scene) {
        universe.turnCameraToNow(dirX,dirY,dirZ,scene);
    }
    @Override
    public void turnCameraToNow(Vector3d dir,int scene) {
        universe.turnCameraToNow(dir,scene);
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
