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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
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
		popup(text,errorWindow,0);
		errorWindow.setVisible(true);
		Timer window_life=new Timer();
		window_life.schedule(new TimerTask(){
			public void run(){
				errorWindow.dispose();
			}
		},1000);
	}
	public static void popup(String text,JWindow errorWindow,int offset){
		JPanel contentPane = (JPanel) errorWindow.getContentPane();
		int i=0;
		contentPane.add(new JLabel(text),i);
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		errorWindow.pack();
		Point p=MouseInfo.getPointerInfo().getLocation();
		errorWindow.setLocation(p.x,p.y-errorWindow.getHeight()-offset);
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
			d.revalidate();//for dbl scrolls
			d.repaint();//for dbl repaint when image is small(no scrolls)
		}
	}
	public interface ItRunnable{public void run(ItemEvent e);}
	public static class ItListener implements ItemListener{
		public ItListener(Component destDraw,ItRunnable run){
			d=destDraw;r=run;
		}
		private ItRunnable r;private Component d;
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			r.run(arg0);
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
			d.revalidate();//dbl, talked already about this
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
	public interface MsEvBRunnable extends MsEvRunnable{public boolean run(MouseEvent e);}
	public interface MsEvVRunnable extends MsEvRunnable{public void run(MouseEvent e);}
	public interface MsEvRunnable{}
	public static class MsMotListener implements MouseMotionListener{
		public MsMotListener(Component destDraw,MsEvBRunnable msEvRunnable){
			d=destDraw;r=msEvRunnable;
		}
		public MsMotListener(Component destDraw,MsEvVRunnable msEvRunnable){
			d=destDraw;r=msEvRunnable;
		}
		private MsEvRunnable r;private Component d;
		@Override
		public void mouseDragged(MouseEvent e){
			if(r instanceof MsEvBRunnable){if(((MsEvBRunnable)r).run(e)==false)return;}
			else ((MsEvVRunnable)r).run(e);
			d.repaint();
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
	public static class MsMoveListener implements MouseMotionListener{
		private MsEvVRunnable r;private Component d;
		public MsMoveListener(Component destDraw,MsEvVRunnable Runnable){
			d=destDraw;r=Runnable;
		}
		@Override
		public void mouseDragged(MouseEvent e) {}
		@Override
		public void mouseMoved(MouseEvent e) {
			r.run(e);d.repaint();
		}
	}
	public static class MsOutListener implements MouseListener{
		public MsOutListener(Component destDraw,MsEvVRunnable msEvRunnable){
			d=destDraw;r=msEvRunnable;
		}
		private MsEvVRunnable r;private Component d;
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {
			r.run(e);d.repaint();
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
	}
}
