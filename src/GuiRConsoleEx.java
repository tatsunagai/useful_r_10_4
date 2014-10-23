

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

public class GuiRConsoleEx implements REngineCallbacks, REngineInputInterface, REngineOutputInterface, REngineUIInterface {

	private JTextPane outputTextPane;
	private JRIEngine rengine;
	private LinkedList<String> commandQueue = new LinkedList<String>();

	private SimpleAttributeSet inputAttr = new SimpleAttributeSet();
	private SimpleAttributeSet outputAttr = new SimpleAttributeSet();
	private SimpleAttributeSet errorAttr = new SimpleAttributeSet();

	// �R�}���h�q�X�g��
	private List<String> commandHistory = new ArrayList<String>();
	// ���݂̃q�X�g�����̈ʒu
	private int historyPos;
	
	// �o�̓o�b�t�@�̃T�C�Y
	private static final int BUFFER_SIZE = 1024;
	// �o�̓o�b�t�@
	private StringBuilder buffer = new StringBuilder(BUFFER_SIZE);
	// �o�̓o�b�t�@�̌��݂̃^�C�v
	private int bufferType;
	
	// �v�Z����
	private boolean busy;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GuiRConsoleEx();
			}
		});
	}
	
	public GuiRConsoleEx() {
		initComponents();
		initAttributeSets();
		initR();
	}

	private void initComponents() {
		// JFrame���쐬
		JFrame frame = createFrame();
		
		// �R���\�[���o��
		outputTextPane = createOutputTextPane();
		// �R���\�[������
		JTextArea inputTextArea = createInputTextArea();
		
		// JFrame�ɃR���\�[���o�͂ƃR���\�[�����͂�z�u����
		// ���C�A�E�g��BorderLayout�ɐݒ�
		frame.setLayout(new BorderLayout());
		// �R���\�[���o�͂�JScrollPane�ɓ���Ē����ɔz�u
		frame.add(new JScrollPane(outputTextPane), BorderLayout.CENTER);
		// �R���\�[�����͂�JScrollPane�ɓ���ĉ����i��j�ɔz�u
		frame.add(new JScrollPane(inputTextArea), BorderLayout.SOUTH);
		
		// JFrame��\������
		frame.setVisible(true);
	}
	
	private void initAttributeSets() {
		inputAttr.addAttribute(StyleConstants.Foreground, Color.RED);
		outputAttr.addAttribute(StyleConstants.Foreground, Color.BLACK);
		errorAttr.addAttribute(StyleConstants.Foreground, Color.BLUE);
	}
	
	private JFrame createFrame() {
		// "GUI R �R���\�[��"�Ƃ����^�C�g����JFrame���쐬
		JFrame frame = new JFrame("GUI R �R���\�[��");
		// �T�C�Y��ݒ�
		frame.setSize(600, 600);
		// �\������ʒu����ʂ̒����ɐݒ�
		frame.setLocationRelativeTo(null);
		// JFrame������Ƃ��̓����'�Ȃ�'�ɐݒ�
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// �������̓����ݒ�
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				addCommandToQueue("base::q(save=\"no\")");
			}
		});
		return frame;
	}
	
	private JTextPane createOutputTextPane() {
		// JTextPane�͐F�t���\�ȃe�L�X�g�R���|�[�l���g
		JTextPane outputTextPane = new JTextPane();
		outputTextPane.setEditable(false); // �ҏW�s�ɐݒ�
		// �����t�H���g �T�C�Y12�ɐݒ�
		outputTextPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		// �{�[�_�[ ���E��2�̃X�y�[�X
		outputTextPane.setBorder(new EmptyBorder(0, 2, 0, 2));
		return outputTextPane;
	}
	
	private JTextArea createInputTextArea() {
		// JTextArea�́i�F�t���Ȃǂ̂ł��Ȃ��j�e�L�X�g�R���|�[�l���g
		JTextArea inputTextArea = new JTextArea();
		inputTextArea.setLineWrap(true); // �s���Ő܂�Ԃ��悤�ɐݒ�
		inputTextArea.setRows(2); // �\���̈��2�s�ɐݒ�
		// �����t�H���g �T�C�Y12�ɐݒ�
		inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		// �{�[�_�[ ���E��2�̃X�y�[�X
		inputTextArea.setBorder(new EmptyBorder(0, 2, 0, 2));
		// Enter���������Ƃ�������R�}���h�̑��M�ɐݒ�
		inputTextArea.getActionMap().put("submitCommand", new SubmitCommandAction());
		inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				"submitCommand");
		// �㉺�L�[�Ńq�X�g���𑀍�
		inputTextArea.getActionMap().put("historyUp", new HistoryUpAction());
		inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				"historyUp");
		inputTextArea.getActionMap().put("historyDown", new HistoryDownAction());
		inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				"historyDown");
		// ESC�L�[�Ōv�Z�𒆒f
		inputTextArea.getActionMap().put("stop", new StopAction());
		inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				"stop");
		return inputTextArea;
	}
	
	private void initR() {
		try {
			rengine = new JRIEngine(new String[]{"--no-save"}, // R�̋N������
					this, // R�Θb���s�R�[���o�b�N
					false); // �����ɑΘb���[�v���J�n���邩
			rengine.getRni().startMainLoop(); // �Θb���[�v���J�n
		} catch (REngineException e) {
			System.exit(1); // R�̐ڑ��Ɏ��s�����ꍇ�͏I��
		}
	}
	
	private synchronized void addCommandToQueue(String command) {
		commandQueue.addLast(command); // �L���[�̍Ō�ɃR�}���h��ǉ�
		notifyAll(); // getCommandFromQueue()����wait����Thread���N����
	}
	
	private synchronized String getCommandFromQueue() {
		while (commandQueue.isEmpty()) { // �L���[����ł���Ԃ̓��[�v����
			rengine.getRni().rniIdle(); // ����idle�i�������Ă��Ȃ��j���Ƃ�R�ɒʒm
			try {
				wait(50); // 50�~���b�҂�
			} catch (InterruptedException e) {
			}
		}
		return commandQueue.poll(); // �L���[�̍ŏ��̃R�}���h�擾���āA�L���[�������
	}

	private class SubmitCommandAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea inputTextArea = (JTextArea) e.getSource();
			addCommandToQueue(inputTextArea.getText()); // �R�}���h���L���[�ɒǉ�
			inputTextArea.setText(""); // ���͂���ɂ���
		}
	}

	private class HistoryUpAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea inputTextArea = (JTextArea) e.getSource();
			historyPos--;
			if (historyPos < 0) {
				historyPos = 0;
				inputTextArea.setText("");
			} else {
				inputTextArea.setText(commandHistory.get(historyPos));
			}
		}
	}

	private class HistoryDownAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea inputTextArea = (JTextArea) e.getSource();
			historyPos++;
			if (historyPos >= commandHistory.size()) {
				historyPos = commandHistory.size();
				inputTextArea.setText("");
			} else {
				inputTextArea.setText(commandHistory.get(historyPos));
			}
		}
	}

	private class StopAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (busy) {
				rengine.getRni().rniStop(0);
				commandQueue.clear();
			}
		}
	}
	
	//---------------------------------------------------------------------
	// �ȉ���R�̑Θb���[�v�̃R�[���o�b�N�֐�
	//---------------------------------------------------------------------
	@Override
	public String RReadConsole(REngine eng, String prompt, int addToHistory) {
		writeConsole(prompt, 2, true); // �v�����v�g���o��
		
		String command = getCommandFromQueue(); // �R�}���h���L���[����擾
		command += "\n"; // �R�}���h�̍Ō�ɉ��s��ǉ�
		
		writeConsole(command, 2, true); // ���͂����R�}���h���o��
		
		if (addToHistory != 0) { // �q�X�g���ɒǉ����ׂ����̔���
			String c = command.trim(); // �Ō�̉��s�ƑO��̋󔒂�����
			if (c.length() > 0) {
				// �����𒴂����q�X�g��������
				while (commandHistory.size() >= 512) {
					commandHistory.remove(0);
				}
				// �q�X�g���ɒǉ�
				commandHistory.add(c);
				historyPos = commandHistory.size();
			}
		}
		
		return command;
	}
	
	@Override
	public void RWriteConsole(REngine eng, String text, int oType) {
		writeConsole(text, oType, false);
	}

	private void writeConsole(String text, int type, boolean writeImmediately) {
		if (writeImmediately) {
			flushBuffer();
			write(text, type);
		} else {
			// �^�C�v���ς�������̓o�b�t�@��flush����
			if (bufferType != type)
				flushBuffer();

			bufferType = type;

			// �o�b�t�@�������ς��ɂȂ鎞��flush����
			if (buffer.length() + text.length() > buffer.capacity())
				flushBuffer();

			// �o�b�t�@�ɒǉ�
			buffer.append(text);
		}
	}

	private void flushBuffer() {
		// �o�b�t�@�̕��������ʂɏo�͂��āA�o�b�t�@����ɂ���
		if (buffer.length() > 0) {
			write(buffer.toString(), bufferType);
			buffer.setLength(0);
		}
	}
	
	private void write(final String text, final int type) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Document document = outputTextPane.getDocument();
				SimpleAttributeSet attrSet;
				switch(type) {// type�ɉ������F��AttributeSet��I��
				case 0: attrSet = outputAttr; break;
				case 1: attrSet = errorAttr; break;
				default: attrSet = inputAttr; break;
				}
				try {// text��}��
					document.insertString(document.getLength(), text, attrSet);
				} catch (BadLocationException e) {
					// �ʏ킱�̗�O�͔������Ȃ�
				}
				// ��ԉ����\�������悤�ɃX�N���[��
				outputTextPane.scrollRectToVisible(new Rectangle(0, outputTextPane.getHeight(), 0, 0));
			}
		});
	}
	
	@Override
	public void RShowMessage(REngine eng, String text) { 
		writeConsole(text, 1, true); // �G���[�Ƃ��ďo��
	}

	@Override
	public void RFlushConsole(REngine eng) {
		flushBuffer();
	}

	@Override
	public void RBusyState(REngine eng, int state) {
		busy = (state == 1);
	}

	@Override
	public String RChooseFile(REngine eng, boolean newFile) {
		return null;
	}
}