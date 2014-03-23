package com.book.pong_v5;

import android.graphics.PointF;

import com.book.simplegameengine_v5.SGEntity;
import com.book.simplegameengine_v5.SGTrigger;
import com.book.simplegameengine_v5.SGWorld;

public class TrgUpperWall extends SGTrigger 
{
	public TrgUpperWall(SGWorld world, PointF position, PointF dimensions)
	{
		super(world, position, dimensions);
	}
	
	@Override
	public void onHit(SGEntity entity, float elapsedTimeInSeconds)
	{
		EntBall ball = (EntBall)entity;
		ball.setCollisionType(EntBall.COLLISION_EDGE);
		ball.setPosition(ball.getPosition().x, 0);
		ball.setVelocity(ball.getVelocity().x, -ball.getVelocity().y);
		
		if(ball.getVelocity().x > 0)
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
}
