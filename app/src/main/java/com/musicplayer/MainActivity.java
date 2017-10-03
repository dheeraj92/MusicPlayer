package com.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener,  SensorEventListener {
	
	//------------------------------------use for sensor
	SensorManager manager;
	Sensor sense;
	TextView tv;
	long t1,t2,t3,t4;
	boolean b1=false,b2=false,b3=false,b4=false;
	//----------------------------------------------------
	
	byte[] art;
	MediaMetadataRetriever meta=new MediaMetadataRetriever();;
	String titleSong, album,artist,genre, composer, year, duration;
	private int k=0;
	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	
	ImageView album_art;
	private  MediaPlayer mp;
	
	Animation title;

	private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 200;
	
	private Handler mHandler = new Handler();
	private SongsFilter songFilter;
	private Converter conv;
	private int seekForwardTime = 5000; 
	private int seekBackwardTime = 5000; 
	private int currentSongIndex = 0; 
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {



		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainview);
		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		album_art=(ImageView)findViewById(R.id.image);
		
		//----------use for animation on song title
		title=AnimationUtils.loadAnimation(this, R.anim.titleanimation);
		
		songTitleLabel.setAnimation(title);
		songTitleLabel.startAnimation(title);
		//-----------------------------------
		
		//------------------use for sensor
		manager=(SensorManager)getSystemService(SENSOR_SERVICE);
		sense=manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//-------------------
		
		mp = new MediaPlayer();
		songFilter = new SongsFilter();
		conv = new Converter();


		requestPermission();
		
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important
		/*
		songsList = songFilter.getPlayList();
		if(songsList.size()>0) {
			playSong(0);
		}
		else {
			k = 1;
		}
			*/
		
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(k==0) {
					if(mp!=null) {
						if (mp.isPlaying()) {
							mp.pause();
							btnPlay.setImageResource(R.drawable.btn_play);
						} else {
							mp.start();
							btnPlay.setImageResource(R.drawable.btn_pause);
						}
					}
				}else {
					Toast.makeText(MainActivity.this, "you have no song", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(k==0 && mp!=null){
					int currentPosition = mp.getCurrentPosition();
					if(currentPosition + seekForwardTime <= mp.getDuration()){
						mp.seekTo(currentPosition + seekForwardTime);
					}else{
						mp.seekTo(mp.getDuration());
					}
				}else {
					Toast.makeText(MainActivity.this, "you have no song", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {	
				if(k==0 && mp!=null){
					int currentPosition = mp.getCurrentPosition();
					if(currentPosition - seekBackwardTime >= 0){
						mp.seekTo(currentPosition - seekBackwardTime);
					}else{
						mp.seekTo(0);
					}
				} else {
					Toast.makeText(MainActivity.this, "you have no song", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				playNextSong();

			}
		});
		
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				playPreviousSong();
			}
		});
		
		btnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(k==0){
					if(isRepeat){
						isRepeat = false;
						Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
						btnRepeat.setImageResource(R.drawable.btn_repeat);
					} else {
						isRepeat = true;
						Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
						isShuffle = false;
						btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
						btnShuffle.setImageResource(R.drawable.btn_shuffle);
					}
				} else {
                    Toast.makeText(MainActivity.this, "you have no song", Toast.LENGTH_LONG).show();
                }
			}
		});
		
		btnShuffle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(k==0){
                    if(isShuffle){
                        isShuffle = false;
                        Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                        btnShuffle.setImageResource(R.drawable.btn_shuffle);
                    }else{
                        isShuffle= true;
                        Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                        isRepeat = false;
                        btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                        btnRepeat.setImageResource(R.drawable.btn_repeat);
                    }
				} else {
                    Toast.makeText(MainActivity.this, "you have no song", Toast.LENGTH_LONG).show();
                }
			}
		});
		
		
		btnPlaylist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), FullList.class);
				startActivityForResult(i, 100);			
			}
		});
		
		
		
	}
	
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
         	 currentSongIndex = data.getExtras().getInt("songIndex");
         	
             playSong(currentSongIndex);
        }
 
    }

	public void requestPermission(){
		songsList = songFilter.getPlayList();
		isSongEmpty();
		if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				//start audio recording or whatever you planned to do
				songsList = songFilter.getPlayList();
				isSongEmpty();
			}else {
				if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}else{
					//Never ask again and handle your app without permission.
				}
			}
		}
	}
	
	public void  playSong(int songIndex){
		try {
        	mp.reset();
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			mp.prepare();
			mp.start();
			
        	songTitleLabel.setText(songsList.get(songIndex).get("songTitle"));
        	
        	//--------------image set
        	String songPath=songsList.get(songIndex).get("songPath");
        	meta.setDataSource(songPath);
        	try{
        		art=meta.getEmbeddedPicture();
    			Bitmap songImage=BitmapFactory.decodeByteArray(art, 0, art.length);
    			album_art.setImageBitmap(songImage);
        	}
        	catch(Exception e){
        		album_art.setImageResource(R.drawable.adele);
        	}
        	//-----------------------
			
			btnPlay.setImageResource(R.drawable.btn_pause);
			
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void playPreviousSong(){
		if(k==0){
			if(currentSongIndex > 0){
				playSong(currentSongIndex - 1);
				currentSongIndex = currentSongIndex - 1;
			}else{
				playSong(songsList.size() - 1);
				currentSongIndex = songsList.size() - 1;
			}
		} else {
			Toast.makeText(MainActivity.this, "you have no song", Toast.LENGTH_LONG).show();
		}
	}

	public void playNextSong(){
		if(k==0){
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				playSong(0);
				currentSongIndex = 0;
			}
		} else {
			Toast.makeText(MainActivity.this, "you have no song", Toast.LENGTH_LONG).show();
		}
	}

	public void isSongEmpty(){
		if(songsList.size()>0) {
			k = 0;
		}else{
			k=1;
		}
	}

	
	
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   long totalDuration = mp.getDuration();
			   long currentDuration = mp.getCurrentPosition();
			  
			   
			   songTotalDurationLabel.setText(""+conv.milliSecondsToTimer(totalDuration));
			   
			   songCurrentDurationLabel.setText(""+conv.milliSecondsToTimer(currentDuration));
			   
			   
			   int progress = (int)(conv.getProgressPercentage(currentDuration, totalDuration));
			   
			   songProgressBar.setProgress(progress);
			   
			   
		       mHandler.postDelayed(this, 100);
		   }
		};
		
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		if(k==0){
			mHandler.removeCallbacks(mUpdateTimeTask);
			int totalDuration = mp.getDuration();
			int currentPosition = conv.progressToTimer(seekBar.getProgress(), totalDuration);
		
			mp.seekTo(currentPosition);
		
			updateProgressBar();
    }
	}

	
	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		
		if(isRepeat){
			playSong(currentSongIndex);
		} else if(isShuffle){
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1));
			playSong(currentSongIndex);
		} else{
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}
	
	@Override
	 public void onDestroy(){
	 super.onDestroy();
	 mHandler.removeCallbacks(mUpdateTimeTask);
	    mp.release();
	 }


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	//float [] history = new float[2];
	//String [] direction = {"NONE","NONE"};
/*
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
		{
			Toast.makeText(MainActivity.this, "Sensor Status Unreliable",Toast.LENGTH_SHORT).show();
			return;
		}
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
		{
			float x = event.values[2];
		if(x<=-9){
			b1=true;
			t1=System.currentTimeMillis();
		}
		else if(x>=-1 && b1){
			b2=true;
			t2=System.currentTimeMillis();
		}
		else if(x>=9){
			b3=true;
			t3=System.currentTimeMillis();
		}
		else if(x<=1 && b3){
			b4=true;
			t4=System.currentTimeMillis();
		}
		
		if(b1 && b2 && (t2-t1)<=1000){
			Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show(); //Replace with next button function
			playNextSong();
			b1=false;
			b2=false;
		}
		
		if(b3 && b4 && (t4-t3)<=1000 ){
			Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
			playPreviousSong();
			b3=false;
			b4=false;
		}
			/*float xChange = history[0] - event.values[0];
			float yChange = history[1] - event.values[1];

			history[0] = event.values[0];
			history[1] = event.values[1];

			if (xChange > 9 && k==0){
				direction[0] = "LEFT";
				Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}
			}
			else if (xChange < -9 & k==0){
				direction[0] = "RIGHT";
				Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show(); //Replace with next button function
				if(currentSongIndex < (songsList.size() - 1)){
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				}else{
					playSong(0);
					currentSongIndex = 0;
				}
			}

			if (yChange > 2){
				direction[1] = "DOWN";
			}
			else if (yChange < -2){
				direction[1] = "UP";
			}
		}
		
	}*/
	protected void onResume() {
		super.onResume();
		manager.registerListener(this, sense, SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		manager.unregisterListener(this);
	}

	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;
	private static final int SHAKE_THRESHOLD = 800;
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			// only allow one update every 100ms.
			if ((curTime - lastUpdate) > 100) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				x = event.values[0];
				y = event.values[1];
				z = event.values[2];

				if(Round(x,4)>10.0000){
					Toast.makeText(this, "left shake detected", Toast.LENGTH_SHORT).show();
				}
				else if(Round(x,4)<-10.0000){
					//Log.d("sensor", "X Left axis: " + x);
					Toast.makeText(this, "right shake detected", Toast.LENGTH_SHORT).show();
				}

				float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;

				// Log.d("sensor", "diff: " + diffTime + " - speed: " + speed);
				if (speed > SHAKE_THRESHOLD) {
					//Log.d("sensor", "shake detected w/ speed: " + speed);
					//Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
				}
				last_x = x;
				last_y = y;
				last_z = z;
			}
		}
	}

	public static float Round(float Rval, int Rpl) {
		float p = (float)Math.pow(10,Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (float)tmp/p;
	}


}