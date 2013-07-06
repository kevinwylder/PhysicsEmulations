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
	ArrayList<PointF> forces=new ArrayList<PointF>();
	float gap;
	float k=.0005f;
	float dt=.4f;
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
		for(int i=0;i<forces.size();i++){
			forces.get(i).set(0,0);
		}
		for(int A=0;A<1;A++){
			for(int i=grabbedIndex+1;i<points.size();i++){
				PointF parent=points.get(i-1);
				PointF self=points.get(i);
				PointF child=new PointF();
				try{
					child=points.get(i+1);
				}catch(Exception e){
					child.set(self);
				}
				double distanceParent=Math.sqrt(Math.pow(parent.x-self.x,2)+Math.pow(parent.y-self.y,2));
				double distanceChild=Math.sqrt(Math.pow(child.x-self.x,2)+Math.pow(child.y-self.y,2));
				if(distanceParent>gap){
					double angle=Math.atan2(parent.y-self.y,parent.x-self.x);
					forces.get(i).x+=(float)(Math.pow((distanceParent-gap),2)*Math.cos(angle))*k;
					forces.get(i).y+=(float)(Math.pow((distanceParent-gap),2)*Math.sin(angle))*k;
				}else if(distanceChild>gap){
					double angle=Math.atan2(self.y-child.y,self.x-child.x);
					forces.get(i).x+=(float)(Math.pow((distanceChild-gap),2)*Math.cos(angle))*k;
					forces.get(i).y+=(float)(Math.pow((distanceChild-gap),2)*Math.sin(angle))*k;
				}
			}	
			for(int i=grabbedIndex-1;i>=0;i--){
				PointF parent=points.get(i+1);
				PointF self=points.get(i);
				PointF child=new PointF();
				try{
					child=points.get(i-1);
				}catch(Exception e){
					child.set(self);
				}
				double distanceParent=Math.sqrt(Math.pow(parent.x-self.x,2)+Math.pow(parent.y-self.y,2));
				double distanceChild=Math.sqrt(Math.pow(child.x-self.x,2)+Math.pow(child.y-self.y,2));
				if(distanceParent>gap){
					double angle=Math.atan2(parent.y-self.y,parent.x-self.x);
					forces.get(i).x+=(float)(Math.pow((distanceParent-gap),2)*Math.cos(angle))*k;
					forces.get(i).y+=(float)(Math.pow((distanceParent-gap),2)*Math.sin(angle))*k;
				}else if(distanceChild>gap){
					double angle=Math.atan2(self.y-child.y,self.x-child.x);
					forces.get(i).x+=(float)(Math.pow((distanceChild-gap),2)*Math.cos(angle))*k;
					forces.get(i).y+=(float)(Math.pow((distanceChild-gap),2)*Math.sin(angle))*k;
				}
			}
		}
		for(int i=0;i<points.size();i++){
			float mass=.5f;
			velocities.get(i).x+=(forces.get(i).x*dt)/mass;
			velocities.get(i).y+=(forces.get(i).y*dt)/mass;
			points.get(i).x+=velocities.get(i).x*dt;
			points.get(i).y+=velocities.get(i).y*dt;
			velocities.get(i).x*=.95f;
			velocities.get(i).y*=.95f;
		}
		for(int i=1;i<points.size();i++){
			canvas.drawLine(points.get(i-1).x,points.get(i-1).y,points.get(i).x,points.get(i).y,paint);
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
		forces.clear();
		gap=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getResources().getDisplayMetrics());
		int num=(int)(height*.65/gap);
		float x=width/2;
		for(int i=0;i<num;i++){
			points.add(new PointF(x,i*gap+15*gap));
			velocities.add(new PointF(0,0));
			forces.add(new PointF(0,0));
		}
	}
	
	public void setForces(int index){
		PointF home=points.get(index);
		for(int i=0;i<points.size();i++){
			if(i!=index){
				PointF guest=points.get(i);
				float dT=Math.abs(index-i)*gap;
				float dA=(float)Math.sqrt(Math.pow(home.x-guest.x,2)+Math.pow(home.y-guest.y,2));
				if(dT>dA){
					double angle;
					if(i<index){
						angle=Math.atan2(guest.y-home.y,guest.x-home.x);
					}else{
						angle=Math.atan2(home.y-guest.y,home.x-guest.x);
					}
					forces.get(i).x+=(float)(Math.pow((dA-dT),2)*Math.cos(angle))*k;
					forces.get(i).y+=(float)(Math.pow((dA-dT),2)*Math.sin(angle))*k;
				}
			}
		}
	}
	
}
	
