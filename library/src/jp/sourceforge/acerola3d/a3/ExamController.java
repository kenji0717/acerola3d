package jp.sourceforge.acerola3d.a3;

import java.awt.event.*;
import javax.vecmath.*;

/**
 * EXAMINモードのコントローラ。
 */
class ExamController extends A3Controller {
    Tuple3d lastPhysicalV = null;
    int lastMouseX;
    int lastMouseY;
    A3Object targetA3;

    public void init() {
        lastMouseX = 0;
        lastMouseY = 0;
    }

    /**
     * マウスが押された時のイベントをキャッチします。
     */
    public void mousePressed(A3Event ae) {
        MouseEvent me = ae.getMouseEvent();
        lastMouseX = me.getX();
        lastMouseY = me.getY();
        A3Object a3 = ae.getA3Object();
        if (a3!=null) {
            Vector3d v = new Vector3d();
            v.sub(a3.getLoc(),a3canvas.getCameraLoc());
            v = a3canvas.virtualCSToPhysicalCS(v);
            lastPhysicalV = a3canvas.canvasToPhysicalCS(me.getX(),me.getY(),-v.z);
        }
    }

    /**
     * マウスがドラッグされた時のイベントをキャッチします。
     */
    public void mouseDragged(A3Event ae) {
        MouseEvent me = ae.getMouseEvent();
        int shiftX = me.getX() - lastMouseX;
        int shiftY = me.getY() - lastMouseY;
        if ((me.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)!=0) {
            Vector3d selectedV = new Vector3d();
            if (targetA3!=null)
                selectedV.set(targetA3.getLoc());
            else
                selectedV.set(0.0,0.0,0.0);
            Vector3d tmpV = new Vector3d();
            tmpV.sub(a3canvas.getCameraLoc(),selectedV);
            a3canvas.setCameraScaleImmediately(tmpV.length()/2.0);//2009,08/14追加
            Quat4d q0 = new Quat4d(0.0,0.0,0.0,1.0);
            double t = - shiftX * 0.01;
            Quat4d q1 = new Quat4d(0.0,Math.sin(t),0.0,Math.cos(t));
            t = - shiftY * 0.01;
            Quat4d q2 = new Quat4d(Math.sin(t),0.0,0.0,Math.cos(t));
            q0.mul(q1);
            q0.mul(q2);

            //カメラの方向を考慮した変換????
            Quat4d cameraQ = a3canvas.getCameraQuat();
            q1.set(q0.x,q0.y,q0.z,0.0);
            q2.mul(cameraQ,q1);
            cameraQ.conjugate();
            q2.mul(cameraQ);
            q0.x=q2.x;q0.y=q2.y;q0.z=q2.z;

            q1.set(tmpV.x,tmpV.y,tmpV.z,0.0);
            q2.mul(q0,q1);
            q0.conjugate();
            q2.mul(q0);
            selectedV.add(new Vector3d(q2.x,q2.y,q2.z));
            a3canvas.setCameraLocImmediately(selectedV);
            turnToSelectedObject();
        } else if ((me.getModifiersEx()&MouseEvent.BUTTON2_DOWN_MASK)!=0) {
            double s = (100+shiftY)/100.0;
            Vector3d selectedV = new Vector3d();
            if (targetA3!=null)
                selectedV.set(targetA3.getLoc());
            else
                selectedV.set(0.0,0.0,0.0);
            Vector3d tmpV = new Vector3d();
            tmpV.sub(a3canvas.getCameraLoc(),selectedV);
            tmpV.scale(s);
            a3canvas.setCameraScaleImmediately(tmpV.length()/2.0);//2009,08/14追加
            tmpV.add(selectedV);
            a3canvas.setCameraLocImmediately(tmpV);
        } else if ((me.getModifiersEx()&MouseEvent.BUTTON3_DOWN_MASK)!=0) {
            ;
        }
        lastMouseX = me.getX();
        lastMouseY = me.getY();
    }

    /**
     * マウスがリリースされた時のイベントをキャッチします。
     */
    public void mouseReleased(A3Event e) {
    }

    /**
     * マウスがクリックされた時のイベントをキャッチします。
     */
    public void mouseClicked(A3Event ae) {
        targetA3 = ae.getA3Object();
        turnToSelectedObject();
    }

    /**
     * マウスがダブルクリックされた時のイベントをキャッチします。
     */
    public void mouseDoubleClicked(A3Event ae) {
        ;
    }

    //カメラを選択されたオブジェクトに向ける処理をするメソッド
    //もし、選択されたものがなければ原点を向くようにする。
    void turnToSelectedObject() {
        Vector3d selectedV = new Vector3d();
        if (targetA3!=null) {
            selectedV.set(targetA3.getLoc());
        } else {
            selectedV.set(0.0,0.0,0.0);
        }
        Quat4d tmpQ1 = a3canvas.getCameraQuat();
        Quat4d tmpQ2 = new Quat4d(0.0,0.0,-1.0,0.0);
        Quat4d tmpQ3 = new Quat4d();
        tmpQ3.mul(tmpQ1,tmpQ2);
        tmpQ1.conjugate();
        tmpQ3.mul(tmpQ1);
        Vector3d tmpV1 = new Vector3d(tmpQ3.x,tmpQ3.y,tmpQ3.z);
        Vector3d tmpV2 = new Vector3d();
        tmpV2.sub(selectedV,a3canvas.getCameraLoc());
        a3canvas.setCameraScaleImmediately(tmpV2.length()/2.0); //2009,08/14追加
if (tmpV2.length()<0.00001) {System.out.println("x");return;}
        tmpV2.normalize();
        Vector3d tmpV3 = new Vector3d();
        tmpV3.cross(tmpV1,tmpV2);
if (tmpV3.length()<0.00001) {System.out.println("X");return;}
        tmpV3.normalize();
        double t = 0.5*Math.acos(tmpV1.dot(tmpV2));
        double st = Math.sin(t);
        double ct = Math.cos(t);
        Quat4d q = new Quat4d(st*tmpV3.x,st*tmpV3.y,st*tmpV3.z,ct);
        tmpQ1.set(a3canvas.getCameraQuat());
        q.mul(tmpQ1);
        a3canvas.setCameraQuatImmediately(q);
    }
    /**
     * キーボードが押された時のイベントをキャッチします。
     */
    public void keyPressed(KeyEvent e) {
    }

    /**
     * キーボードが離された時のイベントをキャッチします。
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * キーボードがタイプされた時のイベントをキャッチします。
     */
    public void keyTyped(KeyEvent e) {
    }
}
