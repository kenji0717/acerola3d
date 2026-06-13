package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import com.sun.j3d.utils.geometry.Box;

/**
 * 3Dのテキストを表示するためのA3Object。
 * デフォルトでテキストの背後にボードを配置して、
 * クリックしやすくしています。
 */
public class A3Text3D extends A3Object {
    String text = "dummy";
    String fontName="Sanserif";
    Font font;
    FontRenderContext frc;
    Text3D text3D;
    Transform3D t;
    TransformGroup tg;
    Align align = Align.CENTER;
    Color3f textColor = new Color3f(0.8f,0.8f,0.8f);
    Material textMat;
    Color3f boardColor = new Color3f(0.0f,0.0f,1.0f);
    Material boardMat;
    boolean boardVisible = true;
    BranchGroup rootGroup;
    BranchGroup boardGroup;

    public static final Align LEFT = Align.LEFT;
    public static final Align CENTER = Align.CENTER;
    public static final Align RIGHT = Align.RIGHT;

    /**
     * テキストの配置を指定するための列挙型
     */
    enum Align {
        /** 左揃え */
        LEFT,
        /** 中央揃え */
        CENTER,
        /** 右揃え */
        RIGHT
    }

    /**
     * テキストを指定してA3Text3Dオブジェクトを生成します。
     */
    public A3Text3D(String s) {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3Text3D"));
        text = s;
        realConstructor();
    }

    /**
     * テキストとフォントを指定してA3Text3Dオブジェクトを生成します。
     */
    public A3Text3D(String text,String fontName) {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3Text3D"));
        this.text = text;
        this.fontName = fontName;
        realConstructor();
    }

    /**
     * テキストとフォントを指定してA3Text3Dオブジェクトを生成します。
     */
    public A3Text3D(String text,Font font) {
        super(new A3InitData("jp.sourceforge.acerola3d.a3.A3Text3D"));
        this.text = text;
        this.font = font;
        realConstructor();
    }

    /**
     * A3InitDataをもとにA3Text3Dオブジェクトを生成します。
     * A3InitDataの引数の数は1か2か3か4か5か6でなければなりません。
     * 3番目の引数の配置は"LEFT"か"CENTER"か"RIGHT"です。
     * フォント名の"Sanserif"です。
     * 配置のデフォルトは"CENTER"です。
     * テキストの色のデフォルトは0.8f,0.8f,0.8fです。
     * ボードの色のデフォルトは0.0f,0.0f,1.0fです。
     * ボードはデフォルトで表示されます。
     * 
     * <table border="1" summary="required data in A3InitData">
     * <tr><td>0:String </td><td>表示するテキスト        </td><td>必須</td></tr>
     * <tr><td>1:String </td><td>フォント名              </td><td>任意</td></tr>
     * <tr><td>2:String </td><td>テキストの配置          </td><td>任意</td></tr>
     * <tr><td>3:Color3f</td><td>テキストの色            </td><td>任意</td></tr>
     * <tr><td>4:Color3f</td><td>ボードの色              </td><td>任意</td></tr>
     * <tr><td>5:boolean</td><td>ボードを表示するかどうか</td><td>任意</td></tr>
     * </table>
     */
    public A3Text3D(A3InitData d) {
        super(d);
        text = d.getString(0);
        if (d.getDataCount()>=2)
            fontName=d.getString(1);
        if (d.getDataCount()>=3) {
            if (d.getString(2).equals("LEFT"))
                align=LEFT;
            else if (d.getString(2).equals("CENTER"))
                align=CENTER;
            else if (d.getString(2).equals("RIGHT"))
                align=RIGHT;
        }
        if (d.getDataCount()>=4)
            textColor=(Color3f)d.get(3);
        if (d.getDataCount()>=5)
            boardColor=(Color3f)d.get(4);
        if (d.getDataCount()>=6)
            boardVisible=d.getBoolean(5);

        realConstructor();
    }

    void realConstructor() {
        if (font==null)
            font = new Font(fontName,Font.PLAIN,1);
        frc = new FontRenderContext(font.getTransform(),false,true);

        Font3D f3d = new Font3D(font,new FontExtrusion());
        text3D = new Text3D(f3d,"dummy",new Point3f());
        text3D.setCapability(Text3D.ALLOW_STRING_WRITE);
        text3D.setCapability(Text3D.ALLOW_POSITION_WRITE);
        Shape3D textShape = new Shape3D();
        textShape.setGeometry(text3D);
        Appearance ap = new Appearance();
        textMat = new Material();
        textMat.setCapability(Material.ALLOW_COMPONENT_WRITE);
        textMat.setSpecularColor(new Color3f(0.0f,0.0f,0.0f));
        ap.setMaterial(textMat);
        textShape.setAppearance(ap);

        ap = new Appearance();
        boardMat = new Material();
        boardMat.setCapability(Material.ALLOW_COMPONENT_WRITE);
        boardMat.setSpecularColor(new Color3f(0.0f,0.0f,0.0f));
        ap.setMaterial(boardMat);
        Box box = new Box();
        box.setAppearance(ap);
        t = new Transform3D();
        tg = new TransformGroup(t);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.addChild(box);
        boardGroup = new BranchGroup();
        boardGroup.setCapability(BranchGroup.ALLOW_DETACH);
        boardGroup.addChild(tg);

        rootGroup = new BranchGroup();
        rootGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
        rootGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        rootGroup.addChild(textShape);
        rootGroup.addChild(boardGroup);

        setAll();

        setNode(rootGroup);
    }

    void setAll() {
        Rectangle2D r = font.getStringBounds(text,frc);
        Point3f p = new Point3f(0.0f,(float)(-r.getHeight()/2.0),0.0f);
        Vector3d v = new Vector3d(0.0,-0.3,0.0);
        if (align==Align.LEFT) {
            p.x = 0.0f;
            v.x = r.getWidth()/2.0;
        } else if (align==Align.CENTER) {
            p.x = (float)(-r.getWidth()/2.0);
            v.x = 0.0;
        } else if (align==Align.RIGHT) {
            p.x = (float)(-r.getWidth());
            v.x = -r.getWidth()/2.0;
        }
        final Vector3d scale = new Vector3d(r.getWidth()/2.0+0.2,r.getHeight()/2.0+0.2,0.1);
        final Point3f pp = new Point3f(p);
        final Vector3d vv = new Vector3d(v);

        /*
        text3D.setPosition(pp);
        text3D.setString(text);
        textMat.setDiffuseColor(textColor);
        boardMat.setDiffuseColor(boardColor);
        t.setTranslation(vv);
        t.setScale(scale);
        tg.setTransform(t);
        if (boardVisible) {
            if (rootGroup.indexOfChild(boardGroup)==-1)
                rootGroup.addChild(boardGroup);
        } else {
            if (rootGroup.indexOfChild(boardGroup)!=-1)
                rootGroup.removeChild(boardGroup);
        }
        */

        
        Runnable run = new Runnable() {
            public void run() {
                text3D.setPosition(pp);
                text3D.setString(text);
                textMat.setDiffuseColor(textColor);
                boardMat.setDiffuseColor(boardColor);
                t.setTranslation(vv);
                t.setScale(scale);
                tg.setTransform(t);
                if (boardVisible) {
                    if (rootGroup.indexOfChild(boardGroup)==-1)
                        rootGroup.addChild(boardGroup);
                } else {
                    if (rootGroup.indexOfChild(boardGroup)!=-1)
                        rootGroup.removeChild(boardGroup);
                }
            }
        };
        runInBehavior(run);
        
    }

    /**
     * テキストを変更します。
     */
    public void setString(String s) {
        text = s;
        setAll();
    }

    /**
     * テキストの配置を変更します。
     * 指定する値はA3Text3D.LEFT,A3Text3D.CENTER,A3Text3D.RIGHTの
     * いずれかです。
     */
    public void setAlign(Align a) {
        align = a;
        setAll();
    }

    /**
     * テキストの色を変更します。
     */
    public void setTextColor(Color3f c) {
        textColor=c;
        setAll();
    }

    /**
     * 背景のボードの色を変更します。
     */
    public void setBoardColor(Color3f c) {
        boardColor=c;
        setAll();
    }

    /**
     * 背景のボードの表示、非表示を指定します。
     */
    public void setBoardVisible(boolean b) {
        boardVisible = b;
        setAll();
    }

    /**
     * A3UpdateDataの情報をもとに現在のA3Text3Dオブジェクトの
     * 状態を更新します。
     * A3UpdateDataの引数の数は1か2か3か4か5でなければなりません。
     * 2番目の引数の配置は"LEFT"か"CENTER"か"RIGHT"です。
     * 
     * <table border="1" summary="required data in A3UpdateData">
     * <tr><td>0:String </td><td>表示するテキスト        </td><td>必須</td></tr>
     * <tr><td>1:String </td><td>テキストの配置          </td><td>任意</td></tr>
     * <tr><td>2:Color3f</td><td>テキストの色            </td><td>任意</td></tr>
     * <tr><td>3:Color3f</td><td>ボードの色              </td><td>任意</td></tr>
     * <tr><td>4:boolean</td><td>ボードを表示するかどうか</td><td>任意</td></tr>
     * </table>
     */
    public void update(A3UpdateData d) {
        super.update(d);
        text = d.getString(0);
        if (d.getDataCount()>=2) {
            if (d.getString(1).equals("LEFT"))
                align=LEFT;
            else if (d.getString(1).equals("CENTER"))
                align=CENTER;
            else if (d.getString(1).equals("RIGHT"))
                align=RIGHT;
        }
        if (d.getDataCount()>=3)
            textColor=(Color3f)d.get(2);
        if (d.getDataCount()>=4)
            boardColor=(Color3f)d.get(3);
        if (d.getDataCount()>=5)
            boardVisible=d.getBoolean(4);

        setAll();
    }
}
