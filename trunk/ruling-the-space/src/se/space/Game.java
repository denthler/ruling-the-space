package se.ruling.space;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
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
	private boolean EditMode = false;
	private boolean dragView = false;
	private boolean paused = false;
	private int frameCount = 0; 
	private int updateCount = 0;
	private List<GameObject> selectedObjects;
	static public HashMap<String,Image> objectList;
	private Timer t;
	private MainMenu menu;
	private Lobby lobby;
	private boolean isFullscreen;
	private boolean isHost;
	public Team redTeam;
	public Team blueTeam;
	public Team grayTeam;
	private Team myTeam;
	public static final boolean DEBUG_MODE = false;
	public static final String IMAGE_PATH = "images/";

	//Network
	private NetworkClient n;
	private boolean NetworkWorldLoaded = false;
	private boolean NetworkSaved = false;
	private boolean runOnce = false;
	private boolean victory = false;

	@Override
	public void init(GameContainer container) throws SlickException {
		container.setShowFPS(false);
		objectList = new HashMap<String,Image>();
		try {
			objectList.put(Game.IMAGE_PATH + "ship.png", new Image(Game.IMAGE_PATH + "ship.png"));
			objectList.put(Game.IMAGE_PATH + "earth.png", new Image(Game.IMAGE_PATH + "earth.png"));
			objectList.put(Game.IMAGE_PATH + "spacestation.png", new Image(Game.IMAGE_PATH + "spacestation.png"));
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		blueTeam=new Team(Color.blue);
		redTeam=new Team(Color.red);
		grayTeam=new Team(Color.gray);
		myTeam=redTeam;
		t= Timer.createTimer(1000);
		isFullscreen=false;
		isHost=true;
		container.setMinimumLogicUpdateInterval(20);
		container.setMaximumLogicUpdateInterval(20);
		container.setUpdateOnlyWhenVisible(false);
		container.setAlwaysRender(true);
		container.setSmoothDeltas(true);
		container.setFullscreen(isFullscreen);
		
		//Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize = new Dimension(container.getWidth(),container.getHeight());
		input = container.getInput();
		gameWorld = new World(this, new Dimension(2000, 2000));
		setWorldView(new View(gameWorld, screenSize));
		tiles = new TileMap(this, gameWorld, getWorldView(),"maps/default.xD");
		minimap = new Minimap(this, gameWorld, getWorldView());

		gui = new Gui(gameWorld, screenSize);
		editor = new Editor(gameWorld, screenSize,this);
		gameWorld.init(gui);
		gameWorld.update(0);
		//lobby = new Lobby(this,gameWorld,getWorldView());
		lobby = new Lobby();
		menu = new MainMenu(this,gameWorld,getWorldView());
		menu.setLobby(lobby);
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
			}
		}
		if(menu.isVisible()){
			if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){

			}
		}
		if(input.isKeyDown(Input.KEY_LCONTROL)&&input.isKeyPressed(Input.KEY_S)&&EditMode){
			tiles.saveMap();
		}
		if(input.isKeyDown(Input.KEY_LCONTROL)&&input.isKeyPressed(Input.KEY_O)){
			tiles.loadMap();
		}
		if(input.isKeyDown(Input.KEY_LCONTROL)&&input.isKeyPressed(Input.KEY_E)){
			this.EditMode=!this.EditMode;
		}
		if(input.isKeyDown(Input.KEY_P)){
			if(t.isDone()){
				paused=!paused;
				t.reset();
			}
		}


		setNet(menu.getNet());
		//TODO Network
		if(menu.hasJoined() && !menu.isNetworkServerRunning()){
			gameWorld.setClient(true);
			if(n.playerid==2){
				setMyTeam(blueTeam);
			}
			if(n.playerid==3){
				setMyTeam(grayTeam);
			}
		}
		
		if(menu.isNetworkServerRunning()){
			gameWorld.setServer(true);
			gameWorld.setClient(false);
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
		boolean isVictory=false;
		for(GameObject obj: gameWorld.getGameObjects()){
			if(obj.getTeam()==myTeam){
				isVictory=true;
			}
			else{
				isVictory=false;
				break;
			}
		}
		if(isVictory){
			victory=true;
		}
		getWorldView().update();
		if(!paused) {
			gameWorld.update(delta);
			updateCount++;
		}
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
			if(editor.Area.contains(input.getMouseX(),input.getMouseY())){
				editor.mouseClick(input.getMouseX(), input.getMouseY());
			}
			else if(menu.Area.contains(input.getMouseX(),input.getMouseY())){
				menu.mouseClick(input.getMouseX(), input.getMouseY(), g);
			}
			else if(gui.Area.contains(input.getMouseX(),input.getMouseY())){
				gui.mouseClick(input.getMouseX(), input.getMouseY());
			}
			else if(minimap.Area.contains(input.getMouseX(),input.getMouseY())){

			}
			if(EditMode){
				if(editor.getType().equals("tile"))
					tiles.setTile(mouseWorldX, mouseWorldY, editor.getSelectedType());
				else if(!minimap.Area.contains(input.getMouseX(), input.getMouseY()) 
						&& !gui.Area.contains(input.getMouseX(), input.getMouseY()) 
						&& !editor.Area.contains(input.getMouseX(), input.getMouseY())){
					if(t.isDone()){
						int speed;
						if(editor.getSelectedType().equals("earth.png")
								||editor.getSelectedType().equals("spacestation.png")){
							speed=0;
						}
						else{
							speed=1;
						}
						GameObject tempObj = new GameObject(this.gameWorld,mouseWorldX, mouseWorldY,
								new Image(Game.IMAGE_PATH + editor.getSelectedType()),
								speed,editor.getSelectedTeam(),editor.getSelectedType());
						if(editor.getSelectedType().equals("spacestation.png")){
							tempObj.setHealth(2000);
							tempObj.setDammage(5);
						}
						World.addObject(tempObj);
						t.reset();
					}
				}
			}
		}
		if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)&&!EditMode){
			if(isHost){
				int spaceX=0,spaceY=0;
				if(this.getSelectedObjects()!=null)
					for(GameObject ob : this.getSelectedObjects()){
						int offset=(int) (Math.sqrt(getSelectedObjects().size())*50)-25;
						ob.move(mouseWorldX+spaceX-offset/2,mouseWorldY+spaceY-offset/2);
						spaceX+=50;
						if(spaceX>offset){
							spaceY+=50;
							spaceX=0;
						}
					}
			}
			else{

			}
		}
		if(!minimap.Area.contains(input.getMouseX(), input.getMouseY())==true
				&& !gui.Area.contains(input.getMouseX(), input.getMouseY())==true
				&& (!EditMode||!editor.Area.contains(input.getMouseX(), input.getMouseY()))==true
		){
			if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)&& !EditMode) {
				g.setColor(Color.white);
				Rectangle rct = new Rectangle(	dragSelect.x>mouseWorldX?mouseWorldX:dragSelect.x,
						dragSelect.y>mouseWorldY?mouseWorldY:dragSelect.y,
								dragSelect.x>mouseWorldX?dragSelect.x-mouseWorldX:mouseWorldX-dragSelect.x,
										dragSelect.y>mouseWorldY?dragSelect.y-mouseWorldY:mouseWorldY-dragSelect.y);
				g.draw(rct);

				setSelectedObjects(gameWorld.getMyUnits(rct));
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

		if(menu.lobbyIsVisible() && menu.joinIsVisible()){
			//org.newdawn.slick.KeyListener e;
			//try{
			boolean added = false;
			//try {
				//int sleepTime = 150;
				input.disableKeyRepeat();
				for(int i = 0; i < 10; i++){
					if(input.isKeyPressed(i) && !added){
						menu.addIp("" + Input.getKeyName(i));
						added = true;
						//Thread.sleep(sleepTime);
						break;
					}
				}
				for(int i = 10; i < 200; i++){
					if(input.isKeyPressed(i) && !added){
						if(Input.getKeyName(i).equals("PERIOD")){
							menu.addIp(".");
							//Thread.sleep(sleepTime);
						}
						else if(Input.getKeyName(i).equals("MINUS")){
							menu.addIp("-");
							//Thread.sleep(sleepTime);
						}
						else if(Input.getKeyName(i).equals("RETURN")){
							menu.join();
							//Thread.sleep(sleepTime);
						}
						else{
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
		if(input.isKeyPressed(Input.KEY_DELETE)){
			for(GameObject obj:getSelectedObjects()){
				obj.setHealth(0);
			}
		}
		gui.draw(g);
		if(this.EditMode){
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
		frameCount++;

		//Detta pausar programmet i en millisekund, för att datorn inte
		//ska bli superseg när jag minimerar spelet
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			AppGameContainer app = new AppGameContainer(new Game("TestGame"));
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
	public void setServer(boolean s){
		menu.setServer(s);
	}
	public void setClient(boolean c){
		menu.setClient(c);
	}
	public void setNet(NetworkClient client){
		n = client;
	}
	public NetworkClient getNet(){
		return n;
	}
	public World getGameworld(){
		return gameWorld;
	}
}