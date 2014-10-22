package org.rosuda.javaGD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;

public class SwingGDInterface extends GDInterface {
	
	private JFrame window;
	
	public SwingGDInterface() {
		// �����Ȃ��̃R���X�g���N�^�ŃC���X�^���X���쐬�����
		
		// ����JRIEngine�̏�������R���痘�p����ꍇ�̂ݕK�v
		if (REngine.getLastEngine() == null) {
			try {
				// ���łɋN�����Ă���R�𗘗p����
				new JRIEngine(new String[]{"--zero-init"});
			} catch (REngineException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void gdOpen(final double w, final double h) {
		// �f�o�C�X���J��
		super.gdOpen(w, h);
		
		try {
			// Swing�p�X���b�h�Ŏ��s���A���̎��s�̊�����҂�
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					// SwingGDContainer�̍쐬
					SwingGDContainer container = new SwingGDContainer();
					container.setPreferredSize(new Dimension((int) w, (int) h));
					container.addComponentListener(new ComponentAdapter() {
						@Override
						public void componentResized(ComponentEvent e) {
							// �`��̈�̃T�C�Y���ς�������Ƃ�R���ɒʒm����
							try {
								if (getDeviceNumber() >= 0)
									REngine.getLastEngine().parseAndEval("JavaGD:::.javaGD.resize("+(getDeviceNumber()+1)+")");
							} catch (REngineException e1) {
								e1.printStackTrace();
							} catch (REXPMismatchException e1) {
								e1.printStackTrace();
							}
						}
					});
					SwingGDInterface.this.c = container;

					// �O���t�B�b�N�f�o�C�X�E�B���h�E�̍쐬
					window = new JFrame();
					window.setLayout(new BorderLayout());
					window.add(container);
					window.pack(); // �E�B���h�E���̃R���|�[�l���g�ɃE�B���h�E�T�C�Y�����킹��
					window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					window.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							// �E�B���h�E����鑀��������Ƃ��ɁA
							// R���ɂ��̃f�o�C�X�����悤�ɒʒm����
							try {
								if (getDeviceNumber() >= 0)
									REngine.getLastEngine().parseAndEval("dev.off("+(getDeviceNumber()+1)+")");
							} catch (REngineException e1) {
								e1.printStackTrace();
							} catch (REXPMismatchException e1) {
								e1.printStackTrace();
							}
						}
					});
					window.setVisible(true);
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void gdClose() {
		// �f�o�C�X�����
		super.gdClose();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (window != null) {
					window.dispose();
				}
			}
		});
	}
	
	@Override
	public void gdNewPage(int devNr) {
		// �V�K�y�[�W�̊J�n�ƃf�o�C�X�ԍ��̐ݒ�
		super.gdNewPage(devNr);
		updateTitle();
	}
	
	@Override
	public void gdActivate() {
		// �f�o�C�X���A�N�e�B�u�ɂȂ���
		super.gdActivate();
		updateTitle();
	}
	
	@Override
	public void gdDeactivate() {
		// �f�o�C�X���A�N�e�B�u�łȂ��Ȃ���
		super.gdDeactivate();
		updateTitle();
	}
	
	private void updateTitle() {
		// �E�B���h�E�̃^�C�g�����X�V
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (window == null)
					return;
				String title = "Graphics: "+(getDeviceNumber()+1)+" ";
				title += (active) ? "(ACTIVE)" : "(inactive)";
				window.setTitle(title);
			}
		});
	}
}
