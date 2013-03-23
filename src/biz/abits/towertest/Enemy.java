package biz.abits.towertest;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.util.Log;

public class Enemy extends Sprite{
	//I am Enemy class
	private int health = 1000;
	private static int credits = 10;
	public float speed = 2.0f; //movement speed (distance to move per update)
	VertexBufferObjectManager vbom;
	private static String texture = "enemy.png";
	public Path path;
	//TODO Add waypoints  as ArrayList. make move to waypoint, set waypoint, addWaypoint functions.

	/**
	 * Create a new enemy with size
	 * @param b 
	 * @param pX Xcoord location
	 * @param pY Ycoord location
	 * @param pWidth
	 * @param pHeight
	 * @param pTextureRegion
	 * @param tvbom
	 */
	public Enemy(TextureRegion b,float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
		vbom = tvbom;
		path = new Path();
	}
	
	/**
	 * Create a new enemy with size and a set Path list of waypoints
	 * @param p Path of waypoints
	 * @param b 
	 * @param pX Xcoord location
	 * @param pY Ycoord location
	 * @param pWidth
	 * @param pHeight
	 * @param pTextureRegion
	 * @param tvbom
	 */
	public Enemy(Path p, TextureRegion b,float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
		vbom = tvbom;
		path = p;
	}

	/**
	 * Create a new enemy
	 * @param pX
	 * @param pY
	 * @param pTextureRegion
	 * @param tvbom
	 */
	public Enemy(float pX, float pY, TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pTextureRegion,tvbom);
		vbom = tvbom;
	}
	
	/**
	 * Create a new enemy with a set Path list of waypoints
	 * @param p Path of waypoints
	 * @param b 
	 * @param pX Xcoord location
	 * @param pY Ycoord location
	 * @param pTextureRegion
	 * @param tvbom
	 */
	public Enemy(Path p, float pX, float pY, TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pTextureRegion,tvbom);
		vbom = tvbom;
		path = p;
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
	 * move 3 pixels per 5ms i think the 6f is speed i think
	 */
	public void move(){
		//TODO use waypoints
		setPosition(getX()+speed,getY()); 
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
}