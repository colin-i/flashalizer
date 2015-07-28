package dbitsl;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import dbitsl.DBitsL.content;
import util.util.MsEvBRunnable;
import util.util.MsEvVRunnable;
import util.util.MsEvRunnable;
import util.util.AcListener;
import util.util.ItListener;
import util.util.ItRunnable;

import static graphics.Graphics.panel_button_add;

class Tools extends JPanel{
	private static final long serialVersionUID = 1L;
	private Color color;private JButton clrBtn;
	private int side_w;private int side_h;
	private static content draw;private JPanel panel1;private JPanel panel2;
	@Override
	public Component add(Component c){if(panel2==null)panel1.add(c);else panel2.add(c);return c;}
	private void panel(){
		JPanel p;if(panel2==null){panel1=new JPanel();p=panel1;}else{panel2=new JPanel();p=panel2;}
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		add(p,this.getComponentCount());
	}
	Tools(content draw){Tools.draw=draw;
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		panel();
		//
		char first_b='p';
		ImageIcon first_im=image(first_b);
		side_w=first_im.getIconWidth()+panel_button_add;side_h=first_im.getIconHeight()+panel_button_add;
		//
		clrBtn=new JButton();clrBtn.setToolTipText("Color");clrBtn.setPreferredSize(new Dimension(side_w,side_h));
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
		add(new JSeparator());
		//
		JButton copy=pushButton('c',"Copy");
		copy.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Rectangle sel;if((sel=getSelection())==null)return;
				BufferedImage b=draw.img.getSubimage(sel.x,sel.y,sel.width,sel.height);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new clipImage(b),null);
			}
		});
		add(copy);
		JButton paste=pushButton('s',"Paste");
		paste.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				Transferable transferable=Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
				if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)){
					try {
						//this is pasting an item, plus is selecting it
						group.setSelected(selection.getModel(),true);
						//prepare selection image and stuff
						selImg=(BufferedImage)transferable.getTransferData(DataFlavor.imageFlavor);
						BufferedImage current=draw.img;
						selection_begin=origPointInterpretation(DBitsL.getViewPosition());
						int in_x=selection_begin.x;int in_y=selection_begin.y;
						int in_w=selImg.getWidth();int in_h=selImg.getHeight();
						int in_r=in_x+in_w;int in_b=in_y+in_h;
						int new_w=Math.max(current.getWidth(),in_r);
						int new_h=Math.max(current.getHeight(),in_b);
						selection_end=new Point(in_r,in_b);
						//prepare base image and main image
						baseImg=new BufferedImage(new_w,new_h,BufferedImage.TYPE_INT_ARGB);
						java.awt.Graphics g=baseImg.getGraphics();
						g.drawImage(current,0,0,null);
						g.dispose();
						selMerge(selection_begin.x,selection_begin.y);
						//the grid can have another dimension
						DBitsL.sizedZoom();
					} catch (UnsupportedFlavorException | IOException e) {e.printStackTrace();}
				}
			}
		}));
		add(paste);
		//
		JButton flipV=pushButton('1',"Flip X Center");
		flipV.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				Rectangle sel;if((sel=getSelection())==null)return;
				BufferedImage image=draw.img.getSubimage(sel.x,sel.y,sel.width,sel.height);
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0,-image.getHeight(null));
				flip(tx,image,sel);
			}
		}));
		add(flipV);
		JButton flipH=pushButton('2',"Flip Y Center");
		flipH.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				Rectangle sel;if((sel=getSelection())==null)return;
				BufferedImage image=draw.img.getSubimage(sel.x,sel.y,sel.width,sel.height);
				AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
				tx.translate(-image.getWidth(null), 0);
				flip(tx,image,sel);
			}
		}));
		add(flipH);
		//
		panel();
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
		selection=add_rBt_ex('g',"Selection",
			new MsEvVRunnable(){@Override public void run(MouseEvent e){
				Point p=origPointTranslation(e);
				boolean a=selection_end==null;
				if(a==false)a=selectionOutside(p);
				if(a){
					selection_begin=p;selection_end=null;selection_motion=null;
					selCursorMaskOut();
					return;
				}
				if(selection_motion==null){
					Rectangle sel=getSelection();
					selImg=draw.img.getSubimage(sel.x,sel.y,sel.width,sel.height);
					BufferedImage im=draw.img;
					baseImg=new BufferedImage(im.getColorModel(),im.copyData(null),im.getColorModel().isAlphaPremultiplied(),null);
					Graphics2D g=(Graphics2D)baseImg.getGraphics();
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
					g.fill(sel);
					g.dispose();
				}
				selection_motion=p;
			}},
			new MsEvVRunnable(){@Override public void run(MouseEvent e){
				Point p=origPointTranslation(e);
				if(selection_motion==null){selection_end=p;return;}
				double dif_x=p.x-selection_motion.x;double dif_y=p.y-selection_motion.y;
				Rectangle s=getSelection();int w=draw.img.getWidth();int h=draw.img.getHeight();
				if(s.x+dif_x<0)dif_x=0-s.x;//left motion
				else if(w<s.getMaxX()+dif_x)dif_x=w-s.getMaxX();//right motion
				if(s.y+dif_y<0)dif_y=0-s.y;//up motion
				else if(h<s.getMaxY()+dif_y)dif_y=h-s.getMaxY();//down motion
				selMerge(s.x+(int)dif_x,s.y+(int)dif_y);
				selection_begin.x+=dif_x;selection_begin.y+=dif_y;
				selection_end.x+=dif_x;selection_end.y+=dif_y;
				selection_motion=p;
			}}
			,new Runnable(){
			@Override
			public void run() {
				selCursorMaskOut();
			}}
		);
		selection.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selection_begin=null;selection_end=null;
			}});
		formsRunnable lineDrag=new formsRunnable(){
			@Override
			public void run(Point p,java.awt.Graphics g) {
				g.drawLine(forms_begin.x,forms_begin.y,p.x,p.y);
			}
		};
		add_rBt_forms('l',"Line",lineDrag);
		//
		char crv='u';
		Runnable curveBack=new Runnable(){@Override public void run(){
			if(curve.getSelectedIcon()==curveReset)curve.setSelectedIcon(curveIcon);}
		};
		Runnable curveDesel=new Runnable(){@Override public void run(){
			curveBack.run();
			formsSettersOut();
		}};
		curve=add_rBt_forms_ex(crv,"Curve",new MsEvBRunnable(){
			@Override
			public boolean run(MouseEvent e){
				Point p=origPoint(e);
				if(forms_begin==null)return formsDefaultPress(p);
				curve.setSelectedIcon(curveReset);
				return curveDraw(e);
			}
			},new MsEvBRunnable(){
			@Override
			public boolean run(MouseEvent e) {
				if(curve.getSelectedIcon()==curveIcon)return formsDefaultDrag(e,lineDrag);
				return curveDraw(e);
			}},curveDesel
		);
		curveIcon=curve.getSelectedIcon();curveReset=radio_image(imageX(crv+Character.toString('2')),true);
		curve.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent arg0){curveBack.run();}});
		//
		add_rBt_forms('r',"Rectangle",new formsRunnable(){
			@Override
			public void run(Point p,java.awt.Graphics g){
				Rectangle r=computeRect(forms_begin,p);
				g.drawRect(r.x,r.y,r.width,r.height);
		}});
		add_rBt_forms('o',"Circle",new formsRunnable(){
			@Override
			public void run(Point p,java.awt.Graphics g){
				Rectangle r=computeRect(forms_begin,p);
				if(r.width<r.height){//must cut from height
					r.height=r.width;
					if(p.y<forms_begin.y)r.y=forms_begin.y-r.width;
				}else if(r.height<r.width){//must cut from width
					r.width=r.height;
					if(p.x<forms_begin.x)r.x=forms_begin.x-r.height;
				}
				g.drawOval(r.x,r.y,r.width,r.height);
		}});
		add_rBt_forms('v',"Oval",new formsRunnable(){
			@Override
			public void run(Point p,java.awt.Graphics g){
				Rectangle r=computeRect(forms_begin,p);
				g.drawOval(r.x,r.y,r.width,r.height);
		}});
		//
		draw.add(selectionCursor=new selCursor());
		draw.add(formsB=new formsSetter(Color.BLUE));draw.add(formsE=new formsSetter(Color.RED));formsSettersOut();
	}
	//
	private JButton pushButton(char c,String tip){
		JButton b=new JButton(image(c));b.setPreferredSize(new Dimension(side_w,side_h));b.setToolTipText(tip);return b;
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
		util.util.popup(t,easeCoords,10);
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
		return imageX(Character.toString(c));
	}
	private static ImageIcon imageX(String s){
		return new ImageIcon("img/dbl/"+s+".png");
	}
	private radio add_rBt_plus(char c,String tip,MsEvRunnable hit,MsEvRunnable drag,Runnable deselect,boolean bCursor){
		ImageIcon im=image(c);
		Cursor cursor;if(bCursor)cursor=Toolkit.getDefaultToolkit().createCustomCursor(im.getImage(),new Point(),null);
		else cursor=Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		radio r=new radio(radio_image(im,false));
		r.setSelectedIcon(radio_image(im,true));
		r.addItemListener(new ItListener(draw,new ItRunnable(){
			@Override
			public void run(ItemEvent arg0) {
				if(arg0.getStateChange()==ItemEvent.SELECTED)draw.setCursor(cursor);
				else if(deselect!=null)deselect.run();
			}
		}));
		r.setToolTipText(tip);
		group.add(r);
		r.hit=hit;r.drag=drag;
		add(r);
		return r;
	}
	private JRadioButton add_rBt_ex(char c,String tip,MsEvRunnable hit,MsEvRunnable drag,Runnable deselect){
		return add_rBt_plus(c,tip,hit,drag,deselect,true);
	};
	private JRadioButton add_rBt(char c,String tip,MsEvRunnable hit,MsEvRunnable drag){
		return add_rBt_ex(c,tip,hit,drag,null);
	}
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
		private MsEvRunnable hit;private MsEvRunnable drag;
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
				return computeRect(b,e);
			}
		}
		return null;
	}
	private static Rectangle computeRect(Point b,Point e){
		int left=Math.min(b.x,e.x);int top=Math.min(b.y,e.y);
		int right=Math.max(b.x,e.x);int bottom=Math.max(b.y,e.y);
		return new Rectangle(left,top,right-left,bottom-top);
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
        int phase=10;
        //black
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[]{phase},0));
        g2d.drawRect(sel.x,sel.y,sel.width,sel.height);
        //white
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[]{phase},phase));
        g2d.drawRect(sel.x,sel.y,sel.width,sel.height);
        //gets rid of the copy
        g2d.dispose();
        //cursor
        selectionCursor.setBounds(sel);
	}
	private BufferedImage baseImg;private BufferedImage selImg;private Point selection_motion;
	private void selMerge(int x,int y){
		BufferedImage new_img=new BufferedImage(baseImg.getColorModel(),baseImg.copyData(null),baseImg.getColorModel().isAlphaPremultiplied(),null);
		java.awt.Graphics g=new_img.getGraphics();
		g.drawImage(selImg,x,y,null);
		g.dispose();
		draw.img=new_img;
	}
	private boolean selectionOutside(Point p){
		Rectangle s=getSelection();
		int x=p.x;
		if(x<s.x)return true;
		if(s.getMaxX()<=x)return true;
		int y=p.y;
		if(y<s.y)return true;
		if(s.getMaxY()<=y)return true;
		return false;
	}
	//
	private class selCursor extends JComponent{
		private static final long serialVersionUID = 1L;
		private selCursor(){
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
			MouseAdapter ma=new MouseAdapter(){
				@Override public void mousePressed(MouseEvent e){trans(e);}
				@Override public void mouseDragged(MouseEvent e){trans(e);}
				@Override public void mouseMoved(MouseEvent e){trans(e);}
			};
			addMouseListener(ma);addMouseMotionListener(ma);
		}
		private void trans(MouseEvent e){
			Rectangle sel=getSelection();
			Point p=e.getPoint();p.translate(sel.x*DBitsL.zoom_level,sel.y*DBitsL.zoom_level);
			draw.dispatchEvent(new MouseEvent((Component)e.getSource(),e.getID(),e.getWhen(),e.getModifiers(),p.x,p.y,e.getClickCount(),e.isPopupTrigger()));
		}
	}
	private static selCursor selectionCursor;
	private void selCursorMaskOut(){selectionCursor.setBounds(new Rectangle());}
	//
	private static Point forms_begin;
	private interface formsRunnable{
		void run(Point p,java.awt.Graphics g);
	}
	private void add_rBt_forms(char c,String tip,formsRunnable drag){
		add_rBt_forms_ex(c,tip,new MsEvBRunnable(){
			@Override
			public boolean run(MouseEvent e) {
				return formsDefaultPress(origPoint(e));
			}
		},new MsEvBRunnable(){
			@Override
			public boolean run(MouseEvent e){
				return formsDefaultDrag(e,drag);
			}
		},formsSettersOutRun);
	}
	private JRadioButton add_rBt_forms_ex(char c,String tip,MsEvBRunnable press,MsEvBRunnable drag,Runnable desel){
		radio r=add_rBt_plus(c,tip,press,drag,desel,false);
		r.addActionListener(formsReset);
		return r;
	}
	private boolean formsDefaultPress(Point p){
		forms_begin=p;if(forms_begin==null)return false;
		BufferedImage im=draw.img;
		baseImg=new BufferedImage(im.getColorModel(),im.copyData(null),im.getColorModel().isAlphaPremultiplied(),null);
		formsSettersOut();
		return false;
	}
	private boolean formsDefaultDrag(MouseEvent e,formsRunnable drag){
		if(forms_begin==null)return false;
		forms_end=origPointTranslation(e);
		if(formsB.isVisible()==false){
			formsB.setVisible(true);formsE.setVisible(true);
			formsSettersLocation(formsB,forms_begin);
		}
		formsSettersLocation(formsE,forms_end);
		return formsDraw(forms_end,drag);
	}
	private boolean formsDraw(Point p,formsRunnable run){
		BufferedImage new_img=new BufferedImage(baseImg.getColorModel(),baseImg.copyData(null),baseImg.getColorModel().isAlphaPremultiplied(),null);
		java.awt.Graphics2D g=(Graphics2D)new_img.getGraphics();
		g.setStroke(new BasicStroke(1));g.setColor(color);
		run.run(p,g);
		g.dispose();
		draw.img=new_img;
		return true;
	}
	//
	private JRadioButton curve;private Icon curveIcon;private Icon curveReset;private static Point forms_end;
	private boolean curveDraw(MouseEvent e){
		return formsDraw(origPointTranslation(e),new formsRunnable(){@Override public void run(Point p, Graphics g) {
			((Graphics2D)g).draw(new QuadCurve2D.Double(forms_begin.x,forms_begin.y,p.x,p.y,forms_end.x,forms_end.y));
		}});
	}
	//
	private void flip(AffineTransform tx,BufferedImage image,Rectangle sel){
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		
		Graphics2D g2=(Graphics2D)draw.img.getGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		g2.drawImage(image,sel.x,sel.y,sel.width,sel.height,null);
		g2.dispose();
	}
	//
	private static formsSetter formsB;private static formsSetter formsE;
	private class formsSetter extends JButton{
		private static final long serialVersionUID = 1L;
		private formsSetter(Color c){
			Dimension d=new Dimension();d.width=10;d.height=d.width;
			setBounds(new Rectangle(d));//NO setPreferredSize(d)
			setBackground(c);
		}
	}
	private void formsSettersOut(){
		formsB.setVisible(false);formsE.setVisible(false);
	}
	private Runnable formsSettersOutRun=new Runnable(){
		@Override public void run(){formsSettersOut();}
	};
	private ActionListener formsReset=new ActionListener(){
		@Override public void actionPerformed(ActionEvent e) {forms_begin=null;formsSettersOut();}
	};
	private static void formsSettersLocation(formsSetter s,Point p){
		s.setLocation(new Point(p.x*DBitsL.zoom_level,p.y*DBitsL.zoom_level));
	}
	static void formsSettersLocations(){
		if(formsB.isVisible()){
			formsSettersLocation(formsB,forms_begin);formsSettersLocation(formsE,forms_end);
		}
	}
}
