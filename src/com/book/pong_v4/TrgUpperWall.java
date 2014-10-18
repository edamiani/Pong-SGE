package com.book.pong_v4;

import android.graphics.PointF;

import com.book.simplegameengine_v4.SGEntity;
import com.book.simplegameengine_v4.SGTrigger;
import com.book.simplegameengine_v4.SGWorld;

public class TrgUpperWall extends SGTrigger  
{
	public TrgUpperWall(SGWorld world, PointF position, PointF dimensions) 
	{
		super(world, GameModel.TRG_UPPER_WALL_ID, position, dimensions);
	}
	
	@Override
	public void onHit(SGEntity entity, float elapsedTimeInSeconds) 
	{
		EntBall ball = (EntBall)entity;
		PointF ballVelocity = ball.getVelocity();
		
		ball.setPosition(ball.getPosition().x, 0);
		ball.setVelocity(ballVelocity.x, -ballVelocity.y);
		
		if(ball.getVelocity().x > 0) 
		{
			removeFlags(EntBall.STATE_ROLL_CW);
		}
		else 
		{
			addFlags(EntBall.STATE_ROLL_CW);
		}
		
		ball.setHasCollided(true);
		ball.setCollisionType(EntBall.COLLISION_EDGE);
	}
}

