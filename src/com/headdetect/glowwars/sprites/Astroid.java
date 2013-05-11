package com.headdetect.glowwars.sprites;

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
import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.headdetect.glowwars.Constants;
import com.headdetect.glowwars.activities.GameActivity;

public class Astroid extends AnimatedSprite {

	// ===========================================================
	// Constants
	// ===========================================================
	
	public static final int SIZE = 90;

	
	// ===========================================================
	// Fields
	// ===========================================================

	private static BuildableBitmapTextureAtlas mTextureOne;
	private static BuildableBitmapTextureAtlas mTextureTwo;
	private static BuildableBitmapTextureAtlas mTextureThree;
	private static TiledTextureRegion mRegionOne;
	private static TiledTextureRegion mRegionTwo;
	private static TiledTextureRegion mRegionThree;

	private static GameActivity mGameActivity;

	private static GenericPool< Astroid > mPool;

	private int health = 15;
	
	private Body mBody;

	// ===========================================================
	// Constructors
	// ===========================================================

	private Astroid( float pX , float pY , float pWidth , float pHeight , ITiledTextureRegion pTextureRegion , VertexBufferObjectManager pVertexBufferObjectManager ) {
		super( pX , pY , pWidth , pHeight , pTextureRegion , pVertexBufferObjectManager );
		

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void prepareAstroid( GameActivity mAct ) {
		mTextureOne = new BuildableBitmapTextureAtlas( mAct.getTextureManager() , 512 , 128 , TextureOptions.BILINEAR );
		mTextureTwo = new BuildableBitmapTextureAtlas( mAct.getTextureManager() , 512 , 128 , TextureOptions.BILINEAR );
		mTextureThree = new BuildableBitmapTextureAtlas( mAct.getTextureManager() , 512 , 128 , TextureOptions.BILINEAR );
		
		
		mRegionOne = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset( mTextureOne , mAct , "astroid_1.png" , 4 , 1 );
		mRegionTwo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset( mTextureTwo , mAct , "astroid_2.png" , 4 , 1 );
		mRegionThree = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset( mTextureThree , mAct , "astroid_3.png" , 4 , 1 );

		mGameActivity = mAct;

		mPool = new GenericPool< Astroid >( 24 ) {

			private int count = 0;

			@Override
			public Astroid onAllocatePoolItem() {

				ITiledTextureRegion mRegion;

				switch ( count++ % 3 ) {
					case 0:
						mRegion = mRegionOne;
						break;
					case 1:
						mRegion = mRegionTwo;
						break;
					case 2:
						mRegion = mRegionThree;
						break;
					default:
						return null;
				}

				Astroid roid = new Astroid( -SIZE , -SIZE , SIZE , SIZE , mRegion , mGameActivity.getVertexBufferObjectManager() );
				return roid;
			}

			@Override
			protected void onHandleRecycleItem( final Astroid sprt ) {
				sprt.setIgnoreUpdate( true );
				sprt.setVisible( false );
			}

		};
	}

	public static void buildTexture() {
		if ( mGameActivity == null ) {
			throw new NullPointerException( "You must call Astroid.prepareAstroid(GameActivity) before calling" );
		}

		try {

			mTextureOne.build( new BlackPawnTextureAtlasBuilder< IBitmapTextureAtlasSource , BitmapTextureAtlas >( 0 , 0 , 0 ) );
			mTextureOne.load();
			
			mTextureTwo.build( new BlackPawnTextureAtlasBuilder< IBitmapTextureAtlasSource , BitmapTextureAtlas >( 0 , 0 , 0 ) );
			mTextureTwo.load();
			
			mTextureThree.build( new BlackPawnTextureAtlasBuilder< IBitmapTextureAtlasSource , BitmapTextureAtlas >( 0 , 0 , 0 ) );
			mTextureThree.load();

		} catch ( Exception e ) {
			Debug.e( e );
		}
	}

	public static Astroid create( float x , float y ) {
		Astroid sstroid = mPool.obtainPoolItem();
		sstroid.setVisible( true );
		sstroid.setCurrentTileIndex( MathUtils.random( 0 , 3 ) );
		
		sstroid.mBody = PhysicsFactory.createCircleBody( mGameActivity.physicsWorld , sstroid.getX() + SIZE / 2 , sstroid.getY() + SIZE / 2 , SIZE , BodyType.DynamicBody , Constants.LASER_FIXTURE );
		sstroid.mBody.setBullet( true );
		sstroid.mBody.setAwake( true );
		sstroid.mBody.setUserData( sstroid );
		mGameActivity.physicsWorld.registerPhysicsConnector( new PhysicsConnector( sstroid , sstroid.mBody , true , true ) );
		
		sstroid.transform( x , y );
		
		
		return sstroid;
	}
	
	public void transform( float x , float y ) {
		final Vector2 mVec = mBody.getTransform().getPosition();
		mVec.set( x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT , y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT );
		mBody.setTransform( mVec , 0 );
	}

	public void hit() {
		health -= 5;

		if ( health <= 0 ) {
			setVisible( false );
			//TODO: Play noise
		}
	}

	public void addForce() {
		if(mBody != null) {
			
			final float vX = MathUtils.random( 3f , 10f );
			final float vY = MathUtils.random( -1f , 1f );
			
			mBody.setLinearVelocity( -vX , vY );
		}
		
	}

	public void recycle() {
		mPool.recyclePoolItem( this );		
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
