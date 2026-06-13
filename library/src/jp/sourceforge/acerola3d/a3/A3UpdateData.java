package jp.sourceforge.acerola3d.a3;

import java.io.Serializable;
import java.net.URL;
import javax.vecmath.*;

/**
 * A3Objectのupdateメソッドに渡すためのデータセットです。
 * A3Objectを統一的に扱うために全てのA3Objectのサブクラスの
 * updateメソッドがこのオブジェクトを受け取り状態を更新できる
 * ことが望まれます。(このパッケージで提供されているA3Objectの
 * サブクラスはそのようになっています。)
 * 
 * このA3UpdateDataには位置座標、回転、拡大率がデフォルトの
 * データとしてセットできます。さらに、それぞれのA3Objectのサブクラスを
 * 更新するのに必要なデータをsetメソッドを使って指定してやります。
 * このsetメソッドで指定すべきデータはそれぞれのA3Objectにより
 * 異なり、それぞれのAPIで説明されているはずです。
 */
public class A3UpdateData implements Serializable {
    private static final long serialVersionUID = 1L;
    Serializable data[] = null;
    Vector3d loc = null;
    Quat4d quat = null;
    double scale = 1.0;
    String label = null;
    String balloon = null;

    /**
     * A3UpdateDataのコンストラクタです。
     */
    public A3UpdateData() {
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
     * ラベル情報をセットします。
     */
    public void setLable(String l) {
        label = l;
    }

    /**
     * ラベル情報を取り出します。
     */
    public String getLabel() {
        return label;
    }

    /**
     * 吹き出し情報をセットします。
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
     * デフォルトデータ以外のデータの個数を返します。
     */
    public int getDataCount() {
        return data.length;
    }

    /**
     * 目的のA3Objectの更新に必要なデータをセットします。
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
