package com.indefstudios.ballistabattles;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
	final BallistaBattles game;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	
	static final float WORLD_TO_BOX=0.01f;  
    static final float BOX_WORLD_TO=100f;
    World world; 
    Box2DDebugRenderer debugRenderer;  
	
    int framesSinceLastEnemySpawn=0;
    
    boolean notTooouchingYou = true;;
    int touchX=0;
    int touchY=0;
    int startTouchX=0;
    int startTouchY=0;
    
    
    //Filter catagories
    //enemy		: 0x0001;  // 0000000000000001
    //scenery	: 0x0002;  // 0000000000000010
    //boulder	: 0x0004;  // 0000000000000100
    
    //Filter masks
    //enemy		: scenery|boulder
    //scenery	: boulder|enemy
    //boulder 	: boulder|enemy|scenery
    
	final short CATAGORY_SCENERY=0x0002;	//100
	final short MASK_SCENERY = -1;			//ones all the way downnnn, because two's comp.
    
	public GameScreen(final BallistaBattles gam) {	
		this.game = gam;
		world = new World(new Vector2(0, -10), true);
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		
		
		
//		camera = new OrthographicCamera(20f, h);
//		batch = new SpriteBatch();
//		
//		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
//		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//		
//		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
//		
//		sprite = new Sprite(region);
//		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
//		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
//		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		
		camera = new OrthographicCamera(30,20);  
       // camera.viewportHeight = 320;  
       // camera.viewportWidth = 480;  
         
		
		//N: this line makes the camera show the positive quadrant of the 2d graph based coordinate system
		// eg: negative coords will be to the left or below the screen.
//		camera.setToOrtho(false,30,20);
		camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
        camera.update();  
        
        createGround();
        createCollisionListener();
        
        debugRenderer = new Box2DDebugRenderer();
   
		
	}

	private void createGround()
	{
		//Ground body  
        BodyDef groundBodyDef =new BodyDef();  
        
        //seems to be the center of the body it's setting
        groundBodyDef.position.set(new Vector2(camera.viewportWidth/2,1)); 
        
        Body groundBody = world.createBody(groundBodyDef);  
        PolygonShape groundshape = new PolygonShape();  
        groundshape.setAsBox(30, 1.0f);  
        //groundBody.createFixture(groundshape, 0.0f); 
        FixtureDef groundFixture = new FixtureDef();
        groundFixture.density=0.0f;
        groundFixture.shape = groundshape;
        groundFixture.restitution = .5f;
        groundFixture.friction=.8f;
        groundFixture.filter.categoryBits = CATAGORY_SCENERY;
        groundFixture.filter.maskBits = MASK_SCENERY;
        groundBody.createFixture(groundFixture);
        groundshape.dispose();
	}
	private void createCollisionListener() {
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Gdx.app.log("beginContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
                
                Object A = fixtureA.getBody().getUserData();
                Object B = fixtureB.getBody().getUserData();
                
                if(A instanceof GameObject && B instanceof GameObject)
                {
                	Gdx.app.log("Collision","Boom, headshot");
                	((GameObject)A).isDead=true;
                	((GameObject)B).isDead=true;
                	
                	//considering having boulders explode into smaller boulders
                	//or maybe some kind of upgrade system with bomb boulders, etc
                	// this would be where to make that happen.
                	if(A instanceof Boulder)
                	{
                		
                	}
                	if(B instanceof Boulder)
                	{
                		
                	}
                }
                
                
                
                
//                if(fixtureA.getUserData() instanceof Enemy||fixtureA.getUserData() instanceof Boulder)
//                {
//                	((Enemy)fixtureA.getUserData()).isDead = true;
//                }
//                if(fixtureB.getUserData() instanceof Enemy||fixtureA.getUserData() instanceof Boulder)
//                {
//                	((Enemy)fixtureA.getUserData()).isDead = true;
//                }
//                if((fixtureA.getUserData() instanceof Enemy&&fixtureB.getUserData() instanceof Boulder)
//                		|| fixtureB.getUserData() instanceof Enemy && fixtureA.getUserData() instanceof Boulder)
//                {
//                	
//                }
            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if(fixtureA==null||fixtureB==null)
                {
                	Gdx.app.log("endContact", "between some NULLS");
                	return;
                }
                Gdx.app.log("endContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
            }
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}

        });
    }
	public void launchBoulder(Vector2 lastTouch)
	{

        Vector2 impulse = new Vector2(5,10);
        impulse.sub(lastTouch);
        
        Boulder b = new Boulder(world,impulse);

	}
	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render(float delta) {	
		
//		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//		
//		batch.setProjectionMatrix(camera.combined);
//		batch.begin();
//		sprite.draw(batch);
//		batch.end();
//		
		
//		if (Gdx.input.isTouched()) {
//			if(!notTooouchingYou)//it was being touched before
//			{
////				Vector3 vec = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
////				camera.unproject(vec);
//				
//				touchX=Gdx.input.getX();
//				touchY=Gdx.input.getY();
//			}
//			else
//			{
//				notTooouchingYou =false;
//				startTouchX=Gdx.input.getX();
//				startTouchY=Gdx.input.getY();
//			}
//			
//			//launchBoulder(100,200);
//		}
//		else
//		{
//			if(!notTooouchingYou)//it was being touched, now it is not. I really shouldn't have let my inner nathan 
//			{						// name that variable.
//				int xchange = startTouchX - touchX;
//				int ychange = startTouchY - touchY;
//				ychange*=-1;// for some reason while the coord system is based on 1st quadrant, the input is in android native 
//							// pixels, top left origin. 
//				Vector3 impulse = new Vector3(xchange,ychange,0);
//				camera.unproject(impulse);
//				launchBoulder(new Vector2(impulse.x,impulse.y), xchange, ychange);
//				notTooouchingYou=true;
//			}
//		}
		
		if (Gdx.input.isTouched()) {	
			if(notTooouchingYou)
			{
				notTooouchingYou = false;//I'm touching you now..
			}
			else//it was being touched before
			{
				touchX=Gdx.input.getX();
				touchY=Gdx.input.getY();
			}
		}
		else
		{
			if(!notTooouchingYou)//it was being touched, now it is not. I really shouldn't have let my inner child 
			{						// name that variable.

				Vector3 lastTouch = new Vector3(touchX,touchY,0);
				camera.unproject(lastTouch);
				launchBoulder(new Vector2(lastTouch.x,lastTouch.y));
				notTooouchingYou=true;
			}
		}
		
		if(framesSinceLastEnemySpawn>45)
		{
			spawnEnemy();
			framesSinceLastEnemySpawn=0;
		}
		else
			framesSinceLastEnemySpawn++;
		
		debugRenderer.render(world, camera.combined);
		
		//suggested is , 8, 3). Higher is better accuracy, lower is better performance
		world.step(1/60f, 6, 2);
		sweepBodies();
		
		
		
	}

	private void sweepBodies() {
		//Delete bodies that fall off the screen
		Array<Body> bodylist = new Array<Body>();
		world.getBodies(bodylist);
		//Iterator<Body>=bodylist.iterator();
		for(int i=0;i<bodylist.size;i++)
		{
			Body b = bodylist.get(i);
			
			if(b.getPosition().y<0)
			{
				world.destroyBody(b);
			}
			
			if(b.getUserData()!=null)
			{
				Object thing = b.getUserData();
				if(thing instanceof GameObject)
				{
					if(((GameObject)thing).isDead)
					{
						if(((GameObject) thing).framesDead>180)
						{
							world.destroyBody(b);
						}
						else
						{
							((GameObject) thing).framesDead++;
						}
					}
					
					if(thing instanceof Enemy)
					{
						//set linear velocity to some kind of constant here
						if(!((Enemy)thing).isDead)
						{
							b.setLinearVelocity(-5, .15f);
						}
						
					}
					
				}
			}
			
		}
		
	}

	private void spawnEnemy() {

		Enemy e = new Enemy(world);
		
	}
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	@Override
	public void show() {
	
	}
 
	@Override
	public void hide() {
	}
}
