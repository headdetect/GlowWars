package com.headdetect.glowwars;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.BitmapFont;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.headdetect.glowwars.activities.GameActivity;

public class TextUtils {

	// ===========================================================
	// Constants
	// ===========================================================

	public static TextOptions HORIZONTAL_ALIGN_CENTER = new TextOptions(HorizontalAlign.CENTER);
	public static TextOptions HORIZONTAL_ALIGN_LEFT = new TextOptions(HorizontalAlign.LEFT);
	public static TextOptions HORIZONTAL_ALIGN_RIGHT = new TextOptions(HorizontalAlign.RIGHT);

	// ===========================================================
	// Fields
	// ===========================================================

	private static GameActivity mGameActivity;
	private static BitmapFont mFont;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void prepare(GameActivity mGame) {
		mGameActivity = mGame;
		mFont = new BitmapFont(mGameActivity.getTextureManager(), mGameActivity.getAssets(), "font/origin_bold_14.fnt");
		
	}
	
	public static void buildFonts() {
		if ( mGameActivity == null ) {
			throw new NullPointerException( "TextUtils.prepare(GameActivity) must be called first" );
		}
		
		mFont.load();
	}

	public static Text getText(String text) {
		return getText(text, HORIZONTAL_ALIGN_CENTER);
	}

	public static Text getText(String text, TextOptions options) {
		return getText(new Vector2(), text, options);
	}

	public static Text getText(Vector2 Pos, String text, TextOptions options) {
		return getText(Pos, text, options, 1);
	}

	public static Text getText(Vector2 pos, String text, TextOptions options, float scale) {
		if ( mGameActivity == null ) {
			throw new NullPointerException( "TextUtils.prepare(GameActivity) must be called first" );
		}
		
		Text txt = new Text(pos.x, pos.y, mFont, text, options, mGameActivity.getVertexBufferObjectManager());
		txt.setScaleCenter(txt.getWidth() / 2, txt.getHeight() / 2);
		txt.setScale(scale);
		txt.setColor(Color.WHITE);
		return txt;
	}

	public static float measureString(String toMeasure) {
		Text txt = getText(toMeasure, HORIZONTAL_ALIGN_CENTER);
		return txt.getWidth();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}