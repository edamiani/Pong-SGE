package com.book.pong_v5;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.book.simplegameengine_v5.SGEntity;
import com.book.simplegameengine_v5.SGTimer;
import com.book.simplegameengine_v5.SGWorld;

public class GameModel extends SGWorld 
{
	public static final int SCENE_WIDTH = 480;
	public static final int SCENE_HEIGHT = 320;
	
	public static final int BALL_SIZE = 16;
	public static final int DISTANCE_FROM_EDGE = 16;
	public static final int GAP_SIZE = 50;
	public static final int GOAL_WIDTH = 1024;
	public static final int PADDLE_HEIGHT = 98;
	public static final int PADDLE_WIDTH = 23;
	public static final int WALL_HEIGHT = 1024;
	
	public static final int STATE_RESTART = 0;
	public static final int STATE_RUNNING = 1;
	public static final int STATE_GAME_OVER = 2;
	public static final int STATE_GOAL = 3;
	public static final int STATE_PAUSED = 4;
	
	public static final int BALL_ID = 0;
	public static final int OPPONENT_ID = 1;
	public static final int PLAYER_ID = 2;
	
	private EntBall	 				mBall;
	private int						mCurrentState;
	private int						mDifficultyLevel;
	private ArrayList<SGEntity>		mEntities = new ArrayList<SGEntity>();
	private boolean					mGameOver = false;
	private TrgGap					mGap;
	private SGTimer					mGoalTimer;
	private TrgLeftGoal				mLeftGoal;
	private TrgLowerWall			mLowerWall;
	private EntOpponent 			mOpponent;
	private TrgRightGoal			mRightGoal;
	private int						mOpponentScore;
	private EntPaddle				mPlayer;
	private int						mPlayerScore;
	private int						mPreviousState;
	private Random					mRandom = new Random();
	private SGTimer					mRestartTimer;
	private StringBuilder			mStringBuilder = new StringBuilder();
	private TrgUpperWall			mUpperWall;
	private int						mWhoScored;
	
	public GameModel(Point worldDimensions, int difficultyLevel)
	{
		super(worldDimensions);

		mDifficultyLevel = difficultyLevel;
		mRestartTimer = new SGTimer(0.8f);
		mGoalTimer = new SGTimer(1.2f);
	}
	
	@Override
	public void createScene()
	{
		Point worldDimensions = getWorldDimensions();
		mCurrentState = STATE_RESTART;
		
		// Bola
		PointF tempPosition = new PointF((worldDimensions.x / 2) - (BALL_SIZE / 2), 
										 (worldDimensions.y / 2) - (BALL_SIZE / 2));
		PointF tempDimensions = new PointF(BALL_SIZE, BALL_SIZE);
		mBall = new EntBall(this, tempPosition, tempDimensions, mDifficultyLevel);
		registerEntity(mBall);
		
		// Paddle do jogador
		tempPosition.set(DISTANCE_FROM_EDGE, 
						 (worldDimensions.y / 2) - (PADDLE_HEIGHT / 2));
		tempDimensions.set(PADDLE_WIDTH, PADDLE_HEIGHT);
		mPlayer = new EntPlayer(this, tempPosition, tempDimensions);
		mPlayer.setDebugColor(Color.CYAN);
		registerEntity(mPlayer);
		
		// Paddle do oponente
		tempPosition.set(worldDimensions.x - (DISTANCE_FROM_EDGE + PADDLE_WIDTH), 
				 		 (worldDimensions.y / 2) - (PADDLE_HEIGHT / 2));
		tempDimensions.set(PADDLE_WIDTH, PADDLE_HEIGHT);
		mOpponent = new EntOpponent(this, tempPosition, tempDimensions, mDifficultyLevel);
		mOpponent.setDebugColor(Color.GREEN);
		registerEntity(mOpponent);
		
		// Meta do jogador
		tempPosition.set(-GOAL_WIDTH, -WALL_HEIGHT);
		tempDimensions.set(GOAL_WIDTH - BALL_SIZE, worldDimensions.y + (2 * WALL_HEIGHT));
		mLeftGoal =  new TrgLeftGoal(this, tempPosition, tempDimensions);		
		mLeftGoal.addObservedEntity(mBall);
		registerEntity(mLeftGoal);
		
		// Meta do oponente
		tempPosition.set(worldDimensions.x + BALL_SIZE, -WALL_HEIGHT);
		tempDimensions.set(GOAL_WIDTH - BALL_SIZE, worldDimensions.y + (2 * WALL_HEIGHT));
		mRightGoal = new TrgRightGoal(this, tempPosition, tempDimensions);
		mRightGoal.addObservedEntity(mBall);
		registerEntity(mRightGoal);
		
		// Vão superior
		tempPosition.set(0, 0);
		tempDimensions.set(worldDimensions.x, GAP_SIZE);
		mGap = new TrgGap(this, tempPosition, tempDimensions);
		mGap.addObservedEntity(mOpponent);
		mGap.addObservedEntity(mPlayer);
		registerEntity(mGap);
		
		// Parede inferior
		tempPosition.set(0, worldDimensions.y);
		tempDimensions.set(worldDimensions.x, WALL_HEIGHT);
		mLowerWall = new TrgLowerWall(this, tempPosition, tempDimensions);
		mLowerWall.addObservedEntity(mBall);
		mLowerWall.addObservedEntity(mOpponent);
		mLowerWall.addObservedEntity(mPlayer);
		registerEntity(mLowerWall);
		
		// Parede superior
		tempPosition.set(0, -WALL_HEIGHT);
		tempDimensions.set(worldDimensions.x, WALL_HEIGHT);
		mUpperWall = new TrgUpperWall(this, tempPosition, tempDimensions);
		mUpperWall.addObservedEntity(mBall);
		registerEntity(mUpperWall);
	}
	
	public void movePlayer(float x, float y)
	{
		if(mCurrentState == STATE_RUNNING)
		{
			mPlayer.move(0, y);
		}
	}
	
	@Override
	public void registerEntity(SGEntity entity)
	{
		mEntities.add(entity);
		//getView().onEntityCreated(entity);
	}
	
	@Override
	public void step(float elapsedTimeInSeconds)
	{		
		if(mCurrentState == STATE_RUNNING)
		{
			mBall.step(elapsedTimeInSeconds);
			mOpponent.step(elapsedTimeInSeconds);
			mPlayer.step(elapsedTimeInSeconds);
			
			mGap.step(elapsedTimeInSeconds);
			mLowerWall.step(elapsedTimeInSeconds);
			mUpperWall.step(elapsedTimeInSeconds);
			
			mLeftGoal.step(elapsedTimeInSeconds);
			mRightGoal.step(elapsedTimeInSeconds);
		}
		else if(mCurrentState == STATE_GOAL)
		{	
			if(!mGoalTimer.hasStarted())
			{
				mGoalTimer.start();
			}
			
			if(mGoalTimer.step(elapsedTimeInSeconds) == true)
			{
				if(mOpponentScore == 5)
				{
					mCurrentState = STATE_GAME_OVER;
				}
				else
				{
					mGoalTimer.stopAndReset();
					mBall.calculateSpeed(mPlayerScore);
					mLeftGoal.setIsActive(true);
					mRightGoal.setIsActive(true);

					mCurrentState = STATE_RESTART;
				}
			}
		}
		else if(mCurrentState == STATE_RESTART)
		{
			if(!mRestartTimer.hasStarted())
			{
				resetWorld();
				mRestartTimer.start();
			}
			
			if(mRestartTimer.step(elapsedTimeInSeconds) == true)
			{
				mRestartTimer.stopAndReset();
				mCurrentState = STATE_RUNNING;
			}
		}
	}
	
	public void logScore()
	{
		mStringBuilder.delete(0, mStringBuilder.length());
		mStringBuilder.append("Placar: ");
		mStringBuilder.append(mPlayerScore);
		mStringBuilder.append(" X ");
		mStringBuilder.append(mOpponentScore);
		
		Log.d("PongV2", mStringBuilder.toString());
	}
	
	public void resetWorld()
	{
		mBall.setPosition((getWorldDimensions().x / 2) - (mBall.getDimensions().x / 2), 
						  (getWorldDimensions().y / 2) - (mBall.getDimensions().y / 2));
		
		_calculateBallAngle();
		
		mOpponent.setPosition(mOpponent.getPosition().x, 
							  (getWorldDimensions().y / 2) - (mOpponent.getDimensions().y / 2));
		
		mPlayer.setPosition(mPlayer.getPosition().x, 
				  			(getWorldDimensions().y / 2) - (mPlayer.getDimensions().y / 2));
	}
	
	public void pause()
	{
		mPreviousState = mCurrentState;
		mCurrentState = STATE_PAUSED;
	}
	
	public void unpause()
	{
		mCurrentState = mPreviousState;
	}
	
	public EntBall 		getBall() { return mBall; }
	public int			getCurrentState() { return mCurrentState; }
	public ArrayList<SGEntity> 
						getEntities() { return mEntities; }
	public EntOpponent	getOpponent() { return mOpponent; }
	public int 			getOpponentScore() { return mOpponentScore; }
	public EntPaddle 	getPlayer() { return mPlayer; }
	public int 			getPlayerScore() { return mPlayerScore; }
	public SGTimer 		getRestartTimer() { return mRestartTimer; }
	
	public void increaseOpponentScore() { mOpponentScore++; }
	public void increasePlayerScore() { mPlayerScore++; }
	
	public boolean isGameOver() { return mGameOver; }
	
	public void setCurrentState(int state) { mCurrentState = state; }
	public void setGameOver(boolean gameOver) { mGameOver = gameOver; }
	public void setWhoScored(int whoScored) { mWhoScored = whoScored; }
	
	public int whoScored() { return mWhoScored; }
	
	private void _calculateBallAngle()
	{
		int chooseSide = mRandom.nextInt(4);
		
		int angle;
		if(chooseSide == 0) // Superior direito
		{
			angle = mRandom.nextInt(16);
		}
		else if(chooseSide == 1) // Superior esquerdo
		{
			angle = mRandom.nextInt(16) + 165;
		}
		else if(chooseSide == 2) // Inferior esquerdo
		{
			angle = mRandom.nextInt(16) + 180;
		}
		else // chooseSide == 3, inferior direito
		{
			angle = mRandom.nextInt(16) + 345;
		}
		
		float radian = (float)(angle * (Math.PI / 180));
		
		mBall.setPosition(getWorldDimensions().x / 2 - mBall.getDimensions().x / 2, 
						  getWorldDimensions().y / 2 - mBall.getDimensions().y / 2);
		
		float speed = mBall.getSpeed();
		mBall.setVelocity(speed * (float)Math.cos(radian), speed * (float)Math.sin(radian));
	}
}
