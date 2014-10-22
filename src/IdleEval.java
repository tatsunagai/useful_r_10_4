

import org.rosuda.JRI.Mutex;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineCallbacks;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;

public class IdleEval implements REngineCallbacks {
	
	public static REXP idleParseAndEval(JRIEngine engine, String command, REXP env,
			boolean resolve) throws REngineException, REXPMismatchException {
		Mutex mutex = engine.getRni().getRsync(); // �����Ǘ��p�I�u�W�F�N�g
		// R�ƒʐM���邽�߂̃��b�N���擾�����݂�
		boolean v = mutex.safeLock();
		if(!v) // ���b�N�̎擾�ł��Ȃ������ꍇ�Anull��Ԃ�
			return null;
		try {
			return engine.parseAndEval(command, env, resolve);
		} finally {
			mutex.unlock(); // ���b�N�̉��
		}
	}

	public static REXP timeoutParseAndEval(JRIEngine engine, String command, REXP env,
			boolean resolve, long timeout) throws REngineException, REXPMismatchException {
		Mutex mutex = engine.getRni().getRsync(); // �����Ǘ��p�I�u�W�F�N�g
		// �w��~���b�̊ԁAR�ƒʐM���邽�߂̃��b�N���擾�����݂�
		boolean v = mutex.safeLockWithTimeout(timeout);
		if(!v) // ���b�N�̎擾�ł��Ȃ������ꍇ�Anull��Ԃ�
			return null;
		try {
			return engine.parseAndEval(command, env, resolve);
		} finally {
			mutex.unlock(); // ���b�N�̉��
		}
	}

}
