/*
 * Copyright (c) 2016 名片项目组 All rights reserved.
 */

package com.poetic.emotion;



import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* <p>支持表情的 EmotionTextView</p>
 * <p>Created by poetic on  2016/4/7 17:13 &nbsp;Email: <a href="mailto:dequanking@qq.com">dequanking@qq.com.</a></p>
 */
public class EmotionTextView extends TextView {
    public EmotionTextView(Context context) {
        super(context);
        setTextIsSelectable(true);
    }

    public EmotionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextIsSelectable(true);
    }

    public EmotionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextIsSelectable(true);
    }

    public void setTextString(CharSequence text){
        setText("非常好诶");
        textSize = (int) getTextSize();// 需要在此初始化文本大小
        EmotionMetaData data;
        Map<Integer, EmotionMetaData> map = EmotionData.getMap();
        if(map == null)
            return;

        int j =0;
        for(Integer integer : map.keySet()){
            if(j >= 10)
                break;
            data = map.get(integer);
            append(data.getUnicode());
            append("你好");
            j++;
        }


        String toConvert = getText().toString();
        int k = 0;
        for (int i = 0; i < toConvert.length(); ) {
            int codePoint = Character.codePointAt(toConvert, i);
            i += Character.charCount(codePoint);
            if(map.containsKey(codePoint) || preview.isCombination()){
                EmotionMetaData meta = map.get(codePoint);
                init(meta, codePoint,toConvert.substring(k,i));
            } else {
                preview = EmotionMetaData.init();
            }
            k = i;
        }

    }
    private EmotionMetaData preview = EmotionMetaData.init(); // 出示第一项随机给一个不是组合的值
    private int textSize = (int) getTextSize();
    public void setTextString(int resId){
        setText(resId);
        //init();
    }


    public CharSequence getTextCharSequence(){
        return null;
    }

    /** 在此设置 {@link #setText(CharSequence, BufferType)} */
    void init(EmotionMetaData meta, int codePoint, String regex){
        String unicodeStr = "";
        if(preview.isCombination() && preview.getAssistValue() == codePoint){
            // 如果前一项为组合值， 那么就要判断当前值是否等于前一项的辅助值 ,此时需要组合合并的值
            regex = preview.getUnicode().toString() + preview.getAssistUnicode();
            unicodeStr = preview.getUnicodeStr();
            preview = EmotionMetaData.init();
        } else if(meta != null && meta.isCombination()){
            preview = meta;
            unicodeStr = meta.getUnicodeStr();
        } else if(meta != null){
            unicodeStr = meta.getUnicodeStr();
        }
        String str1 = getText().toString();
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str1);
        while (match.find()) {
            SpannableStringBuilder spannableString = new SpannableStringBuilder(getText());
            Drawable drawable = null;
            drawable = EmotionUtil.getDrawableByName(unicodeStr, getContext());

            if (drawable == null) {
                //Log.d("EmotionTextView", "name is:" + unicodeStr);
                return;
            }
            drawable.setBounds(0, 0, textSize, textSize);
            ImageSpan span = new ImageSpan(drawable);
            spannableString.setSpan(span, match.start(), match.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(spannableString, TextView.BufferType.SPANNABLE);
        }
    }



}
