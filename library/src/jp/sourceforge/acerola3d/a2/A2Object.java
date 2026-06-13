package jp.sourceforge.acerola3d.a2;

import java.awt.*;

public abstract class A2Object {
    double x;
    double y;
    double nextX;
    double nextY;
    boolean interpolating = false;
    protected boolean needRepaint = true;
    A2Canvas canvas = null;
    Object userData = null;

    public A2Object(boolean interpolating) {
        this.interpolating = interpolating;
    }
    public abstract void paint(Graphics g);
    public final void setLoc(double x,double y) {
        if (interpolating) {
            nextX = x;
            nextY = y;
        } else {
            this.x = x;
            this.y = y;
        }
    }
    public final void setLocImmediately(double x,double y) {
        this.x = nextX = x;
        this.y = nextY = y;
    }
    final void processStimulus() {
        x = x + 0.2*(nextX-x);
        y = y + 0.2*(nextY-y);
    }
    final void setA2Canvas(A2Canvas canvas) {
        this.canvas = canvas;
    }
    final void removeA2Canvas() {
        canvas = null;
    }
    protected void repaint() {
        needRepaint = true;
    }
    public final void setInterpolating(boolean b) {
        interpolating = b;
    }

    public final void setUserData(Object o) {
        userData = o;
    }
    public final Object getUserData() {
        return userData;
    }
}
