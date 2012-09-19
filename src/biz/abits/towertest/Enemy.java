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
	private int health = 500;
	private static int credits = 10;
	public float x,y;
	public float speed = 2.0f; //movement speed (distance to move per update)
	VertexBufferObjectManager vbom;
	public static String texture = "enemy.png";
	//TODO Add waypoints  as ArrayList. make move to waypoint, set waypoint, addWaypoint functions.

	public Enemy(TextureRegion b,float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
		vbom = tvbom;
		x=pX; //some x n y of the enemy
		y=pY;
	}

	public Enemy(float pX, float pY, TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pTextureRegion,tvbom);
		vbom = tvbom;
		x=pX; //some x n y of the enemy
		y=pY;
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