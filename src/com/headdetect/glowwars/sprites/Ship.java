package com.headdetect.glowwars.sprites;

import static com.headdetect.glowwars.activities.GameActivity.CAMERA_HEIGHT;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.headdetect.glowwars.Constants;
import com.headdetect.glowwars.activities.GameActivity;

public class Ship extends AnimatedSprite {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int SIZE = 128;

	// ===========================================================
	// Fields
	// ===========================================================

	private static BuildableBitmapTextureAtlas mAtlas;

	private static TiledTextureRegion mRegion;

	private static GameActivity mGameActivity;

	private boolean animate = true;

	private int health = 20;

	private Body mBody;

	private ShipEvents mEvents;

	// ===========================================================
	// Interface Overrides
	// ===========================================================

	private final IAnimationListener mOnAnimationEvent = new IAnimationListener() {

		@Override
		public void onAnimationStarted( AnimatedSprite pAnimatedSprite , int pInitialLoopCount ) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFrameChanged( AnimatedSprite pAnimatedSprite , int pOldFrameIndex , int pNewFrameIndex ) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationLoopFinished( AnimatedSprite pAnimatedSprite , int pRemainingLoopCount , int pInitialLoopCount ) {
			animate = true;

		}

		@Override
		public void onAnimationFinished( AnimatedSprite pAnimatedSprite ) {
			animate = true;

		}
	};

	// ===========================================================
	// Constructors
	// ===========================================================

	private Ship( float pX , float pY , float pWidth , float pHeight , ITiledTextureRegion pTiledTextureRegion , VertexBufferObjectManager pTiledSpriteVertexBufferObject ) {
		super( pX , pY , pWidth , pHeight , pTiledTextureRegion , pTiledSpriteVertexBufferObject );

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getHealth() {
		return health;
	}

	public void setHealth( int health ) {
		this.health = health;
	}

	public Body getBody() {
		return mBody;
	}

	public void setBody( Body body ) {
		this.mBody = body;
	}

	public ShipEvents getEvents() {
		return mEvents;
	}

	public void setEvents( ShipEvents mEvents ) {
		this.mEvents = mEvents;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void prepareShip( GameActivity mAct ) {
		mAtlas = new BuildableBitmapTextureAtlas( mAct.getTextureManager() , 512 , 128 , TextureOptions.BILINEAR );
		mRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset( mAtlas , mAct , "ship.png" , 4 , 1 );
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

	public static Ship createShip() {
		if ( mGameActivity == null ) {
			throw new NullPointerException( "Ship.prepareShip(BaseGameActivity) must be called first" );
		}

		Ship ship = new Ship( 0 , 0 , SIZE , SIZE , mRegion , mGameActivity.getVertexBufferObjectManager() );

		final float halfWidth = SIZE * 0.5f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = SIZE * 0.5f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

		final float top = -halfHeight;
		final float bottom = halfHeight;
		final float left = -halfHeight;
		final float centerY = 0;
		final float right = halfWidth;

		final Vector2[] vertices = { new Vector2( centerY , right ) , new Vector2( left , bottom ) , new Vector2( left , top ) };

		ship.mBody = PhysicsFactory.createPolygonBody( mGameActivity.physicsWorld , ship , vertices , BodyType.StaticBody , Constants.SHIP_FIXTURE );
		ship.mBody.setFixedRotation( true );
		ship.mBody.setLinearVelocity( 0 , 0 );
		ship.mBody.resetMassData();

		mGameActivity.physicsWorld.registerPhysicsConnector( new PhysicsConnector( ship , ship.mBody , true , false ) );

		ship.transform( 20 + SIZE, CAMERA_HEIGHT / 2 - SIZE / 2 );

		return ship;
	}

	public void hit() {
		if ( !animate ) {
			return;
		}

		setHealth( getHealth() - 4 );

		this.animate( 10 , 8 , mOnAnimationEvent );

		if ( health <= 0 ) {
			setVisible( false );

			if ( mEvents != null ) {
				mEvents.onDeath();
			}
		}
	}

	public void transform( float x , float y ) {
		final Vector2 mVec = mBody.getTransform().getPosition();
		mVec.set( x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT , ( y + getHeight() / 2 ) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT );
		mBody.setTransform( mVec , 0 );
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface ShipEvents {
		void onDeath();
	}
}
