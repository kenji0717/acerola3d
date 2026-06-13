package jp.sourceforge.acerola3d.a3;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * マウスイベント、キーボードイベントをキャッチして
 * 様々な処理を実行するコントローラで、おもにナビゲーション
 * モードの実装などに使用される。
 */
public abstract class A3Controller implements A3Listener, KeyListener {
    /** コントロール対象のA3Canvas。 */
    protected A3CanvasInterface a3canvas;

    /**
     * このコントローラがA3Canvasにセットされる時に
     * 最初に呼び出されるメソッド。
     */
    public void init() {
    }

    /** このコントローラにコンストラクタ。 */
    final void setA3CanvasInterface(A3CanvasInterface c) {
        a3canvas = c;
    }

    /**
     * マウスがクリックされた時のイベントをキャッチします。
     */
    public void mouseClicked(A3Event e) {
    }

    /**
     * マウスがダブルクリックされた時のイベントをキャッチします。
     */
    public void mouseDoubleClicked(A3Event e) {
    }

    /**
     * マウスがドラッグされた時のイベントをキャッチします。
     */
    public void mouseDragged(A3Event e) {
    }

    /**
     * マウスが押された時のイベントをキャッチします。
     */
    public void mousePressed(A3Event e) {
    }

    /**
     * マウスがリリースされた時のイベントをキャッチします。
     */
    public void mouseReleased(A3Event e) {
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

    /**
     * このコントローラが解除される時に呼び出されるメソッド。
     */
    public void stop() {
    }
}
