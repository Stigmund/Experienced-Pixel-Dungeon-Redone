package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.items.bags.CheeseCheest;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class StigPDChanges {

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){

        ChangeInfo changes = new ChangeInfo("StigPD: "+ Game.version, true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.getStigImage(Icons.CHANGELOG_MAN),
                "Developer Commentary",
                "_-_ Released January 6th, 2024\n" +
                        "_-_ 6 days after Experienced Pixel Dungeon 2.16.2\n" +
                        "_-_ Specifically because I wanted the Glitch from the Cheesy Cheest back! LOL"));

        changes.addButton( new ChangeButton(Icons.get(Icons.CHEESY_CHEEST),
                "Cheesy Cheest Glitch",
                "_-_ Re-introduced the Incredibly Cheesy Cheest's duplication glitch. Because why not!\n" +
                        "Optional!\n" +
                        "When you have the Cheesy Cheest, simply click it in the item inventory to see an additional checkbox to toggle the glitch."));

        changes.addButton( new ChangeButton(new ItemSprite(new Shortsword()),
                "Minimum Range",
                "_-_ Removed duelist minimum range to use special abilities for the weapons:\n"+
                "  - Rapier\n"+
                "  - Spear\n"+
                "  - Glaive"));

        changes.addButton( new ChangeButton(Icons.get(Icons.CHECKED),
                "Hero Starting Items",
                "_-_ Added a menu on hero creation to toggle all hero's default starting artifacts and bags to be added to the newly created character.\n" +
                        "_-_ You cannot disable the selected hero's default starting items, but can toggle other heroes'.\n" +
                        "_-_ Allows you to play rat king but with other class specific talents.'.\n" +
                        "_-_ Duelist has no default starting items'."));

        changes.addButton( new ChangeButton(Icons.get(Icons.TALENT),
                "Toggleable Perks",
                "_-_ The in-game list of perks (accessed by clicking the star in your character info) are now toggleable checkboxes, enabling or disabling the perks functionality.\n"+
                        "_-_ UNTESTED - simply replaced all perk check references, tested with the rat spawn on search."));

        changes.addButton( new ChangeButton(new ItemSprite(new MagesStaff()),
                "Mage's Staff Change",
                "_-_ When imbuing a wand into the staff, the staff will always return the current wand at it's previously added level.\n"+
                        "_-_ No alchemy re-agent, perk or talents are taken into account.\n" +
                        "_-_ UNTESTED"));

        changes.addButton( new ChangeButton(new BlacksmithSprite(),
                "Blacksmith Change",
                "_-_ No longer disappears on completion of the wand quest.\n"+
                        "_-_ Removes all keys of truth (UNTESTED) \n" +
                        "_-_ TODO: change the blacksmith to either: \n" +
                        "  - 1) Use the left as the master item \n" +
                        "  - 2) Change the second if to and else if, so a higher level, lower tier item doesn't get upgraded!."));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS),
                "Other Changes",
                "_-_ Added a UI mechanism to allow items to define their own WndUseItem window!\n" +
                        "_-_ Any window that is or extends WndUseItem can now optionally close and parent windows instead of both automatically closing.\n" +
                        "_-_ Checkboxes can now have icons before the text."));
    }
}