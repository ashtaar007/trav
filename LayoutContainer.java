import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;

class LayoutContainer{
	final static Rectangle extraRectangle = new Rectangle();
	TextLayout layout;
	float drawPosX;
	float drawPosY;
	int initialIndex;
	int containerIndex;
	public LayoutContainer(TextLayout layout, float drawPosX, float drawPosY, int initialIndex, int containerIndex){
		this.layout=layout;
		this.drawPosX=drawPosX;
		this.drawPosY=drawPosY;
		this.initialIndex=initialIndex;
		this.containerIndex=containerIndex;
	}
	public boolean display(Graphics2D g, int caretIndex, Point2D caretLocation, boolean useLeftCaret, boolean isBeingPushed){
		layout.draw(g,drawPosX,drawPosY);
		if(isBeingPushed&&containsCaret(caretIndex,useLeftCaret)){
			g.setColor(Color.red);
			
			
			int caretLayoutIndex = caretIndex-initialIndex;
			TextHitInfo hit = TextHitInfo.beforeOffset(caretLayoutIndex);
			Shape caret = layout.getCaretShape(hit);
    		g.translate(drawPosX, drawPosY);
    		g.draw(caret);
    		g.translate(-drawPosX, -drawPosY);
    		return true;
		}
		return false;
	}
	public int getEndIndex(){
		return initialIndex+layout.getCharacterCount();
	}
	public boolean containsCaret(int caretIndex, boolean useLeftCaret){
		boolean value = ((initialIndex<=caretIndex && useLeftCaret) || initialIndex<caretIndex)
				&& ((caretIndex<=getEndIndex()&&!useLeftCaret) || caretIndex<getEndIndex());
		/*System.out.println("initialIndex:"+initialIndex+" caretIndex:"+caretIndex
				+" useLeftCaret:"+useLeftCaret+" endIndex:"+getEndIndex()+" result:"+value);*/
		
		
		return value;
	}
	public boolean containsCaret(Point2D caretLocation, int textBoxLeftEdge,int textBoxWidth){
		extraRectangle.setFrame(textBoxLeftEdge,(int)(drawPosY-layout.getAscent()),textBoxWidth,(int)(layout.getAscent()+layout.getDescent()));
		return extraRectangle.contains(caretLocation);
			
	}
	public String toString(){
		String s = "containerIndex:"+containerIndex+" initialIndex:"+initialIndex+" endIndex:"+getEndIndex();
		return s;
	}
}
