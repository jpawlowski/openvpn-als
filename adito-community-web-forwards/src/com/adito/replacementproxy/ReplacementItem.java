
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.adito.replacementproxy;

import com.adito.boot.Util;

public class ReplacementItem {

  private Replacement replacement;
  private boolean canMoveUp, canMoveDown;
  private int index; 
  
  public ReplacementItem(int index, Replacement replacement) {
    this.replacement = replacement;
    this.index = index;
  }
  
  public String getIndexString() {
    return String.valueOf(index);
  }
  
  public String moveDownIndexString() {
      return String.valueOf(index+1);
    }

  public String moveUpIndexString() {
      return String.valueOf(index-1);
    }

  public int getIndex() {
    return index;
  }
  
  public void setIndex(int index) {
    this.index = index;
  }
  
  public Replacement getReplacement() {
    return replacement;
  }
  
  public String getMatchPatternText() {
    return Util.trimToSize(replacement.getMatchPattern(), 27, true);
  }
  
  public String getReplacePatternText() {
    return Util.trimToSize(replacement.getReplacePattern(), 27, true);
  }
  
  public boolean getCanMoveDown() {
    return canMoveDown;
  }
  
  public void setCanMoveDown(boolean canMoveDown) {
    this.canMoveDown = canMoveDown;
  }
  
  public boolean getCanMoveUp() {
    return canMoveUp;
  }
  
  public void setCanMoveUp(boolean canMoveUp) {
    this.canMoveUp = canMoveUp;
  }

}
