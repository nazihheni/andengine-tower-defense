package biz.abits.towertest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
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

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import android.content.Context;
import android.content.Entity;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

@SuppressWarnings("unused")
public class TowerTest extends SimpleBaseGameActivity implements IOnSceneTouchListener, IScrollDetectorListener,
		IPinchZoomDetectorListener {
	// I am Main class//
	// TODO use accelerometer to pan screen around
	// TODO Use SpriteBatch class for bullets, enemies, towers.

	// For Snapping tower to a grid
	public static boolean enableSnap = true;
	public static TMXLayer tmxLayer;

	// for use in situations that 'this' is not accessible
	TowerTest self = this;
	// ========================================
	// Create Camera and Scenes
	// /=======================================
	// int CAMERA_WIDTH = 800;
	// int CAMERA_HEIGHT = 480;
	/** maximum they can zoom in = 4 */
	public static float MAX_ZOOM = 4f;
	/** maximum they can zoom out = 0.5 */
	public static float MIN_ZOOM = 0.5f;
	public static int CAMERA_WIDTH = 1280;
	public static int CAMERA_HEIGHT = 720;
	public static int TOWER_WIDTH = 96;
	public static int TOWER_HEIGHT = 96;
	private static ZoomCamera zoomCamera;
	/** used to offset the pan to adjust for panning from a tower */
	public static float currentXoffset = 0;
	/** used to offset the pan to adjust for panning from a tower */
	public static float currentYoffset = 0;
	// private Camera camera;

	private SurfaceScrollDetector mScrollDetector;
	private PinchZoomDetector mPinchZoomDetector;
	private float mPinchZoomStartedCameraZoomFactor;

	private HUD hud;
	private Scene scene;
	private ProgressBar waveProgress; // add to wave class
	public static TMXTiledMap mTMXTiledMap;
	private ButtonSprite pauseButton;

	Point lStarts[] = { new Point(0, 0) }; // define where the enemies will start at
	Point lEnds[] = { new Point(15, 0) }; // define where the enemies will end at
	int[] waves = { 1, 2, 5, 10, 20, 40, 80 };

	private Level currentLevel = new Level(waves, lStarts, lEnds);

	// ========================================
	// Tower in Arrays
	// ========================================
	BitmapTextureAtlas towerImage;
	TextureRegion towerTexture;
	ArrayList<Tower> arrayTower;
	/** a basic Tower in the HUD Sprite used to build towers */
	Tower buildBasicTower;
	// ========================================
	// The Bullet in Array
	// ========================================
	BitmapTextureAtlas bulletImage;
	TextureRegion bulletTexture;

	// ========================================
	// The Enemy and Array
	// ========================================
	BitmapTextureAtlas enImage;
	TextureRegion enTexture;
	TextureRegion hitAreaTextureGood;
	TextureRegion hitAreaTextureBad;
	TextureRegion texPause;
	TextureRegion texPlay;
	final String hitAreaTexGoodStr = "towerRangeGood.png";
	final String hitAreaTexBadStr = "towerRangeBad.png";
	final String texPauseStr = "pause.png";
	final String texPlayStr = "play.png";
	Enemy enemy;
	ArrayList<Enemy> arrayEn;

	// ========================================
	// TMXTile Map Player
	// ========================================
	private BitmapTextureAtlas playerImage;
	private TiledTextureRegion playerTexture;

	// ========================================
	// Others
	// ========================================
	// for touches
	float touchX;
	float touchY;
	long touchDuration;

	/** Enemy X location - updated real time in a loop */
	float targetX;
	/** Enemy Y location - updated real time in a loop */
	float targetY;

	Font font10;
	Font font20;
	Font font40;

	final FPSCounter fpsCounter = new FPSCounter();
	Text fpsText;
	static Text creditText;
	static long credits;
	final long initialCredits = 3000;
	// TODO Jared made this bigger, because it's tiny size made it difficult to
	// test
	static boolean paused = false;

	@Override
	public EngineOptions onCreateEngineOptions() {
		Log.i("Location:", "onCreateEngineOptions");
		// =================================================================================//
		// Setup Camera
		// ================================================================================//
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		CAMERA_HEIGHT = metrics.heightPixels;
		CAMERA_WIDTH = metrics.widthPixels;

		zoomCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		zoomCamera.setBounds(-CAMERA_WIDTH * 0.25f, -CAMERA_WIDTH * 0.25f, CAMERA_WIDTH * 1.25f, CAMERA_HEIGHT * 1.25f);
		zoomCamera.setBoundsEnabled(true);

		EngineOptions mEngine = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(),
				zoomCamera);

		if (MultiTouch.isSupported(this)) {
			if (MultiTouch.isSupportedDistinct(this))
				Toast.makeText(this, "MultiTouch detected Pinch Zoom will work properly!", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this,
						"MultiTouch detected, but your device has problems distinguishing between fingers",
						Toast.LENGTH_LONG).show();
		} else
			Toast.makeText(this, "Sorry your device does NOT support MultiTouch! Use Zoom Buttons.", Toast.LENGTH_LONG)
					.show();
		mEngine.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		return mEngine;
	}

	/**
	 * Load all game resources
	 */
	@Override
	protected void onCreateResources() {
		Log.i("Location:", "onCreateResources");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		TextureManager tm = this.getTextureManager();

		// =================================================================================//
		// Load Textures
		// ================================================================================//
		// ==== Towers
		towerTexture = Tower.loadSprite(this.getTextureManager(), this);
		// ==== Projectiles
		bulletTexture = Projectile.loadSprite(this.getTextureManager(), this);
		// ==== Enemies
		enTexture = Enemy.loadSprite(this.getTextureManager(), this);

		hitAreaTextureGood = TowerRange.loadSprite(this.getTextureManager(), this, hitAreaTexGoodStr);

		hitAreaTextureBad = TowerRange.loadSprite(this.getTextureManager(), this, hitAreaTexBadStr);

		texPause = TowerTest.loadSprite(this.getTextureManager(), this, texPauseStr);

		texPlay = TowerTest.loadSprite(this.getTextureManager(), this, texPlayStr);

		// =================================================================================//
		// Load Player Textures
		// ================================================================================//
		// ==== Default Map
		// TODO sprite sheet and make animated
		/*
		 * playerImage = new BitmapTextureAtlas(this.getTextureManager(), 72, 128, TextureOptions.DEFAULT); playerTexture = BitmapTextureAtlasTextureRegionFactory
		 * .createTiledFromAsset(playerImage, this, "player.png", 0, 0, 3, 4); playerImage.load();
		 */
		// =================================================================================//
		// Load Sounds
		// ================================================================================//
		// ==== Default Map
		SoundFactory.setAssetBasePath("mfx/");
		Tower.loadSound(this.mEngine.getSoundManager(), this);
		// =================================================================================//
		// Load Fonts
		// ================================================================================//
		/*
		 * final ITexture fontTexture = new BitmapTextureAtlas(this.getTextureManager(),256,256); mFont = FontFactory
		 * .createFromAsset(this.getFontManager(),fontTexture,this.getAssets (),"COMIC.TTF",18f,true,Color.WHITE);
		 */
		/*
		 * final ITexture strokeFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR); final ITexture strokeOnlyFontTexture = new
		 * BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		 * 
		 * this.font10 = new StrokeFont(this.getFontManager(), strokeFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 10, true, Color.BLACK, 2, Color.WHITE);
		 * this.font20 = new StrokeFont(this.getFontManager(), strokeFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 20, true, Color.BLACK, 2, Color.WHITE);
		 * this.font40 = new StrokeFont(this.getFontManager(), strokeFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, true, Color.BLACK, 2, Color.WHITE);
		 * //this.font20 = new StrokeFont(this.getFontManager(), strokeOnlyFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 20, true, Color.BLACK, 2,
		 * Color.WHITE, true);
		 */
		this.font10 = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
				TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 10);
		this.font20 = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
				TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 20);
		this.font40 = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
				TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40);

		this.font10.load();
		this.font20.load();
		this.font40.load();

	}

	/**
	 * Create Scene Create all scenes to use in game
	 */
	@Override
	protected Scene onCreateScene() {
		Log.i("Location:", "onCreateScene");
		this.mEngine.registerUpdateHandler(new FPSLogger());
		scene = new Scene();
		// HUD does not move with camera, it is stationary
		hud = new HUD();
		// number of enemies remaining
		waveProgress = new ProgressBar(zoomCamera, 20, 64, 100, 10, this.getVertexBufferObjectManager());
		waveProgress.setProgressColor(1.0f, 0.0f, 0.0f, 1.0f).setFrameColor(0.4f, 0.4f, 0.4f, 1.0f).setBackColor(0.0f,
				0.0f, 0.0f, 0.2f);
		// waveProgress.setMax(wave_size);
		zoomCamera.setHUD(hud);
		// zoomCamera.setHUD(waveProgress); //TODO fix this
		// =====================================
		// TMXTileMap
		// =====================================
		Log.i("Location:", "TMXMap block");
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(),
					TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(),
					new ITMXTilePropertiesListener() {
						@Override
						public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap,
								final TMXLayer pTMXLayer, final TMXTile pTMXTile,
								final TMXProperties<TMXTileProperty> pTMXTileProperties) {
						}
					});
			// Load the Desert Map
			Log.i("Location:", "TMXMap Loading...");
			TowerTest.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/grid.tmx");
			Log.i("Location:", "TMXMap Loaded");
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		tmxLayer = TowerTest.mTMXTiledMap.getTMXLayers().get(0);
		// tmxTileProperty =
		// this.mTMXTiledMap.getTMXTilePropertiesByGlobalTileID(0));
		scene.attachChild(tmxLayer);

		// final TMXObjectGroup tmxObjectGroup =
		// this.mTMXTiledMap.getTMXObjectGroups().get(0);
		// if(tmxObjectGroup.getTMXObjectGroupProperties().containsTMXProperty("type",
		// "collision"))

		// =====================================
		// Interface & HUD
		// =====================================
		this.mScrollDetector = new SurfaceScrollDetector(this);
		this.mPinchZoomDetector = new PinchZoomDetector(this);
		mPinchZoomDetector.setEnabled(true);
		/*
		 * {
		 * 
		 * @Override public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		 * this.mCamera.setZoomFactor(Math.min( Math.max(this.maxZoom, this.mPinchZoomStartedCameraZoomFactor pZoomFactor), 2)); }
		 * 
		 * @Override public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		 * this.mCamera.setZoomFactor(Math.min( Math.max(this.maxZoom, this.mPinchZoomStartedCameraZoomFactor pZoomFactor), 2)); } };
		 */

		this.mEngine.registerUpdateHandler(fpsCounter);
		// xcoord,ycoord,font,initial text?,length,vbom
		final Rectangle fpsMask = this.makeColoredRectangle(CAMERA_WIDTH - 100, 20, 20, 100, .8f, .8f, .8f, 1f);
		hud.attachChild(fpsMask);
		fpsText = new Text(CAMERA_WIDTH - 100, 20, this.font20, "FPS:", "FPS: xxx.xx".length(), this
				.getVertexBufferObjectManager());
		final Rectangle creditMask = this.makeColoredRectangle(20, 20, 40, 100, .8f, .8f, .8f, 1f);
		hud.attachChild(creditMask);

		// Pause button
		pauseButton = new ButtonSprite(TowerTest.CAMERA_WIDTH - 140, 20, texPause, this.getVertexBufferObjectManager(),
				pauseListener);
		hud.attachChild(pauseButton);
		hud.registerTouchArea(pauseButton);

		creditText = new Text(20, 20, this.font40, "$", 12, this.getVertexBufferObjectManager());

		// final Entity rectangleGroup = new Entity(CAMERA_WIDTH / 2,
		// CAMERA_HEIGHT / 2); //group shapes together

		// Initialize the credits they have to display it at first
		credits = initialCredits;
		creditText.setText("$" + credits);
		// maybe use this for HUD
		/*
		 * scene.registerUpdateHandler(new TimerHandler(1 / 10.0f, true, new ITimerCallback() {
		 * 
		 * @Override public void onTimePassed(final TimerHandler pTimerHandler) { //elapsedText.setText("Seconds elapsed: " +
		 * ChangeableTextExample.this.mEngine.getSecondsElapsedTotal()); fpsText.setText("FPS: " + new DecimalFormat("#.##").format(fpsCounter.getFPS()));
		 * creditText.setText("$" + credits); } }));
		 */

		// scene.attachChild(fpsText);
		// scene.attachChild(creditText);
		hud.attachChild(fpsText);
		hud.attachChild(creditText);

		// TODO setup build buttons in HUD

		// =====================================
		// Tower & Enemy stuff
		// =====================================
		arrayTower = new ArrayList<Tower>();
		// arrayBullet = new ArrayList<Sprite>(); //useless // we have array of
		// bullets in Tower class
		arrayEn = new ArrayList<Enemy>();

		Log.i("Location:", "registerUpdateHandler");
		scene.registerUpdateHandler(hudLoop);
		// hud.setOnSceneTouchListener(this); //TODO enable these two lines
		// hud.setTouchAreaBindingOnActionDownEnabled(true);
		// Register our update Handler
		scene.registerUpdateHandler(loop);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setOnSceneTouchListener(this);

		hud.setTouchAreaBindingOnActionDownEnabled(true);
		// A tower button to build other towers xcoord,ycoord,xsize,ysize
		buildBasicTower = new Tower(scene, bulletTexture, 150, 0, TOWER_WIDTH, TOWER_HEIGHT, towerTexture,
				hitAreaTextureGood, hitAreaTextureBad, this.getVertexBufferObjectManager());
		// TODO add to hud
		hud.attachChild(buildBasicTower);
		hud.registerTouchArea(buildBasicTower); // register touch area , so this
												// allows you to drag it
		// hud.setTouchAreaBindingEnabled(true);

		BuildTowerTouchHandler btth = new BuildTowerTouchHandler(buildBasicTower, scene, credits, arrayTower,
				hitAreaTextureGood, hitAreaTextureBad, bulletTexture, towerTexture, self.getVertexBufferObjectManager());
		hud.setOnAreaTouchListener(btth);
		// scene.setOnAreaTouchListener(btth);

		add_enemy(this.getVertexBufferObjectManager()); // timer add enemy every amount of defined secs
		return scene;
	}

	/**
	 * This gets called when the screen is touched
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		Log.i("Location:", "onSceneTouchEvent");
		this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

		if (this.mPinchZoomDetector.isZooming()) {
			this.mScrollDetector.setEnabled(false);
		} else {
			if (pSceneTouchEvent.isActionDown()) {
				this.mScrollDetector.setEnabled(true);
			}
			this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
		}
		/*
		 * if (pSceneTouchEvent.isActionDown()) { return true; }
		 * 
		 * if (pSceneTouchEvent.isActionUp()) { return true; } if (pSceneTouchEvent.isActionMove()) { return true; }
		 */
		return true;
	}

	OnClickListener pauseListener = new OnClickListener() {
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			togglePauseGame();
		}
	};

	IUpdateHandler loop = new IUpdateHandler() {
		@Override
		public void reset() {
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			// =================MAIN GAME LOOP=======================

			/*
			 * try { Thread.sleep(1); } catch (InterruptedException e) { // TODO Auto-generated catch block e.printStackTrace(); }
			 */

			collision(); // run the <--collision every update
			// fpsText.setText("FPS: " + new
			// DecimalFormat("#.##").format(fpsCounter.getFPS()));
			// code ends
		}
	};
	// TODO not sure these should be separate
	IUpdateHandler hudLoop = new IUpdateHandler() {
		@Override
		public void reset() {
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			// =================HUD LOOP=======================
			fpsText.setText("FPS: " + new DecimalFormat("#.##").format(fpsCounter.getFPS()));
			// code ends
		}
	};

	// TODO put in a thread
	public void collision() {
		// Lets Loop our array of enemies
		// ***************************************************************
		// TODO WE SHOULD PROBABLY MULTITHREAD THIS LOOP FO' SHIZZLE!!!!!!
		// ***************************************************************
		if (arrayEn.size() > 0) {
			for (int j = 0; j < arrayEn.size(); j++) {// iterate through the enemies
				Enemy enemy = arrayEn.get(j);
				// Lets Loop our Towers
				for (int k = 0; k < arrayTower.size(); k++) {// iterate through the towers
					Tower tower = arrayTower.get(k);
					// check if they are in range of the tower
					// TODO, add physics for collision
					if (tower.distanceTo(enemy) < tower.maxRange()) { // if(enemy.collidesWith(tower)){
						tower.fire(enemy, scene, arrayEn, this);// call fire and pass the tower and enemy to fire
						// Log.i("Location:","Firing on enemy");
						// TODO find a way to end thread?
						// break; //do NOT enable this line or it will only
						// allow ONE tower to fire!
					}
				}
			}
		}
	}

	/**
	 * Handles the adding of credits to the score
	 * 
	 * @param enCredits
	 */
	public static void addCredits(long enCredits) {
		credits += enCredits;
		creditText.setText("$" + credits);
		// update screen to reflect new score
	}

	// break this all out to a wave class, also use SpriteBatch
	int currentWaveNum = 0;
	int currentEnemyCount = 0;
	int currentDelayBetweenWaves = 0;

	public void add_enemy(VertexBufferObjectManager vbom) {
		final float delay = 3f; // delay between adding enemies
		final int delayBetweenWaves = 20;
		final VertexBufferObjectManager tvbom = vbom;
		TimerHandler enemy_handler = new TimerHandler(delay, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (!paused) {
					// =================Code must go here=======================
					// Log.i("allow_enemy:",""+allow_enemy);
					Random a = new Random();
					// int x = a.nextInt(CAMERA_WIDTH-60)+20;
					// int y = a.nextInt(CAMERA_HEIGHT-60)+20;

					float x = 20;
					float y = CAMERA_HEIGHT / 2;
					if (currentDelayBetweenWaves >= delayBetweenWaves) {
						currentEnemyCount = 0;
						currentDelayBetweenWaves = 0;
					}

					if (currentLevel.wave[currentWaveNum] > currentEnemyCount) {
						// TODO fix the last argument here
						enemy = new Enemy(x, y, 96, 96, enTexture, tvbom, currentLevel);
						enemy.setPathandMove(currentLevel.endLoc[0], TowerTest.this, tmxLayer);
						// TODO make it assign which end location based on the wave
						scene.attachChild(enemy);
						arrayEn.add(enemy);
						waveProgress.setProgress(1 - (float) (currentEnemyCount / currentLevel.wave[currentWaveNum]));
						Log.i("waveProg", "" + (float) currentEnemyCount / currentLevel.wave[currentWaveNum]);
						currentEnemyCount++;
					} else if (currentDelayBetweenWaves < delayBetweenWaves) {
						currentDelayBetweenWaves++;
					}
					// ================= end of code==========================
				}
			}
		});

		getEngine().registerUpdateHandler(enemy_handler);

	}

	/**
	 * Makes rectangles
	 * 
	 * @param pX x coord
	 * @param pY y coord
	 * @param pWidth width
	 * @param pHeight height
	 * @param pRed color value
	 * @param pGreen color value
	 * @param pBlue color value
	 * @param pAlpha color Alpha value
	 * @return
	 */
	private Rectangle makeColoredRectangle(final float pX, final float pY, final float pWidth, final float pHeight,
			final float pRed, final float pGreen, final float pBlue, final float pAlpha) {

		final Rectangle coloredRect = new Rectangle(pX, pY, pHeight, pWidth, this.getVertexBufferObjectManager());
		coloredRect.setColor(pRed, pGreen, pBlue, pAlpha);
		return coloredRect;
	}

	// =====================================
	// Pinch Zoom and Scroll stuff
	// =====================================
	// TODO establish limits
	// static float currentZoom = 1;

	/**
	 * Translates x coordinate from hud coordinates to scene coordinates (used for tower placement)
	 * 
	 * @param x X coordinate to be translated
	 * @return translated X coordinate
	 */
	public static float sceneTransX(float x) {
		float myZoom = zoomCamera.getZoomFactor();
		float myXOffset = zoomCamera.getCenterX() - TowerTest.CAMERA_WIDTH / 2 / myZoom;
		float newX = x / myZoom + myXOffset;
		return newX;
	}

	/**
	 * Translates y coordinate from hud coordinates to scene coordinates (used for tower placement)
	 * 
	 * @param y Y coordinate to be translated
	 * @return translated Y coordinate
	 */
	public static float sceneTransY(float y) {
		float myZoom = zoomCamera.getZoomFactor();
		float myYOffset = zoomCamera.getCenterY() - TowerTest.CAMERA_HEIGHT / 2 / myZoom;
		float newY = y / myZoom + myYOffset;
		return newY;
	}

	public static float getPanX() {
		return zoomCamera.getCenterX();
	}

	public static float getPanY() {
		return zoomCamera.getCenterY();
	}

	public static float getZoom() {
		return zoomCamera.getZoomFactor();
	}

	@Override
	public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX,
			final float pDistanceY) {
		final float zoomFactor = TowerTest.zoomCamera.getZoomFactor();
		// currentZoom = zoomFactor;
		// currentXoffset = pDistanceX;
		// currentYoffset = pDistanceY;
		TowerTest.zoomCamera.offsetCenter((-pDistanceX) / zoomFactor - currentXoffset * zoomFactor, (-pDistanceY)
				/ zoomFactor - currentYoffset * zoomFactor);
	}

	@Override
	public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX,
			final float pDistanceY) {
		final float zoomFactor = TowerTest.zoomCamera.getZoomFactor();
		// currentZoom = zoomFactor;
		// currentXoffset = pDistanceX;
		// currentYoffset = pDistanceY;
		TowerTest.zoomCamera.offsetCenter((-pDistanceX) / zoomFactor - currentXoffset * zoomFactor, (-pDistanceY)
				/ zoomFactor - currentYoffset * zoomFactor);
	}

	@Override
	public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX,
			final float pDistanceY) {
		final float zoomFactor = TowerTest.zoomCamera.getZoomFactor();
		// currentZoom = zoomFactor;
		// currentXoffset = pDistanceX;
		// currentYoffset = pDistanceY;
		TowerTest.zoomCamera.offsetCenter((-pDistanceX) / zoomFactor - currentXoffset * zoomFactor, (-pDistanceY)
				/ zoomFactor - currentYoffset * zoomFactor);
	}

	@Override
	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
		final float zoomFactor = TowerTest.zoomCamera.getZoomFactor();
		// currentZoom = zoomFactor;
		this.mPinchZoomStartedCameraZoomFactor = zoomFactor; // TODO
																// maximum/minimum
																// zoom factor
	}

	@Override
	public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent,
			final float pZoomFactor) {
		// currentZoom = pZoomFactor;
		// TowerTest.zoomCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor
		// * pZoomFactor);
		TowerTest.zoomCamera.setZoomFactor(Math.min(Math.max(TowerTest.MIN_ZOOM, this.mPinchZoomStartedCameraZoomFactor
				* pZoomFactor), TowerTest.MAX_ZOOM));

	}

	@Override
	public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent,
			final float pZoomFactor) {
		// currentZoom = pZoomFactor;
		// TowerTest.zoomCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor
		// * pZoomFactor);
		TowerTest.zoomCamera.setZoomFactor(Math.min(Math.max(TowerTest.MIN_ZOOM, this.mPinchZoomStartedCameraZoomFactor
				* pZoomFactor), TowerTest.MAX_ZOOM));
	}

	public void togglePauseGame() {
		paused = !paused;
		// freeze all bullets
		if (paused) {
			// pauseButton

			for (int k = 0; k < arrayTower.size(); k++) {// iterate through the
															// towers
				Tower tower = arrayTower.get(k);
				tower.freezeBullets(scene);
			}
			for (int k = 0; k < arrayEn.size(); k++) {// iterate through the
														// towers
				Enemy enemy = arrayEn.get(k);
				enemy.freeze();
			}
		} else {
			for (int k = 0; k < arrayTower.size(); k++) {// iterate through the
															// towers
				Tower tower = arrayTower.get(k);
				tower.resumeBullets(scene, arrayEn, this);
			}
			for (int k = 0; k < arrayEn.size(); k++) {// iterate through the
														// towers
				Enemy enemy = arrayEn.get(k);
				enemy.startMoving();
			}
		}
	}

	public static TextureRegion loadSprite(TextureManager tm, Context c, String strtex) {
		TextureRegion tr;
		BitmapTextureAtlas towerImage;
		towerImage = new BitmapTextureAtlas(tm, 512, 512);
		tr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(towerImage, c, strtex, 0, 0);
		tm.loadTexture(towerImage);
		return tr;
	}

	public static int getColFromX(float pX) {
		return Math.round(pX / mTMXTiledMap.getTileWidth());
	}

	public static int getRowFromY(float pY) {
		return Math.round(pY / mTMXTiledMap.getTileHeight());
	}

	// END OF CLASS
}