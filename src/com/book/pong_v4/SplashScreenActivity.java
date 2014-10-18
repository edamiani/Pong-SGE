package com.book.pong_v4;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.book.simplegameengine_v4.SGActivity;
import com.book.simplegameengine_v4.SGInputPublisher;
import com.book.simplegameengine_v4.SGInputSubscriber;

public class SplashScreenActivity extends SGActivity implements SGInputSubscriber 
{
	private SplashScreenView mView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		this.enableKeepScreenOn();
		this.enableFullScreen();
		
		mView = new SplashScreenView(this);
		setContentView(mView);
		
		setInputPublisher(new SGInputPublisher(this));
		getInputPublisher().registerSubscriber(this);
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() 
	{
		startNextActivity();
	}
	
	public void startNextActivity() 
	{
		Intent intent = new Intent(this, StartScreenActivity.class);
		startActivity(intent);
		overridePendingTransition(0, 0);
		finish();
	}
	
	@Override
	public void onDown(MotionEvent event) {	}
	
	@Override
	public void onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX, float distanceY) {	}
	
	@Override
	public void onUp(MotionEvent event) 
	{
		startNextActivity();
	}
}

