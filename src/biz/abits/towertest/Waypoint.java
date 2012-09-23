package biz.abits.towertest;

/**
 * Waypoints are used by enemies to set a path.
 * @author abinning
 *
 */
public class Waypoint{
	public float x,y;
	
	/**
	 * Create a new Waypoint with coords
	 * @param fx
	 * @param fy
	 */
	public Waypoint(float fx, float fy){
		x = fx;
		y = fy;
	}
	
	/**
	 * set booth coords
	 * @param fx
	 * @param fy
	 */
	public void setCoords(float fx, float fy){
		x = fx;
		y = fy;
	}
	/**
	 * Get X coord
	 * @return float X
	 */
	public float getX() { return x; }
	/**
	 * Get Y coord
	 * @return float Y
	 */
	public float getY() { return y; }
}
