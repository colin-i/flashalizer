package workspace;

import java.util.ArrayList;
import java.util.List;

import workspace.Project.Errors1;
import workspace.Project.Errors2;
import graphics.character.HeightInt;
import graphics.character.TEit;
import graphics.character.TF;
import graphics.character.WidthInt;
import graphics.frame.RefId;
import graphics.frame.SpriteId;
import graphics.frame.ActionStr;
import graphics.frame.DepthInt;
import graphics.frame.XInt;
import graphics.frame.YInt;
import static actionswf.ActionSwf.FillStyleType_none;

public class Elements {
	private static String untitled="Untitled";
	public static class Button extends element{
		public @WidthInt int width;
		public @HeightInt int height;
		ButtonData structure;
		Button(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		static class ButtonData{
			int def_fill;
			int def_line_sz;
			int def_line;
			int ov_fill;
			int ov_line_sz;
			int ov_line;
			int xcurve;
			int ycurve;
			String text;
			@NamedId @Errors2 String font_id;
			int font_height;
			int font_vertical_offset;
			String actions;
			ButtonData(Object[]v) throws IllegalArgumentException, IllegalAccessException{element.Element(v,this);}
			ButtonData(){
				/*def_fill=0;def_line_sz=0;def_line=0;ov_fill=0;ov_line_sz=0;ov_line=0;xcurve=0;ycurve=0;*/
				text="";font_id="";/*font_height=0;font_vertical_offset=0;*/
				actions="";
			}
		}
	}
	//if not static then Field[] from getDeclaredFields on previous static will have 3 fields: font.. font_flags and "this"
	public static class Font extends element{
		String fontname;int font_flags;
		Font(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public Font() throws IllegalArgumentException, IllegalAccessException{
			this(new Object[]{"_sans",0,untitled});
		}
	}
	public static class Text extends element{
		public @WidthInt int width;
		public @HeightInt int height;
		String variablename;
		public @TF int flags;
		public EditText structure;
		Text(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		public Text() throws IllegalArgumentException, IllegalAccessException{
			this(new Object[]{100,50,"",0,new EditText(),untitled});
		}
		public static class EditText{
			@NamedId @Errors1 String font_id;
			int font_height;
			String fontclassname;
			int rgba;
			int maxlength;
			public @TEit String initialtext;
			int layout_align;
			int layout_leftmargin;
			int layout_rightmargin;
			int layout_indent;
			int layout_leading;
			//this constructor on the first position, getDeclaredConstructors()[0]
			EditText(Object[]v) throws IllegalArgumentException, IllegalAccessException{element.Element(v,this);}
			EditText(){
				font_id="";/*font_height=0;*/fontclassname="";/*rgba=0;maxlength=0;*/initialtext="";/*layout_align=0;layout_leftmargin=0;layout_rightmargin=0;layout_indent=0;layout_leading=0;*/
			}
		}
	}
	public static class Shape extends element{
		public @WidthInt int width;
		public @HeightInt int height;
		Object[]args;
		Shape(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		static class ShapeWithStyle{
			int fill;Object fill_arg;
			int line_height;int line_color;
			Object[]records;
			ShapeWithStyle(int f,Object f_arg,int lh,int lc,Object[]r){
				fill=f;fill_arg=f_arg;line_height=lh;line_color=lc;records=r;
			}
			ShapeWithStyle(){
				fill=FillStyleType_none;/*line_height=0*/;records=new Object[0];
			}
			static int end_of_values=-1;
			private static boolean phase;
			private static boolean edge;
			private static boolean flags;private static final int StateMoveTo=1;
			private static boolean straight;
			private static boolean cx;private static boolean cy;private static boolean x;//private static boolean y;
			static void phase_start(){phase=false;}
			static boolean phase_get(int val){
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
			Object[]toArray(){
				List<Object>lst=new ArrayList<Object>();
				lst.add(fill);
				if(fill!=FillStyleType_none)lst.add(fill_arg);
				lst.add(line_height);
				if(line_height!=0)lst.add(line_color);
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
		String imagepath;
		DBL(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
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
	public static class SpriteNew extends element{public SpriteNew(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}}
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
