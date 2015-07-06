package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;

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
	//function+drawer
	public static class TableEx extends JTable{
		private static final long serialVersionUID = 1L;
		public void setValueAtEx(Object aValue, int row, int column){
			super.setValueAt(aValue,row,column);
			DefaultTableModel m=(DefaultTableModel)getModel();
			m.fireTableCellUpdated(row,column);
		}
	}
	public static class PanelEx extends JPanel{
		private static final long serialVersionUID = 1L;
		public void removeAll(){
			super.removeAll();
			getParent().validate();
		}
		public Component add(Component comp){
			super.add(comp);
			getParent().validate();
			return comp;
		}
		public Component add(Component comp,int index){
			super.add(comp,index);
			revalidate();
			return comp;
		}
	}
	public static class ButtonEx extends JButton{
		private static final long serialVersionUID = 1L;
		public void setLocation(int x,int y){
			super.setLocation(x,y);
			getParent().repaint();
		}
	}
	public static class ComponentEx extends JComponent{
		private static final long serialVersionUID = 1L;
		public Component add(Component comp,int index){
			super.add(comp,index);
			repaint();
			return comp;
		}
		public void remove(Component comp){
			super.remove(comp);
			repaint();
		}
	}
	//
	public static class TreeSelListener implements TreeSelectionListener{
		public TreeSelListener(Component destDraw,Runnable run){
			d=destDraw;r=run;
		}
		private Runnable r;private Component d;
		@Override
		public void valueChanged(TreeSelectionEvent e){
			r.run();
			d.repaint();
		}
	}
	public static class ChListener implements ChangeListener{
		public ChListener(Component destDraw,Runnable run){
			d=destDraw;r=run;
		}
		private Runnable r;private Component d;
		@Override
		public void stateChanged(ChangeEvent arg0) {
			r.run();
			d.repaint();
		}
	}
	public static class AcListener implements ActionListener{
		public AcListener(Component destDraw,Runnable run){
			d=destDraw;r=run;
		}
		private Runnable r;private Component d;
		@Override
		public void actionPerformed(ActionEvent e) {
			r.run();
			d.repaint();
		}
	}
	public static class FocListener implements FocusListener{
		@Override
		public void focusGained(FocusEvent e) {}
		@Override
		public void focusLost(FocusEvent e) {
			r.run();
			d.repaint();
		}
		public FocListener(Component destDraw,Runnable run){
			d=destDraw;r=run;
		}
		private Runnable r;private Component d;
	}
	public interface MsEvBRunnable{public boolean run(MouseEvent e);}
	public static class MsMotListener implements MouseMotionListener{
		public MsMotListener(Component destDraw,MsEvBRunnable msEvRunnable){
			d=destDraw;r=msEvRunnable;
		}
		private MsEvBRunnable r;private Component d;
		@Override
		public void mouseDragged(MouseEvent e){
			r.run(e);d.repaint();
		}
		@Override
		public void mouseMoved(MouseEvent e){}
	}
	public static class MsListener implements MouseListener{
		public MsListener(Component destDraw,MsEvBRunnable msEvRunnable){
			d=destDraw;r=msEvRunnable;
		}
		private MsEvBRunnable r;private Component d;
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {
			if(r.run(e))d.repaint();
		}
		@Override
		public void mouseReleased(MouseEvent e) {}
	}
	//
}
