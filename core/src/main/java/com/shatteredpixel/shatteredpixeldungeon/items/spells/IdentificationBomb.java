/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDivination;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.specialized.ToggleAction;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IdentificationBomb extends Spell implements ToggleAction {

    {
        image = SPDSettings.identificationBomb().getSprite();
    }

    @Override
    protected void onCast(Hero hero) {

        switch (SPDSettings.identificationBomb()) {
            default:
            case IDENTIFY:
                identifyAllUnidentifiedItems(hero);
                break;
            case DESTROY:
                destroyAllUnidentifiedItems(hero);
                break;
        }
    }

    private void identifyAllUnidentifiedItems(Hero hero) {

        useItem(hero);

        curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );

        // identify equipped items if unidentified (existing function)
        int identifiedItems = hero.belongings.observe();

        List<Item> toIdentify = hero.belongings.getBags()
                                    .stream()
                                    .flatMap(b -> b.items.stream().filter(i -> !i.isIdentified()))
                                    .collect(Collectors.toList());

        identifiedItems += toIdentify.size();

        // identify items first!
        toIdentify.forEach(item -> {

            item.identify();
            Badges.validateItemLevelAquired(item);
        });

        bombFeedback(hero, identifiedItems);
    }

    private void destroyAllUnidentifiedItems(Hero hero) {

        useItem(hero);

        int destroyedItems = 0;
        ArrayList<Item> toDestroy = new ArrayList<>();

        for (Item item: hero.belongings){
            if (!item.isIdentified() && (item instanceof EquipableItem || item instanceof Wand)){
                toDestroy.add(item);
                destroyedItems++;
            }
        }

        for (Item item: toDestroy){
            item.detachAll(hero.belongings.backpack);
        }

        bombFeedback(hero, destroyedItems);
    }

    private void useItem(Hero hero) {

        Sample.INSTANCE.play( Assets.Sounds.BLAST, 2f );
        detach( hero.belongings.backpack );
        GameScene.flash( 0x8000A0FF );
        GLog.i(Messages.get(IdentificationBomb.class, "blast"));
        CellEmitter.center(hero.pos).burst(BlastParticle.FACTORY, 100);
        Catalog.countUse(getClass());
    }

    private void bombFeedback(Hero hero, int itemCount) {

        hero.damage(Math.max(0, Math.round(hero.HT*(0.66d * Math.pow(0.9, itemCount)))), new Bomb.ConjuredBomb());

        if (hero.isAlive()) {

            Buff.prolong(hero, Blindness.class, Blindness.DURATION);
            Dungeon.observe();
        }
        else {

            Badges.validateDeathFromFriendlyMagic();
            Dungeon.fail( Bomb.class );
            GLog.n( Messages.get(Bomb.class, "ondeath") );
        }
    }

    @Override
    public long value() {
        return 20 + 50;
    }

    public static class Recipe extends SimpleRecipe {

        private static final int OUT_QUANTITY = 1;

        {
            inputs =  new Class[]{Bomb.class, ScrollOfDivination.class};
            inQuantity = new int[]{1, 1};

            cost = 16;

            output = IdentificationBomb.class;
            outQuantity = OUT_QUANTITY;
        }

    }

    public boolean highlightInventorySlot() {

        return false;
    }

    @Override
    public boolean doToggleAction() {

        IdentifyBombType state = SPDSettings.identificationBomb().toggle();
        setState(state.equals(IdentifyBombType.IDENTIFY));

        return state();
    }

    @Override
    public boolean state() {
        return SPDSettings.identificationBomb().equals(IdentifyBombType.IDENTIFY);
    }

    @Override
    public void setState(boolean state) {
        SPDSettings.identificationBomb(state ? IdentifyBombType.IDENTIFY : IdentifyBombType.DESTROY);
        image = SPDSettings.identificationBomb().getSprite();
    }

    public enum IdentifyBombType {
        IDENTIFY(ItemSpriteSheet.IDENTIFY_BOMB_IDENTIFY),
        DESTROY(ItemSpriteSheet.IDENTIFY_BOMB_DESTROY);

        private final int sprite;

        IdentifyBombType(int sprite) {

            this.sprite = sprite;
        }

        public int getSprite() {

            return this.sprite;
        }

        public IdentifyBombType toggle() {

            switch (this) {
                default:
                case IDENTIFY:
                    return DESTROY;
                case DESTROY:
                    return IDENTIFY;
            }
        }

        public static IdentifyBombType from(String string) {

            switch (string) {
                default:
                case "IDENTIFY":
                    return IDENTIFY;
                case "DESTROY":
                    return DESTROY;
            }
        }
    }
}
