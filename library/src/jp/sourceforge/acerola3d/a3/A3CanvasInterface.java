package jp.sourceforge.acerola3d.a3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyListener;
import javax.vecmath.*;
import java.io.*;
import javax.media.j3d.*;
import java.util.ArrayList;

/**
 * A3CanvasとJA3Canvasを統一的に扱うために
 * 導入されたインタフェース。
 */
public interface A3CanvasInterface {

    // A3Objectの追加と削除
    /**
     * A3Objectを追加して表示されるようにします。
     */
    public void add(A3Object a);

    /**
     * 指定されたA3Objectの登録を削除して表示されないように
     * します。
     */
    public void del(A3Object a);

    /**
     * アクティブなシーンに登録されている全てのA3Objectを削除して表示されないようにします。
     * ただしaddLockedA3で追加した固定オブジェクトは削除されません。
     */
    public void delAll();
    /**
     * シーンを指定して、登録されている全てのA3Objectを削除して表示されないようにします。
     * ただしaddLockedA3で追加した固定オブジェクトは削除されません。
     */
    public void delAll(int scene);

    /**
     * 背景を表すA3Objectをセットします。
     */
    public void setBackground(A3Object a);

    /**
     * 背景を削除します。
     */
    public void delBackground();

    /**
     * アバタをセットします。
     */
    public void setAvatar(A3Object a);

    /**
     * セットされたアバタを取得します。
     */
    public A3Object getAvatar();

    // リスナ設定のラッパーメソッド
    /**
     * A3Listenerを登録します。
     */
    public void addA3Listener(A3Listener l);

    /**
     * 指定されたA3Listenerの登録を抹消します。
     */
    public void removeA3Listener(A3Listener l);

    /**
     * KeyListenerを登録します。
     */
    public void addKeyListener(KeyListener l);
    /**
     * 指定されたKeyListenerの登録を抹消します。
     */
    public void removeKeyListener(KeyListener l);

    /**
     * カメラのデフォルトの位置を指定します。
     */
    public void setDefaultCameraLoc(double x,double y,double z);

    /**
     * カメラのデフォルトの位置を指定します。
     */
    public void setDefaultCameraLoc(Vector3d loc);

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraQuat(double x,double y,double z,double w);

    /**
     * カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraQuat(Quat4d quat);

    /**
     * カメラのデフォルトの回転を指定します(ラジアン版)。
     */
    public void setDefaultCameraRot(double x,double y,double z);

    /**
     * カメラのデフォルトの回転を指定します(ラジアン版)。
     */
    public void setDefaultCameraRot(Vector3d rot);

    /**
     * カメラのデフォルトの回転を指定します(デグリー版)。
     */
    public void setDefaultCameraRev(double x,double y,double z);

    /**
     * カメラのデフォルトの回転を指定します(デグリー版)。
     */
    public void setDefaultCameraRev(Vector3d rev);

    /**
     * カメラのデフォルトの拡大率を指定します。
     */
    public void setDefaultCameraScale(double s);

    /**
     * カメラの位置、回転、拡大率をリセットしてデフォルトに戻します。
     */
    public void resetCamera();

    /**
     * カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(double x,double y,double z);

    /**
     * カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(Vector3d loc);

    /**
     * カメラの位置を即時に指定します。
     */
    public void setCameraLocImmediately(double x,double y,double z);

    /**
     * カメラの位置を即時に指定します。
     */
    public void setCameraLocImmediately(Vector3d loc);

    /**
     * 現在のカメラの位置に引数で与えられたベクトルを加えて新し位置をセットします。
     */
    public void addCameraLoc(double x,double y,double z);

    /**
     * 現在のカメラの位置に引数で与えられたベクトルを加えて新し位置をセットします。
     */
    public void addCameraLoc(Vector3d loc);

    /**
     * 現在のカメラの位置に引数で与えられたベクトルを加えて新し位置を即時にセットします。
     */
    public void addCameraLocImmediately(double x,double y,double z);

    /**
     * 現在のカメラの位置に引数で与えられたベクトルを加えて新し位置を即時にセットします。
     */
    public void addCameraLocImmediately(Vector3d loc);

    /**
     * カメラを前方に指定距離だけ移動します。
     */
    public void moveCameraForward(double l);

    /**
     * カメラを前方に指定距離だけ即時に移動します。
     */
    public void moveCameraForwardImmediately(double l);

    /**
     * カメラを後方に指定距離だけ移動します。
     */
    public void moveCameraBackward(double l);

    /**
     * カメラを後方に指定距離だけ即時に移動します。
     */
    public void moveCameraBackwardImmediately(double l);

    /**
     * カメラを右方向に指定距離だけ移動します。
     */
    public void moveCameraRight(double l);

    /**
     * カメラを右方向に指定距離だけ即時に移動します。
     */
    public void moveCameraRightImmediately(double l);

    /**
     * カメラを左方向に指定距離だけ移動します。
     */
    public void moveCameraLeft(double l);

    /**
     * カメラを左方向に指定距離だけ即時に移動します。
     */
    public void moveCameraLeftImmediately(double l);

    /**
     * カメラを上方向に指定距離だけ移動します。
     */
    public void moveCameraUp(double l);

    /**
     * カメラを上方向に指定距離だけ即時に移動します。
     */
    public void moveCameraUpImmediately(double l);

    /**
     * カメラを下方向に指定距離だけ移動します。
     */
    public void moveCameraDown(double l);

    /**
     * カメラを下方向に指定距離だけ即時に移動します。
     */
    public void moveCameraDownImmediately(double l);

    /**
     * カメラを指定した座標の方向に指定距離だけ移動します。
     * 回転はしません。
     */
    public void moveCameraTo(Vector3d v, double l);

    /**
     * カメラを指定した座標の方向に指定距離だけ移動します。
     * 回転はしません。
     */
    public void moveCameraTo(double x, double y, double z, double l);

    /**
     * カメラを指定したA3Objectの方向に指定距離だけ移動します。
     * 回転はしません。
     */
    public void moveCameraTo(A3Object a, double l);

    /**
     * カメラを指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToImmediately(Vector3d v, double l);

    /**
     * カメラを指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToImmediately(double x, double y, double z, double l);

    /**
     * カメラを指定したA3Objectの方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToImmediately(A3Object a, double l);

    /**
     * シーンを指定して、カメラを前方に指定距離だけ移動します。
     */
    public void moveCameraForward(double l,int scene);

    /**
     * シーンを指定して、カメラを前方に指定距離だけ即時に移動します。
     */
    public void moveCameraForwardImmediately(double l,int scene);

    /**
     * シーンを指定して、カメラを後方に指定距離だけ移動します。
     */
    public void moveCameraBackward(double l,int scene);

    /**
     * シーンを指定して、カメラを後方に指定距離だけ即時に移動します。
     */
    public void moveCameraBackwardImmediately(double l,int scene);

    /**
     * シーンを指定して、カメラを右方向に指定距離だけ移動します。
     */
    public void moveCameraRight(double l,int scene);

    /**
     * シーンを指定して、カメラを右方向に指定距離だけ即時に移動します。
     */
    public void moveCameraRightImmediately(double l,int scene);

    /**
     * シーンを指定して、カメラを左方向に指定距離だけ移動します。
     */
    public void moveCameraLeft(double l,int scene);

    /**
     * シーンを指定して、カメラを左方向に指定距離だけ即時に移動します。
     */
    public void moveCameraLeftImmediately(double l,int scene);

    /**
     * シーンを指定して、カメラを上方向に指定距離だけ移動します。
     */
    public void moveCameraUp(double l,int scene);

    /**
     * シーンを指定して、カメラを上方向に指定距離だけ即時に移動します。
     */
    public void moveCameraUpImmediately(double l,int scene);

    /**
     * シーンを指定して、カメラを下方向に指定距離だけ移動します。
     */
    public void moveCameraDown(double l,int scene);

    /**
     * シーンを指定して、カメラを下方向に指定距離だけ即時に移動します。
     */
    public void moveCameraDownImmediately(double l,int scene);

    /**
     * シーンを指定して、カメラを指定した座標の方向に指定距離だけ移動します。
     * 回転はしません。
     */
    public void moveCameraTo(Vector3d v, double l,int scene);

    /**
     * シーンを指定して、カメラを指定した座標の方向に指定距離だけ移動します。
     * 回転はしません。
     */
    public void moveCameraTo(double x, double y, double z, double l,int scene);

    /**
     * シーンを指定して、カメラを指定したA3Objectの方向に指定距離だけ移動します。
     * 回転はしません。
     */
    public void moveCameraTo(A3Object a, double l,int scene);

    /**
     * シーンを指定して、カメラを指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToImmediately(Vector3d v, double l,int scene);

    /**
     * シーンを指定して、カメラを指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToImmediately(double x, double y, double z, double l,int scene);

    /**
     * シーンを指定して、カメラを指定したA3Objectの方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToImmediately(A3Object a, double l,int scene);







    /**
     * 指定した角度(degree)だけカメラを上の方に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraUp(double deg);

    /**
     * 指定した角度(degree)だけカメラを上の方に即時に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraUpNow(double deg);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを上の方に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraUp(double deg,int scene);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを上の方に即時に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraUpNow(double deg,int scene);

    /**
     * 指定した角度(degree)だけカメラを下の方に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraDown(double deg);

    /**
     * 指定した角度(degree)だけカメラを下の方に即時に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraDownNow(double deg);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを下の方に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraDown(double deg,int scene);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを下の方に即時に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraDownNow(double deg,int scene);

    /**
     * 指定した角度(degree)だけカメラを右の方に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraRight(double deg);

    /**
     * 指定した角度(degree)だけカメラを右の方に即時に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraRightNow(double deg);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを右の方に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraRight(double deg,int scene);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを右の方に即時に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraRightNow(double deg,int scene);

    /**
     * 指定した角度(degree)だけカメラを左の方に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraLeft(double deg);

    /**
     * 指定した角度(degree)だけカメラを左の方に即時に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraLeftNow(double deg);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを左の方に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraLeft(double deg,int scene);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを左の方に即時に回転します。方向はカメラを基準とした方向です。
     */
    public void turnCameraLeftNow(double deg,int scene);

    /**
     * 指定した角度(degree)だけカメラを右に傾けます。正確にはカメラの前方方向に右ネジを進める時と同じ方向に回転させます。
     */
    public void rollCameraRight(double deg);

    /**
     * 指定した角度(degree)だけカメラを右に即時に傾けます。正確にはカメラの前方方向に右ネジを進める時と同じ方向に回転させます。
     */
    public void rollCameraRightNow(double deg);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを右に傾けます。正確にはカメラの前方方向に右ネジを進める時と同じ方向に回転させます。
     */
    public void rollCameraRight(double deg,int scene);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを右に即時に傾けます。正確にはカメラの前方方向に右ネジを進める時と同じ方向に回転させます。
     */
    public void rollCameraRightNow(double deg,int scene);

    /**
     * 指定した角度(degree)だけカメラを左に傾けます。正確にはカメラの前方方向に右ネジを進める時と逆方向に回転させます。
     */
    public void rollCameraLeft(double deg);

    /**
     * 指定した角度(degree)だけカメラを左に即時に傾けます。正確にはカメラの前方方向に右ネジを進める時と逆方向に回転させます。
     */
    public void rollCameraLeftNow(double deg);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを左に傾けます。正確にはカメラの前方方向に右ネジを進める時と逆方向に回転させます。
     */
    public void rollCameraLeft(double deg,int scene);

    /**
     * シーンを指定して、指定した角度(degree)だけカメラを左に即時に傾けます。正確にはカメラの前方方向に右ネジを進める時と逆方向に回転させます。
     */
    public void rollCameraLeftNow(double deg,int scene);

    /**
     * 方向ベクトルのX,Y,Z成分を指定して，カメラがそちらを向くように回転します。
     */
    public void turnCameraTo(double dirX,double dirY,double dirZ);

    /**
     * 方向ベクトルを指定して，カメラがそちらを向くように回転します。
     */
    public void turnCameraTo(Vector3d dir);

    /**
     * 方向ベクトルのX,Y,Z成分を指定して，カメラがそちらを向くように即時に回転します。
     */
    public void turnCameraToNow(double dirX,double dirY,double dirZ);

    /**
     * 方向ベクトルを指定して，カメラがそちらを向くように即時に回転します。
     */
    public void turnCameraToNow(Vector3d dir);

    /**
     * 方向ベクトルのX,Y,Z成分とシーンを指定して，カメラがそちらを向くように回転します。
     */
    public void turnCameraTo(double dirX,double dirY,double dirZ,int scene);

    /**
     * 方向ベクトルとシーンを指定して，カメラがそちらを向くように回転します。
     */
    public void turnCameraTo(Vector3d dir,int scene);

    /**
     * 方向ベクトルのX,Y,Z成分とシーンを指定して，カメラがそちらを向くように即時に回転します。
     */
    public void turnCameraToNow(double dirX,double dirY,double dirZ,int scene);

    /**
     * 方向ベクトルとシーンを指定して，カメラがそちらを向くように即時に回転します。
     */
    public void turnCameraToNow(Vector3d dir,int scene);
    






    /**
     * カメラの現在位置を返します。
     */
    public Vector3d getCameraLoc();

    /**
     * カメラの目標位置を返します。
     * カメラは常に補完が効いているので最終的に到達する位置という意味です。
     */
    public Vector3d getCameraTargetLoc();

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(double x,double y,double z,double w);

    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(Quat4d quat);

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraQuatImmediately(double x,double y,double z,double w);

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraQuatImmediately(Quat4d quat);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成します。
     */
    public void mulCameraQuat(double x,double y,double z,double w);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成します。
     */
    public void mulCameraQuat(Quat4d quat);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して即時に反映させます。
     */
    public void mulCameraQuatImmediately(double x,double y,double z,double w);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して即時に反映させます。
     */
    public void mulCameraQuatImmediately(Quat4d quat);

    /**
     * カメラの現在の回転を返します。
     */
    public Quat4d getCameraQuat();

    /**
     * カメラの目標回転を返します。
     * カメラは常に補完が効いているので最終的に到達する回転という意味です。
     */
    public Quat4d getCameraTargetQuat();

    /**
     * カメラの回転を指定します(オイラー角をラジアンで指定:x,y,z版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRot(double x,double y,double z);

    /**
     * カメラの回転を指定します(オイラー角をラジアンで指定:Vector3d版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRot(Vector3d rot);

    /**
     * カメラの回転を即時に指定します(オイラー角をラジアンで指定:x,y,z版)。
     */
    public void setCameraRotImmediately(double x,double y,double z);

    /**
     * カメラの回転を即時に指定します(オイラー角をラジアンで指定:Vector3d版)。
     */
    public void setCameraRotImmediately(Vector3d rot);

    /**
     * 現在のカメラの回転に引数で与えられた回転を
     * 合成します(オイラー角をラジアンで指定:x,y,z版)。
     */
    public void mulCameraRot(double x,double y,double z);

    /**
     * 現在のカメラの回転に引数で与えられた回転を
     * 合成します(オイラー角をラジアンで指定:Vector3d版)。
     */
    public void mulCameraRot(Vector3d rot);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して
     * 即時に反映させます(オイラー角をラジアンで指定:x,y,z版)。
     */
    public void mulCameraRotImmediately(double x,double y,double z);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して
     * 即時に反映させます(オイラー角をラジアンで指定:Vector3d版)。
     */
    public void mulCameraRotImmediately(Vector3d rot);

    /**
     * カメラの現在の回転を返します(オイラー角をラジアンで取得)。
     */
    public Vector3d getCameraRot();

    /**
     * カメラの目標回転を返します(オイラー角をラジアンで取得)。
     * カメラは常に補完が効いているので最終的に到達する回転という意味です。
     */
    public Vector3d getCameraTargetRot();

    /**
     * カメラの回転を指定します(オイラー角をデグリーで指定:x,y,z版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRev(double x,double y,double z);

    /**
     * カメラの回転を指定します(オイラー角をデグリーで指定:Vector3d版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRev(Vector3d rev);

    /**
     * カメラの回転を即時に指定します(オイラー角をデグリーで指定:x,y,z版)。
     */
    public void setCameraRevImmediately(double x,double y,double z);

    /**
     * カメラの回転を即時に指定します(オイラー角をデグリーで指定:Vector3d版)。
     */
    public void setCameraRevImmediately(Vector3d rev);

    /**
     * 現在のカメラの回転に引数で与えられた回転を
     * 合成します(オイラー角をデグリーで指定:x,y,z版)。
     */
    public void mulCameraRev(double x,double y,double z);

    /**
     * 現在のカメラの回転に引数で与えられた回転を
     * 合成します(オイラー角をデグリーで指定:Vector3d版)。
     */
    public void mulCameraRev(Vector3d rev);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して
     * 即時に反映させます(オイラー角をデグリーで指定:x,y,z版)。
     */
    public void mulCameraRevImmediately(double x,double y,double z);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して
     * 即時に反映させます(オイラー角をデグリーで指定:Vector3d版)。
     */
    public void mulCameraRevImmediately(Vector3d rev);

    /**
     * カメラの現在の回転を返します(オイラー角をデグリーで取得)。
     */
    public Vector3d getCameraRev();

    /**
     * カメラの目標回転を返します(オイラー角をデグリーで取得)。
     * カメラは常に補完が効いているので最終的に到達する回転という意味です。
     */
    public Vector3d getCameraTargetRev();

    /**
     * カメラの拡大率を指定します。自動的に補完が働き滑らかにカメラの拡大率が
     * 変ります。拡大率がデフォルトの1.0の時は10cmより手前と100mより奥はクリッピングされて
     * 表示されません。拡大率を0.1にすれば1cmから10mの間を表示できるようになり、
     * 10.0にすれば1mから1kmの間を表示できるようになります。
     */
    public void setCameraScale(double s);

    /**
     * カメラの拡大率を即時に指定します。拡大率がデフォルトの1.0の時は10cmより
     * 手前と100mより奥はクリッピングされて表示されません。拡大率を0.1にすれば
     * 1cmから10mの間を表示できるようになり、10.0にすれば1mから1kmの間を
     * 表示できるようになります。
     */
    public void setCameraScaleImmediately(double s);

    /**
     * 現在のカメラの拡大率に引数で与えられた数値を掛け合せ新し拡大率としてセットします。
     */
    public void mulCameraScale(double s);

    /**
     * 現在のカメラの拡大率に引数で与えられた数値を掛け合せ新し拡大率として即時にセットします。
     */
    public void mulCameraScaleImmediately(double s);

    /**
     * カメラの拡大率を返します。
     */
    public double getCameraScale();

    /**
     * カメラの目標拡大率を返します。
     * カメラは常に補完が効いているので最終的に到達する拡大率という意味です。
     */
    public double getCameraTargetScale();

    /**
     * ルックアットポイントを使って、カメラの回転を指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPoint(Vector3d lookAt);

    /**
     * ルックアットポイントを使って、カメラの回転を即時に指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointImmediately(Vector3d lookAt);

    /**
     * ルックアットポイントを使って、カメラの回転を指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPoint(double x,double y,double z);

    /**
     * ルックアットポイントを使って、カメラの回転を即時に指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointImmediately(double x,double y,double z);

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up);

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up);

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up);

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up);

    /**
     * 対象となるオブジェクトをルックアットポイントとしてカメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(A3Object a);

    /**
     * 対象となるオブジェクトをルックアットポイントとし、上方向ベクトルを使ってカメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(A3Object a,Vector3d up);

    /**
     * ヘッドライトのON,OFFを設定します。
     */
    public void setHeadLightEnable(boolean b);
    // マウスナビゲーションのモード設定
    /**
     * ナビゲーションモードです。主に3D仮想空間をマウスで観覧する時の
     * モードを設定するための列挙型ですが、A3オブジェクトの位置などを
     * 編集できるモードも含まれます。ナビゲーションモードを設定すると、
     * a3パッケージがカメラやA3Objectの座標や回転などをコントロール
     * するようになるので、A3Canvas.setCameraLoc()や
     * A3Object.setLoc()などのメソッドと併用する時は注意が必要です。
     * 将来他のモードが追加される可能性があります。
     */
    public enum NaviMode {
        /** a3パッケージがナビゲーションに関与しないモードです。 */
        NONE,
        /** 特定のa3オブジェクトに着目してそれを中心に観覧するモードです。 */
        EXAMINE,
        /** 平面を歩いているようなナビゲーションモードです。 */
        WALK,
        /** 空間を自由に飛ぶようなナビゲーションモードです。 */
        FLY,
        /** 選択されたオブジェクトの位置、回転、大きさを変更できるモードです。 */
        EDIT,
        /** EXAMINEモードと似ていますが、注視点が任意に変更できる観覧モードです。 */
        SIMPLE,
        /** アバタの後方からアバタを追跡するような観覧モードです。A23.setDefaultUpperDirection()などを
         * 用いてZ軸が上になる座標系に設定している場合には正常に機能しないので、ChaseControllerを
         * 用いる方法を検討して下さい。 */
        CHASE,
        /** FPS(First Person Shooting)で良くつかわれるようなナビゲーションです。
         * a,dで左右、w,sで前後に動き、キーボードの方向キーで視点を変更します。
         * 視点がどちらを向いていても移動で高さが変らないようにしています。
         */
        FPS,
        /** FPS(First Person Shooting)で良くつかわれるようなナビゲーションですが、
         * 視点の方向によって地面をはなれる方向にも移動できます。
         * a,dで左右、w,sで前後に動き、キーボードの方向キーで視点を変更します。
         */
        FPS_FLY,
        /** ユーザ定義のナビゲーションモードです。 */
        USER
    }

    /**
     * ナビゲーションモードを指定します。
     * 必要であればナビゲーションの初期化に必要なパラメータも渡してやります。
     */
    public void setNavigationMode(NaviMode m,Object...params);

    /**
     * ナビゲーションの大まかなスピードを設定します。
     * 単位はm/s。デフォルト値は1.0。
     */
    public void setNavigationSpeed(double s);

    /**
     * ナビゲーションの大まかなスピードを取得します。
     * 単位はm/s。A3Controllerの作成者はこの値を参照して
     * ナビゲーションのスピードを
     * 計算することが望まれます。
     */
    public double getNavigationSpeed();

    /**
     * A3Controllerをセットします。これをセットするとナビゲーションモードが
     * USERに自動的にセットされるので、以前設定していたモードは無効になります。
     * また、設定されているA3Controllerを解除して無効にするためには引数に
     * nullを指定して呼び出して下さい。
     */
    public void setA3Controller(A3Controller c);
//  ----------座標変換とピッキングのためのラッパーメソッド---------
    /**
     * アバタの座標を利用して、スクリーン上の点(x,y)を仮想空間上の
     * 点(x,y,z)に変換します。
     * 変換後の座標は、カメラ正面方向に垂直な仮想の面を想定して
     * その面上にあると仮定して計算します。その仮想の面は
     * A3CanvasInterfaceに設定されたアバタの座標を含むように
     * カメラからの距離が設定されます。
     */
    public Point3d canvasToVirtualCS(int x,int y);

    /**
     * スクリーン上の点(x,y)を仮想空間上の点(x,y,z)に変換します。
     * 変換後の座標は、カメラ正面方向に垂直で距離がdisとなる仮想の
     * 面上にあるとして計算するようになっています。この距離disは
     * <em>物理座標系</em>における長さで指定して下さい。
     * 物理座標系における長さとなるので、カメラの拡大縮小率を
     * 1.0以外に設定している場合には注意して下さい。
     */
    public Point3d canvasToVirtualCS(int x,int y,double dis);

    /**
     * アバタの座標を利用して、スクリーン上の点(x,y)を物理空間上の
     * 点(x,y,z)に変換します。
     * 変換後の座標は、カメラ正面方向に垂直な仮想の面を想定して
     * その面上にあると仮定して計算します。その仮想の面は
     * A3CanvasInterfaceに設定されたアバタの座標を含むように
     * カメラからの距離が設定されます。つまり変換後のZ座標は
     * アバタの物理空間におけるZ座標と等しくなります。
     */
    public Point3d canvasToPhysicalCS(int x,int y);

    /**
     * スクリーン上の点(x,y)を物理空間上の点(x,y,z)に変換します。
     * 変換後の座標は、カメラ正面方向に垂直で距離がdisとなる仮想の
     * 面の上にあるとして計算するようになっています。この距離disは
     * 物理座標系における長さで指定して下さい。つまり変換後の
     * z座標は必ず-disになります。
     */
    public Point3d canvasToPhysicalCS(int x,int y,double dis);

    /**
     * 物理空間上の点(x,y,z)を仮想空間上の点(x,y,z)に変換します。
     */
    public Vector3d physicalCSToVirtualCS(Vector3d v);

    /**
     * 物理空間上の点(x,y,z)をスクリーン上の点(x,y)に変換します。
     */
    public Point physicalCSToCanvas(Point3d p);

    /**
     * 仮想空間上の点(x,y,z)をスクリーン上の点(x,y)に変換します。
     */
    public Point virtualCSToCanvas(Point3d p);

    /**
     * 仮想空間上の点(x,y,z)を物理空間上の点(x,y,z)に変換します。
     */
    public Vector3d virtualCSToPhysicalCS(Vector3d v);

    /**
     * カメラの座標系(物理空間の座標系)のX軸方向の単位ベクトルを
     * 仮想空間の座標系で表したベクトルを返します。
     */
    public Vector3d getCameraUnitVecX();

    /**
     * カメラの座標系(物理空間の座標系)のY軸方向の単位ベクトルを
     * 仮想空間の座標系で表したベクトルを返します。
     */
    public Vector3d getCameraUnitVecY();

    /**
     * カメラの座標系(物理空間の座標系)のZ軸方向の単位ベクトルを
     * 仮想空間の座標系で表したベクトルを返します。
     */
    public Vector3d getCameraUnitVecZ();

    /**
     * A3Canvas上の点(x,y)にあるA3Objectをピックアップします。
     * なにもオブジェクトが無い場合にはnullを返します。
     */
    public A3Object pickA3(int x,int y);

    /**
     * 仮想空間の座標系においてoriginで指定した位置からdirで指定した
     * 方向に半直線を延し、その半直線上にある一番近いA3Objectを返します。
     * なにもオブジェクトが無い場合にはnullを返します。
     */
    public A3Object pick(Vector3d origin,Vector3d dir);
//  ----------J3DGraphics2D(文字描画など)---------
    /**
     * A3Canvasに2Dの描画を行うためのComponent2Dを登録します。
     */
    public void add(Component2D c);
    /**
     * A3Canvasから2Dの描画を行うためのComponent2Dを削除します。
     */
    public void del(Component2D c);
    /**
     * シーンを指定して、A3Canvasに2Dの描画を行うためのComponent2Dを登録します。
     */
    public void add(Component2D c,int scene);
    /**
     * シーンを指定して、A3Canvasから2Dの描画を行うためのComponent2Dを削除します。
     */
    public void del(Component2D c,int scene);
//  ----------おまけ機能---------
    /**
     * A3Canvasに表示されている内容をPNG画像としてファイルに保存します。
     */
    public void saveImage(File file) throws IOException;
    /**
     * A3Canvasに表示されている内容をBufferedImageで返します。
     */
    public BufferedImage snapshot();
//  ----------シーン関係のメソッド---------
    /**
     * シーンを準備します。すでにその番号のシーンが
     * 存在していれば何もしません(既存の内容はそのまま)。
     */
    public void prepareScene(int s);
    /**
     * シーンを切り替えます。
     */
    public void changeActiveScene(int s);
    /**
     * シーンを指定してA3Objectを追加します。
     */
    public void add(A3Object a,int s);
    /**
     * シーンを指定して、A3Objectを削除します。
     */
    public void del(A3Object a,int s);
    /**
     * シーンを指定して、カメラのデフォルトの位置を指定します。
     */
    public void setDefaultCameraLoc(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラのデフォルトの位置を指定します。
     */
    public void setDefaultCameraLoc(Vector3d loc,int scene);

    /**
     * シーンを指定して、カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraQuat(double x,double y,double z,double w,int scene);

    /**
     * シーンを指定して、カメラのデフォルトの回転を指定します。
     */
    public void setDefaultCameraQuat(Quat4d quat,int scene);

    /**
     * シーンを指定して、カメラのデフォルトの回転を指定します(ラジアン版)。
     */
    public void setDefaultCameraRot(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラのデフォルトの回転を指定します(ラジアン版)。
     */
    public void setDefaultCameraRot(Vector3d rot,int scene);

    /**
     * シーンを指定して、カメラのデフォルトの回転を指定します(デグリー版)。
     */
    public void setDefaultCameraRev(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラのデフォルトの回転を指定します(デグリー版)。
     */
    public void setDefaultCameraRev(Vector3d rev,int scene);

    /**
     * シーンを指定して、カメラのデフォルトの拡大率を指定します。
     */
    public void setDefaultCameraScale(double s,int scene);

    /**
     * シーンを指定して、カメラの位置、回転、拡大率をリセットしてデフォルトに戻します。
     */
    public void resetCamera(int scene);

    /**
     * シーンを指定して、カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(Vector3d loc,int scene);

    /**
     * シーンを指定して、カメラの位置を即時に指定します。
     */
    public void setCameraLocImmediately(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラの位置を即時に指定します。
     */
    public void setCameraLocImmediately(Vector3d loc,int scene);

    /**
     * シーンを指定して、現在のカメラの位置に引数で与えられたベクトルを加えて新し位置をセットします。
     */
    public void addCameraLoc(double x,double y,double z,int scene);

    /**
     * シーンを指定して、現在のカメラの位置に引数で与えられたベクトルを加えて新し位置をセットします。
     */
    public void addCameraLoc(Vector3d loc,int scene);

    /**
     * シーンを指定して、現在のカメラの位置に引数で与えられたベクトルを加えて新し位置を即時にセットします。
     */
    public void addCameraLocImmediately(double x,double y,double z,int scene);

    /**
     * シーンを指定して、現在のカメラの位置に引数で与えられたベクトルを加えて新し位置を即時にセットします。
     */
    public void addCameraLocImmediately(Vector3d loc,int scene);

    /**
     * シーンを指定して、カメラの現在位置を返します。
     */
    public Vector3d getCameraLoc(int scene);

    /**
     * シーンを指定して、カメラの目標位置を返します。
     * カメラは常に補完が効いているので最終的に到達する位置という意味です。
     */
    public Vector3d getCameraTargetLoc(int scene);

    /**
     * シーンを指定して、カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(double x,double y,double z,double w,int scene);

    /**
     * シーンを指定して、カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(Quat4d quat,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します。
     */
    public void setCameraQuatImmediately(double x,double y,double z,double w,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します。
     */
    public void setCameraQuatImmediately(Quat4d quat,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた回転を合成します。
     */
    public void mulCameraQuat(double x,double y,double z,double w,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた回転を合成します。
     */
    public void mulCameraQuat(Quat4d quat,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた回転を合成して即時に反映させます。
     */
    public void mulCameraQuatImmediately(double x,double y,double z,double w,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた回転を合成して即時に反映させます。
     */
    public void mulCameraQuatImmediately(Quat4d quat,int scene);

    /**
     * シーンを指定して、カメラの現在の回転を返します。
     */
    public Quat4d getCameraQuat(int scene);

    /**
     * シーンを指定して、カメラの目標回転を返します。
     * カメラは常に補完が効いているので最終的に到達する回転という意味です。
     */
    public Quat4d getCameraTargetQuat(int scene);

    /**
     * シーンを指定して、カメラの回転を指定します(ラジアン版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRot(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラの回転を指定します(ラジアン版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRot(Vector3d rot,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します(ラジアン版)。
     */
    public void setCameraRotImmediately(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します(ラジアン版)。
     */
    public void setCameraRotImmediately(Vector3d rot,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成します(ラジアン版)。
     */
    public void mulCameraRot(double x,double y,double z,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成します(ラジアン版)。
     */
    public void mulCameraRot(Vector3d rot,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成して即時に反映させます(ラジアン版)。
     */
    public void mulCameraRotImmediately(double x,double y,double z,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成して即時に反映させます(ラジアン版)。
     */
    public void mulCameraRotImmediately(Vector3d rot,int scene);

    /**
     * シーンを指定して、カメラの現在の回転を返します(ラジアン版)。
     */
    public Vector3d getCameraRot(int scene);

    /**
     * シーンを指定して、カメラの目標回転を返します(ラジアン版)。
     * カメラは常に補完が効いているので最終的に到達する回転という意味です。
     */
    public Vector3d getCameraTargetRot(int scene);

    /**
     * シーンを指定して、カメラの回転を指定します(デグリー版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRev(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラの回転を指定します(デグリー版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRev(Vector3d rev,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します(デグリー版)。
     */
    public void setCameraRevImmediately(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します(デグリー版)。
     */
    public void setCameraRevImmediately(Vector3d rev,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成します(デグリー版)。
     */
    public void mulCameraRev(double x,double y,double z,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成します(デグリー版)。
     */
    public void mulCameraRev(Vector3d rev,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成して即時に反映させます(デグリー版)。
     */
    public void mulCameraRevImmediately(double x,double y,double z,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成して即時に反映させます(デグリー版)。
     */
    public void mulCameraRevImmediately(Vector3d rev,int scene);

    /**
     * シーンを指定して、カメラの現在の回転を返します(デグリー版)。
     */
    public Vector3d getCameraRev(int scene);

    /**
     * シーンを指定して、カメラの目標回転を返します(デグリー版)。
     * カメラは常に補完が効いているので最終的に到達する回転という意味です。
     */
    public Vector3d getCameraTargetRev(int scene);

    /**
     * シーンを指定して、カメラの拡大率を指定します。自動的に補完が働き滑らかにカメラの拡大率が
     * 変ります。拡大率がデフォルトの1.0の時は10cmより手前と100mより奥はクリッピングされて
     * 表示されません。拡大率を0.1にすれば1cmから10mの間を表示できるようになり、
     * 10.0にすれば1mから1kmの間を表示できるようになります。
     */
    public void setCameraScale(double s,int scene);

    /**
     * シーンを指定して、カメラの拡大率を即時に指定します。拡大率がデフォルトの1.0の時は10cmより
     * 手前と100mより奥はクリッピングされて表示されません。拡大率を0.1にすれば
     * 1cmから10mの間を表示できるようになり、10.0にすれば1mから1kmの間を
     * 表示できるようになります。
     */
    public void setCameraScaleImmediately(double s,int scene);

    /**
     * シーンを指定して、現在のカメラの拡大率に引数で与えられた数値を掛け合せ新し拡大率としてセットします。
     */
    public void mulCameraScale(double s,int scene);

    /**
     * シーンを指定して、現在のカメラの拡大率に引数で与えられた数値を掛け合せ新し拡大率として即時にセットします。
     */
    public void mulCameraScaleImmediately(double s,int scene);

    /**
     * シーンを指定して、カメラの拡大率を返します。
     */
    public double getCameraScale(int scene);

    /**
     * シーンを指定して、カメラの目標拡大率を返します。
     * カメラは常に補完が効いているので最終的に到達する拡大率という意味です。
     */
    public double getCameraTargetScale(int scene);

    /**
     * シーンを指定し、ルックアットポイントを使ってカメラの回転を指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPoint(Vector3d lookAt,int scene);

    /**
     * シーンを指定し、ルックアットポイントを使ってカメラの回転を即時に指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointImmediately(Vector3d lookAt,int scene);

    /**
     * シーンを指定し、ルックアットポイントを使ってカメラの回転を指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPoint(double x,double y,double z,int scene);

    /**
     * シーンを指定し、ルックアットポイントを使ってカメラの回転を即時に指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointImmediately(double x,double y,double z,int scene);

    /**
     * シーンを指定し、ルックアットポイントと上方向ベクトルを使ってカメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(Vector3d lookAt,Vector3d up,int scene);

    /**
     * シーンを指定し、ルックアットポイントと上方向ベクトルを使ってカメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointImmediately(Vector3d lookAt,Vector3d up,int scene);

    /**
     * シーンを指定し、ルックアットポイントと上方向ベクトルを使ってカメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(double x,double y,double z,Vector3d up,int scene);

    /**
     * シーンを指定し、ルックアットポイントと上方向ベクトルを使ってカメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointImmediately(double x,double y,double z,Vector3d up,int scene);

    /**
     * シーンを指定し、対象となるオブジェクトをルックアットポイントとしてカメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(A3Object a,int scene);

    /**
     * シーンを指定し、対象となるオブジェクトをルックアットポイントとし、上方向ベクトルを使ってカメラの回転を
     * 指定します。
     */
    public void setCameraLookAtPoint(A3Object a,Vector3d up,int scene);

    /**
     * シーンを指定して、ナビゲーションモードを指定します。
     * 必要であればナビゲーションの初期化に必要なパラメータも渡してやります。
     */
    public void setNavigationMode(int scene,NaviMode m,Object...params);

    /**
     * シーンを指定して、ナビゲーションの大まかなスピードを設定します。
     * A3Controllerの作成者はこのスピードを参照して
     * ナビゲーションのスピードを計算することが望まれます。
     */
    public void setNavigationSpeed(double s,int scene);

    /**
     * シーンを指定して、ナビゲーションの大まかなスピードを取得します。
     */
    public double getNavigationSpeed(int scene);

    /**
     * シーンを指定して、A3Controllerをセットします。これをセットするとナビゲーションモードが
     * USERに自動的にセットされるので、以前設定していたモードは無効になります。
     * また、設定されているA3Controllerを解除して無効にするためには引数に
     * nullを指定して呼び出して下さい。
     */
    public void setA3Controller(A3Controller c,int scene);

    /**
     * シーンを指定して、背景を表すA3Objectをセットします。
     */
    public void setBackground(A3Object a,int scene);

    /**
     * シーンを指定して、背景を削除します。
     */
    public void delBackground(int scene);

    /**
     * シーンを指定して、アバタをセットします。
     */
    public void setAvatar(A3Object a,int scene);

    /**
     * シーンを指定して、セットされたアバタを取得します。
     */
    public A3Object getAvatar(int scene);
//  ---------- LockedA3の処理 ----------
    /**
     * A3Objectを追加してカメラに対して固定した位置に
     * 表示されるようにします。
     */
    public void addLockedA3(A3Object a);
    /**
     * 指定されたA3Objectの登録を削除してカメラに対して固定した
     * 位置に表示されないようにします。
     */
    public void delLockedA3(A3Object a);
    /**
     * カメラに対して固定して表示されるようい登録されている
     * 全てのA3Objectを削除して表示されないようにします。
     */
    public void delAllLockedA3();
    /**
     * シーンを指定して、A3Objectを追加してカメラに対して固定した位置に
     * 表示されるようにします。
     */
    public void addLockedA3(A3Object a,int scene);
    /**
     * シーンを指定して、指定されたA3Objectの登録を削除してカメラに対して固定した
     * 位置に表示されないようにします。
     */
    public void delLockedA3(A3Object a,int scene);
    /**
     * シーンを指定して、カメラに対して固定して表示されるようい登録されている
     * 全てのA3Objectを削除して表示されないようにします。
     */
    public void delAllLockedA3(int scene);
    /**
     * 上方向をY軸とするのかZ軸とするのかの変更を行う。
     * デフォルトはY軸で、この場合は特に何もかわらないが、
     * Z軸が設定された場合は表示されるA3Objectが
     * 自動的に回転されて正常な向きで表示されるようになる。
     */
    public void setUpperDirection(A3Object.UpperDirection d);
    /**
     * シーンを指定して、上方向をY軸とするのかZ軸とするのかの
     * 変更を行う。
     * デフォルトはY軸で、この場合は特に何もかわらないが、
     * Z軸が設定された場合は表示されるA3Objectが
     * 自動的に回転されて正常な向きで表示されるようになる。
     */
    public void setUpperDirection(A3Object.UpperDirection d,int scene);
    /**
     * 上方向をY軸としているのかZ軸としているのかを得るための関数です。
     */
    public A3Object.UpperDirection getUpperDirection();
    /**
     * シーンを指定して、上方向をY軸としているのかZ軸としているのか
     * を得るための関数です。
     */
    public A3Object.UpperDirection getUpperDirection(int scene);
    /**
     * このCanvasのサイズを返します。
     */
    public Dimension getCanvasSize();
    /**
     * このCanvasが保持する仮想環境、その他のリソースを開放します。
     */
    public void cleanUp();
    /**
     * このCanvasが保持する仮想環境に新しい視点からの描画をするための
     * SubCanvasを追加します。
     */
    public void addA3SubCanvas(A3CanvasInterface sc);

    /**
     * カメラの投影法を設定するためのメソッド。
     * デフォルトは透視投影法。
     */
    public void setProjectionMode(ProjectionMode m);

    /**
     * このCanvasの物理座標系における横幅を設定するためのメソッドです。
     * ただしカメラの投影方法が平行投影方法(PARALLEL)の場合のみ
     * 有効で、透視投影法(PERSPECTIVE)の場合は無視されます。
     */
    public void setCanvasWidthInPWorld(double s);

    /**
     * 透視投影法の場合の画角(Field of View)を設定するためのメソッド。
     * 平行投影法(PARALLEL)の場合は、この値は無視されます。
     */
    public void setFieldOfView(double f);

    /**
     * サウンドの音量を設定します。デフォルト値は1.0です。
     */
    public void setSoundGain(double g);

    /**
     * サウンドの音量を取得します。
     */
    public double getSoundGain();

    /**
     * FPS(Frame Per Second)を取得します。
     */
    public int getFPS();

    /**
     * カメラやA3Object座標などの更新インターバルを設定します。
     * 引数はミリ秒で与えてください。デフォルト値は33ミリ秒です。
     */
    public void setUpdateInterval(long l);

    /**
     * カメラやA3Object座標などの更新インターバルを取得します。
     */
    public long getUpdateInterval();

    /**
     * カメラやA3Object座標などの更新のタイミングまでwaitします。
     * 引数はタイムアウト時間(ミリ秒)で、その時間が過ぎたら復帰します。
     */
    public void waitForUpdate(long timeout);

    /**
     * 1フレームごとに確実に処理を行いたいタスクを登録します。
     * このメソッドでRunnableを実装したオブジェクトを登録すると、
     * Canvas3DのpostSwapで実行されます。
     */
    public void insertTaskIntoRenderingLoop(Runnable task);

    /**
     * insertTaskIntoRenderingLoop(Runnable task)で登録した
     * タスクの登録を解除します。
     */
    public void removeTaskFromRenderingLoop(Runnable task);

    /**
     * Acerola3Dのメインタイマーが起動されるごとに確実に処理を行いたい
     * タスクを登録します。このメソッドでRunnableを実装したオブジェクトを
     * 登録すると、A3CanvasInterface.setUpdateInterval()で指定した
     * 間隔で繰返し実行されます。
     */
    public void insertTaskIntoTimerLoop(Runnable task);

    /**
     * insertTaskIntoTimerLoop(Runnable task)で登録した
     * タスクの登録を解除します。
     */
    public void removeTaskFromTimerLoop(Runnable task);

    /**
     * カメラの補間のスピードを調節するための係数を指定します。1.0から0.0の
     * 間の数値を指定して下さい。小さいほど補間が速くなります。0.0に設定したら
     * まったく補完されないようになります。
     * デフォルトは0.1です。
     */
    public void setCameraInterpolateRatio(double ir);

    /**
     * Zバッファの処理方法を変更します。具体的には
     * View.setDepthBufferFreezeTransparent(boolean)を
     * そのまま呼び出します。
     */
    public void setDepthBufferFreezeTransparent(boolean b);

    //################################################################################
    //CameraInterface
    //################################################################################
    /**
     * ViewPlatformを内包するTransformGroupを返すメソッドですが、
     * ライブラリ内部で仕様する物なので利用しないで下さい。
     */
    public TransformGroup getTransformGroupForViewPlatform();

    /**
     * 内部で使用しているjavax.media.j3d.Canvas3Dを返すメソッドですが、
     * ライブラリ内部で仕様する物なので利用しないで下さい。
     */
    public Canvas3D getCanvas3D();

    //################################################################################
    //SubCanvas
    //################################################################################
    /**
     * ViewPlatformを含むBranchGroupを返すメソッドですが、
     * ライブラリ内部で仕様する物なので利用しないで下さい。
     */
    public BranchGroup getBranchGroupForViewPlatform();

    /**
     * PickingBehaviorをセットするためのメソッドですが、
     * ライブラリ内部で仕様する物なので利用しないで下さい。
     */
    public void setPickingBehavior(PickingBehavior pb);

    /**
     * A3VirtualUniverseをセットするメソッドですが、
     * ライブラリ内部で仕様する物なので利用しないで下さい。
     */
    public void setVirtualUniverse(A3VirtualUniverse vu);

    /**
     * セットされているA3VirtualUniverseを返すメソッドですが、
     * ライブラリ内部で仕様する物なので利用しないで下さい。
     */
    public A3VirtualUniverse getVirtualUniverse();

    //################################################################################
    //Now系メソッド。Immediatelyを呼ぶだけでOK。
    //################################################################################

    /**
     * カメラの位置を即時に指定します。
     */
    public void setCameraLocNow(double x,double y,double z);

    /**
     * カメラの位置を即時に指定します。
     */
    public void setCameraLocNow(Vector3d loc);

    /**
     * 現在のカメラの位置に引数で与えられたベクトルを加えて新し位置を即時にセットします。
     */
    public void addCameraLocNow(double x,double y,double z);

    /**
     * 現在のカメラの位置に引数で与えられたベクトルを加えて新し位置を即時にセットします。
     */
    public void addCameraLocNow(Vector3d loc);

    /**
     * カメラを前方に指定距離だけ即時に移動します。
     */
    public void moveCameraForwardNow(double l);

    /**
     * カメラを後方に指定距離だけ即時に移動します。
     */
    public void moveCameraBackwardNow(double l);

    /**
     * カメラを右方向に指定距離だけ即時に移動します。
     */
    public void moveCameraRightNow(double l);

    /**
     * カメラを左方向に指定距離だけ即時に移動します。
     */
    public void moveCameraLeftNow(double l);

    /**
     * カメラを上方向に指定距離だけ即時に移動します。
     */
    public void moveCameraUpNow(double l);

    /**
     * カメラを下方向に指定距離だけ即時に移動します。
     */
    public void moveCameraDownNow(double l);

    /**
     * カメラを指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToNow(Vector3d v, double l);

    /**
     * カメラを指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToNow(double x, double y, double z, double l);

    /**
     * カメラを指定したA3Objectの方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToNow(A3Object a, double l);

    /**
     * シーンを指定して、カメラを前方に指定距離だけ即時に移動します。
     */
    public void moveCameraForwardNow(double l,int scene);

    /**
     * シーンを指定して、カメラを後方に指定距離だけ即時に移動します。
     */
    public void moveCameraBackwardNow(double l,int scene);

    /**
     * シーンを指定して、カメラを右方向に指定距離だけ即時に移動します。
     */
    public void moveCameraRightNow(double l,int scene);

    /**
     * シーンを指定して、カメラを左方向に指定距離だけ即時に移動します。
     */
    public void moveCameraLeftNow(double l,int scene);

    /**
     * シーンを指定して、カメラを上方向に指定距離だけ即時に移動します。
     */
    public void moveCameraUpNow(double l,int scene);

    /**
     * シーンを指定して、カメラを下方向に指定距離だけ即時に移動します。
     */
    public void moveCameraDownNow(double l,int scene);

    /**
     * シーンを指定して、カメラを指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToNow(Vector3d v, double l,int scene);

    /**
     * シーンを指定して、カメラを指定した座標の方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToNow(double x, double y, double z, double l,int scene);

    /**
     * シーンを指定して、カメラを指定したA3Objectの方向に指定距離だけ即時に移動します。
     * 回転はしません。
     */
    public void moveCameraToNow(A3Object a, double l,int scene);

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraQuatNow(double x,double y,double z,double w);

    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraQuatNow(Quat4d quat);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して即時に反映させます。
     */
    public void mulCameraQuatNow(double x,double y,double z,double w);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して即時に反映させます。
     */
    public void mulCameraQuatNow(Quat4d quat);

    /**
     * カメラの回転を即時に指定します(オイラー角をラジアンで指定:x,y,z版)。
     */
    public void setCameraRotNow(double x,double y,double z);

    /**
     * カメラの回転を即時に指定します(オイラー角をラジアンで指定:Vector3d版)。
     */
    public void setCameraRotNow(Vector3d rot);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して
     * 即時に反映させます(オイラー角をラジアンで指定:x,y,z版)。
     */
    public void mulCameraRotNow(double x,double y,double z);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して
     * 即時に反映させます(オイラー角をラジアンで指定:Vector3d版)。
     */
    public void mulCameraRotNow(Vector3d rot);

    /**
     * カメラの回転を即時に指定します(オイラー角をデグリーで指定:x,y,z版)。
     */
    public void setCameraRevNow(double x,double y,double z);

    /**
     * カメラの回転を即時に指定します(オイラー角をデグリーで指定:Vector3d版)。
     */
    public void setCameraRevNow(Vector3d rev);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して
     * 即時に反映させます(オイラー角をデグリーで指定:x,y,z版)。
     */
    public void mulCameraRevNow(double x,double y,double z);

    /**
     * 現在のカメラの回転に引数で与えられた回転を合成して
     * 即時に反映させます(オイラー角をデグリーで指定:Vector3d版)。
     */
    public void mulCameraRevNow(Vector3d rev);

    /**
     * カメラの拡大率を即時に指定します。拡大率がデフォルトの1.0の時は10cmより
     * 手前と100mより奥はクリッピングされて表示されません。拡大率を0.1にすれば
     * 1cmから10mの間を表示できるようになり、10.0にすれば1mから1kmの間を
     * 表示できるようになります。
     */
    public void setCameraScaleNow(double s);

    /**
     * 現在のカメラの拡大率に引数で与えられた数値を掛け合せ新し拡大率として即時にセットします。
     */
    public void mulCameraScaleNow(double s);

    /**
     * ルックアットポイントを使って、カメラの回転を即時に指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointNow(Vector3d lookAt);

    /**
     * ルックアットポイントを使って、カメラの回転を即時に指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointNow(double x,double y,double z);

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointNow(Vector3d lookAt,Vector3d up);

    /**
     * ルックアットポイントと上方向ベクトルを使って、カメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointNow(double x,double y,double z,Vector3d up);

    /**
     * 対象となるオブジェクトをルックアットポイントとしてカメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointNow(A3Object a);

    /**
     * 対象となるオブジェクトをルックアットポイントとし、上方向ベクトルを使ってカメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointNow(A3Object a,Vector3d up);

    /**
     * シーンを指定して、カメラの位置を即時に指定します。
     */
    public void setCameraLocNow(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラの位置を即時に指定します。
     */
    public void setCameraLocNow(Vector3d loc,int scene);

    /**
     * シーンを指定して、現在のカメラの位置に引数で与えられたベクトルを加えて新し位置を即時にセットします。
     */
    public void addCameraLocNow(double x,double y,double z,int scene);

    /**
     * シーンを指定して、現在のカメラの位置に引数で与えられたベクトルを加えて新し位置を即時にセットします。
     */
    public void addCameraLocNow(Vector3d loc,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します。
     */
    public void setCameraQuatNow(double x,double y,double z,double w,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します。
     */
    public void setCameraQuatNow(Quat4d quat,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた回転を合成して即時に反映させます。
     */
    public void mulCameraQuatNow(double x,double y,double z,double w,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた回転を合成して即時に反映させます。
     */
    public void mulCameraQuatNow(Quat4d quat,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します(ラジアン版)。
     */
    public void setCameraRotNow(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します(ラジアン版)。
     */
    public void setCameraRotNow(Vector3d rot,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成して即時に反映させます(ラジアン版)。
     */
    public void mulCameraRotNow(double x,double y,double z,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成して即時に反映させます(ラジアン版)。
     */
    public void mulCameraRotNow(Vector3d rot,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します(デグリー版)。
     */
    public void setCameraRevNow(double x,double y,double z,int scene);

    /**
     * シーンを指定して、カメラの回転を即時に指定します(デグリー版)。
     */
    public void setCameraRevNow(Vector3d rev,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成して即時に反映させます(デグリー版)。
     */
    public void mulCameraRevNow(double x,double y,double z,int scene);

    /**
     * シーンを指定して、現在のカメラの回転に引数で与えられた
     * 回転を合成して即時に反映させます(デグリー版)。
     */
    public void mulCameraRevNow(Vector3d rev,int scene);

    /**
     * シーンを指定して、カメラの拡大率を即時に指定します。拡大率がデフォルトの1.0の時は10cmより
     * 手前と100mより奥はクリッピングされて表示されません。拡大率を0.1にすれば
     * 1cmから10mの間を表示できるようになり、10.0にすれば1mから1kmの間を
     * 表示できるようになります。
     */
    public void setCameraScaleNow(double s,int scene);

    /**
     * シーンを指定して、現在のカメラの拡大率に引数で与えられた数値を掛け合せ新し拡大率として即時にセットします。
     */
    public void mulCameraScaleNow(double s,int scene);

    /**
     * シーンを指定し、ルックアットポイントを使ってカメラの回転を即時に指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointNow(Vector3d lookAt,int scene);

    /**
     * シーンを指定し、ルックアットポイントを使ってカメラの回転を指定します。
     * 上方向ベクトルはそのカメラが存在するシーンのupperDirectionの
     * 値を用いて計算します。
     */
    public void setCameraLookAtPointNow(double x,double y,double z,int scene);

    /**
     * シーンを指定し、ルックアットポイントと上方向ベクトルを使ってカメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointNow(Vector3d lookAt,Vector3d up,int scene);

    /**
     * シーンを指定し、ルックアットポイントと上方向ベクトルを使ってカメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointNow(double x,double y,double z,Vector3d up,int scene);

    /**
     * シーンを指定し、対象となるオブジェクトをルックアットポイントとしてカメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointNow(A3Object a,int scene);

    /**
     * シーンを指定し、対象となるオブジェクトをルックアットポイントとし、上方向ベクトルを使ってカメラの回転を
     * 即時に指定します。
     */
    public void setCameraLookAtPointNow(A3Object a,Vector3d up,int scene);
//****************************************
    /**
     * addされている全てのA3Objectを返します．
     */
    public ArrayList<A3Object> getAll();

    /**
     * addされているA3Objectの中で引数で指定されているClassまたは
     * そのサブクラスのインスタンスのみを返します．
     */
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c);

    /**
     * シーンを指定し、addされている全てのA3Objectを返します．
     */
    public ArrayList<A3Object> getAll(int scene);

    /**
     * シーンを指定し、addされているA3Objectの中で引数で指定されているClassまたは
     * そのサブクラスのインスタンスのみを返します．
     */
    public ArrayList<A3Object> getAll(Class<? extends A3Object> c,int scene);
    /*
    //ここより下のメソッドは上のgetAllメソッドとUtilのユーティリティ
    //メソッドで実現できるようにしよう(2015,06/15)
    public A3Object getNearest(Vector3d v);
    public A3Object getNearest(double x,double y,double z);
    public A3Object getNearest(Vector3d v,Class<? extends A3Object> c);
    public A3Object getNearest(double x,double y,double z,Class<? extends A3Object> c);
    public ArrayList<A3Object> getNeighborhood(Vector3d v,double r);
    public ArrayList<A3Object> getNeighborhood(double x,double y,double z,double r);
    public ArrayList<A3Object> getNeighborhood(Vector3d v,double r,Class<? extends A3Object> c);
    public ArrayList<A3Object> getNeighborhood(double x,double y,double z,double r,Class<? extends A3Object> c);
    public ArrayList<A3Object> getObjectsInFrustum(Frustum f);
    public ArrayList<A3Object> getObjectsInFrustum(Frustum f,,Class<? extends A3Object> c);
    //----------------------------------------
    public A3Object getNearest(Vector3d v,int scene);
    public A3Object getNearest(double x,double y,double z,int scene);
    public A3Object getNearest(Vector3d v,Class<? extends A3Object> c,int scene);
    public A3Object getNearest(double x,double y,double z,Class<? extends A3Object> c,int scene);
    public ArrayList<A3Object> getNeighborhood(Vector3d v,double r,int scene);
    public ArrayList<A3Object> getNeighborhood(double x,double y,double z,double r,int scene);
    public ArrayList<A3Object> getNeighborhood(Vector3d v,double r,Class<? extends A3Object> c,int scene);
    public ArrayList<A3Object> getNeighborhood(double x,double y,double z,double r,Class<? extends A3Object> c,int scene);
    public ArrayList<A3Object> getObjectsInFrustum(Frustum f,int scene);
    public ArrayList<A3Object> getObjectsInFrustum(Frustum f,,Class<? extends A3Object> c,int scene);
    */
    /**
     * カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(A3Object a);
    /**
     * カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(A3Object a);
    /**
     * カメラの回転を指定します(オイラー角をデグリーで指定:x,y,z版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRev(A3Object a);
    /**
     * カメラの拡大率を指定します。自動的に補完が働き滑らかにカメラの拡大率が
     * 変ります。拡大率がデフォルトの1.0の時は10cmより手前と100mより奥はクリッピングされて
     * 表示されません。拡大率を0.1にすれば1cmから10mの間を表示できるようになり、
     * 10.0にすれば1mから1kmの間を表示できるようになります。
     */
    public void setCameraScale(A3Object a);
    /**
     * カメラの位置，回転，拡大率を同時に指定します。
     * 自動的に補完が働き滑らかに変ります。
     * 拡大率がデフォルトの1.0の時は10cmより手前と100mより奥はクリッピングされて
     * 表示されません。拡大率を0.1にすれば1cmから10mの間を表示できるようになり、
     * 10.0にすれば1mから1kmの間を表示できるようになります。
     */
    public void setCameraLocRevScale(A3Object a);
    //
    /**
     * カメラの位置を即時に指定します。
     */
    public void setCameraLocNow(A3Object a);
    /**
     * カメラの回転を即時に指定します。
     */
    public void setCameraQuatNow(A3Object a);
    /**
     * カメラの回転を即時に指定します(オイラー角をデグリーで指定)。
     */
    public void setCameraRevNow(A3Object a);
    /**
     * カメラの拡大率を即時に指定します。拡大率がデフォルトの1.0の時は10cmより
     * 手前と100mより奥はクリッピングされて表示されません。拡大率を0.1にすれば
     * 1cmから10mの間を表示できるようになり、10.0にすれば1mから1kmの間を
     * 表示できるようになります。
     */
    public void setCameraScaleNow(A3Object a);
    /**
     * カメラの位置，回転，拡大率を同時に即時に指定します。
     * 拡大率がデフォルトの1.0の時は10cmより手前と100mより奥はクリッピングされて
     * 表示されません。拡大率を0.1にすれば1cmから10mの間を表示できるようになり、
     * 10.0にすれば1mから1kmの間を表示できるようになります。
     */
    public void setCameraLocRevScaleNow(A3Object a);
    //
    /**
     * シーンを指定して、カメラの位置を指定します。自動的に補完が働き滑らかにカメラの位置が
     * 変ります。
     */
    public void setCameraLoc(A3Object a,int scene);
    /**
     * シーンを指定して、カメラの回転を指定します。自動的に補完が働き滑らかにカメラの回転が
     * 変ります。
     */
    public void setCameraQuat(A3Object a,int scene);
    /**
     * シーンを指定して、カメラの回転を指定します(オイラー角をデグリーで指定:x,y,z版)。
     * 自動的に補完が働き滑らかにカメラの回転が変ります。
     */
    public void setCameraRev(A3Object a,int scene);
    /**
     * シーンを指定して、カメラの拡大率を指定します。自動的に補完が働き滑らかにカメラの拡大率が
     * 変ります。拡大率がデフォルトの1.0の時は10cmより手前と100mより奥はクリッピングされて
     * 表示されません。拡大率を0.1にすれば1cmから10mの間を表示できるようになり、
     * 10.0にすれば1mから1kmの間を表示できるようになります。
     */
    public void setCameraScale(A3Object a,int scene);
    /**
     * シーンを指定して、カメラの位置，回転，拡大率を同時に指定します。
     * 自動的に補完が働き滑らかに変ります。
     * 拡大率がデフォルトの1.0の時は10cmより手前と100mより奥はクリッピングされて
     * 表示されません。拡大率を0.1にすれば1cmから10mの間を表示できるようになり、
     * 10.0にすれば1mから1kmの間を表示できるようになります。
     */
    public void setCameraLocRevScale(A3Object a,int scene);
    //
    /**
     * シーンを指定して、カメラの位置を即時に指定します。
     */
    public void setCameraLocNow(A3Object a,int scene);
    /**
     * シーンを指定して、カメラの回転を即時に指定します。
     */
    public void setCameraQuatNow(A3Object a,int scene);
    /**
     * シーンを指定して、カメラの回転を即時に指定します(オイラー角をデグリーで指定)。
     */
    public void setCameraRevNow(A3Object a,int scene);
    /**
     * シーンを指定して、カメラの拡大率を即時に指定します。拡大率がデフォルトの1.0の時は10cmより
     * 手前と100mより奥はクリッピングされて表示されません。拡大率を0.1にすれば
     * 1cmから10mの間を表示できるようになり、10.0にすれば1mから1kmの間を
     * 表示できるようになります。
     */
    public void setCameraScaleNow(A3Object a,int scene);
    /**
     * シーンを指定して、カメラの位置，回転，拡大率を同時に即時に指定します。
     * 拡大率がデフォルトの1.0の時は10cmより手前と100mより奥はクリッピングされて
     * 表示されません。拡大率を0.1にすれば1cmから10mの間を表示できるようになり、
     * 10.0にすれば1mから1kmの間を表示できるようになります。
     */
    public void setCameraLocRevScaleNow(A3Object a,int scene);
}
