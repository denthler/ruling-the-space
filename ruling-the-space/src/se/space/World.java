package se.space;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class World implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Game game;
	private Gui gui;
	private Dimension worldSize;
	private List<GameObject> gameObjects;
	private static List<GameObject> objectsToAdd;
	private Random rand = new Random();

	//Network
	private int portNr = 15000;
	boolean isClient = false;
	boolean isServer = false;
	boolean ServerOneTime = false;
	boolean ServerOneTimeSetted = false;
	boolean ClientOneTime = true;
	boolean ClientOneTime2 = true;
	private int netTime = 0;
	private NetworkClient n;

	Color colorA = null;
	Color colorB = null;
	Color colorC = null;
	String teamcolorA = "";
	String teamcolorB = "";
	String teamcolorC = "";
	int teamToLoad = 0;

	public World(Game game, Dimension worldSize) {
		this.setGame(game);
		this.worldSize = worldSize;
		setGameObjects(new ArrayList<GameObject>());
		objectsToAdd = new ArrayList<GameObject>();
	}
	public Dimension getWorldSize() {
		return new Dimension(worldSize);
	}

	public int getWidth() {
		return worldSize.width;
	}

	public int getHeight() {
		return worldSize.height;
	}

	public void setServer(boolean s){
		if(!ServerOneTimeSetted){
			ServerOneTime = true;
			ServerOneTimeSetted = true;
		}
		isServer = s;
	}

	public void setClient(boolean c){
		isClient = c;
	}

	public void updateNetwork(){

		if(n==null){
			n = game.getNet();
		}

		if(n!=null && n.hasJoined){

			//Needs to be higher than 0, maybe at least 5
			int timeBeforeNetUpdate = 20;

			if(n!=null && netTime > timeBeforeNetUpdate){

				if(ClientOneTime){
					//Load Blue+Gray
					if(n.playerid==1){
						colorA = Color.blue;
						colorB = Color.gray;	
						teamcolorA = "BLUE";
						teamcolorB = "GRAY";
						//OWN TEAM
						colorC = Color.red;
						teamcolorC = "RED";
					}
					//Load Red+Gray
					else if(n.playerid==2){
						colorA = Color.red;
						colorB = Color.gray;	
						teamcolorA = "RED";
						teamcolorB = "GRAY";
						//OWN TEAM
						colorC = Color.blue;
						teamcolorC = "BLUE";
					}
					//Load Red+Blue
					else if(n.playerid==3){
						colorA = Color.red;
						colorB = Color.blue;
						teamcolorA = "RED";
						teamcolorB = "BLUE";
						//OWN TEAM
						colorC = Color.gray;
						teamcolorC = "GRAY";
					}
					n.sendObject(new NetworkObject("LOADALLREDGAMEOBJECT"+n.playerid,"COMMAND",n.playerid));
					n.sendObject(new NetworkObject("LOADALLBLUEGAMEOBJECT"+n.playerid,"COMMAND",n.playerid));
					n.sendObject(new NetworkObject("LOADALLGRAYGAMEOBJECT"+n.playerid,"COMMAND",n.playerid));
				}
				else{
					//Laddar varrannan gång, för att få bättre prestanda
					if(teamToLoad==0){
						n.sendObject(new NetworkObject("LOADALL"+teamcolorA+"GAMEOBJECT"+n.playerid,"COMMAND",n.playerid));
						teamToLoad = 1;
					}
					else if (teamToLoad==1){
						n.sendObject(new NetworkObject("LOADALL"+teamcolorB+"GAMEOBJECT"+n.playerid,"COMMAND",n.playerid));
						teamToLoad = 0;
					}
				}
			}

			if(!ServerOneTime && n!=null && n.dataToLoad){

				List<Object> toAdd = n.getResponseArrayAndRemove();
				//System.out.println("WORLD:"+toAdd);

				List<GameObject> newgameObjects = new ArrayList<GameObject>();
				ArrayList<Integer> gameObjectsToRemove = new ArrayList<Integer>();
				int updateingteam = 0;
				for(Object o : toAdd){
					Object obj = o;
					List<Object> go = null;
					try{
						go = (List<Object>) obj;
					}
					catch(Exception e){
						//Incorrect message
						//System.out.println(n.getResponse() + " is not a List");
					}
					if(go != null){

						for(Object in : go){
							try{
								List<Object> list = (ArrayList) in;

								//System.out.println(in);

								int id = Integer.parseInt((String) list.get(0).toString());
								double angle = Double.parseDouble((String) list.get(1).toString());
								double x = Double.parseDouble((String) list.get(2).toString());
								double y = Double.parseDouble((String) list.get(3).toString());
								double movex = Double.parseDouble((String) list.get(4).toString());
								double movey = Double.parseDouble((String) list.get(5).toString());
								double curspeed = Double.parseDouble((String) list.get(6).toString());
								Team team = ((Team) list.get(12));
								String type = ((String) list.get(13));

								GameObject gObj = null;
								//gObj = new GameObject(game.getGameworld(), (int) x, (int) y, Game.IMAGE_PATH + type,Game.IMAGE_PATH + type, (int) speed, team, type);
								gObj = GameObject.createObject(type, game.getGameworld(), (int)x, (int)y, team);
								gObj.setid(id);
								gObj.setCurSpeed(curspeed);
								gObj.setAngle((int) angle);
								gObj.setMoveX(movex);
								gObj.setMoveY(movey);


								if(team.getColor().equals(colorA)){
									if(teamcolorA.equals("RED")){
										gObj.setTeam(game.redTeam);
									}
									else if(teamcolorA.equals("BLUE")){
										gObj.setTeam(game.blueTeam);
									}
									else if(teamcolorA.equals("GRAY")){
										gObj.setTeam(game.grayTeam);
									}
									updateingteam = 1; //A
								}
								else if(team.getColor().equals(colorB)){
									if(teamcolorB.equals("RED")){
										gObj.setTeam(game.redTeam);
									}
									else if(teamcolorB.equals("BLUE")){
										gObj.setTeam(game.blueTeam);
									}
									else if(teamcolorB.equals("GRAY")){
										gObj.setTeam(game.grayTeam);
									}
									updateingteam = 2; //B
								}
								else if(team.getColor().equals(colorC)){
									if(teamcolorC.equals("RED")){
										gObj.setTeam(game.redTeam);
									}
									else if(teamcolorC.equals("BLUE")){
										gObj.setTeam(game.blueTeam);
									}
									else if(teamcolorC.equals("GRAY")){
										gObj.setTeam(game.grayTeam);
									}
									updateingteam = 3; //C
									//System.out.println("Speed: "+speed + " Angle:" +angle);
									//								if(team.getColor().equals(Color.red)){
									//									gObj.setTeam(game.redTeam);
									//								}
								}
								if(gObj != null){
									if(updateingteam == 1 && gObj.getTeam().getColor().equals(colorA)){
										//if(gObj.getTeam().getColor().equals(colorA)){
										newgameObjects.add(gObj);
										//gameObjects.remove(gObj);
									}
									else if(updateingteam == 2 && gObj.getTeam().getColor().equals(colorB)){
										newgameObjects.add(gObj);
										//gameObjects.remove(gObj);
									}
									else if(updateingteam == 3 && gObj.getTeam().getColor().equals(colorC)){
										newgameObjects.add(gObj);
										//gameObjects.remove(gObj);
									}
									for(int i = 0; i < gameObjects.size(); i++){
										if(updateingteam == 1 && gameObjects.get(i).getTeam().getColor().equals(colorA)){
											gameObjectsToRemove.add(i);
										}
										else if(updateingteam == 2 && gameObjects.get(i).getTeam().getColor().equals(colorB)){
											gameObjectsToRemove.add(i);
										}
										else if(updateingteam == 3 && gameObjects.get(i).getTeam().getColor().equals(colorC)){
											gameObjectsToRemove.add(i);
										}
									}

								}

							}
							catch(java.lang.ClassCastException e){
								e.printStackTrace();
							}
							if(!newgameObjects.isEmpty()){
								//System.out.println("New objects loaded");
							}
						}
						//Object obj = n.getResponse();
						//System.out.println("WORLD"+n.playerid+" DATA RECIEVED: "+obj);
					}




					n.dataToLoad = false;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				ClientOneTime = false;
				gameObjects = removeElements(gameObjects,gameObjectsToRemove);
				gameObjects.addAll(newgameObjects);

			}


			//System.out.println("CLIENT"+isClient);
			if(n != null && netTime > timeBeforeNetUpdate){

				//Send blue team to server
				try {
					List<Object> l = new ArrayList<Object>();
					List<Object> red = new ArrayList<Object>();
					List<Object> blue = new ArrayList<Object>();
					List<Object> gray = new ArrayList<Object>();

					for(GameObject gObj : gameObjects){
						List<Object> tmpList = new ArrayList<Object>();
						tmpList = gObj.getNetGameObject();
						Team team = ((Team) tmpList.get(12));

						if((ServerOneTime || n.playerid==1) && team.getColor().equals(Color.red)){
							red.add(tmpList);
						}
						else if((ServerOneTime || n.playerid==2) && team.getColor().equals(Color.blue)){
							blue.add(tmpList);
						}
						else if((ServerOneTime || n.playerid==3) && team.getColor().equals(Color.gray)){
							gray.add(tmpList);
						}

					}

					//System.out.println("SET:"+ServerOneTime);
					if(ServerOneTime){
						n.sendObject(new NetworkObject("ADDRED"+n.playerid, red,n.playerid));
						n.sendObject(new NetworkObject("ADDBLUE"+n.playerid, blue,n.playerid));
						n.sendObject(new NetworkObject("ADDGRAY"+n.playerid, gray,n.playerid));
						ServerOneTime=false;
					}
					else if(n.playerid==1&&!ClientOneTime){
						n.sendObject(new NetworkObject("ADDRED"+n.playerid, red,n.playerid));
					}
					else if(n.playerid==2&&!ClientOneTime){
						n.sendObject(new NetworkObject("ADDBLUE"+n.playerid, blue,n.playerid));
					}
					else if(n.playerid==3&&!ClientOneTime){
						n.sendObject(new NetworkObject("ADDGRAY"+n.playerid, gray,n.playerid));
					}
				}catch(Exception e){
					e.printStackTrace();
				}


				netTime = 0;
			}

			netTime++;
		}
	}

	public void moveScreenToUnit(GameObject g){
		float x = (float)-(g.getxPos()-game.getWorldView().getScreenSize().width/2);
		float y = (float)-(g.getyPos()-game.getWorldView().getScreenSize().height/2+50);
		game.getWorldView().setCurrentViewLocation(new Point2D.Float(x,y));
	}

	/**
	 * creates a new List containing all elements of {@code original},
	 * apart from those with an index in {@code indices}.
	 * Neither the original list nor the indices collection is changed.
	 * @return a new list containing only the remaining elements.
	 */
	public <X> List<X> removeElements(List<X> original, Collection<Integer> indices) {
		// wrap for faster access.
		indices = new HashSet<Integer>(indices);
		List<X> output = new ArrayList<X>();
		int len = original.size();
		for(int i = 0; i < len; i++) {
			if(!indices.contains(i)) {
				output.add(original.get(i));
			}
		}
		return output;
	}


	public void update(int delta) {
		//Timer.updateTimers(delta);

		//Reset collision data
		//for(GameObject object : gameObjects) {
		//	object.setColliding(false);
		//}


		//Add the objects created last update
		for(GameObject objectToAdd : objectsToAdd) {
			getGameObjects().add(objectToAdd);
		}
		objectsToAdd.clear();



		//ai.update();

		//Update state for all GameObjects
		for(GameObject object : getGameObjects()) {
			if(object.isShip() && object.getTeam()==game.getMyTeam()){
				object.checkLevelUp();
			}
			if(!game.isEditMode() && !game.isPaused()){
				object.update();
			}
		}
		//Remove dead objects from the game
		Iterator<GameObject> it = getGameObjects().iterator();
		while(it.hasNext()) {
			GameObject object = it.next();
			if(!object.isAlive()) {
				object.destroy();
				it.remove();
			}
		}

		
	}
	public void init(Gui gui) {
		this.gui = gui;
	}
	public List<GameObject> getAllUnits() {
		// TODO Auto-generated method stub
		return null;
	}
	public List<GameObject> getAllUnits(Rectangle rt){
		List<GameObject> returnObj = new ArrayList<GameObject>();
		for(GameObject object : getGameObjects()) {
			if(rt.intersects(object.getSpriteRectangle())){
				returnObj.add(object);
			}
		}
		return returnObj;

	}
	public List<GameObject> getMyUnits(Rectangle rt){
		List<GameObject> returnObj = new ArrayList<GameObject>();
		for(GameObject object : getGameObjects()) {
			if(rt.intersects(object.getSpriteRectangle())){//rt.contains((float)object.getxPos(), (float)object.getyPos())){
				if(object.getTeam()==getGame().getMyTeam()){
					returnObj.add(object);
				}
			}
		}
		return returnObj;
	}

	public void render(Graphics g) {
		for(GameObject object : getGameObjects()) {
			object.draw(g);
		}
	}

	public static void addObject(GameObject netGameObject) {
		objectsToAdd.add(netGameObject);
	}
	public void setGameObjects(List<GameObject> gameObjects) {
		this.gameObjects = gameObjects;
	}
	public List<GameObject> getGameObjects() {
		return gameObjects;
	}
	public void setGame(Game game) {
		this.game = game;
	}
	public Game getGame() {
		return game;
	}
	public void restartWorldNetwork(NetworkClient nc){
		if(game.isServer()){
			setServer(true);
		}
		ClientOneTime = true;
		ClientOneTime2 = true;
		n = nc;
	}
}
