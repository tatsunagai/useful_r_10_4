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
		// 描画命令を持つGDObjectを保持する
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gdObjects.add(o);
			}
		});
	}

	@Override
	public void reset() {
		// 保持していたGDObjectをクリアして、何も描画しなくする
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
		// GDStateの取得
		return gdState;
	}

	@Override
	public void syncDisplay(boolean finish) {
		// 画面の再描画を行う
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
		// デバイスが閉じられる時に呼びされる
	}

	@Override
	public void setDeviceNumber(int dn) {
		// デバイス番号を設定
		deviceNumber = dn;
	}
	
	@Override
	public int getDeviceNumber() {
		// デバイス番号を取得
		return deviceNumber;
	}

	@Override
	public boolean prepareLocator(LocatorSync ls) {
		// 今回この機能は実装しない
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
		// この関数内でJPanelの描画を行う
		super.paintComponent(g);
		
		// 描画に使用するGraphics2Dを作成
		Graphics2D g2 = (Graphics2D) g.create();
		
		// アンチエイリアスなどの設定
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		
		// 背景を白で塗る
		Dimension d = getSize();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, d.width, d.height);
		
		g2.setFont(gdState.f);
		
		// GDObjectを使用して描画
		for (GDObject obj : gdObjects) {
			obj.paint(this, gdState, g2);
		}
		
		// 作成したGraphics2Dを破棄
		g2.dispose();
	}
}
