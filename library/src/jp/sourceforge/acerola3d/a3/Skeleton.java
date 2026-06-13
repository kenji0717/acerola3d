package jp.sourceforge.acerola3d.a3;

import javax.vecmath.*;

/**
 * BVHのモーションキャプチャデータを可視化する
 * ためのA3Objectです。
 *
 */
public class Skeleton extends A3Object {
    SkeletonBehavior skeletonBehavior;

    /**
     * URLを指定してSkeletonオブジェクトを生成します。
     */
    public Skeleton(String url) throws Exception {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.Skeleton"));
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.Skeleton");
        d.set(url,true,false);
        realConstructor(d);
    }

    /**
     * A3InitDataをもとにSkeletonオブジェクトを生成するコンストラクタ。
     * A3InitDataの引数の数は、3か4か5でなければなりません。
     * 
     * <table border="1" summary="required data in A3InitData">
     * <tr><td>0:String  </td><td>BVHのURL      </td><td>必須</td></tr>
     * <tr><td>1:boolean </td><td>defaultPose   </td><td>必須</td></tr>
     * <tr><td>2:boolean </td><td>pause         </td><td>必須</td></tr>
     * <tr><td>3:double  </td><td>pauseTime     </td><td>任意</td></tr>
     * <tr><td>4:String[]</td><td>selectedBones </td><td>任意</td></tr>
     * </table>
     */
    public Skeleton(A3InitData d) throws Exception {
        super(d);
        realConstructor(d);
    }
    void realConstructor(A3InitData d) throws Exception {
        jp.sourceforge.acerola3d.A23.initA23();
        String urlString = d.getString(0);
        skeletonBehavior = new SkeletonBehavior(this,urlString);
        setNode(skeletonBehavior.getNode());
        skeletonBehavior.init();
        if (d.getBoolean(1))
            defaultPose();
        else {
            if (d.getBoolean(2))
                pause();
            else
                start();
        }
        if (d.getDataCount()>=4)
            setPauseTime(d.getDouble(3));
        if (d.getDataCount()>=5) {
            String selectedBones[] = (String[])d.get(4);
            for (int i=0;i<selectedBones.length;i++) {
                setBoneSelected(selectedBones[i],true);
            }
        }
    }

    /**
     * モーションの再生ではなく、デザイン用のデフォルトのポーズをとるように指定する。
     */
    public void defaultPose() {
        skeletonBehavior.defaultPose();
    }

    /**
     * モーションの再生をスタートさせる。
     */
    public void start() {
        skeletonBehavior.start();
    }

    /**
     * モーションの再生を一時停止する。
     */
    public void pause() {
        skeletonBehavior.pause();
    }

    /**
     * 一時停止している時に、どの瞬間で停止するかを設定する。
     */
    public void setPauseTime(double t) {
        skeletonBehavior.setPauseTime(t);
    }

    /**
     * 指定した骨の選択・非選択を設定する。選択されている骨は少し明い色で表示される。
     */
    public void setBoneSelected(String boneName,boolean b) {
        skeletonBehavior.setBoneSelected(boneName,b);
    }

    /**
     * ルートの骨の名前を返す。
     */
    public String getRootBone() {
        return skeletonBehavior.getRootBone();
    }

    public String getParentBone(String boneName) {
        return skeletonBehavior.getParentBone(boneName);
    }

    /**
     * 指定された骨の子の骨の名前のリストを返す。
     */
    public String[] getChildBones(String boneName) {
        return skeletonBehavior.getChildBones(boneName);
    }

    /**
     * 指定された骨のキャラクタ座標系におけるオフセットを返す。
     * つまり指定された骨からたどってルートの骨までの全ての
     * オフセットの合計を返す。
     */
    public Vector3d getOffset(String boneName) {
        return skeletonBehavior.getOffset(boneName);
    }

    /**
     * モーションの再生時間を返します。単位は秒です。
     */
    public double getMotionLength() {
        return skeletonBehavior.getMotionLength();
    }

    /**
     * A3UpdateDataの情報をもとにSkeletonオブジェクトの
     * 状態を更新します。
     *
     * A3UpdateDataにセットするデータは以下のようになります。
     *
     * <table border="1" summary="required data in A3UpdateData">
     * <tr><td>0:boolean </td><td>defaultPose     </td><td>必須</td></tr>
     * <tr><td>1:boolean </td><td>pause           </td><td>必須</td></tr>
     * <tr><td>2:double  </td><td>pauseTime       </td><td>任意</td></tr>
     * <tr><td>3:String[]</td><td>selectedBones   </td><td>任意</td></tr>
     * <tr><td>4:String[]</td><td>unSelectedBones </td><td>任意</td></tr>
     * </table>
     */
    public void update(A3UpdateData d) {
        super.update(d);
        if (d.getBoolean(0))
            defaultPose();
        else {
            if (d.getBoolean(1))
                pause();
            else
                start();
        }
        if (d.getDataCount()>=3)
            setPauseTime(d.getDouble(2));
        if (d.getDataCount()>=4) {
            String selectedBones[] = (String[])d.get(3);
            for (int i=0;i<selectedBones.length;i++) {
                setBoneSelected(selectedBones[i],true);
            }
        }
        if (d.getDataCount()>=5) {
            String unSelectedBones[] = (String[])d.get(4);
            for (int i=0;i<unSelectedBones.length;i++) {
                setBoneSelected(unSelectedBones[i],false);
            }
        }
    }
    /**
     * Boneの名前の一覧を返します。
     */
    public String[] getBones() {
        return skeletonBehavior.getAllBones();
    }
    /**
     * 選択(強調表示)されているBoneを全てリセットします。
     */
    public void resetSelected() {
        for (String bn:getBones()) {
            setBoneSelected(bn,false);
        }
    }
}
