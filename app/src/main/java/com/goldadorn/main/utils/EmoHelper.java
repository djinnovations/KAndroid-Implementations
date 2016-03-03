package com.goldadorn.main.utils;

import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.ITypeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 3/3/16.
 */
public class EmoHelper {
    public static Map<String,Integer> emoMap;
    public static String getEmoCorrectedText(String value)
    {
        if(emoMap==null) {
            emoMap = new HashMap<>();
            fillData(emoMap);
        }
        for (Map.Entry<String, Integer> entry : emoMap.entrySet()) {
            try {
                value = value.replaceAll(entry.getKey(),new String(Character.toChars(entry.getValue())));
            }catch (Exception e)
            {
                System.out.println(e);
            }

        }
        return value+" ";
    }

    private static void fillData(Map<String, Integer> emoMap) {
        emoMap.put(":)",0x1F60A);
        emoMap.put("-<@%",0x1F41D);
        emoMap.put(":(|)",0x1F435);
        emoMap.put(":(:)",0x1F437);
        emoMap.put("(]:{",0x1F473);
        emoMap.put("<\3",0x1F494);
        emoMap.put("</3",0x1F494);
        emoMap.put("<3",0x1F49C);
        emoMap.put("~@~",0x1F4A9);
        emoMap.put(":D",0x1F600);
        emoMap.put(":-D",0x1F600);
        emoMap.put("^_^",0x1F601);
        emoMap.put(":)",0x1F603);
        emoMap.put(":-)",0x1F603);
        emoMap.put("=)",0x1F603);
        emoMap.put("=D",0x1F604);
        emoMap.put("^_^;;",0x1F605);
        emoMap.put("O:)",0x1F607);
        emoMap.put("O:-)",0x1F607);
        emoMap.put("O=)",0x1F607);
        emoMap.put("}:)",0x1F608);
        emoMap.put("}:-)",0x1F608);
        emoMap.put("}=)",0x1F608);
        emoMap.put(";)",0x1F609);
        emoMap.put(";-)",0x1F609);
        emoMap.put("B)",0x1F60E);
        emoMap.put("B-)",0x1F60E);
        emoMap.put(":-|",0x1F610);
        emoMap.put(":|",0x1F610);
        emoMap.put("=|",0x1F610);
        emoMap.put("-_-",0x1F611);
        emoMap.put("o_o;",0x1F613);
        emoMap.put("u_u",0x1F614);
        emoMap.put(":/",0x1F615);
        emoMap.put(":-/",0x1F615);
        emoMap.put("=/",0x1F615);
        emoMap.put(":S",0x1F616);
        emoMap.put(":-S",0x1F616);
        emoMap.put(":s",0x1F616);
        emoMap.put(":-s",0x1F616);
        emoMap.put(":*",0x1F617);
        emoMap.put(":-*",0x1F617);
        emoMap.put(";*",0x1F618);
        emoMap.put(";-*",0x1F618);
        emoMap.put(":P",0x1F61B);
        emoMap.put(":-P",0x1F61B);
        emoMap.put("=P",0x1F61B);
        emoMap.put(":p",0x1F61B);
        emoMap.put(":-p",0x1F61B);
        emoMap.put("=p",0x1F61B);
        emoMap.put(";P",0x1F61C);
        emoMap.put(";-P",0x1F61C);
        emoMap.put(";p",0x1F61C);
        emoMap.put(";-p",0x1F61C);
        emoMap.put(":(",0x1F61E);
        emoMap.put(":-(",0x1F61E);
        emoMap.put("=(",0x1F61E);
        emoMap.put("T_T",0x1F622);
        emoMap.put(":'(",0x1F622);
        emoMap.put(";_;",0x1F622);
        emoMap.put("='(",0x1F622);
        emoMap.put(">_<",0x1F623);
        emoMap.put("D:",0x1F626);
        emoMap.put("o.o",0x1F62E);
        emoMap.put(":o",0x1F62E);
        emoMap.put(":-o",0x1F62E);
        emoMap.put("=o",0x1F62E);
        emoMap.put("O.O",0x1F632);
        emoMap.put(":O",0x1F632);
        emoMap.put(":-O",0x1F632);
        emoMap.put("=O",0x1F632);
        emoMap.put("x_x",0x1F635);
        emoMap.put("X-O",0x1F635);
        emoMap.put("X-o",0x1F635);
        emoMap.put("X(",0x1F635);
        emoMap.put("X-(",0x1F635);
        emoMap.put(":X)",0x1F638);
        emoMap.put("(=^..^=)",0x1F638);
        emoMap.put("(=^.^=)",0x1F638);
        emoMap.put("=^_^=",0x1F638);
    }
}
