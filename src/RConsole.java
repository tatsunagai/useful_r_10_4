

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
					new String[]{"--no-save"}, // Rの起動引数
					console, // 対話ループ用コールバック
					true); // 対話ループをすぐに開始するか
		} catch (REngineException e) {
			System.err.println("Rとの接続に失敗しました");
		}
	}
	
	public RConsole() {
		stdInReader = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public String RReadConsole(REngine eng, String prompt, int addToHistory) {
		// プロンプトを表示
		System.out.print(prompt);
		// 入力の読み込み
		try {
			String s = stdInReader.readLine(); // 標準入力から１行読み込み
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
