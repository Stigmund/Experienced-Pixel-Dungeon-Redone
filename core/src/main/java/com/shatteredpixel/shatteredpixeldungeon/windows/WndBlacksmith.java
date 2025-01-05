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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class WndBlacksmith extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 180;

	private static final int GAP  = 2;

	private static boolean useGold = false;

	private ArrayList<BlacksmithButton> buttons = new ArrayList<>();
	private float buttonStartPos = 0;
	private final int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
	private static WndBlacksmith self;
	private RenderedTextBlock message = null;

	public WndBlacksmith( Blacksmith troll, Hero hero ) {
		super();
		self = this;
		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		IconTitle titlebar = new IconTitle();
		titlebar.icon( troll.sprite() );
		titlebar.label( Messages.titleCase( troll.name() ) );
		titlebar.setRect( 0, 0, width, 0 );
		add( titlebar );

		message = PixelScene.renderTextBlock( Messages.get(this, "prompt", Blacksmith.Quest.favor, Dungeon.gold), 6 );
		message.maxWidth( width );
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );

		float pos = message.bottom() + 3*GAP;
		buttonStartPos = pos;

		WndBlacksmith updatable = this;

		RedButton swapCurrency = new RedButton((useGold ? "Use Favor" : "Use Gold"), 6){
			@Override
			protected void onClick() {
				useGold = !useGold;
				text((useGold ? "Use Favor" : "Use Gold"));
				updateWindow();
			}
		};
		swapCurrency.setSize(40, swapCurrency.reqHeight());
		//swapCurrency.setPos(width - swapCurrency.width(), pos - swapCurrency.height() - GAP);
		swapCurrency.setPos(width - swapCurrency.width(), message.bottom() - swapCurrency.height());
		add(swapCurrency);

		BlacksmithButton pickaxe = new BlacksmithButton(
				(btn) -> Messages.get(this, "pickaxe", btn.getCostGen(), btn.getCostUnit()),
				() -> Blacksmith.Quest.pickaxe != null ? (Statistics.questScores[2] >= WndBlacksmith.WndSmith.cost ? 0 : 250) : null) {
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						troll.sprite(),
						Messages.titleCase( troll.name() ),
						Messages.get(WndBlacksmith.class, "pickaxe_verify") + (getCostGen() == 0 ? "\n\n" + Messages.get(WndBlacksmith.class, "pickaxe_free") : ""),
						Messages.get(WndBlacksmith.class, "pickaxe_yes"),
						Messages.get(WndBlacksmith.class, "pickaxe_no")
				){
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							if (Blacksmith.Quest.pickaxe.doPickUp( Dungeon.hero )) {
								GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", Blacksmith.Quest.pickaxe.name()) ));
							} else {
								Dungeon.level.drop( Blacksmith.Quest.pickaxe, Dungeon.hero.pos ).sprite.drop();
							}
							purchase(getCostGen());
							Blacksmith.Quest.pickaxe = null;
							WndBlacksmith.this.hide();

							if (!Blacksmith.Quest.rewardsAvailable()){
								Notes.remove( Notes.Landmark.TROLL );
							}
						}
					}
				});
			}
		};
		pickaxe.enable(Blacksmith.Quest.pickaxe != null && canPurchase(pickaxe.getCostGen()));
		buttons.add(pickaxe);

		BlacksmithButton reforge = new BlacksmithButton(
				(btn) -> Messages.get(this, "reforge", btn.getCostGen(), btn.getCostUnit()),
				() -> (int) (100 * Math.pow(1.5, Blacksmith.Quest.reforges))) {
			@Override
			protected void onClick() {
				GameScene.show(new WndReforge(troll, WndBlacksmith.this, getCostGen()));
			}
		};
		buttons.add(reforge);

		BlacksmithButton harden = new BlacksmithButton(
				(btn) -> Messages.get(this, "harden", btn.getCostGen(), btn.getCostUnit()),
				() -> (int) (400 * Math.pow(1.5, Blacksmith.Quest.hardens))) {
			@Override
			protected void onClick() {
				GameScene.selectItem(new HardenSelector(getCostGen()));
			}
		};
		buttons.add(harden);

		BlacksmithButton upgrade = new BlacksmithButton(
				(btn) -> Messages.get(this, "upgrade", btn.getCostGen(), btn.getCostUnit()),
				() -> (int) (400 * Math.pow(1.5, Blacksmith.Quest.upgrades))) {
			@Override
			protected void onClick() {
				GameScene.selectItem(new UpgradeSelector(getCostGen()));
			}
		};
		buttons.add(upgrade);

		/*BlacksmithButton smith = new BlacksmithButton(
				(btn) -> Messages.get(this, "smith", btn.getCostGen(), btn.getCostUnit()),
				() -> 2500) {
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						troll.sprite(),
						Messages.titleCase( troll.name() ),
						Messages.get(WndBlacksmith.class, "smith_verify"),
						Messages.get(WndBlacksmith.class, "smith_yes"),
						Messages.get(WndBlacksmith.class, "smith_no")
				){
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							purchase(2500);
							Blacksmith.Quest.smiths++;
							WndBlacksmith.this.hide();
							GameScene.show(new WndSmith(troll, hero));
						}
					}
				});
			}
		};*/
		BlacksmithButton smith = new BlacksmithButton(
				(btn) -> Messages.get(this, "smith", btn.getCostGen(), btn.getCostUnit()),
				() -> 2500) {
			@Override
			protected void onClick() {

				GameScene.show(new WndSmith(troll, hero));
			}
		};
		buttons.add(smith);

		int cashOutModifier = (int) Math.pow(Dungeon.cycle + 1, 5);

		BlacksmithButton cashOut = new BlacksmithButton(
				(btn) -> Messages.get(this, "cashout", cashOutModifier),
				() -> Blacksmith.Quest.favor == 0 ? null : Blacksmith.Quest.favor) {
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						troll.sprite(),
						Messages.titleCase( troll.name() ),
						Messages.get(WndBlacksmith.class, "cashout_verify", Blacksmith.Quest.favor*cashOutModifier),
						Messages.get(WndBlacksmith.class, "cashout_yes"),
						Messages.get(WndBlacksmith.class, "cashout_no")
				){
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							new Gold(Blacksmith.Quest.favor*cashOutModifier).doPickUp(Dungeon.hero, Dungeon.hero.pos);
							Blacksmith.Quest.favor = 0;
							WndBlacksmith.this.hide();
						}
					}
				});
			}
		};
		buttons.add(cashOut);

		for (BlacksmithButton b : buttons){
			b.leftJustify = true;
			b.multiline = true;
			b.setSize(width, b.reqHeight());
			b.setRect(0, pos, width, b.reqHeight());
			b.enable(b.active); //so that it's visually reflected
			add(b);
			pos = b.bottom() + GAP;
		}

		resize(width, (int)pos);

	}


	private String getUnit() {

		return (useGold ? "Gold" : "Favor");
	}

	private boolean canPurchase(int _requiredAmount) {

		return (useGold ? Dungeon.gold : Blacksmith.Quest.favor) >= _requiredAmount;
	}

	private static void purchase(int _amount) {

		if (useGold) Dungeon.gold -= _amount;
		else Blacksmith.Quest.favor -= _amount;
	}

	private void updateWindow() {

		message.text(Messages.get(this, "prompt", Blacksmith.Quest.favor, Dungeon.gold));

		float pos = buttonStartPos;
		for (BlacksmithButton b : buttons) {

			b.updateText();
			b.setSize(width, b.reqHeight());
			b.setRect(0, pos, width, b.reqHeight());
			b.setEnabled();
			//add(b);
			pos = b.bottom() + GAP;
		}

		resize(width, (int)pos);
	}

	protected class BlacksmithButton extends RedButton {

		private final Function<BlacksmithButton, String> textGen;
		private final Supplier<Integer> costGen;

		protected BlacksmithButton(Function<BlacksmithButton, String> _text, Supplier<Integer> _cost) {

			super("", 6);
			textGen = _text;
			costGen = _cost;
			setEnabled();
		}

		protected void setEnabled() {

			Integer cost = costGen.get();
			enable(cost != null && canPurchase(cost));
		}

		protected int getCostGen() {

			Integer cost = costGen.get();
			return cost == null ? 0 : cost;
		}

		protected String getTextGen() {

			return textGen.apply(this);
		}

		protected String getCostUnit() {

			return getUnit().toLowerCase();
		}

		protected void updateText() {

			text(textGen.apply(this));
		}
	}

	//public so that it can be directly called for pre-v2.2.0 quest completions
	public static class WndReforge extends Window {

		private static final int WIDTH		= 120;

		private static final int BTN_SIZE	= 32;
		private static final float GAP		= 2;
		private static final float BTN_GAP	= 5;

		private ItemButton btnPressed;

		private ItemButton btnItem1;
		private ItemButton btnItem2;
		private RedButton btnReforge;

		public WndReforge( Blacksmith troll, Window wndParent, int cost ) {
			super();

			IconTitle titlebar = new IconTitle();
			titlebar.icon( troll.sprite() );
			titlebar.label( Messages.titleCase( troll.name() ) );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );

			RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "message"), 6 );
			message.maxWidth( WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );

			btnItem1 = new ItemButton() {
				@Override
				public Chrome.Type getType() {
					return Chrome.Type.GREEN_BUTTON;
				}

				@Override
				protected void onClick() {
					btnPressed = btnItem1;
					GameScene.selectItem( itemSelector );
				}
			};
			btnItem1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
			add( btnItem1 );

			btnItem2 = new ItemButton() {
				@Override
				protected void onClick() {
					btnPressed = btnItem2;
					GameScene.selectItem( itemSelector );
				}
			};
			btnItem2.setRect( btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE );
			add( btnItem2 );

			btnReforge = new RedButton( Messages.get(this, "reforge") ) {
				@Override
				protected void onClick() {

					Item first, second;
					first = btnItem1.item();
					second = btnItem2.item();

					/*if (btnItem1.item() instanceof EquipableItem.Tierable && btnItem2.item() instanceof EquipableItem.Tierable){
						if (((EquipableItem.Tierable) btnItem1.item()).tier() < ((EquipableItem.Tierable) btnItem2.item()).tier()) {
							first = btnItem2.item();
							second = btnItem1.item();
						}
					}
					if (btnItem1.item().trueLevel() < btnItem2.item().trueLevel() && !(btnItem1.item() instanceof FishingRod)) {
						first = btnItem2.item();
						second = btnItem1.item();
					}*/

					Sample.INSTANCE.play( Assets.Sounds.EVOKE );
					ScrollOfUpgrade.upgrade( Dungeon.hero );
					Item.evoke( Dungeon.hero );

					if (second.isEquipped( Dungeon.hero )) {
						((EquipableItem)second).doUnequip( Dungeon.hero, false );
					}
					second.detach( Dungeon.hero.belongings.backpack );

					if (second instanceof Armor){
						BrokenSeal seal = ((Armor) second).checkSeal();
						if (seal != null){
							Dungeon.level.drop( seal, Dungeon.hero.pos );
						}
					}
					if (first instanceof MissileWeapon && first.quantity() > 1){
						first = first.split(1);
					}

					long level = first.trueLevel();
					first.level(level+second.trueLevel()+1); //prevents on-upgrade effects like enchant/glyph removal
					if (first instanceof MissileWeapon && !Dungeon.hero.belongings.contains(first)) {
						if (!first.collect()){
							Dungeon.level.drop( first, Dungeon.hero.pos );
						}
					}
					Badges.validateItemLevelAquired( first );
					Item.updateQuickslot();

					// [CHANGED] static cost 2500
					purchase(cost);
					Blacksmith.Quest.reforges++;

					if (!Blacksmith.Quest.rewardsAvailable()){
						Notes.remove( Notes.Landmark.TROLL );
					}

					hide();
					if (wndParent != null){
						wndParent.hide();
					}
				}
			};
			btnReforge.enable( false );
			btnReforge.setRect( 0, btnItem1.bottom() + BTN_GAP, WIDTH, 20 );
			add( btnReforge );


			resize( WIDTH, (int)btnReforge.bottom() );
		}

		protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

			@Override
			public String textPrompt() {
				return Messages.get(WndReforge.class, "prompt");
			}

			@Override
			public Class<?extends Bag> preferredBag(){
				return Belongings.Backpack.class;
			}

			@Override
			public boolean itemSelectable(Item item) {
				return item.isIdentified() && !item.cursed && item.isUpgradable();
			}

			@Override
			public void onSelect( Item item ) {
				if (item != null && btnPressed.parent != null) {
					btnPressed.item(item);

					Item item1 = btnItem1.item();
					Item item2 = btnItem2.item();

					//need 2 items
					if (item1 == null || item2 == null) {
						btnReforge.enable(false);

					} else if (item1 == item2 && item1.quantity() == 1) {
						btnReforge.enable(false);

					} else {
						btnReforge.enable(true);
					}
				}
			}
		};

	}

	private class HardenSelector extends WndBag.ItemSelector {

		int cost = 0;

		public HardenSelector(int cost) {
			super();
			this.cost = cost;
		}

		@Override
		public String textPrompt() {
			return Messages.get(this, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item.isUpgradable()
					&& item.isIdentified() && !item.cursed
					&& ((item instanceof MeleeWeapon && !((Weapon) item).enchantHardened)
					|| (item instanceof Armor && !((Armor) item).glyphHardened));
		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {
				if (item instanceof Weapon){
					((Weapon) item).enchantHardened = true;
				} else if (item instanceof Armor){
					((Armor) item).glyphHardened = true;
				}

				purchase(cost);
				Blacksmith.Quest.hardens++;

				WndBlacksmith.this.hide();

				Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				Item.evoke( Dungeon.hero );

				if (!Blacksmith.Quest.rewardsAvailable()){
					Notes.remove( Notes.Landmark.TROLL );
				}
			}
		}
	}

	private class UpgradeSelector extends WndBag.ItemSelector {

		private final int cost;

		public UpgradeSelector(int costGen) {
			super();
			cost = costGen;
		}

		@Override
		public String textPrompt() {
			return Messages.get(this, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item.isUpgradable()
					&& item.isIdentified()
					&& !item.cursed
					&& item.level() > 0;
		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {
				purchase(cost);
				Blacksmith.Quest.upgrades++;

				ScrollOfUpgrade upgrades = new ScrollOfUpgrade();
				long trueLevel = item.trueLevel();
				upgrades.quantity(trueLevel);
				if (!upgrades.doPickUp(Dungeon.hero, Dungeon.hero.pos, 0f)) {
					Dungeon.level.drop(upgrades, Dungeon.hero.pos).sprite.drop();
				} else {
					GLog.i(Messages.get(Hero.class, "you_now_have", upgrades.name()));
				}
				item.level(0);

				WndBlacksmith.this.hide();

				Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				Item.evoke( Dungeon.hero );
				Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, "+" + trueLevel);

				if (!Blacksmith.Quest.rewardsAvailable()){
					Notes.remove( Notes.Landmark.TROLL );
				}

				Catalog.countUse(item.getClass());
			}
		}
	}

	public static class WndSmith extends Window {

		private static final int WIDTH      = 120;
		private static final int BTN_SIZE	= 32;
		private static final int BTN_GAP	= 5;
		private static final int GAP		= 2;
		public static final int cost = 2500;

		public WndSmith( Blacksmith troll, Hero hero ){
			super();

			IconTitle titlebar = new IconTitle();
			titlebar.icon(troll.sprite());
			titlebar.label(Messages.titleCase(troll.name()));

			RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "prompt"), 6 );

			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );

			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );

			if (Blacksmith.Quest.smithRewards == null || Blacksmith.Quest.smithRewards.isEmpty()){
				Blacksmith.Quest.generateRewards(false);
			}

			int count = 0;
			int btnPerRow = 0;
			float pos = message.top() + message.height() + BTN_GAP;
			float tempMaxWidth = BTN_GAP;
			for (Object o : Blacksmith.Quest.smithRewards)
			{
				float testX = tempMaxWidth + BTN_SIZE + BTN_GAP;
				if (testX > WIDTH) break;

				btnPerRow++;
				tempMaxWidth = testX;
			}
			final float btnMaxWith = tempMaxWidth;

			Function<List<Item>, Float> getXPos = (list) -> {
				float w = (BTN_SIZE * list.size()) + (BTN_GAP * (list.size() -1));
				float x = ((WIDTH - Math.min(w, btnMaxWith)) / 2);
				if (w > btnMaxWith) x += BTN_GAP;
				return x;
			};

			float xPos = getXPos.apply(Blacksmith.Quest.smithRewards);
			float yPos = pos;

			for (Item i : Blacksmith.Quest.smithRewards){
				count++;
				ItemButton btnReward = new ItemButton(){
					@Override
					protected void onClick() {
						GameScene.show(new RewardWindow(troll, hero, item()));
					}
				};
				btnReward.item( i );
				//btnReward.setRect( count*(WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
				btnReward.setRect(xPos, yPos, BTN_SIZE, BTN_SIZE);
				add( btnReward );

				pos = btnReward.bottom() + BTN_GAP;
				xPos += BTN_SIZE + BTN_GAP;

				if (count % btnPerRow == 0 && count < Blacksmith.Quest.smithRewards.size()) {

					List<Item> remainingWeapons = Blacksmith.Quest.smithRewards.subList(count, Math.min(Blacksmith.Quest.smithRewards.size(), count + btnPerRow));
					xPos = getXPos.apply(remainingWeapons);
					yPos = pos;
				}
			}

			RedButton btnCancel = new RedButton("Cancel"){
				@Override
				protected void onClick() {

					onBackPressed();
				}
			};

			btnCancel.setRect( 0, pos, WIDTH, 20 );
			add( btnCancel );


			resize(WIDTH, (int)btnCancel.bottom());

		}

		@Override
		public void onBackPressed() {

			Blacksmith.Quest.smithRewards = null;
			WndSmith.this.hide();
		}

		private class RewardWindow extends WndInfoItem {

			public RewardWindow( Blacksmith troll, Hero hero, Item item ) {
				super(item);

				RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm")){
					@Override
					protected void onClick() {

						GameScene.show(new WndOptions(
								troll.sprite(),
								Messages.titleCase( troll.name() ),
								Messages.get(WndBlacksmith.class, "smith_verify"),
								Messages.get(WndBlacksmith.class, "smith_yes"),
								Messages.get(WndBlacksmith.class, "smith_no")
						){
							@Override
							protected void onSelect(int index) {
								if (index == 0){

									purchase(WndSmith.cost);
									Blacksmith.Quest.smiths++;

									if (item instanceof Weapon && Blacksmith.Quest.smithEnchant != null){
										((Weapon) item).enchant(Blacksmith.Quest.smithEnchant);
									} else if (item instanceof Armor && Blacksmith.Quest.smithGlyph != null){
										((Armor) item).inscribe(Blacksmith.Quest.smithGlyph);
									}

									item.identify(false);
									Sample.INSTANCE.play(Assets.Sounds.EVOKE);
									Item.evoke( Dungeon.hero );

									if (item.doPickUp( Dungeon.hero )) {
										GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", item.name())) );
									} else {
										Dungeon.level.drop( item, Dungeon.hero.pos ).sprite.drop();
									}

									Blacksmith.Quest.smithRewards = null;

									if (!Blacksmith.Quest.rewardsAvailable()){
										Notes.remove( Notes.Landmark.TROLL );
									}

									RewardWindow.this.hide();
									WndSmith.this.hide();
									self.updateWindow();
								}
							}
						});
					}
				};
				btnConfirm.setRect(0, height+2, width/2-1, 16);
				add(btnConfirm);

				RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel")){
					@Override
					protected void onClick() {
						RewardWindow.this.hide();
					}
				};
				btnCancel.setRect(btnConfirm.right()+2, height+2, btnConfirm.width(), 16);
				add(btnCancel);

				resize(width, (int)btnCancel.bottom());
			}
		}
	}
}