package biz.abits.towertest;

import java.util.ArrayList;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

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

	public Projectile(float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion, VertexBufferObjectManager tvbom, Scene sc) {
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
		// to predict where the enemy will be, we need a parametric equation for
		// the enemy's position
		final double enemytotowerAngle = Math.atan2(getMidY() - target.getMidY(), getMidX() - target.getMidX()); // angle of the hypotenuse (-pi to pi)
		final double enemyAngle = target.getCurrentAngle(); // (-pi to pi)
		double incAngle = enemytotowerAngle - enemyAngle; // this angle SHOULD be between 0 and pi/2
		while (incAngle <= -180)
			incAngle += 360;
		while (incAngle > 180)
			incAngle -= 360;

		final double d0 = Math.sqrt(Math.pow(getMidX() - target.getMidX(), 2) + Math.pow(getMidY() - target.getMidY(), 2)); // starting distance
		//final double y0 = d0 * Math.sin(incAngle);// ; // starting y distance to target
		final double x0 = d0 * Math.cos(incAngle);// ; // starting x distance to target
		// this quadratic equation is based on the law of cosines, just FYI
		final double a = Math.pow(target.speed, 2) - Math.pow(Projectile.speed, 2);
		final double b = -2 * target.speed * x0;
		final double c = Math.pow(d0, 2);
		final double t = (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a); // quadratic formula wee!
		final double bulletDist = Projectile.speed * t;
		final double enTravelDist = target.speed * t;
		// D=r*t
		// therefore t = D/r
		trajectory = new MoveByModifier((float) (bulletDist / Projectile.speed), (float) (target.getMidX() - getMidX() + Math.cos(target.getCurrentAngle()) * enTravelDist),
				(float) (target.getMidY() - getMidY() + Math.sin(target.getCurrentAngle()) * enTravelDist));
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
						// scene.detachChild(Projectile.this); // When else should we remove bullets? Check its range?
						Projectile.this.detachSelf();
					}
				});
				source.removeBullet(Projectile.this);
				// enemy takes damage
				if (target.takeDamage(source.damage, source.damageType) < 1) { // then the enemy dies
					if (target.isAlive) { // this prevents getting multiple credits for one kill!
						target.isAlive = false;
						TowerTest.addCredits(target.getCredits());
					}
					myContext.getEngine().runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							scene.detachChild(target);
						}
					});
					arrayEn.remove(target); // TODO play death animation enemy function pass scene to // detach }
				}
			}
		});
		registerEntityModifier(trajectory);
	}

	public float getMidX() {
		return getX() + getWidth() / 2;
	}

	public float getMidY() {
		return getY() + getHeight() / 2;
	}
}
