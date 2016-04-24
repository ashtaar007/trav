import java.awt.*;
import java.awt.font.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
class Button extends Rectangle{
	/*to add new types of Buttons
	 * 1. Add new Button Class (and Simulation variables ?)
	 * 2. Add to Simulation.initButtons
	 * 3. Add to Simulation displayButtons loop
	 * 4. Add to MouseManager.mousePressed
	 * 5. Add to MouseManager.mouseReleased
	 */
	
	
	
	public static final 
	Hashtable<TextAttribute, Object> textAttributes =
		       new Hashtable<TextAttribute, Object>();
	static {
		textAttributes.put(TextAttribute.FAMILY, "Serif");
		textAttributes.put(TextAttribute.SIZE, 14);
	}
	public final static Color baseColor=Color.darkGray;
	public final static Color pushedColor=Color.blue;
	public final static Color textColor=Color.green;
	public final static Color borderColor=Color.cyan;
	public boolean isBeingPushed;
	AttributedCharacterIterator title;
	public Button(int x, int y, int width, int height, String title){
		super(x,y,width,height);
		if(title.length()>0)
			this.title=(new AttributedString(title,textAttributes)).getIterator();
	}
	public void display(Graphics2D g){
		this.display(g,baseColor,pushedColor,textColor);
	}
	public void display(Graphics2D g, Color baseColor, Color pushedColor, Color textColor){
		g.setColor(isBeingPushed?pushedColor:baseColor);
		g.fill(this);
		g.setColor(textColor);
		Simulation.displayWrappedText(title,this, true, true);
		g.setColor(borderColor);
		g.draw(this);
	}
	public void push(){
	}
}

class DropDownMenuButton extends Button{
	final static Color textColor = Color.red;
	public DropDownMenuButton(int x, int y, int width, int height, String title){
		super(x,y,width,height,title);
	}
	public void push(){
	}
	public void display(Graphics2D g){
		super.display(g,baseColor,pushedColor,textColor);
	}
}
class DropDownMenu extends Button{
	final static Color baseColor = Color.lightGray;
	final static Color pushedColor = Color.gray;
	final static Color textColor = Color.black;
	Map<String, DropDownMenuButton> map;
	public DropDownMenu(int x, int y, int width, int height, String title){
		super(x,y,width,height,title);
		map = new HashMap<String, DropDownMenuButton>();
	}
	public boolean contains(int x, int y){
		boolean isContained = super.contains(x, y);
		if(!isBeingPushed)
			return isContained;
		else{
			for (DropDownMenuButton button : map.values()) {
				button.isBeingPushed=button.contains(x,y);
				isContained |= button.isBeingPushed;
			}
		}
		return isContained;
	}
	public void add(String title){ //needs more arguments when DropDownMenuButton is more developed
		int y = this.y + (map.size()+1)*height;
		map.put(title, new DropDownMenuButton(x,y,width,height,title));
	}
	public void display(Graphics2D g){
		super.display(g,baseColor,pushedColor,textColor);
	}
	public void push(){
		//doStuff
	}
}
public class RectangleSwitch extends Button{
	BooleanObject screenControl;
	AttributedCharacterIterator titleTrue;
	AttributedCharacterIterator titleFalse;
	public RectangleSwitch(int x, int y, int width, int height, BooleanObject screenControl, String titleTrue, String titleFalse){
		super(x,y,width,height,"");
		this.titleTrue=(new AttributedString(titleTrue,textAttributes)).getIterator();
		this.titleFalse=(new AttributedString(titleFalse,textAttributes)).getIterator();
		this.screenControl = screenControl;
	}
	public void display(Graphics2D g){
		title=screenControl.value?titleTrue:titleFalse;
		super.display(g);
	}
	public void push(){
		screenControl.value=!screenControl.value;
	}
}

class BooleanObject{
	boolean value;
	public BooleanObject(boolean value){
		this.value=value;
	}
}