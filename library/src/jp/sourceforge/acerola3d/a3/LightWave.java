package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import com.sun.j3d.loaders.lw3d.Lw3dLoader;
//import java.net.URL;
import com.sun.j3d.loaders.Scene;

/**
 * LightWave
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class LightWave extends A3Object {
    static Lw3dLoader loader = null;
    public LightWave(String filePath) throws Exception {
        super(new A3InitData("LightWave"));
        if (loader==null) {
            loader = new Lw3dLoader(Lw3dLoader.LOAD_ALL);
            //loader.setBaseUrl(new URL("x-res:///data/"));
        }

        BranchGroup bg = new BranchGroup();
        //URL url = new URL(urlString);
        //Scene s = loader.load(url);
        Scene s = loader.load(filePath);

        bg.addChild(s.getSceneGroup());

        setNode(bg);
    }
}
