package com.book.pong_v5;

import android.graphics.PointF;
import android.util.Log;

import com.book.simplegameengine_v5.SGEntity;
import com.book.simplegameengine_v5.SGTrigger;
import com.book.simplegameengine_v5.SGWorld;

public class TrgRightGoal extends SGTrigger 
{
	public TrgRightGoal(SGWorld world, PointF position, PointF dimensions)
	{
		super(world, position, dimensions);
	}

	@Override
	public void onHit(SGEntity entity, float elapsedTimeInSeconds)
	{		
		if(isActive())
		{
			GameModel model = (GameModel)getWorld();
			model.increasePlayerScore();
			//model.setCurrentState(GameModel.STATE_RESTART);
			
			if(model.getPlayerScore() == 10)
			{
				model.getOpponent().addFlags(EntPaddle.STATE_CONCERNED);
				model.getOpponent().removeFlags(EntPaddle.STATE_HAPPY);
			}
			else if(model.getPlayerScore() == 20)
			{
				model.getOpponent().addFlags(EntPaddle.STATE_ANGRY);
				model.getOpponent().removeFlags(EntPaddle.STATE_CONCERNED);
			}
			
			Log.d("PongV5", "Jogador marca um ponto!");
			model.logScore();
			
			EntBall ball = (EntBall)entity;
			ball.calculateSpeed(model.getPlayerScore());
			
			EntOpponent opponent = model.getOpponent();
			opponent.calculateSpeed(model.getPlayerScore());
			opponent.increaseReaction();
			
			model.setWhoScored(GameModel.PLAYER_ID);
			model.setCurrentState(GameModel.STATE_GOAL);
			setIsActive(false);
		}
	}
}
