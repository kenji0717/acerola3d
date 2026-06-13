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
class A23URLStreamHandlerFactory implements URLStreamHandlerFactory {
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equals("x-rzip")) {
            return new RZipURLStreamHandler();
        } else if (protocol.equals("x-res")) {
            return new ResURLStreamHandler();
        } else {
            return null;
        }
    }
}
