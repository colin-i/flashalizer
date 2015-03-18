package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import workspace.Elements;
import workspace.WorkSpace;

public class display extends JScrollPane{
	private static final long serialVersionUID = 1L;
	private class content extends JComponent{
		private static final long serialVersionUID = 1L;
		private content(){
			setPreferredSize(new Dimension(WorkSpace.project.width,WorkSpace.project.height));
		}
		@Override
		protected void paintComponent(java.awt.Graphics g) {
			g.setColor(new Color(WorkSpace.project.backgroundcolor));//setBackground(JComponent does not paint its background, use JPanel);setColor:current color here
			g.fillRect(0,0,WorkSpace.project.width,WorkSpace.project.height);
		}
	}
	display(){
		setViewportView(new content());
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RefId{}
	static Field getRefField(String elem){
		Class<?>[]cls=Elements.class.getDeclaredClasses();
		for(Class<?>c:cls){
			if(c.getSimpleName().equals(elem)){
				Field[]flds=c.getDeclaredFields();
				for(Field fd:flds){
					if(fd.isAnnotationPresent(RefId.class))return fd;
				}
				return null;
			}
		}
		return null;
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface SpriteId{}
	static Field getSpriteField(String elem){
		Class<?>[]cls=Elements.class.getDeclaredClasses();
		for(Class<?>c:cls){
			if(c.getSimpleName().equals(elem)){
				Field[]flds=c.getDeclaredFields();
				for(Field fd:flds){
					if(fd.isAnnotationPresent(SpriteId.class))return fd;
				}
				return null;
			}
		}
		return null;
	}
}
