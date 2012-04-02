package se.space;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import se.space.spaceships.Destroyer;
import se.space.spaceships.StandardShip;

/**
 * Displays the game's GUI
 */
public class Gui implements Serializable{
	private World 		world;
	private String 		statusText = "";
	private Timer 		statusTextTimer;
	private Dimension 	screenSize;
	public 	Rectangle 	Area;
	public 	HashMap<Rectangle,GameObject> rctGame;
	public 	HashMap<GameObject,Rectangle> gameRct;
	private HashMap<String,Rectangle> buttons;
	private Timer t;
	private Timer update;
	private Timer updateGold;
	private int gold;
	private Image frame;
	private Image squareFrame;
	int level;
	private String health;
	int damage;
	GameObject currentObject;


	public Gui(World world, Dimension screenSize) {
		try {
			world.getGame();
			frame = new Image(Game.IMAGE_PATH+"horizontal.png");
			squareFrame = new Image(Game.IMAGE_PATH+"squareFrame.png");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t=Timer.createTimer(1000);
		this.world = world;
		this.screenSize = screenSize;
		statusTextTimer = Timer.createTimer(3000);
		this.Area=new Rectangle(0, screenSize.height - 150, screenSize.width, 150);
		buttons = new HashMap<String,Rectangle>();
		buttons.put("ship", new Rectangle((float) (this.screenSize.getWidth()-280), this.screenSize.height-160, 50, 50));
		buttons.put("destroyer", new Rectangle((float) (this.screenSize.getWidth()-200), this.screenSize.height-160, 50, 50));
		update = Timer.createTimer(500);
		updateGold = Timer.createTimer(5000);
		

	}

	public void draw(Graphics g) {
		drawMainGui(g);
		if(updateGold.isDone()){
			gold=(int)world.getGame().getMyTeam().getGold();
			updateGold.reset();
		}
		if(update.isDone()){
			update.reset();
			if(currentObject!=null){
				level = currentObject.getLevel();
				damage = currentObject.getDamage();
				health = ""+currentObject.getHealth()+"/"+currentObject.getMaxHealth();
			}
			
		}
			g.setColor(Color.darkGray);
			g.fillRect((float) (screenSize.getWidth()-200), 0, 200, 30);
			g.setColor(Color.yellow);
			g.drawString("Gold: "+gold,(float) (screenSize.getWidth()-180),10);
	}

	/**
	 * Draw the GUI at the bottom of the screen.
	 */
	private void drawMainGui(Graphics g) {
		//Draw background rectangle
		g.setColor(Color.lightGray);
		g.fillRect(0, screenSize.height - 150, screenSize.width, 150);
		for(int x=0;x<screenSize.width;x+=20){
			g.drawImage(frame, x, screenSize.height-170);
			g.drawImage(frame, x, -10);
		}
		//Draw selected units info
		g.setColor(Color.white);
		//int count = 0;
		g.setColor(Color.darkGray);
		g.fillRect((float) (this.screenSize.getWidth()-300), this.screenSize.height-180, 300, 180);
		g.drawImage(squareFrame, (float) (this.screenSize.getWidth()-320), this.screenSize.height-200);
		if(world.getGame().getSelectedObjects()!=null){
			int xPos=300,yPos=screenSize.height - 140;
			rctGame=new HashMap<Rectangle,GameObject>();
			gameRct=new HashMap<GameObject,Rectangle>();
			for(GameObject obj:world.getGame().getSelectedObjects()){
				if(xPos+obj.getSprite().getWidth()>screenSize.getWidth()-300){
					xPos=300;
					yPos+=50;
				}
				if(yPos>screenSize.height-obj.getSprite().getHeight()){
					break;
				}
				Rectangle tempRct=new Rectangle(xPos, yPos, obj.getSprite().getWidth(), obj.getSprite().getHeight());

				rctGame.put(tempRct, obj);
				gameRct.put(obj, tempRct);
				if(obj==this.currentObject){
					g.setColor(Color.green);
				}
				else{
					g.setColor(Color.blue);
				}
				g.draw(tempRct);
				Image img =obj.getSprite().copy();
				img.setRotation(0);
				g.drawImage(img, xPos, yPos);
				xPos+=10+obj.getSprite().getWidth();
			}
			if(world.getGame().getSelectedObjects().contains(this.currentObject)){
				if(currentObject.isBuilding()){
					currentObject.drawBuildInterface(g,buttons);
				}
				else if(currentObject.isShip()){
					currentObject.drawShipInterface(g,buttons,(float)this.screenSize.getWidth(),(float)this.screenSize.getHeight());
				}
			}
			else{
				for(GameObject obj:world.getGame().getSelectedObjects()){
					currentObject=obj;
					break;
				}
			}
		}
	}
	public void mouseClick(int x, int y){
		try{
			if(world.getGame().getSelectedObjects()!=null){
				for(GameObject obj:world.getGame().getSelectedObjects()){
					if(gameRct.get(obj).contains(x, y)){
						this.currentObject=obj;
						break;
					}
				}
			}
			for(String s:buttons.keySet()){
				Rectangle rct = buttons.get(s);
				if(rct.contains(x, y)){
					if(t.isDone()){
						if(this.currentObject.getType().equals("spacestation.png")){
							GameObject tempObj;
							if(s.equals("destroyer")){
								tempObj = new Destroyer(this.world,currentObject.getX(), currentObject.getY(),
										Game.IMAGE_PATH +s+".png",// "ship.png"),
										1,currentObject.getTeam(),s);
							}
							else{
								tempObj = new StandardShip(this.world,currentObject.getX(), currentObject.getY(),
										Game.IMAGE_PATH +s+".png",// "ship.png"),
										1,currentObject.getTeam(),s);
							}
							tempObj.move((int)currentObject.getMoveX(),(int)currentObject.getMoveY());
							currentObject.build(tempObj, 10000);
							t.reset();
						}
					}
				}
			}
		}
		catch(NullPointerException e){
			System.err.println("Null pointer at Gui.mouseClick");
			e.printStackTrace();
		}
	}


	/**
	 * Sets the status text shown at the bottom of the screen.
	 */
	public void setStatusText(String statusText) {
		this.statusText = statusText;
		statusTextTimer.reset();
	}
}
