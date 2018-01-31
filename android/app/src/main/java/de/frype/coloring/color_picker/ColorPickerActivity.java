package de.frype.coloring.color_picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import de.frype.coloring.ColoringUtils;
import de.frype.coloring.R;
import de.frype.ui.RegularGridLayout;
import de.frype.util.Utils;

/**
 * Shows a selection of colors as grid of small buttons filled with the color. Upon touch of a button, this color is
 * selected (stored in the Library instance), and the activity returns.
 */
public class ColorPickerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        // back button action: set result as canceled and go back
        ImageButton imageButton = findViewById(R.id.backButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // create all the color buttons (after we know the size of the color selection regular grid)
        final RegularGridLayout layout = findViewById(R.id.colorSelectionGridLayout);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Utils.removeOnGlobalLayoutListener(layout, this);
                Context context = ColorPickerActivity.this;
                float button_space_px = context.getResources().getDimension(R.dimen.color_selection_button_size) + 2 * context.getResources().getDimension(R.dimen.color_selection_button_margin);

                int width_px = layout.getWidth();
                int height_px = layout.getHeight();

                // compute the number of columns and rows that we can display
                int columns = (int) Math.floor(width_px / button_space_px);
                int rows = (int) Math.floor(height_px / button_space_px);

                layout.setColumns(columns);
                layout.setRows(rows);

                // now create an array of buttons
                LayoutInflater inflater = LayoutInflater.from(context);
                float[] hsv = new float[3];
                for (int row = 0; row < rows; row++) {
                    for (int column = 0; column < columns; column++) {
                        // get a new view
                        View view = inflater.inflate(R.layout.element_color_selection_grid, layout, false);
                        layout.addView(view);
                        float row_fraction = (float) row / rows; // in [0,1)
                        float value = 1 - 0.7f * (float)Math.exp(-row_fraction / 0.5f); // saturated increase
                        if (column == 0) {
                            // first column is gray level only
                            hsv[0] = 0; // hue = 0
                            hsv[1] = 0; // saturation = 0
                            hsv[2] = 0.8f * row_fraction + 0.1f;
                        } else {
                            // rainbow where hue is column fraction and saturation as well as brightness is row fraction
                            hsv[0] = (float) (column - 1) / (columns - 1) * 360; // [0,360)
                            hsv[1] = 0.3f * row_fraction + 0.7f; // saturation
                            hsv[2] = 0.8f * row_fraction + 0.2f;
                        }

                        // convert hsv to rgb
                        final int color = Color.HSVToColor(hsv);

                        // get gradient colors around given color
                        int[] gradientColors = ColoringUtils.colorSelectionButtonBackgroundGradient(color);

                        if (Build.VERSION.SDK_INT < 16) {
                            GradientDrawable newGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, gradientColors);
                            //noinspection deprecation
                            newGradientDrawable.setStroke(1, context.getResources().getColor(R.color.border_button));
                            //noinspection deprecation
                            view.setBackgroundDrawable(newGradientDrawable);
                        } else {
                            GradientDrawable drawable = (GradientDrawable) view.getBackground();
                            drawable.mutate();
                            drawable.setColors(gradientColors);
                        }

                        // for each button set the action to an outer "color selected" method with the color as parameter
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ColorPickerActivity.this.colorSelected(color);
                            }
                        });
                    }
                }
                // need to position all the color buttons
                layout.requestLayout();
            }
        });
    }

    /**
     * A new color has been selected, finish the activity and set the color as result.
     *
     * @param color
     */
    private void colorSelected(int color) {
        Intent data = new Intent();
        data.putExtra("color", color);
        setResult(RESULT_OK, data);
        finish();
    }
}