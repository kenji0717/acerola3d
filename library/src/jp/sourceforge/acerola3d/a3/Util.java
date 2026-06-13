package jp.sourceforge.acerola3d.a3;

import java.net.URL;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;
import com.sun.j3d.loaders.Scene;
import com.bulletphysics.util.ObjectArrayList;
import static java.lang.Math.*;
import java.nio.*;

/**
 * a3パッケージのユーティリティクラス。
 */
public class Util {
    /**
     * このシステムで使用している一切のリソースを開放します。未実装。
     */
    public static void cleanUp() {
        ;
    }

//================================================================================

    /**
     * 自前の四元数どうしのかけ算(合成)。javax.vecmath.Quat4dのjavadocに
     * The quaternion is always normalized.と書いてあるので不安になって
     * 作ってみたけどmulはnormalizeされないっぽいので、特に意味なし。
     * (でもQuat4dのコンストラクタは正規化する)
     */
    public static Quat4d mul(Quat4d a,Quat4d b) {
        Quat4d ret = new Quat4d();
        ret.x = a.w*b.x + a.x*b.w + a.y*b.z - a.z*b.y;
        ret.y = a.w*b.y - a.x*b.z + a.y*b.w + a.z*b.x;
        ret.z = a.w*b.z + a.x*b.y - a.y*b.x + a.z*b.w;
        ret.w = a.w*b.w - a.x*b.x - a.y*b.y - a.z*b.z;
        return ret;
    }

    /**
     * 与えられた四元数を用いて与えられたベクトルを原点中心に回転(拡大縮小)させた
     * ベクトルを返します。
     */
    public static Vector3d trans(Quat4d q,Vector3d v) {
        //Quat4d qc = new Quat4d(-q.x,-q.y,-q.z,q.w);//これだと正規化されてダメ
        Quat4d qc = new Quat4d();
        qc.x=-q.x;qc.y=-q.y;qc.z=-q.z;qc.w=q.w;
        //Quat4d vq = new Quat4d(v.x,v.y,v.z,0.0);//これだと正規化されてダメ
        Quat4d vq = new Quat4d();
        vq.x=v.x;vq.y=v.y;vq.z=v.z;vq.w=0.0;
        vq.mul(q,vq);
        vq.mul(qc);
        return new Vector3d(vq.x,vq.y,vq.z);
    }

    /**
     * オイラー角を四元数に変換するメソッド。
     * オイラー角はz軸,x軸,y軸の順で回転させる場合を想定しています。
     */
    public static Quat4d euler2quat(Vector3d v) {
        return euler2quat(v.x,v.y,v.z);
    }
    /**
     * オイラー角を四元数に変換するメソッド。
     * オイラー角はz軸,x軸,y軸の順で回転させる場合を想定しています。
     */
    public static Quat4d euler2quat(double x,double y,double z) {
        Quat4d q = new Quat4d(0.0,0.0,0.0,1.0);
        q.mul(new Quat4d(       0.0,sin(y/2.0),       0.0,cos(y/2.0)));
        q.mul(new Quat4d(sin(x/2.0),       0.0,       0.0,cos(x/2.0)));
        q.mul(new Quat4d(       0.0,       0.0,sin(z/2.0),cos(z/2.0)));
        return q;
    }
    static Quat4d euler2quat_OLD(Vector3d v) {
        Transform3D t0 = new Transform3D();
        Transform3D t1 = new Transform3D();
        //2010,01/18:順番変更 z,y,x -> z,x,y
        t1.rotY(v.y);
        t0.mul(t1);
        t1.rotX(v.x);
        t0.mul(t1);
        t1.rotZ(v.z);
        t0.mul(t1);
        Quat4d q = new Quat4d();
        t0.get(q);
        return q;
    }

    /**
     * 四元数をオイラー角に変換するメソッド。
     * オイラー角はz軸,x軸,y軸の順で回転させる場合を想定しています。
     */
    public static Vector3d quat2euler(Quat4d q) {
        Matrix3d m = new Matrix3d();
        m.set(q);
        //m.normalize();
        Vector3d ret = new Vector3d();
        if (m.m12==1.0) {
            ret.x = PI/2.0;
            ret.y = atan2(-m.m20,m.m00);
            ret.z = 0.0;
        } else if (m.m12==-1.0) {
            ret.x = -PI/2.0;
            ret.y = atan2(-m.m20,m.m00);
            ret.z = 0.0;
        } else {
            ret.x = asin(-m.m12);
            ret.y = atan2(m.m02,m.m22);
            ret.z = atan2(m.m10,m.m11);
        }
        return ret;
    }
    //http://d.hatena.ne.jp/It_lives_vainly/20070829/1188384519
    //上のページではy,x,zの順番だけど逆のz,x,yにして計算しなおしてやった。

    /**
     * 四元数を行列に変換するメソッドです。javax.vecmathパッケージにも
     * 同じ機能の変換メソッドが用意されていますが、簡単なので
     * 自前実装を作りました。
     */
    public static Matrix3d quat2matrix(Quat4d q) {
        Matrix3d m = new Matrix3d();
        m.m00 = 1.0 - 2.0 * q.y * q.y - 2.0 * q.z * q.z;
        m.m01 = 2.0 * q.x * q.y - 2.0 * q.w * q.z;
        m.m02 = 2.0 * q.x * q.z + 2.0 * q.w * q.y;

        m.m10 = 2.0 * q.x * q.y + 2.0 * q.w * q.z;
        m.m11 = 1.0 - 2.0 * q.x * q.x - 2.0 * q.z * q.z;
        m.m12 = 2.0 * q.y * q.z - 2.0 * q.w * q.x;

        m.m20 = 2.0 * q.x * q.z - 2.0 * q.w * q.y;
        m.m21 = 2.0 * q.y * q.z + 2.0 * q.w * q.x;
        m.m22 = 1.0 - 2.0 * q.x * q.x - 2.0 * q.y * q.y;
        return m;
    }

    /**
     * 行列を四元数に変換するメソッドです(Matrix3d版)。javax.vecmathパッケージにも
     * 同じ機能の変換メソッドが用意されていますが、少し気になるところがあるので
     * 自前実装を作りました。
     */
    public static Quat4d matrix2quat(Matrix3d m) {
        //http://marupeke296.com/DXG_No58_RotQuaternionTrans.html

        // 最大成分を検索
        double elem[] = new double[4]; // 0:x, 1:y, 2:z, 3:w
        elem[0] =  m.m00 - m.m11 - m.m22 + 1.0;
        elem[1] = -m.m00 + m.m11 - m.m22 + 1.0;
        elem[2] = -m.m00 - m.m11 + m.m22 + 1.0;
        elem[3] =  m.m00 + m.m11 + m.m22 + 1.0;

        int biggestIndex = 0;
        for (int i=1;i<4;i++) {
            if (elem[i] > elem[biggestIndex])
                biggestIndex = i;
        }

        if (elem[biggestIndex]<0.0) {
            //引数の行列に間違いあり！
            //どうすりゃ良いのかわからない．
            return null;
        }

        // 最大要素の値を算出
        double v = sqrt( elem[biggestIndex] ) * 0.5;
        double mult = 0.25 / v;
        Quat4d q = new Quat4d();

        switch (biggestIndex) {
        case 0: // x
            q.x = v;
            q.y = (m.m10 + m.m01) * mult;
            q.z = (m.m02 + m.m20) * mult;
            q.w = (m.m21 - m.m12) * mult;
            break;
        case 1: // y
            q.x = (m.m10 + m.m01) * mult;
            q.y = v;
            q.z = (m.m21 + m.m12) * mult;
            q.w = (m.m02 - m.m20) * mult;
            break;
        case 2: // z
            q.x = (m.m02 + m.m20) * mult;
            q.y = (m.m21 + m.m12) * mult;
            q.z = v;
            q.w = (m.m10 - m.m01) * mult;
            break;
        case 3: // w
            q.x = (m.m21 - m.m12) * mult;
            q.y = (m.m02 - m.m20) * mult;
            q.z = (m.m10 - m.m01) * mult;
            q.w = v;
            break;
        }

        return q;
    }

    /**
     * 行列を四元数に変換するメソッドです(Matrix3f版)。javax.vecmathパッケージにも
     * 同じ機能の変換メソッドが用意されていますが、少し気になるところがあるので
     * 自前実装を作りました。
     */
    public static Quat4d matrix2quat(Matrix3f m) {
        //http://marupeke296.com/DXG_No58_RotQuaternionTrans.html

        // 最大成分を検索
        double elem[] = new double[4]; // 0:x, 1:y, 2:z, 3:w
        elem[0] =  m.m00 - m.m11 - m.m22 + 1.0;
        elem[1] = -m.m00 + m.m11 - m.m22 + 1.0;
        elem[2] = -m.m00 - m.m11 + m.m22 + 1.0;
        elem[3] =  m.m00 + m.m11 + m.m22 + 1.0;

        int biggestIndex = 0;
        for (int i=1;i<4;i++) {
            if (elem[i] > elem[biggestIndex])
                biggestIndex = i;
        }

        if (elem[biggestIndex]<0.0) {
            //引数の行列に間違いあり！
            //どうすりゃ良いのかわからない．
            return null;
        }

        // 最大要素の値を算出
        double v = sqrt( elem[biggestIndex] ) * 0.5;
        double mult = 0.25 / v;
        Quat4d q = new Quat4d();

        switch (biggestIndex) {
        case 0: // x
            q.x = v;
            q.y = (m.m10 + m.m01) * mult;
            q.z = (m.m02 + m.m20) * mult;
            q.w = (m.m21 - m.m12) * mult;
            break;
        case 1: // y
            q.x = (m.m10 + m.m01) * mult;
            q.y = v;
            q.z = (m.m21 + m.m12) * mult;
            q.w = (m.m02 - m.m20) * mult;
            break;
        case 2: // z
            q.x = (m.m02 + m.m20) * mult;
            q.y = (m.m21 + m.m12) * mult;
            q.z = v;
            q.w = (m.m10 - m.m01) * mult;
            break;
        case 3: // w
            q.x = (m.m21 - m.m12) * mult;
            q.y = (m.m02 - m.m20) * mult;
            q.z = (m.m10 - m.m01) * mult;
            q.w = v;
            break;
        }

        return q;
    }

    /**
     * 点aを点bに移す原点中心の回転を表す四元数を作成します。
     * a,bの長さが異なっていれば作成される四元数には拡大縮小率も含みます。
     * ただし、このメソッドで作成される回転の四元数は点aを点bに移す
     * 最もすなおな回転なので、上方向ベクトルなどは考慮されない物に
     * なっています。
     */
    public static Quat4d a2bQuat(Vector3d a,Vector3d b) {
        double aLen = a.length();
        double bLen = b.length();
        double ct = a.dot(b)/aLen/bLen;//cos(Θ)
        double ct2 = sqrt(0.5*(1+ct));//cos(Θ/2)
        double st2 = sqrt(0.5*(1-ct));//sin(Θ/2)
        double k = sqrt(bLen/aLen);
        Vector3d na = new Vector3d(a);
        na.normalize();
        Vector3d nb = new Vector3d(b);
        nb.normalize();
        Vector3d jiku = new Vector3d();
        jiku.cross(na,nb);
        if (jiku.length()<0.00001) {
            if (ct>0.0) {
                //return new Quat4d(0.0,0.0,0.0,k*1.0);//これだと正規化されてkが効かない
                Quat4d ret = new Quat4d();
                ret.x=ret.y=ret.z=0.0;ret.w=k;
                return ret;
            } else {
                //return new Quat4d(0.0,k*1.0,0.0,0.0);//これだと正規化されてkが効かない
                Quat4d ret = new Quat4d();
                ret.x=ret.z=ret.w=0.0;ret.y=k;
                return ret;
            }
        }
        jiku.normalize();
        //return new Quat4d(k*st2*jiku.x,k*st2*jiku.y,k*st2*jiku.z,k*ct2);//これだと正規化されてkが効かない
        Quat4d ret = new Quat4d();
        ret.x = k*st2*jiku.x;
        ret.y = k*st2*jiku.y;
        ret.z = k*st2*jiku.z;
        ret.w = k*ct2;
        return ret;
    }

    /**
     * oldFrontベクトルとoldTopベクトルで表される座標系を、上方向ベクトル(up)を
     * 考慮して、newFrontベクトルがZ軸の正の方向となるような座標系に移す
     * 原点中心の回転を表す四元数を作成します。ただし、a2bQuatと異なり
     * 拡大縮小率は考慮されず回転成分のみの四元数を返します。oldFront,oldTop,newFront,up
     * の各ベクトルは0ベクトルでない物を指定して下さい。そしてoldFrontとoldTopは
     * 直行していなければなりません。
     */
    public static Quat4d frontFacingQuat(Vector3d oldFront,Vector3d oldTop,Vector3d newFront,Vector3d up) {
        Vector3d oldFrontN = new Vector3d(oldFront);
        oldFrontN.normalize();
        Vector3d oldTopN = new Vector3d(oldTop);
        oldTopN.normalize();
        Vector3d oldRightN = new Vector3d();
        oldRightN.cross(oldFrontN,oldTopN);
        Vector3d newFrontN = new Vector3d(newFront);
        newFrontN.normalize();
        Vector3d upN = new Vector3d(up);
        upN.normalize();

        //old座標系を通常座標系に移す行列をaに作る
        Matrix3d a = new Matrix3d();
        a.m00 = oldRightN.x; a.m01 = oldTopN.x; a.m02 = oldFrontN.x; 
        a.m10 = oldRightN.y; a.m11 = oldTopN.y; a.m12 = oldFrontN.y; 
        a.m20 = oldRightN.z; a.m21 = oldTopN.z; a.m22 = oldFrontN.z;
        a.invert();

        //newTopNを作る
        double dTmp = upN.dot(newFrontN);
        Vector3d vTmp1 = new Vector3d(newFrontN);
        vTmp1.scale(dTmp);
        Vector3d newTopN = new Vector3d(upN);
        newTopN.sub(vTmp1);
        newTopN.normalize();
        //
        Vector3d newRightN = new Vector3d();
        newRightN.cross(newFrontN,newTopN);
        //通常座標系をnew座標系に移す行列をbに作る
        Matrix3d b = new Matrix3d();
        b.m00 = newRightN.x; b.m01 = newTopN.x; b.m02 = newFrontN.x; 
        b.m10 = newRightN.y; b.m11 = newTopN.y; b.m12 = newFrontN.y; 
        b.m20 = newRightN.z; b.m21 = newTopN.z; b.m22 = newFrontN.z;

        b.mul(a);
        Quat4d q = matrix2quat(b);
        
        return q;
    }

    /**
     * 正面(0,0,1)、上(0,1,0)、右(-1,0,0)の座標系の点を
     * 上方向ベクトル(up)を考慮してfrontベクトルが正面となる座標系の点に
     * 変換するための四元数を作成するメソッドです。
     * 拡大縮小率は反映されません。引数のベクトルには0ベクトルを与えないで下さい。
     * 主にY軸が上になるデフォルトの座標系におけるA3Objectの回転を計算するのに
     * Acerola3D内部で使用する目的のメソッドですが、汎用のfrontFacingQuatより
     * 計算量が少ないですし、ついでなので公開しています。
     */
    public static Quat4d frontFacingQuat_A3Y(Vector3d front,Vector3d up) {
        Vector3d frontN = new Vector3d(front);
        Vector3d upN = new Vector3d(up);
        upN.normalize();
        frontN.normalize();

        double d = frontN.dot(upN);
        Vector3d vTmp = new Vector3d(frontN);
        vTmp.scale(d);
        Vector3d top = new Vector3d(upN);
        top.sub(vTmp);
        if (top.lengthSquared()<0.00001) {
            //この場合とりあえず適当
            frontN.set(0.0,0.0,1.0);
            top.set(0.0,1.0,0.0);
        } else {
            top.normalize();
        }
        Vector3d right = new Vector3d();
        right.cross(frontN,top);

        Matrix3d m = new Matrix3d();
        m.m00 = -right.x;m.m01 = top.x;m.m02 = frontN.x;
        m.m10 = -right.y;m.m11 = top.y;m.m12 = frontN.y;
        m.m20 = -right.z;m.m21 = top.z;m.m22 = frontN.z;

        Quat4d q = matrix2quat(m);
        return q;
    }
    /**
     * 正面(0,-1,0)、上(0,0,1)、右(-1,0,0)の座標系の点を
     * 上方向ベクトル(up)を考慮してfrontベクトルが正面となる座標系の点に
     * 変換するための四元数を作成するメソッドです。
     * 拡大縮小率は反映されません。引数のベクトルには0ベクトルを与えないで下さい。
     * 主にZ軸が上になる座標系におけるA3Objectの回転を計算するのに
     * Acerola3D内部で使用する目的のメソッドですが、汎用のfrontFacingQuatより
     * 計算量が少いですし、ついでなので公開しています。
     */
    public static Quat4d frontFacingQuat_A3Z(Vector3d front,Vector3d up) {
        Vector3d frontN = new Vector3d(front);
        Vector3d upN = new Vector3d(up);
        upN.normalize();
        frontN.normalize();

        double d = frontN.dot(upN);
        Vector3d vTmp = new Vector3d(frontN);
        vTmp.scale(d);
        Vector3d top = new Vector3d(upN);
        top.sub(vTmp);
        if (top.lengthSquared()<0.00001) {
            //この場合とりあえず適当
            frontN.set(0.0,-1.0,0.0);
            top.set(0.0,0.0,1.0);
        } else {
            top.normalize();
        }
        Vector3d right = new Vector3d();
        right.cross(frontN,top);

        Matrix3d m = new Matrix3d();
        m.m00 = -right.x;m.m01 = -frontN.x;m.m02 = top.x;
        m.m10 = -right.y;m.m11 = -frontN.y;m.m12 = top.y;
        m.m20 = -right.z;m.m21 = -frontN.z;m.m22 = top.z;

        Quat4d q = matrix2quat(m);
        return q;
    }
    /**
     * 正面(0,0,-1)、上(0,1,0)、右(1,0,0)の座標系の点を
     * 上方向ベクトル(up)を考慮してfrontベクトルが正面となる座標系の点に
     * 変換するための四元数を作成するメソッドです。
     * 拡大縮小率は反映されません。引数のベクトルには0ベクトルを与えないで下さい。
     * 主にカメラの回転を計算するのにAcerola3D内部で使用する目的のメソッドですが、
     * 汎用のfrontFacingQuatより計算量が少ないですし、ついでなので公開しています。
     */
    public static Quat4d frontFacingQuat_CAMERA(Vector3d front,Vector3d up) {
        Vector3d frontN = new Vector3d(front);
        Vector3d upN = new Vector3d(up);
        upN.normalize();
        frontN.normalize();

        double d = frontN.dot(upN);
        Vector3d vTmp = new Vector3d(frontN);
        vTmp.scale(d);
        Vector3d top = new Vector3d(upN);
        top.sub(vTmp);
        if (top.lengthSquared()<0.00001) {
            //この場合とりあえず適当
            frontN.set(0.0,0.0,-1.0);
            top.set(0.0,1.0,0.0);
        } else {
            top.normalize();
        }
        Vector3d right = new Vector3d();
        right.cross(frontN,top);

        Matrix3d m = new Matrix3d();
        m.m00 = right.x;m.m01 = top.x;m.m02 = -frontN.x;
        m.m10 = right.y;m.m11 = top.y;m.m12 = -frontN.y;
        m.m20 = right.z;m.m21 = top.z;m.m22 = -frontN.z;

        Quat4d q = matrix2quat(m);
        return q;
    }
  //垂直軸まわりの回転：ヨー(yaw)
  //左右軸まわりの回転：ピッチ(pitch)
  //前後軸まわりの回転：ロール(roll)

    /**
     * デグリーのオイラー角からラジアンのオイラー角への変換。
     */
    public static Vector3d rev2rot(Vector3d rev) {
        Vector3d ret = new Vector3d(rev);
        ret.scale(Math.PI/180.0);
        return ret;
    }

    /**
     * ラジアンのオイラー角からデグリーのオイラー角への変換。
     */
    public static Vector3d rot2rev(Vector3d rot) {
        Vector3d ret = new Vector3d(rot);
        ret.scale(180.0/Math.PI);
        return ret;
    }
//================================================================================

    /**
     * 引数として与えられたjavax.media.j3d.Nodeの形状に対応する
     * com.bulletphysics.collision.shapes.ConvexHullShapeを
     * 生成します．
     */
    static public ConvexHullShape makeConvexHullShape(Node n) {
        ObjectArrayList<Vector3f> vertexes1 = new ObjectArrayList<Vector3f>();
        Transform3D t0 = new Transform3D();
        t0.setIdentity();
        listingVertexes(n,vertexes1,t0);
        ConvexHullShape chs1 = new ConvexHullShape(vertexes1);
        float margin = chs1.getMargin();
        ShapeHull sh = new ShapeHull(chs1);
        sh.buildHull(margin);
        ObjectArrayList<Vector3f> vertexes2 = sh.getVertexPointer();
//System.out.println("v1:"+vertexes1.size());
//System.out.println("v2:"+vertexes2.size());
        ConvexHullShape chs2 = new ConvexHullShape(vertexes2);
        return chs2;
    }
    /**
     * 引数として与えられたjavax.media.j3d.Nodeに含まれる点の集合を
     * com.bulletphysics.util.ObjectArrayList＜Vector3f＞で返します。
     */
    public static ObjectArrayList<Vector3f> listingVertexes(Node n) {
        ObjectArrayList<Vector3f> vertexes1 = new ObjectArrayList<Vector3f>();
        Transform3D t0 = new Transform3D();
        t0.setIdentity();
        listingVertexes(n,vertexes1,t0);
        return vertexes1;
    }
    static void listingVertexes(Node n,ObjectArrayList<Vector3f> vertexes,Transform3D t0) {
        if (n instanceof Shape3D) {
            Shape3D s = (Shape3D)n;
            Transform3D t = new Transform3D();
            try {
                s.getLocalToVworld(t);
            } catch(Exception ee) {
                //ee.printStackTrace();
            }
            //System.out.println("t:"+t.toString());
            int numGeo = s.numGeometries();
            for (int i=0;i<numGeo;i++) {
                Geometry geo = s.getGeometry(i);
                if (geo instanceof GeometryArray) {
                    GeometryArray ga = (GeometryArray)geo;
                    try {
                        int ici = ga.getInitialCoordIndex();
                        int vvc = ga.getValidVertexCount();
                        Point3f p = new Point3f();
                        for (int j=ici;j<ici+vvc;j++) {
                            ga.getCoordinate(j,p);
                            t.transform(p);
                            vertexes.add(new Vector3f(p));
                        }
                    } catch(Exception ee) {
                        //ee.printStackTrace();
                    }
                } else {
                    System.out.println("Util.listingVertexes():"+geo.getClass().getName());
                    System.out.print("  "+geo.getClass().getName());
                    System.out.println(" is not GeometryArray.");
                }
            }
        } else if (n instanceof Group) {
            Enumeration<?> e = ((Group)n).getAllChildren();
            while (e.hasMoreElements()) {
                listingVertexes((Node)e.nextElement(),vertexes,t0);
            }
        } else if (n instanceof Link) {
            Transform3D t = new Transform3D();
            try {
                n.getLocalToVworld(t);
            } catch(Exception ee) {
                //ee.printStackTrace();
            }
            t.mul(t0,t);
            SharedGroup sg = ((Link)n).getSharedGroup();
            Enumeration<?> e = sg.getAllChildren();
            while (e.hasMoreElements()) {
                listingVertexes((Node)e.nextElement(),vertexes,t);
            }
        } else if (n instanceof Behavior) {
            ;
        } else {
            System.out.print("Util.listingVertexes().???: ");
            System.out.println(n.getClass().getName());
        }
    }
//================================================================================

    static TriangleIndexVertexArray makeTriangleIndexVertexArray(Node n) {
        int vertStride = 4 * 3 /* sizeof(btVector3) */;
        int indexStride = 3 * 4 /* 3*sizeof(int) */;

        int iTmp[] = countVertsAndTriangles(n);

        int totalVerts = iTmp[0];
        int totalTriangles = iTmp[1];

//System.out.println("totalVerts:"+totalVerts);
//System.out.println("totalTriangles:"+totalTriangles);

        ByteBuffer vertices = ByteBuffer.allocateDirect(totalVerts * vertStride).order(ByteOrder.nativeOrder());
        ByteBuffer gIndices = ByteBuffer.allocateDirect(totalTriangles * 3 * 4).order(ByteOrder.nativeOrder());
        Transform3D t0 = new Transform3D();
        t0.setIdentity();
        makeVertsAndTriangles(n,vertices,gIndices,0,t0);

        TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(totalTriangles,
                gIndices,
                indexStride,
                totalVerts, vertices, vertStride);

        return indexVertexArrays;
    }

    /**
     * 引数として与えられたjavax.media.j3d.Nodeの形状に対応する
     * com.bulletphysics.collision.shapes.BvhTriangleMeshShapeを
     * 生成します．
     */
    public static BvhTriangleMeshShape makeBvhTriangleMeshShape(Node n) {
        TriangleIndexVertexArray indexVertexArrays = makeTriangleIndexVertexArray(n);

        boolean useQuantizedAabbCompression = true;
        BvhTriangleMeshShape shape = new BvhTriangleMeshShape(indexVertexArrays, useQuantizedAabbCompression);
        return shape;
    }

    /**
     * 引数として与えられたjavax.media.j3d.Nodeの形状に対応する
     * com.bulletphysics.extras.gimpact.GImpactMeshShapeを
     * 生成します．
     */
    public static GImpactMeshShape makeGImpactMeshShape(Node n) {
        TriangleIndexVertexArray indexVertexArrays = makeTriangleIndexVertexArray(n);

        GImpactMeshShape shape = new GImpactMeshShape(indexVertexArrays);
        shape.updateBound();
        return shape;
    }

    static int[] countVertsAndTriangles(Node n) {
        int verCount=0;
        int triCount=0;
        if (n instanceof Shape3D) {
            Shape3D s = (Shape3D)n;
            int numGeo = s.numGeometries();
            for (int i=0;i<numGeo;i++) {
                Geometry geo = s.getGeometry(i);
                int triCountTmp = 0;
                int verCountTmp = 0;
                if (geo instanceof GeometryArray) {
                    GeometryArray ga = (GeometryArray)geo;
                    int vvc = ga.getValidVertexCount();
                    if (ga instanceof QuadArray) {
                        triCountTmp = vvc/4*2;
                        verCountTmp = (vvc/4*2)*3;
                    } else if (ga instanceof TriangleFanArray) {
                        int ns = ((TriangleFanArray)ga).getNumStrips();
                        triCountTmp = vvc-2*ns;
                        verCountTmp = (vvc-2*ns)*3;
                    } else if (ga instanceof TriangleArray) {
                        triCountTmp = vvc/3;
                        verCountTmp = vvc;
                    } else if (ga instanceof LineStripArray) {
                        triCountTmp = vvc/2;
                        verCountTmp = (vvc/2)*3;
                    } else {
                        System.out.println("GAHA1");
                    }
                } else {
                    System.out.println("GAHA2");
                }
                verCount += verCountTmp;
                triCount += triCountTmp;
            }
        } else if (n instanceof Group) {
            Enumeration<?> e = ((Group)n).getAllChildren();
            while (e.hasMoreElements()) {
                int iTmp[] = countVertsAndTriangles((Node)e.nextElement());
                verCount += iTmp[0];
                triCount += iTmp[1];
            }
        } else if (n instanceof Link) {
            SharedGroup sg = ((Link)n).getSharedGroup();
            Enumeration<?> e = sg.getAllChildren();
            while (e.hasMoreElements()) {
                int iTmp[] = countVertsAndTriangles((Node)e.nextElement());
                verCount += iTmp[0];
                triCount += iTmp[1];
            }
        } else if (n instanceof Behavior) {
            ;
        } else {
            System.out.println("GAHA3");
            System.out.println(n.getClass().getName());
        }
        int ret[] = new int[2];
        ret[0] = verCount;
        ret[1] = triCount;
        return ret;
    }

    static int makeVertsAndTriangles(Node n,ByteBuffer vertices,ByteBuffer gIndices,int idx,Transform3D t0) {
        if (n instanceof Shape3D) {
            Shape3D s = (Shape3D)n;
            Transform3D t = new Transform3D();
            try {
                s.getLocalToVworld(t);
            } catch(Exception ee) {
                ee.printStackTrace();
            }
            t.mul(t0,t);
            int numGeo = s.numGeometries();
            for (int i=0;i<numGeo;i++) {
                Geometry geo = s.getGeometry(i);
                if (geo instanceof GeometryArray) {
                    GeometryArray ga = (GeometryArray)geo;
                    if (ga instanceof QuadArray) {
                        idx = addQuadArray(ga,vertices,gIndices,idx,t);
                    } else if (ga instanceof TriangleFanArray) {
                        idx = addTriangleFanArray(ga,vertices,gIndices,idx,t);
                    } else if (ga instanceof TriangleArray) {
                        idx = addTriangleArray(ga,vertices,gIndices,idx,t);
                    } else if (ga instanceof LineStripArray) {
                        idx = addLineStripArray(ga,vertices,gIndices,idx,t);
                    } else {
                        System.out.println("GAHA4");
                    }
                } else {
                    System.out.println("GAHA5");
                }
            }
        } else if (n instanceof Group) {
            Enumeration<?> e = ((Group)n).getAllChildren();
            while (e.hasMoreElements()) {
                idx = makeVertsAndTriangles((Node)e.nextElement(),vertices,gIndices,idx,t0);
            }
        } else if (n instanceof Link) {
            Transform3D t = new Transform3D();
            try {
                n.getLocalToVworld(t);
            } catch(Exception ee) {
                //ee.printStackTrace();
            }
            t.mul(t0,t);
            SharedGroup sg = ((Link)n).getSharedGroup();
            Enumeration<?> e = sg.getAllChildren();
            while (e.hasMoreElements()) {
                idx = makeVertsAndTriangles((Node)e.nextElement(),vertices,gIndices,idx,t);
            }
        } else if (n instanceof Behavior) {
            ;
        } else {
            System.out.println("GAHA6");
            System.out.println(n.getClass().getName());
        }
        return idx;
    }
    static int addQuadArray(GeometryArray ga,ByteBuffer vertices,ByteBuffer gIndices,int idx,Transform3D t) {
        int ici = ga.getInitialCoordIndex();
        int vvc = ga.getValidVertexCount();
        for (int i=ici;i<ici+vvc;i=i+4) {
            Point3f p0 = new Point3f();
            Point3f p1 = new Point3f();
            Point3f p2 = new Point3f();
            Point3f p3 = new Point3f();
            ga.getCoordinate(i+0,p0); t.transform(p0);
            ga.getCoordinate(i+1,p1); t.transform(p1);
            ga.getCoordinate(i+2,p2); t.transform(p2);
            ga.getCoordinate(i+3,p3); t.transform(p3);
            vertices.putFloat(p0.x);
            vertices.putFloat(p0.y);
            vertices.putFloat(p0.z);
            vertices.putFloat(p1.x);
            vertices.putFloat(p1.y);
            vertices.putFloat(p1.z);
            vertices.putFloat(p2.x);
            vertices.putFloat(p2.y);
            vertices.putFloat(p2.z);
            vertices.putFloat(p2.x);
            vertices.putFloat(p2.y);
            vertices.putFloat(p2.z);
            vertices.putFloat(p3.x);
            vertices.putFloat(p3.y);
            vertices.putFloat(p3.z);
            vertices.putFloat(p0.x);
            vertices.putFloat(p0.y);
            vertices.putFloat(p0.z);
            gIndices.putInt(idx+0);
            gIndices.putInt(idx+1);
            gIndices.putInt(idx+2);
            gIndices.putInt(idx+3);
            gIndices.putInt(idx+4);
            gIndices.putInt(idx+5);
            idx = idx + 6;
        }
        return idx;
    }
    static int addTriangleFanArray(GeometryArray ga,ByteBuffer vertices,ByteBuffer gIndices,int idx,Transform3D t) {
        int ici = ga.getInitialCoordIndex();
        int vvc = ga.getValidVertexCount();
        int ns = ((TriangleFanArray)ga).getNumStrips();
        int svc[] = new int[ns];
        ((TriangleFanArray)ga).getStripVertexCounts(svc);
        int stripStart = ici;
        for (int i=0;i<ns;i++) {
            Point3f p0 = new Point3f();//forの外に出さないこと
            ga.getCoordinate(stripStart,p0); t.transform(p0);
            for (int j=1;j<svc[i]-1;j++) {
                Point3f p1 = new Point3f();//forの外に出さないこと
                Point3f p2 = new Point3f();//forの外に出さないこと
                ga.getCoordinate(stripStart+j+0,p1); t.transform(p1);
                ga.getCoordinate(stripStart+j+1,p2); t.transform(p2);
                vertices.putFloat(p0.x);
                vertices.putFloat(p0.y);
                vertices.putFloat(p0.z);
                vertices.putFloat(p1.x);
                vertices.putFloat(p1.y);
                vertices.putFloat(p1.z);
                vertices.putFloat(p2.x);
                vertices.putFloat(p2.y);
                vertices.putFloat(p2.z);
                gIndices.putInt(idx+0);
                gIndices.putInt(idx+1);
                gIndices.putInt(idx+2);
                idx = idx + 3;
            }
            stripStart += svc[i];
        }
        return idx;
    }
    static int addTriangleArray(GeometryArray ga,ByteBuffer vertices,ByteBuffer gIndices,int idx,Transform3D t) {
        int ici = ga.getInitialCoordIndex();
        int vvc = ga.getValidVertexCount();
        for (int i=ici;i<ici+vvc;i=i+3) {
            Point3f p0 = new Point3f();//forの外に出さないこと
            Point3f p1 = new Point3f();//forの外に出さないこと
            Point3f p2 = new Point3f();//forの外に出さないこと
            ga.getCoordinate(i+0,p0); t.transform(p0);
            ga.getCoordinate(i+1,p1); t.transform(p1);
            ga.getCoordinate(i+2,p2); t.transform(p2);
            vertices.putFloat(p0.x);
            vertices.putFloat(p0.y);
            vertices.putFloat(p0.z);
            vertices.putFloat(p1.x);
            vertices.putFloat(p1.y);
            vertices.putFloat(p1.z);
            vertices.putFloat(p2.x);
            vertices.putFloat(p2.y);
            vertices.putFloat(p2.z);
            gIndices.putInt(idx+0);
            gIndices.putInt(idx+1);
            gIndices.putInt(idx+2);
            idx = idx + 3;
        }
        return idx;
    }
    static int addLineStripArray(GeometryArray ga,ByteBuffer vertices,ByteBuffer gIndices,int idx,Transform3D t) {
        int ici = ga.getInitialCoordIndex();
        int vvc = ga.getValidVertexCount();
        for (int i=ici;i<ici+vvc;i=i+2) {
            Point3f p0 = new Point3f();//forの外に出さないこと
            Point3f p1 = new Point3f();//forの外に出さないこと
            ga.getCoordinate(i+0,p0); t.transform(p0);
            ga.getCoordinate(i+1,p1); t.transform(p1);
            vertices.putFloat(p0.x);
            vertices.putFloat(p0.y);
            vertices.putFloat(p0.z);
            vertices.putFloat(p1.x);
            vertices.putFloat(p1.y);
            vertices.putFloat(p1.z);
            vertices.putFloat(p0.x);
            vertices.putFloat(p0.y);
            vertices.putFloat(p0.z);
            gIndices.putInt(idx+0);
            gIndices.putInt(idx+1);
            gIndices.putInt(idx+2);
            idx = idx + 3;
        }
        return idx;
    }

//================================================================================


    /**
     * VRMLファイルをロードしてJava3Dのシーングラフを返します。
     */
    public static Node loadVRML_A(URL url) throws Exception {
        VRML.initVRML();
        Scene scene = VRML.loader.load(url);
        BranchGroup bg = new BranchGroup();
        bg.addChild(scene.getSceneGroup());
        for (Background b: scene.getBackgroundNodes()) {
            bg.addChild(b);
        }
        for (Fog f: scene.getFogNodes()) {
            bg.addChild(f);
        }
        return bg;
    }

    /**
     * VRMLファイルをロードしてJava3Dのシーングラフを返します。
     * 返されるシーングラフはSharedGroupとして共有できない
     * 背景や霧などの要素を除外した物になります。
     * 背景や霧も含んだシーングラフが必要な場合はloadVRML_A(URL)
     * メソッドを使用してください。
     */
    public static Node loadVRML_B(URL url) throws Exception {
        VRML.initVRML();
        Scene scene = VRML.loader.load(url);
        BranchGroup bg = new BranchGroup();
        bg.addChild(scene.getSceneGroup());
        return bg;
    }

    /**
     * 引数で指定したミリ秒だけプログラムの実行を一時停止します．
     * 内部でThread.sleep(long)を呼び出していますが，例外を
     * 出さないようになっています．
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

//----------------------------------------------------------

    /**
     * 引数で与えられたA3Objectのリストの中から
     * pointに一番近い物を選び出して返します．
     * 引数のA3Objectのリストが空の場合はnullがかえります．
     */
    public static A3Object getNearest(List<A3Object> objects,Vector3d point) {
        double maxLen2 = Double.MAX_VALUE;
        Vector3d tmp = new Vector3d();
        A3Object ret = null;
        for (A3Object o:objects) {
            tmp.sub(point,o.getLoc());
            if (maxLen2<tmp.lengthSquared()) {
                ret = o;
                maxLen2 = tmp.lengthSquared();
            }
        }
        return ret;
    }

    /**
     * 引数で与えられたA3Objectのリストの中から
     * centerを中心とする半径radiusの球に含まれる物のみ選択しリストにして返します．
     */
    public static ArrayList<A3Object> objectsInSphere(List<A3Object> objects,Vector3d center,double radius) {
        ArrayList<A3Object> ret = new ArrayList<A3Object>();
        Vector3d tmp = new Vector3d();
        double dd = radius*radius;
        for (A3Object o:objects) {
            tmp.sub(center,o.getLoc());
            if (tmp.lengthSquared()<dd)
                ret.add(o);
        }
        return ret;
    }

    /**
     * 引数で与えられたA3Objectのリストの中から
     * apexを頂点としdir方向にdeg度の角度で広がる円錐に含まれる物のみ選択し
     * リストにして返します．
     */
    public static ArrayList<A3Object> objectsInCone(List<A3Object> objects,Vector3d apex,Vector3d dir,double deg) {
        ArrayList<A3Object> ret = new ArrayList<A3Object>();
        Vector3d dir2 = new Vector3d(dir);
        dir2.normalize();
        double dot = Math.cos(deg/180.0*Math.PI);
        Vector3d tmp = new Vector3d();
        for (A3Object o:objects) {
            tmp.sub(o.getLoc(),apex);
            if (tmp.lengthSquared()<0.000000001) continue;//ちょっと手抜き
            tmp.normalize();
            if (dir2.dot(tmp)>dot)
                ret.add(o);
        }
        return ret;
    }

    class DistPack {
        A3Object o;
        double d;
        DistPack(A3Object o,double d) {
            this.o=o; this.d=d;
        }
    }
    class DPComparator implements Comparator<DistPack> {
        public int compare(DistPack a, DistPack b) {
            if (a.d>b.d) return 1;
            if (a.d==b.d) return 0;
            return -1;
        }
    }
    /**
     * 与えられたA3Objectのリストをpointからの距離でソートします．
     * 引数は変更されず，ソートされた返り値のリストは新たなリストとして生成されます．
     */
    public ArrayList<A3Object> sortByDistanceFromPoint(List<A3Object> objects,Vector3d point) {
        ArrayList<DistPack> dps = new ArrayList<DistPack>();
        Vector3d tmp = new Vector3d();
        double d;
        for (A3Object o:objects) {
            tmp.sub(o.getLoc(),point);
            dps.add(new DistPack(o,tmp.lengthSquared()));
        }
        Collections.sort(dps, new DPComparator());
        ArrayList<A3Object> ret = new ArrayList<A3Object>();
        for (DistPack dp:dps) {
            ret.add(dp.o);
        }
        return ret;
    }
}
