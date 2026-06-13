package jp.sourceforge.acerola3d.a3;

import jp.sourceforge.acerola3d.A23;
import javax.media.*;
import java.net.URL;

/**
 * JMFを使って動画を表示するためのA3Objectです。
 */
public class A3Video extends A3Object {
    A3VideoController controller;
    public A3Video(String url) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3Video"));
        //jp.sourceforge.acerola3d.A23.initA23();
        MediaLocator ml = null;
        if (url.startsWith("x-res:")) {
            ClassLoader cl = A23.getClassLoader();
            url = url.substring(6);
            while (url.startsWith("/"))
                url = url.substring(1);
            URL urlObj = cl.getResource(url);
            ml = new MediaLocator(urlObj);
        } else {
            ml = new MediaLocator(url);
        }
        controller = new A3VideoController();
        controller.open(ml);
        setNode(controller.a3VideoRenderer.scene);
    }
    public void init() {
        super.init();
        controller.start();
    }
    public void dispose() {
        super.dispose();
        controller.stop();
    }
}
