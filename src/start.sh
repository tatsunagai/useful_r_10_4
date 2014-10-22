#!/bin/sh

# 指定している値はLinuxの場合の例

# 環境変数R_HOME Rのインストールディレクトリ
R_HOME=/usr/lib64/R
export R_HOME

# Javaのライブラリパス libjri.so(libjri.jnilib)の場所
LIB_PATH=/usr/lib64/R/library/rJava/jri

# Javaのクラスパス jri用jarファイルの場所
CLASS_PATH=/usr/lib64/R/library/rJava/jri

# プログラムの実行（プログラムのjarがmyapp.jar、メインクラスがexample.Mainの場合）
# クラスパスは*でフォルダ内のすべてjarを追加
# クラスパスの区切りはコロン
java -Djava.library.path=$LIB_PATH -classpath $CLASS_PATH/*:myapp.jar example.Main
