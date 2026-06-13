package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;
import org.newdawn.j3d.loaders.md2.MD2Loader;
import org.newdawn.j3d.loaders.md2.MD2Model;
import org.newdawn.j3d.loaders.md2.MD2ModelInstance;
import java.io.*;

/**
 * MD2
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class MD2 extends A3Object {
    static MD2Loader loader = null;
    MD2Behavior behavior;
    public MD2(String model,String skin) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.MD2"));
        if (loader==null) {
            loader = new MD2Loader();
        }

        FileInputStream fisModel = new FileInputStream(model);
        FileInputStream fisSkin = new FileInputStream(skin);
        MD2Model md2model = loader.loadWithPCX(fisModel,fisSkin);
        MD2ModelInstance modelInstance = md2model.getInstance();
        modelInstance.setAnimation("run");

        BranchGroup bg = new BranchGroup();
        bg.addChild(modelInstance);

        behavior = new MD2Behavior(modelInstance);
        behavior.setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),Double.MAX_VALUE));
        bg.addChild(behavior);

        setNode(bg);
    }
}
