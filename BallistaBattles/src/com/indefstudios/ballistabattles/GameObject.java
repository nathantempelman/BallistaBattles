package com.indefstudios.ballistabattles;

public abstract class GameObject {
	float height = 1.8f;
	float width = .5f;
	float x;
	float y;
	
	//to get rid of them after x frames and the like.
	boolean isDead = false;
	int framesDead = 0;
}
