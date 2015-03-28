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
import static graphics.frame.sprites;
import graphics.frame.item;
import graphics.frame.sprite_item;
import graphics.frame.frame_item;
import graphics.frame.frame_entry;

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
			/*if(sel_path!=null)there is at least one frame*/showFrame(((frame_entry)((DefaultMutableTreeNode)sel_path.getPathComponent(sel_pos)).getUserObject()).frame,true,g,0,0);
		}
		private TreePath sel_path;private int sel_pos;
		private void showFrame(frame_item frame,boolean same_level,java.awt.Graphics g,int x_off,int y_off){
			int sel_pos_depth=-1;DefaultMutableTreeNode next=null;
			if(same_level==true){
				int p=sel_pos+1;
				if(p!=sel_path.getPathCount()){//is next an item?
					DefaultMutableTreeNode node=(DefaultMutableTreeNode)sel_path.getPathComponent(sel_pos);
					next=(DefaultMutableTreeNode)sel_path.getPathComponent(p);
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
				sprite_item s=sprites.get(it.character.value);
				int x_pos=x_off+it.x;int y_pos=y_off+it.y;
				if(s!=null){
					if(s.frames.length!=0){//no frames?
						boolean level_bool=false;frame_item f=s.frames[0];
						if(same_level==true){
							if(it.depth==sel_pos_depth){//selected item
								int in_pos=sel_pos+2;
								if(in_pos<sel_path.getPathCount()){
									//and has a selected frame
									sel_pos=in_pos;//set next in the selected TreePath
									DefaultMutableTreeNode nextframe=(DefaultMutableTreeNode)sel_path.getPathComponent(sel_pos);
									int n=next.getChildCount();
									for(int a=0;a<n;a++){
										if(next.getChildAt(a)==nextframe){
											f=((frame_entry)nextframe.getUserObject()).frame;//set frame
											break;
										}
									}
									level_bool=true;//set same level boolean
								}
							}
						}
						showFrame(f,level_bool,g,x_pos,y_pos);
					}
					continue;
				}
				g.drawImage(img,x_pos,y_pos,it.character.width,it.character.height,null);
			}
		}
	}
	static JComponent component;
	display(){
		component=new content();
		setViewportView(component);
	}
}
