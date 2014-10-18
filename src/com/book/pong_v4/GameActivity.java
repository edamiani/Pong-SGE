package com.book.pong_v4;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.book.simplegameengine_v4.SGActivity;
import com.book.simplegameengine_v4.SGDialog;
import com.book.simplegameengine_v4.SGInputPublisher;
import com.book.simplegameengine_v4.SGPreferences;

public class GameActivity extends SGActivity 
{
	private GameController 	mController;
	private SGDialog 		mDialog;
	private int 			mDifficultyLevel;	
	private GameModel 		mModel;
	private GameView 		mView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		enableFullScreen();
		enableKeepScreenOn();
		
		SGPreferences preferences = getPreferences();
		if(preferences.getInt("first_time", -1) == -1) 
		{
			preferences.begin()
				.putInt("first_time", 1)
				.putInt("difficulty", 0)
				.putInt("high_score", 15)
				.end();
			Log.d("PongV2", "Primeira inicialização.");
		}
		
		Point worldDimensions = new Point(480, 320);
		
		Bundle bundle = getIntent().getBundleExtra("settings");
		mDifficultyLevel = bundle.getInt("difficulty");
		preferences.putInt("difficulty", mDifficultyLevel);

		mModel = new GameModel(worldDimensions, mDifficultyLevel);
		
		mView = new GameView(this, mModel);
		setContentView(mView);
		
		SGInputPublisher inputPublisher = new SGInputPublisher(this);		
		mController = new GameController(mModel, mView.getGui());
		inputPublisher.registerSubscriber(mController);
		setInputPublisher(inputPublisher);
		
		mDialog = new SGDialog(this, R.string.dialog_game_message, R.string.dialog_ok, R.string.dialog_cancel) 
		{
			@Override
			public void onOk() 
			{
				returnToStartScreen();
			}
			
			@Override
			public void onCancel() 
			{
				mModel.unpause();
			}
		};

	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		mView.getMusicPlayer().release();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		mView.getMusicPlayer().pause();
		
		overridePendingTransition(0, 0);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		mView.getMusicPlayer().resume();
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	@Override
	public void onBackPressed() 
	{
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
	
	public void returnToStartScreen()
	{
		finish();
		overridePendingTransition(0, 0);
	}
}


