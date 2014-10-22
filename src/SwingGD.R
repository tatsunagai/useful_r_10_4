# ライブラリの読み込み
library(rJava)
library(JavaGD)

# 環境変数にJavaのグラフィックデバイス用クラスを設定
Sys.setenv(JAVAGD_CLASS_NAME = "org/rosuda/javaGD/SwingGDInterface")

# 環境変数にJavaのクラスパスを設定
Sys.setenv(CLASSPATH = "/path/to/SwingGD.jar;/path/to/REngine.jar;/path/to/JRIEngine.jar;/path/to/JRI.jar")

# デバイスを開くための関数を定義
SwingGD <- function(width = 600, height = 600, ...) {
	JavaGD(width = width, height = height, ...)
	plot.new()
}

# デバイスを開く
SwingGD()

# 標準で開くデバイスに設定
options(device = SwingGD)
