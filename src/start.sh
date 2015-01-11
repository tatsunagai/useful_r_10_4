#!/bin/sh

# 指定している値はLinuxの場合の例

# 環境変数R_HOME Rのインストールディレクトリ
R_HOME=/usr/lib64/R
export R_HOME

# 環境変数LD_LIBRARY_PATH libR.soの場所
LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$R_HOME/lib"
export LD_LIBRARY_PATH

# Javaのライブラリパス libjri.so(libjri.jnilib)の場所
LIB_PATH=/usr/lib64/R/library/rJava/jri

# Javaのクラスパス jri用jarファイルの場所
CLASS_PATH=/usr/lib64/R/library/rJava/jri

# プログラムの実行（プログラムのjarがmyapp.jar、メインクラスがexample.Mainの場合）
# クラスパスは*でフォルダ内のすべてjarを追加
# クラスパスの区切りはコロン
java -Djava.library.path=$LIB_PATH -classpath $CLASS_PATH/*:myapp.jar example.Main
