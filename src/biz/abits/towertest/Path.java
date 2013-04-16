package biz.abits.towertest;

import java.util.ArrayList;
import java.util.Iterator;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.util.algorithm.path.ICostFunction;
import org.andengine.util.algorithm.path.IPathFinderMap;
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
	public org.andengine.util.algorithm.path.Path rcPath;
	private Waypoint end;
	private Level level;
	private ArrayList<Point> xyPath;
	final static private double tolerance = 0.000001;

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
		if (findPath()) {
			// now convert the col/row path, to xy coordinates, for later use
			// xyPath = new org.andengine.entity.modifier.PathModifier.Path(A_Path.getLength());
			convertRCPathtoXY();
			optimizeXYPath();
		}
		// xyPath = xyPath.to(TowerTest.getXFromCol(A_Path.getX(i)), TowerTest.getYFromRow(A_Path.getY(i)));
	}

	public Path(Enemy en, Waypoint pEnd, TMXLayer pTmxlayer, Level plevel, ArrayList<Point> pxyPath, org.andengine.util.algorithm.path.Path pA_Path) {
		enemy = en;
		waypoints = new ArrayList<Waypoint>();
		iterator = waypoints.iterator();
		end = pEnd;
		tmxlayer = pTmxlayer;
		level = plevel;
		xyPath = pxyPath;
		rcPath = pA_Path;
	}

	public org.andengine.entity.modifier.PathModifier.Path getEntityPath() {
		if (xyPath == null) {
			return null;
		} else {
			org.andengine.entity.modifier.PathModifier.Path tempPath = new org.andengine.entity.modifier.PathModifier.Path(xyPath.size());
			for (int i = 0; i < xyPath.size(); i++)
				tempPath = tempPath.to(xyPath.get(i).x, xyPath.get(i).y);
			return tempPath;
		}
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
				TMXProperties<TMXTileProperty> tmxTileProperties = TowerTest.mTMXTiledMap.getTMXTileProperties(tmxTile.getGlobalTileID());
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
					if ((pX == level.startLoc[i].x) && (pY == level.startLoc[i].y)) {
						return false;
					}
				}
				return true;
			}
		}
	};

	ICostFunction<Enemy> CostCallback = new ICostFunction<Enemy>() {
		@Override
		public float getCost(IPathFinderMap<Enemy> pPathFinderMap, int pFromX, int pFromY, int pToX, int pToY, Enemy pEntity) {
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
		if (rcPath != null) {
			final int len = rcPath.getLength();
			final int[] xs = new int[len]; // A_Path.getX(pY).mXs;
			final int[] ys = new int[len];
			for (int i = 0; i < len; i++) {
				xs[i] = rcPath.getX(i);
				ys[i] = rcPath.getY(i);
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
		} else {
			return false; // no reason to check the path if it's crapola right? :-P
		}
		return false; // we hit the end, so it's not on this path
	}

	private boolean findPath() {
		Log.w("Finder", "I just calculated a path for " + TowerTest.getColFromX(enemy.getX()) + "," + TowerTest.getColFromX(enemy.getY()));
		// coords
		try {
			rcPath = TowerTest.finder.findPath(PathMap, TowerTest.pColMin, TowerTest.pRowMin, TowerTest.pColMax, TowerTest.pRowMax, enemy, enemy.getCol(), enemy.getRow(),
					end.x, end.y, TowerTest.allowDiagonal, Heuristic, CostCallback);
			if (rcPath == null) {
				return false;
			} else {
				return true;
			}
		} catch (NullPointerException e) {
			rcPath = null;
			return false;
		}

		/*
		 * float dY = target.getMidY() - this.getMidY(); // some calc about how far the bullet can go, in this case up to the enemy float dX = target.getMidX() -
		 * this.getMidX();//+(Math.abs(gY)/Enemy.speed/Projectile.speed); float dist = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)); //D=r*t //therefore t = D/r
		 * trajectory = new MoveByModifier(dist/Projectile.speed, dX, dY); this.registerEntityModifier(trajectory);
		 */
	}

	private void convertRCPathtoXY() {
		xyPath = new ArrayList<Point>();
		for (int i = 0; i < rcPath.getLength(); i++)
			xyPath.add(new Point(TowerTest.getXFromCol(rcPath.getX(i)), TowerTest.getYFromRow(rcPath.getY(i))));
		xyPath.add(0, new Point(enemy.getX(), enemy.getY())); // add their current location to the beginning
	}

	/**
	 * ensures that there are no overlapping paths, as well as removes middle-nodes for straight paths
	 */
	private void optimizeXYPath() {
		double currentAngle; // angle of the previous segment
		double newAngle;
		double newAngleInverse;
		// first remove all middle-nodes
		if (xyPath.size() > 2) { // no point in doing it if the line is already as short as it can be
			// set the starting angle to the first two points
			currentAngle = Math.atan2(xyPath.get(1).y - xyPath.get(0).y, xyPath.get(1).x - xyPath.get(0).x); // -pi to pi
			// currentAngle = ((currentAngle < 0) ? currentAngle + 2 * Math.PI : currentAngle); //make it positive
			for (int i = 1; i < xyPath.size() - 1; i++) { // flip through all the points to see if we should remove the previous
				// if it's on the same angle as our last, then remove the previous
				// what's our angle from the last point
				newAngle = Math.atan2(xyPath.get(i + 1).y - xyPath.get(i).y, xyPath.get(i + 1).x - xyPath.get(i).x);
				newAngleInverse = ((newAngle > 0) ? newAngle - Math.PI : newAngle + Math.PI); // the inverse angle, allows it to treat angles
				// that are the opposite direction as the same direction, therefore removing double-backs :-)
				if ((Math.abs(newAngle - currentAngle) < tolerance) || (Math.abs(newAngleInverse - currentAngle) < tolerance)) { // onARoll and the angles are equal
																																	// (close enough)
					xyPath.remove(i);// remove the previous point, since it's the same angle as our current
					i--;
				} else {
					// we must have changed direction, so store this current point as our new corner
					currentAngle = newAngle;
				}
			}
		}
	}

	/**
	 * gets the direction (in radians) that the enemy is traveling
	 * 
	 * @return angle (between -pi and pi)
	 */
	public double getCurrentAngle() {
		double m;
		double b;
		if (xyPath.size() > 1) {
			for (int i = 0; i < xyPath.size() - 1; i++) { // flip through all the segments
				if (((xyPath.get(i).y <= enemy.getY()) && (enemy.getY() <= xyPath.get(i + 1).y))
						|| ((xyPath.get(i + 1).y <= enemy.getY()) && (enemy.getY() <= xyPath.get(i).y))) { // getY() is between y1 and y2
					if (((xyPath.get(i).x <= enemy.getX()) && (enemy.getX() <= xyPath.get(i + 1).x)) // getX() is between x1 and x2
							|| ((xyPath.get(i + 1).x <= enemy.getX()) && (enemy.getX() <= xyPath.get(i).x))) {
						// that means it could be on our line (it's within our box of x1,y1, and x2,y2 all that's left is to check the equation of the line
						// y = m * x + b
						// b = y1 - mx1
						if ((xyPath.get(i + 1).x - xyPath.get(i).x) == 0) { // vertical line, therefore
							if (Math.abs(enemy.getX() - xyPath.get(i).x) < tolerance) {
								return Math.atan2(xyPath.get(i + 1).y - xyPath.get(i).y, xyPath.get(i + 1).x - xyPath.get(i).x);
							}
						} else {
							m = (xyPath.get(i + 1).y - xyPath.get(i).y) / (xyPath.get(i + 1).x - xyPath.get(i).x);
							b = xyPath.get(i).y - m * xyPath.get(i).x;
							if (Math.abs((m * enemy.getX() + b) - enemy.getY()) < tolerance) {
								return Math.atan2(xyPath.get(i + 1).y - xyPath.get(i).y, xyPath.get(i + 1).x - xyPath.get(i).x);
							}
						}
					}
				}
			}
		}
		Log.w("Path", "Warning, enemy was not found on his path!");
		return 0; // return 0 if all else fails
	}

	public Path clone(Enemy newEnemy) {
		if (rcPath == null) {
			return null;
		} else {
			org.andengine.util.algorithm.path.Path tempA_Path = new org.andengine.util.algorithm.path.Path(rcPath.getLength());
			for (int i = 0; i < rcPath.getLength(); i++)
				tempA_Path.set(i, rcPath.getX(i), rcPath.getY(i));
			ArrayList<Point> tempXYPath = new ArrayList<Point>();
			for (int i = 0; i < xyPath.size(); i++)
				tempXYPath.add(new Point(xyPath.get(i).x, xyPath.get(i).y));
			return new Path(newEnemy, end, tmxlayer, level, tempXYPath, tempA_Path);
		}
	}
}