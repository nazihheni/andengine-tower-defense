package biz.abits.towertest;

import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

import com.badlogic.gdx.math.Vector2;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class Enemy extends Sprite {
	// I am Enemy class
	private int health = 1000;
	private static int credits = 10;
	public static float speed = 50.0f; // movement speed (distance to move per
										// update)
	private static String texture = "enemy.png";
	public Path path;
	private MoveByModifier trajectory;

	// TODO Add waypoints as ArrayList. make move to waypoint, set waypoint,
	// addWaypoint functions.

	/**
	 * Create a new enemy with a set Path list of waypoints
	 * 
	 * @param p Path of waypoints
	 * @param b
	 * @param pX Xcoord location
	 * @param pY Ycoord location
	 * @param pTextureRegion
	 * @param tvbom
	 * @param level
	 */
	public Enemy(float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion,
			VertexBufferObjectManager tvbom, Level level) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, tvbom);
	}

	public void setPathandMove(Point pEnd, BaseGameActivity myContext, TMXLayer pTmxlayer) {
		path = new Path(Enemy.this, pEnd, pTmxlayer);
		startMoving();
	}

	/**
	 * This function sets the texture for the tower type and returns a Texture Region to preload the texture.
	 * 
	 * @param tm Texture manager; usually passed in as this.getTextureManager()
	 * @param c Context; usually passed in as this
	 * @return TextureRegion to load
	 */
	public static TextureRegion loadSprite(TextureManager tm, Context c) {
		TextureRegion tr;
		Log.i("Location:", "Enemy loadSprite");
		BitmapTextureAtlas towerImage;
		towerImage = new BitmapTextureAtlas(tm, 512, 512);
		tr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(towerImage, c, texture, 0, 0);
		tm.loadTexture(towerImage);
		return tr;
	}

	/**
	 * Deal damage to the enemy, modified by type <br>
	 * 
	 * @param amount amount of damage to subtract from helth
	 * @param type used to modify the amount of damage based on armor.
	 * @return Less than 1 if enemy died
	 */
	public int takeDamage(int amount, String type) {
		health -= amount;
		return health;
	}

	/**
	 * Get enemy current health
	 * 
	 * @return Health of enemy
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Get enemy Credit value if killed
	 * 
	 * @return Credit value of enemy
	 */
	public int getCredits() {
		return credits;
	}

	public Vector2 getPosition() {
		return new Vector2(this.getX(), this.getY());
	}

	public Vector2 getVelocity() {
		return new Vector2(Enemy.speed, 0); // hard-coded to horizontal for now
	}

	public float getXSpeed() {
		return Enemy.speed;
	}

	public float getYSpeed() {
		return 0;
	}

	public float getMidX() {
		return this.getX() + this.getWidth() / 2;
	}

	public float getMidY() {
		return this.getY() + this.getHeight() / 2;
	}

	public void freeze() {
		this.unregisterEntityModifier(trajectory);
	}

	public void startMoving() {
		float dY = 0;
		float dX = TowerTest.CAMERA_WIDTH;
		float dist = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
		// D=r*t
		// therefore t = D/r
		trajectory = new MoveByModifier(dist / Enemy.speed, dX, dY);
		this.registerEntityModifier(trajectory);
	}

	public int getCol() {
		return TowerTest.getColFromX(this.getX());
	}

	public int getRow() {
		return TowerTest.getRowFromY(this.getY());
	}
}
