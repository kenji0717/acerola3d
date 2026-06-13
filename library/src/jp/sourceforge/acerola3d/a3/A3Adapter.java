package jp.sourceforge.acerola3d.a3;

/**
 * A3Listenerのメソッドを実装したアダプタクラスです。
 * A3Listenerで定義されたメソッドに対して空のメソッドを
 * 定義してあるので、extendsして必要なメソッドだけ
 * オーバーライドして使用します。
 */
public class A3Adapter implements A3Listener {
    /**
     * マウスが押された時のイベントをキャッチします。
     */
    public void mousePressed(A3Event e){;}

    /**
     * マウスがドラッグされた時のイベントをキャッチします。
     */
    public void mouseDragged(A3Event e){;}

    /**
     * マウスがリリースされた時のイベントをキャッチします。
     */
    public void mouseReleased(A3Event e){;}

    /**
     * マウスがクリックされた時のイベントをキャッチします。
     */
    public void mouseClicked(A3Event e){;}

    /**
     * マウスがダブルクリックされた時のイベントをキャッチします。
     */
    public void mouseDoubleClicked(A3Event e){;}
}
