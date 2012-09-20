package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.util.Log;
//TODO clean up for use in Tower Class
//TODO extend GenericPool https://jimmaru.wordpress.com/2012/05/19/jimvaders-my-own-invaders-clone-thingie-tutorial/ or make spritebatch or both
public class Projectile extends Sprite{
        //I am Enemy class
        public float x,y;
        public float targetX,targetY;
        public final float speed = 5f; //movement speed higher is faster
        public MoveByModifier trajectory;
        VertexBufferObjectManager vbom;
        public static String texture = "bullet.png";

        public Projectile(float pX, float pY, float pWidth, float pHeight,
                        TextureRegion pTextureRegion,VertexBufferObjectManager tvbom) {

                super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
                vbom = tvbom;
                x=pX; //some x n y of the projectile
                y=pY;
        }

        /**
         * Set the target location this Projectile is traveling to
         * @param tx
         * @param ty
         */
        public void setTarget(float tx, float ty){
                targetX = tx;
                targetY = ty;
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
                float gY =  targetY -  this.getY(); // some calc about how far the bullet can go, in this case up to the enemy
                float gX =  targetX - this.getX();
                trajectory = new MoveByModifier(1/this.speed, gX,  gY);
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
        
        /**
         * Tells you if the bullet has reached the end of it's trajectory
         * @return
         */
        public boolean isDone() {
                return this.trajectory.isFinished();
                //return (targetX == x && targetY == y);
        }
}