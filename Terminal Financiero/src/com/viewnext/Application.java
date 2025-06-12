package com.viewnext;

import com.viewnext.frames.HomeFrame;
import com.viewnext.tools.Encrypter;


public class Application {
    public static void main(String[] args) {
        Encrypter.loadKey("/com/viewnext/resources/key.txt"); //Carga la contraseña
        new HomeFrame();
    }
}