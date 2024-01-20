package com.shatteredpixel.shatteredpixeldungeon.windows;

import static com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene.align;
import static com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene.uiCamera;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toast;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.PointerArea;

public class AnimatedToast extends Toast {

    public static AnimatedToast toast;
    public static boolean kill = false;
    private float X;
    private float Y;
    protected final float START_Y;
    private final float END_Y;
    protected float SPEED = 150;
    protected float REDUCTION_FACTOR = 75;
    protected float PERCENT_REMAINING = 0.4f;
    public static void toast(String _text) {

        killToast();
        Game.scene().addToFront((toast = new AnimatedToast(_text)));
    }

    private AnimatedToast(String _text) {

        super(_text);
        text.setHightlighting(false);
        camera = uiCamera;
        START_Y = Y = uiCamera.height;
        END_Y = (uiCamera.height - 60 + ((60 - height()) / 2));
        X = (uiCamera.width - width()) / 2;

        PointerArea hotArea = new PointerArea( 0, 0, uiCamera.width, uiCamera.height ) {

            @Override
            protected void onClick( PointerEvent event ) {

                onClose();
            }
        };
        add(hotArea);
        sendToBack(hotArea);

        setPos(X, Y);
    }

    private void setPosition() {

        setPos(X, Y);
    }

    public static void killToast() {

        if (toast != null) {

            toast.killAndErase();
            toast.destroy();
            toast = null;
        }
    }

    @Override
    public synchronized void update() {
        super.update();

        if ((START_Y > END_Y && Y <= END_Y) // Going up, if Y is less than end point
                || (START_Y < END_Y && Y >= END_Y)) { // Going down, if Y is greater than end point

            Y = END_Y;
        }

        float speedFactor = getSpeedFactor();

        // Going up, if Y is still greater than end point, reduce
        if (START_Y > END_Y && Y > END_Y) Y -= (Game.elapsed * speedFactor);
        // Going down, if Y is still less than end point, increase
        else if (START_Y < END_Y && Y < END_Y) Y += (Game.elapsed * speedFactor);

        setPosition();
    }

    private float getSpeedFactor() {

        float returnSpeed = SPEED;
        float totalHeight = Math.max(START_Y, END_Y) - Math.min(START_Y, END_Y);
        float currentHeight = Math.abs(Y - END_Y); // if going up
        if (START_Y < END_Y) { // if going down

            totalHeight += Math.abs(Y);
            currentHeight = Math.abs(END_Y - Y);
        }
        float currentP = (currentHeight / totalHeight);
        if (currentP <= PERCENT_REMAINING) {

            // start at 500
            // at PERCENT_REMAINING (40%)
            // start decreasing speed by 100
            returnSpeed = SPEED - (SPEED - ((currentP * 10) * REDUCTION_FACTOR));
        }

        return returnSpeed;
    }

    @Override
    protected void onClose() {

        killToast();
    }
}