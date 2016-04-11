import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;
import java.applet.Applet;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
public class Simulation{
	static JFrame mainFrame;
	static Camera camera;
	private Timer timer;
    public final static int delay = 8; // every .33 second
    int i =0;
    long lastTime;
	public Simulation(){
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration gc = device.getDefaultConfiguration();
        mainFrame = new JFrame(gc);
        mainFrame.setUndecorated(true);
        mainFrame.setIgnoreRepaint(true);
        device.setFullScreenWindow(mainFrame);
        Rectangle bounds = mainFrame.getBounds();
        mainFrame.createBufferStrategy(2);
        camera = new Camera(bounds);
        BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();
        this.addEscapeListener(mainFrame);
        mainFrame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				Simulation.camera.keyReleased(e);
			}
			@Override
			public void keyPressed(KeyEvent e) {
				Simulation.camera.keyPressed(e);
			}
		});
        mainFrame.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {		
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {				
			}
			@Override
			public void mousePressed(MouseEvent arg0) {			
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
        mainFrame.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
			}
			@Override
			public void mouseMoved(MouseEvent e) {	
			}
		});
        
        RenderObject[] renderObjects = this.createObjects();
        GravityObject[] gravityObjects = this.createGravityObjects();
       
        lastTime = System.currentTimeMillis();        
        ActionListener action = new ActionListener()
        {   
                @Override
            public void actionPerformed(ActionEvent event)
            {
                Graphics g = bufferStrategy.getDrawGraphics();
                if (!bufferStrategy.contentsLost()) {
                    	//Draw Stuff Here
                    drawStuff(g, bounds, renderObjects, gravityObjects);
                    	
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
	
	

	public void drawStuff(Graphics g1, Rectangle bounds, RenderObject[] renderObjects, GravityObject[] gravityObjects){
		Graphics2D g = (Graphics2D) g1;
    	//draw background;
		i++;
		g.setColor(Color.white);
		g.fillRect(0,0,bounds.width, bounds.height);
		long timeElapsed = System.currentTimeMillis()-lastTime;
		System.out.println(timeElapsed);
		camera.updatePosition(timeElapsed);
		this.drawLine(g,-1000,0,1000,0,Color.red);
		this.drawLine(g,0,-1000,0,1000,Color.red);
		
		for (int i = 0; i < renderObjects.length; i++) {
			renderObjects[i].updateObject(timeElapsed, gravityObjects);
			this.drawRenderObject(renderObjects[i], g);
            

            //g.drawLine((int)p.getCenterX() - 5, (int)p.getCenterY(), (int)p.getCenterX() + 5, (int)p.getCenterY());

            //g.drawLine((int)p.getCenterX(), (int)p.getCenterY() -5, (int)p.getCenterX(), (int)p.getCenterY() + 5);
            
            //g.fill(p);
	
            //g.drawString("P"+ i + "(" + p.getCenterX() + "," + p.getCenterY() + ")", (float) p.getMaxX(), (float) p.getMaxY());

        }
		for (int i = 0; i < gravityObjects.length; i++) {
			gravityObjects[i].updateObject(timeElapsed, gravityObjects);
			this.drawRenderObject(gravityObjects[i], g);
		}
		lastTime += timeElapsed;
		
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
        
           
	}
	public void drawLine(Graphics2D g, double x1i,double y1i,double x2i,double y2i, Color color){
		int x1 =(int) ((x1i-camera.x-camera.bounds.getCenterX())*camera.zoom+camera.bounds.getCenterX());
		int y1 =(int) ((y1i-camera.y-camera.bounds.getCenterY())*camera.zoom+camera.bounds.getCenterY());
		int x2 =(int) ((x2i-camera.x-camera.bounds.getCenterX())*camera.zoom+camera.bounds.getCenterX());
		int y2 =(int) ((y2i-camera.y-camera.bounds.getCenterY())*camera.zoom+camera.bounds.getCenterY());
		g.setColor(color);
		g.drawLine(x1,y1,x2,y2);
	}
	public RenderObject[] createObjects(){
		RenderObject[] renderObjects = {
				new RenderObject(500,0,5,0,.1),
				new RenderObject(400,0,5,0,.1),
				new RenderObject(300,0,5,0,.1)
				
		};
		return renderObjects;
	}
	public GravityObject[] createGravityObjects(){
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
	        }
	    };
	    mainframe.getRootPane().registerKeyboardAction(escListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
}
