package jp.sourceforge.acerola3d.a2;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class A2Canvas extends JPanel implements KeyListener,
        MouseListener, MouseMotionListener, Runnable {
    private static final long serialVersionUID = 1L;

    ArrayList<A2Object> a2Objects = new ArrayList<A2Object>();
    ArrayList<A2Listener> a2Listeners = new ArrayList<A2Listener>();

    public static A2Canvas createA2Canvas() {
        return createA2Canvas(500,500);
    }
    public static A2Canvas createA2Canvas(int w,int h) {
        A2Canvas a = new A2Canvas(w,h);
        Thread t = new Thread(a);
        t.start();
        return a; 
    }
    A2Canvas(int w,int h) {
        setPreferredSize(new Dimension(w,h));
    }

   // A2Objectの追加と削除
    public void add(A2Object a) {
        if (!a2Objects.contains(a))
            a2Objects.add(a);
        a.setA2Canvas(this);
        a.repaint();
    }
    public void del(A2Object a) {
        a2Objects.remove(a);
        a.removeA2Canvas();
        repaint();
    }
    A2Object background = null;
    public void setBackground(A2Object a) {
        background = a;
        repaint();
    }
    // リスナ設定のソッド
    public void addA2Listener(A2Listener l) {
        if (!a2Listeners.contains(l))
            a2Listeners.add(l);
    }
    public void removeA2Listener(A2Listener l) {
        a2Listeners.remove(l);
    }

    // cameraの手動操作のための変数とメソッド
    double cameraX = 0.0;
    double cameraY = 0.0;
    double cameraS = 1.0;
    double defaultX = 0.0;
    double defaultY = 0.0;
    double defaultS = 1.0;
    public void setDefaultCamera(double x,double y,double s) {
        defaultX = x;
        defaultY = y;
        defaultS = s;
    }
    public void resetCamera() {
        cameraX = defaultX;
        cameraY = defaultY;
        cameraS = defaultS;
        repaint();
    }
    public void setCameraLoc(double x,double y) {
        cameraX = x;
        cameraY = y;
        repaint();
    }
    public void setCameraLocImmediately(double x,double y) {
        cameraX = x;
        cameraY = y;
        repaint();
    }
    public void setCameraScale(double s) {
        cameraS = s;
        repaint();
    }
    public void setCameraScaleImmediately(double s) {
        cameraS = s;
        repaint();
    }
    /*
    public void update(Graphics g) {
        paint(g);
    }*/
    public void paint(Graphics g) {
        super.paint(g); // 最終的にはとれよ
        if (background!=null)
            background.paint(g);
        for (A2Object a2 : a2Objects) {
            a2.paint(g);
        }
    }

    // A2Eventリスナ
    public void keyPressed(KeyEvent ke) {;}
    public void keyReleased(KeyEvent ke) {;}
    public void keyTyped(KeyEvent ke) {
        for (A2Listener l : a2Listeners) {
            A2Event e = new A2Event();
            e.setKeyEvent(ke);
            l.keyTyped(e);
        }
    }
    public void mouseClicked(MouseEvent me) {
        for (A2Listener l : a2Listeners) {
            A2Event e = new A2Event();
            e.setMouseEvent(me);
            l.mouseClicked(e);
        }
    }
    public void mouseEntered(MouseEvent me) {;}
    public void mouseExited(MouseEvent me) {;}
    public void mousePressed(MouseEvent me) {
        for (A2Listener l : a2Listeners) {
            A2Event e = new A2Event();
            e.setMouseEvent(me);
            l.mousePressed(e);
        }
    }
    public void mouseReleased(MouseEvent me) {
        for (A2Listener l : a2Listeners) {
            A2Event e = new A2Event();
            e.setMouseEvent(me);
            l.mouseReleased(e);
        }
    }
    public void mouseDragged(MouseEvent me) {
        for (A2Listener l : a2Listeners) {
            A2Event e = new A2Event();
            e.setMouseEvent(me);
            l.mouseDragged(e);
        }
    }
    public void mouseMoved(MouseEvent me) {;}

//  ----------座標変換とピッキングのためのメソッド---------
    public Point canvasToVirtualCS(int x,int y) {
        return null;
    }
    public A2Object pickA2(int x,int y) {
        return null;
    }
//  ----------おまけ機能---------
    public void saveImage(File file) throws IOException {
        int w = getWidth();
        int h = getHeight();
        BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        paint(g);
        ImageIO.write(bi,"png",file);
    }
//  ----------テスト---------
    public boolean imageUpdate(Image img,int infoflags,int x,int y,
            int width,int height) {
        //System.out.println("A2Canvas.imageUpdate()");
        return super.imageUpdate(img,infoflags,x,y,width,height);
    }
    public void run() {
        while (true) {
            boolean b = false;
            for (A2Object a : a2Objects) {
                if (a.needRepaint==true) {
                    b = true;
                    break;
                }
            }
            if (b) {
                repaint();
            }
            try{Thread.sleep(100);}catch(Exception e){;}
        }
    }
}
