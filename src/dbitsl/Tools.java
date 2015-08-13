package dbitsl;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
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
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
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
import javax.swing.JSlider;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dbitsl.DBitsL.content;
import util.util.MsEvBRunnable;
import util.util.MsEvVRunnable;
import util.util.MsMoveListener;
import util.util.MsOutListener;
import util.util.MsEvRunnable;
import util.util.MsMotListener;
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
		int w=first_im.getIconWidth();int h=first_im.getIconHeight();
		side_w=w+panel_button_add;side_h=h+panel_button_add;
		//
		clrBtn=pushBut("Color");
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
		JButton eyeDrp=pushButton('d',"Screen Eyedropper");
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
		sizeButton=pushBut("Size Value");
		sizeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new sizeComponent();
			}
		});
		sizeBut(w,h);
		add(sizeButton);
		//
		composite=checkButton('m',"Color Overwrite");
		add(composite);
		//
		add(new JSeparator());
		//
		easeB=checkButton('e',"Mouse Coordinates and Gridlines");
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
		JButton all=pushButton('a',"Select All");
		all.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run(){
				group.setSelected(selection.getModel(),true);
				selection_begin=new Point();
				BufferedImage im=draw.img;
				selection_end=new Point(im.getWidth(),im.getHeight());
				selection_motion=null;//selection_motion is in conjunction with selImg
			}}));
		add(all);
		JButton copy=pushButton('c',"Copy");
		copy.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				BufferedImage img;if((img=getSelImg())==null)return;
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new clipImage(img),null);
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
						img_on_img(origPointInterpretation(DBitsL.getViewPosition()),
								(BufferedImage)transferable.getTransferData(DataFlavor.imageFlavor),draw.img);
					} catch (UnsupportedFlavorException | IOException e) {e.printStackTrace();}
				}
			}
		}));
		add(paste);
		JButton delete=pushButton('t',"Delete");;
		delete.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				if(getSelImg()==null)return;
				draw.img=baseImg;selectionClear();
			}}));
		add(delete);
		//
		JButton rotateR=pushButton('3',"Rotate Right 90");
		rotateR.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				rotate(90);
			}
		}));
		add(rotateR);
		JButton rotateL=pushButton('4',"Rotate Left 90");
		rotateL.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				rotate(-90);
			}
		}));
		add(rotateL);
		//
		JButton flipV=pushButton('1',"Flip X Center");
		flipV.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				BufferedImage img;if((img=getSelImg())==null)return;
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0,-img.getHeight(null));
				flip(tx);
			}
		}));
		add(flipV);
		JButton flipH=pushButton('2',"Flip Y Center");
		flipH.addActionListener(new AcListener(draw,new Runnable(){
			@Override
			public void run() {
				BufferedImage img;if((img=getSelImg())==null)return;
				AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
				tx.translate(-img.getWidth(null), 0);
				flip(tx);
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
				Graphics2D g=(Graphics2D)draw.img.getGraphics();
				color(g);
				g.fillRect(p.x,p.y,size,size);
				g.dispose();
				return true;
			}
		};
		//
		JRadioButton pencil=add_rBt(first_b,"Pencil",run,run);
		pencil.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){draw.addMouseMotionListener(pencilMove);draw.addMouseListener(pencilOut);}
				else{draw.removeMouseMotionListener(pencilMove);draw.removeMouseListener(pencilOut);}
			}
		});
		pencilMove=new MsMoveListener(draw,new MsEvVRunnable(){@Override public void run(MouseEvent e) {pencilLoc=origPoint(e);}});
		pencilOut=new MsOutListener(draw,new MsEvVRunnable(){@Override public void run(MouseEvent e) {pencilLoc=null;}});
		pencil.setSelected(true);
		//
		add_rBt('f',"Fill",new MsEvBRunnable(){
			@Override
			public boolean run(MouseEvent e) {
				Point pnt=origPoint(e);if(pnt==null)return false;
				int x=pnt.x;int y=pnt.y;
				BufferedImage img=draw.img;
				int target_color=img.getRGB(x,y);
				int fill_color=color.getRGB();
				if(target_color==fill_color)return false;
				//
				int w=img.getWidth();int h=img.getHeight();
				boolean b[][]=new boolean[h][w];
				List<Point>points=new ArrayList<Point>();
				//
				if(composite.isSelected()==false){
					//mix 2 colors with alpha
					Graphics2D g=(Graphics2D)img.getGraphics();
					g.setColor(color);g.fill(new Rectangle(x,y,1,1));
					fill_color=img.getRGB(x,y);
					g.dispose();
					//set back
					img.setRGB(x,y,target_color);
				}
				//
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
				if(selection_motion==null)setSelImg();
				selection_motion=p;
			}},
			new MsEvVRunnable(){@Override public void run(MouseEvent e){
				Point p=origPointTranslation(e);
				if(selection_motion==null){selection_end=p;return;}
				int dif_x=p.x-selection_motion.x;int dif_y=p.y-selection_motion.y;
				Rectangle s=getSelection();int w=draw.img.getWidth();int h=draw.img.getHeight();
				if(s.x+dif_x<0)dif_x=0-s.x;//left motion
				else if(w<s.getMaxX()+dif_x)dif_x=(int)(w-s.getMaxX());//right motion
				if(s.y+dif_y<0)dif_y=0-s.y;//up motion
				else if(h<s.getMaxY()+dif_y)dif_y=(int)(h-s.getMaxY());//down motion
				selection_begin.x+=dif_x;selection_begin.y+=dif_y;
				selMerge();
				selection_end.x+=dif_x;selection_end.y+=dif_y;
				selection_motion=p;
			}}
			,new Runnable(){
			@Override
			public void run() {
				selCursorMaskOut();
			}}
		);
		selection.addActionListener(new AcListener(draw,new Runnable(){
			@Override public void run() {selectionClear();}})
		);
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
		curve.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent arg0){
			forms_begin=null;curveBack.run();}}
		);
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
		draw.add(formsB=new formsSetter(Color.BLUE,new graphPoint(){
			@Override public void setPoint(Point p){forms_begin=p;}
			@Override public Point getPoint(){return forms_begin;}})
		);
		draw.add(formsE=new formsSetter(Color.RED,new graphPoint(){
			@Override public void setPoint(Point p){forms_end=p;}
			@Override public Point getPoint(){return forms_end;}})
		);
		formsSettersOut();
		//
		draw.add(selB=new selSetter(Color.BLUE,new graphPoint(){
			@Override public void setPoint(Point p){selection_begin=p;}
			@Override public Point getPoint(){return selection_begin;}})
		);
		draw.add(selE=new selSetter(Color.RED,new graphPoint(){
			@Override public void setPoint(Point p){selection_end=p;}
			@Override public Point getPoint(){return selection_end;}})
		);
		selSettersOut();
		draw.add(selectionCursor=new selCursor());
		
	}
	//
	private JButton pushButton(char c,String tip){
		JButton b=pushBut(tip);b.setIcon(image(c));return b;
	}
	private JButton pushBut(String tip){
		JButton b=new JButton();b.setPreferredSize(new Dimension(side_w,side_h));b.setToolTipText(tip);return b;
	}
	//
	private static JCheckBox easeB;private static JWindow easeCoords;
	static void ease(MouseEvent e){
		if(easeB.isSelected()==false)return;
		//if not mouse on draw then translate from children to draw
		Point p;
		if(e.getSource()!=draw){
			Point p1=((JComponent)e.getSource()).getLocation();
			p=e.getPoint();
			p.translate(p1.x,p1.y);
		}else p=e.getPoint();
		p=origPointEx(p);
		if(p==null){
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
		return origPointEx(e.getPoint());
	}
	private static Point origPointEx(Point p){
		p=origPointInterpretation(p);
		BufferedImage img=draw.img;
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
		//
		goodSelPoint(selection_begin,selB);goodSelPoint(selection_end,selE);
		//
		int z=DBitsL.zoom_level;
		sel.x*=z;sel.y*=z;sel.width*=z;sel.height*=z;
		marker(g,sel,Color.BLACK,Color.WHITE,10);
        //cursor
        selectionCursor.setBounds(sel);
	}
	private static void marker(java.awt.Graphics g,Rectangle rect,Color a,Color b,int phase){
		//creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();
        //black
        g2d.setColor(a);
        g2d.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[]{phase},0));
        g2d.drawRect(rect.x,rect.y,rect.width,rect.height);
        //white
        g2d.setColor(b);
        g2d.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[]{phase},phase));
        g2d.drawRect(rect.x,rect.y,rect.width,rect.height);
        //gets rid of the copy
        g2d.dispose();
	}
	private BufferedImage baseImg;private BufferedImage selImg;private Point selection_motion;
	private void selMerge(){
		BufferedImage new_img=new BufferedImage(baseImg.getColorModel(),baseImg.copyData(null),baseImg.getColorModel().isAlphaPremultiplied(),null);
		java.awt.Graphics g=new_img.getGraphics();
		g.drawImage(selImg,selection_begin.x,selection_begin.y,null);
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
			};
			addMouseListener(ma);addMouseMotionListener(ma);
			addMouseListener(easeAdapter);addMouseMotionListener(easeAdapter);
		}
		private void trans(MouseEvent e){
			Rectangle sel=getSelection();
			Point p=e.getPoint();p.translate(sel.x*DBitsL.zoom_level,sel.y*DBitsL.zoom_level);
			draw.dispatchEvent(new MouseEvent((Component)e.getSource(),e.getID(),e.getWhen(),e.getModifiers(),p.x,p.y,e.getClickCount(),e.isPopupTrigger()));
		}
	}
	private static selCursor selectionCursor;
	private void selCursorMaskOut(){
		selectionCursor.setBounds(new Rectangle());selSettersOut();
	}
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
		r.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent arg0){formsSettersOut();}});
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
			settersLocation(formsB,forms_begin);
			formsB.run=drag;formsE.run=drag;
		}
		settersLocation(formsE,forms_end);
		return formsDefaultDraw(drag);
	}
	private boolean formsDefaultDraw(formsRunnable drag){return formsDraw(forms_end,drag);};
	private boolean formsDraw(Point p,formsRunnable run){
		BufferedImage new_img=new BufferedImage(baseImg.getColorModel(),baseImg.copyData(null),baseImg.getColorModel().isAlphaPremultiplied(),null);
		Graphics2D g=(Graphics2D)new_img.getGraphics();
		g.setStroke(new BasicStroke(size));color(g);
		run.run(p,g);
		g.dispose();
		draw.img=new_img;
		return true;
	}
	private void color(Graphics2D g){
		if(composite.isSelected())g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		g.setColor(color);
	}
	//
	private JRadioButton curve;private Icon curveIcon;private Icon curveReset;private static Point forms_end;
	private boolean curveDraw(MouseEvent e){
		return formsDraw(origPointTranslation(e),new formsRunnable(){@Override public void run(Point p, Graphics g) {
			((Graphics2D)g).draw(new QuadCurve2D.Double(forms_begin.x,forms_begin.y,p.x,p.y,forms_end.x,forms_end.y));
		}});
	}
	//
	private void flip(AffineTransform tx){
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		selImg=op.filter(selImg,null);
		selMerge();
	}
	//
	private static formsSetter formsB;private static formsSetter formsE;
	private class setter extends JButton{
		private static final long serialVersionUID = 1L;
		graphPoint point;setter thisSetter;
		private setter(Color c,graphPoint p){
			Dimension d=new Dimension();d.width=10;d.height=d.width;
			setBounds(new Rectangle(d));//NO setPreferredSize(d)
			setBackground(c);
			thisSetter=this;
			addMouseListener(easeAdapter);addMouseMotionListener(easeAdapter);
			point=p;
		}
		void set(MouseEvent e){
			Point p=origPointTranslation(e);
			Point pnt=point.getPoint();
			Point t=new Point(pnt.x+p.x,pnt.y+p.y);
			point.setPoint(t);
		}
	}
	private class formsSetter extends setter{
		private static final long serialVersionUID = 1L;
		private formsRunnable run;
		private formsSetter(Color c,graphPoint p){
			super(c,p);
			addMouseMotionListener(new MsMotListener(draw,new MsEvVRunnable(){
				@Override
				public void run(MouseEvent e){
					thisSetter.set(e);
					settersLocation(thisSetter,p.getPoint());
					formsDefaultDraw(run);
				}})
			);
		}
	}
	private void formsSettersOut(){
		formsB.setVisible(false);formsE.setVisible(false);
	}
	private Runnable formsSettersOutRun=new Runnable(){
		@Override public void run(){formsSettersOut();}
	};
	private static void settersLocation(setter thisSetter,Point p){
		thisSetter.setLocation(new Point(p.x*DBitsL.zoom_level,p.y*DBitsL.zoom_level));
	}
	static void formsSettersLocations(){
		if(formsB.isVisible()){
			settersLocation(formsB,forms_begin);settersLocation(formsE,forms_end);
		}
	}
	//
	private interface graphPoint{void setPoint(Point p);Point getPoint();}
	//
	private void img_on_img(Point p,BufferedImage in,BufferedImage current){
		//prepare selection image
		selImg=in;
		selection_begin=p;
		//stuff
		int in_x=selection_begin.x;int in_y=selection_begin.y;
		int in_w=selImg.getWidth();int in_h=selImg.getHeight();
		int in_r=in_x+in_w;int in_b=in_y+in_h;
		int new_w=Math.max(current.getWidth(),in_r);
		int new_h=Math.max(current.getHeight(),in_b);
		selection_end=new Point(in_r,in_b);
		//prepare base image and main image
		BufferedImage b=new BufferedImage(new_w,new_h,BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics g=b.getGraphics();
		g.drawImage(current,0,0,null);//is b=new...,not baseImg=...; at rotation current=baseImg
		g.dispose();
		baseImg=b;
		selMerge();
		//the grid can have another dimension
		DBitsL.sizedZoom();
	}
	private BufferedImage getSelImg(){
		if(selection.isSelected()==false)return null;
		if(selection_begin==null)return null;
		if(selection_end==null)return null;
		if(selection_motion==null){
			setSelImg();
			selection_motion=new Point();//non-null, at rotate it matter to not reset the baseImg
		}
		return selImg;
	}
	private void setSelImg(){
		Rectangle sel=getSelection();
		selImg=draw.img.getSubimage(sel.x,sel.y,sel.width,sel.height);
		BufferedImage im=draw.img;
		baseImg=new BufferedImage(im.getColorModel(),im.copyData(null),im.getColorModel().isAlphaPremultiplied(),null);
		Graphics2D g=(Graphics2D)baseImg.getGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		g.fill(sel);
		g.dispose();
	}
	//
	static MouseAdapter easeAdapter=new MouseAdapter(){
		@Override public void mouseDragged(MouseEvent e){
			ease(e);
		}
		@Override public void mouseMoved(MouseEvent e){
			ease(e);
		}
		@Override public void mouseExited(MouseEvent e){
			easeOff();
		}
	};
	private void rotate(int d){
		BufferedImage img;if((img=getSelImg())==null)return;
		AffineTransform at = new AffineTransform();
        //translate it to the center of the component
        at.translate(img.getHeight()/2,img.getWidth()/2);
        //do the actual rotation
        at.rotate(Math.toRadians(d));
        //translate the object, rotate around the center
        at.translate(-img.getWidth()/2,-img.getHeight()/2);
        //draw the image
        BufferedImage b=new BufferedImage(img.getHeight(),img.getWidth(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d=(Graphics2D)b.getGraphics();
        g2d.drawImage(img,at,null);g2d.dispose();
        //get current center
        Rectangle sel=getSelection();int c_x=sel.x+sel.width/2;int c_y=sel.y+sel.height/2;
        //pass normal x or 0 if x is negative,... 
        img_on_img(new Point(Math.max(c_x-(sel.height/2),0),Math.max(c_y-(sel.width/2),0)),b,baseImg);
	}
	//
	private void selectionClear(){
		selection_begin=null;selection_end=null;selCursorMaskOut();
	}
	//
	private static selSetter selB;private static selSetter selE;
	private class selSetter extends setter{
		private static final long serialVersionUID = 1L;
		private selSetter(Color c,graphPoint p){
			super(c,p);
			addMouseMotionListener(new MsMotListener(draw,new MsEvVRunnable(){
				@Override
				public void run(MouseEvent e){
					thisSetter.set(e);
					selection_motion=null;//in conjunction with selImg
				}})
			);
		}
	}
	private static void goodSelPoint(Point p,selSetter s){
		s.setVisible(true);
		settersLocation(s,goodPoint(p));
	}
	private void selSettersOut(){
		selB.setVisible(false);selE.setVisible(false);
	}
	//
	private static Integer size=1;
	private JButton sizeButton;
	private class sizeComponent extends JSlider implements ChangeListener{
		private static final long serialVersionUID = 1L;
		private static final int sz=8;
		private sizeComponent(){
			super(1,sz,size);
			setMajorTickSpacing(1);//This method will also set up a label table
			setPaintTicks(true);//By default, this property is false
			setPaintLabels(true);//By default, this property is false
			addChangeListener(this);
			//
			win=new JWindow(SwingUtilities.getWindowAncestor(sizeButton));
			JPanel contentPane=(JPanel)win.getContentPane();
			contentPane.add(this);
			Point p=MouseInfo.getPointerInfo().getLocation();
			win.setLocation(p.x,p.y);
			win.pack();
			win.setVisible(true);
		}
		private JWindow win;
		@Override
		public void stateChanged(ChangeEvent arg0) {
			if(!getValueIsAdjusting()){
				size=getValue();
				win.dispose();
			}
		}
	}
	private void sizeBut(int w,int h){
		BufferedImage imge=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr=(Graphics2D) imge.getGraphics();
		Font f=new Font("Courier New",Font.PLAIN,20);
		gr.setFont(f);gr.setColor(Color.BLACK);
		String s="S";
		FontRenderContext frc=gr.getFontRenderContext();
		TextLayout layout = new TextLayout(s,f,frc);
		layout.draw(gr,0,0);
		Rectangle2D bounds = layout.getBounds();
		int nr=(int)((h-bounds.getHeight())/2);
		gr.drawString(s,(int)((w-bounds.getWidth())/2),nr+(int)bounds.getHeight());
		gr.dispose();
		sizeButton.setIcon(new ImageIcon(imge));
	}
	//
	private static Point pencilLoc;
	static void pencilMarkerDraw(java.awt.Graphics g){
		if(pencilLoc==null)return;
		int z=DBitsL.zoom_level;
		marker(g,new Rectangle(pencilLoc.x*z,pencilLoc.y*z,size*z,size*z),new Color(192,128,96),new Color(192/2,128/2,96/2),2);
		pencilLoc=null;
	}
	private MsMoveListener pencilMove;
	private MsOutListener pencilOut;
	//
	private JCheckBox checkButton(char c,String tip){
		ImageIcon im=image(c);
		BufferedImage img=new BufferedImage(side_w,side_h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();g.drawImage(im.getImage(),panel_button_add/2,panel_button_add/2,null);g.dispose();
		JCheckBox b=new JCheckBox(new ImageIcon(img));b.setToolTipText(tip);
		img=new BufferedImage(side_w,side_h,BufferedImage.TYPE_INT_ARGB);
		g = img.createGraphics();
		g.setColor(Color.GREEN);g.fillRect(0,0,side_w,side_h);
		g.drawImage(im.getImage(),panel_button_add/2,panel_button_add/2,null);
		g.dispose();
		b.setSelectedIcon(new ImageIcon(img));
		return b;
	}
	private JCheckBox composite;
}
