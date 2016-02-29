package com.goldadorn.main.icons;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.io.Closeable;
import java.io.IOException;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;

/**
 * Created by bhavinpadhiyar on 7/15/15.
 */
public class IconsUtils
{
    public static void applyToMenuItem(Activity activity,MenuItem menuItem, IIcon icon,int colorRes)
    {
        int color = activity.getResources().getColor(colorRes);
        Drawable iconDrawable = new IconicsDrawable(activity,icon).color(color).actionBar();
        menuItem.setIcon(iconDrawable);
    }
    public static Drawable getFontIconDrawable(Activity activity,IIcon icon,int colorRes,int size)
    {
        int color = activity.getResources().getColor(colorRes);
        Drawable iconDrawable = new IconicsDrawable(activity,icon).color(color).sizeDp(size);
        return iconDrawable;
    }
    public static Drawable getFontIconDrawable(Activity activity,String icon,int colorRes,int size)
    {
        int color = activity.getResources().getColor(colorRes);
        Drawable iconDrawable = new IconicsDrawable(activity,icon).color(color).sizeDp(size);
        return iconDrawable;
    }
    public static String getFormattedName(IIcon icon)
    {
        return icon.getFormattedName().replaceFirst("_", "-");
    }

    public static String getFormattedName(String icon)
    {
        if(icon==null)
            return "";
        return icon.replaceFirst("_", "-");
    }



    static int convertDpToPx(Context context, float dp) {
        return (int) applyDimension(COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    static boolean isEnabled(int[] stateSet) {
        for (int state : stateSet)
            if (state == android.R.attr.state_enabled)
                return true;
        return false;
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // Don't care
            }
        }
    }

    public static void applyToButtonLeft(Activity activity,Button button, IIcon icon,int colorRes,int size)
    {
        Drawable iconDrawable = getFontIconDrawable(activity,icon,colorRes,size);
        button.setCompoundDrawables(iconDrawable,null,null,null);
    }
    public static void applyToButtonRight(Activity activity,Button button, IIcon icon,int colorRes,int size)
    {
        Drawable iconDrawable = getFontIconDrawable(activity, icon, colorRes, size);
        button.setCompoundDrawables(null,null,iconDrawable,null);
    }
    public static void applyToButtonTop(Activity activity,Button button, IIcon icon,int colorRes,int size)
    {
        Drawable iconDrawable = getFontIconDrawable(activity, icon, colorRes, size);
        button.setCompoundDrawables(null,iconDrawable,null,null);
    }
    public static void applyToButtonBottom(Activity activity,Button button, IIcon icon,int colorRes,int size)
    {
        Drawable iconDrawable = getFontIconDrawable(activity, icon, colorRes, size);
        button.setCompoundDrawables(null,null,null,iconDrawable);
    }
    public static void applyToImageButton(Activity activity,ImageButton button, IIcon icon,int colorRes,int size)
    {
        Drawable iconDrawable = getFontIconDrawable(activity, icon, colorRes, size);
        button.setImageDrawable(iconDrawable);
    }

    public static Bitmap getFontIconBitmap(Activity activity,IIcon icon,int colorRes,int size)
    {
        int color = activity.getResources().getColor(colorRes);
        Drawable iconDrawable = new IconicsDrawable(activity,icon).color(color).sizeDp(size);
        return drawableToBitmap(iconDrawable);
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}
