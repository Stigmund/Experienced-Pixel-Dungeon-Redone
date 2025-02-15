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

import static com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem.BUTTON_HEIGHT;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotHeart;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PsycheChest;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Cheese;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfUnstable;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RotGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndWandmaker;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Wandmaker extends NPC {

	private boolean blockCheeseFlag = false;

	{
		spriteClass = WandmakerSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	@Override
	public Notes.Landmark landmark() {
		return Notes.Landmark.WANDMAKER;
	}

	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			return true;
		}
		return super.act();
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage( long dmg, Object src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}
	
	@Override
	public boolean reset() {
		return true;
	}

	private void giftCheeseWand() {

		Dungeon.hero.belongings.getItem(Cheese.class).detach(Dungeon.hero.belongings.backpack);
		Item wand = new WandOfUnstable().identify();
		if (wand.doPickUp( Dungeon.hero )) {
			GLog.i( Messages.get(Dungeon.hero, "you_now_have", wand.name()) );
		} else {
			Dungeon.level.drop( wand, pos ).sprite.drop();
		}
		//yell( Messages.get(this, "farewell", Dungeon.hero.name()) );
		//Wandmaker.this.destroy();

		//Wandmaker.this.sprite.die();

		//Wandmaker.Quest.complete();

		Badges.validateUnstable();
	}
	
	@Override
	public boolean interact(Char c) {
		sprite.turnTo( pos, Dungeon.hero.pos );

		if (c != Dungeon.hero){
			return true;
		}

		Wandmaker self = this;
		if (Dungeon.hero.belongings.getItem(Cheese.class) != null && !blockCheeseFlag){
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					blockCheeseFlag = true;
					GameScene.show(new WndCheeseQuest(Wandmaker.this, Messages.get(Wandmaker.class, "cheese"), self)/*{
						@Override
						public void hide() {
							super.hide();

							Dungeon.hero.belongings.getItem(Cheese.class).detach(Dungeon.hero.belongings.backpack);
							Item wand = new WandOfUnstable().identify();
							if (wand.doPickUp( Dungeon.hero )) {
								GLog.i( Messages.get(Dungeon.hero, "you_now_have", wand.name()) );
							} else {
								Dungeon.level.drop( wand, pos ).sprite.drop();
							}
							//yell( Messages.get(this, "farewell", Dungeon.hero.name()) );
							//Wandmaker.this.destroy();

							//Wandmaker.this.sprite.die();

							//Wandmaker.Quest.complete();

							Badges.validateUnstable();
						}
					}*/);
				}
			});
			return true;
		}

		blockCheeseFlag = false;

		if (Quest.given && !Quest.complete) {
			
			Item item;
			switch (Quest.type) {
				case 1:
				default:
					item = Dungeon.hero.belongings.getItem(CorpseDust.class);
					break;
				case 2:
					item = Dungeon.hero.belongings.getItem(Embers.class);
					break;
				case 3:
					item = Dungeon.hero.belongings.getItem(Rotberry.Seed.class);
					break;
			}

			if (item != null) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndWandmaker( Wandmaker.this, item ) );
					}
				});
			} else {
				String msg;
				switch(Quest.type){
					case 1: default:
						msg = Messages.get(this, "reminder_dust", Messages.titleCase(Dungeon.hero.name()));
						break;
					case 2:
						msg = Messages.get(this, "reminder_ember", Messages.titleCase(Dungeon.hero.name()));
						break;
					case 3:
						msg = Messages.get(this, "reminder_berry", Messages.titleCase(Dungeon.hero.name()));
						break;
				}
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndQuest(Wandmaker.this, msg));
					}
				});
			}
			
		} else if (!Quest.given) {

			String msg1 = "";
			String msg2 = "";
			switch(Dungeon.hero.heroClass){
				case WARRIOR:
					msg1 += Messages.get(this, "intro_warrior");
					break;
				case ROGUE:
					msg1 += Messages.get(this, "intro_rogue");
					break;
				case MAGE:
					msg1 += Messages.get(this, "intro_mage", Messages.titleCase(Dungeon.hero.name()));
					break;
				case HUNTRESS:
					msg1 += Messages.get(this, "intro_huntress");
					break;
				case DUELIST:
					msg1 += Messages.get(this, "intro_duelist");
					break;
				case RAT_KING:
					msg1 += Messages.get(this, "intro_ratking");
					break;
			}

			msg1 += Messages.get(this, "intro_1");

			switch (Quest.type){
				case 1:
					msg2 += Messages.get(this, "intro_dust");
					break;
				case 2:
					msg2 += Messages.get(this, "intro_ember");
					break;
				case 3:
					msg2 += Messages.get(this, "intro_berry");
					break;
			}

			msg2 += Messages.get(this, "intro_2");
			final String msg1Final = msg1;
			final String msg2Final = msg2;
			
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndQuest(Wandmaker.this, msg1Final){
						@Override
						public void hide() {
							super.hide();
							GameScene.show(new WndQuest(Wandmaker.this, msg2Final));
						}
					});
				}
			});

			Quest.given = true;
		}

		return true;
	}
	
	public static class Quest {

		private static int type;
		// 1 = corpse dust quest
		// 2 = elemental embers quest
		// 3 = rotberry quest
		
		private static boolean spawned;
		
		private static boolean given;
		private static boolean complete;
		
		public static Wand wand1;
		public static Wand wand2;
		
		public static void reset() {
			spawned = false;
			type = 0;

			wand1 = null;
			wand2 = null;
		}
		
		private static final String NODE		= "wandmaker";
		
		private static final String SPAWNED		= "spawned";
		private static final String TYPE		= "type";
		private static final String GIVEN		= "given";
		private static final String COMPLETE		= "complete";
		private static final String WAND1		= "wand1";
		private static final String WAND2		= "wand2";

		private static final String RITUALPOS	= "ritualpos";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				
				node.put( TYPE, type );
				
				node.put( GIVEN, given );
				node.put( COMPLETE, complete );
				
				node.put( WAND1, wand1 );
				node.put( WAND2, wand2 );

				if (type == 2){
					node.put( RITUALPOS, CeremonialCandle.ritualPos );
				}

			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {

				type = node.getInt(TYPE);
				
				given = node.getBoolean( GIVEN );
				complete = node.getBoolean( COMPLETE );
				
				wand1 = (Wand)node.get( WAND1 );
				wand2 = (Wand)node.get( WAND2 );

				if (type == 2){
					CeremonialCandle.ritualPos = node.getInt( RITUALPOS );
				}

			} else {
				reset();
			}
		}
		
		private static boolean questRoomSpawned;
		
		public static void spawnWandmaker( Level level, Room room ) {
			if (questRoomSpawned) {
				
				questRoomSpawned = false;
				
				Wandmaker npc = new Wandmaker();
				boolean validPos;
				//Do not spawn wandmaker on the entrance, in front of a door, or on bad terrain.
				do {
					validPos = true;
					npc.pos = level.pointToCell(room.random((room.width() > 6 && room.height() > 6) ? 2 : 1));
					if (npc.pos == level.entrance()){
						validPos = false;
					}
					for (Point door : room.connected.values()){
						if (level.trueDistance( npc.pos, level.pointToCell( door ) ) <= 1){
							validPos = false;
						}
					}
					if (level.traps.get(npc.pos) != null
							|| !level.passable[npc.pos]
							|| level.map[npc.pos] == Terrain.EMPTY_SP){
						validPos = false;
					}
				} while (!validPos);
				level.mobs.add( npc );

				spawned = true;

				given = false;
				complete = false;
				wand1 = (Wand) Generator.random(Generator.Category.WAND);
				wand1.cursed = false;
				wand1.upgrade();
                switch (Dungeon.cycle){
                    case 1: wand1.upgrade(4); break;
                    case 2: wand1.upgrade(35); break;
                }

				wand2 = (Wand) Generator.random(Generator.Category.WAND);
				ArrayList<Item> toUndo = new ArrayList<>();
				while (wand2.getClass() == wand1.getClass()) {
					toUndo.add(wand2);
					wand2 = (Wand) Generator.random(Generator.Category.WAND);
				}
				for (Item i :toUndo){
					Generator.undoDrop(i);
				}
				wand2.cursed = false;
				wand2.upgrade();
                switch (Dungeon.cycle){
                    case 1: wand2.upgrade(4); break;
                    case 2: wand2.upgrade(35); break;
                }

			}
		}
		
		public static ArrayList<Room> spawnRoom( ArrayList<Room> rooms) {
			questRoomSpawned = false;
			if (!spawned && (type != 0 || (Dungeon.depth == PsycheChest.questDepth ||
					(Dungeon.depth > 6 && Random.Int( 10 - Dungeon.depth ) == 0)))) {
				// decide between 1,2, or 3 for quest type.
				if (type == 0) type = Random.Int(3)+1;
				
				switch (type){
					case 1: default:
						rooms.add(new MassGraveRoom());
						break;
					case 2:
						rooms.add(new RitualSiteRoom());
						break;
					case 3:
						rooms.add(new RotGardenRoom());
						break;
				}
		
				questRoomSpawned = true;
				PsycheChest.questDepth = -1;

			}
			return rooms;
		}

		//quest is active if:
		public static boolean active(){
			//it is not completed
			if (wand1 == null || wand2 == null
					|| !(Dungeon.level instanceof RegularLevel) || Dungeon.hero == null){
				return false;
			}

			//and...
			if (type == 1){
				//hero is in the mass grave room
				if (((RegularLevel) Dungeon.level).room(Dungeon.hero.pos) instanceof MassGraveRoom) {
					return true;
				}

				//or if they are corpse dust cursed
				for (Buff b : Dungeon.hero.buffs()) {
					if (b instanceof CorpseDust.DustGhostSpawner) {
						return true;
					}
				}

				return false;
			} else if (type == 2){
				//hero has summoned the newborn elemental
				for (Mob m : Dungeon.level.mobs) {
					if (m instanceof Elemental.NewbornFireElemental) {
						return true;
					}
				}

				//or hero is in the ritual room and all 4 candles are with them
				if (((RegularLevel) Dungeon.level).room(Dungeon.hero.pos) instanceof RitualSiteRoom) {
					long candles = 0;
					if (Dungeon.hero.belongings.getItem(CeremonialCandle.class) != null){
						candles += Dungeon.hero.belongings.getItem(CeremonialCandle.class).quantity();
					}

					if (candles >= 4){
						return true;
					}

					for (Heap h : Dungeon.level.heaps.valueList()){
						if (((RegularLevel) Dungeon.level).room(h.pos) instanceof RitualSiteRoom){
							for (Item i : h.items){
								if (i instanceof CeremonialCandle){
									candles += i.quantity();
								}
							}
						}
					}

					if (candles >= 4){
						return true;
					}

				}

				return false;
			} else {
				//hero is in the rot garden room and the rot heart is alive
				if (((RegularLevel) Dungeon.level).room(Dungeon.hero.pos) instanceof RotGardenRoom) {
					for (Mob m : Dungeon.level.mobs) {
						if (m instanceof RotHeart) {
							return true;
						}
					}
				}

				return false;
			}
		}
		
		public static void complete() {
			wand1 = null;
			wand2 = null;

			Quest.complete = true;
			Notes.remove( Notes.Landmark.WANDMAKER );
			Statistics.questScores[1] = 2000;
		}
	}

	private static class WndCheeseQuest extends WndQuest {

		private final WndCheeseQuest wnd;
		private final Wandmaker wandmaker;

		public void hide() {

			super.hide();
			wandmaker.blockCheeseFlag = true;
			wandmaker.interact(Dungeon.hero);
		}

		protected int addExtraContent(int y, int width) {

			int halfWidth = (width /2);
			int btnWidth = halfWidth - GAP;

			RedButton ok = new RedButton("Yes", 8, true ) {
				@Override
				protected void onClick() {

					wandmaker.giftCheeseWand();
					hide();
				}
			};
			ok.setPos(0, y);
			ok.setSize( btnWidth, BUTTON_HEIGHT );
			add( ok );

			RedButton cancel = new RedButton("No", 8 ) {
				@Override
				protected void onClick() {

					hide();
				}
			};

			cancel.setPos((float) (halfWidth + (GAP / 2)), y);
			cancel.setSize( btnWidth, BUTTON_HEIGHT );
			add( cancel );

			return (int) (cancel.bottom());
		}

		public WndCheeseQuest(NPC questgiver, String text, Wandmaker wandmaker) {

			super(questgiver, text);
			this.wnd = this;
			this.wandmaker = wandmaker;
		}
	}
}
