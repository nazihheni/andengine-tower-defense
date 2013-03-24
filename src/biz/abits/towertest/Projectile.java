package biz.abits.towertest;

import java.util.ArrayList;
import java.util.Vector;

import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;

import android.content.Context;
import android.util.Log;
//TODO clean up for use in Tower Class
//TODO extend GenericPool https://jimmaru.wordpress.com/2012/05/19/jimvaders-my-own-invaders-clone-thingie-tutorial/ or make spritebatch or both
public class Projectile extends Sprite{
        //I am Enemy class
        Enemy target;
        Tower source;
        public final static float speed = 300f; //movement speed higher is faster (distance to move per update)
        public MoveByModifier trajectory;
        public MoveByModifier targetTrajectory;
        VertexBufferObjectManager vbom;
        public static String texture = "bullet.png";

        public Projectile(float pX, float pY, float pWidth, float pHeight,
                        TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {

                super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
                vbom = tvbom;
        }

        /**
         * Set the target location this Projectile is traveling to
         * @param Tower t, source bullet came from
         * @param Enemy t, target we're shooting at 
         */
        public void setTarget(Tower t, Enemy e){
        	target = e;
            source = t;
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

        public void shoot() {
        	
        	/*
        	//to predict where the enemy will be, we need a parametric equation for the enemy's position
        	//Xt = Xo+Enemy.speed*t
        	//         (1)
        	//Pt =  
        	
        	double d0 = source.distanceTo(target);
        	double dy = Math.abs(source.getMidY() - target.getMidY());//vertical distance from tower to enemy
        	//Enemy.speed;
        	//Projectile.speed;
        	
        			
        	//This solves for how long it will take until the bullet hits the target
        	double t1 = ( (Math.sqrt(Math.pow(Projectile.speed,2) * Math.pow(d0, 2) - Math.pow(dy, 2) * Math.pow(Enemy.speed,2)) - 
        			Math.sqrt(Math.pow(d0, 2) - Math.pow(dy, 2)) * Enemy.speed) / 
        			( Math.pow(Projectile.speed,2) - Math.pow(Enemy.speed, 2) ) );
        	double t2 = ( -(Math.sqrt(Math.pow(Projectile.speed,2) * Math.pow(d0, 2) - Math.pow(dy, 2) * Math.pow(Enemy.speed,2)) + 
        			Math.sqrt(Math.pow(d0, 2) - Math.pow(dy, 2)) * Enemy.speed) / 
        			( Math.pow(Projectile.speed,2) - Math.pow(Enemy.speed, 2) ) );
        	
        	double dx = Math.sqrt(Math.pow(Math.sqrt(t1 - Math.sqrt(Math.pow(d0, 2)-Math.pow(dy, 2))),2)+Math.pow(dy, 2));
        	
        	Log.e("Jared","t1 "+t1);
        	Log.e("Jared","t2 "+t2);
        	Log.e("Jared","dx "+dx);
        	
        	
        	/*
        	Vector2 totarget = target.getPosition().add(source.getPosition());
        	float a = target.getVelocity().dot(target.getVelocity()) - (Projectile.speed * Projectile.speed);
        	float b = 2 * target.getVelocity().dot(totarget);
        	float c = totarget.dot(totarget);
        	float p = -b / (2 * a);
        	float q = (float)Math.sqrt((b * b) - 4 * a * c) / (2 * a);
        	float t1 = p - q;
        	float t2 = p + q;
        	float t;
        	if (t1 > t2 && t2 > 0) {
        	    t = t2;
        	} else {
        	    t = t1;
        	}
        	Vector2 aimSpot = target.getPosition().add(target.getVelocity().mul(t));
        	Vector2 bulletPath = aimSpot.sub(source.getPosition());
        	float timeToImpact = bulletPath.len() / Projectile.speed;//speed must be in units per second
        	*/
        	
        	
        	//old code
        	float dY = target.getMidY() - this.getMidY(); // some calc about how far the bullet can go, in this case up to the enemy
        	float dX = target.getMidX() - this.getMidX();//+(Math.abs(gY)/Enemy.speed/Projectile.speed);
        	float dist = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
        	//D=r*t
        	//therefore t = D/r
        	trajectory = new MoveByModifier(dist/Projectile.speed, dX, dY);
        	this.registerEntityModifier(trajectory);

        }
        
        /**
         * Stops this bullet if it is in motion still
         */
        public void stop(Scene scene, ArrayList<Projectile> arrayBullets) {
            this.unregisterEntityModifier(trajectory);
                scene.detachChild(this);
                arrayBullets.remove(this);
        }
        
        public void freeze() {
            this.unregisterEntityModifier(trajectory);
        }
        
        /**
         * Tells you if the bullet has reached the end of it's trajectory
         * @return
         */
        public boolean isDone() {
                return this.trajectory.isFinished();
                //return (targetX == x && targetY == y);
        }

        public float getMidX() {
    		return this.getX() + this.getWidth()/2;
    	}
    	
    	public float getMidY() {
    		return this.getY() + this.getHeight()/2;
    	}
}