package com.headdetect.glowwars.sprites;

import static com.headdetect.glowwars.activities.GameActivity.CAMERA_HEIGHT;
import static com.headdetect.glowwars.activities.GameActivity.CAMERA_WIDTH;

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

	// ===========================================================
	// Constructors
	// ===========================================================

	private ButtonShoot( float pX , float pY , float pWidth , float pHeight , ITiledTextureRegion pTiledTextureRegion , VertexBufferObjectManager pTiledSpriteVertexBufferObject ) {
		super( pX , pY , pWidth , pHeight , pTiledTextureRegion , pTiledSpriteVertexBufferObject );

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
}
