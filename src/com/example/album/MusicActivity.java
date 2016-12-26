package com.example.album;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.album.MusicService.MBinder;;;

public class MusicActivity extends Activity {
	
	
	private ImageView imageView1;
	private TextView timeView,songTimel,songTimer,text_songName,textMusicLyric;
	private VerticalSeekBar seekbarVoice;
	//MyTextView textSinger;
	private SeekBar seekBar;
	private Button buttonMode,buttonPrevious,buttonContral,buttonNext,buttonVoice;
	Animation anim;
	//private MyBroadcastReceiver timeChangeReceiver;
	private MusicService ms;
	private String time,songName;
	private long longtime = 0;
	private int curTime = 0,ttime = 0;
	private final static int MODE_ONE = R.drawable.mos;
	private final static int MODE_CIRCLE =R.drawable.mcs;
	private final static int MODE_RANDOM = R.drawable.mrs;
	private static int mode[] = {MODE_CIRCLE,MODE_ONE,MODE_RANDOM};
	private static int curMode = 0,curAngel = 0;
	private boolean isPlaying = false,isFirstPlay = true,hasLyric = false,isChanging = false;
	
	private final static String TAG = "MusicActivity";

	private BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context contex, Intent intent) {
			// TODO Auto-generated method stub
			if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
				longtime += 60000;
				refreshTime();
		    }
		}
	};
	private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context contex, Intent intent) {
			if(intent.getAction().equals(MusicService.MFILTER)) {
				
		    	  if (intent.getIntExtra(MusicService.CURTIME,0)!=0) {
		    		  curTime = intent.getIntExtra(MusicService.CURTIME, 0) / 1000;
		    		  if(isPlaying && !isChanging) {
		    			  refreshseekBar();
		    		  }
		    	  }else if(intent.getIntExtra(MusicService.TOTALTIME,0)!=0) {
		    		  ttime = intent.getIntExtra(MusicService.TOTALTIME, 0) / 1000;
		    		  songTimer.setText(timeFormat(ttime));
		    	  }else if(!(TextUtils.isEmpty(intent.getStringExtra(MusicService.NAME)))) {
		    		  String str = intent.getStringExtra(MusicService.NAME);
		    		  songName = str.substring(0, str.indexOf("."));
		    		  text_songName.setText(songName);
		    	 }
			}
	    }
	};
	private ServiceConnection sc = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			ms =null;
			Log.d(TAG,"bind disconnect");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			ms = ((MBinder)service).getService();
			Log.d(TAG,"bind connect");
		}
	};
	
	private static final int MSG_UPDATE_TIME = 11;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_music);
		
		init();
		registerReceiver(timeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
		registerReceiver(serviceReceiver, new IntentFilter(MusicService.MFILTER));
		
		 
	}
	@Override
	public void onDestroy() {
		Log.d(TAG,"onDestroy");
		unregisterReceiver(timeChangeReceiver);
		unregisterReceiver(serviceReceiver);
		unbindService(sc);
		super.onDestroy();
	}
	
	private void init() {
		imageView1 = (ImageView)findViewById(R.id.image_music_rotate);
		timeView = (TextView)findViewById(R.id.text_music_time);
		seekBar = (SeekBar)findViewById(R.id.seekbar_music);
		seekbarVoice = (VerticalSeekBar)findViewById(R.id.seekbar_music_voice);
		songTimel = (TextView)findViewById(R.id.text_music_time_l);
		songTimer = (TextView)findViewById(R.id.text_music_time_r);
		textMusicLyric = (TextView)findViewById(R.id.text_music_lyric);
		//textSinger = (MyTextView)findViewById(R.id.textView_music_singer);
		text_songName = (TextView)findViewById(R.id.textView_music_songname);
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
		seekbarVoice.setVisibility(View.GONE);
		if(!hasLyric) {
			textMusicLyric.setGravity(Gravity.CENTER);
		}
		longtime = System.currentTimeMillis();
		songTimer.setText(timeFormat(ttime));
		refreshTime();
		//refreshseekBar();
		Log.d(TAG,"init time_r : " + ttime);
		songTimer.setText(timeFormat(ttime));
		anim = new RotateAnimation(1, 360 , Animation.RELATIVE_TO_SELF, 0.5f,  
                Animation.RELATIVE_TO_SELF, 0.5f); 
        //LinearInterpolator lir = new LinearInterpolator();  
        //anim.setInterpolator(lir);  
        anim.setDuration(20000);
        anim.setRepeatCount(-1);
        Intent bindIntent = new Intent(this,MusicService.class);
		bindService(bindIntent, sc, BIND_AUTO_CREATE);
		//ms.setMode(curMode); 为什么这里会出错！！！！！！！！
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		builder.setTitle("Title")  
		       .setMessage("Dialog content.")  
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
		               @Override  
		               public void onClick(DialogInterface dialog,   
		               int which) {  
		            	   
		            	   /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		                   intent.setType("/*");
		                   startActivityForResult(intent, 1);*/
		               }  
		       })  
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
		               @Override  
		               public void onClick(DialogInterface dialog,  
		                int which) {  
		               }  
		        })  
		       .show();  
		
		
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
		int progress = (int) ((curTime * 1.0 / ttime) * 270);
		seekBar.setProgress(progress);
		songTimel.setText(timeFormat(curTime));
	}
	public String timeFormat(int time) {
		int min,sec;
		min = time / 60;
		sec = time % 60;
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
					buttonContral.setBackgroundResource(R.drawable.plays);
					imageView1.startAnimation(anim);
					if(isFirstPlay) {
						ms.startPlay();
						isFirstPlay = false;
					}else {
						ms.restart();
					}
				}else {
					buttonContral.setBackgroundResource(R.drawable.stops);
					imageView1.clearAnimation();
					ms.parse();
				}
				break;
			case R.id.button_music_mode:
				curMode = (curMode + 1) % 3;
				buttonMode.setBackgroundResource(mode[curMode]);
				ms.setMode(curMode);
				break;
			case R.id.button_music_previous:
				isPlaying = true;
				buttonContral.setBackgroundResource(R.drawable.plays);
				imageView1.startAnimation(anim);
				ms.playPrevious();
				break;
				/*new Thread(new Runnable() {
					public void run() {
						sendKyeEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MENU,MainActivity.this);//不能跑在主线程
					}
				}).start();
				//sendKyeEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MENU,MainActivity.this);
				text1.setText("send menu");*/
			case R.id.button_music_next:
				isPlaying = true;
				buttonContral.setBackgroundResource(R.drawable.plays);
				imageView1.startAnimation(anim);
				ms.playNext();
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
				/*if (hasFocus) {
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
				break;*/
			case R.id.seekbar_music:
				if(!hasFocus) {
					//isChanging = false;
				}
		
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
				/*if(event.getAction()==MotionEvent.ACTION_DOWN){
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
				break;*/
			}
			return true;
		}
	};
	private OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(fromUser && isPlaying) {
				isChanging = true;
				Log.d(TAG,"seekbar change : " + progress);
				curTime = (int)(progress * ttime / 270.0);
				ms.setPosition(curTime * 1000);
				songTimel.setText(timeFormat(curTime));
/*				Log.d(TAG,"curtime :  change" + curTime);
				Log.d(TAG,"curtime :  change" + ttime);*/
				isChanging = false;
			}
		}
			

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			//isChanging = false;
		}

	};
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  {  
        if (resultCode == Activity.RESULT_OK)  
        {  
            Uri uri = data.getData();  
            Toast.makeText(MusicActivity.this, "make it : " + uri.getEncodedPath(), Toast.LENGTH_SHORT).show();
        }  
	} */ 
}
