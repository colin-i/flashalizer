package dbitsl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import util.util.ChListener;
import util.util.MsMotListener;
import util.util.MsListener;
import util.util.MsEvBRunnable;

public class DBitsL {
	private BufferedImage img;
	public DBitsL(String src,Component c){
		try {
			if(src.length()>0){
				File src_file=new File(src);
				String n=ManagementFactory.getRuntimeMXBean().getName();
				String dest="tmp/"+n.split("@")[0]+".png";Path destPat=Paths.get(dest);
				if(src_file.isFile()){
					Runtime runtime = Runtime.getRuntime();
					Process shellProcess=runtime.exec("dbl2png.exe \""+src+"\" "+dest);
					shellProcess.waitFor();
					File f=new File(dest);
					img=ImageIO.read(f);
					Files.delete(destPat);
				}else if(src_file.exists()==false)img=new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
				//else directory or not a normal file
				if(img!=null){
					JDialog dg=new JDialog(SwingUtilities.getWindowAncestor(c),"DBL",JDialog.ModalityType.DOCUMENT_MODAL);
					Container container=dg.getContentPane();
					container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
					drawArea=new content(img);
					//
					JPanel p=new JPanel();p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
					p.add(new JLabel("Zoom"));
					p.add(new slider());
					container.add(p);
					//
					JPanel pan=new JPanel();pan.setLayout(new BorderLayout());
					pan.add(new Tools(drawArea),BorderLayout.WEST);
					scrollArea=new JScrollPane(drawArea);
					pan.add(scrollArea);
					container.add(pan);
					//
					JButton save=new JButton("Save");
					save.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent arg0) {
							try {
								ImageIO.write(img,"png",new File(dest));
								Runtime runtime = Runtime.getRuntime();
								Process shellProcess = runtime.exec("png2dbl.exe "+dest+" \""+src+"\"");
								shellProcess.waitFor();
								Files.delete(destPat);
								dg.dispose();
							} catch (IOException | InterruptedException e) {e.printStackTrace();}
						}});
					container.add(save);
					dg.pack();
					dg.setVisible(true);
				}
			}
		} catch (IOException | InterruptedException e){e.printStackTrace();}
	}
	private content drawArea;private JScrollPane scrollArea;
	static class content extends JComponent{
		private static final long serialVersionUID = 1L;
		BufferedImage img;
		private content(BufferedImage img){
			setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
			this.img=img;
			addMouseMotionListener(new imgMsMotListener(this,new MsEvBRunnable(){
				@Override
				public boolean run(MouseEvent e) {
					return Tools.drag(e);
				}
			}));
			addMouseListener(new imgMsListener(this,new MsEvBRunnable(){
				@Override
				public boolean run(MouseEvent e) {
					return Tools.hit(e);
				}
			}));
		}
		@Override
		protected void paintComponent(java.awt.Graphics g){
			g.drawImage(img,0,0,img.getWidth()*zoom_level,img.getHeight()*zoom_level,null);//img.getScaledInstance,AffineTransform
			g.dispose();
		}
	}
	private static class imgMsMotListener extends MsMotListener{
		private imgMsMotListener(Component destDraw, MsEvBRunnable msEvRunnable){super(destDraw, msEvRunnable);}
		@Override
		public void mouseDragged(MouseEvent e){
			Tools.ease(e);
			super.mouseDragged(e);
		}
		@Override
		public void mouseMoved(MouseEvent e){Tools.ease(e);}
	}
	private static class imgMsListener extends MsListener{
		private imgMsListener(Component destDraw, MsEvBRunnable msEvRunnable){super(destDraw, msEvRunnable);}
		@Override public void mouseExited(MouseEvent e){Tools.easeOff();}
	}
	static int zoom_level=1;
	private class slider extends JSlider{
		private static final long serialVersionUID = 1L;
		private static final int min=1;private static final int max=16;
		private slider(){
			super(min,max,1);
			setMajorTickSpacing(1);//This method will also set up a label table
			setPaintTicks(true);//By default, this property is false
			setPaintLabels(true);//By default, this property is false
			addChangeListener(new ChListener(drawArea,new Runnable(){
				@Override
				public void run() {
					if(!getValueIsAdjusting()){
						JViewport v=scrollArea.getViewport();
						Point p=v.getViewPosition();
						int orig_x=p.x/zoom_level;int orig_y=p.y/zoom_level;
						zoom_level=getValue();
						drawArea.setPreferredSize(new Dimension(img.getWidth()*zoom_level,img.getHeight()*zoom_level));
						v.setViewPosition(new Point(orig_x*zoom_level,orig_y*zoom_level));
					}
				}
			}));
			int n=max-min;Dimension dim=new Dimension();dim.width=20*n;dim.height=50;setPreferredSize(dim);
		}
	}
}
