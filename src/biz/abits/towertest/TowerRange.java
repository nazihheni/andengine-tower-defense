package biz.abits.towertest;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;

public class TowerRange extends Sprite {
	private int zIndex = 2000; // show above the towers
	private static String texture;

	/**
	 * Create a new enemy
	 * 
	 * @param pX
	 * @param pY
	 * @param pTextureRegion
	 * @param tvbom
	 */
	public TowerRange(float pX, float pY, TextureRegion pTextureRegion, VertexBufferObjectManager tvbom) {
		super(pX, pY, pTextureRegion, tvbom);
		this.setZIndex(zIndex);
	}

}