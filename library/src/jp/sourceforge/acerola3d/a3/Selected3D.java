package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.Sphere;
import java.net.URL;
import com.sun.j3d.utils.image.TextureLoader;
import javax.vecmath.*;


class Selected3D extends BranchGroup {
    public Selected3D() {
        TransformGroup tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Sphere sphere = new Sphere(1.0f,Sphere.GENERATE_NORMALS |
            Sphere.GENERATE_TEXTURE_COORDS |
            Sphere.GENERATE_TEXTURE_COORDS_Y_UP, 45);
        Material ma = new Material();
        ma.setSpecularColor(0.0f,0.0f,0.0f);
        //ma.setShininess(64.0f);
        Appearance app = sphere.getAppearance();
        app.setMaterial(ma);
        URL url = null;
        try {
            url = new URL("x-res:///jp/sourceforge/acerola3d/resources/selected3d.png");
        } catch(Exception e) {
        }
        TextureLoader tex = new TextureLoader(url,
            new String("RGBA"),
            TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, null);
        if (tex != null)
            app.setTexture(tex.getTexture());
        TransparencyAttributes ta = new TransparencyAttributes(
            TransparencyAttributes.BLENDED,1.0f);
        app.setTransparencyAttributes(ta);
        tg.addChild(sphere);

        sphere = new Sphere(1.0f,Sphere.GENERATE_NORMALS |
            Sphere.GENERATE_NORMALS_INWARD |
            Sphere.GENERATE_TEXTURE_COORDS |
            Sphere.GENERATE_TEXTURE_COORDS_Y_UP, 45);
        app = sphere.getAppearance();
        app.setMaterial(ma);
        if (tex != null)
            app.setTexture(tex.getTexture());
        app.setTransparencyAttributes(ta);
        tg.addChild(sphere);

        this.addChild(tg);

        Selected3DBehavior sb = new Selected3DBehavior(tg);
        sb.setSchedulingBounds(new BoundingSphere(new Point3d(),Double.MAX_VALUE));
        this.addChild(sb);
    }

    public Node cloneNode(boolean forceDuplication) {
        Selected3D newNode = new Selected3D();
        newNode.duplicateNode(this,forceDuplication);
        return newNode;
    }
  
    public void duplicateNode(Node node, boolean forceDuplication) {
        super.duplicateNode(node,forceDuplication);
    }
  
    public void updateNodeReferences(NodeReferenceTable table) {
        super.updateNodeReferences(table);
    }
}
