package dbitsl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import static graphics.Graphics.panel_button_add;

class Tools extends JPanel{
	private static final long serialVersionUID = 1L;
	static Color color=new Color(0);private JButton clrBtn;
	private int side_w;private int side_h;
	private JComponent draw;
	
	Tools(JComponent draw){this.draw=draw;
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		char first_b='p';
		ImageIcon first_im=image(first_b);
		side_w=first_im.getIconWidth()+panel_button_add;side_h=first_im.getIconHeight()+panel_button_add;
		//
		clrBtn=new JButton();clrBtn.setPreferredSize(new Dimension(side_w,side_h));
		set_bgrColor();
		clrBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JColorChooser colorChooser = new JColorChooser();
				Dialog dialog =JColorChooser.createDialog(
					clrBtn,"Pick a Color",
					true,//modal
					colorChooser,
					new ActionListener(){
						 public void actionPerformed(ActionEvent e){
							 color=colorChooser.getColor();
							 set_bgrColor();
						 }
					},null
				);
				dialog.setVisible(true);
			}
		});
		add(clrBtn);
		//
		add_rBt(first_b).setSelected(true);
	}
	private void set_bgrColor(){
		BufferedImage bi=new BufferedImage(side_w,side_h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(color);
        g.fillRect(0,0,side_w,side_h);
		g.dispose();
		clrBtn.setIcon(new ImageIcon(bi));
	}
	private static ImageIcon image(char c){
		return new ImageIcon("img/dbl/"+c+".png");
	}
	private JRadioButton add_rBt(char c){
		JRadioButton radio=new JRadioButton();
		radio.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				JRadioButton r=(JRadioButton)arg0.getSource();
				ImageIcon im=image(c);
				BufferedImage bi=new BufferedImage(side_w,side_h,BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				g.drawImage(im.getImage(),panel_button_add/2,panel_button_add/2,null);
				if(arg0.getStateChange()==ItemEvent.SELECTED){
					g.setColor(Color.BLUE);
					g.setStroke(new BasicStroke(1));
					g.drawRect(0,0,bi.getWidth()-1,bi.getHeight()-1);
					//
					draw.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(im.getImage(),new Point(),null));
				}
				g.dispose();
				r.setIcon(new ImageIcon(bi));
				
			}
		});
		add(radio);
		return radio;
	};
}
