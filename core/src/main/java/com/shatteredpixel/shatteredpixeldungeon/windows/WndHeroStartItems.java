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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroStartingEquipment;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;
import java.util.Map;

public class WndHeroStartItems extends Window {
	private static final int WIDTH		= 120;
	private static final int TTL_HEIGHT = 16;
	private static final int BTN_HEIGHT = 16;
	private static final int GAP        = 1;

	private boolean editable;
	private ArrayList<CheckBox> boxes;

	public WndHeroStartItems(HeroClass _selectedHero) {

		super();

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 12 );
		title.hardlight( TITLE_COLOR );
		title.setPos(
				(WIDTH - title.width()) / 2,
				(TTL_HEIGHT - title.height()) / 2
		);
		PixelScene.align(title);
		add( title );

		boxes = new ArrayList<>();

		float pos = TTL_HEIGHT + 3;

		RedButton selectAll = new RedButton("All") {

			@Override
			protected void onClick() {

				super.onClick();

				HeroStartingEquipment.setAll(true, boxes);
			}
		};
		selectAll.setRect( 0, pos, ((float) WIDTH / 2) - 1, 14 );
		add( selectAll );

		RedButton selectNone = new RedButton("None") {

			@Override
			protected void onClick() {

				super.onClick();

				HeroStartingEquipment.setAll(false, boxes);
			}
		};
		selectNone.setRect( (float) WIDTH / 2, pos, (float) WIDTH / 2, 14 );
		add( selectNone );

		pos = selectNone.bottom();
		pos += GAP;

		int i = 0;
		Map<HeroStartingEquipment, Boolean> startingEquipment = HeroStartingEquipment.getStartingEquipment();

		for (Map.Entry<HeroStartingEquipment, Boolean> es : startingEquipment.entrySet()) {

			final HeroStartingEquipment se = es.getKey();
			final Item item = se.getItem();
			String name = Messages.get(item, "name_short");
			if (name.equals(Messages.NO_TEXT_FOUND)) {

				name = Messages.get(item, "name");
			}

			CheckBox cb = new CheckBox(Messages.titleCase(name), new ItemSprite(item)) {

				@Override
				protected void onClick() {

					super.onClick();

					startingEquipment.put(se, checked());
				}
			};
			cb.checked(es.getValue());
			cb.active = true;
			cb.enable(true);
			if (se.doesItemBelongToHeroClass(_selectedHero)) {

				cb.checked(true);
				cb.active = false;
				cb.enable(false);
				cb.textColor(TITLE_COLOR);
				//startingEquipment.put(se, true);
			}

			if (i > 0) {
				pos += GAP;
			}
			cb.setRect( 0, pos, WIDTH, BTN_HEIGHT );

			add( cb );
			boxes.add( cb );

			pos = cb.bottom();

			i++;
		}

		resize( WIDTH, (int)pos );
	}
}