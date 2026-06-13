package jp.sourceforge.acerola3d.a3;
import java.util.Comparator;
//降順でソートさせたいんだけど、Arraysには降順のソートが無いみたいなので、
//このComparatorでは値を逆に返す。
class Component2DComparator implements Comparator<Component2D> {
    static Component2DComparator comparator = new Component2DComparator();
    public int compare(Component2D c1,Component2D c2){
        if (c1.z<c2.z)
            return -1;
        else if (c1.z>c2.z)
            return 1;
        else
            return 0;
    }
}
