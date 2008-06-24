package com.sshtools.ui.awt;

import java.awt.image.RGBImageFilter;

class GrayFilter
    extends RGBImageFilter {
  private int darkness = -5263441;

  GrayFilter() {
    canFilterIndexColorModel = true;
  }

  public GrayFilter(int darkness) {
    this();
    this.darkness = darkness;
  }

  public int filterRGB(int x, int y, int rgb) {
    return rgb & darkness;
  }
}
