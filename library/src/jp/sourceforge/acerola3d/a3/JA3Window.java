package jp.sourceforge.acerola3d.a3;

import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.io.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * A3Objectを表示するためのWindowです。このウィンドウには
 * デフォルトでJA3Canvasが内包されており、
 * JA3Canvasと同名のメソッドは単に内包されているA3Canvas
 * の同じ名前のメソッドを呼び出しているだけです。
 * このクラスのaddメソッドを用いてA3Objectを登録すると、
 * A3ObjectがこのWindowの中に表示されるように
 * なっています。また、3D仮想空間におけるカメラの操作
 * も、このクラスのメソッドを通じて行うことができます。
 * それと、A3Listenerの登録先にもなります。
 */
public class JA3Window extends JFrame implements A3CanvasInterface {
    private static final long serialVersionUID = 1L;
    JA3Canvas canvas;

    /**
     * (w,h)の大きさのA3Windowを生成します。
     */
    public JA3Window(int w,int h) {
        jp.sourceforge.acerola3d.A23.initA23();//2014,11/10追加(FrustumCulling=falseのためだけに)
        final int ww = w;
        final int hh = h;
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent we) {
                            dispose();
                            System.exit(0);
                        }
                    });
                    canvas = JA3Canvas.createJA3Canvas(ww,hh);
                    getContentPane().add(canvas);
                    pack();
                    setVisible(true);
                    canvas.requestFocusInWindow();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * このA3Windowに内蔵されるJA3Canvasを返します。
     */
    public JA3Canvas getJA3Canvas() {
        return canvas;
    }

    // A3Objectの追加と削除
    @Override
    public void add(A3Object a) {
        canvas.add(a);
    }

    @Override
    public void del(A3Object a) {
        canvas.del(a);
    }

    @Override
    public void delAll() {
        canvas.delAll();
    }

    @Override
    public void delAll(int scene) {
        canvas.delAll(scene);
    }

    @Override
    public void setBackground(A3Object a) {
        canvas.setBackground(a);
    }

    @Override
    public void delBackground() {
        canvas.delBackground();
    }

    @Override
    public void setAvatar(A3Object a) {
        canvas.setAvatar(a);
    }

    @Override
    public A3Object getAvatar() {
        return canvas.getAvatar();
    }

    // リスナ設定のラッパーメソッド
    @Override
    public void addA3Listener(A3Listener l) {
        canvas.addA3Listener(l);
    }

    @Override
    public void removeA3Listener(A3Listener l) {
        canvas.removeA3Listener(l);
    }

    @Override
    public void setDefaultCameraLoc(double x,double y,double z) {
        canvas.setDefaultCameraLoc(x,y,z);
    }

    @Override
    public void setDefaultCameraLoc(Vector3d loc) {
        canvas.setDefaultCameraLoc(loc);
    }

    @Override
    public void setDefaultCameraQuat(double x,double y,double z,double w) {
        canvas.setDefaultCameraQuat(x,y,z,w);
    }

    @Override
    public void setDefaultCameraQuat(Quat4d quat) {
        canvas.setDefaultCameraQuat(quat);
    }

    @Override
    public void setDefaultCameraRot(double x,double y,double z) {
        canvas.setDefaultCameraRot(x,y,z);
    }

    @Override
    public void setDefaultCameraRot(Vector3d rot) {
        canvas.setDefaultCameraRot(rot);
    }

    @Override
    public void setDefaultCameraRev(double x,double y,double z) {
        canvas.setDefaultCameraRev(x,y,z);
    }

    @Override
    public void setDefaultCameraRev(Vector3d rev) {
        canvas.setDefaultCameraRev(rev);
    }

    @Override
    public void setDefaultCameraScale(double s) {
        canvas.setDefaultCameraScale(s);
    }

    @Override
    public void resetCamera() {
        canvas.resetCamera();
    }

    @Override
    public void setCameraLoc(double x,double y,double z) {
        canvas.setCameraLoc(x,y,z);
    }

    @Override
    public void setCameraLoc(Vector3d loc) {
        canvas.setCameraLoc(loc);
    }

    @Override
    public void setCameraLocImmediately(double x,double y,double z) {
        canvas.setCameraLocImmediately(x,y,z);
    }

    @Override
    public void setCameraLocImmediately(Vector3d loc) {
        canvas.setCameraLocImmediately(loc);
    }

    @Override
    public void addCameraLoc(double x,double y,double z) {
        canvas.addCameraLoc(x,y,z);
    }

    @Override
    public void addCameraLoc(Vector3d loc) {
        canvas.addCameraLoc(loc);
    }

    @Override
    public void addCameraLocImmediately(double x,double y,double z) {
        canvas.addCameraLocImmediately(x,y,z);
    }

    @Override
    public void addCameraLocImmediately(Vector3d loc) {
        canvas.addCameraLocImmediately(loc);
    }

    @Override
    public void moveCameraForward(double l) {
        canvas.moveCameraForward(l);
    }

    @Override
    public void moveCameraForwardImmediately(double l) {
        canvas.moveCameraForwardImmediately(l);
    }

    @Override
    public void moveCameraBackward(double l) {
        canvas.moveCameraBackward(l);
    }

    @Override
    public void moveCameraBackwardImmediately(double l) {
        canvas.moveCameraBackwardImmediately(l);
    }

    @Override
    public void moveCameraRight(double l) {
        canvas.moveCameraRight(l);
    }

    @Override
    public void moveCameraRightImmediately(double l) {
        canvas.moveCameraRightImmediately(l);
    }

    @Override
    public void moveCameraLeft(double l) {
        canvas.moveCameraLeft(l);
    }

    @Override
    public void moveCameraLeftImmediately(double l) {
        canvas.moveCameraLeftImmediately(l);
    }

    @Override
    public void moveCameraUp(double l) {
        canvas.moveCameraUp(l);
    }

    @Override
    public void moveCameraUpImmediately(double l) {
        canvas.moveCameraUpImmediately(l);
    }

    @Override
    public void moveCameraDown(double l) {
        canvas.moveCameraDown(l);
    }

    @Override
    public void moveCameraDownImmediately(double l) {
        canvas.moveCameraDownImmediately(l);
    }

    @Override
    public void moveCameraTo(Vector3d v, double l) {
        canvas.moveCameraTo(v,l);
    }

    @Override
    public void moveCameraTo(double x, double y, double z, double l) {
        canvas.moveCameraTo(x,y,z,l);
    }

    @Override
    public void moveCameraTo(A3Object a, double l) {
        canvas.moveCameraTo(a,l);
    }

    @Override
    public void moveCameraToImmediately(Vector3d v, double l) {
        canvas.moveCameraToImmediately(v,l);
    }

    @Override
    public void moveCameraToImmediately(double x, double y, double z, double l) {
        canvas.moveCameraToImmediately(x,y,z,l);
    }

    @Override
    public void moveCameraToImmediately(A3Object a, double l) {
        canvas.moveCameraToImmediately(a,l);
    }

    @Override
    public void moveCameraForward(double l,int scene) {
        canvas.moveCameraForward(l,scene);
    }

    @Override
    public void moveCameraForwardImmediately(double l,int scene) {
        canvas.moveCameraForwardImmediately(l,scene);
    }

    @Override
    public void moveCameraBackward(double l,int scene) {
        canvas.moveCameraBackward(l,scene);
    }

    @Override
    public void moveCameraBackwardImmediately(double l,int scene) {
        canvas.moveCameraBackwardImmediately(l,scene);
    }

    @Override
    public void moveCameraRight(double l,int scene) {
        canvas.moveCameraRight(l,scene);
    }

    @Override
    public void moveCameraRightImmediately(double l,int scene) {
        canvas.moveCameraRightImmediately(l,scene);
    }

    @Override
    public void moveCameraLeft(double l,int scene) {
        canvas.moveCameraLeft(l,scene);
    }

    @Override
    public void moveCameraLeftImmediately(double l,int scene) {
        canvas.moveCameraLeftImmediately(l,scene);
    }

    @Override
    public void moveCameraUp(double l,int scene) {
        canvas.moveCameraUp(l,scene);
    }

    @Override
    public void moveCameraUpImmediately(double l,int scene) {
        canvas.moveCameraUpImmediately(l,scene);
    }

    @Override
    public void moveCameraDown(double l,int scene) {
        canvas.moveCameraDown(l,scene);
    }

    @Override
    public void moveCameraDownImmediately(double l,int scene) {
        canvas.moveCameraDownImmediately(l,scene);
    }

    @Override
    public void moveCameraTo(Vector3d v, double l, int scene) {
        canvas.moveCameraTo(v,l,scene);
    }

    @Override
    public void moveCameraTo(double x, double y, double z, double l, int scene) {
        canvas.moveCameraTo(x,y,z,l,scene);
    }

    @Override
    public void moveCameraTo(A3Object a, double l, int scene) {
        canvas.moveCameraTo(a,l,scene);
    }

    @Override
    public void moveCameraToImmediately(Vector3d v, double l, int scene) {
        canvas.moveCameraToImmediately(v,l,scene);
    }

    @Override
    public void moveCameraToImmediately(double x, double y, double z, double l, int scene) {
        canvas.moveCameraToImmediately(x,y,z,l,scene);
    }

    @Override
    public void moveCameraToImmediately(A3Object a, double l, int scene) {
        canvas.moveCameraToImmediately(a,l,scene);
    }

    @Override
    public Vector3d getCameraLoc() {
        return canvas.getCameraLoc();
    }

    @Override
    public Vector3d getCameraTargetLoc() {
        return canvas.getCameraTargetLoc();
    }

    @Override
    public void setCameraQuat(double x,double y,double z,double w) {
        canvas.setCameraQuat(x,y,z,w);
    }

    @Override
    public void setCameraQuat(Quat4d quat) {
        canvas.setCameraQuat(quat);
    }

    @Override
    public void setCameraQuatImmediately(double x,double y,double z,double w) {
        canvas.setCameraQuatImmediately(x,y,z,w);
    }

    @Override
    public void setCameraQuatImmediately(Quat4d quat) {
        canvas.setCameraQuatImmediately(quat);
    }

    @Override
    public void mulCameraQuat(double x,double y,double z,double w) {
        canvas.setCameraQuat(x,y,z,w);
    }

    @Override
    public void mulCameraQuat(Quat4d quat) {
        canvas.setCameraQuat(quat);
    }

    @Override
    public void mulCameraQuatImmediately(double x,double y,double z,double w) {
        canvas.setCameraQuatImmediately(x,y,z,w);
    }

    @Override
    public void mulCameraQuatImmediately(Quat4d quat) {
        canvas.setCameraQuatImmediately(quat);
    }

    @Override
    public Quat4d getCameraQuat() {
        return canvas.getCameraQuat();
    }

    @Override
    public Quat4d getCameraTargetQuat() {
        return canvas.getCameraTargetQuat();
    }

    @Override
    public void setCameraRot(double x, double y, double z) {
        canvas.setCameraRot(x,y,z);
    }

    @Override
    public void setCameraRot(Vector3d rot) {
        canvas.setCameraRot(rot);
    }

    @Override
    public void setCameraRotImmediately(double x, double y, double z) {
        canvas.setCameraRotImmediately(x,y,z);
    }

    @Override
    public void setCameraRotImmediately(Vector3d rot) {
        canvas.setCameraRotImmediately(rot);
    }

    @Override
    public void mulCameraRot(double x, double y, double z) {
        canvas.setCameraRot(x,y,z);
    }

    @Override
    public void mulCameraRot(Vector3d rot) {
        canvas.setCameraRot(rot);
    }

    @Override
    public void mulCameraRotImmediately(double x, double y, double z) {
        canvas.setCameraRotImmediately(x,y,z);
    }

    @Override
    public void mulCameraRotImmediately(Vector3d rot) {
        canvas.setCameraRotImmediately(rot);
    }

    @Override
    public Vector3d getCameraRot() {
        return canvas.getCameraRot();
    }

    @Override
    public Vector3d getCameraTargetRot() {
        return canvas.getCameraTargetRot();
    }

    @Override
    public void setCameraRev(double x, double y, double z) {
        canvas.setCameraRev(x,y,z);
    }

    @Override
    public void setCameraRev(Vector3d rev) {
        canvas.setCameraRev(rev);
    }

    @Override
    public void setCameraRevImmediately(double x, double y, double z) {
        canvas.setCameraRevImmediately(x,y,z);
    }

    @Override
    public void setCameraRevImmediately(Vector3d rev) {
        canvas.setCameraRevImmediately(rev);
    }

    @Override
    public void mulCameraRev(double x, double y, double z) {
        canvas.setCameraRev(x,y,z);
    }

    @Override
    public void mulCameraRev(Vector3d rev) {
        canvas.setCameraRev(rev);
    }

    @Override
    public void mulCameraRevImmediately(double x, double y, double z) {
        canvas.setCameraRevImmediately(x,y,z);
    }

    @Override
    public void mulCameraRevImmediately(Vector3d rev) {
        canvas.setCameraRevImmediately(rev);
    }

    @Override
    public Vector3d getCameraRev() {
        return canvas.getCameraRev();
    }

    @Override
    public Vector3d getCameraTargetRev() {
        return canvas.getCameraTargetRev();
    }

    @Override
    public void setCameraScale(double s) {
        canvas.setCameraScale(s);
    }

    @Override
    public void setCameraScaleImmediately(double s) {
        canvas.setCameraScaleImmediately(s);
    }

    @Override
    public void mulCameraScale(double s) {
        canvas.mulCameraScale(s);
    }

    @Override
    public void mulCameraScaleImmediately(double s) {
        canvas.mulCameraScaleImmediately(s);
    }

    @Override
    public double getCameraScale() {
        return canvas.getCameraScale();
    }

    @Override
    public double getCameraTargetScale() {
        return canvas.getCameraTargetScale();
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt) {
        canvas.setCameraLookAtPoint(lookAt);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt) {
        canvas.setCameraLookAtPointImmediately(lookAt);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z) {
        canvas.setCameraLookAtPoint(x,y,z);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z) {
        canvas.setCameraLookAtPointImmediately(x,y,z);
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up) {
        canvas.setCameraLookAtPoint(lookAt,up);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up) {
        canvas.setCameraLookAtPointImmediately(lookAt,up);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up) {
        canvas.setCameraLookAtPoint(x,y,z,up);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up) {
        canvas.setCameraLookAtPointImmediately(x,y,z,up);
    }

    @Override
    public void setHeadLightEnable(boolean b) {
        canvas.setHeadLightEnable(b);
    }

    @Override
    public void setNavigationMode(A3Canvas.NaviMode m,Object...params) {
        canvas.setNavigationMode(m,params);
    }

    @Override
    public void setNavigationSpeed(double s) {
        canvas.setNavigationSpeed(s);
    }

    @Override
    public double getNavigationSpeed() {
        return canvas.getNavigationSpeed();
    }

    @Override
    public void setA3Controller(A3Controller c) {
        canvas.setA3Controller(c);
    }
//  ----------座標変換とピッキングのためのラッパーメソッド---------
    @Override
    public Point3d canvasToVirtualCS(int x,int y) {
        return canvas.canvasToVirtualCS(x,y);
    }

    @Override
    public Point3d canvasToVirtualCS(int x,int y,double dis) {
        return canvas.canvasToVirtualCS(x,y,dis);
    }

    @Override
    public Point3d canvasToPhysicalCS(int x,int y) {
        return canvas.canvasToPhysicalCS(x,y);
    }

    @Override
    public Point3d canvasToPhysicalCS(int x,int y,double dis) {
        return canvas.canvasToPhysicalCS(x,y,dis);
    }

    @Override
    public Vector3d physicalCSToVirtualCS(Vector3d v) {
        return canvas.physicalCSToVirtualCS(v);
    }

    @Override
    public Point physicalCSToCanvas(Point3d p) {
        return canvas.physicalCSToCanvas(p);
    }

    @Override
    public Point virtualCSToCanvas(Point3d p) {
        return canvas.virtualCSToCanvas(p);
    }

    @Override
    public Vector3d virtualCSToPhysicalCS(Vector3d v) {
        return canvas.virtualCSToPhysicalCS(v);
    }

    @Override
    public Vector3d getCameraUnitVecX() {
        return canvas.getCameraUnitVecX();
    }

    @Override
    public Vector3d getCameraUnitVecY() {
        return canvas.getCameraUnitVecY();
    }

    @Override
    public Vector3d getCameraUnitVecZ() {
        return canvas.getCameraUnitVecZ();
    }

    @Override
    public A3Object pickA3(int x,int y) {
        return canvas.pickA3(x,y);
    }

    @Override
    public A3Object pick(Vector3d origin,Vector3d dir) {
        return canvas.pick(origin,dir);
    }

    @Override
    public void saveImage(File file) throws IOException {
        canvas.saveImage(file);
    }

    @Override
    public BufferedImage snapshot() {
        return canvas.snapshot();
    }

    @Override
    public int getFPS() {
        return canvas.getFPS();
    }

    @Override
    public void setUpdateInterval(long l) {
        canvas.setUpdateInterval(l);
    }

    @Override
    public long getUpdateInterval() {
        return canvas.getUpdateInterval();
    }

    @Override
    public void waitForUpdate(long timeout) {
        canvas.waitForUpdate(timeout);
    }

    @Override
    public void insertTaskIntoRenderingLoop(Runnable task) {
        canvas.insertTaskIntoRenderingLoop(task);
    }

    @Override
    public void removeTaskFromRenderingLoop(Runnable task) {
        canvas.removeTaskFromRenderingLoop(task);
    }

    @Override
    public void insertTaskIntoTimerLoop(Runnable task) {
        canvas.insertTaskIntoTimerLoop(task);
    }

    @Override
    public void removeTaskFromTimerLoop(Runnable task) {
        canvas.removeTaskFromTimerLoop(task);
    }

    @Override
    public void setCameraInterpolateRatio(double ir) {
        canvas.setCameraInterpolateRatio(ir);
    }

    @Override
    public void setDepthBufferFreezeTransparent(boolean b) {
        canvas.setDepthBufferFreezeTransparent(b);
    }
//  ----------シーン関係のメソッド---------
    @Override
    public void prepareScene(int s) {
        canvas.prepareScene(s);
    }

    @Override
    public void changeActiveScene(int s) {
        canvas.changeActiveScene(s);
    }

    @Override
    public void add(A3Object a,int s) {
        canvas.add(a,s);
    }

    @Override
    public void del(A3Object a,int s) {
        canvas.del(a,s);
    }

    @Override
    public void setDefaultCameraLoc(double x,double y,double z,int scene) {
        canvas.setDefaultCameraLoc(x,y,z);
    }

    @Override
    public void setDefaultCameraLoc(Vector3d loc,int scene) {
        canvas.setDefaultCameraLoc(loc,scene);
    }

    @Override
    public void setDefaultCameraQuat(double x,double y,double z,double w,int scene) {
        canvas.setDefaultCameraQuat(x,y,z,w,scene);
    }

    @Override
    public void setDefaultCameraQuat(Quat4d quat,int scene) {
        canvas.setDefaultCameraQuat(quat,scene);
    }

    @Override
    public void setDefaultCameraRot(double x,double y,double z,int scene) {
        canvas.setDefaultCameraRot(x,y,z,scene);
    }

    @Override
    public void setDefaultCameraRot(Vector3d rot,int scene) {
        canvas.setDefaultCameraRot(rot,scene);
    }

    @Override
    public void setDefaultCameraRev(double x,double y,double z,int scene) {
        canvas.setDefaultCameraRev(x,y,z,scene);
    }

    @Override
    public void setDefaultCameraRev(Vector3d rev,int scene) {
        canvas.setDefaultCameraRev(rev,scene);
    }

    @Override
    public void setDefaultCameraScale(double s,int scene) {
        canvas.setDefaultCameraScale(s,scene);
    }

    @Override
    public void resetCamera(int scene) {
        canvas.resetCamera(scene);
    }

    @Override
    public void setCameraLoc(double x,double y,double z,int scene) {
        canvas.setCameraLoc(x,y,z,scene);
    }

    @Override
    public void setCameraLoc(Vector3d loc,int scene) {
        canvas.setCameraLoc(loc,scene);
    }

    @Override
    public void setCameraLocImmediately(double x,double y,double z,int scene) {
        canvas.setCameraLocImmediately(x,y,z,scene);
    }

    @Override
    public void setCameraLocImmediately(Vector3d loc,int scene) {
        canvas.setCameraLocImmediately(loc,scene);
    }

    @Override
    public void addCameraLoc(double x,double y,double z,int scene) {
        canvas.addCameraLoc(x,y,z,scene);
    }

    @Override
    public void addCameraLoc(Vector3d loc,int scene) {
        canvas.addCameraLoc(loc,scene);
    }

    @Override
    public void addCameraLocImmediately(double x,double y,double z,int scene) {
        canvas.addCameraLocImmediately(x,y,z,scene);
    }

    @Override
    public void addCameraLocImmediately(Vector3d loc,int scene) {
        canvas.addCameraLocImmediately(loc,scene);
    }

    @Override
    public Vector3d getCameraLoc(int scene) {
        return canvas.getCameraLoc(scene);
    }

    @Override
    public Vector3d getCameraTargetLoc(int scene) {
        return canvas.getCameraTargetLoc(scene);
    }

    @Override
    public void setCameraQuat(double x,double y,double z,double w,int scene) {
        canvas.setCameraQuat(x,y,z,w,scene);
    }

    @Override
    public void setCameraQuat(Quat4d quat,int scene) {
        canvas.setCameraQuat(quat,scene);
    }

    @Override
    public void setCameraQuatImmediately(double x,double y,double z,double w,int scene) {
        canvas.setCameraQuatImmediately(x,y,z,w,scene);
    }

    @Override
    public void setCameraQuatImmediately(Quat4d quat,int scene) {
        canvas.setCameraQuatImmediately(quat,scene);
    }

    @Override
    public void mulCameraQuat(double x,double y,double z,double w,int scene) {
        canvas.mulCameraQuat(x,y,z,w,scene);
    }

    @Override
    public void mulCameraQuat(Quat4d quat,int scene) {
        canvas.mulCameraQuat(quat,scene);
    }

    @Override
    public void mulCameraQuatImmediately(double x,double y,double z,double w,int scene) {
        canvas.mulCameraQuatImmediately(x,y,z,w,scene);
    }

    @Override
    public void mulCameraQuatImmediately(Quat4d quat,int scene) {
        canvas.mulCameraQuatImmediately(quat,scene);
    }

    @Override
    public Quat4d getCameraQuat(int scene) {
        return canvas.getCameraQuat(scene);
    }

    @Override
    public Quat4d getCameraTargetQuat(int scene) {
        return canvas.getCameraTargetQuat(scene);
    }

    @Override
    public void setCameraRot(double x,double y,double z,int scene) {
        canvas.setCameraRot(x,y,z,scene);
    }

    @Override
    public void setCameraRot(Vector3d rot,int scene) {
        canvas.setCameraRot(rot,scene);
    }

    @Override
    public void setCameraRotImmediately(double x,double y,double z,int scene) {
        canvas.setCameraRotImmediately(x,y,z,scene);
    }

    @Override
    public void setCameraRotImmediately(Vector3d rot,int scene) {
        canvas.setCameraRotImmediately(rot,scene);
    }

    @Override
    public void mulCameraRot(double x,double y,double z,int scene) {
        canvas.mulCameraRot(x,y,z,scene);
    }

    @Override
    public void mulCameraRot(Vector3d rot,int scene) {
        canvas.mulCameraRot(rot,scene);
    }

    @Override
    public void mulCameraRotImmediately(double x,double y,double z,int scene) {
        canvas.mulCameraRotImmediately(x,y,z,scene);
    }

    @Override
    public void mulCameraRotImmediately(Vector3d rot,int scene) {
        canvas.mulCameraRotImmediately(rot,scene);
    }

    @Override
    public Vector3d getCameraRot(int scene) {
        return canvas.getCameraRot(scene);
    }

    @Override
    public Vector3d getCameraTargetRot(int scene) {
        return canvas.getCameraTargetRot(scene);
    }

    @Override
    public void setCameraRev(double x,double y,double z,int scene) {
        canvas.setCameraRev(x,y,z,scene);
    }

    @Override
    public void setCameraRev(Vector3d rev,int scene) {
        canvas.setCameraRev(rev,scene);
    }

    @Override
    public void setCameraRevImmediately(double x,double y,double z,int scene) {
        canvas.setCameraRevImmediately(x,y,z,scene);
    }

    @Override
    public void setCameraRevImmediately(Vector3d rev,int scene) {
        canvas.setCameraRevImmediately(rev,scene);
    }

    @Override
    public void mulCameraRev(double x,double y,double z,int scene) {
        canvas.mulCameraRev(x,y,z,scene);
    }

    @Override
    public void mulCameraRev(Vector3d rev,int scene) {
        canvas.mulCameraRev(rev,scene);
    }

    @Override
    public void mulCameraRevImmediately(double x,double y,double z,int scene) {
        canvas.mulCameraRevImmediately(x,y,z,scene);
    }

    @Override
    public void mulCameraRevImmediately(Vector3d rev,int scene) {
        canvas.mulCameraRevImmediately(rev,scene);
    }

    @Override
    public Vector3d getCameraRev(int scene) {
        return canvas.getCameraRev(scene);
    }

    @Override
    public Vector3d getCameraTargetRev(int scene) {
        return canvas.getCameraTargetRev(scene);
    }

    @Override
    public void setCameraScale(double s,int scene) {
        canvas.setCameraScale(s,scene);
    }

    @Override
    public void setCameraScaleImmediately(double s,int scene) {
        canvas.setCameraScaleImmediately(s,scene);
    }

    @Override
    public void mulCameraScale(double s,int scene) {
        canvas.mulCameraScale(s,scene);
    }

    @Override
    public void mulCameraScaleImmediately(double s,int scene) {
        canvas.mulCameraScaleImmediately(s,scene);
    }

    @Override
    public double getCameraScale(int scene) {
        return canvas.getCameraScale(scene);
    }

    @Override
    public double getCameraTargetScale(int scene) {
        return canvas.getCameraTargetScale(scene);
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,int scene) {
        canvas.setCameraLookAtPoint(lookAt,scene);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt,int scene) {
        canvas.setCameraLookAtPointImmediately(lookAt,scene);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,int scene) {
        canvas.setCameraLookAtPoint(x,y,z,scene);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,int scene) {
        canvas.setCameraLookAtPointImmediately(x,y,z,scene);
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up,int scene) {
        canvas.setCameraLookAtPoint(lookAt,up,scene);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up,int scene) {
        canvas.setCameraLookAtPointImmediately(lookAt,up,scene);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up,int scene) {
        canvas.setCameraLookAtPoint(x,y,z,up,scene);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up,int scene) {
        canvas.setCameraLookAtPointImmediately(x,y,z,up,scene);
    }

    @Override
    public void setNavigationMode(int scene,NaviMode m,Object...params) {
        canvas.setNavigationMode(scene,m,params);
    }

    @Override
    public void setNavigationSpeed(double s,int scene) {
        canvas.setNavigationSpeed(s,scene);
    }

    @Override
    public double getNavigationSpeed(int scene) {
        return canvas.getNavigationSpeed(scene);
    }

    @Override
    public void setA3Controller(A3Controller c,int scene) {
        canvas.setA3Controller(c,scene);
    }

    @Override
    public void setBackground(A3Object a,int scene) {
        canvas.setBackground(a,scene);
    }

    @Override
    public void delBackground(int scene) {
        canvas.delBackground(scene);
    }

    @Override
    public void setAvatar(A3Object a,int scene) {
        canvas.setAvatar(a,scene);
    }

    @Override
    public A3Object getAvatar(int scene) {
        return canvas.getAvatar(scene);
    }
//  ---------- Component2D関係 ----------
    @Override
    public void add(Component2D c) {
        canvas.add(c);;
    }

    @Override
    public void del(Component2D c) {
        canvas.del(c);
    }

    @Override
    public void add(Component2D c,int scene) {
        canvas.add(c,scene);
    }

    @Override
    public void del(Component2D c,int scene) {
        canvas.del(c,scene);
    }
//  ---------- KeyListener関係 ----------
    /**
     * KeyListenerを登録します。実際にはこのA3WindowのKeyListenerを
     * 登録するのではなく、このA3Windowが表示しているA3Canvasに登録
     * するようにオーバーライドしています。
     */
    @Override
    public void addKeyListener(KeyListener l) {
        canvas.addKeyListener(l);
    }
    /**
     * 指定されたKeyListenerの登録を抹消します。実際にはこのA3Windowの
     * KeyListenerの登録を抹消するのではなく、このA3Windowが表示している
     * A3Canvasのに登録されているKeyListenerを抹消するようにオーバーライド
     * しています。
     */
    @Override
    public void removeKeyListener(KeyListener l) {
        canvas.removeKeyListener(l);
    }
//  ---------- LockedA3の処理 ----------
    @Override
    public void addLockedA3(A3Object a) {
        canvas.addLockedA3(a);
    }

    @Override
    public void delLockedA3(A3Object a) {
        canvas.delLockedA3(a);
    }

    @Override
    public void delAllLockedA3() {
        canvas.delAllLockedA3();
    }

    @Override
    public void addLockedA3(A3Object a,int scene) {
        canvas.addLockedA3(a,scene);
    }

    @Override
    public void delLockedA3(A3Object a,int scene) {
        canvas.delLockedA3(a,scene);
    }

    @Override
    public void delAllLockedA3(int scene) {
        canvas.delAllLockedA3(scene);
    }

    @Override
    public void setUpperDirection(A3Object.UpperDirection d) {
        canvas.setUpperDirection(d);
    }

    @Override
    public void setUpperDirection(A3Object.UpperDirection d,int scene) {
        canvas.setUpperDirection(d,scene);
    }

    @Override
    public A3Object.UpperDirection getUpperDirection() {
        return canvas.getUpperDirection();
    }

    @Override
    public A3Object.UpperDirection getUpperDirection(int scene) {
        return canvas.getUpperDirection(scene);
    }

    @Override
    public Dimension getCanvasSize() {
        return canvas.getCanvasSize();
    }

    @Override
    public void cleanUp() {
        ;
    }

    @Override
    public void addA3SubCanvas(A3CanvasInterface sc) {
        canvas.addA3SubCanvas(sc);
    }

    @Override
    public void setProjectionMode(ProjectionMode m) {
        canvas.setProjectionMode(m);
    }

    @Override
    public void setCanvasWidthInPWorld(double s) {
        canvas.setCanvasWidthInPWorld(s);
    }

    @Override
    public void setFieldOfView(double f) {
        canvas.setFieldOfView(f);
    }

    @Override
    public TransformGroup getTransformGroupForViewPlatform() {
        return canvas.getTransformGroupForViewPlatform();
    }

    @Override
    public Canvas3D getCanvas3D() {
        return canvas.getCanvas3D();
    }

    @Override
    public void setSoundGain(double g) {
        canvas.setSoundGain(g);
    }

    @Override
    public double getSoundGain() {
        return canvas.getSoundGain();
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
        return canvas.getVirtualUniverse();
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
        canvas.turnCameraUp(deg);
    }
    @Override
    public void turnCameraUpNow(double deg) {
        canvas.turnCameraUpNow(deg);
    }
    @Override
    public void turnCameraUp(double deg,int scene) {
        canvas.turnCameraUp(deg,scene);
    }
    @Override
    public void turnCameraUpNow(double deg,int scene) {
        canvas.turnCameraUpNow(deg,scene);
    }
    @Override
    public void turnCameraDown(double deg) {
        canvas.turnCameraDown(deg);
    }
    @Override
    public void turnCameraDownNow(double deg) {
        canvas.turnCameraDownNow(deg);
    }
    @Override
    public void turnCameraDown(double deg,int scene) {
        canvas.turnCameraDown(deg,scene);
    }
    @Override
    public void turnCameraDownNow(double deg,int scene) {
        canvas.turnCameraDownNow(deg,scene);
    }
    @Override
    public void turnCameraRight(double deg) {
        canvas.turnCameraRight(deg);
    }
    @Override
    public void turnCameraRightNow(double deg) {
        canvas.turnCameraRightNow(deg);
    }
    @Override
    public void turnCameraRight(double deg,int scene) {
        canvas.turnCameraRight(deg,scene);
    }
    @Override
    public void turnCameraRightNow(double deg,int scene) {
        canvas.turnCameraRightNow(deg,scene);
    }
    @Override
    public void turnCameraLeft(double deg) {
        canvas.turnCameraLeft(deg);
    }
    @Override
    public void turnCameraLeftNow(double deg) {
        canvas.turnCameraLeftNow(deg);
    }
    @Override
    public void turnCameraLeft(double deg,int scene) {
        canvas.turnCameraLeft(deg,scene);
    }
    @Override
    public void turnCameraLeftNow(double deg,int scene) {
        canvas.turnCameraLeftNow(deg,scene);
    }
    @Override
    public void rollCameraRight(double deg) {
        canvas.rollCameraRight(deg);
    }
    @Override
    public void rollCameraRightNow(double deg) {
        canvas.rollCameraRightNow(deg);
    }
    @Override
    public void rollCameraRight(double deg,int scene) {
        canvas.rollCameraRight(deg,scene);
    }
    @Override
    public void rollCameraRightNow(double deg,int scene) {
        canvas.rollCameraRightNow(deg,scene);
    }
    @Override
    public void rollCameraLeft(double deg) {
        canvas.rollCameraLeft(deg);
    }
    @Override
    public void rollCameraLeftNow(double deg) {
        canvas.rollCameraLeftNow(deg);
    }
    @Override
    public void rollCameraLeft(double deg,int scene) {
        canvas.rollCameraLeft(deg,scene);
    }
    @Override
    public void rollCameraLeftNow(double deg,int scene) {
        canvas.rollCameraLeftNow(deg,scene);
    }
    //****************************************
    @Override
    public void setCameraLookAtPoint(A3Object a) {
        canvas.setCameraLookAtPoint(a);
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,Vector3d up) {
        canvas.setCameraLookAtPoint(a,up);
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,int scene) {
        canvas.setCameraLookAtPoint(a,scene);
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,Vector3d up,int scene) {
        canvas.setCameraLookAtPoint(a,up,scene);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a) {
        canvas.setCameraLookAtPointNow(a);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,Vector3d up) {
        canvas.setCameraLookAtPointNow(a,up);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,int scene) {
        canvas.setCameraLookAtPointNow(a,scene);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,Vector3d up,int scene) {
        canvas.setCameraLookAtPointNow(a,up);
    }
    @Override
    public ArrayList<A3Object> getAll() {
        return canvas.getAll();
    }
    @Override
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c) {
        return canvas.getAll(c);
    }
    @Override
    public ArrayList<A3Object> getAll(int scene) {
        return canvas.getAll(scene);
    }
    @Override
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c,int scene) {
        return canvas.getAll(c,scene);
    }
//-------
    @Override
    public void turnCameraTo(double dirX,double dirY,double dirZ) {
        canvas.turnCameraTo(dirX,dirY,dirZ);
    }
    @Override
    public void turnCameraTo(Vector3d dir) {
        canvas.turnCameraTo(dir);
    }
    @Override
    public void turnCameraToNow(double dirX,double dirY,double dirZ) {
        canvas.turnCameraToNow(dirX,dirY,dirZ);
    }
    @Override
    public void turnCameraToNow(Vector3d dir) {
        canvas.turnCameraToNow(dir);
    }
    @Override
    public void turnCameraTo(double dirX,double dirY,double dirZ,int scene) {
        canvas.turnCameraTo(dirX,dirY,dirZ,scene);
    }
    @Override
    public void turnCameraTo(Vector3d dir,int scene) {
        canvas.turnCameraTo(dir,scene);
    }
    @Override
    public void turnCameraToNow(double dirX,double dirY,double dirZ,int scene) {
        canvas.turnCameraToNow(dirX,dirY,dirZ,scene);
    }
    @Override
    public void turnCameraToNow(Vector3d dir,int scene) {
        canvas.turnCameraToNow(dir,scene);
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
