

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;

public class Quit {

	public static void main(String[] args) throws REngineException {
		JRIEngine engine = null;
		try {
			engine = new JRIEngine(new String[0]);
			
			// JRIEngine���g��������...
			
		} finally {
			if (engine != null) {
				try {
					// �v���O�����̏I��
					// ���̌Ăяo���Ńv���O�����̃v���Z�X���ƏI������
					engine.parseAndEval("base::q(save=\"no\")");
				} catch (REngineException e) {
				} catch (REXPMismatchException e) {
				} finally {
					// �ʏ킱�̃R�[�h�͎��s����Ȃ�
					// JRIEngine���I������
					engine.close();
				}
			}
		}
	}
}
