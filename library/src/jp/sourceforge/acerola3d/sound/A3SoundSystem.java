package jp.sourceforge.acerola3d.sound;

import com.jogamp.openal.*;
import com.jogamp.openal.util.*;
import javax.vecmath.*;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.net.URL;
import java.net.URLConnection;
//import java.util.concurrent.TimeUnit;

public class A3SoundSystem {
    static final A3SoundSystem defaultSoundSystem = new A3SoundSystem();
    static ArrayList<A3SoundSystem> soundSystems = new ArrayList<A3SoundSystem>();
    AL al;
    ArrayList<A3Sound> sounds = new ArrayList<A3Sound>();
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    public static A3SoundSystem getDefaultSoundSystem() {
        return defaultSoundSystem;
    }

    A3SoundSystem() {
        try {
            //ALut.alutInit();
            ALC alc = ALFactory.getALC();
            ALCdevice device = alc.alcOpenDevice(null);
            if (device!=null) {
                ALCcontext context = alc.alcCreateContext(device, null);
                if (context!=null) {
                    alc.alcMakeContextCurrent(context);
                }
            }
            al = ALFactory.getAL();
            al.alGetError();
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        //距離による減衰の計算方法
        //al.alDistanceModel(AL.AL_EXPONENT_DISTANCE);
        //al.alDistanceModel(AL.AL_EXPONENT_DISTANCE_CLAMPED);
        //al.alDistanceModel(AL.AL_LINEAR_DISTANCE);
        //al.alDistanceModel(AL.AL_LINEAR_DISTANCE_CLAMPED);
        //al.alDistanceModel(AL.AL_INVERSE_DISTANCE);
        //al.alDistanceModel(AL.AL_INVERSE_DISTANCE_CLAMPED);
        //int e = al.alGetError();
        //System.out.println("gaha:"+e);
    }
    public A3Sound load(String url,float segno,A3SoundType type,float gain,boolean loop,Vector3d offset,Vector3d dir) throws Exception {
        //圧縮フォーマットで64kByte以上はLongSoundにする．
        boolean isLongSound = false;
        if (url.toLowerCase().endsWith(".mp3")||url.toLowerCase().endsWith(".ogg")) {
            URL url1 = new URL(url);
            URLConnection c = url1.openConnection();
            if (c.getContentLength()>1024*64) {
                isLongSound = true;
            }
        }

        A3Sound newSound = null;
        if (isLongSound) {
            newSound = new LongSound();
            newSound.init(url,this,segno,type,gain,loop,offset,dir);
        } else {
            newSound = new ShortSound();
            newSound.init(url,this,segno,type,gain,loop,offset,dir);
        }
        synchronized (sounds) {
            sounds.add(newSound);
        }
        return newSound;
    }
    public void setListenerLoc(float x,float y,float z) {
        if (al==null) return;
        al.alListener3f(AL.AL_POSITION,x,y,z);
    }
    public void setListenerLoc(Vector3d l) {
        if (al==null) return;
        al.alListener3f(AL.AL_POSITION,(float)l.x,(float)l.y,(float)l.z);
    }
    public void setListenerRot(float x,float y,float z) {
        Quat4d qx = new Quat4d(Math.sin(x/2.0),0.0,0.0,Math.cos(x/2.0));
        Quat4d qy = new Quat4d(0.0,Math.sin(y/2.0),0.0,Math.cos(y/2.0));
        Quat4d qz = new Quat4d(0.0,0.0,Math.sin(z/2.0),Math.cos(z/2.0));
        Quat4d q = new Quat4d(0.0,0.0,0.0,1.0);
        q.mul(qx);
        q.mul(qy);
        q.mul(qz);
        setListenerQuat(q);
    }
    public void setListenerRot(Vector3d r) {
        float x = (float)r.x;
        float y = (float)r.y;
        float z = (float)r.z;
        setListenerRot(x,y,z);
    }
    public void setListenerQuat(Quat4d q) {
        if (al==null) return;
        Quat4d qTmp = new Quat4d(0.0,0.0,-1.0,0.0);
        Quat4d qq = new Quat4d(q);
        Quat4d qc = new Quat4d(q);
        qc.conjugate();
        qTmp.mul(qq,qTmp);
        qTmp.mul(qc);
        float lx = (float)qTmp.x;
        float ly = (float)qTmp.y;
        float lz = (float)qTmp.z;
        qTmp = new Quat4d(0.0,1.0,0.0,0.0);
        qTmp.mul(qq,qTmp);
        qTmp.mul(qc);
        float ux = (float)qTmp.x;
        float uy = (float)qTmp.y;
        float uz = (float)qTmp.z;

        float lookAtAndUp[] = {lx,ly,lz,ux,uy,uz};
        al.alListenerfv(AL.AL_ORIENTATION,lookAtAndUp,0);
    }
    public void setListenerVel(float x,float y,float z) {
        if (al==null) return;
        al.alListener3f(AL.AL_VELOCITY,x,y,z);
    }
    public void setListenerVel(Vector3d v) {
        if (al==null) return;
        al.alListener3f(AL.AL_VELOCITY,(float)v.x,(float)v.y,(float)v.z);
    }
    public Vector3d getListenerVel() {
        if (al==null) return null;
        // TODO
        return null;
    }
    public void setListenerGain(double g) {
        if (al==null) return;
        al.alListenerf(AL.AL_GAIN,(float)g);
    }
    public double getListenerGain() {
        if (al==null) return -1.0;
        float f[] = new float[1];
        al.alGetListenerf(AL.AL_GAIN,f,0);
        return f[0];
    }
    public void cleanUp() {
        executor.shutdownNow();
        ArrayList<A3Sound> sTmp = new ArrayList<A3Sound>(sounds);
        for (A3Sound s:sTmp) {
            s.cleanUp();
        }
        ALut.alutExit();
        System.out.println("SoundSystem.cleanUp()");
    }
}
