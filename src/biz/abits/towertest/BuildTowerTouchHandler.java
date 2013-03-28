package biz.abits.towertest;
//TODO fix hand off of touch event from HUD to Scene.
//it breaks as soon as your move off the buildBasiTower sprite
import java.util.ArrayList;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;
/**
 * Used to build a tower when dragged off of the HUD
 *  implements IOnAreaTouchListener
 * @author abinning
 *
 */
public class BuildTowerTouchHandler implements IOnAreaTouchListener{
	//used to determine if they've moved far enough to start panning (o.w., they might want to tap on the tower instead)
	double distTraveled = 0;
	float lastX = 0;
	float lastY = 0;
	float firstX = 0;
	float firstY = 0;
	float startingOffsetX = 0;
	float startingOffsetY = 0;
	
	TouchEvent firstTouchEvent = null;
	/** Tells us if we can create a new tower, false = no, not allowed, true = yes, we can make a tower */
	boolean createNewTower;
	boolean showHitArea;
	boolean currentlyDragging = false;
	Tower tw;
	Scene scene;
	//Scene hud;
	//float touchX, touchY;
	Tower buildTower;
	ArrayList<Tower> arrayTower;
	TextureRegion bulletTexture;
	TextureRegion towerTexture;
	TextureRegion hitAreaTextureGood;
	TextureRegion hitAreaTextureBad;
	VertexBufferObjectManager tvbom;
	/**
	 * Used to build a tower when dragged off of the HUD
	 * @param bt the buildTower button (tower type)
	 * @param s Scene
	 * @param creds reference to credits
	 * @param al array list to add new tower to
	 * @param btex bullet TextureRegion for tower
	 * @param ttex Tower TextureRegion
	 * @param vbom VertexBufferObjectManager
	 * @param hagtex TextureRegion for tower
	 * @param habtex TextureRegion for tower
	 */
	public BuildTowerTouchHandler(Tower bt, Scene s, long creds, ArrayList<Tower> al, TextureRegion hagtex, TextureRegion habtex, TextureRegion btex, TextureRegion ttex,VertexBufferObjectManager vbom){ //Scene h, 
		scene = s;
		//hud = h;
		buildTower = bt;
		arrayTower = al;
		bulletTexture = btex;
		towerTexture = ttex;
		tvbom = vbom;
		hitAreaTextureGood = hagtex;
		hitAreaTextureBad = habtex;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		//touchDuration = event.getEventTime() - event.getDownTime();
		if (pSceneTouchEvent.isActionDown()) { createNewTower = true; }
		if (pSceneTouchEvent.isActionUp()) {
			tw.setHitAreaShown(scene, false); //note: we MUST hide the hit area BEFORE setting moveable to false!
			tw.moveable = false;
			createNewTower = true;
			if (tw.hasPlaceError() ||  TowerTest.credits < buildTower.getCredits()) {
				//refund credits and remove tower, because they can't place it where it is
				scene.detachChild(tw);
				arrayTower.remove(tw);
			} else {
				float newX = TowerTest.sceneTransX(pSceneTouchEvent.getX()) - tw.getXHandleOffset();
				float newY = TowerTest.sceneTransY(pSceneTouchEvent.getY()) - tw.getYHandleOffset();
				final TMXTile tmxTile = TowerTest.tmxLayer.getTMXTileAt(newX, newY);
				tmxTile.setGlobalTileID(TowerTest.mTMXTiledMap, 31);
				//remove the credits, since we're placing it here
				TowerTest.addCredits(-buildTower.getCredits());
			}
			//if location is good continue, else destroy tower and refund cost
			return true;
		} else if (pSceneTouchEvent.isActionMove()) {
			if(createNewTower){
				//This is the part that creates the tower when you hit the "creation" tower
				createNewTower = false;
				float newX = TowerTest.sceneTransX(pSceneTouchEvent.getX() + startingOffsetX) - buildTower.getXHandleOffset();
				float newY = TowerTest.sceneTransY(pSceneTouchEvent.getY() + startingOffsetY) - buildTower.getYHandleOffset();
				tw = new Tower(scene, bulletTexture,newX,newY,96,96,towerTexture, hitAreaTextureGood, hitAreaTextureBad,tvbom)
				{
					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
						//TODO add code for upgrades, better make a separate class for it, perhaps contained within the Tower class
						//TODO there is a problem, if you drag from a tower, it ALWAYS acts like you started dragging from the last tower
						if (pSceneTouchEvent.isActionDown()) {
							if (!currentlyDragging) {
								lastX = pSceneTouchEvent.getX();
								lastY = pSceneTouchEvent.getY();
								firstX = lastX;
								firstY = lastY;
								distTraveled = 0;
								showHitArea = true;
								//Log.e("Jared","I just set showHitArea to TRUE!");
								currentlyDragging = true;
								firstTouchEvent = pSceneTouchEvent; //back it up
								return true;
							} else {
								Log.e("Jared","I happened!");
								return true;
							}
						} else if (pSceneTouchEvent.isActionMove()) {
							distTraveled += Math.sqrt((Math.pow(lastX - pSceneTouchEvent.getX(),2))+(Math.pow(lastY - pSceneTouchEvent.getY(),2)));
							//store x and y for next move event
							lastX = pSceneTouchEvent.getX();
							lastY = pSceneTouchEvent.getY();
							if (distTraveled < TowerTest.TOWER_HEIGHT) {
								return true; //tell it we handled the touch event, because they haven't gone far enough (should be true)
							} else {
								if (showHitArea){ //that means it's the first time we've ran this, so..
									//pSceneTouchEvent.obtain(firstTouchEvent.getX(), firstTouchEvent.getY(), firstTouchEvent.getAction(), firstTouchEvent.getPointerID(), firstTouchEvent.getMotionEvent());
									startingOffsetX = firstX - lastX;
									startingOffsetY = firstY - lastY;
									TowerTest.currentXoffset = lastX-firstX;
									TowerTest.currentYoffset = lastY-firstY;
									//Log.e("Jared", "I modified my touch event!");
								}
								//Log.e("Jared","I just set showHitArea to FALSE!");
								showHitArea = false;
								return false; //pass it through if it's already too far
							}
						} else if (pSceneTouchEvent.isActionUp()) {
							TowerTest.currentXoffset = 0;
							TowerTest.currentYoffset = 0;
							if (showHitArea) {
								//Log.e("Jared","showHitArea is "+showHitArea);
								this.setHitAreaShown(scene, !this.getHitAreaShown()); //toggle hit area circle
								currentlyDragging = false;
								//Log.e("Jared", "Done dragging show it");
								return true;
								//do upgrade
							} else {
								//NOT Upgrading Tower, they were panning around
								//Log.e("Jared","I just set showHitArea to FALSE!");
								showHitArea = false;
								//Log.e("Jared", "Done dragging");
								currentlyDragging = false;
								return false;
							}
						} else {
							//Log.e("Jared","I just set showHitArea to FALSE!");
							showHitArea = false;
							return false;
						}
					}
				}; //end of tower definition
				
				
				tw.checkClearSpotAndPlace(scene, newX, newY);
				tw.setHitAreaShown(scene, true);
				arrayTower.add(tw); // add to array
				scene.registerTouchArea(tw); // register touch area , so this allows you to drag it
				scene.attachChild(tw); // add it to the scene
			}else if(tw.moveable){
				//This moves it to it's new position whenever they move their finger
				float newX = TowerTest.sceneTransX(pSceneTouchEvent.getX()) - tw.getXHandleOffset();
				float newY = TowerTest.sceneTransY(pSceneTouchEvent.getY()) - tw.getYHandleOffset();
				tw.checkClearSpotAndPlace(scene, newX, newY);
			}	
			return true;
		}
		return true;
	}
}
