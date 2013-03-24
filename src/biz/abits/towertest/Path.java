package biz.abits.towertest;

import java.util.ArrayList;
import java.util.Iterator;

import org.andengine.entity.modifier.MoveByModifier;

/**
 * Path is a list of waypoints used to travel
 * The ArrayList waypoints is public and can be access directly
 * @author abinning
 *
 */
public class Path{
	public ArrayList<Waypoint> waypoints;
	Iterator<Waypoint> iterator;
	/**
	 * initialized the list
	 */
	public Path(){
		waypoints = new ArrayList<Waypoint>();
		iterator = waypoints.iterator();
		findPath();
	}
	
	/**
	 * Add new Waypoint
	 * @param wp
	 */
	public void add(Waypoint wp){
		waypoints.add(wp);
	}
	
	/**
	 * Remove Waypoint
	 * @param wp
	 */
	public void remove(Waypoint wp){
		waypoints.remove(wp);
	}

	/**
	 * Add new Waypoint at index
	 * @param wp
	 */
	public void add(int index, Waypoint wp){
		waypoints.add(index, wp);
	}
	
	/**
	 * Remove Waypoint at index
	 * @param wp
	 */
	public void remove(int index){
		waypoints.remove(index);
	}
	
	/**
	 * get iterator
	 * @param wp
	 */
	public Iterator<Waypoint> iterator(){
		return iterator;
	}
	/**
	 * Get next waypoint using iterator
	 * @return
	 */
	public Waypoint next(){
		return iterator.next();
	}
	
	/**
	 * check to see if there is a next waypoint using iterator
	 * @return
	 */
	public boolean hasNext(){
		return iterator.hasNext();
	}
	
	/**
	 * Get waypoint by index
	 * @param index
	 * @return
	 */
	public Waypoint get(int index){
		return waypoints.get(index);
	}


	private void findPath()
	{
		/**
		float dY = target.getMidY() - this.getMidY(); // some calc about how far the bullet can go, in this case up to the enemy
    	float dX = target.getMidX() - this.getMidX();//+(Math.abs(gY)/Enemy.speed/Projectile.speed);
    	float dist = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    	//D=r*t
    	//therefore t = D/r
    	trajectory = new MoveByModifier(dist/Projectile.speed, dX, dY);
    	this.registerEntityModifier(trajectory);
    	**/
	}
}