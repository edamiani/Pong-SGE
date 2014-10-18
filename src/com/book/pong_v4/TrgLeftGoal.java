package com.book.pong_v4;

import android.graphics.PointF;
import android.util.Log;

import com.book.simplegameengine_v4.SGEntity;
import com.book.simplegameengine_v4.SGTrigger;
import com.book.simplegameengine_v4.SGWorld;

public class TrgLeftGoal extends SGTrigger 
{
	public TrgLeftGoal(SGWorld world, PointF position, PointF dimensions) 
	{
		super(world, GameModel.TRG_LEFT_GOAL_ID, position, dimensions);
	}
	
	@Override
	public void onHit(SGEntity entity, float elapsedTimeInSeconds) 
	{
		GameModel model = (GameModel)getWorld();
		
		model.increaseOpponentScore();
		
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
		
		Log.d("PongV2", "Oponente marca um ponto!");
		model.logScore();
		
		model.setCurrentState(GameModel.STATE_GOAL);
		model.setWhoScored(GameModel.OPPONENT_ID);
	}
}
