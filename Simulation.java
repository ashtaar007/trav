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
public class Simulation{//TODO: Create text input boxes
	public static final Rectangle extraRectangle = new Rectangle();
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
    static long lastTime;
    static MouseManager mouseManager;
    static ArrayList<RenderObject> renderObjects;
    static GravityObject[] gravityObjects;
    ArrayList<RenderObject> selectedObjects;
    static Color myColor = new Color(128,0,128,255);
    
    public static final Map<Integer,Button> menuButtonsDefault = new HashMap<Integer,Button>();
    //Simulation buttons
    static Map<String,Map<Integer,Button>> simulationMaps = new HashMap<String,Map<Integer,Button>>();
    //static Map<Integer,Button> simulationMaps.get("buttons") = new HashMap<Integer,Button>();
    static{
    	simulationMaps.put("buttons",new HashMap<Integer,Button>());
    }
    //ShipBuilder buttons
    static Map<String,Map<Integer,Button>> builderMaps = new HashMap<String,Map<Integer,Button>>();
    static{
    	builderMaps.put("buttons",new HashMap<Integer,Button>());
    	builderMaps.put("menus",new HashMap<Integer,Button>());
    	builderMaps.put("menuButtons",menuButtonsDefault);
    	builderMaps.put("textBoxes",new HashMap<Integer,Button>());
    	builderMaps.put("checkBoxes",new HashMap<Integer,Button>());
    }
    
    
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
		this.displayButtons();
	}
	static void calculateSimulation(long timeElapsed){
		double factoredTimeElapsed=(isPaused)?0:timeElapsed*timeFactor;
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
		int height = bounds.height-topOfMenuY;
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
			RenderObject currentObject = this.selectedObjects.get(i);
			extraRectangle.setBounds(currentX, topOfMenuY, widthPerBox, height);
			this.displayWrappedText(currentObject.bottomDisplayParagraph,extraRectangle, false,true);
		}
		
	}
	
	//BUTTON METHODS
	
	
	void initButtons(Rectangle bounds){
		int x = (int)(.95*bounds.width);
		int y = (int)(.975*bounds.height);
		int width = (int)(.05*bounds.width);
		int height = (int)(.025*bounds.height);
		int id = RectangleSwitch.BUILDER;
		simulationMaps.get("buttons").put(id,new RectangleSwitch(id,x,y,width,height,isShipBuilderScreen,"Simulator","Builder"));
		builderMaps.get("buttons").put(id,(Button)simulationMaps.get("buttons").get(RectangleSwitch.BUILDER));
		y = (int)(.95*bounds.height);
		id = RectangleSwitch.SETUP;
		simulationMaps.get("buttons").put(id,new RectangleSwitch(id,x,y,width,height,isSetupMode,"Play","Setup"));
		x = (int)(.45*bounds.width);
		y = (int)(.25*bounds.height);
		String name = "testMenu";
		id=-99;
		builderMaps.get("menus").put(id, new DropDownMenu(id,x,y,width,height,name));
		((DropDownMenu)builderMaps.get("menus").get(id)).add(id,"First lolololool");
		((DropDownMenu)builderMaps.get("menus").get(id)).add(id+1,"Second");
		x = (int)(.65*bounds.width);
		builderMaps.get("textBoxes").put(id, new TextBox(id,x,y,width,height*4,
				"Deviant                                       Art        Lol    Getpwndlolololololololooololoololololololoolololol"
				));
		x = (int)(.25*bounds.width);
		y = (int)(.25*bounds.height);
		width = (int)(.005*bounds.width);
		height = (int)(.01*bounds.height);
		builderMaps.get("checkBoxes").put(id, new CheckBox(id,x,y,width,height));
	}
	static void displayButtons(){
		if(!isShipBuilderScreen.value){//Simulation Buttons
			for (Map<Integer,Button> map : simulationMaps.values()) {
				for (Button button : map.values()) {
					button.display(g);
				}
			}
		}
		else{//ShipBuilder buttons
			for (Map<Integer,Button> map : builderMaps.values()) {
				for (Button button : map.values()) {
					button.display(g);
				}
			}
		}
	}
	public static void checkButtonsPressed(int mouseInitialClickLocationX, int mouseInitialClickLocationY){
		if(!isShipBuilderScreen.value){//Simulation Buttons
			for (Map<Integer,Button> map : simulationMaps.values()) {
				for (Button button : map.values()) {
					button.mousePressed(mouseInitialClickLocationX,mouseInitialClickLocationY);
				}
			}
		}
		else{//ShipBuilder buttons
			for (Map<Integer,Button> map : builderMaps.values()) {
				for (Button button : map.values()) {
					button.mousePressed(mouseInitialClickLocationX,mouseInitialClickLocationY);
				}
			}
		}
	}
	public static void checkButtonsReleased(int mouseReleaseLocationX, int mouseReleaseLocationY){
		if(!isShipBuilderScreen.value){//Simulation Buttons
			for (Map<Integer,Button> map : simulationMaps.values()) {
				for (Button button : map.values()) {
					button.mouseReleased(mouseReleaseLocationX,mouseReleaseLocationY);
				}
			}
		}
		else{//ShipBuilder buttons
			for (Map<Integer,Button> map : builderMaps.values()) {
				for (Button button : map.values()) {
					button.mouseReleased(mouseReleaseLocationX,mouseReleaseLocationY);
				}
			}
		}
	}
	public static void checkButtonsMoved(int mouseCurrentLocationX, int mouseCurrentLocationY){
		if(!isShipBuilderScreen.value){//Simulation Buttons
			for (Map<Integer,Button> map : simulationMaps.values()) {
				for (Button button : map.values()) {
					button.mouseMoved(mouseCurrentLocationX,mouseCurrentLocationY);
				}
			}
		}
		else{//ShipBuilder buttons
			for (Map<Integer,Button> map : builderMaps.values()) {
				for (Button button : map.values()) {
					button.mouseMoved(mouseCurrentLocationX,mouseCurrentLocationY);
				}
			}
		}
	}
	public static TextBox getPressedTextBox(){
		if(isShipBuilderScreen.value){
			for (Button textBox : builderMaps.get("textBoxes").values()) {
				if (textBox.isBeingPushed())
					return (TextBox)textBox;
			}
		}
		return null;
	}
	public static void displayWrappedText(AttributedCharacterIterator paragraph, Rectangle rect, boolean widthCentered, boolean heightCentered){
		int paragraphEnd = paragraph.getEndIndex();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
		float breakWidth = rect.width-2;
        float drawPosY = rect.y;
        float drawPosX;
        float maxY = rect.y+rect.height;
        float height = rect.height;
        float drawPosXInitial =widthCentered?rect.x+1:rect.x+2;
        float totalLayoutHeight=0;
        float nextHeight=0;
        if(heightCentered){
	        while (lineMeasurer.getPosition() < paragraphEnd) {
	        	TextLayout layout = lineMeasurer.nextLayout(breakWidth);
	        	nextHeight=layout.getAscent()+layout.getLeading()+layout.getDescent();
	        	if(totalLayoutHeight+nextHeight>height)break;
	        	totalLayoutHeight+=nextHeight;
	        	
	        }
	        lineMeasurer.setPosition(0);
	        drawPosY += (height - totalLayoutHeight)/2;
        }
        while (lineMeasurer.getPosition() < paragraphEnd) {
        	TextLayout layout = lineMeasurer.nextLayout(breakWidth);
        	if(widthCentered) drawPosX = drawPosXInitial+(float)((breakWidth-layout.getBounds().getWidth())/2);
        	else drawPosX = drawPosXInitial;
        	drawPosY += layout.getAscent();
        	if(drawPosY+layout.getDescent()<=maxY)
        		layout.draw(g, drawPosX, drawPosY);
        	else break;
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
