package se.space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Dimension;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import se.space.buildings.*;
import se.space.spaceships.*;

public class TileMap {
	private Game 							game;
	static public HashMap<String,Image> 	sprite;
	private Image							cachedImage;
	private World 							world;
	private View							worldView;
	private Tile 							list[][];
	private String 							mapFile;
	private static boolean					isSaving;


	public TileMap(Game game, String file){
		mapFile=file;
		Dimension d = loadDimension(mapFile);
		this.world=new World(game,d);
		Dimension screenSize = game.getScreenSize();
		this.worldView = new View(world, screenSize);
		game.setWorldView(worldView);
		this.game=world.getGame();
		//this.worldView=worldView;
		int numTilesX = world.getWidth() / Tile.DIMENSIONS+10;
		int numTilesY = world.getHeight() / Tile.DIMENSIONS+10;
		list= new Tile[numTilesX][numTilesY];
		try {
			for(Tile[] tempList:list)
				for(Tile tempTile:tempList)
					tempTile = new Tile(Game.IMAGE_PATH + "groundTile1.png",Tile.DIMENSIONS,Tile.DIMENSIONS);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sprite = new HashMap<String,Image>();
			sprite.put(Game.IMAGE_PATH + "groundTile1.png", new Image(Game.IMAGE_PATH + "groundTile1.png"));
			sprite.put(Game.IMAGE_PATH + "groundTile2.png", new Image(Game.IMAGE_PATH + "groundTile2.png"));
			sprite.put(Game.IMAGE_PATH + "groundTile3.png", new Image(Game.IMAGE_PATH + "groundTile3.png"));
			sprite.put(Game.IMAGE_PATH + "astroid1.png", new Image(Game.IMAGE_PATH + "astroid1.png"));
			sprite.put(Game.IMAGE_PATH + "astroid2.png", new Image(Game.IMAGE_PATH + "astroid2.png"));
		} catch(SlickException ex) {
			System.out.println("Could not load image");
			ex.printStackTrace();
		}
		//drawMap(game,world,worldView);// to create a random map
		loadThis(mapFile);


	}
	private Dimension loadDimension(String mapFile) {
		BufferedReader in = null;
		try{
			in = new BufferedReader(new FileReader(mapFile));
		}
		catch(FileNotFoundException e){System.out.print("File not found");}
		try{
			int x = Integer.parseInt(in.readLine());
			int y = Integer.parseInt(in.readLine());
			in.close();
			return new Dimension(x,y);
		}
		catch(Exception e){e.printStackTrace();}
		return null;
	}
	public World getWorld(){
		return world;
	}
	private void loadThis(String tempMapFile) {
		if(world.getGameObjects()!=null){
			for(GameObject obj:world.getGameObjects()){
				obj.health=0;
			}
		}
		world.setGameObjects(new ArrayList<GameObject>());
		//world.setObjectsToAdd(new ArrayList<GameObject>());
		BufferedReader in = null;
		try{
			in = new BufferedReader(new FileReader(""+tempMapFile));
		}
		catch(FileNotFoundException e){System.out.print("File not found");}
		String line;
		try{
			try {
				cachedImage = new Image(world.getWidth(), world.getHeight());
				Graphics g = cachedImage.getGraphics();
				//		        load tiles
				in.readLine();
				in.readLine();
				for(int yPos = 0; (line = in.readLine())!=null; yPos++) {
					if(line.equals("///")){
						break;
					}
					String[] lineStrings = line.split(" ");
					for(int xPos = 0; xPos<lineStrings.length-1; xPos++) {
						list[yPos][xPos]=new Tile(lineStrings[xPos],yPos*Tile.DIMENSIONS,xPos*Tile.DIMENSIONS);
						try{
							g.drawImage(sprite.get(lineStrings[xPos]), yPos * Tile.DIMENSIONS, xPos * Tile.DIMENSIONS);
						}
						catch(NullPointerException e){
							System.out.println(lineStrings[xPos]);
						}
					}
				}
				//		        load objects
				while((line = in.readLine())!=null){
					String[] lineStrings = line.split("\t");
					Team t=game.grayTeam;
					if(game.redTeam.getColor().equals(new Color(Integer.decode(lineStrings[3]),Integer.decode(lineStrings[4]),Integer.decode(lineStrings[5])))){
						t=game.redTeam;
					}
					if(game.blueTeam.getColor().equals(new Color(Integer.decode(lineStrings[3]),Integer.decode(lineStrings[4]),Integer.decode(lineStrings[5])))){
						t=game.blueTeam;
					}
					int speed;
					if(lineStrings[0].split("/")[1].equals("earth.png")
							||lineStrings[0].split("/")[1].equals("spacestation.png")){
						speed=0;
					}
					else{
						speed=1;
					}
					int x = Integer.decode(lineStrings[1]);
					int y = Integer.decode(lineStrings[2]);
					String imgPath = lineStrings[0];
					String type = lineStrings[0].split("/")[1].replace(".png","");
					int health = Integer.decode(lineStrings[6]);
					GameObject tempObj = null;
					tempObj = GameObject.createObject(type, world, x, y, t);
					/*if(type.equals("ship.png")){
						tempObj = new StandardShip(world,x,y,imgPath,speed,t,type);
						tempObj.setHealth(health);
					}

					else if(type.equals("spacestation.png")){
						tempObj =  new Spacestation(world,x,y,imgPath,speed,t,type);
						tempObj.setHealth(health);
						//tempObj.setDamage(5);
					}
					else if(type.equals("earth.png")){
						tempObj = new Earth(world,x,y,imgPath,speed,t,type);
					}*/
					if(tempObj!=null){
						world.getGameObjects().add(tempObj);
					}
				}
				g.flush();
			} catch (SlickException e) {
				System.out.println("loadThis error");
				e.printStackTrace();
			}
		}
		catch(IOException e){}

		game.blueTeam.resetGold();
		game.redTeam.resetGold();
		game.grayTeam.resetGold();

		game.resetVictoryCondition();


		game.setSelectedObjects(null);
		game.setSelectMode(false);
		game.setDoubleClick(false);

		//Generate new ids for objects when loading
		if(game.isServer()){
			int i = 0;

			for(GameObject go : game.getGameworld().getGameObjects()){
				go.setid(i);
				i++;
			}
		}
	}
	public Tile[][] getTiles(){
		return list;
	}
	public void setTile(int x, int y, String name){
		try {
			Graphics g = cachedImage.getGraphics();
			if(!list[(int) (x/20)][(int) (y/20)].getTileName().equals(Game.IMAGE_PATH+name))
			{
				g.drawImage(sprite.get(Game.IMAGE_PATH+name), ((int)(x/20))*20, ((int)(y/20))*20);
				list[(int) (x/20)][(int) (y/20)].setTileName(Game.IMAGE_PATH+name);
			}
			g.flush();
		} 
		catch (SlickException e) {
			System.out.println("setTile error");
			e.printStackTrace();
		}

	}
	public void drawMap(Game game, World world, View worldView){
		this.game= 		game;
		this.world= 	world;
		this.worldView=	worldView;
		try {
			cachedImage = new Image(world.getWidth() + 20 * 2, world.getHeight()-150 + 20 * 2);
			Graphics g = cachedImage.getGraphics();

			int numTilesX = world.getWidth() / 20+10;
			int numTilesY = world.getHeight() / 20+10;
			list= new Tile[numTilesX][numTilesY];


			for(int yPos = 0; yPos < numTilesY; yPos++) {
				for(int xPos = 0; xPos < numTilesX; xPos++) {
					int rand = (int) (Math.random()*3)+1;
					list[xPos][yPos]=new Tile(Game.IMAGE_PATH + "groundTile"+rand+".png",xPos,yPos);
					g.drawImage(sprite.get(Game.IMAGE_PATH + "groundTile"+rand+".png"), xPos * 20, yPos * 20);
				}
			}

			g.flush();
		} catch(SlickException ex) {
			System.out.println("Could not create image for drawing tiles.");
			ex.printStackTrace();
		}
	}
	private class FFilter extends FileFilter {

		//Accept all directories and all gif, jpg, tiff, or png files.
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension != null) {
				if (extension.equals("map")) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}
		public String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i+1);
			}
			return ext;
		}

		//The description of this filter
		public String getDescription() {
			return ".map";
		}
	}
	public void loadMap(){
		//JFrame test = new JFrame();
		//test.setVisible(true);
		//test.setUndecorated(true);
		BufferedWriter bw = null;
		File file = null;
		FFilter filter = new FFilter();
		JFileChooser fc = new JFileChooser("maps");
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(filter);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			TileMap t = new TileMap(game,file.getPath());
			game.changeGameWorld(t.getWorld());
			//loadThis(file.getPath());
		}
		fc = null;
		//test.setVisible(false);
	}
	private class SaveGame extends Thread{
		Tile[][] tileList;
		public SaveGame(Tile[][] tempList){
			tileList=tempList;
		}
		JFrame test = new JFrame();
		public void run(){
			SaveThisGame();
		}
		public void SaveThisGame(){
			test.setVisible(true);
			test.setAlwaysOnTop(true);
			//test.setUndecorated(true);
			BufferedWriter bw = null;
			File file = null;
			FFilter filter = new FFilter();
			JFileChooser fc = new JFileChooser("maps");
			fc.setAcceptAllFileFilterUsed(false);
			fc.addChoosableFileFilter(filter);
			int returnVal = fc.showSaveDialog(test);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				if((filter.getExtension(file)==(null))){
					file = new File(file.getPath()+".map");
				}
				else{
					file = new File(file.getPath());
				}
				try {
					bw = new BufferedWriter(new FileWriter(file));
					//					write tiles
					for(Tile[] tempList : tileList){
						for(Tile  tempTile: tempList){
							if(tempTile!=null)
								bw.write(tempTile.getTileName()+" ");
						}
						bw.write("\n");
					}
					//					write objects
					bw.write("///\n");
					for(GameObject obj:world.getGameObjects()){
						bw.write(obj.getSprite().getResourceReference()
								+"\t"+(int)obj.getxPos()
								+"\t"+(int)obj.getyPos()
								+"\t"+obj.getTeam().getColor().getRed()
								+"\t"+obj.getTeam().getColor().getGreen()
								+"\t"+obj.getTeam().getColor().getBlue()
								+"\t"+obj.getHealth()
								+"\n");
					}
					bw.close();
					System.out.println("Saved map as "+file.getName());
				} catch (IOException e) {
					System.out.print("failed to save map");
					e.printStackTrace();
				}
			}
			test.setVisible(false);
			isSaving=false;
		}
	}
	public void saveMap(){
		if(!isSaving){
			isSaving=true;
			Thread save = new SaveGame(list.clone());
			save.start();
		}
	}
	public void draw(Graphics g) {
		//Point2D.Float viewLocation = worldView.getCurrentViewLocation();
		/*float tileOffsetX = -viewLocation.x % sprite[0].getWidth();
		float startX = -viewLocation.x - tileOffsetX - sprite[0].getWidth();
		float tileOffsetY = -viewLocation.y % sprite[0].getHeight();
		float startY = -viewLocation.y - tileOffsetY - sprite[0].getHeight();*/
		g.drawImage(cachedImage, 0, 0);
	}
}
