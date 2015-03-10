package actionswf;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

import workspace.element.NamedId;

public interface ActionSwf extends Library{//Library is used by com.sun.jna.Native
	static final String lib_name="actionswf";
	ActionSwf INSTANCE = (ActionSwf)Native.loadLibrary(lib_name,ActionSwf.class);
	
	int swf_button(ButtonData structure);
	int swf_font(String fontname,int font_flags);
	int swf_text(int bound_width,int bound_height,String variablename,int flags,EditText structure);
	int swf_shape(int width,int height,int[] args);
	int swf_image(String imagepath);
	int swf_dbl(String imagepath);
	
	void swf_done();
	void swf_new(String path,int width,int height,int backgroundcolor,int fps);
	void swf_placeobject(@NamedId int refid,int depth);
	void swf_placeobject_coords(@NamedId int refid,int depth,int x,int y);
	void swf_removeobject(int depth);
	void swf_showframe();
	
	int swf_sprite_done(@NamedId int spriteid);
	int swf_sprite_new();
	void swf_sprite_placeobject(@NamedId int spriteid,@NamedId int refid,int depth);
	void swf_sprite_placeobject_coords(@NamedId int spriteid,@NamedId int refid,int depth,int x,int y);
	void swf_sprite_removeobject(@NamedId int spriteid,int depth);
	void swf_sprite_showframe(@NamedId int spriteid);
	
	void swf_exports_add(@NamedId int refid,String name);
	void swf_exports_done();
	
	void action(String ac);
	void action_sprite(@NamedId int spriteid,String ac);
	
	interface privat extends Library{
		privat INST=(privat)Native.loadLibrary(lib_name,privat.class);
		Byte erbool_get();
		void erbool_reset();
		void abort();
	}
	
	public static final int FillStyleType_none=-1;
	class EditText extends Structure{
		//"..Structure has unknown size, ensure all fields are public"
		public int font_id;
		public int font_height;
		public String fontclassname;
		public int rgba;
		public int maxlength;
		public String initialtext;
		public int layout_align;
		public int layout_leftmargin;
		public int layout_rightmargin;
		public int layout_indent;
		public int layout_leading;
	}
	class ButtonData extends Structure{//the object is passed through com.sun.jna.Library, Structure is required
		public int width;
		public int height;
		public int def_fill;
		public int def_line_h;
		public int def_line;
		public int ov_fill;
		public int ov_line_h;
		public int ov_line;
		public int xcurve;
		public int ycurve;
		public String text;
		public int font_id;
		public int font_height;
		public int font_vertical_offset;
		public String actions;
	}
}