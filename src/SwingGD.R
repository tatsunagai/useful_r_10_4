# ���C�u�����̓ǂݍ���
library(rJava)
library(JavaGD)

# ���ϐ���Java�̃O���t�B�b�N�f�o�C�X�p�N���X��ݒ�
Sys.setenv(JAVAGD_CLASS_NAME = "org/rosuda/javaGD/SwingGDInterface")

# ���ϐ���Java�̃N���X�p�X��ݒ�
Sys.setenv(CLASSPATH = "/path/to/SwingGD.jar;/path/to/REngine.jar;/path/to/JRIEngine.jar;/path/to/JRI.jar")

# �f�o�C�X���J�����߂̊֐����`
SwingGD <- function(width = 600, height = 600, ...) {
	JavaGD(width = width, height = height, ...)
	plot.new()
}

# �f�o�C�X���J��
SwingGD()

# �W���ŊJ���f�o�C�X�ɐݒ�
options(device = SwingGD)