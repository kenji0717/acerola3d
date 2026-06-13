package jp.sourceforge.acerola3d.sound;

import com.jogamp.openal.*;

import javax.vecmath.*;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.nio.*;

import jp.sourceforge.acerola3d.a3.Util;

public class ShortSound implements A3Sound {
    String file;
    A3SoundSystem soundSystem;
    float segnoTime;
    A3SoundType type;
    float gain;
    boolean loop=false;
    Vector3f offset = new Vector3f();
    Vector3f loc = new Vector3f();
    Quat4d quat = new Quat4d(0.0,0.0,0.0,1.0);
    Vector3f orgDir = new Vector3f(0.0f,0.0f,1.0f);
    Vector3f dir = new Vector3f(0.0f,0.0f,1.0f);
    int buffer[] = new int[1];
    int source[] = new int[1];
    boolean sourceInit = false;//source[0]が初期化済みかどうかを記録しておく

    ShortSound() {
    }
    
    public void init(String file,A3SoundSystem ss,float segno,A3SoundType t,float g,boolean l,Vector3d offset,Vector3d dir) throws Exception {
        this.file = file;
        soundSystem = ss;
        segnoTime = segno;
        type = t;
        gain = g;
        loop = l;
        this.offset.set(offset);
        this.orgDir.set(dir);
        this.dir.set(dir);

        initBuffer();
        //initSource();
    }

    void initBuffer() throws Exception {
        if (soundSystem.al==null) return;
        int format[] = new int[1];
        ByteBuffer data[] = new ByteBuffer[1];
        int size[] = new int[1];
        int freq[] = new int[1];

        URL url = new URL(file);
        InputStream is = url.openStream();
        AudioInputStream ais = AudioSystem.getAudioInputStream(is);
        AudioFormat audioFormat = ais.getFormat();
        audioFormat = new AudioFormat(
            audioFormat.getSampleRate(),
            16,
            audioFormat.getChannels(),
            true,
            false);
        ais = AudioSystem.getAudioInputStream(audioFormat,ais);
/*
long skipB=50000l;
long sum=0;
long count=0;
while (sum<skipB) {
    count = ais.skip(skipB-sum);
    System.out.println(count);
    sum+=count;
}
System.out.println("sum:"+sum);
*/
        //ais.skip(100000l);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte b[] = new byte[1024];
        int cnt=0;
        while (true) {
            if ((cnt=ais.read(b))==-1)
                break;
            baos.write(b,0,cnt);
        }
        b = baos.toByteArray();
        data[0] = ByteBuffer.wrap(b);
        if (audioFormat.getChannels()==1)
            format[0] = AL.AL_FORMAT_MONO16;
        else
            format[0] = AL.AL_FORMAT_STEREO16;
        size[0] = b.length;
        freq[0] = (int)audioFormat.getSampleRate();

        soundSystem.al.alGenBuffers(1,buffer,0); check(1);
        soundSystem.al.alBufferData(buffer[0],format[0],data[0],size[0],freq[0]); check(2);
    }
    void initSource() {
        if (soundSystem.al==null) return;
        soundSystem.al.alGenSources(1,source,0); check(3);
        sourceInit=true;

        soundSystem.al.alSourcei(source[0],AL.AL_BUFFER,buffer[0]);
        soundSystem.al.alSourcef(source[0],AL.AL_PITCH,1.0f);
        soundSystem.al.alSourcef(source[0],AL.AL_GAIN,1.0f);
        Vector3d v = Util.trans(quat,new Vector3d(offset));
        v.add(new Vector3d(loc));
        soundSystem.al.alSource3f(source[0],AL.AL_POSITION,(float)v.x,(float)v.y,(float)v.z);
        //al.alSource3i(source[0],AL.AL_VELOCITY,0,0,0);
        soundSystem.al.alSourcef(source[0],AL.AL_GAIN,gain);
        if (loop)
            soundSystem.al.alSourcei(source[0],AL.AL_LOOPING,AL.AL_TRUE);
        else
            soundSystem.al.alSourcei(source[0],AL.AL_LOOPING,AL.AL_FALSE);
        if (type==A3SoundType.BackgroundSound) {
            soundSystem.al.alSourcei(source[0],AL.AL_SOURCE_RELATIVE,AL.AL_TRUE);
            soundSystem.al.alSource3f(source[0],AL.AL_POSITION,0.0f,0.0f,0.0f);
            loc.set(0.0f,0.0f,0.0f);
        }
        if (type==A3SoundType.ConeSound) {
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_INNER_ANGLE,90.0f);
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_OUTER_ANGLE,135.0f);
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_OUTER_GAIN,0.0f);
            soundSystem.al.alSource3f(source[0],AL.AL_DIRECTION,dir.x,dir.y,dir.z);
        }
        check(4);
        //距離による減衰の計算に関係するパラメータ
        //
        //soundSystem.al.alSourcef(source[0],AL.AL_ROLLOFF_FACTOR,0.001f);
        //float f[] = new float[1];
        //soundSystem.al.alGetSourcef(source[0],AL.AL_ROLLOFF_FACTOR,f,0);
        //System.out.println("GAHA1:"+f[0]);
        //
        //soundSystem.al.alSourcef(source[0],AL.AL_REFERENCE_DISTANCE,0.001f);
        //float f[] = new float[1];
        //soundSystem.al.alGetSourcef(source[0],AL.AL_REFERENCE_DISTANCE,f,0);
        //System.out.println("GAHA2:"+f[0]);
        //
        //soundSystem.al.alSourcef(source[0],AL.AL_MAX_DISTANCE,100.0f);
        //float f[] = new float[1];
        //soundSystem.al.alGetSourcef(source[0],AL.AL_MAX_DISTANCE,f,0);
        //System.out.println("GAHA2:"+f[0]);
        //check(100);
    }
    //現在のソースが有効かどうか判定
    boolean alIsSource() {
        //return soundSystem.al.alIsSource(source[0]);
        //本当は上のやつで調べたらいいと思ったんだけど、この関数はたぶん自分の
        //思ってるのとは違う用途で使うのかもしれない
        return sourceInit;
    }
    public void start() {
        if (soundSystem.al==null) return;
        if (!alIsSource())
            initSource();
        soundSystem.al.alSourcePlay(source[0]); check(5);
    }
    public void segno() {
        if (soundSystem.al==null) return;
        if (!alIsSource())
            return;
        soundSystem.al.alSourcef(source[0],AL.AL_SEC_OFFSET,segnoTime);// check(6);
        soundSystem.al.alGetError();//上の行，Linuxだと必ずErrorになるようなので，とりあえず無視してエラーをリセット
        soundSystem.al.alSourcePlay(source[0]); check(7);
    }
    public void stop() {
        if (soundSystem.al==null) return;
        if (!alIsSource())
            return;
        soundSystem.al.alSourceStop(source[0]);
        soundSystem.al.alDeleteSources(1,source,0);
        sourceInit=false;
    }
    public void pause() {
        if (soundSystem.al==null) return;
        if (!alIsSource())
            return;
        soundSystem.al.alSourcePause(source[0]);
    }
    public void rewind() {
        if (soundSystem.al==null) return;
        if (!alIsSource())
            return;
        soundSystem.al.alSourceRewind(source[0]);
    }
    public void setLoc(float x,float y,float z) {
        if (soundSystem.al==null) return;
        if (type!=A3SoundType.BackgroundSound) {
            loc.set(x,y,z);
            if (alIsSource()) {
                Vector3d v = Util.trans(quat,new Vector3d(offset));
                v.add(new Vector3d(loc));
                soundSystem.al.alSource3f(source[0],AL.AL_POSITION,(float)v.x,(float)v.y,(float)v.z);
            }
        }
    }
    public void setLoc(Vector3d v) {
        setLoc((float)v.x,(float)v.y,(float)v.z);
    }
    public void setRot(float x,float y,float z) {
        Quat4d q = Util.euler2quat(x,y,z);
        setQuat(q);
    }
    public void setRot(Vector3d r) {
        Quat4d q = Util.euler2quat(r);
        setQuat(q);
    }
    public void setQuat(Quat4d q) {
        if (soundSystem.al==null) return;
        if (type!=A3SoundType.BackgroundSound) {
            quat.set(q);
            Vector3d v = Util.trans(q,new Vector3d(orgDir));
            dir.set(v);
            if (alIsSource())
                soundSystem.al.alSource3f(source[0],AL.AL_DIRECTION,dir.x,dir.y,dir.z);
        }
    }
    public void setVel() {
    }
    public void setGain(float g) {
        if (soundSystem.al==null) return;
        gain = g;
        if (alIsSource())
            soundSystem.al.alSourcef(source[0],AL.AL_GAIN,g);
    }
    public float getGain() {
        return gain;
    }
    public void setDirection(float x,float y,float z) {
        if (soundSystem.al==null) return;
        orgDir.set(x,y,z);
        Vector3d v = Util.trans(quat,new Vector3d(orgDir));
        dir.set(v);
        if (alIsSource())
            soundSystem.al.alSource3f(source[0],AL.AL_DIRECTION,dir.x,dir.y,dir.z);
    }
    public void setDirection(Vector3d d) {
        setDirection((float)d.x,(float)d.y,(float)d.z);
    }
    public void setOffset(float x,float y,float z) {
        if (soundSystem.al==null) return;
        offset.set(x,y,z);
        if (alIsSource()) {
            Vector3d v = Util.trans(quat,new Vector3d(offset));
            v.add(new Vector3d(loc));
            soundSystem.al.alSource3f(source[0],AL.AL_POSITION,(float)v.x,(float)v.y,(float)v.z);
        }
    }
    public void setOffset(Vector3d o) {
        setOffset((float)o.x,(float)o.y,(float)o.z);
    }
    public void setLoop(boolean l) {
        if (soundSystem.al==null) return;
        loop = l;
        if (alIsSource()) {
            if (loop) {
                soundSystem.al.alSourcei(source[0],AL.AL_LOOPING,AL.AL_TRUE);
            } else {
                soundSystem.al.alSourcei(source[0],AL.AL_LOOPING,AL.AL_FALSE);
            }
        }
    }
    public void cleanUp() {
        if (soundSystem.al==null) return;
        soundSystem.al.alSourceStop(source[0]);
        soundSystem.al.alDeleteSources(1,source,0);
        sourceInit=false;
        //soundSystem.al.alDeleteBuffers(1,buffer,0);
        synchronized (soundSystem.sounds) {
            soundSystem.sounds.remove(this);
        }
    }
    public void setType(A3SoundType t) {
        if (soundSystem.al==null) return;
        type = t;
        if (!alIsSource())
            return;
        if (t==A3SoundType.BackgroundSound) {
            soundSystem.al.alSourcei(source[0],AL.AL_SOURCE_RELATIVE,AL.AL_TRUE);
            soundSystem.al.alSource3f(source[0],AL.AL_POSITION,0.0f,0.0f,0.0f);
        } else if (t==A3SoundType.ConeSound) {
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_INNER_ANGLE,90.0f);
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_OUTER_ANGLE,135.0f);
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_OUTER_GAIN,0.0f);
            soundSystem.al.alSource3f(source[0],AL.AL_DIRECTION,dir.x,dir.y,dir.z);
        } else {
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_INNER_ANGLE,360.0f);
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_OUTER_ANGLE,360.0f);
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_OUTER_GAIN,0.0f);
            //soundSystem.al.alSource3f(source[0],AL.AL_DIRECTION,0.0f,0.0f,0.0f);
        }
    }
    public void setSegno(float s) {
        segnoTime = s;
    }
    public A3Sound copy() {
        ShortSound ret = new ShortSound();
        ret.file = file;
        ret.soundSystem = soundSystem;
        ret.segnoTime = segnoTime;
        ret.type = type;
        ret.gain = gain;
        ret.loop = loop;
        ret.offset.set(offset);
        ret.orgDir.set(orgDir);
        ret.dir.set(dir);
        ret.buffer[0] = buffer[0];
        //soundSystem.al.alGenSources(1,ret.source,0); check(8);
        //soundSystem.al.alSourcei(ret.source[0],AL.AL_BUFFER,ret.buffer[0]);
        //soundSystem.al.alSourcef(ret.source[0],AL.AL_PITCH,1.0f);
        //soundSystem.al.alSourcef(ret.source[0],AL.AL_GAIN,1.0f);
        //soundSystem.al.alSource3f(ret.source[0],AL.AL_POSITION,0.0f,0.0f,0.0f);
        ////al.alSource3i(ret.source[0],AL.AL_VELOCITY,0,0,0);
        ////soundSystem.al.alSourcei(ret.source[0],AL.AL_LOOPING,AL.AL_FALSE);
        check(9);
        return ret;
    }
    void check(int i) {
        if (soundSystem.al==null) return;
        int e = soundSystem.al.alGetError();
        if (e==AL.AL_NO_ERROR)
            return;
        String s = "??????";
        switch(e) {
            case AL.AL_INVALID_NAME: s="AL_INVALID_NAME";break;
            case AL.AL_INVALID_ENUM: s="AL_INVALID_ENUM";break;
            case AL.AL_INVALID_VALUE: s="AL_INVALID_VALUE";break;
            case AL.AL_INVALID_OPERATION: s="AL_INVALID_OPERATION";break;
            case AL.AL_OUT_OF_MEMORY: s="AL_OUT_OF_MEMORY";break;
        }
        System.out.println("----------ShortSound----------");
        System.out.println("OpenAL error raised..."+s+i);
        System.out.println("------------------------------\n");
    }
}
