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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionIndicator extends Tag {

	//private static final Class<?extends Buff>[] actionBuffClasses = new Class[]{Preparation.class, SnipersMark.class, Combo.class, Marked.class, Berserk.class, Momentum.class, MeleeWeapon.Charger.class, MonkEnergy.class};

	Visual primaryVis;
	Visual secondVis;

	// for game scene refresh
	public static Map<String, ActionIndicator> instances = new HashMap<>();
	public static List<Action> actions = new ArrayList<>();
	public String name;

	public ActionIndicator(Action action) {
		super( 0 );

		setInstance(action);
		setSize( SIZE, SIZE );
		visible = false;
	}

	private void setInstance(Action action) {

		//this.action = action;
		this.name = action.key();
		instances.put(name, this);
	}

	public static Optional<Action> getAction(Action action) {
		synchronized (ActionIndicator.class) {
			return Optional.of(actions.stream().filter(a -> a == action).findFirst()).orElse(null);
		}
	}

	public static boolean hasAction(Action _action) {

		return getAction(_action).isPresent();
	}

	public static Optional<Action> getAction(String _class) {

		synchronized (ActionIndicator.class) {
			return Optional.of(actions.stream().filter(a -> _class.equals(a.key())).findFirst()).orElse(null);
		}
	}

	public static Optional<ActionIndicator> getInstance(Action action) {

		return getInstance(action.key());
	}

	public static Optional<ActionIndicator> getInstance(String action) {

		synchronized (ActionIndicator.class) {
			return Optional.ofNullable(instances.get(action));
		}
	}

	@Override
	public GameAction keyAction() {
		return SPDAction.TAG_ACTION;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		instances.remove(this);
	}
	
	@Override
	protected synchronized void layout() {
		super.layout();
		
		if (primaryVis != null){
			if (!flipped)   primaryVis.x = x + (SIZE - primaryVis.width()) / 2f + 1;
			else            primaryVis.x = x + width - (SIZE + primaryVis.width()) / 2f - 1;
			primaryVis.y = y + (height - primaryVis.height()) / 2f;
			PixelScene.align(primaryVis);
			if (secondVis != null){
				if (secondVis.width() > 16) secondVis.x = primaryVis.center().x - secondVis.width()/2f;
				else                        secondVis.x = primaryVis.center().x + 8 - secondVis.width();
				if (secondVis instanceof BitmapText){
					//need a special case here for text unfortunately
					secondVis.y = primaryVis.center().y + 8 - ((BitmapText) secondVis).baseLine();
				} else {
					secondVis.y = primaryVis.center().y + 8 - secondVis.height();
				}
				PixelScene.align(secondVis);
			}
		}
	}
	
	private boolean needsRefresh = false;
	
	@Override
	public void update() {
		super.update();

		Optional<Action> action = getAction(name);

		if (!visible && action.isPresent()) {
			visible = true;
			needsRefresh = true;
			flash();
		} else {
			visible = action.isPresent();
		}

		if (needsRefresh) {
			if (primaryVis != null) {
				primaryVis.destroy();
				primaryVis.killAndErase();
				primaryVis = null;
			}
			if (secondVis != null) {
				secondVis.destroy();
				secondVis.killAndErase();
				secondVis = null;
			}
			if (action.isPresent()) {
				primaryVis = action.get().primaryVisual();
				add(primaryVis);

				secondVis = action.get().secondaryVisual();
				if (secondVis != null) {
					add(secondVis);
				}

				setColor(action.get().indicatorColor());
			}

			layout();
			needsRefresh = false;
		}

		if (!Dungeon.hero.ready) {
			if (primaryVis != null) primaryVis.alpha(0.5f);
			if (secondVis != null) secondVis.alpha(0.5f);
		} else {
			if (primaryVis != null) primaryVis.alpha(1f);
			if (secondVis != null) secondVis.alpha(1f);
		}
	}

	@Override
	protected void onClick() {
		super.onClick();

		Optional<Action> action = getAction(name);

		if (action.isPresent() && Dungeon.hero.ready) {
			action.get().doAction();
		}
	}

	@Override
	protected String hoverText() {

		return getAction(name).map(a -> Messages.titleCase(a.actionName())).orElse(null);
	}

	@Override
	protected boolean onLongClick() {
		return false;//findAction(true);
	}

	public static boolean setAction(Action action) {

		// if not usable, or the exact same action, do nothing
		if (!action.usable() || ActionIndicator.actions.contains(action)) return false;
		ActionIndicator.actions.add(action);
		refresh(action);
		return true;
	}

	/*private static final Class<?extends Buff>[] actionBuffClasses = new Class[]{Preparation.class, SnipersMark.class, Combo.class, Marked.class, Berserk.class, Momentum.class, MeleeWeapon.Charger.class, MonkEnergy.class};
	private static boolean findAction(boolean cycle) {
		if(action == null) cycle = false;
		int start = -1;
		if(cycle) while(++start < actionBuffClasses.length && !actionBuffClasses[start].isInstance(action));

		for(int i = (start+1)%actionBuffClasses.length; i != start && i < actionBuffClasses.length; i++) {
			Buff b = Dungeon.hero.buff(actionBuffClasses[i]);
			if(b != null && setAction((Action)b)) return true;
			if(cycle && i+1 == actionBuffClasses.length) i = -1;
		}
		return false;
	}*/

	public static void clearAction(Action action){

		actions.removeIf(a -> ActionIndicator.getAction(a).isPresent());
	}

	public static void refresh(Action action){
		synchronized (ActionIndicator.class) {
			ActionIndicator.getInstance(action).ifPresent(ai -> ai.needsRefresh = true);
		}
	}

	public static void refresh(){

		synchronized (ActionIndicator.class) {
			instances.values().forEach(ai -> ai.needsRefresh = true);
		}
	}

	/*public static void refresh(Class<? extends Action> action){

		getAction(action).ifPresent(ActionIndicator::refresh);
	}*/

	public interface Action {

		default String key() {

			return getClass().getSimpleName();
		}
		String actionName();

		default int actionIcon(){
			return HeroIcon.NONE;
		}

		//usually just a static icon, unless overridden
		default Visual primaryVisual(){
			return new HeroIcon(this);
		}

		//a smaller visual on the bottom-right, usually a tiny icon or bitmap text
		default Visual secondaryVisual(){
			return null; //no second visual by default
		}

		int indicatorColor();

		void doAction();

		default boolean usable() { return true; }
		default boolean isSelectable() { return !ActionIndicator.hasAction(this) && usable(); }

	}

}
