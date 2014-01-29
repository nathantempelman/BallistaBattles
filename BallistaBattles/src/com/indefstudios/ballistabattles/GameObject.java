package com.indefstudios.ballistabattles;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class GameObject {
	float height = 1.8f;
	float width = .5f;
	float x;
	float y;
	Body body;
	
	//to get rid of them after x frames and the like.
	boolean isDead = false;
	int framesDead = 0;
}
