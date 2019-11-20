package com.mcompany;

// CLASS MAIN


import java.awt.EventQueue;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        System.out.println(0+'!');
        System.out.println(0+'_');
        System.out.println(0+'#');


        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame window = new MyJFrame();
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setVisible(true);
            }
        });


    }

}



