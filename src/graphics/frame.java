package graphics;

import java.awt.Button;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

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
import graphics.character.Character;

public class frame extends JPanel implements TreeSelectionListener{
	private static final long serialVersionUID = 1L;
	frame(){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		Dimension d=new Dimension();d.width=200;
		setPreferredSize(d);
		setBorder(BorderFactory.createTitledBorder("Frame"));
	}
	static JTree tree;
	static frame_item[]frames;
	void init(){
		DefaultMutableTreeNode top=new DefaultMutableTreeNode();
		frames=get_frames(ctag_root,null);
		noding(top,frames);
		tree=new JTree(top);
		tree.setRootVisible(false);
		tree.addMouseListener(ml);
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
			addItem(standard,"Standard").setSelected(true);addItem(remove,"On/Off remove tag");addItem(depth,"On/Off depth of character");
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
		private static final char standard='c';private static final char remove='r';private static final char depth='d';
		//private static boolean is_sel_standard(){return is_sel(standard);}
		private static boolean is_sel_remove(){return is_sel(remove);}
		private static boolean is_sel_depth(){return is_sel(depth);}
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
		Character character;int depth;Integer remove;//remove=null or integer
		int x;int y;
		item(Character c,int object,int x,int y){
			character=c;depth=object;this.x=x;this.y=y;
		}
		@Override
		public String toString(){
			return character.toString();
		}
	}
	class frame_item{
		item[]elements;
		item[]eshow;
		String action;
		private frame_item(item[]e,String a){
			elements=e;action=a;
		}
	}
	class frame_entry{
		private String value;
		private frame_entry(DefaultMutableTreeNode n){
			value="Frame"+n.getChildCount();
		}
		@Override
		public String toString(){
			return value;
		}
	}
	void noding(DefaultMutableTreeNode parent,frame_item[]frames){
		for(frame_item f:frames){
			frame_entry fr=new frame_entry(parent);
			DefaultMutableTreeNode frame=new DefaultMutableTreeNode(fr);
			for(item el:f.elements){
				DefaultMutableTreeNode node=new DefaultMutableTreeNode(el);
				frame_item[]s=el.character.frames;
				if(s!=null)noding(node,s);
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
	frame_item[]sprite_frames(String n){
		return get_frames(ctag_sprite,n);
	}
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
		//sort by depth and reset the depth of all characters, to be adjusted with the user interface, and used at display items
		depths_set_sort(contexts);
		//display elements list
		build_eshow(contexts);
		
		return contexts;
	}
	private void depths_set_sort(frame_item[]contexts){
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
	}
	void build_eshow(frame_item[]contexts){
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
	}
	private boolean place_remove_dependent(Integer remove,int i){
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
							Character c=character.placeableCharacter(name);
							if(c!=null){
								int x;int y;
								if(n.equals(placement)){x=0;y=0;}
								else/*(n.equals(placement coordinates))*/{
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
		for(int d:removes)set_remove(d,frames);								 //cannot place and remove an item
		frames.add(new frame_item(items.toArray(new item[items.size()]),as));//in the same frame
	}
	private void set_remove(int d,List<frame_item>frames){
		for(frame_item f:frames){
			item[]els=f.elements;
			for(item i:els){
				if(i.depth==d){
					i.remove=frames.size();//frames size is the current frame index
					return;
				}
			}
		}
	}
	static frame_item[]get_frame_items(DefaultMutableTreeNode parent){
		if(parent.getParent()==null)return frames;
		else{
			item sprite=(item)parent.getUserObject();
			return sprite.character.frames;
		}
	}
	int get_max_depth(DefaultMutableTreeNode parent){
		frame_item[]fr=get_frame_items(parent);
		int max_pos=-1;//example: -1 + one element is 0, same as depth 0
		for(frame_item f:fr)max_pos+=f.elements.length;
		return max_pos;
	}
	private MouseListener ml = new MouseAdapter() {
		private int max_pos;private int sel_pos;
		private DefaultMutableTreeNode parent;
		@Override
		public void mousePressed(MouseEvent e){
			int selRow = tree.getRowForLocation(e.getX(), e.getY());
			TreePath path=tree.getPathForLocation(e.getX(), e.getY());
			if(selRow == -1)return;
			DefaultMutableTreeNode sel_node=(DefaultMutableTreeNode)path.getLastPathComponent();
			Object obj=sel_node.getUserObject();
			if(obj instanceof item){
				if(bar.is_sel_remove()||bar.is_sel_depth()){
					item it=(item)obj;
					JDialog dg=new JDialog(SwingUtilities.getWindowAncestor(tree),Dialog.ModalityType.DOCUMENT_MODAL);
					dg.setTitle(it.character.toString());
	
					int orientation;int min_pos=0;
					TreePath frame_path=path.getParentPath();
					TreeNode frame=(TreeNode)frame_path.getLastPathComponent();
					TreePath parent_path=frame_path.getParentPath();
					parent=(DefaultMutableTreeNode)parent_path.getLastPathComponent();
					if(bar.is_sel_remove()){
						int n=parent.getChildCount();
						for(int i=0;i<n;i++){
							if(frame==parent.getChildAt(i)){
								min_pos=i+1;
								break;
							}
						}
						max_pos=parent.getChildCount();
						sel_pos=max_pos;
						if(it.remove!=null)sel_pos=it.remove;
						orientation=JSlider.HORIZONTAL;
					}else/*(bar.is_sel_depth())*/{
						max_pos=get_max_depth(parent);
						sel_pos=it.depth;
						orientation=JSlider.VERTICAL;
					}
					
					JSlider slide=new JSlider(orientation,min_pos,max_pos,sel_pos);
					slide.setMajorTickSpacing(1);//This method will also set up a label table
					slide.setPaintTicks(true);//By default, this property is false
					slide.setPaintLabels(true);//By default, this property is false
	
					JScrollPane s=new JScrollPane(slide);
					
					Container ctnr=dg.getContentPane();
					ctnr.setLayout(new BoxLayout(ctnr,BoxLayout.Y_AXIS));
					
					ctnr.add(s);
					
					Button btn=new Button("OK");
					btn.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							int val=slide.getValue();
							frame_item[]frms=get_frame_items(parent);
							if(bar.is_sel_remove()){
								Integer v=val;
								if(val==max_pos)v=null;
								it.remove=v;
							}else/*(bar.is_sel_depth())*/{
								for(frame_item f:frms){
									for(item i:f.elements){
										int d=i.depth;
										if(sel_pos<d&&d<=val)i.depth--;
										else if(val<=d&&d<sel_pos)i.depth++;
									}
								}
								it.depth=val;
								depths_set_sort(frms);
								DefaultTreeModel model=(DefaultTreeModel)tree.getModel();
								walk(model,(DefaultMutableTreeNode)model.getRoot(),it,node_pos_new(frms,val));
							}
							build_eshow(frms);
							display.draw();//used at bar.is_sel_depth()
							dg.dispose();
						}
					});
					ctnr.add(btn);
					
					dg.pack();
					dg.setVisible(true);
					return;
				}
			}
		}
	};
	private int node_pos_new(frame_item[]frms,int depth){
		for(frame_item f:frms){
			int j=0;
			for(item i:f.elements){
				if(i.depth==depth)return j;
				j++;
			}
		}
		return 0;
	}
	private boolean walk(DefaultTreeModel model,DefaultMutableTreeNode boss,item it,int pos_new){
		int  cc=model.getChildCount(boss);
		for( int i=0; i < cc; i++){
			DefaultMutableTreeNode child=(DefaultMutableTreeNode)model.getChild(boss,i);
			Object obj=child.getUserObject();
			if(obj==it){
				model.removeNodeFromParent(child);
				model.insertNodeInto(child,boss,pos_new);
				return false;//return,will be a loop if there is a same sub-child,at these frames the item is only once here at depths
			}
			if(!model.isLeaf(child))if(walk(model,child,it,pos_new)==false)return true;
		}
		return true;
	} 
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		display.draw();
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RefId{}
	static Field getRefField(String elem){
		return character.getField(elem,RefId.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface SpriteId{}
	static Field getSpriteField(String elem){
		return character.getField(elem,SpriteId.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ActionStr{}
	private Field getActionField(String elem){
		return character.getField(elem,ActionStr.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DepthInt{}
	private Field getDepthField(String elem){
		return character.getField(elem,DepthInt.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface XInt{}
	private Field getXField(String elem){
		return character.getField(elem,XInt.class);
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface YInt{}
	private Field getYField(String elem){
		return character.getField(elem,YInt.class);
	}
}
