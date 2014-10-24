

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;

public class ParseAndEval {

	public static void main(String[] args) {
		JRIEngine engine = null;
		try {
			// JRIEngine���쐬����B����JRIEngine���g�p����R�ƒʐM����B
			engine = new JRIEngine(new String[0]);
			
			// 'sum(1:10)'�����s���Č��ʂ��擾����
			REXP value = engine.parseAndEval("sum(1:10)");
			
			// ���ʂ�Java�̐����Ƃ��Ď擾���ĕ\��
			if (value.isInteger()) {
				System.out.println(value.asInteger());
			}
		} catch (REngineException e) {
			// REXPMismatchException�ɓ��Ă͂܂�Ȃ���O
		} catch (REXPMismatchException e) {
			// �ϊ��ł��Ȃ��^�Ŏ擾���悤�����Ƃ��ɔ��������O
		} finally {
			// JRIEngine���I������
			if (engine != null)
				engine.close();
		}
	}
	
}
