package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import java.awt.image.BufferedImage;

/**
 * 仮想空間に配置することで，配置された視点からの
 * 視野を画像として取り出すことができるA3Objectです．
 * ある視点からの視野を表示するだけであればA3SubCanvasやJA3SubCanvasが
 * 適していますが，画像をBufferedImageとして取り出して
 * 画像処理するような場合にこれを使用することができます．
 */
public class A3VideoCamera extends A3Object {
    A3Eye a3Eye;

    /**
     * オフスクリーンのサイズを指定してA3VideoCameraオブジェクトを
     * 生成します．ただし，震度情報の処理は現在(2013,03/13)未実装．
     */
    public A3VideoCamera(int w,int h) {
        this(w,h,false);
    }
    /**
     * オフスクリーンのサイズと深度情報を使用するか指定してA3VideoCameraオブジェクトを
     * 生成します．
     */
    public A3VideoCamera(int w,int h,boolean depth) {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3VideoCamera"));
        a3Eye = new A3Eye(w,h,depth);
        setNode(a3Eye);
    }

    /**
     * このA3VideoCameraと同じ場所に表示するためのjavax.media.j3d.Nodeを
     * 追加します．このメソッドを用いて目に見えるNodeを追加しなければ，
     * A3VideoCameraは仮想空間内で不可視です．
     */
    public void addNode(Node n) {
        a3Eye.addChild(n);
    }

    /**
     * 引数で指定したBufferedImageに現在の視点からの視野を
     * レンダリングします．
     * 引数として与えるBufferedImageのサイズは，A3Eyeを
     * 生成する時のコンストラクタで指定したサイズでなければ
     * なりません．
     */
    public void renderOffscreenBuffer(BufferedImage bi) {
        a3Eye.renderOffscreenBuffer(bi);
    }

    /**
     * カメラの投影法を設定するためのメソッド。
     * デフォルトは透視投影法。
     */
    public void setProjectionMode(ProjectionMode m) {
        a3Eye.setProjectionMode(m);
    }

    /**
     * このA3VideoCameraのオフスクリーンの物理座標系における
     * 横幅を設定するためのメソッドです。
     * ただしカメラの投影方法が平行投影方法(PARALLEL)の場合のみ
     * 有効で、透視投影法(PERSPECTIVE)の場合は無視されます。
     */
    public void setCanvasWidthInPWorld(double s) {
        a3Eye.setCanvasWidthInPWorld(s);
    }

    /**
     * 透視投影法の場合の画角(Field of View)を設定するためのメソッド。
     * 平行投影法(PARALLEL)の場合は、この値は無視されます。
     */
    public void setFieldOfView(double f) {
        a3Eye.setFieldOfView(f);
    }

    /**
     * 深度情報が保存されているfloatの配列を返します。
     * ただし，このメソッドは現在(2013,03/13)未実装．
     */
    public float[] getDepthData() {
        return a3Eye.getDepthData();
    }
}
