package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

import java.util.Arrays;
import java.util.stream.IntStream;

public class DebugUtils {

    public static DebugUtils db = new DebugUtils();

    public DebugUtils() {

    }

    public static void revealLevel() {

        //Dungeon.observeAll();
       /*Level level = Dungeon.level;
        if (level != null && level.map != null) {

            int len = level.width() * level.height();
            IntStream.range(0, len).boxed().forEach(cell -> {

                level.mapped[cell] = true;
                level.discoverable[cell] = false;
            });
        }*/
       /* Level level = Dungeon.level;
        if (level != null && level.map != null) {

            int len = level.width() * level.height();
            //Arrays.stream(level.map)
            IntStream.range(0, len)
                    .boxed()
                    .forEach(cell -> level.discover(cell, false));

            GameScene.updateMap();
        }*/
    }

    public static void heal() {

        if (Dungeon.hero != null) {

            Dungeon.hero.HP = Dungeon.hero.HT;
        }
    }
}