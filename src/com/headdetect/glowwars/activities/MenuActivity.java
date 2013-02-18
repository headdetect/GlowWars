package com.headdetect.glowwars.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.headdetect.glowwars.BuildConfig;
import com.headdetect.glowwars.R;
import com.headdetect.glowwars.Utils;

public class MenuActivity extends Activity {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private Button btnStart;

	private OnClickListener btnStartClickListener = new OnClickListener() {

		@Override
		public void onClick( final View view ) {
			Intent sillyIntent = new Intent(MenuActivity.this, GameActivity.class);
			sillyIntent.putExtra(GameActivity.SET_DIFFICULTY, GameActivity.DIFFICULTY_EASY);
			startActivity(sillyIntent);
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

	@SuppressWarnings( "unused" )
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		if ( BuildConfig.DEBUG && Utils.DEBUG ) {
			setContentView( R.layout.activity_menu_debug );
		} else {
			setContentView( R.layout.activity_menu );
		}

		// - Initialize Views - //

		btnStart = (Button) findViewById( R.id.btnStart );
		btnStart.setOnClickListener( btnStartClickListener );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.menu , menu );
		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
