package jp.sourceforge.acerola3d.sound;

import com.jogamp.openal.*;
import javax.vecmath.*;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.nio.ByteBuffer;
import jp.sourceforge.acerola3d.a3.Util;

public class LongSound implements A3Sound {
    static int BUFFER_SIZE = 4096*16;
    static int NUM_BUFFERS = 4;

    String file;
    A3SoundSystem soundSystem;
    float segnoTime;
    A3SoundType type;
    float gain;
    boolean loop=false;
    long sleepTime = 0;
    byte inputBytes[];
    AudioInputStream ais;
    int buffers[] = new int[NUM_BUFFERS];
    int source[] = new int[1];
    boolean sourceInit = false;//source[0]が初期化済みかどうかを記録しておく
    int format;
    int numChannels;
    int rate;
    Vector3f offset = new Vector3f();
    Vector3f loc = new Vector3f();
    Quat4d quat = new Quat4d(0.0,0.0,0.0,1.0);
    Vector3f orgDir = new Vector3f(0.0f,0.0f,1.0f);
    Vector3f dir = new Vector3f(0.0f,0.0f,1.0f);
    boolean stopRequest = false;
    boolean isScheduled = false;

    LongSound() {
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

        initBuffer1();
        initBuffer2();
        //initSource();
    }

    void initBuffer1() throws Exception {
        URL url = new URL(file);
        InputStream is = url.openStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte b[] = new byte[1024];
        int cnt=0;
        while (true) {
            if ((cnt=is.read(b))==-1)
                break;
            baos.write(b,0,cnt);
        }
        inputBytes = baos.toByteArray();
    }
    void initBuffer2() throws Exception {
        if (soundSystem.al==null) return;

        ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);

        ais = AudioSystem.getAudioInputStream(bais);
        AudioFormat audioFormat = ais.getFormat();
        audioFormat = new AudioFormat(
            audioFormat.getSampleRate(),
            16,
            audioFormat.getChannels(),
            true,
            false);
        ais = AudioSystem.getAudioInputStream(audioFormat,ais);
        audioFormat = ais.getFormat();

        numChannels = audioFormat.getChannels();
        int numBytesPerSample = 2;
        if (numChannels==1)
            format = AL.AL_FORMAT_MONO16;
        else
            format = AL.AL_FORMAT_STEREO16;
        rate = (int)audioFormat.getSampleRate();

        sleepTime = (long)(1000.0*BUFFER_SIZE/numBytesPerSample/numChannels/rate/10.0);
        sleepTime = (sleepTime + 10)/10*10;

        soundSystem.al.alGenBuffers(NUM_BUFFERS,buffers,0); check(1);
    }
    void initSource() {
        if (soundSystem.al==null) return;
        soundSystem.al.alGenSources(1,source,0); check(2);
        sourceInit=true;

        soundSystem.al.alSourcef(source[0],AL.AL_PITCH,1.0f);
        soundSystem.al.alSourcef(source[0],AL.AL_GAIN,1.0f);
        Vector3d v = Util.trans(quat,new Vector3d(offset));
        v.add(new Vector3d(loc));
        soundSystem.al.alSource3f(source[0],AL.AL_POSITION,(float)v.x,(float)v.y,(float)v.z);
        //al.alSource3i(source[0],AL.AL_VELOCITY,0,0,0);
        soundSystem.al.alSourcef(source[0],AL.AL_GAIN,gain);
        //ストリーミングの場合LOOPの設定をすると後でINVALID_OPERATION
        //とか言われる．(Linuxだと言われないんだけど．．．)
        //if (loop)
        //    soundSystem.al.alSourcei(source[0],AL.AL_LOOPING,AL.AL_TRUE);
        //else
        //    soundSystem.al.alSourcei(source[0],AL.AL_LOOPING,AL.AL_FALSE);
        if (type==A3SoundType.BackgroundSound) {
            soundSystem.al.alSourcei(source[0],AL.AL_SOURCE_RELATIVE,AL.AL_TRUE);
            soundSystem.al.alSource3f(source[0],AL.AL_POSITION,0.0f,0.0f,0.0f);
            loc.set(0.0f,0.0f,0.0f);
        }
        if (type==A3SoundType.ConeSound) {
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_INNER_ANGLE,90.0f);
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_OUTER_ANGLE,1350.0f);
            soundSystem.al.alSourcef(source[0],AL.AL_CONE_OUTER_GAIN,0.0f);
            soundSystem.al.alSource3f(source[0],AL.AL_DIRECTION,dir.x,dir.y,dir.z);
        }
        check(3);
    }
    //現在のソースが有効かどうか判定
    boolean alIsSource() {
        //return soundSystem.al.alIsSource(source[0]);
        //本当は上のやつで調べたらいいと思ったんだけど、この関数はたぶん自分の
        //思ってるのとは違う用途で使うのかもしれない
        return sourceInit;
    }
    public void start() {
        if (!alIsSource())
            initSource();
        stopRequest = false;
        if (isScheduled==true)
            return;
        Runnable r = new Runnable() {
            public void run() {
                playLoop();
            }
        };
        soundSystem.executor.schedule(r,0l,TimeUnit.MILLISECONDS);
        isScheduled = true;
    }
    public void segno() {
        if (!alIsSource())
            return;
        resetStream();

        int numBytesPerSample = 2;
        int skipBytes = (int)(segnoTime * numBytesPerSample * numChannels * rate);

        int sum = 0;
        try {
            byte dummy[] = new byte[1024];
            while (sum < skipBytes) {
                int i = ais.read(dummy,0,1024);
                if (i<=0)
                    break;
                sum = sum + i;
            }
        } catch (IOException e) {
            System.out.println("???1");
        }
        if (!isScheduled) {
            stopRequest = false;
            Runnable r = new Runnable() {
                public void run() {
                    playLoop();
                }
            };
            soundSystem.executor.schedule(r,0l,TimeUnit.MILLISECONDS);
            isScheduled = true;
        }
    }
    public void stop() {
        if (soundSystem.al==null) return;
        if (!alIsSource())
            return;
        stopRequest = true;
        soundSystem.al.alSourceStop(source[0]);
        soundSystem.al.alSourcei(source[0],AL.AL_BUFFER,AL.AL_NONE);
        soundSystem.al.alDeleteSources(1,source,0);
        sourceInit=false;
        resetStream();
    }
    public void pause() {
        if (soundSystem.al==null) return;
        if (!alIsSource())
            return;
        stopRequest = true;
        soundSystem.al.alSourcePause(source[0]);
    }
    public void rewind() {
        if (soundSystem.al==null) return;
        //とりあえずstop()と同じでいいんじゃね．
        if (!alIsSource())
            return;
        stopRequest = true;
        soundSystem.al.alSourceStop(source[0]);
        soundSystem.al.alSourcei(source[0],AL.AL_BUFFER,AL.AL_NONE);
        soundSystem.al.alDeleteSources(1,source,0);
        sourceInit=false;
        resetStream();
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
        loop = l;
    }
    public void cleanUp() {
        if (soundSystem.al==null) return;
        soundSystem.al.alSourceStop(source[0]);
        empty();
        soundSystem.al.alDeleteSources(1,source,0);
        sourceInit=false;
        soundSystem.al.alDeleteBuffers(1,buffers,0);
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

    //----------------------------------------

    int read(byte[] buffer) throws Exception {
        int bytesRead = 0, cnt = 0;

        while (bytesRead < buffer.length) {
            cnt = ais.read(buffer, bytesRead, buffer.length-bytesRead);
            if (cnt <= 0) {
                if (loop==true) {
                    resetStream();
                    continue;
                } else {
                    break;
                }
            }
            bytesRead += cnt;
        }
        return bytesRead;
    }

    /**
     * Play the stream
     */
    boolean playback() {
        if (playing())
                return true;

        for (int i = 0; i < NUM_BUFFERS; i++) {
                if (!stream(buffers[i]))
                    return false;
        }

        soundSystem.al.alSourceQueueBuffers(source[0], NUM_BUFFERS, buffers, 0);
        soundSystem.al.alSourcePlay(source[0]);
    
        return true;
    }
    /**
     * Check if the source is playing
     */
    boolean playing() {
        int[] state = new int[1];
        soundSystem.al.alGetSourcei(source[0], AL.AL_SOURCE_STATE, state, 0);
        return (state[0] == AL.AL_PLAYING);
    }
    /**
     * Update the stream if necessary
     */
    boolean update() {
        int[] processed = new int[1];
        boolean active = true;
        soundSystem.al.alGetSourcei(source[0], AL.AL_BUFFERS_PROCESSED, processed, 0);
        while (processed[0] > 0) {
            int[] buffer = new int[1];
            soundSystem.al.alSourceUnqueueBuffers(source[0], 1, buffer, 0); check(4);
            active = stream(buffer[0]);
            soundSystem.al.alSourceQueueBuffers(source[0], 1, buffer, 0); check(5);
            processed[0]--;
        }
        return active;
    }
    /**
     * Reloads a buffer (reads in the next chunk)
     */
    boolean stream(int buffer) {
        byte[] pcm = new byte[BUFFER_SIZE];
        int    size = 0;

        try {
            //if ((size = ais.read(pcm)) <= 0)
            if ((size = read(pcm)) <= 0)
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        ByteBuffer data = ByteBuffer.wrap(pcm, 0, size);
        soundSystem.al.alBufferData(buffer, format, data, size, rate);
        check(6);
        
        return true;
    }
    /**
     * Empties the queue
     */
    void empty() {
        int queued[] = new int[1];
        soundSystem.al.alGetSourcei(source[0],AL.AL_BUFFERS_QUEUED,queued,0);
        while (queued[0]>0) {
            int buffer[] = new int[1];
            soundSystem.al.alSourceUnqueueBuffers(source[0],1,buffer,0);
            check(7);
            queued[0]--;
        }
    }

    void playLoop() {
        if (stopRequest) {
            isScheduled = false;
            return;
        }
        if (!playing()) {
            if (!playback()) {
                System.out.println("???2");
                isScheduled = false;
                return;
            }
        } else {
            if (!update()) {
                isScheduled = false;
                return;
            }
        }
        Runnable r = new Runnable() {
            public void run() {
                playLoop();
            }
        };
        soundSystem.executor.schedule(r,sleepTime,TimeUnit.MILLISECONDS);
        isScheduled = true;
    }

    void resetStream() {
        try {
            ais.close();
            ais = null;
            ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
            ais = AudioSystem.getAudioInputStream(bais);
            AudioFormat audioFormat = ais.getFormat();
            audioFormat = new AudioFormat(
                audioFormat.getSampleRate(),
                16,
                audioFormat.getChannels(),
                true,
                false);
            ais = AudioSystem.getAudioInputStream(audioFormat,ais);
        } catch (Exception e) {
            System.out.println("???3");
        }
    }
    //----------------------------------------

    public A3Sound copy() {
        LongSound ret = new LongSound();
        ret.file = file;
        ret.soundSystem = soundSystem;
        ret.segnoTime = segnoTime;
        ret.type = type;
        ret.gain = gain;
        ret.loop = loop;
        ret.sleepTime = sleepTime;
        ret.inputBytes = inputBytes;
        ret.format = format;
        ret.rate = rate;
        ret.offset.set(offset);
        ret.orgDir.set(orgDir);
        ret.dir.set(dir);
        try {
            ret.initBuffer2();
        } catch (Exception e) {
            System.out.println("???4");
        }
        //ret.initSource();
        return ret;
    }
    void check(int i) {
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
        System.out.println("----------LongSound----------");
        System.out.println("OpenAL error raised..."+s+i);
        System.out.println("-----------------------------\n");
    }
}
