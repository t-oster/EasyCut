/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.easycut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import com.t_oster.liblasercut.BlackWhiteRaster;

/**
 *
 * @author thommy
 */
public class PreView extends View
{

  private BlackWhiteRaster bw;

  public PreView(Context co, BlackWhiteRaster bw)
  {
    super(co);
    this.bw = bw;
    this.invalidate();
  }

  @Override
  public void onDraw(Canvas c)
  {
    Paint blk = new Paint();
    blk.setColor(Color.BLACK);
    blk.setStrokeWidth(1);
    Paint wht = new Paint();
    wht.setColor(Color.WHITE);
    wht.setStrokeWidth(1);
    if (this.bw != null)
    {
      for (int x = 0; x < c.getWidth(); x++)
      {
        for (int y = 0; y < c.getHeight(); y++)
        {
          c.drawPoint(x, y, bw.isBlack(x, y) ? blk : wht);
        }
      }
    }
  }
}
