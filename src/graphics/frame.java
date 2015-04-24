package graphics;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import static graphics.Graphics.panel_button_add;
import static graphics.character.getAField;

public class frame extends JPanel implements TreeSelectionListener{
	private static final long serialVersionUID = 1L;
	frame(){
		setLayout(new BorderLayout());
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
		tree.addTreeSelectionListener(this);
		JScrollPane s=new JScrollPane(tree);add(s);
		
		JPanel pan=new bar();
		add(pan,BorderLayout.SOUTH);
	}
	private class bar extends JPanel{
		private static final long serialVersionUID = 1L;
		private void add_button(char img,String tip,ActionListener aclst){
			ImageIcon im=new ImageIcon("img/frame/"+img+".gif");
			JButton b=new JButton(im);
			b.setToolTipText(tip);
			b.addActionListener(aclst);
			b.setPreferredSize(new Dimension(im.getIconWidth()+panel_button_add,im.getIconHeight()+panel_button_add));
			add(b);
		}
		private bar(){
			setLayout(new FlowLayout());
			add_button('f',"Add Frame",new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode top=current_top();
					frame_item[]frms=get_frame_items(top);int total=frms.length;
					List<frame_item>lst=new ArrayList<frame_item>();
					for(int i=0;i<total;i++)lst.add(frms[i]);
					frame_item f_i=new frame_item(total);
					lst.add(f_i);
					frms=lst.toArray(new frame_item[lst.size()]);
					
					set_frame_items(top,frms);//required at next step(and not only)
					
					DefaultTreeModel md=(DefaultTreeModel) tree.getModel();
					Graphics.character.step(md,(DefaultMutableTreeNode) md.getRoot(),frms,-1,f_i);
					
					build_eshow(frms);
					display.draw();
					value_changed();
				}
			});
			add_button('d',"Delete",new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					TreePath pt=tree.getSelectionPath();
					if(pt!=null){
						DefaultMutableTreeNode nd=(DefaultMutableTreeNode)pt.getLastPathComponent();
						Object nd_obj=nd.getUserObject();
						DefaultMutableTreeNode top=current_top();
						frame_item[]frms=get_frame_items(top);
						if(nd_obj instanceof item){
							for(int i=0;i<frms.length;i++){
								frame_item f=frms[i];
								for(item it:f.elements){
									if(it==nd_obj){
										delete_item(frms,i,it);
										display.draw();
										value_changed();
										break;
									}
								}
							}
						}else/*if(nd_obj instance of frame_item)*/{
							if(1<top.getChildCount()){//do not delete last frame
								DefaultTreeModel md=(DefaultTreeModel) tree.getModel();
								int pos=0;for(;pos<top.getChildCount();pos++)if(top.getChildAt(pos)==nd)break;
								frame_item out_frame=frms[pos];
								List<frame_item>frs=new ArrayList<frame_item>();
								for(frame_item f:frms)if(out_frame!=f)frs.add(f);
								frms=frs.toArray(new frame_item[frs.size()]);
								
								Integer total=frms.length;
								for(int i=0;i<total;i++){
									frame_item f=frms[i];
									if(i>=pos)f.setPos(i);
									//verify for lost of removeTag if placed in last frame that is deleted
									for(item it:f.elements){
										if(it.remove==total){
											it.remove=null;
										}
									}
								}
								
								set_frame_items(top,frms);
								
								walk(md,(DefaultMutableTreeNode)md.getRoot(),out_frame,-1);
								depths_set_sort(frms);
								build_eshow(frms);
								display.draw();
							}
						}
					}
				}
			});
			add_button('e',"Expand All",new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					for(int i=0;i<tree.getRowCount();i++){
						tree.expandRow(i);
					}
				}
			});
			add_button('c',"Collapse All",new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					for(int i=0;i<tree.getRowCount();i++){
						tree.collapseRow(i);
					}
				}
			});
		}
	}
	void delete_item(frame_item[]frms,int row,item it){
		frame_item f=frms[row];
		List<item>its=new ArrayList<item>();
		for(item itm:f.elements)if(itm!=it)its.add(itm);
		frms[row].elements=its.toArray(new item[its.size()]);
		
		DefaultTreeModel md=(DefaultTreeModel) tree.getModel();
		walk(md,(DefaultMutableTreeNode)md.getRoot(),it,-1);
		depths_set_sort(frms);
		build_eshow(frms);
	}
	TreePath selection_frame(){
		TreePath pt=tree.getSelectionPath();
		if(pt!=null){
			for(;;){
				DefaultMutableTreeNode nd=(DefaultMutableTreeNode)pt.getLastPathComponent();
				Object nd_obj=nd.getUserObject();
				if(nd_obj instanceof frame_item)break;
				pt=pt.getParentPath();
			}
		}
		return pt;
	}
	DefaultMutableTreeNode current_top(){
		TreePath sel=selection_frame();
		if(sel==null)return (DefaultMutableTreeNode) ((DefaultTreeModel)tree.getModel()).getRoot();
		return (DefaultMutableTreeNode)sel.getParentPath().getLastPathComponent();
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
		@X int x;@Y int y;
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
		private int pos;
		private frame_item(item[]e,String a,int p){
			elements=e;action=a;pos=p;
		}
		frame_item(int p){
			elements=new item[0];eshow=new item[0];action="";pos=p;
		}
		private void setPos(int p){pos=p;}
		@Override
		public String toString(){
			String value="Frame"+pos;
			if(action.length()!=0)value+=" +Action";
			return value;
		}
	}
	void noding(DefaultMutableTreeNode parent,frame_item[]frames){
		for(frame_item f:frames){
			DefaultMutableTreeNode frame_node=new DefaultMutableTreeNode(f);
			for(item el:f.elements){
				DefaultMutableTreeNode node=new DefaultMutableTreeNode(el);
				frame_item[]s=el.character.frames;
				if(s!=null)noding(node,s);
				frame_node.add(node);
			}
			parent.add(frame_node);
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
		for(int d:removes)set_remove(d,frames);										//cannot place and remove an item at the same frame
		int pos=frames.size();														//
		frames.add(new frame_item(items.toArray(new item[items.size()]),as,pos));	//
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
	private void set_frame_items(DefaultMutableTreeNode parent,frame_item[]frms){
		if(parent.getParent()==null)frames=frms;
		else{
			item sprite=(item)parent.getUserObject();
			sprite.character.frames=frms;
		}
	}
	int get_max_depth(DefaultMutableTreeNode parent){
		frame_item[]fr=get_frame_items(parent);
		int max_pos=-1;//example: -1 + one element is 0, same as depth 0
		for(frame_item f:fr)max_pos+=f.elements.length;
		return max_pos;
	}
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
	private boolean walk(DefaultTreeModel model,DefaultMutableTreeNode boss,Object tree_entry,int pos_new){
		int  cc=model.getChildCount(boss);
		for( int i=0; i < cc; i++){
			DefaultMutableTreeNode child=(DefaultMutableTreeNode)model.getChild(boss,i);
			Object obj=child.getUserObject();
			if(obj==tree_entry){
				model.removeNodeFromParent(child);
				if(pos_new!=-1)model.insertNodeInto(child,boss,pos_new);
				return false;//return,will be a loop if there is a same sub-child,at these frames the item is only once here at depths
			}
			if(!model.isLeaf(child))if(walk(model,child,tree_entry,pos_new)==false)return true;
		}
		return true;
	}
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		value_changed();
	}
	void value_changed(){
		Container disp=Graphics.frameData.getParent();
		int component_pos=0;
		for(;component_pos<disp.getComponentCount();component_pos++){
			if(disp.getComponent(component_pos)==Graphics.frameData)break;
		}
		disp.remove(Graphics.frameData);
		Graphics.frameData=new Panel();
		Graphics.frameData.setLayout(new BoxLayout(Graphics.frameData,BoxLayout.Y_AXIS));
		
		DefaultMutableTreeNode sel_node=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if(sel_node!=null){//there is lost of selection at depth when removing and inserting||||||and place new object at character
			Object obj=sel_node.getUserObject();
			if(obj instanceof item){
				item it=(item)obj;
				TreePath frame_path=tree.getSelectionPath().getParentPath();
				TreeNode frame=(TreeNode)frame_path.getLastPathComponent();
				TreePath parent_path=frame_path.getParentPath();
				DefaultMutableTreeNode parent=(DefaultMutableTreeNode)parent_path.getLastPathComponent();
				int min_pos = 0;int max_pos;int sel_pos;
				
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
				slider rem=new slider(min_pos,max_pos,sel_pos,parent);
				rem.run=new slider_run(){
					public void Run(slider source){
						Integer v=source.value;
						if(source.value==source.max_pos)v=null;
						it.remove=v;
					}
				};
				
				max_pos=get_max_depth(parent);
				sel_pos=it.depth;
				slider dpt=new slider(0,max_pos,sel_pos,parent);
				dpt.run=new slider_run(){
					public void Run(slider source){
						for(frame_item f:source.frames){
							for(item i:f.elements){
								int d=i.depth;
								if(source.sel_pos<d&&d<=source.value)i.depth--;
								else if(source.value<=d&&d<source.sel_pos)i.depth++;
							}
						}
						it.depth=source.value;
						depths_set_sort(source.frames);
						DefaultTreeModel model=(DefaultTreeModel)tree.getModel();
						walk(model,(DefaultMutableTreeNode)model.getRoot(),it,node_pos_new(source.frames,source.value));
					}
				};
				
				JScrollPane remv=new JScrollPane(rem);
				remv.setBorder(BorderFactory.createTitledBorder("RemoveTag"));
				Graphics.frameData.add(remv);
				JScrollPane dpth=new JScrollPane(dpt);
				dpth.setBorder(BorderFactory.createTitledBorder("Depth"));
				Graphics.frameData.add(dpth);
				
				//also add x y
				try {
					Panel xy=new Panel();xy.setLayout(new BoxLayout(xy,BoxLayout.X_AXIS));
					xy.add(new Label("X"));
					xy.add(Graphics.character.new InputTextField(getAField(item.class,X.class),it));
					xy.add(new Label("Y"));
					xy.add(Graphics.character.new InputTextField(getAField(item.class,Y.class),it));
					Graphics.frameData.add(xy);
				}catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
			}
		}
		
		disp.add(Graphics.frameData,component_pos);
		Graphics.frameData.revalidate();
		
		display.draw();
	}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)private @interface X{}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)private @interface Y{}
	private interface slider_run{
		void Run(slider s);
	}
	private class slider extends JSlider implements ChangeListener{
		private static final long serialVersionUID = 1L;
		slider(int min,int max,int sel,DefaultMutableTreeNode p){
			super(min,max,sel);parent=p;sel_pos=sel;max_pos=max;
			setMajorTickSpacing(1);//This method will also set up a label table
			setPaintTicks(true);//By default, this property is false
			setPaintLabels(true);//By default, this property is false
			addChangeListener(this);
			int n=max-min;Dimension dim=new Dimension();dim.width=20*n;dim.height=50;setPreferredSize(dim);
		}
		private DefaultMutableTreeNode parent;
		private int value;
		private frame_item[]frames;
		private slider_run run;
		private int sel_pos;private int max_pos;
		@Override
		public void stateChanged(ChangeEvent e) {
			if(!getValueIsAdjusting()){
				value=getValue();
				frames=get_frame_items(parent);
				run.Run(this);
				build_eshow(frames);
				display.draw();//used at depth and after build elements show
			}
		}
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
