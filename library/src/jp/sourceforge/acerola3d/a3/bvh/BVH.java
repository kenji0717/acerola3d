package jp.sourceforge.acerola3d.a3.bvh;

import java.net.*;
import java.util.ArrayList;

import javax.media.j3d.*;
import javax.vecmath.Vector3d;

import jp.sourceforge.acerola3d.a3.*;

/**
 * BVH(BioVision Hierarchical data)のモーションキャプチャデータから
 * 生成されるAction3DのためのMotionデータです。
 */
public class BVH implements Motion {
    ParserBVH parser;

    /**
     * URLで指定したファイルからBVHのインスタンスを生成するコンストラクタ。
     */
    public BVH(URL url) throws Exception {
        parser = new ParserBVH(url);
    }

    /**
     * URLの文字列で指定したファイルからBVHのインスタンスを生成するコンストラクタ。
     */
    public BVH(String url) throws Exception {
        parser = new ParserBVH(new URL(url));
    }

    /**
     * 秒単位でモーションの長さを返します。
     */
    public double getMotionLength() {
        return parser.getMotionLength();
    }

    /**
     * デフォルトのフレームタイムを返します。
     */
    public double getDefaultFrameTime() {
        return parser.getDefaultFrameTime();
    }

    /**
     * rootのBoneの名前を返します。
     */
    public String getRootBone() {
        return parser.getRootBone();
    }
    /**
     * 指定されたBoneに接続されている親のBoneの名前を
     * 文字列で返します。
     */
    public String getParentBone(String b) {
        return parser.getParentBone(b);
    }
    /**
     * 指定されたBoneに接続されている子のBoneの名前を
     * 文字列の配列で返します。
     */
    public String[] getChildBones(String b) {
        return parser.getChildBones(b);
    }
    /**
     * 全てのBoneの名前を返します。
     */
    public String[] getAllBones() {
        return parser.getAllBones();
    }

    /**
     * 指定された骨と時間における座標変換を返します。
     */
    public Transform3D getTransform3D(String bone,double time) {
        return parser.getTransform3D(bone,time);
    }

    /**
     * 指定された骨で設定されているHIERARCHYパートのオフセットを返す。
     * モーションが付いていないデフォルトのポーズを構成するために使用する。
     */
    public Vector3d getOffset(String b) {
        return parser.getOffset(b);
    }

    /**
     * 指定された骨の子供の骨で設定されているHIERARCHYパートのオフセットの一覧を返す。
     * つまりは、この骨を描画する時に描画すべき線のtailの座標を返す。headは原点の
     * 座標になる。普通に骨が繋っている場合はここで返される複数の座標は同じ値に
     * なるが、子供の骨がこの骨と離れている場合は、それぞれの子供の骨のheadに
     * あたる座標がtailとして返されるということになる。
     */
    public ArrayList<Vector3d> getBoneTails(String b) {
        return parser.getBoneTails(b);
    }
}
