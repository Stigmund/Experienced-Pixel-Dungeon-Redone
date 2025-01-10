/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2024 Trashbox Bobylev
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AsynchronousAndroidAudio;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.rohitss.uceh.UCEHandler;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.EXPORT_DIR;
import static com.shatteredpixel.shatteredpixeldungeon.GamesInProgress.slotStates;

import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsImpl;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.UpdateImpl;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.DownloadType;
import com.shatteredpixel.shatteredpixeldungeon.utils.DownloadListener;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class AndroidLauncher extends AndroidApplication implements DownloadListener {
	
	public static AndroidApplication instance;
	
	private static AndroidPlatformSupport support;

	private final int STORAGE_PERMISSION_CODE = 101;
	private final int EXTERNAL_STORAGE_PERMISSION_CODE  = 23;

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			GdxNativesLoader.load();
			FreeType.initFreeType();
		} catch (Exception e){
			AndroidMissingNativesHandler.error = e;
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

			// instance.requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);

			SPDSettings.set(instance.getPreferences("ShatteredPixelDungeon"),
					this,
					getResources().getConfiguration().getLocales().get(0));

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

		//we manage this ourselves
		config.useImmersiveMode = false;

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
	public boolean exportFile(String _exportDir, String _internalGameDir) {

		// TODO: try this: https://stackoverflow.com/questions/65482280/how-do-you-get-the-path-of-the-android-documents-directory-in-android-q

		// doesn't work in debug mode apparently!
		//if (instance.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

			try {

				//exportFromOther();
				exportSaveFile(_exportDir, _internalGameDir);
				return true;
			}
			catch (Exception e) {

				e.printStackTrace();
				return false;
			}
		//}
		/*else {

			// Can't toast on a thread that has not called Looper.prepare() (e.g. not a main thread)
			//Toast.makeText(instance, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
		}*/

		//exportSaveFile2(_internalGameDir);
		//exportSaveFile3(_internalGameDir);

		//return false;
	}

	@Override
	public boolean importFile(String _importDir, String _internalGameDir) {

		try {

			importSaveFile(_importDir, _internalGameDir);
			return true;
		}
		catch (Exception e) {

			e.printStackTrace();
			return false;
		}
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

		if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {

			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private File getExportDirectory(String _saveDir) {

		return new File(instance.getContext().getExternalFilesDirs("")[0], EXPORT_DIR + _saveDir);
	}

	/**
	 * _saveDir and _internalGameDir are the end of the path, not the start.
	 * The start is determined by type (or internal if it's the _internalGameDir).
	 * @param _exportDir
	 * @param _internalGameDir
	 */
	public void exportSaveFile(String _exportDir, String _internalGameDir) {

		System.out.println("Has WRITE_EXTERNAL_STORAGE: "+ instance.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));

		File exportDirectory = getExportDirectory(_exportDir);
		System.out.println("exportDirectory: "+ exportDirectory.getAbsolutePath());
		System.out.println("- Exists? "+ exportDirectory.exists());
		if (!exportDirectory.exists()) {

			System.out.println("- Creating Dir! "+ exportDirectory.exists());
			exportDirectory.mkdirs();
			System.out.println("- Exists Now? "+ exportDirectory.exists());
		}

		File existingGame = new File(getContext().getFilesDir(), _internalGameDir);
		System.out.println("existingGame: "+ existingGame.getAbsolutePath());
		System.out.println("- Exists? "+ existingGame.exists());
		if (!exportDirectory.exists()) {

			System.out.println("- Creating Dir! "+ existingGame.exists());
			exportDirectory.mkdirs();
			System.out.println("- Exists Now? "+ existingGame.exists());
		}

		File[] files = getFiles(existingGame);
		Arrays.stream(files).forEach(f -> writeFile(exportDirectory, f));

		System.out.println("DONE!");
	}

	public void importSaveFile(String _importDir, String _internalGameDir) {

		File importDirectory = getExportDirectory(_importDir);
		System.out.println("importDirectory: "+ importDirectory.getAbsolutePath());
		System.out.println("- Exists? "+ importDirectory.exists());

		if (importDirectory.exists()) {

			File gameDirectory = new File(getContext().getFilesDir(), _internalGameDir);
			System.out.println("gameDirectory: "+ gameDirectory.getAbsolutePath());
			System.out.println("- Exists? "+ gameDirectory.exists());

			if (gameDirectory.exists()) {

				gameDirectory.delete();
			}

			System.out.println("- Creating Dir! "+ gameDirectory.exists());
			gameDirectory.mkdirs();
			System.out.println("- Exists Now? "+ gameDirectory.exists());

			Arrays.stream(getFiles(importDirectory)).forEach(f -> writeFile(gameDirectory, f));

			System.out.println("DONE!");
		}
	}

	private File[] getFiles(File _file) {

		return Optional.ofNullable(_file.listFiles()).orElse(new File[0]);
	}

	private boolean writeFile(File _outputDir, File _fileToCopy) {

		boolean success = false;
		try (InputStream inStream = java.nio.file.Files.newInputStream(_fileToCopy.toPath())) {

			// Path to the your output file
			//File publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			//String outputPath = new File(publicDownloads, "game.dat").getAbsolutePath();

			try (OutputStream outStream = java.nio.file.Files.newOutputStream(new File(_outputDir, _fileToCopy.getName()).toPath())) {

				// transfer bytes from the inputfile to the outputfile

				byte[] buffer = new byte[1024];
				int length;

				while ((length = inStream.read(buffer)) > 0) {

					outStream.write(buffer, 0, length);
				}

				success = true;
			}
			catch (Exception e) {

				e.printStackTrace();
			}
		}
		catch (Exception e) {

			e.printStackTrace();
		}

		return success;
	}
}