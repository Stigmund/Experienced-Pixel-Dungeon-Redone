package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.buttons.CopyButton;

import java.util.function.BiConsumer;

public interface WndUsesHeroSelector {
    String getActionTitle();
    Runnable getOnConfirmAction();
    BiConsumer<CopyButton, Window> getOnHeroSelect();
}