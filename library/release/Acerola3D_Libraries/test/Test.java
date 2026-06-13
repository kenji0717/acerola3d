import jp.sourceforge.acerola3d.a3.*;

public class Test {
    public static void main(String args[]) {
        A3Window w = new A3Window(300,300);
        w.setNavigationMode(A3CanvasInterface.NaviMode.SIMPLE,2.0);
        Action3D a3 = new Action3D("x-res:///axis.a3");
        w.add(a3);
    }
}
