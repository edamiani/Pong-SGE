package com.book.pong_v5;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.book.simplegameengine_v5.SGGui;
import com.book.simplegameengine_v5.SGInputSubscriber;

public class GameController implements SGInputSubscriber 
{
	private SGGui mGui;
	private GameModel mModel;
	private PointF mTempPosition = new PointF();
	
	private boolean mSendToModel = true;
	
	public GameController(GameModel model, SGGui gui)
	{
		mModel = model;
		mGui = gui;
	}
	
	@Override
	public void onDown(MotionEvent event) 
	{
		mTempPosition.set(event.getX(), event.getY());
		if(mGui.injectDown(mTempPosition))
		{
			mSendToModel = false;
		}
	}

	@Override
	public void onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX, float distanceY) 
	{
		if(mSendToModel == true && !mModel.isGameOver())
		{
			mModel.movePlayer(-distanceX, -distanceY);
		}
	}

	@Override
	public void onUp(MotionEvent event) 
	{
		mTempPosition.set(event.getX(), event.getY());
		mGui.injectUp(mTempPosition);
		
		mSendToModel = true;
	}
}
