package graphics;

import static actionswf.ActionSwf.HasFont;
import static actionswf.ActionSwf.HasLayout;
import static actionswf.ActionSwf.HasText;
import static actionswf.ActionSwf.HasTextColor;
import static actionswf.ActionSwf.Multiline;
import static actionswf.ActionSwf.ReadOnly;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JRadioButtonMenuItem;

import workspace.InputText;
import workspace.IntInputText;
import workspace.WorkSpace;
import workspace.Elements.Text;

class text{
	text(graphics.character.Character chr){
		try{
			Text t=(Text)chr.element;
			character cr=Graphics.character;
			Panel panel;
			
			panel=cr.new_panel();
			cr.add_one_field(panel,new Label("Text"));
			TextArea tx=new TextArea(t.structure.initialtext);
			tx.addFocusListener(new FocusListener(){
				@Override
				public void focusGained(FocusEvent arg0){}
				@Override
				public void focusLost(FocusEvent arg0){
					String value=tx.getText();
					value=value.replace("\r\n","\n");
					t.structure.initialtext=value;
					flags_set(value,t,HasText);
				}
			});
			WorkSpace.textPopup.add(tx);
			panel.add(tx);
			display.characterData.add(panel);
			
			panel=cr.new_panel();
			
			cr.add_one_field(panel,new Label("Font"));
			InputText txt=new InputText(t.structure.font_id);
			txt.addFocusListener(new FocusListener(){
				@Override
				public void focusGained(FocusEvent e){}
				@Override
				public void focusLost(FocusEvent e){
					String value=txt.getText();
					t.structure.font_id=value;
					flags_set(value,t,HasFont);
				}
			});
			panel.add(txt);
			
			cr.add_one_field(panel,new Label("Height"));
			IntInputText fH=new IntInputText(t.structure.font_height);
			fH.addFocusListener(new FocusListener(){
				@Override
				public void focusGained(FocusEvent e){}
				@Override
				public void focusLost(FocusEvent e){
					fH.focus_Lost();
					int value=Integer.parseInt(fH.getText());
					t.structure.font_height=value;
				}
			});
			panel.add(fH);
			
			int color=t.structure.rgba;
			Color c=new Color(color&(0xff00*0x100*0x100),color&(0xff00*0x100),color&0xff00,color==0?0xff:color&0xff);
			Button new_color_b=new Button();
			new_color_b.setBackground(c);
			JColorChooser colorChooser=new JColorChooser();colorChooser.setColor(c);
			Dialog dialog =JColorChooser.createDialog(
				new_color_b,"Pick a Color",
				true,//modal
				colorChooser,
				new ActionListener(){
					 public void actionPerformed(ActionEvent e){
						Color c=colorChooser.getColor();
						new_color_b.setBackground(c);
						t.structure.rgba=(c.getRed()*0x100*0x100*0x100)|(c.getGreen()*0x100*0x100)|(c.getBlue()*0x100)|c.getAlpha();
						flags_set(t.structure.rgba,t,HasTextColor);
					}
				}
				,null);
			new_color_b.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					dialog.setVisible(true);
				}
			});
			panel.add(new_color_b);
			
			cr.add_one_field(panel,new Label("Align"));
			ButtonGroup group = new ButtonGroup();
			JRadioButtonMenuItem radio;
			ActionListener al=new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					int i=0;
					for(Enumeration<AbstractButton> buttons=group.getElements();buttons.hasMoreElements();) {
						AbstractButton button=buttons.nextElement();
						if(button.isSelected()){
							t.structure.layout_align=i;
							t.flags|=HasLayout;
							break;
						}
						i++;
					}
				}
			};
			radio=new JRadioButtonMenuItem("Left");radio.addActionListener(al);group.add(radio);panel.add(radio);
			radio=new JRadioButtonMenuItem("Right");radio.addActionListener(al);group.add(radio);panel.add(radio);
			radio=new JRadioButtonMenuItem("Center");radio.addActionListener(al);group.add(radio);panel.add(radio);
			radio=new JRadioButtonMenuItem("Justify");radio.addActionListener(al);group.add(radio);panel.add(radio);
			
			display.characterData.add(panel);
			
			panel=cr.new_panel();
			add_flag(panel,t,"Multiline",Multiline);
			add_flag(panel,t,"ReadOnly",ReadOnly);
			display.characterData.add(panel);
		}
		catch (IllegalArgumentException | SecurityException e) {e.printStackTrace();}
	}
	private void flags_set(Object value,Text t,int flag){
		boolean set=true;int flags=t.flags;
		if(value instanceof String)
			{if(((String)value).length()==0)set=false;}
		else/*Integer*/if((int)value==0)set=false;
		if(set==true)flags|=flag;
		else flags&=~flag;
		t.flags=flags;
	}
	private void add_flag(Panel panel,Text t,String nm,int flag){
		JCheckBox chk=new JCheckBox(nm);
		if((t.flags&flag)!=0)chk.setSelected(true);
		chk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(chk.isSelected())t.flags|=flag;
				else t.flags&=~flag;
			}
		});
		panel.add(chk);
	}
}
