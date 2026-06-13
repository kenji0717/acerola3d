package jp.sourceforge.acerola3d.a3;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.vecmath.Quat4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 * Editモードのコントローラ。つまり、
 * A3Canvasに表示されているA3Objectの場所、回転、拡大率を
 * マウスで編集するモードのコントローラ。
 */
class EditController extends A3Controller {
    int lastMouseX;
    int lastMouseY;

    public void init() {
        lastMouseX = 0;
        lastMouseY = 0;
    }

    /**
     * マウスが押された時のイベントをキャッチします。
     */
    public void mousePressed(A3Event ae) {
        //全モード共通の処理
        MouseEvent me = ae.getMouseEvent();
        lastMouseX = me.getX();
        lastMouseY = me.getY();
    }

    /**
     * マウスがドラッグされた時のイベントをキャッチします。
     */
    public void mouseDragged(A3Event ae) {
        A3Object a3 = ae.getA3Object();
        if (a3==null)
            return;
        MouseEvent me = ae.getMouseEvent();
        if ((me.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)!=0) {
            //平行移動(Shift+で奥行も)
            Vector3d v = a3.getLoc();
            v = a3canvas.virtualCSToPhysicalCS(v);
            Tuple3d vTmp1 = a3canvas.canvasToPhysicalCS(lastMouseX,lastMouseY,-v.z);
            Tuple3d vTmp2 = a3canvas.canvasToPhysicalCS(me.getX(),me.getY(),-v.z);
            v.sub(vTmp2,vTmp1);
            if ((me.getModifiers()&MouseEvent.SHIFT_MASK)!=0) {
                v.set(v.x,0.0,-v.y);
            }
            v = a3canvas.physicalCSToVirtualCS(v);
            v.sub(a3canvas.getCameraLoc());
            v.add(a3.getLoc());
            a3.setLocImmediately(v);
        } else if ((me.getModifiersEx()&MouseEvent.BUTTON2_DOWN_MASK)!=0) {
            //スケール変更
            double s = (100-(me.getY()-lastMouseY))/100.0;
            s = s * a3.getScale();
            a3.setScaleImmediately(s);
        } else if ((me.getModifiersEx()&MouseEvent.BUTTON3_DOWN_MASK)!=0) {
            //回転
            int shiftX = me.getX() - lastMouseX;
            int shiftY = me.getY() - lastMouseY;
            Quat4d q0 = new Quat4d(0.0,0.0,0.0,1.0);
            double t = shiftX * 0.01;
            Quat4d q1 = new Quat4d(0.0,Math.sin(t),0.0,Math.cos(t));
            t = shiftY * 0.01;
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

            q1 = a3.getQuat();
            q0.mul(q1);
            a3.setQuatImmediately(q0);
        }
        lastMouseX = me.getX();
        lastMouseY = me.getY();
    }

    /**
     * マウスがリリースされた時のイベントをキャッチします。
     */
    public void mouseReleased(A3Event ae) {
        ;
    }

    /**
     * マウスがクリックされた時のイベントをキャッチします。
     */
    public void mouseClicked(A3Event ae) {
        ;
    }

    /**
     * マウスがダブルクリックされた時のイベントをキャッチします。
     */
    public void mouseDoubleClicked(A3Event ae) {
        ;
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
