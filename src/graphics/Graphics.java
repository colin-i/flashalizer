package graphics;

import javax.swing.BoxLayout;
import javax.swing.JComponent;

import workspace.WorkSpace;

public class Graphics extends JComponent{
	private static final long serialVersionUID = 1L;
	public Graphics(){
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		add(new frame());
		add(new display());
		add(new character());
		WorkSpace.container.add(this);
	}
}
