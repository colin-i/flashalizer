
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import workspace.Functions;
import workspace.Project;
import workspace.WorkSpace;
import workspace.function;
import static workspace.element.NamedId;

public class flashalizer {
	public static void main(String[] args) {
		//create f_list and NamedId to elements
		//run this later and got: duplicate class definition for name:... ; may be from .class. or some reflection or other thing, java assist using same reflection and cause the duplicate 
		try{
			ClassPool cp = ClassPool.getDefault();
			CtClass cc = cp.get("workspace.Elements");
			CtClass[]ccx=cc.getDeclaredClasses();
			CtClass x=cp.get("actionswf.ActionSwf");
			Functions.f_list=new ArrayList<function>();
			for(int j=0;j<ccx.length;j++){
				CtClass c=ccx[j];
				String[]a=c.getSimpleName().split("\\$");
				String b=Project.elements_names_convertor(a[a.length-1],null);
				CtMethod method=x.getDeclaredMethod(b);
				function f=new function(method);
				Functions.f_list.add(f);
				if(Functions.hasReturn(f)){
					CtField fld=new CtField(cp.get("java.lang.String"),NamedId,c);
					fld.setModifiers(Modifier.PUBLIC);
					c.addField(fld);
					c.toClass();
				}
			}
			Functions.f_list.add(new function(x.getDeclaredMethod("swf_new")));Functions.f_list.add(new function(x.getDeclaredMethod("swf_done")));
			for(function f:Functions.f_list)f.set_type(x.getDeclaredMethod(f.name));
		}
		catch (NotFoundException | CannotCompileException | ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
	
		//go to folder location(from C, open C:\...flashalizer.jar, no external files)
		Class<?> c=MethodHandles.lookup().lookupClass();
		URL url = c.getProtectionDomain().getCodeSource().getLocation();
		File f;
		try {
			f = new File(url.toURI());
			f=f.getParentFile();
			System.setProperty("user.dir",f.getPath()+"/");
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return;
		}
		//to avoid static on many declarations, use this
		WorkSpace wspace=new WorkSpace();wspace.main(args);
	}
}
