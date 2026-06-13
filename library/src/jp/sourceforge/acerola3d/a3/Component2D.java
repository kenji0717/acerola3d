package jp.sourceforge.acerola3d.a3;

import java.awt.Graphics2D;
import java.lang.reflect.Constructor;

import jp.sourceforge.acerola3d.A23;

/**
 * A3CanvasInterfaceに通常の2D描画の機能を追加するための
 * オブジェクトです。このクラスのpaint()メソッドを
 * 実装したオブジェクトをJA3WindowやA3Screenにaddすることで
 * 3D描画の上に2Dグラフィックを表示させることができます。
 */
public abstract class Component2D {
    static Component2D advertisement = null;
    static {
        String className = System.getProperty("acerola3d.advertisement");
        if (className!=null) {
            System.out.println("acerola3d.advertisement="+className);
            try {
                ClassLoader classLoader = A23.getClassLoader();
                Class<?> c = classLoader.loadClass(className);
                Class<? extends Component2D> cc;
                cc = c.asSubclass(Component2D.class);
                Constructor<? extends Component2D> con = cc.getDeclaredConstructor(new Class[0]);

                advertisement = con.newInstance(new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    protected double z = 0.0;
    /**
     * 描画のためのメソッドです。
     */
    public abstract void paint(Graphics2D g,A3CanvasInterface canvas);
    /**
     * ソート前にソートの順番を計算するための物理座標のZ値を計算するために
     * 呼び出されるメソッドです。
     */
    public void calPhysicalZ(A3CanvasInterface canvas) {
        ;
    }
}
