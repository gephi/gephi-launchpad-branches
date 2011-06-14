package org.gephi.newslider;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author daniel
 */
public class Newslider extends JFrame {
  public Newslider() {
    super("MultiKnobSlider prototype");  
    
    // JSlider slider = new JSlider();
    // slider.putClientProperty( "JSlider.isFilled", Boolean.TRUE );      
    
    MultiKnobSlider mSlider = new MultiKnobSlider(3);
    mSlider.setValueAt(25, 0);                        
    mSlider.setValueAt(50, 1); 
    mSlider.setValueAt(75, 2); 
    mSlider.setFillColorAt(Color.darkGray,  0); 
    mSlider.setTrackFillColor(Color.darkGray);
    mSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
    
    mSlider.setMajorTickSpacing(10);
    mSlider.setPaintTicks(true);
    // mSlider.setPaintTrack(false);
    mSlider.setPaintLabels(true);
    mSlider.setOpaque(false);

    mSlider.setPreferredSize(new Dimension(400,100));
    
    getContentPane().setLayout(new FlowLayout());
    // getContentPane().add(slider);
    getContentPane().add(mSlider);
  }

  public static void main (String args[]) {
    Newslider f = new Newslider();
    f.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	System.exit(0);
      }
    });
    f.setSize(600, 200);
    f.setVisible(true);

  }
}
