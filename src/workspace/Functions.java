package workspace;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import static actionswf.ActionSwf.FillStyleType_none;

public class Functions extends JTable{
	private static final long serialVersionUID = 1L;
	static Functions table;static Component container;
	private JButton ButtonData_button=new JButton(ButtonData);
	{
		ButtonData_button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				structure_dialog_run run=new structure_dialog_run(){
					public void Run(Field f,InputText txt){
						if(f.getName().equals("def_fill"))txt.setToolTipText("Shape fill color");else if(f.getName().equals("def_line_sz"))txt.setToolTipText("Shape line size");else if(f.getName().equals("def_line"))txt.setToolTipText("Shape line color");
						else if(f.getName().equals("ov_fill"))txt.setToolTipText("Shape mouse over fill color");else if(f.getName().equals("ov_line_sz"))txt.setToolTipText("Shape mouse over line size");else if(f.getName().equals("ov_line"))txt.setToolTipText("Shape mouse over line color");
						else if(f.getName().equals("xcurve"))txt.setToolTipText("Shape curve size on x");else if(f.getName().equals("ycurve"))txt.setToolTipText("Shape curve size on y");
						else if(f.getName().equals("text"))txt.setToolTipText("Button text");else if(f.getName().equals("font_id"))txt.setToolTipText("Text font id");else if(f.getName().equals("font_height"))txt.setToolTipText("Text font height");else if(f.getName().equals("font_vertical_offset"))txt.setToolTipText("Text font vertical offset");else if(f.getName().equals("font_color"))txt.setToolTipText("Font rgba color");
						else/*if(f.getName().equals("actions"))*/txt.setToolTipText("ActionScript on press");
					}
				};
				structure_dialog(ButtonData,run);
			}
		});
	}
	private JButton EditText_button=new JButton(EditText);
	{
		EditText_button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				structure_dialog_run run=new structure_dialog_run(){
					public void Run(Field f,InputText txt){
						if(f.getName().equals("font_id"))txt.setToolTipText("If HasFont, ID of font to use.");else if(f.getName().equals("font_height"))txt.setToolTipText("If HasFont, Height of font in pixels.");
						else if(f.getName().equals("fontclassname"))txt.setToolTipText("If HasFontClass, Class name");
						else if(f.getName().equals("rgba"))txt.setToolTipText("If HasTextColor, Color of text.");
						else if(f.getName().equals("maxlength"))txt.setToolTipText("If HasMaxLength, Text is restricted to this length.");
						else if(f.getName().equals("initialtext"))txt.setToolTipText("If HasText, Text that is initially displayed.");
						else if(f.getName().equals("layout_align"))txt.setToolTipText("If HasLayout, 0=Left; 1=Right; 2=Center; 3=Justify");else if(f.getName().equals("layout_leftmargin"))txt.setToolTipText("If HasLayout, Left margin in pixels.");else if(f.getName().equals("layout_rightmargin"))txt.setToolTipText("If HasLayout, Right margin in pixels.");else if(f.getName().equals("layout_indent"))txt.setToolTipText("If HasLayout, Indent in pixels.");else/* if(f.getName().equals("layout_leading"))*/txt.setToolTipText("If HasLayout, Leading in pixels.");
					}
				};
				structure_dialog(EditText,run);
			}
		});
	}
	private interface structure_dialog_run{
		void Run(Field f,InputText txt);
	}
	private void structure_dialog(String title,structure_dialog_run run){
		JDialog dg=new JDialog(SwingUtilities.getWindowAncestor(this),title,Dialog.ModalityType.DOCUMENT_MODAL);
		Container ct=dg.getContentPane();
		ct.setLayout(new BoxLayout(ct,BoxLayout.Y_AXIS));
		Container c=new Container();c.setLayout(new GridLayout(0,2));

		Object obj=getValueAt(getEditingRow(),getEditingColumn());
		Field[]fs=obj.getClass().getDeclaredFields();
		for(int i=0;i<fs.length;i++){
			Field f=fs[i];
			c.add(new Label(f.getName()));
			try{
				InputText txt;
				if(f.getType().getSimpleName().equals("int"))txt=new IntInputText((int)f.get(obj));
				else txt=new InputText(f.get(obj).toString());
				run.Run(f,txt);
				if(f.isAnnotationPresent(element.NamedId.class))txt.setBackground(NamedId_color);
				c.add(txt);
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}

		ct.add(c);
		Button btn=new Button("OK");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int j=0;
				for(int i=0;i<fs.length;i++){
					Field f=fs[i];
					j++;InputText z=(InputText) c.getComponent(j);j++;
					try {
						if(f.getType().getSimpleName().equals("String"))f.set(obj,z.getText());
						else f.set(obj,Long.decode(z.getText()).intValue());
					}catch (IllegalArgumentException | IllegalAccessException er){
						er.printStackTrace();
					}
				}
				dg.dispose();
			}
		});
		ct.add(btn);
		dg.pack();
		dg.setVisible(true);
	}
	private JButton Shape_button=new JButton("Shape");
	private void ShapeRecord_tip(Container c_rec){
		GridLayout layo=(GridLayout)c_rec.getLayout();
		for(int x=0;x<layo.getColumns();x++){
			InputText it=(InputText)c_rec.getComponent(c_rec.getComponents().length-layo.getColumns()+x);
			if(x==0)it.setToolTipText("Non-edge record=0; Edge record=1");
			else if(x==1)it.setToolTipText(tooltip_multiline(new String[]{"If Non-edge; StateMoveTo=1|StateFillStyle0=2|StateFillStyle1=4|StateLineStyle=8","If Edge; Straight edge=1, Curved edge=0"}));
			else if(x==2)it.setToolTipText(tooltip_multiline(new String[]{"If Non-edge, If StateMoveTo, MoveDeltaX","If Edge, If Straight, DeltaX","If Edge, If Curved, ControlDeltaX"}));
			else if(x==3)it.setToolTipText(tooltip_multiline(new String[]{"If Non-edge, If StateMoveTo, MoveDeltaY","If Edge, If Straight, DeltaY","If Edge, If Curved, ControlDeltaY"}));
			else if(x==4)it.setToolTipText("If Edge, If Curved, AnchorDeltaX");
			else/* if(x==5)*/it.setToolTipText("If Edge, If Curved, AnchorDeltaY");
		}
	}
	{
		Shape_button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JDialog dg=new JDialog(SwingUtilities.getWindowAncestor(table),"ShapeWithStyle",Dialog.ModalityType.DOCUMENT_MODAL);
				Container ct=dg.getContentPane();ct.setLayout(new BoxLayout(ct,BoxLayout.Y_AXIS));
				Container c=new Container();c.setLayout(new GridLayout(0,4));
				
				int ed_r=getEditingRow();int ed_c=getEditingColumn();
				Object[]ints=(Object[])getValueAt(ed_r,ed_c);int i=0;
				int test_val=(int) ints[i++];
				c.add(new Label("FillStyleType"));
				//
				String arg = null;if(test_val!=FillStyleType_none)arg=ints[i++].toString();
				InputText fill_arg=new InputText(arg);
				//
				IntInputText fill_type=new IntInputText();
				fill_type.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void changedUpdate(DocumentEvent arg0) {
						//Plain text components do not fire these events
					}
					@Override
					public void insertUpdate(DocumentEvent arg0) {
						ltner();
					}
					@Override
					public void removeUpdate(DocumentEvent arg0) {
						ltner();
					}
					private void ltner(){
						String text=fill_type.getText();
						int type = 0;
						try{
							type=Long.decode(text).intValue();
						}
						catch(NumberFormatException x){/*text.length==0 or 0x */}
						if(WorkSpace.project.isShapeBitmap(type))fill_arg.setBackground(NamedId_color);
						else fill_arg.setBackground(Color.WHITE);
					}
				});
				fill_type.setText(Integer.toString(test_val));//this is required here to apply the correct background color with the above listener
				c.add(fill_type);fill_type.setToolTipText(tooltip_multiline(new String[]{"-1=no fill","0x00=solid fill","0x40=repeating bitmap fill, 0x41=clipped bitmap fill, 0x42=non-smoothed repeating bitmap, 0x43=non-smoothed clipped bitmap"}));
				//
				c.add(new Label("Arg"));
				c.add(fill_arg);fill_arg.setToolTipText("If solid fill, Color; If bitmap fill, BitmapId");
				IntInputText txt;
				test_val=(int) ints[i++];
				c.add(new Label("LineStyleWidth"));
				txt=new IntInputText(test_val);c.add(txt);txt.setToolTipText("Width of line in pixels.");
				int argColor=0;if(test_val!=0)argColor=(int) ints[i++];
				c.add(new Label("Color"));
				txt=new IntInputText(argColor);c.add(txt);txt.setToolTipText("If Width!=0, Color value including alpha channel information.");
				
				ct.add(c);
				Container c_rec=new Container();c_rec.setLayout(new GridLayout(0,6));
				int column=0;
				for(;;){
					int val=(int) ints[i++];
					if(column==0){
						if(val==Elements.Shape.ShapeWithStyle.end_of_values)break;
						Elements.Shape.ShapeWithStyle.phase_start();
					}
					c_rec.add(new IntInputText(val));column++;
					if(Elements.Shape.ShapeWithStyle.phase_get(val)==false){
						GridLayout layo=(GridLayout)c_rec.getLayout();
						for(;column<layo.getColumns();column++)c_rec.add(new IntInputText());
						ShapeRecord_tip(c_rec);
						column=0;
					}
				}
				ct.add(c_rec);
				
				Button rec=new Button("Add ShapeRecord");
				rec.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						GridLayout layo=(GridLayout)c_rec.getLayout();
						for(int i=0;i<layo.getColumns();i++)c_rec.add(new IntInputText());
						ShapeRecord_tip(c_rec);
						dg.pack();
					}
				});
				ct.add(rec);
				Button btn=new Button("OK");
				btn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						int ft=Long.decode(((IntInputText)c.getComponent(1)).getText()).intValue();
						Object ft_arg = 0;
						if(ft!=FillStyleType_none){
							ft_arg=((InputText)c.getComponent(3)).getText();
							if(WorkSpace.project.isShapeBitmap(ft)==false){
								try{
									ft_arg=Long.decode((String)ft_arg).intValue();
								}catch(NumberFormatException er){
									JOptionPane.showMessageDialog(null,"Numeric vaule at Arg required");
									return;
								}
							}
						}
						int l_w=Long.decode(((IntInputText)c.getComponent(5)).getText()).intValue();
						int l_color = 0;
						if(l_w!=0)l_color=Long.decode(((IntInputText)c.getComponent(7)).getText()).intValue();
						List<Integer>intrs=new ArrayList<Integer>();
						GridLayout layo=(GridLayout)c_rec.getLayout();
						for(int i=0;i<c_rec.getComponentCount()/layo.getColumns();i++){
							int j=0;Elements.Shape.ShapeWithStyle.phase_start();
							for(;;){
								int val=Long.decode(((IntInputText)c_rec.getComponent(i*layo.getColumns()+j++)).getText()).intValue();
								boolean in=Elements.Shape.ShapeWithStyle.phase_get(val);
								intrs.add(val);
								if(in==false)break;
							}
						}
						Object[]dest=new Object[intrs.size()];for(int i=0;i<dest.length;i++)dest[i]=intrs.get(i);
						setValueAt(new Elements.Shape.ShapeWithStyle(ft,ft_arg,l_w,l_color,dest).toArray(),ed_r,ed_c);
						dg.dispose();
					}
				});
				ct.add(btn);
				dg.pack();
				dg.setVisible(true);
			}
		});
	}
	private class ToolTipWrapper{ 
		private String value;
		private String toolTip;
		private ToolTipWrapper(String value,String toolTip) {
			this.value = value;
			this.toolTip = toolTip;
		}
		//used by the ComboBox to get the value
		public String toString() {
			return value;
		}
	}
	private class ToolTipRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
			JComponent component = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			//index -1 will run into errors without instanceOf ..
			if (value instanceof ToolTipWrapper)list.setToolTipText(((ToolTipWrapper)value).toolTip);
			return component;
		}
	}
	private List<ToolTipWrapper> ToolTips=new ArrayList<ToolTipWrapper>();
	private ToolTipWrapper ToolTips_get(String v){
		for(int i=0;i<ToolTips.size();i++){
			if(ToolTips.get(i).value.equals(v))return ToolTips.get(i);
		}
		return null;
	}
	Functions(){
		//create the table
		setTableHeader(null);table=this;
		
		//create a combo box
		JComboBox<ToolTipWrapper> comboBox = new JComboBox<ToolTipWrapper>();
		
		//add the functions to combo box and iterate maximum arguments for the table
		int total_args=0;
		for(function f:f_list){
			String n=f.name;
			total_args=Math.max(f.number_of_args,total_args);
			if(n.equals("swf_new")==false&&n.equals("swf_done")==false){
				ToolTips.add(new ToolTipWrapper(n,n));
				comboBox.addItem(ToolTips.get(ToolTips.size()-1));
			}
		}
		
		//get default model
		DefaultTableModel model=(DefaultTableModel)getModel();
		//set rows and columns
		for(int i=0;i<(1+total_args+1);i++)model.addColumn(null);
		//minimum model
		Object[] n=WorkSpace.project.builder.swf_new__arguments();
		ArrayList<Object> temp=new ArrayList<Object>(Arrays.asList(n));
		temp.add(0,new ToolTipWrapper("swf_new","swf_new"));
		n=temp.toArray();
		model.addRow(n);
		//last show frame is wrote later
		int max_size=WorkSpace.project.elements.size()-1;
		for(int a=0;a<max_size;a++){
			Object[]obj=new Object[getColumnCount()];
			Object element=WorkSpace.project.elements.get(a);
			String el_type=element.getClass().getSimpleName();
			Class<?>[]el_types=Elements.class.getDeclaredClasses();
			for(int x=0;x<el_types.length;x++){
				Class<?>c=el_types[x];
				if(el_type.equals(c.getSimpleName())){
					Field[]fields=c.getDeclaredFields();
					String f_name=Project.elements_names_convertor(el_type,null);
					obj[0]=ToolTips_get(f_name);
					for(function f:f_list){
						if(f_name.equals(f.name)){
							try{
								for(int y=0;y<f.number_of_args;y++){
									obj[y+1]=fields[y].get(element);
								}
								if(hasReturn(f))obj[getColumnCount()-1]=fields[fields.length-1].get(element);
							}catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
							break;
						}
					}
					break;
				}
			}
			model.addRow(obj);
		}
		//add one row
		model.addRow(new Object[getColumnCount()]);
		
		//add combo box listener
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
				//this is not solo used 'cause a bug will trigger for our c box row(1) and at nothing selected when clicking our c box then click on another c box(0)
					//getSelectedRow() will point to our c box(1)
					//Schedule a job for the event-dispatching thread
					//still avoiding the bug
					int thisRow=getSelectedRow();
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						//getSelectedRow() will be updated and will point to another c box(0)
						public void run(){
							//still avoiding the bug
							if(thisRow==getSelectedRow()){
								for(int i=1;i<getColumnCount();i++)setValueAt(null,thisRow,i);
								for(function f:f_list){
									if(f.name.equals(((ToolTipWrapper)event.getItem()).value)){
										for(int i=0;i<f.number_of_args;i++){
											String t=f.args_types.get(i);Object a;
											if(t.equals("int"))a=0;
											else if(t.equals("String"))a="";
											else if(t.equals(ButtonData))a=new Elements.Button.ButtonData();
											else if(t.equals(EditText))a=new Elements.Text.EditText();
											else/*if(t.equals("Object[]"))*/a=new Elements.Shape.ShapeWithStyle().toArray();
											setValueAt(a,thisRow,i+1);
										}
										if(hasReturn(f))setValueAt("",thisRow,getColumnCount()-1);
										break;
									}
								}
								
								int modelEnd_pos=getRowCount()-modelEnd_sum;
								if(modelEnd_pos-1==getSelectedRow())model.insertRow(modelEnd_pos,new Object[getColumnCount()]);
								else repaint();
							}
						}
					});
				}
			}
		});
		
		//default table editor
		setDefaultEditor(getColumnClass(0),new TableEditor(new JTextField()));
		
		//set the combo box to column 0 and renderer
		TableColumn fnColumn=getColumnModel().getColumn(0);
		fnColumn.setCellEditor(new DefaultCellEditor(comboBox));
		ToolTipRenderer renderer = new ToolTipRenderer();
		comboBox.setRenderer(renderer);
		
		//set modelEnd_sum
		modelEnd_sum=getRowCount();
		//minimum model
		Object[] minimum_model_row=new Object[getColumnCount()];minimum_model_row[0]=ToolTips_get("swf_showframe");
		model.addRow(minimum_model_row);
		minimum_model_row[0]=new ToolTipWrapper("swf_done","swf_done");model.addRow(minimum_model_row);
		//set modelEnd_sum
		modelEnd_sum=getRowCount()-modelEnd_sum;
		
		//set renderer for marking non-used cells(reminder: can subclass some code to set for individual cells)
		setDefaultRenderer(getColumnClass(0),new TableRenderer());
	
		//to frame container
		container=new JScrollPane(this);
		WorkSpace.container.add(container);
	}
	// Determine editor to be used by row
	@Override
	public TableCellEditor getCellEditor(int row, int column)
	{
		if(edit_block(row,column)){
			Window topLevelWin = SwingUtilities.getWindowAncestor(this);
			JWindow errorWindow = new JWindow(topLevelWin);
			JPanel contentPane = (JPanel) errorWindow.getContentPane();
			contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
			//(String... message)//for(integer i=0;i<message.length;i++){
			contentPane.add(new JLabel("Uneditable cell"));
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
			return null;
		}
		return super.getCellEditor(row, column);
	}
	@Override
	public String getToolTipText(MouseEvent e){
		int row = rowAtPoint( e.getPoint() );
		int column = columnAtPoint( e.getPoint() );
		if(column>0){
			ToolTipWrapper tt=(ToolTipWrapper)getValueAt(row,0);
			if(tt!=null){
				Object f=tt.value;
				
				Object[]tips=null;
				
				if(f.equals("swf_button"))tips=new String[]{"Width in pixels","Height in pixels","Button data"};
				else if(f.equals("swf_font"))tips=new Object[]{"FontName field",new String[]{"FontFlagsBold=1|FontFlagsItalic=2|FontFlagsWideCodes=4|FontFlagsWideOffsets=8","FontFlagsANSI=0x10|FontFlagsSmallText=0x20|FontFlagsShiftJIS=0x40|FontFlagsHasLayout=0x80"}};
				else if(f.equals("swf_text"))tips=new Object[]{"Width in pixels","Height in pixels","VariableName field",new String[]{"HasFont=0x1|HasMaxLength=0x2|HasTextColor=0x4|ReadOnly=0x8","Password=0x10|Multiline=0x20|WordWrap=0x40|HasText=0x80","UseOutlines=0x100|HTML=0x200|WasStatic=0x400|Border=0x800","NoSelect=0x1000|HasLayout=0x2000|AutoSize=0x4000|HasFontClass=0x8000"},"EditText structure"};
				else if(f.equals("swf_shape"))tips=new String[]{"Width in pixels","Height in pixels","SHAPEWITHSTYLE field"};
				//else if(f.equals("swf_image"))tips=new String[]{"DefineBitsLossless 1 or 2 image path"};
				else if(f.equals("swf_dbl"))tips=new String[]{"DefineBitsLossless 1 or 2 image path"};
				
				//else if(f.equals("swf_done"))
				else if(f.equals("swf_new"))tips=new String[]{"SWF file name: "+getValueAt(row,1),"Width in pixels","Height in pixels","Background rgb color","Frames per second"};
				else if(f.equals("swf_placeobject"))tips=new String[]{"CharacterId field","Depth of character"};
				else if(f.equals("swf_placeobject_coords"))tips=new String[]{"CharacterId field","Depth of character","X position in pixels","Y position in pixels"};
				else if(f.equals("swf_removeobject"))tips=new String[]{"Depth of character"};
				//else if(f.equals("swf_showframe"))
				
				else if(f.equals("swf_sprite_done"))tips=new String[]{"Sprite pre-id"};
				//else if(f.equals("swf_sprite_new"))
				else if(f.equals("swf_sprite_placeobject"))tips=new String[]{"Sprite pre-id","CharacterId field","Depth of character"};
				else if(f.equals("swf_sprite_placeobject_coords"))tips=new String[]{"Sprite pre-id","CharacterId field","Depth of character","X position in pixels","Y position in pixels"};
				else if(f.equals("swf_sprite_removeobject"))tips=new String[]{"Sprite pre-id","Depth of character"};
				else if(f.equals("swf_sprite_showframe"))tips=new String[]{"Sprite pre-id"};
				
				else if(f.equals("swf_exports_add"))tips=new String[]{"CharacterId field","Name for exported character"};
				//else if(f.equals("swf_exports_done"))
				
				else if(f.equals("action"))tips=new String[]{"ActionScript string"};
				else if(f.equals("action_sprite"))tips=new String[]{"Sprite pre-id","ActionScript string"};
				
				if(tips!=null){
					if(column<1+tips.length){
						String tp=tips[column-1].getClass().getSimpleName();
						if(tp.equals("String"))return (String)tips[column-1];
						else return tooltip_multiline((String[])tips[column-1]);
					}
				}
			}
		}
		return super.getToolTipText(e);
	}
	private String tooltip_multiline(String[]vals){
		String out="<html>";
		for(int i=0;i<vals.length;i++){
			if(i>0)out+="<br>";
			out+=vals[i];
		}
		out+="</html>";
		return out;
	}
	private boolean edit_block(int row,int column){
		if(row==0){
			//also block s w f name changes at 1
			if(column==0||column==1)return true;
		}else if(row>=getRowCount()-modelEnd_sum){
			if(column==0)return true;
		}
		return false;
	}
	void update() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<Object>elems=new ArrayList<Object>();
		try{
			for(int i=0;i<getRowCount();i++){
				ToolTipWrapper tt=(ToolTipWrapper)getValueAt(i,0);
				if(tt!=null){
					String s=tt.value;
					for(function f:f_list){
						if(f.name.equals(s)){
							List<Object> x=new ArrayList<Object>();
							for(int j=0;j<f.number_of_args;j++){
								Object val=getValueAt(i,j+1);
								//an Integer can be set but when typing will become String
								x.add(val);
							}
							if(hasReturn(f))x.add(getValueAt(i,getColumnCount()-1));
							//
							if(s.equals("swf_done"))break;
							if(s.equals("swf_new")){
								WorkSpace.project.width=Long.decode(x.get(1).toString()).intValue();WorkSpace.project.height=Long.decode(x.get(2).toString()).intValue();WorkSpace.project.backgroundcolor=Long.decode(x.get(3).toString()).intValue();WorkSpace.project.fps=Long.decode(x.get(4).toString()).intValue();
							}
							else{
								String element=Project.elements_names_convertor(null,s);
								Class<?>[]cs=Elements.class.getDeclaredClasses();
								for(int a=0;a<cs.length;a++){
									String simpleName=cs[a].getSimpleName();
									if(simpleName.equals(element)){
										Class<?>c=cs[a];
										List<Object>values=new ArrayList<Object>();
										Field[]fields=c.getDeclaredFields();
										for(int z=0;z<fields.length;z++){
											Object o=x.get(z);
											if(fields[z].getGenericType().getTypeName().equals("int"))o=Long.decode(o.toString()).intValue();
											values.add(o);
										}
										elems.add(WorkSpace.project.runtime_instance(c,values));
										break;
									}
								}
							}
							break;
						}
					}
				}
			}
			WorkSpace.project.elements=elems;
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(null,"User number input error in table");
			throw e;
		}
	}
	private int modelEnd_sum;
	public static List<function> f_list;
	private class TableRenderer extends DefaultTableCellRenderer{
		//TableCellRenderer doesn't have setBackground
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){ 
			Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			//columns 0-max at renderer, 1-max at TableEditor
			if(column>0){
				Component val=get_at_cell(row,column,c);
				if(val!=null)return val;
			}
			if(!isSelected)c.setBackground(table.getBackground());
			return c;
		}
	}
	private static final Color NamedId_color=new Color(0xcc,0xff,0xcc);
	private Component get_at_cell(int row,int column,Component renderer){
		//column 0 is only at renderer, editor at 0 is combo box
		function f = null;
		ToolTipWrapper tt=(ToolTipWrapper)getValueAt(row,0);
		if(tt!=null){
			Object v=tt.value;
			for(int i=0;i<f_list.size();i++){
				f=f_list.get(i);
				if(f.name.equals(v)){
					int arg_index=column-1;
					if(f.number_of_args>arg_index){
						if(f.args_types.get(arg_index).equals(ButtonData))return ButtonData_button;
						else if(f.args_types.get(arg_index).equals(EditText))return EditText_button;
						else if(f.args_types.get(arg_index).equals("Object[]"))return Shape_button;
						if(renderer!=null){
							if(f.args_isNamed.get(arg_index)==true){
								renderer.setBackground(NamedId_color);
								return renderer;
							}
						}
						return null;
					}
					break;
				}
			}
		}
		if(renderer!=null){
			if(column<getColumnCount()-1||tt==null)renderer.setBackground(new Color(0xee,0xee,0xee));//Gray94
			else{
				if(hasReturn(f))renderer.setBackground(NamedId_color);
				else renderer.setBackground(new Color(0xcc,0xdd,0xcc));
			}
			return renderer;
		}
		return null;
	}
	static final String ButtonData="ButtonData";
	static final String EditText="EditText";
	private class TableEditor extends DefaultCellEditor{
		private static final long serialVersionUID = 1L;
		private TableEditor(JTextField textField){
			super(textField);
		}
		@Override		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			Component val=get_at_cell(row,column,null);
			if(val!=null)return val;
			return super.getTableCellEditorComponent(table,value,isSelected,row,column);
		}
		@Override		
		public Object getCellEditorValue(){
			if(get_at_cell(getEditingRow(),getEditingColumn(),null)!=null){
				//override getCellEditorValue here, will consider a text and will broke the EditText structure,etc..
				return getValueAt(getEditingRow(),getEditingColumn());
			}
			return super.getCellEditorValue();
		}
	}
	public static boolean hasReturn(function f){
		return (f.return_type.equals("void")==false);
	}
}
