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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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

import actionswf.ActionSwf;
import workspace.WorkSpace;

public class character extends JScrollPane{
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
		String value;
		private char letter;
		private boolean exported;
		int width;int height;
		private Character(String v,char l,boolean e){
			value=v;letter=l;exported=e;
		}
		@Override
		public String toString(){return value;}
	}
	private type Types[];
	static Map<String,Character>placeables;
	private class PlaceableTag{
		private String name;
		private Field width;
		private Field height;
		private PlaceableTag(String n){
			name=n;
			width=frame.getField(n,WidthInt.class);
			height=frame.getField(n,HeightInt.class);
		}
	}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface WidthInt{}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface HeightInt{}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)public @interface DBLStr{}
	private void create_nodes(DefaultMutableTreeNode top){
		try{
			placeables=new HashMap<String,Character>();
			type plc[]={new type(button,'b'),new type(text,'t'),new type(shape,'s'),new type(image,'i'),new type(spritedone,'m')};
			List<Object>els=WorkSpace.project.elements;
			
			List<PlaceableTag>p=new ArrayList<PlaceableTag>();
			List<type>t=new ArrayList<type>();
			for(type x:plc){
				t.add(x);
				p.add(new PlaceableTag(x.name));
			}
			PlaceableTag[]PlaceableTags=p.toArray(new PlaceableTag[p.size()]);
			t.add(new type(font,'f'));t.add(new type(dbl,'l'));
			Types=t.toArray(new type[t.size()]);
			
			Field fd=frame.getRefField(exportsadd);
			List<String>exports=new ArrayList<String>();
			for(int i=0;i<els.size();i++){
				Object e=els.get(i);
				if(e.getClass().getSimpleName().equals(exportsadd)){
					exports.add((String)fd.get(e));
				}
			}
			
			Field fd_img=frame.getField(image,DBLStr.class);
			for(int i=0;i<els.size();i++){
				Object e=els.get(i);
				for(int j=0;j<Types.length;j++){
					String type=Types[j].name;
					if(e.getClass().getSimpleName().equals(type)){
						Field f=e.getClass().getDeclaredField(NamedId);
						String name=(String)f.get(e);
						boolean exported=false;
						for(String n:exports){
							if(n.equals(name)){exported=true;break;}
						}
						Character c=new Character(name,Types[j].letter,exported);
						top.add(new DefaultMutableTreeNode(c));
						for(PlaceableTag a:PlaceableTags){
							if(a.name.equals(type)){
								placeables.put(c.value,c);
								if(Types[j].name.equals(spritedone)){
									Field b=frame.getSpriteField(spritedone);
									Graphics.frame.add_sprite(name,(String)b.get(e));
								}else{
									int w = 0;int h = 0;
									if(type.equals(image)){
										Object inter=ActionSwf.privat.INST;
										Object[]obj={fd_img.get(e)};
										try {
											w=(int) workspace.WorkSpace.project.builder.call(inter,"swf_dbl_width",obj);
											h=(int) workspace.WorkSpace.project.builder.call(inter,"swf_dbl_height",obj);
										} catch (Throwable e1) {e1.printStackTrace();}
									}else{w=(int)a.width.get(e);h=(int)a.height.get(e);}
									c.width=w;c.height=h;
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
