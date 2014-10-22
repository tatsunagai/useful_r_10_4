

import org.rosuda.JRI.Mutex;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineCallbacks;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;

public class IdleEval implements REngineCallbacks {
	
	public static REXP idleParseAndEval(JRIEngine engine, String command, REXP env,
			boolean resolve) throws REngineException, REXPMismatchException {
		Mutex mutex = engine.getRni().getRsync(); // 同期管理用オブジェクト
		// Rと通信するためのロックを取得を試みる
		boolean v = mutex.safeLock();
		if(!v) // ロックの取得できなかった場合、nullを返す
			return null;
		try {
			return engine.parseAndEval(command, env, resolve);
		} finally {
			mutex.unlock(); // ロックの解放
		}
	}

	public static REXP timeoutParseAndEval(JRIEngine engine, String command, REXP env,
			boolean resolve, long timeout) throws REngineException, REXPMismatchException {
		Mutex mutex = engine.getRni().getRsync(); // 同期管理用オブジェクト
		// 指定ミリ秒の間、Rと通信するためのロックを取得を試みる
		boolean v = mutex.safeLockWithTimeout(timeout);
		if(!v) // ロックの取得できなかった場合、nullを返す
			return null;
		try {
			return engine.parseAndEval(command, env, resolve);
		} finally {
			mutex.unlock(); // ロックの解放
		}
	}

}
