package jp.sourceforge.acerola3d.a3;

import javax.vecmath.*;
import java.awt.*;
import java.awt.geom.*;

class A3Balloon extends Component2D {
    A3Object a3;
    String description = "none";
    double rightOffsetX;
    double rightOffsetY;
    double leftOffsetX;
    double leftOffsetY;
    double topOffsetX;
    double topOffsetY;
    double bottomOffsetX;
    double bottomOffsetY;
    int width;
    int height;
    A3Object.BalloonDir direction = A3Object.BalloonDir.RIGHT;

    A3Balloon(String s,A3Object a3) {
        description = s;
        this.a3 = a3;
    }
    public void paint(Graphics2D g,A3CanvasInterface canvas) {
        //Graphicsのtransformを使う方法はJA3Canvasでは問題ないけど、
        //A3Canvasでは、へんにクリッピングされてしまいうまくいかない。
        //AffineTransform saveAT = g.getTransform();

        g.setStroke(new BasicStroke(2.0f));

        Vector3d v = a3.getLoc();
        if (!a3.lockedA3)
            v = canvas.virtualCSToPhysicalCS(v);
        Vector3d vv = null;
        if (direction==A3Object.BalloonDir.RIGHT) {
            vv = new Vector3d(rightOffsetX,rightOffsetY,0.0);
        } else if (direction==A3Object.BalloonDir.LEFT) {
            vv = new Vector3d(leftOffsetX,leftOffsetY,0.0);
        } else if (direction==A3Object.BalloonDir.TOP) {
            vv = new Vector3d(topOffsetX,topOffsetY,0.0);
        } else if (direction==A3Object.BalloonDir.BOTTOM) {
            vv = new Vector3d(bottomOffsetX,bottomOffsetY,0.0);
        }
        vv.scale(a3.getScale());
        if (!a3.lockedA3)
            vv.scale(1.0/canvas.getCameraScale());
        Vector3d p3d = new Vector3d(v.x+vv.x,v.y+vv.y,v.z+vv.z);
        Point p = canvas.physicalCSToCanvas(new Point3d(p3d));

        int i0 = description.length();
        int i1 = i0/10;
        int i2 = i0%10;
        String desc[] = new String[i1+(i2==0?0:1)];
        for (int i=0;i<desc.length;i++) {
            int j0 = i*10;
            int j1 = i*10+10;
            j1 = j1>description.length()?description.length():j1;
            desc[i]=description.substring(j0,j1);
        }
        FontMetrics fm = g.getFontMetrics();
        int ir=i0<10?i0:10;
        width = fm.stringWidth("あ")*ir+20;
        height = fm.getHeight()*desc.length+20;
        //width  = width<50 ?50 :width;
        //height = height<50?50:height;

        int offsetX,offsetY;
        if (direction==A3Object.BalloonDir.RIGHT) {
            offsetX=p.x+10;
            offsetY=p.y-height/2;
        } else if (direction==A3Object.BalloonDir.LEFT) {
            offsetX=p.x-width-10;
            offsetY=p.y-height/2;
        } else if (direction==A3Object.BalloonDir.TOP) {
            offsetX=p.x-width/2;
            offsetY=p.y-height-10;
        } else {//if (direction==A3Object.BalloonDir.BOTTOM) {
            offsetX=p.x-width/2;
            offsetY=p.y+10;
        }
        RoundRectangle2D.Double rec
            = new RoundRectangle2D.Double(offsetX,offsetY,width,height,10.0,10.0);

        g.setColor(Color.white);
        g.fill(rec);
        g.setColor(Color.black);
        g.draw(rec);
        for (int i=0;i<desc.length;i++) {
            g.drawString(desc[i],offsetX+10,offsetY+i*fm.getHeight()+20);
        }

        int x[] = new int[3];
        int y[]= new int[3];
        int l1x,l1y,l2x,l2y;
        if (direction==A3Object.BalloonDir.RIGHT) {
            x[0]=p.x+0;x[1]=p.x+12;x[2]=p.x+12;
            y[0]=p.y+0;y[1]=p.y+ 6;y[2]=p.y- 6;
            l1x=p.x+10;l1y=p.y+5;
            l2x=p.x+10;l2y=p.y-5;
        } else if (direction==A3Object.BalloonDir.LEFT) {
            x[0]=p.x+0;x[1]=p.x-12;x[2]=p.x-12;
            y[0]=p.y+0;y[1]=p.y+ 6;y[2]=p.y- 6;
            l1x=p.x-10;l1y=p.y+5;
            l2x=p.x-10;l2y=p.y-5;
        } else if (direction==A3Object.BalloonDir.TOP) {
            x[0]=p.x+0;x[1]=p.x+ 6;x[2]=p.x- 6;
            y[0]=p.y+0;y[1]=p.y-12;y[2]=p.y-12;
            l1x=p.x+5;l1y=p.y-10;
            l2x=p.x-5;l2y=p.y-10;
        } else {//if (direction==A3Object.BalloonDir.BOTTOM) {
            x[0]=p.x+0;x[1]=p.x+ 6;x[2]=p.x- 6;
            y[0]=p.y+0;y[1]=p.y+12;y[2]=p.y+12;
            l1x=p.x+5;l1y=p.y+10;
            l2x=p.x-5;l2y=p.y+10;
        }
        g.setColor(Color.white);
        g.fillPolygon(x,y,3);
        g.setColor(Color.black);
        g.drawLine(p.x,p.y,l1x,l1y);
        g.drawLine(p.x,p.y,l2x,l2y);
    }
    public void setString(String s) {
        description = s;
    }
    public void setDir(A3Object.BalloonDir d) {
        direction = d;
    }
    public void setOffset(A3Object.BalloonDir d,double x,double y) {
        direction = d;
        if (d==A3Object.BalloonDir.RIGHT) {
            rightOffsetX = x;
            rightOffsetY = y;
        } else if (d==A3Object.BalloonDir.LEFT) {
            leftOffsetX = x;
            leftOffsetY = y;
        } else if (d==A3Object.BalloonDir.TOP) {
            topOffsetX = x;
            topOffsetY = y;
        } else if (d==A3Object.BalloonDir.BOTTOM) {
            bottomOffsetX = x;
            bottomOffsetY = y;
        }
    }
    public void getOffset(A3Object.BalloonDir d,double ret[]) {
        if (d==A3Object.BalloonDir.RIGHT) {
            ret[0] = rightOffsetX;
            ret[1] = rightOffsetY;
        } else if (d==A3Object.BalloonDir.LEFT) {
            ret[0] = leftOffsetX;
            ret[1] = leftOffsetY;
        } else if (d==A3Object.BalloonDir.TOP) {
            ret[0] = topOffsetX;
            ret[1] = topOffsetY;
        } else if (d==A3Object.BalloonDir.BOTTOM) {
            ret[0] = bottomOffsetX;
            ret[1] = bottomOffsetY;
        } else {
            ret[0]=ret[1]=0.0;
        }
    }
    public void calPhysicalZ(A3CanvasInterface canvas) {
        Vector3d v = canvas.virtualCSToPhysicalCS(a3.getLoc());
        z = v.z;
    }
}
