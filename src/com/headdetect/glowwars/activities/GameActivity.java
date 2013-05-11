package com.headdetect.glowwars.activities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.input.touch.controller.MultiTouchController;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import android.view.KeyEvent;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.headdetect.glowwars.RandomTimerHandler;
import com.headdetect.glowwars.RandomTimerHandler.IRandomTimerCallback;
import com.headdetect.glowwars.TextUtils;
import com.headdetect.glowwars.sprites.Astroid;
import com.headdetect.glowwars.sprites.ButtonShoot;
import com.headdetect.glowwars.sprites.ButtonShoot.TouchEventListener;
import com.headdetect.glowwars.sprites.Laser;
import com.headdetect.glowwars.sprites.Ship;
import com.headdetect.glowwars.sprites.Ship.ShipEvents;

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

	protected Scene mScene;
	protected Scene mPauseScene;

	protected Ship mShip;
	protected ButtonShoot mButtonShoot;

	protected ArrayList< Laser > mActiveLasers;
	protected ArrayList< Astroid > mActiveAstroids;

	public PhysicsWorld physicsWorld;

	// ===========================================================
	// Interface Overrides
	// ===========================================================

	private final IUpdateHandler mGlobalUpdate = new IUpdateHandler() {

		@Override
		public void onUpdate( float pSecondsElapsed ) {
			for ( int i = 0; i < mActiveLasers.size(); i++ ) {
				final Laser l = mActiveLasers.get( i );
				
				if(l.getX() > CAMERA_WIDTH + Laser.SIZE + 1){
					l.setVisible( false );
				}
				
				if ( l != null && !l.isVisible() ) {
					removeEntity( l );
					l.recycle();
					mActiveLasers.remove( i );
				}
			}

			for ( int i = 0; i < mActiveAstroids.size(); i++ ) {
				final Astroid l = mActiveAstroids.get( i );
				
				if(l.getX() < -Laser.SIZE - 1){
					l.setVisible( false );
				}
				
				if ( l != null && !l.isVisible() ) {
					removeEntity( l );
					l.recycle();
					mActiveAstroids.remove( i );
				}
			}
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub

		}

	};

	private final IRandomTimerCallback mAstroidTimerCallback = new IRandomTimerCallback() {

		@Override
		public void onTimePassed( final RandomTimerHandler pTimerHandler ) {

			if ( mActiveAstroids == null ) {
				return;
			}


			final float x = CAMERA_WIDTH + Astroid.SIZE + 50;
			final float y = MathUtils.random( 50 , CAMERA_HEIGHT - 50 );

			Astroid astroid = Astroid.create( x , y );

			try {
				mActiveAstroids.add( astroid );
			} catch ( Exception e ) {
				astroid.setVisible( false );
				astroid.dispose();
				astroid = null;
				return;
			}

			mScene.attachChild( astroid );
			astroid.addForce();

			Debug.d( "SENDING ASTROID" );

		}
	};

	private final IOnSceneTouchListener mSceneTouchListener = new IOnSceneTouchListener() {

		@Override
		public boolean onSceneTouchEvent( Scene pScene , TouchEvent mEvent ) {
			float fx = mEvent.getX();
			float fy = mEvent.getY();

			mShip.transform( fx , fy );

			return true;
		}
	};

	private final TouchEventListener mButtonShootTouchEventListener = new TouchEventListener() {

		@Override
		public void onUp() {

		}

		@Override
		public void onDown() {
			if ( mShip == null || mActiveLasers == null ) {
				return;
			}

			final float x = mShip.getX() + Ship.SIZE + 1;
			final float y = mShip.getY() + Ship.SIZE / 2;

			Laser laser = Laser.create( x , y );

			try {
				mActiveLasers.add( laser );
			} catch ( Exception e ) {
				laser.setVisible( false );
				laser.dispose();
				laser = null;
				return;
			}

			mScene.attachChild( laser );
			laser.addForce();

			Debug.d( "SENDING LASER" );

		}
	};

	private final ContactListener mContactListener = new ContactListener() {

		@Override
		public void beginContact( Contact contact ) {

			final Body bodyA = contact.getFixtureA().getBody();
			final Body bodyB = contact.getFixtureB().getBody();

			if ( bodyA.getUserData() instanceof Astroid && bodyB.equals( mShip.getBody() ) ) {
				mShip.hit();
				mShip.getBody().resetMassData();

				Astroid a = (Astroid) bodyA.getUserData();
				a.setVisible( false );
			}

			if ( bodyB.getUserData() instanceof Astroid && bodyA.equals( mShip.getBody() ) ) {
				mShip.hit();
				mShip.getBody().resetMassData();

				Astroid a = (Astroid) bodyB.getUserData();
				a.setVisible( false );
			}

		}

		@Override
		public void endContact( Contact contact ) {
			final Body bodyA = contact.getFixtureA().getBody();
			final Body bodyB = contact.getFixtureB().getBody();

			final Object bodyObjectA = bodyA.getUserData();
			final Object bodyObjectB = bodyB.getUserData();

			if ( bodyObjectA instanceof Laser && bodyObjectB instanceof Astroid ) {
				Laser l = (Laser) bodyObjectA;
				Astroid a = (Astroid) bodyObjectB;

				l.setVisible( false );
				a.hit();
			}

			if ( bodyObjectB instanceof Laser && bodyObjectA instanceof Astroid ) {
				Laser l = (Laser) bodyObjectB;
				Astroid a = (Astroid) bodyObjectA;

				l.setVisible( false );
				a.hit();
			}
		}

		@Override
		public void preSolve( Contact contact , Manifold oldManifold ) {
			// TODO Auto-generated method stub

		}

		@Override
		public void postSolve( Contact contact , ContactImpulse impulse ) {
			// TODO Auto-generated method stub

		}
	};

	private final ShipEvents mShipEventHandler = new ShipEvents() {

		@Override
		public void onDeath() {

			Toast.makeText( GameActivity.this , "You died" , Toast.LENGTH_SHORT ).show();

			// pause();
		}

	};

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
		TextUtils.buildFonts();

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

		physicsWorld = new FixedStepPhysicsWorld( 50 , 5 , new Vector2( 0 , 0 ) , false , 8 , 8 );
		physicsWorld.setContactListener( mContactListener );

		mScene = new Scene();
		mScene.setBackground( new Background( 0f , 0f , 0f ) );
		mEngine.registerUpdateHandler( mGlobalUpdate );
		mScene.registerUpdateHandler( physicsWorld );

		mActiveLasers = new ArrayList< Laser >( 24 );
		mActiveAstroids = new ArrayList< Astroid >( 24 );

		mShip = Ship.createShip();
		mButtonShoot = ButtonShoot.createButton();

		mShip.setEvents( mShipEventHandler );
		mButtonShoot.setTouchEventListener( mButtonShootTouchEventListener );

		mScene.attachChild( mShip );
		mScene.attachChild( mButtonShoot );

		mScene.registerTouchArea( mButtonShoot );
		mScene.setTouchAreaBindingOnActionDownEnabled( true );

		mScene.registerUpdateHandler( new RandomTimerHandler( .5f , 3f , true , mAstroidTimerCallback ) );
		mScene.setOnSceneTouchListener( mSceneTouchListener );

		// -----------------------------------------
		// - Pause Scene
		// -----------------------------------------

		mPauseScene = new Scene();
		Rectangle dimShape = new Rectangle( 0 , 0 , CAMERA_WIDTH , CAMERA_HEIGHT , this.getVertexBufferObjectManager() );
		dimShape.setColor( 0f , 0f , 0f , .8f );
		Text pText = TextUtils.getText( new Vector2( CAMERA_WIDTH / 2 - TextUtils.measureString( lblPause ) / 2 , CAMERA_HEIGHT / 2 ) , lblPause , TextUtils.HORIZONTAL_ALIGN_CENTER );

		mPauseScene.attachChild( dimShape );
		mPauseScene.attachChild( pText );

		pOnCreateSceneCallback.onCreateSceneFinished( mScene );
	}

	@Override
	public void onPopulateScene( Scene pScene , OnPopulateSceneCallback pOnPopulateSceneCallback ) throws Exception {

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

	public void removeEntity( IEntity mSprite ) {
		if ( mSprite == null )
			return;

		final EngineLock mLock = mEngine.getEngineLock();
		mLock.lock();
		try {
			mEngine.getScene().detachChild( mSprite );
			mSprite.dispose();
			mSprite = null;
		} catch ( Exception e ) {
		}
		mLock.unlock();

	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
