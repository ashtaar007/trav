import java.awt.*;
import java.awt.event.*;
public class Camera {
	double x; //location of center of camera
	double y; 
	double zoom;
	double zoomSpeed=1.05;
	double panSpeed=5*Simulation.delay;//This is divided by timeElapsed later, which is 17->25
	boolean isMovingUp=false;
	boolean isMovingDown=false;
	boolean isMovingLeft=false;
	boolean isMovingRight=false;
	boolean isZoomingIn=false;
	boolean isZoomingOut=false;
	Rectangle bounds;
	public Camera (Rectangle rect){
		x=-rect.getCenterX();
		y=-rect.getCenterY();
		zoom=1.0;
		bounds = rect;
	}
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_RIGHT){
			isMovingRight=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_LEFT){
			isMovingLeft=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_UP){
			isMovingUp=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_DOWN){
			isMovingDown=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_Z){
			isZoomingIn=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_X){
			isZoomingOut=false;
		}
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_RIGHT){
			isMovingRight=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_LEFT){
			isMovingLeft=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_UP){
			isMovingUp=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_DOWN){
			isMovingDown=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_Z){
			isZoomingIn=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_X){
			isZoomingOut=true;
		}
	}
	public void updatePosition(long timeElapsed){
		double moveAmount = panSpeed/(Math.min(1, zoom)*timeElapsed);
		if(isMovingRight) x+=moveAmount;
		if(isMovingLeft) x-=moveAmount;
		if(isMovingUp) y-=moveAmount;
		if(isMovingDown) y+=moveAmount;
		if(isZoomingIn) zoom*=zoomSpeed;
		if(isZoomingOut) zoom*=1/zoomSpeed;
	}
}
