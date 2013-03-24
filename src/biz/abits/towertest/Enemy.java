package biz.abits.towertest;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;

import android.content.Context;
import android.util.Log;

public class Enemy extends Sprite{
	//I am Enemy class
	private int health = 1000;
	private static int credits = 10;
	public static float speed = 3.0f; //movement speed (distance to move per update)
	VertexBufferObjectManager vbom;
	private static String texture = "enemy.png";
	public Path path;
	//TODO Add waypoints  as ArrayList. make move to waypoint, set waypoint, addWaypoint functions.

	
	
	/**
	 * Create a new enemy with a set Path list of waypoints
	 * @param p Path of waypoints
	 * @param b 
	 * @param pX Xcoord location
	 * @param pY Ycoord location
	 * @param pTextureRegion
	 * @param tvbom
	 */
	public Enemy(float pX, float pY, TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pTextureRegion,tvbom);
		vbom = tvbom;
		path = new Path();
	}
	
	/**
	 * This function sets the texture for the tower type and returns a Texture Region to preload the texture.
	 * @param tm Texture manager; usually passed in as this.getTextureManager()
	 * @param c Context; usually passed in as this
	 * @return TextureRegion to load
	 */
	public static TextureRegion loadSprite(TextureManager tm, Context c){
		TextureRegion tr;
		Log.i("Location:","Enemy loadSprite");
		BitmapTextureAtlas towerImage;
		towerImage = new BitmapTextureAtlas(tm,512,512);
		tr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(towerImage, c, texture, 0, 0);
		tm.loadTexture(towerImage);
		return tr;
	}
	
	/**
	 * Deal damage to the enemy, modified by type <br>
	 * @param amount amount of damage to subtract from helth
	 * @param type used to modify the amount of damage based on armor.
	 * @return Less than 1 if enemy died
	 */
	public int takeDamage(int amount, String type){
		health -= amount;
		return health;
	}
	
	/**
	 * Get enemy current health
	 * @return Health of enemy
	 */
	public int getHealth(){
		return health;
	}
	
	/**
	 * Get enemy Credit value if killed
	 * @return Credit value of enemy
	 */
	public int getCredits(){
		return credits;
	}
	
	public float getInterceptX() {
		return this.getX()+this.getWidth()/2;
	}
	
	public float getInterceptY() {
		return this.getY()+this.getHeight()/2; //we'll probably modify this later to account for Y movement
	}
	
	public Vector2 getPosition() {
		return new Vector2(this.getX(), this.getY());
	}
	
	public Vector2 getVelocity() {
		return new Vector2(Enemy.speed, 0); //hard-coded to horizontal for now
	}

	public float getMidX() {
		return this.getX() + this.getWidth()/2;
	}
	
	public float getMidY() {
		return this.getY() + this.getHeight()/2;
	}
}