package com.example.album;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MusicActivity extends Activity {
	
	
	private ImageView imageView1;
	private TextView timeView,songTimel,songTimer;
	private ProgressBar progressBar;
	private Button buttonMode,buttonPrevious,buttonContral,buttonNext,buttonVoice;
	Animation anim;
	private String time;
	private long longtime = 0;
	int curTime = 0;
	private double ttime = 240.0;
	boolean isPlaying = false;

	private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (Intent.ACTION_TIME_TICK.equals(arg1.getAction())) {
				longtime += 60000;
				refreshTime(longtime);
		       }
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
				refreshProgressBar();
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
		registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
	
	}
	@Override
	public void onDestroy() {
	   unregisterReceiver(mTimeRefreshReceiver);
	}
	
	private void init() {
		imageView1 = (ImageView)findViewById(R.id.image_music_rotate);
		timeView = (TextView)findViewById(R.id.text_music_time);
		progressBar = (ProgressBar)findViewById(R.id.pgb_music);
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
		
		
		longtime = System.currentTimeMillis();
		
		refreshTime(longtime);
		
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
	
	public void refreshTime(long longtime) {
		Date curDate = new Date(longtime);//获取当前时间       
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm"); 
		time = formatter.format(curDate);
		timeView.setText(time);
	}
	public void refreshProgressBar() {
		int min,sec;
		min = curTime / 60;
		sec = curTime % 60;
		String str = new String("");
		int progress = (int) ((curTime / ttime) * 100);
		progressBar.setProgress(progress);
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
		songTimel.setText(str);
		
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
				}else {
					buttonContral.setBackgroundResource(R.drawable.pause);
					imageView1.clearAnimation();
				}
				break;
			/*case R.id.button_2:
				//Intent musicIntent = new Intent(MainActivity.this,MusicActivity.class);
				startActivity(new Intent(MainActivity.this,MusicActivity.class));
				break;
			case R.id.button_menu:
				new Thread(new Runnable() {
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
			/*case R.id.button_music_contral:
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
				break;*/
			/*case R.id.button_music_contral:
				if (hasFocus) {
					songTimer.setText("text focous");
				} else {
					songTimer.setText("text unfocous");
				}
				break;
			case R.id.button_music_mode:
				if (hasFocus) {
					songTimer.setText("image focous");
				} else {
					songTimer.setText("image unfocous");
				}
				break;
			case R.id.button_music_next:
				if (hasFocus) {
					songTimer.setText("menu focous");
				} else {
					songTimer.setText("menu unfocous");
				}
				break;
			case R.id.button_music_previous:
				if (hasFocus) {
					songTimer.setText("line focous");
				} else {
					songTimer.setText("line unfocous");
				}				
				break;
			case R.id.button_music_voice:
				if (hasFocus) {
					songTimer.setText("focous");
				} else {
					songTimer.setText("unfocous");
				}
				break;*/
			default:
				break;
			}

		}

	};
	


}
