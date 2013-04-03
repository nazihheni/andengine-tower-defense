package biz.abits.towertest;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.util.modifier.ease.IEaseFunction;
import org.andengine.entity.modifier.PathModifier.Path;

public class ExtendedPathModifier extends PathModifier {

	private boolean mStatePaused = false;
	private float mTimeModifier = 1f;

	private IExtendedPathModifierListener mExtendedPathModifierListener;

	public ExtendedPathModifier(final float pDuration, final org.andengine.entity.modifier.PathModifier.Path pPath,
			final IEntityModifierListener pEntityModiferListener, final IPathModifierListener pPathModifierListener,
			final IExtendedPathModifierListener pExtendedPathModifierListener, final IEaseFunction pEaseFunction)
			throws IllegalArgumentException {
		super(pDuration, pPath, pEntityModiferListener, pPathModifierListener, pEaseFunction);
		mExtendedPathModifierListener = pExtendedPathModifierListener;
	}

	public ExtendedPathModifier(float pDuration, org.andengine.entity.modifier.PathModifier.Path pPath) {
		super(pDuration, pPath);
	}

	public void pauseModifier() {
		if (!mStatePaused) {
			mExtendedPathModifierListener.onPause(this);
			mStatePaused = true;
		}
	}

	public void resumeModifier() {
		if (!mStatePaused) {
			mExtendedPathModifierListener.onResume(this);
			mStatePaused = false;
		}
	}

	public void setTimeModifier(final float pTimeModifier) {
		mTimeModifier = pTimeModifier;
	}

	@Override
	public float onUpdate(final float pSecondsElapsed, final IEntity pEntity) {
		if (mStatePaused)
			return 0f;
		if (mTimeModifier != 1f)
			return super.onUpdate(pSecondsElapsed * mTimeModifier, pEntity);
		return super.onUpdate(pSecondsElapsed, pEntity);
	}

	public static interface IExtendedPathModifierListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		public void onPause(final PathModifier pPathModifier);

		public void onResume(final PathModifier pPathModifier);
	}
}