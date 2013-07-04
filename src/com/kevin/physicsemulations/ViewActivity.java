package com.kevin.physicsemulations;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class ViewActivity extends Activity
{
	
	BaseView view;
	Boolean gravity=false;
	
	@Override
	public void onCreate(Bundle sis){
		super.onCreate(sis);
		ActionBar ab=getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		SpinnerAdapter sa=ArrayAdapter.createFromResource(this,R.array.categories,R.layout.spinner_text);
		ab.setListNavigationCallbacks(sa, new ActionBar.OnNavigationListener(){
			public boolean onNavigationItemSelected(int p1, long p2){
				BaseView tmp=new BaseView(ViewActivity.this);
				switch(p1){
					case 0: tmp=new Box(ViewActivity.this); break;
					case 1: tmp=new MultipleBoxes(ViewActivity.this); break;
					case 2: tmp=new com.kevin.physicsemulations.Thread(ViewActivity.this); break;
					
				}
				view=tmp;
				setContentView(view);
				gravity=false;
				return false;
			}			
		});
		ab.setTitle("");
		ab.setSelectedNavigationItem(getIntent().getIntExtra("pos",0));
		ab.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId()==R.id.Gravity){
			gravity=!gravity;
			view.setGravity(gravity);
		}
		return true;
	}
	
}
