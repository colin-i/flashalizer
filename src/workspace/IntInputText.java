package workspace;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.text.ParseException;

class IntInputText extends InputText implements FocusListener{
	private static final long serialVersionUID = 1L;
	IntInputText(int i){
		super(Integer.toString(i));verifier();
	}
	IntInputText(){super();verifier();}
	private void verifier(){
		addFocusListener(this);//InputVerifier forgets to verify on tab or on OK click or maybe more
	}
	@Override
	public void focusLost(FocusEvent x) {
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
	@Override
	public void focusGained(FocusEvent arg0){}
}
