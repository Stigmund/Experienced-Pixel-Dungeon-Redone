package com.shatteredpixel.shatteredpixeldungeon.windows;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.EXPORT_DIR;
import static com.shatteredpixel.shatteredpixeldungeon.GamesInProgress.MAX_SLOTS;
import static com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene.align;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPaneClickable;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WndImportSelectGame extends Window {

    // TODO: Copy of WndCopyGame but instead of slots, it's external saves.
    //		 Maybe try and come up with a  way to read those files instead of game1...
    private final ArrayList<ImportButton> boxes = new ArrayList<>();
    private static final int SLOT_WIDTH = 120;
    private static final int SLOT_HEIGHT = 30;

    private final List<GameInfo> games = readGamesFromExternalStorage();
    private final int NUM_GAMES = GamesInProgress.checkAll().size();
    private final int NEW_GAME = GamesInProgress.firstEmpty();

    public static Pattern pattern = Pattern.compile("^(?:\\[([0-9 _:-]+)\\]|([\\S ]+))");
    public WndImportSelectGame(final boolean _hasSelected) {

        float gap = SLOT_HEIGHT + (10 - MAX_SLOTS);
        float pos = (10 - MAX_SLOTS);
        ScrollPane pane = new ScrollPane(new Component()) {
            @Override
            public void onClick(float x, float y) {

                for (int i = 0; i < boxes.size(); i++) {

                    if (boxes.get(i).onClick(x, y)) break;
                }
            }
        };
        add(pane);
        Component content = pane.content();


        WndImportSelectGame win = this;
        int WIDTH = Math.min(138, (int) (PixelScene.uiCamera.width * 0.8));
        for (GameInfo gameInfo : games) {

            GamesInProgress.Info game =  gameInfo.info;

            ImportSlotButton existingGame = new ImportSlotButton(){
                protected void onClick() {

                //owner.setOverwriteSlot(slot);
                //hide();
                ShatteredPixelDungeon.scene().addToFront(new WndImportGame(gameInfo, null));
            }};


            existingGame.set(gameInfo);
            existingGame.setRect((WIDTH - SLOT_WIDTH) / 2f, pos, SLOT_WIDTH, SLOT_HEIGHT);
            align(existingGame);
            content.add(existingGame);
            boxes.add(existingGame);
            pos += gap;
        }

        /*if (NUM_GAMES < MAX_SLOTS && _hasSelected) {

            ImportNewButton copyToNew = new ImportNewButton();
            copyToNew.set(NEW_GAME);
            copyToNew.setRect((WIDTH - SLOT_WIDTH) / 2f, pos, SLOT_WIDTH, SLOT_HEIGHT);
            content.add(copyToNew);
            boxes.add(copyToNew);
            pos += gap;
        }*/

        ImportSlotCloseButton cancelButton = new ImportSlotCloseButton();
        cancelButton.set();
        cancelButton.setRect((WIDTH - SLOT_WIDTH) / 2f, pos, SLOT_WIDTH, SLOT_HEIGHT);
        content.add(cancelButton);
        boxes.add(cancelButton);
        pos += gap;

        float contentSize = pos;
        int maxheight =  (int) (PixelScene.uiCamera.height * 0.8);
        if (pos > maxheight) pos = maxheight;

        resize(WIDTH, (int) pos);
        pane.setRect(0, 0, WIDTH, pos);

        content.setSize(WIDTH, contentSize);
    }

    private List<GameInfo> readGamesFromExternalStorage() {

        List<GameInfo> games = new ArrayList<>();
        FileHandle fh = FileUtils.getFileHandle(Files.FileType.External, EXPORT_DIR);

        Arrays.stream(Optional.ofNullable(fh.list()).orElse(new FileHandle[0]))
                .forEach(f -> {

                    try {

                        GamesInProgress.Info info = GamesInProgress.readInfoFromFile(new File(f.file(), "game.dat"));
                        if (info != null) {

                            games.add(new GameInfo(info, f.file().getName()));
                        }
                    }
                    catch(Exception e) {

                        e.printStackTrace();
                    }
                });

        return games;
    }

    class GameInfo {

        public GamesInProgress.Info info;
        public String saveDir;

        public String formattedTitleLine;

        public GamesInProgress.Info getInfo() {

            return info;
        }

        public GameInfo(GamesInProgress.Info _info, String _saveDir) {

            info = _info;
            saveDir = _saveDir;
        }
    }

    private class ImportButton extends StartScene.SaveSlotButton implements ScrollPaneClickable {

        protected GameInfo gameInfo;

        @Override
        protected void layout() {
            super.layout();

            // makes it so theres no click area, meaning the scroll pane can account for the buttons!
            hotArea.width = hotArea.height = 0;
        }

        @Override
        public boolean onClick(float x, float y) {

            if (!inside(x, y)) return false;
            onClick();
            return true;
        }
    };

    private class ImportNewButton extends ImportButton {

        @Override
        protected void onClick() {

            ShatteredPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
                    "Are you mad?",
                    "",
                    "Yes,",
                    "No" ) {
                @Override
                protected void onSelect( int index ) {
                    if (index == 0) {

                        hide();
                    }
                }
            } );
        }
    }

    private class ImportSlotCloseButton extends ImportButton {

        @Override
        protected Chrome.Type getType() {

            return Chrome.Type.RED_GEM;
        }

        public void set() {

            name.text("Cancel");
            name.resetColor();

            layout();
        }

        @Override
        protected void onClick() {
            //overwriteSlot = null;
            hide();
        }
    }

    private class ImportSlotButton extends ImportButton {

        protected RenderedTextBlock date;
        protected RenderedTextBlock seconds;

        /*@Override
        protected void onClick() {

            //owner.setOverwriteSlot(slot);
            //hide();
            ShatteredPixelDungeon.scene().addToFront(new WndImportGame(this, gameInfo, null));
        }*/

        @Override
        protected void createChildren() {
            super.createChildren();

            date = PixelScene.renderTextBlock(5);
            add(date);

            seconds = PixelScene.renderTextBlock(4);
            add(seconds);
        }

        private boolean parseAndSetDirectoryName(String _fileName) {

            gameInfo.formattedTitleLine = _fileName;
            Matcher m = pattern.matcher(_fileName);

            if (!m.find()) {

                return false;
            }

            String fileName = m.group(1);

            try {

                String[] dateAndTime = fileName.split(" ");
                String time = dateAndTime[1];
                String hoursAndMins = time.substring(0, time.lastIndexOf(":"));
                String secs = time.substring(time.lastIndexOf(":")+1);

                gameInfo.formattedTitleLine = String.format("%s - %s:%s", dateAndTime[0], hoursAndMins, secs);
                date.text(String.format("%s - %s", dateAndTime[0], hoursAndMins));
                date.resetColor();

                seconds.text(":"+ secs);
                seconds.resetColor();

                return true;
            }
            catch (Exception e) {

                e.printStackTrace();

                return false;
            }
        }
        public void set( GameInfo _info ) {

            gameInfo = _info;
            GamesInProgress.Info info = gameInfo.info;

            if (info.subClass != HeroSubClass.NONE){
                name.text(Messages.titleCase(info.subClass.title()));
            } else {
                name.text(Messages.titleCase(info.heroClass.title()));
            }

            if (!parseAndSetDirectoryName(gameInfo.saveDir)) {

                date.text(gameInfo.saveDir);
                date.resetColor();

                seconds.text("");
                date.resetColor();
            }

            if (hero == null){
                hero = HeroSprite.avatar(info.heroClass,info.armorTier);
                add(hero);

                steps = new Image(Icons.get(Icons.STAIRS));
                add(steps);
                depth = new BitmapText(PixelScene.pixelFont);
                add(depth);

                classIcon = new Image(Icons.get(info.heroClass));
                add(classIcon);
                level = new BitmapText(PixelScene.pixelFont);
                add(level);
            } else {
                hero.copy(HeroSprite.avatar(info.heroClass,info.armorTier));

                classIcon.copy(Icons.get(info.heroClass));
            }

            depth.text(Integer.toString(info.depth));
            depth.measure();

            level.text(Integer.toString(info.level));
            level.measure();

            if (info.challenges > 0){
                name.hardlight(Window.TITLE_COLOR);
                depth.hardlight(Window.TITLE_COLOR);
                level.hardlight(Window.TITLE_COLOR);
            } else {
                name.resetColor();
                depth.resetColor();
                level.resetColor();
            }

            if (info.daily){
                if (info.dailyReplay){
                    steps.hardlight(1f, 0.5f, 2f);
                } else {
                    steps.hardlight(0.5f, 1f, 2f);
                }
            } else if (!info.customSeed.isEmpty()){
                steps.hardlight(1f, 1.5f, 0.67f);
            }

            layout();
        }

        @Override
        protected void layout() {
            super.layout();

            bg.x = x;
            bg.y = y;
            bg.size( width, height );

            hero.x = x+8;
            hero.y = y + (height - hero.height())/2f;
            align(hero);

            float combinedHeight = name.height() + date.height() + 2;
            float combinedY = (height - combinedHeight)/2f;
            float posX = hero.x + hero.width() + 6;
            name.setPos(posX, y + combinedY);
            align(name);

            date.setPos(posX, y + combinedY + name.height() + 2);
            align(date);

            if (!seconds.text().isEmpty()) {

                seconds.setPos(date.right(), date.bottom() - seconds.height());
                align(seconds);
            }

            classIcon.x = x + width - 24 + (16 - classIcon.width())/2f;
            classIcon.y = y + (height - classIcon.height())/2f;
            align(classIcon);

            level.x = classIcon.x + (classIcon.width() - level.width()) / 2f;
            level.y = classIcon.y + (classIcon.height() - level.height()) / 2f + 1;
            align(level);

            steps.x = x + width - 40 + (16 - steps.width())/2f;
            steps.y = y + (height - steps.height())/2f;
            align(steps);

            depth.x = steps.x + (steps.width() - depth.width()) / 2f;
            depth.y = steps.y + (steps.height() - depth.height()) / 2f + 1;
            align(depth);
        }
    }
}