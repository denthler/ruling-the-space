package se.space;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.AttributeSet.FontAttribute;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import se.space.buildings.*;
import se.space.spaceships.*;

public class Game extends BasicGame implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4648492793045507077L;

	public Game(String title) {
		super(""+title);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */

	private Robot robot;
	private boolean useRobot = false;
	
	private Point mouseLocation;
	private Point centerLocation;
	private boolean relativeMouseMode;
	private boolean isRecentering;

	private transient World gameWorld;
	private View worldView;
	private TileMap tiles;
	private Minimap minimap;
	private Gui gui;
	private Editor editor;
	private Input input;
	private Point dragSelect = null;
	private Dimension screenSize;
	private boolean quit = false;
	private boolean editMode = false;
	private boolean dragView = false;
	private boolean paused = false;
	private boolean selectMode = false;
	private boolean minimapMode = false;
	private boolean guiMode = false;
	private int frameCount = 0; 
	private int updateCount = 0;
	private List<GameObject> selectedObjects;
	//static public HashMap<String,Image> objectImageList;
	static public HashMap<String,GameObject> objectList;
	private List<List<GameObject>>numberGroup= new ArrayList<List<GameObject>>();
	private int[] numberKeys = {Input.KEY_0,Input.KEY_1,Input.KEY_2,Input.KEY_3,
			Input.KEY_4,Input.KEY_5,Input.KEY_6,Input.KEY_7,Input.KEY_8,Input.KEY_9};
	private Timer hotkeyTimer = Timer.createTimer(1000);
	private int followGroup;
	private int lastUsedGroup;
	private Timer t;
	private Timer doubleClickTimer;
	private boolean doubleClick;
	
	private MainMenu menu;
	private Lobby lobby;
	private boolean isFullscreen;
	private boolean isHost;
	private boolean isServer = false;
	private boolean isClient = false;
	private boolean hasJoined = false;
	public Team redTeam;
	public Team blueTeam;
	public Team grayTeam;
	private Team myTeam;
	public static final boolean DEBUG_MODE = false;
	public static final String IMAGE_PATH = "images/";

	//Network
	private static NetworkClient n;
	
	private boolean victory = false;
	private boolean defeat = false;

	@Override
	public void init(GameContainer container) throws SlickException {
		container.setShowFPS(false);
		objectList = new HashMap<String,GameObject>();
		objectList.put("ship",new StandardShip(null,-1,-1,
			Game.IMAGE_PATH+"ship.png",Game.IMAGE_PATH+"shipIcon.png",1,null,"ship"));
		objectList.put("destroyer",new Destroyer(null,-1,-1,
				Game.IMAGE_PATH+"destroyer.png",Game.IMAGE_PATH+"destroyerIcon.png",1,null,"destroyer"));
		objectList.put("healer",new HealerShip(null,-1,-1,
				Game.IMAGE_PATH+"healer.png",Game.IMAGE_PATH+"healerIcon.png",1,null,"healer"));
		objectList.put("builder",new BuilderShip(null,-1,-1,
				Game.IMAGE_PATH+"builder.png",Game.IMAGE_PATH+"builderIcon.png",1,null,"builder"));
		
		objectList.put("earth",new Earth(null,-1,-1,
				Game.IMAGE_PATH+"earth.png",Game.IMAGE_PATH+"earthIcon.png",1,null,"earth"));
		objectList.put("spacestation",new Spacestation(null,-1,-1,
				Game.IMAGE_PATH+"spacestation.png",Game.IMAGE_PATH+"spacestationIcon.png",1,null,"spacestation"));
		
			
		
	/*	objectImageList = new HashMap<String,Image>();
		for(String s:objectList.keySet()) {
			objectImageList.put("ship.png", new Image(Game.IMAGE_PATH + "ship.png"));
			objectImageList.put("destroyer.png", new Image(Game.IMAGE_PATH + "destroyer.png"));
			objectImageList.put("earth.png", new Image(Game.IMAGE_PATH + "earth.png"));
			objectImageList.put("spacestation.png", new Image(Game.IMAGE_PATH + "spacestation.png"));
		}*/
		blueTeam=new Team(Color.blue);
		redTeam=new Team(Color.red);
		grayTeam=new Team(Color.gray);
		myTeam=redTeam;
		t= Timer.createTimer(1000);
		doubleClickTimer = Timer.createTimer(1000);
		isFullscreen=false;
		isHost=true;
		for(int i=0;i<10;i++){
			this.numberGroup.add(null);
		}
		container.setMinimumLogicUpdateInterval(20);
		container.setMaximumLogicUpdateInterval(20);
		container.setUpdateOnlyWhenVisible(true);
		container.setAlwaysRender(false);
		container.setSmoothDeltas(true);
		container.setFullscreen(isFullscreen);
		
		//Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize = new Dimension(container.getWidth(),container.getHeight());
		input = container.getInput();
		tiles = new TileMap(this,"maps/default.map");
		changeGameWorld(tiles.getWorld());
		/**gameWorld = new World(this, new Dimension(2000, 2000));
		setWorldView(new View(gameWorld, screenSize));
		
		tiles = new TileMap(this, gameWorld, getWorldView(),"maps/default.map");
		minimap = new Minimap(this, gameWorld, getWorldView());
		
		lobby = new Lobby();
		menu = new MainMenu(this,gameWorld,getWorldView(),n);
		menu.setLobby(lobby);
		
		

		gui = new Gui(gameWorld, screenSize);
		editor = new Editor(gameWorld, screenSize,this);
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gameWorld.init(gui);
		gameWorld.update(0);
		//lobby = new Lobby(this,gameWorld,getWorldView());
		dragSelect=new Point();**/
		
		

	}
	public Dimension getScreenSize(){
		return screenSize;
	}
	public void changeGameWorld(World world){
		gameWorld = world;
		//setWorldView(new View(gameWorld, screenSize));
		
		lobby = new Lobby();
		menu = new MainMenu(this,gameWorld,getWorldView(),n);
		menu.setLobby(lobby);
		
		minimap = new Minimap(this, gameWorld, getWorldView());

		gui = new Gui(gameWorld, screenSize);
		editor = new Editor(gameWorld, screenSize,this);
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gameWorld.init(gui);
		gameWorld.update(0);
		//lobby = new Lobby(this,gameWorld,getWorldView());
		dragSelect=new Point();
	}
	@Override
	public void update(GameContainer container, int delta)
	throws SlickException {
		if(quit) {
			container.exit();
		}
		if(input.isKeyDown(Input.KEY_LALT)&&input.isKeyPressed(Input.KEY_ENTER)){
			isFullscreen=!isFullscreen;
			container.setFullscreen(isFullscreen);
		}
		/////Move with arrow keys
		if(input.isKeyDown(Input.KEY_LEFT)) {
			getWorldView().setXScrollDirection(View.ScrollX.LEFT);
		} else if(input.isKeyDown(Input.KEY_RIGHT)) {
			getWorldView().setXScrollDirection(View.ScrollX.RIGHT);
		}
		else{
			getWorldView().setXScrollDirection(View.ScrollX.NONE);
		}
		if(input.isKeyDown(Input.KEY_DOWN)) {
			getWorldView().setYScrollDirection(View.ScrollY.DOWN);
		} else if(input.isKeyDown(Input.KEY_UP)) {
			getWorldView().setYScrollDirection(View.ScrollY.UP);
		}
		else{
			getWorldView().setYScrollDirection(View.ScrollY.NONE);
		}
		
		///////////move with mouse
		if(input.getMouseX()<10){
			getWorldView().setXScrollDirection(View.ScrollX.LEFT);
		}
		else if(input.getMouseX()>screenSize.width-10){
			getWorldView().setXScrollDirection(View.ScrollX.RIGHT);
		}
		if(input.getMouseY()<10){
			getWorldView().setYScrollDirection(View.ScrollY.UP);
		}
		else if(input.getMouseY()>screenSize.height-10){
			getWorldView().setYScrollDirection(View.ScrollY.DOWN);
		}
		///////////////////////////////////////////
		//////
		if(input.isKeyPressed(Input.KEY_ESCAPE)){

			menu.setVisible(!menu.isVisible());
			if(menu.lobbyIsVisible() && menu.isVisible()){
				menu.setLobbyVisible(false);
				menu.setJoinIsVisible(false);
			}
		}
		boolean lcont = false;
		if(!editMode && input.isKeyDown(Input.KEY_LCONTROL) && !menu.lobbyIsVisible()){
			lcont = true;
			for(int i=0;i<numberKeys.length;i++){
				if(input.isKeyPressed(numberKeys[i])){
					if(this.selectedObjects!=null){
						ArrayList<GameObject> go = new ArrayList<GameObject>();
						for(GameObject s:this.selectedObjects){
							if(s.isAlive()){
								go.add(s);
							}
						}
						this.numberGroup.set(i,go);
					}
				}
			}
		}
		if(!editMode && !lcont && !menu.lobbyIsVisible()){
			for(int i=0;i<numberKeys.length;i++){
				if(input.isKeyPressed(numberKeys[i])){
					if(hotkeyTimer.isDone() || i!=lastUsedGroup){
						hotkeyTimer.reset();
						lastUsedGroup = i;
						followGroup = -1;
						ArrayList<GameObject> go = (ArrayList<GameObject>) this.numberGroup.get(i);
						this.selectedObjects = new ArrayList<GameObject>();
						if(go != null && go.size()>0){
							for(GameObject g:go){
								if(g.isAlive()){
									this.selectedObjects.add(g);
								}
							}
						}
					}
					else{
						//lastUsedGroup = -1;
						ArrayList<GameObject> go = (ArrayList<GameObject>) this.numberGroup.get(i);
						if(go != null && go.size()>0){
							for(GameObject g:go){
								if(g.isAlive()){
									//System.out.println(g.getX()+"-"+g.getY()+"-"+worldView.getScreenSize().width+"-"+worldView.getScreenSize().height);
									//System.out.println(g.getxPos()+","+g.getyPos());
									gameWorld.moveScreenToUnit(g);
									followGroup = i;
									break;
								}
							}
						}
					}
				}
			}
		}
		
		if(followGroup!=-1 && input.isKeyDown(numberKeys[followGroup])){
			ArrayList<GameObject> go = (ArrayList<GameObject>) this.numberGroup.get(followGroup);
			if(go != null && go.size()>0){
				for(GameObject g:go){
					if(g.isAlive()){
						gameWorld.moveScreenToUnit(g);
						break;
					}
				}
			}
		}
		if(input.isKeyDown(Input.KEY_LCONTROL)&&input.isKeyPressed(Input.KEY_S)&&editMode){
			if(useRobot)
				robot.keyRelease(KeyEvent.VK_CONTROL);
			tiles.saveMap();
		}
		if(input.isKeyDown(Input.KEY_LCONTROL)&&input.isKeyPressed(Input.KEY_O)){
			if(useRobot)
				robot.keyRelease(KeyEvent.VK_CONTROL);
			tiles = new TileMap(this,"maps/default.map");
			changeGameWorld(tiles.getWorld());
			tiles.loadMap();
		}
		if(input.isKeyDown(Input.KEY_LCONTROL)&&input.isKeyPressed(Input.KEY_E)){
			this.editMode=!this.editMode;
		}
		if(input.isKeyDown(Input.KEY_P)){
			if(t.isDone()){
				paused=!paused;
				t.reset();
			}
		}
		
		if(menu.lobbyIsVisible() && menu.joinIsVisible()){
			//org.newdawn.slick.KeyListener e;
			//try{
			boolean added = false;
			//try {
				//int sleepTime = 150;
				input.disableKeyRepeat();

				for(int i = 0; i < 200; i++){
					if(input.isKeyPressed(i) && !added){
						if(Input.getKeyName(i).equals("PERIOD")){
							menu.addIp(".");
							//Thread.sleep(sleepTime);
						}
						else if(Input.getKeyName(i).equals("MINUS")){
							menu.addIp("-");
							//Thread.sleep(sleepTime);
						}
						else if(Input.getKeyName(i).equals("UNDERLINE")){
							menu.addIp("_");
							//Thread.sleep(sleepTime);
						}
						else if(Input.getKeyName(i).equals("RETURN")){
							menu.join();
							//Thread.sleep(sleepTime);
						}
						else if(Input.getKeyName(i).matches("(\\w|\\d)") || Input.getKeyName(i).equals("BACK")){
							menu.addIp("" + Input.getKeyName(i));
							//Thread.sleep(sleepTime);
						}
						added = true;
						break;
					}
				}
				input.enableKeyRepeat();

			///} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
		}


		setNet(menu.getNet());
		//TODO Network
		if(hasJoined && !isServer){
			gameWorld.setClient(true);
			if(n.playerid==2){
				setMyTeam(blueTeam);
			}
			if(n.playerid==3){
				setMyTeam(grayTeam);
			}
		}
		
		if(isServer){
			gameWorld.setServer(true);
			gameWorld.setClient(false);

			int i = 0;
			
			for(GameObject go : getGameworld().getGameObjects()){
				go.setid(i);
				i++;
			}
			
		}
		
		/*if(menu.hasJoined() && menu.isNetworkServerRunning() && !runOnce){
				//NetworkObject nnetobj = new NetworkObject("SetWorld", gameWorld);
				//n = new NetworkClient(new Socket("localhost", menu.getServerPort()), nnetobj);

				//n = new NetworkClient(new Socket("localhost", menu.getServerPort()), "UPDATE");
				
				if(!NetworkSaved){
					NetworkObject netobj = new NetworkObject("SAVEWORLD",gameWorld);
					n.sendObject(netobj);
					//n = new NetworkClient(new Socket("localhost", menu.getServerPort()), netobj);
					if(n.getResponse().equals("Save success")){
						NetworkSaved = true;
					}
				}
				if(!NetworkWorldLoaded && NetworkSaved){
					//n = new NetworkClient(new Socket("localhost", menu.getServerPort()), "LOADWORLD");
					n.sendObject("LOADWORLD");
					gameWorld = (World) n.getResponse();

					if(gameWorld != null){
						NetworkWorldLoaded = true;
						System.out.println("Wolrd loaded");
					}

				}

				System.out.println(n.getResponse());
				runOnce = true;
		}*/
		
		
		boolean isVictory=true;
		boolean isDefeat=true;
		for(GameObject obj: gameWorld.getGameObjects()){
			if(obj.getTeam()!=myTeam){
				isVictory=false;
				break;
			}
		}
		if(isVictory){
			victory=true;
		}
		if(!isVictory){
			for(GameObject obj: gameWorld.getGameObjects()){
				if(obj.getTeam()==myTeam){
					isDefeat=false;
					break;
				}
			}
			if(isDefeat){
				defeat=true;
			}
		}
		getWorldView().update();
		if(!paused) {
			gameWorld.update(delta);
			updateCount++;
		}
	}
	public boolean isDoubleClick() {
		return doubleClick;
	}
	public void setDoubleClick(boolean doubleClick) {
		this.doubleClick = doubleClick;
	}
	public boolean isSelectMode() {
		return selectMode;
	}
	public void setSelectMode(boolean selectMode) {
		this.selectMode = selectMode;
	}
	public boolean isEditMode() {
		return editMode;
	}
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		Timer.updateTimers(50);
		g.translate(getWorldView().getViewLocationRect().getX(), getWorldView().getViewLocationRect().getY());
		tiles.draw(g);
		gameWorld.render(g);
		
		int mouseWorldX = input.getMouseX() - (int)getWorldView().getCurrentViewLocation().x;
		int mouseWorldY = input.getMouseY() - (int)getWorldView().getCurrentViewLocation().y;
		
		if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)){
			//g.drawString("BLA: "+input.getMouseX()+"-"+input.getMouseY(), worldView.getScreenSize().width/2-75, worldView.getScreenSize().height/2);
			//g.drawString("_"+editor.Area.getMaxY()+editor.Area.getMinY(), worldView.getScreenSize().width/2-75, worldView.getScreenSize().height/2+20);
			if(editMode && editor.Area.contains(input.getMouseX(),input.getMouseY())){
				editor.mouseClick(input.getMouseX(), input.getMouseY());
			}
			else if(menu.Area.contains(input.getMouseX(),input.getMouseY())){
				menu.mouseClick(input.getMouseX(), input.getMouseY(), g);
			}
			else if(!selectMode && gui.Area.contains(input.getMouseX(),input.getMouseY())){
				this.guiMode = true;
				gui.mouseClick(input.getMouseX(), input.getMouseY());
			}//this.selectedObjects.size()==0 && 
			
			if(!this.selectMode && minimap.Area.contains(input.getMouseX(),input.getMouseY())){
				minimapMode = true;
				//getWorldView().setXScrollDirection(View.ScrollX.LEFT);
				float scaleX = gameWorld.getWidth() / (float)minimap.getMinimapWidth();
				float scaleY = gameWorld.getHeight() / (float)minimap.getMinimapHeight();
				Rectangle viewRect = minimap.getShownMinimapRectangle();
				float xfix = -viewRect.getWidth()/2;//(minimap.getMinimapX()+ viewRect.getX()) / 2*scaleX;
				float yfix = -viewRect.getHeight()/2;//25;//-viewRect.getMaxY()/2;
				//System.out.println(xfix+"_"+yfix);
				
				float xpos = -((input.getMouseX()-minimap.Area.getMinX()+xfix)*scaleX);
				float ypos = -((input.getMouseY()-minimap.Area.getMinY()+yfix)*scaleY);
				float a = -1*(float)getWorldView().getCurrentViewLocation().getX();
				float b = -1*(float)getWorldView().getCurrentViewLocation().getY();
				//g.drawString(getWorldView().getCurrentViewLocation().toString(),a+50,b+50); //worldView.getScreenSize().width/2-75, worldView.getScreenSize().height/2-20);
				//g.drawString(getWorldView().getCurrentViewLocation().toString(), worldView.getScreenSize().width/2-75, worldView.getScreenSize().height/2-20);
				getWorldView().setCurrentViewLocation(new Point2D.Float(xpos,ypos));
				
				
			}
			if(editMode){
				if(editor.getType().equals("tile"))
					tiles.setTile(mouseWorldX, mouseWorldY, editor.getSelectedType());
				else if(!minimap.Area.contains(input.getMouseX(), input.getMouseY()) 
						&& !gui.Area.contains(input.getMouseX(), input.getMouseY())
						&& !editor.Area.contains(input.getMouseX(), input.getMouseY())){
					if(t.isDone()){
						int speed;
						if(editor.getSelectedType().equals("earth")
								||editor.getSelectedType().equals("spacestation")){
							speed=0;
						}
						else{
							speed=1;
						}
						if(editor.getSelectedType().equals("ship")){
							
						}
							
						GameObject tempObj = GameObject.createObject(editor.getSelectedType(),this.gameWorld,mouseWorldX,mouseWorldY,editor.getSelectedTeam());
						//tempObj.setAngle(240);
						World.addObject(tempObj);
						t.reset();
					}
				}
			}
		}
		else // If left mouse-button wasn't down
		{
			selectMode = false;
			guiMode = false;
			minimapMode = false;
		}
		if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)){
			if(isHost && minimap.Area.contains(input.getMouseX(),input.getMouseY())){
				float scaleX = gameWorld.getWidth() / (float)minimap.getMinimapWidth();
				float scaleY = gameWorld.getHeight() / (float)minimap.getMinimapHeight();
				int xpos = (int)((input.getMouseX()-minimap.Area.getMinX())*scaleX);
				int ypos = (int)((input.getMouseY()-minimap.Area.getMinY())*scaleY);
				/*float fxpos = -((input.getMouseX()-minimap.Area.getMinX()-50)*scaleX);
				float fypos = -((input.getMouseY()-minimap.Area.getMinY()-25)*scaleY);
				float a = -1*(float)getWorldView().getCurrentViewLocation().getX();
				float b = -1*(float)getWorldView().getCurrentViewLocation().getY();
				
				getWorldView().setCurrentViewLocation(new Point2D.Float(fxpos,fypos));*/
				int spaceX=0,spaceY=0;
				if(this.getSelectedObjects()!=null){
					double curmaxspeed = getMinSpeed(this.getSelectedObjects());
					for(GameObject ob : this.getSelectedObjects()){
						int offset=(int) (Math.sqrt(getSelectedObjects().size())*50)-25;
						ob.setCurSpeed(curmaxspeed);
						ob.move(xpos+spaceX-offset/2,ypos+spaceY-offset/2);
						spaceX+=50;
						if(spaceX>offset){
							spaceY+=50;
							spaceX=0;
						}
					}
				}
			}
			else if(isHost && !editMode){
				int spaceX=0,spaceY=0;
				if(this.getSelectedObjects()!=null){
					double curmaxspeed = getMinSpeed(this.getSelectedObjects());
					for(GameObject ob : this.getSelectedObjects()){
						int offset=(int) (Math.sqrt(getSelectedObjects().size())*50)-25;
						ob.setCurSpeed(curmaxspeed);
						ob.move(mouseWorldX+spaceX-offset/2,mouseWorldY+spaceY-offset/2);
						spaceX+=50;
						if(spaceX>offset){
							spaceY+=50;
							spaceX=0;
						}
					}
				}
			}
			else if(editMode){
				Rectangle rct = new Rectangle(mouseWorldX,mouseWorldY,1,1);
				setSelectedObjects(gameWorld.getAllUnits(rct));
				for(GameObject ob : selectedObjects){
					ob.setAlive(false);
				}
				selectedObjects.clear();
			}
			else{

			}
		}
		/*if(!minimap.Area.contains(input.getMouseX(), input.getMouseY())==true
				&& !gui.Area.contains(input.getMouseX(), input.getMouseY())==true
				&& (!EditMode||!editor.Area.contains(input.getMouseX(), input.getMouseY()))==true
		)*/
		if(!guiMode && !minimapMode && (!editMode||!editor.Area.contains(input.getMouseX(), input.getMouseY())==true))
		{
			if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
				if(doubleClickTimer.isDone()){
					doubleClickTimer.reset();
					doubleClick = false;
				}
				else if(selectedObjects!=null && selectedObjects.size()>0){
					String type = null;
					for(GameObject o:selectedObjects){
						if(type==null){
							type = o.getType();
						}
						else{
							if(!type.equals(o.getType())){
								type = null;
								break;
							}
						}
					}
					if(type!=null){
						doubleClick = true;
						//System.out.println(dragSelect.x+"_"+dragSelect.y);
						Rectangle r = getWorldView().getViewLocationRect();
						float y  = screenSize.height-r.getMaxY();
						float x = screenSize.width-r.getMaxX();
						Rectangle r2 = new Rectangle(x,y,screenSize.width,screenSize.height);
						//System.out.println(r.getMaxX()+"_"+r.getMinX()+":"+r.getMaxY()+"_"+r.getMinY());
						List<GameObject> go = gameWorld.getMyUnits(r2);
						ArrayList<GameObject> objectsToSelect = new ArrayList<GameObject>();
						for(GameObject o:go){
							if(type.equals(o.getType())){
								objectsToSelect.add(o);
							}
						}
						setSelectedObjects(objectsToSelect);
					}
				}
			}
			if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)&& !editMode && !doubleClick) {
				this.selectMode = true;
				followGroup = -1;
				g.setColor(Color.white);
				Rectangle rct = new Rectangle(	dragSelect.x>mouseWorldX?mouseWorldX:dragSelect.x,
						dragSelect.y>mouseWorldY?mouseWorldY:dragSelect.y,
								dragSelect.x>mouseWorldX?dragSelect.x-mouseWorldX:mouseWorldX-dragSelect.x,
										dragSelect.y>mouseWorldY?dragSelect.y-mouseWorldY:mouseWorldY-dragSelect.y);
				g.draw(rct);

				if(input.isKeyDown(Input.KEY_LSHIFT) && selectedObjects!=null){
					for(GameObject o:gameWorld.getMyUnits(rct)){
						if(!selectedObjects.contains(o)){
							selectedObjects.add(o);
						}
					}
				}
				else if(input.isKeyDown(Input.KEY_LCONTROL) && selectedObjects!=null){
					for(GameObject o:gameWorld.getMyUnits(rct)){
						if(selectedObjects.contains(o)){
							selectedObjects.remove(o);
						}
					}
				}
				else{
					setSelectedObjects(gameWorld.getMyUnits(rct));
				}
			}
			else{
				dragSelect.x=mouseWorldX;
				dragSelect.y=mouseWorldY;
			}
		}
		else{
			dragSelect.x=mouseWorldX;
			dragSelect.y=mouseWorldY;
		}

		

		//Draw GUI and rings around selected objects
		g.resetTransform();
		if(getSelectedObjects()!=null){
			for(GameObject obj:getSelectedObjects()){
				if(Math.sqrt(Math.abs(Math.pow(obj.getMoveX()-obj.getX(), 2)+Math.pow(obj.getMoveY()-obj.getY(), 2)))>5){
					g.setColor(obj.getTeam().getColor());
					g.fillRect((float)obj.getMoveX()-1+getWorldView().getCurrentViewLocation().x
							, (float)obj.getMoveY()-1+getWorldView().getCurrentViewLocation().y
							, 3, 3);
				}
				g.setColor(Color.green);
				g.drawOval(obj.getX()-5+getWorldView().getCurrentViewLocation().x, obj.getY()-5+getWorldView().getCurrentViewLocation().y, obj.getSprite().getWidth()+10, obj.getSprite().getHeight()+10);
			}
		}
		/*if(input.isKeyPressed(Input.KEY_DELETE)){
			for(GameObject obj:getSelectedObjects()){
				obj.setHealth(0);
			}
		}*/
		gui.draw(g);
		if(this.editMode){
			editor.draw(g);
		}
		minimap.draw(g);
		if(menu.isVisible() || menu.lobbyIsVisible()){
			menu.draw(g);
		}
		if(paused){
			g.setColor(Color.red);
			g.drawString("GAME PAUSED", 100, 10);
			container.setPaused(paused);
		}
		if(victory){
			g.setColor(Color.orange);
			g.setAntiAlias(true);
			g.drawString("VICTORY!", worldView.getScreenSize().width/2-75, worldView.getScreenSize().height/2);
		}
		if(defeat){
			g.setColor(Color.red);
			g.setAntiAlias(true);
			g.drawString("DEFEATED!", worldView.getScreenSize().width/2-75, worldView.getScreenSize().height/2);
		}
		frameCount++;

	}
	public static void main(String[] args) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			AppGameContainer app = new AppGameContainer(new Game("Ruling the space"));
			app.setVerbose(DEBUG_MODE);
			if(DEBUG_MODE) {
				System.out.println("Supports alpha in backbuffer: " + app.supportsAlphaInBackBuffer());
				System.out.println("Supports multi-sampling: " + app.supportsMultiSample());
			}
			app.setVSync(true);
			app.setTitle("Space Conquest");
			app.setDisplayMode(d.width, d.height, false);
			app.start();
		} catch(SlickException e) {
			e.printStackTrace();
		}
	}
	public void resetVictoryCondition(){
		victory=false;
		defeat=false;
	}
	public void setSelectedObjects(List<GameObject> selectedObjects) {
		this.selectedObjects = selectedObjects;
	}
	public List<GameObject> getSelectedObjects() {
		return selectedObjects;
	}
	public void setWorldView(View worldView) {
		this.worldView = worldView;
	}
	public View getWorldView() {
		return worldView;
	}
	public void setMyTeam(Team myTeam) {
		this.myTeam = myTeam;
	}
	public Team getMyTeam() {
		return myTeam;
	}
	public Team getTeam(Color c){
		if(c.equals(Color.blue)){
			return blueTeam;
		}
		else if(c.equals(Color.red)){
			return redTeam;
		}
		else if(c.equals(Color.gray)){
			return grayTeam;
		}
		return null;
	}
	public void setIsServer(boolean s){
		isServer=s;
	}
	public boolean isServer(){
		return isServer;
	}
	public void setIsClient(boolean c){
		isClient=c;
	}
	public boolean isClient(){
		return isClient();
	}
	public void setJoined(boolean j){
		hasJoined=j;
	}
	public boolean hasJoined(){
		return hasJoined;
	}
	public static void setNet(NetworkClient client){
		n = client;
	}
	public static NetworkClient getNet(){
		return n;
	}
	public World getGameworld(){
		return gameWorld;
	}
	public double getMinSpeed(List<GameObject> gameobjects){
		double minspeed=Double.MAX_VALUE;
		for(GameObject go : gameobjects){
			if(go.isShip()){
				if(minspeed>go.getSpeed()){
					minspeed = go.getSpeed();
				}
			}
		}
		return minspeed;
	}
}
