package biz.abits.towertest;
//TODO fix hand off of touch event from HUD to Scene.
//it breaks as soon as your move off the buildBasiTower sprite
import java.util.ArrayList;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
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
	double distTraveled = 0;
	float lastX = 0;
	float lastY = 0;
	boolean createNewTower;
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
			tw.moveable = false;
			createNewTower = true;
			tw.setHitAreaShown(scene, false);
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
				float newX = TowerTest.sceneTransX(pSceneTouchEvent.getX()) - buildTower.getXHandleOffset();
				float newY = TowerTest.sceneTransY(pSceneTouchEvent.getY()) - buildTower.getYHandleOffset();
				tw = new Tower(scene, bulletTexture,newX,newY,96,96,towerTexture, hitAreaTextureGood, hitAreaTextureBad,tvbom)
				{
					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
						//TODO add code for upgrades, better make a separate class for it, perhaps contained within the Tower class
						if (pSceneTouchEvent.isActionDown()) {
							lastX = pSceneTouchEvent.getX();
							lastY = pSceneTouchEvent.getY();
							distTraveled = 0;
						} else if (pSceneTouchEvent.isActionMove()) {
							distTraveled += Math.sqrt((Math.pow(lastX - pSceneTouchEvent.getX(),2))+(Math.pow(lastY - pSceneTouchEvent.getY(),2)));
							lastX = pSceneTouchEvent.getX();
							lastY = pSceneTouchEvent.getY();
							if (distTraveled < TowerTest.TOWER_HEIGHT) {
								//Log.i("Location:","Holding it in until they go far enough outside");
								return true;
							} else {
								//Log.i("Location:","Passing touch through");
								return false;//pass it through if it's already too far
							}
						} else if (pSceneTouchEvent.isActionUp()) {
							if (distTraveled < TowerTest.TOWER_HEIGHT) {
								//do upgrade
								Log.i("Location:","Upgrading Tower " + distTraveled);
								this.setHitAreaShown(scene, !this.getHitAreaShown());
							} else {
								//Log.i("Location:","NOT Upgrading Tower" + distTraveled);
							}
						}
						return true;
					}
				};
				tw.setHitAreaShown(scene, true);
				arrayTower.add(tw); // add to array
				scene.registerTouchArea( tw); // register touch area , so this allows you to drag it
				scene.attachChild( tw); // add it to the scene
			}else if(tw.moveable){
				//This moves it to it's new position whenever they move their finger
				float newX = TowerTest.sceneTransX(pSceneTouchEvent.getX()) - tw.getXHandleOffset();
				float newY = TowerTest.sceneTransY(pSceneTouchEvent.getY()) - tw.getYHandleOffset();
				final TMXTile tmxTile = TowerTest.tmxLayer.getTMXTileAt(newX, newY);
				final TMXProperties<TMXTileProperty> tmxTileProperties = TowerTest.mTMXTiledMap.getTMXTileProperties(tmxTile.getGlobalTileID());  
				//final Rectangle currentTileRectangle = new Rectangle(0, 0, TowerTest.mTMXTiledMap.getTileWidth(), TowerTest.mTMXTiledMap.getTileHeight(), this.getVertexBufferObjectManager());
		      	//Snaps tower to tile
				if (TowerTest.enableSnap) {
					newX = tmxTile.getTileX();
					newY = tmxTile.getTileY();
				}			
				if(tmxTileProperties.containsTMXProperty("Collidable", "False" ))
				{
					tw.setTowerPlaceError(scene, true);
				} else {
					tw.setTowerPlaceError(scene, false);
				}
				tw.setPosition(newX, newY);
			}	
			return true;
		}
		return true;
	}
}
