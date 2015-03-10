package workspace;
import javax.swing.JTextField;
import javax.swing.undo.UndoManager;


class InputText extends JTextField{
	private static final long serialVersionUID = 1L;
	UndoManager umanager;
	InputText(String string) {
		super(string);
		//this.setText(default_text);
		workspace.textPopup.add(this);
	}
	InputText(){workspace.textPopup.add(this);}
}
