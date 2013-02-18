package com.headdetect.glowwars.sprites;

import static com.headdetect.glowwars.activities.GameActivity.CAMERA_HEIGHT;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.AnimatedSprite;
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

public class Ship extends AnimatedSprite {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static BuildableBitmapTextureAtlas mAtlas;

	private static TiledTextureRegion mRegion;

	private static BaseGameActivity mGameActivity;

	private boolean damaging;
	private int health;

	private IUpdateHandler mUpdateHandler = new IUpdateHandler() {

		private int colorIndex = 0;

		@Override
		public void onUpdate( float pSecondsElapsed ) {
			if ( damaging ) {

				if ( colorIndex >= 4 ) {
					damaging = false;
					colorIndex = 0;
					return;
				}

				setCurrentTileIndex( colorIndex++ );
			}

		}

		@Override
		public void reset() {
			colorIndex = 0;
			setCurrentTileIndex( 0 );
		}
	};

	// ===========================================================
	// Constructors
	// ===========================================================

	private Ship( float pX , float pY , float pWidth , float pHeight , ITiledTextureRegion pTiledTextureRegion , VertexBufferObjectManager pTiledSpriteVertexBufferObject ) {
		super( pX , pY , pWidth , pHeight , pTiledTextureRegion , pTiledSpriteVertexBufferObject );

		this.registerUpdateHandler( mUpdateHandler );
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

	public boolean isDamaging() {
		return damaging;
	}

	public void setDamaging( boolean damaging ) {
		this.damaging = damaging;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void prepareShip( BaseGameActivity mAct ) {
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
		
		final int SIZE = 128;

		return new Ship( 20 , CAMERA_HEIGHT / 2 - SIZE / 2, SIZE , SIZE , mRegion , mGameActivity.getVertexBufferObjectManager() );
	}

	public void hit() {
		setDamaging( true );
		setHealth( getHealth() - 4 );
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
