/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2020 Trashbox Bobylev
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

package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.specialized.WndCheeseCheest;

public class CheeseCheest extends Bag {

	/*
		Deliberately not added to the actions list, instead an itemWindow is set
		this is because it's easier/more flexible to rearrange the window
		instead of coming up with a mechanism to define different action buttons/widths etc.
	 */
	public static final String AC_GLITCH = "GLITCH";

	{
		image = ItemSpriteSheet.CHEEST;

		itemWindow = WndCheeseCheest.class;
	}

	@Override
	public boolean canHold( Item item ) {
		if (item instanceof Artifact || item instanceof Ring){
			return super.canHold(item);
		} else {
			return false;
		}
	}

	@Override
	public boolean collect(Bag container) {
		if (super.collect( container )) {

			if (!Dungeon.LimitedDrops.CHEESY_CHEEST.dropped()){
				Dungeon.LimitedDrops.CHEESY_CHEEST.drop();
			}

			return true;
		} else {
			return false;
		}
	}

	/*public int capacity(){
		return 36;
	}*/

	@Override
	public int value() {
		return 650 * (Dungeon.cycle + 1);
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_GLITCH )) {

			boolean state = !SPDSettings.cheeseChestGlitch();
			SPDSettings.cheeseChestGlitch(state);

			GLog.i(String.format("%s%s",
								 (state ? GLog.POSITIVE : GLog.NEGATIVE),
								 Messages.get(this, "toggle_glitch", (SPDSettings.cheeseChestGlitch() ? "On" : "Off"))));
		}
	}
}
