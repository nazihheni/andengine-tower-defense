package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.util.Log;
//TODO clean up for use in Tower Class
//TODO extend GenericPool https://jimmaru.wordpress.com/2012/05/19/jimvaders-my-own-invaders-clone-thingie-tutorial/
public class Projectile extends Sprite{
	//I am Enemy class
	public float x,y;
	public float speed = 0.5f; //movement speed
	VertexBufferObjectManager vbom;
	public static String texture = "bullet.png";

	public Projectile(TextureRegion b,float pX, float pY, float pWidth, float pHeight,
			TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {

		super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
		vbom = tvbom;
		x=pX; //some x n y of the projectile
		y=pY;
	}

	/**
	 * This function sets the texture for the tower type and returns a Texture Region to preload the texture.
	 * @param tm Texture manager; usually passed in as this.getTextureManager()
	 * @param c Context; usually passed in as this
	 * @return TextureRegion to load
	 */
	public static TextureRegion loadSprite(TextureManager tm, Context c){
		TextureRegion tr;
		Log.i("Location:","Enemy loadSprite");
		BitmapTextureAtlas towerImage;
		towerImage = new BitmapTextureAtlas(tm,512,512);
		tr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(towerImage, c, texture, 0, 0);
		tm.loadTexture(towerImage);
		return tr;
	}
}