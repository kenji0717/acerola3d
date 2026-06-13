package jp.sourceforge.acerola3d.a2;

import java.awt.event.*;

public class A2Event {
    String message;
    KeyEvent keyEvent;
    MouseEvent mouseEvent;
    A2Object a2Object;

    public A2Event() {
        message = "";
    }
    public A2Event(String msg) {
        message = msg;
    }

    public void setMouseEvent(MouseEvent me) {
        mouseEvent = me;
    }
    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    public void setKeyEvent(KeyEvent ke) {
        keyEvent = ke;
    }
    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    public void setA2Object(A2Object a2) {
        a2Object = a2;
    }

    public A2Object getA2Object() {
        return a2Object;
    }
}
