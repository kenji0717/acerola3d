package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.jlindamood.MS3D.MilkLoader;
import com.jlindamood.MS3D.MilkAnimation;
import java.net.URL;
import com.sun.j3d.loaders.Scene;

/**
 * MilkShape3D
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class MilkShape3D extends A3Object {
    static MilkLoader loader = null;
    MilkAnimation anim;
    public MilkShape3D(String urlString) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.MilkShape3D"));
        if (loader==null) {
            loader = new MilkLoader();
            loader.setFlags(MilkLoader.LOAD_ALL);
        }

        BranchGroup bg = new BranchGroup();
        URL url = new URL(urlString);
        Scene s = loader.load(url);

        //Transform3D t = new Transform3D();
        //TransformGroup tg = new TransformGroup();
        //tg.setTransform(t);
        //bg.addChild(tg);

        //tg.addChild(s.getSceneGroup());
        bg.addChild(s.getSceneGroup());

        anim = (MilkAnimation)s.getBehaviorNodes()[0];
        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),Double.MAX_VALUE));
        //tg.addChild(anim);
        bg.addChild(anim);
        setNode(bg);
    }

    public void setDuration(int d) {
        anim.setDuration(d);
    }

    public void setFrames(int s,int e) {
        anim.setFrames(s,e);
    }
}
