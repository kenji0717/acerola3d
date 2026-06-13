title: Acerola3D Library

Acerola3D Library
========================================

* @version@, @date@
* Kenji Saito: <ksaito.sourceforge.jp@gmail.com>

概要
----------------------------------------

Java6以上がインストール済みのWindows，Mac OS X,
LinuxパソコンでAcerola3Dを使ったプログラ
ミングをするのに必要なライブラリを
まとめて入れてあります．このREADME.txt
ファイルと同じ階層にlibという
名前のフォルダがあります．この
このフォルダを適当な場所に保存して，
そのフォルダをCLASSPATH環境変数に設定して下さい．

詳しくは以下のページを参照して下さい．

* <http://acerola3d.sourceforge.jp/docs/programming/setup/>

Mac OS Xで使用する場合の注意
----------------------------------------

Mac OS XはデフォルトでJavaがインストールされているため，
これを利用してAcerola3Dのプログラミングが可能です．ただ，
Mac OS XのJavaには非常に古いバージョンのJava3Dライブラリが
含まれており，これがAcerola3Dが使用している最新のJava3D
ライブラリと衝突して上手く動かないことがあります．
これを回避するには，以下に示すファイルを削除してしまうか，
他の別の場所に移動して下さい．

* /System/Library/Java/Extensionsフォルダの中にある以下のファイル
    + j3daudio.jar, j3dcore.jar, j3dutils.jar, vecmath.jar, libJ3D.jnilib,
    + libJ3DAudio.jnilib, libJ3DUtils.jnilib


含まれるライブラリの入手先
----------------------------------------

このパッケージにはAcerola3Dが依存している
既存のライブラリも含まれています．含まれて
いるライブラリの一覧と入手先，変更点は
list.txtファイルを参照して下さい．

簡易開発環境
----------------------------------------

このパッケージ自体が簡単なプログラミングを行うための
簡易開発環境にもなっています．この簡易開発環境を使用
するためには，事前にJavaのJDKをインストールして
javacなどのコマンドが使用できるように環境変数などを
設定しておいて下さい．

* 参考(Windowsの場合)
    + <http://www.javadrive.jp/install/>

Windowsの場合はCommandPrompt.bat，Mac OS Xの場合は
Terminal.commandをダブルクリックしてみて下さい．
環境変数などが設定済みのコマンドプロンプトやターミナルが
起動します．Windowsの場合，以下のようにコマンドを入力
することで，プログラムのコンパイル実行が可能です．

    cd   test
    javac   Test.java
    java   Test

testフォルダにはあらかじめTest.javaというファイル名で
サンプルプログラムが保存されています．このプログラムで
使用しているaxis.a3という3Dオブジェクトのデータファイルは
a3filesというフォルダの中に保存されています．a3shell.sh
というファイルはLinuxとMac OS Xにおいて既存の仮想端末や
ターミナルを簡易開発環境にします．

Java Web Startによる配備方法
----------------------------------------

このREADME.txtファイルと同じ場所にある
libフォルダの中にある滑てのjarファイルに
自分で署名をして，acerola3d.jnlpファイルに
必要な変更をしてからこのREADME.txtファイルが
保存されているフォルダごとウェブサーバにアップロードして
使用して下さい．簡単に署名をするためのバッチファイルなども
binフォルダに入っています．

配備方法の詳細は以下のページの
「Acerola3DライブラリJava Web Startの配備」
の節を参照して下さい．

* <http://acerola3d.sourceforge.jp/docs/programming/deployment/>

