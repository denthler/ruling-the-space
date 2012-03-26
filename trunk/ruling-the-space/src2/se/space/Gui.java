package se.ruling.space;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

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
	private int gold;
	private Image frame;
	private Image squareFrame;
	int level;
	private String health;
	int dammage;
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
		buttons.put("build", new Rectangle((float) (this.screenSize.getWidth()-280), this.screenSize.height-160, 50, 50));
		update = Timer.createTimer(5000);
		

	}

	public void draw(Graphics g) {
		drawMainGui(g);
		if(update.isDone()){
			gold=(int)world.getGame().getMyTeam().getGold();
			update.reset();
			if(currentObject!=null){
				level = currentObject.getLevel();
				dammage = currentObject.getDammage();
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
		int count = 0;
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
				if(currentObject.getType().equals("spacestation.png")){
					drawBuildInterface(g);
				}
				else if(currentObject.getType().equals("ship.png")){
					drawShipInterface(g);
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
					}
				}
			}
			for(Rectangle rct:buttons.values()){
				if(rct.contains(x, y)){
					if(t.isDone()){
						if(this.currentObject.getType().equals("spacestation.png")){
							GameObject tempObj = new GameObject(this.world,currentObject.getX(), currentObject.getY(),
									new Image(Game.IMAGE_PATH + "ship.png"),
									1,currentObject.getTeam(),"ship.png");
							tempObj.move((int)currentObject.getMoveX(),(int)currentObject.getMoveY());
							currentObject.build(tempObj, 10000);
							t.reset();
						}
						else if(this.currentObject.getType().equals("ship.png")){
							currentObject.build(currentObject, 5000);
						}
					}
				}
			}
		}
		catch(NullPointerException e){
			System.err.println("Null pointer at Gui.mouseClick");
			e.printStackTrace();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void drawShipInterface(Graphics g) {
		// TODO Auto-generated method stub
		if(this.currentObject.getExp()<100*this.currentObject.getLevel()){
			g.setColor(Color.red);
		}
		else
			g.setColor(Color.green);
		for(Rectangle rct:buttons.values()){
			g.fill(rct);
			g.drawImage(Game.objectList.get(Game.IMAGE_PATH +"ship.png"), rct.getX()+5, rct.getY()+5);
			g.setColor(Color.yellow);
			g.drawString("upgrade", rct.getX()-2, rct.getMaxY()+5);
			
			g.drawString("Level: "+level, rct.getMaxX()+50, rct.getY());
			g.drawString("Dammage: "+dammage, rct.getMaxX()+50, rct.getY()+20);
			g.drawString("Health: "+health, rct.getMaxX()+50, rct.getY()+40);
			
		}

	}

	private void drawBuildInterface(Graphics g) {
		// TODO Auto-generated method stub
		if(world.getGame().getMyTeam().getGold()<200){
			g.setColor(Color.red);
		}
		else
			g.setColor(Color.green);
		for(Rectangle rct:buttons.values()){
			g.fill(rct);
			g.drawImage(Game.objectList.get(Game.IMAGE_PATH +"ship.png"), rct.getX()+5, rct.getY()+5);
			g.setColor(Color.yellow);
			g.drawString("200", rct.getX()+10, rct.getMaxY()+5);
			
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
