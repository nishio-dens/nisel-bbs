package bbs.gui;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.AWTException;
import java.awt.Event;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import bbs.BBSConfiguration;
import bbs.server.BBSServer;

import polaris.upnp.UPnPManager;
import polaris.upnp.gui.UPnP;
import javax.swing.JToolBar;
import javax.swing.JInternalFrame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.UnknownHostException;

public class GUIMain {

	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JDialog aboutDialog = null;  //  @jve:decl-index=0:visual-constraint="583,10"
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	private SystemTray systemTray;
	private TrayIcon trayIcon;  //  @jve:decl-index=0:
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JTextField jTextField = null;
	private JTextField jTextField1 = null;
	private JTextField jTextField2 = null;
	private JCheckBox jCheckBox = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JLabel jLabel3 = null;
	GUIManager guiManager = null;
	private JLabel jLabel4 = null;
	private JTextField jTextField3 = null;
	/**
	 * This method initializes jFrame
	 *
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("/resources/P2PBBS.png"));
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(545, 247);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("Nisel BBS");
			//トレイ関係
			systemTray = SystemTray.getSystemTray();
			Image image = null;
			try {
				image = new ImageIcon(getClass().getResource(
						"/resources/P2PBBS.png")).getImage();
			}catch(Exception e) {
				System.err.println( "システムトレイ用のアイコンが見つかりませんでした." );
				e.printStackTrace();
				image = new BufferedImage(16,16, BufferedImage.TYPE_INT_RGB);
			}
			final PopupMenu popup = new PopupMenu();
			trayIcon = new TrayIcon(image, "P2PBBS", popup);

			MenuItem item1 = new MenuItem("Open Window");
			item1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jFrame.setVisible(true);
				}
			});
			MenuItem item2 = new MenuItem("Exit");
			item2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					systemTray.remove(trayIcon);
					jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					jFrame.dispose();
					if( guiManager != null ) {
						guiManager.stop();
					}
					System.exit(0);
				}
			});
			popup.add(item1);
			popup.add(item2);

			try{
				systemTray.add(trayIcon);
			}catch(AWTException e) {
				e.printStackTrace();
			}
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints11.gridwidth = 3;
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 3;
			gridBagConstraints11.ipadx = 312;
			gridBagConstraints11.ipady = 1;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.insets = new Insets(4, 4, 5, 16);
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.insets = new Insets(4, 15, 4, 3);
			gridBagConstraints10.gridy = 3;
			gridBagConstraints10.ipadx = 24;
			gridBagConstraints10.ipady = 6;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridx = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.insets = new Insets(5, 5, 3, 4);
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 5;
			gridBagConstraints9.ipadx = 504;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridwidth = 4;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new Insets(5, 6, 5, 20);
			gridBagConstraints8.gridy = 4;
			gridBagConstraints8.ipadx = 52;
			gridBagConstraints8.ipady = 7;
			gridBagConstraints8.gridx = 3;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.insets = new Insets(5, 5, 5, 6);
			gridBagConstraints7.gridy = 4;
			gridBagConstraints7.ipadx = 52;
			gridBagConstraints7.ipady = 7;
			gridBagConstraints7.gridx = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.insets = new Insets(4, 12, 18, 5);
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 4;
			gridBagConstraints6.ipadx = 108;
			gridBagConstraints6.ipady = -3;
			gridBagConstraints6.gridwidth = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints5.gridwidth = 3;
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.ipadx = 329;
			gridBagConstraints5.ipady = 1;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new Insets(3, 4, 4, 16);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints4.gridwidth = 3;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.ipadx = 269;
			gridBagConstraints4.ipady = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(4, 4, 5, 16);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridwidth = 3;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.ipadx = 369;
			gridBagConstraints3.ipady = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(12, 4, 4, 16);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(5, 15, 3, 3);
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.ipadx = 11;
			gridBagConstraints2.ipady = 6;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(11, 15, 4, 3);
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.ipadx = 89;
			gridBagConstraints1.ipady = 6;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(3, 15, 3, 3);
			gridBagConstraints.gridy = 2;
			gridBagConstraints.ipadx = 11;
			gridBagConstraints.ipady = 6;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridx = 0;
			jLabel4 = new JLabel();
			jLabel4.setText("ブラウザアドレス");
			jLabel3 = new JLabel();
			jLabel3.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			jLabel3.setText("情報");
			jLabel2 = new JLabel();
			jLabel2.setHorizontalTextPosition(SwingConstants.TRAILING);
			jLabel2.setText("利用するIPアドレス");
			jLabel1 = new JLabel();
			jLabel1.setHorizontalTextPosition(SwingConstants.TRAILING);
			jLabel1.setText("接続先");
			jLabel = new JLabel();
			jLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
			jLabel.setText("利用するポート番号");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(jLabel, gridBagConstraints);
			jContentPane.add(jLabel1, gridBagConstraints1);
			jContentPane.add(jLabel2, gridBagConstraints2);
			jContentPane.add(getJTextField(), gridBagConstraints3);
			jContentPane.add(getJTextField1(), gridBagConstraints4);
			jContentPane.add(getJTextField2(), gridBagConstraints5);
			jContentPane.add(getJCheckBox(), gridBagConstraints6);
			jContentPane.add(getJButton(), gridBagConstraints7);
			jContentPane.add(getJButton1(), gridBagConstraints8);
			jContentPane.add(jLabel3, gridBagConstraints9);
			jContentPane.add(jLabel4, gridBagConstraints10);
			jContentPane.add(getJTextField3(), gridBagConstraints11);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar
	 *
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if( guiManager != null ) {
						guiManager.stop();
					}
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();
					aboutDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutDialog
	 *
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(getJFrame(), true);
			aboutDialog.setTitle("About");
			aboutDialog.setContentPane(getAboutContentPane());
			aboutDialog.setSize(new Dimension(156, 91));
			//aboutDialog.setContentPane(getAboutContentPane());
		}
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new BorderLayout());
			aboutContentPane.add(getAboutVersionLabel(), BorderLayout.CENTER);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutVersionLabel
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getAboutVersionLabel() {
		if (aboutVersionLabel == null) {
			aboutVersionLabel = new JLabel();
			aboutVersionLabel.setText("Nisel BBS Version 0.7");
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}

	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setText("");
		}
		return jTextField;
	}

	/**
	 * This method initializes jTextField1
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
			try {
				jTextField1.setText( java.net.InetAddress.getLocalHost().getHostAddress().toString() );
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return jTextField1;
	}

	/**
	 * This method initializes jTextField2
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField2() {
		if (jTextField2 == null) {
			jTextField2 = new JTextField();
			jTextField2.setText("40001");
		}
		return jTextField2;
	}

	/**
	 * This method initializes jCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setSelected(false);
			jCheckBox.setEnabled(true);
			jCheckBox.setText("UPnP機能を利用する");
		}
		return jCheckBox;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("接続");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if( guiManager == null ) {
						guiManager = new GUIManager(trayIcon, getJFrame(), jLabel3);
					}
					if( jTextField3.getText() != null ) {
						BBSConfiguration.setGATEWAY_ADDRESS(jTextField3.getText());
					}
					guiManager.start(jTextField.getText(), jTextField1
							.getText(), jTextField2.getText(), jCheckBox
							.isSelected());
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("終了");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if( guiManager != null ) {
						guiManager.stop();
					}
					System.exit(0);
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField3() {
		if (jTextField3 == null) {
			jTextField3 = new JTextField();
			jTextField3.setText("localhost");
		}
		return jTextField3;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUIMain application = new GUIMain();
				application.getJFrame().setVisible(true);
			}
		});
	}

}
