/*
 * Copyright (c) 2016 名片项目组 All rights reserved.
 */

package com.poetic.emotion;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.lang.reflect.Field;

/**
 * <p>Created by poetic on  2016/4/8 14:51 &nbsp;Email: <a href="mailto:dequanking@qq.com">dequanking@qq.com.</a></p>
 */
public class EmotionUtil {
    public static  String getStringByName(String name, Context context){
        try {
            Field field = Class.forName("com.poetic.emotion.R$string").getField(name);
            int drawableRes = field.getInt(field);
            return context.getString(drawableRes);
        } catch (Resources.NotFoundException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (NoSuchFieldException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable getDrawableByName(String name, Context context){
        try {
            Field field = Class.forName("com.poetic.emotion.R$drawable").getField(name);
            int drawableRes = field.getInt(field);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return context.getDrawable(drawableRes);
            } else {
                return context.getResources().getDrawable(drawableRes);
            }
        } catch (Resources.NotFoundException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (NoSuchFieldException e){
            e.printStackTrace();
        }
        return null;
    }

}
