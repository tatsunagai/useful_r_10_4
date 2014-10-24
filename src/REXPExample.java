

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
		// 結果を取得する必要がない場合
		// RのオブジェクトをJavaのREXPクラスに変換する処理が省略される
		engine.parseAndEval("a <- sum(1:10)", null, false);

		// 解決しない指定の場合、参照のREXP
		REXP reference = engine.parseAndEval("sum(1:10)", null, false);
		if (reference.isReference()) { //　参照であることの判別
			REXP value = engine.resolveReference(reference); // 参照を解決する
		}

		// NULLの判定
		REXP value = engine.parseAndEval("ifelse(sample(1:10, 1) > 5, 1, NULL)");
		boolean isNull = value.isNull();

		// 整数の取得（interger）
		// 実際のクラスはREXPInteger
		REXP integers = engine.parseAndEval("1:10");
		if (integers.isInteger()) {
			int iValue = integers.asInteger(); // 1番目だけ取得
			int[] iArray = integers.asIntegers();
			// NAの判定
			boolean isNA = REXPInteger.isNA(iValue);
			boolean[] isNAs = integers.isNA(); // NAならばtrue
			// サイズの取得
			int len = integers.length();
		}

		// 数値の取得（double）
		// 実際のクラスはREXPDouble
		REXP numbers = engine.parseAndEval("rnorm(n = 10)");
		if (numbers.isNumeric()) {
			double dValue = numbers.asDouble(); // 1番目だけ取得
			double[] dArray = numbers.asDoubles();
			// NAの判定
			boolean isNA = REXPDouble.isNA(dValue);
			boolean[] isNAs = numbers.isNA(); // NAならばtrue
		}

		// 文字列の取得（character）
		// 実際のクラスはREXPString
		REXP character = engine.parseAndEval("c(\"abc\", \"efg\")");
		if (character.isString()) {
			String sValue = character.asString(); // 1番目だけ取得
			String[] sArray = character.asStrings();
			// NAの判定
			boolean isNA = sValue == null; // NAはnullで返される
			boolean[] isNAs = character.isNA(); // NAならばtrue
		}

		// 真偽値の取得（logical）
		// 実際のクラスはREXPLogical
		REXP lValues = engine.parseAndEval("rnorm(n = 10) > 0");
		if (lValues instanceof REXPLogical) {
			REXPLogical logicals = (REXPLogical) lValues;
			boolean[] trueArray = logicals.isTRUE(); // TRUEならtrue FALSE,NAはfalse
			boolean[] falseArray = logicals.isFALSE(); // FALSEならtrue TRUE,NAはfalse
			// NAの判定
			boolean[] isNAs = logicals.isNA(); // NAならばtrue
			// TRUE, FALSE, NAの判定
			byte[] bytes = logicals.asBytes(); // asBytesはREXPLogicalにキャストしなくても使える
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

		// リストの取得
		REXP list = engine.parseAndEval("list(a = 1:5, b = \"abc\")");
		// isList()はNULLでもtrueを返すので、NULLでない確認は別途必要
		if (list.isList() && !list.isNull()) {
			RList rList = list.asList();

			REXP first = rList.at(0); // 1番目の要素を取得
			REXP second = rList.at(1); // 2番目の要素を取得
			REXP b = rList.at("b"); // bという名前の要素を取得

			String firstKey = rList.keyAt(0); // 1番目の名前（キー）を取得
			String[] keys = rList.keys(); // 名前の配列を取得

			int size = rList.size(); // 要素数を取得
		}

		// attributeの取得
		REXP iris = engine.parseAndEval("iris"); // irisデータフレームを取得
		// classというattributeがあるか判定
		boolean hasClass = iris.hasAttribute("class");
		// classというattributeを取得
		REXP classAttr = iris.getAttribute("class"); // ないときはnull
		// attribute全体をリストの形で取得
		// リストの時と同様に処理可能
		REXPList attrList = iris._attr(); // ないときはnull
		if (attrList != null) {
			RList rList = attrList.asList();
			classAttr = rList.at("class");
		}

		// factorクラスの取得
		REXP fValue = engine.parseAndEval("factor(c(\"男\", \"女\", \"男\"))");
		if (fValue.isFactor()) {
			RFactor factor = fValue.asFactor();

			// 値の取得
			String[] values = factor.asStrings(); // ラベル（文字列）で取得
			int[] indices = factor.asIntegers(); // インデックス（整数）で取得

			// レベル（値の種類一覧）の取得
			String[] levels = factor.levels();

			// 値の数
			int[] counts = factor.counts();
			int maleCount = factor.count("男");
		}

		// matrixクラスの取得
		REXP matrixValue = engine.parseAndEval("matrix(1:10, 2, 5)");
		if (matrixValue.isInteger()) {
			int[] values = matrixValue.asIntegers(); // 値
			int[] dim = matrixValue.dim(); // 行数、列数
		
			// 2行3列目を取得
			int i = 2, j = 3;
			int valueAt = values[(j-1)*dim[0]+(i-1)];
		}
		
		// doubleのmatrixクラスの取得
		REXP doubleMatrixValue = engine.parseAndEval("matrix(rnorm(10), 2, 5)");
		// doubleであることの判定
		if (!doubleMatrixValue.isInteger() && doubleMatrixValue.isNumeric()) {
			double[][] matrix = doubleMatrixValue.asDoubleMatrix();
		}

		// inherits
		REXP iris2 = engine.parseAndEval("iris");
		boolean isDataFrame = iris.inherits("data.frame"); // data.frameクラスか確認

	}
}
