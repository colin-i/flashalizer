package workspace;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;



public class PopUp extends JPopupMenu
	{
		private final static long serialVersionUID = 0;

		private Clipboard clipboard;

		private JMenuItem jmenuItem_undo;
		private JMenuItem jmenuItem_cut;
		private JMenuItem jmenuItem_copy;
		private JMenuItem jmenuItem_paste;
		private JMenuItem jmenuItem_delete;
		private JMenuItem jmenuItem_selectAll;

		private InputText jtextComponent;

		PopUp()
		{
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			jmenuItem_undo = new JMenuItem("undo");
			jmenuItem_undo.setEnabled(false);
			jmenuItem_undo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					jtextComponent.umanager.undo();
				}
			});

			add(jmenuItem_undo);

			add(new JSeparator());

			jmenuItem_cut = new JMenuItem("cut");
			jmenuItem_cut.setEnabled(false);
			jmenuItem_cut.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					jtextComponent.cut();
				}
			});

			add(jmenuItem_cut);

			jmenuItem_copy = new JMenuItem("copy");
			jmenuItem_copy.setEnabled(false);
			jmenuItem_copy.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					jtextComponent.copy();
				}
			});

			add(jmenuItem_copy);

			jmenuItem_paste = new JMenuItem("paste");
			jmenuItem_paste.setEnabled(false);
			jmenuItem_paste.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					jtextComponent.paste();
				}
			});

			add(jmenuItem_paste);

			jmenuItem_delete = new JMenuItem("delete");
			jmenuItem_delete.setEnabled(false);
			jmenuItem_delete.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					jtextComponent.replaceSelection("");
				}
			});

			add(jmenuItem_delete);

			add(new JSeparator());

			jmenuItem_selectAll = new JMenuItem("select all");
			jmenuItem_selectAll.setEnabled(false);
			jmenuItem_selectAll.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					jtextComponent.selectAll();
				}
			});

			add(jmenuItem_selectAll);
		}

		void add(InputText jtextComponent)
		{
			jtextComponent.addMouseListener(new MouseAdapter()
			{
				public void mouseReleased(MouseEvent event)
				{
					if (event.getButton() == 3)
					{
						processClick(event);
					}
				}
			});

			jtextComponent.umanager=new UndoManager();
			jtextComponent.getDocument().addUndoableEditListener(new UndoableEditListener()
			{
				public void undoableEditHappened(UndoableEditEvent event)
				{
					jtextComponent.umanager.addEdit(event.getEdit());
				}
			});
		}

		private void processClick(MouseEvent event)
		{
			jtextComponent = (InputText)event.getSource();

			boolean enableUndo = jtextComponent.umanager.canUndo();
			boolean enableCut = false;
			boolean enableCopy = false;
			boolean enablePaste = false;
			boolean enableDelete = false;
			boolean enableSelectAll = false;

			String text = jtextComponent.getText();
			if (text.length() > 0)
			{
				String selectedText = jtextComponent.getSelectedText();
				enableSelectAll = true;
				if (selectedText != null)
				{
					enableCut = true;
					enableCopy = true;
					enableDelete = true;
				}
			}

			try {
				if (clipboard.getData(DataFlavor.stringFlavor) != null)
				{
					enablePaste = true;
				}
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}

			jmenuItem_undo.setEnabled(enableUndo);
			jmenuItem_cut.setEnabled(enableCut);
			jmenuItem_copy.setEnabled(enableCopy);
			jmenuItem_paste.setEnabled(enablePaste);
			jmenuItem_delete.setEnabled(enableDelete);
			jmenuItem_selectAll.setEnabled(enableSelectAll);

			show(jtextComponent,event.getX(),event.getY());
		}
	}

