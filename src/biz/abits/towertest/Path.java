package biz.abits.towertest;

import java.util.ArrayList;
import java.util.Iterator;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.util.algorithm.path.ICostFunction;
import org.andengine.util.algorithm.path.IPathFinderMap;
import org.andengine.util.algorithm.path.astar.AStarPathFinder;
import org.andengine.util.algorithm.path.astar.IAStarHeuristic;
import org.andengine.util.algorithm.path.astar.NullHeuristic;

import android.util.Log;

/**
 * Path is a list of waypoints used to travel The ArrayList waypoints is public and can be access directly
 * 
 * @author abinning
 * 
 */
public class Path implements Cloneable {
	Enemy enemy;
	TMXLayer tmxlayer;
	public ArrayList<Waypoint> waypoints;
	Iterator<Waypoint> iterator;
	public org.andengine.util.algorithm.path.Path A_Path;
	private Waypoint end;
	private Level level;
	private ArrayList<Point> xyPath;

	// public org.andengine.entity.modifier.PathModifier.Path xyPath;

	/**
	 * initialized the list and finds a path based on current level configuration
	 */
	public Path(Enemy en, Waypoint pEnd, TMXLayer pTmxlayer, Level plevel) {
		enemy = en;
		waypoints = new ArrayList<Waypoint>();
		iterator = waypoints.iterator();
		end = pEnd;
		tmxlayer = pTmxlayer;
		level = plevel;
		findPath();
		// now convert the col/row path, to xy coordinates, for later use
		// xyPath = new org.andengine.entity.modifier.PathModifier.Path(A_Path.getLength());
		xyPath = new ArrayList<Point>();
		for (int i = 0; i < A_Path.getLength(); i++)
			xyPath.add(new Point(TowerTest.getXFromCol(A_Path.getX(i)), TowerTest.getYFromRow(A_Path.getY(i))));
		trimPathToEnemy(); // I bet you can't guess what this one does!
		// xyPath = xyPath.to(TowerTest.getXFromCol(A_Path.getX(i)), TowerTest.getYFromRow(A_Path.getY(i)));
	}

	public Path(Enemy en, Waypoint pEnd, TMXLayer pTmxlayer, Level plevel, ArrayList<Point> pxyPath,
			org.andengine.util.algorithm.path.Path pA_Path) {
		enemy = en;
		waypoints = new ArrayList<Waypoint>();
		iterator = waypoints.iterator();
		end = pEnd;
		tmxlayer = pTmxlayer;
		level = plevel;
		xyPath = pxyPath;
		A_Path = pA_Path;
	}

	public org.andengine.entity.modifier.PathModifier.Path getEntityPath() {
		org.andengine.entity.modifier.PathModifier.Path tempPath = new org.andengine.entity.modifier.PathModifier.Path(
				xyPath.size() - 1);
		for (int i = 0; i < xyPath.size() - 1; i++)
			tempPath = tempPath.to(xyPath.get(i).x, xyPath.get(i).y);
		return tempPath;
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
			try {
				TMXTile tmxTile = TowerTest.tmxLayer.getTMXTileAt(TowerTest.getXFromCol(pX), TowerTest.getYFromRow(pY));
				TMXProperties<TMXTileProperty> tmxTileProperties = TowerTest.mTMXTiledMap.getTMXTileProperties(tmxTile
						.getGlobalTileID());
				if (tmxTileProperties.containsTMXProperty("Collidable", "False")) {
					// set the circle to red
					// it is blocked!
					return true;
				} else {
					// set the circle to green
					return false;
				}
			} catch (Exception e) { // this happens when it's drug off the map
				// its broken! (can't get the value)

				// TODO this next line crashes, I'm pretty sure it's because level.endLoc is undefined somehow????

				for (int i = 0; i < level.endLoc.length; i++) {
					if ((pX == level.endLoc[i].x) && (pY == level.endLoc[i].y)) {
						return false;
					}
				}
				for (int i = 0; i < level.startLoc.length; i++) {
					if ((pX == level.startLoc[0].x) && (pY == level.startLoc[0].y)) {
						return false;
					}
				}
				return true;
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

	public boolean checkRemainingPath(int pX, int pY) {
		final int len = A_Path.getLength();
		final int[] xs = new int[len]; // A_Path.getX(pY).mXs;
		final int[] ys = new int[len];
		for (int i = 0; i < len; i++) {
			xs[i] = A_Path.getX(i);
			ys[i] = A_Path.getY(i);
		}

		final int enCol = TowerTest.getColFromX(enemy.getX());
		final int enRow = TowerTest.getRowFromY(enemy.getY());
		for (int i = len - 1; i >= 0; i--) { // starts checking from the end
			if (xs[i] == pX && ys[i] == pY) {
				return true; // the point WAS on our remaining path
			} else if (xs[i] == enCol && ys[i] == enRow) {
				return false; // we found the enemy first, so no reason to check the rest
			}
		}
		return false; // we hit the end, so it's not on this path
	}

	private void findPath() {
		Log.w("Finder", "I just calculated a path for " + TowerTest.getColFromX(enemy.getX()) + ","
				+ TowerTest.getColFromX(enemy.getY()));
		// coords
		A_Path = TowerTest.finder.findPath(PathMap, TowerTest.pColMin, TowerTest.pRowMin, TowerTest.pColMax,
				TowerTest.pRowMax, enemy, enemy.getCol(), enemy.getRow(), end.x, end.y, TowerTest.allowDiagonal,
				Heuristic, CostCallback);

		/*
		 * float dY = target.getMidY() - this.getMidY(); // some calc about how far the bullet can go, in this case up to the enemy float dX = target.getMidX() -
		 * this.getMidX();//+(Math.abs(gY)/Enemy.speed/Projectile.speed); float dist = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)); //D=r*t //therefore t = D/r
		 * trajectory = new MoveByModifier(dist/Projectile.speed, dX, dY); this.registerEntityModifier(trajectory);
		 */
	}

	/**
	 * Trims the path to where the enemy is
	 * 
	 * @param pX x value in coordinates (NOT int column, int row)
	 * @param pY y value in coordinates (NOT int column, int row)
	 */
	public void trimPathToEnemy() {
		int trimNum = 0;
		for (int i = 0; i < xyPath.size() - 1; i++) { // flip through all the segments to check
			if (xyPath.get(i).x == xyPath.get(i + 1).x) { // Vertical segment
				if (enemy.getX() == xyPath.get(i).x)
					if (((xyPath.get(i).y <= enemy.getY()) && (enemy.getY() <= xyPath.get(i + 1).y))
							|| ((xyPath.get(i + 1).y <= enemy.getY()) && (enemy.getY() <= xyPath.get(i).y))) {
						Log.e("Jared", "I'm AM on the path");
						trimNum = i;
					}
			} else { // Horizontal segment //if(xyPath.getY(i) == xyPath.getY(i+1)){
				if (enemy.getY() == xyPath.get(i).y)
					if (((xyPath.get(i).x <= enemy.getX()) && (enemy.getX() <= xyPath.get(i + 1).x))
							|| ((xyPath.get(i + 1).x <= enemy.getX()) && (enemy.getX() <= xyPath.get(i).x))) {
						Log.e("Jared", "I'm AM on the path");
						trimNum = i;
					}
			}
		}
		// now remove everything before trimNum and tack on the enemy's current xy
		for (int i = trimNum; i >= 0; i--) {
			xyPath.remove(i);
		}
		xyPath.add(0, new Point(enemy.getX(), enemy.getY())); // add their current location to the beginning
	}

	@SuppressWarnings("unchecked")
	public Path clone(Enemy newEnemy) {
		org.andengine.util.algorithm.path.Path tempA_Path = new org.andengine.util.algorithm.path.Path(A_Path
				.getLength());
		for (int i = 0; i < A_Path.getLength(); i++)
			tempA_Path.set(i, A_Path.getX(i), A_Path.getY(i));
		return new Path(newEnemy, end, tmxlayer, level, (ArrayList<Point>) xyPath.clone(), A_Path);
	}
}