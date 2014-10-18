package com.book.pong_v4;

import android.graphics.PointF;

import com.book.simplegameengine_v4.SGEntity;
import com.book.simplegameengine_v4.SGWorld;

public class EntPaddle extends SGEntity 
{
	public static final int NUM_OF_SECTORS = 10;
	
	public static final int STATE_HIT			= 0x01;
	public static final int STATE_LOOKING_UP	= 0x02;
	public static final int STATE_HAPPY			= 0x04;
	public static final int STATE_CONCERNED		= 0x08;
	public static final int STATE_ANGRY			= 0x10;
	
	private float mSectorSize;

	public EntPaddle(SGWorld world, int id, PointF position, PointF dimensions) 
	{
		super(world, id, "paddle", position, dimensions);
		
		mSectorSize = dimensions.y / (NUM_OF_SECTORS - 1);
		
		addFlags(STATE_HAPPY);
	}
	
	public float getSectorSize() { return mSectorSize; }
}

