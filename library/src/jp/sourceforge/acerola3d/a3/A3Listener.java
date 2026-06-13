package jp.sourceforge.acerola3d.a3;

/**
 * A3Eventをキャッチするためのリスナです。現在のところ、
 * マウスのイベントを処理するためのリスナになります。
 * A3CanvasかA3WindowのaddA3Listenerでリスナの
 * 登録をするようになっています。
 */
public interface A3Listener {
    /**
     * マウスが押された時のイベントをキャッチします。
     */
    public void mousePressed(A3Event e);

    /**
     * マウスがドラッグされた時のイベントをキャッチします。
     */
    public void mouseDragged(A3Event e);

    /**
     * マウスがリリースされた時のイベントをキャッチします。
     */
    public void mouseReleased(A3Event e);

    /**
     * マウスがクリックされた時のイベントをキャッチします。
     */
    public void mouseClicked(A3Event e);

    /**
     * マウスがダブルクリックされた時のイベントをキャッチします。
     */
    public void mouseDoubleClicked(A3Event e);
}
