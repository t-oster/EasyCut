/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.easycut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 *
 * @author thommy
 */
public class SelectRectView extends ImageView{

    //seleced area in %
    private double x = 15;
    private double y = 15;
    private double width = 65;
    private double height = 65;

    public SelectRectView(Context c) {
        super(c);
    }

    public double getSelectedX() {
        return x;
    }

    public double getSelectedY() {
        return y;
    }

    public double getSelectedWidth() {
        return width;
    }

    public double getSelectedHeight() {
        return height;
    }
    /**
     * indicates wether we're moving
     * or resizing the selected area
     */
    private boolean moving = true;
    private float startx = 0;
    private float starty = 0;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int w = this.getWidth();
        int h = this.getHeight();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startx = e.getX();
                starty = e.getY();
                if (startx + 10 > (this.x + this.width) * w / 100 && starty + 10 > (this.y + this.height) * h / 100) {
                    moving = false;
                } else {
                    moving = true;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float diffx = e.getX()-startx;
                float diffy = e.getY()-starty;
                if (moving) {
                    this.x += (100 * diffx / w);
                    this.y += (100 * diffy / h);
                } else {
                    this.width += (100 * diffx / w);
                    this.height += (100 * diffy / h);
                }
                startx = e.getX();
                starty = e.getY();
                this.invalidate();
                break;
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        int w = c.getWidth();
        int h = c.getHeight();
        Paint p = new Paint();
        p.setStrokeWidth(3);
        p.setColor(Color.YELLOW);
        p.setStyle(Paint.Style.STROKE);
        c.drawRect((int) (x * w / 100), (int) (y * h / 100),
                (int) ((x+width) * w / 100), (int) ((y+height) * h / 100), p);
    }

}
