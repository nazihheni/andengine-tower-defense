package biz.abits.towertest;

import java.io.IOException;
import java.util.ArrayList;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.util.Log;

/**
 * Basic Tower class contains it's own projectiles and provides methods for firing
 *
 * @author Andrew Binning
 * @see Projectile
 * @see SplashTower
 */
public class Tower extends Sprite{
	//I am Tower class and have my own bullets //
	//TODO fire range, acquisition range, pattern/type.
	private static String texture = "tower.png";
	private long cooldown = 500; //in milliseconds | 1 sec = 1,000 millisec 
	private long credits = 50; //cost to build tower in credits
	private int level = 1; //level of tower
	private int maxLevel = 10; //level of tower
	public int damage = 100; //Tower damage
	public String damageType = "normal";
	private float cdMod = 0.5f;
	private long lastFire = 0;
	private static int total = 0; //total number of this type of tower
	TextureRegion tower;
	TextureRegion bullet;
	private static String strFire = "tower.ogg"; 
	private static Sound soundFire;
	float x,y;
	boolean moveable = true;
	Projectile SpriteBullet;
	//int speed = 500;
	VertexBufferObjectManager vbom;
	ArrayList<Projectile> arrayBullets; //may change to spritebatch
	//Body range = PhysicsFactory.createCircularBody();
 
	//constructor
	public Tower(TextureRegion b,float pX, float pY, float pWidth, float pHeight,
			TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
		vbom = tvbom;
		bullet = b; // we need bullet TextureRegion to make one
		x=pX; //some x n y of the tower
		y=pY;
		arrayBullets = new ArrayList<Projectile>(); // create a new ArrayList
		total++;
	}

	/**
	 * Fires projectiles
	 * check cooldown in milli seconds with: <br>
	 *   long elapsedTime = System.currentTimeMillis() - towerVar.getLastFire;
	 * @param targetX target attacking
	 * @param targetY target attacking
	 * @param tx location of projectile
	 * @param ty location of projectile
	 * @return boolean True if tower fired (created bullet sprite), else false
	 */
	public boolean fire(float targetX,float targetY,float tx,float ty){
		//TODO move bullet to mouth of cannon
		long elapsed = System.currentTimeMillis() - lastFire;
		//only fire if tower is off cool down
		if( elapsed > cooldown * cdMod && !moveable){ //not on cooldown, and not actively being placed
			SpriteBullet  = new Projectile(tx,ty, 10f, 10f, bullet,vbom);

			float gY =  targetY -  SpriteBullet.getY(); // some calc about how far the bullet can go, in this case up to the enemy
			float gX =  targetX - SpriteBullet.getX();
		
			MoveByModifier movMByod = new MoveByModifier(0.1f, gX,  gY);
			SpriteBullet.registerEntityModifier(movMByod);
			
			arrayBullets.add(SpriteBullet);
			lastFire = System.currentTimeMillis();
			//TODO check sound settings
			//soundFire.play();
			return true;
		}
		else return false;
	}
	
	/**
	 * Get the cool down milliseconds
	 * @return cool down in milliseconds
	 */
	public long getCD(){ return cooldown; }

	/**
	 * Get the cool down Modifier as a float to represent a percentage
	 * if( elapsed > cooldown * cdMod)
	 * @return cool down Modifier float
	 */
	public float getCDMod(){ return cdMod; }

	/**
	 * Set the cool down Modifier as a float to represent a percentage
	 * if( elapsed > cooldown * cdMod)
	 */
	public void setCDMod(long cdm){ cdMod = cdm; }

	/**
	 * This function sets the texture for the tower type and returns a Texture Region to preload the texture.
	 * @param tm Texture manager; usually passed in as this.getTextureManager()
	 * @param c Context; usually passed in as this
	 * @return TextureRegion to load
	 */
	public static TextureRegion loadSprite(TextureManager tm, Context c){
		TextureRegion tr;
		Log.i("Location:","Tower loadSprite");
		BitmapTextureAtlas towerImage;
		towerImage = new BitmapTextureAtlas(tm,512,512);
		tr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(towerImage, c, texture, 0, 0);
		tm.loadTexture(towerImage);
		return tr;
	}
	
	/**
	 * Loads the sound from mfx/
	 * @param sm SoundManager passed from engine
	 * @param act SimpleBaseGameActivity Base class (this) 
	 */
	public static void loadSound(SoundManager sm, SimpleBaseGameActivity act){
		try {
			soundFire = SoundFactory.createSoundFromAsset(sm, act, strFire);
		} catch (final IOException e) {  Debug.e(e);  }
	}
	
	public Sprite getBulletSprite(){
		return SpriteBullet; // our main class uses this to attach to the scene
	}
	
	public ArrayList<Projectile> getArrayList(){ 
		return arrayBullets; // our main class uses this to check bullets etc
	}
	
	/**
	 * Get to cost to build this tower
	 * @return build cost in credits
	 */
	public long getCredits(){ return credits; }

	/**
	 * Get Current tower level
	 * @return tower level
	 */
	public int getLevel(){ return level; }
	
	/**
	 * Upgrade tower one level
	 * @return Returns false if tower already at max level
	 */
	public boolean upgradeLevel(){ if(level == maxLevel)  return false; else level++; return true; }
	
}