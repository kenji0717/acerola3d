package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * 仮想空間における霧を表現するためのクラス。
 */
public class A3Fog extends A3Object {
    static BoundingSphere bs = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);
    Fog fog;
    /**
     * デフォルトのパラメータでA3Fogオブジェクトを生成します。
     */
    public A3Fog() {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3Fog"));
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Fog");
        d.set(A3FogType.EXPONENTIAL,1.0f,1.0f,1.0f,1.0f);
        realConstructor(d);
    }
    /**
     * 霧のタイプを指定してA3Fogオブジェクトを生成します。
     */
    public A3Fog(A3FogType t) {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3Fog"));
        A3InitData d = new A3InitData("jp.sourceforge.acerola3d.a3.A3Fog");
        if (t==A3FogType.EXPONENTIAL) {
            d.set(t,1.0f,1.0f,1.0f,1.0f);
        } else if (t==A3FogType.LINEAR) {
            d.set(t,1.0f,1.0f,1.0f,0.0,100.0);
        }
        realConstructor(d);
    }

    /**
     * A3InitDataをもとにA3Fogオブジェクトを生成するコンストラクタ。
     * A3InitDataにセットされるデータは、0番目の霧のタイプによって2パターンある。
     * 
     * 霧のタイプが指数霧の場合
     * <table border="1" summary="required data in A3InitData">
     * <tr><td>0:A3FogType </td><td>EXPONENTIAL   </td><td>必須</td></tr>
     * <tr><td>1:float     </td><td>霧の色(赤成分) </td><td>必須</td></tr>
     * <tr><td>2:float     </td><td>霧の色(緑成分) </td><td>必須</td></tr>
     * <tr><td>3:float     </td><td>霧の色(青成分) </td><td>必須</td></tr>
     * <tr><td>4:float     </td><td>霧の密度       </td><td>必須</td></tr>
     * </table>
     *
     * 霧のタイプが線形霧の場合
     * <table border="1" summary="required data in A3InitData">
     * <tr><td>0:A3FogType </td><td>LINEAR        </td><td>必須</td></tr>
     * <tr><td>1:float     </td><td>霧の色(赤成分) </td><td>必須</td></tr>
     * <tr><td>2:float     </td><td>霧の色(緑成分) </td><td>必須</td></tr>
     * <tr><td>3:float     </td><td>霧の色(青成分) </td><td>必須</td></tr>
     * <tr><td>4:double    </td><td>霧の前方距離   </td><td>必須</td></tr>
     * <tr><td>5:double    </td><td>霧の後方距離   </td><td>必須</td></tr>
     * </table>
     */
    public A3Fog(A3InitData d) {
        super(d);
        realConstructor(d);
    }

    void realConstructor(A3InitData d) {
        A3FogType t = (A3FogType)d.get(0);
        if (t == A3FogType.EXPONENTIAL) {
            float r = d.getFloat(1);
            float g = d.getFloat(2);
            float b = d.getFloat(3);
            float dis = d.getFloat(4);
            fog = new ExponentialFog(r,g,b,dis);
            fog.setCapability(Fog.ALLOW_COLOR_WRITE);
            fog.setCapability(ExponentialFog.ALLOW_DENSITY_WRITE);
        } else if (t == A3FogType.LINEAR) {
            float r = d.getFloat(1);
            float g = d.getFloat(2);
            float b = d.getFloat(3);
            double ft = d.getDouble(4);
            double bk = d.getDouble(5);
            fog = new LinearFog(r,g,b,ft,bk);
            fog.setCapability(Fog.ALLOW_COLOR_WRITE);
            fog.setCapability(LinearFog.ALLOW_DISTANCE_WRITE);
        }
        fog.setInfluencingBounds(bs);
        setNode(fog);
    }

    /**
     * A3UpdateDataの情報をもとに現在の3Dオブジェクトの
     * 状態を更新します。
     *
     * A3UpdateDataにセットするデータは以下のようになります。
     * 
     * 霧のタイプが指数霧の場合
     * <table border="1" summary="required data in A3UpdateData">
     * <tr><td>0:float     </td><td>霧の色(赤成分) </td><td>必須</td></tr>
     * <tr><td>1:float     </td><td>霧の色(緑成分) </td><td>必須</td></tr>
     * <tr><td>2:float     </td><td>霧の色(青成分) </td><td>必須</td></tr>
     * <tr><td>3:float     </td><td>霧の密度       </td><td>必須</td></tr>
     * </table>
     *
     * 霧のタイプが線形霧の場合
     * <table border="1" summary="required data in A3UpdateData">
     * <tr><td>0:float     </td><td>霧の色(赤成分) </td><td>必須</td></tr>
     * <tr><td>1:float     </td><td>霧の色(緑成分) </td><td>必須</td></tr>
     * <tr><td>2:float     </td><td>霧の色(青成分) </td><td>必須</td></tr>
     * <tr><td>3:double    </td><td>霧の前方距離   </td><td>必須</td></tr>
     * <tr><td>4:double    </td><td>霧の後方距離   </td><td>必須</td></tr>
     * </table>
     */
    public void update(A3UpdateData d) {
        super.update(d);
        float r = d.getFloat(0);
        float g = d.getFloat(1);
        float b = d.getFloat(2);
        fog.setColor(r,g,b);
        if (fog instanceof ExponentialFog) {
            float dis = d.getFloat(3);
            ((ExponentialFog)fog).setDensity(dis);
        } else if (fog instanceof LinearFog) {
            double ft = d.getDouble(3);
            double bk = d.getDouble(4);
            ((LinearFog)fog).setFrontDistance(ft);
            ((LinearFog)fog).setBackDistance(bk);
        }
    }

    /**
     * 霧の色をセットします。
     */
    public void setColor(float r,float g,float b) {
        fog.setColor(r,g,b);
    }

    /**
     * 霧の濃度をセットします。
     */
    public void setDensity(float d) {
        ((ExponentialFog)fog).setDensity(d);
    }

    /**
     * 霧の前方距離をセットします。
     */
    public void setFrontDistance(double d) {
        ((LinearFog)fog).setFrontDistance(d);
    }

    /**
     * 霧の後方距離をセットします。
     */
    public void setBackDistance(double d) {
        ((LinearFog)fog).setBackDistance(d);
    }
}
