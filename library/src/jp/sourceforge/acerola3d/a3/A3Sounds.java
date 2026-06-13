package jp.sourceforge.acerola3d.a3;

import jp.sourceforge.acerola3d.sound.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;

/**
 * 効果音やBGMを3D仮想空間内に配置するためのA3Objectです。
 * このオブジェクトを生成したら、A3Windowなどにaddする前に
 * loadメソッドを使用してサウンドをロードします。名前を付けて
 * 複数のサウンドをロードさせることができます。A3Windowに
 * addして必要に応じて座標をセットし、startメソッドでサウンドを
 * 再生して下さい。
 */
public class A3Sounds extends A3Object {
    HashMap<String,A3Sound> a3sounds = new HashMap<String,A3Sound>();

    /**
     * 3D空間内でサウンドを再生するためのA3Objectを生成します。
     * 生成後サウンドをロードしてからA3CanvasInterfaceにaddして下さ。
     */
    public A3Sounds() {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.Sounds"));
        setNode(new BranchGroup());//ダミー
    }

    /**
     * A3InitDataをもとに3D空間内でサウンドを再生するためのA3Objectを生成します。
     * まだ未実装です。
     */
    public A3Sounds(A3InitData d) {
        super(d);
        setNode(new BranchGroup());//ダミー
    }

    /**
     * サウンドをロードし、引数の情報で初期化します。複数のサウンドがロード可能なので、
     * soundNameで区別します。soundUrlにサウンドファイルのURLを指定して下さ。
     * 引数soundTypeに与えることのできる文字列は"Point","Background","Cone"
     * のどれかになります。gainで音量、loopでループ再生するかどうか指定します。
     * offsetはサウンドの再生場所を指定した座標からずらしたい場合に指定し、
     * dirはConeサウンドの場合の音声の方向を指定します。
     */
    public void load(String soundName,String soundUrl,String soundType,double gain,boolean loop,Vector3d offset,Vector3d dir) throws Exception {
        A3SoundType type = A3SoundType.PointSound;
        if (soundType.equals("Point"))
            type = A3SoundType.PointSound;
        else if (soundType.equals("Background"))
            type = A3SoundType.BackgroundSound;
        else if (soundType.equals("Cone"))
            type = A3SoundType.ConeSound;
        A3Sound s = Action3DData.soundSystem.load(soundUrl,0.0f,type,(float)gain,loop,offset,dir);
        a3sounds.put(soundName,s);
    }

    /**
     * 名前で指定したサウンドを再生します。一時停止したサウンドの
     * 再開にも使用できます。
     */
    public void start(String soundName) {
        A3Sound s = a3sounds.get(soundName);
        if (s!=null)
            s.start();
    }

    /**
     * 名前で指定したサウンドを停止します。
     */
    public void stop(String soundName) {
        A3Sound s = a3sounds.get(soundName);
        if (s!=null)
            s.stop();
    }

    /**
     * 名前で指定したサウンドを一時停止します。
     */
    public void pause(String soundName) {
        A3Sound s = a3sounds.get(soundName);
        if (s!=null)
            s.pause();
    }

    /**
     * 名前で指定したサウンドがConeSoundの場合、
     * その方向を指定します。
     */
    public void setSoundDirection(String soundName,Vector3d d) {
        A3Sound s = a3sounds.get(soundName);
        if (s!=null)
            s.setDirection(d);
    }

    /**
     * 名前で指定したサウンドがConeSoundの場合、
     * その方向を指定します。
     */
    public void setSoundDirection(String soundName,double x,double y,double z) {
        A3Sound s = a3sounds.get(soundName);
        if (s!=null)
            s.setDirection((float)x,(float)y,(float)z);
    }

    /**
     * 名前で指定したサウンドの再生場所をずらすためのオフセットを指定します。
     */
    public void setSoundOffset(String soundName,Vector3d o) {
        A3Sound s = a3sounds.get(soundName);
        if (s!=null)
            s.setOffset(o);
    }

    /**
     * 名前で指定したサウンドの再生場所をずらすためのオフセットを指定します。
     */
    public void setSoundOffset(String soundName,double x,double y,double z) {
        A3Sound s = a3sounds.get(soundName);
        if (s!=null)
            s.setOffset((float)x,(float)y,(float)z);
    }

    /**
     * A3UpdateDataの情報をもとにSoundsの状態を更新します。
     * まだ未実装です。
     */
    @Override
    public void update(A3UpdateData d) {
        super.update(d);
        System.out.println("Sounds.update(). not implemented yet!");
    }

    void setSoundLocQuat(Vector3d v,Quat4d q) {
        for (A3Sound s : a3sounds.values()) {
            s.setLoc(v);
            s.setQuat(q);
        }
    }
    void setSoundTypeLocked() {
        for (A3Sound s : a3sounds.values()) {
            s.setType(A3SoundType.BackgroundSound);
        }
    }
}
