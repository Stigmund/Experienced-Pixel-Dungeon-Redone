package com.shatteredpixel.shatteredpixeldungeon.ui.buttons;

import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPaneClickable;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndCopyGame;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndCopySelectSlot;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUsesHeroSelector;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CopyButton extends StartScene.SaveSlotButton implements ScrollPaneClickable {

    public Integer slot = null;

    public CopyButton() {

    }

    @Override
    protected void onClick() {

        //action.accept(slot);
       // WndCopySelectSlot.opener.onHeroSelect(slot);
        //WndCopySelectSlot.opener = null;
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

    public void set(int _slot) {
        slot = _slot;
        super.set(slot);
    }

    public int getSlot() {

        return slot;
    }
}