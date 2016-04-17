import java.awt.*;
import java.awt.image.BufferStrategy;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;
import java.applet.Applet;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
public class Simulation{
	private static final boolean test = false;
	static JFrame mainFrame;
	static Camera camera;
	private Timer timer;
	static boolean isPaused;
	static boolean isSpeedingUp;
	static boolean isSlowingDown;
	public static double timeFactor = 1;
    public final static int delay = 8; // every .33 second
    static int i =0;
    static long lastTime;
    static MouseManager mouseManager;
    static ArrayList<RenderObject> renderObjects;
    static GravityObject[] gravityObjects;
	public Simulation(){
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration gc = device.getDefaultConfiguration();
        mainFrame = new JFrame(gc);
        
        mainFrame.setUndecorated(true);
        mainFrame.setIgnoreRepaint(true);
        device.setFullScreenWindow(mainFrame);
        Rectangle bounds = mainFrame.getBounds();
        if(!test)mainFrame.createBufferStrategy(2);
        else mainFrame.createBufferStrategy(1);
        
        camera = new Camera(bounds);
        BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();
        this.addEscapeListener(mainFrame);
        mainFrame.addKeyListener(camera);
        mouseManager = new MouseManager(this);
        mainFrame.addMouseListener(mouseManager);
        mainFrame.addMouseMotionListener(mouseManager);
        
        renderObjects = this.createObjects();
        gravityObjects = this.createGravityObjects();
       
        lastTime = System.currentTimeMillis();        
        ActionListener action = new ActionListener()
        {   
                @Override
            public void actionPerformed(ActionEvent event)
            {
                Graphics g = bufferStrategy.getDrawGraphics();
                if (!bufferStrategy.contentsLost()) {
                    	//Draw Stuff Here
                    drawStuff(g, bounds);
                    	
                     //
                     bufferStrategy.show();
                     g.dispose();
                }
             }
        };

        timer = new Timer(delay, action);
        timer.setInitialDelay(0);
        timer.start();    
	}
	
	
	

	public void drawStuff(Graphics g1, Rectangle bounds){
		Graphics2D g = (Graphics2D) g1;
    	//draw background;
		i++;
		g.setColor(Color.white);
		if(!test)g.fillRect(0,0,bounds.width, bounds.height);
		long timeElapsed = System.currentTimeMillis()-lastTime;
		//System.out.println(timeElapsed);
		camera.updatePosition(timeElapsed);
		this.drawLine(g,-1000,0,1000,0,Color.red);
		this.drawLine(g,0,-1000,0,1000,Color.red);
		double factoredTimeElapsed;
		if(this.isPaused){
			factoredTimeElapsed=0;
		}
		else{
			factoredTimeElapsed=timeElapsed*timeFactor;
		}
		boolean deleteObject = false;
		for (int i = 0; i < renderObjects.size(); i++) {
			deleteObject = renderObjects.get(i).updateObject(factoredTimeElapsed, gravityObjects);
			if(deleteObject){
				renderObjects.remove(i);
				i--;
				continue;
			}
			this.drawRenderObject(renderObjects.get(i), g);
            

            //g.drawLine((int)p.getCenterX() - 5, (int)p.getCenterY(), (int)p.getCenterX() + 5, (int)p.getCenterY());

            //g.drawLine((int)p.getCenterX(), (int)p.getCenterY() -5, (int)p.getCenterX(), (int)p.getCenterY() + 5);
            
            //g.fill(p);
	
            //g.drawString("P"+ i + "(" + p.getCenterX() + "," + p.getCenterY() + ")", (float) p.getMaxX(), (float) p.getMaxY());

        }
		for (int i = 0; i < gravityObjects.length; i++) {
			gravityObjects[i].updateObject(factoredTimeElapsed, gravityObjects);
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
		this.drawTestTextLayout(g);
		lastTime += timeElapsed;
		
	}
	private LineBreakMeasurer lineMeasurer;
	private int paragraphStart;
	private int paragraphEnd;
	private static final 
    Hashtable<TextAttribute, Object> map =
       new Hashtable<TextAttribute, Object>();

	static {
		map.put(TextAttribute.FAMILY, "Serif");
		map.put(TextAttribute.SIZE, 18);
		//map.put(TextAttribute.WEIGHT, 1);
		//map.put(TextAttribute.BACKGROUND, Color.WHITE);
	} 
	private static AttributedString vanGogh = new AttributedString(
	        "Many people believe that Vincent van Gogh painted his best works " +
	        "during the two-year period he spent in Provence. Here is where he " +
	        "painted The Starry Night--which some consider to be his greatest " +
	        "work of all. However, as his artistic brilliance reached new " +
	        "heights in Provence, his physical and mental health plummeted. ",
	        map);

	public void drawTestTextLayout(Graphics2D g){
		if (lineMeasurer == null) {
            AttributedCharacterIterator paragraph = vanGogh.getIterator();
            paragraphStart = paragraph.getBeginIndex();
            paragraphEnd = paragraph.getEndIndex();
            FontRenderContext frc = g.getFontRenderContext();
            lineMeasurer = new LineBreakMeasurer(paragraph, frc);
        }
		//modify breakWidth for variable wrapping widths
		float breakWidth = (float)mainFrame.getSize().width;
        float drawPosY = 0;
        lineMeasurer.setPosition(paragraphStart);
        while (lineMeasurer.getPosition() < paragraphEnd) {
        	
        	TextLayout layout = lineMeasurer.nextLayout(breakWidth);
        	float drawPosX = 0;
        	drawPosY += layout.getAscent();
        	layout.draw(g, drawPosX, drawPosY);
        	drawPosY += layout.getDescent() + layout.getLeading();
        }
	}
	Color myColor = new Color(128,0,128,255);
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
	
	public void addEscapeListener(JFrame mainframe) {
	    ActionListener escListener = new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	timer.stop();
	        	mainframe.dispose();
	        	System.exit(1);
	        }
	    };
	    mainframe.getRootPane().registerKeyboardAction(escListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
}
