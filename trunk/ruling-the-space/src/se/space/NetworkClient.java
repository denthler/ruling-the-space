package se.space;

import java.io.*;
import java.net.*;
import java.util.*;


public class NetworkClient extends Thread {
	private Socket client = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	//private BufferedOutputStream boos;
	//private BufferedInputStream bois;
	private Object msg;
	private Object receivedResponese;
	private List<Object> receivedResponeseA = new ArrayList<Object>();
	public int playerid = 0;
	public boolean hasJoined = false;
	public boolean dataToLoad = false;

//	public NetworkClient(Socket clientSocket, Object msgtosend) {
//		client = clientSocket;
//		
//		try{
//			client.setTcpNoDelay(true);
//		}
//		catch(Exception e){
//			System.out.println("setTcpNoDelay Failed");
//		}
//		
//		msg = msgtosend;
//		try {
//			client.setSoTimeout(1000);
//			oos = new ObjectOutputStream(client.getOutputStream());
//			ois = new ObjectInputStream(client.getInputStream());
//			oos.writeObject(new NetworkObject("ClientInit",msg,playerid));
//			oos.flush();
//		} catch(Exception e1) {
//			try {
//				client.close();
//			}catch(Exception e) {
//				System.out.println(e.getMessage());
//			}
//			return;
//		}
//		// Set the thread priority down so that the ServerSocket
//					// will be responsive to new clients.
//		setPriority( MIN_PRIORITY );
//	}

	public NetworkClient(Socket clientSocket) {
		client = clientSocket;
		
		try {
			//client.setSoTimeout(1000);
			oos = new ObjectOutputStream(client.getOutputStream());
			ois = new ObjectInputStream(client.getInputStream());
			//boos = new BufferedOutputStream(oos);
			//bois = new BufferedInputStream(ois);
		} catch(Exception e1) {
			try {
				client.close();
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
			return;
		}
		while(client != null && !hasJoined){
			sendObject("JOIN");
		}
		// Set the thread priority down so that the ServerSocket
					// will be responsive to new clients.
					setPriority( MIN_PRIORITY );
	}

	public void closeConnectionToServer(){
		try {
			// close streams and connections
			oos.writeObject(new NetworkObject("DISCONNECT"+playerid,null,playerid));
			oos.flush();
			ois.close();
			oos.close();
			//client.close();
			//client = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public void sendObject(Object o){
		try {
			//oos = new ObjectOutputStream(client.getOutputStream());
			//ois = new ObjectInputStream(client.getInputStream());
			//oos.reset();
			oos.writeObject((Object)o);
			oos.flush();
			//oos.reset();

			try{
				NetworkObject no = (NetworkObject) o;
				//System.out.println("Client"+playerid+" sent >> " + no.getString() + " || " + no.getObject());
			}
			catch (Exception e){
				//System.out.println("Client"+playerid+" sent >> " + o);
			}
			System.out.println("Client"+playerid+": WAITING FOR READ");

			if(hasJoined){
				//int playid = 0;


				//while(playid!=playerid){
					Object recResp = ois.readObject();
					System.out.println("Client"+playerid+" Response to be understood: "+recResp);


					NetworkObject nob = (NetworkObject) recResp;



					try{
						//playid = nob.getPlayerid();
						if(nob.getPlayerid()==playerid && !nob.getObject().equals("COMMAND")){
							receivedResponeseA.add(nob.getObject());
							dataToLoad = true;
							System.out.println("Client"+playerid+" Got networkObject: "+receivedResponeseA.get(receivedResponeseA.size()-1));
						}
						else if(nob.getPlayerid()==playerid && nob.getObject().equals("COMMAND")){
							System.out.println("Client"+playerid+" Got String: "+nob.getString());
						}
						else{
							System.out.println("Client"+playerid+" Packetdropped, intended for player: "+nob.getPlayerid()+" Message:"+nob.getString());
						}
					}
					catch(ClassCastException e){
						System.out.println("Client"+playerid+" INVALID DATA:"+recResp);
						e.printStackTrace();
					}
				//} //ENDWHILE
			}
			else{
				receivedResponese = ois.readObject();
				System.out.println("Client received << " + receivedResponese);
				if(receivedResponese.toString().contains("Joined")){
					String[] s = receivedResponese.toString().split(" ");
					playerid = Integer.parseInt(s[1]);
					hasJoined=true;
				}
				else if(receivedResponese.toString().contains("Busy")){
					hasJoined=false;
				}
				else if(receivedResponese.toString().contains("RUNNING")){
					playerid = 1;
					hasJoined=true;
				}
			}

			//ois.close();
			//oos.close();
			oos.reset();

		}
		catch (SocketException e){
			System.out.println("Disconnected?");
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RuntimeException(e);
		}		 catch (ClassNotFoundException e) {
			//			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	////////////////////////////

	public void run() {
		//Nothing here required, thread works anyway
	}

	public synchronized void setResponse(Object resp){
		receivedResponese = resp;
	}

	public synchronized Object getResponse(){
		if(receivedResponese != null){
			return receivedResponese;
		}
		else{
			return "no response";
		}
	}

	public synchronized List<Object> getResponseArray(){
		if(receivedResponese != null){
			return receivedResponeseA;
		}
		else{
			System.out.println("No array received");
		}
		return null;
	}
	
	public synchronized List<Object> getResponseArrayAndRemove(){
		if(!receivedResponeseA.isEmpty()){
			List<Object> lo = new ArrayList<Object>();
			
			for (Object o : receivedResponeseA) {
				lo.add(o);
		    }
			
			receivedResponeseA.clear();
			return lo;
		}
		else{
			System.out.println("No array received");
		}
		return null;
	}

}
