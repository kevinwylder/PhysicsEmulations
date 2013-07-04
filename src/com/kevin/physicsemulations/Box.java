package com.kevin.physicsemulations;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class Box extends BaseView
{
	
	float[] history=new float[10];
	
	float x;
	float y;
	float vx;
	float vy;
	float ay;
	float tvx;
	float tvy;
	float tay;
	float size;
	Paint paint=new Paint();
	
	Boolean fingerDown=false;
	
	public Box(Context ctx){
		super(ctx);
		paint.setColor(Color.RED);
	}
	

	public void setGravity(Boolean active){
		if(active)ay=.6f;
		else ay=0;
	}

	public void drawCanvas(Canvas canvas){
		x+=vx;
		y+=vy;
		vy+=ay;
		if(x>width-size){
			x=width-size;
			vx*=-.9f;
		}else if(x<0){
			x=0;
			vx*=-.9f;
		}else if(y>height-size){
			y=height-size;
			vy*=-.9;
			if(ay!=0&vy+.3<0){
				vy+=.3;
			}
		}else if(y<0&ay==0){
			y=0;
			vy*=-.9f;
		}
		canvas.drawRect(x,y,x+size,y+size,paint);
	}

	public void handleTouch(MotionEvent event){
		float ex=event.getX();
		float ey=event.getY();
		if(event.getPointerCount()==1){
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				history=new float[]{
					-1,-1,
					-1,-1,
					-1,-1,
					-1,-1,
					-1,-1
				};
				if(ex>x-size&ex<x+size+size&ey>y-size&y<y+size+size){
					fingerDown=true;
					tvx=vx;
					tvy=vy;
					tay=ay;
					vx=0;
					vy=0;
					ay=0;
				}else fingerDown=false;
			}else if(fingerDown&event.getAction()==MotionEvent.ACTION_MOVE){
				x=ex-(size/2);
				y=ey-(size/2);
				for(int i=8;i>1;i-=2){
					history[i]=history[i-2];
					history[i+1]=history[i-1];
				}
				history[0]=ex;
				history[1]=ey;
			}else if(event.getAction()==MotionEvent.ACTION_UP){
				boolean continu=true;
				float sigmavx=0;
				float sigmavy=0;
				for(int i=2;i<10;i++){
					if(history[i]==-1){
						continu=false;
						i=10;
					}else{
						if(i%2==0) sigmavx+=history[i-2]-history[i];
						else sigmavy+=history[i-2]-history[i];
					}
				}
				if(continu){
					vx=sigmavx/5;
					vy=sigmavy/5;
					ay=tay;
				}else if(fingerDown){
					vx=tvx;
					vy=tvy;
					ay=tay;
				}
				fingerDown=false;
			}
		}else{
			
		}
	}

	public void surfaceChangedMethod(){
		x=(float)Math.random()*(width-size);
		y=(float)Math.random()*(height-size);
		size=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30,getResources().getDisplayMetrics());
		double angle=Math.random()*(Math.PI*2);
		vx=(float)Math.cos(angle)*12;
		vy=(float)Math.sin(angle)*12;
	}
	
	
}
