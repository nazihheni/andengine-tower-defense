/**
 * 
 */
package biz.abits.towertest;

import org.andengine.entity.scene.Scene;

/**
 * @author jared meadows Simple extension of the scene class that allows you to pause it
 */
public class PauseableScene extends Scene {

	public static boolean isPaused;

	/**
	 * 
	 */
	public PauseableScene() {
		super();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		if (PauseableScene.isPaused)
			return;
		super.onManagedUpdate(pSecondsElapsed);
	}

	public void setPaused(boolean p) {
		isPaused = p;
	}
}
