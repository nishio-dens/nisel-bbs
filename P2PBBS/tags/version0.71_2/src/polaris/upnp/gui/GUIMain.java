package polaris.upnp.gui;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Event;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import java.awt.Point;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JDialog;
import java.awt.Dimension;
import javax.swing.JTable;
import java.awt.Rectangle;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.UnknownHostException;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

public class GUIMain {

	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JDialog aboutDialog = null;  //  @jve:decl-index=0:visual-constraint="871,10"
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private UPnP upnp = null;
	private UPnPTableModel tableModel = null;
	private JLabel informationLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JDialog jDialog = null;  //  @jve:decl-index=0:visual-constraint="880,86"
	private JPanel jContentPane1 = null;
	private JLabel jLabel = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JTextField jTextField = null;
	private JComboBox jComboBox = null;
	private JTextField jTextField1 = null;
	private JTextField jTextField2 = null;
	private JTextArea jTextArea = null;
	private JButton jButton3 = null;
	private JButton jButton4 = null;
	
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(851, 315);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("PolarisUPnP");
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
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.insets = new Insets(8, 3, 8, 19);
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.ipadx = 174;
			gridBagConstraints7.ipady = 10;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 4;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.insets = new Insets(8, 7, 8, 3);
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.ipadx = 22;
			gridBagConstraints6.ipady = 10;
			gridBagConstraints6.anchor = GridBagConstraints.EAST;
			gridBagConstraints6.gridx = 3;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new Insets(8, 1, 2, 12);
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.ipadx = 754;
			gridBagConstraints5.ipady = 7;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridwidth = 5;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.insets = new Insets(5, 11, 8, 6);
			gridBagConstraints41.gridy = 1;
			gridBagConstraints41.ipadx = 44;
			gridBagConstraints41.ipady = 3;
			gridBagConstraints41.gridx = 2;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(5, 13, 8, 11);
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.ipadx = 44;
			gridBagConstraints3.ipady = 3;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(5, 11, 8, 13);
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.ipadx = 18;
			gridBagConstraints2.ipady = 3;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridwidth = 5;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.ipadx = 369;
			gridBagConstraints1.ipady = -258;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.insets = new Insets(12, 10, 4, 9);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new Insets(8, 1, 2, 12);
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.ipadx = 790;
			gridBagConstraints4.ipady = 7;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridwidth = 5;
			jLabel2 = new JLabel();
			jLabel2.setText("0.0.0.0");
			jLabel1 = new JLabel();
			jLabel1.setText("外部アドレス");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setEnabled(false);
			jContentPane.add(getJScrollPane(), gridBagConstraints1);
			jContentPane.add(getJButton(), gridBagConstraints2);
			jContentPane.add(getJButton1(), gridBagConstraints3);
			jContentPane.add(getJButton2(), gridBagConstraints41);
			jContentPane.add(getInformationLabel(), gridBagConstraints5);
			jContentPane.add(jLabel1, gridBagConstraints6);
			jContentPane.add(jLabel2, gridBagConstraints7);
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
					//aboutDialog.pack();
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
			aboutDialog.setSize(new Dimension(350, 72));
			aboutDialog.setContentPane(getAboutContentPane());
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
			aboutVersionLabel.setText("Polaris UPnP version 1.0 created by Nishio.");
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			tableModel = new UPnPTableModel();
			jTable.setModel( tableModel );
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			jTable.setShowGrid(true);
			int[] minWidth = {20,130,50,100,50,130,150,100};
			for(int i=0; i < minWidth.length; i++ ) {
				jTable.getColumnModel().getColumn(i).setMinWidth(minWidth[i]);
			}			
		}
		return jTable;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("ルータ情報取得");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if( upnp == null ) {
						upnp = new UPnP(tableModel, getInformationLabel(), jLabel2);
						getJButton1().setEnabled(true);
						getJButton2().setEnabled(true);
					}
					upnp.discover();
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
			jButton1.setText("ポート開放");
			jButton1.setEnabled(false);
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JDialog mappingDialog = getJDialog();
					Point loc = getJFrame().getLocation();
					loc.translate(50, 50);
					mappingDialog.setLocation(loc);
					mappingDialog.setVisible(true);
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("ポート削除");
			jButton2.setEnabled(false);
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int row = getJTable().getSelectedRow();
					if( row >= 0) {
						int extPort = Integer.parseInt( tableModel.getValueAt(row, 2).toString() );
						String protocol = tableModel.getValueAt(row, 3).toString();
						int rc = JOptionPane.showConfirmDialog(null, "外部ポート:" + extPort
								+ " プロトコル:" + protocol + " を削除してもよいですか?");
						switch(rc) {
						case JOptionPane.YES_OPTION:
							upnp.deletePortMapping(extPort, protocol);
							tableModel.clear(row);
							break;
						case JOptionPane.NO_OPTION:
							break;
						case JOptionPane.CANCEL_OPTION:
							break;
						case JOptionPane.CLOSED_OPTION:
							break;
						default:
							break;
						}
					}
				}
			});
		}
		return jButton2;
	}

	/**
	 * This method initializes informationLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getInformationLabel() {
		if (informationLabel == null) {
			informationLabel = new JLabel();
			informationLabel.setText("Information");
		}
		return informationLabel;
	}

	/**
	 * This method initializes jDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getJDialog() {
		if (jDialog == null) {
			jDialog = new JDialog(getJFrame());
			jDialog.setSize(new Dimension(458, 261));
			jDialog.setTitle("PortMapping");
			jDialog.setContentPane(getJContentPane1());
		}
		return jDialog;
	}

	/**
	 * This method initializes jContentPane1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane1() {
		if (jContentPane1 == null) {
			jLabel6 = new JLabel();
			jLabel6.setBounds(new Rectangle(24, 143, 146, 26));
			jLabel6.setText("概要");
			jLabel5 = new JLabel();
			jLabel5.setBounds(new Rectangle(24, 84, 147, 26));
			jLabel5.setText("外部ポート番号");
			jLabel4 = new JLabel();
			jLabel4.setBounds(new Rectangle(24, 115, 146, 26));
			jLabel4.setText("内部ポート番号");
			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(24, 54, 149, 26));
			jLabel3.setText("プロトコル");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(24, 23, 147, 26));
			jLabel.setText("開放したい内部IP");
			jContentPane1 = new JPanel();
			jContentPane1.setLayout(null);
			jContentPane1.add(jLabel, null);
			jContentPane1.add(jLabel3, null);
			jContentPane1.add(jLabel4, null);
			jContentPane1.add(jLabel5, null);
			jContentPane1.add(jLabel6, null);
			jContentPane1.add(getJTextField(), null);
			jContentPane1.add(getJComboBox(), null);
			jContentPane1.add(getJTextField1(), null);
			jContentPane1.add(getJTextField2(), null);
			jContentPane1.add(getJTextArea(), null);
			jContentPane1.add(getJButton3(), null);
			jContentPane1.add(getJButton4(), null);
			try {
				getJTextField().setText( java.net.InetAddress.getLocalHost().getHostAddress().toString() );
			} catch (UnknownHostException e) {
				//無視
			}
		}
		return jContentPane1;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(new Rectangle(182, 23, 248, 23));
		}
		return jTextField;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.setBounds(new Rectangle(182, 52, 248, 23));
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			model.addElement("UDP");
			model.addElement("TCP");
			jComboBox.setModel(model);
		}
		return jComboBox;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setBounds(new Rectangle(182, 84, 248, 23));
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
			jTextField2.setBounds(new Rectangle(182, 117, 248, 23));
		}
		return jTextField2;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setBounds(new Rectangle(182, 147, 248, 37));
		}
		return jTextArea;
	}

	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton3() {
		if (jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setBounds(new Rectangle(193, 194, 121, 30));
			jButton3.setText("OK");
			jButton3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String internalIP = getJTextField().getText().toString();
					String protocol = getJComboBox().getSelectedItem().toString();
					int extPort = 0;
					int intPort = 0;
					try{
						extPort = Integer.parseInt( getJTextField1().getText().toString() );
						intPort = Integer.parseInt( getJTextField2().getText().toString() );
					}catch(Exception ex) {
						ex.printStackTrace();
					}
					String desc = getJTextArea().getText().toString();
					if( upnp != null ) {
						upnp.addPortMapping(extPort, protocol, intPort, internalIP, desc);
					}
					getJDialog().setVisible(false);
				}
			});
		}
		return jButton3;
	}

	/**
	 * This method initializes jButton4	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton4() {
		if (jButton4 == null) {
			jButton4 = new JButton();
			jButton4.setBounds(new Rectangle(324, 194, 121, 30));
			jButton4.setText("キャンセル");
			jButton4.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getJDialog().setVisible(false);
				}
			});
		}
		return jButton4;
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
