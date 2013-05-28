package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

public class Enemy extends Sprite {
	// I am Enemy class
	private int health = 100000;
	private final int credits = 10;
	public float speed = 50.0f; // movement speed (distance to move per ?)
	public final static String texture = "enemy.png";
	public Path path;
	public ProgressBar healthBar;
	public ZoomCamera camP;
	private PathModifier trajectory;
	/** used to verify that the target hasn't died yet, makes sure that they don't get duplicate kill credit for more than one bullet hitting target and striking killing blow */
	public boolean isAlive = true;
	// static (only set once) area
	public static ArrayList<Enemy> arrayEn;
	private static Level level;
	

	/**
	 * Create a new enemy with a set Path list of waypoints (also sets static variables)
	 * 
	 * @param p Path of waypoints
	 * @param b
	 * @param pX Xcoord location
	 * @param pY Ycoord location
	 * @param iTextureRegion
	 * @param tvbom
	 * @param plevel
	 * @param sc
	 * @param pArrayEn
	 */
	public Enemy(float pX, float pY, float pWidth, float pHeight, ITextureRegion iTextureRegion, VertexBufferObjectManager tvbom, Level plevel, Scene sc,
			ArrayList<Enemy> pArrayEn) {
		super(pX, pY, pWidth, pHeight, iTextureRegion, tvbom);
		level = plevel;
		arrayEn = pArrayEn;
		
	}

	/**
	 * Create a new enemy with a set Path list of waypoints
	 * 
	 * @param p Path of waypoints
	 * @param b
	 * @param pX Xcoord location
	 * @param pY Ycoord location
	 * @param iTextureRegion
	 * @param tvbom
	 */
	public Enemy(float pX, float pY, float pWidth, float pHeight, ITextureRegion iTextureRegion, VertexBufferObjectManager tvbom) {
		super(pX, pY, pWidth, pHeight, iTextureRegion, tvbom);
	}

	public void createPath(Waypoint pEnd, BaseGameActivity myContext, TMXLayer pTmxlayer, ArrayList<Enemy> arrayEn) {
		path = new Path(Enemy.this, pEnd, pTmxlayer, level);
	}

	public void setPathandMove(Waypoint pEnd, BaseGameActivity myContext, TMXLayer pTmxlayer, ArrayList<Enemy> arrayEn) {
		createPath(pEnd, myContext, pTmxlayer, arrayEn);
		camP = new ZoomCamera(0, 0, Enemy.SPRITE_SIZE, Enemy.SPRITE_SIZE);
		healthBar = new ProgressBar(camP, 20, 64, 100, 10, getVertexBufferObjectManager());
		healthBar.setProgressColor(1.0f, 0.0f, 0.0f, 1.0f).setFrameColor(0.4f, 0.4f, 0.4f, 1.0f).setBackColor(0.0f, 0.0f, 0.0f, 0.2f);
		healthBar.setProgress(0);
		this.attachChild(healthBar);
		// path = new Path(Enemy.this, pEnd, pTmxlayer, level);
		startMoving(myContext);
	}

	public void stop() {
		unregisterEntityModifier(trajectory);
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

	public double getXSpeed() {
		return Math.cos(getCurrentAngle()) * speed;
	}

	public double getYSpeed() {
		return Math.sin(getCurrentAngle()) * speed;
	}
	
	public double getCurrentAngle() {
		return path.getCurrentAngle();		
	}

	public float getMidX() {
		return getX() + getWidth() / 2;
	}

	public float getMidY() {
		return getY() + getHeight() / 2;
	}

	public void startMoving(final BaseGameActivity myContext) {
		if (path != null) {
			// convert our type of path we have to their type of path
			final org.andengine.entity.modifier.PathModifier.Path tempPath = path.getEntityPath();
			if (tempPath != null) {
				// now find the total length of the path
				final float dist = tempPath.getLength();

				// D=r*t
				// therefore t = D/r
				trajectory = new PathModifier(dist / speed, tempPath);
				trajectory.addModifierListener(new IModifierListener<IEntity>() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { // Do stuff here if you want to
					}

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						myContext.getEngine().runOnUpdateThread(new Runnable() {
							@Override
							public void run() {
								// scene.detachChild(Enemy.this);
								Enemy.this.detachSelf();
								arrayEn.remove(Enemy.this);
								// subtract a life
								TowerTest.subtractLives(1);
							}
						}); // enemy takes damage
					}
				});
				registerEntityModifier(trajectory);
			}
		}
	}

	/** returns which column the enemy is in (between 0 for the first column, and 14 for the last column) */
	public int getCol() {
		return TowerTest.getColFromX(getX());
	}

	/** returns which row the enemy is in (between 0 for the first row, and 6 for the last row) */
	public int getRow() {
		return TowerTest.getRowFromY(getY());
	}

	@Override
	public Enemy clone() {
		// no need to use the other constructor, since those are already set
		final Enemy returnEnemy = new Enemy(getX(), getY(), getWidth(), getHeight(), getTextureRegion(), getVertexBufferObjectManager());
		returnEnemy.path = path.clone(returnEnemy);
		return returnEnemy;
	}
}
