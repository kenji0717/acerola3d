package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

/**
 * 仮想空間内に配置することで，配置された視点からの
 * 視野を画像として取り出すことができるjavax.media.j3d.Nodeです．
 * ある視点からの視野を表示するだけであればA3SubCanvasやJA3SubCanvasが
 * 適していますが，画像をBufferedImageとして取り出して
 * 画像処理するような場合にこれを使用することができます．
 * ただ，このA3EyeはJava3DのNodeなので，実際にAcerola3Dの仮想空間に
 * 配置するには，A3Objectでラップすることになります．
 * 手軽に利用するにはA3EyeのかわりにA3VideoCameraを利用すると
 * 良いでしょう．
 */
public class A3Eye extends BranchGroup {
    int width;
    int height;
    BufferedImage image;
    ImageComponent2D buffer;
    Canvas3D offscreen;
    View view;
    AffineTransformOp op;
    //以下深度センサー用
    boolean useDepth=false;
    float[] depthData;
    GraphicsContext3D gc3d;
    Raster raster;

    /**
     * オフスクリーンのサイズを指定してA3Eyeオブジェクトを
     * 生成します．
     */
    public A3Eye(int w,int h) {
        this(w,h,false);
    }

    /**
     * オフスクリーンのサイズと深度情報を扱うかの指定をしてA3Eyeオブジェクトを
     * 生成します．ただし，震度情報の処理は現在(2013,03/13)未実装．
     */
    public A3Eye(int w,int h,boolean depth) {
        useDepth = depth;
        width = w;
        height = h;
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -height);
        op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        buffer = new ImageComponent2D(
            ImageComponent.FORMAT_RGB,image,true,true);
        buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gcs[] = gd.getConfigurations();
        GraphicsConfigTemplate3D gct3d = new GraphicsConfigTemplate3D();
        GraphicsConfiguration gc = gct3d.getBestConfiguration(gcs);
        offscreen = new Canvas3D(gc,true) {
@Override
                public void postSwap() {
    //System.out.println("GAHA--------------------");
                }
            };
        offscreen.setOffScreenBuffer(buffer);
        Screen3D s = offscreen.getScreen3D();
        s.setSize(new Dimension(256,256));
        s.setPhysicalScreenWidth(1.0);
        s.setPhysicalScreenHeight(1.0);

        if (useDepth) {
            depthData = new float[w*h];
            gc3d = offscreen.getGraphicsContext3D();
            DepthComponentFloat dcf = new DepthComponentFloat(w,h);
            dcf.setCapability(DepthComponent.ALLOW_DATA_READ);
            raster = new Raster(new javax.vecmath.Point3f(),
               Raster.RASTER_DEPTH,0,0,w,h,null,dcf);
            raster.setCapability(Raster.ALLOW_DEPTH_COMPONENT_READ);
            raster.setType(Raster.RASTER_DEPTH);
        }

        ViewPlatform viewPlatform = new ViewPlatform();
        view = new View();
        view.addCanvas3D(offscreen);
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());
        view.setFrontClipDistance(0.1);
        view.setBackClipDistance(100.0);
        view.setUserHeadToVworldEnable(true);
        view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION );
        //view.setProjectionPolicy(View.PARALLEL_PROJECTION);
        //view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
        view.setDepthBufferFreezeTransparent(true);
        view.attachViewPlatform(viewPlatform);
        this.addChild(viewPlatform);
    }

    /**
     * 引数で指定したBufferedImageに現在の視点からの視野を
     * レンダリングします．
     * 引数として与えるBufferedImageのサイズは，A3Eyeを
     * 生成する時のコンストラクタで指定したサイズでなければ
     * なりません．
     */
    public void renderOffscreenBuffer(BufferedImage bi) {
        offscreen.renderOffScreenBuffer();
        offscreen.waitForOffScreenRendering();

        op.filter(image,bi);
    }

    /**
     * 投影法を設定するためのメソッド。
     * デフォルトは透視投影法。
     */
    public void setProjectionMode(ProjectionMode m) {
        if (m==ProjectionMode.PERSPECTIVE) {
            view.setProjectionPolicy(javax.media.j3d.View.PERSPECTIVE_PROJECTION );
            view.setScreenScalePolicy(View.SCALE_SCREEN_SIZE);
        } else if (m==ProjectionMode.PARALLEL) {
            view.setProjectionPolicy(javax.media.j3d.View.PARALLEL_PROJECTION);
            view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        }
    }

    /**
     * このA3Eyeのオフスクリーンの物理座標系における横幅を
     * 設定するためのメソッドです。
     * ただし投影方法が平行投影方法(PARALLEL)の場合のみ
     * 有効で、透視投影法(PERSPECTIVE)の場合は無視されます。
     */
    public void setCanvasWidthInPWorld(double s) {
        double psw = offscreen.getScreen3D().getPhysicalScreenWidth();
        view.setScreenScale(psw/s);
    }

    /**
     * 透視投影法の場合の画角(Field of View)を設定するためのメソッド。
     * 平行投影法(PARALLEL)の場合は、この値は無視されます。
     */
    public void setFieldOfView(double f) {
        view.setFieldOfView(f);
    }

    /**
     * 深度情報が保存されているfloatの配列を返します。
     * ただし，このメソッドは現在(2013,03/13)未実装．
     */
    public float[] getDepthData() {
        ////offscreen.renderOffScreenBuffer();
        ////offscreen.waitForOffScreenRendering();
        //gc3d.readRaster(raster);
        DepthComponentFloat dcf =
            (DepthComponentFloat)raster.getDepthComponent();
        dcf.getDepthData(depthData);
        return depthData;
    }
}
