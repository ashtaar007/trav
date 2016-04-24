import java.awt.*;
import java.awt.event.*;
public class Camera implements KeyListener{
	double x; //location of center of camera (used for transforms)
	double y;
	final double centerX; //location of center of screen
	final double centerY;
	double zoom;
	double zoomSpeed=1.05;
	double panSpeed=15*Simulation.delay;//This is divided by timeElapsed later, which is 17->25
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
		centerX = rect.getCenterX();
		centerY = rect.getCenterY();
		zoom=1.0;
		bounds = rect;
	}
	
	public void keyReleased(KeyEvent e) {
		
		
		//check if Text Box is pressed - if so, need to perform typing actions instead
		//might be able to skip this as an optimization later
		int keyCode = e.getKeyCode();
		TextBox currentTextBox = Simulation.getPressedTextBox();
		if(currentTextBox != null){
			
			return;
		}
		if(keyCode==KeyEvent.VK_RIGHT){
			isMovingRight=false;
		}
		if(keyCode==KeyEvent.VK_LEFT){
			isMovingLeft=false;
		}
		if(keyCode==KeyEvent.VK_UP){
			isMovingUp=false;
		}
		if(keyCode==KeyEvent.VK_DOWN){
			isMovingDown=false;
		}
		if(keyCode==KeyEvent.VK_Z){
			isZoomingIn=false;
		}
		if(keyCode==KeyEvent.VK_X){
			isZoomingOut=false;
		}
		if(keyCode==KeyEvent.VK_C){
			Simulation.isSpeedingUp=false;
		}
		if(keyCode==KeyEvent.VK_V){
			Simulation.isSlowingDown=false;
		}
	}
	public void keyPressed(KeyEvent e) {
		//check if Text Box is pressed - if so, need to perform typing actions instead
		int keyCode = e.getKeyCode();
		if(keyCode==KeyEvent.VK_ESCAPE){
        	System.exit(1);
		}
		TextBox currentTextBox = Simulation.getPressedTextBox();
		if(currentTextBox != null){
			if(((int)e.getKeyChar()) != 65535 &&keyCode!=127&&keyCode!=8&&keyCode!=92&&keyCode!=10){
				System.out.println(e.getKeyChar()+" Char code: " + (int)e.getKeyChar() + " Event: "+e);
				String currentString = currentTextBox.titleText;
				currentTextBox.titleText = currentString.substring(0, currentTextBox.caretIndex).concat(""+e.getKeyChar())
						.concat(currentString.substring(currentTextBox.caretIndex));
				currentTextBox.reconstructTitle();
			}
			return;
		}
		
		
		if(keyCode==KeyEvent.VK_RIGHT){
			isMovingRight=true;
		}
		if(keyCode==KeyEvent.VK_LEFT){
			isMovingLeft=true;
		}
		if(keyCode==KeyEvent.VK_UP){
			isMovingUp=true;
		}
		if(keyCode==KeyEvent.VK_DOWN){
			isMovingDown=true;
		}
		if(keyCode==KeyEvent.VK_Z){
			isZoomingIn=true;
		}
		if(keyCode==KeyEvent.VK_X){
			isZoomingOut=true;
		}
		if(keyCode==KeyEvent.VK_C){
			Simulation.isSpeedingUp=true;
		}
		if(keyCode==KeyEvent.VK_V){
			Simulation.isSlowingDown=true;
		}
		if(keyCode==KeyEvent.VK_SPACE){
			Simulation.isPaused=!Simulation.isPaused;
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
		if(Simulation.isSpeedingUp) Simulation.timeFactor*=1.02;//this actually controls simulation speed, not the camera
		if(Simulation.isSlowingDown) Simulation.timeFactor/=1.02;
	}
	public double getDrawX(double xi){
		double drawX = ((xi-x-centerX)*zoom+centerX);
		return drawX;
	}
	public double getDrawY(double yi){
		double drawY = ((yi-y-centerY)*zoom+centerY);
		return drawY;
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
}
