package graphics;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import workspace.Elements.Button;
import workspace.Elements.Button.ButtonData;
import static graphics.Graphics.characterData;

class button {
	private JPanel panel;
	button(graphics.character.Character chr){
		character cr=Graphics.character;
		Button bt=(Button)chr.element;
		ButtonData data=bt.structure;
		
		panel=cr.new_panel();
		panel.setBorder(BorderFactory.createTitledBorder("Normal"));
		color_chooser(data.def_fill,new button_Runnable(){@Override public void run(Color c){data.def_fill=Graphics.character.color2rgba(c);}});
		characterData.add(panel);
	}
	private interface button_Runnable{
		void run(Color c);
	}
	private void color_chooser(int rgba,button_Runnable b){
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
					b.run(c);
				}
			}
			,null
		);
		bt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(true);
			}
		});
		panel.add(bt);
	}
}
