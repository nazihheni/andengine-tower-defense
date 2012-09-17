package biz.abits.towertest;

import java.util.ArrayList;
import java.util.Random;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.util.Log;

@SuppressWarnings("unused")
public class TowerTest extends SimpleBaseGameActivity implements IOnSceneTouchListener{
	//I am Main class//
	//TODO use accelerometer to pan screen around
	//TODO Use SpriteBatch class for bullets, enemies, towers.
	
	//for use in situations that 'this' is not accessible
	TowerTest self = this;
	//========================================
	//	Create Camera and Scenes
	///=======================================
	//int CAMERA_WIDTH = 800;
	//int CAMERA_HEIGHT = 480;
	int CAMERA_WIDTH = 1280;
	int CAMERA_HEIGHT = 720;
	Camera camera;
	Scene scene;
	private TMXTiledMap mTMXTiledMap;
	
	//========================================
	//			Tower in Arrays
	//========================================
	BitmapTextureAtlas towerImage;
	TextureRegion towerTexture;
	ArrayList<Tower> arrayTower;
	Tower tw; // this is Tower class and its only used when creating towers on touch event
	Tower buildTower; //a basic Tower Sprite used to build towers 
	
	//========================================
	//		 The Bullet in Array
	//========================================
	BitmapTextureAtlas bulletImage;
	TextureRegion bulletTexture;
	ArrayList<Sprite> arrayBullet;
	
	//========================================
	//		 The Enemy and Array
	//========================================
	BitmapTextureAtlas enImage;
	TextureRegion enTexture;
	Enemy enemy;
	ArrayList<Enemy> arrayEn;
	
	//========================================
	//		 TMXTile Map Player
	//========================================	
	private BitmapTextureAtlas playerImage;
	private TiledTextureRegion playerTexture;
	
	//========================================
	//		Others
	//========================================
	// for touches
	float touchX; 
	float touchY;
	long touchDuration;
	
	// Enemy location // updated real time in a loop
	float targetX;
	float targetY;
	
	Handler TIMER_ONE; // our thread
	
	//==== just for testing outputs ====// never used
	BitmapTextureAtlas fontTexture;
	Font font;
	//ChangeableText fpsFont;

	    @Override
	    public EngineOptions onCreateEngineOptions() {
			Log.i("Location:","onCreateEngineOptions");
			//=================================================================================//
			//								Setup Camera
			//================================================================================//			
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);

			CAMERA_HEIGHT = metrics.heightPixels;
			CAMERA_WIDTH = metrics.widthPixels;
			
			camera = new Camera(0,0,CAMERA_WIDTH,CAMERA_HEIGHT);
			EngineOptions mEngine = new EngineOptions(true,ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),camera);
			mEngine.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
			return mEngine;
	    }

	    @Override
	    protected void onCreateResources() {
			Log.i("Location:","onCreateResources");
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			TextureManager tm =  this.getTextureManager();

			//=================================================================================//
			//								Load  Textures
			//================================================================================//
			//==== Towers
			towerTexture = Tower.loadSprite(this.getTextureManager(),this);
			//==== Projectiles
			bulletTexture = Projectile.loadSprite(this.getTextureManager(),this);
			//==== Enemies
			enTexture = Enemy.loadSprite(this.getTextureManager(),this);
			
			//=================================================================================//
			//								Load Player Textures
			//================================================================================//
			//==== Default Map 		
			//TODO sprite sheet and make animated
			/*playerImage = new BitmapTextureAtlas(this.getTextureManager(), 72, 128, TextureOptions.DEFAULT);
			playerTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerImage, this, "player.png", 0, 0, 3, 4);
			playerImage.load();
			*/
			//=================================================================================//
			//								Load Sounds
			//================================================================================//
			//==== Default Map 	
			SoundFactory.setAssetBasePath("mfx/");
			Tower.loadSound(this.mEngine.getSoundManager(), this);
			//==== for text
			fontTexture = new BitmapTextureAtlas(tm,256, 256,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			//font = FontFactory.create(this.getFontManager(), tm,fontTexture, Typeface.DEFAULT, 40, true, Color.RED);
			font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40);
			font.load(); 
	    }
	
	    @Override
	    protected Scene onCreateScene() {
			Log.i("Location:","onCreateScene");
			this.mEngine.registerUpdateHandler(new FPSLogger());
			scene = new Scene();
			
			//=====================================
			//		TMXTileMap
			//=====================================
			Log.i("Location:","TMXMap block");
			try {
				final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
					@Override
					public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
						/* We are going to count the tiles that have the property "cactus=true" set. */
						if(pTMXTileProperties.containsTMXProperty("cactus", "true")) {
							;//TMXTiledMapExample.this.mCactusCount++;
						}
					}
				});
				//Load the Desert Map
				Log.i("Location:","TMXMap Loading...");
				this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/desert.tmx");
				//this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/test.tmx");
				Log.i("Location:","TMXMap Loaded");		
				//this is our update thread
				this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						;//Toast.makeText(TMXTiledMapExample.this, "Cactus count in this TMXTiledMap: " + TMXTiledMapExample.this.mCactusCount, Toast.LENGTH_LONG).show();
					}
				});
			} catch (final TMXLoadException e) {
				Debug.e(e);
			}

			final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
			scene.attachChild(tmxLayer);
			
			//=====================================
			//		Tower & Enemy stuff
			//=====================================
			arrayTower 	= new ArrayList<Tower>();
		//	arrayBullet	= new ArrayList<Sprite>(); //useless // we have array of bullets in Tower class
			arrayEn		= new ArrayList<Enemy>();

			Log.i("Location:","registerUpdateHandler");				
			scene.registerUpdateHandler(loop);
			scene.setTouchAreaBindingOnActionDownEnabled(true); //TODO check this is the right event/whatever
			scene.setOnSceneTouchListener(this);
			//A tower button to build other towers xcoord,ycoord,xsize,ysize
			buildTower = new Tower(bulletTexture,50,50,150,150,towerTexture,this.getVertexBufferObjectManager());
			scene.attachChild( buildTower); // add it to the scene
			scene.registerTouchArea( buildTower); // register touch area , so this allows you to drag it
			scene.setOnAreaTouchListener(new IOnAreaTouchListener() {
				@Override
				public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					boolean createNewTower = true;
					/*if (pSceneTouchEvent.isActionDown()) {
						//touchDuration = event.getEventTime() - event.getDownTime();
						touchX = pSceneTouchEvent.getX();
						touchY = pSceneTouchEvent.getY();
						//150,150 is the size of the Sprite
					    tw = new Tower(bulletTexture,touchX ,touchY,150,150,towerTexture,self.getVertexBufferObjectManager())
					    {
					    	@Override
					    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					    		tw.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					    		if (pSceneTouchEvent.isActionUp()) {
									tw.moveable = false;
									return true;
								}
					    		return true;
					        }
					   };
					   arrayTower.add(tw); // add to array
					   scene.registerTouchArea( tw); // register touch area , so this allows you to drag it
					   scene.attachChild( tw); // add it to the scene
					   return true;
					}*/ 
					if (pSceneTouchEvent.isActionUp()) {
						tw.moveable = false;
						createNewTower = true;
						return true;
					}
					if (pSceneTouchEvent.isActionMove()) {
						Log.i("Tower Moveable:",""+tw.moveable);
						if(createNewTower){
							createNewTower = false;
							touchX = pSceneTouchEvent.getX();
							touchY = pSceneTouchEvent.getY();
							//150,150 is the size of the Sprite
						    tw = new Tower(bulletTexture,touchX ,touchY,150,150,towerTexture,self.getVertexBufferObjectManager())
						    {
						    	@Override
						    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
						    		//TODO add code for upgrades
						    		if (pSceneTouchEvent.isActionDown()) {
						    			//do upgrade
						    			Log.i("Location:","Upgrading Tower");
						    		}
						    		return true;
						        }
						   };
						   arrayTower.add(tw); // add to array
						   scene.registerTouchArea( tw); // register touch area , so this allows you to drag it
						   scene.attachChild( tw); // add it to the scene
						}

						if(tw.moveable){
							tw.setPosition(pSceneTouchEvent.getX() - tw.getWidth() / 2, pSceneTouchEvent.getY() - tw.getHeight() / 2);
							touchX = pSceneTouchEvent.getX();
				        	touchY = pSceneTouchEvent.getY();
						}
						return true;
					}
					return true;
				}
				
			});
			add_enemy(this.getVertexBufferObjectManager()); //timer add enemy every amount of defined secs

			this.runOnUiThread(new Runnable() {
			    @Override
			    public void run() {
			    	TIMER_ONE = new Handler(); 
			    	TIMER_ONE_START(); 
			    }
			});

			return scene;
/*				Log.i("Location:","onCreateScene");
		    	Scene scene = new Scene();
		        scene.setBackground(new Background(0.09804f, 0.6274f, 0));
		        return scene;
*/		        
	    }

		@Override
		public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
			Log.i("Location:","onSceneTouchEvent");
/*
			if (pSceneTouchEvent.isActionDown()) {
				 //touchDuration = event.getEventTime() - event.getDownTime();
				touchX = pSceneTouchEvent.getX();
		        touchY = pSceneTouchEvent.getY();
		        //TODO check the 150x150 is this the collision size?
		        tw = new Tower(bulletTexture,touchX ,touchY,150,150,towerTexture,this.getVertexBufferObjectManager())
				 {
				  @Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					  tw.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
			         		                return true;
			        }
			    };
			   arrayTower.add(tw); // add to array
			   scene.registerTouchArea( tw); // register touch area , so this allows you to drag it
			   scene.attachChild( tw); // add it to the scene
				return true;
			} 

			if (pSceneTouchEvent.isActionUp()) {
				tw.moveable = false;			
				return true;
			}
			if (pSceneTouchEvent.isActionMove()) {
				touchX = pSceneTouchEvent.getX();
		        touchY = pSceneTouchEvent.getY();
				return true;
			}
*/
			return true;
	}
		

		
		IUpdateHandler loop = new IUpdateHandler() {
		    @Override
		    public void reset() {
		    }
		    @Override
		    public void onUpdate(float pSecondsElapsed) {
				//=================Code must go here=======================
		    	//this is free to do any other things you like!
		    	//code ends
		        }
		};
		
	public void TIMER_ONE_START(){
		// == THREAD STARTS	
		new Thread(new Runnable() {
			    @Override
				    public void run() {  while (true) {  try {  Thread.sleep(5); TIMER_ONE.post(new Runnable() {
					    @Override
						public void run(){
					    	// ======= BEGIN WRITING ========================================================================
					    	collision(); // run the <--collision every 5ms
					    	// == END WRITING ================================================================================
						} /* Code finishes*/ 
				    });
			} catch (Exception e) {}}}}).start(); /* Re-start the thread */ 
		// == THREAD ENDS
	}
	
	//TODO put in a thread
	public void collision(){
		//Lets Loop our array of enemies
		//for(Enemy enemy: arrayEn){

		for(int j = 0; j < arrayEn.size();j++){
			Enemy enemy = arrayEn.get(j);

			//enemy.setPosition(enemy.getX()+3/6f,enemy.getY());  //you can use to move enemy
			//Lets Loop our Towers
			//for(Tower tower: arrayTower){
			for(int k = 0; k < arrayTower.size(); k++){
				Tower tower = arrayTower.get(k);
					
				//check if they collide The size of the Tower is the range of the tower or something maybe
				//TODO, add physics for collision
				//if enemy is in tower range

				if(enemy.collidesWith(tower)){
					fire(tower,enemy);// call fire and pass the tower and enemy to fire
					//Log.i("Location:","Firing on enemy");
					//TODO find a way to end thread?
					break; // take a break
				}
			}
			enemy.move();
		}
	}

	
	public void fire(Tower tower,Enemy enemy){
		targetX = enemy.getX()+enemy.getWidth()/2; // simple get the enemy x,y and center it and tell the bullet where to aim and fire
		targetY = enemy.getY()+enemy.getHeight()/2;
		boolean fired = tower.fire(targetX, targetY,tower.getX()+tower.getWidth()/2,tower.getY()+tower.getHeight()/2); //Asks the tower to open fire and places the bullet in middle of tower
		if(fired){
			ArrayList<Sprite> getBullet = tower.getArrayList(); //gets bullets from Tower class where our bullets are fired from
	
			scene.attachChild(tower.getBulletSprite());
			//for(Sprite bullet : getBullet){
			for(int i = 0; i < getBullet.size(); i++){
				Sprite bullet;
				bullet = getBullet.get(i);
				
				if(bullet.collidesWith(enemy)){
					scene.detachChild(bullet); // no longer needed to be shown
					getBullet.remove(bullet);  // also remove it from array so we don't check it again
					if(enemy.takeDamage(100,"normal") < 1){
						scene.detachChild(enemy);
						//TODO play death animation enemy function pass scene to detach
					} 
					break; // take a break
				}
			}
		}
	}
	
	int allow_enemy = 20; // number of enemies to allow
	public void add_enemy(VertexBufferObjectManager vbom){
		final float delay = 2f; //delay between adding enemies
		final VertexBufferObjectManager tvbom = vbom;
		
		TimerHandler enemy_handler = new TimerHandler(delay,true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				//=================Code must go here=======================
				Log.i("allow_enemy:",""+allow_enemy);		
				Random a = new Random();
				//int x = a.nextInt(CAMERA_WIDTH-60)+20;
				//int y = a.nextInt(CAMERA_HEIGHT-60)+20;
				
				int x = 20;
				int y = CAMERA_HEIGHT/2;
				
				if(allow_enemy > 0){
					//TODO fix the last argument here
					enemy = new Enemy(x,y,enTexture,tvbom);
					scene.attachChild(enemy);
					arrayEn.add(enemy);
					allow_enemy--;
				}
				else{}
				//================= end of code==========================
			}
		} );

		getEngine().registerUpdateHandler(enemy_handler);
		
	}
		
//  END OF CLASS
}

	