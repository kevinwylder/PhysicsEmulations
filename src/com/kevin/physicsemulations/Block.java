package com.kevin.physicsemulations;

import android.graphics.*;
import android.util.*;
import java.util.*;

public class Block
{
	
	int canvasWidth;
	int canvasHeight;
	int width;
	public float x=0;
	public float y=0;
	public float vx=0;
	public float vy=0;
	public float ax=0;
	public float ay=0;
	Paint paint=new Paint();
	
	
	public Block(int _canvasWidth,int _canvasHeight){
		canvasWidth=_canvasWidth;
		canvasHeight=_canvasHeight;
		width=(int)(Math.sqrt(canvasWidth*canvasHeight)*.01);
		paint.setColor(Color.RED);
	}
	
	
	public void update(Canvas canvas,ArrayList<Block> siblings,Boolean detectCollisions){
		x+=vx;
		y+=vy;
		vx+=ax;
		vy+=ay;
		if(x+width>canvasWidth){
			x=canvasWidth-width;
			vx*=-.95f;
			ax=-ax;
		}
		if(x<0){
			x=0;
			vx*=-.95f;
			ax=-ax;
		}
		if(y+width>canvasHeight){
			y=canvasHeight-width;
			vy*=-.95f;
			ay=-ay;
		}
		if(y<0){
			y=0;
			vy*=-.95f;
			ay=-ay;
		}
		if(canvas!=null){
			canvas.drawRect(x,y,x+width,y+width,paint);
		}
		if(detectCollisions){
			for(int i=0;i<siblings.size();i++){
				if(siblings.get(i)!=this){
					float dx=siblings.get(i).x-x;
					float dy=siblings.get(i).y-y;
					if(dx<width&dx>-width&dy<width&dy>-width){
						//Collide
						Log.i("collision",x+","+y);
					}
				}
			}
		}
	}
}
