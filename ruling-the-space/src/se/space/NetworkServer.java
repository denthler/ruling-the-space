package se.space;

import java.net.*;
import java.util.*;
import java.io.*;

public class NetworkServer extends Thread {

	private ServerSocket gameServer;

	private int playerid = 0;


	//Game Varibles
	private transient World savedWorld = null;
	//private static List<GameObject> objectsToAdd;
	//private static List<GameObject> allObjects;

	public static List<Object> allObjectsO;
	public static List<Object> objectsToAddO;

	public static List<Object> allObjectsBlue;
	public static List<Object> allObjectsRed;
	public static List<Object> allObjectsGray;

	public NetworkServer(int portnr) throws Exception {
		//objectsToAdd = new ArrayList<GameObject>();
		//allObjects = new ArrayList<GameObject>();
		gameServer = new ServerSocket(portnr);
		System.out.println("Server listening on " + portnr);
		this.start();
		this.setPriority( NORM_PRIORITY - 4 );
	} 

	public static synchronized void setGameObjects(List<Object> o){
		allObjectsO = o;
	}

	public static synchronized List<Object> getGameObjects(){
		return allObjectsO;
	}

	public static synchronized void addTeamGameObjects(List<Object> o){
		allObjectsO.addAll(o);
	}

	public static synchronized void resetTeamGameObjects(){
		allObjectsO = new ArrayList<Object>();
	}

	public static synchronized void setBlueGameObjects(List<Object> o){
		allObjectsBlue = o;
	}

	public static synchronized List<Object> getBlueGameObjects(){
		return allObjectsBlue;
	}

	public static synchronized void setRedGameObjects(List<Object> o){
		allObjectsRed = o;
	}

	public static synchronized List<Object> getRedGameObjects(){
		return allObjectsRed;
	}

	public static synchronized void setGrayGameObjects(List<Object> o){
		allObjectsGray = o;
	}

	public static synchronized List<Object> getGrayGameObjects(){
		return allObjectsGray;
	}

	public static synchronized void setObjectsToAdd(List<Object> o){
		objectsToAddO = o;
	}

	public static synchronized List<Object> getObjectsToAdd(){
		return objectsToAddO;
	}

	public void run() {
		while(true) {
			try {
				System.out.println("Waiting for connections.");
				Socket client = gameServer.accept();

				//new ServerConnection( gameServer.accept(),playerid ).start();
				playerid++;
				ServerConnection server = new ServerConnection(client,playerid);
				new Thread(server).start();
				System.out.println("Connection accepted "+playerid);
			}
			catch(Exception e){
				System.out.println("__ " + e);
			}
		}
	}

	public void exit(){
		Thread.interrupted();
	}


	class ServerConnection extends Thread {

		Socket client;

		boolean connection = true; // connection tester for current client
		boolean commandSent = false;
		String s;
		Object o;
		private int pid = 0;
		boolean disablejoin=false;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;

		// Pass the socket as a argument to the constructor
		ServerConnection ( Socket client, int playerid ) throws SocketException {
			this.client = client;
			this.client.setTcpNoDelay(true);

			pid=playerid;
			// Set the thread priority down so that the ServerSocket
			// will be responsive to new clients.
			//setPriority( NORM_PRIORITY - 4 );
			setPriority( MIN_PRIORITY );
		}

		public synchronized void loop(){
			try{
				o = ois.readObject();
			}
			catch (IOException e){
				o = null;
			}
			catch (ArrayIndexOutOfBoundsException e){
				System.out.println("Arrray Error");
				e.printStackTrace();
				o = null;
			}
			catch (ClassCastException e){
				System.out.println("Class Error");
				e.printStackTrace();
				o = null;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				o = null;
			}

			try{
				if(o!=null){
					s = o.toString();
					System.out.println("Server"+pid+" received << " + s);
					commandSent = false; 


					try{
						NetworkObject netobj = (NetworkObject) o;
						if(netobj.getPlayerid()==pid){
							if(netobj.getString().indexOf("SAVEWORLD"+pid) != -1){
								World hm = (World) netobj.getObject();
								if(hm != null){
									System.out.println("Server"+pid+" World recevied");
									savedWorld = hm;
								}
							}
							else if(netobj.getString().indexOf("ADDALL"+pid) != -1){

								setGameObjects((List<Object>) netobj.getObject());
								oos.writeObject(new NetworkObject("Units saved","COMMAND",pid));

								System.out.println("Server"+pid+" All gameobject added " + getGameObjects());
								commandSent = true;
							}
							else if(netobj.getString().indexOf("ADDRED"+pid) != -1){
								setRedGameObjects((List<Object>) netobj.getObject());

								oos.writeObject(new NetworkObject("Red Units saved","COMMAND",pid));

								//System.out.println("Server"+pid+" All red gameobject added " + getRedGameObjects());
								commandSent = true;
							}
							else if(netobj.getString().indexOf("ADDBLUE"+pid) != -1){
								setBlueGameObjects((List<Object>) netobj.getObject());

								oos.writeObject(new NetworkObject("Blue Units saved","COMMAND",pid));

								//System.out.println("Server"+pid+" All blue gameobject added " + getBlueGameObjects());
								commandSent = true;
							}
							else if(netobj.getString().indexOf("ADDGRAY"+pid) != -1){
								setGrayGameObjects((List<Object>) netobj.getObject());

								oos.writeObject(new NetworkObject("Gray Units saved","COMMAND",pid));

								//System.out.println("Server"+pid+" All gray gameobject added " + getGrayGameObjects());
								commandSent = true;
							}
							else if(netobj.getString().indexOf("ADD"+pid) != -1){
								setObjectsToAdd((List<Object>) netobj.getObject());

								oos.writeObject(new NetworkObject("Unit to add saved","COMMAND",pid));

								System.out.println(getObjectsToAdd());
								commandSent = true;
							}
							else if (netobj.getString().indexOf("LOADALLGAMEOBJECT"+pid) != -1){

								if(getRedGameObjects()!=null&&getBlueGameObjects()!=null){

									if(!getRedGameObjects().isEmpty() && !getBlueGameObjects().isEmpty() && !getGrayGameObjects().isEmpty()){
										resetTeamGameObjects();
										addTeamGameObjects(getRedGameObjects());
										addTeamGameObjects(getBlueGameObjects());
										addTeamGameObjects(getGrayGameObjects());
									}

									if(!getGameObjects().isEmpty()){
										oos.writeObject(new NetworkObject("ALL GAME OBJECTS LOADED",getGameObjects(),pid));

										System.out.println("Server"+pid+" Sent >>>Obj<<<" + getGameObjects());
									}
									else{
										System.out.println("Server"+pid+" Sent No objects loaded");

										oos.writeObject(new NetworkObject("No objects loaded","COMMAND",pid));
									}

								}
								else{
									oos.writeObject(new NetworkObject("No objects loaded","COMMAND",pid));
								}
								commandSent = true;
							}
							else if (getRedGameObjects() != null && netobj.getString().indexOf("LOADALLREDGAMEOBJECT"+pid) != -1){
								System.out.println(">>>Obj<<<" + getRedGameObjects());

								if(!getRedGameObjects().isEmpty()){
									oos.writeObject(new NetworkObject("ALL RED GAME OBJECTS LOADED",getRedGameObjects(),pid));

									commandSent = true;
								}
								else{
									//oos.writeObject(new NetworkObject("No red objects loaded","COMMAND",pid));

									//commandSent = true;
								}
							}
							else if (getBlueGameObjects() != null && netobj.getString().indexOf("LOADALLBLUEGAMEOBJECT"+pid) != -1){
								System.out.println(">>>Obj<<<" + getBlueGameObjects());
								if(!getBlueGameObjects().isEmpty()){
									oos.writeObject(new NetworkObject("ALL BLUE GAME OBJECTS LOADED",getBlueGameObjects(),pid));

									commandSent = true;
								}
								else{
									//oos.writeObject(new NetworkObject("No blue objects loaded","COMMAND",pid));

									//commandSent = true;
								}
							}
							else if (getGrayGameObjects() != null && netobj.getString().indexOf("LOADALLGRAYGAMEOBJECT"+pid) != -1){
								System.out.println(">>>Obj<<<" + getGrayGameObjects());
								if(!getGrayGameObjects().isEmpty()){
									oos.writeObject(new NetworkObject("ALL GRAY GAME OBJECTS LOADED",getGrayGameObjects(),pid));

									commandSent = true;
								}
								else{
									//oos.writeObject(new NetworkObject("No gray objects loaded","COMMAND",pid));

									//commandSent = true;
								}
							}
							else if(netobj.getString().indexOf("DISCONNECT"+pid) != -1){
								commandSent = true;
								connection = false;
							}
							else{
								//oo.add("Server: Command not found");
								//oos.writeObject(oo);

								//oos.writeObject(new NetworkObject("Server"+pid+": Command "+o+" not found","COMMAND",pid));
								//commandSent = true;
							}
							//oos.writeObject("END");
							//oos.flush();
						}
						else{
							//DO NOTHING, WRONG THREAD
						}
					}
					catch(ClassCastException e){
						//System.out.println("ClassCastException" + e);
						System.out.println("Server"+pid+": Object not found, retrying with string. Serverpid: "+pid);
						String[] splitMsgs = null;
						if(s != null){
							splitMsgs = s.split(";");
						}
						System.out.println("Server"+pid+": "+s+"||||"+splitMsgs.toString());

						if (splitMsgs != null){
							for(int i = 0; i < splitMsgs.length; i++){
								if (splitMsgs[i].indexOf("JOIN") != -1) {
									if(!disablejoin){
										System.out.println("Server"+pid+" Joined");

										oos.writeObject("Joined "+pid);

										commandSent = true;
										disablejoin=true;
									}
									else{
										oos.writeObject("Busy "+pid);
										commandSent = true;
									}
								}
								else if (splitMsgs[i].indexOf("RUNNING?") != -1) {
									oos.writeObject("RUNNING");

									commandSent = true;
								}
								else if (splitMsgs[i].indexOf("CHAT"+pid) != -1) {
									//System.out.println(splitMsgs[i]);
									String c = "";
									if (splitMsgs[i] != null){
										String chat[] = splitMsgs[i].split(" ");
										c += chat[1] + ">>";
										for(int j = 2; j < chat.length; j++){
											c += chat[j] + " ";
										}
									}

									oos.writeObject(c);

									commandSent = true;
								} 
								else if (splitMsgs[i].indexOf("UPDATE"+pid) != -1) {
									//TODO Send data where all players are located

									oos.writeObject(new NetworkObject("Sending updates","COMMAND",pid));

									commandSent = true;
								}
								else if (splitMsgs[i].indexOf("LOADWORLD"+pid) != -1){
									oos.writeObject(new NetworkObject("LOADING WORLD",savedWorld,pid));

									commandSent = true;
								}
								else if (splitMsgs[i].indexOf("LOADGAMEOBJECT"+pid) != -1){
									if(!getObjectsToAdd().isEmpty()){
										oos.writeObject(new NetworkObject("LOADING GAME OBJECT",getObjectsToAdd(),pid));

										commandSent = true;
									}
									else{
										oos.writeObject(new NetworkObject("No objects to add loaded","COMMAND",pid));

										commandSent = true;
									}
								}
								/*else if (splitMsgs[i].indexOf("GETNEWPOS") != -1){
								for(GameObject go : allObjects){
									oos.writeObject(go.getX());
									oos.writeObject(go.getY());
								}
								commandSent = true;
							}*/

								else{
									System.out.println("Server"+pid+": Command not found: "+splitMsgs[i]);

									//oos.writeObject(new NetworkObject("Command not found","COMMAND",pid));
									//commandSent = true;
								}

							}
						}
					}

					if(commandSent){
						oos.flush();
						//oos.reset();

						//System.out.println("Server"+pid+" sent: "+oo);
						//oos.writeObject("END");
						//oos.flush();
						commandSent = false;
					}
					else{
						NetworkObject nerror = (NetworkObject)o;
						System.out.println("Server"+pid+": ERROR ON COMMAND: "+nerror.getString()+" ERORR DATA: "+nerror.getObject()+" FOR SERVER THREAD: "+nerror.getPlayerid());
						oos.writeObject(new NetworkObject("ERROR","COMMAND",nerror.getPlayerid()));
						oos.flush();
						//oos.reset();
					}
				}

				if(connection){
					oos.reset();
				}
			}
			catch(SocketException e){
				connection = false;
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Horrible bug, game shutdown");
				e.printStackTrace();
				System.exit(1);
			}
			//			} catch ( IOException e ) {
			//				try{
			//					NetworkObject z = (NetworkObject)o;
			//					System.out.println( "SERVER"+pid+": I/O error " + z.getString()+"||||"+z.getObject() );
			//				}
			//				catch (Exception e2){
			//					Object z = o;
			//					System.out.println( "SERVER"+pid+": I/O error " + z );
			//				}
			//				System.out.println(ois);
			//				System.out.println();
			//				e.printStackTrace();
			//				System.exit(1);
			//				//break;
			//			}
			//			 //On return from run() the thread process will stop. 
			//			catch (ClassNotFoundException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
		}

		public void run() {

			System.out.println("Accepted a connection from: "+
					client.getInetAddress());
			System.out.println();
			try {
				ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
				oos = new ObjectOutputStream(client.getOutputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			while (connection) {
				loop();
			}

			// Disconnect (and ID) client
			System.out.println(client.toString()); 
			System.out.println( "Disconnecting... ");

			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
