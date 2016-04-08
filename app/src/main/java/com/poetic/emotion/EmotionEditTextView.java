/*
 * Copyright (c) 2016 名片项目组 All rights reserved.
 */

package com.poetic.emotion;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>自定义表情输入框</p>
 * <p>Created by poetic on  2016/4/7 17:14 &nbsp;Email: <a href="mailto:dequanking@qq.com">dequanking@qq.com.</a></p>
 */
public class EmotionEditTextView extends EditText implements TextWatcher {

    private EmotionMetaData preview = EmotionMetaData.init(); // 出示第一项随机给一个不是组合的值

    private int textSize = (int) getTextSize();

    public EmotionEditTextView(Context context) {
        super(context);
        addTextChangedListener(this);
    }

    public EmotionEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(this);
    }

    public EmotionEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(this);
        setTextIsSelectable(true);
    }
    private boolean isChanged = false;
    private int cursor = 0;
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(isChanged)
            return;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){

    }
    @Override
    public void afterTextChanged(Editable s) {
        cursor = getSelectionEnd();
        post(new Runnable() {
            @Override
            public void run() {
                doParser();
            }
        });

    }

    private void doParser(){
        removeTextChangedListener(this);
        isChanged = true;
        setTextString(getText());
        isChanged = false;
        setSelection(cursor);
        addTextChangedListener(this);
    }
    public void setTextString(CharSequence text){
        //Log.d("setTextString", "text:" + text.toString());
        if(text.length() ==0)
            return;
        textSize = (int) getTextSize();// 需要在此初始化文本大小
        Map<Integer, EmotionMetaData> map = EmotionData.getMap();
        if(map == null)
            return;
        int k = 0;
        for (int i = 0; i < text.length(); ) {
            int codePoint = Character.codePointAt(text, i);
            i += Character.charCount(codePoint);
            if(map.containsKey(codePoint) || preview.isCombination()){
                EmotionMetaData meta = map.get(codePoint);
                init(meta, codePoint,text.subSequence(k,i).toString(), text.toString());
            } else {
                preview = EmotionMetaData.init();
            }
            k = i;
        }

    }

    /** 在此设置 {@link #setText(CharSequence, BufferType)} */
    void init(EmotionMetaData meta, int codePoint, String regex, String text){
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
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(text);
        while (match.find()) {
            SpannableStringBuilder spannableString = new SpannableStringBuilder(getText());
            Drawable drawable = null;
            drawable = EmotionUtil.getDrawableByName(unicodeStr, getContext());

            if (drawable == null) {
                return;
            }
            drawable.setBounds(0, 0, textSize, textSize);
            ImageSpan span = new ImageSpan(drawable);
            spannableString.setSpan(span, match.start(), match.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(spannableString, TextView.BufferType.SPANNABLE);
        }
    }

    /** 获取当前输入框中的字符 */
    public final String getTextString(){
        return turnUnicode(super.getText().toString());
    }
    /**
     * 把可能替换的字符串提取出来
     */
    private String turnUnicode(String text){
        ArrayList<String> items = new ArrayList<>();
        int j =0;// 用于存储 '[' 开始的位置
        for(int i=0; i<text.length();i++){
            if(text.charAt(i) == '['){
                j=i;
            }
            if(text.charAt(i) == ']'){
                items.add(text.substring(j,i+1));
            }
        }
        text=findUnicode(items,text);
        return text;
    }
    /**
     * 找出需要替换的
     */
    private String findUnicode(ArrayList<String> list,String content){

        Map<CharSequence,Integer> firstMap = EmotionData.getFirstResNameMap();
        Map<CharSequence,Integer> secondMap = EmotionData.getSecondResNameMap();
        Map<CharSequence,Integer> thirdMap = EmotionData.getThirdResNameMap();
        Map<CharSequence,Integer> fourthMap = EmotionData.getFourthResNameMap();
        ArrayList<CharSequence> unicodeList = new ArrayList<>();
        for(int i=0;i<list.size();i++) {
            if(!replaceUnicode(list.get(i), firstMap, unicodeList,content).equals(content)) {
                content = replaceUnicode(list.get(i), firstMap, unicodeList,content);
            }else if(!replaceUnicode(list.get(i),secondMap,unicodeList,content).equals(content)) {
                content = replaceUnicode(list.get(i),secondMap,unicodeList,content);
            }else if(!replaceUnicode(list.get(i),thirdMap,unicodeList,content).equals(content)) {
                content = replaceUnicode(list.get(i),thirdMap,unicodeList,content);
            }else if(!replaceUnicode(list.get(i), fourthMap, unicodeList, content).equals(content)) {
                content = replaceUnicode(list.get(i), fourthMap, unicodeList, content);
            }
        }
        return content;
    }
    /**
     * 需要替换的替换成unicode
     */
    private  String replaceUnicode(String substringItem, Map<CharSequence,Integer> map, ArrayList<CharSequence> unicodeList,String content){
        for (CharSequence unicode : map.keySet()) {
            unicodeList.add(unicode);
        }
        for (int y = 0; y < unicodeList.size(); y++) {
            if (substringItem.equals("[/" + getString(map.get(unicodeList.get(y))) + "]")) {
                content=content.replace(substringItem,unicodeList.get(y));
                unicodeList.clear();
                return content;
            }
        }
        unicodeList.clear();
        return content;
    }
    /**
     * 将转换后的 unicode 转换为我们规定好的字符串
     * @param content
     */
    private void unicodeBackToSpecifiedString(String content){
        for(int i=0;i<content.length();i++){
            if(content.charAt(i) == '\ud83d'||content.charAt(i)== '\ud83c'){
                append(findSpecifiedString(content.substring(i, i + 2)));
                i++;
            }else{
                append(content.charAt(i) + "");
            }
        }
    }
    /**
     * 找到对应的字符串
     * @param unicode
     * @return
     */
    private CharSequence findSpecifiedString(String unicode){
        Map<CharSequence,Integer> firstMap= EmotionData.getFirstResNameMap();
        Map<CharSequence,Integer> secondMap= EmotionData.getSecondResNameMap();
        Map<CharSequence,Integer> thirdMap= EmotionData.getThirdResNameMap();
        Map<CharSequence,Integer> fourthMap= EmotionData.getFourthResNameMap();
        for(CharSequence coolEmotionKeySet:firstMap.keySet()){
            if(coolEmotionKeySet.equals(unicode)){
                return "[/"+getString(firstMap.get(unicode))+"]";
            }
        }
        for(CharSequence normalEmotionKeySet:secondMap.keySet()){
            if(normalEmotionKeySet.equals(unicode)){
                return "[/"+getString(secondMap.get(unicode))+"]";
            }
        }
        for(CharSequence normalEmotionKeySet:thirdMap.keySet()){
            if(normalEmotionKeySet.equals(unicode)){
                return "[/"+getString(thirdMap.get(unicode))+"]";
            }
        }
        for(CharSequence normalEmotionKeySet:fourthMap.keySet()){
            if(normalEmotionKeySet.equals(unicode)){
                return "[/"+getString(fourthMap.get(unicode))+"]";
            }
        }
        return unicode;
    }

    private String getString(int res){
        return getContext().getString(res);
    }
}

