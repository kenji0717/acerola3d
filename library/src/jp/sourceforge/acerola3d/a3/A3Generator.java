package jp.sourceforge.acerola3d.a3;

import java.lang.reflect.*;

import jp.sourceforge.acerola3d.*;


/**
 * A3Objectを統一的に扱い同様の方法でインスタンスを
 * 生成するために使用するユーティリティクラスです。
 */
public class A3Generator {
    static Class<?> classArg[] = {jp.sourceforge.acerola3d.a3.A3InitData.class};

    /**
     * A3InitDataの情報をもとにA3Objectのインスタンスを生成します。
     * 全てのA3Objectを統一的に扱うシステムを作成する場合に、全てのA3Object
     * を同じ方法でインスタンス化する方法を提供するものです。詳細はA3InitData
     * クラスと、それぞれのA3Objectのコンストラクタを参照して下さい。
     */
    public static A3Object generate(A3InitData d) {
        try {
            A3Object a3 = null;
            Class<?> a3Class = A23.getClassLoader().loadClass(d.getClassName());
            Class<? extends A3Object> cc = a3Class.asSubclass(A3Object.class);
            Constructor<? extends A3Object> con = cc.getConstructor(classArg);
            Object objArg[] = {d};
            a3 = con.newInstance(objArg);
            return a3;
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                ((InvocationTargetException)e).getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
            return null;
        }
    }
}
