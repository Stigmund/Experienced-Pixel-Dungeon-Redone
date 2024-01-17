package com.shatteredpixel.shatteredpixeldungeon.ui.buttons;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.function.BiConsumer;

public class CopySlotCloseButton extends CopyButton {

    public CopySlotCloseButton(BiConsumer<CopyButton, Window> action, Window _window) {
        super(action, _window);
    }

    @Override
    protected Chrome.Type getType() {

        return Chrome.Type.RED_GEM;
    }

    @Override
    public void set(int _slot) {

        this.slot = _slot;
        name.text("Cancel");
        name.resetColor();

        layout();
    }
}