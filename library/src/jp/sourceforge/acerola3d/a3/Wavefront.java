package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.Scene;

/**
 * Wavefront(OBJ)
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class Wavefront extends A3Object {
    static ObjectFile loader = null;
    public Wavefront(String filePath) throws Exception {
        super(new A3InitData("LightWave"));
        if (loader==null) {
            loader = new ObjectFile();
        }

        BranchGroup bg = new BranchGroup();
        Scene s = loader.load(filePath);

        bg.addChild(s.getSceneGroup());

        setNode(bg);
    }
}
