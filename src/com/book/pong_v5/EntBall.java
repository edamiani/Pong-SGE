package com.book.pong_v5;

import android.graphics.PointF;
import android.graphics.RectF;

import com.book.simplegameengine_v5.SGEntity;
import com.book.simplegameengine_v5.SGWorld;

public class EntBall extends SGEntity 
{
	public static final int COLLISION_NONE = 0;
	public static final int COLLISION_PLAYER = 1;
	public static final int COLLISION_OPPONENT = 2;
	public static final int COLLISION_EDGE = 3;
	
	private static final float MAX_SPEED = 480.0f;
	
	public static final int STATE_ROLL_CCW = 0x01;
	public static final int STATE_ROLL_CW = 0x02;
	
	private int		mCollisionType = COLLISION_NONE;
	private int		mDifficultyOffset = 0;
	private float 	mSpeed;
	public PointF 	mVelocity = new PointF();
	
	private float 	mCosTable[] = new float[10];
	private float 	mSinTable[] = new float[10];
	
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
		
		addFlags(STATE_ROLL_CCW);
	}
	
	@Override
	public void step(float elapsedTimeInSeconds)
	{
		GameModel model = (GameModel)getWorld();
		EntPaddle player = model.getPlayer();
		EntOpponent opponent = model.getOpponent();
		
		move(mVelocity.x * elapsedTimeInSeconds, mVelocity.y * elapsedTimeInSeconds);
		
		if(model.collisionTest(this.getBoundingBox(), player.getBoundingBox()))
		{
			mCollisionType = COLLISION_PLAYER;
			
			setPosition(player.getBoundingBox().right, getPosition().y);
			
			mSpeed += 10;
			
			int sector = calculateSector(model, player);
			
			mVelocity.x = mSpeed * mCosTable[sector];
			mVelocity.y = -(mSpeed * mSinTable[sector]);
			
			player.addFlags(EntPaddle.STATE_HIT);
			opponent.removeFlags(EntPaddle.STATE_HIT);
			
			if(sector <= 4)
			{
				addFlags(EntBall.STATE_ROLL_CCW);
				removeFlags(EntBall.STATE_ROLL_CW);
			}
			else
			{
				addFlags(EntBall.STATE_ROLL_CW);
				removeFlags(EntBall.STATE_ROLL_CCW);
			}
		}
		else if(model.collisionTest(this.getBoundingBox(), opponent.getBoundingBox()))
		{
			mCollisionType = COLLISION_OPPONENT;
			
			setPosition(opponent.getBoundingBox().left - getDimensions().x, getPosition().y);
			
			mSpeed += 10;

			int sector = calculateSector(model, opponent);
			
			mVelocity.x = -(mSpeed * mCosTable[sector]);
			mVelocity.y = -(mSpeed * mSinTable[sector]);
			
			opponent.addFlags(EntPaddle.STATE_HIT);
			player.removeFlags(EntPaddle.STATE_HIT);
			
			if(sector <= 4)
			{
				addFlags(EntBall.STATE_ROLL_CW);
				removeFlags(EntBall.STATE_ROLL_CCW);
			}
			else
			{
				addFlags(EntBall.STATE_ROLL_CCW);
				removeFlags(EntBall.STATE_ROLL_CW);
			}
		}
		else
		{
			mCollisionType = COLLISION_NONE;
			
			opponent.removeFlags(EntPaddle.STATE_HIT);
			player.removeFlags(EntPaddle.STATE_HIT);
		}
	}
	
	public int calculateSector(GameModel model, EntPaddle entity)
	{
		float ballCenterY = getPosition().y + (getDimensions().y / 2);
		RectF opponentBoundingBox = entity.getBoundingBox();
		float sectorSize = entity.getSectorSize();
		int sector;
		
		if(ballCenterY < opponentBoundingBox.top)
		{
			sector = 0;
		}
		else if(ballCenterY > opponentBoundingBox.bottom)
		{
			sector = 9;
		}
		else
		{
			sector = (int) Math.ceil((ballCenterY - opponentBoundingBox.top) / sectorSize);
		}
		
		return sector;
	}
	
	public float calculateSpeed(int playerScore)
	{
		float playerScoreSqr = (playerScore + 8 + mDifficultyOffset) * (playerScore + 8 + mDifficultyOffset);
		mSpeed = (playerScoreSqr / (150 + playerScoreSqr)) * MAX_SPEED;
		
		return mSpeed;
	}
	
	public int		getCollisionType() { return mCollisionType; }
	public float 	getSpeed() { return mSpeed; }
	public PointF 	getVelocity() { return mVelocity; }
	
	public void		setCollisionType(int collisionType) { mCollisionType = collisionType; }
	public void 	setVelocity(float speedX, float speedY) { mVelocity.set(speedX, speedY); }
}
