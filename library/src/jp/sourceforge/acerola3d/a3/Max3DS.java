package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.microcrowd.loader.java3d.max3ds.Loader3DS;
import java.net.URL;
import com.sun.j3d.loaders.Scene;

/**
 * Max3DS
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class Max3DS extends A3Object {
    static Loader3DS loader = null;
    public Max3DS(String urlString) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.Max3DS"));
        if (loader==null) {
            loader = new Loader3DS();
        }

        BranchGroup bg = new BranchGroup();
        URL url = new URL(urlString);
        Scene s = loader.load(url);

        BoundingSphere bs = new BoundingSphere(new Point3d(),Double.MAX_VALUE);

        Behavior behaviors[] = s.getBehaviorNodes();
        if (behaviors!=null) {
            for (Behavior b:behaviors) {
                b.setEnable(true);
                if (b instanceof Interpolator)
                    b.setSchedulingBounds(bs);
                if (b instanceof TransformInterpolator)
                    ((TransformInterpolator)b).getTarget().setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            }
        }

        bg.addChild(s.getSceneGroup());

        setNode(bg);
    }
}
