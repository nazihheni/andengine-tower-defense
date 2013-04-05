package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import com.badlogic.gdx.math.Vector2;

public class Enemy extends Sprite {
	// I am Enemy class
	private int health = 5000;
	private static int credits = 10;
	public static float speed = 50.0f; // movement speed (distance to move per
										// update)
	public static String texture = "enemy.png";
	public Path path;
	private PathModifier trajectory;
	/** used to verify that the target hasn't died yet, makes sure that they don't get duplicate kill credit for more than one bullet hitting target and striking killing blow */
	public boolean isAlive = true;
	private final Level level;

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
			VertexBufferObjectManager tvbom, Level plevel, Scene sc) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, tvbom);
		scene = sc;
		level = plevel;
	}

	public void setPathandMove(Waypoint pEnd, BaseGameActivity myContext, TMXLayer pTmxlayer, ArrayList<Enemy> arrayEn) {
		path = new Path(Enemy.this, pEnd, pTmxlayer, level);
		startMoving(arrayEn, myContext);
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
		return new Vector2(getX(), getY());
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
		return getX() + getWidth() / 2;
	}

	public float getMidY() {
		return getY() + getHeight() / 2;
	}

	public void startMoving(final ArrayList<Enemy> arrayEn, final BaseGameActivity myContext) {
		// convert our type of path we have to their type of path
		org.andengine.entity.modifier.PathModifier.Path tempPath = new org.andengine.entity.modifier.PathModifier.Path(
				path.A_Path.getLength());
		for (int i = 0; i < path.A_Path.getLength(); i++)
			tempPath = tempPath.to(TowerTest.getXFromCol(path.A_Path.getX(i)), TowerTest.getYFromRow(path.A_Path
					.getY(i)));

		// now find the total length of the path
		final float dist = tempPath.getLength();

		// D=r*t
		// therefore t = D/r
		trajectory = new PathModifier(dist / Enemy.speed, tempPath);
		trajectory.addModifierListener(new IModifierListener<IEntity>() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { // Do stuff here if you want to
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				myContext.getEngine().runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						scene.detachChild(Enemy.this);
						arrayEn.remove(Enemy.this);
						//subtract a life
						TowerTest.subtractLives(1);
					}
				}); // enemy takes damage
			}
		});
		registerEntityModifier(trajectory);
	}

	/** returns which column the enemy is in (between 0 for the first column, and 14 for the last column) */
	public int getCol() {
		return TowerTest.getColFromX(getX());
	}

	/** returns which row the enemy is in (between 0 for the first row, and 6 for the last row) */
	public int getRow() {
		return TowerTest.getRowFromY(getY());
	}
}
