package com.kevin.physicsemulations;

import android.content.*;
import android.graphics.*;
import android.view.*;
import java.util.*;
import android.util.*;

public class Mesh extends BaseView
{
    
	Paint paint=new Paint();
	ArrayList<PointF> points=new ArrayList<PointF>();
	ArrayList<PointF> velocities=new ArrayList<PointF>();
	ArrayList<PointF> forces=new ArrayList<PointF>();
	int grabbedIndex=0;
	boolean grabbed=false;
	int rowSize=7;
	float k=.005f;
	float dT=.1f;
	float gap;
	float dip;
	boolean gravity=false;
	Boolean[] recursion;
	
	public Mesh(Context ctx){
		super(ctx);
		dip=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1,getResources().getDisplayMetrics());
		paint.setStrokeWidth(dip*2.5f);
		recursion=new Boolean[rowSize*rowSize];
	}
	
	public void drawCanvas(Canvas canvas){
		for(int i=0;i<forces.size();i++){
			forces.get(i).set(0,0);
			recursion[i]=false;
		}
		recursion[grabbedIndex]=true;
		applyForces(grabbedIndex);
		recursion(grabbedIndex);
		for(int i=0;i<forces.size();i++){
			velocities.get(i).x+=forces.get(i).x*dT;
			velocities.get(i).y+=forces.get(i).y*dT;
			velocities.get(i).x*=.9f;
			velocities.get(i).y*=.9f;
			points.get(i).x+=velocities.get(i).x;
			points.get(i).y+=velocities.get(i).y;
		}
		for(int i=0;i<points.size();i++){
			if(i%rowSize!=rowSize-1&recursion[i]){
				canvas.drawLine(points.get(i).x,points.get(i).y,points.get(i+1).x,points.get(i+1).y,paint);
			}
			if(i<rowSize*(rowSize-1)){
				canvas.drawLine(points.get(i).x,points.get(i).y,points.get(i+rowSize).x,points.get(i+rowSize).y,paint);
			}
		}
	}
	
	public void handleTouch(MotionEvent event){
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			float distance=300f;
			for(int i=0;i<points.size();i++){
				double d=distance(new PointF(event.getX(),event.getY()),points.get(i));
				if(d<distance){
					grabbedIndex=i;
					grabbed=true;
					distance=(float)d;
				}
			}
		}else if(event.getAction()==MotionEvent.ACTION_MOVE){
			if(grabbed){
				points.get(grabbedIndex).set(event.getX(),event.getY());
			}
		}else if(event.getAction()==MotionEvent.ACTION_UP){
			grabbed=false;
		}
	}
	
	public void setGravity(Boolean active){
		gravity=active;
	}
	
	public void surfaceChangedMethod(){
		points.clear();
		velocities.clear();
		forces.clear();
		float wid=180*dip;
		gap=wid/rowSize;
		float init=(width-wid)/2;
		for(int i=0;i<rowSize*rowSize;i++){
			points.add(new PointF(init+((i%rowSize)*gap),(gap*2)+(int)Math.ceil(i/rowSize)*gap));
			velocities.add(new PointF(0,0));
			forces.add(new PointF(0,0));
			recursion[i]=false;
		}
	}
	
	public double distance(PointF a,PointF b){
		return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2));
	}
	
	public void applyForces(int index){
		PointF home=points.get(index);
		for(int i=0;i<forces.size();i++){
			if(!recursion[i]){
				PointF guest=points.get(i);
				float dT=(float)Math.sqrt(Math.pow(gap*(i%rowSize-index%rowSize),2)+Math.pow(gap*(Math.floor(i/rowSize)-Math.floor(index/rowSize)),2));
				float dA=(float)distance(home,guest);
				if(dT<dA){
					double angle=Math.atan2(home.y-guest.y,home.x-guest.x);
					forces.get(i).x+=(float)Math.pow((dA-dT),2)*Math.cos(angle)*k;
					forces.get(i).y+=(float)Math.pow((dA-dT),2)*Math.sin(angle)*k;
				}else if(dT*.3f>dA){
					double angle=Math.atan2(home.y-guest.y,home.x-guest.x);
					forces.get(i).x-=(float)Math.pow(dA-(.3f*dT),2)*Math.cos(angle)*k;
					forces.get(i).y-=(float)Math.pow(dA-(.3f*dT),2)*Math.sin(angle)*k;
				}
			}
		}
	}
	
	public void recursion(int index){
		int up=index-rowSize;
		int down=index+rowSize;
		int left=index-1;
		int right=index+1;
		Log.v("recursion",""+index);
		if(up>-1){
			if(!recursion[up]){
				applyForces(up);
				recursion[up]=true;
				recursion(up);
			}
		}
		if(down<rowSize*rowSize){
			if(!recursion[down]){
				applyForces(down);
				recursion[down]=true;
				recursion(down);
			}
		}
		if(left%rowSize!=rowSize-1&left>-1){
			if(!recursion[left]){
				applyForces(left);
				recursion[left]=true;
				recursion(left);
			}
		}
		if(right%rowSize!=0&right<rowSize*rowSize){
			if(!recursion[right]){
				applyForces(right);
				recursion[right]=true;
				recursion(right);
			}
		}
	}
	
}
