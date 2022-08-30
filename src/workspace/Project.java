package workspace;

//import com.sun.jna.Library;
//import com.sun.jna.Native;

import graphics.frame.SpriteId;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import xml.StaXParser;
import xml.StaXWriter;
import actionswf.ActionSwf;
import actionswf.ActionSwf.privat;
import static workspace.element.NamedId;
import static actionswf.ActionSwf.HasText;
import static actionswf.ActionSwf.repeating_bitmap_fill;

public class Project{
	private Path path;
	boolean folder_set(String folder,boolean straight){return folder_set_base(folder,straight,false);}
	boolean folder_set_base(String folder,boolean straight,boolean no_open){
	//return folder_set_base_extra(folder,straight,no_open,null);}
	//boolean folder_set_base_extra(String folder,boolean straight,boolean no_open,String extra){
		Path p=path;//when want to test if 'open' a 'folder' has a valid '.xml'
		try{
			path=Paths.get(folder);
			if(Files.isDirectory(path)){
				if(straight==false){
					if(JOptionPane.showConfirmDialog(null,"Directory already exists. Select it?","Already exists",JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
						return false;
				}else if(no_open==false)open();
			}else{
				if(straight==true){
					JOptionPane.showMessageDialog(null,"There is no project at "+folder,null,JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				Files.createDirectory(path);
			}
		}catch(Throwable e){
			e.printStackTrace();
			path=p;
			JOptionPane.showMessageDialog(null,"Can't create/open: "+folder,null,JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		//System.setProperty("user.dir",path.toString());  //this for dbl with relative paths, but is still not resolving
		//the below method is failing random
		//char[] test = new char[300];
		//int q=Kernel32.GetCurrentDirectoryW(300,test);
		//System.out.println("test "+String.valueOf(q)+" "+String.valueOf(test));
		/*String s=path.toString();
		char[] c=s.toCharArray();
		int x=Kernel32.SetCurrentDirectoryW(c);
		if(x==0){
			System.out.println("SetCurrentDirectoryW failed for "+s);
			if(extra!=null){
				x=Kernel32.SetCurrentDirectoryW(extra.toCharArray());
				if(x==0){
					System.out.println("SetCurrentDirectoryW failed for "+extra);
				}
			}
		}*/
		//use Path p = Paths.get(dbl); if (p.isAbsolute())  or file.isAbsolute()

		return true;
	}

	/*private static interface MyKernel32 extends Library {
		public MyKernel32 INSTANCE = (MyKernel32) Native.loadLibrary("Kernel32", MyKernel32.class);

		//BOOL SetCurrentDirectory( LPCTSTR lpPathName );
		int SetCurrentDirectoryW(char[] pathName);

		//int GetCurrentDirectoryW(int nBufferLength,char[] lpBuffer);
	}
	private MyKernel32 Kernel32=MyKernel32.INSTANCE;*/

	public String folder_file(String ext){
		return path.toString()+'/'+path.getFileName().toString()+"."+ext;
	}
	private static final String proj_ext="xml";
	public String folder_file_default(){
		return folder_file(proj_ext);
	}
	int width_default=640;int height_default=480;int backgroundcolor_default=0xffFFff;int fps_default=12;
	public int width;     public int height;     public int backgroundcolor;          int fps;
	public List<Object> elements=new ArrayList<Object>();//data,write,read,display,build   ,new project
	void newproj(int wd,int hg,int bcolor,int fs){
		width=wd;height=hg;backgroundcolor=bcolor;fps=fs;
		elements.clear();
		fresh_proj();
		elements.add(new Elements.ShowFrame());
	}
	private void fresh_proj(){
		WorkSpace.frame.setTitle(path.getFileName().toString()+" - "+"Flashalizer");
	}
	public boolean isShapeBitmap(int type){
		return (repeating_bitmap_fill<=type&&type<=0x43);
	}
	public String newInst="newInstance";
	Object runtime_instance(Class<?>c,List<Object>values) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Constructor<?>ctructor=c.getDeclaredConstructors()[0];
		Method[]ms=ctructor.getClass().getDeclaredMethods();
		for(int z=0;z<ms.length;z++){
			if(ms[z].getName().equals(newInst)){
				//passing the array is Object object=values.toArray();
				Object object=new Object[]{(values.toArray())};
				return ms[z].invoke(ctructor,object);
			}
		}
		return null;//Java Problem
	}
	private class xml{
		private void write(String ext) throws XMLStreamException, IOException, IllegalAccessException{
			StaXWriter wr=new StaXWriter(folder_file(ext));
			String swf="SWF";
			wr.start(swf);
				String head="Header";
				wr.start(head);
					wr.data("width",Integer.toString(width));
					wr.data("height",Integer.toString(height));
					wr.data("backgroundcolor",Integer.toString(backgroundcolor));
					wr.data("fps",Integer.toString(fps));
				wr.end(head);
				String els="Elements";
				wr.start(els);
					for(int a=0;a<elements.size();a++){
						Object element=elements.get(a);
						String el_type=element.getClass().getSimpleName();
						Class<?>[]el_types=Elements.class.getDeclaredClasses();
						for(int x=0;x<el_types.length;x++){
							Class<?>c=el_types[x];
							if(el_type.equals(c.getSimpleName())){
								write_base(wr,element);
								break;
							}
						}
					}
				wr.end(els);
			wr.end(swf);
			wr.close();
		}
		private static final String exclude_xml="exclude";
		private void write_base(StaXWriter wr,Object obj) throws XMLStreamException,IllegalAccessException{
			Class<?>c=obj.getClass();
			String nm=c.getSimpleName();
			if(obj instanceof elementplus)wr.start_attr(nm,exclude_xml,Boolean.toString(((elementplus)obj).exclude));
			else wr.start(nm);
			Field[]fields=c.getDeclaredFields();
			for(int y=0;y<fields.length;y++){
				Field fld=fields[y];
				Object val=fld.get(obj);String tp=fld.getType().getSimpleName();String name=fld.getName();
				if(tp.equals("int"))wr.data(name,Integer.toString((int)val));
				else if(tp.equals("String"))wr.data(name,val.toString());
				else if(tp.equals("Object[]")){String s="Array";Object[]a=(Object[])val;wr.start(s);for(int x=0;x<a.length;x++)wr.data("I"+Integer.toString(x),a[x].toString());wr.end(s);}
				else/*EditText,button*/write_base(wr,val);
			}
			wr.end(c.getSimpleName());
		}
		private void read() throws Throwable{
			StaXParser rd=new StaXParser(folder_file("xml"));
			elements.clear();
			rd.advance();//s w f
				rd.advance();//Header
					width=Long.decode(rd.data()).intValue();
					height=Long.decode(rd.data()).intValue();
					backgroundcolor=Long.decode(rd.data()).intValue();
					fps=Long.decode(rd.data()).intValue();
				rd.advance();
				rd.advance();//Elements
					String tag;
					while((tag=rd.advance_start())!=null)elements.add(read_base(rd,tag,Elements.class.getDeclaredClasses()));
			rd.close();
			for(int i=0;i<elements.size();i++){
				Object ob=elements.get(i).getClass();Object x=Elements.ShowFrame.class;
				if(ob==x){
					fresh_proj();
					return;
				}
			}
			throw new Throwable();
		}
		private Object read_base(StaXParser rd,String className,Class<?>[]cs) throws XMLStreamException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			for(int a=0;a<cs.length;a++){
				String simpleName=cs[a].getSimpleName();
				if(simpleName.equals(className)){
					Class<?>c=cs[a];
					//
					String exclstr=null;
					if(c.getSuperclass().getSimpleName().equals(elementplus.class.getSimpleName()))exclstr=rd.get_attr(exclude_xml);
					//
					List<Object>values=new ArrayList<Object>();
					Field[]fields=c.getDeclaredFields();
					for(int z=0;z<fields.length;z++){
						String tp=fields[z].getType().getSimpleName();
						if(tp.equals("int"))values.add(Long.decode(rd.data()).intValue());
						else if(tp.equals("String"))values.add(rd.data());
						else{
							rd.advance();
							if(tp.equals("Object[]")){
								List<Object>ints=new ArrayList<Object>();String iget;while((iget=rd.data())!=null)ints.add(iget);
								Object[]dest=new Object[ints.size()];
								dest[0]=Long.decode((String)ints.get(0)).intValue();int i=1;
								if(isShapeBitmap((int)dest[0])){i=2;dest[1]=ints.get(1);}
								for(;i<dest.length;i++)dest[i]=Long.decode((String)ints.get(i)).intValue();
								values.add(dest);
							}
							else/*EditText,button*/values.add(read_base(rd,tp,c.getDeclaredClasses()));
						}
					}
					
					//last reader advance
					rd.advance();
					Object ob=runtime_instance(c,values);
					if(exclstr!=null)((elementplus)ob).exclude=Boolean.parseBoolean(exclstr);
					return ob;
				}
			}
			return null;//Java Problem
		}
	}
	void save_default(){save(proj_ext);}
	void save(String ext){
		try {
			WorkSpace.updateElements();
			xml x=new xml();x.write(ext);
		} catch (XMLStreamException | IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private void open() throws Throwable{
		xml x=new xml();
		List<Object>clone=new ArrayList<Object>(elements);
		int w=width;int h=height;int c=backgroundcolor;int f=fps;
		try{
			x.read();
		}catch(Throwable e){
			elements=clone;
			width=w;height=h;backgroundcolor=c;fps=f;
			throw e;
		}
	}
	void build(){
		builder.build();
	}
	Builder builder=new Builder();
	class Builder{
		private ActionSwf as=ActionSwf.INSTANCE;
		private privat prv=ActionSwf.privat.INST;
		private Map<String, Integer>ids;private Map<String, Integer>ids_sprite;
		//private String error_msg;
		private int ids_get_ex(String name,boolean isSprite) throws Throwable{
			Integer val;
			if(isSprite==false)val=ids.get(name);
			else val=ids_sprite.get(name);
			if(val==null)throw new ThrowAndStop("Undeclared "+name);
			return val;
		}
		private int ids_get(String name) throws Throwable{
			return ids_get_ex(name,false);
		}
		private class ThrowAndStop extends Throwable{
			private static final long serialVersionUID = 1L;private ThrowAndStop(String s){super(s);}
		}
		private void build(){
			try{
				WorkSpace.updateElements();
				ids=new HashMap<String, Integer>();ids_sprite=new HashMap<String, Integer>();
				//we have unidentified access violation and have to implement this
				String rec_ext="recover";
    			File rec_file=new File(folder_file(rec_ext));
    			save(rec_ext);
    			//
				caller("swf_new",swf_new__arguments());
				for(int a=0;a<elements.size();a++){
					Object element=elements.get(a);
					if(element instanceof elementplus&&((elementplus)element).exclude)continue;
					Class<?>clas=element.getClass();
					String el_type=clas.getSimpleName();
					Class<?>[]el_types=Elements.class.getDeclaredClasses();
					for(int x=0;x<el_types.length;x++){
						Class<?>c=el_types[x];
						if(el_type.equals(c.getSimpleName())){
							String f_name=elements_names_convertor(el_type,null);
							for(int z=0;z<Functions.f_list.size();z++){
								function fn=Functions.f_list.get(z);
								if(f_name.equals(fn.name)){
									List<Object>values=new ArrayList<Object>();
									Field[]fields=c.getDeclaredFields();
									for(int y=0;y<fn.number_of_args;y++){
										Field fld=fields[y];
										Object val=fld.get(element);
										if(fn.args_isNamed.get(y)==true)val=ids_get_ex((String)val,fld.isAnnotationPresent(SpriteId.class));
										else{
											String tp=fn.args_types.get(y);
											if(tp.equals(Functions.ButtonData)||tp.equals(Functions.EditText)){
												Object newobj;
												if(tp.equals(Functions.ButtonData))newobj=new ActionSwf.ButtonData();
												else/*tp.equals(Functions.EditText)*/newobj=new ActionSwf.EditText();
												Field[]vals=val.getClass().getDeclaredFields();Field[]newvals=newobj.getClass().getDeclaredFields();
												boolean Error2=false;
												for(int n=0;n<vals.length;n++){
													boolean isError=false;
													Object nv=vals[n].get(val);
													if(vals[n].isAnnotationPresent(NamedId.class)){
														String s=(String)nv;
														try{nv=ids_get(s);}
														catch(Throwable e){
															isError=true;
															if(s.length()==0){
																Object[]ans={Errors1.class,Errors2.class};
																for(Object cs:ans){
																	@SuppressWarnings("unchecked")Class<? extends Annotation>cls=(Class<? extends Annotation>)cs;
																	if(vals[n].isAnnotationPresent(cls)){
																		if(cls==Errors1.class){
																			//it is required to declare a font and enter the id for the text
																			if((((Elements.Text)element).flags&HasText)==0)isError=false;
																		}
																		else/*Errors2.class*/{
																			Error2=true;isError=false;
																		}
																		break;
																	}
																}
															}
															if(isError)throw e;
															else isError=true;//for not setting bad value bottom from here
														}
													}
													if(isError==false)newvals[n].set(newobj,nv);
												}
												if(Error2==true)((ActionSwf.ButtonData)newobj).text=null;
												val=newobj;
											}else if(tp.equals("Object[]")){
												Object[]src=((Object[])val).clone();
												if(isShapeBitmap((int)src[0]))src[1]=ids_get((String)src[1]);
												int[]dest=new int[src.length];
												for(int i=0;i<src.length;i++)dest[i]=(int)src[i];
												val=dest;
											}
										}
										values.add(val);
									}
									Object[]vals=values.toArray();
									Object result=caller(elements_names_convertor(el_type,null),vals);
									if(Functions.hasReturn(fn)){
										Map<String, Integer>map;
										if(f_name.equals("swf_sprite_new"))map=ids_sprite;
										else map=ids;
										map.put((String)c.getDeclaredField(NamedId).get(element),(Integer)result);
									}
									break;
								}
							}
							break;
						}
					}
				}
				caller("swf_done",null);
				//
				rec_file.delete();
			} catch (Throwable e) {
				e.printStackTrace();
				if(e instanceof ThrowAndStop)prv.freereset();
				JOptionPane.showMessageDialog(null,e.getMessage());
			}
		}
		private Object caller(String f,Object[]params) throws Throwable{
			return call(as,f,params);
		}
		private Object call(Object inter,String f,Object[] params) throws Throwable{
			Method[] m=inter.getClass().getDeclaredMethods();
			for(int i=0;i<m.length;i++){
				if(m[i].getName()==f){
					Object x=m[i].invoke(inter,params);
					error();
					return x;
				}
			}
			return null;//Java Problem
		}
		private void error() throws Throwable{
			Byte er=prv.erbool_get();
			if(er!=0){
				prv.erbool_reset();
				throw new Throwable("Error(user input or space)");
			}
		}
		Object[] swf_new__arguments(){
			return new Object[]{outString(),width,height,backgroundcolor,fps};
		}
	}
	String outString(){
		return folder_file("swf");
	}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)@interface Errors1{}
	@Target(ElementType.FIELD)@Retention(RetentionPolicy.RUNTIME)@interface Errors2{}
	private static final String sprite="Sprite";
	public static final String button="Button";
	public static final String font="Font";
	public static final String text="Text";
	public static final String shape="Shape";
	//public static final String image="Image";
	public static final String dbl="DBL";
	public static final String placement="Placement";
	public static final String placementcoords="PlacementCoords";
	public static final String remove="Remove";
	public static final String showframe="ShowFrame";
	public static final String spritedone=sprite+"Done";
	public static final String spriteplacement=sprite+"Placement";
	public static final String spriteplacementcoords=sprite+"PlacementCoords";
	public static final String spriteremove=sprite+"Remove";
	public static final String spriteshowframe=sprite+"ShowFrame";
	public static final String exportsadd="ExportsAdd";
	public static final String action="Action";
	public static final String actionsprite=sprite+"Action";
	public static String elements_names_convertor(String cName,String fName){
		String[][]values={
			{button,"swf_button"},{font,"swf_font"},{text,"swf_text"},{shape,"swf_shape"}/*,{image,"swf_image"}*/,{dbl,"swf_dbl"}
			,{placement,"swf_placeobject"},{placementcoords,"swf_placeobject_coords"},{remove,"swf_removeobject"},{showframe,"swf_showframe"}
			,{spritedone,"swf_sprite_done"},{"SpriteNew","swf_sprite_new"},{spriteplacement,"swf_sprite_placeobject"},{spriteplacementcoords,"swf_sprite_placeobject_coords"},{spriteremove,"swf_sprite_removeobject"},{spriteshowframe,"swf_sprite_showframe"}
			,{exportsadd,"swf_exports_add"},{"ExportsDone","swf_exports_done"}
			,{action,"action"},{actionsprite,"action_sprite"}
		};
		int i=0;
		if(cName != null){
			for(;i<values.length;i++){
				if(cName.equals(values[i][0]))return values[i][1];
			}
		}
		for(;i<values.length;i++){
			if(fName.equals(values[i][1]))return values[i][0];
		}
		return null;//Java Problem
	}
}
