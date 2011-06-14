package org.gephi.newslider;

import java.awt.*;


 // MultiKnobSliderAdditionalUI <--> BasicMultiKnobSliderUI
 //                          <--> MetalMultiKnobSliderUI
 //                          <--> MotifMultiKnobSliderUI
 //
public interface MultiKnobSliderAdditional {

  public Rectangle getTrackRect();
  
  public Dimension getThumbSize();
  
  public int xPositionForValue(int value);
  
  public int yPositionForValue(int value);
  
}

