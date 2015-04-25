package graphics;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import workspace.InputText;
import workspace.WorkSpace;
import workspace.Elements.Shape;
import workspace.Elements.Shape.ShapeWithStyle;
import static actionswf.ActionSwf.FillStyleType_none;
import static actionswf.ActionSwf.solid_fill;
import static actionswf.ActionSwf.repeating_bitmap_fill;
import static graphics.Graphics.characterData;
import graphics.character.Character;

class shape {
	private Character chr;
	private ButtonGroup group;
	private Object[]records;
	private JButton fill_color;private ShapeInputText image_id;
	private ShapeInputText line_sz;private JButton image_clr;
	shape(Character c){
		chr=c;
		ShapeWithStyle shp=new ShapeWithStyle(((Shape)chr.element).args);
		character cr=Graphics.character;
		records=shp.records;
		
		JPanel panel=cr.new_panel();
		JPanel subpanel;
		ActionListener acl=new ActionListener(){
			 public void actionPerformed(ActionEvent e){
				update_records();
			}
		};
		
		group=new ButtonGroup();
		JRadioButton rd1;JRadioButton rd2;JRadioButton rd3;
		subpanel=cr.new_panel();
		subpanel.setBorder(BorderFactory.createTitledBorder("Fill"));
		rd1=new JRadioButton("None");rd2=new JRadioButton("Solid");rd3=new JRadioButton("Image");group.add(rd1);group.add(rd2);group.add(rd3);rd1.addActionListener(acl);rd2.addActionListener(acl);rd3.addActionListener(acl);
		int color=0xff;
		String img_id="";
		if(shp.fill!=FillStyleType_none){
			boolean is_bitmap=WorkSpace.project.isShapeBitmap(shp.fill);
			if(is_bitmap==false){color=(int)shp.fill_arg;rd2.setSelected(true);}
			else{img_id=(String)shp.fill_arg;rd3.setSelected(true);}
		}else rd1.setSelected(true);
		subpanel.add(rd1);
		subpanel.add(rd2);fill_color=color_chooser(color);subpanel.add(fill_color);
		subpanel.add(rd3);image_id=new ShapeInputText(img_id);subpanel.add(image_id);
		panel.add(subpanel);
		
		subpanel=cr.new_panel();
		subpanel.setBorder(BorderFactory.createTitledBorder("Line"));
		subpanel.add(new JLabel("Size"));line_sz=new ShapeInputText(shp.line_size);subpanel.add(line_sz);
		image_clr=color_chooser(shp.line_color);subpanel.add(image_clr);
		panel.add(subpanel);
		
		characterData.add(panel);
	}
	private class ShapeInputText extends InputText implements FocusListener{
		private static final long serialVersionUID = 1L;
		private boolean isInteger;
		private ShapeInputText(Object in){
			super(in);isInteger=in instanceof Integer;addFocusListener(this);
		}
		@Override public void focusGained(FocusEvent arg0){}
		@Override
		public void focusLost(FocusEvent arg0){
			if(isInteger)super.focus_Lost();
			update_records();
		}
	}
	private JButton color_chooser(int rgba/*,button_Runnable b*/){
		JColorChooser chooser;Color c;JButton bt;
		c=Graphics.character.rgba2color(rgba);
		bt=new JButton();bt.setBackground(c);
		chooser=new JColorChooser();chooser.setColor(c);
		Dialog dialog =JColorChooser.createDialog(
			bt,"Pick a Color",
			true,//modal
			chooser,
			new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					Color c=chooser.getColor();
					bt.setBackground(c);
					update_records();
				}
			}
			,null
		);
		bt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(true);
			}
		});
		return bt;
	}
	private void update_records(){
		int fill;Object fill_arg = null;
		int line_size;int line_color;
		int i=0;
		for(Enumeration<AbstractButton> buttons=group.getElements();buttons.hasMoreElements();) {
			AbstractButton button=buttons.nextElement();
			if(button.isSelected())break;
			i++;
		}
		if(i==0)fill=FillStyleType_none;
		else if(i==1){fill=solid_fill;fill_arg=Graphics.character.color2rgba(fill_color.getBackground());}
		else/**/{fill=repeating_bitmap_fill;fill_arg=image_id.getText();}
		line_size=Long.decode(line_sz.getText()).intValue();
		line_color=Graphics.character.color2rgba(image_clr.getBackground());
		
		((Shape)chr.element).args=new ShapeWithStyle(fill,fill_arg,line_size,line_color,records).toArray();
	}
}
