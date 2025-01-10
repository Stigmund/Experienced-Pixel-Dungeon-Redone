package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.specialized.ToggleAction;
import com.shatteredpixel.shatteredpixeldungeon.windows.specialized.WndUseItemWithToggle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import javax.naming.Context;

public class WndUtils {

    public static WndUseItem getItemWindow(final Window _owner, final Item _item) {

        if (_item instanceof ToggleAction) {

            return new WndUseItemWithToggle(_owner, _item);
        }

        // default/fallback
        return new WndUseItem(_owner, _item);
    }
}