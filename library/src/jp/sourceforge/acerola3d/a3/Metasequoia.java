package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;
import jp.kurusugawa.java3d.loader.mqo.MQOLoader;
import java.net.URL;
import com.sun.j3d.loaders.Scene;
import java.util.*;

/**
 * Metasequoia
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class Metasequoia extends A3Object {
    static MQOLoader loader = null;
    public Metasequoia(String urlString) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.Metasequoia"));
        if (loader==null) {
            loader = new MQOLoader();
        }

        BranchGroup bg = new BranchGroup();
        URL url = new URL(urlString);
        Scene s = loader.load(url);

        bg.addChild(s.getSceneGroup());

        setNode(bg);
    }
}
