package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import org.newdawn.j3d.loaders.bsp.*;
import java.net.URL;
import java.io.*;

/**
 * BSP
 * 
 * 詳しくは以下のページ参照。
 * <ul>
 * <li><a href="http://sourceforge.jp/projects/acerola3d/svn/view/trunk/a3test/src/a3test/loaders/?root=acerola3d">サンプルプログラム</a></li>
 * <li><a href="http://acerola3d.sourceforge.jp/docs/programming/loaders/">概要</a></li>
 * </ul>
 */
public class BSP extends A3Object {
    static BSPLoader loader = null;
    public BSP(String modelPath,String baseDir) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.BSP"));
        if (loader==null) {
            loader = new BSPLoader();
        }
        BranchGroup bg = new BranchGroup();
        FileInputStream fis = new FileInputStream(modelPath);
        bg.addChild(loader.load(fis,baseDir));
        setNode(bg);
    }
}
