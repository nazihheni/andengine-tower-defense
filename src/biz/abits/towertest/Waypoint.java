package biz.abits.towertest;

/**
 * Waypoints are used by enemies to set a path.
 * 
 * @author abinning
 * 
 */
public class Waypoint {
	public int x, y;

	/**
	 * Create a new Waypoint with coords
	 * 
	 * @param fx
	 * @param fy
	 */
	public Waypoint(int fx, int fy) {
		x = fx;
		y = fy;
	}

	/**
	 * set booth coords
	 * 
	 * @param fx
	 * @param fy
	 */
	public void setCoords(int fx, int fy) {
		x = fx;
		y = fy;
	}

	/**
	 * Get X coord
	 * 
	 * @return float X
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get Y coord
	 * 
	 * @return float Y
	 */
	public int getY() {
		return y;
	}
}
