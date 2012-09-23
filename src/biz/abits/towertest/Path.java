package biz.abits.towertest;

import java.util.ArrayList;
import java.util.Iterator;

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
}