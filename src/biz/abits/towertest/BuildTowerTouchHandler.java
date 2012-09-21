package biz.abits.towertest;
//TODO fix hand off of touch event from HUD to Scene.
//it breaks as soon as your move off the buildBasiTower sprite
import java.util.ArrayList;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
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
	boolean createNewTower;
	Tower tw;
	long credits;
	Scene scene;
	float touchX, touchY;
	Tower buildTower;
	ArrayList<Tower> arrayTower;
	TextureRegion bulletTexture;
	TextureRegion towerTexture;
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
	 */
	public BuildTowerTouchHandler(Tower bt, Scene s, long creds, ArrayList<Tower> al, TextureRegion btex, TextureRegion ttex,VertexBufferObjectManager vbom){
		credits = creds;
		scene = s;
		buildTower = bt;
		arrayTower = al;
		bulletTexture = btex;
		towerTexture = ttex;
		tvbom = vbom;
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		//touchDuration = event.getEventTime() - event.getDownTime();
		if (pSceneTouchEvent.isActionDown()) { createNewTower = true; }
		if (pSceneTouchEvent.isActionUp()) {
			tw.moveable = false;
			createNewTower = true;
			//if location is good continue, else destroy tower and refund cost
			return true;
		}
		if (pSceneTouchEvent.isActionMove()) {
			if(createNewTower && credits >= buildTower.getCredits()){
				TowerTest.addCredits(-buildTower.getCredits());
				createNewTower = false;
				touchX = pSceneTouchEvent.getX();
				touchY = pSceneTouchEvent.getY();

				//150,150 is the size of the Sprite
			    tw = new Tower(bulletTexture,touchX ,touchY,150,150,towerTexture,tvbom)
			    {
			    	@Override
			    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			    		//TODO add code for upgrades, better make a separate class for it, perhaps contained within the Tower class
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
			}else if(tw.moveable){
				tw.setPosition(pSceneTouchEvent.getX() - tw.getWidth() / 2, pSceneTouchEvent.getY() - tw.getHeight() / 2);
				touchX = pSceneTouchEvent.getX();
	        	touchY = pSceneTouchEvent.getY();
			}
			return true;
		}
		return true;
	}				
}
