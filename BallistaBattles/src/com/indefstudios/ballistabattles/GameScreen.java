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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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
    //scenery	: 0x0002; // 0000000000000010
    //boulder	: 0x0004; // 0000000000000100
    
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
        groundFixture.friction=0f;
        groundFixture.filter.categoryBits = CATAGORY_SCENERY;
        groundFixture.filter.maskBits = MASK_SCENERY;
        groundBody.createFixture(groundFixture);
        groundshape.dispose();
        
//        //Dynamic Body  
//        BodyDef bodyDef = new BodyDef();  
//        bodyDef.type = BodyType.DynamicBody;  
//        bodyDef.position.set(50, 5);  
//        Body body = world.createBody(bodyDef);  
//        CircleShape dynamicCircle = new CircleShape();  
//        dynamicCircle.setRadius(1f);  
//        FixtureDef fixtureDef = new FixtureDef();  
//        fixtureDef.shape = dynamicCircle;  
//        fixtureDef.density = 1.0f;  
//        fixtureDef.friction = 0.0f;  
//        fixtureDef.restitution = 1;  
//        body.createFixture(fixtureDef);  
//        
//        dynamicCircle.dispose();
        
        debugRenderer = new Box2DDebugRenderer();
   
		
	}
	//Density of rocks, possibly our upgrade system? Gold rocks seems badass.
//			Shale: 2.75 x 1000 kg/m3 = 2750 kg/m3
//			Granite: 2.65 x 1000 kg/m3 = 2650 kg/m3
//			Sandstone: 2.2 x 1000 kg/m3 = 2200 kg/m3
//			Basalt: 2.65 x 1000 kg/m3 = 2650 kg/m3
//			Marble: 2.7 x 1000 kg/m3 = 2700 kg/m3
//			Limestone: 2.45 x 1000 kg/m3 = 2450 kg/m3
//			Steel: 7.85 x 1000 kg/m3 = 7850 kg/m3
//			Gold: 14 x 1000 kg/m3 = 14,000 kg/m3
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
				notTooouchingYou = false;//I'm touching you now... bitch!?
			}
			else//it was being touched before
			{
				touchX=Gdx.input.getX();
				touchY=Gdx.input.getY();
			}
		}
		else
		{
			if(!notTooouchingYou)//it was being touched, now it is not. I really shouldn't have let my inner nathan 
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
						if(((GameObject) thing).framesDead>100)
						{
							world.destroyBody(b);
						}
						else
						{
							((GameObject) thing).framesDead++;
						}
					}
					
					if(thing instanceof Enemy)//this is probably bad design, could make enemy and boulder inherit from 
					{									//something, but for now it doesn't matter. plus this includes static bodies?
						//set linear velocity to some kind of constant here
						com.badlogic.gdx.physics.box2d.Contact contact;
						
					}
					
				}
			}
			
		}
		
		
	}

	private void spawnEnemy() {
//		BodyDef bodyDef = new BodyDef();  
//        bodyDef.type = BodyType.DynamicBody;  
//        bodyDef.position.set(camera.viewportWidth, 5);  
//        Body body = world.createBody(bodyDef);
//       
//        PolygonShape enemyBox = new PolygonShape();
//        enemyBox.setAsBox(.5f, 1f);
//        FixtureDef fixtureDef = new FixtureDef();  
//        fixtureDef.shape = enemyBox;  
//        fixtureDef.density = 1.0f;  
//        fixtureDef.friction = .3f;  
//        fixtureDef.restitution = .5f;  
//        body.createFixture(fixtureDef);  
//        enemyBox.dispose();
//        body.setLinearVelocity(new Vector2(-5,0));
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
