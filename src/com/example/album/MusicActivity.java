package com.example.album;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.example.album.MusicService.MBinder;;;

public class MusicActivity extends Activity {
	
	
	private ImageView imageView1;
	private TextView timeView,songTimel,songTimer;
	private SeekBar seekBar;
	private Button buttonMode,buttonPrevious,buttonContral,buttonNext,buttonVoice;
	Animation anim;
	private MyBroadcastReceiver timeChangeReceiver,serviceReceiver;
	private MusicService ms;
	private String time,songName;
	private long longtime = 0;
	int curTime = 0,ttime = 240;
	boolean isPlaying = false;
	
	private final static String TAG = "MusicActivity";

	class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			Log.d(TAG,intent.getAction());
			if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
				longtime += 60000;
				refreshTime();
		    }else if(intent.getAction().equals(MusicService.MFILTER)) {
		    	  if (intent.getIntExtra(MusicService.CURTIME,0)!=0) {
		    		  curTime = intent.getIntExtra(MusicService.CURTIME, 0);
		    	  }else if(intent.getIntExtra(MusicService.TOTALTIME,0)!=0) {
		    		  ttime = intent.getIntExtra(MusicService.TOTALTIME, 0);
		    	  }else if(!(TextUtils.isEmpty(intent.getStringExtra(MusicService.NAME)))) {
		    		  songName = intent.getStringExtra(MusicService.NAME);
		    	  }
		    }
		}
	}
	private ServiceConnection sc = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			ms =null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			ms = ((MBinder)service).getService();
		}
	};
	
	private static final int MSG_UPDATE_TIME = 11;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_UPDATE_TIME:
				curTime ++;
				if(curTime >= ttime) {
					curTime = 0;
				}
				refreshseekBar();
				if(isPlaying) {
					handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
				}
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_music);
		init();
		registerReceiver(timeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
		//registerReceiver(serviceReceiver, new IntentFilter(MusicService.MFILTER));
		
		 
	}
	@Override
	public void onDestroy() {
	   unregisterReceiver(timeChangeReceiver);
	 //  unregisterReceiver(serviceReceiver);
	   //unbindService(sc);
	}
	
	private void init() {
		imageView1 = (ImageView)findViewById(R.id.image_music_rotate);
		timeView = (TextView)findViewById(R.id.text_music_time);
		seekBar = (SeekBar)findViewById(R.id.seekbar_music);
		songTimel = (TextView)findViewById(R.id.text_music_rtime);
		songTimer = (TextView)findViewById(R.id.text_music_ttime);
		
		buttonMode = (Button)findViewById(R.id.button_music_mode); 
		buttonPrevious = (Button)findViewById(R.id.button_music_previous); 
		buttonContral = (Button)findViewById(R.id.button_music_contral); 
		buttonNext = (Button)findViewById(R.id.button_music_next); 
		buttonVoice = (Button)findViewById(R.id.button_music_voice); 
		
		buttonMode.setOnFocusChangeListener(focusListener);
		buttonPrevious.setOnFocusChangeListener(focusListener);
		buttonContral.setOnFocusChangeListener(focusListener);
		buttonNext.setOnFocusChangeListener(focusListener);
		buttonVoice.setOnFocusChangeListener(focusListener);
		
		buttonMode.setOnClickListener(listener);
		buttonPrevious.setOnClickListener(listener);
		buttonContral.setOnClickListener(listener);
		buttonNext.setOnClickListener(listener);
		buttonVoice.setOnClickListener(listener);
		seekBar.setOnSeekBarChangeListener(seekBarListener);
		
		
		longtime = System.currentTimeMillis();
		
		refreshTime();
		refreshseekBar();
		anim = new RotateAnimation(1, 360 , Animation.RELATIVE_TO_SELF, 0.5f,  
                Animation.RELATIVE_TO_SELF, 0.5f);  
        LinearInterpolator lir = new LinearInterpolator();  
        anim.setInterpolator(lir);  
        anim.setDuration(18000);
        anim.setRepeatCount(-1);
		
		/*new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					 try {
						Thread.sleep(1000);
						handler.sendEmptyMessage(MSG_UPDATE_TIME);
						count++;
						if(count > 60000) {
							count = 0;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
				}
				
			}
		});*/
	}
	
	public void refreshTime() {
		Date curDate = new Date(longtime);//获取当前时间       
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm"); 
		time = formatter.format(curDate);
		timeView.setText(time);
	}
	public void refreshseekBar() {
		int progress = (int) ((curTime * 1.0 / ttime) * 100);
		Log.d(TAG,"refreshSeekbar");
		seekBar.setProgress(progress);
		songTimel.setText(timeFormat(curTime));
		songTimer.setText(timeFormat(ttime));
	}
	public String timeFormat(int time) {
		int min,sec;
		min = curTime / 60;
		sec = curTime % 60;
		String str = new String("");
		if(min < 10) {
			str += "0";
			str += min;
			str += ":";
			if(sec < 10) {
				str += "0";
				str += sec;
			}
			else {
				str += sec;
			}
		}else {
			str += min;
			str += ":";
			if(sec < 10) {
				str += "0";
				str += sec;
			}
			else {
				str += sec;
			}
		}
		return str;
	}
	
	private OnClickListener listener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.button_music_contral:
				isPlaying = !isPlaying;
				if(isPlaying) {
					buttonContral.setBackgroundResource(R.drawable.play);
					imageView1.startAnimation(anim);
					handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
					ms.restart();
				}else {
					buttonContral.setBackgroundResource(R.drawable.pause);
					imageView1.clearAnimation();
					ms.parse();
				}
				break;
			case R.id.button_music_mode:
				Log.d(TAG,"clieck the mode button start service");
				startService(new Intent(MusicActivity.this,MusicService.class));
				//bindService(intent,sc,Context.BIND_AUTO_CREATE);
				break;
			case R.id.button_music_previous:
				Log.d(TAG,"clieck the p button stop service");
				Intent intent1 = new Intent(MusicActivity.this,MusicService.class);
				stopService(intent1);
				
				/*new Thread(new Runnable() {
					public void run() {
						sendKyeEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MENU,MainActivity.this);//不能跑在主线程
					}
				}).start();
				//sendKyeEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MENU,MainActivity.this);
				text1.setText("send menu");*/
			default:

				break;
			}
		}
	};
	
	private OnFocusChangeListener focusListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button_music_contral:
				if (hasFocus) {
					if(isPlaying) {
						buttonContral.setBackgroundResource(R.drawable.c1);
					}else {
						buttonContral.setBackgroundResource(R.drawable.c3);
					}
				} else {
					if(isPlaying) {
						buttonContral.setBackgroundResource(R.drawable.c2);
					}else {
						buttonContral.setBackgroundResource(R.drawable.c4);
					}
				}
				break;
		
			default:
				break;
			}
		}
	};
	
	private OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.button_music_contral:
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					if(isPlaying) {
						buttonContral.setBackgroundResource(R.drawable.c1);
					}else {
						buttonContral.setBackgroundResource(R.drawable.c3);
					}
	            }else if(event.getAction()==MotionEvent.ACTION_UP){  
	            	if(isPlaying) {
						buttonContral.setBackgroundResource(R.drawable.c2);
					}else {
						buttonContral.setBackgroundResource(R.drawable.c4);
					} 
	            }  
				break;
			}
			return true;
		}
	};
	private OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			Log.d(TAG,"seekbar change : " + progress);
			curTime = (int)(progress * ttime / 100.0);
			songTimel.setText(timeFormat(curTime));
			Log.d(TAG,"curtime :  change" + curTime);
			Log.d(TAG,"curtime :  change" + ttime);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

	};

}
