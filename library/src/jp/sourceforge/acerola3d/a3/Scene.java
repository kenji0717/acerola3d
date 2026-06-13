package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;

import java.util.*;

import jp.sourceforge.acerola3d.a3.A3CanvasInterface.NaviMode;
import jp.sourceforge.acerola3d.A23;

/**
 * シーンの情報を全て記録・管理しておくためのオブジェクト
 */
class Scene implements Component2DContainerInterface {
    int sceneNo;
    A3VirtualUniverse universe;
    A3CanvasInterface canvas = null;
    BranchGroup mainGroup;
    BranchGroup cameraGroup;
    A3Object.UpperDirection upperDirection = A3Object.UpperDirection.Y;

    A3Object background = null;
    A3Object avatar = null;
    Hashtable<A3Object,BranchGroup> a3Hash = new Hashtable<A3Object,BranchGroup>();
    ArrayList<Component2D> components2D = new ArrayList<Component2D>();
    Hashtable<A3Object,BranchGroup> lockedA3Hash = new Hashtable<A3Object,BranchGroup>();

    Scene(A3VirtualUniverse v,int no) {
        sceneNo = no;
        universe = v;
        canvas = v.canvas;
        mainGroup = new BranchGroup();
        mainGroup.setCapability(BranchGroup.ALLOW_DETACH);
        mainGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        mainGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        mainGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        cameraGroup = new BranchGroup();
        cameraGroup.setCapability(BranchGroup.ALLOW_DETACH);
        cameraGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        cameraGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        cameraGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        if (A23.getDefaultUpperDirection()==A3Object.UpperDirection.Y) {
            //upperDirection = A3Object.UpperDirection.Y;
            ;//default
        } else if (A23.getDefaultUpperDirection()==A3Object.UpperDirection.Z) {
            upperDirection = A3Object.UpperDirection.Z;
            cameraNowV.set(0.0,-2.0,0.0);
            cameraNowQ.set(1.0*Math.sin(Math.PI/4.0),0.0,0.0,Math.cos(Math.PI/4.0));
            cameraNowS = 1.0;
            cameraNextV.set(cameraNowV);
            cameraNextQ.set(cameraNowQ);
            cameraNextS = cameraNowS;
            defaultV.set(cameraNowV);
            defaultQ.set(cameraNowQ);
            defaultS = cameraNowS;
        } else {
            System.out.println("Error in Scene.(init). ");
        }
    }
    void activate() {
        universe.rootGroup.addChild(mainGroup);
        universe.tGroup.addChild(cameraGroup);
        if (controller!=null) {
            universe.addA3Listener(controller);
            canvas.addKeyListener(controller);
            controller.init();
        }
        //カメラの操作は必要ないはず。
    }
    void deactivate() {
        universe.rootGroup.removeChild(mainGroup);
        universe.tGroup.removeChild(cameraGroup);
        if (controller!=null) {
            universe.removeA3Listener(controller);
            canvas.removeKeyListener(controller);
            controller.stop();
        }
        //カメラの操作は必要ないはず。
    }
    // A3Objectの追加と削除
    /**
     * A3Objectを追加して表示されるようにします。
     */
    public void add(A3Object a) {
        add_OLD(a);
    }
    void add_NEW(A3Object a) {
        if (a3Hash.containsKey(a))
            return;
        setAppearanceOverrideEnable((Group)a.getNode());
        if (!a.getNode().isCompiled())
            ((BranchGroup)a.getNode()).compile();
        a.setComponent2DContainerInterface(this);
        a.lockedA3=false;
        a.init();
        final A3BranchGroup bg = a.getA3BranchGroup();
        Runnable r = new Runnable() {
            public void run() {
                mainGroup.addChild(bg);
            }
        };
        universe.timerBehavior.addRunnable(r);
        a3Hash.put(a,bg);
    }
    void add_OLD(A3Object a) {
        if (a3Hash.containsKey(a))
            return;
        setAppearanceOverrideEnable((Group)a.getNode());
        if (!a.getNode().isCompiled())
            ((BranchGroup)a.getNode()).compile();
        a.setComponent2DContainerInterface(this);
        a.lockedA3=false;
        a.init();
        A3BranchGroup bg = a.getA3BranchGroup();
        mainGroup.addChild(bg);
        a3Hash.put(a,bg);
    }
    @SuppressWarnings("unchecked")
    static void setAppearanceOverrideEnable(Group g) {
        if (g.isCompiled())
            return;
        Enumeration e = g.getAllChildren();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (o instanceof Shape3D) {
                try {
                    ((Shape3D)o).setAppearanceOverrideEnable(true);
                } catch(CapabilityNotSetException ee) {
                    System.out.println("Scene.setAppearanceOverrideEnable().fail!");
                }
            } else if (o instanceof Group) {
                setAppearanceOverrideEnable((Group)o);
            } else if (o instanceof Link){
                SharedGroup sg = ((Link)o).getSharedGroup();
                setAppearanceOverrideEnable(sg);
            }
        }
    }
    /**
     * 指定されたA3Objectの登録を削除して表示されないように
     * します。
     */
    public void del(A3Object a) {
        del_OLD(a);
    }
    void del_NEW(A3Object a) {
        final BranchGroup bg = a3Hash.get(a);
        if (bg==null)
            return;
        //a.unpolygonize();//gaha
        Runnable r = new Runnable() {
            public void run() {
                bg.detach();
            }
        };
        universe.timerBehavior.addRunnable(r);
        a.dispose();
        a3Hash.remove(a);
    }
    void del_OLD(A3Object a) {
        BranchGroup bg = a3Hash.get(a);
        if (bg==null)
            return;
        //a.unpolygonize();//gaha
        bg.detach();
        a.dispose();
        a3Hash.remove(a);
    }

    /**
     * 登録されている全てのA3Objectを削除して表示されないようにします。
     *
     */
    public void delAll() {
        Enumeration<A3Object> e = a3Hash.keys();
        while (e.hasMoreElements()) {
            A3Object a3 = e.nextElement();
            BranchGroup bg = a3Hash.get(a3);
            bg.detach();
            a3.dispose();
        }
        a3Hash.clear();
    }

    A3Object backgroundA3;
    /**
     * 背景を表すA3Objectをセットします。
     */
    public void setBackground(A3Object a) {
        if (a3Hash.containsKey(a))
            del(a);
        backgroundA3 = a;
        a.setPickable(false);
        add(a);
    }

    /**
     * 背景を削除します。
     */
    public void delBackground() {
        if (backgroundA3==null)
            return;
        del(backgroundA3);
        background = null;
    }

    /**
     * アバタをセットします。
     */
    public void setAvatar(A3Object a) {
        avatar = a;
    }

    /**
     * セットされたアバタを取得します。
     */
    public A3Object getAvatar() {
        return avatar;
    }

    // cameraの手動操作のための変数とメソッド
    Vector3d cameraNowV = new Vector3d(0.0,0.0,2.0);
    Quat4d cameraNowQ = new Quat4d(0.0,0.0,0.0,1.0);
    double cameraNowS = 1.0;
    Vector3d cameraNextV = new Vector3d(0.0,0.0,2.0);
    Quat4d cameraNextQ = new Quat4d(0.0,0.0,0.0,1.0);
    double cameraNextS = 1.0;
    Vector3d defaultV = new Vector3d(0.0,0.0,2.0);
    Quat4d defaultQ = new Quat4d(0.0,0.0,0.0,1.0);
    double defaultS = 1.0;

    /**
     * カメラのデフォルトの位置を指定します。
     */
    public void setDefaultCameraLoc(double x,double y,double z) {
        defaultV = new Vector3d(x,y,z);
    }

    /**
     * カメラのデフォルトの位置を指定します。
     */
    public void setDefaultCameraLoc(Vector3d loc) {
        defaultV = new Vector3d(loc);
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraQuat(double x,double y,double z,double w) {
        defaultQ = new Quat4d(x,y,z,w);
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraQuat(Quat4d quat) {
        defaultQ = new Quat4d(quat);
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraRot(double x,double y,double z) {
        defaultQ = Util.euler2quat(x,y,z);
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraRot(Vector3d rot) {
        defaultQ = Util.euler2quat(rot);
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraRev(double x,double y,double z) {
        defaultQ = Util.euler2quat(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z);
    }

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraRev(Vector3d rot) {
        defaultQ = Util.euler2quat(Math.PI/180.0*rot.y,Math.PI/180.0*rot.x,Math.PI/180.0*rot.z);
    }

    /**
     * カメラのデフォルトの拡大率を指定します。
     */
    public void setDefaultCameraScale(double s) {
        defaultS = s;
    }

    /**
     * カメラの位置、回転、拡大率をリセットしてデフォルトに戻します。
     */
    public void resetCamera() {
        cameraNowV.set(defaultV);
        cameraNowQ.set(defaultQ);
        cameraNowS = defaultS;
        cameraNextV.set(defaultV);
        cameraNextQ.set(defaultQ);
        cameraNextS = defaultS;
    }

    /**
     * カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(double x,double y,double z) {
        cameraNextV.set(x,y,z);
    }

    /**
     * カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(Vector3d loc) {
        cameraNextV.set(loc);
    }

    /**
     * カメラの位置を即時に指定します。
     */
    public void setCameraLocImmediately(double x,double y,double z) {
        cameraNowV.set(x,y,z);
        cameraNextV.set(x,y,z);
    }

    /**
     * カメラの位置を即時に指定します。
     */
    public void setCameraLocImmediately(Vector3d loc) {
        cameraNowV.set(loc);
        cameraNextV.set(loc);
    }

    public void addCameraLoc(double x,double y,double z) {
        cameraNextV.x += x;
        cameraNextV.y += y;
        cameraNextV.z += z;
    }
    public void addCameraLoc(Vector3d loc) {
        cameraNextV.add(loc);
    }
    public void addCameraLocImmediately(double x,double y,double z) {
        cameraNextV.x += x;
        cameraNextV.y += y;
        cameraNextV.z += z;
        cameraNowV.set(cameraNextV);
    }
    public void addCameraLocImmediately(Vector3d loc) {
        cameraNextV.add(loc);
        cameraNowV.set(cameraNextV);
    }

    public void moveCameraForward(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,0,1));
        v.scale(-l);
        addCameraLoc(v);
    }

    public void moveCameraForwardImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,0,1));
        v.scale(-l);
        addCameraLocImmediately(v);
    }

    public void moveCameraBackward(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,0,1));
        v.scale(l);
        addCameraLoc(v);
    }

    public void moveCameraBackwardImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,0,1));
        v.scale(l);
        addCameraLocImmediately(v);
    }

    public void moveCameraRight(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(1,0,0));
        v.scale(l);
        addCameraLoc(v);
    }

    public void moveCameraRightImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(1,0,0));
        v.scale(l);
        addCameraLocImmediately(v);
    }

    public void moveCameraLeft(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(1,0,0));
        v.scale(-l);
        addCameraLoc(v);
    }

    public void moveCameraLeftImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(1,0,0));
        v.scale(-l);
        addCameraLocImmediately(v);
    }

    public void moveCameraUp(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,1,0));
        v.scale(l);
        addCameraLoc(v);
    }

    public void moveCameraUpImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,1,0));
        v.scale(l);
        addCameraLocImmediately(v);
    }

    public void moveCameraDown(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,1,0));
        v.scale(-l);
        addCameraLoc(v);
    }

    public void moveCameraDownImmediately(double l) {
        Vector3d v = Util.trans(cameraNowQ,new Vector3d(0,1,0));
        v.scale(-l);
        addCameraLocImmediately(v);
    }
    
    public void moveCameraTo(Vector3d v, double l) {
        Vector3d vv = new Vector3d();
        vv.sub(v,cameraNowV);
        if (vv.length()!=0.0) vv.normalize();
        vv.scale(l);
        addCameraLoc(vv);
    }

    public void moveCameraTo(double x, double y, double z, double l) {
        Vector3d vv = new Vector3d(x,y,z);
        vv.sub(cameraNowV);
        if (vv.length()!=0.0) vv.normalize();
        vv.scale(l);
        addCameraLoc(vv);
    }

    public void moveCameraToImmediately(Vector3d v, double l) {
        Vector3d vv = new Vector3d();
        vv.sub(v,cameraNowV);
        if (vv.length()!=0.0) vv.normalize();
        vv.scale(l);
        addCameraLocImmediately(vv);
    }

    public void moveCameraToImmediately(double x, double y, double z, double l) {
        Vector3d vv = new Vector3d(x,y,z);
        vv.sub(cameraNowV);
        if (vv.length()!=0.0) vv.normalize();
        vv.scale(l);
        addCameraLocImmediately(vv);
    }

    /**
     * カメラの現在位置を返します。
     */
    public Vector3d getCameraLoc() {
        return new Vector3d(cameraNowV);
    }
    public Vector3d getCameraTargetLoc() {
        return new Vector3d(cameraNextV);
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(double x,double y,double z,double w) {
        cameraNextQ.set(x,y,z,w);
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(Quat4d quat) {
        cameraNextQ.set(quat);
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraQuatImmediately(double x,double y,double z,double w) {
        cameraNowQ.set(x,y,z,w);
        cameraNextQ.set(x,y,z,w);
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraQuatImmediately(Quat4d quat) {
        cameraNowQ.set(quat);
        cameraNextQ.set(quat);
    }

    public void mulCameraQuat(double x,double y,double z,double w) {
        cameraNextQ.mul(new Quat4d(x,y,z,w));
    }
    public void mulCameraQuat(Quat4d quat) {
        cameraNextQ.mul(quat);
    }
    public void mulCameraQuatImmediately(double x,double y,double z,double w) {
        cameraNextQ.mul(new Quat4d(x,y,z,w));
        cameraNowQ.set(cameraNextQ);
    }
    public void mulCameraQuatImmediately(Quat4d quat) {
        cameraNextQ.mul(quat);
        cameraNowQ.set(cameraNextQ);
    }
    /**
     * カメラの現在の回転を返します。
     */
    public Quat4d getCameraQuat() {
        return new Quat4d(cameraNowQ);
    }
    public Quat4d getCameraTargetQuat() {
        return new Quat4d(cameraNextQ);
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraRot(double x,double y,double z) {
        setCameraQuat(Util.euler2quat(x,y,z));
    }

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraRot(Vector3d rot) {
        setCameraQuat(Util.euler2quat(rot));
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraRotImmediately(double x,double y,double z) {
        setCameraQuatImmediately(Util.euler2quat(x,y,z));
    }

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraRotImmediately(Vector3d rot) {
        setCameraQuatImmediately(Util.euler2quat(rot));
    }

    public void mulCameraRot(double x,double y,double z) {
        mulCameraQuat(Util.euler2quat(x,y,z));
    }
    public void mulCameraRot(Vector3d rot) {
        mulCameraQuat(Util.euler2quat(rot));
    }
    public void mulCameraRotImmediately(double x,double y,double z) {
        mulCameraQuatImmediately(Util.euler2quat(x,y,z));
    }
    public void mulCameraRotImmediately(Vector3d rot) {
        mulCameraQuatImmediately(Util.euler2quat(rot));
    }
    public Vector3d getCameraRot() {
        return Util.quat2euler(cameraNowQ);
    }
    public Vector3d getCameraTargetRot() {
        return Util.quat2euler(cameraNextQ);
    }
    /**
     * カメラの拡大率を指定します。自動的に補完が働き滑らかにカメラの拡大率が
     * 変ります。
     */
    public void setCameraScale(double s) {
        cameraNextS = s;
    }

    /**
     * カメラの拡大率を即時に指定します。
     */
    public void setCameraScaleImmediately(double s) {
        cameraNowS = s;
        cameraNextS = s;
    }

    public void mulCameraScale(double s) {
        cameraNextS *= s;
    }
    public void mulCameraScaleImmediately(double s) {
        cameraNextS *= s;
        cameraNowS = cameraNextS;
    }
    /**
     * カメラの拡大率を返します。
     */
    public double getCameraScale() {
        return cameraNowS;
    }
    public double getCameraTargetScale() {
        return cameraNextS;
    }

    /**
     * ルックアットポイントを使って、カメラの回転を指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPoint(Vector3d lookAt) {
        Vector3d up = null;
        if (upperDirection==A3Object.UpperDirection.Y)
            up = new Vector3d(0.0,1.0,0.0);
        else
            up = new Vector3d(0.0,0.0,1.0);
        setCameraLookAtPoint(lookAt,up);
    }

    /**
     * ルックアットポイントを使って、カメラの回転を即時に指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointImmediately(Vector3d lookAt) {
        setCameraLookAtPoint(lookAt);
        cameraNowQ.set(cameraNextQ);
    }

    /**
     * ルックアットポイントを使って、カメラの回転を指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPoint(double x,double y,double z) {
        Vector3d lookAt = new Vector3d(x,y,z);
        setCameraLookAtPoint(lookAt);
    }

    /**
     * ルックアットポイントを使って、カメラの回転を指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointImmediately(double x,double y,double z) {
        Vector3d lookAt = new Vector3d(x,y,z);
        setCameraLookAtPoint(lookAt);
        cameraNowQ.set(cameraNextQ);
    }

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up) {
        Vector3d front = new Vector3d(lookAt);
        //front.sub(cameraNowV);
        front.sub(cameraNextV);//こっちの方が良くない？
        cameraNextQ.set(Util.frontFacingQuat_CAMERA(front,up));
    }

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up) {
        setCameraLookAtPoint(lookAt,up);
        cameraNowQ.set(cameraNextQ);
    }

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up) {
        Vector3d lookAt = new Vector3d(x,y,z);
        setCameraLookAtPoint(lookAt,up);
    }

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up) {
        setCameraLookAtPoint(x,y,z,up);
        cameraNowQ.set(cameraNextQ);
    }

    // マウスナビゲーションのモード設定
    NaviMode naviMode = NaviMode.NONE;
    /**
     * ナビゲーションモードを指定します。
     */
    public void setNavigationMode(NaviMode m,Object...params) {
        naviMode = m;
        if (naviMode == NaviMode.NONE)
            setA3Controller0(new NoneController());
        else if (naviMode == NaviMode.WALK)
            setA3Controller0(new WalkController());
        else if (naviMode == NaviMode.FLY)
            setA3Controller0(new FlyController());
        else if (naviMode == NaviMode.EXAMINE)
            setA3Controller0(new ExamController());
        else if (naviMode == NaviMode.EDIT)
            setA3Controller0(new EditController());
        else if (naviMode == NaviMode.SIMPLE)
            setA3Controller0(new SimpleController(params));
        else if (naviMode == NaviMode.CHASE)
            setA3Controller0(new ChaseController(params));
        else if (naviMode == NaviMode.FPS)
            setA3Controller0(new FPSController(params));
        else if (naviMode == NaviMode.FPS_FLY)
            setA3Controller0(new FPSFlyController(params));
    }

    double naviSpeed = 1.0;
    /**
     * ナビゲーションの大まかなスピードを設定します。
     * 単位はm/s。デフォルト値は1.0。
     */
    public void setNavigationSpeed(double s) {
        naviSpeed = s;
    }

    /**
     * ナビゲーションの大まかなスピードを取得します。
     * 単位はm/s。A3Controllerの作成者はこの値を参照して
     * ナビゲーションのスピードを
     * 計算することが望まれます。
     */
    public double getNavigationSpeed() {
        return naviSpeed;
    }

    A3Controller controller = null;
    void setA3Controller0(A3Controller c) {
        if ((canvas!=null)&&(c!=null))
            c.setA3CanvasInterface(canvas);
        if (universe.scene==this) {
            if (controller!=null) {
                universe.removeA3Listener(controller);
                canvas.removeKeyListener(controller);
                controller.stop();
            }
            controller = c;
            if (controller!=null) {
                universe.addA3Listener(controller);
                canvas.addKeyListener(controller);
                controller.init();
            }
        } else {
            controller = c;
        }
    }
    /**
     * A3Controllerをセットします。これをセットするとナビゲーションモードが
     * USERに自動的にセットされるので、以前設定していたモードは無効になります。
     * また，設定されているA3Controllerを解除して無効にするためには引数に
     * nullを指定して呼び出して下さい．
     */
    public void setA3Controller(A3Controller c) {
        naviMode = NaviMode.USER;
        setA3Controller0(c);
    }
//  ---------- Component2Dの処理 ----------
    public void add(Component2D c) {
        synchronized (components2D) {
            if (!components2D.contains(c))
                components2D.add(c);
        }
    }
    public void del(Component2D c) {
        synchronized (components2D) {
            components2D.remove(c);
        }
    }
    public ArrayList<Component2D> getComponents2D() {
        ArrayList<Component2D> ret = null;
        synchronized (components2D) {
            ret = new ArrayList<Component2D>(components2D);
        }
        return ret;
    }
    public A3VirtualUniverse getA3VirtualUniverse() {
        return universe;
    }
//  ---------- LockedA3の処理 ----------
    /**
     * A3Objectを追加してカメラに対して固定した位置に
     * 表示されるようにします。
     */
    public void addLockedA3(A3Object a) {
        if (lockedA3Hash.containsKey(a))
            return;
        a.setComponent2DContainerInterface(this);
        a.lockedA3=true;
        if (a instanceof Action3D) {
            ((Action3D)a).setSoundTypeLocked();
        } else if (a instanceof A3Sounds) {
            ((A3Sounds)a).setSoundTypeLocked();
        }
        a.init();
        A3BranchGroup bg = a.getA3BranchGroup();
        cameraGroup.addChild(bg);
        lockedA3Hash.put(a,bg);
    }

    /**
     * 指定されたA3Objectの登録を削除してカメラに対して固定した
     * 位置に表示されないようにします。
     */
    public void delLockedA3(A3Object a) {
        BranchGroup bg = lockedA3Hash.get(a);
        if (bg==null)
            return;
        bg.detach();
        a.dispose();
        lockedA3Hash.remove(a);
    }

    /**
     * カメラに対して固定して表示されるようい登録されている
     * 全てのA3Objectを削除して表示されないようにします。
     */
    public void delAllLockedA3() {
        Enumeration<A3Object> e = lockedA3Hash.keys();
        while (e.hasMoreElements()) {
            A3Object a3 = e.nextElement();
            BranchGroup bg = lockedA3Hash.get(a3);
            bg.detach();
            a3.dispose();
        }
        lockedA3Hash.clear();
    }
    /**
     * 上方向をY軸とするのかZ軸とするのかの変更を行う。
     * デフォルトはY軸で、この場合は特に何もかわらないが、
     * Z軸が設定された場合は表示されるA3Objectが
     * 自動的に回転されて正常な向きで表示されるようになる。
     */
    public void setUpperDirection(A3Object.UpperDirection d) {
        upperDirection = d;
    }

    //################################################################################
    //turn系メソッド．
    //################################################################################
    public void turnCameraUp(double deg) {
        mulCameraQuat(Util.euler2quat(deg/180.0*Math.PI,0,0));
    }
    public void turnCameraUpNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(deg/180.0*Math.PI,0,0));
    }
    public void turnCameraDown(double deg) {
        mulCameraQuat(Util.euler2quat(-deg/180.0*Math.PI,0,0));
    }
    public void turnCameraDownNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(-deg/180.0*Math.PI,0,0));
    }
    public void turnCameraRight(double deg) {
        mulCameraQuat(Util.euler2quat(0,-deg/180.0*Math.PI,0));
    }
    public void turnCameraRightNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(0,-deg/180.0*Math.PI,0));
    }
    public void turnCameraLeft(double deg) {
        mulCameraQuat(Util.euler2quat(0,deg/180.0*Math.PI,0));
    }
    public void turnCameraLeftNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(0,deg/180.0*Math.PI,0));
    }
    public void rollCameraRight(double deg) {
        mulCameraQuat(Util.euler2quat(0,0,-deg/180.0*Math.PI));
    }
    public void rollCameraRightNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(0,0,-deg/180.0*Math.PI));
    }
    public void rollCameraLeft(double deg) {
        mulCameraQuat(Util.euler2quat(0,0,deg/180.0*Math.PI));
    }
    public void rollCameraLeftNow(double deg) {
        mulCameraQuatImmediately(Util.euler2quat(0,0,deg/180.0*Math.PI));
    }
    public void turnCameraTo(double dirX,double dirY,double dirZ) {
        Vector3d dir = new Vector3d(dirX,dirY,dirZ);
        turnCameraTo(dir);
    }
    public void turnCameraTo(Vector3d dir) {
        Vector3d lookAtPoint = new Vector3d();
        lookAtPoint.add(cameraNowV,dir);
        setCameraLookAtPoint(lookAtPoint);
    }
    public void turnCameraToNow(double dirX,double dirY,double dirZ) {
        Vector3d dir = new Vector3d(dirX,dirY,dirZ);
        turnCameraToNow(dir);
    }
    public void turnCameraToNow(Vector3d dir) {
        Vector3d lookAtPoint = new Vector3d();
        lookAtPoint.add(cameraNowV,dir);
        setCameraLookAtPointImmediately(lookAtPoint);
    }
    
    //****************************************
    public void setCameraLookAtPoint(A3Object a) {
        this.setCameraLookAtPoint(a.getLoc());
    }
    public void setCameraLookAtPoint(A3Object a,Vector3d up) {
        this.setCameraLookAtPoint(a.getLoc(),up);
    }
    public void setCameraLookAtPointNow(A3Object a) {
        this.setCameraLookAtPointImmediately(a.getLoc());
    }
    public void setCameraLookAtPointNow(A3Object a,Vector3d up) {
        this.setCameraLookAtPointImmediately(a.getLoc(),up);
    }
    public ArrayList<A3Object> getAll() {
        ArrayList<A3Object> ret;
        synchronized(a3Hash) {
            ret = new ArrayList<A3Object>(a3Hash.keySet());
        }
        return ret;
    }
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c) {
        ArrayList<A3Object> ret;
        ArrayList<A3Object> ret2 = new ArrayList<A3Object>();
        synchronized(a3Hash) {
            ret = new ArrayList<A3Object>(a3Hash.keySet());
        }
        for (A3Object o:ret) {
            if (c.isInstance(o))
                ret2.add(o);
        }
        return ret2;
    }
}
