package com.book.pong_v4;

import android.content.Intent;
import android.os.Bundle;

import com.book.simplegameengine_v4.SGActivity;
import com.book.simplegameengine_v4.SGInputPublisher;

public class MenuScreenActivity extends SGActivity 
{
	private MenuScreenController mController = null;
	private MenuScreenView mView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		this.enableKeepScreenOn();
		this.enableFullScreen();
		
		mView = new MenuScreenView(this);
		setContentView(mView);
		
		mController = new MenuScreenController(mView.getGui());
		
		setInputPublisher(new SGInputPublisher(this));
		getInputPublisher().registerSubscriber(mController);
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}
	
	@Override 
	protected void onPause() 
	{
		super.onPause();
		
		overridePendingTransition(0, 0);
	}
	
	public void startNextActivity(Intent intent) 
	{
		startActivity(intent);
		overridePendingTransition(0, 0);
		finish();
	}
}

