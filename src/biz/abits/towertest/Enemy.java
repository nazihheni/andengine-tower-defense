package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;

public class Enemy extends Sprite {
	// I am Enemy class
	private int health = 5000;
	private static int credits = 10;
	public static float speed = 50.0f; // movement speed (distance to move per
										// update)
	private static String texture = "enemy.png";
	public Path path;
	private MoveByModifier trajectory;
	/** used to verify that the target hasn't died yet, makes sure that they don't get duplicate kill credit for more than one bullet hitting target and striking killing blow */
	public boolean isAlive = true;

	Scene scene;

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
			VertexBufferObjectManager tvbom, Level level, Scene sc) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, tvbom);
		scene = sc;
	}

	public void setPathandMove(Waypoint pEnd, BaseGameActivity myContext, TMXLayer pTmxlayer, ArrayList<Enemy> arrayEn) {
		path = new Path(Enemy.this, pEnd, pTmxlayer);
		startMoving(arrayEn, myContext);
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

	public void startMoving(final ArrayList<Enemy> arrayEn, final BaseGameActivity myContext) {
		float dY = 0;
		float dX = TowerTest.CAMERA_WIDTH;
		float dist = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
		// D=r*t
		// therefore t = D/r
		// trajectory = new MoveByModifier(dist / Enemy.speed, dX, dY);
		// this.registerEntityModifier(trajectory);

		// convert our type of path we have to their type of path
		org.andengine.entity.modifier.PathModifier.Path tempPath = new org.andengine.entity.modifier.PathModifier.Path(
				path.A_Path.getLength());
		for (int i = 0; i < path.A_Path.getLength(); i++)
			tempPath = tempPath.to(TowerTest.getXFromCol(path.A_Path.getX(i)), TowerTest.getYFromRow(path.A_Path
					.getY(i)));

		PathModifier trajectory2 = new PathModifier(dist / Enemy.speed, tempPath);
		trajectory2.addModifierListener(new IModifierListener<IEntity>() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { // Do stuff here if you want to

			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				myContext.getEngine().runOnUpdateThread(new Runnable() {

					@Override
					public void run() {
						Enemy.this.scene.detachChild(Enemy.this);
						// TODO add code here to subtract a life, since the enemy got through without dieing!
					}
				}); // enemy takes damage
			}
		});

		this.registerEntityModifier(trajectory2);
	}

	/** returns which column the enemy is in (between 0 for the first column, and 14 for the last column) */
	public int getCol() {
		return TowerTest.getColFromX(this.getX());
	}

	/** returns which row the enemy is in (between 0 for the first row, and 6 for the last row) */
	public int getRow() {
		return TowerTest.getRowFromY(this.getY());
	}
}
