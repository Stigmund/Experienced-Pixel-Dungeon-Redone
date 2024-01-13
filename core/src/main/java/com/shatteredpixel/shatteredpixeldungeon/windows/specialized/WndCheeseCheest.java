package com.shatteredpixel.shatteredpixeldungeon.windows.specialized;

import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Perks.addPerk;
import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Perks.earnPerk;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.CheeseCheest;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;

public class WndCheeseCheest extends WndUseItem implements WndCustom {

    public WndCheeseCheest(Window owner, Item item) {

        super(owner, item);

        float y = height;

        if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {

            CheckBox checkBox = new CheckBox(item.actionName(CheeseCheest.AC_GLITCH, Dungeon.hero)) {

                @Override
                protected void onClick() {

                    executeItemAction(owner, item, CheeseCheest.AC_GLITCH, true, false);
                    //addPerk(Dungeon.hero);
                }
            };
            checkBox.enable(true);
            checkBox.active = true;
            checkBox.centerText = true;
            checkBox.checked(SPDSettings.cheeseChestGlitch());
            checkBox.textColor(TITLE_COLOR);

            y += GAP;
            checkBox.setRect(0, y, width, BUTTON_HEIGHT );
            y += BUTTON_HEIGHT;

            add( checkBox );

            //y = layoutButtons(buttons, width, y);
        }

        resize(width, ((int) y));
    }
}