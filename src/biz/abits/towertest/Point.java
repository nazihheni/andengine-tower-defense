package biz.abits.towertest;

/**
 * Points are used by enemies to set a path.
 * 
 * @author jmeadows
 * 
 */
public class Point {
	public float x, y;

	/**
	 * Create a new Point with coords
	 * 
	 * @param f
	 * @param g
	 */
	public Point(float f, float g) {
		x = f;
		y = g;
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
	public float getX() {
		return x;
	}

	/**
	 * Get Y coord
	 * 
	 * @return float Y
	 */
	public float getY() {
		return y;
	}
}
