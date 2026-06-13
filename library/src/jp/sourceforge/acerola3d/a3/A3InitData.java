package jp.sourceforge.acerola3d.a3;

import java.io.*;
import java.net.*;
import javax.vecmath.*;

import jp.sourceforge.acerola3d.A23;

/**
 * A3Objectのコンストラクタに渡すためのデータセットです。
 * A3Objectを統一的に扱うために全てのA3Objectのサブクラスの
 * コンストラクタがこのオブジェクトを受け取りインスタンスを作れる
 * ことが望まれます。(このパッケージで提供されているA3Objectの
 * サブクラスはそのようになっています。)
 * 
 * このA3InitDataにはクラス名、位置座標、回転、拡大率と補完を行うかどうかと
 * 自動回転補正を行うかどうかのデータがデフォルトの
 * 情報としてセットできます。さらに、それぞれのA3Objectのサブクラスを
 * 生成するのに必要なデータをsetメソッドを使って指定してやります。
 * このsetメソッドで指定すべきデータはそれぞれのA3Objectにより
 * 異なり、それぞれのAPIで説明されているはずです。
 */
public class A3InitData implements Serializable {
    private static final long serialVersionUID = 1L;
    String className = null;
    Serializable data[] = null;
    Vector3d loc = null;
    Quat4d quat = null;
    double scale = 1.0;
    boolean isInterpolate = false;
    boolean autoDirectionControl = false;
    String label = null;
    String balloon = null;
    Vector3d upperVector = new Vector3d(0.0,1.0,0.0);
    boolean pickable = true;

    /**
     * A3InitDataのコンストラクタです。生成したいA3Object(のサブクラス)
     * のクラス名を指定して生成します。
     */
    public A3InitData(String className) {
        this.className = className;
        if (A23.getDefaultUpperDirection()==A3Object.UpperDirection.Z) {
            upperVector.set(0.0,0.0,1.0);
        }
    }

    /**
     * クラス名を取り出します。
     */
    public String getClassName() {
        return className;
    }

    /**
     * 位置情報をセットします。
     */
    public void setLoc(double x,double y,double z) {
        this.loc = new Vector3d(x,y,z);
    }

    /**
     * 位置情報をセットします。
     */
    public void setLoc(Vector3d loc) {
        this.loc = loc;
    }

    /**
     * 位置情報を取り出します。
     */
    public Vector3d getLoc() {
        return loc;
    }

    /**
     * 回転情報をセットします。
     */
    public void setQuat(double x,double y,double z,double w) {
        quat = new Quat4d(x,y,z,w);
    }

    /**
     * 回転情報をセットします。
     */
    public void setQuat(Quat4d q) {
        quat = q;
    }

    /**
     * 回転情報を取り出します。
     */
    public Quat4d getQuat() {
        return quat;
    }

    /**
     * 回転情報をセットします。
     */
    public void setRot(double x,double y,double z) {
        quat = Util.euler2quat(x,y,z);
    }

    /**
     * 回転情報をセットします。
     */
    public void setRot(Vector3d rot) {
        quat = Util.euler2quat(rot);
    }

    /**
     * 拡大率をセットします。
     */
    public void setScale(double s) {
        scale = s;
    }

    /**
     * 拡大率を取り出します。
     */
    public double getScale() {
        return scale;
    }

    /**
     * 補完機能のON、OFFを設定します。
     */
    public void setEnableBehavior(boolean b) {
        isInterpolate = b;
    }

    /**
     * 補完機能の情報を取り出します。
     */
    public boolean getEnableBehavior() {
        return isInterpolate;
    }

    /**
     * 自動回転補正機能のON、OFFを設定します。
     */
    public void setAutoDirectionControl(boolean b) {
        autoDirectionControl = b;
    }

    /**
     * 自動回転補正機能の情報を取り出します。
     */
    public boolean getAutoDirectionControl() {
        return autoDirectionControl;
    }

    /**
     * ラベル情報を設定します。
     */
    public void setLabel(String l) {
        label = l;
    }

    /**
     * ラベル情報を取り出します。
     */
    public String getLabel() {
        return label;
    }

    /**
     * 吹き出し情報を設定します。
     */
    public void setBalloon(String b) {
        balloon = b;
    }

    /**
     * 吹き出し情報を取り出します。
     */
    public String getBalloon() {
        return balloon;
    }

    /**
     * このオブジェクトの上方向を設定します。この上方向とは、
     * A3Objectのオートコントロール機能における正面の計算で、
     * 必要になる上方向ベクトルとして使用されます。
     * デフォルトの上方向はY軸の正の方向です。
     */
    public void setUpperVector(Vector3d uv) {
        upperVector = uv;
    }

    /**
     * このオブジェクトの上方向を取得します。この上方向とは、
     * A3Objectのオートコントロール機能における正面の計算で、
     * 必要になる上方向ベクトルとして使用されます。
     * デフォルトの上方向はY軸の正の方向です。
     */
    public Vector3d getUpperVector() {
        return upperVector;
    }
    /**
     * このA3Objectがピッキング可能かどうか指定します。
     * デフォルトはtrueです。
     */
    public void setPickable(boolean b) {
        pickable = b;
    }
    /**
     * このA3Objectがピッキング可能かどうかを取得します。
     */
    public boolean getPickable() {
        return pickable;
    }
    /**
     * デフォルトデータ以外のデータの個数を返します。
     */
    public int getDataCount() {
        return data.length;
    }

    /**
     * コンストラクタで指定したのクラス名のA3Objectを
     * 生成するのに必要なデータをセットします。
     */
    public void set(Serializable... data) {
        this.data = data;
    }

    /**
     * デフォルトデータ以外のデータを取り出します。
     */
    public Serializable[] get() {
        return data;
    }

    /**
     * デフォルトデータ以外のデータのi番目のデータを取り出します。
     */
    public Serializable get(int i) {
        return data[i];
    }

    /**
     * デフォルトデータ以外のデータのi番目のデータを文字列として取り出します。
     */
    public String getString(int i) {
        return (String)data[i];
    }

    /**
     * デフォルトデータ以外のデータのi番目のデータを整数として取り出します。
     */
    public int getInt(int i) {
        return ((Integer)data[i]).intValue();
    }

    /**
     * デフォルトデータ以外のデータのi番目のデータをlongとして取り出します。
     */
    public long getLong(int i) {
        return ((Long)data[i]).longValue();
    }

    /**
     * デフォルトデータ以外のデータのi番目のデータをdoubleとして取り出します。
     */
    public double getDouble(int i) {
        return((Double)data[i]).doubleValue();
    }

    /**
     * デフォルトデータ以外のデータのi番目のデータをfloatとして取り出します。
     */
    public float getFloat(int i) {
        return ((Float)data[i]).floatValue();
    }

    /**
     * デフォルトデータ以外のデータのi番目のデータをbooleanとして取り出します。
     */
    public boolean getBoolean(int i) {
        return ((Boolean)data[i]).booleanValue();
    }

    /**
     * デフォルトデータ以外のデータのi番目のデータをURLとして取り出します。
     */
    public URL getURL(int i) {
        return ((URL)data[i]);
    }
}
