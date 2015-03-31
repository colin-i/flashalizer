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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		create_nodes();
		JTree tree=new JTree(root);
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
	class Character{
		private char letter;
		String export_name;
		Object element;
		private Character(type t,Object el,Field n,String e){
			letter=t.letter;element=el;name=n;export_name=e;
			for(type p:placeableTags){
				if(p.name.equals(t.name)){isPlaceable=true;break;}
			}
		}
		private Field name;
		@Override
		public String toString(){
			try {return (String) name.get(element);}
			catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
			return null;
		}
		private boolean isPlaceable;//Default Value false
		frame_item[]frames;
	}
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
						Field f=e.getClass().getDeclaredField(NamedId);
						String name=(String)f.get(e);
						Character c=new Character(Types[j],e,f,exports.get(name));
						root.add(new DefaultMutableTreeNode(c));
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
					if(Types[i].letter==v.letter){
						if(v.export_name!=null)setIcon(Types[i].icon_exp);
						else setIcon(Types[i].icon);
						break;
					}
				}
			}
			return this;
		}
	}
}
