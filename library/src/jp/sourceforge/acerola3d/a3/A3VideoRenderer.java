package jp.sourceforge.acerola3d.a3;
//このコードはSUNのサイトにあるJMFのサンプルのJ3DRenderer.javaを
//ベースに少し手を加えた物です．
import javax.media.*;
import javax.media.renderer.VideoRenderer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.format.RGBFormat;
import java.awt.*;
import java.awt.image.*;

import javax.media.j3d.*;
import javax.vecmath.*;

class A3VideoRenderer implements VideoRenderer {
    
    /*************************************************************************
     * Variables and Constants
     *************************************************************************/
    
    // The descriptive name of this renderer
    public static final int SS = 128;
    public static final int IW = 320;
    public static final int IH = 240;
    
    private static final String name = "A3VideoRenderer";
    
    protected RGBFormat inputFormat;
    protected RGBFormat supportedRGB;
    protected Format [] supportedFormats;
    
    protected Buffer    lastBuffer = null;
    
    protected int       inWidth = 0;
    protected int       inHeight = 0;
    protected Component component = null;
    protected Rectangle reqBounds = null;
    protected Rectangle bounds = new Rectangle();
    protected boolean started = false;
    protected Object lastData = null;

    BranchGroup scene = null;
    Appearance app = null;
    Texture2D tex = null;
    int count = 0;
    boolean firstFrame;
    int btype = 0;
    int itype = 0;
    int ttype = 0;
    boolean byRef = true;
    
    /*************************************************************************
     * Constructor
     *************************************************************************/
    
    public A3VideoRenderer() {
        // Prepare supported input formats and preferred format
        
        supportedRGB =  new RGBFormat(null,
                                      Format.NOT_SPECIFIED,
                                      Format.byteArray,
                                      Format.NOT_SPECIFIED,
                                      24,
                                      3, 2, 1,
                                      3, Format.NOT_SPECIFIED,
                                      Format.TRUE,
                                      Format.NOT_SPECIFIED);
        
        supportedFormats = new VideoFormat[] {supportedRGB };
        firstFrame = true;
        
        String os = System.getProperty("os.name");
        System.out.println("running on " + os);
        if ( os.startsWith("W") || os.startsWith("w")) {
            btype = BufferedImage.TYPE_3BYTE_BGR;
            itype = ImageComponent.FORMAT_RGB;
            ttype = Texture.RGB;
            byRef = true;
        }else if (os.startsWith("S") || os.startsWith("s")){
            btype = BufferedImage.TYPE_4BYTE_ABGR;
            itype = ImageComponent.FORMAT_RGBA;
            ttype = Texture.RGBA;
            byRef = true;
            
        } else {
            btype = BufferedImage.TYPE_3BYTE_BGR;
            itype = ImageComponent.FORMAT_RGB;
            ttype = Texture.RGB;
            byRef = false;
        }

    }

    /****************************************************************
     * Controls implementation
     ****************************************************************/
    
    /**
     * Returns an array of supported controls
     **/
    public Object[] getControls() {
        // No controls
        return (Object[]) new Control[0];
    }

    /**
     * Return the control based on a control type for the PlugIn.
     */
    public Object getControl(String controlType) {
       try {
          Class<?>  cls = Class.forName(controlType);
          Object cs[] = getControls();
          for (int i = 0; i < cs.length; i++) {
             if (cls.isInstance(cs[i]))
                return cs[i];
          }
          return null;
       } catch (Exception e) {   // no such controlType or such control
         return null;
       }
    }

    /*************************************************************************
     * PlugIn implementation
     *************************************************************************/

    public String getName() {
        return name;
    }
    
    /**
     * Opens the plugin
     */
    public void open() throws ResourceUnavailableException {
        firstFrame = true;

        scene = createSceneGraph();

        count = 0;
        // System.out.println("end of open");

    }

    /**
     * Resets the state of the plug-in. Typically at end of media or when media
     * is repositioned.
     */
    public void reset() {
        // Nothing to do
    }

    public synchronized void close() {

    }

    /*************************************************************************
     * Renderer implementation
     *************************************************************************/

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }
    
    /**
     * Lists the possible input formats supported by this plug-in.
     */
    public Format [] getSupportedInputFormats() {
        return supportedFormats;
    }

    /**
     * Set the data input format.
     */
    public Format setInputFormat(Format format) {
        if ( format != null && format instanceof RGBFormat &&
             format.matches(supportedRGB)) {
            
            inputFormat = (RGBFormat) format;
            Dimension size = inputFormat.getSize();
            inWidth = size.width;
            inHeight = size.height;
            // System.out.println("in setInputFormat = " + format);
            return format;
        } else
            return null;
    }

    /**
     * Processes the data and renders it to a component
     */

    public int process(Buffer buffer) {
        
        if ( buffer.getLength() <= 0 ) 
            return BUFFER_PROCESSED_OK;

        if ( count < 0 ) {
            count ++;
            try {
                //Thread.currentThread().sleep(50);
                Thread.sleep(50);
            } catch ( Exception ex) {
                ex.printStackTrace();
            }

            return BUFFER_PROCESSED_OK;
        }

        count = 0;

        byte[] rawData =(byte[])(buffer.getData());

        // System.out.println("inWidth = " + inWidth);
        // System.out.println("inHeight = " + inHeight);

        BufferedImage bimg = new BufferedImage(SS,SS, btype);
        byte[] byteData = ((DataBufferByte)bimg.getRaster().getDataBuffer()).getData();

        int op, ip, x, y;
        byte alpha_1 = (byte)0xff;
        op = 0;
        int lineStride = 3 * inWidth;

        // scale from inWidth x inHeight to SS x SS
        if ( btype == BufferedImage.TYPE_3BYTE_BGR) {
            for ( int i = 0; i < SS; i++ ) 
                for ( int j = 0; j < SS; j++) {
                    x = (inWidth*j) >> 7;
                    y = (inHeight*i) >> 7;
                    
                    if ( x >= inWidth || y >= inHeight ) {
                        byteData[op++]  = 0;
                        byteData[op++]  = 0;
                        byteData[op++]  = 0;
                    } else {
                        ip = y*lineStride + x*3;
                        byteData[op++] = rawData[ip++];
                        byteData[op++] = rawData[ip++];
                        byteData[op++] = rawData[ip++];
                    }
                }
        } else { // in 4BYTE_ABGR format
            for ( int i = 0; i < SS; i++ ) 
                for ( int j = 0; j < SS; j++) {
                    x = (inWidth*j) >> 7;
                    y = (inHeight*i) >> 7;
                    
                    if ( x >= inWidth || y >= inHeight ) {
                        byteData[op++] = alpha_1;
                        byteData[op++]  = 0;
                        byteData[op++]  = 0;
                        byteData[op++]  = 0;
                    } else {
                        ip = y*lineStride + x*3;
                        byteData[op++] = alpha_1;
                        byteData[op++] = rawData[ip++];
                        byteData[op++] = rawData[ip++];
                        byteData[op++] = rawData[ip++];
                    }
                }
        }

        ImageComponent2D imgcmp = new ImageComponent2D(itype, bimg, byRef, true);
        tex.setImage(0, imgcmp);
        app.setTexture(tex);

        if ( firstFrame ) {
            firstFrame = false;
            // u.addBranchGraph(scene);
            try {
                // give J3D more time to initialize
                //Thread.currentThread().sleep(5500);
                Thread.sleep(5500);
            } catch ( Exception ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                //Thread.currentThread().sleep(30);
                Thread.sleep(30);
            } catch ( Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // System.out.println("in doProcess");
        return BUFFER_PROCESSED_OK;
    }

    //---------------------
    // Java3D related methods
    BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();

        // Create appearance object for textured obj
        app = new Appearance();
        tex = createTexture();

        app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
        tex.setCapability(Texture.ALLOW_IMAGE_WRITE);

        app.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        app.setTextureAttributes(texAttr);

        Point3d[] vertices = new Point3d[4];
        vertices[0] = new Point3d(-1.0, -1.0, 0.0); // 左下  3+-----+2
        vertices[1] = new Point3d( 1.0, -1.0, 0.0); // 右下   |     |
        vertices[2] = new Point3d( 1.0,  1.0, 0.0); // 右上   |     |
        vertices[3] = new Point3d(-1.0,  1.0, 0.0); // 左上  0+-----+1
        TexCoord2f[] txcoords = new TexCoord2f[4];
        txcoords[0] = new TexCoord2f(0.0f, 0.0f); // 左下  3+-----+2
        txcoords[1] = new TexCoord2f(1.0f, 0.0f); // 右下   |     |
        txcoords[2] = new TexCoord2f(1.0f, 1.0f); // 右上   |     |
        txcoords[3] = new TexCoord2f(0.0f, 1.0f); // 左上  0+-----+1
        QuadArray geom = new QuadArray(vertices.length,
            GeometryArray.COORDINATES |
            GeometryArray.NORMALS |
            GeometryArray.TEXTURE_COORDINATE_2);
        geom.setCoordinates(0, vertices);
        geom.setTextureCoordinates(0, 0, txcoords);
        Shape3D shape = new Shape3D(geom,app);
        objRoot.addChild(shape);

        vertices = new Point3d[4];
        vertices[0] = new Point3d( 1.0, -1.0, -0.1); // 左下  3+-----+2
        vertices[1] = new Point3d(-1.0, -1.0, -0.1); // 右下   |     |
        vertices[2] = new Point3d(-1.0,  1.0, -0.1); // 右上   |     |
        vertices[3] = new Point3d( 1.0,  1.0, -0.1); // 左上  0+-----+1
        float[] colors= {
            0.0f,0.0f,1.0f,
            0.0f,0.0f,1.0f,
            0.0f,0.0f,1.0f,
            0.0f,0.0f,1.0f
        };
        geom = new QuadArray(vertices.length,
            QuadArray.COORDINATES | QuadArray.COLOR_3);
        geom.setCoordinates(0, vertices);
        geom.setColors(0, colors);
        shape = new Shape3D(geom);
        objRoot.addChild(shape);

       // Have Java 3D perform optimizations on this scene graph.
        objRoot.compile();

        return objRoot;
    }

    private Texture2D createTexture() {
        int j = 0;
        byte alpha_1 = (byte)0xff;

        BufferedImage bimg = new BufferedImage(SS, SS, btype);
        byte[] byteData = ((DataBufferByte)bimg.getRaster().getDataBuffer()).getData(); 
        if ( btype == BufferedImage.TYPE_4BYTE_ABGR) {
            j = 0;
            for ( int i = 0; i < SS*SS; i++) {
                byteData[j] = alpha_1;
                byteData[j+1] = (byte)192;
                byteData[j+2] = (byte)0;
                byteData[j+3] = (byte)192;
                j += 4;
            }
        } else {
            j = 0;
            for ( int i = 0; i < SS*SS; i++) {
                byteData[j] = (byte)192;
                byteData[j+1] = (byte)0;
                byteData[j+2] = (byte)192;
                j += 3;
            }

        }
        ImageComponent2D imgcmp = new ImageComponent2D(itype, bimg, byRef, true);
        Texture2D tex1 = new Texture2D(Texture2D.BASE_LEVEL, ttype, SS, SS);
        tex1.setImage(0, imgcmp);
        return tex1;
    }

    
    /****************************************************************
     * VideoRenderer implementation
     ****************************************************************/

    /**
     * Returns an AWT component that it will render to. Returns null
     * if it is not rendering to an AWT component.
     */
    public java.awt.Component getComponent() {
        return null;
    }
    
    /**
     * Requests the renderer to draw into a specified AWT component.
     * Returns false if the renderer cannot draw into the specified
     * component.
     */
    public boolean setComponent(java.awt.Component comp) {
        component = comp;
        return true;
    }

    /**
     * Sets the region in the component where the video is to be
     * rendered to. Video is to be scaled if necessary. If <code>rect</code>
     * is null, then the video occupies the entire component.
     */
    public void setBounds(java.awt.Rectangle rect) {
        reqBounds = rect;
    }

    /**
     * Returns the region in the component where the video will be
     * rendered to. Returns null if the entire component is being used.
     */
    public java.awt.Rectangle getBounds() {
        return reqBounds;
    }
    
    /*************************************************************************
     * Local methods
     *************************************************************************/

    int getInWidth() {
        return inWidth;
    }

    int getInHeight() {
        return inHeight;
    }

}
