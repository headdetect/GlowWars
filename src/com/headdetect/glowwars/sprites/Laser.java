package com.headdetect.glowwars.sprites;

import java.io.IOException;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.headdetect.glowwars.Constants;
import com.headdetect.glowwars.activities.GameActivity;

public class Laser extends Sprite {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int SIZE = 16;

	public static final Vector2 SPEED = new Vector2( 8f , 0f );

	// ===========================================================
	// Fields
	// ===========================================================

	private static ITexture mTexture;
	private static ITextureRegion mRegion;
	private static GameActivity mGameActivity;

	private static GenericPool< Laser > mPool;

	private Body mBody;

	// ===========================================================
	// Interface Overrides
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	private Laser( float pX , float pY , float pWidth , float pHeight , ITextureRegion pTextureRegion , VertexBufferObjectManager pVertexBufferObjectManager ) {
		super( pX , pY , pWidth , pHeight , pTextureRegion , pVertexBufferObjectManager );

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Body getBody() {
		return mBody;
	}

	public void setBody( Body mBody ) {
		this.mBody = mBody;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void prepareLaser( GameActivity mAct ) {
		try {
			mTexture = new BitmapTexture( mAct.getTextureManager() , mAct.getTexture( "gfx/laser.png" ) );
		} catch ( IOException e ) {
			e.printStackTrace();
			return;
		}

		mRegion = TextureRegionFactory.extractFromTexture( mTexture );

		mGameActivity = mAct;

		mPool = new GenericPool< Laser >( 40 ) {

			@Override
			public Laser onAllocatePoolItem() {
				return new Laser( -16 , -16 , 16 , 16 , mRegion , mGameActivity.getVertexBufferObjectManager() );
			}

			@Override
			protected void onHandleRecycleItem( final Laser sprt ) {
				sprt.setIgnoreUpdate( true );
				sprt.setVisible( false );
			}

		};
	}

	public static void buildTexture() {
		if ( mGameActivity == null ) {
			throw new NullPointerException( "You must call Laser.prepareLaser(GameActivity) before calling" );
		}

		mTexture.load();
	}

	public static Laser create( float x , float y ) {
		Laser laser = mPool.obtainPoolItem();
		laser.setVisible( true );
		laser.mBody = PhysicsFactory.createCircleBody( mGameActivity.physicsWorld , laser.getX() , laser.getY() , SIZE , BodyType.DynamicBody , Constants.LASER_FIXTURE );
		laser.mBody.setBullet( true );
		laser.mBody.setAwake( true );
		laser.mBody.setUserData( laser );
		mGameActivity.physicsWorld.registerPhysicsConnector( new PhysicsConnector( laser , laser.mBody , true , false ) );
		
		laser.transform( x , y );
		
		return laser;
	}

	public void transform( float x , float y ) {
		final Vector2 mVec = mBody.getTransform().getPosition();
		mVec.set( x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT ,  y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT );
		mBody.setTransform( mVec , 0 );
	}

	public void addForce() {
		mBody.setLinearVelocity( SPEED );
	}

	public void recycle() {
		mPool.recyclePoolItem( this );
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
