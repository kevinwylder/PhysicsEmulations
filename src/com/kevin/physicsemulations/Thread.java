package com.kevin.physicsemulations;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import java.util.*;

public class Thread extends BaseView
{
	
	Paint paint=new Paint();
	ArrayList<PointF> points=new ArrayList<PointF>();
	ArrayList<PointF> velocities=new ArrayList<PointF>();
	float gap;
	boolean gravity=false;
	int grabbedIndex=0;
	
	public Thread(Context ctx){
		super(ctx);
		paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2.5f,getResources().getDisplayMetrics()));
	}
	
	public void setGravity(Boolean active){
		gravity=!gravity;
	}

	public void drawCanvas(Canvas canvas){
		for(int i=1;i<points.size();i++){
			canvas.drawLine(points.get(i-1).x,points.get(i-1).y,points.get(i).x,points.get(i).y,paint);
		}
		if(gravity){
			for(int i=0;i<points.size();i++){
				if(i!=grabbedIndex){
    				points.get(i).x-=velocities.get(i).x;
	    			points.get(i).y-=velocities.get(i).y;
				//	velocities.get(i).y+=8;
				}
			}
		}
		for(int i=grabbedIndex+1;i<points.size();i++){
			PointF p1=points.get(i-1);
			PointF p2=points.get(i);
			double distance=Math.sqrt(Math.pow(p1.x-p2.x,2)+Math.pow(p2.y-p1.y,2));
			if(distance>gap){
	  			if(!gravity){
	    			double angle=Math.atan2(p1.y-p2.y,p2.x-p1.x);
	    			PointF velocity=new PointF();
	     			velocity.set(p2);
	     			p2.set(p1.x+gap*(float)(Math.cos(angle)),p1.y-(gap*(float)(Math.sin(angle))));
	     			velocity.x-=p2.x;
	     			velocity.y-=p2.y;
	    			velocities.get(i).set(velocity);
	    			points.get(i).set(p2);
				}else{
					float vx1=velocities.get(i-1).x;
					float vy1=velocities.get(i-1).y;
					float vx2=velocities.get(i).x;
					float vy2=velocities.get(i).y;
					float vfx1=((5*vx1)+(5*vx2)-(5*vx1)+(5*vx2))/(10);
					float vfx2=((5*vx1)+(5*vx2)-(5*vfx1))/5;
					float vfy1= ((5*vy1)+(5*vy2)-(5*vy1)+(5*vy2))/(10);
					float vfy2= ((5*vy1)+(5*vy2)-(5*vfy1))/5;
					velocities.get(i-1).set(vfx1,vfy1);
					velocities.get(i).set(vfx2,vfy2);
				}
			}
		}	
		for(int i=grabbedIndex-1;i>=0;i--){
			PointF p1=points.get(i+1);
			PointF p2=points.get(i);
			double distance=Math.sqrt(Math.pow(p1.x-p2.x,2)+Math.pow(p2.y-p1.y,2));
			if(distance>gap){
				if(!gravity){
	    			double angle=Math.atan2(p1.y-p2.y,p2.x-p1.x);
	    			PointF velocity=new PointF();
	     			velocity.set(p2);
	     			p2.set(p1.x+gap*(float)(Math.cos(angle)),p1.y-(gap*(float)(Math.sin(angle))));
	     			velocity.x-=p2.x;
	     			velocity.y-=p2.y;
	    			velocities.get(i).set(velocity);
	    			points.get(i).set(p2);
				}else{
					float vx1=velocities.get(i-1).x;
					float vy1=velocities.get(i-1).y;
					float vx2=velocities.get(i).x;
					float vy2=velocities.get(i).y;
					float vfx1=((5*vx1)+(5*vx2)-(5*vx1)+(5*vx2))/(10);
					float vfx2=((5*vx1)+(5*vx2)-(5*vfx1))/5;
					float vfy1= ((5*vy1)+(5*vy2)-(5*vy1)+(5*vy2))/(10);
					float vfy2= ((5*vy1)+(5*vy2)-(5*vfy1))/5;
					velocities.get(i+1).set(vfx1,vfy1);
					velocities.get(i).set(vfx2,vfy2);
				}
			}
		}
	}

	public void handleTouch(MotionEvent event){
		if(event.getAction()==MotionEvent.ACTION_MOVE){
			points.get(grabbedIndex).set(event.getX(),event.getY());
		}else if(event.getAction()==MotionEvent.ACTION_DOWN){
			double closest=Math.pow(2,20);
			for(int i=0;i<points.size();i++){
				double dist=Math.sqrt(Math.pow(points.get(i).x-event.getX(),2)+Math.pow(points.get(i).y-event.getY(),2));
				if(dist<closest){
					closest=dist;
					grabbedIndex=i;
				}
			}
		}
	}

	public void surfaceChangedMethod(){
		points.clear();
		velocities.clear();
		gap=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getResources().getDisplayMetrics());
		int num=(int)(height*.65/gap);
		float x=width/2;
		for(int i=0;i<num;i++){
			points.add(new PointF(x,i*gap+15*gap));
			velocities.add(new PointF(0,0));
		}
	}
}
	
