package graphics;

import static workspace.Project.spritedone;
import static workspace.Project.button;
import static workspace.Project.font;
import static workspace.Project.text;
import static workspace.Project.shape;
import static workspace.Project.image;
import static workspace.Project.dbl;
import static workspace.Project.exportsadd;
import static workspace.element.NamedId;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import workspace.WorkSpace;

class character extends JScrollPane{
	private static final long serialVersionUID = 1L;
	character(){
		setPreferredSize(new Dimension(200,WorkSpace.project.height));
		setBorder(BorderFactory.createTitledBorder("Character"));
		
		DefaultMutableTreeNode top=new DefaultMutableTreeNode();
		create_nodes(top);
		JTree tree=new JTree(top);
		tree.setCellRenderer(new renderer());
		tree.setRootVisible(false);
		
		setViewportView(tree);
	}
	private class type{
		private String name;
		private char letter;
		private ImageIcon icon;private ImageIcon icon_exp;
		private type(String n,char l){
			name=n;letter=l;
			icon=new ImageIcon("img/char/"+letter+".gif");
			
			Image image=icon.getImage();
			BufferedImage bi=new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			g.setColor(Color.BLUE);
			g.drawImage(image,0,0,null);
			g.setStroke(new BasicStroke(1));
			g.drawRect(0,0,bi.getWidth()-1,bi.getHeight()-1);
			g.dispose();
			icon_exp=new ImageIcon(bi);
		}
	}
	private class item{
		private String value;
		private char letter;
		private boolean exported;
		private item(String v,char l,boolean e){
			value=v;letter=l;exported=e;
		}
		@Override
		public String toString(){return value;}
	}
	private type Types[]={new type(button,'b'),new type(font,'f'),new type(text,'t'),new type(shape,'s'),new type(image,'i'),new type(dbl,'l'),new type(spritedone,'m')};
	private void create_nodes(DefaultMutableTreeNode top){
		try{
			List<Object>els=WorkSpace.project.elements;
			
			Field fd=display.getRefField(exportsadd);
			List<String>exports=new ArrayList<String>();
			for(int i=0;i<els.size();i++){
				Object e=els.get(i);
				if(e.getClass().getSimpleName().equals(exportsadd)){
					exports.add((String)fd.get(e));
				}
			}
			
			for(int i=0;i<els.size();i++){
				Object e=els.get(i);
				for(int j=0;j<Types.length;j++){
					if(e.getClass().getSimpleName().equals(Types[j].name)){
						Field f=e.getClass().getDeclaredField(NamedId);
						String name=(String)f.get(e);
						boolean exported=false;
						for(String n:exports){
							if(n.equals(name)){exported=true;break;}
						}
						top.add(new DefaultMutableTreeNode(new item(name,Types[j].letter,exported)));
						if(Types[j].name.equals(spritedone)){
							Field a=display.getSpriteField(spritedone);
							Graphics.frame.add_sprite(name,(String)a.get(e));
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
			item v=(item) ((DefaultMutableTreeNode)value).getUserObject();
			if(v != null){//getting null for some data
				for(int i=0;i<Types.length;i++){
					if(Types[i].letter==v.letter){
						if(v.exported)setIcon(Types[i].icon_exp);
						else setIcon(Types[i].icon);
						break;
					}
				}
			}
			return this;
		}
	}
}
