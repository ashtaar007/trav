import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;

class TextBox extends Button{
	/* Note: Currently width is shrunk and spaces are capped 
	 * to account for defective LineBreakMeasurer which doesn't bound spaces.
	 * Will need to write new LineBreakMeasurer if care about this. Performance could
	 * probably be a lot better too, if that turns out to matter.
	 * 
	 * Highlighting (with mouse drag or shift arrow), copy/paste could be added too.
	 * 
	 * Most important priority: make caret appear in empty string case.
	 */
	
	
	
	
	boolean useLeftCaret = false;
	//class can't have it's own linemeasurer, because need new frc each frame
	final static Rectangle extraRectangle = new Rectangle();
	final static Point2D caretLocation = new Point();
	final static AttributedCharacterIterator singleSpace = (new AttributedString(" ",textAttributes)).getIterator();
	int lastMouseClickX = 0;
	int lastMouseClickY = 0;
	String titleText;
	int caretIndex=0;
	
	
	ArrayList<LayoutContainer> layoutContainers = new ArrayList<LayoutContainer>();
	LayoutContainer caretContainer;
	
	public TextBox(int x, int y, int width, int height, String title){
		super(x,y,width,height,title);
		this.titleText = title;
		trimTitle();
		if(titleText.length()>0)
			this.title=(new AttributedString(titleText,textAttributes)).getIterator();
	}
	public void reconstructTitle(boolean value){
		trimTitle();
		if(titleText.length()>0){
			this.title=(new AttributedString(titleText,textAttributes)).getIterator();
		}
		updateLayouts(title,true,true);
		updateCaretAttributesFromIndex();
		useLeftCaret=value;
		//setCaretUse();
	}
	public void trimTitle(){
		if(titleText.length()==0)return;
		String newString=""+titleText.charAt(0);
		for(int i=1;i<titleText.length();i++){
			char cA = titleText.charAt(i-1);
			char cB = titleText.charAt(i);
			if(!(cB==' '&&cA==' ')){
				newString+=cB;
			}
		}
		titleText=newString;
	}
	public void display(Graphics2D g){
		g.setColor(isBeingPushed?baseColor:baseColor);
		g.fill(this);
		if(caretIndex==0)
			useLeftCaret=true;
		if(titleText.length()>0){
			g.setColor(textColor);
			this.updateLayouts(title, true, true);
			this.displayWrappedEditableText(true,true);
		}
		else{
			g.setColor(textColor);
			this.updateLayouts(singleSpace, true, true);
			this.displayWrappedEditableText(true,true);
		}
		g.setColor(borderColor);
		g.draw(this);
		if(isBeingPushed){//display Carat
			
		}
	}
	public void updateLayouts(AttributedCharacterIterator title,boolean widthCentered, boolean heightCentered){
		int paragraphEnd = title.getEndIndex();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(title, Simulation.frc);
		float breakWidth = width-5;
        float totalLayoutHeight=0;
        float nextHeight=0;
        
        layoutContainers.clear();
        
        
        TextLayout layout;
    	float drawPosX;
    	float drawPosY = y;
    	int initialIndex;
    	int containerIndex=0;
	    while (lineMeasurer.getPosition() < paragraphEnd) {
	    	initialIndex = lineMeasurer.getPosition();
	    	layout = lineMeasurer.nextLayout(breakWidth);
	    	drawPosX=widthCentered?
	    			x+1+(float)((breakWidth-layout.getBounds().getWidth())/2):
	        		x+2;
	    	drawPosY += layout.getAscent();
	    	layoutContainers.add(new LayoutContainer(layout,drawPosX,drawPosY,initialIndex, containerIndex));
	    	containerIndex++;
	    	nextHeight=layout.getAscent()+layout.getLeading()+layout.getDescent();
	    	totalLayoutHeight+=nextHeight;
	        if(totalLayoutHeight+nextHeight>height)break;
	        drawPosY += layout.getDescent() + layout.getLeading();
	    }
	    if(heightCentered){
	    	float extraY = (height - totalLayoutHeight)/2;
	    	for(int i=0;i<layoutContainers.size();i++){
	    		layoutContainers.get(i).drawPosY+=extraY;
	    	}
	    }
	}
	public void displayWrappedEditableText(boolean widthCentered, boolean heightCentered){
		Graphics2D g = Simulation.g;
		for(int i=0;i<layoutContainers.size();i++){
			LayoutContainer currentContainer = layoutContainers.get(i);
			g.setColor(textColor);
			boolean displayedCaret = currentContainer.display(g, caretIndex, caretLocation, useLeftCaret, isBeingPushed);
			if(displayedCaret) updateCaretAttributesFromIndex(currentContainer);
    	}
	}
	public void setCaretUse(){/*
		if(caretIndex==caretContainer.initialIndex)
			useLeftCaret=true;
		if(caretIndex==caretContainer.getEndIndex())
			useLeftCaret=false;
		System.out.println("Set Caret:" + caretIndex + " to " + useLeftCaret);
		System.out.println(caretContainer.initialIndex);
		System.out.println(caretContainer.getEndIndex());
		System.out.println(caretContainer.containerIndex);
		System.out.println(layoutContainers.size()-1);*/
	}
	public void backspaceActions(){
		updateCaretAttributesFromIndex();
		if (caretIndex!=0)
			caretIndex--;
		else return;
		useLeftCaret=false;
	}
	public void endActions(){
		updateCaretAttributesFromIndex();
		useLeftCaret=false;
	}
	public void moveCaretRight(){
		updateCaretAttributesFromIndex();
		if (caretIndex==titleText.length()){
			useLeftCaret=false;
			return;
		}
		if (caretIndex<=caretContainer.getEndIndex())
			caretIndex++;
		if(caretIndex==caretContainer.getEndIndex())
			useLeftCaret=false;
		if(caretIndex==caretContainer.getEndIndex()+1){
			useLeftCaret=true;
			caretIndex--;
		}
	}
	public void moveCaretLeft(){
		updateCaretAttributesFromIndex();
		if (caretIndex!=0)
			caretIndex--;
		else return;
		if(caretIndex==caretContainer.initialIndex)
			useLeftCaret=true;
		if(caretIndex==caretContainer.initialIndex-1){
			useLeftCaret=false;
			caretIndex++;
		}
	}
	public void moveCaretUp(){
		LayoutContainer previousContainer;
		updateCaretAttributesFromIndex();
		if(caretContainer.containerIndex==0)
			return;
		previousContainer = layoutContainers.get(caretContainer.containerIndex-1);
		float extraHeight = caretContainer.layout.getAscent()
				+previousContainer.layout.getLeading()
				+previousContainer.layout.getDescent();
		caretLocation.setLocation(caretLocation.getX(), caretLocation.getY()-extraHeight);
		this.updateCaretIndexFromLocation();
	}
	public void moveCaretDown(){
		LayoutContainer nextContainer;
		updateCaretAttributesFromIndex();
		if(caretContainer.containerIndex==layoutContainers.size()-1)
			return;
		nextContainer = layoutContainers.get(caretContainer.containerIndex+1);
		float extraHeight = nextContainer.layout.getAscent()
				+caretContainer.layout.getLeading()
				+caretContainer.layout.getDescent();
		caretLocation.setLocation(caretLocation.getX(), caretLocation.getY()+extraHeight);
		this.updateCaretIndexFromLocation();
	}
	public void setLastClick(int mouseReleaseLocationX, int mouseReleaseLocationY){
		lastMouseClickX = mouseReleaseLocationX;
		lastMouseClickY = mouseReleaseLocationY;
		if(titleText.length()>0){
			caretLocation.setLocation(mouseReleaseLocationX, mouseReleaseLocationY);
			this.updateCaretIndexFromLocation();
		}	
	}
	public void updateCaretIndexFromLocation(){
		for(int i=0;i<layoutContainers.size();i++){
			LayoutContainer currentContainer = layoutContainers.get(i);
			//System.out.println(currentContainer);

			if(currentContainer.containsCaret(caretLocation, x, width)){
				TextHitInfo hit = currentContainer.layout.hitTestChar(
						(float)(caretLocation.getX()-currentContainer.drawPosX),
						(float)(caretLocation.getY()-currentContainer.drawPosY));
				caretIndex = currentContainer.initialIndex + hit.getInsertionIndex();
				useLeftCaret = (caretLocation.getX()<=x+width/2);
				updateCaretAttributesFromIndex(currentContainer);
    		}
			
		}
	}
	public void updateCaretAttributesFromIndex(LayoutContainer currentContainer){//updates Location and CaretContainer from Index
		caretContainer = currentContainer;
		int caretLayoutIndex = caretIndex-currentContainer.initialIndex;
		TextHitInfo hit = TextHitInfo.afterOffset(caretLayoutIndex);
		currentContainer.layout.hitToPoint(hit,caretLocation);
		caretLocation.setLocation(caretLocation.getX()+currentContainer.drawPosX,caretLocation.getY()+currentContainer.drawPosY);
		caretContainer = currentContainer;
	}
	public void updateCaretAttributesFromIndex(){ //updates Location and CaretContainer from Index
		for(int i=0;i<layoutContainers.size();i++){
			LayoutContainer currentContainer = layoutContainers.get(i);
			if(currentContainer.containsCaret(caretIndex,useLeftCaret)){
				updateCaretAttributesFromIndex(currentContainer);
			}
		}
	}
	
	
	
}