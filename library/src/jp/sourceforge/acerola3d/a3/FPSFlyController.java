package jp.sourceforge.acerola3d.a3;

import java.awt.event.KeyEvent;
import javax.vecmath.*;

/**
 * FPS_FLYモードのコントローラ。
 */
class FPSFlyController extends A3Controller implements Runnable {
    boolean upKey = false;
    boolean downKey = false;
    boolean rightKey = false;
    boolean leftKey = false;
    boolean wKey = false;
    boolean sKey = false;
    boolean dKey = false;
    boolean aKey = false;

    FPSFlyController(Object...params) {
    }

    public void init() {
        a3canvas.insertTaskIntoTimerLoop(this);
    }

    public void stop() {
        a3canvas.removeTaskFromTimerLoop(this);
    }

    public void run() {
        if (upKey==true) {
            Vector3d v = a3canvas.getCameraTargetRot();
            v.x += 0.1;
            a3canvas.setCameraRot(v);
        }
        if (downKey==true) {
            Vector3d v = a3canvas.getCameraTargetRot();
            v.x -= 0.1;
            a3canvas.setCameraRot(v);
        }
        if (rightKey==true) {
            Vector3d v = a3canvas.getCameraTargetRot();
            v.y -= 0.1;
            a3canvas.setCameraRot(v);
        }
        if (leftKey==true) {
            Vector3d v = a3canvas.getCameraTargetRot();
            v.y += 0.1;
            a3canvas.setCameraRot(v);
        }
        double speed = a3canvas.getNavigationSpeed();
        if (wKey==true) a3canvas.moveCameraForward(0.1*speed);
        if (sKey==true) a3canvas.moveCameraBackward(0.1*speed);
        if (dKey==true) a3canvas.moveCameraRight(0.1*speed);
        if (aKey==true) a3canvas.moveCameraLeft(0.1*speed);
    }

    /**
     * キーボードが押された時のイベントをキャッチします。
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_UP: upKey = true; break;
        case KeyEvent.VK_DOWN: downKey = true; break;
        case KeyEvent.VK_RIGHT: rightKey = true; break;
        case KeyEvent.VK_LEFT: leftKey = true; break;
        case KeyEvent.VK_W: wKey = true; break;
        case KeyEvent.VK_S: sKey = true; break;
        case KeyEvent.VK_D: dKey = true; break;
        case KeyEvent.VK_A: aKey = true; break;
        }
    }

    /**
     * キーボードが離された時のイベントをキャッチします。
     */
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_UP: upKey = false; break;
        case KeyEvent.VK_DOWN: downKey = false; break;
        case KeyEvent.VK_RIGHT: rightKey = false; break;
        case KeyEvent.VK_LEFT: leftKey = false; break;
        case KeyEvent.VK_W: wKey = false; break;
        case KeyEvent.VK_S: sKey = false; break;
        case KeyEvent.VK_D: dKey = false; break;
        case KeyEvent.VK_A: aKey = false; break;
        }
    }
}
