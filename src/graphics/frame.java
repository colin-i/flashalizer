package graphics;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import workspace.Elements;
import workspace.WorkSpace;
import static workspace.Project.showframe;
import static workspace.Project.placement;
import static workspace.Project.placementcoords;
import static workspace.Project.remove;
import static workspace.Project.spriteshowframe;
import static workspace.Project.spriteplacement;
import static workspace.Project.spriteplacementcoords;
import static workspace.Project.spriteremove;
import static workspace.Project.action;
import static workspace.Project.actionsprite;
import static graphics.character.placeables;
import graphics.character.Character;

public class frame extends JPanel implements TreeSelectionListener{
	private static final long serialVersionUID = 1L;
	frame(){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(200,WorkSpace.project.height));
		setBorder(BorderFactory.createTitledBorder("Frame"));
		sprites=new HashMap<String,frame.sprite_item>();
	}
	static JTree tree;
	void init(){
		DefaultMutableTreeNode top=new DefaultMutableTreeNode();
		noding(top,get_frames(ctag_root,null));
		tree=new JTree(top);
		tree.setRootVisible(false);
		for(int i=0;i<tree.getRowCount();i++) {
			tree.expandRow(i);
		}
		tree.addTreeSelectionListener(this);
		JScrollPane s=new JScrollPane(tree);add(s);
		add(new bar());
	}
	private static class bar extends Panel{
		private static final long serialVersionUID = 1L;
		private static ButtonGroup working_group;
		private JRadioButton addItem(char x,String tip){
			JRadioButton r=new working_radio(x);r.setToolTipText(tip);
			r.addItemListener(new working_type());
			working_group.add(r);add(r);
			return r;
		}
		private bar(){
			setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
			working_group=new ButtonGroup();
			addItem(standard,"Standard").setSelected(true);addItem(remove,"On/Off remove tag");addItem('d',"On/Off depth of character");
		}
		private class working_radio extends JRadioButton{
			private static final long serialVersionUID = 1L;
			private ImageIcon working_type_on;
			private ImageIcon working_type_off;
			private char type;
			private working_radio(char c){
				super(new ImageIcon("img/frame/"+c+".gif"));type=c;
				working_type_off=(ImageIcon)getIcon();
				working_type_on=character.image_border(working_type_off);
			}
		}
		private class working_type implements ItemListener{
			@Override
			public void itemStateChanged(ItemEvent arg0){
				working_radio rad=(working_radio)arg0.getSource();
				if(arg0.getStateChange()==ItemEvent.SELECTED)rad.setIcon(rad.working_type_on);
				else/*if(arg0.getStateChange()==ItemEvent.DESELECTED)*/rad.setIcon(rad.working_type_off);
			}
		}
		private static final char standard='c';private static final char remove='r';
		private static boolean is_sel_standard(){return is_sel(standard);}
		private static boolean is_sel_remove(){return is_sel(remove);}
		private static boolean is_sel(char c){
			for(Enumeration<AbstractButton> buttons=working_group.getElements();buttons.hasMoreElements();) {
				AbstractButton button=buttons.nextElement();
				if(button.isSelected())return((working_radio)button).type==c;
			}
			return false;
		}
	}
	private class ControlTag{
		private String name;
		private Field ref;
		private Field sprite_id;
		private Field action;
		private Field depth;
		private Field x;
		private Field y;
		private ControlTag(String n){
			name=n;
			ref=getRefField(name);
			sprite_id=getSpriteField(name);
			action=getActionField(name);
			depth=getDepthField(name);
			x=getXField(name);
			y=getYField(name);
		}
	}
	private static final int ctag_root=0;private static final int ctag_sprite=1;
	private class ControlTagEx{
		private ControlTag[]tags;
		private ControlTagEx(String n1,String n2){
			List<ControlTag>l=new ArrayList<ControlTag>();
			l.add(ctag_root,new ControlTag(n1));
			l.add(ctag_sprite,new ControlTag(n2));
			tags=l.toArray(new ControlTag[l.size()]);
		}
	}
	class item{
		Character character;int depth;private Object remove;//remove=null or integer
		private DefaultMutableTreeNode parent;
		int x;int y;
		private item(Character c,int object,int x,int y){
			character=c;depth=object;this.x=x;this.y=y;
		}
		@Override
		public String toString(){
			return character.value;
		}
	}
	class sprite_item{
		frame_item[]frames;
		private sprite_item(frame_item[]f){
			frames=f;
		}
	}
	static Map<String,sprite_item>sprites;
	void add_sprite(String name,String preName){
		sprites.put(name,new sprite_item(get_frames(ctag_sprite,preName)));
	}
	class frame_item{
		private item[]elements;
		item[]eshow;
		private String action;
		private frame_item(item[]e,String a){
			elements=e;action=a;
		}
	}
	class frame_entry{
		private String value;frame_item frame;
		private frame_entry(DefaultMutableTreeNode n,frame_item f){
			value="Frame"+n.getChildCount();frame=f;
		}
		@Override
		public String toString(){
			return value;
		}
	}
	private void noding(DefaultMutableTreeNode parent,frame_item[]frames){
		for(frame_item f:frames){
			frame_entry fr=new frame_entry(parent,f);
			DefaultMutableTreeNode frame=new DefaultMutableTreeNode(fr);
			for(item el:f.elements){
				el.parent=parent;
				DefaultMutableTreeNode node=new DefaultMutableTreeNode(el);
				sprite_item s=sprites.get(el.character.value);
				if(s!=null)noding(node,s.frames);
				frame.add(node);
			}
			if(f.action.length()!=0)fr.value+=" +Action";
			parent.add(frame);
		}
	}
	private ControlTagEx[]CTags={
		new ControlTagEx(placement,spriteplacement),
		new ControlTagEx(placementcoords,spriteplacementcoords),
		new ControlTagEx(remove,spriteremove),
		new ControlTagEx(action,actionsprite)
	};
	private ControlTagEx STag=new ControlTagEx(showframe,spriteshowframe);
	private frame_item[]get_frames(int set,String sprite_name){
		List<Object>els=WorkSpace.project.elements;
		List<frame_item>frames=new ArrayList<frame_item>();
		int pos=0;
		for(int i=pos;i<els.size();i++){
			if(checkItem(STag.tags[set],els.get(i),sprite_name)){
				get_items(els,pos,i,set,sprite_name,frames);
				pos=i+1;
			}
		}
		frame_item[]contexts=frames.toArray(new frame_item[frames.size()]);
		//reset the depth of all characters, to be adjusted with the user interface, and used at display items
		List<List<item>>elems=new ArrayList<List<item>>();int max=0;int i=max;
		for(frame_item f:contexts){
			elems.add(new ArrayList<item>());max+=f.elements.length;
		}
		int minV;int selF = 0;int selI = 0;
		while(i<max){
			//get minimum value with selected frame and selected position
			minV=Integer.MAX_VALUE;
			for(int a=0;a<contexts.length;a++){
				item[]eles=contexts[a].elements;
				for(int b=0;b<eles.length;b++){
					if(eles[b].depth<=minV){//<= guarantee to take all the values
						minV=eles[b].depth;
						selF=a;selI=b;
					}
				}
			}
			//
			item[]eles=contexts[selF].elements;
			eles[selI].depth=i;List<item> l=elems.get(selF);l.add(eles[selI]);//set new depth and add to selF frame stack
			//reset the old elements by removing the added element
			item[]eles_new=new item[eles.length-1];
			for(int c=0;c<selI;c++)eles_new[c]=eles[c];
			for(int c=selI+1;c<eles.length;c++)eles_new[c-1]=eles[c];
			contexts[selF].elements=eles_new;
			//
			i++;
		}
		for(int a=0;a<elems.size();a++){
			List<item>lst=elems.get(a);
			contexts[a].elements=lst.toArray(new item[lst.size()]);
		}
		//display elements list
		List<item>last_list=new ArrayList<item>();
		for(int a=0;a<contexts.length;a++){
			List<item>current_list=new ArrayList<item>();//reset current list
			//add previous elements to current list that are not removed in this frame
			for(item it:last_list){
				if(place_remove_dependent(it.remove,a))current_list.add(it);
			}
			frame_item fr=contexts[a];
			item[]eles=fr.elements;
			for(item it:eles){
				int d=it.depth;int b=0;
				for(;b<current_list.size();b++){
					if(d<current_list.get(b).depth)break;
				}
				current_list.add(b,it);//add item or insert item depending on depth 
			}
			fr.eshow=current_list.toArray(new item[current_list.size()]);
			last_list=current_list;
		}
		
		return contexts;
	}
	private boolean place_remove_dependent(Object remove,int i){
		if(remove==null)return true;
		if(i<((int)remove))return true;
		return false;
	}
	private boolean checkItem(ControlTag t,Object e,String sprite_name){
		if(e.getClass().getSimpleName().equals(t.name)){
			if(sprite_name!=null){
				try {
					String x=(String) t.sprite_id.get(e);
					if(sprite_name.equals(x)){return true;}
				} catch (IllegalArgumentException | IllegalAccessException e1) {e1.printStackTrace();}
				return false;
			}
			return true;
		}
		return false;
	}
	private void get_items(List<Object>els,int start,int end,int set,String sprite_name,List<frame_item>frames){
		List<item>items=new ArrayList<item>();List<Integer>removes=new ArrayList<Integer>();String as=""; 
		for(int j=start;j<end;j++){
			Object el=els.get(j);
			for(int k=0;k<CTags.length;k++){
				ControlTag t=CTags[k].tags[set];
				if(checkItem(t,el,sprite_name)){
					String n=CTags[k].tags[ctag_root].name;
					try {
						if(n.equals(action))as+=(String)t.action.get(el);
						else if(n.equals(remove))removes.add((Integer) t.depth.get(el));
						else{
							String name=(String)t.ref.get(el);
							Character c=placeables.get(name);
							if(c!=null){
								int x;int y;
								if(n.equals(placement)){x=0;y=0;}
								else/*(n.equals(placementcoords))*/{
									x=(int) t.x.get(el);y=(int) t.y.get(el);
								}
								items.add(new item(c,(int)t.depth.get(el),x,y));
							}
						}
					}
					catch (IllegalArgumentException | IllegalAccessException e1) {e1.printStackTrace();}
					break;
				}
			}
		}
		for(int d:removes)set_remove(d,frames);//cannot place and remove an item in the same frame
		frames.add(new frame_item(items.toArray(new item[items.size()]),as));
	}
	private void set_remove(int d,List<frame_item>frames){
		for(frame_item f:frames){
			item[]els=f.elements;
			for(item i:els){
				if(i.depth==d){
					i.remove=frames.size()-1;
					return;
				}
			}
		}
	}
	@Override
	public void valueChanged(TreeSelectionEvent arg0){
		Object obj=((DefaultMutableTreeNode)arg0.getPath().getLastPathComponent()).getUserObject();
		if(obj instanceof item){
			if(bar.is_sel_standard()==false){
				item it=(item)obj;
				JDialog dg=new JDialog(SwingUtilities.getWindowAncestor(this),Dialog.ModalityType.DOCUMENT_MODAL);
				dg.setTitle(it.character.value);

				int sel_pos;int orientation;
				if(bar.is_sel_remove()){
					int scope_frames=it.parent.getChildCount();
					sel_pos=scope_frames;
					if(it.remove!=null)sel_pos=(int) it.remove;
					orientation=JSlider.HORIZONTAL;
				}else/*(bar.is_sel_depth())*/{
					sel_pos=it.depth;
					orientation=JSlider.VERTICAL;
				}
				
				JSlider slide=new JSlider(orientation,sel_pos,sel_pos,sel_pos);
				slide.setMajorTickSpacing(1);//This method will also set up a label table
				slide.setPaintTicks(true);//By default, this property is false
				slide.setPaintLabels(true);//By default, this property is false

				JScrollPane s=new JScrollPane(slide);
				dg.add(s);dg.pack();
				dg.setVisible(true);
				return;
			}
		}
		display.component.repaint();
	}
	static Field getField(String elem,Class<? extends Annotation> annotationClass){
		Class<?>[]cls=Elements.class.getDeclaredClasses();
		for(Class<?>c:cls){
			if(c.getSimpleName().equals(elem)){
				Field[]flds=c.getDeclaredFields();
				for(Field fd:flds){
					if(fd.isAnnotationPresent(annotationClass))return fd;
				}
				return null;
			}
		}
		return null;
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RefId{}
	static Field getRefField(String elem){
		return getField(elem,RefId.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface SpriteId{}
	static Field getSpriteField(String elem){
		return getField(elem,SpriteId.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ActionStr{}
	private Field getActionField(String elem){
		return getField(elem,ActionStr.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DepthInt{}
	private Field getDepthField(String elem){
		return getField(elem,DepthInt.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface XInt{}
	private Field getXField(String elem){
		return getField(elem,XInt.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface YInt{}
	private Field getYField(String elem){
		return getField(elem,YInt.class);
	}
}
