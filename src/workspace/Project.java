package workspace;

import java.io.IOException;
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

public class Project{
	private Path path;
	boolean folder_set(String folder,boolean straight){return folder_set_base(folder,straight,false);}
	boolean folder_set_base(String folder,boolean straight,boolean no_open){
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
		}catch(IOException | XMLStreamException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Can't create/open: "+folder,null,JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}
	private String folder_file(String ext){
		return path.toString()+'/'+path.getFileName().toString()+"."+ext;
	}
	int width_default=640;int height_default=480;int backgroundcolor_default=0xffFFff;int fps_default=12;
	int width;            int height;            int backgroundcolor;                 int fps;
	List<Object> elements=new ArrayList<Object>();//data,write,read,display,build   ,new project
	void newproj(int wd,int hg,int bcolor,int fs){
		width=wd;height=hg;backgroundcolor=bcolor;fps=fs;
		fresh_proj();
		elements.add(new Elements.ShowFrame());
	}
	private void fresh_proj(){
		elements.clear();
		workspace.frame.setTitle(path.getFileName().toString()+" - "+"Flashalizer");
	}
	private class xml{
		private void write() throws XMLStreamException, IOException, IllegalAccessException{
			StaXWriter wr=new StaXWriter(folder_file("xml"));
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
		private void write_base(StaXWriter wr,Object obj) throws XMLStreamException,IllegalAccessException{
			Class<?>c=obj.getClass();
			wr.start(c.getSimpleName());
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
		private void read() throws XMLStreamException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			fresh_proj();
			StaXParser rd=new StaXParser(folder_file("xml"));
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
			//rd.advance();
			rd.close();
		}
		private Object read_base(StaXParser rd,String className,Class<?>[]cs) throws XMLStreamException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			for(int a=0;a<cs.length;a++){
				String simpleName=cs[a].getSimpleName();
				if(simpleName.equals(className)){
					Class<?>c=cs[a];
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
								if(Functions.table.isShapeBitmap((int)dest[0])){i=2;dest[1]=ints.get(1);}
								for(;i<dest.length;i++)dest[i]=Long.decode((String)ints.get(i)).intValue();
								values.add(dest);
							}
							else/*EditText,button*/values.add(read_base(rd,tp,c.getDeclaredClasses()));
						}
					}
					Constructor<?>ctructor=c.getDeclaredConstructors()[0];
					Method[]ms=ctructor.getClass().getDeclaredMethods();
					for(int z=0;z<ms.length;z++){
						if(ms[z].getName().equals("newInstance")){
							//passing the array is Object object=values.toArray();
							Object object=new Object[]{(values.toArray())};
							//last reader advance
							rd.advance();
							return ms[z].invoke(ctructor,object);
						}
					}
				}
			}
			return null;
		}
	}
	void save(){
		try {
			Functions.table.update();
			xml x=new xml();x.write();
		} catch (XMLStreamException | IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private void open() throws XMLStreamException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		xml x=new xml();x.read();
	}
	void build(){
		builder.build();
	}
	Builder builder=new Builder();
	class Builder{
		private ActionSwf as=ActionSwf.INSTANCE;
		private privat prv=ActionSwf.privat.INST;
		private Map<String, Integer>ids;
		private String error_msg;
		private int ids_get(String name) throws Throwable{
			if(ids.get(name)==null){
				error_msg="Undeclared "+name;
				prv.abort();
				throw new Throwable();
			}
			return ids.get(name);
		}
		private void build(){
			try{
				error_msg=null;
				Functions.table.update();
				ids=new HashMap<String, Integer>();
				call("swf_new",swf_new__arguments());
				for(int a=0;a<elements.size();a++){
					Object element=elements.get(a);
					String el_type=element.getClass().getSimpleName();
					Class<?>[]el_types=Elements.class.getDeclaredClasses();
					for(int x=0;x<el_types.length;x++){
						Class<?>c=el_types[x];
						function f = null;
						if(el_type.equals(c.getSimpleName())){
							List<Object>values=new ArrayList<Object>();
							Field[]fields=c.getDeclaredFields();
							String f_name=elements_names_convertor(el_type,null);
							for(int z=0;z<Functions.f_list.size();z++){
								f=Functions.f_list.get(z);
								if(f_name.equals(f.name)){
									for(int y=0;y<f.number_of_args;y++){
										Object val=fields[y].get(element);
										if(f.args_isNamed.get(y)==true)val=ids_get((String)val);
										else{
											String tp=f.args_types.get(y);
											if(tp.equals(Functions.ButtonData)||tp.equals(Functions.EditText)){
												Object newobj;
												if(tp.equals(Functions.ButtonData))newobj=new ActionSwf.ButtonData();
												else/*tp.equals(Functions.EditText)*/newobj=new ActionSwf.EditText();
												Field[]vals=val.getClass().getDeclaredFields();Field[]newvals=newobj.getClass().getDeclaredFields();
												for(int n=0;n<vals.length;n++){
													Object nv=vals[n].get(val);
													if(vals[n].isAnnotationPresent(NamedId.class))nv=ids_get((String)nv);
													newvals[n].set(newobj,nv);
												}
												val=newobj;
											}else if(tp.equals("Object[]")){
												Object[]src=(Object[])val;
												if(Functions.table.isShapeBitmap((int)src[0]))src[1]=ids_get((String)src[1]);
												int[]dest=new int[src.length];
												for(int i=0;i<src.length;i++)dest[i]=(int)src[i];
												val=dest;
											}
										}
										values.add(val);
									}
									break;
								}
							}
							Object[]vals=values.toArray();
							Object result=call(elements_names_convertor(el_type,null),vals);
							if(Functions.hasReturn(f))ids.put((String)c.getDeclaredField(NamedId).get(element),(Integer)result);
							break;
						}
					}
				}
				call("swf_done",null);
			} catch (Throwable e) {
				e.printStackTrace();
				if(error_msg == null)error_msg="Error(user input or space)";
				JOptionPane.showMessageDialog(null,error_msg);
			}
		}
		private Object call(String f,Object[] params) throws Throwable{
			Class<? extends Object> s=as.getClass();
			Method[] m=s.getDeclaredMethods();
			for(int i=0;i<m.length;i++){
				if(m[i].getName()==f){
					Object x=m[i].invoke(as,params);
					error();
					return x;
				}
			}
			return 0;
		}
		private void error() throws Throwable{
			Byte er=prv.erbool_get();
			if(er!=0){
				prv.erbool_reset();
				throw new Throwable();
			}
		}
		Object[] swf_new__arguments(){
			return new Object[]{folder_file("swf"),width,height,backgroundcolor,fps};
		}
	}
	public static String elements_names_convertor(String cName,String fName){
		String[][]values={
			{"Button","swf_button"},{"Font","swf_font"},{"Text","swf_text"},{"Shape","swf_shape"},{"Image","swf_image"},{"DBL","swf_dbl"}
			,{"Placement","swf_placeobject"},{"PlacementCoords","swf_placeobject_coords"},{"Remove","swf_removeobject"},{"ShowFrame","swf_showframe"}
			,{"SpriteDone","swf_sprite_done"},{"SpriteNew","swf_sprite_new"},{"SpritePlacement","swf_sprite_placeobject"},{"SpritePlacementCoords","swf_sprite_placeobject_coords"},{"SpriteRemove","swf_sprite_removeobject"},{"SpriteShowFrame","swf_sprite_showframe"}
			,{"ExportsAdd","swf_exports_add"},{"ExportsDone","swf_exports_done"}
			,{"Action","action"},{"ActionSprite","action_sprite"}
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
		return null;
	}
}