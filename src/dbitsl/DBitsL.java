package dbitsl;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class DBitsL {
	public DBitsL(String src,Component c){
		try {
			String n=ManagementFactory.getRuntimeMXBean().getName();
			String dest="tmp/"+n.split("@")[0]+".png";
			Path destPat=Paths.get(dest);
			Runtime runtime = Runtime.getRuntime();
			Process shellProcess=runtime.exec("dbl2png.exe \""+src+"\" "+dest);
			shellProcess.waitFor();
			File f=new File(dest);
			BufferedImage img=ImageIO.read(f);
			Files.delete(destPat);
			
			JDialog dg=new JDialog(SwingUtilities.getWindowAncestor(c),"DBL",JDialog.ModalityType.DOCUMENT_MODAL);
			Container container=dg.getContentPane();
			//container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
			content desktop=new content(img);
			JScrollPane s=new JScrollPane(desktop);
			container.add(s);
			dg.pack();
			dg.setVisible(true);
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
