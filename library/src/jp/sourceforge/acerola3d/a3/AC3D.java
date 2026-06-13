package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import org.newdawn.j3d.loaders.ac3d.*;
import java.net.URL;

/**
 * AC3D
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class AC3D extends A3Object {
    /**
     * modelPathはクラスパス以降のパス
     */
    public AC3D(String modelPath) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.AC3D"));
        AC3DModel model = AC3DLoader.load(modelPath);
        BranchGroup bg = new BranchGroup();
        bg.addChild(model.createInstance());
        setNode(bg);
    }
}
