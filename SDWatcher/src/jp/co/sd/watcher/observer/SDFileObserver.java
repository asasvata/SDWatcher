package jp.co.sd.watcher.observer;

import jp.co.sd.watcher.R;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;

public class SDFileObserver extends FileObserver {
	
	//インテントフィルタ用のキーワード
	public static final String SD_CHANGED = "contents of SD were changed";
	//インテント用のキーワード
	public static final String EVENT_PATH = "event path";
	public static final String EVENT_DATE = "event date";
	
	//ディレクトリの監視用 デバッグで発見した数値
	private final int DIRECTORY_CREATE = 1073742080;
	private final int DIRECTORY_DELETE = 1073742336;
	//無いと困る
	private Context context;
	
	//コンストラクタ
	public SDFileObserver(String path, Context context) {
		super(path);
		this.context = context;
	}
	//監視中のディレクトリのイベントハンドラ
	@Override
	public void onEvent(int event, String path) {
		
		switch (event){
		
		//ファイルが作成された場合
		case FileObserver.CREATE:
			makeBroadcast(path, 
					context.getString(R.string.notification_massage_created), 
					System.currentTimeMillis());
			break;
			
		//ファイルが削除された場合
		case FileObserver.DELETE:
			makeBroadcast(path, 
					context.getString(R.string.notification_massage_deleted), 
					System.currentTimeMillis());
			break;
		
		//ディレクトリが作成された場合
		case DIRECTORY_CREATE:
			makeBroadcast(path, 
					context.getString(R.string.notification_massage_dir_created), 
					System.currentTimeMillis());
			break;
			
		//ディレクトリが削除された場合
		case DIRECTORY_DELETE:
			makeBroadcast(path, 
					context.getString(R.string.notification_massage_dir_deleted), 
					System.currentTimeMillis());
			break;
		
		//一応
		default:
			break;
		}
	}
	
	//ブロードキャストの実行
//	private void makeBroadcast(String path, String message) {
//		Intent intent = new Intent(SD_CHANGED);
//		path += message;
//		intent.putExtra(EVENT_PATH, path);
//		context.sendBroadcast(intent);
//	}
//	
//	private void makeBroadcast(String path, String message, String date) {
//		Intent intent = new Intent(SD_CHANGED);
//		path += message;
//		intent.putExtra(EVENT_PATH, path);
//		intent.putExtra(EVENT_DATE, date);
//		context.sendBroadcast(intent);
//	}
	
	private void makeBroadcast(String path, String message, long currentTimeMills) {
		Intent intent = new Intent(SD_CHANGED);
		path += message;
		intent.putExtra(EVENT_PATH, path);
		intent.putExtra(EVENT_DATE, currentTimeMills);
		context.sendBroadcast(intent);
	}	
}
