package jp.sourceforge.acerola3d;

import java.util.*;
import java.lang.ref.*;

/**
 * 基本的に辞書(マップ、ハッシュ)ですが、
 * メモリが少なくなると使用頻度とか生成時間で判断して
 * 値のオブジェクトを消して、メモリを再利用します。
 * とうぜん消えたオブジェクトを検索しようとするとnullが
 * 帰ってきます。java.lang.ref.SoftReferenceを使って
 * 実装されています。
 */
public class SoftRefMap<K,V> {
    Hashtable<K,SoftReference<V>> hash = new Hashtable<K,SoftReference<V>>();
    /**
     * キーとオブジェクトを対にしてセットします。
     * @param k キー
     * @param v オブジェクト
     * @return セットしたオブジェクト
     */
    public V put(K k,V v) {
        SoftReference<V> r = new SoftReference<V>(v);
        hash.put(k,r);
        return v;
    }
    /**
     * キーと対になったオブジェクトを取り出します。
     * @param k キー
     * @return kに対応するオブジェクト
     */
    public V get(K k) {
        SoftReference<V> r = hash.get(k);
        if (r==null) {
            return null;
        }
        if (r.get()==null) {
            hash.remove(k);
        }
        return r.get();
    }
    /**
     *  マップからマッピングをすべて削除します。
     */
    public void clear() {
        hash.clear();
    }
    /**
     * キーのマッピングがある場合に、そのマッピングを
     * このマップから削除します 。
     */
    public void remove(K k) {
        hash.remove(k);
    }
}
