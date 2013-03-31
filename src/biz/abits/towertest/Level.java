package biz.abits.towertest;

import org.andengine.extension.tmx.TMXTiledMap;

public class Level {
	private TMXTiledMap mTMXTiledMap;
	public String map = "desert.tmx";
	/** Locations where Enemies should enter the map */
	public Point[] startLoc;
	/** Locations where the Enemies should want to leave */
	public Point[] endLoc;
	Enemy enemy;
	public int[] wave;

	/**
	 * Constructor intializes variables
	 * 
	 * @param waves
	 */
	public Level(int[] waves, Point[] lStarts, Point[] lEnds) {
		startLoc = lStarts;
		endLoc = lEnds;
		wave = waves;
		// enemy wasn't even defined!!!
		// defaultPath = new Path(enemy);

		// defaultPath.add(new Waypoint(200,0));
		// defaultPath.add(new Waypoint(200,500));
		// defaultPath.add(new Waypoint(500,0));
		// defaultPath.add(new Waypoint(500,500));
	}

	/**
	 * Load a TMX map from file name
	 * 
	 * @param sMap File name
	 */
	public static void loadMap(String sMap) {

	}

	/**
	 * Load the level from a file
	 * 
	 * @return true is successful
	 */
	public boolean loadLevel() {
		// read file for: tmx file name, default waypoints, enemy list or modifiers
		return false;
	}
}