package graphics;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import workspace.Elements.Button;
import workspace.Elements.Button.ButtonData;
import workspace.InputText;
import workspace.AreaInputText;
import static graphics.Graphics.characterData;

class button {
	private JPanel subpanel;
	button(graphics.character.Character chr){
		character cr=Graphics.character;
		Button bt=(Button)chr.element;
		ButtonData data=bt.structure;
		JPanel panel;
		
		panel=cr.new_panel();
		subpanel=cr.new_panel();
		subpanel.setBorder(BorderFactory.createTitledBorder("Normal"));
		subpanel.add(color_chooser(data.def_fill,new button_Runnable_int(){@Override public void run(int c){data.def_fill=c;}}));
		line(new ButtonInputText(data.def_line_sz,new button_Runnable_int(){@Override public void run(int val){data.def_line_sz=val;}}),
			color_chooser(data.def_line,new button_Runnable_int(){@Override public void run(int c){data.def_line=c;}}));
		panel.add(subpanel);
		subpanel=cr.new_panel();
		subpanel.setBorder(BorderFactory.createTitledBorder("Over"));
		subpanel.add(color_chooser(data.ov_fill,new button_Runnable_int(){@Override public void run(int c){data.ov_fill=c;}}));
		line(new ButtonInputText(data.ov_line_sz,new button_Runnable_int(){@Override public void run(int val){data.ov_line_sz=val;}}),
			color_chooser(data.ov_line,new button_Runnable_int(){@Override public void run(int c){data.ov_line=c;}}));
		panel.add(subpanel);
		subpanel=cr.new_panel();
		subpanel.setBorder(BorderFactory.createTitledBorder("Down"));
		subpanel.add(color_chooser(data.dn_fill,new button_Runnable_int(){@Override public void run(int c){data.dn_fill=c;}}));
		line(new ButtonInputText(data.dn_line_sz,new button_Runnable_int(){@Override public void run(int val){data.dn_line_sz=val;}}),
			color_chooser(data.dn_line,new button_Runnable_int(){@Override public void run(int c){data.dn_line=c;}}));
		panel.add(subpanel);
		characterData.add(panel);
		
		panel=cr.new_panel();
		panel.setBorder(BorderFactory.createTitledBorder("Curve"));
		panel.add(new JLabel("X"));
		panel.add(new ButtonInputText(data.xcurve,new button_Runnable_int(){@Override public void run(int val){data.xcurve=val;}}));
		panel.add(new JLabel("Y"));
		panel.add(new ButtonInputText(data.ycurve,new button_Runnable_int(){@Override public void run(int val){data.ycurve=val;}}));
		characterData.add(panel);
		
		panel=cr.new_panel();panel.setBorder(BorderFactory.createTitledBorder("Label"));
		panel.add(new ButtonInputText(data.text,new button_Runnable_String(){@Override public void run(String val){data.text=val;}}));
		cr.add_one_field(panel,new JLabel("Font"));panel.add(new ButtonInputText(data.font_id,new button_Runnable_String(){@Override public void run(String val){data.font_id=val;}}));
		cr.add_one_field(panel,new JLabel("Height"));panel.add(new ButtonInputText(data.font_height,new button_Runnable_int(){@Override public void run(int val){data.font_height=val;}}));
		cr.add_one_field(panel,new JLabel("Y Offset"));panel.add(new ButtonInputText(data.font_vertical_offset,new button_Runnable_int(){@Override public void run(int val){data.font_vertical_offset=val;}}));
		cr.add_one_field(panel,color_chooser(data.font_color,new button_Runnable_int(){@Override public void run(int c){data.font_color=c;}}));
		characterData.add(panel);
		
		panel=cr.new_panel();panel.setBorder(BorderFactory.createTitledBorder("Action"));
		AreaInputText tx=new AreaInputText(data.actions);tx.setRows(5);
		tx.addFocusListener(new FocusListener(){
			@Override public void focusGained(FocusEvent arg0){}
			@Override public void focusLost(FocusEvent arg0){data.actions=tx.getText();}
		});
		JScrollPane sc=new JScrollPane(tx);panel.add(sc);
		characterData.add(panel);
	}
	private void line(ButtonInputText sz,JButton clr){
		JPanel pan=new JPanel();pan.setLayout(new BoxLayout(pan,BoxLayout.X_AXIS));
		pan.setBorder(BorderFactory.createTitledBorder("Line"));
		pan.add(new JLabel("Size"));pan.add(sz);pan.add(clr);
		subpanel.add(pan);
	}
	private class ButtonInputText extends InputText implements FocusListener{
		private static final long serialVersionUID = 1L;
		private button_Runnable run;
		private ButtonInputText(Object in,button_Runnable r){
			super(in);run=r;
			addFocusListener(this);
		}
		@Override public void focusGained(FocusEvent arg0){}
		@Override
		public void focusLost(FocusEvent arg0){
			String txt=getText();
			if(run instanceof button_Runnable_int){
				super.focus_Lost();
				((button_Runnable_int)run).run((Long.decode(txt)).intValue());
			}else/*String*/{
				((button_Runnable_String)run).run(txt);
			}
		}
	}
	private interface button_Runnable{}
	private interface button_Runnable_int extends button_Runnable{
		void run(int val);
	}
	private interface button_Runnable_String extends button_Runnable{
		void run(String val);
	}
	private JButton color_chooser(int rgba,button_Runnable b){
		JColorChooser chooser;Color c;JButton bt;
		c=Graphics.character.rgba2color(rgba);
		bt=new JButton();bt.setBackground(c);
		chooser=new JColorChooser();chooser.setColor(c);
		JDialog dialog =JColorChooser.createDialog(
			bt,"Pick a Color",
			true,//modal
			chooser,
			new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					Color c=chooser.getColor();
					bt.setBackground(c);
					((button_Runnable_int)b).run(Graphics.character.color2rgba(c));
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
}
