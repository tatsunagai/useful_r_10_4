rem remではじまる行はコメント

rem 環境変数R_HOMEにRのインストールディレクトリを設定
set R_HOME=C:\Program Files\R\R-3.1.0

rem 環境変数R_USERにユーザ用ディレクトリを設定（ユーザ名がabcの例）
set R_USER=C:\Users\abc\Documents

rem 環境変数PATHにR.dllの場所を追加（64bit版を使用）
set PATH=%R_HOME%\bin\x64;%PATH%

rem Javaライブラリパス jri.dllの場所（64bit版を使用）
rem ユーザ用ディレクトリにrJavaパッケージある場合
set JRI_DIR=%R_USER%\R\win-library\3.1\rJava\jri\x64

rem Javaクラスパス jri用jarファイルをワイルドカードで指定
rem ユーザ用ディレクトリにrJavaパッケージある場合
set JRI_JAR=%R_USER%\R\win-library\3.1\rJava\jri\*

rem プログラムの実行（プログラムのjarがmyapp.jar、メインクラスがexample.Mainの場合）
rem スペースを含むパスは"で囲む
java -Djava.library.path="%JRI_DIR%" -classpath "%JRI_JAR%";myapp.jar example.Main
