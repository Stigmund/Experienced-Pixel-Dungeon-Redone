package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.buttons.CopyButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import static com.shatteredpixel.shatteredpixeldungeon.GamesInProgress.MAX_SLOTS;
import static com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene.align;
import static com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress.getHeroTitle;
import static com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress.statSlot;

public class WndCopyGame extends Window implements WndUsesHeroSelector {

    protected static final int WIDTH    = 120;
    protected int currentSlot;
    protected GamesInProgress.Info currentInfo;
    protected Integer overwriteSlot = null;
    protected GamesInProgress.Info overwriteInfo = null;
    //private final List<Gizmo> content = new ArrayList<>();
    protected final ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();
    protected final int NEW_SLOT = GamesInProgress.firstEmpty();
    private boolean fromRunningGame;
    protected AnimatedToast toast;
    private ArrayList<AnimatedToast> toDestroy = new ArrayList<>();
    private final Runnable action = this::copyGame;
    private final BiConsumer<CopyButton, Window> onHeroSelect = (slotButton, window) -> {

        window.hide();
        ShatteredPixelDungeon.scene().addToFront(new WndCopyGame(currentSlot, slotButton.getSlot(), fromRunningGame));
    };

    public WndCopyGame() {

    }

    public WndCopyGame(int _currentSlot, Integer _currentlySelectedSlot, boolean _fromRunningGame) {

        currentSlot = _currentSlot;
        overwriteSlot = _currentlySelectedSlot != null && _currentlySelectedSlot == 0 ? null : _currentlySelectedSlot;
        if (overwriteSlot == null || overwriteSlot == NEW_SLOT) overwriteInfo = null;
        else overwriteInfo = GamesInProgress.check(overwriteSlot);
        currentInfo = GamesInProgress.check(currentSlot);
        fromRunningGame = _fromRunningGame;

        setContent();
    }

    protected void setContent() {

        boolean copyDisabled = (overwriteSlot == null);
        if (overwriteSlot == null && games.size() < MAX_SLOTS) {

            overwriteSlot = NEW_SLOT;
            copyDisabled = false;
        }

        // Title

        int GAP = 6;
        float pos = setTitle() + GAP;

        boolean newSlot = (overwriteInfo == null);
        String slotText = newSlot
                        ? "Slot "+ overwriteSlot +" (new)"
                        : (overwriteSlot == null
                            ? "No Free Slots"
                            : "Slot "+ overwriteSlot);

        // Save Slot Title
        pos = statSlot(this, pos, "Overwrite Slot:", slotText);

        if (!newSlot) {

            // char info
            pos = WndGameInProgress.setContent(this, pos, overwriteInfo);
        }

        // Select save slot to overwrite
        WndCopyGame owner = this;
        RedButton selectOverwrite = new RedButton("Select a Slot to Overwrite") {
            @Override
            protected void onClick() {

                hide();
                ShatteredPixelDungeon.scene().add( new WndCopySelectSlot(getOnHeroSelect(), currentSlot, overwriteSlot, (overwriteInfo != null)));
            }
        };

        selectOverwrite.setRect(0, pos, WIDTH, 20);
        add( selectOverwrite );

        pos = selectOverwrite.bottom() + 2;

        // Copy button
        RedButton actionButton = new RedButton(getActionTitle()) {
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
                            super.onSelect(index);
                            if (index == 0) {

                                hide();
                                getOnConfirmAction().run();
                            }
                        }
                    });
                }
                else {

                    hide();
                    getOnConfirmAction().run();
                }
            }
        };
        actionButton.setRect(0, pos, ((float) WIDTH / 2) - 1, 20);
        if (copyDisabled) {

            actionButton.enable(false);
            actionButton.active = false;
        }
        add(actionButton);

        // Cancel Button

        RedButton cancel = new RedButton("Cancel") {
            @Override
            public void onClick() {
                hide();
            }
        };
        cancel.setRect(((float) WIDTH / 2) +1, pos, ((float) WIDTH / 2) - 1, 20);
        add(cancel);

        resize(WIDTH, (int) cancel.bottom()+1);
    }

    protected float setTitle() {

        IconTitle title = new IconTitle();
        title.icon( HeroSprite.avatar(currentInfo.heroClass, currentInfo.armorTier) );
        title.label(String.format("Copy Slot %s\n%s", currentSlot, getHeroTitle(currentInfo)));
        title.color(Window.TITLE_COLOR);
        title.setRect( 0, 0, WIDTH, 0 );
        add(title);
        return title.bottom();
    }

    protected void setActionButton(float pos, boolean _disabled) {

        setActionButton(pos, _disabled, "Copy", this::copyGame);
    }

    protected void setActionButton(float pos, boolean _disabled, String _text, Runnable _action) {

        RedButton actionButton = new RedButton(_text) {
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
                            super.onSelect(index);
                            if (index == 0) {

                                hide();
                                getOnConfirmAction().run();
                            }
                        }
                    });
                }
                else {

                    hide();
                    getOnConfirmAction().run();
                }
            }
        };
        actionButton.setRect(0, pos, ((float) WIDTH / 2) - 1, 20);
        if (_disabled) {

            actionButton.enable(false);
            actionButton.active = false;
        }
        add(actionButton);
    }

    private void copyGame() {

        try {

            Dungeon.saveAll();

            Dungeon.copyGame(currentSlot, overwriteSlot);
            if (!fromRunningGame) {

                ShatteredPixelDungeon.switchNoFade(StartScene.class);
            }
            AnimatedToast.toast(String.format("Copied Slot %d to Slot %d!", currentSlot, overwriteSlot));
        }
        catch (IOException e) {

            ShatteredPixelDungeon.reportException(e);
            AnimatedToast.toast("Failed to Copy!");
        }
    }

    public String getActionTitle() {

        return "Copy";
    }

    public Runnable getOnConfirmAction() {

        return action;
    }

    public BiConsumer<CopyButton, Window> getOnHeroSelect() {

        return onHeroSelect;
    }
}