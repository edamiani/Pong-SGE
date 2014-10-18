package com.book.pong_v4;

import android.graphics.Point;
import android.graphics.PointF;

import com.book.simplegameengine_v4.SGEntity;
import com.book.simplegameengine_v4.SGTrigger;
import com.book.simplegameengine_v4.SGWorld;

public class TrgLowerWall extends SGTrigger 
{
	public TrgLowerWall(SGWorld world, PointF position, PointF dimensions) 
	{
		super(world, GameModel.TRG_LOWER_WALL_ID, position, dimensions);
	}
	
	@Override
	public void onHit(SGEntity entity, float elapsedTimeInSeconds) 
	{
		Point worldDimensions = getWorld().getDimensions();
		if(entity.getId() == GameModel.PLAYER_ID) 
		{
			entity.setPosition(entity.getPosition().x, worldDimensions.y - entity.getDimensions().y);
		}
		else if(entity.getId() == GameModel.OPPONENT_ID) 
		{
			EntOpponent opponent = (EntOpponent)entity;
			opponent.setPosition(entity.getPosition().x, worldDimensions.y - entity.getDimensions().y);
		}
		else // (entity.getId() == GameModel.BALL_ID)
		{
			EntBall ball = (EntBall)entity;
			ball.setPosition(ball.getPosition().x, worldDimensions.y - ball.getDimensions().y);
			ball.setVelocity(ball.getVelocity().x, -ball.getVelocity().y);
			
			if(ball.getVelocity().x > 0) 
			{
				addFlags(EntBall.STATE_ROLL_CW);
			}
			else 
			{
				removeFlags(EntBall.STATE_ROLL_CW);
			}
			
			ball.setHasCollided(true);
			ball.setCollisionType(EntBall.COLLISION_EDGE);
		}
	}
}
