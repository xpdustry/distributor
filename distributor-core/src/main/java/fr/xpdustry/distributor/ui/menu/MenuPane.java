package fr.xpdustry.distributor.ui.menu;

import arc.struct.*;
import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.text.Component;
import fr.xpdustry.distributor.ui.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;
import java.util.List;

public final class MenuPane implements Pane {

  private static final MenuOption[][] EMPTY_OPTIONS = {};

  private final Component title;
  private final Component content;
  final MenuOption[][] options;

  MenuPane() {
    this(Components.empty(), Components.empty(), EMPTY_OPTIONS);
  }

  private MenuPane(final Component title, final Component content, final MenuOption[][] options) {
    this.title = title;
    this.content = content;
    this.options = options;
  }

  public Component getTitle() {
    return title;
  }

  public MenuPane setTitle(final Component title) {
    return new MenuPane(title, this.content, this.options);
  }

  public Component getContent() {
    return content;
  }

  public MenuPane setContent(final Component content) {
    return new MenuPane(this.title, content, this.options);
  }

  public List<MenuOption> getOptions() {
    return Arrays.stream(this.options).flatMap(Arrays::stream).toList();
  }

  public MenuOption getOption(final int x, final int y) {
    check(x, y);
    return this.options[y][x];
  }

  public MenuOption getOption(final int id) {
    final var options = getOptions();
    if (id < options.size()) {
      return options.get(id);
    } else {
      throw new IllegalArgumentException("The id is higher than " + getOptions().size());
    }
  }

  public MenuPane setOption(final int x, final int y, final MenuOption option) {
    check(x, y);
    final var copy = getOptionsCopy();
    copy[y] = Arrays.copyOf(this.options[y], this.options[y].length);
    copy[y][x] = option;
    return createNewPane(this.options);
  }

  public MenuPane removeOption(final int x, final int y) {
    check(x, y);
    final var copy = getOptionsCopy();
    copy[y] = Magik.removeElementFromArray(copy[y], x);
    return createNewPane(copy);
  }

  public List<MenuOption> getOptionRow(final int y) {
    check(y);
    return List.of(this.options[y]);
  }

  public MenuPane setOptionRow(final int y, MenuOption... row) {
    check(y);
    final var copy = getOptionsCopy();
    copy[y] = Arrays.copyOf(row, row.length);
    return createNewPane(this.options);
  }

  public MenuPane setOptionRow(final int y, Iterable<MenuOption> options) {
    check(y);
    final var copy = getOptionsCopy();
    copy[y] = options instanceof Collection<MenuOption> collection
      ? collection.toArray(MenuOption[]::new)
      : Seq.with(options).toArray();
    return createNewPane(copy);
  }

  public MenuPane addOptionRow(final MenuOption... row) {
    final var copy = Arrays.copyOf(this.options, this.options.length + 1);
    copy[copy.length - 1] = Arrays.copyOf(row, row.length);
    return new MenuPane(this.title, this.content, copy);
  }

  public MenuPane addOptionRow(final Iterable<MenuOption> options) {
    final MenuOption[] row = options instanceof Collection<MenuOption> collection
      ? collection.toArray(MenuOption[]::new)
      : Seq.with(options).toArray();
    final var copy = Arrays.copyOf(this.options, this.options.length + 1);
    copy[copy.length - 1] = row;
    return new MenuPane(this.title, this.content, copy);
  }

  public MenuPane removeOptionRow(final int y) {
    check(y);
    return createNewPane(Magik.removeElementFromArray(this.options, y));
  }

  public MenuPane removeAllOptions() {
    return createNewPane(EMPTY_OPTIONS);
  }

  public int getOptionRowSize() {
    return options.length;
  }

  @Override
  public boolean isEmpty() {
    return options.length == 0 && title.isEmpty() && content.isEmpty();
  }

  private MenuPane createNewPane(final MenuOption[][] options) {
    return new MenuPane(this.title, this.content, options);
  }

  private MenuOption[][] getOptionsCopy() {
    return Arrays.copyOf(this.options, this.options.length);
  }

  private void check(final int y) {
    if (y >= this.options.length) {
      throw new IllegalArgumentException();
    }
  }

  private void check(final int x, final int y) {
    check(y);
    if (x >= this.options[y].length) {
      throw new IllegalArgumentException();
    }
  }
}
