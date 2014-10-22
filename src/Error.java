import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;


public class Error {
	
	public static void main(String[] args) {
		JRIEngine engine = null;
		try {
			engine = new JRIEngine(new String[0]);

			// �G���[
			try {
				engine.parseAndEval("a b");
			} catch (REngineException e) {
				System.err.println(e.getMessage());
			}

			// �߂�l�Ƃ��Đ��������҂���ꍇ
			REXP sum = engine.parseAndEval("try(sum(\"abc\"))");
			if (sum.isInteger()) {
				//�@�����Ȃ�ΐ���
			} else {
				// �����łȂ���΃G���[
				// ����ɁA�G���[�I�u�W�F�N�g�Ȃ�G���[���b�Z�[�W���擾
				if (sum.isString() && sum.inherits("try-error")) {
					String errorMessage = sum.asString(); 
				}
			}

			// �߂�l�Ƃ��ĕ���������҂���ꍇ
			REXP ls = engine.parseAndEval("try(ls(\"do_not_exists\"))");
			if (ls.isString() && !ls.inherits("try-error")) {
				// ����  try-error�N���X�łȂ����Ƃ��m�F���K�v
			} else {
				// �G���[
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
