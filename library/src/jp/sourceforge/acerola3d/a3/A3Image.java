package jp.sourceforge.acerola3d.a3;

import java.awt.*;
import java.net.URL;

import javax.media.j3d.*;
import javax.vecmath.*;
/**
 * JpegやPNGなどの画像を3D仮想空間に表示するためのA3Objectです。
 */
public class A3Image extends A3Object {
    Image image;
    static Image errImg;

    /**
     * StringのURLをもとにA3Imageオブジェクトを生成するコンストラクタ。
     */
    public A3Image(String url) {
        super(initHack());
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Image");
        d.set(url);
        realConstructor(d);
    }

    /**
     * URLをもとにA3Imageオブジェクトを生成するコンストラクタ。
     */
    public A3Image(URL url) {
        super(initHack());
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Image");
        d.set(url.toString());
        realConstructor(d);
    }

    /**
     * A3InitDataをもとにA3Imageオブジェクトを生成するコンストラクタ。
     * A3InitDataの引数は、1でなければなりません。
     * 
     * <table border="1" summary="required data in A3InitData">
     * <tr><td>0:String </td><td>URL of an image </td><td>必須</td></tr>
     * </table>
     */
    public A3Image(A3InitData d) {
        super(d);
        realConstructor(d);
    }
    static A3InitData initHack() {
        return new A3InitData("jp.sourceforge.acerola3d.a3.A3Image");
    }
    void realConstructor(A3InitData d) {
        jp.sourceforge.acerola3d.A23.initA23();
        makeImage(d.getString(0));

        BranchGroup bg = new BranchGroup();

        Texture texture = makeTexture();
        Appearance app = new Appearance();
        app.setTexture(texture);

        Point3d vertices1[] = new Point3d[4];
        vertices1[0] = new Point3d(-1.0,0.0,0.1);
        vertices1[1] = new Point3d( 1.0,0.0,0.1);
        vertices1[2] = new Point3d( 1.0,2.0,0.1);
        vertices1[3] = new Point3d(-1.0,2.0,0.1);
        TexCoord2f[] txcoords1 = new TexCoord2f[4];
        txcoords1[0] = new TexCoord2f(0.0f,0.0f);
        txcoords1[1] = new TexCoord2f(1.0f,0.0f);
        txcoords1[2] = new TexCoord2f(1.0f,1.0f);
        txcoords1[3] = new TexCoord2f(0.0f,1.0f);
        QuadArray geometry1 = new QuadArray(vertices1.length,
                QuadArray.COORDINATES|
                QuadArray.TEXTURE_COORDINATE_2);
        geometry1.setCoordinates(0,vertices1);
        geometry1.setTextureCoordinates(0,0,txcoords1);
        Shape3D shape1 = new Shape3D(geometry1,app);
        bg.addChild(shape1);

        Point3d vertices2[] = new Point3d[4];
        vertices2[0] = new Point3d(-1.0,2.0,-0.1);
        vertices2[1] = new Point3d( 1.0,2.0,-0.1);
        vertices2[2] = new Point3d( 1.0,0.0,-0.1);
        vertices2[3] = new Point3d(-1.0,0.0,-0.1);
        TexCoord2f[] txcoords2 = new TexCoord2f[4];
        txcoords2[0] = new TexCoord2f(1.0f,1.0f);
        txcoords2[1] = new TexCoord2f(0.0f,1.0f);
        txcoords2[2] = new TexCoord2f(0.0f,0.0f);
        txcoords2[3] = new TexCoord2f(1.0f,0.0f);
        QuadArray geometry2 = new QuadArray(vertices2.length,
                QuadArray.COORDINATES|
                QuadArray.TEXTURE_COORDINATE_2);
        geometry2.setCoordinates(0,vertices2);
        geometry2.setTextureCoordinates(0,0,txcoords2);
        Shape3D shape2 = new Shape3D(geometry2,app);
        bg.addChild(shape2);

        setNode(bg);
    }
    Image makeImage(String source) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        try {
            URL resource = new URL(source);
            image = toolkit.getImage(resource);
            return image;
        } catch(Exception e) {
            try {
                if (errImg == null) {
                    URL resource = new URL("x-res:///jp/sourceforge/acerola3d/resources/error.gif");
                    errImg = toolkit.getImage(resource);
                    return errImg;
                } else {
                    return errImg;
                }
            } catch (Exception ee) {
                ee.printStackTrace();
                return null;
            }
        }
    }
    Texture makeTexture() {
        Image img = image.getScaledInstance(256,256,Image.SCALE_SMOOTH);
        return new com.sun.j3d.utils.image.TextureLoader(img,null).getTexture();
    }
    /**
     * A3UpdateDataの情報をもとに現在の3Dオブジェクトの
     * 状態を更新します。ですが、現在A3Imageは更新する情報を特に持たないので、
     * スーパークラスのupdateを呼んでいるだけです。
     * 
     * なので、A3UpdateDataにセットする情報は座標などの標準的なデータだけで
     * 良いです。
     */
    public void update(A3UpdateData d) {
        super.update(d);
    }

    public static A3Image load(String url) {
        return load(url,false,null);
    }
    public static A3Image load(String url,boolean isEnableBehavior,String label) {
        jp.sourceforge.acerola3d.A23.initA23();
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Image");
        d.setEnableBehavior(isEnableBehavior);
        d.setLabel(label);
        d.set(url);
        A3Image a = new A3Image(d);
        return a;
    }
}
