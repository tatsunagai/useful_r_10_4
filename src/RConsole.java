

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineCallbacks;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.REngineInputInterface;
import org.rosuda.REngine.REngineOutputInterface;
import org.rosuda.REngine.JRI.JRIEngine;

public class RConsole implements REngineCallbacks, REngineInputInterface, REngineOutputInterface {
	
	private BufferedReader stdInReader;

	public static void main(String[] args) {
		try {
			RConsole console = new RConsole();
			new JRIEngine(
					new String[]{"--no-save"}, // R�̋N������
					console, // �Θb���[�v�p�R�[���o�b�N
					true); // �Θb���[�v�������ɊJ�n���邩
		} catch (REngineException e) {
			System.err.println("R�Ƃ̐ڑ��Ɏ��s���܂���");
		}
	}
	
	public RConsole() {
		stdInReader = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public String RReadConsole(REngine eng, String prompt, int addToHistory) {
		// �v�����v�g��\��
		System.out.print(prompt);
		// ���͂̓ǂݍ���
		try {
			String s = stdInReader.readLine(); // �W�����͂���P�s�ǂݍ���
			if (s != null)
				return s + "\n";
		} catch (IOException e) {
			System.err.println("RReadConsole exception: " + e.getMessage());
		}
		return "base::q(save=\"no\")\n";
	}

	@Override
	public void RWriteConsole(REngine eng, String text, int oType) {
		if (oType == 0) {
			System.out.print(text);
		} else {
			System.err.print(text);
		}
	}

	@Override
	public void RShowMessage(REngine eng, String text) {
		System.err.println("*** "+text);
	}
	
	@Override
	public void RFlushConsole(REngine eng) {
	}
	
}
