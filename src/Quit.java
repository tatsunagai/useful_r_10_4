

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;

public class Quit {

	public static void main(String[] args) throws REngineException {
		JRIEngine engine = null;
		try {
			engine = new JRIEngine(new String[0]);
			
			// JRIEngineを使った処理...
			
		} finally {
			if (engine != null) {
				try {
					// プログラムの終了
					// この呼び出しでプログラムのプロセスごと終了する
					engine.parseAndEval("base::q(save=\"no\")");
				} catch (REngineException e) {
				} catch (REXPMismatchException e) {
				} finally {
					// 通常このコードは実行されない
					// JRIEngineを終了する
					engine.close();
				}
			}
		}
	}
}
