package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.util.Log;

public class TowerRange extends Sprite{
	//I am Enemy class
	public float x,y;
	VertexBufferObjectManager vbom;
	private static String texture;
	//TODO Add waypoints  as ArrayList. make move to waypoint, set waypoint, addWaypoint functions.

	/**
	 * Create a new enemy
	 * @param pX
	 * @param pY
	 * @param pTextureRegion
	 * @param tvbom
	 */
	public TowerRange(float pX, float pY, TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pTextureRegion,tvbom);
		vbom = tvbom;
		x=pX; //some x n y of the enemy
		y=pY;
	}
	
	/**
	 * This function sets the texture for the tower type and returns a Texture Region to preload the texture.
	 * @param tm Texture manager; usually passed in as this.getTextureManager()
	 * @param c Context; usually passed in as this
	 * @return TextureRegion to load
	 */
	public static TextureRegion loadSprite(TextureManager tm, Context c, String strtex){
		TextureRegion tr;
		Log.i("Location:","Enemy loadSprite");
		BitmapTextureAtlas towerImage;
		towerImage = new BitmapTextureAtlas(tm,512,512);
		texture = strtex;
		tr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(towerImage, c, texture, 0, 0);
		tm.loadTexture(towerImage);
		return tr;
	}
}