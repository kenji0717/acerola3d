package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;
import jp.sourceforge.acerola3d.A23;
import java.net.URL;
//gaha
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;

/**
 * 仮想空間の背景を表現するためのクラス。
 */
public class A3Background extends A3Object {
    static BoundingSphere bs = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);
    Background background;

    /**
     * デフォルトのパラメータでA3Backgroundオブジェクトを生成します。
     */
    public A3Background() {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3Background"));
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Background");
        d.set(0.0f,0.0f,0.0f);
        realConstructor(d);
    }

    /**
     * R,G,Bで指定された色の背景を持つ3Backgroundオブジェクトを生成します。
     */
    public A3Background(float r,float g,float b) {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3Background"));
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Background");
        d.set(r,g,b);
        realConstructor(d);
    }

    /**
     * URLで指定された画像を球に貼り付けてこれを背景とするA3Backgroundオブジェクトを生成します。
     */
    public A3Background(String url) {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3Background"));
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Background");
        d.set(0.0f,0.0f,0.0f,url);
        realConstructor(d);
    }

    /**
     * A3InitDataをもとにA3Backgroundオブジェクトを生成するコンストラクタ。
     * A3InitDataにセットされるデータは、？？？
     * 
     * <table border="1" summary="required data in A3InitData">
     * <tr><td>0:float  </td><td>背景の色(赤成分) </td><td>必須</td></tr>
     * <tr><td>1:float  </td><td>背景の色(緑成分) </td><td>必須</td></tr>
     * <tr><td>2:float  </td><td>背景の色(青成分) </td><td>必須</td></tr>
     * <tr><td>3:String </td><td>背景の画像のURL  </td><td>任意</td></tr>
     * </table>
     */
    public A3Background(A3InitData d) {
        super(d);
        realConstructor(d);
    }

    void realConstructor(A3InitData d) {
        background = new Background();
        background.setApplicationBounds(bs);
        background.setCapability(Background.ALLOW_COLOR_WRITE);
        background.setCapability(Background.ALLOW_GEOMETRY_WRITE);
        float r = d.getFloat(0);
        float g = d.getFloat(1);
        float b = d.getFloat(2);
        background.setColor(r,g,b);
        if (d.getDataCount()>=4) {
            setImageToBackground(d.getString(3));
        }
        setNode(background);
    }

    void setImageToBackground(String urlString) {
        try {
            A23.initA23();
            URL url = new URL(urlString);
            BranchGroup bg = new BranchGroup();
            Sphere obj = new Sphere(1.0f, Sphere.GENERATE_NORMALS |
                Sphere.GENERATE_NORMALS_INWARD |
                Sphere.GENERATE_TEXTURE_COORDS |
                Sphere.GENERATE_TEXTURE_COORDS_Y_UP, 45);
            Appearance app = obj.getAppearance();
            bg.addChild(obj);
            background.setGeometry(bg);
            TextureLoader tex = new TextureLoader(url,
                new String("RGB"),
                TextureLoader.BY_REFERENCE | TextureLoader.Y_UP,
                null);
            if (tex != null)
                app.setTexture(tex.getTexture());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A3UpdateDataの情報をもとに現在の3Dオブジェクトの
     * 状態を更新します。
     *
     * A3UpdateDataにセットするデータは以下のようになります。
     * 
     * <table border="1" summary="required data in A3UpdateData">
     * <tr><td>0:float  </td><td>背景の色(赤成分) </td><td>必須</td></tr>
     * <tr><td>1:float  </td><td>背景の色(緑成分) </td><td>必須</td></tr>
     * <tr><td>2:float  </td><td>背景の色(青成分) </td><td>必須</td></tr>
     * <tr><td>3:String </td><td>背景の画像のURL  </td><td>任意</td></tr>
     * </table>
     */
    public void update(A3UpdateData d) {
        super.update(d);
        float r = d.getFloat(0);
        float g = d.getFloat(1);
        float b = d.getFloat(2);
        background.setColor(r,g,b);
        if (d.getDataCount()>=4) {
            setImageToBackground(d.getString(3));
        }
    }
    /**
     * このA3Backgroundの色をR,G,Bで指定します。
     */
    public void setColor(float r,float g,float b) {
        background.setColor(r,g,b);
    }
    /**
     * このA3Backgroundの背景画像を引数のURLで指定される画像に
     * セットします。
     */
    public void setImage(String url) {
        setImageToBackground(url);
    }
    /**
     * このA3Backgroundの背景データを引数のBranchGroupの内容に
     * セットします。
     */
    public void setGeometry(BranchGroup bg) {
        background.setGeometry(bg);
    }
}
