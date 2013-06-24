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

class TrajectoryReturn {
	double t;
	double x;
	double y;
	double enemyAngle;
}

public class Projectile extends Sprite {
	// I am Enemy class
	Scene scene;
	Enemy target;
	Tower source;
	public final static float speed = 55f; // movement speed higher is faster
											// (distance to move per update)
	public MoveByModifier trajectory;
	public MoveByModifier targetTrajectory;
	public static String texture = "bullet.png";

	public Projectile(float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion, VertexBufferObjectManager tvbom, Scene sc) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, tvbom);
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
	
	/**
	 * finds the time that it will take for the projectile to hit the enemy based on curLink and prevDist
	 * @param curLink the link for us to assume the Enemy is on
	 * @param prevDist the distance it will take for the enemy to get to this link (how big of a head-start the bullet has)
	 * @return t the time it takes for the bullet to catch the enemy
	 */
	private TrajectoryReturn findTrajectory(int curLink, double prevDist) {
		//don't forget when troubleshooting this stuff:
		//EVERYTHING IS UPSIDE-DOWN, because higher Y means down, so your angles will be upside-down!!!
		TrajectoryReturn trajectoryReturn = new TrajectoryReturn();
		trajectoryReturn.enemyAngle = Math.atan2(target.path.xyPath.get(curLink).y - target.path.xyPath.get(curLink-1).y, target.path.xyPath.get(curLink).x - target.path.xyPath.get(curLink-1).x);
		if (prevDist < 0) {
			trajectoryReturn.x = target.getMidX();
			trajectoryReturn.y = target.getMidY();
		} else {
			//we need to make up for the other links the Enemy has to travel before getting to this link
			//enemyAngle is [-pi..pi]
			Double backAngle = trajectoryReturn.enemyAngle + Math.PI;
			trajectoryReturn.x = target.path.xyPath.get(curLink-1).x + Math.cos(backAngle)*prevDist + target.getMidX() - target.getX();
			trajectoryReturn.y = target.path.xyPath.get(curLink-1).y + Math.sin(backAngle)*prevDist + target.getMidY() - target.getY();
		}
		final double enemytotowerAngle = Math.atan2(getMidY() - trajectoryReturn.y, getMidX() - trajectoryReturn.x); // angle of the hypotenuse (-pi to pi)
		double incAngle = enemytotowerAngle - trajectoryReturn.enemyAngle; // this angle SHOULD be between 0 and pi/2
		
		while (incAngle <= -Math.PI)
			incAngle += 2*Math.PI;
		while (incAngle > Math.PI)
			incAngle -= 2*Math.PI;

		final double d0 = Math.sqrt(Math.pow(getMidX() - trajectoryReturn.x, 2) + Math.pow(getMidY() - trajectoryReturn.y, 2)); // starting distance
		//final double y0 = d0 * Math.sin(incAngle);// ; // starting y distance to target
		final double x0 = d0 * Math.cos(incAngle);// ; // starting x distance to target
		// this quadratic equation is based on the law of cosines, just FYI
		final double a = Math.pow(target.speed, 2) - Math.pow(Projectile.speed, 2);
		final double b = -2 * target.speed * x0;
		final double c = Math.pow(d0, 2);
		trajectoryReturn.t = (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a); // quadratic formula wee!
		return trajectoryReturn;
	}

	/**
	 * This function calculates the trajectory of the bullet, based on the Enemy's current speed and angle, it then gives the bullet that trajectory and sets a listener to remove the bullet for when the bullet reaches it's target
	 * @param arrayEn
	 * @param myContext
	 */
	public void shoot(final ArrayList<Enemy> arrayEn, final BaseGameActivity myContext) {
		int curLink1 = target.getCurrentLink();
		//start with the distance from the Enemy to the next point
		double totalDist = Math.sqrt(Math.pow(target.path.xyPath.get(curLink1).x - target.getX(), 2) + Math.pow(target.path.xyPath.get(curLink1).y - target.getY(), 2));
		TrajectoryReturn trajReturn = findTrajectory(curLink1, -1);
		int curLink = curLink1;
		//if it takes longer for the bullet than it takes for the enemy to turn the corner, look at the next link
		while ((trajReturn.t > (totalDist / target.speed)) && (curLink<target.path.xyPath.size()-1)) { //if it equals the size, then we're on the last link, so who cares, go with it!
			curLink++;
			trajReturn = findTrajectory(curLink, totalDist);
			totalDist += Math.sqrt(Math.pow(target.path.xyPath.get(curLink).x - target.path.xyPath.get(curLink-1).x, 2) + Math.pow(target.path.xyPath.get(curLink).y - target.path.xyPath.get(curLink-1).y, 2));
		}
		double bulletDist = Projectile.speed * trajReturn.t;
		double enTravelDist = target.speed * trajReturn.t;
		// D=r*t
		// therefore t = D/r
		trajectory = new MoveByModifier((float) (bulletDist / Projectile.speed), (float) (trajReturn.x - getMidX() + Math.cos(trajReturn.enemyAngle) * enTravelDist),
				(float) (trajReturn.y - getMidY() + Math.sin(trajReturn.enemyAngle) * enTravelDist));
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
				target.inboundDamage -= source.damage;
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
					arrayEn.remove(target); 
					// TODO play death animation enemy function pass scene to
				}
			}
		});
		registerEntityModifier(trajectory);
		target.inboundDamage += source.damage;
	}

	public float getMidX() {
		return getX() + getWidth() / 2;
	}

	public float getMidY() {
		return getY() + getHeight() / 2;
	}
}
