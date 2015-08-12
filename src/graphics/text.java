package graphics;

import static actionswf.ActionSwf.HasFont;
import static actionswf.ActionSwf.HasMaxLength;
import static actionswf.ActionSwf.HasTextColor;
import static actionswf.ActionSwf.ReadOnly;
import static actionswf.ActionSwf.Password;
import static actionswf.ActionSwf.Multiline;
import static actionswf.ActionSwf.WordWrap;
import static actionswf.ActionSwf.HasText;
import static actionswf.ActionSwf.HTML;
import static actionswf.ActionSwf.Border;
import static actionswf.ActionSwf.NoSelect;
import static actionswf.ActionSwf.HasLayout;
import static actionswf.ActionSwf.AutoSize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import workspace.InputText;
import workspace.IntInputText;
import workspace.AreaInputText;
import workspace.Elements.Text;

class text{
	text(graphics.character.Character chr){
		try{
			Text t=(Text)chr.element;
			character cr=Graphics.character;
			JPanel panel;
			
			panel=cr.new_panel();
			cr.add_one_field(panel,new JLabel("VariableName"));
			InputText var=new InputText(t.variablename);
			var.addFocusListener(new FocusListener(){
				@Override
				public void focusGained(FocusEvent e){}
				@Override
				public void focusLost(FocusEvent e){
					t.variablename=var.getText();
				}
			});
			panel.add(var);
			Graphics.characterData.add(panel);
			//
			panel=new JPanel();panel.setLayout(new BorderLayout());
			panel.add(new JLabel("Text"),BorderLayout.WEST);
			AreaInputText tx=new AreaInputText(t.structure.initialtext);tx.setRows(5);
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
			JScrollPane sc=new JScrollPane(tx);panel.add(sc);
			Graphics.characterData.add(panel);
			//
			panel=cr.new_panel();
			cr.add_one_field(panel,new JLabel("Font"));
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
			cr.add_one_field(panel,new JLabel("Height"));
			IntInputText fH=new IntInputText(t.structure.font_height);
			fH.addFocusListener(new FocusListener(){
				@Override
				public void focusGained(FocusEvent e){}
				@Override
				public void focusLost(FocusEvent e){
					fH.focus_Lost();
					int value=(Long.decode(fH.getText())).intValue();
					t.structure.font_height=value;
				}
			});
			panel.add(fH);
			int color=t.structure.rgba;
			Color c=cr.rgba2color(color);
			JButton new_color_b=new JButton();
			new_color_b.setBackground(c);
			JColorChooser colorChooser=new JColorChooser();colorChooser.setColor(c);
			JDialog dialog =JColorChooser.createDialog(
				new_color_b,"Pick a Color",
				true,//modal
				colorChooser,
				new ActionListener(){
					 public void actionPerformed(ActionEvent e){
						Color c=colorChooser.getColor();
						new_color_b.setBackground(c);
						t.structure.rgba=cr.color2rgba(c);
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
			cr.add_one_field(panel,new JLabel("MaxLength"));
			IntInputText fL=new IntInputText(t.structure.maxlength);
			fL.addFocusListener(new FocusListener(){
				@Override
				public void focusGained(FocusEvent e){}
				@Override
				public void focusLost(FocusEvent e){
					fL.focus_Lost();
					int value=(Long.decode(fL.getText())).intValue();
					t.structure.maxlength=value;
					flags_set(value,t,HasMaxLength);
				}
			});
			panel.add(fL);
			Graphics.characterData.add(panel);
			
			panel=cr.new_panel();
			add_flag(panel,t,"Multiline",Multiline);
			add_flag(panel,t,"ReadOnly",ReadOnly);
			add_flag(panel,t,"NoSelect",NoSelect);
			add_flag(panel,t,"HTML",HTML);
			add_flag(panel,t,"Border",Border);
			add_flag(panel,t,"WordWrap",WordWrap);
			add_flag(panel,t,"Password",Password);
			add_flag(panel,t,"AutoSize",AutoSize);
			Graphics.characterData.add(panel);
			
			panel=cr.new_panel();
			cr.add_one_field(panel,new JLabel("Align"));
			ButtonGroup group = new ButtonGroup();
			JRadioButton radio;
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
			radio=new JRadioButton("Left");radio.addActionListener(al);group.add(radio);panel.add(radio);
			radio=new JRadioButton("Right");radio.addActionListener(al);group.add(radio);panel.add(radio);
			radio=new JRadioButton("Center");radio.addActionListener(al);group.add(radio);panel.add(radio);
			radio=new JRadioButton("Justify");radio.addActionListener(al);group.add(radio);panel.add(radio);
			cr.add_separator(panel);panel.add(new JLabel("Margin"));
			new TextLayoutIntInputText(
				t.structure.layout_leftmargin,t,
				new text_Runnable(){@Override public void run(Text t, int value) {t.structure.layout_leftmargin=value;}}
				,panel
				," Left"
			);
			new TextLayoutIntInputText(
				t.structure.layout_rightmargin,t,
				new text_Runnable(){@Override public void run(Text t, int value) {t.structure.layout_rightmargin=value;}}
				,panel
				," Right"
			);
			cr.add_separator(panel);
			new TextLayoutIntInputText(
				t.structure.layout_indent,t,
				new text_Runnable(){@Override public void run(Text t, int value) {t.structure.layout_indent=value;}}
				,panel
				,"Indent"
			);
			cr.add_separator(panel);
			new TextLayoutIntInputText(
				t.structure.layout_leading,t,
				new text_Runnable(){@Override public void run(Text t, int value) {t.structure.layout_leading=value;}}
				,panel
				,"Leading"
			);
			Graphics.characterData.add(panel);
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
	private void add_flag(JPanel panel,Text t,String nm,int flag){
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
	private interface text_Runnable{
		void run(Text t,int value);
	}
	private class TextLayoutIntInputText extends IntInputText{
		private static final long serialVersionUID = 1L;
		private TextLayoutIntInputText(int i,Text t,text_Runnable r,JPanel p,String n){
			super(i);IntInputText ths=this;
			addFocusListener(new FocusListener(){
				@Override
				public void focusGained(FocusEvent e){}
				@Override
				public void focusLost(FocusEvent e){
					ths.focus_Lost();
					int value=(Long.decode(ths.getText())).intValue();
					r.run(t,value);
					t.flags|=HasLayout;
				}
			});
			p.add(new JLabel(n));
			p.add(this);
		}
	}
}
