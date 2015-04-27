package actionswf;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

import workspace.element.NamedId;

public interface ActionSwf extends Library{//Library is used by com.sun.jna.Native
	static final String lib_name="actionswf";
	ActionSwf INSTANCE = (ActionSwf)Native.loadLibrary(lib_name,ActionSwf.class);
	
	int swf_button(int width,int height,ButtonData structure);
	int swf_font(String fontname,int font_flags);
	int swf_text(int width,int height,String variablename,int flags,EditText structure);
	int swf_shape(int width,int height,int[] args);
	//integer swf_image(String image path);{swf_dbl_width..,Image,new type(image..,Character.width..}
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
		//integer swf_dbl_width(String image path);
		//integer swf_dbl_height(String image path);
	}
	
	public static final int HasFont=0x1;
	public static final int HasMaxLength=0x2;//used when typing, initial text and variable set ignore this
	public static final int HasTextColor=0x4;
	public static final int ReadOnly=0x8;
	public static final int Password=0x10;
	public static final int Multiline=0x20;
	public static final int WordWrap=0x40;
	public static final int HasText=0x80;
	//#define UseOutlines=0x100     maybe no HasFont no HasFontClass and some .ttf imported
	public static final int HTML=0x200;
	//#define WasStatic=0x400   Authored as dynamic text/Authored as static text           ;what is the current use if was static only at the beginning?
	public static final int Border=0x800;
	public static final int NoSelect=0x1000;
	public static final int HasLayout=0x2000;
	public static final int AutoSize=0x4000;//only when size goes outside the bounds
	//#define HasFontClass=0x8000     SymbolClass
	public static final int FillStyleType_none=-1;
	public static final int solid_fill=0;
	public static final int repeating_bitmap_fill=0x40;
	public static final int Non_edge_record=0;
	public static final int StateMoveTo=1;
	public static final int StateFillStyle0=2*StateMoveTo;
	static final int StateFillStyle1=2*StateFillStyle0;
	public static final int StateLineStyle=2*StateFillStyle1;
	public static final int Straight_edge=1;
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
	public static final int FontFlagsBold=1;
	public static final int FontFlagsItalic=2; 
	//const FontFlagsWideCodes=4        codes if NumGlyphs>0
	//const FontFlagsWideOffsets=8      offsets if NumGlyphs>0
	//const FontFlagsANSI=0x10          when codes
	//const FontFlagsSmallText=0x20     nothing detected at device fonts
	//const FontFlagsShiftJIS=0x40      when codes
	//const FontFlagsHasLayout=0x80     not implemented
	
	class ButtonData extends Structure{//the object is passed through com.sun.jna.Library, Structure is required,+public(throwing error otherwise)
		public int def_fill;
		public int def_line_sz;
		public int def_line;
		public int ov_fill;
		public int ov_line_sz;
		public int ov_line;
		public int xcurve;
		public int ycurve;
		public String text;
		public int font_id;
		public int font_height;
		public int font_vertical_offset;
		public int font_color;
		public String actions;
	}
}