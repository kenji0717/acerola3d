package jp.sourceforge.acerola3d.a3;

import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;

class PickingBehavior extends Behavior {
    A3CanvasInterface canvas;
    javax.media.j3d.Locale locale;
    Transform3D vpt = new Transform3D();
    ArrayList<A3Listener> listeners;
    Object eventMaker;

    public PickingBehavior(A3CanvasInterface c,javax.media.j3d.Locale l) {
        canvas = c;
        locale = l;
        listeners = new ArrayList<A3Listener>();
        if (c instanceof A3Window)
            eventMaker = ((A3Window)c).canvas;
        else if (c instanceof JA3Window)
            eventMaker = ((JA3Window)c).canvas;
        else if (c instanceof A3Canvas)
            eventMaker = c;
        else if (c instanceof JA3Canvas)
            eventMaker = c;
        else if (c instanceof A3Screen)
            eventMaker = c;
        else if (c instanceof A3Widget)
            eventMaker = ((A3Widget)c).tCanvas;
        else if (c instanceof JA3Canvas2)
            eventMaker = c;
        else if (c instanceof A3SubCanvas)
            eventMaker = c;
        else if (c instanceof JA3SubCanvas)
            eventMaker =c;
    }

    public void addA3Listener(A3Listener l) {
        synchronized (listeners) {
            if (!listeners.contains(l))
                listeners.add(l);
        }
    }
    public void removeA3Listener(A3Listener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public void initialize() {
        WakeupOnAWTEvent w = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        wakeupOn(w);
    }

    @SuppressWarnings("unchecked")
    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion w = (WakeupCriterion)criteria.nextElement();
//            WakeupOnAWTEvent w = (WakeupOnAWTEvent)
            if (w instanceof WakeupOnAWTEvent) {            
                AWTEvent e[] = ((WakeupOnAWTEvent)w).getAWTEvent();
                for (int i=0;i<e.length;i++) {
                    if (e[i].getID()==MouseEvent.MOUSE_PRESSED)
                        processMousePressed((MouseEvent)e[i]);
                    else if (e[i].getID()==MouseEvent.MOUSE_DRAGGED)
                        processMouseDragged((MouseEvent)e[i]);
                    else if (e[i].getID()==MouseEvent.MOUSE_RELEASED)
                        processMouseReleased((MouseEvent)e[i]);
                }
            } else if (w instanceof WakeupOnElapsedTime) {
                wakeupOnElapsedTime((WakeupOnElapsedTime)w);
            }
        }
    }

    int mouseX;
    int mouseY;
    A3Object currentA3;
    void processMousePressed(MouseEvent me) {
//      System.out.println("gaha:mousePressed");

        if (me.getSource()==eventMaker) {
            mouseX = me.getX();
            mouseY = me.getY();

            currentA3 = pickA3(me.getX(),me.getY());

            A3Event a3Event = new A3Event();
            a3Event.setMouseEvent(me);
            a3Event.setA3Object(currentA3);
            ArrayList<A3Listener> ls = null;
            synchronized (listeners) {
                ls = new ArrayList<A3Listener>(listeners);
            }
            for (A3Listener l : ls) {
                l.mousePressed(a3Event);
            }
        }

        WakeupOnAWTEvent ws[] = new WakeupOnAWTEvent[2];
        ws[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        ws[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
        WakeupOr w = new WakeupOr(ws);
        wakeupOn(w);
    }

    void processMouseDragged(MouseEvent me) {
//      System.out.println("gaha:mouseDragged");

        if (me.getSource()==eventMaker) {
            A3Event a3Event = new A3Event();
            a3Event.setMouseEvent(me);
            a3Event.setA3Object(currentA3);
            ArrayList<A3Listener> ls = null;
            synchronized (listeners) {
                ls = new ArrayList<A3Listener>(listeners);
            }
            for (A3Listener l : ls) {
                l.mouseDragged(a3Event);
            }
        }
        
        WakeupOnAWTEvent ws[] = new WakeupOnAWTEvent[2];
        ws[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        ws[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
        WakeupOr w = new WakeupOr(ws);
        wakeupOn(w);
    }
    void processMouseReleased(MouseEvent me) {
//        System.out.println("gaha:mouseReleased");

        if (me.getSource()==eventMaker) {
            A3Event a3Event = new A3Event();
            a3Event.setMouseEvent(me);
            a3Event.setA3Object(currentA3);            // 下とどっちがいいか？
            A3Object a3 = pickA3(me.getX(),me.getY()); //
//          a3Event.setA3Object(a3);                   // 上とどっちがいいか？
            ArrayList<A3Listener> ls = null;
            synchronized (listeners) {
                ls = new ArrayList<A3Listener>(listeners);
            }
            for (A3Listener l : ls) {
                l.mouseReleased(a3Event);
            }

            //int xx = me.getX()-mouseX;
            //int yy = me.getY()-mouseY;
            //if (xx*xx+yy*yy<18) {          // +
            //    processMouseClicked(me);   // | 下とどっちがいいか？
            //    return;                    // |
            //}                              // +
            if (a3 == currentA3) {         // +
                processMouseClicked(me);     // | 上とどっちがいいか？
                return;                    // |
            }                              // +
        }

        WakeupOnAWTEvent w = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        wakeupOn(w);
    }

    int doubleClickMaxTime = 400; // ダブルクリックと見なす最大時間(ms)
    long clickedTime;
    void processMouseClicked(MouseEvent me) {
        if (System.currentTimeMillis()-clickedTime < doubleClickMaxTime) {
            clickedTime = 0;
            processMouseDoubleClicked(me);
            return;
        }

        meTmp = me;
        clickedTime = System.currentTimeMillis();
        WakeupCriterion ws[] = new WakeupCriterion[2];
        ws[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        ws[1] = new WakeupOnElapsedTime(doubleClickMaxTime);
        WakeupOr w = new WakeupOr(ws);
        wakeupOn(w);
    }
    MouseEvent meTmp;
    void wakeupOnElapsedTime(WakeupOnElapsedTime w) {
        A3Event a3Event = new A3Event();
        a3Event.setA3Object(currentA3);
        a3Event.setMouseEvent(meTmp);
        ArrayList<A3Listener> ls = null;
        synchronized (listeners) {
            ls = new ArrayList<A3Listener>(listeners);
        }
        for (A3Listener l : ls) {
            l.mouseClicked(a3Event);
        }
        WakeupOnAWTEvent ww = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        wakeupOn(ww);
    }
    void processMouseDoubleClicked(MouseEvent me) {
        A3Event a3Event = new A3Event();
        a3Event.setA3Object(currentA3);
        a3Event.setMouseEvent(me);
        ArrayList<A3Listener> ls = null;
        synchronized (listeners) {
            ls = new ArrayList<A3Listener>(listeners);
        }
        for (A3Listener l : ls) {
            l.mouseDoubleClicked(a3Event);
        }
        WakeupOnAWTEvent ww = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        wakeupOn(ww);
    }

    // 座標計算とピッキング
    Point3d canvasToVirtualCS(int x,int y,double dis) {
        Point3d point = canvasToPhysicalCS(x,y,dis);
        point = physicalCSToVirtualCS(point);
        return point;
    }
    Point3d canvasToVirtualCS(int x,int y) {
        A3Object avatar = canvas.getAvatar();
        if (avatar==null)
            return null;
        Vector3d v1 = avatar.getLoc();
        Vector3d v2 = canvas.getCameraLoc();
        Vector3d v = new Vector3d();
        v.sub(v1,v2);
        //return canvasToVirtualCS(x,y,v.length());
        Point3d p = virtualCSToPhysicalCS(new Point3d(v));
        return canvasToVirtualCS(x,y,p.z);
    }
    Point virtualCSToCanvas(Point3d p) {
        Point3d point = virtualCSToPhysicalCS(p);
        return physicalCSToCanvas(point);
    }

    Point3d canvasToPhysicalCS_BAK(int x,int y,double dis) {
        Point3d ret = new Point3d();
        //以下のメソッド便利かと思ったけどCanvas3Dの座標でなくディスプレイ(Screen)
        //の座標になるので逆に使い辛い。javax.media.j3d.Viewで設定できる
        //いろんな投影モードに対応するときなどには使った方がよいかもしれないけど、
        //とりあえず保留しておく。
        canvas.getCanvas3D().getPixelLocationInImagePlate(x,y,ret);
        return ret;
    }
    Point3d canvasToPhysicalCS(int x,int y,double dis) {
        int dw = canvas.getCanvas3D().getWidth();
        int dh = canvas.getCanvas3D().getHeight();
        double ww = canvas.getCanvas3D().getPhysicalWidth();
        double hh = canvas.getCanvas3D().getPhysicalHeight();
        double tt = canvas.getCanvas3D().getView().getFieldOfView();//スクリーンの左右の画角

        // dx,dy,dz:スクリーン上での座標(物理座標系で)
        double dx =  ((double)x)*ww/((double)dw)-ww/2.0;
        double dy = -((double)y)*hh/((double)dh)+hh/2.0;
        double dz = -ww/2.0/Math.tan(tt/2.0);

        if (canvas.getCanvas3D().getView().getProjectionPolicy()==View.PARALLEL_PROJECTION) {
            double s = canvas.getCanvas3D().getView().getScreenScale();
            double sw = canvas.getCanvas3D().getScreen3D().getPhysicalScreenWidth();
            //double sh = camera.getCanvas3D().getScreen3D().getPhysicalScreenHeight();
            //return new Point3d(dx/s*(sw/ww),dy/s*(sh/hh),-dis); //どうやらこっちじゃないみたい
            return new Point3d(dx/s*(sw/ww),dy/s*(sw/ww),-dis);
        }

        double s = dis/(-dz);
        return new Point3d(s*dx,s*dy,s*dz);
    }
    Point3d canvasToPhysicalCS(int x,int y) {
        A3Object avatar = canvas.getAvatar();
        if (avatar==null)
            return null;
        Vector3d v1 = avatar.getLoc();
        Vector3d v2 = canvas.getCameraLoc();
        Vector3d v = new Vector3d();
        v.sub(v1,v2);
        //return canvasToPhysicalCS(x,y,v.length());
        Point3d p = virtualCSToPhysicalCS(new Point3d(v));
        return canvasToPhysicalCS(x,y,p.z);
    }
    Point physicalCSToCanvas(Point3d p) {
        int dw = canvas.getCanvas3D().getWidth();
        int dh = canvas.getCanvas3D().getHeight();
        double ww = canvas.getCanvas3D().getPhysicalWidth();
        double hh = canvas.getCanvas3D().getPhysicalHeight();

        double tt = canvas.getCanvas3D().getView().getFieldOfView();
        tt = ww/2.0/Math.tan(tt/2.0); // 視点とスクリーンの距離
        double zz = -p.z;

        Point ret = new Point();
        if (canvas.getCanvas3D().getView().getProjectionPolicy()==View.PARALLEL_PROJECTION) {
            double s = canvas.getCanvas3D().getView().getScreenScale();
            double sw = canvas.getCanvas3D().getScreen3D().getPhysicalScreenWidth();
            //double sh = camera.getCanvas3D().getScreen3D().getPhysicalScreenHeight();
            ret.x = (int)(( p.x*(dw/((double)ww)))*s*ww/sw+dw/2);
            ret.y = (int)((-p.y*(dh/((double)hh)))*s*ww/sw+dh/2);
        } else {
            ret.x = (int)( p.x*(dw/((double)ww))/(zz/tt))+dw/2;
            ret.y = (int)(-p.y*(dh/((double)hh))/(zz/tt))+dh/2;
        }
        return ret;
    }

    Point3d physicalCSToVirtualCS(Point3d p) {
        Point3d pp = new Point3d(p);
        canvas.getTransformGroupForViewPlatform().getTransform(vpt);
        vpt.transform(pp);
        return pp;
    }
    /* Vector3dで計算させると期待どうりにならないことが判明
    Vector3d physicalCSToVirtualCS(Vector3d v) {
        Vector3d vv = new Vector3d(v);
        vptg.getTransform(vpt);
        vpt.transform(vv);
        return vv;
    }*/
    Point3d virtualCSToPhysicalCS(Point3d p) {
        Point3d pp = new Point3d(p);
        canvas.getTransformGroupForViewPlatform().getTransform(vpt);
        vpt.invert();
        vpt.transform(pp);
        return pp;
    }
    /* Vector3dで計算させると期待どうりにならないことが判明
    Vector3d virtualCSToPhysicalCS(Vector3d v) {
        Vector3d vv = new Vector3d(v);
        vptg.getTransform(vpt);
        vpt.invert();
        vpt.transform(vv);
        return vv;
    }*/

    A3Object pickA3(int x,int y) {
        PickRay pr = null;
        if (canvas.getCanvas3D().getView().getProjectionPolicy()==View.PARALLEL_PROJECTION) {
            Vector3d v0 = new Vector3d(canvasToVirtualCS(x,y,0.0));
            Vector3d v1 = new Vector3d(canvasToVirtualCS(x,y,1.0));
            v1.sub(v0);
            pr = new PickRay(new Point3d(v0),v1);
        } else {
            Vector3d v = new Vector3d(canvasToVirtualCS(x,y,1.0));
            Point3d p = new Point3d(canvas.getCameraLoc());
            v.sub(p);
            pr = new PickRay(p,v);
        }
        //SceneGraphPath sgp = locale.pickClosest(pr);
        SceneGraphPath sgp[] = locale.pickAllSorted(pr);

        if (sgp==null) {
            return null;
        }

        for (int i=0;i<sgp.length;i++) {
            for (int j=0;j<sgp[i].nodeCount();j++) {
                Node n = sgp[i].getNode(j);
                if (n instanceof A3BranchGroup) {
                    A3BranchGroup ebn = (A3BranchGroup)n;
                    if (ebn.getA3().pickable==true)
                        return ebn.getA3();
                }
            }
        }

        return null;
    }
    A3Object pickA3(Vector3d origin,Vector3d dir) {
        PickRay pr = new PickRay(new Point3d(origin),dir);
        //SceneGraphPath sgp = locale.pickClosest(pr);
        SceneGraphPath sgp[] = locale.pickAllSorted(pr);

        if (sgp==null) {
            return null;
        }

        for (int i=0;i<sgp.length;i++) {
            for (int j=0;j<sgp[i].nodeCount();j++) {
                Node n = sgp[i].getNode(j);
                if (n instanceof A3BranchGroup) {
                    A3BranchGroup ebn = (A3BranchGroup)n;
                    if (ebn.getA3().pickable==true)
                        return ebn.getA3();
                }
            }
        }

        return null;
    }
}
