//
// TTAppletFrame - Tim Tyler 1998-2001.
//

package com.texturegarden.utilities;
import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AppletFrame extends Frame implements ActionListener {
  static Applet applet;
  static AppletFrame myself;
  // final static String QUIT = "Quit";

  public AppletFrame(String title, Applet app, int width, int height) {
    super(title);
    applet = app;
    myself = this;

    /*
    MenuBar menubar = new MenuBar();
    Menu file = new Menu("File",true);
    
    menubar.add(file);
    
    file.add(QUIT);
    
    setMenuBar(menubar);
    
    file.addActionListener(this);
    */

    add("Center", applet);
    setSize(new Dimension(width, height));

    show();

    applet.init();
    applet.start();

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }

      public void windowDeiconified(WindowEvent e) {}

      public void windowIconified(WindowEvent e) {}
    });
  }

  public void actionPerformed(ActionEvent e) {
    /*
       String arg = e.getActionCommand();
    
       if (arg == QUIT) {
          applet.stop();
          applet = null;
          System.exit(0);
       }
    */
  }
}
