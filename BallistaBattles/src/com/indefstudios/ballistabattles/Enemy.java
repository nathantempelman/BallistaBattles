package com.indefstudios.ballistabattles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Enemy extends GameObject{
	int health = 100;
	float height = 1.8f;
	float width = .5f;
	float x;
	float y;
	final short CATAGORY_ENEMY=0x0001;	//001
	final short MASK_ENEMY = 0x0006;	//110

	public Enemy(World world)
	{
		BodyDef bodyDef = new BodyDef();  
        bodyDef.type = BodyType.DynamicBody;  
        bodyDef.position.set(30, 3.2f);  
        
        Body body = world.createBody(bodyDef);
       
        PolygonShape enemyBox = new PolygonShape();
        enemyBox.setAsBox(width/2, height/2);
        FixtureDef fixtureDef = new FixtureDef();  
        fixtureDef.shape = enemyBox;  
        fixtureDef.density = 900.0f;  
        fixtureDef.friction = .5f;  
        fixtureDef.restitution = .5f;
        fixtureDef.filter.categoryBits = CATAGORY_ENEMY;
        fixtureDef.filter.maskBits = MASK_ENEMY;
        //fixtureDef.isSensor=true;
        body.createFixture(fixtureDef);  
        enemyBox.dispose();
        body.setLinearVelocity(new Vector2(-5,0));
       
        body.setUserData(this);
	}
}
