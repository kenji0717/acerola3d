package jp.sourceforge.acerola3d.a3;

import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

class A3Behavior extends Behavior {
    static long elapsedTime = 33l;
    static Node selected3d_egg = new Selected3D();
    A3Object a3 = null;
    A3BranchGroup topGroup;
    TransformGroup transGroup;
    Transform3D t;
    Transform3D tt;
    A3VirtualUniverse universe = null;
    boolean isInterpolate = false;
    boolean autoDirectionControl = false;
    boolean billboardControl = false;
    Vector3d nowS = new Vector3d(1,1,1);
    Vector3d nextS = new Vector3d(1,1,1);
    Quat4d nowQ = new Quat4d(0.0,0.0,0.0,1.0);
    Quat4d nextQ = new Quat4d(0.0,0.0,0.0,1.0);
    Vector3d nowV = new Vector3d();
    Vector3d nextV = new Vector3d();
    boolean needRecalc = true;
    Vector3d nowTrans = new Vector3d();
    BranchGroup selected3d_bg = null;
    TransformGroup selected3d_tg = null;
    double interpolateRatio = 0.1;
    boolean isVisible = true;
    ArrayList<Runnable> runnableQueue = null;
    Vector3d tmpV = new Vector3d();
    
    A3Behavior(A3Object a) {
        a3 = a;
        topGroup = new A3BranchGroup();
        topGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
        topGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        topGroup.setA3(a3);
        t = new Transform3D();
        tt = new Transform3D();
        transGroup = new TransformGroup(t);
        transGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transGroup.addChild(this);
        topGroup.addChild(transGroup);
        runnableQueue = new ArrayList<Runnable>();
    }
    void setA3VirtualUniverse(A3VirtualUniverse u) {
        universe = u;
    }
    void setNode(Node n) {
        transGroup.addChild(n);
    }

    //Z軸が上の座標系を使っている場合にtrue、Y軸が上の座標系を使っているか
    //不明な場合はfalseを返すメソッド。
    boolean isZ() {
        if (universe!=null) {
            if (universe.scene.upperDirection==A3Object.UpperDirection.Z) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //現在の回転・位置・拡大率を今すぐオブジェクトに反映させるメソッド
    Quat4d actualQ = new Quat4d();
    static Quat4d fixQ = new Quat4d(1.0*Math.sin(Math.PI/4.0),0.0,0.0,Math.cos(Math.PI/4.0));
    void setNow() {
        //Z軸を上にする座標系でもY軸を上にする座標系用のキャラクタファイルを
        //そのまま使うことができるように、Z軸を上にする座標系の場合に回転の
        //四元数を最後の最後にX軸まわりに90度回転させる。
        if (isZ())
            actualQ.mul(nowQ,fixQ);
        else
            actualQ.set(nowQ);
        if (isVisible)
            tt.setScale(nowS);
        else
            tt.setScale(0);
        t.set(actualQ,nowV,1);
        t.mul(tt);//こうするしかない？
        try {
            transGroup.setTransform(t);
        } catch (BadTransformException e) {
            System.out.println("BadTransformException in A3Behavior.setNow().");
            //e.printStackTrace();
        }
        if (a3 instanceof Action3D) {
            ((Action3D)a3).setSoundLocQuat(nowV,actualQ);
        } else if (a3 instanceof A3Sounds) {
            ((A3Sounds)a3).setSoundLocQuat(nowV,actualQ);
        }
    }
    void init() {
        setNow();
    }
    public void initialize() {
        WakeupOnElapsedTime w = new WakeupOnElapsedTime(elapsedTime);
        wakeupOn(w);
    }
    @SuppressWarnings("unchecked")
    public void processStimulus(Enumeration criteria) {
        long et;
        if (universe==null) {
            et = elapsedTime;
            WakeupOnElapsedTime w = new WakeupOnElapsedTime(elapsedTime);
            wakeupOn(w);
        } else {
            et = universe.elapsedTime;
            WakeupOnBehaviorPost w = null;
            w = new WakeupOnBehaviorPost(universe.getTimerBehavior(),1);
            wakeupOn(w);
        }

        //Runnableの処理
        ArrayList<Runnable> runnableCopy = null;
        synchronized(runnableQueue) {
            runnableCopy = (ArrayList<Runnable>)runnableQueue.clone();
            runnableQueue.clear();
        }
        for (Runnable r:runnableCopy) {
            r.run();
        }

        if (needRecalc==false)
            return;
        double ratio = 1.0-Math.pow(interpolateRatio,((double)et)/1000.0);
        nowTrans.sub(nextV,nowV);
        nowTrans.scale(ratio);
        nowV.add(nowTrans);
        if (autoDirectionControl) {
            autoQuatControl();
        } else if (billboardControl) {
            billboardControl();
        } else {
            nowQ.normalize();
            nowQ.interpolate(nextQ,ratio);
            nowQ.normalize();
        }
        tmpV.sub(nextS,nowS);
        tmpV.scale(ratio);
        nowS.add(tmpV);
        setNow();
        if (!isInterpolate) {
            needRecalc = false;
        }
    }

    void autoQuatControl() {
        Vector3d front = new Vector3d(); 
        front.sub(nextV,nowV);
        if (front.length()>universe.scene.cameraNowS/1000.0) {
            Vector3d up = new Vector3d(a3.upperVector);
            if (isZ())
                nowQ.set(Util.frontFacingQuat_A3Z(front,up));
            else
                nowQ.set(Util.frontFacingQuat_A3Y(front,up));
        }
    }
    void billboardControl() {
        Vector3d front = new Vector3d(); 
        front.sub(universe.scene.cameraNowV,nowV);
        if (front.length()>universe.scene.cameraNowS/1000.0) {
            Vector3d up = new Vector3d(a3.upperVector);
            if (isZ())
                nowQ.set(Util.frontFacingQuat_A3Z(front,up));
            else
                nowQ.set(Util.frontFacingQuat_A3Y(front,up));
        }
    }
    void setEnableBehavior(boolean b) {
        isInterpolate = b;
    }
    void setInterpolateRatio(double ir) {
        interpolateRatio = ir;
    }
    void setAutoDirectionControl(boolean b) {
        autoDirectionControl = b;
        if (autoDirectionControl&&billboardControl)
            billboardControl=false;
    }
    void setBillboardControl(boolean b) {
        billboardControl = b;
        if (billboardControl&&autoDirectionControl)
            autoDirectionControl=false;
    }
    void setTransformImmediately(Transform3D t) {
        t.getScale(nowS);
        t.get(nowV);
        t.get(nowQ);
        nextS.set(nowS);
        nextV.set(nowV);
        nextQ.set(nowQ);
        //setNow();
        needRecalc = true;
    }
    void move(Vector3d v, Quat4d q, double s) {
        if (isInterpolate) {
            nextS.set(s,s,s);
            nextV.set(v);
            nextQ.set(q);
            nextQ.normalize();
        } else {
            nowS.set(s,s,s);
            nowV.set(v);
            nowQ.set(q);
            nextS.set(s,s,s);
            nextV.set(v);
            nextQ.set(q);
        }
        needRecalc = true;
    }
    void moveImmediately(Vector3d v, Quat4d q, double s) {
        nowS.set(s,s,s);
        nowV.set(v);
        nowQ.set(q);
        nextS.set(s,s,s);
        nextV.set(v);
        nextQ.set(q);
        //setNow();
        needRecalc = true;
    }
    void setLoc(Vector3d v) {
        if (isInterpolate) {
            nextV.set(v);
        } else {
            nowV.set(v);
            nextV.set(v);
        }
        needRecalc = true;
    }
    void setLocImmediately(Vector3d v) {
        nowV.set(v);
        nextV.set(v);
        //setNow();
        needRecalc = true;
    }
    void addLoc(Vector3d v) {
        if (isInterpolate) {
            nextV.add(v);
        } else {
            nextV.add(v);
            nowV.set(nextV);
        }
        needRecalc = true;
    }
    void addLocImmediately(Vector3d v) {
        nextV.add(v);
        nowV.set(nextV);
        //setNow();
        needRecalc = true;
    }
    Vector3d getLoc() {
        return new Vector3d(nowV);
    }
    Vector3d getTargetLoc() {
        return new Vector3d(nextV);
    }
    void setQuat(Quat4d q) {
        if (isInterpolate) {
            nextQ.set(q);
        } else {
            nowQ.set(q);
            nextQ.set(q);
        }
        needRecalc = true;
    }
    void setQuatImmediately(Quat4d q) {
        nowQ.set(q);
        nextQ.set(q);
        //setNow();
        needRecalc = true;
    }
    void mulQuat(Quat4d q) {
        if (isInterpolate) {
            nextQ.mul(q);
        } else {
            nextQ.mul(q);
            nowQ.set(nextQ);
        }
        needRecalc = true;
    }
    void mulQuatImmediately(Quat4d q) {
        nextQ.mul(q);
        nowQ.set(nextQ);
        //setNow();
        needRecalc = true;
    }
    Quat4d getQuat() {
        return new Quat4d(nowQ);
    }
    Quat4d getTargetQuat() {
        return new Quat4d(nextQ);
    }
    void setLookAtPoint(Vector3d lookAt) {
        Vector3d front = new Vector3d();
        front.sub(lookAt,nowV);
        Vector3d up = new Vector3d(a3.upperVector);
        Quat4d q = null;
        if (isZ())
            q = Util.frontFacingQuat_A3Z(front,up);
        else
            q = Util.frontFacingQuat_A3Y(front,up);
        nextQ.set(q);
        if (!isInterpolate) {
            nowQ.set(q);
        }
        needRecalc = true;
    }
    void setLookAtPointImmediately(Vector3d lookAt) {
        Vector3d front = new Vector3d();
        front.sub(lookAt,nowV);
        Vector3d up = new Vector3d(a3.upperVector);
        Quat4d q = null;
        if (isZ())
            q = Util.frontFacingQuat_A3Z(front,up);
        else
            q = Util.frontFacingQuat_A3Y(front,up);
        nextQ.set(q);
        nowQ.set(q);
        //setNow();
        needRecalc = true;
    }
    void setScale(double s) {
        if (isInterpolate) {
            nextS.set(s,s,s);
        } else {
            nowS.set(s,s,s);
            nextS.set(s,s,s);
        }
        needRecalc = true;
    }
    void setScaleX(double sx) {
        if (isInterpolate) {
            nextS.x = sx;
        } else {
            nowS.x = sx;
            nextS.x = sx;
        }
        needRecalc = true;
    }
    void setScaleY(double sy) {
        if (isInterpolate) {
            nextS.y = sy;
        } else {
            nowS.y = sy;
            nextS.y = sy;
        }
        needRecalc = true;
    }
    void setScaleZ(double sz) {
        if (isInterpolate) {
            nextS.z = sz;
        } else {
            nowS.z = sz;
            nextS.z = sz;
        }
        needRecalc = true;
    }
    void setScale(Vector3d sv) {
        if (isInterpolate) {
            nextS.set(sv);
        } else {
            nowS.set(sv);
            nextS.set(sv);
        }
        needRecalc = true;
    }
    void setScaleImmediately(double s) {
        nowS.set(s,s,s);
        nextS.set(s,s,s);
        //setNow();
        needRecalc = true;
    }
    void setScaleXImmediately(double sx) {
        nowS.x = sx;
        nextS.x = sx;
        //setNow();
        needRecalc = true;
    }
    void setScaleYImmediately(double sy) {
        nowS.y = sy;
        nextS.y = sy;
        //setNow();
        needRecalc = true;
    }
    void setScaleZImmediately(double sz) {
        nowS.z = sz;
        nextS.z = sz;
        //setNow();
        needRecalc = true;
    }
    void setScaleImmediately(Vector3d sv) {
        nowS.set(sv);
        nextS.set(sv);
        //setNow();
        needRecalc = true;
    }
    void mulScale(double s) {
        if (isInterpolate) {
            nextS.scale(s);
        } else {
            nextS.scale(s);
            nowS.set(nextS);
        }
        needRecalc = true;
    }
    void mulScaleX(double sx) {
        if (isInterpolate) {
            nextS.x *= sx;
        } else {
            nextS.x *= sx;
            nowS.set(nextS);
        }
        needRecalc = true;
    }
    void mulScaleY(double sy) {
        if (isInterpolate) {
            nextS.y *= sy;
        } else {
            nextS.y *= sy;
            nowS.set(nextS);
        }
        needRecalc = true;
    }
    void mulScaleZ(double sz) {
        if (isInterpolate) {
            nextS.z *= sz;
        } else {
            nextS.z *= sz;
            nowS.set(nextS);
        }
        needRecalc = true;
    }
    void mulScale(Vector3d sv) {
        if (isInterpolate) {
            nextS.x *= sv.x;
            nextS.y *= sv.y;
            nextS.z *= sv.z;
        } else {
            nextS.x *= sv.x;
            nextS.y *= sv.y;
            nextS.z *= sv.z;
            nowS.set(nextS);
        }
        needRecalc = true;
    }
    void mulScaleImmediately(double s) {
        nextS.scale(s);
        nowS.set(nextS);
        //setNow();
        needRecalc = true;
    }
    void mulScaleXImmediately(double sx) {
        nextS.x *= sx;
        nowS.set(nextS);
        //setNow();
        needRecalc = true;
    }
    void mulScaleYImmediately(double sy) {
        nextS.y *= sy;
        nowS.set(nextS);
        //setNow();
        needRecalc = true;
    }
    void mulScaleZImmediately(double sz) {
        nextS.z *= sz;
        nowS.set(nextS);
        //setNow();
        needRecalc = true;
    }
    void mulScaleImmediately(Vector3d sv) {
        nextS.x *= sv.x;
        nextS.y *= sv.y;
        nextS.z *= sv.z;
        nowS.set(nextS);
        //setNow();
        needRecalc = true;
    }
    double getScale() {
        return (nowS.x+nowS.y+nowS.z)/3.0;
    }
    double getScaleX() {
        return nowS.x;
    }
    double getScaleY() {
        return nowS.y;
    }
    double getScaleZ() {
        return nowS.z;
    }
    Vector3d getScaleV() {
        return new Vector3d(nowS);
    }
    double getTargetScale() {
        return (nextS.x+nextS.y+nextS.z)/3.0;
    }
    double getTargetScaleX() {
        return nextS.x;
    }
    double getTargetScaleY() {
        return nextS.y;
    }
    double getTargetScaleZ() {
        return nextS.z;
    }
    Vector3d getTargetScaleV() {
        return new Vector3d(nextS);
    }

    public final void moveForward(double l) {
        Vector3d v = getUnitVecZ();
        v.scale(l);
        addLoc(v);
    }
    public final void moveForwardImmediately(double l) {
        Vector3d v = getUnitVecZ();
        v.scale(l);
        addLocImmediately(v);
    }
    public final void moveBackward(double l) {
        Vector3d v = getUnitVecZ();
        v.scale(-l);
        addLoc(v);
    }
    public final void moveBackwardImmediately(double l) {
        Vector3d v = getUnitVecZ();
        v.scale(-l);
        addLocImmediately(v);
    }
    public final void moveRight(double l) {
        Vector3d v = getUnitVecX();
        v.scale(-l);
        addLoc(v);
    }
    public final void moveRightImmediately(double l) {
        Vector3d v = getUnitVecX();
        v.scale(-l);
        addLocImmediately(v);
    }
    public final void moveLeft(double l) {
        Vector3d v = getUnitVecX();
        v.scale(l);
        addLoc(v);
    }
    public final void moveLeftImmediately(double l) {
        Vector3d v = getUnitVecX();
        v.scale(l);
        addLocImmediately(v);
    }
    public final void moveUp(double l) {
        Vector3d v = getUnitVecY();
        v.scale(l);
        addLoc(v);
    }
    public final void moveUpImmediately(double l) {
        Vector3d v = getUnitVecY();
        v.scale(l);
        addLocImmediately(v);
    }
    public final void moveDown(double l) {
        Vector3d v = getUnitVecY();
        v.scale(-l);
        addLoc(v);
    }
    public final void moveDownImmediately(double l) {
        Vector3d v = getUnitVecY();
        v.scale(-l);
        addLocImmediately(v);
    }
    public final void moveTo(Vector3d v, double l) {
        Vector3d vv = new Vector3d();
        vv.sub(v,nowV);
        if (vv.length()!=0.0) vv.normalize();
        vv.scale(l);
        addLoc(vv);
    }
    public final void moveTo(double x, double y, double z, double l) {
        Vector3d vv = new Vector3d(x,y,z);
        vv.sub(nowV);
        if (vv.length()!=0.0) vv.normalize();
        vv.scale(l);
        addLoc(vv);
    }
    public final void moveToImmediately(Vector3d v, double l) {
        Vector3d vv = new Vector3d();
        vv.sub(v,nowV);
        if (vv.length()!=0.0) vv.normalize();
        vv.scale(l);
        addLocImmediately(vv);
    }
    public final void moveToImmediately(double x, double y, double z, double l) {
        Vector3d vv = new Vector3d(x,y,z);
        vv.sub(nowV);
        if (vv.length()!=0.0) vv.normalize();
        vv.scale(l);
        addLocImmediately(vv);
    }
    public final void turnUp(double deg) {
        mulQuat(Util.euler2quat(-deg/180.0*Math.PI,0,0));
    }
    public final void turnUpNow(double deg) {
        mulQuatImmediately(Util.euler2quat(-deg/180.0*Math.PI,0,0));
    }
    public final void turnDown(double deg) {
        mulQuat(Util.euler2quat(deg/180.0*Math.PI,0,0));
    }
    public final void turnDownNow(double deg) {
        mulQuatImmediately(Util.euler2quat(deg/180.0*Math.PI,0,0));
    }
    public final void turnRight(double deg) {
        mulQuat(Util.euler2quat(0,-deg/180.0*Math.PI,0));
    }
    public final void turnRightNow(double deg) {
        mulQuatImmediately(Util.euler2quat(0,-deg/180.0*Math.PI,0));
    }
    public final void turnLeft(double deg) {
        mulQuat(Util.euler2quat(0,deg/180.0*Math.PI,0));
    }
    public final void turnLeftNow(double deg) {
        mulQuatImmediately(Util.euler2quat(0,deg/180.0*Math.PI,0));
    }
    public final void rollRight(double deg) {
        mulQuat(Util.euler2quat(0,0,deg/180.0*Math.PI));
    }
    public final void rollRightNow(double deg) {
        mulQuatImmediately(Util.euler2quat(0,0,deg/180.0*Math.PI));
    }
    public final void rollLeft(double deg) {
        mulQuat(Util.euler2quat(0,0,-deg/180.0*Math.PI));
    }
    public final void rollLeftNow(double deg) {
        mulQuatImmediately(Util.euler2quat(0,0,-deg/180.0*Math.PI));
    }
    public final void turnTo(double dirX, double dirY, double dirZ) {
        Vector3d dir = new Vector3d(dirX,dirY,dirZ);
        turnTo(dir);
    }
    public final void turnTo(Vector3d dir) {
        Vector3d lookAtPoint = new Vector3d();
        lookAtPoint.add(nowV,dir);
        setLookAtPoint(lookAtPoint);
    }
    public final void turnToNow(double dirX, double dirY, double dirZ) {
        Vector3d dir = new Vector3d(dirX,dirY,dirZ);
        turnToNow(dir);
    }
    public final void turnToNow(Vector3d dir) {
        Vector3d lookAtPoint = new Vector3d();
        lookAtPoint.add(nowV,dir);
        setLookAtPointImmediately(lookAtPoint);
    }

    Vector3d getUnitVecX() {
        return Util.trans(nowQ,new Vector3d(1.0,0.0,0.0));
    }
    Vector3d getUnitVecY() {
        return Util.trans(nowQ,new Vector3d(0.0,1.0,0.0));
    }
    Vector3d getUnitVecZ() {
        return Util.trans(nowQ,new Vector3d(0.0,0.0,1.0));
    }
    double getSpeed() {
        if (isInterpolate) {
            if (universe!=null)
                return nowTrans.length()/(universe.elapsedTime/1000.0);
            else
                return nowTrans.length()/(elapsedTime/1000.0);
        } else {
            return 0.0;
        }
    }
    static void setSelected3DMarker(Node n) {
        selected3d_egg = n;
    }
    void setSelected3D(boolean b) {
        if (b&&(selected3d_bg==null)) {
            Node n = selected3d_egg.cloneTree();
            n.setPickable(false);
            selected3d_tg = new TransformGroup();
            selected3d_tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            selected3d_tg.addChild(n);
            selected3d_bg = new BranchGroup();
            selected3d_bg.setCapability(BranchGroup.ALLOW_DETACH);
            selected3d_bg.addChild(selected3d_tg);
            adjustSelected3D();
            topGroup.addChild(selected3d_bg);
        } else if (!b && selected3d_bg!=null) {
            topGroup.removeChild(selected3d_bg);
            selected3d_bg = null;
            selected3d_tg = null;
        }
    }
    void adjustSelected3D() {
        Bounds b = transGroup.getBounds();
        if (b instanceof BoundingSphere) {
            BoundingSphere bs = (BoundingSphere)b;
            Point3d p = new Point3d();
            bs.getCenter(p);
            double r = bs.getRadius();
            Transform3D t = new Transform3D();
            tmpV.set(nowS);
            tmpV.scale(r);
            t.setScale(tmpV);
            p.scale(getScale());//2013,03/12OK???
            Vector3d v = new Vector3d();
            v.add(nowV,p);
            t.setTranslation(v);
            selected3d_tg.setTransform(t);
        } else {
            // TODO
        }
    }
    boolean isSelected3D() {
        if (selected3d_bg==null)
            return false;
        else
            return true;
    }
    void setVisible(boolean b) {
        isVisible = b;
        needRecalc = true;
    }
    void addRunnable(Runnable r) {
        synchronized(runnableQueue) {
            runnableQueue.add(r);
        }
    }
}
