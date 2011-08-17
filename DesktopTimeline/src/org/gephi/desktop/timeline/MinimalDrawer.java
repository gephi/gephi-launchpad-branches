/*
Copyright 2008-2010 Gephi
Authors : Julian Bilcke <julian.bilcke@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.desktop.timeline;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Date;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.gephi.timeline.api.TimelineAnimator;
import org.gephi.timeline.api.TimelineAnimatorEvent;
import org.gephi.timeline.api.TimelineAnimatorListener;
import org.gephi.timeline.api.TimelineModel;
import org.gephi.timeline.api.TimelineModelListener;
import org.gephi.timeline.spi.TimelineDrawer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jbilcke, Daniel Bernardes
 */
@ServiceProvider(service = TimelineDrawer.class)
public class MinimalDrawer extends JPanel
        implements TimelineDrawer,
        MouseListener,
        MouseMotionListener,
        TimelineAnimatorListener {

    private static final long serialVersionUID = 1L;
    private MinimalDrawerSettings settings = new MinimalDrawerSettings();
    private TimelineModel model = null;
    private TimelineAnimator animator = null;
    private Integer latestMousePositionX = null;
    private int currentMousePositionX = 0;
    private static Cursor CURSOR_DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
    private static Cursor CURSOR_LEFT_HOOK = new Cursor(Cursor.E_RESIZE_CURSOR);
    private static Cursor CURSOR_CENTRAL_HOOK = new Cursor(Cursor.MOVE_CURSOR);
    private static Cursor CURSOR_RIGHT_HOOK = new Cursor(Cursor.W_RESIZE_CURSOR);
    private static final int LOC_RESIZE_FROM = 1;
    private static final int LOC_RESIZE_TO = 2;
    private static final int LOC_RESIZE_CENTER = 3;
    private static final int LOC_RESIZE_UNKNOWN = -1;
    private static Locale LOCALE = Locale.ENGLISH;
    private double newfrom = 0;
    private double newto = 1;
    private double sf = -1;
    private double st = -1;
    private Timer viewToModelSync = null;
    private Timer modelToViewSync = null;
    private double oldfrom = -1;
    private double oldto = -1;
    public Action updateModelAction = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
            updateModel();
        }
    };
    public Action updateViewAction = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
            refreshBounds();
        }
    };
    private boolean mouseInside = false;

    public enum TimelineLevel {

        MILLISECOND,
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR,
        YEAR_10,
        YEAR_20,
        YEAR_50,
        YEAR_100
    }

    public enum TimelineState {

        IDLE,
        MOVING,
        RESIZE_FROM,
        RESIZE_TO
    }
    TimelineState currentState = TimelineState.IDLE;

    public enum HighlightedComponent {

        NONE,
        LEFT_HOOK,
        RIGHT_HOOK,
        CENTER_HOOK
    }
    HighlightedComponent highlightedComponent = HighlightedComponent.NONE;

    public MinimalDrawer() {

        setVisible(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        viewToModelSync = new Timer(150, updateModelAction);
        viewToModelSync.setRepeats(true);
        viewToModelSync.start();

        // setEnabled(true);
        // modelToViewSync = new Timer(2000, updateViewAction);
    }

    public void setModel(TimelineModel model) {
        if (model == null) {
            return;
        }
        if (model != this.model) {
            if (this.model != null) {
            }
            this.model = model;
            sf = model.getFromFloat() * (double) getWidth();
            st = model.getToFloat() * (double) getWidth();
            repaint();
        }
    }

    public TimelineModel getModel() {
        return model;
    }

    public void setAnimator(TimelineAnimator animator) {
        this.animator = animator;
        animator.addListener(this);
    }

    public TimelineAnimator getAnimator() {
        return animator;
    }
    
    public void timelineAnimatorChanged(TimelineAnimatorEvent event) {
        if(event.getStopped()) {
            currentState = TimelineState.IDLE;
        } else {
            currentState = TimelineState.MOVING;
        }
        
        // get new position
        newfrom = event.getRelFrom();
        newto   = event.getRelTo();
        updateModel();

        // move the drawer
        sf = newfrom * (double) getWidth();
        st = newto * (double) getWidth();
        
        repaint();
    }
    
    public void refreshBounds() {
        if (model != null) {
            double tsf = model.getFromFloat() * (double) getWidth();
            double tst = model.getToFloat() * (double) getWidth();
            if (tsf != sf) {
                sf = tsf;
            }
            if (tst != st) {
                st = tst;
            }
        }
    }
    
    private void updateModel() {
        if (model != null) {
            if (newfrom != oldfrom || newto != oldto) {
                model.setRangeFromFloat(newfrom, newto);
                oldfrom = newfrom;
                oldto = newto;
            }
        }        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(300, 28));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        // initialization
        if (st == 0) {
            if (model != null) {
                sf = 0.0 * (double) width;
                st = 1.0 * (double) width;
                newfrom = sf * (1.0 / width);
                newto = st * (1.0 / width);
            }
        }



        settings.update(width, height);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setBackground(settings.background.top);
        g2d.setPaint(settings.background.paint);
        g2d.fillRect(0, settings.tmMarginTop + 1, width-1, height - settings.tmMarginBottom - 2);

        if (!this.isEnabled()) {
            return;
        }
        if (model == null) {
            return;
        }

        long min = (long) model.getMinValue();
        long max = (long) model.getMaxValue();
        /*
        System.out.println("\nall min: " + min);
        System.out.println("all max: " + max);
        System.out.println("all date min: " + new DateTime(new Date(min)));
        System.out.println("all date max: " + new DateTime(new Date(max)));
         */

        if (max <= min
                || min == Double.NEGATIVE_INFINITY
                || max == Double.POSITIVE_INFINITY
                || max == Double.NEGATIVE_INFINITY
                || min == Double.POSITIVE_INFINITY) {
            System.out.println("cannot show a model with negative values");
            return;
        }
        if (model.getFromFloat() == Double.NEGATIVE_INFINITY
                || model.getToFloat() == Double.POSITIVE_INFINITY) {
            System.out.println("cannot show a selection with negative values");
            return;
        }

        /*
        System.out.println("min: " + min);
        System.out.println("max: " + max);
        System.out.println("date min: " + new DateTime(new Date(min)));
        System.out.println("date max: " + new DateTime(new Date(max)));
         */

        g2d.setRenderingHints(settings.renderingHints);


        // VISIBLE HOOK (THE LITTLE GREEN RECTANGLE ON EACH SIDE) WIDTH
        int vhw = settings.selection.visibleHookWidth;

        // SELECTED ZONE WIDTH, IN PIXELS
        int sw = (int) st - (int) sf;

        if (highlightedComponent != HighlightedComponent.NONE) {
            g2d.setPaint(settings.selection.mouseOverPaint);
            switch (highlightedComponent) {
                case LEFT_HOOK:
                    g2d.fillRect(
                            (int) sf,
                            settings.tmMarginTop,
                            vhw,
                            height - settings.tmMarginBottom - 1);
                    g2d.setPaint(settings.selection.paint);
                    g2d.fillRect(
                            (int) sf + vhw,
                            settings.tmMarginTop,
                            sw - vhw,
                            height - settings.tmMarginBottom - 1);
                    break;
                case CENTER_HOOK:
                    g2d.setPaint(settings.selection.paint);
                    g2d.fillRect(
                            (int) sf,
                            settings.tmMarginTop,
                            vhw,
                            height - settings.tmMarginBottom - 1);
                    g2d.setPaint(settings.selection.mouseOverPaint);
                    g2d.fillRect(
                            (int) sf + vhw,
                            settings.tmMarginTop,
                            sw - vhw * 2,
                            height - settings.tmMarginBottom - 1);
                    g2d.setPaint(settings.selection.paint);
                    g2d.fillRect(
                            (int) st - vhw,
                            settings.tmMarginTop,
                            vhw,
                            height - settings.tmMarginBottom - 1);
                    break;
                case RIGHT_HOOK:
                    g2d.setPaint(settings.selection.paint);
                    g2d.fillRect(
                            (int) sf,
                            settings.tmMarginTop,
                            sw - vhw,
                            height - settings.tmMarginBottom - 1);
                    g2d.setPaint(settings.selection.mouseOverPaint);
                    g2d.fillRect(
                            (int) st - vhw,
                            settings.tmMarginTop,
                            vhw,
                            height - settings.tmMarginBottom - 1);
                    break;
            }
        } else {
            g2d.setPaint(settings.selection.paint);
            g2d.fillRect((int) sf, settings.tmMarginTop, sw, height - settings.tmMarginBottom - 1);
        }

        //DateTime dtFrom = new DateTime(1455, 1, 1, 1, 1, 1, 1);
        //DateTime dtTo = new DateTime(1960, 2, 10, 1, 1, 1, 1);
//        if (model.getUnit() == DateTime.class) {
//            paintUpperRulerForInterval(g2d,
//                    new DateTime(new Date(min)),
//                    new DateTime(new Date(max)));
//        }

        g2d.setColor(settings.defaultStrokeColor);
        g2d.drawRect((int) sf, settings.tmMarginTop, sw-1, height - settings.tmMarginBottom - 1);

        double v = model.getValueFromFloat(currentMousePositionX * (1.0 / width));

        v += model.getMinValue();

        if (v != Double.NEGATIVE_INFINITY && v != Double.POSITIVE_INFINITY) {
            String str = "";
            int strw = 0;
            if (model.getUnit() == DateTime.class) {
                DateTime d = new DateTime(new Date((long) v));
                if (d != null) {

                    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                    str = fmt.withLocale(LOCALE).print(d);
                    strw = (int) (settings.tip.fontMetrics.getStringBounds(str, null)).getWidth() + 4;
                }
            } else {
                str = new Double(v).toString();
                strw = (int) (settings.tip.fontMetrics.getStringBounds(str, null)).getWidth() + 4;
            }

            int px = currentMousePositionX;
            if (px + strw >= width) {
                px = width - strw;
            }

            if (mouseInside) {
                g2d.setPaint(settings.tip.backgroundColor);
                g2d.fillRect(px, 1, strw, 18);
                g2d.setPaint(settings.tip.fontColor);
                g2d.drawRect(px, 1, strw, 18);
                g2d.setColor(settings.tip.fontColor);
                g2d.drawString(str, px + 4, 16);
            }

        }
    }

    private boolean inRange(int x, int a, int b) {
        return (a < x && x < b);
    }

    /**
     * Position of current x.
     * @param x current location
     * @param r width of slider
     * @return LOC_RESIZE_*
     */
    private int inPosition(int x, int r) {
        boolean resizeFrom = inRange(x, (int) sf - 1, (int) sf + r + 1);
        boolean resizeTo = inRange(x, (int) st - r - 1, (int) st + 1);
        if (resizeFrom && resizeTo) {
            if (inRange(x, (int) sf - 1, (int) (sf + st) / 2)) {
                return LOC_RESIZE_FROM;
            } else if (inRange(x, (int) (sf + st) / 2, (int) st + 1)) {
                return LOC_RESIZE_TO;
            }
        }
        if (resizeFrom) {
            return LOC_RESIZE_FROM;
        } else if (inRange(x, (int) sf + r, (int) st - r)) {
            return LOC_RESIZE_CENTER;
        } else if (resizeTo) {
            return LOC_RESIZE_TO;
        } else {
            return LOC_RESIZE_UNKNOWN;
        }

    }

    public void mouseClicked(MouseEvent e) {
        latestMousePositionX = e.getX();
        currentMousePositionX = latestMousePositionX;
        //throw new UnsupportedOperationException("Not supported yet.");
        /*
        int w = getWidth();
        // small feature: when the zone is too small, and if we double-click,
        // we want to expand the timeline
        if (e.getClickCount() == 2) {
        if (w != 0) {
        if (sf > 0.1 && st < 0.9) {
        newfrom = 0;
        newto = w;
        repaint();
        }
        }
        
        } else if (e.getClickCount() == 2) {
        if (w != 0) {
        newto = st * (1.0 / w);
        repaint(); // so it will repaint all panels
        }
        
        }
         */
    }

    public void mousePressed(MouseEvent e) {
        if (model == null) {
            return;
        }
        int x = e.getX();
        latestMousePositionX = x;
        currentMousePositionX = latestMousePositionX;
        int r = settings.selection.visibleHookWidth+settings.selection.invisibleHookMargin;

        if (currentState == TimelineState.IDLE) {
            int position = inPosition(x, r);
            switch(position){
                case LOC_RESIZE_FROM:
                    highlightedComponent = HighlightedComponent.LEFT_HOOK;
                    currentState = TimelineState.RESIZE_FROM;
                    break;
                case LOC_RESIZE_CENTER:
                    highlightedComponent = HighlightedComponent.CENTER_HOOK;
                    currentState = TimelineState.MOVING;
                    break;
                case LOC_RESIZE_TO:
                    highlightedComponent = HighlightedComponent.RIGHT_HOOK;
                    currentState = TimelineState.RESIZE_TO;
                    break;
                default:
                    break;
            }
        }
//        if(e.isPopupTrigger()) {
//            System.out.println("popup!");
//            MetricPopup.setLocation(e.getX(), e.getY());
//            MetricPopup.setVisible(true);
//        }        
    }

    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (currentState == TimelineState.IDLE) {
            latestMousePositionX = e.getX();
            currentMousePositionX = latestMousePositionX;
        }
        mouseInside = true;
    }

    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (currentState == TimelineState.IDLE) {
            highlightedComponent = HighlightedComponent.NONE;
            latestMousePositionX = e.getX();
            currentMousePositionX = latestMousePositionX;
        }
        mouseInside = false;
        repaint();
    }

    public void mouseReleased(MouseEvent evt) {

        latestMousePositionX = evt.getX();
        currentMousePositionX = latestMousePositionX;
        //highlightedComponent = HighlightedComponent.NONE;
        currentState = TimelineState.IDLE;
        this.getParent().repaint(); // so it will repaint upper and bottom panes
    }

    public void mouseMoved(MouseEvent evt) {
        if (model == null) {
            return;
        }

        //System.out.println("mouse moved");
        currentMousePositionX = evt.getX();
        int x = currentMousePositionX;
        float w = getWidth();
        int r = settings.selection.visibleHookWidth;

        // SELECTED ZONE BEGIN POSITION, IN PIXELS
        // int sf = (int) (model.getFromFloat() * (double) w);

        // SELECTED ZONE END POSITION, IN PIXELS
        //int st = (int) (model.getToFloat() * (double) w);

        HighlightedComponent old = highlightedComponent;
        Cursor newCursor = null;

        int a = 0;//settings.selection.invisibleHookMargin;

        int position = inPosition(x, r);
        switch (position) {
            case LOC_RESIZE_FROM:
                newCursor = CURSOR_LEFT_HOOK;
                highlightedComponent = HighlightedComponent.LEFT_HOOK;
                break;
            case LOC_RESIZE_CENTER:
                highlightedComponent = HighlightedComponent.CENTER_HOOK;
                newCursor = CURSOR_CENTRAL_HOOK;
                break;
            case LOC_RESIZE_TO:
                highlightedComponent = HighlightedComponent.RIGHT_HOOK;
                newCursor = CURSOR_RIGHT_HOOK;
                break;
            default:
                highlightedComponent = HighlightedComponent.NONE;
                newCursor = CURSOR_DEFAULT;
                break;
        }
        if (newCursor != getCursor()) {
            setCursor(newCursor);
        }
        // only repaint if highlight has changed (save a lot of fps)
        //if (highlightedComponent != old) {
        //    repaint();
        //}
        // now we always repaint, because of the tooltip
        repaint();

    }

    public void mouseDragged(MouseEvent evt) {

        if (model == null) {
            return;
        }
        double w = getWidth();

        currentMousePositionX = evt.getX();
        if (currentMousePositionX > (int) w) {
            currentMousePositionX = (int) w;
        }
        if (currentMousePositionX < 0) {
            currentMousePositionX = 0;
        }
        int x = currentMousePositionX;


        int r = settings.selection.visibleHookWidth;

        // SELECTED ZONE BEGIN POSITION, IN PIXELS
        // sf = (model.getFromFloat() * w);

        // SELECTED ZONE END POSITION, IN PIXELS
        //st = (model.getToFloat() * w);

        if (currentState == TimelineState.IDLE) {
            int position = inPosition(x, r);
            switch (position) {
                case LOC_RESIZE_FROM:
                    highlightedComponent = HighlightedComponent.LEFT_HOOK;
                    currentState = TimelineState.RESIZE_FROM;
                    break;
                case LOC_RESIZE_CENTER:
                    highlightedComponent = HighlightedComponent.CENTER_HOOK;
                    currentState = TimelineState.MOVING;
                    break;
                case LOC_RESIZE_TO:
                    highlightedComponent = HighlightedComponent.RIGHT_HOOK;
                    currentState = TimelineState.RESIZE_TO;
                    break;
                default:
                    break;
            }
        }
        double delta = 0;
        if (latestMousePositionX != null) {
            delta = x - latestMousePositionX;
        }
        latestMousePositionX = x;

        // minimal selection zone width (a security to not crush it!)
        int s = settings.selection.minimalWidth;

        switch (currentState) {
            case RESIZE_FROM:

                //problem: moving the left part will crush the security zone
                if ((sf + delta) >= (st - s)) {
                    sf = st - s;
                } else {
                    if (sf + delta <= 0) {
                        sf = 0;
                    } else {
                        sf += delta;
                    }
                }
                break;
            case RESIZE_TO:
                if ((st + delta) <= (sf + s)) {
                    st = sf + s;
                } else {
                    if ((st + delta >= w)) {
                        st = w;
                    } else {
                        st += delta;
                    }
                }
                break;
            case MOVING:
                // collision on the left..
                if ((sf + delta) < 0) {
                    st = (st - sf);
                    sf = 0;
                    // .. or the right
                } else if ((st + delta) >= w) {
                    sf = w - (st - sf);
                    st = w;
                } else {
                    sf += delta;
                    st += delta;
                }
                break;
        }

        if (w != 0) {
            newfrom = sf * (1.0 / w);
            newto = st * (1.0 / w);
        }
        this.repaint(); // so it will repaint all panels

    }
}
