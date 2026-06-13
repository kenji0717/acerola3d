package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;


import org.jdesktop.j3d.loaders.vrml97.VrmlLoader;

import com.sun.j3d.loaders.Scene;

import java.net.*;
import java.util.Hashtable;

import jp.sourceforge.acerola3d.A23;

/**
 * VRMLの3Dデータを3D仮想空間上に表示するためのA3Objectです。
 */
public class VRML extends A3Object {
    static SharedGroup errVRML = null;
    static VrmlLoader loader = null;
    static Hashtable<URL,ShapeAndBF> sharedGroupHash = null;
    static boolean isInitialized = false;

    static void initVRML() {
        if (isInitialized==true)
            return;
        try {
            loader = new VrmlLoader();
            //j3d-vrml97-0.1.0のAPIによればVrmlLoaderのコンストラクタの引数は
            //さしあたり無視されるということらしい．
            //loader = new VrmlLoader(VrmlLoader.LOAD_VIEW_GROUPS|
            //                        VrmlLoader.LOAD_SOUND_NODES|
            //                        VrmlLoader.LOAD_LIGHT_NODES|
            //                        VrmlLoader.LOAD_FOG_NODES);
            sharedGroupHash = new Hashtable<URL,ShapeAndBF>();
            errVRML = new SharedGroup();
            errVRML.addChild(loader.load(new URL("x-res:///jp/sourceforge/acerola3d/resources/error.wrl")).getSceneGroup());
        } catch(Exception e) {
            System.out.println("VRMLLoader.initVRMLLoader(). gaha!");
            e.printStackTrace();
        }
        isInitialized = true;
    }
    public synchronized static VRML load(URL vURL) throws Exception {
        return load(vURL,false,null);
    }
    public synchronized static VRML load(String vURL) throws Exception {
        return load(vURL,false,null);
    }
    public synchronized static VRML load(String vURL,boolean isEnableBehavior,String label) throws Exception {
        A23.initA23();
        return load(new URL(vURL),isEnableBehavior,label);
    }
    public synchronized static VRML load(URL vURL,boolean isEnableBehavior,String label) throws Exception {
        //A23.initA23();
        initVRML();

        A3InitData initData = new A3InitData("jp.sourceforge.acerola3d.a3.VRML");
        initData.setEnableBehavior(isEnableBehavior);
        initData.set(vURL,label);
        VRML vrml = new VRML(initData);
        return vrml;
    }

    /**
     * Action3Dファイルの中にあるVRMLファイルをloadしてSharedGroup
     * をreturnする。
     */
    static ShapeAndBF loadVRML(URL url) throws Exception {
        ShapeAndBF shape = sharedGroupHash.get(url);
        if (shape!=null) {
            return shape;
        } else {
            shape = new ShapeAndBF();
            shape.sg = new SharedGroup();
            Scene scene = loader.load(url);
            shape.sg.addChild(scene.getSceneGroup());
            shape.b = scene.getBackgroundNodes();
            shape.f = scene.getFogNodes();
//          shape.sg.compile();
            sharedGroupHash.put(url,shape);
            return shape;
        }
    }
//  --------------------------------------------------------------------------------------------
//  --------------------------------------------------------------------------------------------
//  --------------------------------------------------------------------------------------------
    URL url;

    /**
     * StringのURLをもとにVRMLオブジェクトを生成するコンストラクタ。
     */
    public VRML(String url) {
        super(initHack());
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.VRML");
        d.set(url);
        realConstructor(d);
    }

    /**
     * URLをもとにVRMLオブジェクトを生成するコンストラクタ。
     */
    public VRML(URL url) {
        super(initHack());
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.VRML");
        d.set(url.toString());
        realConstructor(d);
    }

    /**
     * A3InitDataをもとにVRMLオブジェクトを生成するコンストラクタ。
     * A3InitDataの引数の数は、1でなければなりません。
     * 
     * <table border="1" summary="required data in A3InitData">
     * <tr><td>0:String </td><td>URL of a VRML file</td><td>必須</td></tr>
     * </table>
     */
    public VRML(A3InitData d) {
        super(d);
        realConstructor(d);
    }

    static A3InitData initHack() {
        return new A3InitData("jp.sourceforge.acerola3d.a3.VRML");
    }
    void realConstructor(A3InitData d) {
        A23.initA23();
        initVRML();

        try {
            if (d.get(0) instanceof String)
                url = new URL(d.getString(0));
            else if (d.get(0) instanceof URL)
                url = d.getURL(0);
            BranchGroup bg = new BranchGroup();
            ShapeAndBF shape = loadVRML(url);
            Link l = new Link(shape.sg);
            bg.addChild(l);
            if (shape.b!=null) {
                for (int i=0;i<shape.b.length;i++)
                    bg.addChild(shape.b[i].cloneNode(false));
            }
            if (shape.f!=null) {
                for (int i=0;i<shape.f.length;i++)
                    bg.addChild(shape.f[i].cloneNode(false));
            }
            setNode(bg);
        } catch(Exception e) {
            e.printStackTrace();
            try {
                ShapeAndBF shape = loadVRML(new URL("x-res:///jp/sourceforge/acerola3d/resources/error.wrl"));
                Link l = new Link(shape.sg);
                setNode(l);
            } catch (Exception ee) {
                System.out.println("VRML(). gaha");
                ee.printStackTrace();
            }
        }
    }

    /**
     * A3UpdateDataの情報をもとに現在の3Dオブジェクトの
     * 状態を更新します。ですが、現在VRMLは更新する情報を特に持たないので、
     * スーパークラスのupdateを呼んでいるだけです。
     * 
     * なので、A3UpdateDataにセットする情報は座標などの標準的なデータだけで
     * 良いです。
     */
    public void update(A3UpdateData d) {
        super.update(d);
    }
    /*
    void setSharedGroup(SharedGroup sg) {
    }

    public Node getNode() {
        return null;
    }
    */
    /**
     * キャッシュに保存されたVRMLデータを全て
     * 削除します。
     */
    public static void clearCash() {
        if (sharedGroupHash!=null)
            sharedGroupHash.clear();
    }
    /**
     * 引数のURLから読み込まれたVRMLデータの
     * キャッシュを削除します。
     */
    public static void clearCash(URL url) {
        if (sharedGroupHash!=null)
            sharedGroupHash.remove(url);
    }
    /**
     * 引数のURL文字列から読み込まれたVRMLデータの
     * キャッシュを削除します。
     */
    public static void clearCash(String url) {
        try {
            if (sharedGroupHash!=null)
                sharedGroupHash.remove(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
//通常のオブジェクトと背景、霧をまとめた物
class ShapeAndBF {
    SharedGroup sg;
    Background b[];
    Fog f[];
}
