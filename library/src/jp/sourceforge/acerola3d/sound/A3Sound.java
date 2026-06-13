package jp.sourceforge.acerola3d.sound;

import javax.vecmath.*;

public interface A3Sound {
    public void init(String url,A3SoundSystem ss,float segno,A3SoundType type,float gain,boolean loop,Vector3d offset,Vector3d dir) throws Exception;
    public void start();//resumeとしても働く
    public void segno();
    public void stop();
    public void pause();
    public void rewind();
    public void setLoc(float x,float y,float z);
    public void setLoc(Vector3d v);
    public void setRot(float x,float y,float z);
    public void setRot(Vector3d r);
    public void setQuat(Quat4d q);
    public void setVel();
    public void setGain(float g);
    public float getGain();
    public void setDirection(float x,float y,float z);//内部座標における値
    public void setDirection(Vector3d d);//内部座標における値
    public void setOffset(float x,float y,float z);
    public void setOffset(Vector3d o);
    public void setLoop(boolean l);
    public void cleanUp();
    public A3Sound copy();
    public void setType(A3SoundType t);
    public void setSegno(float s);
}
