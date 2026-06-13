package jp.sourceforge.acerola3d.a3;

import javax.vecmath.*;
import javax.media.j3d.*;

/**
 * A3パッケージにおける全ての3Dオブジェクトの
 * スーパークラス。A3CanvasやA3Windowにaddすることで、
 * 3Dオブジェクトが表示されるようになります。
 * 全ての3Dオブジェクトで必要となる、移動、回転、拡大縮小
 * などの機能を提供するメソッドが定義されています。
 */
public abstract class A3Object {
    static BoundingSphere bs = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);
//    protected static final Serializable NULL = A23.NULL;

    A3Behavior behavior = null;
    Component2DContainerInterface component2DContainer = null;//このA3Objectがシーングラフに追加されているかどうかの判定にも使うことにする。
    A3Label label = null;
    A3Balloon balloon = null;
    A3Selected selected = null;
    boolean isSelected = false;
    Object userData = null;
    boolean lockedA3 = false;
    boolean pickable = true;
    boolean isEmphasized = false;
    boolean isPolygonized = false;
    /**
     * 座標系の上を指定するための列挙型。
     */
    public enum UpperDirection {Y,Z}
    Vector3d upperVector = new Vector3d(0.0,1.0,0.0);
    /**
     * 吹き出しの位置を指定するための列挙型。
     */
    public enum BalloonDir {RIGHT,LEFT,TOP,BOTTOM}

    /**
     * A3InitDataのデータをもとにしてA3Objectのインスタンスを
     * 生成するコンストラクタ。このコンストラクタは、このクラスを
     * 継承する全てのクラスで用意されることが望まれます。この
     * A3InitDataの中に含めるべきデータについては、それぞれの
     * 継承したクラスのAPIで説明されています。
     */
    public A3Object(A3InitData d) {
        behavior = new A3Behavior(this);
        behavior.setSchedulingBounds(bs);
        setEnableBehavior(d.getEnableBehavior());
        setAutoDirectionControl(d.getAutoDirectionControl());
        if (d.loc!=null)
            setLocImmediately(d.loc);
        if (d.quat!=null)
            setQuatImmediately(d.quat);
        setScaleImmediately(d.scale);
        if (d.label!=null)
            setLabel(d.label);
        if (d.balloon!=null)
            setBalloon(d.balloon);
        upperVector = d.upperVector;
        pickable = d.pickable;
    }

    /**
     * A3Canvasなどにaddされる直前に呼び出されるメソッドで、
     * 各種初期設定を行う。
     * このメソッドを上書きする場合はsuper.init();を
     * 記述してスーパークラスでの処理をする必要がある。
     */
    protected void init() {
        behavior.init();
    }

    /**
     * behaviorの働きのON、OFFを設定します。onにすると
     * 自動補完機能が有効になり、座標の変更などの表示が
     * なめらかに行われるようになります。
     * (さらにAction3DではAutoActionControlにも関係。)
     */
    public final void setEnableBehavior(boolean b) {
        behavior.setEnableBehavior(b);
    }

    /**
     * 補間のスピードを調節するための係数を指定します。1.0から0.0の
     * 間の数値を指定して下さい。小さいほど補間が速くなります。
     * デフォルトは0.1です。
     */
    public void setInterpolateRatio(double ir) {
        behavior.setInterpolateRatio(ir);
    }
    /**
     * 進行方向を自動検出して、常に正面を向くように回転を補正する
     * モードのON、OFFを設定する。
     */
    public final void setAutoDirectionControl(boolean b) {
        behavior.setAutoDirectionControl(b);
    }

    /**
     * これを有効にするとカメラに対して常に正面を向くように
     * 回転を自動的に補正します。
     */
    public final void setBillboardControl(boolean b) {
        behavior.setBillboardControl(b);
    }

    /**
     * このオブジェクトの上方向を設定します。この上方向とは、
     * A3Objectのオートコントロール機能における正面の計算で、
     * 必要になる上方向ベクトルとして使用されます。
     * デフォルトの上方向はY軸の正の方向です。
     */
    public void setUpperVector(Vector3d v) {
        upperVector.set(v);
    }

    /**
     * このオブジェクトの上方向を取得します。この上方向とは、
     * A3Objectのオートコントロール機能における正面の計算で、
     * 必要になる上方向ベクトルとして使用されます。
     * デフォルトの上方向はY軸の正の方向です。
     */
    public Vector3d getUpperVector() {
        return new Vector3d(upperVector);
    }

    /**
     * この3Dオブジェクトが表すJava3DのNodeをセットします。
     * このクラスを拡張して使用する場合に、自分自身の
     * コンストラクタの中から呼び出してJava3DのNodeを
     * 初期化するというのが、このメソッドの主な用途になります。
     */
    protected void setNode(Node n) {
//        if (this.getUpperDirection()==UpperDirection.Z) {
//            Transform3D tt = new Transform3D();
////          tt.set(new Quat4d(1.0,0.0,0.0,0.0));
//            tt.set(new AxisAngle4d(1.0,0.0,0.0,Math.PI/2.0));
//            TransformGroup tg = new TransformGroup(tt);
//            tg.addChild(n);
//            behavior.setNode(tg);
//        } else {
//            behavior.setNode(n);
//        }
        behavior.setNode(n);
    }

    /**
     * このAction3Dオブジェクトの表すJava3DのNode
     * を返します。自前のJava3Dプログラムの中で、アクション
     * データを持つ3Dオブジェクトを使いたい場合には、
     * このメソッドで得られるNodeをJava3Dのシーングラフに
     * 追加して下さい。注意点としてprotected void setNode(Node n)
     * でセットしたNodeではなく、それをラップしたA3BranchGroup
     * というクラスのNodeが帰ります。
     */
    public Node getNode() {
        return behavior.topGroup;
    }

    /**
     * A3UpdateDataの情報をもとに現在の3Dオブジェクトの
     * 状態を更新します。A3UpdateDataの中に
     * 含まれるべきデータについては、A3Objectを継承したクラスの
     * APIで説明されます。A3Objectの実装では、
     * A3UpdateDataの中から位置、回転、拡大率の3つの情報を
     * 取り出し更新する処理がなされます。A3Objectを拡張する場合は
     * このメソッドをオーバーライドして他の必要な処理をする
     * ように拡張すると同時にsuper.update(d)というようにして、
     * A3Objectのupdateメソッドを実行しなければ位置、回転、拡大率の
     * 情報が更新されなくなるので注意。
     */
    public void update(A3UpdateData d) {
        if (d.loc!=null)
            setLoc(d.loc);
        if (d.quat!=null)
            setQuat(d.quat);
        //if (d.scale!=t.getScale())
        setScale(d.scale);
        //if (d.label!=null)
        setLabel(d.label);
        //if (d.balloon!=null)
        setBalloon(d.balloon);
    }

    void setComponent2DContainerInterface(Component2DContainerInterface cci) {
        component2DContainer = cci;
        if (component2DContainer==null)
            return;
        behavior.setA3VirtualUniverse(cci.getA3VirtualUniverse());
        if (this instanceof Action3D)
            ((Action3D)this).actionBehavior.setA3VirtualUniverse(cci.getA3VirtualUniverse());
        if (label!=null)
            component2DContainer.add(label);
        if (balloon!=null)
            component2DContainer.add(balloon);
        if (selected!=null)
            component2DContainer.add(selected);
    }

    final A3BranchGroup getA3BranchGroup() {
        return behavior.topGroup;
    }

    /**
     * 位置、回転、拡大率を同時にセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void move(Vector3d v, Quat4d q, double s) {
        behavior.move(v,q,s);
    }

    /**
     * 位置、回転、拡大率を同時にセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     * 引数rotはx軸、y軸、z軸の回転からなるベクトルです。
     */
    public final void move(Vector3d loc, Vector3d rot, double scale) {
        move(loc,Util.euler2quat(rot),scale);
    }

    /**
     * 位置、回転、拡大率を同時に、即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveImmediately(Vector3d v, Quat4d q, double s) {
        behavior.moveImmediately(v,q,s);
    }

    /**
     * 位置、回転、拡大率を同時に、即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 引数rotはx軸、y軸、z軸の回転からなるベクトルです。
     */
    public final void moveImmediately(Vector3d loc, Vector3d rot, double scale) {
        moveImmediately(loc,Util.euler2quat(rot),scale);
    }

    /**
     * 位置をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setLoc(Vector3d v) {
        behavior.setLoc(v);
    }

    /**
     * 位置をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setLoc(double x, double y, double z) {
        setLoc(new Vector3d(x,y,z));
    }

    /**
     * 引数で指定されたA3Objectと同じ位置に座標をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setLoc(A3Object a) {
        setLoc(a.getLoc());
    }

    /**
     * 位置を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setLocImmediately(Vector3d v) {
        behavior.setLocImmediately(v);
    }

    /**
     * 位置を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setLocImmediately(double x,double y,double z) {
        setLocImmediately(new Vector3d(x,y,z));
    }

    /**
     * 引数で指定されたA3Objectと同じ位置に座標を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setLocImmediately(A3Object a) {
        setLocImmediately(a.getLoc());
    }

    /**
     * 現在位置に引数で与えたベクトルを加えて新しい現在位置をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void addLoc(Vector3d v) {
        behavior.addLoc(v);
    }

    /**
     * 現在位置に引数で与えたベクトルを加えて新しい現在位置をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void addLoc(double x, double y, double z) {
        addLoc(new Vector3d(x,y,z));
    }

    /**
     * 現在位置に引数で与えたベクトルを加えて新しい現在位置を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void addLocImmediately(Vector3d v) {
        behavior.addLocImmediately(v);
    }

    /**
     * 現在位置に引数で与えたベクトルを加えて新しい現在位置を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void addLocImmediately(double x,double y,double z) {
        addLocImmediately(new Vector3d(x,y,z));
    }

    /**
     * 現在位置を取得するためのメソッド。
     */
    public final Vector3d getLoc() {
        return behavior.getLoc();
    }
    
    /**
     * 目標位置を取得するためのメソッド。
     * setEnableBehavior(false)の場合はgetLocと同じだが、
     * setEnableBehavior(true)の場合は目標座標を返す。
     */
    public final Vector3d getTargetLoc() {
        return behavior.getTargetLoc();
    }

    /**
     * 回転をセットするためのメソッド(四元数:Quat4d版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setQuat(Quat4d q) {
        behavior.setQuat(q);
    }

    /**
     * 回転をセットするためのメソッド(四元数:x,y,z,w版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setQuat(double x, double y, double z, double w) {
        setQuat(new Quat4d(x,y,z,w));
    }

    /**
     * 引数で指定されたA3Objectと同じ向きに回転をセットするための
     * メソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setQuat(A3Object a) {
        setQuat(a.getQuat());
    }

    /**
     * 回転を即座にセットするためのメソッド(四元数:Quad4d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setQuatImmediately(Quat4d q) {
        behavior.setQuatImmediately(q);
    }

    /**
     * 回転を即座にセットするためのメソッド(四元数:x,y,z,w版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setQuatImmediately(double x,double y,double z,double w) {
        setQuatImmediately(new Quat4d(x,y,z,w));
    }

    /**
     * 引数で指定されたA3Objectと同じ向きに回転を即座にセットするための
     * メソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setQuatImmediately(A3Object a) {
        setQuatImmediately(a.getQuat());
    }

    /**
     * 現在の回転に引数で与えた回転を合成するメソッド(四元数:Quat4d版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void mulQuat(Quat4d q) {
        behavior.mulQuat(q);
    }

    /**
     * 現在の回転に引数で与えた回転を合成するメソッド(四元数:x,y,z,w版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void mulQuat(double x, double y, double z, double w) {
        mulQuat(new Quat4d(x,y,z,w));
    }

    /**
     * 現在の回転に引数で与えた回転を即座に合成するメソッド(四元数:Quad4d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulQuatImmediately(Quat4d q) {
        behavior.mulQuatImmediately(q);
    }

    /**
     * 現在の回転に引数で与えた回転を即座に合成するメソッド(四元数:x,y,z,w版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulQuatImmediately(double x,double y,double z,double w) {
        setQuatImmediately(new Quat4d(x,y,z,w));
    }

    /**
     * 現在の回転を取得するためのメソッド(四元数:Quad4d版)。
     */
    public final Quat4d getQuat() {
        return behavior.getQuat();
    }

    /**
     * 目標回転を取得するためのメソッド(四元数:Quad4d版)。
     * setEnableBehavior(false)の場合はgetQuatと同じだが、
     * setEnableBehavior(true)の場合は目標回転を返す。
     */
    public final Quat4d getTargetQuat() {
        return behavior.getTargetQuat();
    }

    /**
     * 回転をセットするためのメソッド(オイラー角:Vector3d版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRot(Vector3d rot) {
        setQuat(Util.euler2quat(rot));
    }

    /**
     * 回転をセットするためのメソッド(オイラー角:x,y,z版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRot(double x, double y,double z) {
        setQuat(Util.euler2quat(x,y,z));
    }

    /**
     * 回転を即座にセットするためのメソッド(オイラー角:Vector3d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRotImmediately(Vector3d rot) {
        setQuatImmediately(Util.euler2quat(rot));
    }

    /**
     * 回転を即座にセットするためのメソッド(オイラー角:x,y,z版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRotImmediately(double x,double y,double z) {
        setQuatImmediately(Util.euler2quat(x,y,z));
    }

    /**
     * 現在の回転に引数の回転を合成するためのメソッド(オイラー角:Vector3d版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRot(Vector3d rot) {
        mulQuat(Util.euler2quat(rot));
    }

    /**
     * 現在の回転に引数の回転を合成するためのメソッド(オイラー角:x,y,z版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRot(double x, double y,double z) {
        mulQuat(Util.euler2quat(x,y,z));
    }

    /**
     * 現在の回転に引数の回転を即座に合成するためのメソッド(オイラー角:Vector3d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRotImmediately(Vector3d rot) {
        mulQuatImmediately(Util.euler2quat(rot));
    }

    /**
     * 現在の回転に引数の回転を即座に合成するためのメソッド(オイラー角:x,y,z版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRotImmediately(double x,double y,double z) {
        mulQuatImmediately(Util.euler2quat(x,y,z));
    }

    /**
     * 現在の回転を取得するためのメソッド(オイラー角:Vector3d版)。
     */
    public final Vector3d getRot() {
        return Util.quat2euler(getQuat());
    }

    /**
     * 目標回転を取得するためのメソッド(オイラー角:Vector3d版)。
     * setEnableBehavior(false)の場合はgetRotと同じだが、
     * setEnableBehavior(true)の場合は目標回転を返す。
     */
    public final Vector3d getTargetRot() {
        return Util.quat2euler(getTargetQuat());
    }


    /**
     * 回転をセットするためのメソッド(オイラー角をデグリーで指定:Vector3d版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRev(Vector3d rev) {
        setQuat(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
    }

    /**
     * 回転をセットするためのメソッド(オイラー角をデグリーで指定:x,y,z版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRev(double x, double y,double z) {
        setQuat(Util.euler2quat(x*Math.PI/180.0,y*Math.PI/180.0,z*Math.PI/180.0));
    }

    /**
     * 引数で指定されたA3Objectと同じ向きに
     * 回転をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setRev(A3Object a) {
        setQuat(a.getQuat());
    }

    /**
     * 回転を即座にセットするためのメソッド(オイラー角をデグリーで指定:Vector3d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRevImmediately(Vector3d rev) {
        setQuatImmediately(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
    }

    /**
     * 回転を即座にセットするためのメソッド(オイラー角をデグリーで指定:x,y,z版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRevImmediately(double x,double y,double z) {
        setQuatImmediately(Util.euler2quat(x*Math.PI/180.0,y*Math.PI/180.0,z*Math.PI/180.0));
    }

    /**
     * 引数で指定されたA3Objectと同じ向きに
     * 回転を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setRevImmediately(A3Object a) {
        setQuatImmediately(a.getQuat());
    }

    /**
     * 現在の回転に引数の回転を合成するためのメソッド(オイラー角をデグリーで指定:Vector3d版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRev(Vector3d rev) {
        mulQuat(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
    }

    /**
     * 現在の回転に引数の回転を合成するためのメソッド(オイラー角をデグリーで指定:x,y,z版)。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRev(double x, double y,double z) {
        mulQuat(Util.euler2quat(x*Math.PI/180.0,y*Math.PI/180.0,z*Math.PI/180.0));
    }

    /**
     * 現在の回転に引数の回転を即座に合成するためのメソッド(オイラー角をデグリーで指定:Vector3d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRevImmediately(Vector3d rev) {
        mulQuatImmediately(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
    }

    /**
     * 現在の回転に引数の回転を即座に合成するためのメソッド(オイラー角をデグリーで指定:x,y,z版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRevImmediately(double x,double y,double z) {
        mulQuatImmediately(Util.euler2quat(x*Math.PI/180.0,y*Math.PI/180.0,z*Math.PI/180.0));
    }

    /**
     * 現在の回転を取得するためのメソッド(オイラー角をデグリーで取得:Vector3d版)。
     */
    public final Vector3d getRev() {
        Vector3d rev = Util.quat2euler(getQuat());
        rev.scale(180.0/Math.PI);
        return rev;
    }

    /**
     * 目標回転を取得するためのメソッド(オイラー角をデグリーで取得:Vector3d版)。
     * setEnableBehavior(false)の場合はgetRotと同じだが、
     * setEnableBehavior(true)の場合は目標回転を返す。
     */
    public final Vector3d getTargetRev() {
        Vector3d rev = Util.quat2euler(getTargetQuat());
        rev.scale(180.0/Math.PI);
        return rev;
    }
 
    /**
     * Z軸の正の方向をオブジェクトの正面と見做し、それがlookAtで
     * 指定した座標に向くように回転をセットします。その時の計算に
     * 使用する上方向ベクトルはgetUpperVector()で得られる物を
     * 使用します。
     */
    public final void setLookAtPoint(Vector3d lookAt) {
        behavior.setLookAtPoint(lookAt);
    }

    /**
     * Z軸の正の方向をオブジェクトの正面と見做し、それがlookAtで
     * 指定した座標に向くように回転を即時にセットします。その時の計算に
     * 使用する上方向ベクトルはgetUpperVector()で得られる物を
     * 使用します。
     */
    public final void setLookAtPointImmediately(Vector3d lookAt) {
        behavior.setLookAtPointImmediately(lookAt);
    }

    /**
     * Z軸の正の方向をオブジェクトの正面と見做し、それがx,y,zで
     * 指定した座標に向くように回転をセットします。その時の計算に
     * 使用する上方向ベクトルはgetUpperVector()で得られる物を
     * 使用します。
     */
    public final void setLookAtPoint(double x,double y,double z) {
        Vector3d lookAt = new Vector3d(x,y,z);
        behavior.setLookAtPoint(lookAt);
    }

    /**
     * Z軸の正の方向をオブジェクトの正面と見做し、それがx,y,zで
     * 指定した座標に向くように回転を即時にセットします。その時の計算に
     * 使用する上方向ベクトルはgetUpperVector()で得られる物を
     * 使用します。
     */
    public final void setLookAtPointImmediately(double x,double y,double z) {
        Vector3d lookAt = new Vector3d(x,y,z);
        behavior.setLookAtPointImmediately(lookAt);
    }

    /**
     * Z軸の正の方向をオブジェクトの正面と見做し、それが引数で指定された
     * A3Objectに向くように回転をセットします。その時の計算に
     * 使用する上方向ベクトルはgetUpperVector()で得られる物を
     * 使用します。
     */
    public final void setLookAtPoint(A3Object a) {
        behavior.setLookAtPoint(a.getLoc());
    }

    /**
     * Z軸の正の方向をオブジェクトの正面と見做し、それが引数で指定された
     * A3Objectに向くように回転を即時にセットします。その時の計算に
     * 使用する上方向ベクトルはgetUpperVector()で得られる物を
     * 使用します。
     */
    public final void setLookAtPointImmediately(A3Object a) {
        behavior.setLookAtPointImmediately(a.getLoc());
    }

    /**
     * 拡大率をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setScale(double s) {
        behavior.setScale(s);
    }

    /**
     * 引数で指定されたA3Objectと同じ
     * 拡大率をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setScale(A3Object a) {
        behavior.setScale(a.getScale());
    }

    /**
     * 拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleImmediately(double s) {
        behavior.setScaleImmediately(s);
    }

    /**
     * 引数で指定されたA3Objectと同じ
     * 拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleImmediately(A3Object a) {
        behavior.setScaleImmediately(a.getScale());
    }

    /**
     * 現在の拡大率に引数の数値をかけてセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void mulScale(double s) {
        behavior.mulScale(s);
    }

    /**
     * 現在の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleImmediately(double s) {
        behavior.mulScaleImmediately(s);
    }

    /**
     * X軸方向の拡大率をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setScaleX(double sx) {
        behavior.setScaleX(sx);
    }

    /**
     * Y軸方向の拡大率をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setScaleY(double sy) {
        behavior.setScaleY(sy);
    }

    /**
     * Z軸方向の拡大率をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setScaleZ(double sz) {
        behavior.setScaleZ(sz);
    }

    /**
     * X,Y,Z軸方向の拡大率をセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setScale(Vector3d sv) {
        behavior.setScale(sv);
    }

    /**
     * X軸の拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleXImmediately(double sx) {
        behavior.setScaleXImmediately(sx);
    }

    /**
     * Y軸の拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleYImmediately(double sy) {
        behavior.setScaleYImmediately(sy);
    }

    /**
     * Z軸の拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleZImmediately(double sz) {
        behavior.setScaleZImmediately(sz);
    }

    /**
     * X,Y,Z軸の拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleImmediately(Vector3d sv) {
        behavior.setScaleImmediately(sv);
    }

    /**
     * 現在のX軸の拡大率に引数の数値をかけてセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void mulScaleX(double sx) {
        behavior.mulScaleX(sx);
    }

    /**
     * 現在のY軸の拡大率に引数の数値をかけてセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void mulScaleY(double sy) {
        behavior.mulScaleY(sy);
    }

    /**
     * 現在のZ軸の拡大率に引数の数値をかけてセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void mulScaleZ(double sz) {
        behavior.mulScaleZ(sz);
    }

    /**
     * 現在のX,Y,Z軸の拡大率に引数の数値をかけてセットするためのメソッド。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void mulScale(Vector3d sv) {
        behavior.mulScale(sv);
    }

    /**
     * 現在のX軸の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleXImmediately(double sx) {
        behavior.mulScaleXImmediately(sx);
    }

    /**
     * 現在のY軸の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleYImmediately(double sy) {
        behavior.mulScaleYImmediately(sy);
    }

    /**
     * 現在のZ軸の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleZImmediately(double sz) {
        behavior.mulScaleZImmediately(sz);
    }

    /**
     * 現在のX,Y,Z軸の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleImmediately(Vector3d sv) {
        behavior.mulScaleImmediately(sv);
    }

    /**
     * 現在の拡大率を取得するためのメソッド。もし、X,Y,Z軸の
     * 拡大率が異なる場合には、その平均を返します。
     */
    public final double getScale() {
        return behavior.getScale();
    }

    /**
     * 現在のX軸方向の拡大率を取得するためのメソッド。
     */
    public final double getScaleX() {
        return behavior.getScaleX();
    }

    /**
     * 現在のY軸方向の拡大率を取得するためのメソッド。
     */
    public final double getScaleY() {
        return behavior.getScaleY();
    }

    /**
     * 現在のZ軸方向の拡大率を取得するためのメソッド。
     */
    public final double getScaleZ() {
        return behavior.getScaleZ();
    }

    /**
     * 現在のX,Y,Z軸方向の拡大率を取得するためのメソッド。
     */
    public final Vector3d getScaleV() {
        return behavior.getScaleV();
    }

    /**
     * 目標拡大率を取得するためのメソッド。
     * もし、X,Y,Z軸の目標拡大率が異なる場合には、その平均を返します。
     * setEnableBehavior(false)の場合はgetScaleと同じだが、
     * setEnableBehavior(true)の場合は目標拡大率を返す。
     */
    public final double getTargetScale() {
        return behavior.getTargetScale();
    }

    /**
     * X軸の目標拡大率を取得するためのメソッド。
     * setEnableBehavior(false)の場合はgetScaleXと同じだが、
     * setEnableBehavior(true)の場合は目標拡大率を返す。
     */
    public final double getTargetScaleX() {
        return behavior.getTargetScale();
    }

    /**
     * Y軸の目標拡大率を取得するためのメソッド。
     * setEnableBehavior(false)の場合はgetScaleYと同じだが、
     * setEnableBehavior(true)の場合は目標拡大率を返す。
     */
    public final double getTargetScaleY() {
        return behavior.getTargetScaleY();
    }

    /**
     * Z軸の目標拡大率を取得するためのメソッド。
     * setEnableBehavior(false)の場合はgetScaleZと同じだが、
     * setEnableBehavior(true)の場合は目標拡大率を返す。
     */
    public final double getTargetScaleZ() {
        return behavior.getTargetScaleZ();
    }

    /**
     * X,Y,Z軸の目標拡大率を取得するためのメソッド。
     * setEnableBehavior(false)の場合はgetScaleVと同じだが、
     * setEnableBehavior(true)の場合は目標拡大率を返す。
     */
    public final Vector3d getTargetScaleV() {
        return behavior.getTargetScaleV();
    }

    /**
     * 正面方向にlだけに移動します．
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void moveForward(double l) {
        behavior.moveForward(l);
    }

    /**
     * 正面方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveForwardImmediately(double l) {
        behavior.moveForwardImmediately(l);
    }

    /**
     * 後ろ方向にlだけに移動します．
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void moveBackward(double l) {
        behavior.moveBackward(l);
    }

    /**
     * 後ろ方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveBackwardImmediately(double l) {
        behavior.moveBackwardImmediately(l);
    }

    /**
     * 右方向にlだけに移動します．
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void moveRight(double l) {
        behavior.moveRight(l);
    }

    /**
     * 右方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveRightImmediately(double l) {
        behavior.moveRightImmediately(l);
    }

    /**
     * 左方向にlだけに移動します．
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void moveLeft(double l) {
        behavior.moveLeft(l);
    }

    /**
     * 左方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveLeftImmediately(double l) {
        behavior.moveLeftImmediately(l);
    }

    /**
     * 上方向にlだけに移動します．
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void moveUp(double l) {
        behavior.moveUp(l);
    }

    /**
     * 上方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveUpImmediately(double l) {
        behavior.moveUpImmediately(l);
    }

    /**
     * 下方向にlだけに移動します．
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void moveDown(double l) {
        behavior.moveDown(l);
    }

    /**
     * 下方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveDownImmediately(double l) {
        behavior.moveDownImmediately(l);
    }

    /**
     * 指定した座標の方向に指定距離だけ移動します。
     * 回転はしません。
     */
    public final void moveTo(Vector3d v, double l) {
        behavior.moveTo(v,l);
    }

    /**
     * 指定した座標の方向に指定距離だけ移動します。
     * 回転はしません。
     */
    public final void moveTo(double x, double y, double z, double l) {
        behavior.moveTo(x,y,z,l);
    }

    /**
     * 指定したA3Objectの方向に指定距離だけ移動します。
     * 回転はしません。
     */
    public final void moveTo(A3Object a, double l) {
        behavior.moveTo(a.getLoc(),l);
    }

    /**
     * 指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public final void moveToImmediately(Vector3d v, double l) {
        behavior.moveToImmediately(v,l);
    }

    /**
     * 指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public final void moveToImmediately(double x, double y, double z, double l) {
        behavior.moveToImmediately(x,y,z,l);
    }

    /**
     * 指定したA3Objectの方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public final void moveToImmediately(A3Object a, double l) {
        behavior.moveToImmediately(a.getLoc(),l);
    }

    /**
     * 指定した角度(degree)だけ上の方向に回転します。方向はキャラクタ
     * を基準とした方向です。
     */
    public final void turnUp(double deg) {
        behavior.turnUp(deg);
    }

    /**
     * 指定した角度(degree)だけ上の方向に即時に回転します。方向はキャラクタ
     * を基準とした方向です。
     */
    public final void turnUpNow(double deg) {
        behavior.turnUpNow(deg);
    }

    /**
     * 指定した角度(degree)だけ下の方向に回転します。方向はキャラクタ
     * を基準とした方向です。
     */
    public final void turnDown(double deg) {
        behavior.turnDown(deg);
    }

    /**
     * 指定した角度(degree)だけ下の方向に即時に回転します。方向はキャラクタ
     * を基準とした方向です。
     */
    public final void turnDownNow(double deg) {
        behavior.turnDownNow(deg);
    }

    /**
     * 指定した角度(degree)だけ右の方向に回転します。方向はキャラクタ
     * を基準とした方向です。
     */
    public final void turnRight(double deg) {
        behavior.turnRight(deg);
    }

    /**
     * 指定した角度(degree)だけ右の方向に即時に回転します。方向はキャラクタ
     * を基準とした方向です。
     */
    public final void turnRightNow(double deg) {
        behavior.turnRightNow(deg);
    }

    /**
     * 指定した角度(degree)だけ左の方向に回転します。方向はキャラクタ
     * を基準とした方向です。
     */
    public final void turnLeft(double deg) {
        behavior.turnLeft(deg);
    }

    /**
     * 指定した角度(degree)だけ左の方向に即時に回転します。方向はキャラクタ
     * を基準とした方向です。
     */
    public final void turnLeftNow(double deg) {
        behavior.turnLeftNow(deg);
    }

    /**
     * 指定した角度(degree)だけ右に傾けます。正確にはオブジェクトの前方方向に右ネジを進める時と同じ方向に回転させます。
     */
    public void rollRight(double deg) {
        behavior.rollRight(deg);
    }

    /**
     * 指定した角度(degree)だけ右に即時に傾けます。正確にはオブジェクトの前方方向に右ネジを進める時と同じ方向に回転させます。
     */
    public void rollRightNow(double deg) {
        behavior.rollRightNow(deg);
    }

    /**
     * 指定した角度(degree)だけ左に傾けます。正確にはオブジェクトの前方方向に右ネジを進める時と逆方向に回転させます。
     */
    public void rollLeft(double deg) {
        behavior.rollLeft(deg);
    }

    /**
     * 指定した角度(degree)だけ左に即時に傾けます。正確にはオブジェクトの前方方向に右ネジを進める時と逆方向に回転させます。
     */
    public void rollLeftNow(double deg) {
        behavior.rollLeftNow(deg);
    }

    /**
     * 方向ベクトルのX,Y,Z成分を指定して，そちらを向くように回転します。
     */
    public void turnTo(double dirX, double dirY, double dirZ) {
        behavior.turnTo(dirX,dirY,dirZ);
    }

    /**
     * 方向ベクトルを指定して，そちらを向くように回転します。
     */
    public void turnTo(Vector3d dir) {
        behavior.turnTo(dir);
    }

    /**
     * 方向ベクトルのX,Y,Z成分を指定して，そちらを向くように即時に回転します。
     */
    public void turnToNow(double dirX, double dirY, double dirZ) {
        behavior.turnToNow(dirX,dirY,dirZ);
    }

    /**
     * 方向ベクトルを指定して，そちらを向くように即時に回転します。
     */
    public void turnToNow(Vector3d dir) {
        behavior.turnToNow(dir);
    }

    /**
     * このオブジェクトのローカルな座標系のX軸がグローバルな
     * 仮想空間の座標系ではどちらの方向になるのかを返します。
     */
    public final Vector3d getUnitVecX() {
        return behavior.getUnitVecX();
    }

    /**
     * このオブジェクトのローカルな座標系のY軸がグローバルな
     * 仮想空間の座標系ではどちらの方向になるのかを返します。
     */
    public final Vector3d getUnitVecY() {
        return behavior.getUnitVecY();
    }

    /**
     * このオブジェクトのローカルな座標系のZ軸がグローバルな
     * 仮想空間の座標系ではどちらの方向になるのかを返します。
     */
    public final Vector3d getUnitVecZ() {
        return behavior.getUnitVecZ();
    }

    /**
     * このオブジェクトの座標，回転，拡大縮小率を引数で
     * 指定されたオブジェクトと一致させます。
     * behaviorが有効であれば、補完機能が働き変更はなめらかに
     * 反映されます。behaviorが無効であれば変更は即座に反映されます。
     */
    public final void setLocRevScale(A3Object a) {
        behavior.setLoc(a.getLoc());
        behavior.setQuat(a.getQuat());
        behavior.setScale(a.getScale());
    }

    /**
     * このオブジェクトの座標，回転，拡大縮小率を引数で
     * 指定されたオブジェクトと即座に一致させます。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setLocRevScaleNow(A3Object a) {
        behavior.setLocImmediately(a.getLoc());
        behavior.setQuatImmediately(a.getQuat());
        behavior.setScaleImmediately(a.getScale());
    }

    /**
     * このオブジェクトのリソースを開放します。
     */
    public void dispose() {
        if (component2DContainer!=null) {
            if (label!=null)
                component2DContainer.del(label);
            if (balloon!=null)
                component2DContainer.del(balloon);
            if (selected!=null)
                component2DContainer.del(selected);
            component2DContainer = null;
        }
    }

    /**
     * ラベルをセットします。(デスクトップのアイコンの
     * ファイル名のような感じで)表示されているラベルを
     * 消去したい場合には，引数にnullをわたして下さい．
     */
    public void setLabel(String l) {
        if (label==null)
            label = new A3Label(l,this);
        if (l!=null) {
            label.setString(l);
            if (component2DContainer!=null)
                component2DContainer.add(label);
        } else {
            if (component2DContainer!=null)
                component2DContainer.del(label);
        }
    }

    /**
     * ラベルを表示する場合のオフセットを設定します。
     */
    public void setLabelLoc(double x,double y) {
        if (label==null)
            label = new A3Label("",this);
        label.setOffset(x,y);
    }

    /**
     * ラベルを表示する場合のオフセットのX座標を返します。
     */
    public void getLabelLoc(double ret[]) {
        if (label==null) {
            ret[0]=ret[1]=0.0;
        } else {
            label.getOffset(ret);
        }
    }

    /**
     * 吹き出しをセットします。表示されている吹き出しを
     * 消去したい場合には，引数にnullをわたして下さい．
     */
    public void setBalloon(String s) {
        if (balloon==null)
            balloon = new A3Balloon(s,this);
        if (s!=null) {
            balloon.setString(s);
            if (component2DContainer!=null)
                component2DContainer.add(balloon);
        } else {
            if (component2DContainer!=null)
                component2DContainer.del(balloon);
        }
    }

    /**
     * 吹き出しを表示する場合の向きとオフセットを設定します。
     */
    public void setBalloonLoc(BalloonDir d,double x,double y) {
        if (balloon==null)
            balloon = new A3Balloon("",this);
        balloon.setOffset(d,x,y);
    }

    /**
     * 吹き出しを表示する場合の向きとオフセットの座標を返します。
     */
    public void getBalloonLoc(BalloonDir d,double ret[]) {
        if (balloon==null) {
            ret[0]=ret[1]=0.0;
        } else {
            balloon.getOffset(d,ret);
        }
    }

    /**
     * 吹き出しを表示する場合の向きを設定します。
     */
    public void setBalloonDir(BalloonDir d) {
        if (balloon==null)
            balloon = new A3Balloon("",this);
        balloon.setDir(d);
    }

    /**
     * この3Dオブジェクトが選択されているという目印
     * のON、OFFを設定します。
     */
    public final void setSelected(boolean b) {
        isSelected = b;
        if (selected==null)
            selected =new A3Selected(this);
        if (b) {
            if (component2DContainer!=null)
                component2DContainer.add(selected);
        } else {
            if (component2DContainer!=null)
                component2DContainer.del(selected);
        }
    }

    /**
     * この3Dオブジェクトが選択されているかどうかを
     * 返します。
     */
    public final boolean isSelected() {
        return isSelected;
    }

    /**
     * ユーザデータをセットします。A3Objectは
     * 3Dユーザインタフェースでしかないので、
     * この3Dインタフェースが表す、プログラムやエージェントや
     * データの実態を、このメソッドを使ってセットしておけば、
     * これらの関連性が簡単に示せます。
     */
    public final void setUserData(Object o) {
        userData = o;
    }

    /**
     * ユーザデータを返します。A3Objectは
     * 3Dユーザインタフェースでしかないので、
     * この3Dインタフェースが表す、プログラムやエージェントや
     * データの実態を、このメソッドを使ってセットしておけば、
     * これらの関連性が簡単に示せます。
     */
    public final Object getUserData() {
        return userData;
    }
    /**
     * Behaviorが有効である時A3Objectの現在のスピードを返します。
     * Behaviorが無効である場合は常に0を返します。
     */
    public final double getSpeed() {
        return behavior.getSpeed();
    }
    /**
     * ピッキングできるかどうかを設定するメソッドです。
     * デフォルトはtrueです。
     */
    public void setPickable(boolean b) {
        pickable = b;
    }
    /**
     * ピッキングできるかどうかを調べるメソッドです。
     */
    public boolean getPickable() {
        return pickable;
    }

    /**
     * 3D表示版の選択印に使用される3Dオブジェクトをセットします。
     * ここで指定された3DオブジェクトがconeTree()メソッドで複製され
     * 選択印として使用されます。
     */
    public static void setSelected3DMarker(Node n) {
        A3Behavior.setSelected3DMarker(n);
    }
    /**
     * 3D表示版の選択印の表示・非表示を設定します。
     */
    public void setSelected3D(boolean b) {
        behavior.setSelected3D(b);
    }
    /**
     * 3D表示版の選択印のが表示される状態ならtrueを
     * 非表示ならfalseを返します。
     */
    public boolean isSelected3D() {
        return behavior.isSelected3D();
    }
    /**
     * このA3Objectを強調表示にします。
     */
    public void emphasize() {
        component2DContainer.getA3VirtualUniverse().emphasize(this);
        isEmphasized = true;
        if (isPolygonized) {
            unpolygonize();
            isPolygonized = false;
        }
    }
    /**
     * このA3Objectの強調表示をOFFにします。
     */
    public void unemphasize() {
        component2DContainer.getA3VirtualUniverse().unemphasize(this);
        isEmphasized = false;
    }
    /**
     * このA3Objectが強調表示されているかどうかを返します．
     */
    public boolean isEmphasized() {
        return isEmphasized;
    }
    /**
     * このA3Objectをポリゴン表示にします。
     */
    public void polygonize() {
        component2DContainer.getA3VirtualUniverse().polygonize(this);
        isPolygonized = true;
        if (isEmphasized) {
            unemphasize();
            isEmphasized = false;
        }
    }
    /**
     * このA3Objectをポリゴン表示をOFFにします。
     */
    public void unpolygonize() {
        component2DContainer.getA3VirtualUniverse().unpolygonize(this);
        isPolygonized = false;
    }
    /**
     * このA3Objectがポリゴン表示されているかどうかを返します．
     */
    public boolean isPolygonized() {
        return isPolygonized;
    }
    /**
     * このA3Objectの表示・非表示を切り替えます。
     * ただし、この方法での非表示は表示サイズを0にするだけの
     * 手抜きな実装による物なので、例えば音声や霧やスカイボックス
     * などは非表示にできません。これらを完全に消すには
     * A3CanvasInterface.del(A3Object)を使用して下さ。
     */
    public void setVisible(boolean b) {
        behavior.setVisible(b);
    }
    /**
     * Behaviorのスレッドで実行されるべき処理を
     * 登録するためのメソッドです。
     */
    protected void runInBehavior(Runnable r) {
        behavior.addRunnable(r);
    }

    //################################################################################
    //Now系メソッド．Immediately系メソッドとまったく同じ処理
    //################################################################################

    /**
     * 位置、回転、拡大率を同時に、即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveNow(Vector3d v, Quat4d q, double s) {
        behavior.moveImmediately(v,q,s);
    }

    /**
     * 位置、回転、拡大率を同時に、即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 引数rotはx軸、y軸、z軸の回転からなるベクトルです。
     */
    public final void moveNow(Vector3d loc, Vector3d rot, double scale) {
        moveImmediately(loc,Util.euler2quat(rot),scale);
    }

    /**
     * 位置を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setLocNow(Vector3d v) {
        behavior.setLocImmediately(v);
    }

    /**
     * 位置を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setLocNow(double x,double y,double z) {
        setLocImmediately(new Vector3d(x,y,z));
    }

    /**
     * 引数で指定されたA3Objectと同じ位置に座標を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setLocNow(A3Object a) {
        setLocImmediately(a.getLoc());
    }

    /**
     * 現在位置に引数で与えたベクトルを加えて新しい現在位置を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void addLocNow(Vector3d v) {
        behavior.addLocImmediately(v);
    }

    /**
     * 現在位置に引数で与えたベクトルを加えて新しい現在位置を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void addLocNow(double x,double y,double z) {
        addLocImmediately(new Vector3d(x,y,z));
    }

    /**
     * 回転を即座にセットするためのメソッド(四元数:Quad4d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setQuatNow(Quat4d q) {
        behavior.setQuatImmediately(q);
    }

    /**
     * 回転を即座にセットするためのメソッド(四元数:x,y,z,w版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setQuatNow(double x,double y,double z,double w) {
        setQuatImmediately(new Quat4d(x,y,z,w));
    }

    /**
     * 引数で指定されたA3Objectと同じ向きに回転を即座にセットするための
     * メソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setQuatNow(A3Object a) {
        setQuatImmediately(a.getQuat());
    }

    /**
     * 現在の回転に引数で与えた回転を即座に合成するメソッド(四元数:Quad4d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulQuatNow(Quat4d q) {
        behavior.mulQuatImmediately(q);
    }

    /**
     * 現在の回転に引数で与えた回転を即座に合成するメソッド(四元数:x,y,z,w版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulQuatNow(double x,double y,double z,double w) {
        setQuatImmediately(new Quat4d(x,y,z,w));
    }

    /**
     * 回転を即座にセットするためのメソッド(オイラー角:Vector3d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRotNow(Vector3d rot) {
        setQuatImmediately(Util.euler2quat(rot));
    }

    /**
     * 回転を即座にセットするためのメソッド(オイラー角:x,y,z版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRotNow(double x,double y,double z) {
        setQuatImmediately(Util.euler2quat(x,y,z));
    }

    /**
     * 現在の回転に引数の回転を即座に合成するためのメソッド(オイラー角:Vector3d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRotNow(Vector3d rot) {
        mulQuatImmediately(Util.euler2quat(rot));
    }

    /**
     * 現在の回転に引数の回転を即座に合成するためのメソッド(オイラー角:x,y,z版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRotNow(double x,double y,double z) {
        mulQuatImmediately(Util.euler2quat(x,y,z));
    }

    /**
     * 回転を即座にセットするためのメソッド(オイラー角をデグリーで指定:Vector3d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRevNow(Vector3d rev) {
        setQuatImmediately(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
    }

    /**
     * 回転を即座にセットするためのメソッド(オイラー角をデグリーで指定:x,y,z版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void setRevNow(double x,double y,double z) {
        setQuatImmediately(Util.euler2quat(x*Math.PI/180.0,y*Math.PI/180.0,z*Math.PI/180.0));
    }

    /**
     * 引数で指定されたA3Objectと同じ向きに
     * 回転を即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setRevNow(A3Object a) {
        setQuatImmediately(a.getQuat());
    }

    /**
     * 現在の回転に引数の回転を即座に合成するためのメソッド(オイラー角をデグリーで指定:Vector3d版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRevNow(Vector3d rev) {
        mulQuatImmediately(Util.euler2quat(Math.PI/180.0*rev.x,Math.PI/180.0*rev.y,Math.PI/180.0*rev.z));
    }

    /**
     * 現在の回転に引数の回転を即座に合成するためのメソッド(オイラー角をデグリーで指定:x,y,z版)。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     * 座標変換はZ軸まわりの回転、Y軸まわりの回転、X軸まわりの回転
     * の順番で適用されます。
     */
    public final void mulRevNow(double x,double y,double z) {
        mulQuatImmediately(Util.euler2quat(x*Math.PI/180.0,y*Math.PI/180.0,z*Math.PI/180.0));
    }

    /**
     * Z軸の正の方向をオブジェクトの正面と見做し、それがlookAtで
     * 指定した座標に向くように回転を即時にセットします。その時の計算に
     * 使用する上方向ベクトルはgetUpperVector()で得られる物を
     * 使用します。
     */
    public final void setLookAtPointNow(Vector3d lookAt) {
        behavior.setLookAtPointImmediately(lookAt);
    }

    /**
     * Z軸の正の方向をオブジェクトの正面と見做し、それがx,y,zで
     * 指定した座標に向くように回転を即時にセットします。その時の計算に
     * 使用する上方向ベクトルはgetUpperVector()で得られる物を
     * 使用します。
     */
    public final void setLookAtPointNow(double x,double y,double z) {
        Vector3d lookAt = new Vector3d(x,y,z);
        behavior.setLookAtPointImmediately(lookAt);
    }

    /**
     * Z軸の正の方向をオブジェクトの正面と見做し、それが引数で指定された
     * A3Objectに向くように回転を即時にセットします。その時の計算に
     * 使用する上方向ベクトルはgetUpperVector()で得られる物を
     * 使用します。
     */
    public final void setLookAtPointNow(A3Object a) {
        behavior.setLookAtPointImmediately(a.getLoc());
    }

    /**
     * 拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleNow(double s) {
        behavior.setScaleImmediately(s);
    }

    /**
     * 引数で指定されたA3Objectと同じ
     * 拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleNow(A3Object a) {
        behavior.setScaleImmediately(a.getScale());
    }

    /**
     * 現在の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleNow(double s) {
        behavior.mulScaleImmediately(s);
    }

    /**
     * X軸の拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleXNow(double sx) {
        behavior.setScaleXImmediately(sx);
    }

    /**
     * Y軸の拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleYNow(double sy) {
        behavior.setScaleYImmediately(sy);
    }

    /**
     * Z軸の拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleZNow(double sz) {
        behavior.setScaleZImmediately(sz);
    }

    /**
     * X,Y,Z軸の拡大率を即時にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void setScaleNow(Vector3d sv) {
        behavior.setScaleImmediately(sv);
    }

    /**
     * 現在のX軸の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleXNow(double sx) {
        behavior.mulScaleXImmediately(sx);
    }

    /**
     * 現在のY軸の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleYNow(double sy) {
        behavior.mulScaleYImmediately(sy);
    }

    /**
     * 現在のZ軸の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleZNow(double sz) {
        behavior.mulScaleZImmediately(sz);
    }

    /**
     * 現在のX,Y,Z軸の拡大率に引数の数値をかけて即座にセットするためのメソッド。
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void mulScaleNow(Vector3d sv) {
        behavior.mulScaleImmediately(sv);
    }

    /**
     * 正面方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveForwardNow(double l) {
        behavior.moveForwardImmediately(l);
    }

    /**
     * 後ろ方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveBackwardNow(double l) {
        behavior.moveBackwardImmediately(l);
    }

    /**
     * 右方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveRightNow(double l) {
        behavior.moveRightImmediately(l);
    }

    /**
     * 左方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveLeftNow(double l) {
        behavior.moveLeftImmediately(l);
    }

    /**
     * 上方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveUpNow(double l) {
        behavior.moveUpImmediately(l);
    }

    /**
     * 下方向にlだけに移動します．
     * behaviorが有効であってもなくても、変更が即座に反映されます。
     */
    public final void moveDownNow(double l) {
        behavior.moveDownImmediately(l);
    }

    /**
     * 指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public final void moveToNow(Vector3d v, double l) {
        behavior.moveToImmediately(v,l);
    }

    /**
     * 指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public final void moveToNow(double x, double y, double z, double l) {
        behavior.moveToImmediately(x,y,z,l);
    }

    /**
     * 指定したA3Objectの方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public final void moveToNow(A3Object a, double l) {
        behavior.moveToImmediately(a.getLoc(),l);
    }
}
