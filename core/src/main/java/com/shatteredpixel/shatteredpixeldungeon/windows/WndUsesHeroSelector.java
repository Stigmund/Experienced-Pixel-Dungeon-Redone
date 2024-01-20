package com.shatteredpixel.shatteredpixeldungeon.windows;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface WndUsesHeroSelector {
    void setContent();
    String getActionTitle();
    Runnable onConfirmAction();
    void onHeroSelect(int slot);

    Consumer<Integer> getOnHeroSelect();
}