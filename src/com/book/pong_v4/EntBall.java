package com.book.pong_v4;

import android.graphics.PointF;
import android.graphics.RectF;

import com.book.simplegameengine_v4.SGEntity;
import com.book.simplegameengine_v4.SGWorld;

public class EntBall extends SGEntity 
{	
	private static final float MAX_SPEED = 480.0f;
	
	public static final int STATE_ROLL_CW = 0x01;
	
	public static final int COLLISION_NONE = 0;
	public static final int COLLISION_EDGE = 1;
	public static final int COLLISION_OPPONENT = 2;
	public static final int COLLISION_PLAYER = 3;
	
	private int		mCollisionState;
	private float 	mCosTable[] = new float[10];
	private int 	mDifficultyOffset = 0;
	private boolean	mHasCollided;
	private float 	mSinTable[] = new float[10];
	private float 	mSpeed;
	public PointF 	mVelocity = new PointF();
	
	public EntBall(SGWorld world, PointF position, PointF dimensions, int difficultyOffset) 
	{
		super(world, GameModel.BALL_ID, "ball", position, dimensions);
		
		mDifficultyOffset = difficultyOffset;
		
		float radianFactor = (float) (Math.PI / 180);
		
		mCosTable[0] = (float) Math.cos(50 * radianFactor);
		mCosTable[1] = (float) Math.cos(40 * radianFactor);
		mCosTable[2] = (float) Math.cos(30 * radianFactor);
		mCosTable[3] = (float) Math.cos(20 * radianFactor);
		mCosTable[4] = (float) Math.cos(10 * radianFactor);
		mCosTable[5] = (float) Math.cos(350 * radianFactor);
		mCosTable[6] = (float) Math.cos(340 * radianFactor);
		mCosTable[7] = (float) Math.cos(330 * radianFactor);
		mCosTable[8] = (float) Math.cos(320 * radianFactor);
		mCosTable[9] = (float) Math.cos(310 * radianFactor);
		
		mSinTable[0] = (float) Math.sin(50 * radianFactor);
		mSinTable[1] = (float) Math.sin(40 * radianFactor);
		mSinTable[2] = (float) Math.sin(30 * radianFactor);
		mSinTable[3] = (float) Math.sin(20 * radianFactor);
		mSinTable[4] = (float) Math.sin(10 * radianFactor);
		mSinTable[5] = (float) Math.sin(350 * radianFactor);
		mSinTable[6] = (float) Math.sin(340 * radianFactor);
		mSinTable[7] = (float) Math.sin(330 * radianFactor);
		mSinTable[8] = (float) Math.sin(320 * radianFactor);
		mSinTable[9] = (float) Math.sin(310 * radianFactor);		

		calculateSpeed(0);
		
		mVelocity.x = mSpeed * mCosTable[0];
		mVelocity.y = mSpeed * mSinTable[0];
		
		addFlags(STATE_ROLL_CW);
	}
	
	@Override
	public void step(float elapsedTimeInSeconds) 
	{
		move(mVelocity.x * elapsedTimeInSeconds, mVelocity.y * elapsedTimeInSeconds);
		
		RectF 		ballBB = getBoundingBox();
		EntPaddle 	collidedPaddle = null;
		GameModel 	model = (GameModel)getWorld();
		EntPaddle 	player = model.getPlayer();
		EntOpponent	opponent = model.getOpponent();
		RectF 		opponentBB = opponent.getBoundingBox();
		RectF 		playerBB = player.getBoundingBox();		
		
		if(model.collisionTest(ballBB, playerBB)) 
		{
			collidedPaddle = player;
		}
		else if(model.collisionTest(ballBB, opponentBB))
		{
			collidedPaddle = opponent;
		}
			
		if(collidedPaddle != null) 
		{
			float 	ballDimensionX = getDimensions().x;
			float 	ballPositionY = getPosition().y;
			RectF 	paddleBB = collidedPaddle.getBoundingBox();
			float 	paddlePositionY = collidedPaddle.getPosition().y;
			float 	sectorSize = collidedPaddle.getSectorSize();
			int 	sector;	
			
			mSpeed += 10;		
			
			if(ballPositionY < paddlePositionY) 
			{
				sector = 0;
			}
			else 
			{
				float deltaPosition = ballPositionY - paddlePositionY;
				sector = (int) Math.ceil(deltaPosition / sectorSize);
			}
			
			if(collidedPaddle.getId() == GameModel.PLAYER_ID)
			{
				mHasCollided = true;
				mCollisionState = COLLISION_PLAYER;
				
				setPosition(paddleBB.right, ballPositionY);
				mVelocity.x = mSpeed * mCosTable[sector];
				
				player.addFlags(EntPaddle.STATE_HIT);
				opponent.removeFlags(EntPaddle.STATE_HIT);
				
				if(sector <= 4) 
				{
					removeFlags(EntBall.STATE_ROLL_CW);
				}
				else 
				{
					addFlags(EntBall.STATE_ROLL_CW);
				}
			}
			else if(collidedPaddle.getId() == GameModel.OPPONENT_ID)
			{
				mHasCollided = true;
				mCollisionState = COLLISION_OPPONENT;
				
				setPosition(paddleBB.left - ballDimensionX, ballPositionY);
				mVelocity.x = -(mSpeed * mCosTable[sector]);
				
				opponent.addFlags(EntPaddle.STATE_HIT);
				player.removeFlags(EntPaddle.STATE_HIT);
				
				if(sector <= 4) 
				{
					addFlags(EntBall.STATE_ROLL_CW);
				}
				else 
				{
					removeFlags(EntBall.STATE_ROLL_CW);
				}
			}
			
			mVelocity.y = -(mSpeed * mSinTable[sector]);
		}
		else 
		{
			/*if(mCollisionType != COLLISION_EDGE)
			{
				mCollisionType = COLLISION_NONE;
			}*/
			
			opponent.removeFlags(EntPaddle.STATE_HIT);
			player.removeFlags(EntPaddle.STATE_HIT);
		}
		
		if(mHasCollided)
		{
			mHasCollided = false;
		}
		else
		{
			mCollisionState = COLLISION_NONE;
		}
	}
	
	public float calculateSpeed(int playerScore) 
	{
		float playerScoreSqr = (playerScore + 8 + mDifficultyOffset) * (playerScore + 8 + mDifficultyOffset);
		mSpeed = (playerScoreSqr / (150 + playerScoreSqr)) * MAX_SPEED;
		return mSpeed;
	}
	
	public int getCollisionType() { return mCollisionState; }
	public PointF getVelocity() { return mVelocity; }
	
	public boolean hasCollided() { return mHasCollided; }

	public void setCollisionType(int collisionType) { mCollisionState = collisionType; }
	public void setHasCollided(boolean hasCollided) { mHasCollided = hasCollided; }
	public void setVelocity(float speedX, float speedY) { mVelocity.set(speedX, speedY); }
} 

