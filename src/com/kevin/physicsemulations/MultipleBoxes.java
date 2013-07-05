package com.kevin.physicsemulations;

import android.content.*;
import android.graphics.*;
import android.view.*;
import java.util.*;
import android.util.*;

public class MultipleBoxes extends BaseView
{
	
	ArrayList<Block> boxes=new ArrayList<Block>();
	int boxNum;
	boolean gravity;
	Paint paint=new Paint();
	Paint textPaint=new Paint();
	
	@Override
	public MultipleBoxes(Context ctx){
		super(ctx);
		paint.setColor(Color.RED);
		textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,getResources().getDisplayMetrics()));
	}
	
	public void setGravity(Boolean active){
		gravity=active;
		float ay;
		if(active) ay=.6f;
		else ay=0;
		for(int i=0;i<boxNum;i++){
			boxes.get(i).ay=ay;
		}
	}
	
	public void drawCanvas(Canvas canvas){
		for(int i=0;i<boxes.size();i++){
			Block bi=boxes.get(i);
			if(!bi.isGrabbed){
				bi.x+=bi.vx;
				bi.y+=bi.vy;
				bi.vy+=bi.ay;
				for(int a=9;a>0;a--){
					bi.historyX[a]=bi.historyX[a-1];
					bi.historyY[a]=bi.historyY[a-1];
				}
				bi.historyX[0]=bi.x;
				bi.historyY[0]=bi.y;
			}
			for(int a=i+1;a<boxes.size();a++){
				Block ba=boxes.get(a);
				// add to ↓ isGrabbed and if isGrabbed, do secondary collision
				if(ba.x+ba.size>bi.x&ba.x<bi.x+bi.size&ba.y+ba.size>bi.y&ba.y<bi.y+bi.size&bi.lastCollidedWith!=a&!bi.isGrabbed&!ba.isGrabbed){
					ba.lastCollidedWith=i;
					ba.collisionTimer=0;
					bi.lastCollidedWith=a;
					ba.collisionTimer=0;
					float xb=ba.x-bi.x;
					float yb=ba.y-bi.y;
					float greatest=0;
					float second;
					int flag=0;
					if(xb>greatest){
						flag=1;
						second=greatest;
						greatest=xb;
					}else{
						flag=2;
						second=greatest;
						greatest=Math.abs(xb);
					}if(yb>greatest){
						flag=3;
						second=greatest;
						greatest=yb;
					}else if(Math.abs(yb)>greatest){
						flag=4;
						second=greatest;
						greatest=Math.abs(yb);
					}
					if((greatest-second)/ba.size>.9){
						float tmpvx=ba.vx;
						float tmpvy=ba.vy;
						ba.vx=bi.vx;
						ba.vy=bi.vy;
						bi.vx=tmpvx;
						bi.vy=tmpvy;
					}else if(flag==1){
						ba.x=bi.x+bi.size;
						float tmpvx=ba.vx;
						ba.vx=bi.vx;
						bi.vx=tmpvx;
					}else if(flag==2){
						ba.x=bi.x-ba.size;
						float tmpvx=ba.vx;
						ba.vx=bi.vx;
						bi.vx=tmpvx;
					}else if(flag==3){
						ba.y=bi.y+bi.size;
						float tmpvy=ba.vy;
						ba.vy=bi.vy;
						bi.vy=tmpvy;
					}else{
						ba.y=bi.y-ba.size;
						float tmpvy=ba.vy;
						ba.vy=bi.vy;
						bi.vy=tmpvy;
					}
				}
			}
			if(bi.x<0){
				bi.x=0;
				bi.vx*=-.9f;
			}else if(bi.x>width-bi.size){
				bi.x=width-bi.size;
				bi.vx*=-.9f;
			}else if(bi.y>height-bi.size){
				bi.y=height-bi.size;
				bi.vy*=-.9f;
				if(gravity){
					bi.vy+=.3f;
					if(bi.vy+.3<0){
						bi.vy+=bi.ay;
						bi.vx*=.95f;
					}else{
						bi.vy=0;
						bi.vx=0;
					}
					if(bi.vy>-.7)bi.vy=0;
				}
			}else if(!gravity&bi.y<0){
				bi.y=0;
				bi.vy*=-.9f;
			}
			if(bi.lastCollidedWith!=-1){
				bi.collisionTimer++;
				if(bi.collisionTimer>5){
					bi.lastCollidedWith=-1;
				}
			}
			canvas.drawRect(bi.x,bi.y,bi.x+bi.size,bi.y+bi.size,paint);
		}
	}

	public void handleTouch(MotionEvent event){
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			float x=event.getX(event.getActionIndex());
			float y=event.getY(event.getActionIndex());
			for(int i=0;i<boxes.size();i++){
				Block b=boxes.get(i);
				if(b.x-b.size<x&b.x+(2*b.size)>x&b.y-b.size<y&b.y+(2*b.size)>y){
					b.fingerIndex=event.getActionIndex();
					b.isGrabbed=true;
					b.x=x+(b.size/2);
					b.y=y+(b.size/2);
					i=boxes.size();
				}
			}
		}else if(event.getAction()==MotionEvent.ACTION_MOVE){
			for(int i=0;i<event.getPointerCount();i++){
				float x=event.getX(i);
				float y=event.getY(i);
				for(int a=0;a<boxes.size();a++){
					Block b=boxes.get(a);
					if(b.fingerIndex==i){
						b.x=x;
						b.y=y;
						for(int s=9;s<0;s--){
							b.historyX[s]=b.historyX[s-1];
							b.historyY[s]=b.historyY[s-1];
						}
						b.historyX[0]=b.x;
						b.historyY[0]=b.y;
					}
				}
			}
		}else if(event.getAction()==MotionEvent.ACTION_UP){
			int index=event.getActionIndex();
			for(int i=0;i<boxes.size();i++){
				Block b=boxes.get(i);
				if(b.fingerIndex==index){
					i=boxes.size();
					b.fingerIndex=-1;
					float sigmaX=0;
					float sigmaY=0;
					for(int a=9;a>0;a--){
						sigmaX+=b.historyX[a-1]-b.historyX[a];
						sigmaY+=b.historyY[a-1]-b.historyY[a];
					}
					Log.w(Arrays.toString(b.historyX),Arrays.toString(b.historyY));
					b.vx=sigmaX/10;
					b.vy=sigmaY/10;
					b.isGrabbed=false;
				}
			}
		}
	}

	public void surfaceChangedMethod(){
		boxes.clear();
		float ppi=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN,1,getResources().getDisplayMetrics());
		boxNum=3+(int)(width/ppi);
		for(int i=0;i<boxNum;i++){
			boxes.add(new Block(width,height,gravity));
		}
	}
	
	class Block{
		
		public Block(int canvasWidth,int canvasHeight,boolean gravity){
			size=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30,getResources().getDisplayMetrics());
			x=(float)Math.random()*(canvasWidth-size);
			y=(float)Math.random()*(canvasHeight-size);
			double angle=Math.random()*(Math.PI*2);
			vx=(float)Math.cos(angle)*12;
			vy=(float)Math.sin(angle)*12;
			if(gravity) ay=.6f;
			else ay=0;
			Arrays.fill(historyX,0);
			Arrays.fill(historyY,0);
		}
		
		float x;
		float y;
		float vx;
		float vy;
		float ay;
		float tvx;
		float tvy;
		float tay;
		float size;
		int lastCollidedWith=-1;
		int collisionTimer=0;
		
		float[] historyX=new float[10];
		float[] historyY=new float[10];
		
		boolean isGrabbed=false;
		int fingerIndex=-1;
		
	}
	
}
