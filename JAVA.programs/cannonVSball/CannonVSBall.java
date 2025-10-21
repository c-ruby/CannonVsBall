//program  CannonVSBall - CMSC 3320/001 - Group 7
//
//Caleb Ruby - rub4133@pennwest.edu
//Adir Turgeman - tur28711@pennwest.edu
//Caleb Rachocki - rac3146@pennwest.edu
//Ryan Miller - mil0780@pennwest.edu

package CannonVSBall;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;

public class CannonVSBall extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable, MouseListener, MouseMotionListener, ItemListener
{	

	
	
	
	
	//TODO add moon and gravity to menu 
	
	
	
	//TODO fix projectile motion
	
	
	
	
		

	//object class
	class Objc extends Canvas
	{
	//initialization 
		private static final long serialVersionUID = 11L;
		private int ScreenWidth;
		private int ScreenHeight;
		private int SObj;
		
		private int x,y;
		private boolean rect = true;
		private boolean clear = false;
		
		private boolean tail = true;	//tail flag(?)
		
		//movement variables
		int ymin;
		int ymax;
		int xmin;
		int xmax;
		int xold;
		int yold;
		
		boolean right, down;
		
		Image buffer;
		Graphics g;
		
		Vector<Rectangle> walls = new Vector<Rectangle>();
		private final Rectangle ZERO = new Rectangle(0,0,0,0);
		private Rectangle ball = new Rectangle(0,0,0,0);
		
		int rectX, rectY, rectWidth, rectHeight;
		
		
		//polygon data 
		private final int BARREL_LENGTH = 90;	//barrel length constant 
		private final int BARREL_WIDTH	= 21;	//barrel width constant 
		
		
		private Polygon poly = new Polygon();
		Rectangle circle = new Rectangle();
		int l = BARREL_LENGTH; 	//polygon length
		int w = BARREL_WIDTH;
		int deg = 30;	//variable which is converted to radians 
		double currentRad;
		boolean destroyed = false;
		
		//poly center line 
		Point a = new Point();
		Point c = new Point();
			//will be calculated with respect to a
			int c_Xoffset;		
			int c_Yoffset;
			
		//poly points 
		Point a1 = new Point();
		Point a2 = new Point();
		Point c1 = new Point();
		Point c2 = new Point();
		
	//projectile data 		
		//velocity and position 
		double V0Y;
		double V0X;
		double xVelocity;
		double yVelocity;
		boolean vChanged = true;
		
		int projectileTime;
		double gravity = 32.03;
		int vWeight = 600;	//initial velocity value from scrollbar
		
		long prevTime;	//seconds
		long currentTime; //seconds
		long projectileOGTime;
		
		//projectile object 
		int pSize = BARREL_WIDTH-4;	//size of the projectile set to same as the barrel
		Rectangle projectile = new Rectangle(0, 0, pSize, pSize);
		
		//flags
		boolean drawCannonBall = false;
		
		//time
		long time = 0;
		long startTimeMillis = 0;
		
		
	//constructor
		public Objc(int SB, int w, int h	)
		{
			down = true;
			right = true;
			
			startTimeMillis = System.currentTimeMillis();
			
			ScreenWidth = w;
			ScreenHeight = h;
			SObj = SB;
			rect = false;
			clear = false;
			
			calcMinMax();
			y = ymin;
			x = xmin;	
			
			ball.setBounds(x, y, SObj, SObj);
			
			
			
			//find points a and c from which to create the cannon polygon 
			a.setLocation(Perimeter.getX()+Perimeter.getWidth() , Perimeter.getY()+Perimeter.getHeight());
			
			findC();
			
			
			
			
			//poly.getBounds(); //gets the rectangle bounding poly 
				//intersect() also works 
			
			//drawPolygon(Polygon p) & fillPolygon(Polygon p) can be used to create a polygon in graphics 
		}
		
	//polygon math methods 
		private void findC()
		{
			double rad = (deg*2*Math.PI)/360; 	//angle of theta in radians. Initialized to 45, max 90, min 0
			currentRad = rad;
			poly.reset();
			
			//calculate c's x and y offset 
			c_Xoffset = (int) (l*Math.cos(rad));
			c_Yoffset = (int) (l*Math.sin(rad));
			
			//find point c
			c.setLocation(a.x-c_Xoffset, a.y-c_Yoffset);
			
			int xOffset;
			int yOffset;
		
			xOffset = (int) (w/2*Math.cos(rad));	//x1 & x2
			yOffset = (int) (w/2*Math.sin(rad));	//y1 & y2
			
			a1.setLocation(a.x - yOffset, a.y+xOffset);
			a2.setLocation(a.x + yOffset, a.y-xOffset);
			
			c1.setLocation(c.x - yOffset, c.y + xOffset);
			c2.setLocation(c.x + yOffset, c.y-xOffset);
			//System.out.println("Point a:" + a);
			//System.out.println("Point c:" + c);
			
			//System.out.println("Point a1c1:" + a1+c1);
			//System.out.println("Point a2c2:" + a2+c2);
			
			circle.setBounds(a.x-40, a.y-40, 80,80);
			
			//convert angle to radians
			//calculate four barrel points
				
				poly.addPoint(a1.x, a1.y);
				poly.addPoint(c1.x, c1.y);
				poly.addPoint(c2.x, c2.y);
				poly.addPoint(a2.x, a2.y);
				
				
				
				
		}
		
	//projectile math
		private void initV() 
		{
		    V0X = Obj.vWeight/2 * Math.cos(currentRad); // velocity in the x direction stays constant
		    V0Y =Obj.vWeight/2 * Math.sin(currentRad);
		    yVelocity = V0Y;
		   // System.out.println("V0Y: " + V0Y);
		}

		private void makeCannonBall() {
		    drawCannonBall = true;
		    projectile.setLocation(c.x - (pSize - 1) / 2, c.y - (pSize - 1) / 2);
		    Obj.initV();
		}

		public void getTimeInSeconds() 
		{
	        time = System.currentTimeMillis() - startTimeMillis;
	        time = time / 1000; // Convert milliseconds to seconds
	    }
		
		private void moveProjectile() {
		   
		    
			currentTime = System.currentTimeMillis();
			double deltaT = (currentTime - prevTime) / 1000.0; // Convert milliseconds to seconds

			// Update x velocity
			double deltaX = V0X / 2 * Math.cos(currentRad) * deltaT; // Change in x position

			// Update y velocity and position
			double deltaY = V0Y * Math.sin(currentRad) * deltaT - 0.5 * gravity * deltaT * deltaT; // Change in y position
			V0Y -= gravity * deltaT; // Update y velocity due to gravity

			//System.out.println("delta y: " + deltaY + "\t" + V0Y + "\t" + Math.sin(currentRad) + "\t" + deltaT);
		    
		    
		    
		    // Update projectile position
		    projectile.x -= (int) deltaX;
		    projectile.y -= (int) deltaY;

		    //prevTime = currentTime;
		    int i = 0;
			
			int wallsize = Obj.walls.size();
			while(i < wallsize)
			{
				if(Obj.walls.elementAt(i).intersects(projectile))
				{
					Obj.walls.remove(i);
					i=0;
				}
				wallsize = Obj.walls.size();
				i++;
			}
			
			
			if(projectile.intersects(ball))
			{
				playerScore++;
				Obj.x = (int) (0 + (Obj.ball.getWidth()/2+1));
				Obj.y = (int) (0 + (Obj.ball.getWidth()/2+1));
				Obj.walls.removeAllElements();
				Obj.Clear();
				Obj.repaint();
			}
			
			
			
		}

		
	//mutators
		public void rectangle(boolean r)
		{
			rect = r;	
			
			
			
		}
		public void update(Graphics g)		
		{
			paint(g);
		}
		public void reSize(int w,int h)
		{
			ScreenWidth = w;
			ScreenHeight = h;
			/*
			 * y = ScreenHeight/2; 
			 * x = ScreenWidth/2;
			 */
			calcMinMax();
			move();
		}
		public void Clear()
		{
			clear = true;
		}
		public void setX(int newX)
		{
			this.x = newX;
		}
		public void setY(int newY)
		{
			this.y = newY;
		}
		public void setTail(boolean tail)
		{
			this.tail = tail;
		}
		
		public void addOne(Rectangle r)
		{
			walls.addElement(new Rectangle (r));
		}
		public void removeOne(int i)
		{
			walls.removeElementAt(i);
		}
	//paint methods
		
		public void paint(Graphics cg)
		{
			buffer = createImage(getWidth(), getHeight());
			
			if(g != null)
			{
				g.dispose();
			}
			g = buffer.getGraphics();	
			if(clear)
			{
				super.paint(g);
				clear = false;
				g.setColor(Color.red);
				g.drawRect(0,0,ScreenWidth-1,ScreenHeight-1);
			}
			
			//draw the ball
				g.setColor(Color.lightGray);
				g.fillOval(x-(SObj-1)/2, y-(SObj-1)/2, SObj, SObj);
				g.setColor(Color.black);
				g.drawOval(x-(SObj-1)/2, y-(SObj-1)/2, SObj-1, SObj-1);
				ball.setBounds(x-(SObj-1)/2, y-(SObj-1)/2, SObj, SObj);
			
			
			Rectangle temp = new Rectangle();
			
			if(Obj.rectX != 0)
			{
				g.drawRect(Obj.rectX, Obj.rectY, Obj.rectWidth, Obj.rectHeight);
			}
			
			findC();
			//g.drawLine(c.x, c.y, a.x, a.y);
			g.setColor(Color.darkGray);
			
			if(destroyed)
			{
				g.setColor(Color.red);
			}
			
			g.fillOval(circle.x, circle.y, circle.width, circle.height);
			g.setColor(Color.blue);
			if(destroyed)
			{
				g.setColor(Color.red);
			}
			
			g.drawPolygon(poly);
			g.fillPolygon(poly);
			
			//g.drawRect(poly.getBounds().x, poly.getBounds().y, poly.getBounds().width, poly.getBounds().height);
			
			
			//if clicked on cannon 
			if(drawCannonBall)
			{
				g.drawOval(projectile.x, projectile.y, projectile.width, projectile.height);
			}
			
			
			
			g.setColor(Color.black);
			for(int i = 0; i < Obj.walls.size(); i++)
			{
			    temp.setBounds(Obj.walls.elementAt(i));
			    ////System.out.println("Rectangle " + i + ": " + temp);
			    g.fillRect(temp.x, temp.y, temp.width, temp.height);
			}
		
			
			
			xold = x;
			yold = y;
			
			
			cg.drawImage(buffer, 0, 0, null);
		}
			

	//accessors
		public int getObjectSize()
		{
			return this.SObj;
		}
		public int getObjectX()
		{
			return this.x;
		}
		public int getObjectY()
		{
			return this.y;
		}
		public boolean getTail()
		{
			return this.tail;
		}
		
		public Rectangle getOne(int i)
		{
			return walls.elementAt(i);
		}
		public int getWallSize()
		{
			return walls.size();
		}
	//calculate min and max 
		public void calcMinMax() 
		{
			ymin = 0 + (SObj - 1) / 2;  // Adjusted space needed for half object
		    ymax = this.ScreenHeight - (SObj - 1) / 2;
		    xmin = 0 + (SObj - 1) / 2;
		    xmax = this.ScreenWidth - (SObj - 1) / 2;
		   
		    
		}
		
	//size method
		public void ObjSize(int newSize)
		{
			SObj = newSize;
			ball.setBounds(x, y, SObj, SObj);
		}
		
	//check x and y 
		public boolean checkX(int x)
		{
			if(x < xmin || x > xmax)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		public boolean checkY(int y)
		{
			if(y < ymin || y > ymax)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		
	//move
		public void move()
		{
			if(!checkX(x))
			{
				right = !right;
			}
			if(!checkY(y))
			{
				down = !down;
			}
			
			ball.grow(1, 1);
			Rectangle touchedRect = isTouch(ball);
			if (touchedRect != null) {
			    int xline = touchedRect.x;
			    int yline = touchedRect.y;

			    Rectangle leftBound = new Rectangle(xline, yline + 1, 1, touchedRect.height - 1);
			    Rectangle rightBound = new Rectangle(xline + touchedRect.width - 1, yline + 1, 1, touchedRect.height - 1);

			    Rectangle topBound = new Rectangle(xline + 1, yline, touchedRect.width, 3);
			    Rectangle bottomBound = new Rectangle(xline + 1, yline + touchedRect.height - 1, touchedRect.width, 3);

			    if (ball.intersects(leftBound)) {
			        right = false;
			    }
			    if (ball.intersects(rightBound)) {
			        right = true;
			    }
			    
			    if (ball.intersects(topBound)) {
			        down = !down;
			    }
			    if (ball.intersects(bottomBound)) {
			        down = !down;
			    }
			}
			
			Rectangle ballCheck = new Rectangle(ball);
			ballCheck.grow(2, 2);
			if(ballCheck.intersects(poly.getBounds()))
			{
				ballScore++;
				Obj.right = !right;
				Obj.down = !down;
				destroyed = true;
				
			}
			
			
			if(right)
			{
				x++;
			}
			else
			{
				x--;
			}
			
			if(down)
			{
				y--;
				
			}
			else
			{
				y++;
			}
			
			
		}

	//is ball to the wall 
		public Rectangle isTouch(Rectangle ball)
		{
			Rectangle r = new Rectangle(ZERO);
			Rectangle b = new Rectangle(ball);
			b.grow(1, 1);
			int i =0;
			boolean ok = true;
			
			
			while((i<walls.size())&&ok)
			{
				r=walls.elementAt(i);
				if(r.intersects(b))
				{
					
					return r;
					
				}
				else
				{
					i++;
				}
			}
			return ZERO;
			
		}
		
		


	
		
		
		

		
	}
	
	
	
	//initialization
		// Initial application serial number UID
		private static final long serialVersionUID = 10L;
		
		// Initial constants
		private final int WIDTH = 640;
		private final int HEIGHT = 400;
		private final int BUTTONH = 20;
		private final int BUTTONHS = 5;
		
		// initial varibles
		private int WinWidth = WIDTH;
		private int WinHeight = HEIGHT;
		private int ScreenWidth;
		private int ScreenHeight;
		private int WinTop = 10;
		private int WinLeft = 50;
		private int BUTTONW = 50;
		private int Center = (WIDTH/2);
		private int BUTTONS = BUTTONW/4;
		
		// create inset
		private Insets i;
		
		// create buttons
		Button start, shape, clear, tail, quit;
			
		//scrollbar & circle/rectangle constants
		private final int MAXObj = 100;	//max object size 
		private final int MINObj = 10; 	//min object size 
		private final int SPEED = 50;	//initial speed
		private final int SBvisible = 10;	//visible scroll bar
		private final int SBunit = 1;	//scrollbar unit step size 
		private final int SBblock = 10;	//scroll bar block step size 
		private final int SCROLLBARH = BUTTONH;	//scrollbar height 
		private final int SOBJ = 21;	//initial object width 
	
		//scrollbar & circle/rectangle variables
		private int SObj = SOBJ;	//initial object width
		private int SpeedSBmin = 1; //speed scrollbar minimum value 
		private int SpeedSBmax = 100+SBvisible;	//speed scrollbar maximum value with visible offset 
		private int VelocitySBinit = SPEED;	//initial speed scrollbar value 
		private int ScrollBarW;	//scrollbar width 
	
		
		//objects
		private Objc Obj;	//object to draw 
		private Label VelocityL = new Label("Velocity", Label.CENTER);
		private Label AngleL = new Label("Angle", Label.CENTER);
		Scrollbar VelocityScrollbar, AngleScrollBar;
		
		///rectangles 
		private Rectangle Perimeter = new Rectangle(0, 0, WIDTH, HEIGHT);	//ball perimeter 
		private Rectangle db = new Rectangle(0,0,0,0);		//drag box 
		
		//mouse points 
		Point m1 = new Point(0,0);
		Point m2 = new Point(0,0);
		
		
	//menu items 
		private MenuBar MMB; //the menu bar
		//items inside of the menu bar
			private Menu PARAMETERS, CONTROL, ENVIRONMENT;	//top level menus 
			private Menu SPEEDMENU,SIZEMENU; //sub-menus in parameters 
			
			
			private CheckboxMenuItem SLOWER, SLOW, MEDIUM, FAST, FASTER; 	//items within the speed menu
			private CheckboxMenuItem TINY, SMALL, NORMAL, BIG, HUGE; 		//terminal items in SIZE menu
			
			private MenuItem QUIT, RUN, PAUSE, RESTART; 			//CONTROL items
			
			private CheckboxMenuItem MERC, VEN, EARTH, MARS, JUP, SAT, URAN, NEP, PLUTO, MOON, SUN;	//planets under environment 
		
	//score
			private int playerScore = 0;
			private Label PScore = new Label("Your Score: " + playerScore);
			
			private int ballScore = 0;
			private Label BScore = new Label("Ball Score: " + ballScore);
		
			
			private Label timeLabel = new Label("Time: ");
//thread initializing
	//constants
	private final double DELAY = 20;	//value for min time delay, may need tweaked
	
	//variables
	boolean run;	//program loop
	boolean TimePause;	//identify run/pause
	boolean started;	//identify if animation started
	int speed;	//scrollbar speed
	double delay = DELAY;	//current time delay
	
	
	int prevWidth;
	int prevHeight;
	
	//objects
	private Thread thethread;
		
	//p5 addition
	private Panel sheet = new Panel();	//
	private Panel control = new Panel();
	
	
	//update interval
	int interval = 5;
	
	// bouce constructor
	public CannonVSBall()
	{
		Perimeter.grow(-1, -1);
		
		started = false;	//started flag for thread
		
		setLayout(new BorderLayout());		//p5 apply border layout to frame 
		setVisible(true);
		MakeSheet();
		try
		{
			initComponents();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		SizeScreen();
		
		//Obj.vWeight = (int) ((70 * (double) VelocityScrollbar.getValue() / VelocityScrollbar.getMaximum())+1);
		
		start();
	}

//null layout methods, window size, button layout 
	private void MakeSheet()
	{
		i = getInsets();
		// create screen size 
		ScreenWidth = WinWidth-i.left-i.right;
		// create screen height
		ScreenHeight = WinHeight-i.top-(BUTTONH+BUTTONHS)- i.bottom;
		setSize(WinWidth, WinHeight);
		Center = (ScreenWidth/2);
		BUTTONW = (ScreenWidth/11);
		BUTTONS = BUTTONW/4;
		setBackground(Color.LIGHT_GRAY);
		
		ScrollBarW = 2*BUTTONW;	//set new width for the scroll bar 
	}	
	public void initComponents() throws Exception, IOException {
	    TimePause = true;
	    run = true;

	    this.resize(641,400);
	    
	    setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setMinimumSize (getPreferredSize());
	    
	    
	    this.setLayout(new BorderLayout()); // Set the layout of the Frame to BorderLayout

	    sheet = new Panel(new BorderLayout(0,0));
	    sheet.setBackground(Color.blue);

	    
	  //menu items 
		
	  		MMB = new MenuBar(); //create menu bar
	  		
	  		//PARAMETERS MENU BAR 
	  		PARAMETERS = new Menu("PARAMETERS"); // create first menu entry for menu bar
	  		SPEEDMENU = new Menu("SPEED MENU"); //create first menu entry for File Menu
	  		SIZEMENU = new Menu("SIZE MENU"); //create first menu entry for File Menu
	  		
	  		//speedmenu items 
	  			SPEEDMENU.add(SLOWER = new CheckboxMenuItem("Slower"));
	  			SPEEDMENU.add(SLOW = new CheckboxMenuItem("Slow"));
	  			SPEEDMENU.add(MEDIUM = new CheckboxMenuItem("Medium"));
	  			SPEEDMENU.add(FAST = new CheckboxMenuItem("Fast"));
	  			SPEEDMENU.add(FASTER = new CheckboxMenuItem("Faster"));
	  		//SIZEMENU items 
	  			SIZEMENU.add(TINY = new CheckboxMenuItem("Tiny"));
	  			SIZEMENU.add(SMALL = new CheckboxMenuItem("Small"));
	  			SIZEMENU.add(NORMAL = new CheckboxMenuItem("Normal"));
	  			SIZEMENU.add(BIG = new CheckboxMenuItem("Big"));
	  			SIZEMENU.add(HUGE = new CheckboxMenuItem("Huge"));
	  		//add and finish the first MenuBar entry PARAMETERS
	  		PARAMETERS.add(SPEEDMENU); //add Menu SPEEDMENU TO PARAMETERS 
	  		PARAMETERS.add(SIZEMENU);
	  		//PARAMETERS.addSeparator(); // add separator to PARAMETERS Menu
	  		
	  				//add MenuItem quit with short cut key to File Menu
	  				//QUIT = FILE.add(new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q)));
	  		
	  		
	  		
	  		//specify the second menu entry CONTROL
	  		CONTROL = new Menu("CONTROL"); // create second menu entry for Menu bar
	  				
	  		//CONTROL MENU ITEMS 
	  		QUIT = CONTROL.add(new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q)));
	  		RUN = CONTROL.add(new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R)));
	  		PAUSE = CONTROL.add(new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P)));
	  		RESTART = CONTROL.add(new MenuItem("Restart"));
	  		
	  		
	  		
	  		
	  		//third menu environment 
	  		ENVIRONMENT = new Menu("ENVIRONMENT");	//create environment menu
	  		
	  		//environment planet checkbox items 
	  		ENVIRONMENT.add(MERC = new CheckboxMenuItem("Mercury"));
	  		ENVIRONMENT.add(VEN = new CheckboxMenuItem("Venus"));
	  		ENVIRONMENT.add(EARTH = new CheckboxMenuItem("Earth"));
	  		ENVIRONMENT.add(MARS = new CheckboxMenuItem("Mars"));
	  		ENVIRONMENT.add(JUP = new CheckboxMenuItem("Jupiter"));
	  		ENVIRONMENT.add(SAT = new CheckboxMenuItem("Saturn"));
	  		ENVIRONMENT.add(URAN = new CheckboxMenuItem("Uranus"));
	  		ENVIRONMENT.add(NEP = new CheckboxMenuItem("Neptune"));
	  		ENVIRONMENT.add(PLUTO = new CheckboxMenuItem("Pluto"));
	  		ENVIRONMENT.add(MOON = new CheckboxMenuItem("Moon"));
	  		ENVIRONMENT.add(SUN = new CheckboxMenuItem("Sun"));
	  		
		
		
		
		
		//add the upper level menus 
		MMB.add(PARAMETERS); // add PARAMETERS to the menu bar
		MMB.add(CONTROL); // add CONTROL to the menu bar
		MMB.add(ENVIRONMENT); // add ENVIRONMENT to the menu bar
		
		//turn on the Listener for the MenuItem's and the MenuShortCut's
		RUN.addActionListener(this); // add listener for File menu
		PAUSE.addActionListener(this); // add listener for File menu
		QUIT.addActionListener(this); // add listerner for File menu
		RESTART.addActionListener(this); // add listerner for File menu
		
		//turn on the listener for the CheckboxMenuItem's
		SLOWER.addItemListener(this); //add listener for font size
		SLOW.addItemListener(this); //add listener for font size
		MEDIUM.addItemListener(this); //add listener for font size
		FAST.addItemListener(this); //add listener for font size
		FASTER.addItemListener(this); //add listener for font size
		
		TINY.addItemListener(this); //add listener for font size
		SMALL.addItemListener(this); //add listener for font size
		NORMAL.addItemListener(this); //add listener for font size
		BIG.addItemListener(this); //add listener for font size
		HUGE.addItemListener(this); //add listener for font size
		
		MERC.addItemListener(this); //add listener for font size
		VEN.addItemListener(this); //add listener for font size
		EARTH.addItemListener(this); //add listener for font size
		MARS.addItemListener(this); //add listener for font size
		JUP.addItemListener(this); //add listener for font size
		SAT.addItemListener(this); //add listener for font size
		URAN.addItemListener(this); //add listener for font size
		NEP.addItemListener(this); //add listener for font size
		PLUTO.addItemListener(this); //add listener for font size
		MOON.addItemListener(this); //add listener for font size
		SUN.addItemListener(this); //add listener for font size
		
		
	
		
		//add the MenuBar to the frame
		this.setMenuBar(MMB); //add menu bar to the Frame
		
		
	    
	    
	    
	    
	    
		

	    Obj = new Objc(SObj, sheet.getWidth(), sheet.getHeight()); // Set the size of Obj to match the size of sheet
	    Obj.setBackground(Color.white);
	    Obj.setBounds(Perimeter);
	    Obj.addMouseListener(this);
	    Obj.addMouseMotionListener(this);
	    sheet.add(Obj, BorderLayout.CENTER); // Add Obj to the center of sheet
	    Perimeter.setBounds(sheet.getBounds());

	    
	    
	    //System.out.println("\nPerimeter initialized: " + Perimeter);
	    //System.out.println("\nsheetW, sheetH " + sheet.getWidth() + sheet.getHeight());
	    
	    control = new Panel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	
	    gbc.anchor = GridBagConstraints.WEST;

	    this.addComponentListener(this);
	    this.addWindowListener(this);
	    gbc.gridx = 1;
	    Label speedLabel = new Label("Velocity:");
	    gbc.gridy = 1; // Place the label in the next row
	    gbc.gridwidth = 1; // Reset gridwidth for label
	    gbc.weightx = 0.1; // Adjust weightx for label
	    control.add(speedLabel, gbc);
	    
	    // Modify GridBagConstraints for wider scrollbars
	    gbc.weightx = 9;
	    
	   // gbc.gridwidth = GridBagConstraints.REMAINDER; // Make the scrollbar take up the rest of the row

	    // Speed scrollbar
	    gbc.gridx = 2;
	    VelocityScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
	    VelocityScrollbar.setMaximum(SpeedSBmax);
	    VelocityScrollbar.setMinimum(SpeedSBmin);
	    VelocityScrollbar.setUnitIncrement(SBunit);
	    VelocityScrollbar.setValue(VelocitySBinit);
	    VelocityScrollbar.setVisibleAmount(SBvisible);
	    VelocityScrollbar.setBackground(Color.gray);
	    VelocityScrollbar.setPreferredSize(new Dimension(100, 20)); // Set preferred width and height
	    control.add(VelocityScrollbar, gbc);
	    
	    
	    
	    // Label for speed scrollbar
	    gbc.weightx = .1;
	    gbc.gridx = 3;
	    Label sizeLabel = new Label("Angle:");
	   
	    
	    control.add(sizeLabel, gbc);
	    // Size scrollbar
	    gbc.weightx = 9;
	    gbc.gridx = 4;
	    AngleScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
	    AngleScrollBar.setMaximum(MAXObj);
	    AngleScrollBar.setMinimum(MINObj);
	    AngleScrollBar.setUnitIncrement(SBunit);
	    AngleScrollBar.setBlockIncrement(SBblock);
	    AngleScrollBar.setValue(SOBJ);
	    AngleScrollBar.setVisibleAmount(SBvisible);
	    AngleScrollBar.setBackground(Color.gray);
	    AngleScrollBar.setPreferredSize(new Dimension(150, 20)); // Set preferred width and height
	   
	    control.add(AngleScrollBar, gbc);

	    //score labels 
	    gbc.gridx = 5;
	    control.add(PScore, gbc);
	    gbc.gridx = 6;
	    control.add(BScore, gbc);
	    gbc.gridx = 7;
	    control.add(timeLabel, gbc);
	    
	    
	    
	    control.setVisible(true);

	    // Adding components to the existing frame
	    add(sheet, BorderLayout.CENTER); // Add sheet panel to the center
	    add(control, BorderLayout.SOUTH); // Add control panel to the south

	    VelocityScrollbar.addAdjustmentListener(this);
	    AngleScrollBar.addAdjustmentListener(this);

	    
	    
	    this.setSize(this.getWidth() + 1, this.getHeight() + 1);
	    this.setSize(this.getWidth() - 1, this.getHeight() - 1);
	    
	    Perimeter.setBounds(sheet.getX(), sheet.getY(), sheet.getWidth(), sheet.getHeight());
	    
	    //System.out.println("\nPerimeter initialized last time in init : " + Perimeter);
	    //System.out.println("\nsheetW, sheetH " + sheet.getWidth() + sheet.getHeight());
	    
	    
	    
	    this.prevWidth = this.getWidth();
	    prevHeight = this.getHeight();
	}

	private void SizeScreen()
	{
	//, scroll bars, and labels:
		//SpeedScrollBar.setLocation(i.left+BUTTONS,ScreenHeight+BUTTONHS+i.top);
		//ObjSizeScrollBar.setLocation(WinWidth-ScrollBarW-i.right-BUTTONS,ScreenHeight+BUTTONHS+i.top);
		//SPEEDL.setLocation(i.left+BUTTONS,ScreenHeight+BUTTONHS+BUTTONH+i.top);
		//SIZEL.setLocation(WinWidth-ScrollBarW-i.right,ScreenHeight+BUTTONHS+BUTTONH+i.top);
		//SpeedScrollBar.setSize(ScrollBarW,SCROLLBARH);
		//ObjSizeScrollBar.setSize(ScrollBarW,SCROLLBARH);
		//SPEEDL.setSize(ScrollBarW,SCROLLBARH);
		//Obj.setBounds(i.left,i.top,ScreenWidth,ScreenHeight);
		Obj.setSize(sheet.getWidth(), sheet.getHeight());
		Perimeter.grow(sheet.getWidth(), sheet.getHeight());
		
	}
	public void componentResized(ComponentEvent e)
	{
		
		 WinWidth = getWidth(); 
		 WinHeight = getHeight(); 
		MakeSheet(); 
		 SizeScreen();
		  
		 Obj.reSize(ScreenWidth, ScreenHeight);
		int mr = 0, mb = 0;
		Rectangle r = new Rectangle();
		if(!Obj.walls.isEmpty())
		{
			r.setBounds(Obj.walls.elementAt(0));
			 mr = r.x+r.width;
			 mb = r.y + r.height;
			
			for(int i = 0; i < Obj.walls.size(); i++) 
			{
			    r.setBounds(Obj.walls.elementAt(i));
			    mr = Math.max((r.x + r.width), mr);
			    mb = Math.max((r.y+ r.height),mb);
			}
		}
		
		
		
		

		r.setBounds(Obj.ball);
		
		 mr = Math.max((r.x + r.width), mr);
		 mb = Math.max((r.y+ r.height),mb);
		 
		int sw = ScreenWidth;
		int sh = ScreenHeight;
		 
		if(mr > WinWidth || mb > WinHeight) 
		{
			this.setSize(prevWidth, prevHeight); 
			 Perimeter.setBounds(this.getX(), this.getY(), sheet.getWidth(), sheet.getHeight());
			 prevWidth = this.getX();
			 prevHeight = this.getY();
			 
			Obj.a.setLocation(Perimeter.getX()+Perimeter.getWidth() , Perimeter.getY()+Perimeter.getHeight());
			Obj.findC();
			repaint();
			
		}
			
		 
		 //this.setLocation(sheet.getWidth()-1,sheet.getHeight()-1);
		 
		 Perimeter.setBounds(this.getX(), this.getY(), sheet.getWidth(), sheet.getHeight());
		 prevWidth = this.getX();
		 prevHeight = this.getY();
		 
		Obj.a.setLocation(Perimeter.getX()+Perimeter.getWidth() , Perimeter.getY()+Perimeter.getHeight());
		Obj.findC();
		 	 
		 //System.out.println("\nPerimeter initialized: " + Perimeter);
		    //System.out.println("\nsheetW, sheetH " + sheet.getWidth() + sheet.getHeight());
		    
		 
	}
	public void componentHidden(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	
//action event handler
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == RUN) 
		{
	            
	            TimePause = false;
	    } else if (source == PAUSE) 
	    {
	            
	            TimePause = true;
	            // Perform pause actions
	    }
		else if (source == RESTART)	//gonna have restart stuff here too 
		{	
			Obj.destroyed = false;
			
			playerScore = 0;
			ballScore = 0;
			Obj.time = 0;
			PScore.setText("Your Score: " + playerScore);
			BScore.setText("Ball Score: " + ballScore);
			timeLabel.setText("Time: " + Obj.time);
			
			Obj.x = (int) (0 + (Obj.ball.getWidth()/2+1));
			Obj.y = (int) (0 + (Obj.ball.getWidth()/2+1));
			Obj.walls.removeAllElements();
			Obj.Clear();
			Obj.repaint();
			
			
		}
		else if (source == QUIT)
		{
			stop();
		}
	}
	
//scrollbar adjustments 
	public void adjustmentValueChanged(AdjustmentEvent e) 
	{
		
		
		int TS = e.getValue();
		TS = (TS/2)*2+1;
		int half = (TS-1)/2;
		
		Rectangle b = new Rectangle();
		
		b.setBounds(Obj.getX()-half-1, Obj.getY()-half-1, TS+2, TS+2);
		
		Scrollbar sb = (Scrollbar) e.getSource();
		if (sb == VelocityScrollbar) {
		    // Get the value from the velocity scrollbar
		    int velocityValue = VelocityScrollbar.getValue();

		    // Map the velocityValue from its current range to a range between 0 and 100
		    int minVelocity = 100; // Minimum velocity value
		    int maxVelocity = 1200; // Maximum velocity value

		    // Assuming VelocityScrollbar.getMaximum() returns the maximum value of the velocity scrollbar
		    Obj.vWeight = (int) (minVelocity + (maxVelocity - minVelocity) * (double) velocityValue / VelocityScrollbar.getMaximum());

		    

		    // Now you can use the 'velocity' value as needed
		    System.out.println("Velocity value from scroll bar: " + Obj.vWeight);
		    // Or perform any other actions based on the velocity value
		    
		Obj.vChanged = true;    
	}

		if(sb == AngleScrollBar)
		{
			
			
			// Get the value from the angle scrollbar
			int angleValue = AngleScrollBar.getValue();

			// Map the angleValue from its current range to a range between 0 and 90
			double angle = 90 * (double) angleValue / AngleScrollBar.getMaximum();
			
			Obj.deg = (int) angle;
			
			Obj.repaint();
		}
	}
	
//stop/start methods
	public void stop()
	{
		
		this.removeComponentListener(this);
		this.removeWindowListener(this);
		
		VelocityScrollbar.removeAdjustmentListener(this);
		AngleScrollBar.removeAdjustmentListener(this);
		
		run = false;
		thethread.interrupt();
		
		Obj.removeMouseListener(this);
		Obj.removeMouseMotionListener(this);
		
		dispose();
		System.exit(0);
	}
	public void start()
	{
		Obj.repaint();
		
		if(thethread == null)
		{
			thethread = new Thread(this);
			thethread.start();
		}
	}
	
	public void run()
	{
		int updateCount = 0;
		
		while(run)	//while run flag is true 
		{
			
			
			
			//TimePause = false;
			if(!TimePause)	//if not paused
			{
				//update time 
				Obj.getTimeInSeconds();
				//System.out.println("Time: "+ Obj.time);
				
				//update labels
				PScore.setText("Your Score: " + playerScore);
				BScore.setText("Ball Score: " + ballScore);
				timeLabel.setText("Time: " + Obj.time);
				
				
				started = true;
				
				//try to sleep the thread for the delay time 
				try
				{   
					Thread.sleep((long) delay);	
					
					//System.out.println("updateCount: " + updateCount);
				}
				catch(InterruptedException e) {}
				
				//update the size in the object to the new size 
				//SObj = NS;
				//repaint the object
				if (updateCount >= interval)	//so it should activate every "interval" iterations  
				{
					//System.out.println("hey we are moving the ball now");
					updateCount = 0;
					
					Obj.move();
					//move the object to it's next location 
					
						//old size, x, and y are kept to erase the current object before drawing the new object when no tails 
				}
				if(Obj.drawCannonBall)
				{
					Obj.moveProjectile();
				}
				
				
				
				
				
				Obj.repaint();
				
				updateCount++;
			}
			
			
			//allows loop to interupt for pause 
			try
			{
				Thread.sleep(1);	
				
			}
			catch(InterruptedException e) {}
		}
	}
	
//validating object resizing
	public boolean checkSize(Objc Obj)
	{
		boolean goodSize = true;
		//get current x, y, and size from object 
		//calculate the right & bottom of the object 
		int bot = Obj.getObjectY() + (Obj.getObjectSize()-1)/2;
		int right = Obj.getObjectX() + (Obj.getObjectSize()-1)/2;
				
		if(bot > WinTop+ScreenHeight)
		{
			int i = WinTop + ScreenHeight - (Obj.getObjectSize()-1)/2; // set i to the border, minus the space needed for half object 
			Obj.setX(i);
			goodSize = false;
		}
		if(right > WinLeft+ScreenWidth)
		{
			int i = WinLeft + ScreenWidth - (Obj.getObjectSize()-1)/2; // set i to the border, minus the space needed for half object 
			Obj.setY(i);
			goodSize = false;
		}
		return goodSize;
	}
	
	
	
	
	// window methods
		public void windowClosing(WindowEvent e)
		{
			
			stop();			
		}
		// window listener ****goes into stop
		public void windowClosed (WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e){}
		public void windowIconified(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		
	//mouse methods 
		public void mouseDragged(MouseEvent e) {
			m2.setLocation(e.getX(), e.getY());
			
			
			
			Obj.rectX = Math.min(m1.x, m2.x);
			Obj.rectY = Math.min(m1.y, m2.y);
			
			Obj.rectWidth = Math.abs(m1.x-m2.x);
			Obj.rectHeight = Math.abs(m1.y-m2.y);
			
			db.setBounds(Obj.rectX, Obj.rectY, Obj.rectWidth, Obj.rectHeight);
			
			if(Perimeter.contains(db))
			{
				//Obj.setDragBox(db);
				Obj.repaint();
			}
		}

	

		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseClicked(MouseEvent e) {
			Point p = new Point(e.getX(), e.getY());
			
			
			
			int i = 0;
			
			int wallsize = Obj.walls.size();
			while(i < wallsize)
			{
				if(Obj.walls.elementAt(i).contains(p))
				{
					Obj.walls.remove(i);
					i=wallsize;
				}
				wallsize = Obj.walls.size();
				i++;
			}
			
			if(Obj.poly.contains(p)||Obj.circle.contains(p))
			{
				//System.out.println("you clicked the cannon buddy");
				Obj.destroyed = false;
				Obj.makeCannonBall();
				Obj.projectileOGTime = System.currentTimeMillis();
				Obj.currentTime = System.currentTimeMillis();
				Obj.prevTime = Obj.currentTime;
				System.out.println("current time, prev time" + Obj.currentTime + ", " + Obj.prevTime);
				
			}
			
		}

		public void mouseEntered(MouseEvent e) {
			Obj.repaint();
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent e) {
			m1.setLocation(e.getX(), e.getY());
			
		}

		public void mouseReleased(MouseEvent e) {
			Rectangle b = new Rectangle(Obj.ball);
			b.grow(1, 1);
			
			
			Rectangle addWall = new Rectangle(db);
			if(!addWall.intersects(b))
			{
				if(!Obj.poly.intersects(addWall))
				{
					if(!Obj.circle.intersects(addWall))
					{
						if(Perimeter.contains(addWall))
						{
							
							Obj.walls.add(addWall);
							//System.out.println("Rectangle just added " + db);
							for(int i = 0; i < Obj.walls.size(); i++)
							{
							    //System.out.println("walls rectangle " + i + ": " + Obj.walls.elementAt(i));   
							}
							
							
							
							int i = 0;
							int wallsize = Obj.walls.size();
							while(i<wallsize-1)
							{
								if(addWall.contains(Obj.walls.elementAt(i)))
								{
									Obj.walls.remove(Obj.walls.elementAt(i));
									i=0;
								}
								else if((Obj.walls.elementAt(i)).contains(addWall))
								{
									Obj.walls.remove(Obj.walls.lastElement());
									i=0;
								}
								i++;
								wallsize = Obj.walls.size();
							}
							for(i = 0; i < Obj.walls.size(); i++)
							{
							    //System.out.println("walls rectangle " + i + ": " + Obj.walls.elementAt(i));   
							}
						}
					}
				}
				else
				{
					addWall.setBounds(0,0,0,0);
				}
				db.setSize(0, 0);
			}
			Obj.rectX = 0;
			Obj.rectY = 0;
			Obj.rectWidth =0;
			Obj.rectHeight = 0;
			repaint();
		}
		
		
		//these make sure that only one item from a category can be active at a time 
			private void deactivateAllExcept_SPEED(CheckboxMenuItem menuItem) 
			{//make it so only one item from each category can be checked at once
			    
				CheckboxMenuItem[] items = {SLOWER, SLOW, MEDIUM, FAST, FASTER};
				 for (CheckboxMenuItem item : items) {
			        if (item != menuItem && item.getState()) {
			            item.setState(false);
			            //System.out.println(item.getLabel() + " deactivated");
			        }
				 }
		    }
			
			private void deactivateAllExcept_SIZE(CheckboxMenuItem menuItem) 
			{//make it so only one item from each category can be checked at once
			    CheckboxMenuItem[] items = {TINY, SMALL, NORMAL, BIG, HUGE};
			    for (CheckboxMenuItem item : items) {
			        if (item != menuItem && item.getState()) {
			            item.setState(false);
			            //System.out.println(item.getLabel() + " deactivated");
			        }
			    }
			}
		
			private void deactivateAllExcept_PLANETS(CheckboxMenuItem menuItem) 
			{//make it so only one item from each category can be checked at once
			    CheckboxMenuItem[] items = {MERC, VEN, EARTH, MARS, JUP, SAT, URAN, NEP, PLUTO, MOON, SUN};
			    for (CheckboxMenuItem item : items) {
			        if (item != menuItem && item.getState()) {
			            item.setState(false);
			            //System.out.println(item.getLabel() + " deactivated");
			        }
			    }
			}
		//function to check if a ballresize is valid
			private void resizeBall(int size)
			{
		        	int TS = size;
					TS = (TS/2)*2+1;
					int half = (TS-1)/2;
					
					Rectangle b = new Rectangle();
					
					b.setBounds(Obj.getX()-half-1, Obj.getY()-half-1, TS+2, TS+2);
				
					int i = 0;
					boolean ok = true;
					
					while(i < Obj.getWallSize() && ok)
					{
						Rectangle t = new Rectangle(Obj.walls.elementAt(i)); 
						
						if(t.intersects(b))
						{
							ok = false;
						}
						
						i++;
						
					}
					
					
					b.setBounds(Obj.ball);
					b.grow(TS, TS);
					//System.out.println("Updated b: " + b); // Print b to console
					//System.out.println("Perimeter:  " + Perimeter); // Print b to console
					if (ok && Perimeter.contains(b)) {
					    this.setSize(this.getWidth() + 1, this.getHeight() + 1);
					    this.setSize(this.getWidth() - 1, this.getHeight() - 1);
					    Obj.ObjSize(TS);
					    
					} else 
					{
					    
					}


					
					Obj.repaint();
		        	
			}

		//logic to determine if a checkbox is active 
		public void itemStateChanged(ItemEvent e) 
		{
			 Object source = e.getSource();
			    
			 
			 //speed menu 
			 	if (source == SLOWER) {
		            //System.out.println("SLOWER activated");
 		            deactivateAllExcept_SPEED(SLOWER);
		            
		    		interval = 10;
		    		thethread.interrupt();
		    		
		        } else if (source == SLOW) {
		            //System.out.println("SLOW activated");
		            deactivateAllExcept_SPEED(SLOW);
		            
		    		interval = 8;
		    		thethread.interrupt();
		    		
		        } else if (source == MEDIUM) {
		            //System.out.println("MEDIUM activated");
		            deactivateAllExcept_SPEED(MEDIUM);
		            
		    		interval = 5;
		    		thethread.interrupt();
		    		
		        } else if (source == FAST) {
		            //System.out.println("FAST activated");
		            deactivateAllExcept_SPEED(FAST);
		            
		    		interval = 3;
		    		thethread.interrupt();
		    		
		        } else if (source == FASTER) {
		            //System.out.println("FASTER activated");
		            deactivateAllExcept_SPEED(FASTER);
		            
		    		interval = 1;
		    		thethread.interrupt();
		    		
		        } 
		        
			//sizemenu (will set "size")
		        if (source == TINY) {
		            //System.out.println("TINY activated");
		            deactivateAllExcept_SIZE(TINY);
		            resizeBall(10);
		        } else if (source == SMALL) {
		            //System.out.println("SMALL activated");
		            deactivateAllExcept_SIZE(SMALL);
		            resizeBall(30);
		        } else if (source == NORMAL) {
		            //System.out.println("NORMAL activated");
		            deactivateAllExcept_SIZE(NORMAL);
		            resizeBall(50);
		        } else if (source == BIG) {
		            //System.out.println("BIG activated");
		            deactivateAllExcept_SIZE(BIG);
		            resizeBall(70);
		        } else if (source == HUGE) {
		            //System.out.println("HUGE activated");
		            deactivateAllExcept_SIZE(HUGE);
		            resizeBall(90);
		        }
		        
		        //will check "size" and resize the ball if valid
		        
		       
		        
		        
		        if (source == MERC) {
		            //System.out.println("MERC activated");
		            deactivateAllExcept_PLANETS(MERC);
		            Obj.gravity = 12.1;
		        } else if (source == VEN) {
		            //System.out.println("VEN activated");
		            deactivateAllExcept_PLANETS(VEN);
		            Obj.gravity = 29.1;
		        } else if (source == EARTH) {
		            //System.out.println("EARTH activated");
		            deactivateAllExcept_PLANETS(EARTH);
		            Obj.gravity = 32.1;
		        } else if (source == MARS) {
		            //System.out.println("MARS activated");
		            deactivateAllExcept_PLANETS(MARS);
		            Obj.gravity = 12.1;
		        } else if (source == JUP) {
		            //System.out.println("JUP activated");
		            deactivateAllExcept_PLANETS(JUP);
		            Obj.gravity = 75.9;
		        } else if (source == SAT) {
		            //System.out.println("SAT activated");
		            deactivateAllExcept_PLANETS(SAT);
		            Obj.gravity = 29.4;
		        } else if (source == URAN) {
		            //System.out.println("URAN activated");
		            deactivateAllExcept_PLANETS(URAN);
		            Obj.gravity = 28.5;
		        } else if (source == NEP) {
		            //System.out.println("NEP activated");
		            deactivateAllExcept_PLANETS(NEP);
		            Obj.gravity = 36;
		        } else if (source == PLUTO) {
		            //System.out.println("PLUTO activated");
		            deactivateAllExcept_PLANETS(PLUTO);
		            Obj.gravity = 2.3;
		        }
				else if (source == MOON) {
		            //System.out.println("PLUTO activated");
		            deactivateAllExcept_PLANETS(MOON);
		            Obj.gravity = 5.3;
		        }
				else if (source == SUN) {
		            //System.out.println("PLUTO activated");
		            deactivateAllExcept_PLANETS(SUN);
		            Obj.gravity = 898.95;
		        }
		    }
		

		
//main method
	public static void main(String[] args)
	{
		
		CannonVSBall b = new CannonVSBall();
		//PanelMouse p = b.new PanelMouse();
		
	}






	
	
	
	

}
