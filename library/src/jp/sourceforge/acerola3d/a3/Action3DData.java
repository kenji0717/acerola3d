package jp.sourceforge.acerola3d.a3;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.media.j3d.*;

import org.jdesktop.j3d.loaders.vrml97.VrmlLoader;
import javax.vecmath.*;


import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.*;

import com.sun.j3d.loaders.Scene;

import javax.xml.xpath.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.sourceforge.acerola3d.A23;
import jp.sourceforge.acerola3d.a3.bvh.*;

import jp.sourceforge.acerola3d.a3.catalog.*;
import javax.xml.bind.*;
import java.security.AccessController;
import java.security.PrivilegedAction;

import jp.sourceforge.acerola3d.sound.*;

/**
 * ファイルから読み込んだデータを再利用可能な形で保存しておく
 * ためのデータ構造。さらに、このデータはコピー可能であり、
 * 編集可能なようにする。(現在まだ、未整理)
 */
class Action3DData {
    static VrmlLoader loader = null;
    static boolean isInitialized = false;
    static A3SoundSystem soundSystem = null;

    static void initAction3DData() {
        if (isInitialized==true)
            return;
        try {
            loader = new VrmlLoader();
            //j3d-vrml97-0.1.0のAPIによればVrmlLoaderのコンストラクタの引数は
            //さしあたり無視されるということらしい．
            //loader = new VrmlLoader(VrmlLoader.LOAD_VIEW_GROUPS|
            //                        VrmlLoader.LOAD_SOUND_NODES|
            //                        VrmlLoader.LOAD_LIGHT_NODES|
            //                        VrmlLoader.LOAD_FOG_NODES);
        } catch(Exception e) {
            System.out.println("Action3DData.initAction3DData(). gaha!");
            e.printStackTrace();
        }
        isInitialized = true;
    }

    URL url;
    String comment;
    int haltActionNo = 0;
    int walkActionNo = 0;
    int runActionNo = 0;
    double minWalkSpeed = 0.1;
    double minRunSpeed = 1.0;
    boolean autoActionControl = false;
    boolean billboardControl = false;
    String rdf;
    String tags[];
    String profiles[];
    String thumbnails[];
    String html;

    Action actions[];
    String actionNames[];
    HashMap<String,ShapeAndBF> shapes;
    HashMap<String,Motion> motions;
    HashMap<String,Object> sounds;
    Group allActionGroup;
    int currentActionNo;

    static BoundingSphere bounding = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);
    //アクションの中のパーツのデータを保存しておくためのクラス
    class Part {
        String fileName;
        double scale = 1.0;
        double[] offsetXYZ;
        double[] rotationXYZ;
        //motionから得られるTransform3DがセットされるべきTransformGroup
        TransformGroup motionTG;
        //着せ替え機能で使われるTransformGroup．つまり，パーツのオフセット
        //などの設定がされている．特にオフセットなどの指定がなければmotionTGと同じインスタンスが入る．
        TransformGroup adapterTG;
        Node node;
    }
    //アクションデータを保存しておくためのクラス
    class Action {
        int actionNo;
        String actionName;
        boolean loopFlag;
        String bvhFile;
        String soundFile;
        boolean soundLoop;
        String soundType = "PointSound";
        float soundGain = 1.0f;
        float soundOffsetXYZ[] = {0.0f,0.0f,0.0f};
        float soundDirectionXYZ[] = {0.0f,0.0f,1.0f};
        boolean soundContinue = true;
        Object sound;
        double scale;
        double[] offsetXYZ;
        double[] rotationXYZ;
        HashMap<String,Part> pHash;
        double segno;
        double dalsegno;
        A3Object.BalloonDir balloonDirection = A3Object.BalloonDir.RIGHT;
        double rightBalloonOffset[] = {0.0,0.0};
        double leftBalloonOffset[] = {0.0,0.0};
        double topBalloonOffset[] = {0.0,0.0};
        double bottomBalloonOffset[] = {0.0,0.0};
        double labelOffset[] = {0.0,0.0};

        BranchGroup actionRootGroup;//addChild,detachによるアクション切り替えのためのグループ
        TransformGroup actionTransformGroup;
        ActionBehavior behavior;
        Motion motion;
        HashMap<String,TransformGroup> tgMap;

        Action copy() {
            Action ret = new Action();
            ret.actionNo = actionNo;
            ret.actionName = actionName;
            ret.loopFlag = loopFlag;
            ret.bvhFile = bvhFile;
            ret.soundFile = soundFile;
            ret.soundLoop = soundLoop;
            ret.soundType = soundType;
            ret.soundGain = soundGain;
            ret.soundOffsetXYZ = soundOffsetXYZ.clone();
            ret.soundDirectionXYZ = soundDirectionXYZ.clone();
            ret.soundContinue = soundContinue;
            ret.scale = scale;
            ret.rotationXYZ = (rotationXYZ==null)?null:rotationXYZ.clone();
            ret.offsetXYZ = (offsetXYZ==null)?null:offsetXYZ.clone();
            ret.pHash = new HashMap<String,Part>(pHash);//とりあえずPartの書き換えはしないという前提のコピー
            ret.motion = motion;
            ret.segno = segno;
            ret.dalsegno = dalsegno;
            ret.rightBalloonOffset = rightBalloonOffset.clone();
            ret.leftBalloonOffset = leftBalloonOffset.clone();
            ret.topBalloonOffset = topBalloonOffset.clone();
            ret.bottomBalloonOffset = bottomBalloonOffset.clone();
            ret.labelOffset = labelOffset.clone();

            return ret;
        }

        Transform3D makeTransform3D(double offsetXYZ[],double rotationXYZ[],double scale) {
            Transform3D t0a = new Transform3D();
            Transform3D t0b = new Transform3D();
            if (offsetXYZ!=null) {
                t0b.setTranslation(new Vector3d(offsetXYZ[0],offsetXYZ[1],offsetXYZ[2]));
                t0a.mul(t0b);
            }
            if (rotationXYZ!=null) {
                //2010,01/18:順番変更z,y,x -> z,x,y
                t0b.setIdentity();
                t0b.rotY(rotationXYZ[1]/180.0*Math.PI);
                t0a.mul(t0b);
                t0b.rotX(rotationXYZ[0]/180.0*Math.PI);
                t0a.mul(t0b);
                t0b.rotZ(rotationXYZ[2]/180.0*Math.PI);
                t0a.mul(t0b);
            }
            t0b.setIdentity();
            t0b.setScale(scale);
            t0a.mul(t0b);
            return t0a;
        }

        void construct3DNode(Action3D a3) {
            tgMap = new HashMap<String,TransformGroup>();

            actionRootGroup = new BranchGroup();
            actionRootGroup.setCapability(BranchGroup.ALLOW_DETACH);

            actionTransformGroup = new TransformGroup();
            actionRootGroup.addChild(actionTransformGroup);
            if ((rotationXYZ!=null)||(offsetXYZ!=null)||(scale!=1.0)) {
                Transform3D t3d = makeTransform3D(offsetXYZ,rotationXYZ,scale);
                actionTransformGroup.setTransform(t3d);
            }

            if (bvhFile.equals("none"))
                motion = new DummyMotion();
            else
                motion = motions.get(bvhFile);

            String rootBoneName = motion.getRootBone();
            construct(actionTransformGroup,rootBoneName);

            //boneに関連付けされていないPartの処理
            String bones[] = motion.getAllBones();
            String parts[] = pHash.keySet().toArray(new String[0]);
            for (String part : parts) {
                boolean exist = false;
                for (String bone : bones) {
                    if (part.equals(bone)) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    construct(actionTransformGroup,part);
                }
            }

            if (soundFile!=null) {
                Object s = sounds.get(soundFile);
                if (s instanceof MediaContainer) {
                    MediaContainer mc = (MediaContainer)s;
                    if (soundType.equals("BackgroundSound")) {
                        sound = new BackgroundSound(mc,soundGain);
                    } else if (soundType.equals("PointSound")){
                        sound = new PointSound(mc,soundGain,
                                               soundOffsetXYZ[0],
                                               soundOffsetXYZ[1],
                                               soundOffsetXYZ[2]);
                    } else if (soundType.equals("ConeSound")) {
                        /*
                        sound = new ConeSound(mc,soundGain,
                                soundOffsetXYZ[0],
                                soundOffsetXYZ[1],
                                soundOffsetXYZ[2],
                                soundDirectionXYZ[0],
                                soundDirectionXYZ[1],
                                soundDirectionXYZ[2]);
                        */

                        //http://old.siggraph.org/education/materials/siggraph_courses/S99/40/java3d4p.pdf
                        //さっぱり意味がわかりません。
                        Point2f[] myFrontAtten = {
                                new Point2f( 100.0f, 1.0f ),
                                new Point2f( 350.0f, 0.5f ),
                                new Point2f( 600.0f, 0.0f )
                                };
                                Point2f[] myBackAtten = {
                                new Point2f( 50.0f, 1.0f ),
                                new Point2f( 100.0f, 0.5f ),
                                new Point2f( 200.0f, 0.0f )
                                };
                                Point3f[] myAngular = {
                                new Point3f( 0.000f, 1.0f, 20000.0f ),
                                new Point3f( 0.785f, 0.5f, 5000.0f ),
                                new Point3f( 1.571f, 0.0f, 2000.0f ),
                                };
                        sound = new ConeSound();
                        ((ConeSound)sound).setSoundData(mc);
                        ((ConeSound)sound).setInitialGain(soundGain);
                        ((ConeSound)sound).setPosition(soundOffsetXYZ[0],soundOffsetXYZ[1],soundOffsetXYZ[2]);
                        ((ConeSound)sound).setDirection(soundDirectionXYZ[0],soundDirectionXYZ[1],soundDirectionXYZ[2]);
                        ((ConeSound)sound).setDistanceGain(myFrontAtten,myBackAtten);
                        ((ConeSound)sound).setAngularAttenuation(myAngular);

                        /*
                        //http://www.koders.com/java/fid9234663F18A13A2F1B99E463FCFE7B1D75BCB621.aspx?s=DistanceAttenuation#L40
                        //さっぱり意味がわかりません。
                        sound = new ConeSound(mc,soundGain,
                                soundOffsetXYZ[0],
                                soundOffsetXYZ[1],
                                soundOffsetXYZ[2],
                                soundDirectionXYZ[0],
                                soundDirectionXYZ[1],
                                soundDirectionXYZ[2]);
                        float angle = (float) (Math.PI / 4);
                        ((ConeSound)sound).setAngularAttenuation(new Point2f[] {
                            new Point2f(angle, 0)});
                        float distanceAtZero = 40;
                        ((ConeSound)sound).setDistanceGain(new float []{0, distanceAtZero}, new float []{1, 0});
                        */
                    }
                    Sound j3dSound = (Sound)sound;
                    j3dSound.setEnable(false);
                    j3dSound.setPause(false);
                    if (soundLoop)
                        j3dSound.setLoop(-1);
                    else
                        j3dSound.setLoop(0);
                    //j3dSound.setContinuousEnable(true);
                    j3dSound.setSchedulingBounds(bounding);
                    j3dSound.setCapability(Sound.ALLOW_ENABLE_WRITE);
                    //j3dSound.setCapability(Sound.ALLOW_PAUSE_WRITE);
                    j3dSound.setCapability(Sound.ALLOW_INITIAL_GAIN_WRITE);
                    j3dSound.setCapability(Sound.ALLOW_INITIAL_GAIN_READ);
                    SharedGroup sg = new SharedGroup();
                    sg.addChild(j3dSound);
                    Link ll = new Link(sg);
                    actionTransformGroup.addChild(ll);
                } else if (s instanceof A3Sound) {
                    A3Sound a3Sound = ((A3Sound)s).copy();
                    a3Sound.setGain(soundGain);
                    a3Sound.setSegno((float)segno);
                    a3Sound.setLoop(soundLoop);
                    A3SoundType type = null;
                    a3Sound.setOffset(soundOffsetXYZ[0],soundOffsetXYZ[1],soundOffsetXYZ[2]);
                    a3Sound.setDirection(soundDirectionXYZ[0],soundDirectionXYZ[1],soundDirectionXYZ[2]);
                    if (soundType.equals("BackgroundSound")) {
                        type = A3SoundType.BackgroundSound;
                        a3Sound.setType(type);
                    } else if (soundType.equals("ConeSound")) {
                        type = A3SoundType.ConeSound;
                        a3Sound.setType(type);
                    } else {
                        type = A3SoundType.PointSound;
                        a3Sound.setType(type);
                    }
                    sound = a3Sound;
                }
            }
        }

        void construct(Group parent,String boneName) {
            TransformGroup motionTG = new TransformGroup();
            motionTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            parent.addChild(motionTG);
            tgMap.put(boneName,motionTG);

            Part part = pHash.get(boneName);
            if (part!=null) {
                TransformGroup adapterTG = null;
                if ((part.rotationXYZ==null)&&(part.offsetXYZ==null)&&(part.scale==1.0)) {
                    adapterTG = motionTG;
                } else {
                    adapterTG = new TransformGroup(makeTransform3D(part.offsetXYZ,part.rotationXYZ,part.scale));
                    motionTG.addChild(adapterTG);
                }
                BranchGroup bg = new BranchGroup();
                adapterTG.addChild(bg);
                ShapeAndBF sabf = shapes.get(part.fileName);
                bg.addChild(new Link(sabf.sg));
                if (sabf.b!=null)
                    for (int j=0;j<sabf.b.length;j++) 
                        bg.addChild(sabf.b[j].cloneNode(false));
                if (sabf.f!=null)
                    for (int j=0;j<sabf.f.length;j++) 
                        bg.addChild(sabf.f[j].cloneNode(false));
                part.motionTG = motionTG;
                part.adapterTG = adapterTG;
part.node = bg;//gaha
            }

            for (String child : motion.getChildBones(boneName)) {
                construct(motionTG,child);
            }
        }

        Group getRootGroup() {
            return actionRootGroup;
        }

        void start() {
            //if (sound!=null) {
            //    sound.setEnable(true);
            //    //sound.setPause(false);
            //}
        }

        void stop() {
            if (sound!=null) {
                if (sound instanceof Sound) {
                    Sound s = (Sound)sound;
                    s.setEnable(false);
                    //mc.setPause(true);
                } else if (sound instanceof A3Sound) {
                    A3Sound s = (A3Sound)sound;
                    s.stop();
                }
            }
        }

        void soundStart() {
            if (sound instanceof Sound) {
                ((Sound)sound).setEnable(true);
            } else if (sound instanceof A3Sound) {
                ((A3Sound)sound).start();
            }
        }
        void soundStop() {
            if (sound instanceof Sound) {
                ((Sound)sound).setEnable(false);
            } else if (sound instanceof A3Sound) {
                ((A3Sound)sound).stop();
            }
        }
        void setSoundLocQuat(Vector3d v,Quat4d q) {
            if (sound instanceof A3Sound) {
                ((A3Sound)sound).setLoc(v);
                ((A3Sound)sound).setQuat(q);
            }
        }
        boolean isStoped() {
            return behavior.isStoped();
        }

        double getMotionLength() {
            return motion.getMotionLength();
        }

        void setMode(Motion.Mode m) {
            behavior.setMode(m);
        }

        void setPauseTime(double t) {
            behavior.setPauseTime(t);
        }

        void setSoundGain(double g) {
            if (sound instanceof A3Sound) {
                ((A3Sound)sound).setGain((float)g);
            } else if (sound instanceof Sound) {
                ((Sound)sound).setInitialGain((float)g);
            }
        }
        double getSoundGain() {
            if (sound instanceof A3Sound) {
                return ((A3Sound)sound).getGain();
            } else if (sound instanceof Sound) {
                return ((Sound)sound).getInitialGain();
            }
            return -1.0;
        }
    }

    Action3DData() {
    }

    Action3DData(String urlString) throws Exception {
        url = new URL(urlString);
        init(url);
    }
    Action3DData(URL url) throws Exception {
        init(url);
    }

    void init(URL url) throws Exception {
        this.url = url;
        AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    loadCatalog();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        loadAllShapes();
        loadAllMotions();
        loadAllSounds();
        A23.clearZipCache();
    }

    //cloneのような浅いコピーじゃなくて，かといって深い
    //コピーでもなく，その中間のコピー．つまり，変更されない
    //shapes,motions,soundsの中身だけ浅いコピーをするってこと．
    public Action3DData copy() {
        Action3DData ret = new Action3DData();

        ret.url = url;
        ret.comment = comment;
        ret.actionNames = actionNames.clone();
        ret.haltActionNo = haltActionNo;
        ret.walkActionNo = walkActionNo;
        ret.runActionNo = runActionNo;
        ret.minWalkSpeed = minWalkSpeed;
        ret.minRunSpeed = minRunSpeed;
        ret.autoActionControl = autoActionControl;
        ret.billboardControl = billboardControl;
        ret.rdf = rdf;
        ret.tags = tags.clone();
        ret.profiles = profiles.clone();
        ret.thumbnails = thumbnails.clone();
        ret.html = html;

        ret.actions = new Action[actions.length];
        for (int i=0;i<actions.length;i++) {
            ret.actions[i] = actions[i].copy();
        }
        ret.shapes = new HashMap<String,ShapeAndBF>(shapes);
        ret.motions = new HashMap<String,Motion>(motions);
        ret.sounds = new HashMap<String,Object>(sounds);
        return ret;
    }

    void loadCatalog() throws Exception {
        loadCatalog_NEW();
        //loadCatalog_OLD();
    }
    void loadCatalog_NEW() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("jp.sourceforge.acerola3d.a3.catalog");
        Unmarshaller u = jc.createUnmarshaller();
        A3 a3 = null;
        try {
            URL cURL = null;
            if (url.getProtocol().equals("file")) {
                File f = new File(url.toURI());
                if (f.isDirectory()) {
                    cURL = new URL(url.toString()+"/CATALOG.XML");
                } else {
                    cURL = new URL("x-rzip:"+url.toString()+"!/CATALOG.XML");
                }
            } else {
                cURL = new URL("x-rzip:"+url.toString()+"!/CATALOG.XML");
            }
            a3 = (A3)u.unmarshal(cURL.openStream());
        } catch(Exception e) {
            System.out.println("'catalog.xml'");
            //e.printStackTrace();
            try {
                URL cURL = null;
                if (url.getProtocol().equals("file")) {
                    File f = new File(url.toURI());
                    if (f.isDirectory()) {
                        cURL = new URL(url.toString()+"/catalog.xml");
                    } else {
                        cURL = new URL("x-rzip:"+url.toString()+"!/catalog.xml");
                    }
                } else {
                    cURL = new URL("x-rzip:"+url.toString()+"!/catalog.xml");
                }
                a3 = (A3)u.unmarshal(cURL.openStream());
            } catch(Exception ee) {
                throw ee;
            }
        }
        haltActionNo = a3.getHaltActionNo().intValue();
        walkActionNo = a3.getWalkActionNo().intValue();
        runActionNo = a3.getRunActionNo().intValue();
        minWalkSpeed = a3.getMinWalkSpeed();
        minRunSpeed = a3.getMinRunSpeed();
        billboardControl = a3.isBillboard();
        comment = a3.getC();
        actions = new Action[a3.getA().size()];
        actionNames = new String[a3.getA().size()];
        rdf = domSerialize(a3.getAny());
        tags = new String[a3.getTag().size()];
        int i=0;
        for (Tag t:a3.getTag()) {
            tags[i++] = t.getName();
        }
        profiles = new String[a3.getProfile().size()];
        i=0;
        for (Profile p:a3.getProfile()) {
            profiles[i++] = p.getUri();
        }
        thumbnails = new String[a3.getThumbnail().size()];
        i=0;
        for (Thumbnail t:a3.getThumbnail()) {
            if (url.getProtocol().equals("file")) {
                File f = new File(url.toURI());
                if (f.isDirectory()) {
                    thumbnails[i++] = url.toString()+"/"+t.getSrc();
                } else {
                    thumbnails[i++] = "x-rzip:"+url.toString()+"!/"+t.getSrc();
                }
            } else {
                thumbnails[i++] = "x-rzip:"+url.toString()+"!/"+t.getSrc();
            }
        }
        if (a3.getHtmlfile()!=null) {
            if (url.getProtocol().equals("file")) {
                File f = new File(url.toURI());
                if (f.isDirectory()) {
                    html = url.toString()+"/"+a3.getHtmlfile().getSrc();
                } else {
                    html = "x-rzip:"+url.toString()+"!/"+a3.getHtmlfile().getSrc();
                }
            } else {
                html = "x-rzip:"+url.toString()+"!/"+a3.getHtmlfile().getSrc();
            }
        }
        i=0;
        for (A aNode : a3.getA()) {
            Action a = new Action();
            a.actionNo = i;
            a.actionName = aNode.getAn();
            actions[i] = a;
            actionNames[i] = a.actionName;
            a.loopFlag = aNode.isLoop();
            a.bvhFile = aNode.getBvh();
            a.scale = aNode.getScale();
            a.rotationXYZ = makeDouble3(aNode.getRot());
            a.offsetXYZ = makeDouble3(aNode.getOffset());
            a.pHash = new HashMap<String,Part>();
            a.segno = aNode.getSegno();
            a.dalsegno = aNode.getDalsegno();
            a.rightBalloonOffset = makeDouble2(aNode.getRightBalloonOffset());
            a.leftBalloonOffset = makeDouble2(aNode.getLeftBalloonOffset());
            a.topBalloonOffset = makeDouble2(aNode.getTopBalloonOffset());
            a.bottomBalloonOffset = makeDouble2(aNode.getBottomBalloonOffset());
            a.labelOffset = makeDouble2(aNode.getLabelOffset());
            for (P pNode : aNode.getP()) {
                Part p = new Part();
                String partName = pNode.getName().trim();
                p.fileName = pNode.getWrl().trim();
                p.scale = pNode.getScale();
                p.offsetXYZ = makeDouble3(pNode.getOffset());
                p.rotationXYZ = makeDouble3(pNode.getRot());
                a.pHash.put(partName,p);
            }
            S s = aNode.getS();
            if (s!=null) {
                a.soundFile = s.getFile();
                a.soundLoop = s.isLoop();
                if (s.getType()==SoundType.POINT_SOUND)
                    a.soundType = "PointSound";
                else if (s.getType()==SoundType.BACKGROUND_SOUND)
                    a.soundType = "BackgroundSound";
                else if (s.getType()==SoundType.CONE_SOUND)
                    a.soundType = "ConeSound";
                a.soundGain = (float)s.getGain();
                a.soundOffsetXYZ = makeFloat3(s.getOffset());
                a.soundDirectionXYZ = makeFloat3(s.getDirection());
                a.soundContinue = s.isContinue();
            }
            i++;
        }
    }
    double[] makeDouble3(String s) {
        if (s.equals("0.0 0.0 0.0"))
            return null;
        double ret[] = new double[3];
        StringTokenizer st = new StringTokenizer(s);
        int i=0;
        while (st.hasMoreTokens()) {
            ret[i] = Double.parseDouble(st.nextToken());
            i++;
        }
        return ret;
    }
    double[] makeDouble2(String s) {
        //if (s.equals("0.0 0.0"))
        //    return null;
        double ret[] = new double[2];
        StringTokenizer st = new StringTokenizer(s);
        int i=0;
        while (st.hasMoreTokens()) {
            ret[i] = Double.parseDouble(st.nextToken());
            i++;
        }
        return ret;
    }
    float[] makeFloat3(String s) {
        //if (s.equals("0.0 0.0 0.0"))
        //    return null;
        float ret[] = new float[3];
        StringTokenizer st = new StringTokenizer(s);
        int i=0;
        while (st.hasMoreTokens()) {
            ret[i] = Float.parseFloat(st.nextToken());
            i++;
        }
        return ret;
    }
    String domSerialize(org.w3c.dom.Element e) {
        try {
            DOMImplementationRegistry dir = DOMImplementationRegistry.newInstance();
            DOMImplementation di = dir.getDOMImplementation("LS 3.0");
            DOMImplementationLS dils = (DOMImplementationLS)di;
            LSOutput output = dils.createLSOutput();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            output.setByteStream(baos);
            output.setEncoding("UTF-8");
            LSSerializer serializer = dils.createLSSerializer();
            serializer.write(e, output);
            return baos.toString("UTF-8");
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
    }
    void loadCatalog_OLD() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = null;
        try {
            String cURL = null;
            if (url.getProtocol().equals("file")) {
                File f = new File(url.toURI());
                if (f.isDirectory()) {
                    cURL = url.toString() + "/CATALOG.XML";
                } else {
                    cURL = "x-rzip:"+url.toString()+"!/CATALOG.XML";
                }
            } else {
                cURL = "x-rzip:"+url.toString()+"!/CATALOG.XML";
            }
            d = db.parse(cURL);
        } catch(Exception e) {
            try {
                String cURL = null;
                if (url.getProtocol().equals("file")) {
                    File f = new File(url.toURI());
                    if (f.isDirectory()) {
                        cURL = url.toString() + "/catalog.xml";
                    } else {
                        cURL = "x-rzip:"+url.toString()+"!/catalog.xml";
                    }
                } else {
                    cURL = "x-rzip:"+url.toString()+"!/catalog.xml";
                }
                d = db.parse(cURL);
            } catch (Exception ee) {
                throw ee;
            }
        }

        //a3の属性の処理
        org.w3c.dom.Node a3[] = xpath("/a3",d);
        NamedNodeMap nnm = a3[0].getAttributes();
        Attr attr = (Attr)nnm.getNamedItem("haltActionNo");
        if (attr!=null)
            haltActionNo = Integer.parseInt(attr.getValue().trim());
        attr = (Attr)nnm.getNamedItem("walkActionNo");
        if (attr!=null)
            walkActionNo = Integer.parseInt(attr.getValue().trim());
        attr = (Attr)nnm.getNamedItem("runActionNo");
        if (attr!=null)
            runActionNo = Integer.parseInt(attr.getValue().trim());
        attr = (Attr)nnm.getNamedItem("minWalkSpeed");
        if (attr!=null)
            minWalkSpeed = Double.parseDouble(attr.getValue().trim());
        attr = (Attr)nnm.getNamedItem("minRunSpeed");
        if (attr!=null)
            minRunSpeed = Double.parseDouble(attr.getValue().trim());
        attr = (Attr)nnm.getNamedItem("autoActionControl");
        if (attr!=null)
            autoActionControl = Boolean.parseBoolean(attr.getValue().trim());
        attr = (Attr)nnm.getNamedItem("billboard");
        if (attr!=null)
            billboardControl = Boolean.parseBoolean(attr.getValue().trim());
        
        //コメントデータゲット
        org.w3c.dom.Node cc[] = xpath("/a3/c/text()",d);
        if (cc.length!=0)
            comment = ((Text)cc[0]).getData();
        else
            comment = "";

        //全<a>タグ(アクション情報)ゲット
        org.w3c.dom.Node a_nodes[] = xpath("/a3/a",d);
        actions = new Action[a_nodes.length];
        actionNames = new String[a_nodes.length];
        //1アクションごとにループ
        for (int i=0;i<a_nodes.length;i++) {
            Action a = new Action();
            a.actionNo = i;
            nnm = a_nodes[i].getAttributes();
            //以下<a>タグの属性の処理
            attr = (Attr)nnm.getNamedItem("an");
            a.actionName = attr.getValue().trim();
            actions[i] = a;
            actionNames[i] = a.actionName;
            attr = (Attr)nnm.getNamedItem("loop");
            if (attr!=null) {
                Boolean bbb = Boolean.valueOf(attr.getValue());
                a.loopFlag = bbb.booleanValue();
            } else {
                a.loopFlag = false;
            }
            attr = (Attr)nnm.getNamedItem("bvh");
            a.bvhFile = attr.getValue().trim();
            attr = (Attr)nnm.getNamedItem("scale");
            if (attr != null)
                a.scale = Double.parseDouble(attr.getValue());
            else
                a.scale = 1.0;
            attr = (Attr)nnm.getNamedItem("rot");
            if (attr != null) {
                a.rotationXYZ = new double[3];
                StringTokenizer st = new StringTokenizer(attr.getValue());
                int ii=0;
                while (st.hasMoreTokens()) {
                    a.rotationXYZ[ii] = Double.parseDouble(st.nextToken());
                    ii++;
                }
            }
            attr = (Attr)nnm.getNamedItem("offset");
            if (attr != null) {
                a.offsetXYZ = new double[3];
                StringTokenizer st = new StringTokenizer(attr.getValue());
                int ii=0;
                while (st.hasMoreTokens()) {
                    a.offsetXYZ[ii] = Double.parseDouble(st.nextToken());
                    ii++;
                }
            }
            a.pHash = new HashMap<String,Part>();
            org.w3c.dom.Node p_nodes[] = xpath("/a3/a[position()="+(i+1)+"]/p",d);
            for (int j=0;j<p_nodes.length;j++) {
                Part p = new Part();
                nnm = p_nodes[j].getAttributes();
                attr = (Attr)nnm.getNamedItem("name");
                String partName = attr.getValue().trim();
                attr = (Attr)nnm.getNamedItem("wrl");
                p.fileName = attr.getValue().trim();
                attr = (Attr)nnm.getNamedItem("scale");
                if (attr!=null)
                    p.scale = Double.parseDouble(attr.getValue().trim());
                attr = (Attr)nnm.getNamedItem("offset");
                if (attr!=null) {
                    p.offsetXYZ = new double[3];
                    StringTokenizer st = new StringTokenizer(attr.getValue());
                    int ii = 0;
                    while (st.hasMoreTokens()) {
                        p.offsetXYZ[ii] = Double.parseDouble(st.nextToken());
                        ii++;
                    }
                }
                attr = (Attr)nnm.getNamedItem("rot");
                if (attr!=null) {
                    p.rotationXYZ = new double[3];
                    StringTokenizer st = new StringTokenizer(attr.getValue());
                    int ii = 0;
                    while (st.hasMoreTokens()) {
                        p.rotationXYZ[ii] = Double.parseDouble(st.nextToken());
                        ii++;
                    }
                }
                a.pHash.put(partName,p);
            }
            org.w3c.dom.Node s_nodes[] = xpath("/a3/a[position()="+(i+1)+"]/s",d);
            if ((s_nodes != null)&&(s_nodes.length!=0)) {
                nnm = s_nodes[0].getAttributes();
                attr = (Attr)nnm.getNamedItem("file");
                a.soundFile = attr.getValue().trim();
                attr = (Attr)nnm.getNamedItem("loop");
                if (attr!=null)
                    a.soundLoop = Boolean.parseBoolean(attr.getValue().trim());
                attr = (Attr)nnm.getNamedItem("type");
                if (attr!=null)
                    a.soundType = attr.getValue().trim();
                attr = (Attr)nnm.getNamedItem("gain");
                if (attr!=null)
                    a.soundGain = Float.parseFloat(attr.getValue().trim());
                attr = (Attr)nnm.getNamedItem("offset");
                if (attr!=null) {
                    StringTokenizer st = new StringTokenizer(attr.getValue());
                    int ii = 0;
                    while (st.hasMoreTokens()) {
                        a.soundOffsetXYZ[ii] = Float.parseFloat(st.nextToken());
                        ii++;
                    }
                }
            }
        }
    }

    //xpathでNodeを抽出するためのメソッド．でも手抜き．
    //queryによってはNode[]にならないので，その時はバグる．
    static org.w3c.dom.Node[] xpath(String query,Document doc) {
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(query);
            NodeList nl = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);

            org.w3c.dom.Node ret[] = new org.w3c.dom.Node[nl.getLength()];
            for (int i=0;i<nl.getLength();i++) {
                ret[i] = nl.item(i);
            }
            return ret;
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    void loadAllShapes() throws Exception {
        HashSet<Part> s = new HashSet<Part>();
        for (int i=0;i<actions.length;i++)
            s.addAll(actions[i].pHash.values());
        shapes = new HashMap<String,ShapeAndBF>();
        for (Part p : s) {
            URL wrlURL = null;
            if (url.getProtocol().equals("file")) {
                File f = new File(url.toURI());
                if (f.isDirectory()) {
                    wrlURL = new URL(url.toString()+"/"+p.fileName);
                } else {
                    wrlURL = new URL("x-rzip:"+url.toString()+"!/"+p.fileName);
                }
            } else {
                wrlURL = new URL("x-rzip:"+url.toString()+"!/"+p.fileName);
            }
            ShapeAndBF shapeAndBF = new ShapeAndBF();
            shapeAndBF.sg = new SharedGroup();
            Scene scene = loader.load(wrlURL);
            shapeAndBF.sg.addChild(scene.getSceneGroup());
            shapeAndBF.b = scene.getBackgroundNodes(); 
            //Background bs[] = scene.getBackgroundNodes(); 
            //shapeAndBF.b = new Background[bs.length];
            //for (int i=0;i<bs.length;i++)
            //    shapeAndBF.b[i] = (Background)bs[i].cloneNode(false);
            shapeAndBF.f = scene.getFogNodes();
            //Fog fogs[] = scene.getFogNodes();
            //shapeAndBF.f = new Fog[fogs.length];
            //for (int i=0;i<fogs.length;i++)
            //    shapeAndBF.f[i] = (Fog)fogs[i].cloneNode(false);
            //sg.compile();
            shapes.put(p.fileName,shapeAndBF);
        }
    }
    //通常のオブジェクトと背景、霧をまとめた物
    class ShapeAndBF {
        SharedGroup sg;
        Background b[];
        Fog f[];
    }

    void loadAllMotions() throws Exception {
        HashSet<String> s = new HashSet<String>();
        for (int i=0;i<actions.length;i++)
            if (actions[i].bvhFile!=null)
                s.add(actions[i].bvhFile);
        motions = new HashMap<String,Motion>();
        for (String filename : s) {
            if (filename.equals("none"))
                continue;
            URL motionURL = null;
            if (url.getProtocol().equals("file")) {
                File f = new File(url.toURI());
                if (f.isDirectory()) {
                    motionURL = new URL(url.toString()+"/"+filename);
                } else {
                    motionURL = new URL("x-rzip:"+url.toString()+"!/"+filename);
                }
            } else {
                motionURL = new URL("x-rzip:"+url.toString()+"!/"+filename);
            }
            BVH bvh = new BVH(motionURL);
            motions.put(filename,bvh);
        }
    }

    void loadAllSounds() throws Exception {
        HashSet<String> s = new HashSet<String>();
        for (int i=0;i<actions.length;i++)
            if (actions[i].soundFile!=null)
                s.add(actions[i].soundFile);
        sounds = new HashMap<String,Object>();
        for (String filename : s) {
            URL soundURL = null;
            if (url.getProtocol().equals("file")) {
                File f = new File(url.toURI());
                if (f.isDirectory()) {
                    soundURL = new URL(url.toString()+"/"+filename);
                } else {
                    soundURL = new URL("x-rzip:"+url.toString()+"!/"+filename);
                }
            } else {
                soundURL = new URL("x-rzip:"+url.toString()+"!/"+filename);
            }
            if (soundSystem==null) {
                MediaContainer mc = new MediaContainer(soundURL);
                sounds.put(filename,mc);
            } else {
                float segno = 0.0f;
                A3SoundType type = A3SoundType.PointSound;
                float gain = 1.0f;
                boolean loop = false;
                Vector3d offset = new Vector3d();
                Vector3d dir = new Vector3d(0.0,0.0,1.0);
                A3Sound ss = soundSystem.load(soundURL.toString(),segno,type,gain,loop,offset,dir);
                sounds.put(filename,ss);
            }
        }
    }

//------------------------------------------------------------

    void construct3DNode(Action3D a3) {
        for (int i=0;i<actions.length;i++)
            actions[i].construct3DNode(a3);

        //変更するときはちょっと下のchange(int i)のところも一緒に
        construct3DNode_OLD(a3);
        //construct3DNode_NEW(a3);
        ////construct3DNode_NEW2(a3);
    }
    void construct3DNode_OLD(Action3D a3) {
        BranchGroup bg = new BranchGroup();
        bg.setCapability(Group.ALLOW_CHILDREN_WRITE);
        bg.setCapability(Group.ALLOW_CHILDREN_EXTEND);

        bg.addChild(actions[0].actionRootGroup);
        currentActionNo = 0;
        allActionGroup = bg;
    }
    void construct3DNode_NEW(Action3D a3) {
        Switch sw = new Switch();
        sw.setCapability(Switch.ALLOW_SWITCH_WRITE);
        for (int i=0;i<actions.length;i++) {
            sw.addChild(actions[i].actionRootGroup);
        }

        sw.setWhichChild(0);
        currentActionNo = 0;
        allActionGroup = sw;
    }
    /* 作ってみたけどconstruct3DNode_NEWと状況変らず
    Switch sws[] = null;
    void construct3DNode_NEW2(Action3D a3) {
        BranchGroup bg = new BranchGroup();
        sws = new Switch[actions.length];
        for (int i=0;i<actions.length;i++) {
            Switch sw = new Switch();
            sw.setCapability(Switch.ALLOW_SWITCH_WRITE);
            sw.addChild(actions[i].actionRootGroup);
            sw.setWhichChild(Switch.CHILD_NONE);
            bg.addChild(sw);
            sws[i] = sw;
        }
        sws[0].setWhichChild(Switch.CHILD_ALL);
        currentActionNo = 0;
        allActionGroup = bg;
    }
    */
    void change(int i) {
        if (i==currentActionNo)return;
        //変更するときはちょっと上のconstruct3DNode(Action3D a3)のところも一緒に
        change_OLD(i);
        //change_NEW(i);
        ////change_NEW2(i);
    }
    void change_OLD(int i) {
        actions[currentActionNo].actionRootGroup.detach();
        allActionGroup.addChild(actions[i].actionRootGroup);
        currentActionNo = i;
    }
    void change_NEW(int i) {
        ((Switch)allActionGroup).setWhichChild(i);
        currentActionNo = i;
    }
    /* 作ってみたけどchange_NEWと状況変らず
    void change_NEW2(int i) {
        sws[currentActionNo].setWhichChild(Switch.CHILD_NONE);
        sws[i].setWhichChild(Switch.CHILD_ALL);
        currentActionNo = i;
    }
    */

  //------------------------------------------------------------

    Node getNode() {
        return allActionGroup;
    }

    String getActionNameFromActionNo(int an) {
        return actionNames[an];
    }

    int getActionNoFromActionName(String an) {
        for (int i=0;i<actionNames.length;i++)
            if (actionNames[i].equals(an))
                return i;
        return -1;
    }

    int getActionCount() {
        return actions.length;
    }

    String[] getActionNames() {
        return (String[])actionNames.clone();
    }

    boolean isStoped(int actionNo) {
        return actions[actionNo].isStoped();
    }

    double getMotionLength(int actionNo) {
        return actions[actionNo].getMotionLength();
    }

    double getMotionLength(String actionName) {
        int actionNo = getActionNoFromActionName(actionName);
        return actions[actionNo].getMotionLength();
    }

    String getComment() {
        return comment;
    }

    int getFrameCount() {
        return actionNames.length;
    }

    void setMode(Motion.Mode m) {
        for (int i=0;i<actions.length;i++)
            actions[i].setMode(m);
    }

    void setAutoActionControl(boolean b) {
        autoActionControl = b;
    }
    boolean autoActionControl() {
        return autoActionControl;
    }

    void setHaltAction(int i) {
        haltActionNo = i;
    }
    void setHaltAction(Serializable s) {
        if (s instanceof String) {
            setHaltAction(getActionNoFromActionName((String)s));
        } else if (s instanceof Integer) {
            setHaltAction(((Integer)s).intValue());
        }
    }
    int getHaltActionNo() {
        return haltActionNo;
    }

    void setWalkAction(int i) {
        walkActionNo = i;
    }
    void setWalkAction(Serializable s) {
        if (s instanceof String) {
            setWalkAction(getActionNoFromActionName((String)s));
        } else if (s instanceof Integer) {
            setWalkAction(((Integer)s).intValue());
        }
    }
    int getWalkActionNo() {
        return walkActionNo;
    }

    void setMinWalkSpeed(double d) {
        minWalkSpeed = d;
    }
    double getMinWalkSpeed() {
        return minWalkSpeed;
    }

    void setRunAction(int i) {
        runActionNo = i;
    }
    void setRunAction(Serializable s) {
        if (s instanceof String) {
            setRunAction(getActionNoFromActionName((String)s));
        } else if (s instanceof Integer) {
            setRunAction(((Integer)s).intValue());
        }
    }
    int getRunActionNo() {
        return runActionNo;
    }

    void setMinRunSpeed(double d) {
        minRunSpeed = d;
    }
    double getMinRunSpeed() {
        return minRunSpeed;
    }
    void setBillboardControl(boolean b) {
        billboardControl = b;
    }
    boolean getBillboardControl() {
        return billboardControl;
    }
    //------------------------------------------------------------
    void setShape(int actionNo,String boneName,Node shape) {
        Part p = actions[actionNo].pHash.get(boneName);
        //p.adapterTG.removeAllChildren();//なぜかこれだと全部消えちゃう？！
p.adapterTG.removeChild(p.node);//gaha
        p.adapterTG.addChild(shape);
p.node = shape;//gaha
    }
    void setShape(Serializable action,String boneName,Node shape) {
        if (action instanceof String) {
            int i = getActionNoFromActionName((String)action);
            setShape(i,boneName,shape);
        } else if (action instanceof Integer) {
            int i = ((Integer)action).intValue();
            setShape(i,boneName,shape);
        }
    }




    /**
     * このAction3Dで使用されている全てのShapeのファイル名を
     * 返す．同じShapeが異なるBoneやActionで共有されている場合でも，
     * 返り値は重複しないようになっている．
     */
    String[] getShapeFilenames() {
        return shapes.keySet().toArray(new String[0]);
    }

    /**
     * このAction3Dで使用されていShapeをファイル名で指定し，
     * そのShapeを引数で指定されたJava3DのNodeに変更します．
     * 変更前のShapeが異なるBoneやActionで共有されている場合には
     * その全てをnodeに置き換えます．この置き換えにはSharedGroupと
     * Linkを使った実装が使われます．
     */
    void replaceShape(String shapeFilename,Node node) {
        ShapeAndBF sabf = shapes.get(shapeFilename);
        if (sabf==null)
            return;
        SharedGroup sg = new SharedGroup();
        sg.addChild(node);
        for (Action a : actions) {
            for (Part p : a.pHash.values()) {
                if (p.fileName.equals(shapeFilename)) {
                    //p.adapterTG.removeAllChildren();//gahaこれだとダメ?
                    p.adapterTG.removeChild(p.node);
                    p.node = new Link(sg);
                    p.adapterTG.addChild(p.node);
                }
            }
        }
    }

    HashMap<String,Action3D[]> a3shapes = null;
    /**
     * このAction3Dで使用されていShapeをファイル名で指定し，
     * そのShapeを引数で指定されたURLから生成されるAction3Dの
     * getNode()メソッドで得られるNodeで置き換えます．
     * 変更前のShapeが異なるBoneやActionで共有されている場合には
     * その全てを置き換えます．この置き換えの実装では，内部で
     * 置き換えが必要なShapeの数だけAction3DがnewされてShapeごとに
     * 独立したAction3Dがセットされます．これらの複数のAction3Dの
     * Actionのchangeを一括して行うためにはshapeChange()メソッドを
     * 使用して下さい．
     */
    void replaceShapeWithA3(String shapeFilename,String a3url) throws Exception {
        if (a3shapes==null)
            a3shapes = new HashMap<String,Action3D[]>();
        a3shapes.remove(shapeFilename);
        ArrayList<Action3D> al = new ArrayList<Action3D>();
        for (Action a : actions) {
            for (Part p : a.pHash.values()) {
                if (p.fileName.equals(shapeFilename)) {
                    Action3D a3 = new Action3D(a3url);
                    al.add(a3);
                    //p.adapterTG.removeAllChildren();//gahaこれだとダメ?
                    p.adapterTG.removeChild(p.node);
                    p.node = a3.getNode();
                    p.adapterTG.addChild(p.node);
                }
            }
        }
        a3shapes.put(shapeFilename,al.toArray(new Action3D[0]));
    }

    /**
     * ファイル名で指定したShapeがreplaceShapeWithA3()で変更されている
     * 場合に，そのメソッドの中で生成される全てのAction3Dのアクションを
     * 一括で変更します．
     */
    void shapeChange(String shapeFilename,String actionName) {
        Action3D a3s[] = a3shapes.get(shapeFilename);
        if (a3s!=null) {
            for (Action3D a3 : a3s) {
                //a3.change(actionName);
                a3.changeImmediately(actionName);
            }
        }
    }

    //------------------------------------------------------------
    class DummyMotion implements Motion {
        public double getMotionLength() {
            return 0.001;
        }
        public double getDefaultFrameTime() {
            return 0.0333;
        }
        public String getRootBone() {
            return null;
        }
        public String getParentBone(String b) {
            return null;
        }
        public String[] getChildBones(String b) {
            return new String[0];
        }
        public String[] getAllBones() {
            return new String[0];
        }
        public Transform3D getTransform3D(String bone,double time) {
            return new Transform3D();
        }
    }
}
