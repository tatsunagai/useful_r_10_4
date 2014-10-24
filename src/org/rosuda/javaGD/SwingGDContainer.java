package org.rosuda.javaGD;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SwingGDContainer extends JPanel implements GDContainer {
	
	private List<GDObject> gdObjects = new LinkedList<GDObject>();
	private GDState gdState = new GDState();
	private int deviceNumber = -1;
	
	@Override
	public void add(final GDObject o) {
		// �`�施�߂�����GDObject��ێ�����
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gdObjects.add(o);
			}
		});
	}

	@Override
	public void reset() {
		// �ێ����Ă���GDObject���N���A���āA�����`�悵�Ȃ�����
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gdObjects.clear();
				repaint();
			}
		});
	}

	@Override
	public GDState getGState() {
		// GDState�̎擾
		return gdState;
	}

	@Override
	public void syncDisplay(boolean finish) {
		// ��ʂ̍ĕ`����s��
		if (finish) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					repaint();
				}
			});
		}
	}

	@Override
	public void closeDisplay() {
		// �f�o�C�X�������鎞�ɌĂт����
	}

	@Override
	public void setDeviceNumber(int dn) {
		// �f�o�C�X�ԍ���ݒ�
		deviceNumber = dn;
	}
	
	@Override
	public int getDeviceNumber() {
		// �f�o�C�X�ԍ����擾
		return deviceNumber;
	}

	@Override
	public boolean prepareLocator(LocatorSync ls) {
		// ���񂱂̋@�\�͎������Ȃ�
		return false;
	}
	
	@Override
	public Graphics getGraphics() {
		return super.getGraphics();
	}
	
	@Override
	public Dimension getSize() {
		return super.getSize();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// ���̊֐�����JPanel�̕`����s��
		super.paintComponent(g);
		
		// �`��Ɏg�p����Graphics2D���쐬
		Graphics2D g2 = (Graphics2D) g.create();
		
		// �A���`�G�C���A�X�Ȃǂ̐ݒ�
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		
		// �w�i�𔒂œh��
		Dimension d = getSize();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, d.width, d.height);
		
		g2.setFont(gdState.f);
		
		// GDObject���g�p���ĕ`��
		for (GDObject obj : gdObjects) {
			obj.paint(this, gdState, g2);
		}
		
		// �쐬����Graphics2D��j��
		g2.dispose();
	}
}
