package jp.co.sd.watcher.activity;

import java.util.List;

import jp.co.sd.watcher.R;
import jp.co.sd.watcher.service.SDWatcherService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SDWatcherActivity extends Activity implements View.OnClickListener {
	
	//いらないかも
	private SDWatcherService sdWatcherService;
	//同じくいらないかも
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			
			sdWatcherService = null;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			sdWatcherService = ((SDWatcherService.SDWatcherBinder)service).getService();
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //ボタンの取得とリスナーの設定
        Button btnStart = (Button)findViewById(R.id.btn_start);
        Button btnStop = (Button)findViewById(R.id.btn_stop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        
        //自分のサービスが生きてたらstartボタンを無効にする
        //押すたびにfinish()するのでここでOK
        if (isServiceExisted(this, "jp.co.sd.watcher.service.SDWatcherService")) {
        	btnStart.setEnabled(false);
        }
        
        //bindService用のintent いらないかも
        Intent intent = new Intent(
				this, jp.co.sd.watcher.service.SDWatcherService.class);
        //いらないかも
        bindService(intent, serviceConnection, 0);      
    }

//    @Override
//    public void onPause() {
//    	unbindService(serviceConnection);
//    	finish();
//    }
    //ボタンが押された時の処理
	public void onClick(View v) {
		//idの取得
		int id = v.getId();
		
		switch (id) {
		//スタートボタン
		case R.id.btn_start:
			//サービスの開始
			Intent intentStart = new Intent(
					this, jp.co.sd.watcher.service.SDWatcherService.class);
			startService(intentStart);
			//自分は終了
			finish();
			break;
			
		case R.id.btn_stop:
			//サービスの停止
			Intent intentStop = new Intent(
					this, jp.co.sd.watcher.service.SDWatcherService.class);
			stopService(intentStop);
			//自分も停止
			finish();
			break;
		//一応	
		default:
			break;				
		}
	}
	
	//サービスが生きているかの確認用
	public boolean isServiceExisted(Context context, String className) {
		//実行中のサービスを取得する準備
		ActivityManager activityManager = 
			(ActivityManager)getSystemService(ACTIVITY_SERVICE);
		
		//実行中のサービスを取得してListにする
		List<ActivityManager.RunningServiceInfo> serviceList = 
			activityManager.getRunningServices(Integer.MAX_VALUE);
		
		//実行中のサービスがなければ
		if (!(serviceList.size() > 0)) {
			return false;
		}
		//サービスのクラス名に指定したものと同じものがあればtrueを返す
		for (int i = 0; i < serviceList.size(); i++) {
			RunningServiceInfo serviceInfo = serviceList.get(i);
			ComponentName serviceName = serviceInfo.service;
			
			if (serviceName.getClassName().equals(className)) {
				return true;
			}
		}		
		return false;
	}
	
	private static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}