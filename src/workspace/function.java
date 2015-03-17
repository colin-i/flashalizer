package workspace;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static workspace.element.NamedId;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class function{
	public String name;
	int number_of_args;
	List<String> args_types;public List<Boolean> args_isNamed;
	String return_type;
	
	public function(CtMethod method) throws NotFoundException, ClassNotFoundException{
		name=method.getName();
		CtClass[] c=method.getParameterTypes();
		number_of_args=c.length;
		args_isNamed=new ArrayList<Boolean>();Object[][]a=method.getParameterAnnotations();
		
		for(int i=0;i<c.length;i++){
			Object[]b=a[i];Boolean d=false;
			for(int n=0;n<b.length;n++){
				if(nested_conv_base(b[n].toString()).equals(NamedId))d=true;
			}
			args_isNamed.add(d);
		}
		
		return_type=nested_conv(method.getReturnType());
	}
	public void set_type(CtMethod method) throws NotFoundException{
		args_types=new ArrayList<String>();
		CtClass[] c=method.getParameterTypes();
		Field[]f=null;
		for(Class<?>x:Elements.class.getDeclaredClasses()){
			if(x.getSimpleName().equals(Project.elements_names_convertor(null,name))){
				f=x.getDeclaredFields();
				break;
			}
		}
		for(int i=0;i<c.length;i++){
			String type;
			//String<>id,needed at insert,etc.
			if(f!=null)type=f[i].getType().getSimpleName();
			else type=nested_conv(c[i]);
			args_types.add(type);
		}
	}
	private String nested_conv(CtClass s){
		return nested_conv_base(s.getSimpleName());
	}
	private String nested_conv_base(String s){ 
		String[]a=s.split("\\$");
		return a[a.length-1];
	}
}
