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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Perks;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Cheese;
import com.shatteredpixel.shatteredpixeldungeon.items.food.CheeseChunk;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatKingSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.Holiday;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;

public class RatKing extends NPC {

    public int counter = 0;
	public int level = 1;

	{
		spriteClass = RatKingSprite.class;
		
		state = SLEEPING;

		HP = HT = 2000;
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}
	
	@Override
	public float speed() {
		return 2f;
	}
	
	@Override
	protected Char chooseEnemy() {
		return null;
	}

	@Override
	public void damage( long dmg, Object src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
	    if (buff instanceof Barter || buff instanceof MirrorImage.MirrorInvis) return super.add(buff);
		return false;
	}
	
	@Override
	public boolean reset() {
		return true;
	}

	//***This functionality is for when rat king may be summoned by a distortion trap

	@Override
	protected void onAdd() {
		super.onAdd();
		if (firstAdded && Dungeon.depth != 5){
			yell(Messages.get(this, "confused"));
		}
	}

	@Override
	public Notes.Landmark landmark() {
		return Dungeon.depth == 5 ? Notes.Landmark.RAT_KING : null;
	}

	@Override
	protected boolean act() {
		if (Dungeon.depth < 5){
			if (pos == Dungeon.level.exit()){
				destroy();
				Dungeon.level.drop(new CheeseChunk(), pos).sprite.drop();
				sprite.killAndErase();
				return super.act();
			} else {
				target = Dungeon.level.exit();
			}
		} else if (Dungeon.depth > 5){
			if (pos == Dungeon.level.entrance()){
				destroy();
				Dungeon.level.drop(new CheeseChunk(), pos).sprite.drop();
				sprite.killAndErase();
				return super.act();
			} else {
				target = Dungeon.level.entrance();
			}
		}
        Heap heap = Dungeon.level.heaps.get(pos );
        Barter barter = Buff.affect(this, Barter.class);
		if (heap != null && heap.peek().throwPos(this, Dungeon.hero.pos) == Dungeon.hero.pos){
		    Item item = heap.pickUp();
		    barter.stick(item);
            CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
            Sample.INSTANCE.play( Assets.Sounds.PUFF );
        }
		if (!barter.items.isEmpty()){
		    if (!Dungeon.hero.isPerkActive(Perks.Perk.BETTER_BARTERING) || Random.Int(3) != 0)
		        barter.items.remove(barter.items.size() - 1);
			for (int i = 0; i < level*(Dungeon.cycle+1); i++) {
				Item item;
				do {
					item = Generator.random();
				} while (item instanceof Gold);
				if (++counter >= 30*level) {
					counter = 0;
					level = Math.min(level+1, 15);
					item = new Cheese();
				}
				if (item.throwPos(this, Dungeon.hero.pos) == Dungeon.hero.pos)
					item.cast(this, Dungeon.hero.pos);
				Bestiary.countEncounter(getClass());
			}

            spend(2f);
        }
		return super.act();
	}

	//***

	@Override
	public boolean interact(Char c) {
		sprite.turnTo( pos, c.pos );

		if (c != Dungeon.hero){
			return super.interact(c);
		}

		if (state == SLEEPING) {
			notice();
			yell( Messages.get(this, "not_sleeping") );
			state = WANDERING;
		} else {
			yell( Messages.get(this, "what_is_it") );
		}

		KingsCrown crown = Dungeon.hero.belongings.getItem(KingsCrown.class);
		if (state == SLEEPING) {
			notice();
			yell( Messages.get(this, "not_sleeping") );
			state = WANDERING;
		} else if (crown != null){
			if (Dungeon.hero.belongings.armor() == null){
				yell( Messages.get(RatKing.class, "crown_clothes") );
			} else {
				Badges.validateRatmogrify();
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndOptions(
								sprite(),
								Messages.titleCase(name()),
								Messages.get(RatKing.class, "crown_desc"),
								Messages.get(RatKing.class, "crown_yes"),
								Messages.get(RatKing.class, "crown_info"),
								Messages.get(RatKing.class, "crown_no")
						){
							@Override
							protected void onSelect(int index) {
								if (index == 0){
									crown.upgradeArmor(Dungeon.hero, Dungeon.hero.belongings.armor(), new Ratmogrify());
									((RatKingSprite)sprite).resetAnims();
									yell(Messages.get(RatKing.class, "crown_thankyou"));
								} else if (index == 1) {
									GameScene.show(new WndInfoArmorAbility(Dungeon.hero.heroClass, new Ratmogrify()));
								} else {
									yell(Messages.get(RatKing.class, "crown_fine"));
								}
							}
						});
					}
				});
			}
		} else if (Dungeon.hero.armorAbility instanceof Ratmogrify) {
			yell( Messages.get(RatKing.class, "crown_after") );
		} else {
			yell( Messages.get(this, "what_is_it") );
		}
		return true;
	}
	
	@Override
	public String description() {

		String msg = "";
		if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify){
			msg = Messages.get(this, "desc_crown");
		} else if (Holiday.getCurrentHoliday() == Holiday.APRIL_FOOLS){
			msg = Messages.get(this, "desc_birthday");
		} else if (Holiday.getCurrentHoliday() == Holiday.WINTER_HOLIDAYS){
			msg = Messages.get(this, "desc_winter");
		} else {
			msg = super.description();
		}

		msg += "\n\n";
		msg += "Level: \t\t"+ level + "\n";
		msg += "Count: \t\t"+ counter;

		return msg;
	}

    public static class Barter extends Buff {

        private ArrayList<Item> items = new ArrayList<>();

        public void stick(Item heh){
            for (Item item : items){
                if (item.isSimilar(heh)){
                    item.merge(heh);
                    return;
                }
            }
            items.add(heh);
        }

        @Override
        public void detach() {
            for (Item item : items)
                Dungeon.level.drop( item, target.pos).sprite.drop();
            super.detach();
        }

        private static final String ITEMS = "items";

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put( ITEMS , items );
            super.storeInBundle(bundle);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            items = new ArrayList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(ITEMS)));
            super.restoreFromBundle( bundle );
        }


    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("e", counter);
		bundle.put("n", level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains("e")){
            counter = bundle.getInt("e");
        }
		if (bundle.contains("n")){
			level = bundle.getInt("n");
		}
    }
}
