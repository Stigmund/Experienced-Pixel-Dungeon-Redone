package com.shatteredpixel.shatteredpixeldungeon.ui.buttons;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUsesHeroSelector;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CopySlotCloseButton extends CopyButton {

    @Override
    protected Chrome.Type getType() {

        return Chrome.Type.RED_GEM;
    }

    public void set() {

        name.text("Cancel");
        name.resetColor();

        layout();
    }
}