package com.headdetect.glowwars.sprites;

import java.io.IOException;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import com.headdetect.glowwars.activities.GameActivity;

public class Laser extends Sprite {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	private static ITexture mTexture;
	private static ITextureRegion mRegion;
	private static GameActivity mGameActivity;
	
	private static GenericPool<Laser> mPool;

	// ===========================================================
	// Constructors
	// ===========================================================

	private Laser( float pX , float pY , float pWidth , float pHeight , ITextureRegion pTextureRegion , VertexBufferObjectManager pVertexBufferObjectManager ) {
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

	public static void prepareLaser(GameActivity mAct) {
		try {
			mTexture = new BitmapTexture(mAct.getTextureManager(), mAct.getTexture("gfx/laser.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		mRegion = TextureRegionFactory.extractFromTexture(mTexture);
		
		mGameActivity = mAct;
		
		mPool = new GenericPool<Laser> ( 24 ) {
			 
			@Override 
			public Laser onAllocatePoolItem() {
				return new Laser(-16, -16, 16, 16, mRegion, mGameActivity.getVertexBufferObjectManager());
			}
			
			@Override
			protected void onHandleRecycleItem( final Laser sprt ) {
				sprt.setIgnoreUpdate( true );
				sprt.setVisible( false );
			}
			
		};
	}
	
	public static void buildTexture() {
		if(mGameActivity == null){
			throw new NullPointerException("You must call Laser.prepareLaser(GameActivity) before calling");
		}
		
		mTexture.load();
	}
	
	public static Laser create(float x, float y) {
		Laser laser = mPool.obtainPoolItem();
		laser.setPosition( x , y );
		return laser;
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}


