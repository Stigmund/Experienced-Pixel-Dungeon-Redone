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

package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import static com.shatteredpixel.shatteredpixeldungeon.messages.Messages.NO_TEXT_FOUND;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalWisp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Dressable;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ArcaneBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.HolyDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

import java.util.HashSet;

public class AntiMagic extends Armor.Glyph {

	private static ItemSprite.Glowing TEAL = new ItemSprite.Glowing( 0x88EEFF );
	
	public static final HashSet<Class> RESISTS = new HashSet<>();
	static {
		RESISTS.add( MagicalSleep.class );
		RESISTS.add( Charm.class );
		RESISTS.add( Weakness.class );
		RESISTS.add( Vulnerable.class );
		RESISTS.add( Hex.class );
		RESISTS.add( Degrade.class );
		
		RESISTS.add( DisintegrationTrap.class );
		RESISTS.add( GrimTrap.class );

		RESISTS.add( ArcaneBomb.class );
		RESISTS.add( HolyBomb.HolyDamage.class );
		RESISTS.add( ScrollOfRetribution.class );
		RESISTS.add( ScrollOfPsionicBlast.class );
		RESISTS.add( ScrollOfTeleportation.class );
		RESISTS.add( HolyDart.class );

		RESISTS.add( ElementalBlast.class );
		RESISTS.add( CursedWand.class );
		RESISTS.add( WandOfBlastWave.class );
		RESISTS.add( WandOfDisintegration.class );
		RESISTS.add( WandOfFireblast.class );
		RESISTS.add( WandOfFrost.class );
		RESISTS.add( WandOfLightning.class );
		RESISTS.add( WandOfLivingEarth.class );
		RESISTS.add( WandOfMagicMissile.class );
		RESISTS.add( WandOfPrismaticLight.class );
		RESISTS.add( WandOfTransfusion.class );
		RESISTS.add( WandOfWarding.Ward.class );

		RESISTS.add( ElementalStrike.class );
		RESISTS.add( Blazing.class );
		RESISTS.add( WandOfFireblast.FireBlastOnHit.class );
		RESISTS.add( Shocking.class );
		RESISTS.add( WandOfLightning.LightningOnHit.class );
		RESISTS.add( Grim.class );

		RESISTS.add( WarpBeacon.class );

		RESISTS.add( DM100.LightningBolt.class );
		RESISTS.add( Shaman.EarthenBolt.class );
		RESISTS.add( CrystalWisp.LightBeam.class );
		RESISTS.add( Warlock.DarkBolt.class );
		RESISTS.add( Eye.DeathGaze.class );
		RESISTS.add( YogFist.BrightFist.LightBeam.class );
		RESISTS.add( YogFist.DarkFist.DarkBolt.class );
	}

	public static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();
	static {
		IMMUNITIES.add( MagicalSleep.class );
		IMMUNITIES.add( Charm.class );
		IMMUNITIES.add( Weakness.class );
		IMMUNITIES.add( Vulnerable.class );
		IMMUNITIES.add( Hex.class );
		IMMUNITIES.add( Degrade.class );

		IMMUNITIES.add( Blindness.class );
		IMMUNITIES.add( Corruption.class );
		IMMUNITIES.add( Paralysis.class );
		IMMUNITIES.add( CorrosiveGas.class );
		IMMUNITIES.add( Corrosion.class );
		IMMUNITIES.add( Poison.class );
		IMMUNITIES.add( Ooze.class );
		IMMUNITIES.add( ToxicGas.class );
	}
	
	@Override
	public long proc(Armor armor, Char attacker, Char defender, long damage) {
		//no proc effect, see:
		// Hero.damage
		// GhostHero.damage
		// Shadowclone.damage
		// ArmoredStatue.damage
		// PrismaticImage.damage
		return damage;
	}
	
	public static long drRoll(Char ch, long level ){
		return Dungeon.NormalLongRange(
				Math.round(level * genericProcChanceMultiplier(ch)),
				Math.round((3 + (level*1.5f)) * genericProcChanceMultiplier(ch)));
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return TEAL;
	}

	public static boolean resistMagic(Char _char, Class<?> _magic) {

		boolean resisted = false;

		if (_char instanceof Dressable) {

			Armor armor = ((Dressable) _char).armor();
			if (armor != null
					&& armor.hasGlyph(AntiMagic.class, _char)
					&& AntiMagic.IMMUNITIES.contains(_magic)) {

				Resisted txt = getFlavourText(_magic);
				_char.sprite.showStatusWithIcon(txt.colour(), txt.name(), txt.icon());

				return true;
			}
		}

		return resisted;
	}
	
	private static Resisted getFlavourText(Class<?> _magic) {

		String name = Messages.get(_magic, "resist_name");
		if (name.contains(NO_TEXT_FOUND)) name = _magic.getSimpleName() +"?";
		name = Messages.get(Buff.class, "antimagic_resist", name);
		
		if (_magic.isAssignableFrom(MagicalSleep.class)) {

			return new Resisted(name, FloatingText.MAGIC_SLEEP, 0xFFFFF);
		}
		else if (_magic.isAssignableFrom(Charm.class)) {

			return new Resisted(name, FloatingText.CHARM, 0xFF3355);
		}
		else if (_magic.isAssignableFrom(Weakness.class)) {

			return new Resisted(name, FloatingText.WEAKNESS, 0x5E1A80);
		}
		else if (_magic.isAssignableFrom(Vulnerable.class)) {

			return new Resisted(name, FloatingText.VULNERABLE, 0x5E1A80);
		}
		else if (_magic.isAssignableFrom(Hex.class)) {

			return new Resisted(name, FloatingText.HEX, 0x5E1A80);
		}
		else if (_magic.isAssignableFrom(Degrade.class)) {

			return new Resisted(name, FloatingText.DEGRADE, 0x5E1A80);
		}
		else if (_magic.isAssignableFrom(Blindness.class)) {

			return new Resisted(name, FloatingText.BLINDNESS, 0x47649D);
		}
		else if (_magic.isAssignableFrom(Corruption.class)) {

			return new Resisted(name, FloatingText.CORRUPTION, 0xFFFFF);
		}
		else if (_magic.isAssignableFrom(Paralysis.class)) {

			return new Resisted(name, FloatingText.PARALYSIS, 0xFFDB65);
		}
		else if (_magic.isAssignableFrom(CorrosiveGas.class) || _magic.isAssignableFrom(Corrosion.class)) {

			return new Resisted(name, FloatingText.CORROSION, 0xFF8800);
		}
		else if (_magic.isAssignableFrom(ToxicGas.class)) {

			return new Resisted(name, FloatingText.TOXIC, 0x50FF60);
		}
		else if (_magic.isAssignableFrom(Poison.class)) {

			return new Resisted(name, FloatingText.POISON, 0x993399);
		}
		else if (_magic.isAssignableFrom(Ooze.class)) {

			return new Resisted(name, FloatingText.OOZE, 0x008056);
		}

		return new Resisted(name, FloatingText.SHIELDING, CharSprite.DEFAULT);
	}

	public static class Resisted {

		private final String name;
		private final int icon;
		private final int colour;
		
		public Resisted(String name, int icon, int colour) {

			this.name = name;
			this.icon = icon;
			this.colour = colour;
		}

		public String name() {
			return this.name;
		}
		public int icon() {
			return this.icon;
		}
		
		public int colour() {
			return this.colour;
		}
	}
}