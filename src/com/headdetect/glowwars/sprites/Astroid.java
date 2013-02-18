package com.headdetect.glowwars.sprites;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.math.MathUtils;

import com.headdetect.glowwars.activities.GameActivity;

public class Astroid extends AnimatedSprite {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static BuildableBitmapTextureAtlas mTextureOne;
	private static ITiledTextureRegion mRegionOne;

	private static BuildableBitmapTextureAtlas mTextureTwo;
	private static ITiledTextureRegion mRegionTwo;

	private static BuildableBitmapTextureAtlas mTextureThree;
	private static ITiledTextureRegion mRegionThree;

	private static GameActivity mGameActivity;

	private static GenericPool< Astroid > mPool;

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
		mTextureOne = new BuildableBitmapTextureAtlas(mAct.getTextureManager(), 512, 128, TextureOptions.BILINEAR);
		mTextureTwo = new BuildableBitmapTextureAtlas(mAct.getTextureManager(), 512, 128, TextureOptions.BILINEAR);
		mTextureThree = new BuildableBitmapTextureAtlas(mAct.getTextureManager(), 512, 128, TextureOptions.BILINEAR);

		mRegionOne = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mTextureOne, mAct, "astroid_1.png", 4, 1);
		mRegionTwo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mTextureOne, mAct, "astroid_2.png", 4, 1);
		mRegionThree = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mTextureOne, mAct, "astroid_3.png", 4, 1);

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

				Astroid roid = new Astroid( -64 , -64 , 64 , 64 , mRegion , mGameActivity.getVertexBufferObjectManager() );
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

		mTextureOne.load();
		mTextureTwo.load();
		mTextureThree.load();
	}

	public static Astroid create( float x , float y ) {
		Astroid sstroid = mPool.obtainPoolItem();
		sstroid.setPosition( x , y );
		sstroid.setCurrentTileIndex( MathUtils.random( 0 , 3 ) );
		return sstroid;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
