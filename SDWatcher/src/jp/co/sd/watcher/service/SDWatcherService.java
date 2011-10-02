package jp.co.sd.watcher.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.co.sd.watcher.R;
import jp.co.sd.watcher.observer.SDFileObserver;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class SDWatcherService extends Service {
	
	//バインダー　いらないかも
	public class SDWatcherBinder extends Binder {
		public SDWatcherService getService() {
			return SDWatcherService.this;
		}
	}
	
	//一応finalで作っとく
	private final int NOTIFICATION_ID = 0;
	private final int ICON_ID = R.drawable.icon;
	private final String SD_PATH = 
		Environment.getExternalStorageDirectory().getPath() + File.separator;
	
	//通知領域に表示する文字列
	private String notificationTitle;
	private String notificationMessage;
	
	//ノティフィケーションマネージャー
	private NotificationManager notificationManager;
	
	//ファイルオブザーバ
	private SDFileObserver observer;
	
	//SDFileObserverからの通知を受け取るレシーバ
	private class SDWatcherReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//ヌルチェック
			if (intent.getStringExtra(SDFileObserver.EVENT_PATH) != null) { 
				notificationMessage = intent.getStringExtra(SDFileObserver.EVENT_PATH);				
			}
			else {
				notificationMessage = "null";
			}
			
			showNotification(SDWatcherService.this, ICON_ID, 
					notificationMessage, notificationTitle, notificationMessage);			
		}
		
	}
	//レシーバのインスタンス
	private final SDWatcherReceiver receiver = new SDWatcherReceiver();
	
	@Override
	public void onCreate() {
		//タイトルの取得　getStringがstaticじゃないのでここで
		notificationTitle = getString(R.string.notification_title);
		
		//ノティフィケーションマネージャーの取得
		notificationManager = 
			(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		//ファイルオブサーバの取得
		observer = new SDFileObserver(SD_PATH, this);
		
		//インテントフィルターとレシーバの作成
		IntentFilter filter = new IntentFilter(SDFileObserver.SD_CHANGED);
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onStart(Intent intent, int startID) {
		//監視開始
		observer.startWatching();
		//通知領域に表示
		showNotification(this, ICON_ID, getString(R.string.notification_ticker), 
			notificationTitle, getString(R.string.notification_message));
	}
	
	@Override
	public void onDestroy() {
		//レシーバの開放
		unregisterReceiver(receiver);
		//監視停止
		observer.stopWatching();
		//通知の削除
		notificationManager.cancel(NOTIFICATION_ID);
	}
	
	//いらないかも
	@Override
	public IBinder onBind(Intent arg0) {
		return new SDWatcherBinder();
	}
	//これもいらないかも
	@Override
	public boolean onUnbind(Intent intent) {
		return false;
	}
	
	//ノティフィケーションの表示
	private void showNotification(Context context, 
			int iconID, String ticker, String title, String message) {
		//ノティフィケーションオブジェクトの生成
		Notification notification = 
			new Notification(iconID, ticker, System.currentTimeMillis());
		
		//通知領域をタップしたときに呼び出されるActivityの設定
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, 
			new Intent(context, jp.co.sd.watcher.activity.SDWatcherActivity.class), 0);
		
		//新しいメッセージを表示しなおす
		notification.setLatestEventInfo(context, title, message, pIntent);
		
		//一度消してから再表示
		//ノティフィケーションのキャンセル
		notificationManager.cancel(NOTIFICATION_ID);
		//ノティフィケーションの表示
		notificationManager.notify(NOTIFICATION_ID, notification);
		
	}
	
	private String formatDate(long milliSecond) {
		Date date = new Date(milliSecond);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatedDate = simpleDateFormat.format(date);
		return formatedDate;
	}
	
	private static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
