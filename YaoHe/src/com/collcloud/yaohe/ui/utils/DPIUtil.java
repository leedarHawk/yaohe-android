package com.collcloud.yaohe.ui.utils;


import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class DPIUtil
{
  private static Display defaultDisplay;
  private static float mDensity = 160.0F;

  public static int dip2px(float paramFloat)
  {
    return (int)(0.5F + paramFloat * mDensity);
  }

  public static Display getDefaultDisplay(Context context)
  {
    if (defaultDisplay == null)
      defaultDisplay = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
    return defaultDisplay;
  }

  public static float getDensity()
  {
    return mDensity;
  }

  public static int getHeight(Context context)
  {
    return getDefaultDisplay(context).getHeight();
  }

  public static int getWidth(Context context)
  {
    return getDefaultDisplay(context).getWidth();
  }

  public static int px2dip(Context paramContext, float paramFloat)
  {
    return (int)(0.5F + paramFloat / mDensity);
  }

  public static int px2sp(Context paramContext, float paramFloat)
  {
    return (int)(0.5F + paramFloat / paramContext.getResources().getDisplayMetrics().scaledDensity);
  }

  public static void setDensity(float paramFloat)
  {
    mDensity = paramFloat;
  }
}

/** Qualified Name:     com.jingdong.common.utils.DPIUtil
 * JD-Core Version:    0.6.2
 */
