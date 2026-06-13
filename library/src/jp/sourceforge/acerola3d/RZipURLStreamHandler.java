/*
 * Created on 2004/08/14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jp.sourceforge.acerola3d;

import java.net.*;

/**
 * @author ksaito
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
class RZipURLStreamHandler extends URLStreamHandler {
    protected URLConnection openConnection(URL url) {
        RZipURLConnection c = new RZipURLConnection(url);
        return c;
    }
}
