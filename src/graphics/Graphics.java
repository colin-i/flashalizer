package graphics;

import javax.swing.BoxLayout;
import javax.swing.JComponent;

import workspace.WorkSpace;

public class Graphics extends JComponent{
	private static final long serialVersionUID = 1L;
	static frame frame;
	public Graphics(){
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		frame=new frame();
		add(frame);
		add(new display());
		add(new character());//using frame.add_sprite(x)
		frame.init();
		WorkSpace.container.add(this);
	}
}
