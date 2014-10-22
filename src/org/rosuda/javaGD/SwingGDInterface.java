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
		// 引数なしのコンストラクタでインスタンスが作成される
		
		// このJRIEngineの初期化はRから利用する場合のみ必要
		if (REngine.getLastEngine() == null) {
			try {
				// すでに起動しているRを利用する
				new JRIEngine(new String[]{"--zero-init"});
			} catch (REngineException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void gdOpen(final double w, final double h) {
		// デバイスを開く
		super.gdOpen(w, h);
		
		try {
			// Swing用スレッドで実行し、その実行の完了を待つ
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					// SwingGDContainerの作成
					SwingGDContainer container = new SwingGDContainer();
					container.setPreferredSize(new Dimension((int) w, (int) h));
					container.addComponentListener(new ComponentAdapter() {
						@Override
						public void componentResized(ComponentEvent e) {
							// 描画領域のサイズが変わったことをR側に通知する
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

					// グラフィックデバイスウィンドウの作成
					window = new JFrame();
					window.setLayout(new BorderLayout());
					window.add(container);
					window.pack(); // ウィンドウ内のコンポーネントにウィンドウサイズを合わせる
					window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					window.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							// ウィンドウを閉じる操作をしたときに、
							// R側にこのデバイスを閉じるように通知する
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
		// デバイスを閉じる
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
		// 新規ページの開始とデバイス番号の設定
		super.gdNewPage(devNr);
		updateTitle();
	}
	
	@Override
	public void gdActivate() {
		// デバイスがアクティブになった
		super.gdActivate();
		updateTitle();
	}
	
	@Override
	public void gdDeactivate() {
		// デバイスがアクティブでなくなった
		super.gdDeactivate();
		updateTitle();
	}
	
	private void updateTitle() {
		// ウィンドウのタイトルを更新
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
