

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
			// JRIEngineを作成する。このJRIEngine使用してRと通信する。
			engine = new JRIEngine(new String[0]);
			
			// ベクター
			// 整数（integer）
			engine.assign("myInt", new REXPInteger(1));
			engine.assign("myInts", new REXPInteger(new int[]{1,2,3}));
			// 数値（numeric）
			engine.assign("myNumber", new REXPDouble(1.1));
			engine.assign("myNumbers", new REXPDouble(new double[]{1.1, 1.2, 1.3}));
			// 文字列（character）
			engine.assign("myCharacter", new REXPString("abc"));
			engine.assign("myCharacters", new REXPString(new String[]{"abc", "def"}));
			// 真偽値（logical）
			engine.assign("myLogical", new REXPLogical(true));
			engine.assign("myLogicals", new REXPLogical(new boolean[]{true, true, false}));
			
			// リスト
			RList rList = new RList();
			// 名前なしでリストに値を追加する
			rList.add(new REXPString(new String[]{"abc", "def"}));
			// 名前付きでリストに値を追加する
			rList.put("name1", new REXPInteger(new int[]{1,2,3}));
			rList.put("name2", new REXPDouble(new double[]{1.1, 1.2, 1.3}));
			// REXPGenericVectorを作成して代入
			engine.assign("myList", new REXPGenericVector(rList));
			
			// Attribute
			RList attrList = new RList();
			attrList.put("dim", new REXPInteger(new int[]{3, 2}));
			REXPList rexpAttrList = new REXPList(attrList);
			// REXPの作成時にAttributeを渡す
			engine.assign("myAttribute", new REXPInteger(new int[]{1,2,3,4,5,6}, rexpAttrList));
			
			// データフレーム
			RList dataframeList = new RList();
			dataframeList.put("var1", new REXPDouble(new double[]{0, 1.1, 2.2}));
			dataframeList.put("var2", new REXPString(new String[]{"a", "b", "c"}));
			engine.assign("myData", REXP.createDataFrame(dataframeList));
			// マトリックス
			double[][] matrix = new double[][]{
					new double[]{1,2,3},
					new double[]{4,5,6}
			};
			engine.assign("myMatrix", REXP.createDoubleMatrix(matrix));
			// マトリックス（自分でattributeを設定する）
			int[] matrixValues = new int[]{1,2,3,4,5,6};
			RList matrixAttr = new RList();
			matrixAttr.put("dim", new REXPInteger(new int[]{2, 3}));
			engine.assign("myMatrix", new REXPInteger(matrixValues, new REXPList(matrixAttr)));
			
		} catch (REngineException e) {
		} catch (REXPMismatchException e) {
		} finally {
			// JRIEngineを終了する
			if (engine != null)
				engine.close();
		}
	}

}
