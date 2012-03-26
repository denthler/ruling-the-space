package se.ruling.space;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Minimap {
	private Game game;
	private World world;
	private View worldView;
	private final int minimapWidth = 200; 
	private final int minimapHeight = 200;
	private final int minimapX;
	private final int minimapY;
	private final float scaleX;
	private final float scaleY;
	public 	Rectangle 	Area;
	private Image minimapImg;


	public Minimap(Game game, World world, View worldView) {
		try {
			minimapImg = new Image(Game.IMAGE_PATH + "minimap.png");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.game = game;
		this.world = world;
		this.worldView = worldView;
		minimapX = 25;
		minimapY = worldView.getScreenSize().height - minimapHeight-25;
		scaleX = world.getWidth() / (float)minimapWidth;
		scaleY = world.getHeight() / (float)minimapHeight;
		Area = new Rectangle(minimapX, minimapY, minimapWidth, minimapHeight);
	}

	public int getMinimapX() {
		return minimapX;
	}

	public int getMinimapY() {
		return minimapY;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public int getMinimapHeight() {
		return minimapHeight;
	}

	public int getMinimapWidth() {
		return minimapWidth;
	}

	public Rectangle getMinimapBoundary() {
		return new Rectangle(minimapX, minimapY, minimapWidth, minimapHeight);
	}

	/**
	 * Draws the minimap in the top right corner of the screen.
	 * The minimap includes a rectangle showing what part of the map is viewed at the moment.
	 * Also draws all units and buildings in their team's color.
	 */
	public void draw(Graphics g) {
		//Draw background
		g.setColor(Color.black);
		g.fillRect(0, worldView.getScreenSize().height-this.minimapHeight-50, this.minimapWidth+50, this.minimapHeight+50);
		g.setColor(new Color(10, 10, 10));
		g.fillRect(minimapX, minimapY, minimapWidth, minimapHeight);
		for(GameObject obj:world.getGameObjects()){
			g.setColor(obj.getTeam().getColor());
			g.fillRect(obj.getX()/this.getScaleX()+this.getMinimapX()-1, obj.getY()/this.getScaleY()+this.getMinimapY()-1, 3, 3);
		}

		//Draw resources such as trees
		//for(GameObject object : world.getAllUnits()) {
		//	if(object instanceof Tree) {
		//		g.setColor(Color.green);
		//		g.fillRect(minimapX + object.getX() / scaleX, minimapY + object.getY() / scaleY, 3, 3);
		//	}
		//}

		//Draw all units and buildings in their team's color
		//for(ControllableObject object : world.getControllableObjects()) {
		//	if(object.isSelected()) {
		//		g.setColor(Color.white);
		//	} else {
		//		g.setColor(object.getTeam().getTeamColor());
		//	}

		//	g.fillRect(minimapX + object.getX() / scaleX, minimapY + object.getY() / scaleY, 3, 3);
		//}

		//Draw the view rectangle
		g.drawImage(minimapImg, 0, minimapY-25);

		g.setColor(Color.white);
		Rectangle viewRect = worldView.getViewLocationRect();
		g.drawRect(minimapX - viewRect.getX() / scaleX, minimapY - viewRect.getY() / scaleY, (int)(viewRect.getWidth() / scaleX), (int)(viewRect.getHeight() / scaleY));
		
		g.setColor(Color.darkGray);
		g.drawRect(minimapX-1, minimapY-1, minimapWidth+2, minimapHeight+2);
	}
}
