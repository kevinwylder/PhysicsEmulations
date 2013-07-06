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
	PointF finger=new PointF(-1,-1);
	float gap;
	float k=.01f;
	float dt=.1f;
	boolean gravity=false;
	int grabbedIndex=0;
	float maxForce=0;
	float minForce=0;
	
	public Thread(Context ctx){
		super(ctx);
		paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2.5f,getResources().getDisplayMetrics()));
	}
	
	public void setGravity(Boolean active){
		gravity=!gravity;
	}

	public void drawCanvas(Canvas canvas){
		if(finger.x!=-1&finger.y!=-1)points.get(grabbedIndex).set(finger);
		float y=0;
		if(gravity) y=60;
		for(int i=0;i<forces.size();i++){
			forces.get(i).set(0,y);
		/*	PointF self=points.get(i);
			PointF parent;
			PointF child;
			try{
				parent=points.get(i-1);
			}catch(Exception e){
				parent=self;
			}try{
				child=points.get(i+1);
			}catch(Exception e){
				child=self;
			}
			double dParent=distance(self,parent);
			double dChild=distance(self,child);
			if(dParent>gap*4){
				double angle=Math.atan2(parent.y-self.y,parent.x-self.x);
				points.get(i-1).set(self.x+(float)Math.cos(angle)*gap*2,self.y+(float)Math.sin(angle)*gap*2);
			}
			if(dChild>gap*4){
				double angle=Math.atan2(child.y-self.y,child.x-self.x);
				points.get(i+1).set(self.x+(float)Math.cos(angle)*gap*2,self.y+(float)Math.sin(angle)*gap*2);
			}*/
		}
		for(int i=grabbedIndex;i<points.size();i++){
			setForces(i,false);
		}
		for(int i=grabbedIndex;i>0;i--){
			setForces(i,true);
		}
		for(int i=0;i<points.size();i++){
			float mass=.5f;
			velocities.get(i).x+=(forces.get(i).x*dt)/mass;
			velocities.get(i).y+=(forces.get(i).y*dt)/mass;
			points.get(i).x+=velocities.get(i).x*dt;
			points.get(i).y+=velocities.get(i).y*dt;
			velocities.get(i).x*=.8f;
			velocities.get(i).y*=.8f;
			if(gravity&points.get(i).y>height){
				points.get(i).y=height;
				velocities.get(i).y=0;
			}
		}
		for(int i=1;i<points.size();i++){
			paint.setColor(Color.rgb(255-(int)(10-Math.max(Math.log(Math.hypot(forces.get(i).x,forces.get(i).y)),0))*25,0,0));
			canvas.drawLine(points.get(i-1).x,points.get(i-1).y,points.get(i).x,points.get(i).y,paint);
		}
	}

	public void handleTouch(MotionEvent event){
		if(event.getAction()==MotionEvent.ACTION_MOVE){
		//	points.get(grabbedIndex).set(event.getX(),event.getY());
		    finger.set(event.getX(),event.getY());
		}else if(event.getAction()==MotionEvent.ACTION_DOWN){
			double closest=Math.pow(2,20);
			for(int i=0;i<points.size();i++){
				double dist=Math.sqrt(Math.pow(points.get(i).x-event.getX(),2)+Math.pow(points.get(i).y-event.getY(),2));
				if(dist<closest){
					closest=dist;
					grabbedIndex=i;
				}
			}
		}else if(event.getAction()==MotionEvent.ACTION_UP){
			finger.set(-1,-1);
		}
	}

	public void surfaceChangedMethod(){
		points.clear();
		velocities.clear();
		forces.clear();
		gap=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getResources().getDisplayMetrics());
		int num=(int)(height*.25/gap);
		float x=width/2;
		for(int i=0;i<num;i++){
			points.add(new PointF(x,i*gap+15*gap));
			velocities.add(new PointF(0,0));
			forces.add(new PointF(0,0));
		}
	}
	
	public void setForces(int index,boolean up){
		if(!up){
			PointF home=points.get(index);
			for(int i=index+1;i<points.size();i++){
				PointF guest=points.get(i);
				float dT=(i-index)*gap;
				float dA=(float)distance(home,guest);
				if(dT<dA){
					double angle=Math.atan2(guest.y-home.y,guest.x-home.x)+Math.PI;
					forces.get(i).x+=(float)(Math.pow((dA-dT),2)*Math.cos(angle))*k/((i-index)*.2f);
					forces.get(i).y+=(float)(Math.pow((dA-dT),2)*Math.sin(angle))*k/((i-index)*.2f);
				}/*else if(dT-dA<gap*.3*(index-i)){
					double angle=Math.atan2(guest.y-home.y,guest.x-home.x)+Math.PI;
					forces.get(i).x+=(float)(-Math.pow((dA-dT),2)*Math.cos(angle))*k;//((i-index)*.5f);
					forces.get(i).y+=(float)(-Math.pow((dA-dT),2)*Math.sin(angle))*k;//((i-index)*.5f);
				}*/
			}
		}else{
			PointF home=points.get(index);
			for(int i=index-1;i>=0;i--){
				PointF guest=points.get(i);
				float dT=(index-i)*gap;
				float dA=(float)distance(home,guest);
				if(dT<dA){
					double angle=Math.atan2(guest.y-home.y,guest.x-home.x)+Math.PI;
					forces.get(i).x+=(float)(Math.pow((dA-dT),2)*Math.cos(angle))*k/((index-i)*.2f);
					forces.get(i).y+=(float)(Math.pow((dA-dT),2)*Math.sin(angle))*k/((index-i)*.2f);
				}/*else if(dT-dA<gap*.3*(index-i)){
					double angle=Math.atan2(guest.y-home.y,guest.x-home.x)+Math.PI;
					forces.get(i).x+=(float)(-Math.pow((dA-dT),2)*Math.cos(angle))*k;//((i-index)*.5f);
					forces.get(i).y+=(float)(-Math.pow((dA-dT),2)*Math.sin(angle))*k;//((i-index)*.5f);
				}*/
			}
		}
	}
	
	public double distance(PointF a, PointF b){
		return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2));
	}
	
}
	
