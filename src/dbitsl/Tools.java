package dbitsl;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import dbitsl.DBitsL.content;
import util.util.MsEvBRunnable;
import util.util.MsEvVRunnable;
import util.util.MsEvRunnable;
import util.util.AcListener;

import static graphics.Graphics.panel_button_add;

class Tools extends JPanel{
	private static final long serialVersionUID = 1L;
	private Color color;private JButton clrBtn;
	private int side_w;private int side_h;
	private static content draw;private JPanel panel;
	
	Tools(content draw){Tools.draw=draw;panel=this;
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		char first_b='p';
		ImageIcon first_im=image(first_b);
		side_w=first_im.getIconWidth()+panel_button_add;side_h=first_im.getIconHeight()+panel_button_add;
		//
		clrBtn=new JButton();clrBtn.setPreferredSize(new Dimension(side_w,side_h));
		set_bgrColor(new Color(0));
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
							 set_bgrColor(colorChooser.getColor());
						 }
					},null
				);
				dialog.setVisible(true);
			}
		});
		add(clrBtn);
		//eye dropper
		JButton eyeDrp=new JButton(image('d'));eyeDrp.setToolTipText("Screen Eyedropper");eyeDrp.setPreferredSize(new Dimension(side_w,side_h));
		eyeDrp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame frame=new eyedropper(eyeDrp);
				frame.pack();
				frame.setVisible(true);
			}
		});
		add(eyeDrp);
		//
		add(new separator());
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
				boolean b[][]=new boolean[h][w];
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
		selection=add_rBt('g',"Selection",
			new MsEvVRunnable(){@Override public void run(MouseEvent e){selection_begin=origPointTranslation(e);selection_end=null;}},
			new MsEvVRunnable(){@Override public void run(MouseEvent e){selection_end=origPointTranslation(e);}}
		);
		selection.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selection_begin=null;selection_end=null;
			}});
		//
		add(new separator());
		//
		ImageIcon im=image('e');
		BufferedImage img=new BufferedImage(side_w,side_h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();g.drawImage(im.getImage(),panel_button_add/2,panel_button_add/2,null);g.dispose();
		easeB=new JCheckBox(new ImageIcon(img));easeB.setToolTipText("Mouse Coordinates and Gridlines");
		img=new BufferedImage(side_w,side_h,BufferedImage.TYPE_INT_ARGB);
		g = img.createGraphics();
		g.setColor(Color.GREEN);g.fillRect(0,0,side_w,side_h);
		g.drawImage(im.getImage(),panel_button_add/2,panel_button_add/2,null);
		g.dispose();
		easeB.setSelectedIcon(new ImageIcon(img));
		easeB.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				easeGridCreate();
			}
		}));
		add(easeB);
		//
		JButton copy=new JButton(image('c'));copy.setPreferredSize(new Dimension(side_w,side_h));copy.setToolTipText("Copy");
		copy.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Rectangle sel;if((sel=getSelection())==null)return;
				BufferedImage b=draw.img.getSubimage(sel.x,sel.y,sel.width,sel.height);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new clipImage(b),null);
			}
		});
		add(copy);
		JButton paste=new JButton(image('s'));paste.setPreferredSize(new Dimension(side_w,side_h));paste.setToolTipText("Paste");
		paste.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				Transferable transferable=Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
				if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)){
					try {
						BufferedImage in=(BufferedImage)transferable.getTransferData(DataFlavor.imageFlavor);
						BufferedImage current=draw.img;
						selection_begin=origPointInterpretation(DBitsL.getViewPosition());
						int in_x=selection_begin.x;int in_y=selection_begin.y;
						int in_w=in.getWidth();int in_h=in.getHeight();
						int in_r=in_x+in_w;int in_b=in_y+in_h;
						int new_w=Math.max(current.getWidth(),in_r);
						int new_h=Math.max(current.getHeight(),in_b);
						BufferedImage new_img=new BufferedImage(new_w,new_h,BufferedImage.TYPE_INT_ARGB);
						java.awt.Graphics g=new_img.getGraphics();
						g.drawImage(current,0,0,null);
						g.drawImage(in,in_x,in_y,null);
						selection_end=new Point(in_r,in_b);
						draw.img=new_img;
						DBitsL.sizedZoom();
					} catch (UnsupportedFlavorException | IOException e) {e.printStackTrace();}
				}
			}
		}));
		add(paste);
	}
	private class separator extends JComponent{
		private static final long serialVersionUID = 1L;
		private int x=5;private int y=1;
		private separator(){
			setPreferredSize(new Dimension((int)getPreferredSize().getWidth(),x+y+x));
		}
		@Override protected void paintComponent(java.awt.Graphics g){
			java.awt.Graphics2D g2=(Graphics2D) g;
			g2.setColor(new Color(0));
			g2.setStroke(new BasicStroke(1));
			g2.drawLine(0,x,(int) panel.getPreferredSize().getWidth(),x);
		}
	}
	//
	private static JCheckBox easeB;private static JWindow easeCoords;
	static void ease(MouseEvent e){
		if(easeB.isSelected()==false)return;
		Point p=origPoint(e);if(p==null){
			easeOff();
			return;
		}
		easeOff();
		easeOn();
		String t=p.x+","+p.y;
		util.util.popup(t,easeCoords);
	}
	private static void easeOn(){
		Window topLevelWin = SwingUtilities.getWindowAncestor(easeB);
		easeCoords = new JWindow(topLevelWin);
		easeCoords.setVisible(true);
	}
	static void easeOff(){
		if(easeCoords==null)return;
		easeCoords.dispose();easeCoords=null;
	}
	private static BufferedImage easeGrid;
	static void easeGridCreate(){
		if(easeB.isSelected()==false)return;
		int z=DBitsL.zoom_level;
		BufferedImage img=draw.img;
		int wd=img.getWidth();int hg=img.getHeight();
		int w=wd*z;int h=hg*z;
		int[]pixels=new int[h*w*4];
		easeGridX=1;
		for(int col=0;col<wd;col++){
			int c=(col*z+z-1)*4;
			for(int r=0;r<h;r++){
				int v=easeGridPx();int x=r*w*4+c;
				pixels[x]=v;pixels[x+1]=v;//R G
				pixels[x+2]=v;pixels[x+3]=128;//B A
			}
		}
		for(int row=0;row<hg;row++){
			int r=((row+1)*z-1)*w*4;
			for(int c=0;c<w;c++){
				int v=easeGridPx();int x=r+c*4;
				pixels[x]=v;pixels[x+1]=v;pixels[x+2]=v;pixels[x+3]=128;
			}
		}
		easeGrid=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		easeGrid.getRaster().setPixels(0,0,w,h,pixels);
	}
	private static int[]easeGridC={128,128+128/2};private static int easeGridX;
	private static int easeGridPx(){
		return easeGridC[easeGridX^=1];
	}
	static void easeGridDraw(java.awt.Graphics g){
		if(easeB.isSelected()==false)return;
		g.drawImage(easeGrid,0,0,null);
	}
	//
	private static Point origPoint(MouseEvent e){
		BufferedImage img=draw.img;Point p=origPointTranslation(e);
		int x=p.x;
		if(x<0||img.getWidth()<=x)return null;
		int y=p.y;
		if(y<0||img.getHeight()<=y)return null;
		return p;
	}
	private static Point origPointTranslation(MouseEvent e){
		return origPointInterpretation(e.getPoint());
	}
	private static Point origPointInterpretation(Point e){
		return new Point(e.x/DBitsL.zoom_level,e.y/DBitsL.zoom_level);
	}
	private void set_bgrColor(Color c){
		color=c;
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
	private JRadioButton add_rBt(char c,String tip,MsEvRunnable hit,MsEvRunnable drag){
		ImageIcon im=image(c);
		radio r=new radio(radio_image(im,false));
		r.setSelectedIcon(radio_image(im,true));
		r.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange()==ItemEvent.SELECTED)draw.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(im.getImage(),new Point(),null));
			}
		});
		r.setToolTipText(tip);
		group.add(r);
		r.hit=hit;r.drag=drag;
		add(r);
		return r;
	};
	private ImageIcon radio_image(ImageIcon im,boolean sel){
		BufferedImage bi=new BufferedImage(side_w,side_h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(im.getImage(),panel_button_add/2,panel_button_add/2,null);
		if(sel){
			g.setColor(Color.BLUE);
			g.setStroke(new BasicStroke(1));
			g.drawRect(0,0,bi.getWidth()-1,bi.getHeight()-1);
		}
		g.dispose();
		return new ImageIcon(bi);
	}
	private static ButtonGroup group;
	private class radio extends JRadioButton{
		private static final long serialVersionUID = 1L;
		private MsEvRunnable hit;
		private MsEvRunnable drag;
		private radio(ImageIcon i){super(i);}
	}
	static boolean hit(MouseEvent e){
		for(Enumeration<AbstractButton> radios=group.getElements();radios.hasMoreElements();) {
			radio r=(radio)radios.nextElement();
			if(r.isSelected())return bRun(r.hit,e);
		}
		return false;
	}
	static boolean drag(MouseEvent e){
		for(Enumeration<AbstractButton> radios=group.getElements();radios.hasMoreElements();) {
			radio r=(radio)radios.nextElement();
			if(r.isSelected())return bRun(r.drag,e);
		}
		return false;
	}
	private static boolean bRun(MsEvRunnable r,MouseEvent e){
		if(r==null)return false;
		if(r instanceof MsEvBRunnable)return((MsEvBRunnable)r).run(e);
		((MsEvVRunnable)r).run(e);
		return true;
	}
	//
	private class eyedropper extends JFrame{
		private static final long serialVersionUID = 1L;
		private eyedropper(JButton b){
			setTitle("Eyedropper");
			JPanel image_panel = new panel((ImageIcon)b.getIcon());
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			getContentPane().add(new JScrollPane(image_panel));
		}
		private class panel extends JPanel implements MouseListener{
			private static final long serialVersionUID = 1L;
			private Image background_image;
			private panel(ImageIcon im){
				try {
					Dimension screen_size=Toolkit.getDefaultToolkit().getScreenSize();// get the screen dimensions
					Robot robot = new Robot();
					Rectangle rect = new Rectangle(0,0,(int)screen_size.getWidth(),(int)screen_size.getHeight());
					background_image=robot.createScreenCapture(rect);// make the screenshot before showing the frame
					setPreferredSize(screen_size);
					addMouseListener(this);
					setCursor(Toolkit.getDefaultToolkit().createCustomCursor(im.getImage(),new Point(),null));
				} catch (AWTException e) {e.printStackTrace();}
			}
			public void paintComponent(java.awt.Graphics g) {
				g.drawImage(background_image,0,0,null);
			}
			@Override public void mouseClicked(MouseEvent arg0) {}
			@Override public void mouseEntered(MouseEvent arg0) {}
			@Override public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {
				BufferedImage bufer=(BufferedImage)background_image;
				set_bgrColor(new Color(bufer.getRGB(arg0.getX(),arg0.getY())));
				dispose();
			}
			@Override public void mouseReleased(MouseEvent arg0) {}
		}
	}
	// This class is used to hold an image while on the clipboard.
	private class clipImage implements Transferable{
		private Image image;
		private clipImage(Image image){
			this.image = image;
		}
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (!DataFlavor.imageFlavor.equals(flavor)){
				throw new UnsupportedFlavorException(flavor);
			}
			return image;
		}
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}
		@Override
		public boolean isDataFlavorSupported(DataFlavor arg0) {
			return DataFlavor.imageFlavor.equals(arg0);
		}
	}
	//
	private static Point selection_begin;private static Point selection_end;
	private static AbstractButton selection;
	private static Rectangle getSelection(){
		//can convert to good points at the press and drag, or at draw and copy(good when reset image size and then ask for good points) 
		if(selection.isSelected()==true){
			Point b;Point e;
			if((b=goodPoint(selection_begin))!=null&&(e=goodPoint(selection_end))!=null){
				int left=Math.min(b.x,e.x);int top=Math.min(b.y,e.y);
				int right=Math.max(b.x,e.x);int bottom=Math.max(b.y,e.y);
				return new Rectangle(left,top,right-left,bottom-top);
			}
		}
		return null;
	}
	private static Point goodPoint(Point p){
		if(p!=null)return new Point(Math.min(draw.img.getWidth(),Math.max(p.x,0)),Math.min(draw.img.getHeight(),Math.max(p.y,0)));
		return null;
	}
	static void selectionMarkerDraw(java.awt.Graphics g){
		Rectangle sel;if((sel=getSelection())==null)return;
		int z=DBitsL.zoom_level;
		sel.x*=z;sel.y*=z;sel.width*=z;sel.height*=z;
		//creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();
        //white
        g2d.setColor(Color.WHITE);g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(sel.x,sel.y,sel.width,sel.height);
        //black
        g2d.setColor(Color.BLACK);
        Stroke dashed = new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[]{10},0);
        g2d.setStroke(dashed);
        g2d.drawRect(sel.x,sel.y,sel.width,sel.height);
        //gets rid of the copy
        g2d.dispose();
	}
}
