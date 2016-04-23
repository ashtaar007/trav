import java.awt.event.*;
public class MouseManager implements MouseListener, MouseMotionListener{
	Simulation targetSimulation;
	public int mouseInitialClickLocationX;
	public int mouseInitialClickLocationY;
	public int mouseCurrentLocationX;
	public int mouseCurrentLocationY;
	public int mouseReleaseLocationX;
	public int mouseReleaseLocationY;
	boolean isDragging = false;
	public MouseManager(Simulation targetSimulation){
		this.targetSimulation=targetSimulation;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {	
	}
	@Override
	public void mousePressed(MouseEvent e) {
		mouseInitialClickLocationX = e.getX();
		mouseInitialClickLocationY = e.getY();
		//loop through and check all menu buttons
		if(e.getButton() == 1){
			Simulation.checkButtonsPressed(mouseInitialClickLocationX, mouseInitialClickLocationY);
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseReleaseLocationX = e.getX();
		mouseReleaseLocationY = e.getY();
		//loop through and check all menu buttons
		
		int minX;
		int maxX;
		int minY;
		int maxY;
		RenderObject currentObject;
		if(e.getButton() == 1){
			Simulation.checkButtonsReleased(mouseReleaseLocationX,mouseReleaseLocationY);
			targetSimulation.selectedObjects.clear();
			if(!isDragging){
				if(e.getModifiers()==17){//M1+Shift
					for(int i=0;i<targetSimulation.renderObjects.size();i++){
						currentObject = targetSimulation.renderObjects.get(i);
						currentObject.isTargetted|=currentObject.ellipse.contains((double)mouseInitialClickLocationX,(double)mouseInitialClickLocationY);
						if(currentObject.isTargetted) targetSimulation.selectedObjects.add(currentObject);
					}
					for(int i=0;i<targetSimulation.gravityObjects.length;i++){
						currentObject = targetSimulation.gravityObjects[i];
						currentObject.isTargetted|=currentObject.ellipse.contains((double)mouseInitialClickLocationX,(double)mouseInitialClickLocationY);
						if(currentObject.isTargetted) targetSimulation.selectedObjects.add(currentObject);
					}
				}
				else{
					for(int i=0;i<targetSimulation.renderObjects.size();i++){
						currentObject = targetSimulation.renderObjects.get(i);
						currentObject.isTargetted=currentObject.ellipse.contains((double)mouseInitialClickLocationX,(double)mouseInitialClickLocationY);
						if(currentObject.isTargetted) targetSimulation.selectedObjects.add(currentObject);
					}
					for(int i=0;i<targetSimulation.gravityObjects.length;i++){
						currentObject = targetSimulation.gravityObjects[i];
						currentObject.isTargetted=currentObject.ellipse.contains((double)mouseInitialClickLocationX,(double)mouseInitialClickLocationY);
						if(currentObject.isTargetted) targetSimulation.selectedObjects.add(currentObject);
					}
				}
			}
			else{//isDragging == true
				//get targets in box, lose targets outside
				if(mouseInitialClickLocationX>=mouseReleaseLocationX){
					maxX=mouseInitialClickLocationX;
					minX=mouseReleaseLocationX;
				}
				else{
					maxX=mouseReleaseLocationX;
					minX=mouseInitialClickLocationX;
				}
				if(mouseInitialClickLocationY>=mouseReleaseLocationY){
					maxY=mouseInitialClickLocationY;
					minY=mouseReleaseLocationY;
				}
				else{
					maxY=mouseReleaseLocationY;
					minY=mouseInitialClickLocationY;
				}
				if(e.getModifiers()==17){//M1+Shift
					for(int i=0;i<targetSimulation.renderObjects.size();i++){
						currentObject = targetSimulation.renderObjects.get(i);
						currentObject.isTargetted|=this.isContained(currentObject,minX,maxX,minY,maxY);
						if(currentObject.isTargetted) targetSimulation.selectedObjects.add(currentObject);
					}
					for(int i=0;i<targetSimulation.gravityObjects.length;i++){
						currentObject = targetSimulation.gravityObjects[i];
						currentObject.isTargetted|=this.isContained(currentObject,minX,maxX,minY,maxY);
						if(currentObject.isTargetted) targetSimulation.selectedObjects.add(currentObject);
					}
					
				}
				else{
					for(int i=0;i<targetSimulation.renderObjects.size();i++){
						currentObject = targetSimulation.renderObjects.get(i);
						currentObject.isTargetted=this.isContained(currentObject,minX,maxX,minY,maxY);
						if(currentObject.isTargetted) targetSimulation.selectedObjects.add(currentObject);
					}
					for(int i=0;i<targetSimulation.gravityObjects.length;i++){
						currentObject = targetSimulation.gravityObjects[i];
						currentObject.isTargetted=this.isContained(currentObject,minX,maxX,minY,maxY);
						if(currentObject.isTargetted) targetSimulation.selectedObjects.add(currentObject);
					}
				}
			}
		}
		isDragging=false;
	}
	public boolean isContained(RenderObject currentRenderObject, int minX, int maxX, int minY, int maxY){
		if(currentRenderObject.drawLocationX>=minX 
				&& currentRenderObject.drawLocationX<=maxX
				&& currentRenderObject.drawLocationY>=minY
				&& currentRenderObject.drawLocationY<=maxY)
			return true;
		else return false;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseCurrentLocationX = e.getX();
		mouseCurrentLocationY = e.getY();
		isDragging=true;
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseCurrentLocationX = e.getX();
		mouseCurrentLocationY = e.getY();
		Simulation.checkButtonsMoved(mouseCurrentLocationX,mouseCurrentLocationY);
		
	}
}
