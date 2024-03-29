package workspace;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class IntInputText extends InputText implements FocusListener{
	private static final long serialVersionUID = 1L;
	public IntInputText(int i){
		super(i);verifier();
	}
	IntInputText(){super(0);verifier();}
	private void verifier(){
		addFocusListener(this);//InputVerifier forgets to verify on tab or on OK click or maybe more
	}
	@Override
	public void focusLost(FocusEvent x) {
		super.focus_Lost();
	}
	@Override
	public void focusGained(FocusEvent arg0){}
}
