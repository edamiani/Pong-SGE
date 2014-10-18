package com.book.pong_v4;

import android.graphics.PointF;
import android.util.Log;

import com.book.simplegameengine_v4.SGEntity;
import com.book.simplegameengine_v4.SGTrigger;
import com.book.simplegameengine_v4.SGWorld;

public class TrgRightGoal extends SGTrigger 
{
	public TrgRightGoal(SGWorld world, PointF position, PointF dimensions) 
	{
		super(world, GameModel.TRG_RIGHT_GOAL_ID, position, dimensions);
	}
	
	@Override
	public void onHit(SGEntity entity, float elapsedTimeInSeconds) 
	{
		GameModel model = (GameModel)getWorld();
		
		model.increasePlayerScore();
		
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
		
		EntOpponent opponent = model.getOpponent();
		opponent.calculateSpeed(model.getPlayerScore());
		opponent.decreaseReaction();
		
		Log.d("PongV2", "Jogador marca um ponto!");
		model.logScore();
		
		model.setCurrentState(GameModel.STATE_GOAL);
		model.setWhoScored(GameModel.PLAYER_ID);
	}
}
