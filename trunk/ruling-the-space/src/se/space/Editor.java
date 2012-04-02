package se.space;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

/**
 * Displays the game's GUI and handles mouse clicks on it.
 */
public class Editor implements Serializable{
	private World world;
	private Dimension screenSize;
	private String selectedType;
	private String type;
	public 	Rectangle 	Area;
	private Rectangle[] teamColor;
	private HashMap<Image,Rectangle> buttons;
	private Team selectedTeam;
	private Game game;

	public Editor(World world, Dimension screenSize,Game tempGame) {
		teamColor = new Rectangle[3];
		game=tempGame;
		int xPos=screenSize.width-150+15;
		int yPos=30;
		for(int i =0;i<teamColor.length;i++){
			teamColor[i]=new Rectangle(xPos-5, yPos-5, 30, 30);
			xPos+=50;
		}
		
		
		buttons = new HashMap<Image,Rectangle>();
		xPos=screenSize.width-150+25;
		int count=0;
		yPos=100;
		for(Image img: TileMap.sprite.values()){
			count++;
			if(yPos!=100+((int)count/4)*50){
				yPos=100+((int)count/4)*50;
				xPos-=120;
			}
			buttons.put(img,new Rectangle(xPos-5, yPos-5, 30, 30));
			xPos+=40;
		}
		
		
		xPos=screenSize.width-150+25;
		yPos=300;
		for(GameObject go: Game.objectList.values()){
			Image img = go.getSprite();
			buttons.put(img,new Rectangle(xPos-5, yPos-5, img.getWidth()+10, img.getHeight()+10));
			yPos+=img.getHeight()+20;
		}
		this.world = world;
		this.screenSize = screenSize;
		int s = screenSize.height;
		if(s>1000){
			s-=150;
		}
		Area = new Rectangle(screenSize.width-150, 0, 150, s);
		setType("tile");
		setSelectedTeam(game.redTeam);
		selectedType="groundTile1.png";
	}
	public void setSelectedType(String s){
		selectedType=s;
	}
	public String getSelectedType(){
		return selectedType;
	}
	public void draw(Graphics g) {
		drawMainGui(g);
	}
	public void mouseClick(int x, int y){
		System.out.println("x: "+x+", y: "+y);
		for(Image img: TileMap.sprite.values()){
			if(buttons.get(img).contains(x, y)){
				String tempSelectedType[]=img.getResourceReference().split("/");
				selectedType=tempSelectedType[1];
				setType("tile");
			}
		}
		for(GameObject go: Game.objectList.values()){
			Image img = go.getSprite();
			if(buttons.get(img).contains(x, y)){
				String tempSelectedType[]=img.getResourceReference().split("/");
				selectedType=tempSelectedType[1];
				setType("object");
			}
		}
		for(Rectangle rct:teamColor){
			if(rct.contains(x, y)){
				if(rct==teamColor[1]){
					setSelectedTeam(game.blueTeam);
				}
				if(rct==teamColor[0]){
					setSelectedTeam(game.redTeam);
				}
				if(rct==teamColor[2]){
					setSelectedTeam(game.grayTeam);
				}
			}
		}
	}
	/**
	 * Draw the Editor GUI
	 */
	private void drawMainGui(Graphics g) {
		//Draw background rectangle
		g.setColor(Color.darkGray);
		g.fillRect(screenSize.width-150, 0, 150, screenSize.height-150);
		int xPos=screenSize.width-150+15;
		int yPos=100;
		int count=0;
		for(Image img: TileMap.sprite.values()){
			count++;
			if(yPos!=100+((int)count/4)*50){
				yPos=100+((int)count/4)*50;
				xPos-=120;
			}
			if(img.getResourceReference().equals(Game.IMAGE_PATH+selectedType)){
				g.setColor(Color.green);
			}
			else{
				g.setColor(Color.white);
			}
			g.fillRect(xPos-5, yPos-5, 30, 30);
			g.drawImage(img, xPos , yPos);
			xPos+=40;
		}
		xPos=screenSize.width-150+15;
		count=0;
		yPos=300;
		for(GameObject go: Game.objectList.values()){
			Image img = go.getSprite();
			if(img.getResourceReference().equals(Game.IMAGE_PATH+selectedType)){
				g.setColor(Color.green);
			}
			else{
				g.setColor(Color.white);
			}
			g.fillRect(xPos-5, yPos-5, img.getWidth()+10, img.getHeight()+10);
			g.drawImage(img, xPos , yPos);
			yPos+=img.getHeight()+20;
		}
		xPos=screenSize.width-150+15;
		for(Rectangle rct:teamColor){
			if(selectedTeam==game.blueTeam&&rct==teamColor[1]){
				g.setColor(Color.green);
			}
			else if(selectedTeam==game.redTeam&&rct==teamColor[0]){
				g.setColor(Color.green);
			}
			else if(selectedTeam==game.grayTeam&rct==teamColor[2]){
				g.setColor(Color.green);
			}
			else{
				g.setColor(Color.white);
			}
			g.fillRect(xPos-10, 30-10, 40, 40);
			xPos+=50;
			if(rct==teamColor[1]){
				g.setColor(game.blueTeam.getColor());
			}
			else if(rct==teamColor[0]){
				g.setColor(game.redTeam.getColor());
			}
			else{
				g.setColor(game.grayTeam.getColor());
			}
			g.fill(rct);
		}

	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setSelectedTeam(Team selectedTeam) {
		this.selectedTeam = selectedTeam;
	}
	public Team getSelectedTeam() {
		return selectedTeam;
	}
}
