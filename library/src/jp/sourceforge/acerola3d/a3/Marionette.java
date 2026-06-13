package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import java.util.*;
import jp.sourceforge.acerola3d.a3.bvh.*;
import javax.vecmath.*;

/**
 * プログラムからキャラクタのモーションを操作するための
 * オブジェクト．
 */
public class Marionette implements Motion {
    String rootBone;
    HashMap<String,BoneData> boneHash = new HashMap<String,BoneData>();
    HashMap<String,Transform3D> transHash = new HashMap<String,Transform3D>();
    class BoneData {
        String parent = null;
        String children[] = null;
        BoneData(String p,String c[]) {
            parent = p;
            children = c.clone();
        }
    }

    /**
     * 空のMarionetteを生成します。
     */
    public Marionette() {
        ;
    }
    /**
     * Motionのt秒時の姿勢を元にMarionetteを生成します。
     */
    public Marionette(Motion m,double t) {
        String rootBone = m.getRootBone();
        this.setRootBone(rootBone);
        copyData(m,t,rootBone);
    }
    void copyData(Motion m,double t,String bone) {
        this.setTransform3D(bone,m.getTransform3D(bone,t));
        String children[] = m.getChildBones(bone);
        this.setChildBones(bone,children);
        for (String b:children) {
            copyData(m,t,b);
        }
    }
    /**
     * BVHのデフォルトのポーズをベースにしてMarionetteの内部データを
     * 構築します。
     */
    public void setBVHDefaultPose(BVH bvh) {
        boneHash.clear();
        transHash.clear();
        String rootBone = bvh.getRootBone();
        this.setRootBone(rootBone);
        copyBVHDefaultPoseData(bvh,rootBone);
    }
    void copyBVHDefaultPoseData(BVH bvh,String bone) {
        Vector3d v = bvh.getOffset(bone);
        Transform3D t = new Transform3D();
        t.set(v);
        this.setTransform3D(bone,t);
        String children[] = bvh.getChildBones(bone);
        this.setChildBones(bone,children);
        for (String b:children) {
            copyBVHDefaultPoseData(bvh,b);
        }
    }
    /**
     * 秒単位でモーションの長さを返す．
     */
    public double getMotionLength() {
        return -1.0;
    }

    /**
     * デフォルトのフレームタイムを返す．
     */
    public double getDefaultFrameTime() {
        return 0.0333;
    }

    /**
     * ルートのbone名を返す．
     */
    public String getRootBone() {
        return rootBone;
    }

    /**
     * 指定されたBoneに接続されている親のBoneの名前を
     * 文字列で返します。
     */
    public String getParentBone(String b) {
        return boneHash.get(b).parent;
    }

    /**
     * 指定したboneの子boneを返す．
     */
    public String[] getChildBones(String boneName) {
        return boneHash.get(boneName).children.clone();
    }

    /**
     * 全てのBoneの名前を返します。
     */
    public String[] getAllBones() {
        return boneHash.keySet().toArray(new String[0]);
    }

    /**
     * 指定された骨と時間における座標変換を返す．
     */
    public Transform3D getTransform3D(String bone,double time) {
        return new Transform3D(transHash.get(bone));
    }

    /**
     * ルートboneを指定する．
     */
    public void setRootBone(String rootBone) {
        this.rootBone = rootBone;
    }

    /**
     * 指定したboneの子boneをセットする．
     */
    public void setChildBones(String parent,String[] children) {
        BoneData bd = new BoneData(parent,children);
        boneHash.put(parent,bd);
    }

    /**
     * 指定された骨における座標変換をセットする．
     */
    public void setTransform3D(String bone,Transform3D trans) {
        //transHash.put(bone,new Transform3D(trans));
        transHash.put(bone,trans);
    }
}
