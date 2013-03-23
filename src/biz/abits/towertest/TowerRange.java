package biz.abits.towertest;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;

public class TowerRange extends Sprite{
	private int zIndex = 2000; //show above the towers
	private static String texture;

	/**
	 * Create a new enemy
	 * @param pX
	 * @param pY
	 * @param pTextureRegion
	 * @param tvbom
	 */
	public TowerRange(float pX, float pY, TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pTextureRegion,tvbom);
		this.setZIndex(zIndex);
	}
	
	/**
	 * This function sets the texture for the tower type and returns a Texture Region to preload the texture.
	 * @param tm Texture manager; usually passed in as this.getTextureManager()
	 * @param c Context; usually passed in as this
	 * @param strtex; the texture to put (bad/good range)
	 * @return TextureRegion to load
	 */
	public static TextureRegion loadSprite(TextureManager tm, Context c, String strtex){
		TextureRegion tr;
		BitmapTextureAtlas towerImage;
		towerImage = new BitmapTextureAtlas(tm,512,512);
		texture = strtex;
		tr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(towerImage, c, texture, 0, 0);
		tm.loadTexture(towerImage);
		return tr;
	}
}