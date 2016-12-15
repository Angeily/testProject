package com.example.album;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {

	private TextView text1;
	private Button button1,button2;
	private ImageView imageView;
	private boolean keyStop = false;
	private String version;
	private boolean isAndroidM;
	private static final String TAG = "MainActivity";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button1 = (Button) findViewById(R.id.button_1);
		button2 = (Button) findViewById(R.id.button_2);
		text1 = (TextView)findViewById(R.id.textview_1);
		imageView = (ImageView)findViewById(R.id.image_view_1);
		Log.d(TAG,"oncreate");
		button1.setOnClickListener(listener); 
		button2.setOnClickListener(listener);
		version = Build.VERSION.RELEASE;
		isAndroidM = isAndroidM();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private OnClickListener listener = new OnClickListener(){
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch(v.getId()) {
			case R.id.button_1:
				Toast.makeText(MainActivity.this, R.string.success_show,Toast.LENGTH_SHORT).show();
				text1.setText(R.string.success_show);
				break;
			case R.id.button_2:
				//text1.setText(R.string.lie_show);
				text1.setText(version);
				Log.d(TAG,"version : " + version);
				break;
			
			default:
				
				break;
			}
		}
		
		
	};
	@Override
    public boolean dispatchKeyEvent ( KeyEvent event ) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_MENU){
			if(event.getAction() == KeyEvent.ACTION_UP){
				if(isAndroidM) {
					showMenu();
				}
			}
		}else if (event.getKeyCode() == KeyEvent.KEYCODE_PROG_YELLOW){
			if(event.getAction() == KeyEvent.ACTION_UP){
				if(isAndroidM) {
					startGame();
				}else {
					Toast.makeText(MainActivity.this, "系统不支持该游戏", Toast.LENGTH_SHORT).show();
				}
			}
		}else if (event.getKeyCode() == KeyEvent.KEYCODE_PROG_RED){
			if(event.getAction() == KeyEvent.ACTION_UP){
				keyStop = !keyStop;
				if(keyStop) {
					Toast.makeText(MainActivity.this, "大部分按键被屏蔽！", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(MainActivity.this, "屏蔽解除！", Toast.LENGTH_SHORT).show();
				}
			}
		}
		if(keyStop) {
			return true;
		}else {
			return super.dispatchKeyEvent ( event );

		}
		
	}
	public void showMenu(){
		View mDecor = getWindow().getDecorView();
        int vgbId;
        try {
            Class docor = Class.forName ( "com.android.internal.widget.ActionBarOverlayLayout" );
            Class c = Class.forName ( "com.android.internal.R$id" );
            Object obj = c.newInstance();
            Field field = c.getField ( "decor_content_parent" );
            vgbId = field.getInt ( obj );
            Object vgb =  mDecor.findViewById ( vgbId );
            Method show = docor.getMethod("showOverflowMenu");
            show.invoke(vgb);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
	
	}
	
	public void startGame(){
		Intent intent = new Intent();
		intent.setClassName("com.android.systemui", "com.android.systemui.egg.MLandActivity");
		startActivity(intent);
	}
	public boolean isAndroidM() {
		if(version.contains("6.0"));
		return keyStop;
		
	}
}
