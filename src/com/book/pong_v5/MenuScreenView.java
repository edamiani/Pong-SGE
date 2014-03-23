package com.book.pong_v5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;

import com.book.simplegameengine_v5.SGFont;
import com.book.simplegameengine_v5.SGGui;
import com.book.simplegameengine_v5.SGGuiButton;
import com.book.simplegameengine_v5.SGGuiContainer;
import com.book.simplegameengine_v5.SGGuiLabel;
import com.book.simplegameengine_v5.SGImage;
import com.book.simplegameengine_v5.SGImageFactory;
import com.book.simplegameengine_v5.SGRenderer;
import com.book.simplegameengine_v5.SGTileset;
import com.book.simplegameengine_v5.SGView;
import com.book.simplegameengine_v5.SGViewport;
import com.book.simplegameengine_v5.SGViewport.ScalingMode;
import com.book.simplegameengine_v5.SGWidget.Alignment;

public class MenuScreenView extends SGView
{
	MenuScreenActivity		mActivity;
	private SGGuiButton 	mBtnEasy;
	private SGGuiButton 	mBtnHard;
	private SGGuiButton 	mBtnMedium;
	private SGFont 			mFnt8bit;
	private SGGui 			mGui;
	private SGImage			mImgBackground;
	private PointF			mImagePosition = new PointF(0, 0);
	private PointF			mImageDimensions = new PointF();
	private SGGuiLabel		mLblDifficultyLevel;
	private SGGuiLabel		mLblEasy;
	private SGGuiLabel		mLblHard;
	private SGGuiLabel		mLblLoading;
	private SGGuiLabel		mLblMedium;
	
	public MenuScreenView(Context context)
	{
		super(context);
		
		mActivity = (MenuScreenActivity)context;
		
		mGui = new SGGui(getRenderer(), new Point(480, 320));
	}
	
	@Override
	public void setup()
	{
		Context context = getContext();
		SGRenderer renderer = getRenderer();
		SGImageFactory imageFactory = getImageFactory();
		
		Point viewDimensions = getViewDimensions();
		SGViewport viewport = new SGViewport(new Point(480, 320), viewDimensions, ScalingMode.FULL_SCREEN_KEEP_ORIGINAL_ASPECT);
		renderer.setViewport(viewport);
		
		String strDifficultyLevel = context.getResources().getString(R.string.difficulty_level);
		String strEasy = context.getResources().getString(R.string.easy);
		String strMedium = context.getResources().getString(R.string.medium);
		String strHard = context.getResources().getString(R.string.hard);
		String strLoading = context.getResources().getString(R.string.loading);
		
		/******************** Imagem de fundo *********************/
		mImgBackground = imageFactory.createImage("images/menu_screen.png");
		mImageDimensions.set(mImgBackground.getDimensions().x, mImgBackground.getDimensions().y);
		
		SGGuiContainer guiRoot = mGui.getRoot();
		
		/******************** Botão 'fácil' *********************/
		PointF position = new PointF();
		PointF dimensions = new PointF();
		
		Point tilesNum = new Point(2, 1);
		SGTileset tileset = new SGTileset(imageFactory.createImage("gui/button_menu.png"), tilesNum, null);
		position.set(150, 60);
		dimensions.set(32, 32);
		
		mBtnEasy = 
			new SGGuiButton(Alignment.Left, position, dimensions, tileset, mGui) {
				@Override
				public boolean onUp(PointF position) {
					mLblLoading.setIsVisible(true);
					
					Intent intent = new Intent(getContext(), GameActivity.class);
					intent.putExtra("difficulty", 0);
					mActivity.startNextActivity(intent);
					
					return true;
				}
			};
			
		guiRoot.addChild("btnEasy", mBtnEasy);
		
		/******************** Botão 'médio' *********************/
		position.set(150, 130);
		dimensions.set(32, 32);
			
		mBtnMedium = 
			new SGGuiButton(Alignment.Left, position, dimensions, tileset, mGui) {
				@Override
				public boolean onUp(PointF position) {
					mLblLoading.setIsVisible(true);
					
					Intent intent = new Intent(getContext(), GameActivity.class);
					intent.putExtra("difficulty", 10);
					mActivity.startNextActivity(intent);
					
					return true;
				}
			};
				
		guiRoot.addChild("btnMedium", mBtnMedium);
		
		/******************** Botão 'difícil' *********************/
		position.set(150, 200);
		dimensions.set(32, 32);
		
		mBtnHard = 
			new SGGuiButton(Alignment.Left, position, dimensions, tileset, mGui) {
				@Override
				public boolean onUp(PointF position) {
					mLblLoading.setIsVisible(true);
					
					Intent intent = new Intent(getContext(), GameActivity.class);
					intent.putExtra("difficulty", 20);
					mActivity.startNextActivity(intent);
					
					return true;
				}
			};		
		guiRoot.addChild("btnHard", mBtnHard);
		
		/******************** Fonte 8bit Operator *********************/
		SGImage imgFont8bit = imageFactory.createImage("fonts/8bit_operator_white.png");
		Point fontTilesNum = new Point(16, 16);
		SGTileset fontTileset = new SGTileset(imgFont8bit, fontTilesNum, null);
		mFnt8bit = new SGFont(fontTileset, new PointF(16, 32));
		
		/******************** Rótulo 'nível de dificuldade' *********************/
		position.set(0, 10);	
		mLblDifficultyLevel = new SGGuiLabel(Alignment.Center, position, mFnt8bit, strDifficultyLevel);
		guiRoot.addChild("lblDifficultyLevel", mLblDifficultyLevel);
		
		/******************** Rótulo 'fácil' *********************/
		position.set(190, 58);	
		mLblEasy = new SGGuiLabel(Alignment.Left, position, mFnt8bit, strEasy);
		guiRoot.addChild("lblEasy", mLblEasy);
		
		/******************** Rótulo 'médio' *********************/
		position.set(190, 128);		
		mLblMedium = new SGGuiLabel(Alignment.Left, position, mFnt8bit, strMedium);
		guiRoot.addChild("lblMedium", mLblMedium);
		
		/******************** Rótulo 'difícil' *********************/
		position.set(190, 198);	
		mLblHard = new SGGuiLabel(Alignment.Left, position, mFnt8bit, strHard);
		guiRoot.addChild("lblHard", mLblHard);
		
		/******************** Rótulo 'carregando...' *********************/
		position.set(0, 268);	
		mLblLoading = new SGGuiLabel(Alignment.Center, position, mFnt8bit, strLoading);
		guiRoot.addChild("lblLoading", mLblLoading);
		mLblLoading.setIsVisible(false);
	}
	
	@Override
    public void step(Canvas canvas, float timeElapsedInSeconds) 
	{
		SGRenderer renderer = getRenderer();
		
		int screenColor = Color.DKGRAY;
		int viewportColor = Color.BLACK;
		renderer.beginDrawing(canvas, screenColor, viewportColor);
		
		renderer.drawImage(mImgBackground, null, mImagePosition, mImageDimensions);
		
		mGui.render();
		
		renderer.endDrawing();
	}
	
	public SGGui getGui() { return mGui; }
}
