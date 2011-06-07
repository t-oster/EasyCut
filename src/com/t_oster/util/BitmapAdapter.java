/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.t_oster.liblasercut.GreyscaleRaster;

/**
 *
 * @author thommy
 */
public class BitmapAdapter implements GreyscaleRaster
{

  private Bitmap src;

  public BitmapAdapter(Bitmap src)
  {
    this.src = src;
  }

  public Byte getGreyScale(int x, int line)
  {
    int col = this.src.getPixel(x, line);
    return ((byte) ((0.3 * Color.red(col) + 0.59 * Color.green(col) + 0.11 * Color.blue(col)) / 3));
  }

  public void setGreyScale(int x, int y, Byte grey)
  {
    int col = Color.rgb(grey, grey, grey);
    this.src.setPixel(x, y, col);
  }

  public int getWidth()
  {
    return this.src.getWidth();
  }

  public int getHeight()
  {
    return this.src.getHeight();
  }
}
