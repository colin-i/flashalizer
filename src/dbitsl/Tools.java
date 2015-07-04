package dbitsl;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;

import static graphics.Graphics.panel_button_add;

class Tools extends JPanel{
	private static final long serialVersionUID = 1L;
	static int pencil=(new Color(0)).getRGB();
	Tools(JComponent draw){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		btn b=add_button('p',"Pencil",new colorRun(){
			@Override
			public void run(int sRGB){pencil=sRGB;}
		});
		draw.setCursor(b.cursor);
	}
	private btn add_button(char i,String tip,colorRun r){
		btn b=new btn(i);
		ImageIcon im=btn.im;
		b.setToolTipText(tip);
		b.setPreferredSize(new Dimension(im.getIconWidth()+panel_button_add,im.getIconHeight()+panel_button_add));
		b.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				new colorWin(b,r);
			}
		});
		add(b);
		return b;
	}
	private static class btn extends JButton{
		private static final long serialVersionUID = 1L;
		private static ImageIcon im;
		private Cursor cursor;
		private btn(char i){
			super(im=new ImageIcon("img/dbl/"+i+".png"));
			cursor=Toolkit.getDefaultToolkit().createCustomCursor(im.getImage(),new Point(),null);
		}
	}
	private class colorWin{
		private colorWin(JButton starter,colorRun r){
			JColorChooser colorChooser = new JColorChooser();
			Dialog dialog =JColorChooser.createDialog(
				starter,"Pick a Color",
				true,//modal
				colorChooser,
				new ActionListener(){
					 public void actionPerformed(ActionEvent e){
						 r.run(colorChooser.getColor().getRGB());
					 }
				},null
			);
			dialog.setVisible(true);
		}
	}
	private interface colorRun{
		void run(int sRGB);
	}
}
