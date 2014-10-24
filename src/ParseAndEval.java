

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;

public class ParseAndEval {

	public static void main(String[] args) {
		JRIEngine engine = null;
		try {
			// JRIEngineを作成する。このJRIEngineを使用してRと通信する。
			engine = new JRIEngine(new String[0]);
			
			// 'sum(1:10)'を実行して結果を取得する
			REXP value = engine.parseAndEval("sum(1:10)");
			
			// 結果をJavaの整数として取得して表示
			if (value.isInteger()) {
				System.out.println(value.asInteger());
			}
		} catch (REngineException e) {
			// REXPMismatchExceptionに当てはまらない例外
		} catch (REXPMismatchException e) {
			// 変換できない型で取得しようしたときに発生する例外
		} finally {
			// JRIEngineを終了する
			if (engine != null)
				engine.close();
		}
	}
	
}
