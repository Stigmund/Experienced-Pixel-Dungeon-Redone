package com.shatteredpixel.shatteredpixeldungeon.windows.specialized;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.CheeseCheest;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.WndUtils;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.Game;

import java.util.function.Consumer;

/**
 * Same as WndUseItem but with one additional "toggle" button.
 * Items that use this must:
 * - implement ToggleAction.class
 * - set itemWindow = WndUseItemWithToggle.class;
 */
public class WndUseItemWithToggle extends WndUseItem {

    /**
     * Constructor MUST be "ClassName(Window owner, Item item)" to work via reflection.
     * @param owner
     * @param item
     */
    public WndUseItemWithToggle(Window owner, Item item) {

        super(owner, item);

        float y = height;

        if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {

            CheckBox checkBox = new CheckBox(getItem(item).getToggleButtonText()) {

                @Override
                protected void onClick() {

                    super.checked(getItem(item).toggleAction());
                    reloadInventory(item);
                }
            };
            checkBox.enable(true);
            checkBox.active = true;
            checkBox.centerText = true;
            checkBox.checked(getItem(item).state());
            checkBox.textColor(TITLE_COLOR);

            y += GAP;
            checkBox.setRect(0, y, width, BUTTON_HEIGHT );
            y += BUTTON_HEIGHT;

            add( checkBox );
        }

        resize(width, ((int) y));
    }

    private ToggleAction getItem(Item item) {

        return (ToggleAction) item;
    }

    private void reloadInventory(Item item) {

        Bag lb = WndBag.lastBag;

        // hide both windows
        Game.scene().getMembers().stream().filter(m -> m instanceof WndBag).findFirst().ifPresent(g -> ((Window) g).hide());
        Game.scene().getMembers().stream().filter(m -> m instanceof WndUseItemWithToggle).findFirst().ifPresent(g -> ((Window) g).hide());

        // re-show inventory (to redraw - not sure how else to do this)
        WndBag bag = new WndBag(lb);
        GameScene.show(bag);

        // re-show toggleable item window
        Game.scene().addToFront(WndUtils.getItemWindow(bag, item));
    }
}