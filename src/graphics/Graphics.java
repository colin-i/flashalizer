package graphics;

import graphics.frame.frame_item;
import graphics.frame.item;

import java.awt.Container;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import workspace.WorkSpace;
import workspace.Elements.ExportsAdd;
import workspace.Elements.ExportsDone;
import workspace.Elements.SpriteNew;
import workspace.Elements.SpriteDone;
import workspace.Elements.SpritePlacementCoords;
import workspace.Elements.PlacementCoords;
import workspace.Elements.SpriteAction;
import workspace.Elements.Action;
import workspace.Elements.SpriteShowFrame;
import workspace.Elements.ShowFrame;
import workspace.Elements.SpriteRemove;
import workspace.Elements.Remove;
import util.util.PanelEx;

public class Graphics extends JSplitPane{
	private static final long serialVersionUID = 1L;
	static frame frame;static character character;
	static Container frameData;
	static Container characterData;
	public static final int panel_button_add=6;
	private Preferences prefs=Preferences.userRoot().node(this.getClass().getName());
	private static final String divider="divider";
	public Graphics(){
		super(JSplitPane.VERTICAL_SPLIT);
		setOneTouchExpandable(true);
		
		JPanel pane=new PanelEx();pane.setLayout(new BoxLayout(pane,BoxLayout.Y_AXIS));
		setBottomComponent(pane);
		frameData=new Container();
		pane.add(frameData);
		characterData=new Container();
		pane.add(characterData);
		
		JPanel p=new JPanel();p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
		frame=new frame();
		p.add(frame);
		p.add(new display());
		character=new character();p.add(character);//using frame.add_sprite(x)
		frame.init();//using character.characters
		WorkSpace.container.add(this);
		setTopComponent(p);
		
		Window w=SwingUtilities.getWindowAncestor(this);
		w.pack();
		// This works because we pack the window
		double prop=prefs.getInt(divider,-1);
		if(prop==-1)prop=40;
		setDividerLocation((int)Math.max(getHeight()-prop,getMinimumDividerLocation()));
		addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				prefs.putInt(divider,getHeight()-(int)pce.getNewValue());
			}
		});
	}
	public static void update() throws IllegalArgumentException, IllegalAccessException{
		List<Object>elems=new ArrayList<Object>();
		TreeNode chars=graphics.character.root;
		int n=chars.getChildCount();int sprites=0;
		for(int i=0;i<n;i++){
			DefaultMutableTreeNode t=(DefaultMutableTreeNode) chars.getChildAt(i);
			character.Character c=(character.Character)t.getUserObject();
			if(c.frames==null){
				elems.add(c.element);
			}else{
				String internal_name=Integer.toString(sprites);
				elems.add(new SpriteNew(new Object[]{internal_name}));
				add_frames(c.frames,internal_name,elems);
				elems.add(new SpriteDone(new Object[]{internal_name,c.toString()}));
				sprites++;
			}
			if(c.export_name!=null)elems.add(new ExportsAdd(c.toString(),c.export_name));
		}
		elems.add(new ExportsDone());
		add_frames(graphics.frame.frames,null,elems);
		workspace.WorkSpace.project.elements=elems;
	}
	private static void add_frames(frame_item[]frames,String sprite,List<Object>elements){
		Map<Integer,List<Integer>>removes=new HashMap<Integer,List<Integer>>();
		for(int i=0;i<frames.length;i++){
			frame_item f=frames[i];
			for(item it:f.elements){
				if(sprite!=null)elements.add(new SpritePlacementCoords(sprite,it.toString(),it.depth,it.x,it.y));
				else elements.add(new PlacementCoords(it.toString(),it.depth,it.x,it.y));
				Integer d=it.remove;
				if(d!=null){
					List<Integer>lst=removes.get(d);if(lst==null){lst=new ArrayList<Integer>();removes.put(d,lst);}
					lst.add(it.depth);
				}
			}
			List<Integer>lst=removes.get(i);
			if(lst!=null){
				for(int d:lst){
					if(sprite!=null)elements.add(new SpriteRemove(sprite,d));
					else elements.add(new Remove(d));
				}
			}
			if(f.action_has_length()){
				if(sprite!=null)elements.add(new SpriteAction(sprite,f.action));
				else elements.add(new Action(f.action));
			}
			if(sprite!=null)elements.add(new SpriteShowFrame(sprite));
			else elements.add(new ShowFrame());
		}
	}
}
