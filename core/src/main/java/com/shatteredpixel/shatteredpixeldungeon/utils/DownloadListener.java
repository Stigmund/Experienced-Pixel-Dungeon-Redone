package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.ui.DownloadType;

public interface DownloadListener {

    boolean downloadFile(final DownloadType _type, final String _exportDir, final String _internalGameDir);
}