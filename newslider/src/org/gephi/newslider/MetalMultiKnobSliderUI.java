package org.gephi.newslider;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;


public class MetalMultiKnobSliderUI extends MetalSliderUI 
  implements MultiKnobSliderAdditional {

  MultiKnobSliderAdditionalUI additonalUi;
  MouseInputAdapter mThumbTrackListener;
  
  public static ComponentUI createUI(JComponent c)    {
    return new MetalMultiKnobSliderUI((JSlider)c);
  }

  
  public MetalMultiKnobSliderUI()   {
    //super(null);
  }
  
  public MetalMultiKnobSliderUI(JSlider b)   {
    //super(null);
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
    
  
    
  
  Icon thumbRenderer;
  
    @Override
  public void paint( Graphics g, JComponent c ) {
    Rectangle clip = g.getClipBounds();
    Rectangle[] thumbRects = additonalUi.getThumbRects();
    thumbRect = thumbRects[0];    
    int thumbNum = additonalUi.getThumbNum();
    
    if ( slider.getPaintTrack() && clip.intersects( trackRect ) ) {
      boolean filledSlider_tmp = filledSlider;
      filledSlider = false;
      paintTrack( g );
      filledSlider = filledSlider_tmp;
      
      if ( filledSlider ) {
        g.translate(  trackRect.x,  trackRect.y );
        
        Point t1 = new Point(0,0);
        Point t2 = new Point(0,0);
        Rectangle maxThumbRect = new Rectangle(thumbRect);
        thumbRect = maxThumbRect;
        
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
          t2.y = (trackRect.height - 1) - getThumbOverhang();
          t1.y = t2.y - (getTrackWidth() - 1);
          t2.x = trackRect.width - 1;
          int maxPosition = xPositionForValue(slider.getMaximum());
	  thumbRect.x = maxPosition - (thumbRect.width / 2) -2;
	  thumbRect.y = trackRect.y;
        }
        else {
          t1.x = (trackRect.width - getThumbOverhang()) - getTrackWidth();
          t2.x = (trackRect.width - getThumbOverhang()) - 1;
          t2.y = trackRect.height - 1;
          int maxPosition = yPositionForValue(slider.getMaximum());
	  thumbRect.x = trackRect.x;
	  thumbRect.y = maxPosition - (thumbRect.height / 2) -2;
        }   
        
        Color fillColor = ((MultiKnobSlider)slider).getTrackFillColor(); 
        if (fillColor == null) {
          fillColor = MetalLookAndFeel.getControlShadow();
        }
        fillTrack( g, t1, t2, fillColor);
        
        for (int i=thumbNum-1; 0<=i; i--) {
          thumbRect = thumbRects[i];
          fillColor = ((MultiKnobSlider)slider).getFillColorAt(i);
          if (fillColor == null) {
            fillColor = MetalLookAndFeel.getControlShadow();
          }
          fillTrack( g, t1, t2, fillColor);
        }
        
        g.translate( -trackRect.x, -trackRect.y );    
      }      
    }
    if ( slider.getPaintTicks() && clip.intersects( tickRect ) ) {
      paintTicks( g );
    }
    if ( slider.getPaintLabels() && clip.intersects( labelRect ) ) {
      paintLabels( g );
    }    
    
    for (int i=thumbNum-1; 0<=i; i--) {
      if ( clip.intersects( thumbRects[i] ) ) {
        thumbRect = thumbRects[i];
        thumbRenderer = ((MultiKnobSlider)slider).getThumbRendererAt(i);
        if (thumbRenderer == null) {
          if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            thumbRenderer = horizThumbIcon;
          } else {
            thumbRenderer = vertThumbIcon;
          }
        }
        paintThumb( g );
      }
    }    
  }
  
  
    @Override
  public void paintThumb(Graphics g) {     
    thumbRenderer.paintIcon( slider, g, thumbRect.x,     thumbRect.y );    
  }    
  

  public void fillTrack(Graphics g, Point t1, Point t2, Color fillColor) {
    //                               t1-------------------
    //                               |                   |
    //                               --------------------t2    
    int middleOfThumb = 0;
    
    if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
      middleOfThumb = thumbRect.x + (thumbRect.width / 2) - trackRect.x;	        
      if ( slider.isEnabled() ) {
        g.setColor(fillColor);     		  
        g.fillRect( t1.x+2,
		    t1.y+2,
	            middleOfThumb - t1.x -1,
		    t2.y - t1.y -3);		    
        g.setColor(fillColor.brighter());
        g.drawLine( t1.x+1, t1.y+1, middleOfThumb, t1.y+1 );
        g.drawLine( t1.x+1, t1.y+1, t1.x+1,        t2.y-2 );        
      } else {		  
        g.setColor(fillColor);    
        g.fillRect( t1.x, 
		    t1.y,
		    middleOfThumb - t1.x +2,
		    t2.y - t1.y );
      }
    }
    else {
      middleOfThumb = thumbRect.y + (thumbRect.height / 2) - trackRect.y;    
      if ( slider.isEnabled() ) {      	      
        g.setColor( slider.getBackground() );
	g.drawLine( t1.x+1, middleOfThumb, t2.x-2, middleOfThumb );
	g.drawLine( t1.x+1, middleOfThumb, t1.x+1, t2.y - 2 );
	g.setColor( fillColor );
	g.fillRect( t1.x + 2,
		    middleOfThumb + 1,
		    t2.x - t1.x -3,
		    t2.y-2 -  middleOfThumb);
      } else {	      
        g.setColor( fillColor );
	g.fillRect( t1.x,
		    middleOfThumb +2,
	            t2.x-1 - t1.x,
		    t2.y - t1.y );
      }
    }
  }  


  
    @Override
  public void scrollByBlock(int direction) {}
    @Override
  public void scrollByUnit(int direction) {}
  
  
  //  
  //  MultiKnobSliderAdditional
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

