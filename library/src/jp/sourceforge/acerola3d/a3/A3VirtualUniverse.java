package jp.sourceforge.acerola3d.a3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyListener;

import javax.media.j3d.*;
import javax.vecmath.*;

import java.util.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

import jp.sourceforge.acerola3d.sound.*;

/**
 * A3CanvasとJA3Canvasの両方で共通して使うことが
 * できるVirtualUniverseのAcerola3Dバージョン。
 */
class A3VirtualUniverse extends VirtualUniverse implements A3CanvasInterface {
    A3CanvasInterface canvas = null;
    Canvas3D canvas3d = null;
    javax.media.j3d.Locale locale;
    javax.media.j3d.View view;
    BranchGroup rootGroup;
    TransformGroup tGroup;
    Transform3D transform;
    BranchGroup vpGroup;
    ViewPlatform vp;
    HashMap<Integer,Scene> scenes = new HashMap<Integer,Scene>();
    Scene scene;
    TimerBehavior timerBehavior;
    PickingBehavior pickingBehavior;
    CameraBehavior cameraBehavior;
    Light headLight;
    AlternateAppearance emphasizerAA;
    AlternateAppearance polygonizerAA;

    //TimerBehaviorが参照する値。つまり、カメラやA3Objectの座標などの更新インターバル。
    long elapsedTime = 33l;

    A3VirtualUniverse(A3CanvasInterface c) {
        canvas = c;
        if (canvas instanceof A3Canvas)
            init((Canvas3D)c);
        else if (canvas instanceof JA3Canvas)
            init(((JA3Canvas)canvas).getOffscreenCanvas3D());
        else if (canvas instanceof A3Widget)
            init(((A3Widget)canvas).tCanvas.getOffscreenCanvas3D());
        else if (canvas instanceof JA3Canvas2)
            init(((JA3Canvas2)canvas).getOffscreenCanvas3D());
    }
    void init(Canvas3D c3d) {
        canvas3d = c3d;
        locale = new javax.media.j3d.Locale(this);

        PhysicalBody body = new PhysicalBody();
        PhysicalEnvironment environment = new PhysicalEnvironment();

        view = new javax.media.j3d.View();
        view.addCanvas3D(canvas3d);
        view.setPhysicalBody(body);
        view.setPhysicalEnvironment(environment);
        view.setFrontClipDistance(0.1);
        view.setBackClipDistance(100.0);
        view.setUserHeadToVworldEnable(true);
        view.setProjectionPolicy(javax.media.j3d.View.PERSPECTIVE_PROJECTION );
        //view.setProjectionPolicy(javax.media.j3d.View.PARALLEL_PROJECTION);
        //view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
        view.setDepthBufferFreezeTransparent(true);

        vpGroup = new BranchGroup();
        tGroup = new TransformGroup();
        tGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        tGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        vpGroup.addChild(tGroup);
        transform = new Transform3D();
        tGroup.setTransform(transform);
        timerBehavior = new TimerBehavior(this);
        BoundingSphere bs = new BoundingSphere(new Point3d(0.0,0.0,0.0),
                                               Double.MAX_VALUE);
        timerBehavior.setSchedulingBounds(bs);
        tGroup.addChild(timerBehavior);
        vp = new ViewPlatform();
//System.out.println(vp.getActivationRadius());
        tGroup.addChild(vp);
        headLight = new DirectionalLight();
        headLight.setCapability(Light.ALLOW_STATE_WRITE);
        headLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE));
        headLight.setEnable(true);
        tGroup.addChild(headLight);

        view.attachViewPlatform(vp);

//        vpGroup.setCapability(BranchGroup.ALLOW_DETACH);
//        vpGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
//        vpGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
//        vpGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        pickingBehavior = new PickingBehavior(canvas,locale);
        vpGroup.addChild(pickingBehavior);
        BoundingSphere sb = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);
        pickingBehavior.setSchedulingBounds(sb);
        cameraBehavior = new CameraBehavior(this);
        vpGroup.addChild(cameraBehavior);
        cameraBehavior.setSchedulingBounds(sb);

        BranchGroup emphasizerAABG = new BranchGroup();
        Appearance a = new Appearance();
        Material m = new Material();
        m.setCapability(Material.ALLOW_COMPONENT_WRITE);
        a.setMaterial(m);
        emphasizerAA = new AlternateAppearance(a);
        emphasizerAA.setCapability(AlternateAppearance.ALLOW_SCOPE_WRITE);
        emphasizerAA.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE));
        emphasizerAA.addScope(new Group());//dummy
        emphasizerAABG.addChild(emphasizerAA);
        timerBehavior.setEmphasizerMaterial(m);

        BranchGroup polygonizerAABG = new BranchGroup();
        Appearance a2 = new Appearance();
        PolygonAttributes attr = new PolygonAttributes();
        attr.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        a2.setPolygonAttributes(attr);
        polygonizerAA = new AlternateAppearance(a2);
        polygonizerAA.setCapability(AlternateAppearance.ALLOW_SCOPE_WRITE);
        polygonizerAA.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE));
        polygonizerAA.addScope(new Group());//dummy
        polygonizerAABG.addChild(polygonizerAA);

        rootGroup = new BranchGroup();
        //rootGroup.setCapability(BranchGroup.ALLOW_DETACH);
        //rootGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        rootGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        rootGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        rootGroup.addChild(vpGroup);
        rootGroup.addChild(emphasizerAABG);
        rootGroup.addChild(polygonizerAABG);
        
        locale.addBranchGraph(rootGroup);

        AudioDevice mixer = null;
        String mixerClassName = 
        AccessController.doPrivilegedWithCombiner(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty("j3d.audiodevice");
            }
        });

        //System.out.println(mixerClassName);
        if (mixerClassName!=null) {
            try {
                Class<?> c = Class.forName(mixerClassName);
                Class<? extends AudioDevice> mixerClass = c.asSubclass(AudioDevice.class);
                Constructor<? extends AudioDevice> constructor = mixerClass.getConstructor(PhysicalEnvironment.class);
                mixer = constructor.newInstance(environment);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        if (mixer==null) {
            try {
                mixer = new com.sun.j3d.audioengines.javasound.JavaSoundMixer(environment);
            } catch(Exception e) {
                ;
            }
        }
        /*
        if (mixer==null) {
            try {
                mixer = new jp.sourceforge.acerola3d.sound.JOALMixer2(environment);
            } catch(Exception e) {
                ;
            }
        }
        if (mixer==null) {
            try {
                mixer = new org.jdesktop.j3d.audioengines.joal.JOALMixer(environment);
            } catch(Exception e) {
                ;
            }
        }
        if (mixer==null) {
            try {
                //mixer = new com.sun.j3d.audioengines.headspace.HeadspaceMixer(environment);
            } catch(Exception e) {
                ;
            }
        }
        */
        if (mixer == null) {
            System.out.println("null AudioDevice!");
        } else {
            System.out.println("j3d.audiodevice="+mixer.getClass().getName());
            environment.setAudioDevice(mixer);
            try {
                mixer.initialize();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        String action3dSoundType = 
        AccessController.doPrivilegedWithCombiner(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty("acerola3d.action3d.sound");
            }
        });
        if ((action3dSoundType==null)||(!action3dSoundType.equals("J3D_AUDIODEVICE"))) {
            Action3DData.soundSystem = A3SoundSystem.getDefaultSoundSystem();
            System.out.println("acerola3d.action3d.sound=JOAL_DIRECT");
        } else {
            System.out.println("acerola3d.action3d.sound=J3D_AUDIODEVICE");
        }

        scene = new Scene(this,0);
        scenes.put(0,scene);
        //rootGroup.addChild(scene.mainGroup);
        scene.activate();
    }

    TimerBehavior getTimerBehavior() {
        return timerBehavior;
    }

    void prepareVirtualUniverse() {
    }

    // A3Objectの追加と削除
    @Override
    public void add(A3Object a) {
        scene.add(a);
    }

    @Override
    public void del(A3Object a) {
        scene.del(a);
    }

    @Override
    public void delAll() {
        scene.delAll();
    }

    @Override
    public void delAll(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.delAll();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setBackground(A3Object a) {
        scene.setBackground(a);
    }

    @Override
    public void delBackground() {
        scene.delBackground();
    }

    @Override
    public void setAvatar(A3Object a) {
        scene.setAvatar(a);
    }

    @Override
    public A3Object getAvatar() {
        return scene.getAvatar();
    }

    // リスナ設定のラッパーメソッド
    @Override
    public void addA3Listener(A3Listener l) {
        pickingBehavior.addA3Listener(l);
    }

    @Override
    public void removeA3Listener(A3Listener l) {
        pickingBehavior.removeA3Listener(l);
    }

    @Override
    public void setDefaultCameraLoc(double x,double y,double z) {
        scene.setDefaultCameraLoc(x,y,z);
    }

    @Override
    public void setDefaultCameraLoc(Vector3d loc) {
        scene.setDefaultCameraLoc(loc);
    }

    @Override
    public void setDefaultCameraQuat(double x,double ay,double z,double w) {
        scene.setDefaultCameraQuat(x,ay,z,w);
    }

    @Override
    public void setDefaultCameraQuat(Quat4d quat) {
        scene.setDefaultCameraQuat(quat);
    }

    @Override
    public void setDefaultCameraRot(double x,double y,double z) {
        scene.setDefaultCameraRot(x,y,z);
    }

    @Override
    public void setDefaultCameraRot(Vector3d rot) {
        scene.setDefaultCameraRot(rot);
    }

    @Override
    public void setDefaultCameraRev(double x,double y,double z) {
        scene.setDefaultCameraRev(x,y,z);
    }

    @Override
    public void setDefaultCameraRev(Vector3d rev) {
        scene.setDefaultCameraRev(rev);
    }

    @Override
    public void setDefaultCameraScale(double s) {
        scene.setDefaultCameraScale(s);
    }

    @Override
    public void resetCamera() {
        scene.resetCamera();
    }

    @Override
    public void setCameraLoc(double x,double y,double z) {
        scene.setCameraLoc(x,y,z);
    }

    @Override
    public void setCameraLoc(Vector3d loc) {
        scene.setCameraLoc(loc);
    }

    @Override
    public void setCameraLocImmediately(double x,double y,double z) {
        scene.setCameraLocImmediately(x,y,z);
    }

    @Override
    public void setCameraLocImmediately(Vector3d loc) {
        scene.setCameraLocImmediately(loc);
    }

    @Override
    public void addCameraLoc(double x,double y,double z) {
        scene.addCameraLoc(x,y,z);
    }

    @Override
    public void addCameraLoc(Vector3d loc) {
        scene.addCameraLoc(loc);
    }

    @Override
    public void addCameraLocImmediately(double x,double y,double z) {
        scene.addCameraLocImmediately(x,y,z);
    }

    @Override
    public void addCameraLocImmediately(Vector3d loc) {
        scene.setCameraLocImmediately(loc);
    }

    @Override
    public void moveCameraForward(double l) {
        scene.moveCameraForward(l);
    }

    @Override
    public void moveCameraForwardImmediately(double l) {
        scene.moveCameraForwardImmediately(l);
    }

    @Override
    public void moveCameraBackward(double l) {
        scene.moveCameraBackward(l);
    }

    @Override
    public void moveCameraBackwardImmediately(double l) {
        scene.moveCameraBackwardImmediately(l);
    }

    @Override
    public void moveCameraRight(double l) {
        scene.moveCameraRight(l);
    }

    @Override
    public void moveCameraRightImmediately(double l) {
        scene.moveCameraRightImmediately(l);
    }

    @Override
    public void moveCameraLeft(double l) {
        scene.moveCameraLeft(l);
    }

    @Override
    public void moveCameraLeftImmediately(double l) {
        scene.moveCameraLeftImmediately(l);
    }

    @Override
    public void moveCameraUp(double l) {
        scene.moveCameraUp(l);
    }

    @Override
    public void moveCameraUpImmediately(double l) {
        scene.moveCameraUpImmediately(l);
    }

    @Override
    public void moveCameraDown(double l) {
        scene.moveCameraDown(l);
    }

    @Override
    public void moveCameraDownImmediately(double l) {
        scene.moveCameraDownImmediately(l);
    }

    @Override
    public void moveCameraTo(Vector3d v, double l) {
        scene.moveCameraTo(v,l);
    }

    @Override
    public void moveCameraTo(double x, double y, double z, double l) {
        scene.moveCameraTo(x,y,z,l);
    }

    @Override
    public void moveCameraTo(A3Object a, double l) {
        scene.moveCameraTo(a.getLoc(),l);
    }

    @Override
    public void moveCameraToImmediately(Vector3d v, double l) {
        scene.moveCameraToImmediately(v,l);
    }

    @Override
    public void moveCameraToImmediately(double x, double y, double z, double l) {
        scene.moveCameraToImmediately(x,y,z,l);
    }

    @Override
    public void moveCameraToImmediately(A3Object a, double l) {
        scene.moveCameraToImmediately(a.getLoc(),l);
    }

    @Override
    public void moveCameraForward(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraForward(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraForwardImmediately(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraForwardImmediately(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraBackward(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraBackward(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraBackwardImmediately(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraBackwardImmediately(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraRight(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraRight(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraRightImmediately(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraRightImmediately(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraLeft(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraLeft(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraLeftImmediately(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraLeftImmediately(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraUp(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraUp(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraUpImmediately(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraUpImmediately(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraDown(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraDown(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraDownImmediately(double l,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraDownImmediately(l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraTo(Vector3d v, double l, int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraTo(v,l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraTo(double x, double y, double z, double l, int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraTo(x,y,z,l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraTo(A3Object a, double l, int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraTo(a.getLoc(),l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraToImmediately(Vector3d v, double l, int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraToImmediately(v,l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraToImmediately(double x, double y, double z, double l, int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraToImmediately(x,y,z,l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void moveCameraToImmediately(A3Object a, double l, int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.moveCameraToImmediately(a.getLoc(),l);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public Vector3d getCameraLoc() {
        return scene.getCameraLoc();
    }

    @Override
    public Vector3d getCameraTargetLoc() {
        return scene.getCameraTargetLoc();
    }

    @Override
    public void setCameraQuat(double x,double y,double z,double w) {
        scene.setCameraQuat(x,y,z,w);
    }

    @Override
    public void setCameraQuat(Quat4d quat) {
        scene.setCameraQuat(quat);
    }

    @Override
    public void setCameraQuatImmediately(double x,double y,double z,double w) {
        scene.setCameraQuatImmediately(x,y,z,w);
    }

    @Override
    public void setCameraQuatImmediately(Quat4d quat) {
        scene.setCameraQuatImmediately(quat);
    }

    @Override
    public void mulCameraQuat(double x,double y,double z,double w) {
        scene.mulCameraQuat(x,y,z,w);
    }

    @Override
    public void mulCameraQuat(Quat4d quat) {
        scene.mulCameraQuat(quat);
    }

    @Override
    public void mulCameraQuatImmediately(double x,double y,double z,double w) {
        scene.mulCameraQuatImmediately(x,y,z,w);
    }

    @Override
    public void mulCameraQuatImmediately(Quat4d quat) {
        scene.mulCameraQuatImmediately(quat);
    }

    @Override
    public Quat4d getCameraQuat() {
        return scene.getCameraQuat();
    }

    @Override
    public Quat4d getCameraTargetQuat() {
        return scene.getCameraTargetQuat();
    }

    @Override
    public void setCameraRot(double x,double y,double z) {
        scene.setCameraRot(x,y,z);
    }

    @Override
    public void setCameraRot(Vector3d rot) {
        scene.setCameraRot(rot);
    }

    @Override
    public void setCameraRotImmediately(double x,double y,double z) {
        scene.setCameraRotImmediately(x,y,z);
    }

    @Override
    public void setCameraRotImmediately(Vector3d rot) {
        scene.setCameraRotImmediately(rot);
    }

    @Override
    public void mulCameraRot(double x,double y,double z) {
        scene.mulCameraRot(x,y,z);
    }

    @Override
    public void mulCameraRot(Vector3d rot) {
        scene.mulCameraRot(rot);
    }

    @Override
    public void mulCameraRotImmediately(double x,double y,double z) {
        scene.mulCameraRotImmediately(x,y,z);
    }

    @Override
    public void mulCameraRotImmediately(Vector3d rot) {
        scene.mulCameraRotImmediately(rot);
    }

    @Override
    public Vector3d getCameraRot() {
        return scene.getCameraRot();
    }

    @Override
    public Vector3d getCameraTargetRot() {
        return scene.getCameraTargetRot();
    }

    @Override
    public void setCameraRev(double x,double y,double z) {
        scene.setCameraRot(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z);
    }

    @Override
    public void setCameraRev(Vector3d rev) {
        scene.setCameraRot(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z);
    }

    @Override
    public void setCameraRevImmediately(double x,double y,double z) {
        scene.setCameraRotImmediately(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z);
    }

    @Override
    public void setCameraRevImmediately(Vector3d rev) {
        scene.setCameraRotImmediately(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z);
    }

    @Override
    public void mulCameraRev(double x,double y,double z) {
        scene.mulCameraRot(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z);
    }

    @Override
    public void mulCameraRev(Vector3d rev) {
        scene.mulCameraRot(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z);
    }

    @Override
    public void mulCameraRevImmediately(double x,double y,double z) {
        scene.mulCameraRotImmediately(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z);
    }

    @Override
    public void mulCameraRevImmediately(Vector3d rev) {
        scene.mulCameraRotImmediately(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z);
    }

    @Override
    public Vector3d getCameraRev() {
        Vector3d ret = scene.getCameraRot();
        ret.scale(180.0/Math.PI);
        return ret;
    }

    @Override
    public Vector3d getCameraTargetRev() {
        Vector3d ret = scene.getCameraTargetRot();
        ret.scale(180.0/Math.PI);
        return ret;
    }

    @Override
    public void setCameraScale(double s) {
        scene.setCameraScale(s);
    }

    @Override
    public void setCameraScaleImmediately(double s) {
        scene.setCameraScaleImmediately(s);
    }

    @Override
    public void mulCameraScale(double s) {
        scene.mulCameraScale(s);
    }

    @Override
    public void mulCameraScaleImmediately(double s) {
        scene.mulCameraScaleImmediately(s);
    }

    @Override
    public double getCameraScale() {
        return scene.getCameraScale();
    }

    @Override
    public double getCameraTargetScale() {
        return scene.getCameraTargetScale();
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt) {
        scene.setCameraLookAtPoint(lookAt);
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt) {
        scene.setCameraLookAtPointImmediately(lookAt);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z) {
        scene.setCameraLookAtPoint(x,y,z);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z) {
        scene.setCameraLookAtPointImmediately(x,y,z);
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up) {
        scene.setCameraLookAtPoint(lookAt,up);
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up) {
        scene.setCameraLookAtPoint(x,y,z,up);
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up) {
        scene.setCameraLookAtPointImmediately(x,y,z,up);
    }

    @Override
    public void setHeadLightEnable(boolean b) {
        headLight.setEnable(b);
    }

    @Override
    public void setNavigationMode(NaviMode m,Object...params) {
        scene.setNavigationMode(m,params);
    }

    @Override
    public void setNavigationSpeed(double s) {
        scene.setNavigationSpeed(s);
    }

    @Override
    public double getNavigationSpeed() {
        return scene.getNavigationSpeed();
    }

    @Override
    public void setA3Controller(A3Controller c) {
        scene.setA3Controller(c);
    }
//  ----------座標変換とピッキングのためのラッパーメソッド---------
    @Override
    public Point3d canvasToVirtualCS(int x,int y) {
        return pickingBehavior.canvasToVirtualCS(x,y);
    }

    @Override
    public Point3d canvasToVirtualCS(int x,int y,double dis) {
        return pickingBehavior.canvasToVirtualCS(x,y,dis);
    }

    @Override
    public Point3d canvasToPhysicalCS(int x,int y) {
        return pickingBehavior.canvasToPhysicalCS(x,y);
    }

    @Override
    public Point3d canvasToPhysicalCS(int x,int y,double dis) {
        return pickingBehavior.canvasToPhysicalCS(x,y,dis);
    }

    @Override
    public Vector3d physicalCSToVirtualCS(Vector3d v) {
        Point3d p = pickingBehavior.physicalCSToVirtualCS(new Point3d(v));
        return new Vector3d(p);
    }

    @Override
    public Point physicalCSToCanvas(Point3d p) {
        return pickingBehavior.physicalCSToCanvas(p);
    }

    @Override
    public Point virtualCSToCanvas(Point3d p) {
        return pickingBehavior.virtualCSToCanvas(p);
    }

    @Override
    public Vector3d virtualCSToPhysicalCS(Vector3d v) {
        Point3d p = pickingBehavior.virtualCSToPhysicalCS(new Point3d(v));
        return new Vector3d(p);
    }

    @Override
    public Vector3d getCameraUnitVecX() {
        Vector3d v = physicalCSToVirtualCS(new Vector3d(1.0,0.0,0.0));
        v.sub(scene.cameraNowV);
        return v;
    }

    @Override
    public Vector3d getCameraUnitVecY() {
        Vector3d v = physicalCSToVirtualCS(new Vector3d(0.0,1.0,0.0));
        v.sub(scene.cameraNowV);
        return v;
    }

    @Override
    public Vector3d getCameraUnitVecZ() {
        Vector3d v = physicalCSToVirtualCS(new Vector3d(0.0,0.0,1.0));
        v.sub(scene.cameraNowV);
        return v;
    }

    @Override
    public A3Object pickA3(int x,int y) {
        return pickingBehavior.pickA3(x,y);
    }

    @Override
    public A3Object pick(Vector3d origin,Vector3d dir) {
        return pickingBehavior.pickA3(origin,dir);
    }
//  ----------シーン関係のメソッド---------
    @Override
    public void prepareScene(int scene) {
        Scene s = scenes.get(scene);
        if (s==null) {
            s = new Scene(this,scene);
            scenes.put(scene,s);
        }
    }

    @Override
    public void changeActiveScene(int s) {
        Scene newScene = scenes.get(s);
        if (newScene==null) {
            throw new IllegalArgumentException();
        }
        scene.deactivate();
        scene = newScene;
        //カメラをむりやり変える。本当はCameraBehaviorでやるべき。
        transform.set(scene.cameraNowQ,scene.cameraNowV,scene.cameraNowS);
        tGroup.setTransform(transform);
        scene.activate();
    }

    @Override
    public void add(A3Object a,int s) {
        Scene tmpScene = scenes.get(s);
        if (tmpScene!=null) {
            tmpScene.add(a);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void del(A3Object a,int s) {
        Scene tmpScene = scenes.get(s);
        if (tmpScene!=null)
            tmpScene.del(a);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setDefaultCameraLoc(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setDefaultCameraLoc(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setDefaultCameraLoc(Vector3d loc,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setDefaultCameraLoc(loc);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setDefaultCameraQuat(double x,double y,double z,double w,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setDefaultCameraQuat(x,y,z,w);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setDefaultCameraQuat(Quat4d quat,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setDefaultCameraQuat(quat);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setDefaultCameraRot(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setDefaultCameraRot(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setDefaultCameraRot(Vector3d rot,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setDefaultCameraRot(rot);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setDefaultCameraRev(double x,double y,double z,int scene) {
        setDefaultCameraRot(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z,scene);
    }

    @Override
    public void setDefaultCameraRev(Vector3d rev,int scene) {
        setDefaultCameraRot(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z,scene);
    }

    @Override
    public void setDefaultCameraScale(double s,int scene) {
        Scene sTmp = scenes.get(scene);
        if (sTmp!=null)
            sTmp.setDefaultCameraScale(s);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void resetCamera(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.resetCamera();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLoc(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLoc(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLoc(Vector3d loc,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLoc(loc);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLocImmediately(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLocImmediately(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLocImmediately(Vector3d loc,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLocImmediately(loc);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void addCameraLoc(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.addCameraLoc(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void addCameraLoc(Vector3d loc,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.addCameraLoc(loc);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void addCameraLocImmediately(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.addCameraLocImmediately(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void addCameraLocImmediately(Vector3d loc,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.addCameraLocImmediately(loc);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public Vector3d getCameraLoc(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getCameraLoc();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public Vector3d getCameraTargetLoc(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getCameraTargetLoc();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraQuat(double x,double y,double z,double w,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraQuat(x,y,z,w);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraQuat(Quat4d quat,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraQuat(quat);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraQuatImmediately(double x,double y,double z,double w,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraQuatImmediately(x,y,z,w);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraQuatImmediately(Quat4d quat,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraQuatImmediately(quat);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraQuat(double x,double y,double z,double w,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.mulCameraQuat(x,y,z,w);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraQuat(Quat4d quat,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.mulCameraQuat(quat);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraQuatImmediately(double x,double y,double z,double w,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.mulCameraQuatImmediately(x,y,z,w);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraQuatImmediately(Quat4d quat,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.mulCameraQuatImmediately(quat);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public Quat4d getCameraQuat(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getCameraQuat();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public Quat4d getCameraTargetQuat(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getCameraTargetQuat();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraRot(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraRot(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraRot(Vector3d rot,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraRot(rot);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraRotImmediately(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraRotImmediately(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraRotImmediately(Vector3d rot,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraRotImmediately(rot);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraRot(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.mulCameraRot(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraRot(Vector3d rot,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.mulCameraRot(rot);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraRotImmediately(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.mulCameraRotImmediately(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraRotImmediately(Vector3d rot,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.mulCameraRotImmediately(rot);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public Vector3d getCameraRot(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getCameraRot();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public Vector3d getCameraTargetRot(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getCameraTargetRot();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraRev(double x,double y,double z,int scene) {
        setCameraRot(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z,scene);
    }

    @Override
    public void setCameraRev(Vector3d rev,int scene) {
        setCameraRot(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z);
    }

    @Override
    public void setCameraRevImmediately(double x,double y,double z,int scene) {
        setCameraRotImmediately(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z,scene);
    }

    @Override
    public void setCameraRevImmediately(Vector3d rev,int scene) {
        setCameraRotImmediately(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z,scene);
    }

    @Override
    public void mulCameraRev(double x,double y,double z,int scene) {
        mulCameraRot(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z,scene);
    }

    @Override
    public void mulCameraRev(Vector3d rev,int scene) {
        mulCameraRot(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z,scene);
    }

    @Override
    public void mulCameraRevImmediately(double x,double y,double z,int scene) {
        mulCameraRotImmediately(Math.PI/180.0*x,Math.PI/180.0*y,Math.PI/180.0*z,scene);
    }

    @Override
    public void mulCameraRevImmediately(Vector3d rev,int scene) {
        mulCameraRotImmediately(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z,scene);
    }

    @Override
    public Vector3d getCameraRev(int scene) {
        Vector3d ret = getCameraRot(scene);
        ret.scale(180.0/Math.PI);
        return ret;
    }

    @Override
    public Vector3d getCameraTargetRev(int scene) {
        Vector3d ret = getCameraTargetRot(scene);
        ret.scale(180.0/Math.PI);
        return ret;
    }

    @Override
    public void setCameraScale(double s,int scene) {
        Scene sTmp = scenes.get(scene);
        if (sTmp!=null)
            sTmp.setCameraScale(s);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraScaleImmediately(double s,int scene) {
        Scene sTmp = scenes.get(scene);
        if (sTmp!=null)
            sTmp.setCameraScaleImmediately(s);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraScale(double s,int scene) {
        Scene sTmp = scenes.get(scene);
        if (sTmp!=null)
            sTmp.mulCameraScale(s);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void mulCameraScaleImmediately(double s,int scene) {
        Scene sTmp = scenes.get(scene);
        if (sTmp!=null)
            sTmp.mulCameraScaleImmediately(s);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public double getCameraScale(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getCameraScale();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public double getCameraTargetScale(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getCameraTargetScale();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPoint(lookAt);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPointImmediately(lookAt);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPoint(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPointImmediately(x,y,z);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPoint(lookAt,up);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPointImmediately(lookAt,up);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPoint(x,y,z,up);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPointImmediately(x,y,z,up);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setNavigationMode(int scene,NaviMode m,Object...params) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setNavigationMode(m,params);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setNavigationSpeed(double s,int scene) {
        Scene sTmp = scenes.get(scene);
        if (sTmp!=null)
            sTmp.setNavigationSpeed(s);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public double getNavigationSpeed(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getNavigationSpeed();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setA3Controller(A3Controller c,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setA3Controller(c);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setBackground(A3Object a,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setBackground(a);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void delBackground(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.delBackground();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setAvatar(A3Object a,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setAvatar(a);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public A3Object getAvatar(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getAvatar();
        else
            throw new IllegalArgumentException();
    }
//  ---------- Component2Dの処理 ----------
    @Override
    public void add(Component2D c) {
        scene.add(c);
    }

    @Override
    public void del(Component2D c) {
        scene.del(c);
    }

    @Override
    public void add(Component2D c,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.add(c);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void del(Component2D c,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.del(c);
        else
            throw new IllegalArgumentException();
    }

    ArrayList<Component2D> getComponents2D() {
        return scene.getComponents2D();
    }
//  ---------- LockedA3の処理 ----------
    @Override
    public void addLockedA3(A3Object a) {
        scene.addLockedA3(a);
    }

    @Override
    public void delLockedA3(A3Object a) {
        scene.delLockedA3(a);
    }

    @Override
    public void delAllLockedA3() {
        scene.delAllLockedA3();
    }

    @Override
    public void addLockedA3(A3Object a,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.addLockedA3(a);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void delLockedA3(A3Object a,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.delLockedA3(a);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void delAllLockedA3(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.delAllLockedA3();
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void setUpperDirection(A3Object.UpperDirection d) {
        scene.setUpperDirection(d);
    }

    @Override
    public void setUpperDirection(A3Object.UpperDirection d,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setUpperDirection(d);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public A3Object.UpperDirection getUpperDirection() {
        return scene.upperDirection;
    }

    @Override
    public A3Object.UpperDirection getUpperDirection(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.upperDirection;
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void addKeyListener(KeyListener l) {
        //dummy
    }

    @Override
    public void removeKeyListener(KeyListener l) {
        //dummy
    }

    @Override
    public int getFPS() {
        //dummy
        return 0;
    }

    @Override
    public void setUpdateInterval(long l) {
        elapsedTime = l;
    }

    @Override
    public long getUpdateInterval() {
        return elapsedTime;
    }

    @Override
    public void waitForUpdate(long timeout) {
        timerBehavior.waitForUpdate(timeout);
    }

    /**
     * このメソッドはダミーです。
     */
    public void insertTaskIntoRenderingLoop(Runnable task) {
    }

    /**
     * このメソッドはダミーです。
     */
    @Override
    public void removeTaskFromRenderingLoop(Runnable task) {
    }

    @Override
    public void insertTaskIntoTimerLoop(Runnable task) {
        timerBehavior.insertTaskIntoTimerLoop(task);
    }

    @Override
    public void removeTaskFromTimerLoop(Runnable task) {
        timerBehavior.removeTaskFromTimerLoop(task);
    }

    @Override
    public void setCameraInterpolateRatio(double ir) {
        cameraBehavior.setInterpolateRatio(ir);
    }

    @Override
    public void setDepthBufferFreezeTransparent(boolean b) {
        view.setDepthBufferFreezeTransparent(b);
    }

    @Override
    public void saveImage(File file) throws IOException {
        //dummy
    }

    @Override
    public BufferedImage snapshot() {
        //dummy
        return null;
    }

    @Override
    public void setCameraLookAtPointImmediately(Vector3d lookAt, Vector3d up) {
        //dummy
    }

    @Override
    public Dimension getCanvasSize() {
        return null;
    }

    @Override
    public void cleanUp() {
        ;
    }

    @Override
    public void addA3SubCanvas(A3CanvasInterface sc) {
        PickingBehavior pb = new PickingBehavior(sc,locale);
        BoundingSphere sb = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);
        pb.setSchedulingBounds(sb);
        sc.setPickingBehavior(pb);
        sc.setVirtualUniverse(this);
        BranchGroup bg = sc.getBranchGroupForViewPlatform();
        locale.addBranchGraph(bg);
    }

    @Override
    public void setProjectionMode(ProjectionMode m) {
        if (m==ProjectionMode.PERSPECTIVE) {
            view.setProjectionPolicy(javax.media.j3d.View.PERSPECTIVE_PROJECTION );
            view.setScreenScalePolicy(View.SCALE_SCREEN_SIZE);
        } else if (m==ProjectionMode.PARALLEL) {
            view.setProjectionPolicy(javax.media.j3d.View.PARALLEL_PROJECTION);
            view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        }
    }

    @Override
    public void setCanvasWidthInPWorld(double s) {
        double psw = canvas3d.getScreen3D().getPhysicalScreenWidth();
        view.setScreenScale(psw/s);
    }

    @Override
    public void setFieldOfView(double f) {
        view.setFieldOfView(f);
    }

    @Override
    public TransformGroup getTransformGroupForViewPlatform() {
        return tGroup;
    }

    @Override
    public Canvas3D getCanvas3D() {
        return canvas3d;
    }

    @Override
    public void setSoundGain(double g) {
        Action3DData.soundSystem.setListenerGain(g);
    }

    @Override
    public double getSoundGain() {
        return Action3DData.soundSystem.getListenerGain();
    }

    @Override
    public BranchGroup getBranchGroupForViewPlatform() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPickingBehavior(PickingBehavior pb) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setVirtualUniverse(A3VirtualUniverse vu) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public A3VirtualUniverse getVirtualUniverse() {
        return this;
    }

    /**
     * 引数のA3Objectを強調表示にします。
     */
    void emphasize(A3Object a3) {
        emphasize((Group)a3.getNode());
    }

    /**
     * 引数のA3Objectの強調表示をOFFにします。
     */
    void unemphasize(A3Object a3) {
        unemphasize((Group)a3.getNode());
    }

    /**
     * 引数のjavax.media.j3d.Groupを強調表示にします。
     */
    void emphasize(Group g) {
        emphasize_OLD(g);
    }

    void emphasize_NEW(final Group g) {
        Runnable r = new Runnable() {
            public void run() {
                if (emphasizerAA.indexOfScope(g)!=-1)
                    return;
                emphasizerAA.addScope(g);
            }
        };
        timerBehavior.addRunnable(r);;
    }

    void emphasize_OLD(Group g) {
        if (emphasizerAA.indexOfScope(g)!=-1)
            return;
        emphasizerAA.addScope(g);
    }

    /**
     * 引数のjavax.media.j3d.Groupの強調表示をOFFにします。
     */
    void unemphasize(Group g) {
        unemphasize_OLD(g);
    }

    void unemphasize_NEW(final Group g) {
        Runnable r = new Runnable() {
            public void run() {
                if (emphasizerAA.indexOfScope(g)==-1)
                    return;
                emphasizerAA.removeScope(g);
            }
        };
        timerBehavior.addRunnable(r);
    }

    void unemphasize_OLD(Group g) {
        if (emphasizerAA.indexOfScope(g)==-1)
            return;
        emphasizerAA.removeScope(g);
    }

    /**
     * 引数のA3Objectをポリゴン表示にします。
     */
    void polygonize(A3Object a3) {
        polygonize((Group)a3.getNode());
    }

    /**
     * 引数のA3Objectをポリゴン表示をOFFにします。
     */
    void unpolygonize(A3Object a3) {
        unpolygonize((Group)a3.getNode());
    }

    /**
     * 引数のjavax.media.j3d.Groupをポリゴン表示にします。
     */
    void polygonize(Group g) {
        polygonize_OLD(g);
    }

    void polygonize_NEW(final Group g) {
        Runnable r = new Runnable() {
            public void run() {
                if (polygonizerAA.indexOfScope(g)!=-1)
                    return;
                polygonizerAA.addScope(g);
            }
        };
        timerBehavior.addRunnable(r);
    }

    void polygonize_OLD(Group g) {
        if (polygonizerAA.indexOfScope(g)!=-1)
            return;
        polygonizerAA.addScope(g);
    }

    /**
     * 引数のjavax.media.j3d.Groupをポリゴン表示をOFFにします。
     */
    void unpolygonize(Group g) {
        unpolygonize_OLD(g);
    }

    void unpolygonize_NEW(final Group g) {
        Runnable r = new Runnable() {
            public void run() {
                if (polygonizerAA.indexOfScope(g)==-1)
                    return;
                polygonizerAA.removeScope(g);
            }
        };
        timerBehavior.addRunnable(r);
    }

    void unpolygonize_OLD(Group g) {
        if (polygonizerAA.indexOfScope(g)==-1)
            return;
        polygonizerAA.removeScope(g);
    }

    //################################################################################
    //Now系メソッド．Immediatelyを呼ぶだけでOK．
    //################################################################################
    @Override
    public void setCameraLocNow(double x,double y,double z) {
        setCameraLocImmediately(x,y,z);
    }
    @Override
    public void setCameraLocNow(Vector3d loc) {
        setCameraLocImmediately(loc);
    }
    @Override
    public void addCameraLocNow(double x,double y,double z) {
        addCameraLocImmediately(x,y,z);
    }
    @Override
    public void addCameraLocNow(Vector3d loc) {
        addCameraLocImmediately(loc);
    }
    @Override
    public void moveCameraForwardNow(double l) {
        moveCameraForwardImmediately(l);
    }
    @Override
    public void moveCameraBackwardNow(double l) {
        moveCameraBackwardImmediately(l);
    }
    @Override
    public void moveCameraRightNow(double l) {
        moveCameraRightImmediately(l);
    }
    @Override
    public void moveCameraLeftNow(double l) {
        moveCameraLeftImmediately(l);
    }
    @Override
    public void moveCameraUpNow(double l) {
        moveCameraUpImmediately(l);
    }
    @Override
    public void moveCameraDownNow(double l) {
        moveCameraDownImmediately(l);
    }
    @Override
    public void moveCameraToNow(Vector3d v, double l) {
        moveCameraToImmediately(v, l);
    }
    @Override
    public void moveCameraToNow(double x, double y, double z, double l) {
        moveCameraToImmediately(x, y, z, l);
    }
    @Override
    public void moveCameraToNow(A3Object a, double l) {
        moveCameraToImmediately(a, l);
    }
    @Override
    public void moveCameraForwardNow(double l,int scene) {
        moveCameraForwardImmediately(l,scene);
    }
    @Override
    public void moveCameraBackwardNow(double l,int scene) {
        moveCameraBackwardImmediately(l,scene);
    }
    @Override
    public void moveCameraRightNow(double l,int scene) {
        moveCameraRightImmediately(l,scene);
    }
    @Override
    public void moveCameraLeftNow(double l,int scene) {
        moveCameraLeftImmediately(l,scene);
    }
    @Override
    public void moveCameraUpNow(double l,int scene) {
        moveCameraUpImmediately(l,scene);
    }
    @Override
    public void moveCameraDownNow(double l,int scene) {
        moveCameraDownImmediately(l,scene);
    }
    @Override
    public void moveCameraToNow(Vector3d v, double l,int scene) {
        moveCameraToImmediately(v, l,scene);
    }
    @Override
    public void moveCameraToNow(double x, double y, double z, double l,int scene) {
        moveCameraToImmediately(x, y, z, l,scene);
    }
    @Override
    public void moveCameraToNow(A3Object a, double l,int scene) {
        moveCameraToImmediately(a, l,scene);
    }
    @Override
    public void setCameraQuatNow(double x,double y,double z,double w) {
        setCameraQuatImmediately(x,y,z,w);
    }
    @Override
    public void setCameraQuatNow(Quat4d quat) {
        setCameraQuatImmediately(quat);
    }
    @Override
    public void mulCameraQuatNow(double x,double y,double z,double w) {
        mulCameraQuatImmediately(x,y,z,w);
    }
    @Override
    public void mulCameraQuatNow(Quat4d quat) {
        mulCameraQuatImmediately(quat);
    }
    @Override
    public void setCameraRotNow(double x,double y,double z) {
        setCameraRotImmediately(x,y,z);
    }
    @Override
    public void setCameraRotNow(Vector3d rot) {
        setCameraRotImmediately(rot);
    }
    @Override
    public void mulCameraRotNow(double x,double y,double z) {
        mulCameraRotImmediately(x,y,z);
    }
    @Override
    public void mulCameraRotNow(Vector3d rot) {
        mulCameraRotImmediately(rot);
    }
    @Override
    public void setCameraRevNow(double x,double y,double z) {
        setCameraRevImmediately(x,y,z);
    }
    @Override
    public void setCameraRevNow(Vector3d rev) {
        setCameraRevImmediately(rev);
    }
    @Override
    public void mulCameraRevNow(double x,double y,double z) {
        mulCameraRevImmediately(x,y,z);
    }
    @Override
    public void mulCameraRevNow(Vector3d rev) {
        mulCameraRevImmediately(rev);
    }
    @Override
    public void setCameraScaleNow(double s) {
        setCameraScaleImmediately(s);
    }
    @Override
    public void mulCameraScaleNow(double s) {
        mulCameraScaleImmediately(s);
    }
    @Override
    public void setCameraLookAtPointNow(Vector3d lookAt) {
        setCameraLookAtPointImmediately(lookAt);
    }
    @Override
    public void setCameraLookAtPointNow(double x,double y,double z) {
        setCameraLookAtPointImmediately(x,y,z);
    }
    @Override
    public void setCameraLookAtPointNow(Vector3d lookAt,Vector3d up) {
        setCameraLookAtPointImmediately(lookAt,up);
    }
    @Override
    public void setCameraLookAtPointNow(double x,double y,double z,Vector3d up) {
        setCameraLookAtPointImmediately(x,y,z,up);
    }
    @Override
    public void setCameraLocNow(double x,double y,double z,int scene) {
        setCameraLocImmediately(x,y,z,scene);
    }
    @Override
    public void setCameraLocNow(Vector3d loc,int scene) {
        setCameraLocImmediately(loc,scene);
    }
    @Override
    public void addCameraLocNow(double x,double y,double z,int scene) {
        addCameraLocImmediately(x,y,z,scene);
    }
    @Override
    public void addCameraLocNow(Vector3d loc,int scene) {
        addCameraLocImmediately(loc,scene);
    }
    @Override
    public void setCameraQuatNow(double x,double y,double z,double w,int scene) {
        setCameraQuatImmediately(x,y,z,w,scene);
    }
    @Override
    public void setCameraQuatNow(Quat4d quat,int scene) {
        setCameraQuatImmediately(quat,scene);
    }
    @Override
    public void mulCameraQuatNow(double x,double y,double z,double w,int scene) {
        mulCameraQuatImmediately(x,y,z,w,scene);
    }
    @Override
    public void mulCameraQuatNow(Quat4d quat,int scene) {
        mulCameraQuatImmediately(quat,scene);
    }
    @Override
    public void setCameraRotNow(double x,double y,double z,int scene) {
        setCameraRotImmediately(x,y,z,scene);
    }
    @Override
    public void setCameraRotNow(Vector3d rot,int scene) {
        setCameraRotImmediately(rot,scene);
    }
    @Override
    public void mulCameraRotNow(double x,double y,double z,int scene) {
        mulCameraRotImmediately(x,y,z,scene);
    }
    @Override
    public void mulCameraRotNow(Vector3d rot,int scene) {
        mulCameraRotImmediately(rot,scene);
    }
    @Override
    public void setCameraRevNow(double x,double y,double z,int scene) {
        setCameraRevImmediately(x,y,z,scene);
    }
    @Override
    public void setCameraRevNow(Vector3d rev,int scene) {
        setCameraRevImmediately(rev,scene);
    }
    @Override
    public void mulCameraRevNow(double x,double y,double z,int scene) {
        mulCameraRevImmediately(x,y,z,scene);
    }
    @Override
    public void mulCameraRevNow(Vector3d rev,int scene) {
        mulCameraRevImmediately(rev,scene);
    }
    @Override
    public void setCameraScaleNow(double s,int scene) {
        setCameraScaleImmediately(s,scene);
    }
    @Override
    public void mulCameraScaleNow(double s,int scene) {
        mulCameraScaleImmediately(s,scene);
    }
    @Override
    public void setCameraLookAtPointNow(Vector3d lookAt,int scene) {
        setCameraLookAtPointImmediately(lookAt,scene);
    }
    @Override
    public void setCameraLookAtPointNow(double x,double y,double z,int scene) {
        setCameraLookAtPointImmediately(x,y,z,scene);
    }
    @Override
    public void setCameraLookAtPointNow(Vector3d lookAt,Vector3d up,int scene) {
        setCameraLookAtPointImmediately(lookAt,up,scene);
    }
    @Override
    public void setCameraLookAtPointNow(double x,double y,double z,Vector3d up,int scene) {
        setCameraLookAtPointImmediately(x,y,z,up,scene);
    }

    //################################################################################
    //turn系メソッド．
    //################################################################################
    @Override
    public void turnCameraUp(double deg) {
        scene.turnCameraUp(deg);
    }
    @Override
    public void turnCameraUpNow(double deg) {
        scene.turnCameraUpNow(deg);
    }
    @Override
    public void turnCameraUp(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraUp(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraUpNow(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraUpNow(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraDown(double deg) {
        scene.turnCameraDown(deg);
    }
    @Override
    public void turnCameraDownNow(double deg) {
        scene.turnCameraDownNow(deg);
    }
    @Override
    public void turnCameraDown(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraDown(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraDownNow(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraDownNow(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraRight(double deg) {
        scene.turnCameraRight(deg);
    }
    @Override
    public void turnCameraRightNow(double deg) {
        scene.turnCameraRightNow(deg);
    }
    @Override
    public void turnCameraRight(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraRight(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraRightNow(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraRightNow(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraLeft(double deg) {
        scene.turnCameraLeft(deg);
    }
    @Override
    public void turnCameraLeftNow(double deg) {
        scene.turnCameraLeftNow(deg);
    }
    @Override
    public void turnCameraLeft(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraLeft(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraLeftNow(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraLeftNow(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void rollCameraRight(double deg) {
        scene.rollCameraRight(deg);
    }
    @Override
    public void rollCameraRightNow(double deg) {
        scene.rollCameraRightNow(deg);
    }
    @Override
    public void rollCameraRight(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.rollCameraRight(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void rollCameraRightNow(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.rollCameraRightNow(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void rollCameraLeft(double deg) {
        scene.rollCameraLeft(deg);
    }
    @Override
    public void rollCameraLeftNow(double deg) {
        scene.rollCameraLeftNow(deg);
    }
    @Override
    public void rollCameraLeft(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.rollCameraLeft(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void rollCameraLeftNow(double deg,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.rollCameraLeftNow(deg);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraTo(double dirX,double dirY,double dirZ) {
        scene.turnCameraTo(dirX,dirY,dirZ);
    }
    @Override
    public void turnCameraTo(Vector3d dir) {
        scene.turnCameraTo(dir);
    }
    @Override
    public void turnCameraToNow(double dirX,double dirY,double dirZ) {
        scene.turnCameraToNow(dirX,dirY,dirZ);
    }
    @Override
    public void turnCameraToNow(Vector3d dir) {
        scene.turnCameraToNow(dir);
    }





    @Override
        public void turnCameraTo(double dirX,double dirY,double dirZ,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraTo(dirX,dirY,dirZ);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraTo(Vector3d dir,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraTo(dir);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraToNow(double dirX,double dirY,double dirZ,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraToNow(dirX,dirY,dirZ);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void turnCameraToNow(Vector3d dir,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.turnCameraToNow(dir);
        else
            throw new IllegalArgumentException();
    }
    //****************************************
    @Override
    public void setCameraLookAtPoint(A3Object a) {
        scene.setCameraLookAtPoint(a);
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,Vector3d up) {
        scene.setCameraLookAtPoint(a,up);
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPoint(a);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void setCameraLookAtPoint(A3Object a,Vector3d up,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPoint(a,up);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a) {
        scene.setCameraLookAtPointNow(a);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,Vector3d up) {
        scene.setCameraLookAtPointNow(a,up);
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPointNow(a);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public void setCameraLookAtPointNow(A3Object a,Vector3d up,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            s.setCameraLookAtPointNow(a,up);
        else
            throw new IllegalArgumentException();
    }
    @Override
    public ArrayList<A3Object> getAll() {
        return scene.getAll();
    }
    @Override
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c) {
        return scene.getAll(c);
    }
    @Override
    public ArrayList<A3Object> getAll(int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getAll();
        else
            throw new IllegalArgumentException();
    }
    @Override
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c,int scene) {
        Scene s = scenes.get(scene);
        if (s!=null)
            return s.getAll(c);
        else
            throw new IllegalArgumentException();
    }

    //2015,12/06: 指定したA3Objectの情報をもとにカメラを
    //コントロールするメソッド達
    @Override public void setCameraLoc(A3Object a)
    {setCameraLoc(a.getLoc());}
    @Override public void setCameraQuat(A3Object a)
    {setCameraQuat(a.getQuat());}
    @Override public void setCameraRev(A3Object a)
    {setCameraQuat(a.getQuat());}
    @Override public void setCameraScale(A3Object a)
    {setCameraScale(a.getScale());}
    @Override public void setCameraLocRevScale(A3Object a)
    {setCameraLoc(a.getLoc());
     setCameraQuat(a.getQuat());
     setCameraScale(a.getScale());}
    @Override public void setCameraLocNow(A3Object a)
    {setCameraLocNow(a.getLoc());}
    @Override public void setCameraQuatNow(A3Object a)
    {setCameraQuatNow(a.getQuat());}
    @Override public void setCameraRevNow(A3Object a)
    {setCameraQuatNow(a.getQuat());}
    @Override public void setCameraScaleNow(A3Object a)
    {setCameraScaleNow(a.getScale());}
    @Override public void setCameraLocRevScaleNow(A3Object a)
    {setCameraLocNow(a.getLoc());
     setCameraQuatNow(a.getQuat());
     setCameraScaleNow(a.getScale());}
    @Override public void setCameraLoc(A3Object a,int scene)
    {setCameraLoc(a.getLoc(),scene);}
    @Override public void setCameraQuat(A3Object a,int scene)
    {setCameraQuat(a.getQuat(),scene);}
    @Override public void setCameraRev(A3Object a,int scene)
    {setCameraQuat(a.getQuat(),scene);}
    @Override public void setCameraScale(A3Object a,int scene)
    {setCameraScale(a.getScale(),scene);}
    @Override public void setCameraLocRevScale(A3Object a,int scene)
    {setCameraLoc(a.getLoc(),scene);
     setCameraQuat(a.getQuat(),scene);
     setCameraScale(a.getScale(),scene);}
    @Override public void setCameraLocNow(A3Object a,int scene)
    {setCameraLocNow(a.getLoc(),scene);}
    @Override public void setCameraQuatNow(A3Object a,int scene)
    {setCameraQuatNow(a.getQuat(),scene);}
    @Override public void setCameraRevNow(A3Object a,int scene)
    {setCameraQuatNow(a.getQuat(),scene);}
    @Override public void setCameraScaleNow(A3Object a,int scene)
    {setCameraScaleNow(a.getScale(),scene);}
    @Override public void setCameraLocRevScaleNow(A3Object a,int scene)
    {setCameraLocNow(a.getLoc(),scene);
     setCameraQuatNow(a.getQuat(),scene);
     setCameraScaleNow(a.getScale(),scene);}

}
