package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon.LimitedDrops;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.watabou.noosa.Gizmo;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Main enum for storing staring equipment options.
 * Currently has:
 * - Item item - An instance of the item.
 * - List<HeroClass> belongsTo - list of classes this item should always have enabled.
 * - LimitedDrops limitedDrop - null or the LimitedDrops enum for the item.
 */
public enum HeroStartingEquipment {

	CHEEST_BAG(new CheeseCheest(), null, hero -> {

		new CheeseCheest().collect();
		LimitedDrops.CHEESY_CHEEST.drop();
	}),
	POTION_BAG(new PotionBandolier(), Collections.singletonList(HeroClass.RAT_KING), hero -> {

		new PotionBandolier().collect();
		LimitedDrops.POTION_BANDOLIER.drop();
	}),
	SCROLL_BAG(new ScrollHolder(), Collections.singletonList(HeroClass.RAT_KING), hero -> {

		new ScrollHolder().collect();
		LimitedDrops.SCROLL_HOLDER.drop();
	}),
	MAGIC_BAG(new MagicalHolster(), Collections.singletonList(HeroClass.RAT_KING), hero -> {

		new MagicalHolster().collect();
		LimitedDrops.MAGICAL_HOLSTER.drop();
	}),
	FOOD_BAG(new FoodBag(), Collections.singletonList(HeroClass.RAT_KING), hero -> {

		new FoodBag().collect();
		LimitedDrops.FOOD_BAG.drop();
	}),
	TRINKET_BAG(new TrinketBag(), Collections.singletonList(HeroClass.RAT_KING), hero -> {

		new TrinketBag().collect();
		LimitedDrops.TRINKET_BAG.drop();
	}),
	BROKEN_SEAL(new BrokenSeal(), Arrays.asList(HeroClass.RAT_KING, HeroClass.WARRIOR), hero -> {

		if (hero.belongings.armor != null) {

			hero.belongings.armor.affixSeal(new BrokenSeal());
		}
	}),
	SPIRIT_BOW(new SpiritBow(), Arrays.asList(HeroClass.RAT_KING, HeroClass.HUNTRESS), hero -> {

		new SpiritBow().identify().collect();
	}),
	CLOAK_OF_SHADOWS(new CloakOfShadows(), Arrays.asList(HeroClass.RAT_KING, HeroClass.ROGUE), hero -> {

		new CloakOfShadows().identify().collect();
	}),
	MAGE_STAFF(new MagesStaff(), Arrays.asList(HeroClass.RAT_KING, HeroClass.MAGE), hero -> {

		new MagesStaff(new WandOfMagicMissile()).identify().collect();
	});

	private final Item item;
	private final List<HeroClass> belongsTo;
	private final Consumer<Hero> addItem;

	private HeroStartingEquipment(Item _item, List<HeroClass> _belongsTo, Consumer<Hero> _callback) {

		this.item = _item;
		this.belongsTo = _belongsTo;
		this.addItem = _callback;
	}

	private static Map<HeroStartingEquipment, Boolean> STARTING_EQUIPMENT;

	public static Map<HeroStartingEquipment, Boolean> getStartingEquipment() {

		if (STARTING_EQUIPMENT == null || STARTING_EQUIPMENT.isEmpty()) {

			STARTING_EQUIPMENT = new LinkedHashMap<HeroStartingEquipment, Boolean>() {{

				for (HeroStartingEquipment se : HeroStartingEquipment.values()) {

					put(se, false);
				}
			}};
		}

		return STARTING_EQUIPMENT;
	}

	public static void setAll(boolean _state, List<CheckBox> _checkboxes) {

		_checkboxes.stream().filter(CheckBox::isActive).forEach(g -> ((CheckBox) g).checked(_state));

		Map<HeroStartingEquipment, Boolean> startingEquipment = HeroStartingEquipment.getStartingEquipment();
		startingEquipment.forEach((key, value) -> startingEquipment.put(key, _state));
	}

	public void addItem(Hero _hero) {

		addItem.accept(_hero);
	}

	public Item getItem() {

		return item;
	}

	public boolean doesItemBelongToHeroClass(HeroClass _heroClass) {

		return belongsTo != null && belongsTo.contains(_heroClass);
	}
}