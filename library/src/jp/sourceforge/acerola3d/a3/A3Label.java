package jp.sourceforge.acerola3d.a3;

import javax.vecmath.*;

import java.awt.*;

class A3Label extends Component2D {
    A3Object a3;
    String label = "none";
    double offsetX;
    double offsetY;
    int width=0;
    int height=0;
    A3Label(String s,A3Object a3) {
        label = s;
        this.a3 = a3;
    }
    public void paint(Graphics2D g,A3CanvasInterface canvas) {
        Vector3d v = a3.getLoc();
        if (!a3.lockedA3)
            v = canvas.virtualCSToPhysicalCS(v);
        Vector3d vv = new Vector3d(offsetX,offsetY,0.0);
        vv.scale(a3.getScale());
        if (!a3.lockedA3)
            vv.scale(1.0/canvas.getCameraScale());
        Vector3d p3d = new Vector3d(v.x+vv.x,v.y+vv.y,v.z+vv.z);
        Point p = canvas.physicalCSToCanvas(new Point3d(p3d));

        if ((width==0)||(height==0)) {
            FontMetrics fm = g.getFontMetrics();
            width = fm.stringWidth(label)+4;
            height = fm.getHeight()+2;
        }

        g.setColor(Color.WHITE);
        g.drawString(label,p.x-width/2,p.y+height/2);
    }
    public void setString(String s) {
        label = s;
    }
    public void setOffset(double x,double y) {
        offsetX = x;
        offsetY = y;
    }
    public void getOffset(double ret[]) {
        ret[0] = offsetX;
        ret[1] = offsetY;
    }
    public void calPhysicalZ(A3CanvasInterface canvas) {
        Vector3d v = canvas.virtualCSToPhysicalCS(a3.getLoc());
        z = v.z;
    }
}
