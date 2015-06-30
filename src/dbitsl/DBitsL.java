package dbitsl;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

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
					content desktop=new content(img);
					JScrollPane s=new JScrollPane(desktop);
					container.add(s);
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
	private class content extends JComponent{
		private static final long serialVersionUID = 1L;
		private BufferedImage img;
		private content(BufferedImage img){
			setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
			this.img=img;
		}
		@Override
		protected void paintComponent(java.awt.Graphics g){
			g.drawImage(img,0,0,null);
			g.dispose();
		}
	}
}
