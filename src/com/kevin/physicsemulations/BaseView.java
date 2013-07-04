package com.kevin.physicsemulations;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;

public class BaseView extends SurfaceView implements SurfaceHolder.Callback
{
	
	SurfaceHolder holder;
	Handler handler=new Handler();
	Runnable runnable=new Runnable(){
		public void run(){
			if(running){
				Canvas canvas=holder.lockCanvas();
				canvas.drawColor(Color.WHITE);
				drawCanvas(canvas);
				synchronized(holder){
					holder.unlockCanvasAndPost(canvas);
				}
				handler.post(runnable);
			}
			
		}
	};
	Boolean running=true;
	int width=0;
	int height=0;
	
	public BaseView(Context ctx){
		super(ctx);
		holder=getHolder();
		holder.addCallback(this);
		this.setOnTouchListener(new SurfaceView.OnTouchListener(){
			public boolean onTouch(View p1, MotionEvent event){
				handleTouch(event);
				return true;
			}
		});
	}
	
	public void surfaceCreated(SurfaceHolder _holder){
		
	}

	public void surfaceChanged(SurfaceHolder _holder, int p2, int _width, int _height){
		running=false;
		width=_width;
		height=_height;
		surfaceChangedMethod();
		running=true;
		handler.post(runnable);
	}

	public void surfaceDestroyed(SurfaceHolder p1){
		running=false;
	}
	
	public void setGravity(Boolean active){
		
	}
	
	public void drawCanvas(Canvas canvas){
		
	}
	
	public void handleTouch(MotionEvent event){
		
	}
	
	public void surfaceChangedMethod(){
		
	}
}
