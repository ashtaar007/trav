import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

interface Button {
	public void display(Graphics2D g);
	public boolean contains(int x, int y);
	public boolean isBeingPushed();
	public void setIsBeingPushed(boolean value);
	public void push();
	public void mousePressed(int mouseInitialClickLocationX, int mouseInitialClickLocationY);
	public void mouseReleased(int mouseReleaseLocationX, int mouseReleaseLocationY);
	public void mouseMoved(int mouseCurrentLocationX, int mouseCurrentLocationY);
}
class BasicButton extends Rectangle implements Button{
	/*to add new types of Buttons
	 * 1. Add new Button Class and new map for relevant Simulation window)
	 * 2. Make instances and add to the relevant map
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
	private boolean isBeingPushed;
	int id;//can use this in a switch statement to individualize push function
	AttributedCharacterIterator title;
	public BasicButton(int id, int x, int y, int width, int height){
		super(x,y,width,height);
		this.id=id;
	}
	public BasicButton(int id, int x, int y, int width, int height, String title){
		this(id,x,y,width,height);
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
	public void mousePressed(int mouseInitialClickLocationX, int mouseInitialClickLocationY){
		setIsBeingPushed(contains(mouseInitialClickLocationX,mouseInitialClickLocationY));
	}
	public void mouseReleased(int mouseReleaseLocationX, int mouseReleaseLocationY){
		if(isBeingPushed()&&contains(mouseReleaseLocationX,mouseReleaseLocationY))
			push();
		setIsBeingPushed(false);
	}
	public void mouseMoved(int mouseCurrentLocationX, int mouseCurrentLocationY){
	}

	public boolean isBeingPushed(){
		return isBeingPushed;
	}
	public void setIsBeingPushed(boolean value){
		isBeingPushed=value;
	}
}
class CheckBox extends BasicButton implements Button{
	final static Stroke strokeWidth = new BasicStroke(3);
	public final static Color textColor=Color.green;
	public CheckBox(int id, int x, int y, int width, int height){
		super(id,x,y,width,height);
	}
	public void display(Graphics2D g){
		g.setColor(baseColor);
		g.fill(this);
		g.setColor(borderColor);
		g.draw(this);
		if(isBeingPushed()){
			g.setStroke(strokeWidth);
			g.setColor(textColor);
			g.drawLine(x, (int)(y+.2*height), x+width/2, y+height);
			g.drawLine(x+width/2, y+height, x+3*width/2, y-height/2);
		}
	}
	public void push(){
	}
	public void mousePressed(int mouseInitialClickLocationX, int mouseInitialClickLocationY){
		if(contains(mouseInitialClickLocationX,mouseInitialClickLocationY)){
			setIsBeingPushed(!isBeingPushed());
			if(isBeingPushed()){
				push();
			}
		}
	}
	public void mouseReleased(int mouseReleaseLocationX, int mouseReleaseLocationY){
	}
	public boolean isOnCorrectScreen(){
		return !Simulation.isShipBuilderScreen.value;
	}
}

class DropDownMenuButton extends BasicButton implements Button{
	final static Color textColor = Color.red;
	public DropDownMenuButton(int id, int x, int y, int width, int height, String title){
		super(id,x,y,width,height,title);
	}
	public void push(){
		System.out.println(id);
	}
	public void display(Graphics2D g){
		super.display(g,baseColor,pushedColor,textColor);
	}
	public void mousePressed(int mouseInitialClickLocationX, int mouseInitialClickLocationY){
	}
	public void mouseReleased(int mouseReleaseLocationX, int mouseReleaseLocationY){
		if(isBeingPushed()&&contains(mouseReleaseLocationX,mouseReleaseLocationY))
			push();
	}
}
class DropDownMenu extends BasicButton implements Button{
	final static Color baseColor = Color.lightGray;
	final static Color pushedColor = Color.gray;
	final static Color textColor = Color.black;
	Map<Integer, Button> map;
	public DropDownMenu(int id, int x, int y, int width, int height, String title){
		super(id,x,y,width,height,title);
		map = new HashMap<Integer, Button>();
	}
	public boolean contains(int x, int y){
		boolean isContained = super.contains(x, y);
		if(!isBeingPushed())
			return isContained;
		else{
			for (Button button : map.values()) {
				button.setIsBeingPushed(button.contains(x,y));
				isContained |= button.isBeingPushed();
			}
		}
		return isContained;
	}
	public void mousePressed(int mouseInitialClickLocationX, int mouseInitialClickLocationY){
	}
	public void mouseReleased(int mouseReleaseLocationX, int mouseReleaseLocationY){
	}
	public void mouseMoved(int mouseCurrentLocationX, int mouseCurrentLocationY){	
		setIsBeingPushed(contains(mouseCurrentLocationX,mouseCurrentLocationY));
		Simulation.builderMaps.put("menuButtons",isBeingPushed()?map:Simulation.menuButtonsDefault);
	}
	public void add(int id, String title){ //needs more arguments when DropDownMenuButton is more developed
		int y = this.y + (map.size()+1)*height;
		map.put(id, new DropDownMenuButton(id,x,y,width,height,title));
	}
	public void display(Graphics2D g){
		super.display(g,baseColor,pushedColor,textColor);
	}
}
public class RectangleSwitch extends BasicButton implements Button{
	BooleanObject screenControl;
	AttributedCharacterIterator titleTrue;
	AttributedCharacterIterator titleFalse;
	public RectangleSwitch(int id, int x, int y, int width, int height,BooleanObject screenControl, String titleTrue, String titleFalse){
		super(id, x,y,width,height);
		this.titleTrue=(new AttributedString(titleTrue,textAttributes)).getIterator();
		this.titleFalse=(new AttributedString(titleFalse,textAttributes)).getIterator();
		this.screenControl = screenControl;
	}
	public void display(Graphics2D g){
		title=screenControl.value?titleTrue:titleFalse;
		super.display(g);
	}
	final static int BUILDER = 1;
	final static int SETUP = 2;
	public void push(){
		screenControl.value=!screenControl.value;
		
		switch(id){
			case BUILDER:
				Simulation.isSetupMode.value=true;
				Simulation.isPaused=true;
				break;
			case SETUP:
				Simulation.isPaused=!Simulation.isPaused;
				break;
		}
		
	}
}

class BooleanObject{
	boolean value;
	public BooleanObject(boolean value){
		this.value=value;
	}
}