package de.frype.coloring.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import de.frype.coloring.activity.ColorPickerActivity;

/**
 * Created by Jan on 19.11.2015.
 */
public class ColorPickerView extends View {

    private Bitmap bitmap;

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null); // TODO provide a custom Paint?
        }
    }

    public void createBitmap() {
        int width = getWidth();
        int height = getHeight();
        // bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] colors = new int[width * height];

        double w2 = (double) width / 2.;
        double h2 = (double) height / 2.;

        float[] hsv = new float[3];
        int i = 0;
        for (int h = 0; h < height; h++) {
            double y = h - h2;
            for (int w = 0; w < width; w++) {
                double x = w - w2;
                double phi = Math.atan2(y, x);
                double d = Math.max(Math.abs(x / w2), Math.abs(y / h2)); // Manhattan distance
                hsv[0] = (float) ((phi + Math.PI) / (2 * Math.PI) * 360);
                hsv[1] = (float) Math.min(2 * d, 1);
                hsv[2] = (float) Math.min(3 * (1 - d), 1);
                colors[i++] = Color.HSVToColor(hsv);
                //bitmap.setPixel(w, h, Color.HSVToColor(hsv));
            }
        }
        bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        invalidate();
    }

    private void pick_color(int x, int y) {

    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (bitmap != null) {
                int color = bitmap.getPixel(x, y);
                ColorPickerActivity activity = (ColorPickerActivity) getContext();
                activity.colorSelected(color);
            }
        }
        return true;
    }
}