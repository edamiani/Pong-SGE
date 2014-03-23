package com.book.pong_v5;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.book.simplegameengine_v5.SGActivity;
import com.book.simplegameengine_v5.SGDialog;
import com.book.simplegameengine_v5.SGInputPublisher;
import com.book.simplegameengine_v5.SGPreferences;

public class GameActivity extends SGActivity 
{
	private GameController 	mController;
	private SGDialog		mDialog;
	private int 			mDifficultyLevel;
	private GameModel 		mModel;
	private GameView 		mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		enableFullScreen();
		enableKeepScreenOn();
		setOrientation(SGOrientation.LANDSCAPE);
		
		SGPreferences preferences = getPreferences();
		
		if(preferences.getInt("first_time", -1) == -1)
		{
			preferences.begin()
				.putInt("first_time", 1)
				.putInt("difficulty", 1)
				.putInt("high_score", 15)
				.end();
			
			Log.d("PongV2", "First time initialization.");
		}
		
		mDifficultyLevel = getIntent().getIntExtra("difficulty", 0);
		preferences.putInt("difficulty", mDifficultyLevel);
		
		Point worldDimensions = new Point(480, 320);
		mModel = new GameModel(worldDimensions, mDifficultyLevel);
		
		mView = new GameView(this, mModel);
		setContentView(mView);
		
		mController = new GameController(mModel, mView.getGui());
		
		setInputPublisher(new SGInputPublisher(this));
		getInputPublisher().registerSubscriber(mController);
		
		mDialog = new SGDialog(this, R.string.dialog_game_message, R.string.dialog_ok, R.string.dialog_cancel)
		{
			public void onTouchOk()
			{
				returnToStartScreen();
			}
			
			public void onTouchCancel()
			{
				mModel.unpause();
			}
		};
	}
	
	public void returnToStartScreen()
	{
		finish();
		overridePendingTransition(0, 0);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		mView.getMusicPlayer().stop();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		mView.getMusicPlayer().play(true, 1.0f, 1.0f);
	}
	
	@Override 
	public void onStop()
	{
		super.onStop();
		mView.getMusicPlayer().release();
		overridePendingTransition(0, 0);
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		mView.getMusicPlayer().loadMusic("sounds/bgm.wav");
	}
	
	@Override
	public void onBackPressed()
	{
		//super.onBackPressed();
		if(mModel.getCurrentState() != GameModel.STATE_GAME_OVER)
		{
			mModel.pause();
			mDialog.show();
		}
		else
		{
			returnToStartScreen();
		}
	}
}
