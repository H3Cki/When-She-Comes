package com.example.whenshecomes;

import java.util.Vector;

public class KeyboardControls {
    static boolean isKeyboardShowing = false;
    static Vector<Integer> keys = new Vector();
    static Vector keyChars = new Vector();
    static String getKeyArgs(){
        String args = "";
        for(int i = 0; i < keys.size(); i++){
            args += keys.get(i);
            if (i < keys.size()-1){
                args += "|";
            }
        }
        return args;
    }
    static void clearKeys(){
        keys = new Vector();
        keyChars = new Vector();
    }
    static String getKeysAsString(){
        String args = "";
        for(int i = 0; i < keyChars.size(); i++) {
            args += keyChars.get(i);
        }
        return args;
    }
}
