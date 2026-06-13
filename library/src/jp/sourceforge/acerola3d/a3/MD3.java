package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;
import org.newdawn.j3d.loaders.md3.MD3Loader;
import org.newdawn.j3d.loaders.md3.MD3Model;
import org.newdawn.j3d.loaders.md3.MD3ModelInstance;
import org.newdawn.j3d.loaders.md3.MD3AnimConfig;

/**
 * MD3
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class MD3 extends A3Object {
    static MD3Loader loader = null;
    MD3Behavior behavior;
    public MD3(String prefix) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.MD3"));
        if (loader==null) {
            loader = new MD3Loader();
        }

        MD3Model model = loader.loadCharacterModel(prefix);
        MD3ModelInstance modelInstance = model.getInstance();
        modelInstance.setAnimation("lower",MD3AnimConfig.IDLE);

        BranchGroup bg = new BranchGroup();
        bg.addChild(modelInstance);

        behavior = new MD3Behavior(modelInstance);
        behavior.setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),Double.MAX_VALUE));
        bg.addChild(behavior);

        setNode(bg);
    }
}
