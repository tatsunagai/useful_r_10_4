rem rem�ł͂��܂�s�̓R�����g

rem ���ϐ�R_HOME��R�̃C���X�g�[���f�B���N�g����ݒ�
set R_HOME=C:\Program Files\R\R-3.1.0

rem ���ϐ�R_USER�Ƀ��[�U�p�f�B���N�g����ݒ�i���[�U����abc�̗�j
set R_USER=C:\Users\abc\Documents

rem ���ϐ�PATH��R.dll�̏ꏊ��ǉ��i64bit�ł��g�p�j
set PATH=%R_HOME%\bin\x64;%PATH%

rem Java���C�u�����p�X jri.dll�̏ꏊ�i64bit�ł��g�p�j
rem ���[�U�p�f�B���N�g����rJava�p�b�P�[�W����ꍇ
set JRI_DIR=%R_USER%\R\win-library\3.1\rJava\jri\x64

rem Java�N���X�p�X jri�pjar�t�@�C�������C���h�J�[�h�Ŏw��
rem ���[�U�p�f�B���N�g����rJava�p�b�P�[�W����ꍇ
set JRI_JAR=%R_USER%\R\win-library\3.1\rJava\jri\*

rem �v���O�����̎��s�i�v���O������jar��myapp.jar�A���C���N���X��example.Main�̏ꍇ�j
rem �X�y�[�X���܂ރp�X��"�ň͂�
java -Djava.library.path="%JRI_DIR%" -classpath "%JRI_JAR%";myapp.jar example.Main
