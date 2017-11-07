package graphics;

import static workspace.Project.spritedone;
import static workspace.Project.button;
import static workspace.Project.font;
import static workspace.Project.text;
import static workspace.Project.shape;
import static workspace.Project.dbl;
import static workspace.Project.exportsadd;
import static workspace.element.NamedId;
import graphics.frame.frame_item;
import graphics.frame.item;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import dbitsl.DBitsL;
import workspace.Elements;
import workspace.Elements.SpriteNew;
import workspace.WorkSpace;
import workspace.Elements.Button;
import workspace.Elements.Font;
import workspace.Elements.Text;
import workspace.Elements.Shape;
import workspace.Elements.DBL;
import workspace.InputText;
import static graphics.Graphics.panel_button_add;
import static workspace.Elements.default_fonts;
import static actionswf.ActionSwf.FontFlagsBold;
import static actionswf.ActionSwf.FontFlagsItalic;
import util.util.AcListener;
import util.util.FocListener;

public class character extends JPanel implements TreeSelectionListener{
	private static final long serialVersionUID = 1L;
	private JTree tree;
	character(){
		setLayout(new BorderLayout());
		Dimension d=new Dimension();d.width=200;
		setPreferredSize(d);
		setBorder(BorderFactory.createTitledBorder("Character"));
		
		root=new DefaultMutableTreeNode();
		tree=new JTree(root);
		create_nodes();
		tree.setCellRenderer(new renderer());
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(this);
		JScrollPane s=new JScrollPane(tree);
		add(s);
		((DefaultTreeModel)tree.getModel()).reload();
		
		add(new bar(),BorderLayout.SOUTH);
	}
	private class type{
		private String name;
		private char letter;
		private ImageIcon icon;private ImageIcon icon_exp;
		private String screenName;
		private type(String n,char l){
			name=n;letter=l;
			icon=new ImageIcon("img/char/"+letter+".gif");
			icon_exp=image_border(icon);
			if(name.equals(spritedone)){
				icon_a=image_char(icon);//new ImageIcon("img/char/"+letter+"0.gif") invisible sometimes
				icon_exp_a=image_border(icon_a);
			}
		}
		private type(String n,char l,String s){
			this(n,l);screenName=s;
		}
		private ImageIcon icon_a;private ImageIcon icon_exp_a;
		//private void type_a(){icon_a=new ImageIcon("img/char/"+letter+"0.gif");icon_exp_a=image_border(icon_a);}
		private ImageIcon image_border(ImageIcon icon){
			Image image=icon.getImage();
			BufferedImage bi=new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			g.drawImage(image,0,0,null);
			g.setColor(Color.BLUE);
			g.setStroke(new BasicStroke(1));
			g.drawRect(0,0,bi.getWidth()-1,bi.getHeight()-1);
			g.dispose();
			return new ImageIcon(bi);
		}
		private ImageIcon image_char(ImageIcon icon){
			Image image=icon.getImage();
			BufferedImage bi=new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			g.drawImage(image,0,0,null);
			g.setColor(Color.BLUE);
			g.setStroke(new BasicStroke(1));
			g.drawChars(new char[]{'0'}, 0, 1,7,bi.getHeight()-10);//is drawing from bottom
			g.dispose();
			return new ImageIcon(bi);
		}
	}
	private class Character_pre{//using Character and Character_pre for not adding initialize() at every constructor
		type type;
		Object element;
		Field name;Field width;Field height;
		boolean isPlaceable;//Default Value false
		frame_item[]frames;
		protected DefaultMutableTreeNode node=new DefaultMutableTreeNode(this);
		private Character_pre(type t,Object el) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
			type=t;element=el;
			
			name=element.getClass().getDeclaredField(NamedId);
			uniqueName(name,element);
			width=getAField(element.getClass(),WidthInt.class);
			height=getAField(element.getClass(),HeightInt.class);
			for(type p:placeableTags){
				if(p.name.equals(type.name)){
					isPlaceable=true;
					if(type.name.equals(spritedone)){
						if(element instanceof SpriteNew){
							frames=new frame_item[1];frames[0]=Graphics.frame.new frame_item(0);
						}else{
							Field b=frame.getSpriteField(spritedone);
							frames=Graphics.frame.sprite_frames((String)b.get(element));
						}
						frames[0].character=(Character)this;
					}
					break;
				}
			}
			
			DefaultTreeModel model=(DefaultTreeModel)tree.getModel();
			int position=root.getChildCount();//fonts,bits,[position];not sprite,[position];sprite,[position]
			if(isPlaceable==false||frames==null){
				int n=root.getChildCount();
				position=0;
				for(;position<n;position++){
					Character c=(Character)((DefaultMutableTreeNode)root.getChildAt(position)).getUserObject();
					if(c.isPlaceable)break;
				}
				if(isPlaceable==true){
					for(;position<n;position++){
						Character c=(Character)((DefaultMutableTreeNode)root.getChildAt(position)).getUserObject();
						if(c.frames!=null)break;
					}
				}
			}
			model.insertNodeInto(node,root,position);
			//~!@ this is dirty trick when deleting all the nodes and user add one new node but nothing on; the root is unexpanded
			tree.setRootVisible(true);
			tree.expandRow(0);
			tree.setRootVisible(false);
			//~!@
		}
	}
	class Character extends Character_pre{
		String export_name;
		private Character(type t,Object el,Map<String,String>exp) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
			super(t,el);//this(t,el);
			export_name=exp.get((String)name.get(element));
		}
		private Character(type t,Object el) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
			super(t,el);//if(type.name.equals(spritedone))frames[0].character=this;
		}
		void redraw(){((DefaultTreeModel)(tree.getModel())).nodeChanged(node);}
		@Override
		public String toString(){
			try {return (String) name.get(element);}
			catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
			return null;//Java Problem
		}
	}
	static Field getField(String elem,Class<? extends Annotation> annotationClass){
		Class<?>[]cls=Elements.class.getDeclaredClasses();
		for(Class<?>c:cls){
			if(c.getSimpleName().equals(elem)){
				return getAField(c,annotationClass);
			}
		}
		return null;//Java Problem
	}
	static Field getAField(Class<?>c,Class<? extends Annotation> annotationClass){
		Field[]flds=c.getDeclaredFields();
		for(Field fd:flds){
			if(fd.isAnnotationPresent(annotationClass))return fd;
		}
		flds=c.getSuperclass().getDeclaredFields();//for exclude field at elementplus
		for(Field fd:flds){
			if(fd.isAnnotationPresent(annotationClass))return fd;
		}
		return null;
	}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface WidthInt{}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface HeightInt{}
	private type Types[];
	private type[]usedInImportantTags={new type(font,'f'),new type(dbl,'i',"Image")};
	//private type type_sprite=;
	private type placeableTags[]={new type(button,'b'),new type(text,'t'),new type(shape,'s'),new type(spritedone,'m',"Movie")};
	static Character placeableCharacter(String name){
		int n=root.getChildCount();
		for(int i=0;i<n;i++){
			DefaultMutableTreeNode tn=(DefaultMutableTreeNode) root.getChildAt(i);
			Character c=(Character)tn.getUserObject();
			if(c.isPlaceable==true)if(name.equals(c.toString()))return c;
		}
		return null;
	}
	
	static DefaultMutableTreeNode root;
	private void create_nodes(){
		try{
			List<Object>els=WorkSpace.project.elements;
			
			List<type>t=new ArrayList<type>();
			for(type x:usedInImportantTags)t.add(x);
			for(type x:placeableTags)t.add(x);
			Types=t.toArray(new type[t.size()]);
			
			Field fd=frame.getRefField(exportsadd);
			
			Map<String,String>exports=new HashMap<String,String>();
			for(int i=0;i<els.size();i++){
				Object e=els.get(i);
				if(e.getClass().getSimpleName().equals(exportsadd)){
					workspace.Elements.ExportsAdd el=(workspace.Elements.ExportsAdd)e;
					exports.put((String)fd.get(e),el.name);
				}
			}
			
			for(int i=0;i<els.size();i++){
				Object e=els.get(i);
				for(int j=0;j<Types.length;j++){
					String type=Types[j].name;
					if(e.getClass().getSimpleName().equals(type)){
						new Character(Types[j],e,exports);
						break;
					}
				}
			}
		}catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {e1.printStackTrace();}
	}
	private class renderer extends DefaultTreeCellRenderer{
		private static final long serialVersionUID = 1L;
		@Override
		public JComponent getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {
			super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);
			Character v=(Character)((DefaultMutableTreeNode)value).getUserObject();
			if(v != null){//getting null for some data
				for(int i=0;i<Types.length;i++){
					if(Types[i].letter==v.type.letter){
						ImageIcon im;
						if(v.frames!=null&&v.frames[0].action_has_length()){
							if(v.export_name!=null)im=Types[i].icon_exp_a;
							else im=Types[i].icon_a;
						}else{
							if(v.export_name!=null)im=Types[i].icon_exp;
							else im=Types[i].icon;
						}
						setIcon(im);
						//setForeground(new Color(0, 0,255));
						break;
					}
				}
			}
			return this;
		}
	}
	private class bar extends JPanel{
		private static final long serialVersionUID = 1L;
		private class elementAction extends AbstractAction{
			private static final long serialVersionUID = 1L;
			private Class<?>elementClass;private type type;
			private elementAction(Class<?>elCls,type t){
				super(t.screenName==null?t.name:t.screenName);elementClass=elCls;type=t;
			}
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Constructor<?>ctructor=elementClass.getDeclaredConstructors()[1];
				Method[]ms=ctructor.getClass().getDeclaredMethods();
				for(int z=0;z<ms.length;z++){
					if(ms[z].getName().equals(workspace.WorkSpace.project.newInst)){
						Object obj=new Object[]{};
						try{new Character(type,ms[z].invoke(ctructor,obj));}
						catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {e1.printStackTrace();}
						break;
					}
				}
			}
		}
		private void add_button(char img,String tip,ActionListener aclst){
			ImageIcon im=new ImageIcon("img/character/"+img+".gif");
			JButton b=new JButton(im);
			b.setToolTipText(tip);
			b.addActionListener(aclst);
			b.setPreferredSize(new Dimension(im.getIconWidth()+panel_button_add,im.getIconHeight()+panel_button_add));
			add(b);
		}
		private bar(){
			setLayout(new FlowLayout()); 
			
			//Create the pop up menu for add item
			JPopupMenu popup=new JPopupMenu();
			for(type t:Types){
				Class<?>elementClass=null;
				if(t.name.equals(button))elementClass=Button.class;
				else if(t.name.equals(font))elementClass=Font.class;
				else if(t.name.equals(text))elementClass=Text.class;
				else if(t.name.equals(shape))elementClass=Shape.class;
				else if(t.name.equals(dbl))elementClass=DBL.class;
				else/* if(t.name.equals(spritedone))*/elementClass=SpriteNew.class;
				if(elementClass!=null)popup.add(new JMenuItem(new elementAction(elementClass,t)));
			}
			add_button('a',"New Character",new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					popup.show((JComponent) e.getSource(),0,(int)-popup.getPreferredSize().getHeight());
				}
			});
			
			add_button('d',"Delete",new AcListener(display.component,new Runnable(){
				public void run(){
					TreePath pt=tree.getSelectionPath();
					if(pt!=null){
						DefaultTreeModel md=(DefaultTreeModel) tree.getModel();
						DefaultMutableTreeNode node=(DefaultMutableTreeNode)pt.getLastPathComponent();
						md.removeNodeFromParent(node);
						Character chr=(Character)node.getUserObject();
						if(chr.isPlaceable){
							DefaultTreeModel model=(DefaultTreeModel) frame.tree.getModel();
							step(model,(DefaultMutableTreeNode)model.getRoot(),chr);
							Graphics.frame.value_changed();
						}
						//value_changed(); at delete it is fired by tree listener
					}
				}
			}));
			
			add_button('m',"First Frame Action",new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					TreePath pt=tree.getSelectionPath();
					if(pt!=null){
						DefaultMutableTreeNode node=(DefaultMutableTreeNode)pt.getLastPathComponent();
						Character chr=(Character)node.getUserObject();
						if(chr.frames!=null)frame.actionWin(chr.frames[0],e);
					}
				}
			});
		}
		private void step(DefaultTreeModel model,DefaultMutableTreeNode main,Character out){
			for(int i=0;i<model.getChildCount(main);i++){
				DefaultMutableTreeNode child=(DefaultMutableTreeNode)model.getChild(main,i);
				Object obj=child.getUserObject();
				if(obj instanceof item){
					item it=(item)obj;
					if(it.character==out){
						TreeNode frame=child.getParent();
						DefaultMutableTreeNode p=(DefaultMutableTreeNode)frame.getParent();
						frame_item[]frms=graphics.frame.get_frame_items(p);
						int pos=0;for(;pos<p.getChildCount();pos++)if(p.getChildAt(pos)==frame)break;
						Graphics.frame.delete_item(frms,pos,it);
						continue;
					}
				}
				if(!model.isLeaf(child))step(model,child,out);
			}
		} 
	}
	private boolean place_exclude;private JButton place_ruler;
	private void value_nexted(){
		Container ch_data=Graphics.characterData;
		Container disp=ch_data.getParent();
		int component_pos=0;
		for(;component_pos<disp.getComponentCount();component_pos++){
			if(disp.getComponent(component_pos)==ch_data)break;
		}
		disp.remove(ch_data);
		
		Graphics.characterData=new JPanel();
		Graphics.characterData.setLayout(new BoxLayout(Graphics.characterData,BoxLayout.Y_AXIS));
		DefaultMutableTreeNode node=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

		if(node!=null){//is lost of selection, is Place B in A, is A,B; is example bottom
			Character chr=(Character)node.getUserObject();
			JPanel panel;
			
			panel=new_panel();
			add_one_field(panel,new JLabel("Name"));
			try{panel.add(new NameTextField(chr.name,chr.element,node));}
			catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
			add_one_field(panel,new JLabel("Export Name"));
			panel.add(new ExportInputText(chr,node));
			Graphics.characterData.add(panel);
			
			Object elem=chr.element;
			if(chr.isPlaceable==true){
				panel=new_panel();
				if(chr.width!=null){
					add_field(panel,"Width",chr.width,chr);
					add_field(panel,"Height",chr.height,chr);
				}
				Runnable r1=new Runnable(){
				@Override
				public void run(){
					JTree t=frame.tree;
					DefaultTreeModel model;
					model=(DefaultTreeModel)t.getModel();
					frame fr=Graphics.frame;
						
					//frame to add the object
					TreePath pt=fr.selection_frame();
					DefaultMutableTreeNode p=fr.current_top();
					int pos=0;
					if(pt!=null){
						DefaultMutableTreeNode frame_node=(DefaultMutableTreeNode)pt.getLastPathComponent();
						for(;pos<p.getChildCount();pos++)if(p.getChildAt(pos)==frame_node)break;
					}
					DefaultMutableTreeNode f_root=(DefaultMutableTreeNode)model.getRoot();
					frame_item[]frms=frame.get_frame_items(p);
						
					//initial sprite in sprite infinite loop verification
					if(chr.frames!=null){
						if(p!=f_root){
							if(loop_check(chr.frames,frms)){
								Cursor initial_cursor=place_ruler.getCursor();
								place_ruler.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("img/no.png").getImage(),new Point(),null));
								ActionListener taskPerformer = new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										place_ruler.setCursor(initial_cursor);
									}
								};
								new javax.swing.Timer(1000,taskPerformer).start();
								return;
							}
						}
					}
						
					//add the placement
					int new_pos=fr.get_max_depth(p)+1;
					List<item>x=new ArrayList<item>();
					for(item it:frms[pos].elements)x.add(it);
					item it=fr.new item(chr,new_pos,0,0,place_exclude);
					x.add(it);
					frms[pos].elements=x.toArray(new item[x.size()]);
					fr.build_eshow(frms);
						
					//sort rule
					if(chr.frames!=null){
						if(p!=f_root){
							//a,b,c b;a c,b,c b;c b,a c,b;b,c b,a c (of course and b)
							DefaultTreeModel md=(DefaultTreeModel) tree.getModel();
							//this is also the small web format rule, will have "symbol (B) not defined" : example: Sprite A , this is Sprite B placed in Sprite A; sort for the ordered build time: Sprite B,Sprite A
							List<Character>chars_unsorted=new ArrayList<Character>();//get movies from list
							List<DefaultMutableTreeNode>chars_sorted=new ArrayList<DefaultMutableTreeNode>();//keep nodes
							for(int i=0;i<root.getChildCount();i++){
								DefaultMutableTreeNode n=(DefaultMutableTreeNode) root.getChildAt(i);
								Character c=(Character)n.getUserObject();
								if(c.frames!=null){
									chars_unsorted.add(c);
									chars_sorted.add(n);
									md.removeNodeFromParent(n);
								}
							}
							for(int i=0;i<chars_unsorted.size();i++){
								Character unsorted_char=chars_unsorted.get(i);//every unsorted
								int sorted_pos=0;
								for(;sorted_pos<chars_sorted.size();sorted_pos++){//current sorted position
									Character sorted_char=(Character)((DefaultMutableTreeNode)chars_sorted.get(sorted_pos)).getUserObject();
									if(sorted_char==unsorted_char)break;
								}
								for(frame_item f:unsorted_char.frames){
									for(item itm:f.elements){
										if(itm.character.frames!=null){//all inner movies
											int current_pos=sorted_pos+1;//not including the sorted position, forward
											for(;current_pos<chars_sorted.size();current_pos++){
												DefaultMutableTreeNode nod=(DefaultMutableTreeNode)chars_sorted.get(current_pos);
												Character char_s=(Character)nod.getUserObject();
												if(char_s==chr){//is placed wrong
													chars_sorted.remove(current_pos);
													chars_sorted.add(sorted_pos,nod);//in place of the sorted position
													sorted_pos++;//advance the sorted position
													break;
												}
											}
										}
									}
								}
							}
							for(int i=0;i<chars_sorted.size();i++)md.insertNodeInto(chars_sorted.get(i),root,root.getChildCount());
						}
					}
						
					//frame, frame options and display
					step(model,(DefaultMutableTreeNode)model.getRoot(),frms,pos,it);
					fr.value_changed();
				}};
				JButton b=new JButton("Place");
				b.addActionListener(new AcListener(display.component,new Runnable(){
					@Override
					public void run() {
						place_exclude=false;place_ruler=b;
						r1.run();
				}}));
				add_one_field(panel,b);
				JButton b2=new JButton("Place & Exclude");
				b2.addActionListener(new AcListener(display.component,new Runnable(){
					@Override
					public void run() {
						place_exclude=true;place_ruler=b2;
						r1.run();
				}}));
				add_one_field(panel,b2);
				Graphics.characterData.add(panel);
				
				if(elem instanceof Text)new text(chr);
				else if(elem instanceof Button)new button(chr);
				else if(elem instanceof Shape)new shape(chr);
			}else{
				if(elem instanceof DBL){
					DBL dbl=(DBL)elem;
					panel=new_panel();
					panel.add(new JLabel("Path"));
					InputText pth=new InputText(dbl.imagepath);
					pth.addFocusListener(new FocusListener(){
						@Override public void focusGained(FocusEvent arg0) {}
						@Override
						public void focusLost(FocusEvent arg0) {
							dbl.imagepath=pth.getText();
						}});
					panel.add(pth);
					JButton b=new JButton("Editor");
					b.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent arg0) {
							new DBitsL(dbl.imagepath,(Component) arg0.getSource());
						}});
					panel.add(b);
					Graphics.characterData.add(panel);
				}else/* if(element instance of Font)*/{
					Font fnt=(Font)elem;
					panel=new_panel();
					
					JComboBox<String> name=new JComboBox<String>();int indx=-1;
					for(int i=0;i<default_fonts.length;i++){
						name.addItem(default_fonts[i]);
						if(default_fonts[i].equals(fnt.fontname))indx=i;
					}
					name.setSelectedIndex(indx);
					name.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent arg0) {
							fnt.fontname=name.getSelectedItem().toString();
						}});
					panel.add(name);
					
					add_flag_font(panel,fnt,"Bold",FontFlagsBold);
					add_flag_font(panel,fnt,"Italic",FontFlagsItalic);
					
					Graphics.characterData.add(panel);
				}
			}
		}
		
		Graphics.characterData=new JScrollPane(Graphics.characterData);
		disp.add(Graphics.characterData,component_pos);
	}
	private void add_flag_font(JPanel panel,Font f,String nm,int flag){
		JCheckBox chk=new JCheckBox(nm);
		if((f.font_flags&flag)!=0)chk.setSelected(true);
		chk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(chk.isSelected())f.font_flags|=flag;
				else f.font_flags&=~flag;
			}
		});
		panel.add(chk);
	}
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		value_nexted();
	}
	private boolean loop_check(frame_item[]new_frames,frame_item[]parent_frames){
		if(parent_frames==new_frames)return true;
		for(frame_item f:new_frames){
			for(item it:f.elements){
				if(it.character.frames!=null){
					if(loop_check(it.character.frames,parent_frames))return true;
				}
			}
		}
		return false;
	}
	void step(DefaultTreeModel model,DefaultMutableTreeNode main,frame_item[]frames,int pos,Object new_entry){
		frame_item[]frms=frame.get_frame_items(main);
		if(frms==frames){
			DefaultMutableTreeNode new_node=new DefaultMutableTreeNode(new_entry);
			DefaultMutableTreeNode parent=main;
			if(pos!=-1){
				parent=(DefaultMutableTreeNode) main.getChildAt(pos);
				frame_item[]frs=((item)new_entry).character.frames;
				if(frs!=null)Graphics.frame.noding(new_node,frs);
			}
			int p=parent.getChildCount();
			model.insertNodeInto(new_node,parent,p);
			return;
		}
		int cc=model.getChildCount(main);
		for( int i=0; i < cc; i++){
			DefaultMutableTreeNode frame=(DefaultMutableTreeNode)model.getChild(main,i);
			int n=frame.getChildCount();
			for(int j=0;j<n;j++){
				DefaultMutableTreeNode x=(DefaultMutableTreeNode)model.getChild(frame,j);
				if(!model.isLeaf(x))step(model,x,frames,pos,new_entry);
			}
		}
	}
	JPanel new_panel(){
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		return panel;
	}
	private void add_field(JPanel panel,String n,Field f,Character chr){
		add_one_field(panel,new JLabel(n));
		try{panel.add(new IntInputTextField(f,chr.element));}
		catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
	}
	void add_one_field(JPanel panel,JComponent c){
		add_separator(panel);panel.add(c);
	}
	void add_separator(JPanel panel){panel.add(new JSeparator(SwingConstants.VERTICAL));}
	private class NameTextField extends InputText{
		private static final long serialVersionUID = 1L;
		private Field field;private Object element;private DefaultMutableTreeNode node;
		private NameTextField(Field f,Object el,DefaultMutableTreeNode n) throws IllegalArgumentException, IllegalAccessException{
			super(f.get(el));field=f;element=el;node=n;
			addFocusListener(new FocListener(display.component,new Runnable(){
				@Override
				public void run() {
					try {
						field.set(element,getText());
						String s=uniqueName(field,element);
						if(s != null)setText(s);
						DefaultTreeModel model;
						model=(DefaultTreeModel)tree.getModel();
						model.nodeChanged(node);
						model=(DefaultTreeModel)frame.tree.getModel();
						walking(model,model.getRoot());
						Graphics.frame.value_changed();//in case an instance is selected
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {e.printStackTrace();}
				}
			}));
		}
		private void walking(DefaultTreeModel model,Object o){
			int  cc;
			cc = model.getChildCount(o);
			for( int i=0; i < cc; i++) {
				DefaultMutableTreeNode child=(DefaultMutableTreeNode)model.getChild(o,i);
				Object obj=child.getUserObject();
				Object el=null;
				if(obj instanceof item)el=((item)obj).character.element;
				if(el!=null){
					if(el==element){
						model.nodeChanged(child);
						continue;//continue,will be a loop if there is a same sub-child
					}
				}
				if(!model.isLeaf(child))walking(model,child);
			}
		}
	}

	private String uniqueName(Field f,Object e) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
		List<String>a=new ArrayList<String>();
		DefaultTreeModel model;
		model=(DefaultTreeModel)tree.getModel();
		int  cc;
		Object o=model.getRoot();
		cc = model.getChildCount(o);
		for( int i=0; i < cc; i++) {
			DefaultMutableTreeNode child=(DefaultMutableTreeNode)model.getChild(o,i);
			Object obj=child.getUserObject();
			Object el=((Character)obj).element;
			if(el==e)continue;
			Field fd=el.getClass().getDeclaredField(NamedId);
			String s=(String)fd.get(el);
			a.add(s);
		}
		String s=(String) f.get(e);
		String unique=s;int n=2;
		for(int i=0;i<a.size();){
			if(unique.equals(a.get(i))){
				unique=s.concat(String.valueOf(n));n++;
				i=0;continue;
			}
			i++;
		}
		if(!unique.equals(s)){
			f.set(e,unique);
			return unique;
		}
		return null;
	}
	class IntInputTextField extends InputText{
	//used here at width,height&far at frame X,Y
		private static final long serialVersionUID = 1L;
		Field field;Object element;
		private InputText inp;
		IntInputTextField(Field f,Object el) throws IllegalArgumentException, IllegalAccessException{
			super(f.get(el));field=f;element=el;
			inp=this;addFocusListener(new FocListener(display.component,new Runnable(){
				@Override
				public void run() {
					try {
						inp.focus_Lost();
						field.set(element,(Long.decode(getText())).intValue());
					}
					catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
				}
			}));
		}
	}
	Color rgba2color(int color){
		return new Color(color>>>(8+8+8),(color>>>(8+8))&0xff,(color>>>8)&0xff,color&0xff);
	}
	int color2rgba(Color c){
		return (c.getRed()*0x100*0x100*0x100)|(c.getGreen()*0x100*0x100)|(c.getBlue()*0x100)|c.getAlpha();
	}
	private class ExportInputText extends InputText implements FocusListener{
		private static final long serialVersionUID = 1L;
		private Character item;private DefaultMutableTreeNode node;
		private ExportInputText(Character c,DefaultMutableTreeNode n){
			super(c.export_name);
			addFocusListener(this);item=c;node=n;
		}
		@Override public void focusGained(FocusEvent arg0) {}
		@Override public void focusLost(FocusEvent arg0) {
			String text=getText();if(text.equals(""))text=null;
			item.export_name=text;
			((DefaultTreeModel)tree.getModel()).nodeChanged(node);
		}
	}
}
