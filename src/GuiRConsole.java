

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineCallbacks;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.REngineInputInterface;
import org.rosuda.REngine.REngineOutputInterface;
import org.rosuda.REngine.REngineUIInterface;
import org.rosuda.REngine.JRI.JRIEngine;

public class GuiRConsole implements REngineCallbacks, REngineInputInterface, REngineOutputInterface, REngineUIInterface {

	private JTextPane outputTextPane;
	private JRIEngine rengine;
	private LinkedList<String> commandQueue = new LinkedList<String>();

	private SimpleAttributeSet inputAttr = new SimpleAttributeSet();
	private SimpleAttributeSet outputAttr = new SimpleAttributeSet();
	private SimpleAttributeSet errorAttr = new SimpleAttributeSet();
	
	private boolean busy;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GuiRConsole();
			}
		});
	}
	
	public GuiRConsole() {
		initComponents();
		initAttributeSets();
		initR();
	}

	private void initComponents() {
		// JFrameを作成
		JFrame frame = createFrame();
		
		// コンソール出力
		outputTextPane = createOutputTextPane();
		// コンソール入力
		JTextArea inputTextArea = createInputTextArea();
		
		// JFrameにコンソール出力とコンソール入力を配置する
		// レイアウトをBorderLayoutに設定
		frame.setLayout(new BorderLayout());
		// コンソール出力をJScrollPaneに入れて中央に配置
		frame.add(new JScrollPane(outputTextPane), BorderLayout.CENTER);
		// コンソール入力をJScrollPaneに入れて下部（南）に配置
		frame.add(new JScrollPane(inputTextArea), BorderLayout.SOUTH);
		
		// JFrameを表示する
		frame.setVisible(true);
	}
	
	private void initAttributeSets() {
		inputAttr.addAttribute(StyleConstants.Foreground, Color.RED);
		outputAttr.addAttribute(StyleConstants.Foreground, Color.BLACK);
		errorAttr.addAttribute(StyleConstants.Foreground, Color.BLUE);
	}
	
	private JFrame createFrame() {
		// "GUI R コンソール"というタイトルのJFrameを作成
		JFrame frame = new JFrame("GUI R コンソール");
		// サイズを設定
		frame.setSize(600, 600);
		// 表示する位置を画面の中央に設定
		frame.setLocationRelativeTo(null);
		// JFrameを閉じたときの動作を'なし'に設定
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// 閉じた時の動作を設定
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				addCommandToQueue("base::q(save=\"no\")");
			}
		});
		return frame;
	}
	
	private JTextPane createOutputTextPane() {
		// JTextPaneは色付け可能なテキストコンポーネント
		JTextPane outputTextPane = new JTextPane();
		outputTextPane.setEditable(false); // 編集不可に設定
		// 等幅フォント サイズ12に設定
		outputTextPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		// ボーダー 左右に2のスペース
		outputTextPane.setBorder(new EmptyBorder(0, 2, 0, 2));
		return outputTextPane;
	}
	
	private JTextArea createInputTextArea() {
		// JTextAreaは（色付けなどのできない）テキストコンポーネント
		JTextArea inputTextArea = new JTextArea();
		inputTextArea.setLineWrap(true); // 行末で折り返すように設定
		inputTextArea.setRows(2); // 表示領域を2行に設定
		// 等幅フォント サイズ12に設定
		inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		// ボーダー 左右に2のスペース
		inputTextArea.setBorder(new EmptyBorder(0, 2, 0, 2));
		// Enterを押したとき動作をコマンドの送信に設定
		inputTextArea.getActionMap().put("submitCommand", new SubmitCommandAction());
		inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				"submitCommand");
		// ESCキーで計算を中断
		inputTextArea.getActionMap().put("stop", new StopAction());
		inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				"stop");
		return inputTextArea;
	}
	
	private void initR() {
		try {
			rengine = new JRIEngine(new String[]{"--no-save"}, // Rの起動引数
					this, // R対話実行コールバック
					false); // すぐに対話ループを開始するか
			rengine.getRni().startMainLoop(); // 対話ループを開始
		} catch (REngineException e) {
			System.exit(1); // Rの接続に失敗した場合は終了
		}
	}
	
	private synchronized void addCommandToQueue(String command) {
		commandQueue.addLast(command); // キューの最後にコマンドを追加
		notifyAll(); // getCommandFromQueue()内でwait中のThreadを起こす
	}
	
	private synchronized String getCommandFromQueue() {
		while (commandQueue.isEmpty()) { // キューが空である間はループする
			rengine.getRni().rniIdle(); // 現在idle（何もしていない）ことをRに通知
			try {
				wait(50); // 50ミリ秒待つ
			} catch (InterruptedException e) {
			}
		}
		return commandQueue.poll(); // キューの最初のコマンド取得して、キューから消去
	}

	private class SubmitCommandAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea inputTextArea = (JTextArea) e.getSource();
			addCommandToQueue(inputTextArea.getText()); // コマンドをキューに追加
			inputTextArea.setText(""); // 入力を空にする
		}
	}

	private class StopAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (busy) { // 計算中のみ中断を実行
				rengine.getRni().rniStop(0);
				commandQueue.clear();
			}
		}
	}
	
	//---------------------------------------------------------------------
	// 以下はRの対話ループのコールバック関数
	//---------------------------------------------------------------------
	@Override
	public String RReadConsole(REngine eng, String prompt, int addToHistory) {
		writeConsole(prompt, 2); // プロンプトを出力
		
		String command = getCommandFromQueue(); // コマンドをキューから取得
		command += "\n"; // コマンドの最後に改行を追加
		
		writeConsole(command, 2); // 入力したコマンドを出力
		return command;
	}
	
	@Override
	public void RWriteConsole(REngine eng, String text, int oType) {
		writeConsole(text, oType);
	}

	private void writeConsole(final String text, final int type) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Document document = outputTextPane.getDocument();
				SimpleAttributeSet attrSet;
				switch(type) {// typeに応じた色のAttributeSetを選択
				case 0: attrSet = outputAttr; break;
				case 1: attrSet = errorAttr; break;
				default: attrSet = inputAttr; break;
				}
				try {// textを挿入
					document.insertString(document.getLength(), text, attrSet);
				} catch (BadLocationException e) {
					// 通常この例外は発生しない
				}
				// 一番下が表示されるようにスクロール
				outputTextPane.scrollRectToVisible(new Rectangle(0, outputTextPane.getHeight(), 0, 0));
			}
		});
	}
	
	@Override
	public void RShowMessage(REngine eng, String text) { 
		writeConsole(text, 1); // エラーとして出力
	}

	@Override
	public void RFlushConsole(REngine eng) { }
	
	@Override
	public void RBusyState(REngine eng, int state) {
		busy = (state == 1); // ビジー状態をメンバ変数に保存
	}
	
	@Override
	public String RChooseFile(REngine eng, boolean newFile) {
		return null;
	}
}
