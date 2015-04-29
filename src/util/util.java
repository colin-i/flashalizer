package util;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class util {
	public static void message_popup(String text,JComponent c){
		Window topLevelWin = SwingUtilities.getWindowAncestor(c);
		JWindow errorWindow = new JWindow(topLevelWin);
		JPanel contentPane = (JPanel) errorWindow.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		//(String... message)//for(integer i=0;i<message.length;i++){
		contentPane.add(new JLabel(text));
		//}
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		errorWindow.pack();
		
		Point p=MouseInfo.getPointerInfo().getLocation();
		errorWindow.setLocation(p.x,p.y-errorWindow.getHeight());
		errorWindow.setVisible(true);
		
		Timer window_life=new Timer();
		window_life.schedule(new TimerTask(){
			public void run(){
				errorWindow.dispose();
			}
		},1000);
	}
}
