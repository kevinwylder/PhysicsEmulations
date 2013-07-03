package com.kevin.physicsemulations;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.*;
import android.view.*;

public class MainList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView lv=new ListView(this);
		String[] array=new String[]{
			"Box", "Multiple Boxes", "Thread", 
			"Mesh", "Irregular Box", "Multiple Irregular Boxes"
		};
		lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,array));
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> p1, View p2, int pos, long p4){
				Intent intent=new Intent(MainList.this,ViewActivity.class);
				intent.putExtra("pos",pos);
				startActivity(intent);
			}
		});
		setContentView(lv);
	}


}
