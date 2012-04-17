package se.space;

import java.awt.Dimension;
import java.net.Socket;
import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

public class MainMenu {

	private Dimension screenSize;
	private boolean visible = false;
	private boolean lobbyVisible = false;
	private boolean joinVisible = false;



	//Main menu
	private HashMap<String,Rectangle> buttons;
	private LinkedList<String> MenuItems;

	//Lobby menu
	private HashMap<String,Rectangle> lobbyButtons;
	private LinkedList<String> lobbyMenuItems;

	//Join menu
	private HashMap<String,Rectangle> joinButtons;
	private LinkedList<String> joinMenuItems;

	public Rectangle Area;
	private Lobby lobby;
	
	private Game game;

	//Network
//	private boolean isRunning = false;
//	private boolean hasJoined = false;
	private int lobbyPortNr = 15000;
	private String ipToConnect;
	private NetworkClient n;
	private NetworkServer server;
	private int netTime = 0;
	private String nick;

	public MainMenu(Game game, World gameWorld, View worldView,NetworkClient net) {
		n=net;
		this.game = game;
		
		screenSize = worldView.getScreenSize();
		buttons = new HashMap<String,Rectangle>();
		MenuItems = new LinkedList<String>();
		int xPos=screenSize.width/2-150+25;
		int yPos=100;

		Area = new Rectangle(screenSize.width/2-150, 0, 300, screenSize.height-150);

		//Main menu
		MenuItems.add("Multiplayer");
		MenuItems.add("Load Game");
		MenuItems.add("Save Game");
		MenuItems.add("Exit");


		Iterator<String> i = MenuItems.iterator();
		while (i.hasNext()){
			String next = i.next();
			buttons.put(next,new Rectangle(xPos-5, yPos-5, 260, 30));
			yPos+=40;
		}

		//Lobby menu
		lobbyButtons = new HashMap<String,Rectangle>();
		lobbyMenuItems = new LinkedList<String>();

		lobbyMenuItems.add("Host game");
		lobbyMenuItems.add("Join by ip");
		lobbyMenuItems.add("Menu");

		Iterator<String> j = lobbyMenuItems.iterator();
		while (j.hasNext()){
			String next = j.next();
			lobbyButtons.put(next,new Rectangle(xPos-5, yPos-85, 260, 30));
			yPos+=40;
		}

		//Join menu
		joinButtons = new HashMap<String,Rectangle>();
		joinMenuItems = new LinkedList<String>();

		joinMenuItems.add("Join");
		joinMenuItems.add("Disconnect");
		int yPosTemp = yPos;
		yPos = 110;
		Iterator<String> k = joinMenuItems.iterator();
		while (k.hasNext()){
			String next = k.next();
			joinButtons.put(next,new Rectangle(xPos-5, yPos-5, 130, 30));
			xPos+=130;
		}
		yPos=yPosTemp+40;

		//Network
		//n = new NetworkClient(new Socket(ipToConnect, lobbyPortNr), "RUNNING?");
	}

	public void setLobby(Lobby l){
		lobby = l;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean b) {
		visible = b;
	}

	public boolean lobbyIsVisible(){
		return lobbyVisible;
	}

	public void setLobbyVisible(boolean b){
		lobbyVisible = b;
	}

	public boolean joinIsVisible(){
		return joinVisible;
	}

	public void setJoinIsVisible(boolean b){
		joinVisible = b;
	}

	public void addIp(String s){
		if(s.equals("BACK") && ipToConnect.length()>0){
			ipToConnect = ipToConnect.substring(0, ipToConnect.length()-1); 
		}
		else if(!s.equals("BACK")){
			ipToConnect += "" + s;
		}
	}

	public void draw(Graphics g) {
		if(visible){
			drawMenu(g);
		}
		if(lobbyVisible){
			drawLobby(g);
		}
		if(joinVisible){
			drawJoinByIp(g);
			drawJoinMenu(g);
		}
		else{
			ipToConnect = "";
		}
		if(game.isServer()&&!visible){
			drawJoinMenu(g);
		}
	}

	public void drawJoinMenu(Graphics g){
		int xPos=screenSize.width/2-150+25;
		int yPos=110;

		//		g.setColor(Color.darkGray);
		//
		//		g.fillRect(screenSize.width/2-150, 50, 300, screenSize.height-250);

		if(game.isServer()){
			g.setColor(Color.red);
		}
		else{
			g.setColor(Color.green);
		}

		Iterator<String> i = joinMenuItems.iterator();
		while (i.hasNext()){
			String next = i.next();

			g.drawString(next, xPos, yPos);
			g.draw(joinButtons.get(next));
			yPos+=0;
			xPos+=130;
			g.setColor(Color.green);
		}

	}


	public void drawMenu(Graphics g){
		int xPos=screenSize.width/2-150+25;
		int yPos=100;

		g.setColor(Color.darkGray);

		g.fillRect(screenSize.width/2-150, 50, 300, screenSize.height-250);

		g.setColor(Color.green);

		Iterator<String> i = MenuItems.iterator();
		while (i.hasNext()){
			String next = i.next();

			g.drawString(next, xPos, yPos);
			g.draw(buttons.get(next));
			yPos+=40;
		}

	}

	public void drawLobby(Graphics g){
		int xPos=screenSize.width/2-150+25;
		int yPos=180;

		g.setColor(Color.darkGray);

		g.fillRect(screenSize.width/2-200, 0, 400, screenSize.height-150);

		g.setColor(Color.green);

		g.drawString("Lobby", screenSize.width/2-150, 50);


		Iterator<String> i = lobbyMenuItems.iterator();
		while (i.hasNext()){
			String next = i.next();

			if(next.equals("Host game") && game.isServer()){
				g.drawString("starting...", xPos, yPos);
			}
			else{
				g.drawString(next, xPos, yPos);
			}
			g.draw(lobbyButtons.get(next));
			yPos+=40;
		}

		if(game.isServer() && netTime > 1000){

			//TODO fix this code
			/*
			g.setColor(Color.green);
			n.sendObject("RUNNING?");
			if(n.getResponse().equals("RUNNING")){
				g.drawString("Server already running", screenSize.width/2-150, 80);
			}
			else{
				g.drawString("Server not running", screenSize.width/2-150, 80);
			}
			 */

			netTime = 0;
		}
		else if(game.isServer()){
			g.drawString("Server already running", screenSize.width/2-150, 80);
			netTime++;
		}
		else if(game.hasJoined()){
			g.drawString("You has joined the game as player"+n.playerid, screenSize.width/2-150, 150);
		}
	}

	public void drawJoinByIp(Graphics g){
		if(lobbyVisible){
			g.drawString(ipToConnect, screenSize.width/2-100, 85);
			g.drawLine(screenSize.width/2-100, 100, screenSize.width/2+100, 100);
		}
	}

	public void mouseClick(int x, int y, Graphics g){

		if(visible){
			Iterator<String> i = MenuItems.iterator();
			while (i.hasNext()){
				i.next();
				if(buttons.get("Exit") != null && buttons.get("Exit").contains(x, y)){
					if(n != null){
						n.closeConnectionToServer();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.exit(0);
				}
				else if(buttons.get("Multiplayer") != null && buttons.get("Multiplayer").contains(x, y)){
					visible = false;
					lobbyVisible = true;
					joinVisible = false;
					//lobby.setMenu(this);
					//lobby.setVisible(true);
					//TODO setup lobby
					//lobby.setupLobby();
				}
			}
		}
		if(lobbyVisible){

			Iterator<String> i = lobbyMenuItems.iterator();
			while (i.hasNext()){
				i.next();
				if(lobbyButtons.get("Menu") != null && lobbyButtons.get("Menu").contains(x, y)){
					visible = true;
					lobbyVisible = false;
					joinVisible = false;
				}
				else if(lobbyButtons.get("Host game") != null && lobbyButtons.get("Host game").contains(x, y)){
					//TODO Create a server
					if(!game.isServer()){
						game.setIsServer(true);

						try {
							//Skapar en ny server
							server = new NetworkServer(lobbyPortNr,game);

							//Väntar på att servern ska starta
							Thread.sleep(500);

							//Joinar server som nu skapats
							n = new NetworkClient(new Socket(ipToConnect, lobbyPortNr),game);

							//Resetar world (krävs ifall servern körts tidigare)
							game.getGameworld().restartWorldNetwork(n);
							
						} catch (Exception e1) {
							System.out.println("Exception " + e1);
						}

					}
					else{
					}
				}
				else if(lobbyButtons.get("Join by ip") != null && lobbyButtons.get("Join by ip").contains(x, y)){
					joinVisible = true;
				}
				else if(joinIsVisible() && joinButtons.get("Join") != null && joinButtons.get("Join").contains(x, y)){
					if(n == null && !game.hasJoined()){
						join();
					}
				}
				else if(n!=null && n.hasJoined && joinIsVisible() && joinButtons.get("Disconnect") != null && joinButtons.get("Disconnect").contains(x, y)){
					n.hasJoined = false;
					game.setJoined(false);
					game.setIsClient(false);
					n.closeConnectionToServer();
					n = null;
					Game.setNet(n);
				}
				else if(server!=null && game.isServer() && joinButtons.get("Disconnect") != null && joinButtons.get("Disconnect").contains(x, y)){
					int players = server.getPlayerid();
					if(players>1){
						for (int j= 2;j<=players;j++){ 
							n.sendObject(new NetworkObject("DISCONNECT"+j,"COMMAND",j));
						}
					}
					n.hasJoined = false;
					game.setJoined(false);
					n.closeConnectionToServer();
					n = null;
					Game.setNet(n);
					
					game.setIsServer(false);
					server.disconnect();

				}
			}
		}

	}


	public int getServerPort(){
		if(game.isServer()){
			return lobbyPortNr;
		}
		return -1;
	}

	public void join(){
		try {
			n = new NetworkClient(new Socket(ipToConnect, lobbyPortNr),game);
			Thread.sleep(1000);

			if(n.hasJoined==true){
				game.setIsClient(true);
				game.setJoined(true);
				game.getGameworld().restartWorldNetwork(n);
			}
		} 
		catch(java.net.ConnectException e){
			System.out.println("Unable to join");
			//TODO update gui
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}

	public NetworkClient getNet(){
		return n;
	}
}
