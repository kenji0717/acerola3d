package jp.sourceforge.acerola3d.a3.bvh;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import jp.sourceforge.acerola3d.a3.bvh.analysis.*;
import jp.sourceforge.acerola3d.a3.bvh.lexer.*;
import jp.sourceforge.acerola3d.a3.bvh.node.*;
import jp.sourceforge.acerola3d.a3.bvh.parser.*;


class ParserBVH extends DepthFirstAdapter {
    int frameCount;
    double frameTime;
    String rootBone;
    HashMap<String,Bone> bones = new HashMap<String,Bone>();

    //BVHをパース中に使用するテンポラリデータが入ってる変数
    Tmp t;

    public ParserBVH(URL url) throws Exception {
        InputStream is = url.openStream();
        Reader reader = new InputStreamReader(is,"UTF-8");
        Lexer lexer = new Lexer(new PushbackReader(reader));
        Parser parser = new Parser(lexer);
        jp.sourceforge.acerola3d.a3.bvh.node.Node ast = parser.parse();

        t = new Tmp();
        ast.apply(this); // <- BVHをパースしてtの中にデータを構築する．
        convert(); // <- tの中の生のBVHデータをTransform3Dに変換
        t = null;
    }

    public double getMotionLength() {
        return frameCount*frameTime;
    }

    public double getDefaultFrameTime() {
        return frameTime;
    }

    public String getRootBone() {
        return rootBone;
    }

    public String getParentBone(String b) {
        Bone bone = bones.get(b);
        return bone.parent;
    }

    public String[] getChildBones(String b) {
        Bone bone = bones.get(b);
        if (bone==null)
            return new String[0];
        return bone.children.clone();
    }

    public String[] getAllBones() {
        return bones.keySet().toArray(new String[0]);
    }
    public Transform3D getTransform3D(String bone,double time) {
        Bone b = bones.get(bone);
        if (b==null)
            return null;
        int frame = (int)(time/frameTime);
        frame = (frame<0)?0:frame;
        frame = (frame>=frameCount)?frameCount-1:frame;
        return new Transform3D(b.transforms[frame]);
    }
    // 指定された骨で設定されているHIERARCHYパートのオフセットを返す。
    // モーションが付いていないデフォルトのポーズを構成するために使用する。
    public Vector3d getOffset(String b) {
        Bone bone = bones.get(b);
        if (bone==null)
            return null;
        return new Vector3d(bones.get(b).offset);
    }
    //指定された骨の子供の骨で設定されているHIERARCHYパートのオフセットの一覧を返す。
    //つまりは、この骨を描画する時に描画すべき線のtailの座標を返す。headは原点の
    //座標になる。普通に骨が繋っている場合はここで返される複数の座標は同じ値に
    //なるが、子供の骨がこの骨と離れている場合は、それぞれの子供の骨のheadに
    //あたる座標がtailとして返されるということになる。
    public ArrayList<Vector3d> getBoneTails(String b) {
        ArrayList<Vector3d> boneTails = bones.get(b).boneTails;
        if (boneTails==null)
            return null; //ありえないはずだけど念のため
        else
            return new ArrayList<Vector3d>(boneTails);
    }
    //最終的にBoneの情報を保存しておくためのクラス
    class Bone {
        String name;
        String parent;
        String children[];
        Transform3D transforms[];
        Vector3d offset = null;
        ArrayList<Vector3d> boneTails = null;
    }

    //チャンネルのタイプ
    enum ChannelType {
        PX,PY,PZ,
        RX,RY,RZ,
        SX,SY,SZ
    }
    //パースする時にBVHのBlockのデータをそのままの形で保存しておくための
    //クラス．Blockは最終的にはBoneに変換される．
    class Block {
        String name;
        String parent;
        ArrayList<String> children;
        double  offsetX;
        double  offsetY;
        double  offsetZ;
        Channel x_position;
        Channel y_position;
        Channel z_position;
        Channel z_rotation;
        Channel x_rotation;
        Channel y_rotation;
        Channel x_scale;
        Channel y_scale;
        Channel z_scale;
        ArrayList<ChannelType> channelOrder = new ArrayList<ChannelType>();
        ArrayList<Vector3d> boneTails = new ArrayList<Vector3d>();
    }

    //Channelのデータを保存するためのクラス
    //Channelは一つのBlock対して複数含まれる．
    class Channel {
        double data[];
    }

    //パース中に一時的に必要となるデータをまとめて
    //保存するためのクラス．少しでもメモリを節約して
    //みようというもがきクラス．
    class Tmp {
        int frameCount;
        double frameTime;
        ArrayList<Block> blockQueue;
        Stack<Block> blockStack;
        ArrayList<Channel> channelQueue;

        Block rootBlock;
        Block parentBlock;
        Block currentBlock;

        Tmp() {
            blockQueue = new ArrayList<Block>();
            blockStack = new Stack<Block>();
            channelQueue = new ArrayList<Channel>();
        }
        void channelsInit() {
            Iterator<Channel> i = channelQueue.iterator();
            while (i.hasNext()) {
                i.next().data = new double[frameCount];
            }
        }
    }

//------------------------------------------------------------
//以下BVHをパースするためのメソッド
//------------------------------------------------------------

    //以下文法解析の部分
    //tの中にあるparentBlock,currentBlock,blockStackあたりの同期は，
    //各ブロックのcaseA???のメソッドに入った瞬間にやることとする．
    //また，今回は混乱しそうなので，inA???,outA???のメソッドは
    //使わないことにする．
    public void caseABvh(ABvh node){
        node.getHierarchyPart().apply(this);
        node.getMotionPart().apply(this);
    }

    public void caseAHierarchyPart(AHierarchyPart node) {
        node.getRootBlock().apply(this);
    }

    public void caseARootBlock(ARootBlock node) {
        t.rootBlock = new Block();
        t.currentBlock = t.rootBlock;
        t.blockQueue.add(t.currentBlock);

        t.currentBlock.name = node.getIdentifier().getText();
        t.currentBlock.parent = null;
        t.currentBlock.children = new ArrayList<String>();

        node.getOffsetLine().apply(this);
        node.getChannelsLine().apply(this);

        Object temp[] = node.getSuperBlock().toArray();
        for(int i = 0; i < temp.length; i++) {
            ((PSuperBlock) temp[i]).apply(this);
        }
    }

    public void caseAOffsetLine(AOffsetLine node) {
        PNumber x_number = node.getXOffset();
        PNumber y_number = node.getYOffset();
        PNumber z_number = node.getZOffset();

        double x = pNumber2Double(x_number);
        double y = pNumber2Double(y_number);
        double z = pNumber2Double(z_number);
        t.currentBlock.offsetX = x;
        t.currentBlock.offsetY = y;
        t.currentBlock.offsetZ = z;
        if (t.parentBlock!=null)
            t.parentBlock.boneTails.add(new Vector3d(x,y,z));
    }

    public void caseAChannelsLine(AChannelsLine node) {
        TInteger integer = node.getInteger();
        int channelsCount = Integer.parseInt(integer.getText());

        Object temp[] = node.getChannelType().toArray();
        if (channelsCount != temp.length) {
            System.out.println("ActionGenerator.caseAChannelsBlock()");
            System.out.println("pars error!!!");
        }

        for (int i=0;i<temp.length;i++) {
            Channel c = new Channel();
            t.channelQueue.add(c);
            if (temp[i] instanceof AXPositionChannelType) {
                t.currentBlock.x_position = c;
                t.currentBlock.channelOrder.add(ChannelType.PX);
            } else if (temp[i] instanceof AYPositionChannelType) {
                t.currentBlock.y_position = c;
                t.currentBlock.channelOrder.add(ChannelType.PY);
            } else if (temp[i] instanceof AZPositionChannelType) {
                t.currentBlock.z_position = c;
                t.currentBlock.channelOrder.add(ChannelType.PZ);
            } else if (temp[i] instanceof AXRotationChannelType) {
                t.currentBlock.x_rotation = c;
                t.currentBlock.channelOrder.add(ChannelType.RX);
            } else if (temp[i] instanceof AYRotationChannelType) {
                t.currentBlock.y_rotation = c;
                t.currentBlock.channelOrder.add(ChannelType.RY);
            } else if (temp[i] instanceof AZRotationChannelType) {
                t.currentBlock.z_rotation = c;
                t.currentBlock.channelOrder.add(ChannelType.RZ);
            } else if (temp[i] instanceof AXScaleChannelType) {
                t.currentBlock.x_scale = c;
                t.currentBlock.channelOrder.add(ChannelType.SX);
            } else if (temp[i] instanceof AYScaleChannelType) {
                t.currentBlock.y_scale = c;
                t.currentBlock.channelOrder.add(ChannelType.SY);
            } else if (temp[i] instanceof AZScaleChannelType) {
                t.currentBlock.z_scale = c;
                t.currentBlock.channelOrder.add(ChannelType.SZ);
            }
        }
    }

    public void caseAJointBlock(AJointBlock node) {
        t.blockStack.push(t.parentBlock);
        t.parentBlock = t.currentBlock;
        t.currentBlock = new Block();
        t.blockQueue.add(t.currentBlock);

        t.currentBlock.name = node.getIdentifier().getText();
        t.currentBlock.parent = t.parentBlock.name;
        t.currentBlock.children = new ArrayList<String>();

        t.parentBlock.children.add(t.currentBlock.name);

        node.getOffsetLine().apply(this);
        node.getChannelsLine().apply(this);

        Object temp[] = node.getSuperBlock().toArray();
        for(int i = 0; i < temp.length; i++)
                ((PSuperBlock) temp[i]).apply(this);

        t.currentBlock = t.parentBlock;
        t.parentBlock = t.blockStack.pop();
    }

    public void caseAEndSiteBlock(AEndSiteBlock node) {
        t.blockStack.push(t.parentBlock);
        t.parentBlock = t.currentBlock;
        t.currentBlock = new Block();

        t.currentBlock.name = null;
        node.getOffsetLine().apply(this);

        t.currentBlock = t.parentBlock;
        t.parentBlock = t.blockStack.pop();
    }

    public void caseAMotionPart(AMotionPart node) {
        node.getFramesLine().apply(this);
        node.getFrameTimeLine().apply(this);

        t.channelsInit();

        Object temp[] = node.getNumber().toArray();
        //SableCC3.2のバグ(？)に対するハック
        //このハックが必要なければ上の一行ですむ話。
        //一応作ったけどやっぱり2.18.2を使うことにする．
        //忘れてまた同じことをしないようにコメントアウト
        //してとっておくことにする．
        /*
        LinkedList<PNumber64> temp64 = node.getNumber64();
        Object temp32 = node.getNumber32();
        Object temp16 = node.getNumber16();
        Object temp8 = node.getNumber8();
        Object temp4 = node.getNumber4();
        Object temp2 = node.getNumber2();
        Object temp1 = node.getNumber();
        int dataSize = temp64.size()*64;
        dataSize += (temp32!=null)?32:0;
        dataSize += (temp16!=null)?16:0;
        dataSize += (temp8!=null)?8:0;
        dataSize += (temp4!=null)?4:0;
        dataSize += (temp2!=null)?2:0;
        dataSize += (temp1!=null)?1:0;
        Object temp[] = new Object[dataSize];
        int index=0;
        for (PNumber64 pn64: temp64) {
            ANumber64 an64 = (ANumber64)pn64;
            temp[index+ 0]=an64.getN01();
            temp[index+ 1]=an64.getN02();
            temp[index+ 2]=an64.getN03();
            temp[index+ 3]=an64.getN04();
            temp[index+ 4]=an64.getN05();
            temp[index+ 5]=an64.getN06();
            temp[index+ 6]=an64.getN07();
            temp[index+ 7]=an64.getN08();
            temp[index+ 8]=an64.getN09();
            temp[index+ 9]=an64.getN10();
            temp[index+10]=an64.getN11();
            temp[index+11]=an64.getN12();
            temp[index+12]=an64.getN13();
            temp[index+13]=an64.getN14();
            temp[index+14]=an64.getN15();
            temp[index+15]=an64.getN16();
            temp[index+16]=an64.getN17();
            temp[index+17]=an64.getN18();
            temp[index+18]=an64.getN19();
            temp[index+19]=an64.getN20();
            temp[index+20]=an64.getN21();
            temp[index+21]=an64.getN22();
            temp[index+22]=an64.getN23();
            temp[index+23]=an64.getN24();
            temp[index+24]=an64.getN25();
            temp[index+25]=an64.getN26();
            temp[index+26]=an64.getN27();
            temp[index+27]=an64.getN28();
            temp[index+28]=an64.getN29();
            temp[index+29]=an64.getN30();
            temp[index+30]=an64.getN31();
            temp[index+31]=an64.getN32();
            temp[index+32]=an64.getN33();
            temp[index+33]=an64.getN34();
            temp[index+34]=an64.getN35();
            temp[index+35]=an64.getN36();
            temp[index+36]=an64.getN37();
            temp[index+37]=an64.getN38();
            temp[index+38]=an64.getN39();
            temp[index+39]=an64.getN40();
            temp[index+40]=an64.getN41();
            temp[index+41]=an64.getN42();
            temp[index+42]=an64.getN43();
            temp[index+43]=an64.getN44();
            temp[index+44]=an64.getN45();
            temp[index+45]=an64.getN46();
            temp[index+46]=an64.getN47();
            temp[index+47]=an64.getN48();
            temp[index+48]=an64.getN49();
            temp[index+49]=an64.getN50();
            temp[index+50]=an64.getN51();
            temp[index+51]=an64.getN52();
            temp[index+52]=an64.getN53();
            temp[index+53]=an64.getN54();
            temp[index+54]=an64.getN55();
            temp[index+55]=an64.getN56();
            temp[index+56]=an64.getN57();
            temp[index+57]=an64.getN58();
            temp[index+58]=an64.getN59();
            temp[index+59]=an64.getN60();
            temp[index+60]=an64.getN61();
            temp[index+61]=an64.getN62();
            temp[index+62]=an64.getN63();
            temp[index+63]=an64.getN64();
            index+=64;
        }
        if (temp32!=null) {
            ANumber32 an32 = (ANumber32)temp32;
            temp[index+ 0]=an32.getN01();
            temp[index+ 1]=an32.getN02();
            temp[index+ 2]=an32.getN03();
            temp[index+ 3]=an32.getN04();
            temp[index+ 4]=an32.getN05();
            temp[index+ 5]=an32.getN06();
            temp[index+ 6]=an32.getN07();
            temp[index+ 7]=an32.getN08();
            temp[index+ 8]=an32.getN09();
            temp[index+ 9]=an32.getN10();
            temp[index+10]=an32.getN11();
            temp[index+11]=an32.getN12();
            temp[index+12]=an32.getN13();
            temp[index+13]=an32.getN14();
            temp[index+14]=an32.getN15();
            temp[index+15]=an32.getN16();
            temp[index+16]=an32.getN17();
            temp[index+17]=an32.getN18();
            temp[index+18]=an32.getN19();
            temp[index+19]=an32.getN20();
            temp[index+20]=an32.getN21();
            temp[index+21]=an32.getN22();
            temp[index+22]=an32.getN23();
            temp[index+23]=an32.getN24();
            temp[index+24]=an32.getN25();
            temp[index+25]=an32.getN26();
            temp[index+26]=an32.getN27();
            temp[index+27]=an32.getN28();
            temp[index+28]=an32.getN29();
            temp[index+29]=an32.getN30();
            temp[index+30]=an32.getN31();
            temp[index+31]=an32.getN32();
            index+=32;
        }
        if (temp16!=null) {
            ANumber16 an16 = (ANumber16)temp16;
            temp[index+ 0]=an16.getN01();
            temp[index+ 1]=an16.getN02();
            temp[index+ 2]=an16.getN03();
            temp[index+ 3]=an16.getN04();
            temp[index+ 4]=an16.getN05();
            temp[index+ 5]=an16.getN06();
            temp[index+ 6]=an16.getN07();
            temp[index+ 7]=an16.getN08();
            temp[index+ 8]=an16.getN09();
            temp[index+ 9]=an16.getN10();
            temp[index+10]=an16.getN11();
            temp[index+11]=an16.getN12();
            temp[index+12]=an16.getN13();
            temp[index+13]=an16.getN14();
            temp[index+14]=an16.getN15();
            temp[index+15]=an16.getN16();
            index+=16;
        }
        if (temp8!=null) {
            ANumber8 an8 = (ANumber8)temp8;
            temp[index+0]=an8.getN01();
            temp[index+1]=an8.getN02();
            temp[index+2]=an8.getN03();
            temp[index+3]=an8.getN04();
            temp[index+4]=an8.getN05();
            temp[index+5]=an8.getN06();
            temp[index+6]=an8.getN07();
            temp[index+7]=an8.getN08();
            index+=8;
        }
        if (temp4!=null) {
            ANumber4 an4 = (ANumber4)temp4;
            temp[index+0]=an4.getN01();
            temp[index+1]=an4.getN02();
            temp[index+2]=an4.getN03();
            temp[index+3]=an4.getN04();
            index+=4;
        }
        if (temp2!=null) {
            ANumber2 an2 = (ANumber2)temp2;
            temp[index+0]=an2.getN01();
            temp[index+1]=an2.getN02();
            index+=2;
        }
        if (temp1!=null) {
            temp[index+0]=temp1;
            index+=1;
        }
        */
        //SableCC3.2のバグ(？)に対するハックここまで。
        if (temp.length != (t.channelQueue.size()*t.frameCount)) {
            System.out.println("ActionGenerator.caseAMotionPart().");
            System.out.println("parse error!!!");
            System.out.print(temp.length);
            System.out.print(" and "+t.channelQueue.size()*t.frameCount);
            System.out.println(" do not match.");
            return;
        }

        int tempCount = 0;
        for (int i=0;i<t.frameCount;i++) {
            for (int j=0;j<t.channelQueue.size();j++) {
                PNumber pn = (PNumber)temp[tempCount++];
                double d = pNumber2Double(pn);
                t.channelQueue.get(j).data[i] = d;
            }
        }
    }

    public void caseAFramesLine(AFramesLine node) {
        TInteger integer = node.getInteger();
        String is = integer.getText();
        t.frameCount = Integer.parseInt(is);
    }

    public void caseAFrameTimeLine(AFrameTimeLine node) {
        PNumber num = node.getNumber();
        t.frameTime = pNumber2Double(num);
    }

//----------

    protected double pNumber2Double(PNumber num) {
        if (num instanceof ARealNumberNumber) {
            TRealNumber rn = ((ARealNumberNumber)num).getRealNumber();
            String rns = rn.getText();
            return Double.parseDouble(rns);
        } else {
            TInteger integer = ((AIntegerNumber)num).getInteger();
            int iii = Integer.parseInt(integer.getText());
            return (double)iii;
        }
    }

//------------------------------------------------------------
//BVHの生データをTransform3Dに変換する．
//------------------------------------------------------------

    void convert() {
        frameCount = t.frameCount;
        frameTime = t.frameTime;
        rootBone = t.rootBlock.name;

        //たぶん，ループの中でnewすると無駄が多くなるのでここで宣言しておく
        Transform3D t1 = new Transform3D();
        Transform3D t2 = new Transform3D();

        Iterator<Block> i = t.blockQueue.iterator();
        while (i.hasNext()) {
            Block block = i.next();
            Bone bone = new Bone();
            bone.name = block.name;
            bone.parent = block.parent;
            bone.children = block.children.toArray(new String[0]);
            bone.transforms = new Transform3D[frameCount];
            bone.offset = new Vector3d(block.offsetX,block.offsetY,block.offsetZ);
            bone.boneTails = block.boneTails;
            for (int j=0;j<frameCount;j++) {
                Transform3D t0 = new Transform3D();
                t0.setTranslation(new Vector3d(block.offsetX,block.offsetY,block.offsetZ));

                t1.setIdentity();
                for (ChannelType ct : block.channelOrder){
                    t2.setIdentity();
                    if (ct==ChannelType.PX)
                        t2.setTranslation(new Vector3d(block.x_position.data[j],0.0,0.0));
                    else if (ct==ChannelType.PY)
                        t2.setTranslation(new Vector3d(0.0,block.y_position.data[j],0.0));
                    else if (ct==ChannelType.PZ)
                        t2.setTranslation(new Vector3d(0.0,0.0,block.z_position.data[j]));
                    else if (ct==ChannelType.RX)
                        t2.rotX(block.x_rotation.data[j]/360.0*2.0*Math.PI);
                    else if (ct==ChannelType.RY)
                        t2.rotY(block.y_rotation.data[j]/360.0*2.0*Math.PI);
                    else if (ct==ChannelType.RZ)
                        t2.rotZ(block.z_rotation.data[j]/360.0*2.0*Math.PI);
                    else if (ct==ChannelType.SX)
                        t2.setScale(new Vector3d(block.x_scale.data[j],1.0,1.0));
                    else if (ct==ChannelType.SY)
                        t2.setScale(new Vector3d(1.0,block.y_scale.data[j],1.0));
                    else if (ct==ChannelType.SZ)
                        t2.setScale(new Vector3d(1.0,1.0,block.z_scale.data[j]));
                    t1.mul(t1,t2);
                }

                t0.mul(t1);
                bone.transforms[j] = t0;
            }
            bones.put(bone.name,bone);
        }
    }
}
