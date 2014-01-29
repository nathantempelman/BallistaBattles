package com.indefstudios.ballistabattles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Boulder extends GameObject {
	int health = 100;
	float radius = 1f;
	float x;
	float y;
	final short CATAGORY_BOULDER=0x0004;	//100
	final short MASK_BOULDER = 0x0007;		//111

	public Boulder(World world)
	{
		BodyDef bodyDef = new BodyDef();  
        bodyDef.type = BodyType.DynamicBody;  
        bodyDef.position.set(5, 10);  
        Body body = world.createBody(bodyDef);  
        CircleShape dynamicCircle = new CircleShape();  
        dynamicCircle.setRadius(1f);  
        FixtureDef fixtureDef = new FixtureDef();  
        fixtureDef.shape = dynamicCircle;  
        fixtureDef.density = 2000.0f;  
        fixtureDef.friction = 1f;  
        fixtureDef.restitution = .5f;
        fixtureDef.filter.categoryBits = CATAGORY_BOULDER;
        fixtureDef.filter.maskBits = MASK_BOULDER;
        body.createFixture(fixtureDef);
        body.setUserData(this);
        dynamicCircle.dispose();
        
        
	}
	public Boulder(World world, Vector2 velocity)
	{
		BodyDef bodyDef = new BodyDef();  
        bodyDef.type = BodyType.DynamicBody;  
        bodyDef.position.set(5, 10);  
        Body body = world.createBody(bodyDef);  
        CircleShape dynamicCircle = new CircleShape();
        //I think radius here is actually diameter. More research required.
        dynamicCircle.setRadius(radius);  
        FixtureDef fixtureDef = new FixtureDef();  
        fixtureDef.shape = dynamicCircle;  
        fixtureDef.density = 2000.0f;  
        fixtureDef.friction = 1f;  
        fixtureDef.restitution = .5f;
        fixtureDef.filter.categoryBits = CATAGORY_BOULDER;
        fixtureDef.filter.maskBits = MASK_BOULDER;
        body.createFixture(fixtureDef);  
        
        //link the boulder box2d object with this class
        body.setUserData(this);
        
        body.setLinearVelocity(velocity.scl(2f));
        
        dynamicCircle.dispose();
	}
}
