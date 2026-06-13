package jp.sourceforge.acerola3d.a3;

import java.awt.event.*;

/**
 * A3Eventを表現するオブジェクトです。現在のところ
 * マウスイベントを表現するオブジェクトになっています。
 */
public class A3Event {
    String message;
    MouseEvent mouseEvent;
    A3Object a3Object;

    /**
     * A3Eventのインスタンスを生成するコンストラクタ。
     */
    public A3Event() {
        message = "";
    }

    /**
     * A3Eventのインスタンスを生成するコンストラクタ。
     * メッセージのストリングを指定することができます。
     */
    public A3Event(String msg) {
        message = msg;
    }

    void setMouseEvent(MouseEvent me) {
        mouseEvent = me;
    }

    /**
     * このイベントに関連するjava.awt.event.MouseEvent
     * を返します。
     */
    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    void setA3Object(A3Object a3) {
        a3Object = a3;
    }

    /**
     * このイベントに関連するA3Objectを返します。
     * これは、イベントがクリックやダブルクリックの場合に
     * 意味を持ちます。関係のないイベントやクリックした場所に
     * A3Objectが無い場合はnullが返されます。
     */
    public A3Object getA3Object() {
        return a3Object;
    }
}
