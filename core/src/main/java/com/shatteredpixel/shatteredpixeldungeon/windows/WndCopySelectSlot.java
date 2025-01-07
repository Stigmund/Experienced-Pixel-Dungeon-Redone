package com.shatteredpixel.shatteredpixeldungeon.windows;

import static com.shatteredpixel.shatteredpixeldungeon.GamesInProgress.MAX_SLOTS;
import static com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene.align;

import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.buttons.CopyButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.buttons.CopySlotCloseButton;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class WndCopySelectSlot extends Window {

    protected final int NEW_SLOT = GamesInProgress.firstEmpty();
    private final List<CopyButton> boxes = new ArrayList<>();
    private static final int SLOT_WIDTH = 120;
    private static final int SLOT_HEIGHT = 30;

    public WndCopySelectSlot(BiConsumer<CopyButton, Window> _onHeroSelect, final int _currentSlot, final Integer _currentlySelectedSlot, boolean _hasSelected) {

        float gap = SLOT_HEIGHT + (10 - MAX_SLOTS);
        float pos = (10 - MAX_SLOTS);
        ScrollPane pane = new ScrollPane(new Component()) {
            @Override
            public void onClick(float x, float y) {

                for (int i = 0; i < boxes.size(); i++) {

                    if (boxes.get(i).onClick(x, y)) break;
                }
            }
        };
        add(pane);
        Component content = pane.content();

        int WIDTH = Math.min(138, (int) (PixelScene.uiCamera.width * 0.8));
        ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();
        games.sort(Comparator.comparingInt(i -> i.slot));

        // Existing Slots
        for (GamesInProgress.Info game : games) {

            if (game.slot != _currentSlot && (_currentlySelectedSlot == null || _currentlySelectedSlot != game.slot)) {

                CopyButton existingGame = new CopyButton(_onHeroSelect, this);
                existingGame.set(game.slot);
                existingGame.setRect((WIDTH - SLOT_WIDTH) / 2f, pos, SLOT_WIDTH, SLOT_HEIGHT);
                align(existingGame);
                content.add(existingGame);
                boxes.add(existingGame);
                pos += gap;
            }
        }

        // New Slot
        if (games.size() < MAX_SLOTS && _hasSelected) {

            CopyButton copyToNew = new CopyButton(_onHeroSelect, this);
            copyToNew.set(NEW_SLOT);
            copyToNew.setRect((WIDTH - SLOT_WIDTH) / 2f, pos, SLOT_WIDTH, SLOT_HEIGHT);
            content.add(copyToNew);
            boxes.add(copyToNew);
            pos += gap;
        }

        // Cancel
        CopySlotCloseButton cancelButton = new CopySlotCloseButton((copyButton, window) -> window.hide(), this);
        cancelButton.set();
        cancelButton.setRect((WIDTH - SLOT_WIDTH) / 2f, pos, SLOT_WIDTH, SLOT_HEIGHT);
        content.add(cancelButton);
        boxes.add(cancelButton);
        pos += gap;

        float contentSize = pos;
        int maxheight =  (int) (PixelScene.uiCamera.height * 0.8);
        if (pos > maxheight) pos = maxheight;

        resize(WIDTH, (int) pos);
        pane.setRect(0, 0, WIDTH, pos);

        content.setSize(WIDTH, contentSize);
    }
}