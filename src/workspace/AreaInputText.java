package workspace;

import javax.swing.JTextArea;
import javax.swing.undo.UndoManager;

public class AreaInputText extends JTextArea implements Input{
	private static final long serialVersionUID = 1L;
	private UndoManager umanager;
	public AreaInputText(String s){
		super(s);WorkSpace.textPopup.add(this);
	}
	@Override
	public UndoManager getUmanager() {
		return umanager;
	}
	@Override
	public void setUmanager(UndoManager m) {
		umanager=m;
	}
}
