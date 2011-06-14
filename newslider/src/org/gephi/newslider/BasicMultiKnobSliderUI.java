package org.gephi.newslider;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


public class BasicMultiKnobSliderUI extends BasicSliderUI
                implements MultiKnobSliderAdditional {

  MultiKnobSliderAdditionalUI additonalUi;
  MouseInputAdapter mThumbTrackListener;
  
  
  public static ComponentUI createUI(JComponent c)    {
    return new BasicMultiKnobSliderUI((JSlider)c);
  }

  
  public BasicMultiKnobSliderUI()   {
    super(null);
  }
  
  public BasicMultiKnobSliderUI(JSlider b)   {
    super(b);
  }

  
    @Override
  public void installUI(JComponent c)   {
    additonalUi = new MultiKnobSliderAdditionalUI(this);
    additonalUi.installUI(c);
    mThumbTrackListener = createMThumbTrackListener((JSlider) c);
    super.installUI(c);
  }
  
    @Override
  public void uninstallUI(JComponent c) {
    super.uninstallUI(c);
    additonalUi.uninstallUI(c);
    additonalUi = null;
    mThumbTrackListener = null;
  }
  
  protected MouseInputAdapter createMThumbTrackListener( JSlider slider ) {
    return additonalUi.trackListener;
  }
    
    @Override
  protected TrackListener createTrackListener( JSlider slider ) {
    return null;
  }
  
    @Override
  protected ChangeListener createChangeListener( JSlider slider ) {
    return additonalUi.changeHandler;
  }

    @Override
  protected void installListeners( JSlider slider ) {
    slider.addMouseListener(mThumbTrackListener);
    slider.addMouseMotionListener(mThumbTrackListener);
    slider.addFocusListener(focusListener);
    slider.addComponentListener(componentListener);
    slider.addPropertyChangeListener( propertyChangeListener );
    slider.getModel().addChangeListener(changeListener);
  }

    @Override
  protected void uninstallListeners( JSlider slider ) {
    slider.removeMouseListener(mThumbTrackListener);
    slider.removeMouseMotionListener(mThumbTrackListener);
    slider.removeFocusListener(focusListener);
    slider.removeComponentListener(componentListener);
    slider.removePropertyChangeListener( propertyChangeListener );
    slider.getModel().removeChangeListener(changeListener);
  }

    @Override
  protected void calculateGeometry() {
    super.calculateGeometry();
    additonalUi.calculateThumbsSize();
    additonalUi.calculateThumbsLocation();
  }

  
  
    @Override
  protected void calculateThumbLocation() {}
  
    
    
  
  Rectangle zeroRect = new Rectangle();
  
    @Override
  public void paint( Graphics g, JComponent c ) {
    
    Rectangle clip = g.getClipBounds();
    thumbRect = zeroRect;
    
    super.paint( g, c );
    
    int thumbNum = additonalUi.getThumbNum();
    Rectangle[] thumbRects = additonalUi.getThumbRects();
    
    for (int i=thumbNum-1; 0<=i; i--) {
      if ( clip.intersects( thumbRects[i] ) ) {
        thumbRect = thumbRects[i];
        
        paintThumb( g );
        
      }
    }   
  }

  
    @Override
  public void scrollByBlock(int direction)    {}
    @Override
  public void scrollByUnit(int direction) {}
  
  //
  // MultiKnobSliderAdditional
  //
    @Override
  public Rectangle getTrackRect() {
    return trackRect;
  }
  
    @Override
  public Dimension getThumbSize() {
    return super.getThumbSize();
  }
  
    @Override
  public int xPositionForValue(int value) {
    return super.xPositionForValue( value);
  }
    @Override
  public int yPositionForValue(int value) {
    return super.yPositionForValue( value);
  }
  
}

