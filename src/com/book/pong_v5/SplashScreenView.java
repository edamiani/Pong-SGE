package com.book.pong_v5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;

import com.book.simplegameengine_v5.SGImage;
import com.book.simplegameengine_v5.SGRenderer;
import com.book.simplegameengine_v5.SGView;
import com.book.simplegameengine_v5.SGViewport;
import com.book.simplegameengine_v5.SGViewport.ScalingMode;

public class SplashScreenView extends SGView
{	
	private SGImage mImgSplashScreen;
	private PointF	mImagePosition = new PointF(0, 0);
	private PointF	mImageDimensions = new PointF();
	
	public SplashScreenView(Context context)
	{
		super(context);
		
		mImgSplashScreen = getImageFactory().createImage("images/splash_screen.png");
		mImageDimensions.set(mImgSplashScreen.getDimensions().x, mImgSplashScreen.getDimensions().y);
	}
	
	@Override
	public void setup()
	{
		Point viewDimensions = getViewDimensions();
		Point sceneDimensions = new Point(GameModel.SCENE_WIDTH, GameModel.SCENE_HEIGHT);
		SGViewport viewport = new SGViewport(sceneDimensions, viewDimensions, ScalingMode.FULL_SCREEN_KEEP_ORIGINAL_ASPECT);
		getRenderer().setViewport(viewport);
	}
	
	@Override
    public void step(Canvas canvas, float elapsedTimeInSeconds) 
	{		
		SGRenderer renderer = getRenderer();
		
		int screenColor = Color.DKGRAY;
		int viewportColor = Color.BLACK;
		
		renderer.beginDrawing(canvas, screenColor, viewportColor);
		
		renderer.drawImage(mImgSplashScreen, null, mImagePosition, mImageDimensions);
		
		renderer.endDrawing();
	}
}
