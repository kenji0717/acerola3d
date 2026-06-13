package jp.sourceforge.acerola3d.a3;

import javax.vecmath.*;

import java.awt.*;
import java.net.*;

class A3Selected extends Component2D {
    A3Object a3;
    Image selectedImage;
    A3Selected(A3Object a3) {
        this.a3 = a3;
        try {
            jp.sourceforge.acerola3d.A23.initA23();
            selectedImage = Toolkit.getDefaultToolkit().createImage(new URL("x-res:///jp/sourceforge/acerola3d/resources/selected.gif"));
        }catch(Exception e) {
            System.out.println("Error in A3Selected");
            e.printStackTrace();
        }
    }
    public void paint(Graphics2D g,A3CanvasInterface canvas) {
        Vector3d v = a3.getLoc();
        Point p = null;
        if (a3.lockedA3==false) {
            p = canvas.virtualCSToCanvas(new Point3d(v));
        } else {
            p = canvas.physicalCSToCanvas(new Point3d(v));
        }
        g.drawImage(selectedImage,p.x-10,p.y-10,null);
    }
    public void calPhysicalZ(A3CanvasInterface canvas) {
        Vector3d v = canvas.virtualCSToPhysicalCS(a3.getLoc());
        z = v.z;
    }
}
