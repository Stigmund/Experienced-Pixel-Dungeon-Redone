/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.OverloadBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.fishingrods.FishingRod;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.KingBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.RustyShield;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.TreasureBag;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.specialized.ToggleAction;

import java.util.Arrays;
import java.util.List;

public class TrinketBag extends Bag {

	{
		image = ItemSpriteSheet.TRINKET_POUCH;

		order = 6;
	}

	private static final List<Class<?>> ITEMS = Arrays.asList(
			TrinketCatalyst.class,
			ToggleAction.class,
			OverloadBeacon.class,
			CorpseDust.class,
			FishingRod.class,
			TreasureBag.class);

	@Override
	public boolean canHold( Item item ) {

		if (canHold(item, ITEMS)) {
			return super.canHold(item);
		}
		else {
			return false;
		}
	}

	public int capacity(){
		return 48;
	}

	@Override
	public long value() {
		return 40;
	}

}
