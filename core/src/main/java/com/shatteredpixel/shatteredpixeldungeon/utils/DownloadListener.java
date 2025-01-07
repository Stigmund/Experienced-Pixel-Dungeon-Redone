package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.ui.DownloadType;

public interface DownloadListener {

    boolean exportFile(final String _exportDir, final String _internalGameDir);
    boolean importFile(final String _importDir, final String _internalGameDir);
}