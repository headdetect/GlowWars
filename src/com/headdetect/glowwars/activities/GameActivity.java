package com.headdetect.glowwars.activities;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.input.touch.controller.MultiTouchController;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.view.KeyEvent;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.headdetect.glowwars.TextUtils;
import com.headdetect.glowwars.sprites.Astroid;
import com.headdetect.glowwars.sprites.ButtonShoot;
import com.headdetect.glowwars.sprites.Laser;
import com.headdetect.glowwars.sprites.Ship;

public class GameActivity extends BaseGameActivity {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final String SET_DIFFICULTY = "diff";
	public static final int DIFFICULTY_EASY = 1;
	public static final int DIFFICULTY_NORMAL = 2;
	public static final int DIFFICULTY_HARD = 3;

	public static final int CAMERA_WIDTH = 1280;
	public static final int CAMERA_HEIGHT = 768;

	// - TODO: Use R.java
	public static final String lblPause = "- PAUSED -";

	// ===========================================================
	// Fields
	// ===========================================================

	private Scene mScene;
	private Scene mPauseScene;

	private Ship mShip;
	private ButtonShoot mButtonShoot;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera( 0 , 0 , CAMERA_WIDTH , CAMERA_HEIGHT );
		return new EngineOptions( true , ScreenOrientation.LANDSCAPE_SENSOR , new RatioResolutionPolicy( CAMERA_WIDTH , CAMERA_HEIGHT ) , camera );
	}

	@Override
	public void onCreateResources( OnCreateResourcesCallback pOnCreateResourcesCallback ) throws Exception {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath( "gfx/" );

		Debug.d( "Creating textures" );

		Ship.prepareShip( this );
		Astroid.prepareAstroid( this );
		Laser.prepareLaser( this );
		ButtonShoot.prepareButton( this );
		TextUtils.prepare( this );

		Debug.d( "Bulding textures" );

		Ship.buildTexture();
		Laser.buildTexture();
		Astroid.buildTexture();
		ButtonShoot.buildTexture();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	@Override
	public void onCreateScene( OnCreateSceneCallback pOnCreateSceneCallback ) throws Exception {
		mEngine.registerUpdateHandler( new FPSLogger() );
		if ( MultiTouch.isSupported( this ) ) {
			mEngine.setTouchController( new MultiTouchController() );
			if ( !MultiTouch.isSupportedDistinct( this ) ) {
				Toast.makeText( this , "Warning!\nYour device might have problems to distinguish between separate fingers." , Toast.LENGTH_LONG ).show();
			}
		} else {
			Toast.makeText( this , "Sorry your device does not support MultiTouch!" , Toast.LENGTH_LONG ).show();
			finish();
		}

		mScene = new Scene();

		/* Allocate all of the pooled sprites */

		Debug.d( "Filling sprite pool" );

		final int POOL_SIZE = 24;

		for ( int i = 0; i < POOL_SIZE; i++ ) {
			Laser.create( -64 , -64 );
			Astroid.create( -64 , -64 );
		}

		// -----------------------------------------
		// - Pause Scene
		// -----------------------------------------

		mPauseScene = new Scene();
		Rectangle dimShape = new Rectangle( 0 , 0 , CAMERA_WIDTH , CAMERA_HEIGHT , this.getVertexBufferObjectManager() );
		dimShape.setColor( 0f , 0f , 0f , .8f );
		Text pText = TextUtils.getText( new Vector2( CAMERA_WIDTH / 2 - TextUtils.measureString( lblPause ) / 2 , CAMERA_HEIGHT / 2 ) , lblPause , TextUtils.HORIZONTAL_ALIGN_CENTER );

		mPauseScene.attachChild( pText );
		mPauseScene.attachChild( dimShape );

		pOnCreateSceneCallback.onCreateSceneFinished( mScene );
	}

	@Override
	public void onPopulateScene( Scene pScene , OnPopulateSceneCallback pOnPopulateSceneCallback ) throws Exception {
		mShip = Ship.createShip();
		mButtonShoot = ButtonShoot.createButton();

		pScene.attachChild( mShip );
		pScene.attachChild( mButtonShoot );
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onKeyDown( final int pKeyCode , final KeyEvent pEvent ) {
		if ( pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN ) {
			pause();
			return true;
		}
		return super.onKeyDown( pKeyCode , pEvent );
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public IInputStreamOpener getTexture( final String loc ) {
		return new IInputStreamOpener() {
			@Override
			public InputStream open() throws IOException {
				return getAssets().open( loc );
			}
		};
	}

	public void pause() {
		if ( this.mEngine.isRunning() ) {
			this.mScene.setChildScene( mPauseScene );
			this.mEngine.stop();
		} else {
			this.mScene.clearChildScene();
			this.mEngine.start();
		}

	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
