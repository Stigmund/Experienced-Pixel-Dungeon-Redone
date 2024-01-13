/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2020 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.android;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AsynchronousAndroidAudio;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.rohitss.uceh.UCEHandler;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsImpl;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.UpdateImpl;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.utils.DownloadListener;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

public class AndroidLauncher extends AndroidApplication implements DownloadListener {
	
	public static AndroidApplication instance;
	
	private static AndroidPlatformSupport support;

	private final int STORAGE_PERMISSION_CODE = 101;
	
	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			GdxNativesLoader.load();
			FreeType.initFreeType();
		} catch (Exception e){
			AndroidMissingNativesHandler.errorMsg = e.getMessage();
			Intent intent = new Intent(this, AndroidMissingNativesHandler.class);
			startActivity(intent);
			finish();
			return;
		}

		new UCEHandler.Builder(this).setUCEHEnabled(true).build();

		//there are some things we only need to set up on first launch
		if (instance == null) {

			instance = this;

			try {
				Game.version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (PackageManager.NameNotFoundException e) {
				Game.version = "???";
			}
			try {
				Game.versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			} catch (PackageManager.NameNotFoundException e) {
				Game.versionCode = 0;
			}

			if (UpdateImpl.supportsUpdates()) {
				Updates.service = UpdateImpl.getUpdateService();
			}
			if (NewsImpl.supportsNews()) {
				News.service = NewsImpl.getNewsService();
			}

			FileUtils.setDefaultFileProperties(Files.FileType.Local, "");

			// grab preferences directly using our instance first
			// so that we don't need to rely on Gdx.app, which isn't initialized yet.
			// Note that we use a different prefs name on android for legacy purposes,
			// this is the default prefs filename given to an android app (.xml is automatically added to it)
			SPDSettings.set(instance.getPreferences("ShatteredPixelDungeon"));

			File externalDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
			System.out.println("EXTERNAL DOWNLOADS EXIST (DOCS): "+ externalDownloads.exists());

			instance.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
			SPDSettings.setDownloadListener(this);


		} else {
			instance = this;
		}
		
		//set desired orientation (if it exists) before initializing the app.
		if (SPDSettings.landscape() != null) {
			instance.setRequestedOrientation( SPDSettings.landscape() ?
					ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE :
					ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT );
		}
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.depth = 0;
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			//use rgb565 on ICS devices for better performance
			config.r = 5;
			config.g = 6;
			config.b = 5;
		}
		
		config.useCompass = false;
		config.useAccelerometer = false;
		
		if (support == null) support = new AndroidPlatformSupport();
		else                 support.reloadGenerators();
		
		support.updateSystemUI();

		Button.longClick = ViewConfiguration.getLongPressTimeout()/1000f;
		
		initialize(new ShatteredPixelDungeon(support), config);
		
	}

	@Override
	public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
		return new AsynchronousAndroidAudio(context, config);
	}

	@Override
	protected void onResume() {
		//prevents weird rare cases where the app is running twice
		if (instance != this){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				finishAndRemoveTask();
			} else {
				finish();
			}
		}
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		//do nothing, game should catch all back presses
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		support.updateSystemUI();
	}
	
	@Override
	public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
		super.onMultiWindowModeChanged(isInMultiWindowMode);
		support.updateSystemUI();
	}

	@Override
	public void downloadFile(String _internalGameDir) {

		//exportSaveFile(_internalGameDir);
		//exportSaveFile2(_internalGameDir);
		exportSaveFile3(_internalGameDir);
	}

	/**
	 * Android 10 and lower only!
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		System.out.println("here");
		if (requestCode == STORAGE_PERMISSION_CODE) {

			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
			}
		}
	}

	// Handling result
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println(String.format("ACTIVITY RESULT -- requestCode: %d, resultCode: %d, data: %s", requestCode, resultCode, data.toString()));
	}

	public void exportSaveFile3(String _internalGameDir) {

		File readPath = new File(getFilesDir(), _internalGameDir);
		File readFile = new File(readPath, "game.dat");
		Uri readUri = getUriForFile(getContext(), BuildConfig.APPLICATION_ID, readFile);

		File writePath = new File(getExternalFilesDir(""), _internalGameDir);
		File writeFile = new File(writePath, "game.dat");
		Uri writeUri = getUriForFile(getContext(), BuildConfig.APPLICATION_ID, writeFile);

		int flagMode = Intent.FLAG_GRANT_READ_URI_PERMISSION+FLAG_GRANT_WRITE_URI_PERMISSION;
		grantUriPermission("Backups", writeUri, flagMode);

		Intent intent = new Intent();
		intent.setData(readUri);
		intent.setPackage("Backups");
		intent.setFlags(flagMode);
		intent.setAction(Intent.ACTION_SEND);
		startActivity(intent);
	}

	public void exportSaveFile2(String _internalGameDir) {

		File existingGame = new File(instance.getContext().getFilesDir(), _internalGameDir+"/game.dat");
		System.out.println("existingGame: "+ existingGame.getAbsolutePath());
		System.out.println("- Exists? "+ existingGame.exists());

		for (File externalDir : instance.getContext().getExternalFilesDirs("")) {

			File outputDir = new File(externalDir.getAbsolutePath());
			System.out.println("outputDir: "+ outputDir.getAbsolutePath());
			System.out.println("- Exists? "+ outputDir.exists());

			System.out.println("Has WRITE_EXTERNAL_STORAGE: "+ instance.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));

			try (InputStream inStream = java.nio.file.Files.newInputStream(existingGame.toPath())) {

				// Path to the your output file
				//File publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				//String outputPath = new File(publicDownloads, "game.dat").getAbsolutePath();

				try (OutputStream outStream = java.nio.file.Files.newOutputStream(new File(outputDir, "game.dat").toPath())) {

					// transfer bytes from the inputfile to the outputfile

					byte[] buffer = new byte[1024];
					int length;

					while ((length = inStream.read(buffer)) > 0) {

						outStream.write(buffer, 0, length);
					}
				}
				catch (Exception e) {

					e.printStackTrace();
				}
			}
			catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	// Fucking hell! get this far and can't download non http/https uri's ! FML Android sucks!
	// Whole fucking week wasted.
	public void exportSaveFile(String _gameFolder) {

		// https://developer.android.com/reference/androidx/core/content/FileProvider
		File gamePath = new File(instance.getContext().getFilesDir(), _gameFolder);
		//File newFile = new File(gamePath, _gameFile);
		//Uri contentUri = getUriForFile(app.getContext(), "com.trashboxbobylev.experiencedpixeldungeon.redone", newFile);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

			// https://androidclarified.wordpress.com/2018/08/19/android-downloadmanager-example/
			DownloadManager.Request request = new DownloadManager.Request(Uri.fromFile(gamePath)).
					setTitle("Downloading Stig-PD slot: "+ _gameFolder)
					.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, _gameFolder)
					.setRequiresCharging(false)
					.setAllowedOverMetered(true)
					.setAllowedOverRoaming(true);

			DownloadManager dm = (DownloadManager) instance.getSystemService(Context.DOWNLOAD_SERVICE);
			dm.enqueue(request);
		}
	}
}