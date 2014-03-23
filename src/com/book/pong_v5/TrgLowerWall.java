package com.book.pong_v5;

import android.graphics.Point;
import android.graphics.PointF;

import com.book.simplegameengine_v5.SGEntity;
import com.book.simplegameengine_v5.SGTrigger;
import com.book.simplegameengine_v5.SGWorld;

public class TrgLowerWall extends SGTrigger 
{
	public TrgLowerWall(SGWorld world, PointF position, PointF dimensions)
	{
		super(world, position, dimensions);
	}
	
	@Override
	public void onHit(SGEntity entity, float elapsedTimeInSeconds)
	{
		Point worldDimensions = getWorld().getWorldDimensions();
		
		if(entity.getCategory() == "paddle")
		{
			entity.setPosition(entity.getPosition().x, worldDimensions.y - entity.getDimensions().y);
		}
		else
		{
			EntBall ball = (EntBall)entity;
			ball.setCollisionType(EntBall.COLLISION_EDGE);
			ball.setPosition(ball.getPosition().x, worldDimensions.y - ball.getDimensions().y);
			ball.setVelocity(ball.getVelocity().x, -ball.getVelocity().y);
			
			if(ball.getVelocity().x > 0)
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
	}
}
