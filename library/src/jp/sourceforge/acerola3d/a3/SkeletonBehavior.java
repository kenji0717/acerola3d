package jp.sourceforge.acerola3d.a3;

import java.util.*;
import java.net.*;
import jp.sourceforge.acerola3d.a3.bvh.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import static java.lang.Math.*;

/*
 * SkeletonのためのBehavior。
 */
class SkeletonBehavior extends Behavior {
    static BoundingSphere bounding = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);

    Skeleton skeleton;
    BVH bvh;
    BranchGroup allBranchGroup = null;
    boolean pause;
    boolean loopFlag;
    boolean defaultPose;
    long motionLength;
    int elapsedTime;
    long timeOffset = 0;
    long pauseTime;
    HashMap<String,BoneShape> bones = new HashMap<String,BoneShape>();
    ArrayList<SR> selectRequests = new ArrayList<SR>();

    public SkeletonBehavior(Skeleton s,String rulString) {
        skeleton = s;
        try {
            URL url = new URL(rulString);
            bvh = new BVH(url);
        }catch(Exception e) {
            e.printStackTrace();
        }
        motionLength = (long)(bvh.getMotionLength()*1000.0);
        elapsedTime = (int)(1000.0*bvh.getDefaultFrameTime());
        loopFlag = true;
        defaultPose = true;
        this.setSchedulingBounds(bounding);
        allBranchGroup = new BranchGroup();
        allBranchGroup.addChild(constructSkeleton(bvh.getRootBone(),null));
        allBranchGroup.addChild(this);
    }
    BoneShape constructSkeleton(String boneName,BoneShape parent) {
        BoneShape bs = new BoneShape(bvh.getBoneTails(boneName),bvh.getOffset(boneName));
        bones.put(boneName, bs);
        for (String bn : bvh.getChildBones(boneName)) {
            BoneShape bs2 = constructSkeleton(bn,bs);
            bs.addChild(bs2);
        }
        return bs;
    }
    class BoneShape extends TransformGroup {
        Switch mainBone = null;
        LineArray geometries[] = null;
        Vector3d offset;
        BoneShape(ArrayList<Vector3d> tails,Vector3d o) {
            offset = o;
            this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            double maxLen = 0.0;
            Vector3d tg = new Vector3d();
            for (Vector3d t : tails) {
                tg.add(t);
                if (maxLen<t.length())
                    maxLen = t.length();
            }
            tg.scale(1.0/tails.size());
            if (maxLen<0.00001) {
                //この場合はどうすれば良いかわからない。
                mainBone = c1(0.0);
            } else if (tg.length()<(maxLen/100.0)) {
                mainBone = c1(maxLen);
            } else {
                mainBone = c2(tg);
            }
            this.addChild(mainBone);
            geometries = new LineArray[tails.size()];
            int i=0;
            for (Vector3d t : tails) {
                Point3d vertex[] = new Point3d[2];
                vertex[0] = new Point3d(tg);
                vertex[1] = new Point3d(t);
                geometries[i] = new LineArray(2,GeometryArray.COORDINATES|GeometryArray.COLOR_3);
                geometries[i].setCapability(GeometryArray.ALLOW_COLOR_WRITE);
                geometries[i].setCoordinates(0,vertex);
                geometries[i].setColor(0, new Color3f(0.0f,0.0f,1.0f));
                geometries[i].setColor(1, new Color3f(1.0f,0.0f,0.0f));
                Shape3D shape = new Shape3D(geometries[i]);
                this.addChild(shape);
                i++;
            }
        }
        void setSelected(boolean b) {
            for (int i=0;i<geometries.length;i++) {
                if (b) {
                    mainBone.setWhichChild(1);
                    geometries[i].setColor(0, new Color3f(0.8f,0.8f,1.0f));
                    geometries[i].setColor(1, new Color3f(1.0f,0.8f,0.8f));
                } else {
                    mainBone.setWhichChild(0);
                    geometries[i].setColor(0, new Color3f(0.0f,0.0f,1.0f));
                    geometries[i].setColor(1, new Color3f(1.0f,0.0f,0.0f));
                }
            }
        }
        void init() {
            Transform3D t = new Transform3D();
            t.set(offset);
            this.setTransform(t);
        }
    }
    public void initialize() {
        if (elapsedTime==0) //<-- なんでこれが必要なのか
            return;         //<-- わからない
        WakeupOnElapsedTime w = new WakeupOnElapsedTime(elapsedTime);
        wakeupOn(w);
        timeOffset = System.currentTimeMillis();
    }
    @SuppressWarnings("unchecked")
    public void processStimulus(Enumeration criteria) {
        synchronized (bvh) {
            WakeupOnElapsedTime w = new WakeupOnElapsedTime(elapsedTime);
            wakeupOn(w);
            for (SR sr : selectRequests) {
                BoneShape bs = bones.get(sr.name);
                if (bs!=null)
                    bs.setSelected(sr.selected);
            }
            selectRequests.clear();

            if (defaultPose) {
                init();
                return;
            }

            long time;
            long nowTime = System.currentTimeMillis();
            if (pause) {
                time = pauseTime;
            } else {
                time = nowTime - timeOffset;
            }
            time = time % motionLength;
            double timeD = ((double)time)/1000.0;
            for (String boneName : bones.keySet()) {
                TransformGroup tg = bones.get(boneName);
                Transform3D t = bvh.getTransform3D(boneName,timeD);
                tg.setTransform(t);
            }
        }
    }

    void init() {
        for (String boneName : bones.keySet()) {
            BoneShape bs = bones.get(boneName);
            bs.init();
        }
    }

    public void setFrameTime(double d) {
        elapsedTime = (int)(1000.0*d);
    }

    public void setPauseTime(double t) {
        pauseTime = (long)(1000.0*t);
    }

    public void defaultPose() {
        defaultPose = true;
    }

    public void start() {
        defaultPose = false;
        pause = false;
    }
    public void pause() {
        defaultPose = false;
        pause = true;
    }
    public void setBoneSelected(String boneName,boolean b) {
        selectRequests.add(new SR(boneName,b));
    }
    class SR {
        String name;
        boolean selected;
        SR(String n,boolean s) {
            name = n;
            selected = s;
        }
    }
    public String getRootBone() {
        return bvh.getRootBone();
    }
    public String getParentBone(String boneName) {
        return bvh.getParentBone(boneName);
    }
    public String[] getChildBones(String boneName) {
        return bvh.getChildBones(boneName);
    }
    public String[] getAllBones() {
        return bvh.getAllBones();
    }
    //指定された骨のキャラクタ座標系におけるオフセットを返す。
    //つまり指定された骨からたどってルートの骨までの全ての
    //オフセットの合計を返す。
    public Vector3d getOffset(String boneName) {
        Vector3d ret = new Vector3d();
        Vector3d vTmp = bvh.getOffset(boneName);
        if (vTmp==null)
            return null;
        ret.add(vTmp);
        while ((boneName=bvh.getParentBone(boneName))!=null) {
            ret.add(bvh.getOffset(boneName));
        }
        return ret;
    }

    public double getMotionLength() {
        return ((double)motionLength)/1000.0;
    }
//-------------------------------
    Node getNode() {
        return allBranchGroup;
    }

//-------------------------------
    Switch c1(double maxLen) {
        Transform3D t = new Transform3D();
        t.setScale(maxLen);
        TransformGroup transG1 = new TransformGroup(t);
        TransformGroup transG2 = new TransformGroup(t);
        Switch bSwitch = new Switch();
        bSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        VRML.initVRML();
        BranchGroup bg1 = null;
        BranchGroup bg2 = null;
        try {
            URL url1 = new URL("x-res:///jp/sourceforge/acerola3d/resources/DeselectedOrigin.wrl");
            URL url2 = new URL("x-res:///jp/sourceforge/acerola3d/resources/SelectedOrigin.wrl");
            bg1 = VRML.loader.load(url1).getSceneGroup();
            bg2 = VRML.loader.load(url2).getSceneGroup();
        } catch(Exception e) {
            e.printStackTrace();
        }
        transG1.addChild(bg1);
        transG2.addChild(bg2);
        bSwitch.addChild(transG1);
        bSwitch.addChild(transG2);
        bSwitch.setWhichChild(0);
        return bSwitch;
    }
    Switch c2(Vector3d tg) {
        double scale = tg.length();
        Vector3d ntg = new Vector3d(tg);
        ntg.normalize();
        double naiseki = ntg.dot(new Vector3d(0.0,1.0,0.0));
        double kakudo = Math.acos(naiseki);
        Transform3D t = new Transform3D();
        Vector3d gaiseki = new Vector3d();
        gaiseki.cross(new Vector3d(0.0,1.0,0.0),ntg);
        Quat4d q = null;
        if (kakudo>3.14) {
            q = new Quat4d(1.0,0.0,0.0,0.0);
        } else {
            if (gaiseki.length()<0.00001) {
                q = new Quat4d(0.0,0.0,0.0,1.0);
            } else {
                gaiseki.normalize();
                kakudo = kakudo/2.0;
                q = new Quat4d(sin(kakudo)*gaiseki.x,sin(kakudo)*gaiseki.y,sin(kakudo)*gaiseki.z,cos(kakudo));
            }
        }
        t.set(q);
        t.setScale(scale);
        TransformGroup transG1 = new TransformGroup(t);
        TransformGroup transG2 = new TransformGroup(t);
        Switch bSwitch = new Switch();
        bSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        VRML.initVRML();
        BranchGroup bg1 = null;
        BranchGroup bg2 = null;
        try {
            URL url1 = new URL("x-res:///jp/sourceforge/acerola3d/resources/DeselectedBone.wrl");
            URL url2 = new URL("x-res:///jp/sourceforge/acerola3d/resources/SelectedBone.wrl");
            bg1 = VRML.loader.load(url1).getSceneGroup();
            bg2 = VRML.loader.load(url2).getSceneGroup();
        } catch(Exception e) {
            e.printStackTrace();
        }
        transG1.addChild(bg1);
        transG2.addChild(bg2);
        bSwitch.addChild(transG1);
        bSwitch.addChild(transG2);
        bSwitch.setWhichChild(0);
        return bSwitch;
    }
}
