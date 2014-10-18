package com.book.pong_v4;

import android.graphics.PointF;

import com.book.simplegameengine_v4.SGEntity;
import com.book.simplegameengine_v4.SGTrigger;
import com.book.simplegameengine_v4.SGWorld;

public class TrgGap extends SGTrigger 
{
	public static final int GAP_SIZE = 50;
	
	public TrgGap(SGWorld world, PointF position, PointF dimensions) 
	{
		super(world, GameModel.TRG_GAP_ID, position, dimensions);
	}
	
	@Override
	public void onHit(SGEntity entity, float elapsedTimeInSeconds) 
	{
		entity.setPosition(entity.getPosition().x, GAP_SIZE);
	}
}

