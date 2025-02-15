package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.CheeseCheest;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.KingBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
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

        changes.addButton(new ChangeButton(Icons.getStigImage(Icons.CHANGELOG_MAN),
                "Developer Commentary",
                "_-_ Released January 6th, 2024\n" +
                        "_-_ 6 days after Experienced Pixel Dungeon 2.16.2\n" +
                        "_-_ Specifically because I wanted the Glitch from the Cheesy Cheest back! LOL"));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHEESY_CHEEST),
                "Cheesy Cheest Glitch",
                "_-_ Re-introduced the Incredibly Cheesy Cheest's duplication glitch. Because why not!\n" +
                        "_-_ Optional!\n" +
                        "_-_ When you have the Cheesy Cheest, simply click it in the item inventory to see an additional checkbox to toggle the glitch.\n" +
                        "_-_ This is a global setting and persists though all games!"));

        changes.addButton(new ChangeButton(new ItemSprite(new KingBlade()),
                "King's Blade",
                "_-_ King's blade proc effects are now toggleable from the item's info menu!\n" +
                        "_-_ This is a global setting and persists though all games!"));

        changes.addButton(new ChangeButton(new ItemSprite(new Shortsword()),
                "Minimum Range",
                "_-_ Removed duelist minimum range to use special abilities for the weapons:\n"+
                "  - Rapier\n"+
                "  - Spear\n"+
                "  - Glaive"));


        changes.addButton(new ChangeButton(new ItemSprite(new MagesStaff()),
                "Mage's Staff Change",
                "_-_ When imbuing a wand into the staff, the staff will always return the current wand at it's previously added level.\n"+
                        "_-_ No alchemy re-agent, perk or talents are taken into account.\n" +
                        "_-_ UNTESTED"));

        changes.addButton(new ChangeButton(new ItemSprite(new Rotberry.Seed()),
                "Rotberry Seeds",
                "_-_ Two now drop! YAY!"));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHECKED),
                "Hero Starting Items",
                "_-_ Added a menu on hero creation to toggle all hero's default starting artifacts and bags to be added to the newly created character.\n" +
                        "_-_ You cannot disable the selected hero's default starting items, but can toggle other heroes'.\n" +
                        "_-_ Allows you to play rat king but with other class specific talents.'.\n" +
                        "_-_ Duelist has no default starting items'."));

        changes.addButton(new ChangeButton(Icons.get(Icons.TALENT),
                "Toggleable Perks",
                "_-_ The in-game list of perks (accessed by clicking the star in your character info) are now toggleable checkboxes, enabling or disabling the perks functionality."));

        changes.addButton(new ChangeButton(new WandmakerSprite(),
                "Wand Makers Quest Change",
                "_-_ No longer disappears on completion of the wand quest.\n"+
                "_-_ No longer consumes quest items on cheese quest completion."));

        changes.addButton(new ChangeButton(new BlacksmithSprite(),
                "Blacksmith Change",
                "_-_ No longer disappears on completion of the wand quest.\n"+
                "_-_ Always uses the left button as the reforge master item.\n" +
                "_-_ The primary reforge button is now green.\n" +
                "_-_ Option to switch between gold and favor (cost multipliers are shared).\n" +
                "_-_ Removes all keys of truth (UNTESTED)\n" +
                "_-_ Smith weapons don't cost anything until chosen, allowing to close reload the window for a different set.\n" +
                "_-_ All Smith weapons are now shown, making the above change kind of pointless."));

        changes.addButton(new ChangeButton(Icons.get(Icons.BUFFS),
                "Copy Game Slot (Android 10)",
                "_-_ Ability to copy the selected or currently running game slot to a new or existing slot.\n"+
                "_-_ Unable to get exporting to external storage working!"));

        changes.addButton(new ChangeButton(Icons.get(Icons.BUFFS),
                "Import/Export Game (Android 10)",
                "_-_ Ability to export the game to the game's external storage for manual copying afterwards (because even Android 10 can't write to public Documents!).\n"+
                "_-_ Ability to import a game from the game's external storage and copy it to a new slot or overwrite an existing slot."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HALLS_PAGE, null),
                "Journal Game Logs",
                "_-_ New System Setting to toggle saving game logs to the save slot.\n"+
                        "_-_ New tab in the player's Journal displaying the game logs for either the current session or the player's entire logs, depending on the setting above."));


        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS),
                "Other Changes",
                "_-_ Added a UI mechanism to allow items to define their own WndUseItem window!\n" +
                        "_-_ Any window that is or extends WndUseItem can now optionally close and parent windows instead of both automatically closing.\n" +
                        "_-_ Static access toasts for confirmation/error popups.\n" +
                        "_-_ Checkboxes can now have icons before the text.\n" +
                        "_-_ Item info windows now have scrollable text if the text is too large."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16),
                "Bug Fixes",
                "_-_ Blacksmith upgrade cost displaying correctly but using the reforge cost.\n" +
                        "_-_ Duelist weapon swap button now visually updates when pressed.\n" +
                        "_-_ Fixed bug with slime drop if all probs had been exhausted.\n" +
                        "_-_ Fixed Ghost Quest weapon reward being a tier set higher than it should be."));
    }
}