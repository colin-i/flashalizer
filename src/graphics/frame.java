package graphics;

import java.awt.Dimension;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import workspace.WorkSpace;
import static workspace.Project.showframe;
import static workspace.Project.placement;
import static workspace.Project.placementcoords;
import static workspace.Project.remove;
import static workspace.Project.action;

class frame extends JScrollPane{
	private static final long serialVersionUID = 1L;
	frame(){
		setPreferredSize(new Dimension(200,WorkSpace.project.height));
		setBorder(BorderFactory.createTitledBorder("Frame"));
		
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
		private ControlTag(String n){
			name=n;
			ref=display.getRefField(name);
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
	private void create_nodes(DefaultMutableTreeNode top){
		List<Object>els=WorkSpace.project.elements;
		ControlTag[]ControlTags={new ControlTag(placement),new ControlTag(placementcoords),new ControlTag(remove),new ControlTag(action)};
		int pos=0;
		for(int i=pos;i<els.size();i++){
			Object e=els.get(i);
			if(e.getClass().getSimpleName().equals(showframe)){
				DefaultMutableTreeNode frame=new DefaultMutableTreeNode("Frame"+top.getChildCount());
				for(int j=pos;j<i;j++){
					Object el=els.get(j);
					for(int k=0;k<ControlTags.length;k++){
						if(el.getClass().getSimpleName().equals(ControlTags[k].name)){
							String n=ControlTags[k].name;boolean p=false;
							if(ControlTags[k].ref!=null){
								try {n=(String) ControlTags[k].ref.get(el);}
								catch (IllegalArgumentException | IllegalAccessException e1) {e1.printStackTrace();}
								p=true;
							}
							frame.add(new DefaultMutableTreeNode(new item(n,p)));
							break;
						}
					}
				}
				top.add(frame);
				pos=i;
			}
		}
	}
}
