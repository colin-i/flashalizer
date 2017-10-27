package workspace;

import java.util.ArrayList;
import java.util.List;

import workspace.Project.Errors1;
import workspace.Project.Errors2;
import graphics.character.HeightInt;
import graphics.character.WidthInt;
import graphics.frame.RefId;
import graphics.frame.SpriteId;
import graphics.frame.ActionStr;
import graphics.frame.DepthInt;
import graphics.frame.XInt;
import graphics.frame.YInt;
import static actionswf.ActionSwf.FillStyleType_none;
import static actionswf.ActionSwf.StateMoveTo;

public class Elements {
	private static final String untitled="Untitled";
	public static class Button extends element{
		public @WidthInt int width;
		public @HeightInt int height;
		public ButtonData structure;
		Button(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public Button() throws IllegalArgumentException, IllegalAccessException{
			this(new Object[]{100,50,new ButtonData(),untitled});
		}
		public static class ButtonData{
			public int def_fill;
			public int def_line_sz;
			public int def_line;
			public int ov_fill;
			public int ov_line_sz;
			public int ov_line;
			public int dn_fill;
			public int dn_line_sz;
			public int dn_line;
			public int xcurve;
			public int ycurve;
			public String text;
			public @NamedId @Errors2 String font_id;
			public int font_height;
			public int font_vertical_offset;
			public int font_color;
			public String actions;
			ButtonData(Object[]v) throws IllegalArgumentException, IllegalAccessException{element.Element(v,this);}
			ButtonData(){
				def_fill=0xFF;/*def_line_sz=0;*/def_line=0xFF;ov_fill=0xFF;/*ov_line_sz=0;*/ov_line=0xFF;dn_fill=0xFF;/*dn_line_sz=0;*/dn_line=0xFF;
				/*xcurve=0;ycurve=0;*/
				text="";font_id="";/*font_height=0;font_vertical_offset=0;*/font_color=0xFF;
				actions="";
			}
		}
	}
	public static final String[]default_fonts={"_sans","_serif","_typewriter"};
	//if not static then Field[] from getDeclaredFields on previous static will have 3 fields: font.. font_flags and "this"
	public static class Font extends element{
		public String fontname;public int font_flags;
		Font(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public Font() throws IllegalArgumentException, IllegalAccessException{
			this(new Object[]{default_fonts[0],0,untitled});
		}
	}
	public static class Text extends element{
		public @WidthInt int width;
		public @HeightInt int height;
		public String variablename;
		public int flags;
		public EditText structure;
		Text(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public Text() throws IllegalArgumentException, IllegalAccessException{
			this(new Object[]{100,50,"",0,new EditText(),untitled});
		}
		public static class EditText{
			public @NamedId @Errors1 String font_id;
			public int font_height;
			String fontclassname;
			public int rgba;
			public int maxlength;
			public String initialtext;
			public int layout_align;
			public int layout_leftmargin;
			public int layout_rightmargin;
			public int layout_indent;
			public int layout_leading;
			//this constructor on the first position, getDeclaredConstructors()[0]
			EditText(Object[]v) throws IllegalArgumentException, IllegalAccessException{element.Element(v,this);}
			EditText(){
				font_id="";font_height=20;fontclassname="";rgba=0xFF;/*maxlength=0;*/initialtext="";/*layout_align=0;layout_leftmargin=0;layout_rightmargin=0;layout_indent=0;layout_leading=0;*/
			}
		}
	}
	public static class Shape extends element{
		public @WidthInt int width;
		public @HeightInt int height;
		public Object[]args;
		Shape(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public Shape() throws IllegalArgumentException, IllegalAccessException{
			this(new Object[]{200,200,new ShapeWithStyle().toArray(),untitled});
		}
		public static class ShapeWithStyle{
			public int fill;public Object fill_arg;
			public int line_size;public int line_color=0xff;
			public int[]records;
			public ShapeWithStyle(int f,Object f_arg,int lh,int lc,int[]r){
				fill=f;fill_arg=f_arg;line_size=lh;line_color=lc;records=r;
			}
			ShapeWithStyle(){
				fill=FillStyleType_none;/*line_size=0*/;records=new int[0];/*line_color=0xff;*/
			}
			public ShapeWithStyle(Object[]args){
				int i=0;
				fill=(int) args[i++];if(fill!=FillStyleType_none)fill_arg=args[i++];
				line_size=(int) args[i++];if(line_size!=0)line_color=(int) args[i++];
				records=new int[args.length-i];
				for(int j=0;i<args.length;i++,j++)records[j]=(int)args[i];
			}
			public static int end_of_values=-1;
			private static boolean phase;
			private static boolean edge;
			private static boolean flags;
			private static boolean straight;
			private static boolean cx;private static boolean cy;private static boolean x;//private static boolean y;
			public static void phase_start(){phase=false;}
			public static boolean phase_get(int val){
				if(phase==false){
					phase=true;
					if(val==0){edge=false;flags=false;}
					else{edge=true;straight=false;}
					return true;
				}
				if(edge==false){
					if(flags==false){
						if((val&StateMoveTo)!=0){
							flags=true;x=false;
							return true;
						}
					}else if(x==false){x=true;return true;}
					return false;
				}
				if(straight==false){
					straight=true;x=false;
					if(val==1)cy=true;
					else{cx=false;cy=false;}
					return true;
				}
				if(cy==false){
					if(cx==false){cx=true;return true;}
					cy=true;return true;
				}
				if(x==false){x=true;return true;}
				return false;
			}
			public Object[]toArray(){
				List<Object>lst=new ArrayList<Object>();
				lst.add(fill);
				if(fill!=FillStyleType_none)lst.add(fill_arg);
				lst.add(line_size);
				if(line_size!=0)lst.add(line_color);
				for(int i=0;i<records.length;i++)lst.add(records[i]);
				lst.add(end_of_values);
				Object[]dest=new Object[lst.size()];
				for(int i=0;i<dest.length;i++)dest[i]=lst.get(i);
				return dest;
			}
		}
	}
	/*public static class Image extends element{
		public String imagepath;
		Image(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}*/
	public static class DBL extends element{
		public String imagepath;
		DBL(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public DBL() throws IllegalArgumentException, IllegalAccessException{this(new Object[]{"",untitled});}
	}
	
	public static class Placement extends element{
		public @RefId String refid;public @DepthInt int depth;
		Placement(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	public static class PlacementCoords extends element{
		public @RefId String refid;public @DepthInt int depth;public @XInt int x;public @YInt int y;
		PlacementCoords(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public PlacementCoords(String string, int depth2, int x2, int y2){refid=string;depth=depth2;x=x2;y=y2;}
	}
	public static class Remove extends element{
		public @DepthInt int depth;
		Remove(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public Remove(int d) {depth=d;}
	}
	public static class ShowFrame{ShowFrame(Object[]x){};public ShowFrame(){};}
	
	public static class SpriteDone extends element{
		public @SpriteId String spriteid;
		public SpriteDone(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	public static class SpriteNew extends element{
		public SpriteNew(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public SpriteNew() throws IllegalArgumentException, IllegalAccessException{this(new Object[]{untitled});}
	}
	public static class SpritePlacement extends element{
		public @SpriteId String spriteid;public @RefId String refid;public @DepthInt int depth;
		SpritePlacement(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	public static class SpritePlacementCoords extends element{
		public @SpriteId String spriteid;public @RefId String refid;public @DepthInt int depth;public @XInt int x;public @YInt int y;
		SpritePlacementCoords(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public SpritePlacementCoords(String sprite,String string,int depth2,int x2,int y2){spriteid=sprite;refid=string;depth=depth2;x=x2;y=y2;}
	}
	public static class SpriteRemove extends element{
		public @SpriteId String spriteid;public @DepthInt int depth;
		SpriteRemove(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public SpriteRemove(String sprite, int d) {spriteid=sprite;depth=d;}
	}
	public static class SpriteShowFrame extends element{
		public @SpriteId String spriteid;
		SpriteShowFrame(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public SpriteShowFrame(String sprite) {spriteid=sprite;}
	}
	
	public static class ExportsAdd extends element{public @RefId String refid;public String name;
		ExportsAdd(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public ExportsAdd(String r,String n){refid=r;name=n;}
	}
	public static class ExportsDone{ExportsDone(Object[]v){};public ExportsDone(){}}
	
	public static class Action extends element{
		public @ActionStr String ac;
		Action(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public Action(String action){ac=action;}
	}
	public static class SpriteAction extends element{
		public @SpriteId String spriteid;public @ActionStr String ac;
		SpriteAction(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public SpriteAction(String sprite, String action) {spriteid=sprite;ac=action;}
	}
}
