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
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import dbitsl.DBitsL.content;
import util.util.MsEvBRunnable;

import static graphics.Graphics.panel_button_add;

class Tools extends JPanel{
	private static final long serialVersionUID = 1L;
	private Color color=new Color(0);private JButton clrBtn;
	private int side_w;private int side_h;
	private content draw;
	
	Tools(content draw){this.draw=draw;
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
		group=new ButtonGroup();
		MsEvBRunnable run=new MsEvBRunnable(){
			@Override
			public boolean run(MouseEvent e) {
				Point p=origPoint(e);if(p==null)return false;
				draw.img.setRGB(p.x,p.y,color.getRGB());
				return true;
			}
		};
		add_rBt(first_b,"Pencil",run,run).setSelected(true);
		add_rBt('f',"Fill",new MsEvBRunnable(){
			@Override
			public boolean run(MouseEvent e) {
				Point pnt=origPoint(e);if(pnt==null)return false;
				int x=pnt.x;int y=pnt.y;
				BufferedImage img=draw.img;
				int target_color=img.getRGB(x,y);
				int fill_color=color.getRGB();
				if(target_color==fill_color)return false;
				int w=img.getWidth();int h=img.getHeight();
				boolean b[][]=new boolean[w][h];
				List<Point>points=new ArrayList<Point>();
				points.add(new Point(x,y));
				while(0<points.size()){
					int i=points.size()-1;
					Point p=points.get(i);x=p.x;y=p.y;
					points.remove(i);b[y][x]=true;
					int pixColor=img.getRGB(x,y);
					if(pixColor==target_color){
						img.setRGB(x,y,fill_color);
						//east
						if(x+1<w){
							if(b[y][x+1]==false)points.add(new Point(x+1,y));
						}
						//south
						if(y+1<h){
							if(b[y+1][x]==false)points.add(new Point(x,y+1));
						}
						//west
						if(0<x){
							if(b[y][x-1]==false)points.add(new Point(x-1,y));
						}
						//north
						if(0<y){
							if(b[y-1][x]==false)points.add(new Point(x,y-1));
						}
					}
				}
				return true;
			}
		},null);
	}
	private Point origPoint(MouseEvent e){
		BufferedImage img=draw.img;
		int x=e.getX()/DBitsL.zoom_level;
		if(img.getWidth()<=x)return null;
		int y=e.getY()/DBitsL.zoom_level;
		if(img.getHeight()<=y)return null;
		return new Point(x,y);
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
	private JRadioButton add_rBt(char c,String tip,MsEvBRunnable hit,MsEvBRunnable drag){
		radio r=new radio();r.setToolTipText(tip);
		r.image=image(c);
		r.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				set_image((radio)arg0.getSource(),arg0.getStateChange()==ItemEvent.SELECTED);
			}
		});
		set_image(r,false);
		add(r);
		group.add(r);
		r.hit=hit;r.drag=drag;
		return r;
	};
	private void set_image(radio r,boolean sel){
		ImageIcon im=r.image;
		BufferedImage bi=new BufferedImage(side_w,side_h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(im.getImage(),panel_button_add/2,panel_button_add/2,null);
		if(sel){
			g.setColor(Color.BLUE);
			g.setStroke(new BasicStroke(1));
			g.drawRect(0,0,bi.getWidth()-1,bi.getHeight()-1);
			//
			draw.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(im.getImage(),new Point(),null));
		}
		g.dispose();
		r.setIcon(new ImageIcon(bi));
	}
	private static ButtonGroup group;
	private class radio extends JRadioButton{
		private static final long serialVersionUID = 1L;
		private MsEvBRunnable hit;
		private MsEvBRunnable drag;
		private ImageIcon image;
	}
	static boolean hit(MouseEvent e){
		for(Enumeration<AbstractButton> radios=group.getElements();radios.hasMoreElements();) {
			radio r=(radio)radios.nextElement();
			if(r.isSelected()){
				if(r.hit==null)return false;
				return r.hit.run(e);
			}
		}
		return false;
	}
	static boolean drag(MouseEvent e){
		for(Enumeration<AbstractButton> radios=group.getElements();radios.hasMoreElements();) {
			radio r=(radio)radios.nextElement();
			if(r.isSelected()){
				if(r.drag==null)return false;
				r.drag.run(e);
				return true;
			}
		}
		return false;
	}
}
