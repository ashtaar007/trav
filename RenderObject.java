import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;
import java.applet.Applet;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
public class RenderObject {
	double x; //actual location of center of ellipse
	double y;
	double drawLocationX;//location in pixels on monitor
	double drawLocationY;
	double radius; //actual radius
	Color color;
	double vx;
	double vy;//velocity components
	double ax;
	double ay;//acceleration components
	double jx;
	double jy;//jerk components
	boolean isTargetted=false;
	Ellipse2D.Double ellipse; //location, radius for purposes of drawing
	Ellipse2D.Double targettingCircle;
	public RenderObject(){
		this.x=0;
		this.y=0;
		this.radius=5;
		this.color=Color.black;
		this.ellipse = new Ellipse2D.Double();
		this.targettingCircle = new Ellipse2D.Double();
	}
	public RenderObject(double x, double y, double radius){
		this.x=x;
		this.y=y;
		this.radius=radius;
		this.color=Color.black;
		this.ellipse = new Ellipse2D.Double();
		this.targettingCircle = new Ellipse2D.Double();
	}
	public RenderObject(double x, double y, double radius, double vx, double vy){
		this.x=x;
		this.y=y;
		this.vx=vx;
		this.vy=vy;
		this.radius=radius;
		this.color=Color.black;
		this.ellipse = new Ellipse2D.Double();
		this.targettingCircle = new Ellipse2D.Double();
	}
	
	void setLocation(double x, double y){
		this.x=x;
		this.y=y;
	}
	void setVelocity(double vx, double vy){
		this.vx=vx;
		this.vy=vy;
	}
	void setAcceleration(double ax, double ay){
		this.ax=ax;
		this.ay=ay;
	}
	void setJerk(double jx, double jy){
		this.jx=jx;
		this.jy=jy;
	}
	void updateObject(double timeElapsed, GravityObject[] gravityObjects){
		//TODO: If necessary, consider using smaller increments... can do multiple iterations instead of using t=timeElapsed
		double t = timeElapsed;
		this.setLocation(x+vx*t+0.5*ax*t*t+1/6*jx*t*t*t,y+vy*t+0.5*ay*t*t+1/6*jy*t*t*t);
		this.setVelocity(vx+ax*t+0.5*jx*t*t,vy+ay*t+0.5*jy*t*t);
		this.setAcceleration(0, 0);//change this if using maneuvering thrusters
		this.setJerk(0, 0);//change this if using maneuvering thrusters
		for(int i=0; i<gravityObjects.length; i++){
			this.addGravity(gravityObjects[i]);
		}
	}
	
	void updateDrawLocation(Camera camera){
		//if zoom is 2 and x is 10 and y is 10, camera located at 0,0
		//should be drawn at 20,20 with radius 20
		
		//if zoom is 1 and x is 10 and y is 10, camera located at -10,-10
		//should be drawn at 20,20 with radius 10
		
		//if zoom is 2 and x is 10 and y is 10, camera located at -10,-10
		//should be drawn at 40,40 with radius 20 
		//upper left corner 20,20 (10 + 10 - 10)*2
		drawLocationX = (x-camera.x-camera.bounds.getCenterX())*camera.zoom+camera.bounds.getCenterX();
		drawLocationY = (y-camera.y-camera.bounds.getCenterY())*camera.zoom+camera.bounds.getCenterY();
		double ellipseUpperLeftCornerX = drawLocationX - radius*camera.zoom;
		double ellipseUpperLeftCornerY = drawLocationY - radius*camera.zoom;
		double ellipseDiameter = 2*radius*camera.zoom;
		ellipse.setFrame(ellipseUpperLeftCornerX,ellipseUpperLeftCornerY,ellipseDiameter,ellipseDiameter);
		
		double targettingCircleUpperLeftCornerX = ellipseUpperLeftCornerX-1;
		double targettingCircleUpperLeftCornerY = ellipseUpperLeftCornerY-1;
		double targettingCircleDiameter = ellipseDiameter+2;
		
		targettingCircle.setFrame(targettingCircleUpperLeftCornerX,targettingCircleUpperLeftCornerY,targettingCircleDiameter,targettingCircleDiameter);
		
	}
	void addGravity(GravityObject gravityObject){
		if (gravityObject == this) return;
		double dx = gravityObject.x-x;
		double dy = gravityObject.y-y;//Vector to gravity object;
		
		double r = Math.sqrt(dx*dx+dy*dy);
		double rInv = 1/r;
		double ax = gravityObject.gravity*rInv*rInv*rInv*dx;// g/mag^2 is acceleration magnitude, multiplied by x/mag, which is unit vectormagInv*magInv*magInv*dx;// g/mag^2 is acceleration magnitude, multiplied by x/mag, which is unit vectormagInv*magInv*magInv*dx;// g/mag^2 is acceleration magnitude, multiplied by x/mag, which is unit vectorrInv*rInv*rInv*dx;// g/r^2 is acceleration rnitude, multiplied by x/r, which is unit vector
		double ay = gravityObject.gravity*rInv*rInv*rInv*dy;
		if(r<gravityObject.radius){
			//gx*=r/gravityObject.radius;
			//gy*=r/gravityObject.radius;
			
			this.ax=0; 
			this.ay=0; 
			this.vx=0; 
			this.vy=0;
		}
		this.ax += ax;
		//System.out.println(this.ax);
		
		this.ay += ay;
		
		
		//this section makes the calculation almost perfect!
		
		double dvx = gravityObject.vx-vx;
		double dvy = gravityObject.vy-vy;
		double i = (dx*dvx+dy*dvy)*rInv*rInv;
		double jx = ax*(dvx/dx-3*i);
		double jy = ay*(dvy/dy-3*i);
		this.jx += jx;
		this.jy += jy;
	}
}
class GravityObject extends RenderObject{
	double gravity;
	public GravityObject(){
		super();
	}
	public GravityObject(double x, double y, double radius, double vx, double vy, double gravity){
		super(x,y,radius,vx,vy);
		this.gravity=gravity;
	}
	void setGravity(double g){
		this.gravity=g;
	}
	
	
}

