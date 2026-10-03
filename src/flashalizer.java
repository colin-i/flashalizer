
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	private static void printHelp() {
		System.out.println("Usage: flashalizer [folder]");
		System.out.println("       flashalizer --project=folder");
		System.out.println("       flashalizer --help");
		System.out.println("       flashalizer --remove-config");
		System.out.println("  --project=folder  Opens a named project");
		System.out.println("  --help            Shows this help and exits");
		System.out.println("  --remove-config   Removes ${HOME}/flashalizer if is empty");
	}

	private static void removeConfig() {
		Path config=Paths.get(System.getProperty("user.home"),"flashalizer");
		if(!Files.isDirectory(config,LinkOption.NOFOLLOW_LINKS)){
			System.err.println("Not an existing config directory: "+config);
			return;
		}
		try{
			Files.delete(config);
			System.out.println("Removed empty config directory: "+config);
		}catch(DirectoryNotEmptyException e){
			System.out.println("Config directory is not empty; not removed: "+config);
		}catch(IOException e){
			System.err.println("Could not remove config directory "+config+": "+e.getMessage());
		}
	}

	public static void main(String[] args) {
		if(args.length>0&&args[0].equals("--help")){
			printHelp();
			return;
		}
		if(args.length>0&&args[0].equals("--remove-config")){
			removeConfig();
			return;
		}
		String[] projectArgs=args;
		if(args.length>0&&args[0].startsWith("--project=")){
			String folder=args[0].substring("--project=".length());
			if(folder.isEmpty()){
				System.err.println("Missing folder for --project");
				printHelp();
				return;
			}
			projectArgs=new String[]{folder};
		}
		final String[] launchArgs=projectArgs;
		Project.initializeNativeNumericLocale();
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
			Functions.f_list.add(new function(x.getDeclaredMethod("swf_new_ex")));Functions.f_list.add(new function(x.getDeclaredMethod("swf_done")));
			for(function f:Functions.f_list)f.set_type(x.getDeclaredMethod(f.name));
		}
		catch (NotFoundException | CannotCompileException | ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
	
		/*//go to folder location(from C, open C:\...flashalizer.jar, no external files)
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
		}*/

		//to avoid static on many declarations, use this
		//WorkSpace wspace=new WorkSpace();wspace.main(args);
		final WorkSpace wspace = new WorkSpace();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				wspace.main(launchArgs);
			}
		});
	}
}
