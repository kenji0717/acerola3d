package jp.sourceforge.acerola3d.a3;

import jp.sourceforge.acerola3d.a3.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.net.URL;
import com.sun.j3d.loaders.Scene;
import com.eteks.sweethome3d.j3d.DAELoader;

/**
 * COLLADA
 */
public class COLLADA extends A3Object {
    static DAELoader loader = null;
    public COLLADA(String urlString) throws Exception {
        super(new A3InitData("COLLADA"));
        if (loader==null) {
            loader = new DAELoader();
        }

        BranchGroup bg = new BranchGroup();
        URL url = new URL(urlString);
        Scene s = loader.load(url);

        bg.addChild(s.getSceneGroup());

        setNode(bg);
    }
}
