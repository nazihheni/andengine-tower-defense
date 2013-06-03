package biz.abits.towertest;

import java.io.IOException;
import java.util.ArrayList;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;

/**
 * Basic Tower class contains it's own projectiles and provides methods for firing
 * 
 * @author Andrew Binning
 * @see Projectile
 * @see SplashTower
 */
public class Tower extends Sprite {
	// I am Tower class and have my own bullets //
	// TODO fire range, acquisition range, pattern/type.
	public static String texture = "tower.png";
	private long cooldown = 500; // in milliseconds | 1 sec = 1,000 millisec
	private long credits = 50; // cost to build tower in credits
	private int level = 1; // level of tower
	private int maxLevel = 10; // level of tower
	public final int damage = 100; // Tower damage
	public String damageType = "normal";
	private float cdMod = 0.5f;
	private long lastFire = 0;
	private static int total = 0; // total number of this type of tower
	TextureRegion tower;
	TextureRegion bullet;
	private static String strFire = "tower.ogg";
	private static Sound soundFire;
	float targetX;
	float targetY;
	private boolean placeError = false;
	private boolean hitAreaShown = false;
	private boolean hitAreaGoodShown = false;
	private boolean hitAreaBadShown = false;
	private int zIndex = 1000;
	TextureRegion hitAreaTextureGood;
	TextureRegion hitAreaTextureBad;
	TowerRange towerRangeGood;
	TowerRange towerRangeBad;
	/**
	 * Tells us that we can move the tower, also tells us that the tower can shoot, false means it can do neither thing
	 */
	public boolean moveable = true;
	Projectile SpriteBullet;
	static ArrayList<Tower> arrayTower;
	static Scene scene;
	static float lastCheckedX = 0;
	static float lastCheckedY = 0;

	// int speed = 500;
	ArrayList<Projectile> arrayBullets; // may change to spritebatch

	// Body range = PhysicsFactory.createCircularBody();

	// constructor
	/**
	 * Constructor for tower class
	 * 
	 * @param b TextureRegion for bullet
	 * @param pX x coordinate of tower to create
	 * @param pY y coordinate of tower to create
	 * @param pWidth width of tower
	 * @param pHeight height of tower
	 * @param pTextureRegion I don't think this is even used? :-\
	 * @param tvbom VertexBufferObjectManager
	 */
	public Tower(TextureRegion b, float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion, TextureRegion hitAreaTextureGood,
			TextureRegion hitAreaTextureBad, Scene pScene, ArrayList<Tower> pArrayTower, VertexBufferObjectManager tvbom) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, tvbom);
		// towerRangeGood.setPosition(pX, pY);
		bullet = b; // we need bullet TextureRegion to make one
		// x=pX; //some x n y of the tower
		// y=pY;
		scene = pScene;
		arrayBullets = new ArrayList<Projectile>(); // create a new ArrayList
		towerRangeGood = new TowerRange(0, 0, hitAreaTextureGood, getVertexBufferObjectManager());
		towerRangeBad = new TowerRange(0, 0, hitAreaTextureBad, getVertexBufferObjectManager());
		towerRangeGood.setPosition(this.getWidth() / 2 - towerRangeGood.getWidth() / 2, this.getHeight() / 2 - towerRangeGood.getHeight() / 2);
		towerRangeBad.setPosition(this.getWidth() / 2 - towerRangeBad.getWidth() / 2, this.getHeight() / 2 - towerRangeBad.getHeight() / 2);
		arrayTower = pArrayTower;
		this.setZIndex(zIndex); // used to determine the order stuff is drawn in
		total++;
	}

	/**
	 * Fires projectiles check cooldown in milli seconds with: <br>
	 * long elapsedTime = System.currentTimeMillis() - towerVar.getLastFire;
	 * 
	 * @param targetX target attacking
	 * @param targetY target attacking
	 * @param tx location of projectile
	 * @param ty location of projectile
	 * @return boolean True if tower fired (created bullet sprite), else false
	 */
	public boolean fire(Enemy target, Tower source, Scene scene, ArrayList<Enemy> arrayEn, BaseGameActivity myContext) {
		// TODO move bullet to mouth of cannon
		long elapsed = System.currentTimeMillis() - lastFire;
		// only fire if tower is off cool down
		if (elapsed > cooldown * cdMod && !moveable) { // not on cooldown, and
														// not actively being
														// placed
			SpriteBullet = new Projectile(source.getMidX(), source.getMidY(), 10f, 10f, bullet, getVertexBufferObjectManager(), scene); // READY?!?
			SpriteBullet.setTarget(this, target); // AIM...
			SpriteBullet.shoot(arrayEn, myContext); // FIIIIIRE!!!!
			arrayBullets.add(SpriteBullet);
			lastFire = System.currentTimeMillis();
			// TODO check sound settings
			soundFire.play();
			return true;
		} else
			return false;
	}

	public void fire(Enemy enemy, Scene scene, ArrayList<Enemy> arrayEn, BaseGameActivity myContext) {
		if (!TowerTest.paused) {
			try {
				targetX = enemy.getMidX(); // simple get the enemy x,y and center it and tell the bullet where to aim and fire
				targetY = enemy.getMidY();
				// call fire from the tower
				boolean fired = this.fire(enemy, this, scene, arrayEn, myContext); // Asks the tower to open fire and places
																					// the bullet in middle of tower
				if (fired) {
					// ArrayList<Projectile> towerBulletList = this.getArrayList(); // gets bullets from Tower class where our bullets are fired from
					Sprite myBullet = this.getLastBulletSprite();
					scene.attachChild(myBullet);
					// for(Sprite bullet : towerBulletList){
					/*
					 * for(int i = 0; i < towerBulletList.size(); i++){ Projectile bullet; bullet = towerBulletList.get(i); if(bullet.isDone()) { //collidesWith(enemy)){
					 * //WARNING: This function should be called from within postRunnable(Runnable) which is registered to a Scene or the Engine itself, because otherwise it
					 * may throw an IndexOutOfBoundsException in the Update-Thread or the GL-Thread! //nevermind, I threw it in the listener for the bullet onModifierFinished
					 * listener! } }
					 */
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * Get the cool down milliseconds
	 * 
	 * @return cool down in milliseconds
	 */
	public long getCD() {
		return cooldown;
	}

	/**
	 * Get the cool down Modifier as a float to represent a percentage if( elapsed > cooldown * cdMod)
	 * 
	 * @return cool down Modifier float
	 */
	public float getCDMod() {
		return cdMod;
	}

	/**
	 * Set the cool down Modifier as a float to represent a percentage if( elapsed > cooldown * cdMod)
	 */
	public void setCDMod(long cdm) {
		cdMod = cdm;
	}

	/**
	 * Loads the sound from mfx/
	 * 
	 * @param sm SoundManager passed from engine
	 * @param act SimpleBaseGameActivity Base class (this)
	 */
	public static void loadSound(SoundManager sm, SimpleBaseGameActivity act) {
		try {
			soundFire = SoundFactory.createSoundFromAsset(sm, act, strFire);
		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	public Sprite getLastBulletSprite() {
		return SpriteBullet; // our main class uses this to attach to the scene
	}

	public ArrayList<Projectile> getArrayList() {
		return arrayBullets; // our main class uses this to check bullets etc
	}

	/**
	 * Get to cost to build this tower
	 * 
	 * @return build cost in credits
	 */
	public long getCredits() {
		return credits;
	}

	/**
	 * Get Current tower level
	 * 
	 * @return tower level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Upgrade tower one level
	 * 
	 * @return Returns false if tower already at max level
	 */
	public boolean upgradeLevel() {
		if (level == maxLevel)
			return false;
		else
			level++;
		return true;
	}

	/**
	 * Get the total number of these towers that have been built
	 * 
	 * @return number of this tower that has been built
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Sell tower, return credit value
	 * 
	 * @return credit value of tower
	 */
	public long sell(BaseGameActivity myContext) {
		remove(true, myContext);
		return credits;
	}

	/**
	 * removes tower from scene and array, and updates counter
	 * 
	 * @param tower
	 * @param resetTile
	 */
	public void remove(final Tower tower, final boolean resetTile, BaseGameActivity myContext) {
		myContext.getEngine().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				scene.unregisterTouchArea(tower);
				// scene.detachChild(tower);
				tower.detachSelf();
				arrayTower.remove(tower);
				if (resetTile) {
					Log.e("Tower", "removed " + tower.getCol() + "," + tower.getRow());
				}
			}
		});
		total--;
		if (resetTile) { // we must re-calculate ALL paths, since there's no telling how to know if the one they removed will change paths :-\
			try {
				TowerTest.tmxLayer.getTMXTileAt(tower.getX(), tower.getY()).setGlobalTileID(TowerTest.mTMXTiledMap, TowerTest.TILEID_CLEAR);
				Path path;
				// first go through and update all the enemies in the ArrayList
				for (Enemy en : Enemy.arrayEn) {
					path = new Path(en, TowerTest.currentLevel.endLoc[0], TowerTest.tmxLayer, TowerTest.currentLevel);
					en.path = path;
					en.stop();
					en.startMoving(myContext);
				}
				// now don't forget to update the enemyClone!
				for (Enemy en : TowerTest.enemyClone) {
					path = new Path(en, TowerTest.currentLevel.endLoc[0], TowerTest.tmxLayer, TowerTest.currentLevel);
					en.path = path;
				}
			} catch (NullPointerException e) {
				//then they must be in the black void, so no need to reset the tile
			}
		}
	}

	/**
	 * Removes this tower from scene and array, and updates counter
	 * 
	 * @param resetTile true if you want the tile set to traversable
	 */
	public void remove(boolean resetTile, BaseGameActivity myContext) {
		remove(this, resetTile, myContext);
	}

	/**
	 * This gets the X attachment point (allows you to offset where you grab it at, 0,0 is the upper-left)
	 */
	public float getXHandleOffset() {
		return (this.getWidth() / 2) * 1.5f; // asjusted by 1.5 so it looks like
												// you're grabbing the center of
												// the turret
	}

	/**
	 * This gets the Y attachment point (allows you to offset where you grab it at, 0,0 is the upper-left)
	 */
	public float getYHandleOffset() {
		return this.getHeight() / 2; // default to the middle of the sprite
	}

	/**
	 * Checks to see if the tower can be placed here, and places it, also updates the tower if it should have a placement error
	 * 
	 * @param scene
	 * @param newX x value where we're trying to place the tower
	 * @param newY y value where we're trying to place the tower
	 * @param myContext
	 */
	public void checkClearSpotAndPlace(Scene scene, float newX, float newY, BaseGameActivity myContext) {
		final TMXTile tmxTile = TowerTest.tmxLayer.getTMXTileAt(newX, newY);
		try {
			final TMXProperties<TMXTileProperty> tmxTileProperties = TowerTest.mTMXTiledMap.getTMXTileProperties(tmxTile.getGlobalTileID());
			// Snaps tower to tile
			if (TowerTest.enableSnap) {
				newX = tmxTile.getTileX();
				newY = tmxTile.getTileY();
			}
			if (tmxTileProperties.containsTMXProperty("Collidable", "False")) {
				// set the circle to red (it has an error)
				this.setTowerPlaceError(scene, true);
				
				Log.e("Jared","CAN NOT PLACE ON CACTI "+newX+","+newY);
			} else {
				if ((newX != lastCheckedX) || (newY != lastCheckedY)) { // only check it if we haven't checked it yet
					if (Tower.canPlace(newX, newY, false, myContext, this)) {
						// set the circle to green
						this.setTowerPlaceError(scene, false);
						Log.e("Jared","Can Place Here");
					} else {
						this.setTowerPlaceError(scene, true);
						Log.e("Jared","CAN NOT PLACE HERE");
					}
				}
			}
		} catch (Exception e) { // this happens when it's drug off the map
			this.setTowerPlaceError(scene, true);
			Log.e("Jared","ERROR returning TMXTiledMap Properties!!!");
		}
		this.setPosition(newX, newY);
	}

	public static boolean canPlace(float newX, float newY, boolean assignPaths, BaseGameActivity myContext, Tower tw) {
		final TMXTile tmxTile = TowerTest.tmxLayer.getTMXTileAt(newX, newY);
		if (tmxTile != null) {
			int backupTileID = tmxTile.getGlobalTileID();
			tmxTile.setGlobalTileID(TowerTest.mTMXTiledMap, TowerTest.TILEID_BLOCKED);
			// crazy loop action
			boolean towerNotAllowed = false;
			Path[] tempPaths = new Path[Enemy.arrayEn.size() + TowerTest.enemyClone.length];
			boolean[] needsNewPath = new boolean[Enemy.arrayEn.size() + TowerTest.enemyClone.length];
			if (assignPaths) {
				for (int i = 0; i < Enemy.arrayEn.size(); i++) {
					Enemy enemy = Enemy.arrayEn.get(i);
					// if the tower is on this enemy's path, then, check if the enemy can find a new one
					if (enemy.path != null) {
						if (enemy.path.checkRemainingPath(TowerTest.getColFromX(newX), TowerTest.getRowFromY(newY))) {
							// only then, should we check pathfinding!
							tempPaths[i] = new Path(enemy, TowerTest.currentLevel.endLoc[0], TowerTest.tmxLayer, TowerTest.currentLevel);
							if (tempPaths[i].rcPath == null) {
								// they can't put it here!
								towerNotAllowed = true;
								break;
							}
							needsNewPath[i] = true;
						} else {
							needsNewPath[i] = false;
						}
					}
				}
			}
			// also, check the starting points!
			if (!towerNotAllowed) {
				for (int i = 0; i < TowerTest.enemyClone.length; i++) {
					if (TowerTest.enemyClone[i].path == null) {
						towerNotAllowed = true;
						Log.e("Jared","Warning, a starting point doesn't have a path!!!");
					} else {
						if (TowerTest.enemyClone[i].path.rcPath.contains(TowerTest.getColFromX(newX), TowerTest.getColFromX(newY))) {
							tempPaths[Enemy.arrayEn.size() + i] = new Path(TowerTest.enemyClone[i], TowerTest.currentLevel.endLoc[0], TowerTest.tmxLayer, TowerTest.currentLevel);
							if (tempPaths[Enemy.arrayEn.size() + i].rcPath == null) {
								// they can't put it here!
								towerNotAllowed = true;
							}
							needsNewPath[Enemy.arrayEn.size() + i] = true;
						}
					}
				}
			}
			// now that we have all the paths, if they were all good, assign them to their enemies, otherwise remove the tower
			lastCheckedX = newX;
			lastCheckedY = newY;
			if (!assignPaths) {
				tmxTile.setGlobalTileID(TowerTest.mTMXTiledMap, backupTileID);
				return !towerNotAllowed;
			} else {
				if (towerNotAllowed) { // remove it, because one enemy had a problem with it
					tw.remove(false, myContext);
					tmxTile.setGlobalTileID(TowerTest.mTMXTiledMap, backupTileID);
					return false;
				} else {
					// go through and assign the paths, since nobody had a problem with it
					for (int i = 0; i < Enemy.arrayEn.size(); i++) {
						if (needsNewPath[i]) {
							Enemy enemy = Enemy.arrayEn.get(i);
							enemy.path = tempPaths[i];
							enemy.stop();
							enemy.startMoving(myContext);
						}
					}
					for (int i = 0; i < TowerTest.enemyClone.length; i++) {
						if (needsNewPath[Enemy.arrayEn.size() + i]) {
							TowerTest.enemyClone[i].path = tempPaths[Enemy.arrayEn.size() + i];
						}
					}
					return true;
				}
			}
		} else { //the tile is null (the tower is in the black void), so return false
			return false;
		}
	}

	/**
	 * This tells the tower if it is allowed to be placed where it is, when it is being placed
	 */
	public void setTowerPlaceError(Scene scene, boolean towerPlaceError) {
		placeError = towerPlaceError;
		if (hitAreaShown) {
			// update the hit area to reflect this
			setHitAreaShown(scene, hitAreaShown);
		}
		// update graphics
	}

	public boolean getHitAreaShown() {
		return hitAreaShown;
	}

	/**
	 * Enables or disables the display of the "hit area", also updates color if necessary
	 */
	public void setHitAreaShown(Scene scene, boolean showHitArea) {
		if (moveable) {
			towerRangeGood.setPosition(this.getWidth() / 2 - towerRangeGood.getWidth() / 2, this.getHeight() / 2 - towerRangeGood.getHeight() / 2);
			towerRangeBad.setPosition(this.getWidth() / 2 - towerRangeBad.getWidth() / 2, this.getHeight() / 2 - towerRangeBad.getHeight() / 2);
		} else {
			towerRangeGood.setPosition(this.getX() + this.getWidth() / 2 - towerRangeGood.getWidth() / 2, this.getY() + this.getHeight() / 2 - towerRangeGood.getHeight() / 2);
			towerRangeBad.setPosition(this.getX() + this.getWidth() / 2 - towerRangeBad.getWidth() / 2, this.getY() + this.getHeight() / 2 - towerRangeBad.getHeight() / 2);
		}

		if (showHitArea) {
			// we attach it to this sprite, that way it's tied to it!
			if (placeError) {
				if (!hitAreaBadShown) {
					if (this.moveable) {
						this.attachChild(towerRangeBad);
					} else {
						scene.attachChild(towerRangeBad);
					}
					hitAreaBadShown = true;
				}
				if (hitAreaGoodShown) {
					if (this.moveable) {
						this.detachChild(towerRangeGood);
					} else {
						scene.detachChild(towerRangeGood);
					}
					hitAreaGoodShown = false;
				}
			} else {
				if (hitAreaBadShown) {
					if (this.moveable) {
						this.detachChild(towerRangeBad);
					} else {
						scene.detachChild(towerRangeBad);
					}
					hitAreaBadShown = false;
				}
				if (!hitAreaGoodShown) {
					if (this.moveable) {
						this.attachChild(towerRangeGood);
					} else {
						scene.attachChild(towerRangeGood);
					}
					hitAreaGoodShown = true;
				}
			}
		} else {
			// detach both!
			if (hitAreaGoodShown) {
				if (this.moveable) {
					this.detachChild(towerRangeGood);
				} else {
					scene.detachChild(towerRangeGood);
				}
				hitAreaGoodShown = false;
			}
			if (hitAreaBadShown) {
				if (this.moveable) {
					this.detachChild(towerRangeBad);
				} else {
					scene.detachChild(towerRangeBad);
				}
				hitAreaBadShown = false;
			}
		}
		hitAreaShown = showHitArea;
	}

	/** tells us if the tower can be placed where it is */
	public boolean hasPlaceError() {
		return this.placeError;
	}

	/**
	 * function for determining the distance to another sprite
	 * 
	 * @param s sprite you want the distance to
	 * @return the distance to said sprite
	 */
	public double distanceTo(Enemy s) {
		return Math.sqrt(Math.pow(this.getMidX() - s.getMidX(), 2) + Math.pow(this.getMidY() - s.getMidY(), 2));
	}

	/**
	 * Gives you the range of the tower, based on the size of the towerRangeGood circle
	 * 
	 * @return half the height of the towerRangeGood circle
	 */
	public float maxRange() {
		return this.towerRangeGood.getHeight() / 2.f;
	}

	public Vector2 getPosition() {
		return new Vector2(this.getX(), this.getY());
	}

	public float getMidX() {
		return this.getX() + this.getWidth() / 2;
	}

	public float getMidY() {
		return this.getY() + this.getHeight() / 2;
	}

	public void removeBullet(Projectile b) {
		arrayBullets.remove(b);
	}

	/** returns which column the tower is in (between 0 for the first column, and 14 for the last column) */
	public int getCol() {
		return TowerTest.getColFromX(this.getX());
	}

	/** returns which row the tower is in (between 0 for the first row, and 6 for the last row) */
	public int getRow() {
		return TowerTest.getRowFromY(this.getY());
	}
}
