package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;

/**
 * Action3Dにおけるキャラクタのモーションデータを保持する
 * オブジェクトです。Motionという名前ですが、動きだけでなく、
 * Boneの名前とBoneの階層構造のデータも含まれます。
 *
 * BVHのようなモーションデータだけでなく
 * プログラムのような物も想定しています。プログラムの場合は
 * getMotionLength()は負の値(-1.0)を返すこととし、
 * getDefaultFrameTimeで得られる値は0.0333ぐらいの適当な
 * 値を返すものとします。
 */
public interface Motion {
    /**
     * Action3Dの再生モードです。
     */
    enum Mode { PLAY, PAUSE }

    /**
     * 秒単位でモーションの長さを返します。
     */
    public double getMotionLength();

    /**
     * デフォルトのフレームタイムを返します。
     */
    public double getDefaultFrameTime();

    /**
     * rootのBoneの名前を返します。
     */
    public String getRootBone();

    /**
     * 指定されたBoneに接続されている親のBoneの名前を
     * 文字列で返します。
     */
    public String getParentBone(String b);

    /**
     * 指定されたBoneに接続されている子のBoneの名前を
     * 文字列の配列で返します。子が無い場合や、指定された
     * Boneが無い場合はnew String[0]を返して下さ。
     */
    public String[] getChildBones(String b);
    /**
     * 全てのBoneの名前を返します。
     */
    public String[] getAllBones();

    /**
     * 指定された骨と時間における座標変換を返します。このメソッドによって
     * 返される座標変換は関節の回転だけでなく、親の骨からのオフセットや
     * 必要であれば拡大率なども含む最終的な合成された座標変換
     * となっている必要がある点に注意して下さい。指定されたboneが
     * 無い場合はnullを返すようにして下さ。
     */
    public Transform3D getTransform3D(String bone,double time);
}
