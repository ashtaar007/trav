import java.awt.*;
import java.awt.image.BufferStrategy;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.Rectangle2D;
public class Simulation{
	static Graphics2D g;
	static FontRenderContext frc;
	private static final boolean test = false;
	static Frame mainFrame;
	static Camera camera;
	private Timer timer;
	static boolean isPaused;
	static boolean isSpeedingUp;
	static boolean isSlowingDown;
	static BooleanObject isSetupMode = new BooleanObject(false);
	static BooleanObject isShipBuilderScreen = new BooleanObject(false);
	public static double timeFactor = 1;
    public final static int delay = 15; // every .33 second
    public final static double calculationInterval = 5; // every .33 second
    static int i =0;
    static long lastTime;
    static MouseManager mouseManager;
    static ArrayList<RenderObject> renderObjects;
    static GravityObject[] gravityObjects;
    ArrayList<RenderObject> selectedObjects;
    static Color myColor = new Color(128,0,128,255);
    
    static Map<String,RectangleSwitch> buttons = new HashMap<String,RectangleSwitch>();
    
    
    
	public Simulation(){
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration gc = device.getDefaultConfiguration();
        mainFrame = new Frame(gc);
        mainFrame.setUndecorated(true);
        mainFrame.setIgnoreRepaint(true);
        device.setFullScreenWindow(mainFrame);
        Rectangle bounds = mainFrame.getBounds();
        if(!test)mainFrame.createBufferStrategy(2);
        else mainFrame.createBufferStrategy(1);
        
        camera = new Camera(bounds);
        this.initButtons(bounds);
        
        BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();
        mainFrame.addKeyListener(camera);
        mouseManager = new MouseManager(this);
        mainFrame.addMouseListener(mouseManager);
        mainFrame.addMouseMotionListener(mouseManager);
        
        renderObjects = this.createObjects();
        gravityObjects = this.createGravityObjects();
        selectedObjects =  new ArrayList<RenderObject>(0);
       
        lastTime = System.currentTimeMillis(); 
        ActionListener action = new ActionListener()
        {   
                @Override
            public void actionPerformed(ActionEvent event)
            {
                g = (Graphics2D)bufferStrategy.getDrawGraphics();
                frc = g.getFontRenderContext();
                long timeElapsed = System.currentTimeMillis()-lastTime;
                lastTime += timeElapsed;
                camera.updatePosition(timeElapsed);
        		calculateSimulation(timeElapsed);
                if (!bufferStrategy.contentsLost()) {
                    	//Draw Stuff Here
                     drawStuff(timeElapsed);
                     bufferStrategy.show();
                     g.dispose();
                }
             }
        };

        timer = new Timer(delay, action);
        timer.setInitialDelay(0);
        timer.start();    
	}
	
	
	

	public void drawStuff(long timeElapsed){
		g.setColor(Color.white);
		if(!test)g.fillRect(0,0,camera.bounds.width, camera.bounds.height);
		this.drawSimulation(timeElapsed);
		for (RectangleSwitch button : buttons.values()) {
			button.display(g);
		}
	}
	static void calculateSimulation(long timeElapsed){
		double factoredTimeElapsed=isPaused?0:timeElapsed*timeFactor;
		if(factoredTimeElapsed>100000) {timeFactor = 1; return;}
		double timeLeft=factoredTimeElapsed;
		double dt = Math.min(calculationInterval, timeLeft);
		while(timeLeft>0){
			timeLeft-=calculationInterval;
			calculateThings(dt);
		}
	}
	static void calculateThings(double dt){
		boolean deleteObject = false;
		for (int i = 0; i < renderObjects.size(); i++) {
			deleteObject = renderObjects.get(i).updateObject(dt, gravityObjects);
			if(deleteObject){
				renderObjects.remove(i);
				i--;
			}
        }
		for (int i = 0; i < gravityObjects.length; i++) {
			gravityObjects[i].updateObject(dt, gravityObjects);
		}
	}
	void drawSimulation(long timeElapsed){
		this.drawLine(g,-1000,0,1000,0,Color.red);
		this.drawLine(g,0,-1000,0,1000,Color.red);
		
		
		for (int i = 0; i < renderObjects.size(); i++) {
			this.drawRenderObject(renderObjects.get(i), g);
        }
		for (int i = 0; i < gravityObjects.length; i++) {
			this.drawRenderObject(gravityObjects[i], g);
		}
		for (int i = 0; i < renderObjects.size(); i++) {
			this.drawRenderObjectLabel(renderObjects.get(i),g);
		}
		for (int i = 0; i < gravityObjects.length; i++) {
			this.drawRenderObjectLabel(gravityObjects[i], g);
		}
		if(mouseManager.isDragging){
			g.setColor(Color.blue);
			g.drawLine(
					mouseManager.mouseInitialClickLocationX,
					mouseManager.mouseInitialClickLocationY,
					mouseManager.mouseCurrentLocationX,
					mouseManager.mouseInitialClickLocationY);
			g.drawLine(
					mouseManager.mouseCurrentLocationX,
					mouseManager.mouseInitialClickLocationY,
					mouseManager.mouseCurrentLocationX,
					mouseManager.mouseCurrentLocationY);
			g.drawLine(
					mouseManager.mouseInitialClickLocationX,
					mouseManager.mouseInitialClickLocationY,
					mouseManager.mouseInitialClickLocationX,
					mouseManager.mouseCurrentLocationY);
			g.drawLine(
					mouseManager.mouseInitialClickLocationX,
					mouseManager.mouseCurrentLocationY,
					mouseManager.mouseCurrentLocationX,
					mouseManager.mouseCurrentLocationY);			
		}
		this.displaySelectedShipMenu();
	}
	void displaySelectedShipMenu(){
		Rectangle bounds = camera.bounds;
		g.setColor(Color.cyan);
		int topOfMenuY = (int) (bounds.height*0.95);
		//g.fillRect(0, topOfMenuY, bounds.width, bounds.height);
		int currentX=0;
		int widthPerBox = (int)(0.03*bounds.width);
		for(int i=0;i<this.selectedObjects.size() && currentX<0.9*bounds.width;i++){
			currentX=widthPerBox*i;
			g.setColor(Color.black);			
			g.fillRect(widthPerBox*i,topOfMenuY, widthPerBox, bounds.height);
			g.setColor(Color.cyan);
			g.drawLine(currentX, topOfMenuY, currentX, bounds.height);
			g.setColor(Color.green);
			this.displayWrappedText(this.selectedObjects.get(i).bottomDisplayParagraph,currentX,topOfMenuY,widthPerBox, false);
		}
		
	}
	void initButtons(Rectangle bounds){
		int x = (int)(.95*bounds.width);
		int y = (int)(.95*bounds.height);
		int width = (int)(.05*bounds.width);
		int height = (int)(.025*bounds.height);
	    buttons.put("Setup",new RectangleSwitch(x,y,width,height,isSetupMode,"Play","Setup"));
		y = (int)(.975*bounds.height);
		buttons.put("Builder",new RectangleSwitch(x,y,width,height,isShipBuilderScreen,"Simulator","Builder"));
	}
	public static void displayWrappedText(AttributedCharacterIterator paragraph, int x, int y,int width, boolean isCentered){
		int paragraphEnd = paragraph.getEndIndex();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
		float breakWidth = width-2;
        float drawPosY = y;
        float drawPosX;
        while (lineMeasurer.getPosition() < paragraphEnd) {
        	TextLayout layout = lineMeasurer.nextLayout(breakWidth);
        	if(isCentered) drawPosX = x+1+(float)((breakWidth-layout.getBounds().getWidth())/2);
        	else drawPosX = x+2;
        	drawPosY += layout.getAscent();
        	layout.draw(g, drawPosX, drawPosY);
        	drawPosY += layout.getDescent() + layout.getLeading();
        }
		
	}

	public void drawRenderObject(RenderObject obj, Graphics2D g){
		
        obj.updateDrawLocation(camera);
        
        if(obj.isTargetted){
        	g.setColor(Color.green);
        	g.fill(obj.targettingCircle);
        }
        
        g.setColor(obj.color);
        g.fill(obj.ellipse);
        this.drawLine(g,obj.x,obj.y,obj.x+100*obj.vx,obj.y+100*obj.vy, Color.green); //velocity vectors
        this.drawLine(g,obj.x,obj.y,obj.x+200000*obj.ax,obj.y+200000*obj.ay, Color.red); //acceleration vectors
        //this.drawLine(g,obj.x,obj.y,obj.x+10000000*obj.jx,obj.y+10000000*obj.jy, Color.blue); //jerk vectors
    
	}
	public void drawRenderObjectLabel(RenderObject obj, Graphics2D g){
		int x =(int) obj.drawLocationX;
		int y =(int) obj.drawLocationY;
		g.setColor(myColor);
		//if(!obj.isTargetted){
			g.drawString(obj.name,x,y);
		//}
		if(obj.isTargetted){
			//g.drawString(obj.description,x,y);
		}
	}
	public void drawLine(Graphics2D g, double x1i,double y1i,double x2i,double y2i, Color color){
		int x1 =(int) (camera.getDrawX(x1i));
		int y1 =(int) (camera.getDrawY(y1i));
		int x2 =(int) (camera.getDrawX(x2i));
		int y2 =(int) (camera.getDrawY(y2i));
		g.setColor(color);
		g.drawLine(x1,y1,x2,y2);
	}
	public ArrayList<RenderObject> createObjects(){
		//x, y, radius, vx, vy
		ArrayList<RenderObject> renderObjects = new ArrayList<RenderObject>(0);
		renderObjects.add(new RenderObject(500,0,5,0,.1));
		renderObjects.add(new RenderObject(400,0,5,0,.1));
		renderObjects.add(new RenderObject(300,0,5,0,.1));
		renderObjects.add(new RenderObject(200,0,5,0,.1));
		renderObjects.add(new RenderObject(100,0,5,0,.2));
		
		return renderObjects;
	}
	public GravityObject[] createGravityObjects(){
		//x, y, radius, vx, vy, G
		/*int numGravObjects = 200;
		GravityObject[] gravityObjects = new GravityObject[numGravObjects];
		for(int i=0;i<numGravObjects;i++){
			gravityObjects[i] = new GravityObject(1920*(Math.random()-.5),1080*(Math.random()-.5),5,(Math.random()-.5),(Math.random()-.5),1);
		}*/

		GravityObject[] gravityObjects = {
				new GravityObject(0,0,40,0,0,5)
		};
		return gravityObjects;
	}
	
    

	public static void main(String[] args) {
		ShipBuilder builder = new ShipBuilder();
		Ship ship = builder.createShip();
		builder.addHull(ship, 2000, 0);
		
		
		SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new Simulation();
            }
        });
		
	}
}
