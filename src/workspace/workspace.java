package workspace;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;


public class workspace {
	private Preferences prefs=Preferences.userRoot().node(this.getClass().getName());
	private class prop{
		private Label label;
		private String name;
		private prop(String n,String init_value){
			name=n;
			label=new Label();
			String x=prefs.get(name,null);
			if(x==null)set(init_value);
			else label.setText(x);
		}
		private void set(String value){
			label.setText(value);
			prefs.put(name,value);
		}
		private String get(){
			return label.getText();
		}
	}
	private interface JFileChooser_run{
		void Run(JFileChooser c);
	}
	private class menu extends MenuBar{
		private static final long serialVersionUID = 1L;
		private void choose(String title,JFileChooser_run r){
			JFileChooser chooser=new JFileChooser();
			chooser.setDialogTitle(title);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setCurrentDirectory(new File(workpath.get()));
			if(chooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION)r.Run(chooser);
		}
		private class file extends Menu{
			private static final long serialVersionUID = 1L;
			private file(MenuBar menu){
				super("File");
				newp();open();save();properties();exit();
				menu.add(this);
			}
			private void newp(){
				String new_f="New";
				MenuItem newp=new MenuItem(new_f);
				//new dialog
				JDialog dg=new JDialog(frame,new_f,Dialog.ModalityType.DOCUMENT_MODAL);
				Container ctnr=dg.getContentPane();
				ctnr.setLayout(new BoxLayout(ctnr,BoxLayout.Y_AXIS));
				//
				Container ct=new Container();ct.setLayout(new GridLayout(0,2));
				//container
				ct.add(new Label("Name"));
				InputText new_name=new InputText("Untitled");
				ct.add(new_name);
				//
				ct.add(new Label("Width"));
				InputText new_width=new IntInputText(project.width_default);
				ct.add(new_width);
				//
				ct.add(new Label("Height"));
				InputText new_height=new IntInputText(project.height_default);
				ct.add(new_height);
				//
				ct.add(new Label("Background Color"));
				Color new_color=new Color(project.backgroundcolor_default);
				Button new_color_b=new Button(null);
				new_color_b.setBackground(new_color);
				JColorChooser colorChooser = new JColorChooser();
				Dialog dialog =JColorChooser.createDialog(
					new_color_b,"Pick a Color",
					true,//modal
					colorChooser,
					new ActionListener(){
						 public void actionPerformed(ActionEvent e){
							new_color_b.setBackground(colorChooser.getColor());
						}
					},null);
				new_color_b.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						dialog.setVisible(true);
					}
				});
				ct.add(new_color_b);
				//
				ct.add(new Label("FPS"));
				InputText new_fps=new IntInputText(project.fps_default);
				ct.add(new_fps);
				//
				ctnr.add(ct);
				//x
				Button btn=new Button("OK");
				btn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if(project.folder_set(workpath.get()+"/"+new_name.getText(),false)){
							project.newproj(Long.decode(new_width.getText()).intValue(),Long.decode(new_height.getText()).intValue(),colorChooser.getColor().getRGB()&0xffFFff,Long.decode(new_fps.getText()).intValue());
							dg.setVisible(false);
							redrawFuncPane();
						}
					}
				});
				ctnr.add(btn);
				//rest
				dg.pack();
				//new rest
				newp.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						dg.setVisible(true);
					}
				});
				add(newp);
			}
			private void open(){
				MenuItem open=new MenuItem("Open");
				open.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						choose("Open Project",new JFileChooser_run(){public void Run(JFileChooser c){
							project.folder_set(c.getSelectedFile().toString(),true);
							redrawFuncPane();
						}});
					}
				});
				add(open);
			}
			private void save(){
				MenuItem save=new MenuItem("Save");
				save.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						project.save();
					}
				});
				add(save);
			}
			private void properties(){
				//properties
				String prop="Properties";
				MenuItem properties=new MenuItem(prop);
				JDialog dl=new JDialog(frame,prop,Dialog.ModalityType.DOCUMENT_MODAL);
				Container cnt=dl.getContentPane();
				cnt.setLayout(new BoxLayout(cnt,BoxLayout.Y_AXIS));
				//container
				Container cnt1=new Container();
				cnt1.setLayout(new GridLayout(0,2));
				//workspace path
				String choosertitle="Workspace Path";
				Button workpath_browse=new Button(choosertitle);
				workpath_browse.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						choose(choosertitle,new JFileChooser_run(){public void Run(JFileChooser c){
							workpath.set(c.getSelectedFile().toString());
						}});
					}
				});
				cnt1.add(workpath_browse);
				cnt1.add(workpath.label);
				//add
				cnt.add(cnt1);
				//OK
				Button bt=new Button("OK");
				bt.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						dl.setVisible(false);
					}
				});
				cnt.add(bt);
				//rest
				dl.pack();
				//file_prop
				properties.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						dl.setVisible(true);
					}
				});
				add(properties);
			}
			private void exit(){
				MenuItem exit=new MenuItem("Exit");
				exit.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				add(exit);
			}
		}
		private class proj extends Menu{
			private static final long serialVersionUID = 1L;
			private proj(MenuBar menu){
				super("Project");
				build();
				menu.add(this);
			}
			private void build(){
				MenuItem build=new MenuItem("Build");
				build.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						project.build();
					}
				});
				add(build);
			}
		}
		private class help extends Menu{
			private static final long serialVersionUID = 1L;
			private help(MenuBar menu){
				super("Help");
				about();
				menu.add(this);
			}
			private void about(){
				String abt="About";
				MenuItem about=new MenuItem(abt);
				
				//about dialog
				JDialog d=new JDialog(frame,abt,Dialog.ModalityType.DOCUMENT_MODAL);
				Container cp=d.getContentPane();
				cp.setLayout(new BoxLayout(cp,BoxLayout.Y_AXIS));
				//container1
				Container cp1=new Container();
				cp1.setLayout(new FlowLayout());
				cp1.add(new JLabel(img));
				Label l=new Label("Version 1");
				cp1.add(l);
				cp.add(cp1);
				//2
				Button b=new Button("OK");
				b.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						d.setVisible(false);
					}
				});
				cp.add(b);
				//rest
				d.pack();
				
				//about rest
				about.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						d.setVisible(true);
					}
				});
				
				add(about);
			}
		}
		private menu(){
			new file(this);
			new proj(this);
			new help(this);
		}
	}
	private void redrawFuncPane(){
		Functions.container.getParent().remove(Functions.container);
		new Functions();
		//the new table is not visible "in the next second" without re validate
		Functions.container.getParent().revalidate();
	}
	private prop workpath;
	static PopUp textPopup=new PopUp();
	static Project project=new Project();
	static JFrame frame;
	private ImageIcon img;
	public void main(String[] args) {
		//keep this order of declarations
		img = new ImageIcon(System.getProperty("user.dir")+"img/icon.jpg");
		workpath=new prop("workpath",System.getProperty("user.home")+"/Desktop");
		menu menuBar=new menu();//Image,work path
		frame = new JFrame();
		if(args.length==1)project.folder_set(args[0],true);//frame,work path sets path
		else{
			project.folder_set_base(workpath.get(),true,true);//work path sets path
			project.newproj(project.width_default,project.height_default,project.backgroundcolor_default,project.fps_default);//frame,use path
		}
		new Functions();//frame,use path
		
		//Set up the exit.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add program icon
		frame.setIconImage(img.getImage());
		
		//MenuBar to frame
		frame.setMenuBar(menuBar);
		
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
}