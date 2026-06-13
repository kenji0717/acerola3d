package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;
import net.sf.nwn.loader.NWNLoader;
import net.sf.nwn.loader.AnimationBehavior;
import java.net.URL;
import com.sun.j3d.loaders.Scene;
import java.util.*;

/**
 * NWN
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class NWN extends A3Object {
    static NWNLoader loader = null;
    AnimationBehavior anim;
    public NWN(String urlString) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.NWN"));
        if (loader==null) {
            loader = new NWNLoader();
            loader.enableModelCache(true);
        }

        BranchGroup bg = new BranchGroup();
        URL url = new URL(urlString);
        Scene s = loader.load(url);

        Transform3D t1 = new Transform3D();
        t1.rotX(-Math.PI/2.0);
        Transform3D t2 = new Transform3D();
        t2.rotY(Math.PI);
        t2.mul(t1);
        TransformGroup tg = new TransformGroup();
        tg.setTransform(t2);
        bg.addChild(tg);

        tg.addChild(s.getSceneGroup());

        anim = (AnimationBehavior)s.getNamedObjects().get("AnimationBehavior");
        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),Double.MAX_VALUE));
        setNode(bg);
    }

    public String[] getAllAnimationNames() {
        Set set = anim.getAllAnimationNames();
        return (String[])set.toArray(new String[0]);
    }

    public void playAnimation(String animationName,boolean loop) {
        anim.playAnimation(animationName,loop);
    }

    public String[] getDefaultAnimations() {
        List list = anim.getDefaultAnimations();
        return (String[])list.toArray(new String[0]);
    }
}
