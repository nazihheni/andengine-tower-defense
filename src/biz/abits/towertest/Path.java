package biz.abits.towertest;

import java.util.ArrayList;
import java.util.Iterator;

import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXProperty;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.util.algorithm.path.ICostFunction;
import org.andengine.util.algorithm.path.IPathFinderMap;
import org.andengine.util.algorithm.path.astar.AStarPathFinder;
import org.andengine.util.algorithm.path.astar.IAStarHeuristic;
import org.andengine.util.algorithm.path.astar.NullHeuristic;

/**
 * Path is a list of waypoints used to travel The ArrayList waypoints is public and can be access directly
 * 
 * @author abinning
 * 
 */
public class Path {
	Enemy enemy;
	TMXLayer tmxlayer;
	public ArrayList<Waypoint> waypoints;
	Iterator<Waypoint> iterator;
	public org.andengine.util.algorithm.path.Path A_Path;
	private AStarPathFinder<Enemy> finder;
	private Point end;

	/**
	 * initialized the list
	 */
	public Path(Enemy en, Point pEnd, TMXLayer pTmxlayer) {
		enemy = en;
		waypoints = new ArrayList<Waypoint>();
		iterator = waypoints.iterator();
		end = pEnd;
		tmxlayer = pTmxlayer;
		findPath();
	}

	/**
	 * Add new Waypoint
	 * 
	 * @param wp
	 */
	public void add(Waypoint wp) {
		waypoints.add(wp);
	}

	/**
	 * Remove Waypoint
	 * 
	 * @param wp
	 */
	public void remove(Waypoint wp) {
		waypoints.remove(wp);
	}

	/**
	 * Add new Waypoint at index
	 * 
	 * @param wp
	 */
	public void add(int index, Waypoint wp) {
		waypoints.add(index, wp);
	}

	/**
	 * Remove Waypoint at index
	 * 
	 * @param wp
	 */
	public void remove(int index) {
		waypoints.remove(index);
	}

	/**
	 * get iterator
	 * 
	 * @param wp
	 */
	public Iterator<Waypoint> iterator() {
		return iterator;
	}

	/**
	 * Get next waypoint using iterator
	 * 
	 * @return
	 */
	public Waypoint next() {
		return iterator.next();
	}

	/**
	 * check to see if there is a next waypoint using iterator
	 * 
	 * @return
	 */
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * Get waypoint by index
	 * 
	 * @param index
	 * @return
	 */
	public Waypoint get(int index) {
		return waypoints.get(index);
	}

	public Waypoint getLast() {
		return waypoints.get(waypoints.size() - 1);
	}

	IPathFinderMap<Enemy> PathMap = new IPathFinderMap<Enemy>() {
		@Override
		public boolean isBlocked(int pX, int pY, Enemy pEntity) {
			final TMXTile tmxTile = TowerTest.tmxLayer.getTMXTileAt(pX, pY);
			final TMXProperties<TMXTileProperty> tmxTileProperties = TowerTest.mTMXTiledMap
					.getTMXTileProperties(tmxTile.getGlobalTileID());
			if (tmxTileProperties.containsTMXProperty("Collidable", "False")) {
				// set the circle to red
				return true;
			} else {
				// set the circle to green
				return false;
			}
		}
	};

	ICostFunction<Enemy> CostCallback = new ICostFunction<Enemy>() {
		@Override
		public float getCost(IPathFinderMap<Enemy> pPathFinderMap, int pFromX, int pFromY, int pToX, int pToY,
				Enemy pEntity) {
			// Read the cost attribute from the tile at the given position
			// TODO enable this shizz and add cost to tilemap! (KYLE)
			// TMXProperty cost = pEntity.getTMXTile(pFromX,
			// pFromY).getTMXTileProperties(mMap.getMap()).get(0);
			// return Float.parseFloat(cost.getValue());
			return 1f;
		}
	};

	IAStarHeuristic<Enemy> Heuristic = new NullHeuristic<Enemy>();

	private void findPath() {
		int pColMin = 0;
		int pRowMin = 0;
		int pColMax = TowerTest.CAMERA_WIDTH;
		int pRowMax = TowerTest.CAMERA_HEIGHT;
		boolean allowDiagonal = false;

		// TODO code a transformation function that gets int's and returns x,y
		// coords
		finder = new AStarPathFinder<Enemy>();
		A_Path = finder.findPath(PathMap, pColMin, pRowMin, pColMax, pRowMax, enemy, enemy.getCol(), enemy.getRow(),
				end.x, end.y, allowDiagonal, Heuristic, CostCallback);

		/*
		 * float dY = target.getMidY() - this.getMidY(); // some calc about how far the bullet can go, in this case up to the enemy float dX = target.getMidX() -
		 * this.getMidX();//+(Math.abs(gY)/Enemy.speed/Projectile.speed); float dist = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)); //D=r*t //therefore t = D/r
		 * trajectory = new MoveByModifier(dist/Projectile.speed, dX, dY); this.registerEntityModifier(trajectory);
		 */
	}
}