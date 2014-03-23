package com.book.pong_v5;

import android.graphics.PointF;
import android.util.Log;

import com.book.simplegameengine_v5.SGEntity;
import com.book.simplegameengine_v5.SGTrigger;
import com.book.simplegameengine_v5.SGWorld;

public class TrgLeftGoal extends SGTrigger 
{
	public TrgLeftGoal(SGWorld world, PointF position, PointF dimensions)
	{
		super(world, position, dimensions);
	}
	
	@Override
	public void onHit(SGEntity entity, float elapsedTimeInSeconds)
	{
		if(isActive())
		{
			GameModel model = (GameModel)getWorld();
			model.increaseOpponentScore();
			//model.resetWorld();
			
			if(model.getOpponentScore() == 2)
			{
				model.getPlayer().addFlags(EntPaddle.STATE_CONCERNED);
				model.getPlayer().removeFlags(EntPaddle.STATE_HAPPY);
			}
			else if(model.getOpponentScore() == 4)
			{
				model.getPlayer().addFlags(EntPaddle.STATE_ANGRY);
				model.getPlayer().removeFlags(EntPaddle.STATE_CONCERNED);
			}
			
			Log.d("PongV5", "Oponente marca um ponto!");
			model.logScore();
			
			if(model.getOpponentScore() == 5)
			{
				model.setCurrentState(GameModel.STATE_GAME_OVER);
				Log.d("PongV5", "Fim de jogo!");
			}
			/*else
			{
				EntBall ball = (EntBall)entity;
				ball.calculateSpeed(model.getPlayerScore());
				model.setCurrentState(GameModel.STATE_RESTART);
			}*/
			
			model.setWhoScored(GameModel.OPPONENT_ID);
			model.setCurrentState(GameModel.STATE_GOAL);
			setIsActive(false);
		}
	}
}
