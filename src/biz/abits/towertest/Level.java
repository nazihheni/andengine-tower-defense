package biz.abits.towertest;

import org.andengine.extension.tmx.TMXTiledMap;

public class Level{
	private TMXTiledMap mTMXTiledMap;
	public Path defaultPath;
	public String map = "desert.tmx";
	Enemy enemy;
	
	/**
	 * Constructor intializes variables
	 */
	public Level(){
		defaultPath = new Path(enemy);
		defaultPath.add(new Waypoint(200,0));
		defaultPath.add(new Waypoint(200,500));
		defaultPath.add(new Waypoint(500,0));
		defaultPath.add(new Waypoint(500,500));
	}
	
	/**
	 * Load a TMX map from file name
	 * @param sMap File name
	 */
	public static void loadMap(String sMap){
		
	}
	
	/**
	 * Load the level from a file
	 * @return true is successful
	 */
	public boolean loadLevel(){
		//read file for: tmx file name, default waypoints, enemy list or modifiers
		return false;
	}
}