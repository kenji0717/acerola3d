package jp.sourceforge.acerola3d.a2;

import java.util.*;
import java.net.URL;

import jp.sourceforge.acerola3d.*;


public class A2Loader {
    static boolean isInitialized = false;
    static Action2D errA2 = null;
    static Hashtable<String,Action2D> a2Hash = null;

    public static Action2D load(String a2File) {
        A23.initA23();
        initA2Loader();

        Action2D a2 = null;
        try {
            a2 = a2Hash.get(a2File);
            if (a2 == null) {
                a2 = new Action2D(a2File,false);
                a2Hash.put(a2File,a2);
            }
        } catch(Exception e) {
            a2 = errA2;
        }
        return a2;
    }

    public static Action2D load(URL url) {
        //A23.initA23();
        initA2Loader();

        Action2D a2 = null;
        try {
            a2 = a2Hash.get(url.toExternalForm());
            if (a2==null) {
                a2 = new Action2D(url,false);
                a2Hash.put(url.toExternalForm(),a2);
            }
        }catch(Exception e) {
                a2 = errA2;
        }
        return a2;
    }

    static void initA2Loader() {
        if (isInitialized)
            return;
        if (!Action2D.isInitialized())
            Action2D.initAction2D();
        try {
          errA2 = new Action2D("x-res:///jp/sourceforge/acerola3d/resources/error.a2",false);
          a2Hash = new Hashtable<String,Action2D>();
        } catch(Exception e) {
            System.out.println("A2Loader.initA2Loader(). gaha!");
            e.printStackTrace();
        }
        isInitialized = true;
    }
}
