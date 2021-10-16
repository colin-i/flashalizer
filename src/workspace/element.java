package workspace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class element{
	element(){}
	element(Object[]x) throws IllegalArgumentException, IllegalAccessException{
		Element(x,this);
	}
	static void Element(Object[]v,Object c) throws IllegalArgumentException, IllegalAccessException{
		Field[]f=c.getClass().getDeclaredFields();
		for(int i=0;i<f.length;i++)f[i].set(c,v[i]);
	}
	@Target({ElementType.PARAMETER,ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface NamedId{}
	public static final String NamedId="NamedId";
}