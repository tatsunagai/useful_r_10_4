import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;


public class Error {
	
	public static void main(String[] args) {
		JRIEngine engine = null;
		try {
			engine = new JRIEngine(new String[0]);

			// エラー
			try {
				engine.parseAndEval("a b");
			} catch (REngineException e) {
				System.err.println(e.getMessage());
			}

			// 戻り値として整数を期待する場合
			REXP sum = engine.parseAndEval("try(sum(\"abc\"))");
			if (sum.isInteger()) {
				//　整数ならば正常
			} else {
				// 整数でなければエラー
				// さらに、エラーオブジェクトならエラーメッセージを取得
				if (sum.isString() && sum.inherits("try-error")) {
					String errorMessage = sum.asString(); 
				}
			}

			// 戻り値として文字列を期待する場合
			REXP ls = engine.parseAndEval("try(ls(\"do_not_exists\"))");
			if (ls.isString() && !ls.inherits("try-error")) {
				// 正常  try-errorクラスでないことも確認が必要
			} else {
				// エラー
			}
			
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
	
}
