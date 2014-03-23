package com.book.pong_v5;

import android.graphics.PointF;

import com.book.simplegameengine_v5.SGWorld;

public class EntOpponent extends EntPaddle 
{
	public static final float MAX_SPEED = 240;
	public static final float MIN_REACTION = 20;
	
	private int			mDifficultyOffset = 0;
	private float		mReaction;
	private float		mSpeed;
	
	public EntOpponent(SGWorld world, PointF position, PointF dimensions, int difficultyOffset) 
	{
		super(world, GameModel.OPPONENT_ID, position, dimensions);
		
		mDifficultyOffset = difficultyOffset;

		mReaction = dimensions.y / 2;
		
		for(int i = 0; i < mDifficultyOffset; i++)
		{
			increaseReaction();
		}
		
		calculateSpeed(0);
	}
	
	public void calculateSpeed(int playerScore)
	{
		float playerScoreSqr = (playerScore + 5 + mDifficultyOffset) * (playerScore + 5 + mDifficultyOffset);
		mSpeed = (playerScoreSqr / (150 + playerScoreSqr)) * MAX_SPEED;
	}
	
	public void increaseReaction()
	{
		if(--mReaction < MIN_REACTION)
		{
			mReaction = MIN_REACTION;
		}
	}
	
	@Override
	public void step(float elapsedTimeInSeconds)
	{
		PointF position = getPosition();
		PointF dimensions = getDimensions();
		GameModel model = (GameModel)getWorld();
		
		float paddleCenterY = position.y + (dimensions.y / 2);		
		float reactionTop = paddleCenterY - mReaction;
		float reactionBottom = paddleCenterY + mReaction;
		
		EntBall ball = model.getBall();
		float ballCenterY = ball.getPosition().y + (ball.getDimensions().y / 2);
		
		if(reactionTop > ballCenterY)
		{
			move(0, -(mSpeed * elapsedTimeInSeconds));
		}
		else if(reactionBottom < ballCenterY)
		{
			move(0, mSpeed * elapsedTimeInSeconds);
		}
		
		float opponentCenterY = (getPosition().y + ((getBoundingBox().bottom - getBoundingBox().top) / 2));
		
		if(opponentCenterY > ballCenterY)
		{
			addFlags(EntPaddle.STATE_LOOKING_UP);
		}
		else
		{
			removeFlags(EntPaddle.STATE_LOOKING_UP);
		}
	}
	
	public float getReaction() { return mReaction; }
	public float getSpeed() { return mSpeed; }

	public void setReaction(float reaction) { mReaction = reaction; }
	public void setSpeed(float speed) { mSpeed = speed; }
}
