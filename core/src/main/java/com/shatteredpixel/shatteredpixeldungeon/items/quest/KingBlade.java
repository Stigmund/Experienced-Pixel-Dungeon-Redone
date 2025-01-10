/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2019-2024 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2024 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.specialized.ToggleAction;
import com.shatteredpixel.shatteredpixeldungeon.windows.specialized.WndUseItemWithToggle;

public class KingBlade extends Item implements ToggleAction {
    {
        image = ItemSpriteSheet.KING_BLADE;

        itemWindow = WndUseItemWithToggle.class;
    }

    public static long checkAndProc(Char _attacker, Char _defender, Weapon _weapon, long _damage) {

        long damage = _damage;

        if (_attacker instanceof Hero) {

            for (Item item: ((Hero) _attacker).belongings.backpack) {

                if (item instanceof KingBlade && SPDSettings.kingBlade()) {

                    // get two unique procs!
                    Unstable proc1 = new Unstable();
                    Unstable proc2;
                    do {
                        proc2 = new Unstable();
                    } while (proc2.getProcClass().equals(proc1.getProcClass()));

                    damage = proc1.proc(_weapon, _attacker, _defender, damage);
                    damage = proc2.proc(_weapon, _attacker, _defender, damage);
                    logProcs(proc1.getProcName(), proc2.getProcName());
                }
            }
        }

        return damage;
    }

    private static void logProcs(String _procName1, String _procName2) {

        GLog.i(String.format("%s%s",
                GLog.POSITIVE,
                Messages.get(KingBlade.class, "procs", _procName1, _procName2)));
    }

    public boolean doToggleAction() {

        boolean state = !SPDSettings.kingBlade();
        SPDSettings.kingBlade(state);

        return state;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public long value() {
        return quantity * 100;
    }
}