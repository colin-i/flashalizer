package workspace;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.undo.UndoManager;


public class InputText extends JTextField implements Input{
	private static final long serialVersionUID = 1L;
	private UndoManager umanager;
	public InputText(String string) {
		setS(string);
	}
	private void setS(String string){
		setText(string);
		WorkSpace.textPopup.add(this);
	}
	private void setI(int string){
		setS(Integer.toString(string));
	}
	public void focus_Lost(){
		String txt=getText();
		try{
			Long.decode(txt);//.intValue();
		}
		catch(NumberFormatException e){
			e.printStackTrace();
			try{
				setText(NumberFormat.getNumberInstance().parse(txt).toString());
			}catch(ParseException e1){
				setText("0");
				e1.printStackTrace();
			}
		}
	}
	protected InputText(Object obj){
		if(obj instanceof String)setS((String)obj);
		else setI((int)obj);
	}
	InputText(int i){
		setI(i);
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
