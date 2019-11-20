/*created on 2019-10-21
 Author: Marcin Bartosiak */
package com.mcompany;

// CLASS MyJFrame

import javax.swing.JFrame;

public class MyJFrame extends JFrame {


    public MyJFrame() {
//        setSize(370,520);
//        setTitle("Moja aplikacja");
//        setResizable(false);
//
//        MyMainJPanel myMainJPanel = new MyMainJPanel();
//        add(myMainJPanel);
        setSize(227,292);
        setTitle("Moja aplikacja");
        setResizable(false);

        KalkulatorJPanel kalkulatorJPanel = new KalkulatorJPanel();
        add(kalkulatorJPanel);
    }
}