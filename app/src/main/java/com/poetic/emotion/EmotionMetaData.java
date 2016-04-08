/*
 * Copyright (c) 2016 名片项目组 All rights reserved.
 */

package com.poetic.emotion;



import android.os.Parcel;
import android.os.Parcelable;

/**
*<p> 表情元数据结构</p>
 * <p>Created by poetic on  2016/4/7 17:23 &nbsp;Email: <a href="mailto:dequanking@qq.com">dequanking@qq.com.</a></p>
 */
class EmotionMetaData implements Parcelable {
    /** 表情 unicode 描述文本 如: u1F629 [U+1F629] */
    private String unicodeStr;
    /** 对应的两个 32 位长度拼接成的 unicode , 如：U+ 1F629 对应  \ud83d\ude29 */
    private CharSequence unicode;
    /** 辅助字符 */
    private CharSequence assistUnicode;
    private boolean combination = false;
    private int value;
    /** 辅助值, 默认为 -1 */
    private int assistValue = -1;
    public EmotionMetaData(String unicodeStr) {
        // 去掉元素
        this.unicodeStr = unicodeStr;
        unicode = convert2Hex(unicodeStr);
        this.unicodeStr = this.unicodeStr.toLowerCase();
    }

    public static EmotionMetaData init(){
        return new EmotionMetaData("u1f004");
    }
    public int getValue(){
        return value;
    }
    public CharSequence getUnicode() {
        return unicode;
    }

    public String getUnicodeStr() {
        return unicodeStr;
    }


    /** 将字符串转换为 utf16 */
    private CharSequence convert2Hex(String hex){
        combination = false;
        if(hex == null)
            return null;
        if(hex.length() == 10){ // 处理数字键 u0023u20e3
            String temp = hex.substring(1,5); // 0023
            value = Integer.valueOf(temp, 16);
            assistValue = Integer.valueOf(hex.substring(6,10), 16);
            assistUnicode = new String(Character.toChars(assistValue));
            combination = true;
            return new String(Character.toChars(value));
        } else if(hex.length() == 12){ // 处理国旗 u1f1efu1f1f5
            String temp = hex.substring(1,6); // 0023
            value = Integer.valueOf(temp, 16);
            assistValue = Integer.valueOf(hex.substring(7,12), 16);
            assistUnicode = new String(Character.toChars(assistValue));
            combination = true;
            return new String(Character.toChars(value));
        }
        // 字符串是不包含 u的部分，如 U+1F623 中的 u1f623 中的 1f623 部分
        if(hex.charAt(0) == 'u'){
            hex = hex.substring(1, hex.length());
        }
        value = Integer.valueOf(hex,16);
        return new String(Character.toChars(value));
    }

    public int getAssistValue() {
        return assistValue;
    }

    public CharSequence getAssistUnicode() {
        return assistUnicode;
    }

    public boolean isCombination() {
        return combination;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(unicodeStr);
        dest.writeString(unicode.toString());// 可能出错哟
        dest.writeString(assistUnicode.toString());
        dest.writeInt(assistValue);
        dest.writeInt(value);
        if(combination)
            dest.writeInt(1);
        else
            dest.writeInt(0);
    }



    public EmotionMetaData(Parcel source) {
        unicodeStr = source.readString();
        unicode = source.readString();
        assistUnicode = source.readString();
        assistValue = source.readInt();
        value = source.readInt();
        combination = source.readInt() == 1;
    }

    public static Parcelable.Creator<EmotionMetaData> CREATOR = new Parcelable.Creator<EmotionMetaData>(){
        @Override
        public EmotionMetaData createFromParcel(Parcel source) {
            return new EmotionMetaData(source);
        }

        @Override
        public EmotionMetaData[] newArray(int size) {
            return new EmotionMetaData[size];
        }
    };

    @Override
    public String toString() {
        return "EmotionMetaData{" +
                "assistUnicode=" + assistUnicode +
                ", unicodeStr='" + unicodeStr + '\'' +
                ", unicode=" + unicode +
                ", combination=" + combination +
                ", value=" + value +
                ", assistValue=" + assistValue +
                '}';
    }
}

