package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import android.content.Context;
import android.util.Log;
//TODO clean up for use in Tower Class
//TODO extend GenericPool https://jimmaru.wordpress.com/2012/05/19/jimvaders-my-own-invaders-clone-thingie-tutorial/ or make spritebatch or both

public class Projectile extends Sprite {
	// I am Enemy class
	Scene scene;
	Enemy target;
	Tower source;
	public final static float speed = 55f; // movement speed higher is faster
											// (distance to move per update)
	public MoveByModifier trajectory;
	public MoveByModifier targetTrajectory;
	VertexBufferObjectManager vbom;
	public static String texture = "bullet.png";

	public Projectile(float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion,
			VertexBufferObjectManager tvbom, Scene sc) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, tvbom);
		vbom = tvbom;
		scene = sc;
	}

	/**
	 * Set the target location this Projectile is traveling to
	 * 
	 * @param Tower t, source bullet came from
	 * @param Enemy t, target we're shooting at
	 */
	public void setTarget(Tower t, Enemy e) {
		target = e;
		source = t;
	}

	public void shoot(final ArrayList<Enemy> arrayEn, final BaseGameActivity myContext) {
		boolean hadError = false;

		// to predict where the enemy will be, we need a parametric equation for
		// the enemy's position
		// Xt = Xo+Enemy.speed*t
		// (1)
		// Pt =

		double dY;
		double dX;
		/*
		 * double d0 = source.distanceTo(target); dY = target.getMidY() - source.getMidY();//vertical distance from tower to enemy //Enemy.speed; //Projectile.speed;
		 * 
		 * 
		 * //This solves for how long it will take until the bullet hits the target double t1 = ( (Math.sqrt(Math.pow(Projectile.speed,2) * Math.pow(d0, 2) - Math.pow(dY, 2) *
		 * Math.pow(Enemy.speed,2)) - Math.sqrt(Math.pow(d0, 2) - Math.pow(dY, 2)) * Enemy.speed) / ( Math.pow(Projectile.speed,2) - Math.pow(Enemy.speed, 2) ) ); double t2 =
		 * ( -(Math.sqrt(Math.pow(Projectile.speed,2) * Math.pow(d0, 2) - Math.pow(dY, 2) * Math.pow(Enemy.speed,2)) + Math.sqrt(Math.pow(d0, 2) - Math.pow(dY, 2)) *
		 * Enemy.speed) / ( Math.pow(Projectile.speed,2) - Math.pow(Enemy.speed, 2) ) ); dX = Math.sqrt(Math.pow(Math.sqrt(t1 - Math.sqrt(Math.pow(d0, 2)-Math.pow(dY,
		 * 2))),2)+Math.pow(dY, 2));
		 * 
		 * t1 = (Math.sqrt((Math.pow(Projectile.speed,2) * Math.pow(d0, 2)) - (Math.pow(dY, 2) * Math.pow(Enemy.speed,2))) - Math.sqrt(Math.pow(d0, 2) - (Math.pow(dY, 2)) *
		 * Enemy.speed)) / ( Math.pow(Projectile.speed,2) - Math.pow(Enemy.speed, 2) ) ; //dX = Math.sqrt(Math.pow(Math.sqrt(t1 - Math.sqrt(Math.pow(d0, 2)-Math.pow(dY,
		 * 2))),2)+Math.pow(dY, 2)); dX = Enemy.speed*t1 - Math.sqrt( Math.pow(d0, 2)-Math.pow(dY, 2) );
		 * 
		 * t2 = 0;
		 * 
		 * 
		 * 
		 * Log.e("Jared","t1 "+t1); Log.e("Jared","t2 "+t2);
		 */

		/*
		 * Vector2 totarget = target.getPosition().add(source.getPosition()); float a = target.getVelocity().dot(target.getVelocity()) - (Projectile.speed * Projectile.speed);
		 * float b = 2 * target.getVelocity().dot(totarget); float c = totarget.dot(totarget); float p = -b / (2 * a); float q = (float)Math.sqrt((b * b) - 4 * a * c) / (2 *
		 * a); float t1 = p - q; float t2 = p + q; float t; if (t1 > t2 && t2 > 0) { t = t2; } else { t = t1; } Vector2 aimSpot =
		 * target.getPosition().add(target.getVelocity().mul(t)); Vector2 bulletPath = aimSpot.sub(source.getPosition()); float timeToImpact = bulletPath.len() /
		 * Projectile.speed;//speed must be in units per second
		 */

		// old code
		// float dY = target.getMidY() - this.getMidY(); // some calc about how
		// far the bullet can go, in this case up to the enemy
		// float dX = target.getMidX() -
		// this.getMidX();//+(Math.abs(gY)/Enemy.speed/Projectile.speed);
		// dY = aimSpot.y;
		// dX = aimSpot.x;
		dY = target.getMidY() - this.getMidY();
		dX = target.getMidX() - this.getMidX();

		double a = target.getXSpeed() * target.getXSpeed() + target.getYSpeed() * target.getYSpeed() - Projectile.speed
				* Projectile.speed;
		double b = 2 * (target.getXSpeed() * dX + target.getYSpeed() * dY);
		double c = dX * dX + dY * dY;

		// Check we're not breaking into complex numbers
		double q = b * b - 4 * a * c;
		if (q < 0) {
			dY = target.getMidY() - this.getMidY();
			dX = target.getMidX() - this.getMidX();
			Log.e("TowerTest", "Projectile.shoot() could not target!");
			hadError = true;
		} else {
		}

		// The time that we will hit the target
		double t = ((a < 0 ? -1 : 1) * Math.sqrt(q) - b) / (2 * a);

		// Aim for where the target will be after time t
		dX = t * target.getXSpeed();
		dY = t * target.getYSpeed();
		double theta = Math.atan2(dY, dX);

		/*
		 * if (dY>0 && dX>0) { }else if (dY>0 && dX>0) { }else if (dY>0 && dX>0) { }else if (dY>0 && dX>0) { }
		 */

		// bullet.hitPoint = new Point(targ.x + targ.vx * t, targ.y + targ.vy *
		// t);
		dX = t * Projectile.speed * Math.cos(theta);
		dY = t * Projectile.speed * Math.sin(theta);

		dY = target.getMidY() - this.getMidY();
		// dX = (target.getMidX() - this.getMidX())/Math.abs(target.getMidX() -
		// this.getMidX())*dX;

		float dist = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
		// D=r*t
		// therefore t = D/r
		trajectory = new MoveByModifier(dist / Projectile.speed, (float) dX, (float) dY);
		trajectory.addModifierListener(new IModifierListener<IEntity>() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				// Do stuff here if you want to

			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				myContext.getEngine().runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						Projectile.this.scene.detachChild(Projectile.this); // When else should we remove bullets? Check its range?
					}
				});
				source.removeBullet(Projectile.this);
				// enemy takes damage
				if (target.takeDamage(source.damage, source.damageType) < 1) { // then the enemy dies
					if (target.isAlive) {
						target.isAlive = false;
						TowerTest.addCredits(target.getCredits());

						myContext.getEngine().runOnUpdateThread(new Runnable() {
							@Override
							public void run() {
								Projectile.this.scene.detachChild(target);
							}
						});
						arrayEn.remove(target);
						// TODO play death animation enemy function pass scene to
						// detach
					}
				}
			}
		});

		this.registerEntityModifier(trajectory);
		if (!hadError) {
			/*
			 * Log.e("Jared","dx "+dX); //Log.e("Jared","dy "+dY); Log.e("Jared","theta "+theta);
			 */
		}
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
	 * 
	 * @return
	 */
	public boolean isDone() {
		return this.trajectory.isFinished();
		// return (targetX == x && targetY == y);
	}

	public float getMidX() {
		return this.getX() + this.getWidth() / 2;
	}

	public float getMidY() {
		return this.getY() + this.getHeight() / 2;
	}

	public Enemy getTarget() {
		return target;
	}
}
