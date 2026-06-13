package jp.sourceforge.acerola3d.a3;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.vecmath.*;

/**
 * walkモードのコントローラ。
 * 基本的に平面上を動くモード。
 * つまりカメラが上を向いていたとしても前進
 * した時に上方向には動かない．
 */
class WalkController extends A3Controller implements Runnable {
    int startMouseX=-1;
    int startMouseY=-1;
    int lastMouseX;
    int lastMouseY;
    enum MouseStatus {N,B1,B2,B3}
    MouseStatus mouseStatus = MouseStatus.N;

    public void init() {
        startMouseX = -1;
        startMouseY = -1;
        lastMouseX = 0;
        lastMouseY = 0;
        mouseStatus = MouseStatus.N;
        a3canvas.insertTaskIntoTimerLoop(this);
    }

    public void stop() {
        a3canvas.removeTaskFromTimerLoop(this);
    }

    public void run() {
        if (mouseStatus==MouseStatus.B1)
            walkNavi();
        else if (mouseStatus==MouseStatus.B2)
            rotNavi();
        else if (mouseStatus==MouseStatus.B3)
            transNavi();
    }

    /**
     * マウスが押された時のイベントをキャッチします。
     */
    public void mousePressed(A3Event ae) {
        MouseEvent me = ae.getMouseEvent();
        startMouseX = me.getX();
        startMouseY = me.getY();
        lastMouseX = me.getX();
        lastMouseY = me.getY();
        if ((me.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)!=0)
            mouseStatus = MouseStatus.B1;
        else if ((me.getModifiersEx()&MouseEvent.BUTTON2_DOWN_MASK)!=0)
            mouseStatus = MouseStatus.B2;
        else if ((me.getModifiersEx()&MouseEvent.BUTTON3_DOWN_MASK)!=0)
            mouseStatus = MouseStatus.B3;
    }

    /**
     * マウスがドラッグされた時のイベントをキャッチします。
     */
    public void mouseDragged(A3Event ae) {
        MouseEvent me = ae.getMouseEvent();
        if (startMouseX==-1) {
            startMouseX = me.getX();
            startMouseY = me.getY();
        }
        lastMouseX = me.getX();
        lastMouseY = me.getY();
        if ((me.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)!=0)
            mouseStatus = MouseStatus.B1;
        else if ((me.getModifiersEx()&MouseEvent.BUTTON2_DOWN_MASK)!=0)
            mouseStatus = MouseStatus.B2;
        else if ((me.getModifiersEx()&MouseEvent.BUTTON3_DOWN_MASK)!=0)
            mouseStatus = MouseStatus.B3;
    }
    //NAVIモード(walk)
    void walkNavi() {
        Vector3d cameraV = a3canvas.getCameraLoc();
        Quat4d cameraQ = a3canvas.getCameraQuat();
        int shiftX = lastMouseX - startMouseX;
        int shiftY = lastMouseY - startMouseY;
        Vector3d v = Util.trans(cameraQ,new Vector3d(0.0,0.0,1.0));
        if (a3canvas.getUpperDirection()==A3Object.UpperDirection.Z) {
            v.z = 0.0;
        } else {
            v.y = 0.0;
        }
        if (v.length()>0.0001)
            v.normalize();
        long elapsedTime = a3canvas.getUpdateInterval();
        double speed = a3canvas.getNavigationSpeed();
        //speed = speed * a3canvas.getCameraScale();//2009,09/04追加しようかと思ったけど取り止め
        double step = speed*((double)elapsedTime)/1000.0;
        step = 0.04*shiftY*step;
        v.scale(step);
        cameraV.add(v);
        double t = shiftX * -0.004*((double)elapsedTime)/1000.0;
        Quat4d q = new Quat4d(0.0,Math.sin(t),0.0,Math.cos(t));
        cameraQ.mul(q);
        a3canvas.setCameraLocImmediately(cameraV);
        a3canvas.setCameraQuatImmediately(cameraQ);
    }
    //NAVIモード(首ふり)
    void rotNavi() {
        Quat4d cameraQ = a3canvas.getCameraQuat();
        int shiftX = lastMouseX - startMouseX;
        int shiftY = lastMouseY - startMouseY;
        long elapsedTime = a3canvas.getUpdateInterval();
        double t = shiftY * -0.004*((double)elapsedTime)/1000.0;
        Quat4d q = new Quat4d(Math.sin(t),0.0,0.0,Math.cos(t));
        cameraQ.mul(q);
        t = shiftX * -0.004*((double)elapsedTime)/1000.0;
        q = new Quat4d(0.0,Math.sin(t),0.0,Math.cos(t));
        cameraQ.mul(q);
        a3canvas.setCameraQuatImmediately(cameraQ);
    }
    //NAVIモード(平行移動)
    void transNavi() {
        Vector3d cameraV = a3canvas.getCameraLoc();
        Quat4d cameraQ = a3canvas.getCameraQuat();
        int shiftX = lastMouseX - startMouseX;
        int shiftY = lastMouseY - startMouseY;
        long elapsedTime = a3canvas.getUpdateInterval();
        double speed = a3canvas.getNavigationSpeed();
        //speed = speed * a3canvas.getCameraScale();//2009,09/04追加しようかと思ったけど取り止め
        double step = speed*((double)elapsedTime)/1000.0;
        Vector3d v = Util.trans(cameraQ,new Vector3d(0.04*shiftX*step,0.0,0.0));
        cameraV.add(v);
        v = Util.trans(cameraQ,new Vector3d(0.0,-0.04*shiftY*step,0.0));
        cameraV.add(v);
        a3canvas.setCameraLocImmediately(cameraV);
        a3canvas.setCameraQuatImmediately(cameraQ);
    }

    /**
     * マウスがリリースされた時のイベントをキャッチします。
     */
    public void mouseReleased(A3Event ae) {
        mouseStatus = MouseStatus.N;
    }

    /**
     * マウスがクリックされた時のイベントをキャッチします。
     */
    public void mouseClicked(A3Event ae) {
        /*
        A3Object a3 = ae.getA3Object();
        if (ae!=null) {
            a3.setSelected(!(a3.isSelected()));
        }
        */
    }

    /**
     * マウスがダブルクリックされた時のイベントをキャッチします。
     */
    public void mouseDoubleClicked(A3Event ae) {
        /*
        System.out.println("double clicked!");
        */
    }

    /**
     * キーボードが押された時のイベントをキャッチします。
     */
    public void keyPressed(KeyEvent e) {
        ;
    }

    /**
     * キーボードが離された時のイベントをキャッチします。
     */
    public void keyReleased(KeyEvent e) {
        ;
    }

    /**
     * キーボードがタイプされた時のイベントをキャッチします。
     */
    public void keyTyped(KeyEvent e) {
        ;
    }
}
