package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import javax.naming.Context;

public class WndUtils {

    public static WndUseItem getItemWindow(final Window _owner, final Item _item) {

        Constructor<?> constructor = null;
        try {

            constructor = _item.itemWindow.getConstructor(Window.class, Item.class);
            Object window = constructor.newInstance(_owner, _item);

            return (WndUseItem) window;
        }
        catch (Exception e) {

            //throw new RuntimeException(e);
            e.printStackTrace();
        }

        // default/fallback
        return new WndUseItem(_owner, _item);
    }
}