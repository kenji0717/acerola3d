package jp.sourceforge.acerola3d.a3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyListener;
import javax.vecmath.*;
import java.io.*;
import javax.media.j3d.*;
import java.util.ArrayList;

/**
 * まったく実装を持たないA3CanvasInterfaceです．
 * 例えば，実際に3DCGを表示するプログラムと，
 * グラフィカルな表示を必要としない
 * サーバーサイドのプログラムを共存させる
 * ために使用することができます．
 */
public class DummyCanvas implements A3CanvasInterface {
    public DummyCanvas() {//2019,01/18追加(必要ないかもしれないけど)
        jp.sourceforge.acerola3d.A23.initA23();
    }
    public void add(A3Object a) {;}
    public void del(A3Object a) {;}
    public void delAll() {;}
    public void delAll(int scene) {;}
    public void setBackground(A3Object a) {;}
    public void delBackground() {;}
    public void setAvatar(A3Object a) {;}
    public A3Object getAvatar() {return null;}
    public void addA3Listener(A3Listener l) {;}
    public void removeA3Listener(A3Listener l) {;}
    public void addKeyListener(KeyListener l) {;}
    public void removeKeyListener(KeyListener l) {;}
    public void setDefaultCameraLoc(double x,double y,double z) {;}
    public void setDefaultCameraLoc(Vector3d loc) {;}
    public void setDefaultCameraQuat(double x,double y,double z,double w) {;}
    public void setDefaultCameraQuat(Quat4d quat) {;}
    public void setDefaultCameraRot(double x,double y,double z) {;}
    public void setDefaultCameraRot(Vector3d rot) {;}
    public void setDefaultCameraRev(double x,double y,double z) {;}
    public void setDefaultCameraRev(Vector3d rev) {;}
    public void setDefaultCameraScale(double s) {;}
    public void resetCamera() {;}
    public void setCameraLoc(double x,double y,double z) {;}
    public void setCameraLoc(Vector3d loc) {;}
    public void setCameraLocImmediately(double x,double y,double z) {;}
    public void setCameraLocImmediately(Vector3d loc) {;}
    public void addCameraLoc(double x,double y,double z) {;}
    public void addCameraLoc(Vector3d loc) {;}
    public void addCameraLocImmediately(double x,double y,double z) {;}
    public void addCameraLocImmediately(Vector3d loc) {;}
    public void moveCameraForward(double l) {;}
    public void moveCameraForwardImmediately(double l) {;}
    public void moveCameraBackward(double l) {;}
    public void moveCameraBackwardImmediately(double l) {;}
    public void moveCameraRight(double l) {;}
    public void moveCameraRightImmediately(double l) {;}
    public void moveCameraLeft(double l) {;}
    public void moveCameraLeftImmediately(double l) {;}
    public void moveCameraUp(double l) {;}
    public void moveCameraUpImmediately(double l) {;}
    public void moveCameraDown(double l) {;}
    public void moveCameraDownImmediately(double l) {;}
    public void moveCameraTo(Vector3d v, double l) {;}
    public void moveCameraTo(double x, double y, double z, double l) {;}
    public void moveCameraTo(A3Object a, double l) {;}
    public void moveCameraToImmediately(Vector3d v, double l) {;}
    public void moveCameraToImmediately(double x, double y, double z, double l) {;}
    public void moveCameraToImmediately(A3Object a, double l) {;}
    public void moveCameraForward(double l,int scene) {;}
    public void moveCameraForwardImmediately(double l,int scene) {;}
    public void moveCameraBackward(double l,int scene) {;}
    public void moveCameraBackwardImmediately(double l,int scene) {;}
    public void moveCameraRight(double l,int scene) {;}
    public void moveCameraRightImmediately(double l,int scene) {;}
    public void moveCameraLeft(double l,int scene) {;}
    public void moveCameraLeftImmediately(double l,int scene) {;}
    public void moveCameraUp(double l,int scene) {;}
    public void moveCameraUpImmediately(double l,int scene) {;}
    public void moveCameraDown(double l,int scene) {;}
    public void moveCameraDownImmediately(double l,int scene) {;}
    public void moveCameraTo(Vector3d v, double l,int scene) {;}
    public void moveCameraTo(double x, double y, double z, double l,int scene) {;}
    public void moveCameraTo(A3Object a, double l,int scene) {;}
    public void moveCameraToImmediately(Vector3d v, double l,int scene) {;}
    public void moveCameraToImmediately(double x, double y, double z, double l,int scene) {;}
    public void moveCameraToImmediately(A3Object a, double l,int scene) {;}
    public void turnCameraUp(double deg) {;}
    public void turnCameraUpNow(double deg) {;}
    public void turnCameraUp(double deg,int scene) {;}
    public void turnCameraUpNow(double deg,int scene) {;}
    public void turnCameraDown(double deg) {;}
    public void turnCameraDownNow(double deg) {;}
    public void turnCameraDown(double deg,int scene) {;}
    public void turnCameraDownNow(double deg,int scene) {;}
    public void turnCameraRight(double deg) {;}
    public void turnCameraRightNow(double deg) {;}
    public void turnCameraRight(double deg,int scene) {;}
    public void turnCameraRightNow(double deg,int scene) {;}
    public void turnCameraLeft(double deg) {;}
    public void turnCameraLeftNow(double deg) {;}
    public void turnCameraLeft(double deg,int scene) {;}
    public void turnCameraLeftNow(double deg,int scene) {;}
    public void rollCameraRight(double deg) {;}
    public void rollCameraRightNow(double deg) {;}
    public void rollCameraRight(double deg,int scene) {;}
    public void rollCameraRightNow(double deg,int scene) {;}
    public void rollCameraLeft(double deg) {;}
    public void rollCameraLeftNow(double deg) {;}
    public void rollCameraLeft(double deg,int scene) {;}
    public void rollCameraLeftNow(double deg,int scene) {;}
    public void turnCameraTo(double dirX,double dirY,double dirZ) {;}
    public void turnCameraTo(Vector3d dir) {;}
    public void turnCameraToNow(double dirX,double dirY,double dirZ) {;}
    public void turnCameraToNow(Vector3d dir) {;}
    public void turnCameraTo(double dirX,double dirY,double dirZ,int scene) {;}
    public void turnCameraTo(Vector3d dir,int scene) {;}
    public void turnCameraToNow(double dirX,double dirY,double dirZ,int scene) {;}
    public void turnCameraToNow(Vector3d dir,int scene) {;}
    public Vector3d getCameraLoc() {return null;}
    public Vector3d getCameraTargetLoc() {return null;}
    public void setCameraQuat(double x,double y,double z,double w) {;}
    public void setCameraQuat(Quat4d quat) {;}
    public void setCameraQuatImmediately(double x,double y,double z,double w) {;}
    public void setCameraQuatImmediately(Quat4d quat) {;}
    public void mulCameraQuat(double x,double y,double z,double w) {;}
    public void mulCameraQuat(Quat4d quat) {;}
    public void mulCameraQuatImmediately(double x,double y,double z,double w) {;}
    public void mulCameraQuatImmediately(Quat4d quat) {;}
    public Quat4d getCameraQuat() {return null;}
    public Quat4d getCameraTargetQuat() {return null;}
    public void setCameraRot(double x,double y,double z) {;}
    public void setCameraRot(Vector3d rot) {;}
    public void setCameraRotImmediately(double x,double y,double z) {;}
    public void setCameraRotImmediately(Vector3d rot) {;}
    public void mulCameraRot(double x,double y,double z) {;}
    public void mulCameraRot(Vector3d rot) {;}
    public void mulCameraRotImmediately(double x,double y,double z) {;}
    public void mulCameraRotImmediately(Vector3d rot) {;}
    public Vector3d getCameraRot() {return null;}
    public Vector3d getCameraTargetRot() {return null;}
    public void setCameraRev(double x,double y,double z) {;}
    public void setCameraRev(Vector3d rev) {;}
    public void setCameraRevImmediately(double x,double y,double z) {;}
    public void setCameraRevImmediately(Vector3d rev) {;}
    public void mulCameraRev(double x,double y,double z) {;}
    public void mulCameraRev(Vector3d rev) {;}
    public void mulCameraRevImmediately(double x,double y,double z) {;}
    public void mulCameraRevImmediately(Vector3d rev) {;}
    public Vector3d getCameraRev() {return null;}
    public Vector3d getCameraTargetRev() {return null;}
    public void setCameraScale(double s) {;}
    public void setCameraScaleImmediately(double s) {;}
    public void mulCameraScale(double s) {;}
    public void mulCameraScaleImmediately(double s) {;}
    public double getCameraScale() {return 0.0;}
    public double getCameraTargetScale() {return 0.0;}
    public void setCameraLookAtPoint(Vector3d lookAt) {;}
    public void setCameraLookAtPointImmediately(Vector3d lookAt) {;}
    public void setCameraLookAtPoint(double x,double y,double z) {;}
    public void setCameraLookAtPointImmediately(double x,double y,double z) {;}
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up) {;}
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up) {;}
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up) {;}
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up) {;}
    public void setCameraLookAtPoint(A3Object a) {;}
    public void setCameraLookAtPoint(A3Object a,Vector3d up) {;}
    public void setHeadLightEnable(boolean b) {;}
    public void setNavigationMode(NaviMode m,Object...params) {;}
    public void setNavigationSpeed(double s) {;}
    public double getNavigationSpeed() {return 0.0;}
    public void setA3Controller(A3Controller c) {;}
    public Point3d canvasToVirtualCS(int x,int y) {return null;}
    public Point3d canvasToVirtualCS(int x,int y,double dis) {return null;}
    public Point3d canvasToPhysicalCS(int x,int y) {return null;}
    public Point3d canvasToPhysicalCS(int x,int y,double dis) {return null;}
    public Vector3d physicalCSToVirtualCS(Vector3d v) {return null;}
    public Point physicalCSToCanvas(Point3d p) {return null;}
    public Point virtualCSToCanvas(Point3d p) {return null;}
    public Vector3d virtualCSToPhysicalCS(Vector3d v) {return null;}
    public Vector3d getCameraUnitVecX() {return null;}
    public Vector3d getCameraUnitVecY() {return null;}
    public Vector3d getCameraUnitVecZ() {return null;}
    public A3Object pickA3(int x,int y) {return null;}
    public A3Object pick(Vector3d origin,Vector3d dir) {return null;}
    public void add(Component2D c) {;}
    public void del(Component2D c) {;}
    public void add(Component2D c,int scene) {;}
    public void del(Component2D c,int scene) {;}
    public void saveImage(File file) throws IOException {;}
    public BufferedImage snapshot() {return null;}
    public void prepareScene(int s) {;}
    public void changeActiveScene(int s) {;}
    public void add(A3Object a,int s) {;}
    public void del(A3Object a,int s) {;}
    public void setDefaultCameraLoc(double x,double y,double z,int scene) {;}
    public void setDefaultCameraLoc(Vector3d loc,int scene) {;}
    public void setDefaultCameraQuat(double x,double y,double z,double w,int scene) {;}
    public void setDefaultCameraQuat(Quat4d quat,int scene) {;}
    public void setDefaultCameraRot(double x,double y,double z,int scene) {;}
    public void setDefaultCameraRot(Vector3d rot,int scene) {;}
    public void setDefaultCameraRev(double x,double y,double z,int scene) {;}
    public void setDefaultCameraRev(Vector3d rev,int scene) {;}
    public void setDefaultCameraScale(double s,int scene) {;}
    public void resetCamera(int scene) {;}
    public void setCameraLoc(double x,double y,double z,int scene) {;}
    public void setCameraLoc(Vector3d loc,int scene) {;}
    public void setCameraLocImmediately(double x,double y,double z,int scene) {;}
    public void setCameraLocImmediately(Vector3d loc,int scene) {;}
    public void addCameraLoc(double x,double y,double z,int scene) {;}
    public void addCameraLoc(Vector3d loc,int scene) {;}
    public void addCameraLocImmediately(double x,double y,double z,int scene) {;}
    public void addCameraLocImmediately(Vector3d loc,int scene) {;}
    public Vector3d getCameraLoc(int scene) {return null;}
    public Vector3d getCameraTargetLoc(int scene) {return null;}
    public void setCameraQuat(double x,double y,double z,double w,int scene) {;}
    public void setCameraQuat(Quat4d quat,int scene) {;}
    public void setCameraQuatImmediately(double x,double y,double z,double w,int scene) {;}
    public void setCameraQuatImmediately(Quat4d quat,int scene) {;}
    public void mulCameraQuat(double x,double y,double z,double w,int scene) {;}
    public void mulCameraQuat(Quat4d quat,int scene) {;}
    public void mulCameraQuatImmediately(double x,double y,double z,double w,int scene) {;}
    public void mulCameraQuatImmediately(Quat4d quat,int scene) {;}
    public Quat4d getCameraQuat(int scene) {return null;}
    public Quat4d getCameraTargetQuat(int scene) {return null;}
    public void setCameraRot(double x,double y,double z,int scene) {;}
    public void setCameraRot(Vector3d rot,int scene) {;}
    public void setCameraRotImmediately(double x,double y,double z,int scene) {;}
    public void setCameraRotImmediately(Vector3d rot,int scene) {;}
    public void mulCameraRot(double x,double y,double z,int scene) {;}
    public void mulCameraRot(Vector3d rot,int scene) {;}
    public void mulCameraRotImmediately(double x,double y,double z,int scene) {;}
    public void mulCameraRotImmediately(Vector3d rot,int scene) {;}
    public Vector3d getCameraRot(int scene) {return null;}
    public Vector3d getCameraTargetRot(int scene) {return null;}
    public void setCameraRev(double x,double y,double z,int scene) {;}
    public void setCameraRev(Vector3d rev,int scene) {;}
    public void setCameraRevImmediately(double x,double y,double z,int scene) {;}
    public void setCameraRevImmediately(Vector3d rev,int scene) {;}
    public void mulCameraRev(double x,double y,double z,int scene) {;}
    public void mulCameraRev(Vector3d rev,int scene) {;}
    public void mulCameraRevImmediately(double x,double y,double z,int scene) {;}
    public void mulCameraRevImmediately(Vector3d rev,int scene) {;}
    public Vector3d getCameraRev(int scene) {return null;}
    public Vector3d getCameraTargetRev(int scene) {return null;}
    public void setCameraScale(double s,int scene) {;}
    public void setCameraScaleImmediately(double s,int scene) {;}
    public void mulCameraScale(double s,int scene) {;}
    public void mulCameraScaleImmediately(double s,int scene) {;}
    public double getCameraScale(int scene) {return 0.0;}
    public double getCameraTargetScale(int scene) {return 0.0;}
    public void setCameraLookAtPoint(Vector3d lookAt,int scene) {;}
    public void setCameraLookAtPointImmediately(Vector3d lookAt,int scene) {;}
    public void setCameraLookAtPoint(double x,double y,double z,int scene) {;}
    public void setCameraLookAtPointImmediately(double x,double y,double z,int scene) {;}
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up,int scene) {;}
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up,int scene) {;}
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up,int scene) {;}
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up,int scene) {;}
    public void setCameraLookAtPoint(A3Object a,int scene) {;}
    public void setCameraLookAtPoint(A3Object a,Vector3d up,int scene) {;}
    public void setNavigationMode(int scene,NaviMode m,Object...params) {;}
    public void setNavigationSpeed(double s,int scene) {;}
    public double getNavigationSpeed(int scene) {return 0.0;}
    public void setA3Controller(A3Controller c,int scene) {;}
    public void setBackground(A3Object a,int scene) {;}
    public void delBackground(int scene) {;}
    public void setAvatar(A3Object a,int scene) {;}
    public A3Object getAvatar(int scene) {return null;}
    public void addLockedA3(A3Object a) {;}
    public void delLockedA3(A3Object a) {;}
    public void delAllLockedA3() {;}
    public void addLockedA3(A3Object a,int scene) {;}
    public void delLockedA3(A3Object a,int scene) {;}
    public void delAllLockedA3(int scene) {;}
    public void setUpperDirection(A3Object.UpperDirection d) {;}
    public void setUpperDirection(A3Object.UpperDirection d,int scene) {;}
    public A3Object.UpperDirection getUpperDirection() {return null;}
    public A3Object.UpperDirection getUpperDirection(int scene) {return null;}
    public Dimension getCanvasSize() {return null;}
    public void cleanUp() {;}
    public void addA3SubCanvas(A3CanvasInterface sc) {;}
    public void setProjectionMode(ProjectionMode m) {;}
    public void setCanvasWidthInPWorld(double s) {;}
    public void setFieldOfView(double f) {;}
    public void setSoundGain(double g) {;}
    public double getSoundGain() {return 0.0;}
    public int getFPS() {return 0;}
    public void setUpdateInterval(long l) {;}
    public long getUpdateInterval() {return 0l;}
    public void waitForUpdate(long timeout) {;}
    public void insertTaskIntoRenderingLoop(Runnable task) {;}
    public void removeTaskFromRenderingLoop(Runnable task) {;}
    public void insertTaskIntoTimerLoop(Runnable task) {;}
    public void removeTaskFromTimerLoop(Runnable task) {;}
    public void setCameraInterpolateRatio(double ir) {;}
    public void setDepthBufferFreezeTransparent(boolean b) {;}
    public TransformGroup getTransformGroupForViewPlatform() {return null;}
    public Canvas3D getCanvas3D() {return null;}
    public BranchGroup getBranchGroupForViewPlatform() {return null;}
    public void setPickingBehavior(PickingBehavior pb) {;}
    public void setVirtualUniverse(A3VirtualUniverse vu) {;}
    public A3VirtualUniverse getVirtualUniverse() {return null;}
    public void setCameraLocNow(double x,double y,double z) {;}
    public void setCameraLocNow(Vector3d loc) {;}
    public void addCameraLocNow(double x,double y,double z) {;}
    public void addCameraLocNow(Vector3d loc) {;}
    public void moveCameraForwardNow(double l) {;}
    public void moveCameraBackwardNow(double l) {;}
    public void moveCameraRightNow(double l) {;}
    public void moveCameraLeftNow(double l) {;}
    public void moveCameraUpNow(double l) {;}
    public void moveCameraDownNow(double l) {;}
    public void moveCameraToNow(Vector3d v, double l) {;}
    public void moveCameraToNow(double x, double y, double z, double l) {;}
    public void moveCameraToNow(A3Object a, double l) {;}
    public void moveCameraForwardNow(double l,int scene) {;}
    public void moveCameraBackwardNow(double l,int scene) {;}
    public void moveCameraRightNow(double l,int scene) {;}
    public void moveCameraLeftNow(double l,int scene) {;}
    public void moveCameraUpNow(double l,int scene) {;}
    public void moveCameraDownNow(double l,int scene) {;}
    public void moveCameraToNow(Vector3d v, double l,int scene) {;}
    public void moveCameraToNow(double x, double y, double z, double l,int scene) {;}
    public void moveCameraToNow(A3Object a, double l,int scene) {;}
    public void setCameraQuatNow(double x,double y,double z,double w) {;}
    public void setCameraQuatNow(Quat4d quat) {;}
    public void mulCameraQuatNow(double x,double y,double z,double w) {;}
    public void mulCameraQuatNow(Quat4d quat) {;}
    public void setCameraRotNow(double x,double y,double z) {;}
    public void setCameraRotNow(Vector3d rot) {;}
    public void mulCameraRotNow(double x,double y,double z) {;}
    public void mulCameraRotNow(Vector3d rot) {;}
    public void setCameraRevNow(double x,double y,double z) {;}
    public void setCameraRevNow(Vector3d rev) {;}
    public void mulCameraRevNow(double x,double y,double z) {;}
    public void mulCameraRevNow(Vector3d rev) {;}
    public void setCameraScaleNow(double s) {;}
    public void mulCameraScaleNow(double s) {;}
    public void setCameraLookAtPointNow(Vector3d lookAt) {;}
    public void setCameraLookAtPointNow(double x,double y,double z) {;}
    public void setCameraLookAtPointNow(Vector3d lookAt,Vector3d up) {;}
    public void setCameraLookAtPointNow(double x,double y,double z,Vector3d up) {;}
    public void setCameraLookAtPointNow(A3Object a) {;}
    public void setCameraLookAtPointNow(A3Object a,Vector3d up) {;}
    public void setCameraLocNow(double x,double y,double z,int scene) {;}
    public void setCameraLocNow(Vector3d loc,int scene) {;}
    public void addCameraLocNow(double x,double y,double z,int scene) {;}
    public void addCameraLocNow(Vector3d loc,int scene) {;}
    public void setCameraQuatNow(double x,double y,double z,double w,int scene) {;}
    public void setCameraQuatNow(Quat4d quat,int scene) {;}
    public void mulCameraQuatNow(double x,double y,double z,double w,int scene) {;}
    public void mulCameraQuatNow(Quat4d quat,int scene) {;}
    public void setCameraRotNow(double x,double y,double z,int scene) {;}
    public void setCameraRotNow(Vector3d rot,int scene) {;}
    public void mulCameraRotNow(double x,double y,double z,int scene) {;}
    public void mulCameraRotNow(Vector3d rot,int scene) {;}
    public void setCameraRevNow(double x,double y,double z,int scene) {;}
    public void setCameraRevNow(Vector3d rev,int scene) {;}
    public void mulCameraRevNow(double x,double y,double z,int scene) {;}
    public void mulCameraRevNow(Vector3d rev,int scene) {;}
    public void setCameraScaleNow(double s,int scene) {;}
    public void mulCameraScaleNow(double s,int scene) {;}
    public void setCameraLookAtPointNow(Vector3d lookAt,int scene) {;}
    public void setCameraLookAtPointNow(double x,double y,double z,int scene) {;}
    public void setCameraLookAtPointNow(Vector3d lookAt,Vector3d up,int scene) {;}
    public void setCameraLookAtPointNow(double x,double y,double z,Vector3d up,int scene) {;}
    public void setCameraLookAtPointNow(A3Object a,int scene) {;}
    public void setCameraLookAtPointNow(A3Object a,Vector3d up,int scene) {;}
    public ArrayList<A3Object> getAll() {return null;}
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c) {return null;}
    public ArrayList<A3Object> getAll(int scene) {return null;}
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c,int scene) {return null;}
    public void setCameraLoc(A3Object a) {;}
    public void setCameraQuat(A3Object a) {;}
    public void setCameraRev(A3Object a) {;}
    public void setCameraScale(A3Object a) {;}
    public void setCameraLocRevScale(A3Object a) {;}
    public void setCameraLocNow(A3Object a) {;}
    public void setCameraQuatNow(A3Object a) {;}
    public void setCameraRevNow(A3Object a) {;}
    public void setCameraScaleNow(A3Object a) {;}
    public void setCameraLocRevScaleNow(A3Object a) {;}
    public void setCameraLoc(A3Object a,int scene) {;}
    public void setCameraQuat(A3Object a,int scene) {;}
    public void setCameraRev(A3Object a,int scene) {;}
    public void setCameraScale(A3Object a,int scene) {;}
    public void setCameraLocRevScale(A3Object a,int scene) {;}
    public void setCameraLocNow(A3Object a,int scene) {;}
    public void setCameraQuatNow(A3Object a,int scene) {;}
    public void setCameraRevNow(A3Object a,int scene) {;}
    public void setCameraScaleNow(A3Object a,int scene) {;}
    public void setCameraLocRevScaleNow(A3Object a,int scene) {;}
}
