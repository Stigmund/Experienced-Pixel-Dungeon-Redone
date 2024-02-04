package com.shatteredpixel.shatteredpixeldungeon.windows.specialized;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.CheeseCheest;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.KingBlade;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;

public class WndKingBlade extends WndUseItem implements WndCustom {

    public WndKingBlade(Window owner, Item item) {

        super(owner, item);

        float y = height;

        if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {

            CheckBox checkBox = new CheckBox(item.actionName(KingBlade.AC_TOGGLE, Dungeon.hero)) {

                @Override
                protected void onClick() {

                    executeItemAction(owner, item, KingBlade.AC_TOGGLE, true, true);
                    //addPerk(Dungeon.hero);
                }
            };
            checkBox.enable(true);
            checkBox.active = true;
            checkBox.centerText = true;
            checkBox.checked(SPDSettings.kingBlade());
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