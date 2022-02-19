package com.texturegarden.gui.canvas;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageObserver;

import com.texturegarden.TG;
import com.texturegarden.gui.*;
import com.texturegarden.messages.*;

public class TTCanvas extends Canvas implements MouseListener, MouseMotionListener {
  static Image offScrImage;

  static Graphics offscreen_graphics;

  static ImageObserver observer;
  static Canvas canvas;

  static int modifiers;

  static int xoff = 4;
  static int yoff = 4;

  static int img_x;
  static int img_y;

  static Dimension dim;

  public static int x_pixels;
  public static int y_pixels;

  static int gsx = 444;
  static int gsy = 444;

  static int gsx_old = -1;
  static int gsy_old = -1;

  static int old_gridsize = -99;
  static int new_gridsize;

  //static int current_mouse_x;
  //static int current_mouse_y;

  static Point current_mouse = new Point();
  static Point last_mouse = new Point();

  static boolean button_pressed;

  static long time_of_last_button_release;
  static long time_of_last_button_press;
  static int x_of_last_button_release;
  static int y_of_last_button_release;

  TG applet;

  TTCanvas(TG applet) {
    this.applet = applet;
  }

  // methods
  final public void start_up() {
    if (gsx < 1) {
      gsx = 511;
    }
    if (gsy < 1) {
      gsy = 511;
    }

    img_x = gsx;
    img_y = gsy;
  }

  static void nullifyOffScreen() {
    offscreen_graphics = null;
    offScrImage = null;
  }

  final public static void forceResize() {
    gsx_old = -1;
  }

  // draw
  public void update(Graphics g) {
    applet.render.update(g);
  }

  public void paint(Graphics g) {
    applet.render.updateAll(g);
  }

  // mouse motion...
  public void mouseReleased(MouseEvent e) {
    button_pressed = false;
    time_of_last_button_release = System.currentTimeMillis();
    x_of_last_button_release = e.getX();
    y_of_last_button_release = e.getY();

    if (applet.drag_and_drop.dragging) {
      applet.drag_and_drop.end(x_of_last_button_release, y_of_last_button_release);
    }
  }

  public void mousePressed(MouseEvent e) {
    modifiers = e.getModifiers();

    current_mouse.x = e.getX();
    current_mouse.y = e.getY();

    long temp = System.currentTimeMillis();

    boolean dc = false;

    if ((temp - time_of_last_button_release) < 200) {
      int _dx = current_mouse.x - x_of_last_button_release;
      int _dy = current_mouse.y - y_of_last_button_release;

      if (((_dx * _dx) + (_dy * _dy)) < 100) {
        applet.message.send(Message.MOUSE_DOUBLE_CLICK, new MouseActionData(current_mouse.x, current_mouse.y, modifiers));
        dc = true;
      }
    }

    if (!dc) {
      applet.message.send(Message.MOUSE_PRESS, new MouseActionData(current_mouse.x, current_mouse.y, modifiers));
    }

    // TextureGarden.texture_array.action(current_mouse.x, current_mouse.y, modifiers);
    // button_pressed = true;
    // }
  }

  public void mouseDragged(MouseEvent e) {
    modifiers = e.getModifiers();
    int _x = e.getX();
    int _y = e.getY();
    applet.message.send(Message.MOUSE_DRAG, new MouseMovementData(current_mouse.x, current_mouse.y, _x, _y, modifiers));
    current_mouse.x = _x;
    current_mouse.y = _y;
  }

  public void mouseClicked(MouseEvent e) {
    //modifiers = e.getModifiers();
    //current_mouse.x = e.getX();
    //current_mouse.y = e.getY();

    //Message.send(Message.MOUSE_CLICK, new TTMouseAction(current_mouse.x, current_mouse.y, modifiers));
    // TextureGarden.texture_array.action(current_mouse.x, current_mouse.y, modifiers);
  }

  public void mouseEntered(MouseEvent e) {
    // cursorCross();
  }

  public void mouseExited(MouseEvent e) {
    // cursorCross();
  }

  public void mouseMoved(MouseEvent e) {
    modifiers = e.getModifiers();
    int _x = e.getX();
    int _y = e.getY();
    //Message.send(Message.MOUSE_DRAG, new TTMouseMovement(current_mouse.x, current_mouse.y, _x, _y, modifiers);
    current_mouse.x = _x;
    current_mouse.y = _y;

    if (modifiers == 0) {
      if (applet.drag_and_drop.dragging) {
        applet.drag_and_drop.end(x_of_last_button_release, y_of_last_button_release);
      }
    }
  }
}
