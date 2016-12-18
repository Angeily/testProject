package com.example.album;

import java.io.FileNotFoundException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.input.InputManager;
import android.hardware.input.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {

	private TextView text1;
	private Button button1, button2,button_menu;
	private ImageView imageView;
	private ActionBar actionBar;
	private boolean keyStop = false, fullScreen = true;
	private String version;
	private boolean isAndroidM, noEvent;
	private int count;
	private long afterMem,beforeMem;
	private static final String TAG = "MainActivity";
	private static final int MSG_START_DREAM = 10;
	private static final int MSG_ACTIONBAR_SHOW = 11;
	private static final int MSG_SHOW_TOAST = 12;

	private OnClickListener listener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.button_1:
				Intent intent = new Intent(
						Intent.ACTION_GET_CONTENT,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 0);
				break;
			case R.id.button_2:
				new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						clean();
					}
					
				}).start();
				break;
			case R.id.button_menu:
				//sendKyeEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MENU,MainActivity.this);
			default:

				break;
			}
		}
	};
	
	private OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button_1:
				if(event.getAction()==MotionEvent.ACTION_DOWN){  
	                button1.setBackgroundResource(R.drawable.button_focus);  
	            }else if(event.getAction()==MotionEvent.ACTION_UP){  
	                button1.setBackgroundResource(R.drawable.button_background);  
	            }  
				break;
			case R.id.button_2:
				if(event.getAction()==MotionEvent.ACTION_DOWN){  
	                button2.setBackgroundResource(R.drawable.button_focus);  
	            }else if(event.getAction()==MotionEvent.ACTION_UP){  
	                button2.setBackgroundResource(R.drawable.button_background);  
	            }  
				break;
			case R.id.button_menu:
				if(event.getAction()==MotionEvent.ACTION_DOWN){  
	                button_menu.setBackgroundResource(R.drawable.button_menu_press);  
	            }else if(event.getAction()==MotionEvent.ACTION_UP){  
	                button_menu.setBackgroundResource(R.drawable.button_menu);  
	            }  
				break;
				
			default:

				break;
			}
			return false;
		}
	};

	private OnFocusChangeListener focusListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button_1:
				if (hasFocus) {
					button1.setBackgroundResource(R.drawable.button_focus);
				} else {
					button1.setBackgroundResource(R.drawable.button_background);
				}
				break;
			case R.id.button_2:
				if (hasFocus) {
					button2.setBackgroundResource(R.drawable.button_focus);
				} else {
					button2.setBackgroundResource(R.drawable.button_background);
				}
			}

		}

	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_DREAM: // dreaming need to wait keyevent pass by
				Log.d(TAG, "handle the message : " + msg.what);
				if (isAndroidM) {
					startDream();
				} else {
					Toast.makeText(MainActivity.this, "系统不支持",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case MSG_ACTIONBAR_SHOW:
				/*
				 * if(noEvent) { if(!fullScreen) { actionBar.hide(); } }
				 */
				noEvent = true;
				// handler.sendEmptyMessageDelayed(MSG_ACTIONBAR_SHOW, 10000);
				break;
			case MSG_SHOW_TOAST:
				Toast.makeText(MainActivity.this, "帮你个傻逼清理 " + count + " process, "  
                + (afterMem - beforeMem) + "M", Toast.LENGTH_LONG).show();  
				break;
			default:
				Log.d(TAG, "msg : " + msg.what);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button1 = (Button) findViewById(R.id.button_1);
		button2 = (Button) findViewById(R.id.button_2);
		button_menu = (Button) findViewById(R.id.button_menu);
		text1 = (TextView) findViewById(R.id.textview_1);
		imageView = (ImageView) findViewById(R.id.image_view_1);		
		Log.d(TAG, "oncreate");
		button1.setOnClickListener(listener);
		button2.setOnClickListener(listener);
		button_menu.setOnClickListener(listener);
		button1.setOnFocusChangeListener(focusListener);
		button2.setOnFocusChangeListener(focusListener);
		button1.setOnTouchListener(touchListener);
		button2.setOnTouchListener(touchListener);
		button_menu.setOnTouchListener(touchListener);		
		imageView = (ImageView) findViewById(R.id.image_view_1);
		imageView.setVisibility(View.INVISIBLE);
		actionBar = getActionBar();
		version = Build.VERSION.RELEASE;
		isAndroidM = isAndroidM();
		if (fullScreen) {
			actionBar.hide();
		}
		// handler.sendEmptyMessageDelayed(MSG_ACTIONBAR_SHOW,10000);
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		if (requestCode == 0) {
			Uri uri = data.getData();
			bitmapFactory(uri);
		}
	}

	private void bitmapFactory(Uri uri) {
		// TODO Auto-generated method stub
		BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 8;//设置生成的图片为原图的八分之一
		options.inJustDecodeBounds = true;// 这将通知
											// BitmapFactory类只须返回该图像的范围,而无须尝试解码图像本身
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int heightRadio = (int) Math.ceil(options.outHeight / (float) height);
		int widthRadio = (int) Math.ceil(options.outWidth / (float) width);
		if (heightRadio > 1 && widthRadio > 1) {
			if (heightRadio > widthRadio) {
				options.inSampleSize = heightRadio;
			} else {
				options.inSampleSize = widthRadio;
			}
		}
		// 真正解码图片
		options.inJustDecodeBounds = false;
		Bitmap b;
		try {
			b = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri), null, options);
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

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
		switch (id) {
		case R.id.action_settings:
			break;
		case R.id.menu_startgame:
			if (isAndroidM) {
				startGame();
			} else {
				Toast.makeText(MainActivity.this, "系统不支持该游戏",
						Toast.LENGTH_SHORT).show();
			}
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keycode = event.getKeyCode();
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (isAndroidM) {
					showMenu();
				}
			}
			break;
		case KeyEvent.KEYCODE_PROG_YELLOW:
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (isAndroidM) {
					startGame();
				} else {
					Toast.makeText(MainActivity.this, "系统不支持该游戏",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case KeyEvent.KEYCODE_PROG_RED: // red key to prevent other key hit
			if (event.getAction() == KeyEvent.ACTION_UP) {
				keyStop = !keyStop;
				if (keyStop) {
					Toast.makeText(MainActivity.this, "大部分按键被屏蔽！",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "屏蔽解除！",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case KeyEvent.KEYCODE_PROG_BLUE:// blue key to start dreaming
			if (event.getAction() == KeyEvent.ACTION_UP) {
				handler.sendEmptyMessageDelayed(MSG_START_DREAM, 500);
			}
			break;
		case KeyEvent.KEYCODE_PROG_GREEN:
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (fullScreen) {
					actionBar.show();
					fullScreen = false;
				} else {
					actionBar.hide();
					fullScreen = true;
				}
			}
			break;
		default:
			break;
		}
		if (keyStop) {
			return true;
		} else {
			noEvent = false;
			/*
			 * if(!fullScreen) { actionBar.show(); }
			 */
			return super.dispatchKeyEvent(event);
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		super.dispatchTouchEvent(ev);
		noEvent = false;
		/*
		 * if(!fullScreen) { actionBar.show(); }
		 */
		return true;
	}

	public void showMenu() {
		View mDecor = getWindow().getDecorView();
		int vgbId;
		try {
			Class docor = Class
					.forName("com.android.internal.widget.ActionBarOverlayLayout");
			Class c = Class.forName("com.android.internal.R$id");
			Object obj = c.newInstance();
			Field field = c.getField("decor_content_parent");
			vgbId = field.getInt(obj);
			Object vgb = mDecor.findViewById(vgbId);
			Method show = docor.getMethod("showOverflowMenu");
			show.invoke(vgb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startDream() {
		Intent intent = new Intent();
		intent.setClassName("com.android.deskclock",
				"com.android.deskclock.ScreensaverActivity");
		startActivity(intent);
	}

	public void startGame() {
		Intent intent = new Intent();
		intent.setClassName("com.android.systemui",
				"com.android.systemui.egg.MLandActivity");
		startActivity(intent);
	}

	public boolean isAndroidM() {
		return (version.contains("6.0"));
	}
	public void clean(){
	ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
    List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();  
    List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);  
    Log.d(TAG,"size : " + infoList.size());
    Log.d(TAG,"size : " + serviceInfos.size());
    beforeMem = getAvailMemory(MainActivity.this);  
    Log.d(TAG, "-----------before memory info : " + beforeMem);  
    count = 0;  
    if (infoList != null) {  
        for (int i = 0; i < infoList.size(); ++i) {  
        	ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);  
            Log.d(TAG, "process name : " + appProcessInfo.processName);  
            Log.d(TAG,"size : " + infoList.size());
            Log.d(TAG, "importance : " + appProcessInfo.importance);  
            if (appProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {  
                String[] pkgList = appProcessInfo.pkgList;  
                Log.d(TAG,"length : " + pkgList.length);
                for (int j = 0; j < pkgList.length; ++j) {
                    Log.d(TAG, "It will be killed, package name : " + pkgList[j]);  
                    am.killBackgroundProcesses(pkgList[j]);  
                    count++;  
                }  
            }  

        }  
    }  

    afterMem = getAvailMemory(MainActivity.this);  
    Log.d(TAG, "----------- after memory info : " + afterMem);  
    handler.sendEmptyMessage(MSG_SHOW_TOAST);
	}
	
	
	public long getAvailMemory(Context context) {  //获取可用内存
        // 获取android当前可用内存大小  
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        MemoryInfo mi = new MemoryInfo();  
        am.getMemoryInfo(mi);  
        //mi.availMem; 当前系统的可用内存  
        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化  
        Log.d(TAG, "可用内存---->>>" + mi.availMem / (1024 * 1024));  
        return mi.availMem / (1024 * 1024);  
    }
	
	public void sendKyeEvent(int action,int keyCode,Context context){
		long eventTime = SystemClock.uptimeMillis();  
	    long mKeyRemappingSendFakeKeyDownTime = 0;
		if (action == KeyEvent.ACTION_DOWN) {  
	        mKeyRemappingSendFakeKeyDownTime = eventTime;  
	    }  
	  
	    KeyEvent keyEvent = new KeyEvent(mKeyRemappingSendFakeKeyDownTime, eventTime, action, keyCode, 0);  
	    InputManager inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);  
	   // inputManager.
	   // inputManager.injectInputEvent(keyEvent, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC); //INJECT_INPUT_EVENT_MODE_ASYNC
	}
	
}



