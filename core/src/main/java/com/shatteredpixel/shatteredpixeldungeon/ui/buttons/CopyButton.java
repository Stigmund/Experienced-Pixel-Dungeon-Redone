package com.shatteredpixel.shatteredpixeldungeon.ui.buttons;

import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPaneClickable;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.function.BiConsumer;

public class CopyButton extends StartScene.SaveSlotButton implements ScrollPaneClickable {

    public final BiConsumer<CopyButton, Window> action;
    public final Window window;

    public CopyButton(BiConsumer<CopyButton, Window> action, Window _window) {

        this.action = action;
        this.window = _window;
    }

    @Override
    protected void onClick() {

        action.accept(this, window);
    }

    @Override
    protected void layout() {
        super.layout();

        // makes it so theres no click area, meaning the scroll pane can account for the buttons!
        hotArea.width = hotArea.height = 0;
    }

    @Override
    public boolean onClick(float x, float y) {

        if (!inside(x, y)) return false;
        onClick();
        return true;
    }

    public int getSlot() {

        return slot;
    }
}