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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
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

import workspace.Elements;
import workspace.WorkSpace;
import workspace.Elements.Font;
import workspace.Elements.Text;
import workspace.InputText;
import static actionswf.ActionSwf.HasText;

public class character extends JPanel implements TreeSelectionListener{
	private static final long serialVersionUID = 1L;
	private JTree tree;
	character(){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		Dimension d=new Dimension();d.width=200;
		setPreferredSize(d);
		setBorder(BorderFactory.createTitledBorder("Character"));
		
		create_nodes();
		tree=new JTree(root);
		tree.setCellRenderer(new renderer());
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(this);
		JScrollPane s=new JScrollPane(tree);
		add(s);
		
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
		private Character_pre(type t,Object el) throws NoSuchFieldException, SecurityException{
			type=t;element=el;
			
			name=element.getClass().getDeclaredField(NamedId);
			width=getAField(element.getClass(),WidthInt.class);
			height=getAField(element.getClass(),HeightInt.class);
			for(type p:placeableTags){
				if(p.name.equals(type.name)){isPlaceable=true;break;}
			}
			root.add(new DefaultMutableTreeNode(this));
		}
	}
	class Character extends Character_pre{
		String export_name;
		private Character(type t,Object el,Map<String,String>exp) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
			super(t,el);
			export_name=exp.get((String)name.get(element));
		}
		private Character(type t,Object el) throws NoSuchFieldException, SecurityException{
			super(t,el);
		}
		@Override
		public String toString(){
			try {return (String) name.get(element);}
			catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
			return null;
		}
		
		frame_item[]frames;
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
	private static Field getAField(Class<?>c,Class<? extends Annotation> annotationClass){
		Field[]flds=c.getDeclaredFields();
		for(Field fd:flds){
			if(fd.isAnnotationPresent(annotationClass))return fd;
		}
		return null;
	}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface WidthInt{}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface HeightInt{}
	private type Types[];
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
			root=new DefaultMutableTreeNode();
			
			List<type>t=new ArrayList<type>();
			for(type x:placeableTags)t.add(x);
			t.add(new type(font,'f'));t.add(new type(dbl,'l'));
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
						Character c=new Character(Types[j],e,exports);
						for(type a:placeableTags){
							if(a.name.equals(type)){
								if(Types[j].name.equals(spritedone)){
									Field b=frame.getSpriteField(spritedone);
									c.frames=Graphics.frame.sprite_frames((String)b.get(e));
								}
								break;
							}
						}
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
				((DefaultTreeModel)tree.getModel()).reload();
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
		if(node!=null){//only when adding new item and model.reload add comes null
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
						int pos=0;
						DefaultMutableTreeNode p=(DefaultMutableTreeNode)model.getRoot();
						frame fr=Graphics.frame;
						int new_pos=fr.get_max_depth(p)+1;
						frame_item[]frms=frame.get_frame_items(p);
						List<item>x=new ArrayList<item>();
						for(item it:frms[pos].elements)x.add(it);
						item it=fr.new item(chr,new_pos,0,0);
						x.add(it);
						frms[pos].elements=x.toArray(new item[x.size()]);
						fr.build_eshow(frms);
						DefaultMutableTreeNode n=(DefaultMutableTreeNode)t.getPathForRow(pos).getLastPathComponent();
						DefaultMutableTreeNode nd=new DefaultMutableTreeNode(it);
						if(chr.frames!=null)fr.noding(nd,chr.frames);
						model.insertNodeInto(nd,n,n.getChildCount());
						display.draw();
					}});
				add_one_field(panel,b);
				display.characterData.add(panel);
			}
			
			Object elem=chr.element;
			if(elem instanceof Text){
				panel=new_panel();
				add_one_field(panel,new Label("Text"));
				try{panel.add(new InputTextField_editText((Text)chr.element,TEit.class,HasText));}
				catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {e.printStackTrace();}
				display.characterData.add(panel);
			}
		}
		parent.add(display.characterData);
		display.characterData.revalidate();
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
	private class InputTextField extends InputText implements FocusListener{
		private static final long serialVersionUID = 1L;
		private Field field;private Object element;
		private InputTextField(Field f,Object el) throws IllegalArgumentException, IllegalAccessException{
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
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface TF{}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface TEit{}
	private class InputTextField_editText extends InputTextField{
		private static final long serialVersionUID = 1L;
		private InputTextField_editText(Text t,Class<? extends Annotation>an,int flag) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
			super(getAField(t.structure.getClass(),an),t.structure);
			this.flagField=character.getAField(t.getClass(),TF.class);this.text=t;this.flag=flag;
		}
		private Field flagField;private Object text;private int flag;
		@Override
		public void focusLost(FocusEvent arg0){
			super.focusLost(arg0);
			try{
				int flags=(int)flagField.get(text);
				flags|=flag;
				flagField.set(text,flags);
			}
			catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
		}
	}
}
