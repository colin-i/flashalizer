package workspace;

import javax.swing.undo.UndoManager;

interface Input {
	UndoManager getUmanager();
	void setUmanager(UndoManager m);
}
