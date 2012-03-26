package se.space;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import org.newdawn.slick.geom.Rectangle;

public class View {
	public enum ScrollX {NONE, LEFT, RIGHT};
	public enum ScrollY {NONE, UP, DOWN};
	private ScrollX xScrollDirection;
	private ScrollY yScrollDirection;
	private Point2D.Float currentViewLocation; //The top left corner of the view
	private World world;
	private Dimension viewSize;
	private float scrollSpeed = 5;
	private final int RIGHT_EDGE;
	private final int LEFT_EDGE = 0;
	private final int BOTTOM_EDGE;
	private final int TOP_EDGE = 0;
	
	public View(World world, Dimension viewSize) {
		this.world = world;
		this.viewSize = viewSize;
		currentViewLocation = new Point2D.Float(0, 0);
		RIGHT_EDGE = world.getWidth() - viewSize.width;
		BOTTOM_EDGE = world.getHeight() - viewSize.height;

	}

	public void setXScrollDirection(ScrollX xScrollDirection) {
		this.xScrollDirection = xScrollDirection;
	}

	public void setYScrollDirection(ScrollY yScrollDirection) {
		this.yScrollDirection = yScrollDirection;
	}

	public Point2D.Float getCurrentViewLocation() {
		return new Point2D.Float(currentViewLocation.x, currentViewLocation.y);
	}

	/**
	 * Moves the view to a new location, making sure the view isn't outside the map.
	 * If you try to set an invalid view location, it finds the closest valid location.
	 */
	public void setCurrentViewLocation(Point2D.Float currentViewLocation) {
		if(currentViewLocation.x + RIGHT_EDGE <= 0) { //TODO rewrite in a more understandable way
			currentViewLocation.x = -RIGHT_EDGE;
		} else if(currentViewLocation.x - LEFT_EDGE >= 0) {
			currentViewLocation.x = LEFT_EDGE;
		}

		if(currentViewLocation.y + BOTTOM_EDGE <= 0) {
			currentViewLocation.y = -BOTTOM_EDGE;
		} else if(currentViewLocation.y - TOP_EDGE >= 0) {
			currentViewLocation.y = TOP_EDGE;
		}
		
		this.currentViewLocation = new Point2D.Float(currentViewLocation.x, currentViewLocation.y);
	}

	public Rectangle getViewLocationRect() {
		return new Rectangle(currentViewLocation.x, currentViewLocation.y, viewSize.width, viewSize.height);
	}

	/**
	 * Scrolls the view in the set x and y directions making sure it doesn't scroll too far.
	 */
	public void update() {
		final float RIGHT_EDGE_DIST = currentViewLocation.x + RIGHT_EDGE;
		final float LEFT_EDGE_DIST = currentViewLocation.x + LEFT_EDGE;
		final float BOTTOM_EDGE_DIST = currentViewLocation.y + BOTTOM_EDGE;
		final float TOP_EDGE_DIST = currentViewLocation.y + TOP_EDGE;

		//If closer than one step to the left or right map edge then move directly to the map edge
		if(Math.abs(RIGHT_EDGE_DIST) < scrollSpeed) {
			currentViewLocation.x = -RIGHT_EDGE;
		} else if(Math.abs(LEFT_EDGE_DIST) < scrollSpeed) {
			currentViewLocation.x = LEFT_EDGE;
		}

		//If closer than one step to the top or bottom map edge then move directly to the map edge
		if(Math.abs(BOTTOM_EDGE_DIST) < scrollSpeed) {
			currentViewLocation.y = -BOTTOM_EDGE;
		} else if(Math.abs(TOP_EDGE_DIST) < scrollSpeed) {
			currentViewLocation.y = TOP_EDGE;
		}

		//Scroll left and right as long as we haven't scrolled too far
		if(xScrollDirection == ScrollX.RIGHT && RIGHT_EDGE_DIST - scrollSpeed >= 0) {
			currentViewLocation.x -= scrollSpeed;
		} else if(xScrollDirection == ScrollX.LEFT && LEFT_EDGE_DIST + scrollSpeed <= 0) {
			currentViewLocation.x += scrollSpeed;
		}

		//Scroll up and down as long as we haven't scrolled too far
		if(yScrollDirection == ScrollY.DOWN && BOTTOM_EDGE_DIST - scrollSpeed >= 0) {
			currentViewLocation.y -= scrollSpeed;
		} else if(yScrollDirection == ScrollY.UP && TOP_EDGE_DIST + scrollSpeed <= 0) {
			currentViewLocation.y += scrollSpeed;
		}
	}

	/**
	 * @return The size of the game window/panel, in pixels.
	 */
	public Dimension getScreenSize() {
		return new Dimension(viewSize);
	}
}
