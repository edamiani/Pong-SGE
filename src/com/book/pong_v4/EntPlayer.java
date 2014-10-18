package com.book.pong_v4;

import android.graphics.PointF;

import com.book.simplegameengine_v4.SGWorld;

public class EntPlayer extends EntPaddle 
{
	public EntPlayer(SGWorld world, PointF position, PointF dimensions) 
	{
		super(world, GameModel.PLAYER_ID, position, dimensions);
	}
	
	@Override
	public void step(float elapsedTimeInSeconds) 
	{
		float playerHeight = getBoundingBox().bottom - getBoundingBox().top;
		float playerCenterY = getPosition().y + (playerHeight / 2);
		
		EntBall ball = ((GameModel)getWorld()).getBall();
		float ballCenterY = (ball.getPosition().y + ball.getDimensions().y / 2);
		
		if(playerCenterY > ballCenterY) 
		{
			addFlags(EntPaddle.STATE_LOOKING_UP);
		}
		else 
		{
			removeFlags(EntPaddle.STATE_LOOKING_UP);
		}
	}
}

