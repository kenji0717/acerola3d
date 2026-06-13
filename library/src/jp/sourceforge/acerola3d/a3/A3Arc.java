package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * ある座標ともう一つの座標を結ぶ「弧」を表現するA3Objectです。
 */
public class A3Arc extends A3Object {
    double headX,headY,headZ;
    double tailX,tailY,tailZ;
    Point3d[] vertex;
    LineArray geometry;

    /**
     * HeadもTailも(0.0,0.0,0.0)のA3Arcオブジェクトを生成するコンストラクタ。
     */
    public A3Arc() {
        super(initHack());
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Arc");
        d.set(0.0,0.0,0.0,0.0,0.0,0.0);
        realConstructor(d);
    }

    /**
     * HeadとTailの座標を指定してA3Arcオブジェクトを生成するコンストラクタ。
     */
    public A3Arc(double hx,double hy,double hz,double tx,double ty,double tz) {
        super(initHack());
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Arc");
        d.set(hx,hy,hz,tx,ty,tz);
        realConstructor(d);
    }

    /**
     * A3InitDataをもとにA3Arcオブジェクトを生成するコンストラクタ。
     * A3InitDataにセットされるデータの数は、6でなければなりません。
     * 
     * <table border="1" summary="required data in A3InitData">
     * <tr><td>0:Double </td><td>終点のX座標       </td><td>必須</td></tr>
     * <tr><td>1:Double </td><td>終点のY座標       </td><td>必須</td></tr>
     * <tr><td>2:Double </td><td>終点のZ座標       </td><td>必須</td></tr>
     * <tr><td>3:Double </td><td>始点のX座標       </td><td>必須</td></tr>
     * <tr><td>4:Double </td><td>始点のY座標       </td><td>必須</td></tr>
     * <tr><td>5:Double </td><td>始点のZ座標       </td><td>必須</td></tr>
     * </table>
     */
    public A3Arc(A3InitData d) {
        super(d);
        realConstructor(d);
    }
    static A3InitData initHack() {
        return new A3InitData("jp.sourceforge.acerola3d.a3.A3Arc");
    }
    void realConstructor(A3InitData d) {
        headX = d.getDouble(0);
        headY = d.getDouble(1);
        headZ = d.getDouble(2);
        tailX = d.getDouble(3);
        tailY = d.getDouble(4);
        tailZ = d.getDouble(5);
        vertex = new Point3d[2];
        geometry =
            new LineArray(2,GeometryArray.COORDINATES | GeometryArray.COLOR_3);
        geometry.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
//      geometry.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
        //if (this.getUpperDirection()==UpperDirection.Y) {
            vertex[0] = new Point3d(headX,headY,headZ);
            vertex[1] = new Point3d(tailX,tailY,tailZ);
        //} else if (this.getUpperDirection()==UpperDirection.Z){
        //    vertex[0] = new Point3d(headX,headZ,-headY);
        //    vertex[1] = new Point3d(tailX,tailZ,-tailY);
        //}
        geometry.setCoordinates(0,vertex);
        geometry.setColor(0, new Color3f(java.awt.Color.blue));
        geometry.setColor(1, new Color3f(java.awt.Color.red));
        Shape3D shape = new Shape3D(geometry);
        setNode(shape);
    }

    /**
     * A3UpdateDataの情報をもとに現在の3Dオブジェクトの
     * 状態を更新します。
     *
     * A3UpdateDataにセットするデータは以下のようになります。
     * 
     * <table border="1" summary="required data in A3UpdateData">
     * <tr><td>0:Double</td><td>終点のX座標</td><td>必須</td></tr>
     * <tr><td>1:Double</td><td>終点のY座標</td><td>必須</td></tr>
     * <tr><td>2:Double</td><td>終点のZ座標</td><td>必須</td></tr>
     * <tr><td>3:Double</td><td>始点のX座標</td><td>必須</td></tr>
     * <tr><td>4:Double</td><td>始点のY座標</td><td>必須</td></tr>
     * <tr><td>5:Double</td><td>始点のZ座標</td><td>必須</td></tr>
     * </table>
     */
    public void update(A3UpdateData d) {
        super.update(d);
        headX = d.getDouble(0);
        headY = d.getDouble(1);
        headZ = d.getDouble(2);
        tailX = d.getDouble(3);
        tailY = d.getDouble(4);
        tailZ = d.getDouble(5);
        repaint();
    }

    public static A3Arc load() {
        return load(0.0,0.0,0.0,0.0,0.0,0.0,false,null);
    }
    public static A3Arc load(double hx,double hy,double hz,double tx,double ty,double tz,boolean isEnableBehavior,String label) {
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Arc");
        d.setEnableBehavior(isEnableBehavior);
        d.setLabel(label);
        d.set(hx,hy,hz,tx,ty,tz);
        A3Arc a = new A3Arc(d);
        return a;
    }
    public void setHead(double x,double y,double z) {
        headX = x;
        headY = y; 
        headZ = z;
        repaint();
    }
    public void setTail(double x,double y,double z) {
        tailX=x;
        tailY=y;
        tailZ=z;
        repaint();
    }
    void repaint() {
        //if (this.getUpperDirection()==UpperDirection.Y) {
            vertex[0] = new Point3d(headX,headY,headZ);
            vertex[1] = new Point3d(tailX,tailY,tailZ);
        //} else if (this.getUpperDirection()==UpperDirection.Z){
        //    vertex[0] = new Point3d(headX,headZ,-headY);
        //    vertex[1] = new Point3d(tailX,tailZ,-tailY);
        //}
        geometry.setCoordinates(0,vertex);
//        geometry.setColor(0, new Color3f(java.awt.Color.blue));
//        geometry.setColor(1, new Color3f(java.awt.Color.red));
    }
}
