package com.headdetect.glowwars.sprites;

import static com.headdetect.glowwars.activities.GameActivity.CAMERA_HEIGHT;
import static com.headdetect.glowwars.activities.GameActivity.CAMERA_WIDTH;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import android.os.Handler;
import android.view.MotionEvent;

public class ButtonShoot extends AnimatedSprite {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static BuildableBitmapTextureAtlas mAtlas;

	private static TiledTextureRegion mRegion;

	private static BaseGameActivity mGameActivity;

	private TouchEventListener mListener;

	private Handler mTouchHandler;

	public int touchHandlerLagTime = 150;

	// ===========================================================
	// Interface Overrides
	// ===========================================================

	private final Runnable mTouchEventFire = new Runnable() {

		@Override
		public void run() {

			if ( mListener != null ) {
				mListener.onDown();
			}

			mTouchHandler.postDelayed( mTouchEventFire , touchHandlerLagTime );

		}
	};

	// ===========================================================
	// Constructors
	// ===========================================================

	private ButtonShoot( float pX , float pY , float pWidth , float pHeight , ITiledTextureRegion pTiledTextureRegion , VertexBufferObjectManager pTiledSpriteVertexBufferObject ) {
		super( pX , pY , pWidth , pHeight , pTiledTextureRegion , pTiledSpriteVertexBufferObject );

		// Due to the threading stuffs.
		mGameActivity.runOnUiThread( new Runnable() {

			@Override
			public void run() {
				mTouchHandler = new Handler();
			}
		} );

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public TouchEventListener getTouchEventListener() {
		return mListener;
	}

	public void setTouchEventListener( TouchEventListener mListener ) {
		this.mListener = mListener;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onAreaTouched( final TouchEvent event , final float pTouchAreaLocalX , final float pTouchAreaLocalY ) {

		final int action = event.getMotionEvent().getAction();
		final int actionCode = action & MotionEvent.ACTION_MASK;

		if ( actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_POINTER_DOWN ) {
			mTouchHandler.removeCallbacks( mTouchEventFire );
			mTouchHandler.postDelayed( mTouchEventFire , touchHandlerLagTime );

			if ( mListener != null ) {
				mListener.onDown();
			}
			
			this.setCurrentTileIndex( 1 );

		} else if ( actionCode == MotionEvent.ACTION_UP || actionCode == MotionEvent.ACTION_POINTER_UP || actionCode == MotionEvent.ACTION_OUTSIDE ) {
			mTouchHandler.removeCallbacks( mTouchEventFire );

			if ( mListener != null ) {
				mListener.onUp();
			}

			this.setCurrentTileIndex( 0 );
		}

		return true;

	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static void prepareButton( BaseGameActivity mAct ) {
		mAtlas = new BuildableBitmapTextureAtlas( mAct.getTextureManager() , 512 , 256 , TextureOptions.BILINEAR );
		mRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset( mAtlas , mAct , "button_shoot.png" , 2 , 1 );
		mGameActivity = mAct;

	}

	public static void buildTexture() {
		if ( mGameActivity == null ) {
			throw new NullPointerException( "Ship.prepareShip(BaseGameActivity) must be called first" );
		}

		try {

			mAtlas.build( new BlackPawnTextureAtlasBuilder< IBitmapTextureAtlasSource , BitmapTextureAtlas >( 0 , 0 , 0 ) );
			mAtlas.load();

		} catch ( Exception e ) {
			Debug.e( e );
		}
	}

	public static ButtonShoot createButton() {
		if ( mGameActivity == null ) {
			throw new NullPointerException( "ButtonShoot.prepareShip(BaseGameActivity) must be called first" );
		}

		final int SIZE = 230;
		final int PADDING = 28;

		return new ButtonShoot( CAMERA_WIDTH - SIZE - PADDING , CAMERA_HEIGHT - SIZE - PADDING , SIZE , SIZE , mRegion , mGameActivity.getVertexBufferObjectManager() );
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface TouchEventListener {
		void onUp();

		void onDown();
	}
}
