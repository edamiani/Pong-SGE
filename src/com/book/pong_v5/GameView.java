package com.book.pong_v5;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.book.simplegameengine_v5.SGAnimation;
import com.book.simplegameengine_v5.SGEntity;
import com.book.simplegameengine_v5.SGFont;
import com.book.simplegameengine_v5.SGGui;
import com.book.simplegameengine_v5.SGGuiButton;
import com.book.simplegameengine_v5.SGGuiContainer;
import com.book.simplegameengine_v5.SGGuiLabel;
import com.book.simplegameengine_v5.SGImage;
import com.book.simplegameengine_v5.SGImageFactory;
import com.book.simplegameengine_v5.SGMusicPlayer;
import com.book.simplegameengine_v5.SGRenderer;
import com.book.simplegameengine_v5.SGSoundPool;
import com.book.simplegameengine_v5.SGSprite;
import com.book.simplegameengine_v5.SGSpriteDesc;
import com.book.simplegameengine_v5.SGTileset;
import com.book.simplegameengine_v5.SGView;
import com.book.simplegameengine_v5.SGViewport;
import com.book.simplegameengine_v5.SGViewport.ScalingMode;
import com.book.simplegameengine_v5.SGWidget.Alignment;

public class GameView extends SGView
{
	public final static float 	CHAR_SIZE_SMALL = 16;
	public final static float 	CHAR_SIZE_BIG = 32;
	
	public final static int 	SFX_COLLISION_EDGE = 0;
	public final static int 	SFX_COLLISION_OPPONENT = 1;
	public final static int 	SFX_COLLISION_PLAYER = 2;
		
	private boolean 			mIsDebug;
	private GameModel 			mModel;
	private RectF 				mTempDrawingArea = new RectF();
	
	private SGSpriteDesc 		mSprBallDesc;
	private SGSpriteDesc 		mSprOpponentDesc;
	private SGSpriteDesc 		mSprPlayerDesc;
	
	private SGSprite			mSprBall;
	private SGSprite			mSprOpponent;
	private SGSprite			mSprPlayer;
	
	private SGSoundPool 		mSoundPool;
	private int 				mSounds[] = new int[3];
	
	private SGMusicPlayer 		mMusicPlayer;
	
	private String 				mStrGameOver;
	private String 				mStrOpponent;
	private String 				mStrOpponentScore;
	private String 				mStrPaused;
	private String 				mStrPlayerScore;
	private String 				mStrPlayer;
	private String 				mStrScores;
	private String 				mStrStart;
	
	private SGFont				mFntVisitorBig;
	private SGFont				mFntVisitorSmall;
	
	private SGGui 				mGui;
	private SGGuiButton 		mBtnPause;
	private SGGuiContainer		mCtnrInfo;
	private SGGuiContainer		mCtnrScore;
	private SGGuiLabel 			mLblLowerInfo;
	private SGGuiLabel 			mLblUpperInfo;
	private SGGuiLabel 			mLblPlayerScore;
	private SGGuiLabel 			mLblOpponentScore;
	
	private GameView(Context context)
	{
		super(context);
	}
	
	public GameView(Context context, GameModel model)
	{
		super(context);
		
		mModel = model;
		mModel.setView(this);
		
		mSoundPool = new SGSoundPool(context);
		mMusicPlayer = new SGMusicPlayer(context);
		
		mGui = new SGGui(getRenderer(), mModel.getWorldDimensions());
		
		mIsDebug = false;
	}
	
	@Override
	public void setup()
	{
		Context context = getContext();
		SGRenderer renderer = getRenderer();
		
		mModel.createScene();
		
		Point viewDimensions = getViewDimensions();
		SGViewport viewport = new SGViewport(mModel.getWorldDimensions(), viewDimensions, ScalingMode.FULL_SCREEN_KEEP_ORIGINAL_ASPECT);
		renderer.setViewport(viewport);
		
		// Efeitos sonoros
		mSounds[SFX_COLLISION_EDGE] = mSoundPool.loadSound("sounds/collision_edge.wav");
		mSounds[SFX_COLLISION_OPPONENT] = mSoundPool.loadSound("sounds/collision_opponent.wav");
		mSounds[SFX_COLLISION_PLAYER] = mSoundPool.loadSound("sounds/collision_player.wav");
		
		// Música
		mMusicPlayer.loadMusic("sounds/bgm.wav");
		
		// Descrição da bola
		SGImage ballImage = getImageFactory().createImage("ball.png");
		SGTileset ballTileSet = new SGTileset(ballImage, new Point(4, 2), null);
		mSprBallDesc = new SGSpriteDesc(ballTileSet);
		
		int[] ballTilesCCW = { 0, 1, 2 };
		SGAnimation ballAnimationCCW = new SGAnimation(ballTilesCCW, 0.1f);
		mSprBallDesc.addAnimation("roll_ccw", ballAnimationCCW);
		
		int[] ballTilesCW = { 4, 5, 6 };
		SGAnimation ballAnimationCW = new SGAnimation(ballTilesCW, 0.1f);
		mSprBallDesc.addAnimation("roll_cw", ballAnimationCW);
		
		mSprBall = new SGSprite(mSprBallDesc, mModel.getBall());
		
		// Descrição do paddle do oponente
		SGImage opponentImage = getImageFactory().createImage("opponent.png");
		SGTileset opponentTileSet = new SGTileset(opponentImage, new Point(8, 2), new Rect(0, 0, 23, 98));
		
		mSprOpponentDesc = new SGSpriteDesc(opponentTileSet);
		
		int[] opponentTilesUpHappy = { 0, 1 };
		SGAnimation opponentAnimationUpHappy = new SGAnimation(opponentTilesUpHappy, 0.1f);
		mSprOpponentDesc.addAnimation("move_down_happy", opponentAnimationUpHappy);
		
		int[] opponentTilesDownHappy = { 2, 3 };
		SGAnimation opponentAnimationDownHappy = new SGAnimation(opponentTilesDownHappy, 0.1f);
		mSprOpponentDesc.addAnimation("move_up_happy", opponentAnimationDownHappy);
		
		int[] opponentTilesUpConcerned = { 4, 5 };
		SGAnimation opponentAnimationUpConcerned = new SGAnimation(opponentTilesUpConcerned, 0.1f);
		mSprOpponentDesc.addAnimation("move_down_concerned", opponentAnimationUpConcerned);
		
		int[] opponentTilesDownConcerned = { 6, 7 };
		SGAnimation opponentAnimationDownConcerned = new SGAnimation(opponentTilesDownConcerned, 0.1f);
		mSprOpponentDesc.addAnimation("move_up_concerned", opponentAnimationDownConcerned);
		
		int[] opponentTilesUpAngry = { 8, 9 };
		SGAnimation opponentAnimationUpAngry = new SGAnimation(opponentTilesUpAngry, 0.1f);
		mSprOpponentDesc.addAnimation("move_down_angry", opponentAnimationUpAngry);
		
		int[] opponentTilesDownAngry = { 10, 11 };
		SGAnimation opponentAnimationDownAngry = new SGAnimation(opponentTilesDownAngry, 0.1f);
		mSprOpponentDesc.addAnimation("move_up_angry", opponentAnimationDownAngry);
		
		mSprOpponent = new SGSprite(mSprOpponentDesc, mModel.getOpponent());
		
		// Descrição do paddle do jogador
		SGImage playerImage = getImageFactory().createImage("player.png");
		SGTileset playerTileSet = new SGTileset(playerImage, new Point(8, 2), new Rect(0, 0, 23, 98));
		
		mSprPlayerDesc = new SGSpriteDesc(playerTileSet);
		
		int[] playerTilesUpHappy = { 0, 1 };
		SGAnimation playerAnimationUpHappy = new SGAnimation(playerTilesUpHappy, 0.1f);
		mSprPlayerDesc.addAnimation("move_down_happy", playerAnimationUpHappy);
		
		int[] playerTilesDownHappy = { 2, 3 };
		SGAnimation playerAnimationDownHappy = new SGAnimation(playerTilesDownHappy, 0.1f);
		mSprPlayerDesc.addAnimation("move_up_happy", playerAnimationDownHappy);
		
		int[] playerTilesUpConcerned = { 4, 5 };
		SGAnimation playerAnimationUpConcerned = new SGAnimation(playerTilesUpConcerned, 0.1f);
		mSprPlayerDesc.addAnimation("move_down_concerned", playerAnimationUpConcerned);
		
		int[] playerTilesDownConcerned = { 6, 7 };
		SGAnimation playerAnimationDownConcerned = new SGAnimation(playerTilesDownConcerned, 0.1f);
		mSprPlayerDesc.addAnimation("move_up_concerned", playerAnimationDownConcerned);
		
		int[] playerTilesUpAngry = { 8, 9 };
		SGAnimation playerAnimationUpAngry = new SGAnimation(playerTilesUpAngry, 0.1f);
		mSprPlayerDesc.addAnimation("move_down_angry", playerAnimationUpAngry);
		
		int[] playerTilesDownAngry = { 10, 11 };
		SGAnimation playerAnimationDownAngry = new SGAnimation(playerTilesDownAngry, 0.1f);
		mSprPlayerDesc.addAnimation("move_up_angry", playerAnimationDownAngry);
		
		mSprPlayer = new SGSprite(mSprPlayerDesc, mModel.getPlayer());
		
		mMusicPlayer.play(true, 1.0f, 1.0f);
		
		/*************************** Inicialização da GUI ****************************/
		
		//mGui.setViewport(viewport);
		
		mStrGameOver = context.getResources().getString(R.string.game_over);
		mStrOpponent = context.getResources().getString(R.string.opponent);
		mStrPaused = context.getResources().getString(R.string.paused);
		mStrPlayer = context.getResources().getString(R.string.player);
		mStrScores = context.getResources().getString(R.string.scores);
		mStrStart = context.getResources().getString(R.string.start);
		
		mStrPlayerScore = "0";
		mStrOpponentScore = "0";
		
		SGImageFactory imageFactory = getImageFactory();
		SGImage imgFontVisitor = imageFactory.createImage("fonts/font_visitor_white.png");
		
		Point fontTilesNum = new Point(16, 16);
		SGTileset fontTileset = new SGTileset(imgFontVisitor, fontTilesNum, null);
		mFntVisitorBig = new SGFont(fontTileset, new PointF(32, 48));
		mFntVisitorSmall = new SGFont(fontTileset, new PointF(16, 24));
		
		SGGuiContainer guiRoot = mGui.getRoot();
		
		PointF position = new PointF();
		PointF dimensions = new PointF();
		
		// Contêiner - Placar
		position.set(0, 0);
		dimensions.set(0, 0); // Inicialmente sem dimensões
		
		mCtnrScore = new SGGuiContainer(Alignment.Center, position, dimensions);
		guiRoot.addChild("score", mCtnrScore);
		
		// Placar do jogador		
		position.set(-80, 5);
		
		mLblPlayerScore = new SGGuiLabel(Alignment.Center, position, mFntVisitorBig, mStrPlayerScore);
		mCtnrScore.addChild("player_score", mLblPlayerScore);
		
		// Placar do oponente
		position.set(80, 5);
		
		mLblOpponentScore = new SGGuiLabel(Alignment.Center, position, mFntVisitorBig, mStrPlayerScore);
		mCtnrScore.addChild("opponent_score", mLblOpponentScore);
		
		// Contêiner - Info
		position.set(0, 0);
		dimensions.set(0, 0); // Inicialmente sem dimensões
		
		mCtnrInfo = new SGGuiContainer(Alignment.Center, position, dimensions);
		guiRoot.addChild("info", mCtnrInfo);
		
		// Informações - Superior
		position.set(0, 80);
		
		mLblUpperInfo = new SGGuiLabel(Alignment.Center, position, mFntVisitorSmall, "");
		mCtnrInfo.addChild("upper_info", mLblUpperInfo);
		
		// Informações - Inferior
		position.set(0, 100);
		
		mLblLowerInfo = new SGGuiLabel(Alignment.Center, position, mFntVisitorBig, mStrScores);
		mCtnrInfo.addChild("lower_info", mLblLowerInfo);
		
		// Botão de pausa		
		Point tilesNum = new Point(2, 1);
		SGTileset tileset = new SGTileset(imageFactory.createImage("gui/button_pause.png"), tilesNum, null);
		position.set(0, 16);
		dimensions.set(32, 32);
		
		mBtnPause = 
			new SGGuiButton(Alignment.Center, position, dimensions, tileset, mGui) 
			{
				@Override
				public boolean onUp(PointF position)
				{
					if(mModel.getCurrentState() != GameModel.STATE_PAUSED &&
					   mModel.getCurrentState() != GameModel.STATE_GAME_OVER)
					{
						mModel.pause();
					}
					else if(mModel.getCurrentState() == GameModel.STATE_PAUSED)
					{
						mModel.unpause();
					}
					
					return true;
				}
			};
		
		guiRoot.addChild("pause", mBtnPause);
	}
	
	@Override 
	public void step(Canvas canvas, float elapsedTimeInSeconds)
	{		
		if(elapsedTimeInSeconds > 0.5f)
		{
			elapsedTimeInSeconds = 0.1f;
		}
		
		mModel.step(elapsedTimeInSeconds);
		
		SGRenderer renderer = getRenderer();
		
		int screenColor = Color.DKGRAY;
		int viewportColor = Color.BLACK;
		renderer.beginDrawing(canvas, screenColor, viewportColor);
		
		ArrayList<SGEntity>	entities = mModel.getEntities();
		
		int arraySize = entities.size();	
		for(int i = 0; i < arraySize; i++)
		{
			SGEntity entity = entities.get(i);
			
			if(entity.getCategory() == "paddle")
			{
				SGSprite sprite;
				
				if(entity.getId() == GameModel.PLAYER_ID)
					sprite = mSprPlayer;
				else
					sprite = mSprOpponent;				

				if(entity.hasFlag(EntPaddle.STATE_LOOKING_UP))
				{
					if(entity.hasFlag(EntPaddle.STATE_HAPPY))
					{
						sprite.setCurrentAnimation("move_up_happy");
					}
					else if(entity.hasFlag(EntPaddle.STATE_CONCERNED))
					{
						sprite.setCurrentAnimation("move_up_concerned");
					}
					else // entity.hasFlag(EntPaddle.STATE_ANGRY)
					{
						sprite.setCurrentAnimation("move_up_angry");
					}
				}
				else
				{
					if(entity.hasFlag(EntPaddle.STATE_HAPPY))
					{
						sprite.setCurrentAnimation("move_down_happy");
					}
					else if(entity.hasFlag(EntPaddle.STATE_CONCERNED))
					{
						sprite.setCurrentAnimation("move_down_concerned");
					}
					else // entity.hasFlag(EntPaddle.STATE_ANGRY)
					{
						sprite.setCurrentAnimation("move_down_angry");
					}
				}
				
				if(mModel.getCurrentState() == GameModel.STATE_RUNNING)
				{
					if(entity.hasFlag(EntPaddle.STATE_HIT))
					{
						sprite.getCurrentAnimation().start(2);
					}
					sprite.step(elapsedTimeInSeconds);
				}
				
				SGTileset tileset = sprite.getTileSet();
				PointF position = sprite.getPosition();
				PointF dimensions = sprite.getDimensions();
				
				Rect drawingArea;

				drawingArea = tileset.getTile(sprite.getCurrentAnimation().getCurrentTile());
				renderer.drawImage(tileset.getImage(), drawingArea, position, dimensions);
			}
			else if(entity.getCategory() == "ball")
			{
				if(entity.hasFlag(EntBall.STATE_ROLL_CCW))
				{
					mSprBall.setCurrentAnimation("roll_ccw");
					mSprBall.getCurrentAnimation().start(0);
				}
				else
				{
					mSprBall.setCurrentAnimation("roll_cw");
					mSprBall.getCurrentAnimation().start(0);
				}

				if(mModel.getCurrentState() == GameModel.STATE_RUNNING)
				{
					mSprBall.step(elapsedTimeInSeconds);
				}
				
				SGTileset tileset = mSprBall.getTileSet();
				PointF position = mSprBall.getPosition();
				PointF dimensions = mSprBall.getDimensions();
				
				Rect drawingArea;
				
				drawingArea = tileset.getTile(mSprBall.getCurrentAnimation().getCurrentTile());
				renderer.drawImage(tileset.getImage(), drawingArea, position, dimensions);
			}
			else if(entity.getCategory() == "trigger")
			{
				if(mIsDebug)
				{
					mTempDrawingArea.set(entity.getPosition().x, 
										 entity.getPosition().y, 
										 entity.getPosition().x + entity.getDimensions().x, 
										 entity.getPosition().y + entity.getDimensions().y);
					SGEntity.DebugDrawingStyle style = entity.getDebugDrawingStyle();
					
					if(style == SGEntity.DebugDrawingStyle.FILLED)
					{
						renderer.drawRect(mTempDrawingArea, entity.getDebugColor());
					}
					else
					{
						renderer.drawOutlineRect(mTempDrawingArea, entity.getDebugColor());
					}
				}
			}
		}
		
		SGGuiContainer root = (SGGuiContainer)mGui.getRoot();
		SGGuiContainer info = (SGGuiContainer)root.getChild("info");
		
		SGGuiLabel upperInfo = (SGGuiLabel)info.getChild("upper_info");
		SGGuiLabel lowerInfo = (SGGuiLabel)info.getChild("lower_info");
		
		if(mModel.getCurrentState() == GameModel.STATE_GOAL)
		{
			mStrPlayerScore = String.valueOf(mModel.getPlayerScore());			
			mStrOpponentScore = String.valueOf(mModel.getOpponentScore());
			
			SGGuiContainer score = (SGGuiContainer)root.getChild("score");
			
			SGGuiLabel playerScore = (SGGuiLabel)score.getChild("player_score");
			SGGuiLabel opponentScore = (SGGuiLabel)score.getChild("opponent_score");
			
			playerScore.setString(mStrPlayerScore);			
			opponentScore.setString(mStrOpponentScore);
			
			info.setIsVisible(true);
			
			// Texto superior
			String text;
			if(mModel.whoScored() == GameModel.PLAYER_ID)
			{
				text = mStrPlayer;
			}
			else
			{
				text = mStrOpponent;
			}
			upperInfo.setString(text);
			
			// Texto inferior
			lowerInfo.setString(mStrScores);
		}
		else if(mModel.getCurrentState() == GameModel.STATE_RESTART)
		{			
			info.setIsVisible(true);
			
			upperInfo.setString("");
			lowerInfo.setString(mStrStart);
		}
		else if(mModel.getCurrentState() == GameModel.STATE_GAME_OVER)
		{
			SGGuiButton pause = (SGGuiButton)root.getChild("pause");
			
			info.setIsVisible(true);
			
			upperInfo.setString("");			
			lowerInfo.setString(mStrGameOver);
			
			pause.setIsEnabled(false);
		}
		else if(mModel.getCurrentState() == GameModel.STATE_PAUSED)
		{
			info.setIsVisible(true);
			
			upperInfo.setString("");			
			lowerInfo.setString(mStrPaused);
		}
		else // mModel.getCurrentState() == GameModel.STATE_RUNNING
		{
			info.setIsVisible(false);
		}
		
		mGui.update();
		mGui.render();
		
		renderer.endDrawing();
		
		if(mModel.getCurrentState() != GameModel.STATE_PAUSED)
		{
			switch(mModel.getBall().getCollisionType())
			{
			case EntBall.COLLISION_EDGE:
				mSoundPool.playSound(mSounds[SFX_COLLISION_EDGE], 1, 1, 0, 1);
				break;
			case EntBall.COLLISION_OPPONENT:
				mSoundPool.playSound(mSounds[SFX_COLLISION_OPPONENT], 1, 1, 0, 1);
				break;
			case EntBall.COLLISION_PLAYER:
				mSoundPool.playSound(mSounds[SFX_COLLISION_PLAYER], 1, 1, 0, 1);
				break;
			}
		}
	}
	
	public SGGui getGui() { return mGui; }
	public SGMusicPlayer getMusicPlayer() { return mMusicPlayer; }
}
