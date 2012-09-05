package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


public class Tower extends Sprite{
	//I am Tower class and have my own bullets //
	//TODO fire range, acquisistion range, pattern/type.
	TextureRegion bullet;
	float x,y;
	Sprite SpriteBullet;
	int speed = 500;
	VertexBufferObjectManager vbom;
	ArrayList<Sprite> arrayBullets;
	
	public Tower(TextureRegion b,float pX, float pY, float pWidth, float pHeight,
			TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {
		super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
		vbom = tvbom;
		bullet =b; // we need bullet TextureRegoin to make one
		x=pX; //some x n y of the tower
		y=pY;
		arrayBullets = new ArrayList<Sprite>(); // create a new ArrayList
	}
	
	
	public void fire(float targetX,float targetY,float tx,float ty){
		
		SpriteBullet  = new Sprite(tx,ty, 10, 10, bullet,vbom);
		
		float gY =  targetY -  SpriteBullet.getY(); // some calc about how far the bullet can go, in this case up to the enemy
		float gX =  targetX - SpriteBullet.getX();
	
		MoveByModifier movMByod = new MoveByModifier(0.5f, gX,  gY);
		SpriteBullet.registerEntityModifier(movMByod);
		
		speed=500; // needs to be the same as abovespeed
		arrayBullets.add(SpriteBullet);
	}
	
	
	
	
	public Sprite getBulletSprite(){
		return SpriteBullet; // our main class uses this to attach to the scene
	}
	
	
	public ArrayList<Sprite> getArrayList(){
		return arrayBullets; // our main class uses this to check bullets etc
	}
	
	

}