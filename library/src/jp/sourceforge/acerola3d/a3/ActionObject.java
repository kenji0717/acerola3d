package jp.sourceforge.acerola3d.a3;

import java.io.*;

/**
 * アクションデータを含む3Dオブジェクトのクラスを一般化して、
 * これらを統一的に利用できるようにするインタフェースです。
 * Acerola3Dパッケージでアクションデータを含む3Dオブジェクト
 * を実際に実装するにはこのインタフェースをimplementsする
 * だけでなく、A3Objectをextendsする必要もあります。
 * 現在はAction3Dクラスしかこのインターフェースを実装して
 * いませんが、他の実装が作られる場合はこのインターフェースを
 * 実装してもらうことで、Action3Dとあわせて共通の方法で扱う
 * ことができるようにします。
 */
public interface ActionObject {
    /**
     * A3UpdateDataの情報をもとに現在の3Dオブジェクトの
     * 状態を更新します。
     *
     * A3UpdateDataにセットするデータは以下のようになります。
     *
     * <table border="1" summary="required data in A3UpdateData">
     * <tr><td>0:Serializable</td><td>ActionNo   </td><td>必須(変更なしの時は-1にする)</td></tr>
     * <tr><td>1:boolean     </td><td>immediately</td><td>必須</td></tr>
     * </table>
     * 
     * ただし、上記以外のデータを2,3…と続けて入力できるようにする
     * ことは可能であるとします。
     */
    public void update(A3UpdateData d);

    /**
     * 指定したアクションナンバーのアクションに切りかえます。
     * 実際には、アクションの切りかえ要求を待ち行列に追加し、
     * 順番がまわってきたら、指定したアクションが実行されます。
     */
    public void change(int newActionNo);

    /**
     * 指定したアクションに切りかえます。実際には、
     * アクションの切りかえ要求を待ち行列に追加し、順番が
     * まわってきたら、指定したアクションが実行されます。
     * 引数がStringであれば、アクション名と見なし、
     * Integerである場合はアクションナンバーであると見なし、
     * アクションを切り替えます。
     */
    public void change(Serializable s);

    /**
     * 指定したアクションナンバーのアクションに即座に
     * 切りかえます。待ち行列に他のアクションの切りかえ要求が
     * 残っていても、それらを無視して指定したアクションに切りかえます。
     */
    public void changeImmediately(int newActionNo);

    /**
     * 指定したアクションに即座に切りかえます。待ち行列に
     * 他のアクションの切りかえ要求が残っていても、それらを
     * 無視して指定したアクションに切りかえます。
     * 引数がStringであれば、アクション名と見なし、
     * Integerである場合はアクションナンバーであると見なし、
     * アクションを切り替えます。
     */
    public void changeImmediately(Serializable s);

    /**
     * このActionObjectに含まれるアクションの個数を返します。
     */
    public int getActionCount();

    /**
     * このActionObjectに含まれる全てのアクション
     * アクション名の配列を返します。
     */
    public String[] getActionNames();

    /**
     * 指定したアクションナンバーのアクションの
     * アクション名を返します。
     */
    public String getActionName(int i);

    /**
     * 指定したアクション名に対応するアクションナンバーを返します。
     */
    public int getActionNo(String an);

    /**
     * 現在設定されているアクションナンバーを返します。
     */
    public int getActionNo();

    /**
     * 現在設定されているアクション名を返します。
     */
    public String getActionName();

    /**
     * 指定したアクションナンバーのアクションの再生
     * 時間を秒数で返します。
     */
    public double getMotionLength(int actionNo);

    /**
     * 指定したアクション名のアクションの再生
     * 時間を秒数で返します。
     */
    public double getMotionLength(String actionName);

    /**
     * プレイモード(再生か停止か)を設定します。
     */
    public void setMode(Motion.Mode m);

    /**
     * 停止している時に、時間を指定してその時の
     * 状態にする。
     */
    public void setPauseTime(double t);

}
