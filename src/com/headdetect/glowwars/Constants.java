package com.headdetect.glowwars;

import org.andengine.extension.physics.box2d.PhysicsFactory;

import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Constants {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final FixtureDef LASER_FIXTURE = PhysicsFactory.createFixtureDef( .5f , .5f , 0f );
	public static final FixtureDef ASTROID_FIXTURE = PhysicsFactory.createFixtureDef( 1f , .2f , 0f );
	public static final FixtureDef SHIP_FIXTURE = PhysicsFactory.createFixtureDef( 1f , 1f , 0f );
	
	
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Interface Overrides
	// ===========================================================

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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
