package graphics;

import java.awt.Dimension;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

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

class frame extends JScrollPane{
	private static final long serialVersionUID = 1L;
	frame(){
		setPreferredSize(new Dimension(200,WorkSpace.project.height));
		setBorder(BorderFactory.createTitledBorder("Frame"));
		sprites=new HashMap<String,frame.sprite_item>();
	}
	void init(){
		DefaultMutableTreeNode top=new DefaultMutableTreeNode();
		create_nodes(top);
		JTree tree=new JTree(top);
		tree.setRootVisible(false);
		for(int i=0;i<tree.getRowCount();i++) {
			tree.expandRow(i);
		}
		
		setViewportView(tree);
	}
	private class ControlTag{
		private String name;
		private Field ref;
		private Field sprite_id;
		private ControlTag(String n){
			name=n;
			ref=display.getRefField(name);
			sprite_id=display.getSpriteField(name);
		}
	}
	private class ControlTagEx{
		private ControlTag[]tags;
		private ControlTagEx(String n1,String n2){
			tags=new ControlTag[2];
			tags[0]=new ControlTag(n1);tags[1]=new ControlTag(n2);
		}
	}
	private class item{
		private String value;
		private boolean placement;
		private item(String v,boolean p){
			value=v;placement=p;
		}
		@Override
		public String toString(){
			if(placement==true)return value;
			return "<html><i>"+value;
		}
	}
	private class sprite_item{
		private item[][]frames;
		private sprite_item(item[][]f){
			frames=f;
		}
	}
	private Map<String,sprite_item>sprites;
	void add_sprite(String name,String preName){
		sprites.put(name,new sprite_item(get_frames(1,preName)));
	}
	private void noding(DefaultMutableTreeNode parent,item[]elements){
		for(item el:elements){
			DefaultMutableTreeNode node=new DefaultMutableTreeNode(el);
			sprite_item s=sprites.get(el.value);
			if(s!=null){
				for(item[]f:s.frames){
					DefaultMutableTreeNode s_frame=new DefaultMutableTreeNode(frame_notation(node));
					noding(s_frame,f);
					node.add(s_frame);
				}
			}
			parent.add(node);
		}
	}
	private String frame_notation(DefaultMutableTreeNode x){return "Frame"+x.getChildCount();}
	private void create_nodes(DefaultMutableTreeNode top){
		item[][]frames=get_frames(0);
		for(int i=0;i<frames.length;i++){
			DefaultMutableTreeNode frame=new DefaultMutableTreeNode(frame_notation(top));
			noding(frame,frames[i]);
			top.add(frame);
		}
	}
	private ControlTagEx[]CTags={
		new ControlTagEx(placement,spriteplacement),
		new ControlTagEx(placementcoords,spriteplacementcoords),
		new ControlTagEx(remove,spriteremove),
		new ControlTagEx(action,actionsprite)
	};
	private ControlTagEx STag=new ControlTagEx(showframe,spriteshowframe);
	private item[][]get_frames(int set){
		return get_frames(set,null);
	}
	private item[][]get_frames(int set,String sprite_name){
		List<Object>els=WorkSpace.project.elements;
		List<item[]>frames=new ArrayList<item[]>();
		int pos=0;
		for(int i=pos;i<els.size();i++){
			if(checkItem(STag.tags[set],els.get(i),sprite_name)){
				frames.add(get_items(els,pos,i,set,sprite_name));
				pos=i+1;
			}
		}
		return frames.toArray(new item[frames.size()][]);
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
	private item[]get_items(List<Object>els,int start,int end,int set,String sprite_name){
		List<item>items=new ArrayList<item>(); 
		for(int j=start;j<end;j++){
			Object el=els.get(j);
			for(int k=0;k<CTags.length;k++){
				ControlTag t=CTags[k].tags[set];
				if(checkItem(t,el,sprite_name)){
					String n=t.name;boolean p=false;
					if(t.ref!=null){
						try {n=(String)t.ref.get(el);}
						catch (IllegalArgumentException | IllegalAccessException e1) {e1.printStackTrace();}
						p=true;
					}
					items.add(new item(n,p));
					break;
				}
			}
		}
		return items.toArray(new item[items.size()]);
	}
}
