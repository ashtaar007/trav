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
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseReleaseLocationX = e.getX();
		mouseReleaseLocationY = e.getY();
		int minX;
		int maxX;
		int minY;
		int maxY;
		if(!isDragging&&e.getButton()==1){
			for(int i=0;i<targetSimulation.renderObjects.size();i++){
				targetSimulation.renderObjects.get(i).isTargetted=targetSimulation.renderObjects.get(i).ellipse.contains((double)mouseInitialClickLocationX,(double)mouseInitialClickLocationY);
			}
			for(int i=0;i<targetSimulation.gravityObjects.length;i++){
				targetSimulation.gravityObjects[i].isTargetted=targetSimulation.gravityObjects[i].ellipse.contains((double)mouseInitialClickLocationX,(double)mouseInitialClickLocationY);
			}
		}
		if(isDragging&&e.getButton()==1){
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
			for(int i=0;i<targetSimulation.renderObjects.size();i++){
				targetSimulation.renderObjects.get(i).isTargetted=this.isContained(targetSimulation.renderObjects.get(i),minX,maxX,minY,maxY);
			}
			for(int i=0;i<targetSimulation.gravityObjects.length;i++){
				targetSimulation.gravityObjects[i].isTargetted=this.isContained(targetSimulation.gravityObjects[i],minX,maxX,minY,maxY);
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
	}
}
