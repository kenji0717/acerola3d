/*
 * (2007,04/27)
 * Java Web Startの環境の時だけおかしな挙動をするので，
 * 実際にByteArrayOutputStreamに読み込んで返すようにした．
 */

package jp.sourceforge.acerola3d;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

class ResURLConnection extends URLConnection {
    String resStr = null;
    int size = -1;
    String enc = null;
    InputStream inputStream = null;
    public ResURLConnection(URL url) {
        super(url);
        resStr = url.getPath();
        if (resStr.startsWith("/"))
            resStr = resStr.substring(1);
    }
    public void connect() throws IOException {
        URL retURL = A23.classLoader.getResource(resStr);
        URLConnection c = retURL.openConnection();
        enc = c.getContentEncoding();
        //size = c.getContentLength();
        //inputStream = c.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(c.getInputStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        int len;
        byte buf[] = new byte[1024];
        while((len=bis.read(buf))!=-1) {
            bos.write(buf,0,len);
        }
        bos.flush();
        bos.close();
        bis.close();
        byte b[] = baos.toByteArray();
        size = b.length;
        inputStream = new BufferedInputStream(new ByteArrayInputStream(b));
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
    public String getContentEncoding() {
        try {
            if (!connected)
                connect();
        } catch(IOException e) {
            ;
        }
        return enc;
    }
}
