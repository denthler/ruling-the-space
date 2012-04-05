package se.space.spaceships;

import java.util.HashMap;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

import se.space.GameObject;
import se.space.Team;
import se.space.World;

public class StandardShip extends se.space.GameObject {
	private int defDamage = 1;
	private int defHealth = 200;
	private int defPrice = 200;
	private double defSpeed = 1;
	public StandardShip(World tempWorld, int x, int y, String imgPath,String imgIconPath,
			int tempSpeed, Team tempTeam, String tempType) {
		super(tempWorld, x, y, imgPath,imgIconPath, tempSpeed, tempTeam, tempType);
		setDefaultValues();
	}
	public StandardShip(int x, int y, String imgPath,String imgIconPath){
		super(x,y,imgPath,imgIconPath);
		setDefaultValues();
	}
	protected void setDefaultValues(){
		super.setDamage(defDamage);
		super.setHealth(defHealth);
		super.setPrice(defPrice);
		super.setSpeed(defSpeed);
		super.setShip(true);
		setType("ship");
	}
	public void draw(Graphics g){
		super.draw(g);
		//g.fillRect((float)xPos-sprite.getWidth()/2, (float)yPos+sprite.getHeight()/2-level, 5, -level);
		g.fillRect(getX(), getY()+4, (sprite.getWidth()-(sprite.getWidth()*(100*level-exp)/(100*level))), 4);
	}
	
	public void drawInterface(Graphics g,float screenWidth,float screenHeight){
		System.out.println("STANDARD: HEJSAN!");
	// TODO Auto-generated method stub
//		if(this.currentObject.getExp()<100*this.currentObject.getLevel()){
//			g.setColor(Color.red);
//		}
//		else
//			g.setColor(Color.green);
	//for(Rectangle rct:buttons.values()){
		//g.fill(rct);
	
		Image s = this.getSprite();
		Float tmp = s.getRotation();
		s.setRotation(0);
		Rectangle rct = new Rectangle((float) (screenWidth-280), screenHeight-160, 50, 50);
		g.drawImage(this.getSprite(), rct.getX()+5, rct.getY()+5);
		s.setRotation(tmp);
		//g.drawImage(Game.objectList.get(Game.IMAGE_PATH +"ship.png"), rct.getX()+5, rct.getY()+5);
		g.setColor(Color.yellow);
//			g.drawString("upgrade", rct.getX()-2, rct.getMaxY()+5);
		
		g.drawString("Level: "+level, rct.getMaxX()+50, rct.getY());
		g.drawString("Damage: "+damage, rct.getMaxX()+50, rct.getY()+20);
		g.drawString("Health: "+health, rct.getMaxX()+50, rct.getY()+40);
	}
}
