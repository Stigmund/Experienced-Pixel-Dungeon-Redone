package com.shatteredpixel.shatteredpixeldungeon.windows;

import static com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress.getHeroTitle;

import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.buttons.CopyButton;

import java.util.function.BiConsumer;

public class WndImportGame extends WndCopyGame {

    private WndImportSelectGame.GameInfo gameInfo;
    private final Runnable action = this::importGame;

    private final BiConsumer<CopyButton, Window> onHeroSelect = (slotButton, window) -> {

        window.hide();
        ShatteredPixelDungeon.scene().addToFront(new WndImportGame(gameInfo, slotButton.getSlot()));
    };

    public WndImportGame(WndImportSelectGame.GameInfo _info, Integer _currentlySelectedSlot) {

        gameInfo = _info;
        currentSlot = 0;
        currentInfo = gameInfo.info;
        overwriteSlot = _currentlySelectedSlot != null && _currentlySelectedSlot == 0 ? null : _currentlySelectedSlot;
        if (overwriteSlot == null || overwriteSlot == NEW_SLOT) overwriteInfo = null;
        else overwriteInfo = GamesInProgress.check(overwriteSlot);

        setContent();
    }

    @Override
    protected float setTitle() {

        IconTitle title = new IconTitle();
        title.icon( HeroSprite.avatar(currentInfo.heroClass, currentInfo.armorTier) );
        title.label(String.format("Import %s", getHeroTitle(currentInfo)));
        title.color(Window.TITLE_COLOR);
        title.setRect( 0, 0, WIDTH, 0 );
        add(title);

        RenderedTextBlock line2 = PixelScene.renderTextBlock(6);
        line2.text(gameInfo.formattedTitleLine);
        line2.hardlight(Window.WHITE);
        line2.setPos( title.tfLabel.left(), title.tfLabel.bottom() + 2);
        add(line2);

        return line2.bottom();
    }

    @Override
    protected void setActionButton(float pos, boolean _disabled) {

        setActionButton(pos, _disabled, "Import", this::importGame);
    }

    private void importGame() {

    }

    public String getActionTitle() {

        return "Import";
    }

    public Runnable getOnConfirmAction() {

        return action;
    }

    public BiConsumer<CopyButton, Window> getOnHeroSelect() {

        return onHeroSelect;
    }
}