package com.example.album;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MusicService extends Service{
	 
	 private List<File> musicList;
	 private MediaPlayer player;
	 private int curPage;
	 public static final String MFILTER = "broadcast.intent.action.text";
	 public static final String NAME = "name";
	 public static final String TOTALTIME = "totaltime";
	 public static final String CURTIME = "curtime";
	 public static final String TAG = "MusicService";
	 protected static final int MSG_UPDATE_TIME = 11;
	// Timer timer;
	 
	 @Override
	 public IBinder onBind(Intent intent) {//1
	  // TODO Auto-generated method stub
		 return new MBinder();
	 }
	 
	 public class MBinder extends Binder{//2
		 public MusicService getService(){
			 return MusicService.this;
		 } 
	  
		 public MediaPlayer getPlayer(){
		  return player;
		 }
	  
	 }
	 @Override
	 public void onCreate() {
	  // TODO Auto-generated method stub
		 super.onCreate();
		 musicList = new ArrayList<File>();
		 
		 //File rootDir = Environment.getExternalStorageDirectory();//3
		 File rootDir = new File("/storage/external_storage/sda1");
		 Log.d(TAG,"service ocreate()");
		 Log.d(TAG,rootDir.getName());
		 Log.d(TAG,rootDir.getAbsolutePath());
		 Log.d(TAG,Environment.getExternalStorageState());
		 fillMusicList(rootDir);
		 Log.d(TAG,String.valueOf(musicList.size()));
		 player = new MediaPlayer();
		 player.setOnCompletionListener(completionListener);
		 /*if (musicList.size() != 0) {
			 	startPlay();
		 }*/
	 }
	 
	 @Override
	 public void onDestroy()
	 {
		 player.stop();
		 player.release();
		 super.onDestroy();
		 Log.d(TAG,"service destroy");
	 }
	 /*迭代获取 音乐 文件*/
	 private void fillMusicList(File dir){
		 File[] sourceFiles = dir.listFiles();
		 if(sourceFiles == null) return ;
		 Log.d("长度",String.valueOf(sourceFiles.length));
		 for(File file : sourceFiles){
			 if (file.isDirectory()) {
				 Log.d("文件夹名称",String.valueOf(file.getName()));
				 fillMusicList(file);
			 }
			 else {
				 String name = file.getName();
				 Log.d("childname",file.getName());
				 if (name.endsWith(".mp3")||name.endsWith(".acc")) {//支持的格式
					 musicList.add(file);
				 }
			 }
		 }
	 }
	 
	 private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case MSG_UPDATE_TIME:
					if(player != null) {
						mSendBroadCast(CURTIME,player.getCurrentPosition());
						handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
					}
					break;
				}
			}
		};
	 public void startPlay(){
		 mSendBroadCast(NAME,musicList.get(curPage).getName());//4
		 if(musicList.size() <= 0) return;
		 try {
			   player.setDataSource(musicList.get(curPage).getAbsolutePath());
			   player.prepare();
			   player.start();
			   player.getDuration();
			   mSendBroadCast(TOTALTIME,player.getDuration());
			   Log.d(TAG,"duration : " + player.getDuration());
			   handler.sendEmptyMessage(MSG_UPDATE_TIME);
			   //Timer timer = new Timer();
			   /*timer = new Timer();
			   timer.schedule(new TimerTask() {
				   @Override
				   public void run() {
					   mSendBroadCast(CURTIME,player.getCurrentPosition());
					   //Log.d(TAG,"timer schedule run and send broadcast every second");
				   }
			   
			   },0,1000);*/
		 }catch(IllegalArgumentException e) {
			 e.printStackTrace();
		 } catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
		 
	 
	 
	 
	 public void playNext(){
		 curPage = curPage==musicList.size()-1? (curPage+1)%musicList.size() : curPage+1; 
		 Log.d("curpage",String.valueOf(curPage));
		 //timer.cancel();
		 player.reset();
		 startPlay();
	 }
	 public void playPrevious(){
		 curPage = curPage==0? 0 : curPage-1; 
		 Log.d(TAG,String.valueOf(curPage));
		 //timer.cancel();
		 player.reset();
		  startPlay();
	 }
	 public void parse(){
		 
		 player.pause();
	 }
	 public void restart(){
		 player.start();
	 }
	 
	 private void mSendBroadCast(String key, String value){
		 Intent intent = new Intent(MFILTER);
		 intent.putExtra(key,value);//发送广播
		 sendBroadcast(intent);
	 }
	  
	 private void mSendBroadCast(String key, int value){
		 Intent intent = new Intent(MFILTER);
		 intent.putExtra(key,value);//发送广播
		 sendBroadcast(intent);
	 }
	 private OnCompletionListener completionListener = new OnCompletionListener() {
		  	@Override
	   		public void onCompletion(MediaPlayer mp) {
		  		player.reset();
		  		curPage = curPage==musicList.size()-1? (curPage+1)%musicList.size() : curPage+1; 
		  		//timer.cancel();
		  		startPlay();
		  		Log.d(TAG,"completion Listene");
		  	}
	  };
}
