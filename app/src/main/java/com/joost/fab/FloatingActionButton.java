package com.joost.fab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

import com.joost.smartplanner.R;

/**
 * Created by Joost on 27/12/2014.
 * TODO: Add reveal and hide animations
 */
public class FloatingActionButton extends ImageButton {

    private Context context;
    private int bgColor;
    private int bgColorPressed;

    public FloatingActionButton(Context context) {
        super(context);
        this.context = context;
        Log.d("Constructor", "een");
        init(null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        Log.d("Constructor", "twee");
        init(attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        Log.d("Constructor", "drie");
        init(attrs);
    }

    private void init(AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, 0, 0);

        try{
            bgColor = a.getColor(R.styleable.FloatingActionButton_bg_color, Color.GREEN);
            bgColorPressed = a.getColor(R.styleable.FloatingActionButton_bg_color_pressed, Color.LTGRAY);

            Log.d("Color", Integer.toString(bgColor));
            StateListDrawable sld = new StateListDrawable();
            sld.addState(new int[]{android.R.attr.state_pressed}, createButton(bgColorPressed));
            sld.addState(new int[]{}, createButton(bgColor));

            setBackground(sld);

        }catch(Throwable t){

        }finally {
            a.recycle();
        }
    }

    private Drawable createButton(int color){
        ShapeDrawable sd0 = new ShapeDrawable(new OvalShape());
        setWillNotDraw(false);
        sd0.getPaint().setColor(color);

        ShapeDrawable sd1 = new ShapeDrawable(new OvalShape());

        sd1.setShaderFactory(new ShapeDrawable.ShaderFactory(){
            @Override
            public Shader resize(int width, int height) {
                RadialGradient lg = new RadialGradient(width/2,height/2,(int)Math.round(width/2*1.0),
                        new int[]{
                                getResources().getColor(R.color.fab_shader_white),
                                getResources().getColor(R.color.fab_shader_gray),
                                getResources().getColor(R.color.fab_shader_dark_gray),
                                getResources().getColor(R.color.fab_shader_black),
                                getResources().getColor(R.color.fab_shader_black_invis)
                        },
                        new float[]{
                                0.1f,
                                0.2f,
                                0.4f,
                                0.5f,
                                1.0f
                        }, Shader.TileMode.REPEAT);
                return lg;
            }
        });

        LayerDrawable ld = new LayerDrawable(new Drawable[]{  sd0});
        ld.setLayerInset(0, 0, 0, 0, 0);
        //ld.setLayerInset(1, 0, 0, 5, 10);

        return ld;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getBgColorPressed() {
        return bgColorPressed;
    }

    public void setBgColorPressed(int bgColorPressed) {
        this.bgColorPressed = bgColorPressed;
    }
}
