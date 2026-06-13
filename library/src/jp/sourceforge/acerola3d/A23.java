package jp.sourceforge.acerola3d;

import jp.sourceforge.acerola3d.a3.*;
import java.net.*;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * x-res:とx-rzip:プロトコルのURLを使うことができるように
 * 初期化を行うためのクラスです。
 * x-res:プロトコルはJavaのクラスパスから検索されるリソースを
 * URLで参照できるようにするためのプロトコルです。
 * x-rzip:プロトコルはZIPファイルの中のZIPファイルの中のファイル
 * をURLで参照できるようにするためのプロトコルです。
 * ですが、実際には他のクラスを使用するタイミングで自動的に
 * このクラスのメソッドが呼ばれて初期化されるようにしている
 * ので、一般的には知る必要のないクラスです。Acerola3Dや
 * Acerola2Dをまったく使わずに、x-res:とx-rzip:プロトコルの
 * URLだけ使いたい場合などは、A23.initA23();を実行することで
 * それが可能になります。
 */
public class A23 {
    static final A23 a23 = new A23();
    static boolean isInitialized;// = false;
    static ClassLoader classLoader;
    {
        initA23();
    }
//    public static final Serializable NULL = "NULL";

    A23() { ; }
    static A23URLStreamHandlerFactory a23Factory;

    /**
     * URLを拡張してx-res:とx-rzip:プロトコルを使えるように
     * 初期化するメソッドです。
     */
    public static synchronized void initA23() {
        if (!isInitialized) {
            try {
                classLoader = Class.forName("jp.sourceforge.acerola3d.A23").getClassLoader();
            }catch (Exception e) {
                System.out.println("A23.initA23(). A23URLStreamHandlerFactory is disabled.");
                //System.exit(0);
            }
            a23Factory = new A23URLStreamHandlerFactory();
            try {
                AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Object>() {
                    public Object run() {
                        URL.setURLStreamHandlerFactory(a23Factory);
                        return null;
                    }
                });
            } catch(Error e) {
                //lg3dで使うような場合のためにこれを追加．
                System.out.println("A23.initA23(). URLStreamHandlerFactory has already set.");
                //e.printStackTrace();
            }
            //***** 2014,11/01追加ここから *****//
            //以下の一行でJava3DのView Frustum CullingをOFFにしている
            //そうしないとごくまれにシーンの一部が表示されなくなる．
            //たぶんAcerola3Dの方のバグだと思うので，そのバグが修正
            //されたら以下の一行を削除するべし．
            System.setProperty("j3d.viewFrustumCulling","false");
            //***** 2014,11/01追加ここまで *****//
            //***** 2019,01/18追加ここから *****//
            //一部のWindowsなPCで，起動に1分以上かかるという
            //不具合があるのでい以下の一行を入れてみる．
            System.setProperty("jogamp.gluegen.UseTempJarCache","false");
            //***** 2019,01/18追加ここまで *****//
            isInitialized = true;
        }
    }
    /**
     * Acerola3D,Acerola2Dシステムが使用するクラスローダ
     * をセットするメソッドです。
     * 具体的にはx-res:プロトコルでリソースを探す時や、
     * jp.sourceforge.acerola3d.a3.A3Generatorがクラスを
     * 生成する時などに使用されます。
     * デフォルトではA23クラスを読んだクラスローダ
     * がセットされてます。
     * @param cl Acerola3D,Acerola2Dで使用するクラスローダ
     */
    public static void setClassLoader(ClassLoader cl) {
        classLoader = cl;
    }
    /**
     * Acerola3D,Acerola2Dシステムが使用しているクラスローダ
     * を取り出すためのメソッドです。
     * @return Acerola3D,Acerola2Dで使用しているクラスローダ
     */
    public static ClassLoader getClassLoader() {
        return classLoader;
    }
    //以下のメソッドは必要ないようにしてしまった。テスト必要
    /*
    public static void initA23() {
        //if (isInitialized)
        //    return;
        try {
            classLoader = Class.forName("jp.sourceforge.acerola3d.A23").getClassLoader();
        }catch (Exception e) {
            System.out.println("A23.initA23(). fatal error!");
            System.exit(0);
        }


//        System.setProperty("uri.content.handler.pkgs",
//            "vlc.net.content|net.sf.elm_ve.uri.net.content");
//        System.setProperty("uri.content.handler.pkgs",
//            "vlc.net.content");
//        System.setProperty("uri.protocol.handler.pkgs",
//            "vlc.net.protocol|net.sf.elm_ve.uri.net.protocol");
//        URIResourceStreamFactory res_fac = URI.getURIResourceStreamFactory();
//        if (!(res_fac instanceof A23ResourceStreamFactory)) {
//            res_fac = new A23ResourceStreamFactory(res_fac);
//            res_fac = new JavascriptResourceFactory(res_fac);
//            URI.setURIResourceStreamFactory(res_fac);
//        }
//        ContentHandlerFactory c_fac = URI.getContentHandlerFactory();
//        if (!(c_fac instanceof VRMLContentHandlerFactory)) {
//            c_fac = new VRMLContentHandlerFactory(null,null,c_fac);
//            URI.setContentHandlerFactory(c_fac);
//        }
//        FileNameMap fn_map = URI.getFileNameMap();
//        if (!(fn_map instanceof VRMLFileNameMap)) {
//            fn_map = new VRMLFileNameMap(fn_map);
//            URI.setFileNameMap(fn_map);
//        }

        //昔は必要だったけど今は必要ないと思う。
        //initProxy();

        //isInitialized = true;
    }
    */

    /*
    //一応残しておくが、今は不必要な設定
    static void initProxy() {
System.out.println("A23.initProxy(): ftp proxy setting is not implemented yet!?!?");
        try {
            System.getProperties().put("java.net.useSystemProxies","true");
            ProxySelector ps = ProxySelector.getDefault();
            List<Proxy> l = ps.select(new java.net.URI("http://acerola3d.sourceforge.jp/samples/a3/axis.a3"));
            if (l.size()>0) {
                Proxy p = l.get(0);
                if (p.type().equals(Proxy.NO_PROXY)) {
                    System.getProperties().put("proxySet","false");
                } else {
                    String s = p.toString();
                    if (s.equals("DIRECT")) {
                        System.getProperties().put("proxySet","false");
                    } else {
                        String sHost = s.substring(s.lastIndexOf(' ')+1);
                        sHost = sHost.substring(0,sHost.lastIndexOf(':'));
                        if (sHost.contains("/")) // <-- WindowsのJWSの時だけ必要。どうにかして欲しい
                            sHost = sHost.substring(0,sHost.lastIndexOf('/'));
                        String sPort = s.substring(s.lastIndexOf(':')+1);
                        System.getProperties().put("proxySet","true");
                        System.getProperties().put("proxyHost",sHost);
                        System.getProperties().put("proxyPort",sPort);
System.getProperties().put("http.proxyHost",sHost);
System.getProperties().put("http.proxyPort",sPort);
                        System.out.println("http proxy settings");
                        System.out.println("host:"+sHost+":");
                        System.out.println("port:"+sPort+":");
                    }
                }
            } else {
                System.getProperties().put("proxySet","false");
            }
        } catch (Exception e) {
            e.printStackTrace();
//            System.getProperties().put("proxySet","false");
        }
    }
    public static void setHttpProxy(boolean set,String host,String port) {
        if (set == true) {
            System.getProperties().put("proxySet","true");
            System.getProperties().put("proxyHost",host);
            System.getProperties().put("proxyPort",port);
            //for org.ietf.uri
            System.getProperties().put("http.proxyHost",host);
            System.getProperties().put("http.proxyPort",port);
        } else {
            System.getProperties().put("proxySet","false");
            //for org.ietf.uri
            System.getProperties().remove("http.proxyHost");
            System.getProperties().remove("http.proxyPort");
        }
    }
    public static void setFtpProxy(boolean set,String host,String port) {
        if (set == true) {
            System.getProperties().put("ftpProxySet","true");
            System.getProperties().put("ftpProxyHost",host);
            System.getProperties().put("ftpProxyPort",port);
System.out.println("A23.setFtpProxy(): ftp proxy setting for org.ietf.uri is not implemented yet!?!?");
        } else {
            System.getProperties().put("ftpProxySet","false");
        }
    }
    */

    static A3Object.UpperDirection defaultUpperDirection = A3Object.UpperDirection.Y;
    /**
     * 3D仮想空間の上方向のデフォルト値を設定します。
     */
    public static void setDefaultUpperDirection(A3Object.UpperDirection d) {
        defaultUpperDirection = d;
    }
    /**
     * 3D仮想空間の上方向のデフォルト値を取得します。
     */
    public static A3Object.UpperDirection getDefaultUpperDirection() {
        return defaultUpperDirection;
    }

    /**
     * x-rzip://プロトコルの読み込みを高速化するためのキャッシュをクリアします。
     * x-rzip:を大量に利用するようなプログラムを作成した時には適切なタイミングで
     * このメソッドを呼び出すとメモリがクリアされて良いかもしれません。
     */
    public static void clearZipCache() {
        RZipURLConnection.zipCache.clear();
    }
    /**
     * このシステムで使用している一切のリソースを開放します。未実装。
     */
    public static void cleanUp() {
        Util.cleanUp();
    }
}
