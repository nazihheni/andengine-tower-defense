package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Enemy extends Sprite{
	//I am Enemy class
	public float x,y;
	float speed = 1.0f; //movement speed
	VertexBufferObjectManager vbom;
	public String texture = "enemy.png";

	public Enemy(TextureRegion b,float pX, float pY, float pWidth, float pHeight,
			TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {

		super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
		vbom = tvbom;
		x=pX; //some x n y of the tower
		y=pY;
	}
}