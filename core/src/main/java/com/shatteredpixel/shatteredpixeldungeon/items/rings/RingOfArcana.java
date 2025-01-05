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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class RingOfArcana extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ARCANA;
	}

	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats",
					new DecimalFormat("#.##").format(100f * (1.20f + soloVisualBonus()*0.015 - 1f)));
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				info += "\n\n" + Messages.get(this, "combined_stats",
						Messages.decimalFormat("#.##", 100f * (1.20f + combinedBuffedBonus(Dungeon.hero)*0.015 - 1f)));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(20f));
		}
	}

	public String upgradeStat1(long level){
		if (cursed) level = Math.min(-1, level-3);
		return Messages.decimalFormat("#.##", 100f * (1.20f + level*0.015 - 1f)) + "%";
	}

	@Override
	protected RingBuff buff( ) {
		return new Arcana();
	}

	public static float enchantPowerMultiplier(Char target ){
		float multiplier = 1f;
		if (getBuffedBonus(target, Arcana.class) > 0) multiplier = 1.2f;
		if (getBuffedBonus(target, Arcana.class) > 1) multiplier += getBuffedBonus(target, Arcana.class)*0.015;
		return multiplier;
	}

	public class Arcana extends RingBuff {
	}

}
