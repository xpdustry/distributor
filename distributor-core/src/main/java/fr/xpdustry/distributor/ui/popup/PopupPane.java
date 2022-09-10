package fr.xpdustry.distributor.ui.popup;

import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.ui.*;

public final class PopupPane implements Pane {

  private static final PopupPane EMPTY = new PopupPane(Components.empty(), 0, 0, Alignement.CENTER);

  private final Component content;
  private final int shiftX;
  private final int shiftY;
  private final Alignement alignement;

  public static PopupPane create() {
    return EMPTY;
  }

  private PopupPane(final Component content, int shiftX, int shiftY, Alignement alignement) {
    this.content = content;
    this.shiftX = shiftX;
    this.shiftY = shiftY;
    this.alignement = alignement;
  }

  public Component getContent() {
    return content;
  }

  public PopupPane setContent(final Component content) {
    return new PopupPane(content, shiftX, shiftY, alignement);
  }

  public int getShiftX() {
    return shiftX;
  }

  public PopupPane setShiftX(final int shiftX) {
    return new PopupPane(content, shiftX, shiftY, alignement);
  }

  public int getShiftY() {
    return shiftY;
  }

  public PopupPane setShiftY(final int shiftY) {
    return new PopupPane(content, shiftX, shiftY, alignement);
  }

  public Alignement getAlignement() {
    return alignement;
  }

  public PopupPane setAlignement(final Alignement alignement) {
    return new PopupPane(content, shiftX, shiftY, alignement);
  }

  @Override
  public boolean isEmpty() {
    return content.isEmpty();
  }

  public enum Alignement {
    TOP_LEFT, TOP, TOP_RIGHT,
    LEFT, CENTER, RIGHT,
    BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT
  }
}
