package fr.xpdustry.distributor.admin;

import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.struct.*;
import java.io.*;
import java.util.*;
import mindustry.net.*;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.loader.*;
import org.spongepowered.configurate.serialize.*;
import org.spongepowered.configurate.yaml.*;

public final class SimplePermissionManager implements PermissionManager, PluginResource {

  private final Administration administration;
  private final ConfigurationLoader<?> loader;
  private ConfigurationNode root;

  public SimplePermissionManager(final Administration administration, final File directory, final String filename) {
    this.administration = administration;
    this.loader = YamlConfigurationLoader.builder()
      .indent(4)
      .nodeStyle(NodeStyle.BLOCK)
      .file(new File(directory, filename + ".yml"))
      .build();
    this.root = this.loader.createNode();
  }

  @Override
  public void addDefaultPermission(final String permission) {
    final var node = getDefaultPermissionsNode();
    if (!node.hasChild(permission)) {
      node.raw(permission);
    }
  }

  @Override
  public boolean hasDefaultPermission(final String permission) {
    return getDefaultPermissionsNode().hasChild(permission);
  }

  @Override
  public void removeDefaultPermission(final String permission) {
    getDefaultPermissionsNode().removeChild(permission);
  }

  @Override
  public Collection<String> getDefaultPermissions() {
    try {
      return getDefaultPermissionsNode().getList(String.class, Collections.emptyList());
    } catch (final SerializationException e) {
      throw impossible(e);
    }
  }

  @Override
  public void addPermission(final MUUID muuid, final String permission) {
    final var node = getPlayerPermissionsNode(muuid);
    if (!node.hasChild(permission)) {
      node.appendListNode().raw(permission);
    }
  }

  @Override
  public boolean hasPermission(final MUUID muuid, final String permission) {
    return getDefaultPermissionsNode().hasChild(permission)
      || (isValid(muuid) && getPlayerPermissionsNode(muuid).hasChild(permission));
  }

  @Override
  public void removePermission(final MUUID muuid, final String permission) {
    getPlayerPermissionsNode(muuid).removeChild(permission);
  }

  @Override
  public boolean isAdministrator(final MUUID muuid) {
    return administration.isAdmin(muuid.getUUID(), muuid.getUSID());
  }

  @Override
  public void setAdministrator(final MUUID muuid, final boolean administrator) {
    administration.adminPlayer(muuid.getUUID(), muuid.getUSID());
  }

  @Override
  public Collection<String> getPermissions(final MUUID muuid) {
    try {
      return getPlayerPermissionsNode(muuid).getList(String.class, Collections.emptyList());
    } catch (final SerializationException e) {
      throw impossible(e);
    }
  }

  @Override
  public void load() throws IOException {
    this.root = loader.load();
  }

  @Override
  public void save() throws IOException {
    loader.save(this.root);
  }

  private RuntimeException impossible(final Exception e) {
    return new RuntimeException("An unexpected exception happened.", e);
  }

  private boolean isValid(final MUUID muuid) {
    final var info = administration.getInfoOptional(muuid.getUUID());
    return info != null && info.adminUsid != null && info.adminUsid.equals(muuid.getUSID());
  }

  private ConfigurationNode getDefaultPermissionsNode() {
    return root.node("default");
  }

  private ConfigurationNode getPlayerPermissionsNode(final MUUID muuid) {
    return root.node("players", muuid.getUUID());
  }
}
