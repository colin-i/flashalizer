package graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.QuadCurve2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import workspace.InputText;
import workspace.WorkSpace;
import workspace.Elements.Shape;
import workspace.Elements.Shape.ShapeWithStyle;
import static actionswf.ActionSwf.FillStyleType_none;
import static actionswf.ActionSwf.solid_fill;
import static actionswf.ActionSwf.repeating_bitmap_fill;
import static actionswf.ActionSwf.Non_edge_record;
import static actionswf.ActionSwf.Edge_record;
import static actionswf.ActionSwf.StateMoveTo;
import static actionswf.ActionSwf.StateFillStyle0;
import static actionswf.ActionSwf.StateLineStyle;
import static actionswf.ActionSwf.Straight_edge;
import static actionswf.ActionSwf.Curved_edge;
import static graphics.Graphics.characterData;
import static graphics.Graphics.panel_button_add;
import static util.util.message_popup;
import static workspace.Elements.Shape.ShapeWithStyle.phase_start;
import static workspace.Elements.Shape.ShapeWithStyle.phase_get;
import static workspace.Elements.Shape.ShapeWithStyle.end_of_values;
import graphics.character.Character;
import util.util.PanelEx;
import util.util.ButtonEx;
import util.util.ComponentEx;

class shape {
	private Character chr;
	private ButtonGroup group;
	private int[]records_draws;
	private JButton fill_color;private ShapeInputText image_id;
	private ShapeInputText line_sz;private JButton image_clr;
	shape(Character c){
		chr=c;
		ShapeWithStyle shp=new ShapeWithStyle(((Shape)chr.element).args);
		character cr=Graphics.character;
		
		List<Integer>draws=new ArrayList<Integer>();
		for(int i=0;;){
			if(shp.records[i]==end_of_values)break;
			int edge=shp.records[i];boolean about_style=false;
			int j=-1;//for keeping moveTo flag only at records_draws
			if(edge==Non_edge_record){
				int flags=shp.records[i+1];
				if((flags&StateMoveTo)==0)about_style=true;
				else j=i+1;//for keeping moveTo flag only at records_draws
			}
			phase_start();
			do{
				if(about_style==false){
					int val=shp.records[i];
					if(i==j)val=StateMoveTo;//for keeping moveTo flag only at records_draws
					draws.add(val);
				}
			}while(phase_get(shp.records[i++]));
		}
		records_draws=new int[draws.size()];for(int i=0;i<records_draws.length;i++)records_draws[i]=draws.get(i);
		
		JPanel panel=cr.new_panel();
		JPanel subpanel;
		ActionListener acl=new ActionListener(){
			 public void actionPerformed(ActionEvent e){
				 update_args();
			}
		};
		
		group=new ButtonGroup();
		JRadioButton rd1;JRadioButton rd2;JRadioButton rd3;
		subpanel=cr.new_panel();
		subpanel.setBorder(BorderFactory.createTitledBorder("Fill"));
		rd1=new JRadioButton("None");rd2=new JRadioButton("Solid");rd3=new JRadioButton("Image");group.add(rd1);group.add(rd2);group.add(rd3);rd1.addActionListener(acl);rd2.addActionListener(acl);rd3.addActionListener(acl);
		int color=0xff;
		String img_id="";
		if(shp.fill!=FillStyleType_none){
			boolean is_bitmap=WorkSpace.project.isShapeBitmap(shp.fill);
			if(is_bitmap==false){color=(int)shp.fill_arg;rd2.setSelected(true);}
			else{img_id=(String)shp.fill_arg;rd3.setSelected(true);}
		}else rd1.setSelected(true);
		subpanel.add(rd1);
		subpanel.add(rd2);fill_color=color_chooser(color);subpanel.add(fill_color);
		subpanel.add(rd3);image_id=new ShapeInputText(img_id);subpanel.add(image_id);
		panel.add(subpanel);
		
		subpanel=cr.new_panel();
		subpanel.setBorder(BorderFactory.createTitledBorder("Line"));
		subpanel.add(new JLabel("Size"));line_sz=new ShapeInputText(shp.line_size);subpanel.add(line_sz);
		image_clr=color_chooser(shp.line_color);subpanel.add(image_clr);
		panel.add(subpanel);
		
		characterData.add(panel);
		
		subpanel=cr.new_panel();JButton bt=new JButton("Editor");bt.addActionListener(edit);
		subpanel.add(bt);
		characterData.add(subpanel);
	}
	private class ShapeInputText extends InputText implements FocusListener{
		private static final long serialVersionUID = 1L;
		private boolean isInteger;
		private ShapeInputText(Object in){
			super(in);isInteger=in instanceof Integer;addFocusListener(this);
		}
		@Override public void focusGained(FocusEvent arg0){}
		@Override
		public void focusLost(FocusEvent arg0){
			if(isInteger)super.focus_Lost();
			update_args();
		}
	}
	private JButton color_chooser(int rgba/*,button_Runnable b*/){
		JColorChooser chooser;Color c;JButton bt;
		c=Graphics.character.rgba2color(rgba);
		bt=new JButton();bt.setBackground(c);
		chooser=new JColorChooser();chooser.setColor(c);
		JDialog dialog =JColorChooser.createDialog(
			bt,"Pick a Color",
			true,//modal
			chooser,
			new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					Color c=chooser.getColor();
					bt.setBackground(c);
					update_args();
				}
			}
			,null
		);
		bt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(true);
			}
		});
		return bt;
	}
	private void update_args(){
		int fill;Object fill_arg = null;
		int line_size;int line_color;
		int i=0;
		for(Enumeration<AbstractButton> buttons=group.getElements();buttons.hasMoreElements();) {
			AbstractButton button=buttons.nextElement();
			if(button.isSelected())break;
			i++;
		}
		if(i==0)fill=FillStyleType_none;
		else if(i==1){fill=solid_fill;fill_arg=Graphics.character.color2rgba(fill_color.getBackground());}
		else/**/{fill=repeating_bitmap_fill;fill_arg=image_id.getText();}
		line_size=Long.decode(line_sz.getText()).intValue();
		line_color=Graphics.character.color2rgba(image_clr.getBackground());
		
		((Shape)chr.element).args=new ShapeWithStyle(fill,fill_arg,line_size,line_color,update_records(fill,line_size)).toArray();
	}
	private int[] update_records(int fill,int line_size){
		int flags=fill==FillStyleType_none?0:StateFillStyle0;
		if(line_size!=0)flags|=StateLineStyle;
		if(flags==0)return records_draws;
		int[]ar=new int[2+records_draws.length];
		ar[0]=Non_edge_record;ar[1]=flags;for(int i=0,j=2;i<records_draws.length;i++,j++)ar[j]=records_draws[i];
		return ar;
	}
	private Container container;private JCheckBox delete;private PanelEx coordinates;
	private ActionListener edit=new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JDialog dg=new JDialog(SwingUtilities.getWindowAncestor((JButton)arg0.getSource()),"Shape",JDialog.ModalityType.DOCUMENT_MODAL);
			container=dg.getContentPane();
			container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
			
			content desktop=new content();
			JScrollPane s=new JScrollPane(desktop);
			container.add(s);
			
			coordinates=new PanelEx();coordinates.setLayout(new BoxLayout(coordinates,BoxLayout.X_AXIS));
			container.add(coordinates);
			
			buttons=new JPanel();buttons.setLayout(new FlowLayout());
			add_button(false,'n',new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0){
					desktop.pathReset();desktop.selClear();
					message_popup("Done",buttons);
				}
			},"Interrupt the current path");
			delete=(JCheckBox)add_button(true,'d',new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0){}
			},"Delete points");
			container.add(buttons);
			
			JButton bt=new JButton("OK");
			bt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0){
					int current_x;int current_y;int x=0;int y=0;int x_dif_control=0;int y_dif_control=0;
					List<Integer>records=new ArrayList<Integer>();
					for(int i=0;i<desktop.getComponentCount();i++){
						content.point p=(content.point)desktop.getComponent(i);
						Rectangle r=p.getBounds();
						current_x=r.x+gap;current_y=r.y+gap;
						if(p.isNewPath==true){
							//moveTo
							records.add(Non_edge_record);records.add(StateMoveTo);records.add(current_x);records.add(current_y);
						}else{
							int x_dif=current_x-x;int y_dif=current_y-y;
							if(p.isCurve==false){
								//straightLine
								records.add(Edge_record);records.add(Straight_edge);
								records.add(x_dif);records.add(y_dif);
							}
							else if(p.isControl==true){
								x_dif_control=x_dif;y_dif_control=y_dif;
							}
							else{
								//curve
								records.add(Edge_record);records.add(Curved_edge);
								records.add(x_dif_control);records.add(y_dif_control);
								records.add(x_dif);records.add(y_dif);
							}
						}
						x=current_x;y=current_y;
					}
					records_draws=new int[records.size()];for(int i=0;i<records_draws.length;i++)records_draws[i]=records.get(i);
					dg.dispose();
					update_args();
				}}
			);
			container.add(bt);
			
			dg.pack();
			dg.setVisible(true);
		}
	};
	private JPanel buttons;
	private AbstractButton add_button(boolean isCheckbox,char l,ActionListener acl,String tip){
		AbstractButton b;String plus="";
		if(isCheckbox==false)b=new JButton();
		else{
			b=new JCheckBox();plus="0";
			b.setSelectedIcon(new ImageIcon(getClass().getResource("/img/shape/"+l+"1.gif")));
		}
		ImageIcon im=new ImageIcon(getClass().getResource("/img/shape/"+l+plus+".gif"));
		b.setIcon(im);
		b.setToolTipText(tip);
		b.addActionListener(acl);
		b.setPreferredSize(new Dimension(im.getIconWidth()+panel_button_add,im.getIconHeight()+panel_button_add));
		buttons.add(b);
		return b;
	}
	private static final int gap=5;
	private class content extends ComponentEx implements MouseListener{
		private static final long serialVersionUID = 1L;
		private content(){
			Shape Shp=(Shape)chr.element;
			setPreferredSize(new Dimension(Shp.width,Shp.height));
			
			List<dot>pnts=new ArrayList<dot>();
			int x=0;int y=0;boolean new_path=true;
			int edge = 0;int x1 = 0;int y1 = 0;int x2 = 0;int y2 = 0;int type = 0;
			for(int i=0;;){
				if(records_draws.length==i)break;
				phase_start();int j=i;
				do{
					if(j==i)edge=records_draws[i];
					else if((j+1)==i)type=records_draws[i];
					else if((j+2)==i)x1=records_draws[i];
					else if((j+3)==i)y1=records_draws[i];
					else if((j+4)==i)x2=records_draws[i];
					else if((j+5)==i)y2=records_draws[i];
				}while(phase_get(records_draws[i++]));
				if(edge==Non_edge_record){
					x=x1;y=y1;new_path=true;
				}else{
					int xA=x+x1;int yA=y+y1;
					if(new_path){
						pnts.add(new dot(x,y,true,false,false));
						new_path=false;
					}
					dot variable_dot=new dot(xA,yA,false,false,false);
					pnts.add(variable_dot);
					if(type==Straight_edge){x=xA;y=yA;}
					else{
						variable_dot.isCurve=true;variable_dot.isControl=true;
						int xB=xA+x2;int yB=yA+y2;
						pnts.add(new dot(xB,yB,false,true,false));
						x=xB;y=yB;
					}
				}
			}
			for(int i=0;i<pnts.size();i++){//first lines and curves: draw; now buttons to points
				dot p=pnts.get(i);
				add_point(p);
			}
			
			addMouseListener(this);
		}
		private void add_point(dot p){
			add_point_ex(p,getComponentCount());
		}
		private void add_point_ex(dot p,int n){
			point button=new point(p.isNewPath,p.isCurve,p.isControl);
			int lat=2*gap;
			button.setBounds(p.x-gap,p.y-gap,lat,lat);
			button.setBackground(Color.BLUE);
			add(button,n);
		}
		@Override
		protected void paintComponent(java.awt.Graphics g) {
			Shape Shp=(Shape)chr.element;
			g.setColor(Color.WHITE);g.fillRect(0,0,Shp.width,Shp.height);
			g.setColor(Color.BLACK);
			for(int i=0;i<getComponentCount();i++){
				point p=(point)getComponent(i);
				Rectangle r=p.getBounds();
				boolean a=p.isNewPath==false&&p.isCurve==false;
				boolean b=p.isCurve==true&&p.isControl==false;
				if(a||b){
					Rectangle prev_r=((point)getComponent(i-1)).getBounds();
					int prev_x=prev_r.x+gap;int prev_y=prev_r.y+gap;
					int x=r.x+gap;int y=r.y+gap;
					if(a){
						//line
						g.drawLine(prev_x,prev_y,x,y);
					}
					else{
						//curve
						Rectangle prev_prev_r=((point)getComponent(i-2)).getBounds();
						int prev_prev_x=prev_prev_r.x+gap;int prev_prev_y=prev_prev_r.y+gap;
						((Graphics2D)g).draw(new QuadCurve2D.Double(prev_prev_x,prev_prev_y,prev_x,prev_y,x,y));
					}
				}
			}
		}
		private Point path_start;private boolean path_started;
		private void pathReset(){
			path_start=null;path_started=false;
		}
		private static final int no_point=-1;
		private int newPointPos(Point current_point){
			if(couple[couple_index]!=null){
				int i=pos_get(couple[couple_index])+1;
				for(;i<getComponentCount();i++){
					point p=(point) getComponent(i);
					if(p.isNewPath)break;
				}
				return i;
			}
			if(path_start==null){
				path_start=current_point;
				message_popup("Path start",this);
				return no_point;
			}
			if(path_started==false){
				path_started=true;
				//starting point
				add_point(new dot(path_start.x,path_start.y,true,false,false));
			}
			return getComponentCount();
		}
		@Override
		public void mouseClicked(MouseEvent arg0){}
		@Override
		public void mouseEntered(MouseEvent arg0){}
		@Override
		public void mouseExited(MouseEvent arg0){}
		@Override
		public void mousePressed(MouseEvent arg0){
			Point current_point=arg0.getPoint();
			point[]pts=couple_resolve();
			if(pts==null){
				int pos=newPointPos(current_point);
				if(pos!=no_point)add_point_ex(new dot(current_point.x,current_point.y,false,false,false),pos);
			}else{
				point control=pts[0];point anchor=pts[1];
				if(control!=null)remove(control);
				for(int i=0;i<getComponentCount();i++){
					if(getComponent(i)==anchor){
						add_point_ex(new dot(current_point.x,current_point.y,false,true,true),i);
						anchor.isCurve=true;
						break;
					}
				}
			}
		}
		private void selClear(){
			if(couple[couple_index]!=null){
				couple[couple_index].setBackground(Color.BLUE);
				couple[couple_index]=null;
			}
		}
		@Override
		public void mouseReleased(MouseEvent arg0){}
		private class point extends ButtonEx implements ActionListener{
			private static final long serialVersionUID = 1L;
			private boolean isNewPath;
			private boolean isCurve;
			private boolean isControl;
			private point(boolean a,boolean b,boolean c){
				isNewPath=a;isCurve=b;isControl=c;addActionListener(this);
			}
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(delete.isSelected()==false){
					no_previous_coord();
					set_coord(this);
					if(isControl==false){
						couple_index^=1;
						selClear();
						couple[couple_index]=this;
						message_popup("#"+couple_get(couple_index),this);
						setBackground(Color.RED);
					}
				}else{
					no_previous_coord();
					Container main=getParent();
					int i=pos_get(this);
					if(isControl)control_out(i);//1 control delete
					else{
						point end_verify = null;
						//right
						int right=i+1;int left=i-1;
						if(right<main.getComponentCount()){
							point p=(point)main.getComponent(right);
							if(p.isControl)control_out(right);//2 right control delete
						}
						//left
						if(isCurve)control_out(left--);//3 left control delete
						//path start tests
						if(isNewPath){
							end_verify=(point)main.getComponent(right);//5 b
							end_verify.isNewPath=true;//4 new path delete
						}else if(((point)main.getComponent(left)).isNewPath)end_verify=(point)main.getComponent(left);//5 a
						//remove
						remove_point(this);
						if(end_verify!=null){//5 no points
							int end_pos=pos_get(end_verify)+1;
							if(end_pos<main.getComponentCount()){
								point end_test=(point)main.getComponent(end_pos);
								if(end_test.isNewPath==false)end_verify=null;//6 there is a point at right
							}
							if(end_verify!=null){
								remove_point(end_verify);
								pathReset();
							}
						}
					}
				}
			}
			private void no_previous_coord(){
				coordinates.removeAll();
			}
			private void set_coord(JButton p){
				coordinates.add(new JLabel("X"));
				coordinates.add(new coord(this,true));
				coordinates.add(new JLabel("Y"));
				coordinates.add(new coord(this,false));
			}
			private class coord extends InputText implements FocusListener{
				private static final long serialVersionUID = 1L;
				private point pt;private boolean isX;
				private coord(point p,boolean b){
					super((b?p.getX():p.getY())+gap);pt=p;isX=b;addFocusListener(this);
				}
				@Override
				public void focusGained(FocusEvent arg0) {}
				@Override
				public void focusLost(FocusEvent arg0) {
					super.focus_Lost();
					int val=(Long.decode(getText())).intValue()-gap;
					int x=pt.getX();int y=pt.getY();
					if(isX)x=val;else y=val;
					pt.setLocation(x,y);
				}
			}
		}
		private void remove_point(Component p){
			if(couple[0]==p)couple[0]=null;
			if(couple[1]==p)couple[1]=null;
			remove(p);
		}
		private void control_out(int i){
			((point)getComponent(i+1)).isCurve=false;
			remove(getComponent(i));
		}
		private point[]couple={null,null};//index is more difficult(need to be updated)
		private int couple_index;
		private point[] couple_resolve(){
			int a=couple_get(0);int b=couple_get(1);
			int min=Math.min(a,b);int max=Math.max(a,b);
			if(min!=max){
				point control = null;
				int test=min+1;
				point test_p=(point)getComponent(test);
				if(test_p.isControl==true){test=test+1;control=test_p;}
				if(test==max){
					point max_point=(point)getComponent(max);
					if(max_point.isNewPath==false)return new point[]{control,max_point};
				}
			}
			return null;
		}
		private int couple_get(int i){
			if(couple[i]==null)return 0;//can default to 0 if getting null
			return pos_get(couple[i]);
		}
		private int pos_get(point p){
			int a=0;
			for(;a<getComponentCount();a++)if(getComponent(a)==p)break;
			return a;
		}
		private class dot extends Point{
			private static final long serialVersionUID = 1L;
			private boolean isNewPath;private boolean isCurve;private boolean isControl;
			private dot(int x,int y,boolean a,boolean b,boolean c){
				super(x,y);isNewPath=a;isCurve=b;isControl=c;
			}
		}
	}
}
