/*
 * Created on 2004/08/14
 *
 * んー．まだ色々な点できちんと作られてないので，
 * 改良したほうが良いかもしれない．java.net.URLConnectionのAPIを
 * 良く読むべし．
 */
package jp.sourceforge.acerola3d;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.HashMap;

class RZipURLConnection extends URLConnection {
    String internalURL = null;
    String entryName = null;
    int size = -1;
    InputStream inputStream = null;
    static SoftRefMap<String,HashMap<String,byte[]>> zipCache = new SoftRefMap<String,HashMap<String,byte[]>>();

    public RZipURLConnection(URL url) {
        super(url);
        String urlString = url. toExternalForm();
        entryName = urlString.substring(urlString.lastIndexOf('!')+1);
        if (entryName.startsWith("/"))
            entryName = entryName.substring(1);
        internalURL = urlString.substring(0,urlString.lastIndexOf('!'));
        internalURL = internalURL.substring(7);
        internalURL = checkInternalURL(internalURL);
        internalURL = checkInternalURL2(internalURL);
    }
    public void connect() throws IOException {
        HashMap<String,byte[]> hm = zipCache.get(internalURL);
        if (hm==null) {
            hm = new HashMap<String,byte[]>();
            InputStream is = new URL(internalURL).openStream();
            ZipInputStream zis = new ZipInputStream(is);
            BufferedInputStream bis = new BufferedInputStream(zis);
            while (true) {
                ZipEntry ze = zis.getNextEntry();
                if (ze == null)
                    break;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(baos);
                int len;
                byte buf[] = new byte[1024];
                while((len=bis.read(buf))!=-1) {
                    bos.write(buf,0,len);
                }
                bos.flush();
                bos.close();
                byte b[] = baos.toByteArray();
                hm.put(ze.getName(),b);
            }
            bis.close();
            zipCache.put(internalURL,hm);
        }
        byte[] b = hm.get(entryName);
        inputStream = new BufferedInputStream(new ByteArrayInputStream(b));
        size = b.length;
        connected = true;
    }
    public int getContentLength() {
        try {
            if (!connected)
                connect();
        } catch(IOException e) {
            ;
        }
        return size;
    }
    public InputStream getInputStream() throws IOException {
        if (!connected)
            connect();
        if (inputStream==null)
            throw new IOException();
        return inputStream;
    }

    //j3d-vrml97.jarを使うと、どっかのだれかが「file:///home...」を「file://home...」
    //に書き換えてしまう。むりやりなおしてしまうことにする。
    String checkInternalURL(String s) {
        if (s.startsWith("file://")&&(!s.startsWith("file:///")))
            return "file:///"+s.substring(7);
        else
            return s;
    }
    //j3d-vrml97.jarを使うと、どっかのだれかが「x-res:///...」を「x-res://...」
    //に書き換えてしまう。むりやりなおしてしまうことにする。
    String checkInternalURL2(String s) {
        if (s.startsWith("x-res://")&&(!s.startsWith("x-res:///")))
            return "x-res:///"+s.substring(8);
        else
            return s;
    }
}
