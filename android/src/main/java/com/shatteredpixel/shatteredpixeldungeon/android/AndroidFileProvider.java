package com.shatteredpixel.shatteredpixeldungeon.android;

import androidx.annotation.XmlRes;
import androidx.core.content.FileProvider;

public class AndroidFileProvider extends FileProvider {

    public AndroidFileProvider() {

        super(R.xml.write_paths);
    }
}
