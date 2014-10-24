

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RFactor;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.JRI.JRIEngine;

public class REXPExample {
	
	public static void main(String[] args) {
		JRIEngine engine = null;
		try {
			engine = new JRIEngine(new String[0]);
			
			example(engine);
			
		} catch (REngineException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		} finally {
			try {
				engine.parseAndEval("base::q(save=\"no\")");
			} catch (REngineException e) {
				e.printStackTrace();
			} catch (REXPMismatchException e) {
				e.printStackTrace();
			}
			if (engine != null)
				engine.close();
		}
	}
	
	private static void example(JRIEngine engine) throws REngineException, REXPMismatchException {
		// ���ʂ��擾����K�v���Ȃ��ꍇ
		// R�̃I�u�W�F�N�g��Java��REXP�N���X�ɕϊ����鏈�����ȗ������
		engine.parseAndEval("a <- sum(1:10)", null, false);

		// �������Ȃ��w��̏ꍇ�A�Q�Ƃ�REXP
		REXP reference = engine.parseAndEval("sum(1:10)", null, false);
		if (reference.isReference()) { //�@�Q�Ƃł��邱�Ƃ̔���
			REXP value = engine.resolveReference(reference); // �Q�Ƃ���������
		}

		// NULL�̔���
		REXP value = engine.parseAndEval("ifelse(sample(1:10, 1) > 5, 1, NULL)");
		boolean isNull = value.isNull();

		// �����̎擾�iinterger�j
		// ���ۂ̃N���X��REXPInteger
		REXP integers = engine.parseAndEval("1:10");
		if (integers.isInteger()) {
			int iValue = integers.asInteger(); // 1�Ԗڂ����擾
			int[] iArray = integers.asIntegers();
			// NA�̔���
			boolean isNA = REXPInteger.isNA(iValue);
			boolean[] isNAs = integers.isNA(); // NA�Ȃ��true
			// �T�C�Y�̎擾
			int len = integers.length();
		}

		// ���l�̎擾�idouble�j
		// ���ۂ̃N���X��REXPDouble
		REXP numbers = engine.parseAndEval("rnorm(n = 10)");
		if (numbers.isNumeric()) {
			double dValue = numbers.asDouble(); // 1�Ԗڂ����擾
			double[] dArray = numbers.asDoubles();
			// NA�̔���
			boolean isNA = REXPDouble.isNA(dValue);
			boolean[] isNAs = numbers.isNA(); // NA�Ȃ��true
		}

		// ������̎擾�icharacter�j
		// ���ۂ̃N���X��REXPString
		REXP character = engine.parseAndEval("c(\"abc\", \"efg\")");
		if (character.isString()) {
			String sValue = character.asString(); // 1�Ԗڂ����擾
			String[] sArray = character.asStrings();
			// NA�̔���
			boolean isNA = sValue == null; // NA��null�ŕԂ����
			boolean[] isNAs = character.isNA(); // NA�Ȃ��true
		}

		// �^�U�l�̎擾�ilogical�j
		// ���ۂ̃N���X��REXPLogical
		REXP lValues = engine.parseAndEval("rnorm(n = 10) > 0");
		if (lValues instanceof REXPLogical) {
			REXPLogical logicals = (REXPLogical) lValues;
			boolean[] trueArray = logicals.isTRUE(); // TRUE�Ȃ�true FALSE,NA��false
			boolean[] falseArray = logicals.isFALSE(); // FALSE�Ȃ�true TRUE,NA��false
			// NA�̔���
			boolean[] isNAs = logicals.isNA(); // NA�Ȃ��true
			// TRUE, FALSE, NA�̔���
			byte[] bytes = logicals.asBytes(); // asBytes��REXPLogical�ɃL���X�g���Ȃ��Ă��g����
			Boolean[] booleanArray = new Boolean[bytes.length];
			for (int i = 0; i < bytes.length; i++) {
				switch(bytes[i]) {
				case REXPLogical.TRUE:
					booleanArray[i] = true;
					break;
				case REXPLogical.FALSE:
					booleanArray[i] = false;
					break;
				case REXPLogical.NA:
					booleanArray[i] = null;
					break;
				}
			}
		}

		// ���X�g�̎擾
		REXP list = engine.parseAndEval("list(a = 1:5, b = \"abc\")");
		// isList()��NULL�ł�true��Ԃ��̂ŁANULL�łȂ��m�F�͕ʓr�K�v
		if (list.isList() && !list.isNull()) {
			RList rList = list.asList();

			REXP first = rList.at(0); // 1�Ԗڂ̗v�f���擾
			REXP second = rList.at(1); // 2�Ԗڂ̗v�f���擾
			REXP b = rList.at("b"); // b�Ƃ������O�̗v�f���擾

			String firstKey = rList.keyAt(0); // 1�Ԗڂ̖��O�i�L�[�j���擾
			String[] keys = rList.keys(); // ���O�̔z����擾

			int size = rList.size(); // �v�f�����擾
		}

		// attribute�̎擾
		REXP iris = engine.parseAndEval("iris"); // iris�f�[�^�t���[�����擾
		// class�Ƃ���attribute�����邩����
		boolean hasClass = iris.hasAttribute("class");
		// class�Ƃ���attribute���擾
		REXP classAttr = iris.getAttribute("class"); // �Ȃ��Ƃ���null
		// attribute�S�̂����X�g�̌`�Ŏ擾
		// ���X�g�̎��Ɠ��l�ɏ����\
		REXPList attrList = iris._attr(); // �Ȃ��Ƃ���null
		if (attrList != null) {
			RList rList = attrList.asList();
			classAttr = rList.at("class");
		}

		// factor�N���X�̎擾
		REXP fValue = engine.parseAndEval("factor(c(\"�j\", \"��\", \"�j\"))");
		if (fValue.isFactor()) {
			RFactor factor = fValue.asFactor();

			// �l�̎擾
			String[] values = factor.asStrings(); // ���x���i������j�Ŏ擾
			int[] indices = factor.asIntegers(); // �C���f�b�N�X�i�����j�Ŏ擾

			// ���x���i�l�̎�ވꗗ�j�̎擾
			String[] levels = factor.levels();

			// �l�̐�
			int[] counts = factor.counts();
			int maleCount = factor.count("�j");
		}

		// matrix�N���X�̎擾
		REXP matrixValue = engine.parseAndEval("matrix(1:10, 2, 5)");
		if (matrixValue.isInteger()) {
			int[] values = matrixValue.asIntegers(); // �l
			int[] dim = matrixValue.dim(); // �s���A��
		
			// 2�s3��ڂ��擾
			int i = 2, j = 3;
			int valueAt = values[(j-1)*dim[0]+(i-1)];
		}
		
		// double��matrix�N���X�̎擾
		REXP doubleMatrixValue = engine.parseAndEval("matrix(rnorm(10), 2, 5)");
		// double�ł��邱�Ƃ̔���
		if (!doubleMatrixValue.isInteger() && doubleMatrixValue.isNumeric()) {
			double[][] matrix = doubleMatrixValue.asDoubleMatrix();
		}

		// inherits
		REXP iris2 = engine.parseAndEval("iris");
		boolean isDataFrame = iris.inherits("data.frame"); // data.frame�N���X���m�F

	}
}
