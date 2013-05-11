package com.headdetect.glowwars;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.util.math.MathUtils;

public class RandomTimerHandler implements IUpdateHandler {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private float mMinTimerSeconds;
	private float mMaxTimerSeconds;

	private float mTimerSecondsElapsed;
	private boolean mTimerCallbackTriggered;
	protected final IRandomTimerCallback mTimerCallback;
	private boolean mAutoReset;

	// ===========================================================
	// Constructors
	// ===========================================================

	public RandomTimerHandler( final float pMinTimerSeconds , final float pMaxTimerSeconds , final IRandomTimerCallback pTimerCallback ) {
		this( pMinTimerSeconds , pMaxTimerSeconds , false , pTimerCallback );
	}

	public RandomTimerHandler( final float pMinTimerSeconds , final float pMaxTimerSeconds , final boolean pAutoReset , final IRandomTimerCallback pTimerCallback ) {
		this.setMinTimerSeconds( pMinTimerSeconds );
		this.setMaxTimerSeconds( pMaxTimerSeconds );

		this.mAutoReset = pAutoReset;
		this.mTimerCallback = pTimerCallback;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isAutoReset() {
		return this.mAutoReset;
	}

	public void setAutoReset( final boolean pAutoReset ) {
		this.mAutoReset = pAutoReset;
	}

	public float getMinTimerSeconds() {
		return mMinTimerSeconds;
	}

	public void setMinTimerSeconds( float mMinTimerSeconds ) {
		this.mMinTimerSeconds = mMinTimerSeconds;
	}

	public float getMaxTimerSeconds() {
		return mMaxTimerSeconds;
	}

	public void setMaxTimerSeconds( float mMaxTimerSeconds ) {
		this.mMaxTimerSeconds = mMaxTimerSeconds;
	}

	public float getTimerSecondsElapsed() {
		return this.mTimerSecondsElapsed;
	}

	public boolean isTimerCallbackTriggered() {
		return this.mTimerCallbackTriggered;
	}

	public void setTimerCallbackTriggered( boolean pTimerCallbackTriggered ) {
		this.mTimerCallbackTriggered = pTimerCallbackTriggered;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onUpdate( final float pSecondsElapsed ) {
		if ( this.mAutoReset ) {
			this.mTimerSecondsElapsed += pSecondsElapsed;

			final float mTime = MathUtils.random( mMinTimerSeconds , mMaxTimerSeconds );

			while ( this.mTimerSecondsElapsed >= mTime ) {
				this.mTimerSecondsElapsed -= mTime;
				this.mTimerCallback.onTimePassed( this );
			}
		} else {
			if ( !this.mTimerCallbackTriggered ) {
				this.mTimerSecondsElapsed += pSecondsElapsed;
				
				final float mTime = MathUtils.random( mMinTimerSeconds , mMaxTimerSeconds );
				
				if ( this.mTimerSecondsElapsed >= mTime ) {
					this.mTimerCallbackTriggered = true;
					this.mTimerCallback.onTimePassed( this );
				}
			}
		}
	}

	@Override
	public void reset() {
		this.mTimerCallbackTriggered = false;
		this.mTimerSecondsElapsed = 0;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface IRandomTimerCallback {
		public void onTimePassed( final RandomTimerHandler pTimerHandler );
	}
}
