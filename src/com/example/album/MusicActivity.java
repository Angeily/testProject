package com.example.album;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MusicActivity extends Activity {
	
	private int currAngle; 
	ImageView imageView1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_music);
		imageView1 = (ImageView)findViewById(R.id.image_music_rotate);
		Animation anim = new RotateAnimation(1, 360 , Animation.RELATIVE_TO_SELF, 0.5f,  
                Animation.RELATIVE_TO_SELF, 0.5f);  
        /** ���ٲ�ֵ�� */  
        LinearInterpolator lir = new LinearInterpolator();  
        anim.setInterpolator(lir);  
        anim.setDuration(9000);
        anim.setRepeatCount(-1);
		//Animation testAnim = AnimationUtils.loadAnimation(this, R.anim.anim_music);  
		imageView1.startAnimation(anim);  
		
	}
	
    public void positive(View v) {  
        Animation anim = new RotateAnimation(1, 360 , Animation.RELATIVE_TO_SELF, 0.5f,  
                Animation.RELATIVE_TO_SELF, 0.5f);  
        /** ���ٲ�ֵ�� */  
        LinearInterpolator lir = new LinearInterpolator();  
        anim.setInterpolator(lir);  
        anim.setDuration(3000);
        anim.setRepeatCount(-1);
        /** ������ɺ󲻻ָ�ԭ״ */  
        //anim.setFillAfter(true);  
      /*  currAngle += 180;  
        if (currAngle > 360) {  
            currAngle = currAngle - 360;  
        }  */
        imageView1.startAnimation(anim);  
    }  
      
    public void negative(View v) {  
    	  Animation anim = new RotateAnimation(0, -360 , Animation.RELATIVE_TO_SELF, 0.5f,  
                  Animation.RELATIVE_TO_SELF, 0.5f);  
          /** ���ٲ�ֵ�� */  
          LinearInterpolator lir = new LinearInterpolator();  
          anim.setInterpolator(lir);  
          anim.setDuration(3000);
          anim.setRepeatCount(Animation.INFINITE);
          /** ������ɺ󲻻ָ�ԭ״ */  
          anim.setFillAfter(true);  
        /*  currAngle += 180;  
          if (currAngle > 360) {  
              currAngle = currAngle - 360;  
          }  */
          imageView1.startAnimation(anim);
    }

}
