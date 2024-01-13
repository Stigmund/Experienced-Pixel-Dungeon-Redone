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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.watabou.noosa.Game;

import java.util.ArrayList;
import java.util.Locale;

public class WndGameInProgress extends Window {
	
	private static final int WIDTH    = 120;
	
	private static int GAP	  = 6;
	
	//private float pos;

	protected static String getHeroTitle(GamesInProgress.Info _info) {

		String className = null;
		if (_info.subClass != HeroSubClass.NONE){

			className = _info.subClass.title();
		}
		else {

			className = _info.heroClass.title();
		}

		className = Messages.get(WndGameInProgress.class, "title", _info.level, className).toUpperCase(Locale.ENGLISH);

		return className;
	}

	protected static float setContent(Window _window, float inPos, GamesInProgress.Info _info) {

		float pos = inPos;

		IconTitle title = new IconTitle();
		title.icon( HeroSprite.avatar(_info.heroClass, _info.armorTier) );
		title.label((Messages.get(WndGameInProgress.class, "title", _info.level, getHeroTitle(_info))).toUpperCase(Locale.ENGLISH));
		title.color(Window.TITLE_COLOR);
		title.setRect( 0, pos, WIDTH, 0 );
		_window.add(title);

		if (_info.challenges > 0) GAP -= 2;

		pos = title.bottom() + GAP;

		if (_info.challenges > 0) {
			RedButton btnChallenges = new RedButton( Messages.get(WndGameInProgress.class, "challenges") ) {
				@Override
				protected void onClick() {
					Game.scene().add( new WndChallenges( _info.challenges, false ) );
				}
			};
			btnChallenges.icon(Icons.get(Icons.CHALLENGE_ON));
			float btnW = btnChallenges.reqWidth() + 2;
			btnChallenges.setRect( (WIDTH - btnW)/2, pos, btnW , 18 );
			_window.add( btnChallenges );

			pos = btnChallenges.bottom() + GAP;
		}

		pos += GAP;

		int strBonus = _info.strBonus;
		if (strBonus > 0)           pos = statSlot(_window, pos,  Messages.get(WndGameInProgress.class, "str"), _info.str + " + " + strBonus );
		else if (strBonus < 0)      pos = statSlot(_window, pos, Messages.get(WndGameInProgress.class, "str"), _info.str + " - " + -strBonus );
		else                        pos = statSlot(_window, pos, Messages.get(WndGameInProgress.class, "str"), _info.str );
		if (_info.shld > 0)  pos = statSlot(_window, pos, Messages.get(WndGameInProgress.class, "health"), _info.hp + "+" + _info.shld + "/" + _info.ht );
		else                pos = statSlot(_window, pos, Messages.get(WndGameInProgress.class, "health"), (_info.hp) + "/" + _info.ht );
		pos = statSlot(_window, pos, Messages.get(WndGameInProgress.class, "exp"), _info.exp + "/" + Hero.maxExp(_info.level) );

		pos += GAP;
		pos = statSlot(_window, pos, Messages.get(WndGameInProgress.class, "gold"), _info.goldCollected );
		pos = statSlot(_window, pos, Messages.get(WndGameInProgress.class, "depth"), _info.maxDepth );
		if (_info.daily) {
			if (_info.dailyReplay) {
				pos = statSlot(_window, pos,Messages.get(WndGameInProgress.class, "replay_for"), "_" + _info.customSeed + "_");
			} else {
				pos = statSlot(_window, pos,Messages.get(WndGameInProgress.class, "daily_for"), "_" + _info.customSeed + "_");
			}
		} else if (!_info.customSeed.isEmpty()){
			pos = statSlot(_window, pos, Messages.get(WndGameInProgress.class, "custom_seed"), "_" + _info.customSeed + "_" );
		} else {
			pos = statSlot(_window, pos, Messages.get(WndGameInProgress.class, "dungeon_seed"), DungeonSeed.convertToCode(_info.seed) );
		}

		pos += GAP;

		return pos;
	}
	
	public WndGameInProgress(final int slot){
		
		final GamesInProgress.Info info = GamesInProgress.check(slot);
		float pos = WndGameInProgress.setContent(this, 0, info);

		RedButton cont = new RedButton(Messages.get(this, "continue")){
			@Override
			protected void onClick() {
				super.onClick();
				
				GamesInProgress.curSlot = slot;
				
				Dungeon.hero = null;
				Dungeon.daily = Dungeon.dailyReplay = false;
				ActionIndicator.action = null;
				InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
				ShatteredPixelDungeon.switchScene(InterlevelScene.class);
			}
		};
		
		RedButton erase = new RedButton( Messages.get(this, "erase")){
			@Override
			protected void onClick() {
				super.onClick();
				
				ShatteredPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
						Messages.get(WndGameInProgress.class, "erase_warn_title"),
						Messages.get(WndGameInProgress.class, "erase_warn_body"),
						Messages.get(WndGameInProgress.class, "erase_warn_yes"),
						Messages.get(WndGameInProgress.class, "erase_warn_no") ) {
					@Override
					protected void onSelect( int index ) {
						if (index == 0) {
							Dungeon.deleteGame(slot, true);
							ShatteredPixelDungeon.switchNoFade(StartScene.class);
						}
					}
				} );
			}
		};

		RedButton copy = new RedButton(Messages.get(this, "copy")) {
			@Override
			protected void onClick() {
				super.onClick();

				ShatteredPixelDungeon.scene().addToFront(new WndCopyGame(slot));
			}
		};

		cont.icon(Icons.get(Icons.ENTER));
		//cont.setRect(0, pos, WIDTH/2 -1, 20);
		cont.setRect(0, pos, WIDTH, 20);
		add(cont);

		pos = cont.bottom() + 2;

		erase.icon(Icons.get(Icons.CLOSE));
		//erase.setRect(WIDTH/2 + 1, pos, WIDTH/2 - 1, 20);
		erase.setRect(0, pos, WIDTH/2 - 1, 20);
		add(erase);

		copy.icon(Icons.get(Icons.BUFFS));
		//copy.setRect(0, pos, WIDTH, 20);
		copy.setRect(WIDTH/2 + 1, pos, WIDTH/2 -1, 20);
		add(copy);

		resize(WIDTH, (int)copy.bottom()+1);
	}
	
	protected static float statSlot( Window _window, float inPos, String label, String value ) {

		float pos = inPos;
		RenderedTextBlock txt = PixelScene.renderTextBlock( label, 8 );
		txt.setPos(0, inPos);
		_window.add( txt );

		int size = 8;
		if (value.length() >= 14) size -=2;
		if (value.length() >= 18) size -=1;
		txt = PixelScene.renderTextBlock( value, size );
		txt.setPos(WIDTH * 0.55f, pos + (6 - txt.height())/2);
		PixelScene.align(txt);
		_window.add( txt );
		
		pos += GAP + txt.height();

		return pos;
	}
	
	private static float statSlot( Window _window, float inPos, String label, int value ) {
		return statSlot( _window, inPos, label, Integer.toString( value ) );
	}
}
