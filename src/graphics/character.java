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
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextArea;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import workspace.Elements;
import workspace.WorkSpace;
import workspace.Elements.Font;
import workspace.Elements.Text;
import workspace.InputText;
import workspace.IntInputText;
import static actionswf.ActionSwf.HasFont;
import static actionswf.ActionSwf.HasTextColor;
import static actionswf.ActionSwf.HasText;
import static actionswf.ActionSwf.Multiline;
import static actionswf.ActionSwf.HasLayout;

public class character extends JPanel implements TreeSelectionListener{
	private static final long serialVersionUID = 1L;
	private JTree tree;
	character(){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
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
		
		add(new bar());
	}
	private class type{
		private String name;
		private char letter;
		private ImageIcon icon;private ImageIcon icon_exp;
		private type(String n,char l){
			name=n;letter=l;
			icon=new ImageIcon("img/char/"+letter+".gif");
			icon_exp=image_border(icon);
		}
	}
	static ImageIcon image_border(ImageIcon icon){
		Image image=icon.getImage();
		BufferedImage bi=new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.BLUE);
		g.drawImage(image,0,0,null);
		g.setStroke(new BasicStroke(1));
		g.drawRect(0,0,bi.getWidth()-1,bi.getHeight()-1);
		g.dispose();
		return new ImageIcon(bi);
	}
	private class Character_pre{//using Character and Character_pre for not adding initialize() at every constructor
		type type;
		Object element;
		Field name;Field width;Field height;
		boolean isPlaceable;//Default Value false
		frame_item[]frames;
		private Character_pre(type t,Object el) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
			type=t;element=el;
			
			name=element.getClass().getDeclaredField(NamedId);
			width=getAField(element.getClass(),WidthInt.class);
			height=getAField(element.getClass(),HeightInt.class);
			for(type p:placeableTags){
				if(p.name.equals(type.name)){
					isPlaceable=true;
					if(type.name.equals(spritedone)){
						Field b=frame.getSpriteField(spritedone);
						frames=Graphics.frame.sprite_frames((String)b.get(element));
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
				if(frames==null){
					for(;position<n;position++){
						Character c=(Character)((DefaultMutableTreeNode)root.getChildAt(position)).getUserObject();
						if(c.frames!=null)break;
					}
				}
			}
			DefaultMutableTreeNode n=new DefaultMutableTreeNode(this);
			model.insertNodeInto(n,root,position);
		}
	}
	class Character extends Character_pre{
		String export_name;
		private Character(type t,Object el,Map<String,String>exp) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
			super(t,el);
			export_name=exp.get((String)name.get(element));
		}
		private Character(type t,Object el) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
			super(t,el);
		}
		@Override
		public String toString(){
			try {return (String) name.get(element);}
			catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
			return null;
		}
	}
	static Field getField(String elem,Class<? extends Annotation> annotationClass){
		Class<?>[]cls=Elements.class.getDeclaredClasses();
		for(Class<?>c:cls){
			if(c.getSimpleName().equals(elem)){
				return getAField(c,annotationClass);
			}
		}
		return null;
	}
	static Field getAField(Class<?>c,Class<? extends Annotation> annotationClass){
		Field[]flds=c.getDeclaredFields();
		for(Field fd:flds){
			if(fd.isAnnotationPresent(annotationClass))return fd;
		}
		return null;
	}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface WidthInt{}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface HeightInt{}
	private type Types[];
	private type[]usedInImportantTags={new type(font,'f'),new type(dbl,'l')};
	private type placeableTags[]={new type(button,'b'),new type(text,'t'),new type(shape,'s')/*,new type(image,'i')*/,new type(spritedone,'m')};
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
		public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {
			super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);
			Character v=(Character)((DefaultMutableTreeNode)value).getUserObject();
			if(v != null){//getting null for some data
				for(int i=0;i<Types.length;i++){
					if(Types[i].letter==v.type.letter){
						if(v.export_name!=null)setIcon(Types[i].icon_exp);
						else setIcon(Types[i].icon);
						break;
					}
				}
			}
			return this;
		}
	}
	private class bar extends Panel{
		private static final long serialVersionUID = 1L;
		private class elementAction extends AbstractAction{
			private static final long serialVersionUID = 1L;
			private Class<?>elementClass;private type type;
			private elementAction(Class<?>elCls,type t){
				super(t.name);elementClass=elCls;type=t;
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
		private bar(){
			setLayout(new BoxLayout(this,BoxLayout.X_AXIS));//without this the height is too big and this will be good when more items will be added 
			JButton b=new JButton(new ImageIcon("img/character.gif"));
			b.setToolTipText("New Character");
			
			//Create the pop up menu.
			JPopupMenu popup=new JPopupMenu();
			for(type t:Types){
				Class<?>elementClass=null;
				if(t.name.equals(font))elementClass=Font.class;
				else if(t.name.equals(text))elementClass=Text.class;
				if(elementClass!=null)popup.add(new JMenuItem(new elementAction(elementClass,t)));
			}
			b.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					popup.show((Component) e.getSource(),0,(int)-popup.getPreferredSize().getHeight());
				}
			});
			
			add(b);
		}
	}
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		Container parent=display.characterData.getParent();
		parent.remove(display.characterData);
		display.characterData=new Panel();
		display.characterData.setLayout(new BoxLayout(display.characterData,BoxLayout.Y_AXIS));
		DefaultMutableTreeNode node=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

		if(node!=null){//is lost of selection, is Place B in A, is A,B; is example bottom
			Character chr=(Character)node.getUserObject();
			Panel panel;
			
			panel=new_panel();
			add_field(panel,"Name",chr.name,chr);
			display.characterData.add(panel);
			
			if(chr.isPlaceable==true){
				panel=new_panel();
				if(chr.width!=null){
					add_field(panel,"Width",chr.width,chr);
					add_field(panel,"Height",chr.height,chr);
				}
				Button b=new Button("Place");
				b.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
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
									Component c=(Component)arg0.getSource();
									Cursor initial_cursor=c.getCursor();
									c.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("img/no.png").getImage(),new Point(),null));
									ActionListener taskPerformer = new ActionListener() {
										public void actionPerformed(ActionEvent evt) {
											c.setCursor(initial_cursor);
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
						item it=fr.new item(chr,new_pos,0,0);
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
						display.draw();
					}});
				add_one_field(panel,b);
				display.characterData.add(panel);
			}
			
			Object elem=chr.element;
			if(elem instanceof Text){
				try{
					Text t=(Text)chr.element;
					
					panel=new_panel();
					add_one_field(panel,new Label("Text"));
					TextArea tx=new TextArea(t.structure.initialtext);
					tx.addFocusListener(new FocusListener(){
						@Override
						public void focusGained(FocusEvent arg0){}
						@Override
						public void focusLost(FocusEvent arg0){
							String value=tx.getText();
							value=value.replace("\r\n","\n");
							t.structure.initialtext=value;
							flags_set(value,t,HasText);
						}
					});
					WorkSpace.textPopup.add(tx);
					panel.add(tx);
					display.characterData.add(panel);
					
					panel=new_panel();
					
					add_one_field(panel,new Label("Font"));
					InputText txt=new InputText(t.structure.font_id);
					txt.addFocusListener(new FocusListener(){
						@Override
						public void focusGained(FocusEvent e){}
						@Override
						public void focusLost(FocusEvent e){
							String value=txt.getText();
							t.structure.font_id=value;
							flags_set(value,t,HasFont);
						}
					});
					panel.add(txt);
					
					add_one_field(panel,new Label("Height"));
					IntInputText fH=new IntInputText(t.structure.font_height);
					fH.addFocusListener(new FocusListener(){
						@Override
						public void focusGained(FocusEvent e){}
						@Override
						public void focusLost(FocusEvent e){
							fH.focus_Lost();
							int value=Integer.parseInt(fH.getText());
							t.structure.font_height=value;
						}
					});
					panel.add(fH);
					
					int color=t.structure.rgba;
					Color c=new Color(color&(0xff00*0x100*0x100),color&(0xff00*0x100),color&0xff00,color==0?0xff:color&0xff);
					Button new_color_b=new Button();
					new_color_b.setBackground(c);
					JColorChooser colorChooser=new JColorChooser();colorChooser.setColor(c);
					Dialog dialog =JColorChooser.createDialog(
						new_color_b,"Pick a Color",
						true,//modal
						colorChooser,
						new ActionListener(){
							 public void actionPerformed(ActionEvent e){
								Color c=colorChooser.getColor();
								new_color_b.setBackground(c);
								t.structure.rgba=(c.getRed()*0x100*0x100*0x100)|(c.getGreen()*0x100*0x100)|(c.getBlue()*0x100)|c.getAlpha();
								flags_set(t.structure.rgba,t,HasTextColor);
							}
						}
						,null);
					new_color_b.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							dialog.setVisible(true);
						}
					});
					panel.add(new_color_b);
					
					JCheckBox chk=new JCheckBox("Multiline");
					if((t.flags&Multiline)!=0)chk.setSelected(true);
					chk.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(chk.isSelected())t.flags|=Multiline;
							else t.flags&=~Multiline;
						}
					});
					panel.add(chk);
					
					add_one_field(panel,new Label("Align"));
					ButtonGroup group = new ButtonGroup();
					JRadioButtonMenuItem radio;
					ActionListener al=new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							int i=0;
							for(Enumeration<AbstractButton> buttons=group.getElements();buttons.hasMoreElements();) {
								AbstractButton button=buttons.nextElement();
								if(button.isSelected()){
									t.structure.layout_align=i;
									t.flags|=HasLayout;
									break;
								}
								i++;
							}
						}
					};
					radio=new JRadioButtonMenuItem("Left");radio.addActionListener(al);group.add(radio);panel.add(radio);
					radio=new JRadioButtonMenuItem("Right");radio.addActionListener(al);group.add(radio);panel.add(radio);
					radio=new JRadioButtonMenuItem("Center");radio.addActionListener(al);group.add(radio);panel.add(radio);
					radio=new JRadioButtonMenuItem("Justify");radio.addActionListener(al);group.add(radio);panel.add(radio);
					
					display.characterData.add(panel);
					}
				catch (IllegalArgumentException | SecurityException e) {e.printStackTrace();}
			}
		}
		
		parent.add(display.characterData);
		display.characterData.revalidate();
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
		int  cc=model.getChildCount(main);
		for( int i=0; i < cc; i++){
			DefaultMutableTreeNode frame=(DefaultMutableTreeNode)model.getChild(main,i);
			int n=frame.getChildCount();
			for(int j=0;j<n;j++){
				DefaultMutableTreeNode x=(DefaultMutableTreeNode)model.getChild(frame,j);
				if(!model.isLeaf(x))step(model,x,frames,pos,new_entry);
			}
		}
	}
	private Panel new_panel(){
		Panel panel=new Panel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		return panel;
	}
	private void add_field(Panel panel,String n,Field f,Character chr){
		add_one_field(panel,new Label(n));
		try{panel.add(new InputTextField(f,chr.element));}
		catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
	}
	private void add_one_field(Panel panel,Component c){
		panel.add(new JSeparator(SwingConstants.VERTICAL));
		panel.add(c);
	}
	class InputTextField extends InputText implements FocusListener{
		private static final long serialVersionUID = 1L;
		Field field;Object element;
		InputTextField(Field f,Object el) throws IllegalArgumentException, IllegalAccessException{
			super(f.get(el));field=f;element=el;addFocusListener(this);
		}
		@Override
		public void focusGained(FocusEvent arg0) {}
		@Override
		public void focusLost(FocusEvent arg0) {
			try {
				if(field.get(element) instanceof Integer){
					super.focus_Lost();
					field.set(element,Integer.parseInt(getText()));
					
					display.draw();
				}else/*String*/{
					field.set(element,getText());
					
					DefaultTreeModel model;
					model=(DefaultTreeModel)tree.getModel();
					walk(model,model.getRoot(),false);
					model=(DefaultTreeModel)frame.tree.getModel();
					walk(model,model.getRoot(),true);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
		}
		private void walk(DefaultTreeModel model,Object o,boolean isItem){
			int  cc;
			cc = model.getChildCount(o);
			for( int i=0; i < cc; i++) {
				DefaultMutableTreeNode child=(DefaultMutableTreeNode)model.getChild(o,i);
				Object obj=child.getUserObject();
				Object el=null;
				if(isItem==true){if(obj instanceof item)el=((item)obj).character.element;}
				else el=((Character)obj).element;
				if(el!=null){
					if(el==element){
						model.nodeChanged(child);
						continue;//continue,will be a loop if there is a same sub-child
					}
				}
				if(!model.isLeaf(child))walk(model,child,isItem);
			}
		} 
	}
	private void flags_set(Object value,Text t,int flag){
		boolean set=true;int flags=t.flags;
		if(value instanceof String)
			{if(((String)value).length()==0)set=false;}
		else/*Integer*/if((int)value==0)set=false;
		if(set==true)flags|=flag;
		else flags&=~flag;
		t.flags=flags;
	}
}
