package biz.abits.towertest;
//TODO fix hand off of touch event from HUD to Scene.
//it breaks as soon as your move off the buildBasiTower sprite

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

/**
 * Used to build a tower when dragged off of the HUD
 *  implements IOnAreaTouchListener
 * @author abinning
 *
 */
public class PauseTouchHandler implements IOnAreaTouchListener{
	boolean createNewTower;
	Scene scene;
	//Scene hud;
	//float touchX, touchY;
	Rectangle pauseB;
	/**
	 * Used to build a tower when dragged off of the HUD
	 * @param pauseButton the buildTower button (tower type)
	 * @param s Scene
	 * @param creds reference to credits
	 * @param al array list to add new tower to
	 * @param btex bullet TextureRegion for tower
	 * @param ttex Tower TextureRegion
	 * @param vbom VertexBufferObjectManager
	 */
	public PauseTouchHandler(Rectangle pauseButton, Scene s){ //Scene h, 
		scene = s;
		//hud = h;
		pauseB = pauseButton;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		//touchDuration = event.getEventTime() - event.getDownTime();
		if (pSceneTouchEvent.isActionDown()) { TowerTest.togglePauseGame(); }
		if (pSceneTouchEvent.isActionUp()) { return true; }
		if (pSceneTouchEvent.isActionMove()) { return true; }
		return true;
	}				
}
