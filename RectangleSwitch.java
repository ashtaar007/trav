import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Hashtable;
public class RectangleSwitch extends Rectangle{
	private static final 
	Hashtable<TextAttribute, Object> switchAttributes =
		       new Hashtable<TextAttribute, Object>();
	static {
		switchAttributes.put(TextAttribute.FAMILY, "Serif");
		switchAttributes.put(TextAttribute.SIZE, 18);
	}
	private Color baseColor=Color.darkGray;
	private Color pushedColor=Color.blue;
	public boolean isBeingPushed;
	BooleanObject screenControl;
	AttributedCharacterIterator titleTrue;
	AttributedCharacterIterator titleFalse;
	public RectangleSwitch(int x, int y, int width, int height, BooleanObject screenControl, String titleTrue, String titleFalse){
		super(x,y,width,height);
		this.titleTrue=(new AttributedString(titleTrue,switchAttributes)).getIterator();
		this.titleFalse=(new AttributedString(titleFalse,switchAttributes)).getIterator();
		this.screenControl = screenControl;
	}
	public void display(Graphics2D g){
		g.setColor(isBeingPushed?pushedColor:baseColor);
		g.fill(this);
		g.setColor(Color.green);
		AttributedCharacterIterator title = screenControl.value?titleTrue:titleFalse;
		Simulation.displayWrappedText(title,this.x,this.y,this.width, true);
		g.setColor(Color.cyan);
		g.draw(this);
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