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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Dressable;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ArmoredStatue extends Statue implements Dressable {

	{
		spriteClass = StatueSprite.class;
	}

	protected Armor armor;

	public ArmoredStatue(){
		super();

		//double HP
		HP = HT = 30 + Dungeon.escalatingDepth() * 10;
	}

	@Override
	public void createWeapon(boolean useDecks) {
		super.createWeapon(useDecks);

		armor = Generator.randomArmor();
		armor.cursed = false;
		armor.inscribe(Armor.Glyph.random());
	}

	private static final String ARMOR	= "armor";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ARMOR, armor );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		armor = (Armor)bundle.get( ARMOR );
	}

	@Override
	public long drRoll() {
		return (int) (super.drRoll() + Random.NormalLongRange( armor.DRMin(), armor.DRMax()));
	}

	//used in some glyph calculations
	public Armor armor(){
		return armor;
	}

	@Override
	public boolean isImmune(Class effect) {
		if (effect == Burning.class
				&& armor != null
				&& armor.hasGlyph(Brimstone.class, this)){
			return true;
		}
		return super.isImmune(effect);
	}

	@Override
	public long defenseProc(Char enemy, long damage) {
		damage = (int) armor.proc(enemy, this, damage);
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(long dmg, Object src) {
		//TODO improve this when I have proper damage source logic
		if (armor != null && armor.hasGlyph(AntiMagic.class, this)
				&& AntiMagic.RESISTS.contains(src.getClass())){
			dmg -= AntiMagic.drRoll(this, armor.buffedLvl());
			dmg = Math.max(dmg, 0);
		}

		super.damage( dmg, src );

		//for the rose status indicator
		Item.updateQuickslot();
	}

	@Override
	public CharSprite sprite() {
		CharSprite sprite = super.sprite();
		if (armor != null) {
			((StatueSprite) sprite).setArmor(armor.visibleTier);
		} else {
			((StatueSprite) sprite).setArmor(3);
		}
		return sprite;
	}

	@Override
	public float speed() {
		return armor.speedFactor(this, super.speed());
	}

	@Override
	public float stealth() {
		return armor.stealthFactor(this, super.stealth());
	}

	@Override
	public int defenseSkill(Char enemy) {
		return Math.round(armor.evasionFactor(this, super.defenseSkill(enemy)));
	}

	@Override
	public void die( Object cause ) {
		armor.identify(false);
		Dungeon.level.drop( armor, pos ).sprite.drop();
		super.die( cause );
	}

	@Override
	public String description() {
		String desc = Messages.get(this, "desc");
		if (weapon != null && armor != null){
			desc += "\n\n" + Messages.get(this, "desc_arm_wep", weapon.name(), armor.name());
		}
		return desc;
	}
}