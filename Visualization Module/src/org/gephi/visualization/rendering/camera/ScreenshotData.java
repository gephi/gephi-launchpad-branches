/*
Copyright 2008-2011 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
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
package org.gephi.visualization.rendering.camera;

/**
 * Data used to draw a screenshot.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class ScreenshotData {
    public final ImageSize frameBufferSize;
    public final ImageSize screenshotSize;
    public final int numberOfHorizontalTiles;
    public final int numberOfVerticalTiles;
    private final float tilesWidth;
    private final float tilesHeight;

    public ScreenshotData(ImageSize frameBufferSize, ImageSize screenshotSize) {
        this.frameBufferSize = frameBufferSize;
        this.screenshotSize = screenshotSize;
        
        int i = 1;
        while (this.frameBufferSize.width < this.screenshotSize.width / i) {
            ++i;
        }
        this.numberOfHorizontalTiles = i;
        this.tilesWidth = 1.0f / this.numberOfHorizontalTiles;
        
        int j = 1;
        while (this.frameBufferSize.height < this.screenshotSize.height / j) {
            ++j;
        }
        this.numberOfVerticalTiles = j;
        this.tilesHeight = 1.0f / this.numberOfVerticalTiles;
    }
    
    public RenderArea getRenderAreaOfTile(int i, int j) {
        if (i < 0 || i >= this.numberOfHorizontalTiles || 
                j < 0 || j >= this.numberOfVerticalTiles) {
            return null;
        }
        
        
        final Rectangle rect = new Rectangle(i * this.tilesWidth, j * this.tilesHeight, 
                this.tilesWidth, this.tilesHeight);
        return new RenderArea(this.screenshotSize.aspectRatio(), rect, 0, 10000);
    }
}
