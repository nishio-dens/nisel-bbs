package polaris.upnp.gui;

import javax.swing.table.DefaultTableModel;

import polaris.upnp.RooterPortMappingEntry;

public class UPnPTableModel extends DefaultTableModel {

/*	private String newRemoteHost = null;
	private String newExternalPort = null;
	private String newProtocol = null;
	private String newInternalPort = null;
	private String newInternalClient = null;
	private String newEnabled = null;
	private String newPortMappingDescription = null;
	private String newLeaseDuration = null;
	*/
	private static final ColumnContext[] columnArray = {
        new ColumnContext("No",     Integer.class, false),
        new ColumnContext("ホスト"  , String.class,  false),
        new ColumnContext("外部ポート", String.class,  false),
        new ColumnContext("プロトコル",     String.class,  false),
        new ColumnContext("内部ポート", String.class,  false),
        new ColumnContext("内部クライアント",	String.class,  false),
        new ColumnContext("概要", 	String.class,  false),
        new ColumnContext("リース期間", String.class,  false)
    };
	private int number = 0;
	
	/**
	 * 
	 * @param entry
	 */
	public void add(RooterPortMappingEntry entry) {
		Object[] obj = {number, entry.getNewRemoteHost(), entry.getNewExternalPort(),
				entry.getNewProtocol(), entry.getNewInternalPort(), entry.getNewInternalClient(),
				entry.getNewPortMappingDescription(), entry.getNewLeaseDuration() };
		super.addRow(obj);
		number++;
	}
	
	/**
	 * すべてのエントリーを削除
	 */
	public void clear() {
		/*for(int i=0; i < this.getRowCount(); i++) {
			super.removeRow(i);
		}*/
		number = 0;
		this.setRowCount(0);
	}
	
	/**
	 * 指定したエントリーを削除
	 * @param index
	 */
	public void clear(int index) {
		super.removeRow(index);
	}
	
	public boolean isCellEditable(int row, int col) {
		return columnArray[col].isEditable;
	}
	public Class<?> getColumnClass(int modelIndex) {
		return columnArray[modelIndex].columnClass;
	}
	public int getColumnCount() {
		return columnArray.length;
	}
	public String getColumnName(int modelIndex) {
		return columnArray[modelIndex].columnName;
	}
	
	//http://terai.xrea.jpより
	private static class ColumnContext {
        public final String  columnName;
        public final Class   columnClass;
        public final boolean isEditable;
        public ColumnContext(String columnName, Class columnClass, boolean isEditable) {
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.isEditable = isEditable;
        }
    }
}
