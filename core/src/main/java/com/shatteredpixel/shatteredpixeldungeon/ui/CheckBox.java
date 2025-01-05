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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Image;

public class CheckBox extends RedButton {

	private boolean checked = false;
	private Image checkboxIcon;
	public boolean centerText = false;

	public CheckBox( String label ) {
		super( label );
		
		icon( Icons.get( Icons.UNCHECKED ) );
	}

	public CheckBox(String label, Image icon) {

		super( label );

		if (icon != null) {

			checkboxIcon = icon;
			add(checkboxIcon);
		}

		icon( Icons.get( Icons.UNCHECKED ) );
	}

	@Override
	protected void layout() {

		final int CHECKBOX_ICON_MARGIN = 3;
		final float CHECKBOX_ICON_TEXT_OFFSET = 25;

		if (this.checkboxIcon != null) {

			height = checkboxIcon.height + (CHECKBOX_ICON_MARGIN * 2);
		}

		super.layout();

		float textMargin = (height - text.height()) / 2;
		float margin = (height - icon.height) / 2;

		// checkbox icon is the new icon left justified!
		float textOffset = x + textMargin;
		if (this.checkboxIcon != null) {

			textOffset = x + CHECKBOX_ICON_TEXT_OFFSET;

			checkboxIcon.x = x + (CHECKBOX_ICON_TEXT_OFFSET - checkboxIcon.width) / 2;
			checkboxIcon.y = y + CHECKBOX_ICON_MARGIN;
			PixelScene.align(icon);
		}

		if (centerText) {

			textOffset = (width - text.width()) / 2;
		}
		
		text.setPos(textOffset, y + textMargin);
		PixelScene.align(text);

		// "icon" is the checkbox mark!
		icon.x = x + width - margin - icon.width;
		icon.y = y + margin;
		PixelScene.align(icon);
	}
	
	public boolean checked() {
		return checked;
	}
	
	public void checked( boolean value ) {
		if (checked != value) {
			checked = value;
			icon.copy( Icons.get( checked ? Icons.CHECKED : Icons.UNCHECKED ) );
		}
	}
	
	@Override
	protected void onClick() {
		super.onClick();
		checked( !checked );
	}
}
