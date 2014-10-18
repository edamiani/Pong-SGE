package com.book.pong_v4;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.book.simplegameengine_v4.SGAnimation;
import com.book.simplegameengine_v4.SGEntity;
import com.book.simplegameengine_v4.SGFont;
import com.book.simplegameengine_v4.SGGui;
import com.book.simplegameengine_v4.SGImage;
import com.book.simplegameengine_v4.SGImageFactory;
import com.book.simplegameengine_v4.SGMusicPlayer;
import com.book.simplegameengine_v4.SGRenderer;
import com.book.simplegameengine_v4.SGSoundPool;
import com.book.simplegameengine_v4.SGSprite;
import com.book.simplegameengine_v4.SGSpriteDesc;
import com.book.simplegameengine_v4.SGTileset;
import com.book.simplegameengine_v4.SGView;
import com.book.simplegameengine_v4.SGViewport;
import com.book.simplegameengine_v4.SGViewport.ScalingMode;
import com.book.simplegameengine_v4.SGWidget.Alignment;
import com.book.simplegameengine_v4.SGWidgetButton;
import com.book.simplegameengine_v4.SGWidgetContainer;
import com.book.simplegameengine_v4.SGWidgetLabel;

public class GameView extends SGView {
	public final static float	CHAR_SIZE_BIG = 32;
	public final static float	CHAR_SIZE_SMALL = 16;
	
	public final static int 	SFX_COLLISION_EDGE = 0;
	public final static int 	SFX_COLLISION_OPPONENT = 1;
	public final static int 	SFX_COLLISION_PLAYER = 2;

	private boolean 			mIsDebug = false;
	private GameModel 			mModel;
	
	private SGImage				mImgField;
	private Rect				mTempRectSrc = new Rect();
	private RectF				mTempRectFDest = new RectF();
	
	private SGSpriteDesc 		mSprBallDesc;
	private SGSpriteDesc 		mSprOpponentDesc;
	private SGSpriteDesc 		mSprPlayerDesc;
	
	private SGSprite			mSprBall;
	private SGSprite			mSprOpponent;
	private SGSprite			mSprPlayer;
	
	private SGFont 				mFntVisitorBig;
	private SGFont 				mFntVisitorSmall;
	
	private SGMusicPlayer 		mMusicPlayer;
	private SGSoundPool 		mSoundPool;
	private int 				mSounds[] = new int[3];
	
	private String 				mStrGameOver;
	private String 				mStrOpponent;
	private String 				mStrOpponentScore;
	private String 				mStrPaused;
	private String 				mStrPlayer;
	private String 				mStrPlayerScore;
	private String 				mStrScores;
	private String 				mStrStart;
	
	private SGGui 				mGui;
	
	private SGWidgetButton 		mBtnPause;
	private SGWidgetContainer	mCtnrInfo;
	private SGWidgetContainer	mCtnrScore;
	private SGWidgetLabel 		mLblLowerInfo;
	private SGWidgetLabel 		mLblOpponentScore;
	private SGWidgetLabel 		mLblPlayerScore;
	private SGWidgetLabel 		mLblUpperInfo;
	
	//private PointF				mTempPosition = new PointF();
	
	private GameView(Context context) 
	{
		super(context);
	}
	
	public GameView(Context context, GameModel model) 
	{
		super(context);
		
		mModel = model;
		
		mSoundPool = new SGSoundPool(context);
		mMusicPlayer = new SGMusicPlayer(context);
		
		mGui = new SGGui(getRenderer(), mModel.getDimensions());
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		if(!hasWindowFocus)
		{
			mModel.pause();
		}
	}
	
	@Override
	public void setup()
	{		
		Point viewDimensions = getDimensions();
		SGViewport viewport = new SGViewport(mModel.getDimensions(), viewDimensions, ScalingMode.FULL_SCREEN_KEEP_ORIGINAL_ASPECT);
		getRenderer().setViewport(viewport);
		
		mModel.setup();
		
		// Campo		
		mImgField = getImageFactory().createImage("images/field.png");
		Point fieldDimensions = mImgField.getDimensions();
		mTempRectSrc.set(0, 0, fieldDimensions.x, fieldDimensions.y);
		mTempRectFDest.set(0, 0, fieldDimensions.x, fieldDimensions.y);
		
		// Bola		
		SGImage ballImage = getImageFactory().createImage("tilesets/ball.png");
		SGTileset tileset = new SGTileset(ballImage, new Point(4, 2), null);		
		mSprBallDesc = new SGSpriteDesc(tileset);
		
		int[] ballTilesCCW = { 0, 1, 2, 3 };
		SGAnimation animation = new SGAnimation(ballTilesCCW, 0.1f);
		mSprBallDesc.addAnimation("roll_ccw", animation);
		
		int[] ballTilesCW = { 4, 5, 6, 7 };
		animation = new SGAnimation(ballTilesCW, 0.1f);
		mSprBallDesc.addAnimation("roll_cw", animation);
		
		mSprBall = new SGSprite(mSprBallDesc, mModel.getBall());
		
		// Animações dos paddles
		int[] tilesDownHappy = { 0, 1 };
		int[] tilesUpHappy = { 2, 3 };
		int[] tilesDownConcerned = { 4, 5 };
		int[] tilesUpConcerned = { 6, 7 };
		int[] tilesDownAngry = { 8, 9 };
		int[] tilesUpAngry = { 10, 11 };
		
		// Paddle do oponente
		SGImage opponentImage = getImageFactory().createImage("tilesets/opponent.png");
		tileset = new SGTileset(opponentImage, new Point(8, 2), new Rect(0, 0,
								GameModel.PADDLE_WIDTH, 
								GameModel.PADDLE_HEIGHT));		
		mSprOpponentDesc = new SGSpriteDesc(tileset);
		
		animation = new SGAnimation(tilesDownHappy, 0.1f);	
		mSprOpponentDesc.addAnimation("move_down_happy", animation);
		
		animation = new SGAnimation(tilesUpHappy, 0.1f);	
		mSprOpponentDesc.addAnimation("move_up_happy", animation);
		
		animation = new SGAnimation(tilesDownConcerned, 0.1f);	
		mSprOpponentDesc.addAnimation("move_down_concerned", animation);
		
		animation = new SGAnimation(tilesUpConcerned, 0.1f);		
		mSprOpponentDesc.addAnimation("move_up_concerned", animation);
		
		animation = new SGAnimation(tilesDownAngry, 0.1f);		
		mSprOpponentDesc.addAnimation("move_down_angry", animation);
		
		animation = new SGAnimation(tilesUpAngry, 0.1f);	
		mSprOpponentDesc.addAnimation("move_up_angry", animation);
		
		mSprOpponent = new SGSprite(mSprOpponentDesc, mModel.getOpponent());
		
		// Paddle do jogador
		SGImage playerImage = getImageFactory().createImage("tilesets/player.png");
		tileset = new SGTileset(playerImage, new Point(8, 2), new Rect(0, 0,
									GameModel.PADDLE_WIDTH,
									GameModel.PADDLE_HEIGHT));		
		mSprPlayerDesc = new SGSpriteDesc(tileset);
		
		animation = new SGAnimation(tilesDownHappy, 0.1f);
		mSprPlayerDesc.addAnimation("move_down_happy", animation);
		
		animation = new SGAnimation(tilesUpHappy, 0.1f);
		mSprPlayerDesc.addAnimation("move_up_happy", animation);
		
		animation = new SGAnimation(tilesDownConcerned, 0.1f);
		mSprPlayerDesc.addAnimation("move_down_concerned", animation);
		
		animation = new SGAnimation(tilesUpConcerned, 0.1f);
		mSprPlayerDesc.addAnimation("move_up_concerned", animation);
		
		animation = new SGAnimation(tilesDownAngry, 0.1f);
		mSprPlayerDesc.addAnimation("move_down_angry", animation);
		
		animation = new SGAnimation(tilesUpAngry, 0.1f);
		mSprPlayerDesc.addAnimation("move_up_angry", animation);
		
		mSprPlayer = new SGSprite(mSprPlayerDesc, mModel.getPlayer());
		
		SGImageFactory imageFactory = getImageFactory();
		SGImage imgFontVisitor = imageFactory.createImage("fonts/font_visitor_white.png");
		Point tilesNum = new Point(16, 16);
		SGTileset tilesetVisitor = new SGTileset(imgFontVisitor, tilesNum, null);
		
		mFntVisitorBig = new SGFont(tilesetVisitor, new PointF(CHAR_SIZE_BIG, CHAR_SIZE_BIG));
		mFntVisitorSmall = new SGFont(tilesetVisitor, new PointF(CHAR_SIZE_SMALL, CHAR_SIZE_SMALL));
		
		// Efeitos sonoros
		mSounds[SFX_COLLISION_EDGE] = mSoundPool.loadSound("sounds/collision_edge.wav");
		mSounds[SFX_COLLISION_OPPONENT] = mSoundPool.loadSound("sounds/collision_opponent.wav");
		mSounds[SFX_COLLISION_PLAYER] = mSoundPool.loadSound("sounds/collision_player.wav");
		
		// Música
		mMusicPlayer.loadMusic("sounds/bgm.ogg");
		mMusicPlayer.play(true, 1.0f, 1.0f);
		
		// GUI		
		mStrPlayerScore = "0";
		mStrOpponentScore = "0";
		
		SGWidgetContainer guiRoot = mGui.getRoot();
		
		PointF position = new PointF();
		PointF dimensions = new PointF();
		
		// Contêiner - Placar
		position.set(0, 0);
		dimensions.set(0, 0); // Inicialmente sem dimensões
		mCtnrScore = new SGWidgetContainer(Alignment.Center, position, dimensions);
		guiRoot.addChild("score", mCtnrScore);
		
		// Placar do jogador		
		position.set(-80, 14);
		mLblPlayerScore = new SGWidgetLabel(Alignment.Center, position, mFntVisitorBig, mStrPlayerScore);
		mCtnrScore.addChild("player_score", mLblPlayerScore);
		
		// Placar do oponente
		position.set(80, 14);
		mLblOpponentScore = new SGWidgetLabel(Alignment.Center, position, mFntVisitorBig, mStrOpponentScore);
		mCtnrScore.addChild("opponent_score", mLblOpponentScore);

		// Textos informativos
		Context context = getContext();		
		mStrGameOver = context.getResources().getString(R.string.game_over);
		mStrOpponent = context.getResources().getString(R.string.opponent);
		mStrPaused = context.getResources().getString(R.string.paused);
		mStrPlayer = context.getResources().getString(R.string.player);
		mStrScores = context.getResources().getString(R.string.scores);
		mStrStart = context.getResources().getString(R.string.start);
		
		// Contêiner - Info
		position.set(0, 0);
		dimensions.set(0, 0); // Inicialmente sem dimensões
		mCtnrInfo = new SGWidgetContainer(Alignment.Center, position, dimensions);
		
		guiRoot.addChild("info", mCtnrInfo);
		
		// Informações - Superior
		position.set(0, 80);		
		mLblUpperInfo = new SGWidgetLabel(Alignment.Center, position, mFntVisitorSmall, "");
		
		mCtnrInfo.addChild("upper_info", mLblUpperInfo);
		
		// Informações - Inferior
		position.set(0, 100);
		mLblLowerInfo = new SGWidgetLabel(Alignment.Center, position, mFntVisitorBig, mStrScores);
		
		mCtnrInfo.addChild("lower_info", mLblLowerInfo);
		
		// Botão de pausa		
		tilesNum = new Point(2, 1);
		tileset = new SGTileset(imageFactory.createImage("gui/button_pause.png"), tilesNum, null);
		position.set(0, 16);
		dimensions.set(32, 32);
		
		mBtnPause = 
			new SGWidgetButton(Alignment.Center, position, dimensions, tileset, mGui) 
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
		mModel.step(elapsedTimeInSeconds);
		
		SGRenderer renderer = getRenderer();
		
		int screenColor = Color.BLACK;
		int viewportColor = Color.LTGRAY;
		renderer.beginDrawing(canvas, screenColor, viewportColor);
		
		ArrayList<SGEntity>	entities = mModel.getEntities();
		
		if(mIsDebug == true)
		{
			for(SGEntity currentEntity : entities)
			{
				SGEntity.DebugDrawingStyle style = currentEntity.getDebugDrawingStyle();
				if(style == SGEntity.DebugDrawingStyle.FILLED) 
				{
					renderer.drawRect(currentEntity.getBoundingBox(), currentEntity.getDebugColor());
				}
				else
				{
					renderer.drawOutlineRect(currentEntity.getBoundingBox(), currentEntity.getDebugColor());
				}
			}
		}
		else
		{
			renderer.drawImage(mImgField, mTempRectSrc, mTempRectFDest);
			
			for(SGEntity currentEntity : entities)
			{
				if(currentEntity.getCategory() == "paddle") 
				{
					SGSprite sprite;
					if(currentEntity.getId() == GameModel.PLAYER_ID) 
					{
						sprite = mSprPlayer;
					}
					else 
					{
						sprite = mSprOpponent;
					}
					
					if(currentEntity.hasFlag(EntPaddle.STATE_LOOKING_UP)) 
					{
						if(currentEntity.hasFlag(EntPaddle.STATE_HAPPY)) 
						{
							sprite.setCurrentAnimation("move_up_happy", true);
						}
						else if(currentEntity.hasFlag(EntPaddle.STATE_CONCERNED)) 
						{
							sprite.setCurrentAnimation("move_up_concerned", true);
						}
						else // entity.hasFlag(EntPaddle.STATE_ANGRY)
						{ 
							sprite.setCurrentAnimation("move_up_angry", true);
						}
					}
					else 
					{
						if(currentEntity.hasFlag(EntPaddle.STATE_HAPPY)) 
						{
							sprite.setCurrentAnimation("move_down_happy", true);
						}
						else if(currentEntity.hasFlag(EntPaddle.STATE_CONCERNED)) 
						{
							sprite.setCurrentAnimation("move_down_concerned", true);
						}
						else // entity.hasFlag(EntPaddle.STATE_ANGRY)
						{
							sprite.setCurrentAnimation("move_down_angry", true);
						}
					}
					
					if(currentEntity.hasFlag(EntPaddle.STATE_HIT))
					{
						sprite.getCurrentAnimation().start(2);
					}
					
					if(mModel.getCurrentState() == GameModel.STATE_RUNNING)
					{
						sprite.getCurrentAnimation().play();
					}
					else
					{
						sprite.getCurrentAnimation().pause();
					}
					
					sprite.step(elapsedTimeInSeconds);
					
					SGTileset tileset = sprite.getTileSet();
					PointF position = sprite.getPosition();
					PointF dimensions = sprite.getDimensions();
					int currentTile = sprite.getCurrentAnimation().getCurrentTile();
					Rect drawingArea = tileset.getTile(currentTile);
					
					renderer.drawImage(tileset.getImage(), drawingArea,
									   position, dimensions);
				}
				else if(currentEntity.getCategory() == "ball") 
				{
					if(currentEntity.hasFlag(EntBall.STATE_ROLL_CW)) 
					{
						mSprBall.setCurrentAnimation("roll_cw", true);
						mSprBall.getCurrentAnimation().start(0);
					}
					else
					{
						mSprBall.setCurrentAnimation("roll_ccw", true);
						mSprBall.getCurrentAnimation().start(0);
					}
					
					if(mModel.getCurrentState() == GameModel.STATE_RUNNING)
					{
						mSprBall.getCurrentAnimation().play();
					}
					else
					{
						mSprBall.getCurrentAnimation().stop();
					}
					
					mSprBall.step(elapsedTimeInSeconds);
					
					SGTileset tileset = mSprBall.getTileSet();
					PointF position = mSprBall.getPosition();
					PointF dimensions = mSprBall.getDimensions();
					int currentTile = mSprBall.getCurrentAnimation().getCurrentTile();
					Rect drawingArea = tileset.getTile(currentTile);
					
					renderer.drawImage(tileset.getImage(), drawingArea, 
									   position, dimensions);
				}
			}
			
			if(mModel.getCurrentState() == GameModel.STATE_GOAL) 
			{
				mStrPlayerScore = String.valueOf(mModel.getPlayerScore());
				mStrOpponentScore = String.valueOf(mModel.getOpponentScore());
				
				mLblPlayerScore.setString(mStrPlayerScore);
				mLblOpponentScore.setString(mStrOpponentScore);
				
				mCtnrInfo.setIsVisible(true);
				
				// Texto superior
				String text;
				
				if(mModel.getWhoScored() == GameModel.PLAYER_ID) 
				{
					text = mStrPlayer;
				}
				else 
				{
					text = mStrOpponent;
				}
				
				mLblUpperInfo.setString(text);
				
				// Texto inferior
				mLblLowerInfo.setString(mStrScores);
			}
			else if(mModel.getCurrentState() == GameModel.STATE_RESTART) 
			{
				//mCtnrInfo.setIsVisible(true);
				
				mLblUpperInfo.setString("");
				mLblLowerInfo.setString(mStrStart);
			}
			else if(mModel.getCurrentState() == GameModel.STATE_GAME_OVER) 
			{
				//mCtnrInfo.setIsVisible(true);
				
				mLblUpperInfo.setString("");
				mLblLowerInfo.setString(mStrGameOver);
				
				mBtnPause.setIsEnabled(false);
			}
			else if(mModel.getCurrentState() == GameModel.STATE_PAUSED) {
				mCtnrInfo.setIsVisible(true);
				
				mLblUpperInfo.setString("");
				mLblLowerInfo.setString(mStrPaused);
			}
			else { // mModel.getCurrentState() == GameModel.STATE_RUNNING
				mCtnrInfo.setIsVisible(false);
			}
			
			mGui.update();
			
			mGui.render();
		}
		
		renderer.endDrawing();
		
		if(mModel.getCurrentState() == GameModel.STATE_RUNNING)
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
	
	public void stopMusic() 
	{
		mMusicPlayer.stop();
	}

	
	public SGGui getGui() { return mGui; }
	
	public SGMusicPlayer getMusicPlayer() 
	{
		return mMusicPlayer;
	}
}