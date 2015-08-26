package workspace;

import graphics.Graphics;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

import util.util.PanelEx;

public class WorkSpace {
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
	private class menu extends JMenuBar{
		private static final long serialVersionUID = 1L;
		private void choose(String title,JFileChooser_run r){
			JFileChooser chooser=new JFileChooser();
			chooser.setDialogTitle(title);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setCurrentDirectory(new File(workpath.get()));
			if(chooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION)r.Run(chooser);
		}
		private class file extends JMenu{
			private static final long serialVersionUID = 1L;
			private file(JMenuBar menu){
				super("File");
				newp();open();save();properties();exit();
				menu.add(this);
			}
			private void newp(){
				String new_f="New";
				JMenuItem newp=new JMenuItem(new_f);
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
				JButton new_color_b=new JButton();
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
				JButton btn=new JButton("OK");
				btn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if(project.folder_set(workpath.get()+"/"+new_name.getText(),false)){
							project.newproj(Long.decode(new_width.getText()).intValue(),Long.decode(new_height.getText()).intValue(),colorChooser.getColor().getRGB()&0xffFFff,Long.decode(new_fps.getText()).intValue());
							dg.setVisible(false);
							resetPerspective();
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
				JMenuItem open=new JMenuItem("Open");
				open.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						choose("Open Project",new JFileChooser_run(){public void Run(JFileChooser c){
							project.folder_set(c.getSelectedFile().toString(),true);
							resetPerspective();
						}});
					}
				});
				add(open);
			}
			private void save(){
				JMenuItem save=new JMenuItem("Save");
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
				JMenuItem properties=new JMenuItem(prop);
				JDialog dl=new JDialog(frame,prop,Dialog.ModalityType.DOCUMENT_MODAL);
				Container cnt=dl.getContentPane();
				cnt.setLayout(new BoxLayout(cnt,BoxLayout.Y_AXIS));
				//container
				Container cnt1=new Container();
				cnt1.setLayout(new GridLayout(0,2));
				//workspace path
				String choosertitle="Workspace Path";
				JButton workpath_browse=new JButton(choosertitle);
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
				JButton bt=new JButton("OK");
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
				JMenuItem exit=new JMenuItem("Exit");
				exit.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				add(exit);
			}
		}
		private class proj extends JMenu{
			private static final long serialVersionUID = 1L;
			private proj(JMenuBar menu){
				super("Project");
				build();run();
				
				addSeparator();
				ButtonGroup group = new ButtonGroup();
				//((JRadioButtonMenuItem)
						add_radio(group,funcs)
				//).setSelected(true)
				;
				((JRadioButtonMenuItem)
						add_radio(group,graphs)
				).setSelected(true)
				;
				//perspective=funcs;
				perspective=graphs;
				
				menu.add(this);
			}
			private JRadioButtonMenuItem last_radio;
			private JRadioButtonMenuItem add_radio(ButtonGroup group,String name){
				JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(name);
				group.add(rbMenuItem);
				rbMenuItem.addActionListener(new persp());
				rbMenuItem.addItemListener(new persp_item());
				add(rbMenuItem);
				return rbMenuItem;
			}
			private class persp implements ActionListener{
				@Override
				public void actionPerformed(ActionEvent arg0){
					try{
						updateElements();
						perspective=arg0.getActionCommand();
						resetPerspective();
					}catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
						last_radio.setSelected(true);
						e.printStackTrace();
					}
				}
			}
			private class persp_item implements ItemListener{
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange()==ItemEvent.DESELECTED)last_radio=(JRadioButtonMenuItem) e.getItem();
				}
			}
			private void build(){
				JMenuItem build=new JMenuItem("Build");
				build.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						project.build();
					}
				});
				add(build);
			}
			private void run(){
				JMenuItem run=new JMenuItem("Run");
				run.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						try {
							Desktop.getDesktop().open(new File(project.outString()));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
				add(run);
			}
		}
		private class help extends JMenu{
			private static final long serialVersionUID = 1L;
			private help(JMenuBar menu){
				super("Help");
				about();
				menu.add(this);
			}
			private void about(){
				String abt="About";
				JMenuItem about=new JMenuItem(abt);
				
				//about dialog
				JDialog d=new JDialog(frame,abt,Dialog.ModalityType.DOCUMENT_MODAL);
				Container cp=d.getContentPane();
				cp.setLayout(new BoxLayout(cp,BoxLayout.Y_AXIS));
				//container1
				Container cp1=new Container();
				cp1.setLayout(new FlowLayout());
				cp1.add(new JLabel(img));
				Label l=new Label("Logo");
				cp1.add(l);
				cp.add(cp1);
				//2
				JButton b=new JButton("OK");
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
	public static Container container;
	private static Component getPerspective(){return container.getComponent(0);}
	private void resetPerspective(){
		container.remove(getPerspective());
		addPerspective();
		//the new table is not visible "in the next second" without re validate
	}
	private prop workpath;
	static PopUp textPopup=new PopUp();
	public static Project project=new Project();
	static JFrame frame;
	private ImageIcon img;
	private static final String funcs="Functions view";
	private static final String graphs="Graphics view";
	private String perspective;
	private void addPerspective(){
		if(perspective.equals(graphs))new Graphics();
		else new Functions();
	}
	static void updateElements() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if(getPerspective()==Functions.container)Functions.table.update();
		else Graphics.update();
	}
	public void main(String[] args) {
		//keep this order of declarations
		img = new ImageIcon(System.getProperty("user.dir")+"img/icon.jpg");
		workpath=new prop("workpath",System.getProperty("user.home")+"/Desktop");
		menu menuBar=new menu();//Image,work path
		frame = new JFrame();
		boolean tryOpen=args.length==1;
		if(tryOpen)tryOpen=project.folder_set(args[0],true);//frame,work path sets path
		if(tryOpen==false){
			project.folder_set_base(workpath.get(),true,true);//work path sets path
			project.newproj(project.width_default,project.height_default,project.backgroundcolor_default,project.fps_default);//frame,use path
		}
		
		container=new PanelEx();container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
		frame.setContentPane(container);
		addPerspective();//frame,use path,container
		
		//Set up the exit.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add program icon
		frame.setIconImage(img.getImage());
		
		//MenuBar to frame
		frame.setJMenuBar(menuBar);
		
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
}