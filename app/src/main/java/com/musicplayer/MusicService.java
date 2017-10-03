package com.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

public class MusicService extends Service implements OnCompletionListener {
	MediaPlayer mp;
	@Override
	public void onCreate() {
		super.onCreate();
		//mp=MediaPlayer.create(this, R.raw.song);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(!mp.isPlaying())
			mp.start();
		return START_STICKY;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mp.isPlaying())
			mp.stop();
	}
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		stopSelf();
	}

}
