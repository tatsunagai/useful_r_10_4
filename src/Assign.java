

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.JRI.JRIEngine;

public class Assign {
	
	public static void main(String[] args) {

		JRIEngine engine = null;
		try {
			// JRIEngine���쐬����B����JRIEngine�g�p����R�ƒʐM����B
			engine = new JRIEngine(new String[0]);
			
			// �x�N�^�[
			// �����iinteger�j
			engine.assign("myInt", new REXPInteger(1));
			engine.assign("myInts", new REXPInteger(new int[]{1,2,3}));
			// ���l�inumeric�j
			engine.assign("myNumber", new REXPDouble(1.1));
			engine.assign("myNumbers", new REXPDouble(new double[]{1.1, 1.2, 1.3}));
			// ������icharacter�j
			engine.assign("myCharacter", new REXPString("abc"));
			engine.assign("myCharacters", new REXPString(new String[]{"abc", "def"}));
			// �^�U�l�ilogical�j
			engine.assign("myLogical", new REXPLogical(true));
			engine.assign("myLogicals", new REXPLogical(new boolean[]{true, true, false}));
			
			// ���X�g
			RList rList = new RList();
			// ���O�Ȃ��Ń��X�g�ɒl��ǉ�����
			rList.add(new REXPString(new String[]{"abc", "def"}));
			// ���O�t���Ń��X�g�ɒl��ǉ�����
			rList.put("name1", new REXPInteger(new int[]{1,2,3}));
			rList.put("name2", new REXPDouble(new double[]{1.1, 1.2, 1.3}));
			// REXPGenericVector���쐬���đ��
			engine.assign("myList", new REXPGenericVector(rList));
			
			// Attribute
			RList attrList = new RList();
			attrList.put("dim", new REXPInteger(new int[]{3, 2}));
			REXPList rexpAttrList = new REXPList(attrList);
			// REXP�̍쐬����Attribute��n��
			engine.assign("myAttribute", new REXPInteger(new int[]{1,2,3,4,5,6}, rexpAttrList));
			
			// �f�[�^�t���[��
			RList dataframeList = new RList();
			dataframeList.put("var1", new REXPDouble(new double[]{0, 1.1, 2.2}));
			dataframeList.put("var2", new REXPString(new String[]{"a", "b", "c"}));
			engine.assign("myData", REXP.createDataFrame(dataframeList));
			// �}�g���b�N�X
			double[][] matrix = new double[][]{
					new double[]{1,2,3},
					new double[]{4,5,6}
			};
			engine.assign("myMatrix", REXP.createDoubleMatrix(matrix));
			// �}�g���b�N�X�i������attribute��ݒ肷��j
			int[] matrixValues = new int[]{1,2,3,4,5,6};
			RList matrixAttr = new RList();
			matrixAttr.put("dim", new REXPInteger(new int[]{2, 3}));
			engine.assign("myMatrix", new REXPInteger(matrixValues, new REXPList(matrixAttr)));
			
		} catch (REngineException e) {
		} catch (REXPMismatchException e) {
		} finally {
			// JRIEngine���I������
			if (engine != null)
				engine.close();
		}
	}

}
