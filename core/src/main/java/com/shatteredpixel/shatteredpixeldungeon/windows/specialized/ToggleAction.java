package com.shatteredpixel.shatteredpixeldungeon.windows.specialized;

import static com.shatteredpixel.shatteredpixeldungeon.messages.Messages.NO_TEXT_FOUND;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public interface ToggleAction {

    default ItemSprite.Glowing isGlowing() {

        if (state()) {
            return new ItemSprite.Glowing( 0x00FF00 );
        }
        else {
            return null;
        }
    }

    default boolean highlightInventorySlot() {

        return true;
    }

    /**
     * Default implementation to perform the implemented state() and setState() functions.<br/>
     * Inverts the toggle value - e.g. true -> false and false -> true.
     * @return boolean (the resulting final state of the toggle)
     */
    default boolean doToggleAction() {

        boolean state = !state();
        setState(state);

        return state;
    }

    /**
     * Main method the interface uses to identify the current toggle state (true or false).<br/>
     * This must be implemented for the toggle functionality to work.
     * @return boolean (current toggle state)
     */
    default boolean state() {

        return getItem().enabled;
    }

    /**
     * Main method the interface uses to set the internally used value of the toggle state.<br/>
     * This must be implemented for the toggle functionality to work.
     * @param state (the desired new state of the toggle)
     */
    default void setState(boolean state) {

        getItem().enabled = state;
    }

    default Item getItem() {

        return (Item) this;
    }

    /**
     * The button action within the Item's info window when he toggle button is pressed.<br/>
     * Performs the default toggle action while also logging the action to the game journal.
     * @return state (the current state)
     */
    default boolean toggleAction() {

        boolean state = doToggleAction();
        printToggleJournal(state);

        return state;
    }

    default String getToggleButtonText() {

        String msg = Messages.get(this, "toggle_button");
        if (msg.equals(NO_TEXT_FOUND)) {

            msg = "Toggle On/Off";
        }

        return msg;
    }

    default void printToggleJournal(boolean state) {

        String msg = Messages.get(this, "toggle_journal_"+ (state ? "on" : "off"));
        if (msg.equals(NO_TEXT_FOUND)) {

            msg = String.format("%s: %s", Messages.titleCase(Messages.get(this, "name")), (state ? "On" : "Off"));
        }

        GLog.i(String.format("%s%s",
                (state ? GLog.POSITIVE : GLog.NEGATIVE),
                msg));
    }
}