package jp.sourceforge.acerola3d.a3;

import jp.sourceforge.acerola3d.a3.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.interactivemesh.j3d.interchange.ext3d.XModelLoader;
import java.net.URL;
import com.sun.j3d.loaders.Scene;
import java.util.*;

/**
 * X3D
 */
public class X3D extends A3Object {
    static XModelLoader loader = null;
    public X3D(String urlString) throws Exception {
        super(new A3InitData("X3D"));
        if (loader==null) {
            loader = new XModelLoader();
        }

        BranchGroup bg = new BranchGroup();
        URL url = new URL(urlString);
        Scene s = loader.load(url);

        bg.addChild(s.getSceneGroup());

        setNode(bg);
    }
}
