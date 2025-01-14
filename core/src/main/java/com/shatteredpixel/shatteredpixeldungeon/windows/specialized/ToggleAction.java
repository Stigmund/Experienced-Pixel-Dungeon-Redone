package com.shatteredpixel.shatteredpixeldungeon.windows.specialized;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public interface ToggleAction {

    default boolean doToggleAction() {

        boolean state = !state();
        setState(state);

        return state;
    }

    boolean state();
    void setState(boolean state);

    default boolean toggleAction() {

        boolean state = doToggleAction();
        printToggleJournal(state);

        return state;
    }

    default String getToggleButtonText() {

        return Messages.get(this, "toggle_button");
    }

    default void printToggleJournal(boolean state) {

        GLog.i(String.format("%s%s",
                (state ? GLog.POSITIVE : GLog.NEGATIVE),
                Messages.get(this, "toggle_journal_"+ (state ? "on" : "off"))));
    }
}