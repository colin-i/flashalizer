package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import workspace.WorkSpace;
import static graphics.frame.tree;
import graphics.frame.item;
import graphics.frame.frame_item;
import graphics.character.Character;

class display extends JScrollPane{
	private static final long serialVersionUID = 1L;
	private class content extends JComponent{
		private static final long serialVersionUID = 1L;
		private Image img;
		private content(){
			try {
				img=ImageIO.read(new File("img/display.gif"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			setPreferredSize(new Dimension(WorkSpace.project.width,WorkSpace.project.height));
		}
		@Override
		protected void paintComponent(java.awt.Graphics g) {
			g.setColor(new Color(WorkSpace.project.backgroundcolor));//setBackground(JComponent does not paint its background, use JPanel);setColor:current color here
			g.fillRect(0,0,WorkSpace.project.width,WorkSpace.project.height);
			
			sel_path=tree.getSelectionPath();sel_pos=1;
			if(sel_path==null)sel_path=tree.getPathForRow(0);//null if row >= getRowCount()
			/*if(sel_path!=null)there is at least one frame*/showFrame(
				get_frame_item((DefaultMutableTreeNode)sel_path.getPathComponent(sel_pos))
				,true,g,0,0);
		}
		private frame_item get_frame_item(DefaultMutableTreeNode f){
			DefaultMutableTreeNode parent=(DefaultMutableTreeNode) f.getParent();
			frame_item[]frms=frame.get_frame_items(parent);
			for(int a=0;a<parent.getChildCount();a++){
				if(parent.getChildAt(a)==f)return frms[a];
			}
			return null;
		}
		private TreePath sel_path;private int sel_pos;
		private void showFrame(frame_item frame,boolean same_level,java.awt.Graphics g,int x_off,int y_off){
			int sel_pos_depth=-1;
			if(same_level==true){
				int p=sel_pos+1;
				if(p!=sel_path.getPathCount()){//is next an item?
					DefaultMutableTreeNode node=(DefaultMutableTreeNode)sel_path.getPathComponent(sel_pos);
					DefaultMutableTreeNode next=(DefaultMutableTreeNode)sel_path.getPathComponent(p);
					int n=node.getChildCount();
					for(int i=0;i<n;i++){
						if(node.getChildAt(i)==next){
							sel_pos_depth=((item)next.getUserObject()).depth;//get depth at position
							break;
						}
					}
				}
			}
			item[]items=frame.eshow;
			for(int i=0;i<items.length;i++){
				item it=items[i];
				frame_item[]s=it.character.frames;
				int x_pos=x_off+it.x;int y_pos=y_off+it.y;
				if(s!=null){
					if(s.length!=0){//no frames?
						boolean level_bool=false;frame_item f=s[0];
						if(same_level==true){
							if(it.depth==sel_pos_depth){//selected item
								int in_pos=sel_pos+2;
								if(in_pos<sel_path.getPathCount()){
									//and has a selected frame
									sel_pos=in_pos;//set next in the selected TreePath
									f=get_frame_item((DefaultMutableTreeNode)sel_path.getPathComponent(sel_pos));//set frame
									level_bool=true;//set same level boolean
								}
							}
						}
						showFrame(f,level_bool,g,x_pos,y_pos);
					}
					continue;
				}
				Character cr=it.character;Object el=cr.element;
				int w = 0;int h = 0;
				try{
					w=(int)cr.width.get(el);
					h=(int)cr.height.get(el);
				}catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
				g.drawImage(img,x_pos,y_pos,w,h,null);
			}
		}
	}
	private static JComponent component;
	display(){
		component=new content();
		this.setViewportView(new content());
	}
	static void draw() {
		component.repaint();
	}
}
