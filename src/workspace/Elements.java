package workspace;

import java.util.ArrayList;
import java.util.List;

import static actionswf.ActionSwf.FillStyleType_none;

public class Elements {
	static class Button extends element{
		ButtonData structure;
		Button(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		static class ButtonData{
			int width;
			int height;
			int def_fill;
			int def_line_h;
			int def_line;
			int ov_fill;
			int ov_line_h;
			int ov_line;
			int xcurve;
			int ycurve;
			String text;
			@NamedId String font_id;
			int font_height;
			int font_vertical_offset;
			String actions;
			ButtonData(Object[]v) throws IllegalArgumentException, IllegalAccessException{element.Element(v,this);}
			ButtonData(){
				width=0;height=0;def_fill=0;def_line_h=0;def_line=0;ov_fill=0;ov_line_h=0;ov_line=0;xcurve=0;ycurve=0;
				text="";font_id="";font_height=0;font_vertical_offset=0;
				actions="";
			}
		}
	}
	//if not static then Field[] from getDeclaredFields on previous static will have 3 fields: font.. font_flags and "this"
	static class Font extends element{
		String fontname;int font_flags;
		Font(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class Text extends element{
		int bound_width;
		int bound_height;
		String variablename;
		int flags;
		EditText structure;
		Text(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
		static class EditText{
			@NamedId String font_id;
			int font_height;
			String fontclassname;
			int rgba;
			int maxlength;
			String initialtext;
			int layout_align;
			int layout_leftmargin;
			int layout_rightmargin;
			int layout_indent;
			int layout_leading;
			//this constructor on the first position, getDeclaredConstructors()[0]
			EditText(Object[]v) throws IllegalArgumentException, IllegalAccessException{element.Element(v,this);}
			EditText(){
				font_id="";font_height=0;fontclassname="";rgba=0;maxlength=0;initialtext="";layout_align=0;layout_leftmargin=0;layout_rightmargin=0;layout_indent=0;layout_leading=0;
			}
		}
	}
	static class Shape extends element{
		int width;
		int height;
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
				fill=FillStyleType_none;line_height=0;records=new Object[0];
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
	static class Image extends element{
		String imagepath;
		Image(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class DBL extends element{
		String imagepath;
		DBL(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	
	static class Placement extends element{
		String refid;int depth;
		Placement(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class PlacementCoords extends element{
		String refid;int depth;int x;int y;
		PlacementCoords(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class Remove extends element{
		int depth;
		Remove(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class ShowFrame{ShowFrame(Object[]x){};ShowFrame(){};}
	
	static class SpriteDone extends element{
		String spriteid;
		SpriteDone(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class SpriteNew extends element{SpriteNew(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}}
	static class SpritePlacement extends element{
		String spriteid;String refid;int depth;
		SpritePlacement(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class SpritePlacementCoords extends element{
		String spriteid;String refid;int depth;int x;int y;
		SpritePlacementCoords(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class SpriteRemove extends element{
		String spriteid;int depth;
		SpriteRemove(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class SpriteShowFrame extends element{
		String spriteid;
		SpriteShowFrame(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	
	static class ExportsAdd extends element{String refid;String name;ExportsAdd(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}}
	static class ExportsDone{ExportsDone(Object[]v){}}
	
	static class Action extends element{
		String ac;
		Action(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
	static class ActionSprite extends element{
		String spriteid;String ac;
		ActionSprite(Object[]x)throws IllegalArgumentException,IllegalAccessException{super(x);}
	}
}
