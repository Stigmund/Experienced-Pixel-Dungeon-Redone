package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.shatteredpixel.shatteredpixeldungeon.GamesInProgress.MAX_SLOTS;
import static com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene.align;
import static com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress.getHeroTitle;
import static com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress.statSlot;

public class WndCopyGame extends Window {

    private static final int WIDTH    = 120;
    private final int currentSlot;
    private final GamesInProgress.Info currentInfo;
    private Integer overwriteSlot = null;
    private GamesInProgress.Info overwriteInfo = null;
    private final List<Gizmo> content = new ArrayList<>();
    private final ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();
    private final int NEW_SLOT = games.size()+1;
    private final WndCopyGame owner;

    public WndCopyGame(final int _currentSlot) {

        owner = this;
        currentSlot = _currentSlot;
        currentInfo = GamesInProgress.check(currentSlot);

        setContent();
    }

    public void setOverwriteSlot(Integer _slot) {

        overwriteSlot = _slot;

        // if new slot re-selected
        if (_slot == NEW_SLOT) overwriteInfo = null;
        else overwriteInfo = GamesInProgress.check(_slot);

        clearContent();
        setContent();
    }

    private void clearContent() {

        int removed = 0;
        if (length == 0) return;
        for (int i=0; i < content.size(); i++) {
            Gizmo g = content.get( i );
            if (g != null) {
                g.parent = null;
                members.remove(g);
                removed++;
            }
        }
        content.clear();
        length -= removed;
    }

    public synchronized void addTo(Gizmo _g) {

        content.add(_g);
        super.add(_g);
    }

    private void setContent() {

        boolean copyDisabled = (overwriteSlot == null);
        if (overwriteSlot == null && games.size() < MAX_SLOTS) {

            overwriteSlot = NEW_SLOT;
            copyDisabled = false;
        }

        // Title

        IconTitle title = new IconTitle();
        title.icon( HeroSprite.avatar(currentInfo.heroClass, currentInfo.armorTier) );
        title.label("Copy Slot "+ currentSlot +"\n"+ getHeroTitle(currentInfo));
        title.color(Window.TITLE_COLOR);
        title.setRect( 0, 0, WIDTH, 0 );
        addTo(title);

        int GAP = 6;
        float pos = title.bottom() + GAP;

        boolean newSlot = (overwriteInfo == null);
        String slotText = newSlot
                        ? "Slot "+ overwriteSlot +" (new)"
                        : (overwriteSlot == null
                            ? "No Free Slots"
                            : "Slot "+ overwriteSlot);

        // Save Slot Title
        pos = statSlot(this, pos, "Overwrite Slot:", slotText);
        addMembersToContent(2);

        if (!newSlot) {

            // char info
            int elementsBefore = members.size();
            pos = WndGameInProgress.setContent(this, pos, overwriteInfo);
            addMembersToContent(members.size() - elementsBefore);
        }

        // Select save slot to overwrite

        RedButton selectOverwrite = new RedButton("Select a Slot to Overwrite") {
            @Override
            protected void onClick() {
                super.onClick();

                ShatteredPixelDungeon.scene().add( new WndSelectSlot(currentSlot, (overwriteInfo != null)));
            }
        };

        selectOverwrite.setRect(0, pos, WIDTH, 20);
        addTo( selectOverwrite );

        pos = selectOverwrite.bottom() + 2;

        // Copy button

        RedButton copySlot = new RedButton("Copy") {
            @Override
            protected void onClick() {

                if (overwriteInfo != null) {

                    ShatteredPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
                            "Are you sure you want to overwrite save slot "+ overwriteSlot +"?",
                            Messages.get(WndGameInProgress.class, "erase_warn_body"),
                            "Yes, overwrite slot "+ overwriteSlot,
                            "No, I want to reselect" ) {
                        @Override
                        protected void onSelect( int index ) {
                            if (index == 0) {

                                hide();
                                owner.hide();
                                Dungeon.copyGame(currentSlot, overwriteSlot);
                                ShatteredPixelDungeon.switchNoFade(StartScene.class);
                            }
                        }
                    } );
                }
                else {

                    hide();
                    Dungeon.copyGame(currentSlot, overwriteSlot);
                    ShatteredPixelDungeon.switchNoFade(StartScene.class);
                }
            }
        };
        copySlot.setRect(0, pos, ((float) WIDTH / 2) - 1, 20);
        if (copyDisabled) {

            copySlot.enable(false);
            copySlot.active = false;
        }
        addTo(copySlot);

        // Cancel Button

        RedButton cancel = new RedButton("Cancel") {
            @Override
            protected void onClick() {
                hide();
            }
        };
        cancel.setRect(((float) WIDTH / 2) +1, pos, ((float) WIDTH / 2) - 1, 20);
        addTo(cancel);

        resize(WIDTH, (int) cancel.bottom()+1);
    }

    // god damn it! lol
    private void addMembersToContent(int _numberFromEnd) {

        for (int i = members.size()-_numberFromEnd; i < members.size(); i++) {

            content.add(members.get(i));
        }
    }

    private interface ScrollBarClickable {
        boolean onClick(float x, float y);
    }

    private class CopyButton extends StartScene.SaveSlotButton implements ScrollBarClickable {

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
    };

    private class WndSelectSlot extends Window {

        private final ArrayList<CopyButton> boxes = new ArrayList<>();
        private static final int SLOT_WIDTH = 120;
        private static final int SLOT_HEIGHT = 30;

        public WndSelectSlot(final int _excludeSlot, boolean _hasSelected) {

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
            for (GamesInProgress.Info game : games) {

                if (game.slot != _excludeSlot) {

                    CopySlotButton existingGame = new CopySlotButton();
                    existingGame.set(game.slot);
                    existingGame.setRect((WIDTH - SLOT_WIDTH) / 2f, pos, SLOT_WIDTH, SLOT_HEIGHT);
                    align(existingGame);
                    content.add(existingGame);
                    boxes.add(existingGame);
                    pos += gap;
                }
            }

            if (games.size() < MAX_SLOTS && _hasSelected) {

                CopyNewButton copyToNew = new CopyNewButton();
                copyToNew.set(NEW_SLOT);
                copyToNew.setRect((WIDTH - SLOT_WIDTH) / 2f, pos, SLOT_WIDTH, SLOT_HEIGHT);
                content.add(copyToNew);
                boxes.add(copyToNew);
                pos += gap;
            }

            CopySlotCloseButton cancelButton = new CopySlotCloseButton();
            cancelButton.set();
            cancelButton.setRect((WIDTH - SLOT_WIDTH) / 2f, pos, SLOT_WIDTH, SLOT_HEIGHT);
            content.add(cancelButton);
            boxes.add(cancelButton);
            pos += gap;

            resize(WIDTH, (int) pos);
            pane.setRect(0, 0, WIDTH, pos);

            content.setSize(WIDTH, pos);
        }

        private class CopyNewButton extends CopyButton {

            @Override
            protected void onClick() {

                owner.setOverwriteSlot(NEW_SLOT);
                hide();
            }
        }

        private class CopySlotCloseButton extends CopyButton {

            @Override
            protected Chrome.Type getType() {

                return Chrome.Type.RED_GEM;
            }

            public void set() {

                name.text("Cancel");
                name.resetColor();

                layout();
            }

            @Override
            protected void onClick() {
                //overwriteSlot = null;
                hide();
            }
        }

        private class CopySlotButton extends CopyButton {

            @Override
            protected void onClick() {

                owner.setOverwriteSlot(slot);
                hide();
            }
        }
    }
}