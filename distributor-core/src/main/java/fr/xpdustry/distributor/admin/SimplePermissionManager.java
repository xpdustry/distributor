package fr.xpdustry.distributor.admin;

import arc.util.*;
import com.google.gson.*;
import com.google.gson.stream.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import mindustry.net.*;

public final class SimplePermissionManager implements PermissionManager {

  private final Administration administration;
  private final File file;
  private final Set<String> defaults = new HashSet<>();
  private final Map<String, Set<String>> permissions = new HashMap<>();

  public SimplePermissionManager(final Administration administration, final File directory, final String filename) {
    this.administration = administration;
    this.file = new File(directory, filename + ".json");

    if (this.file.exists()) {
      try (final var reader = new FileReader(this.file, StandardCharsets.UTF_8)) {
        final var object = new Gson().fromJson(reader, JsonObject.class);

        object.get("defaults").getAsJsonArray()
          .forEach(def -> this.defaults.add(def.getAsString()));

        object.get("permissions").getAsJsonObject()
          .entrySet()
          .forEach(entry -> {
            final var perms = new HashSet<String>();
            entry.getValue().getAsJsonArray().forEach(e -> perms.add(e.getAsString()));
            permissions.put(entry.getKey(), perms);
          });
      } catch (final IOException e) {
        Log.info("Failed to read permission file.");
      }
    }
  }

  @Override
  public void addDefaultPermission(final String permission) {
    if (defaults.add(permission)) {
      save();
    }
  }

  @Override
  public boolean hasDefaultPermission(final String permission) {
    return defaults.contains(permission);
  }

  @Override
  public void removeDefaultPermission(final String permission) {
    if (defaults.remove(permission)) {
      save();
    }
  }

  @Override
  public Collection<String> getDefaultPermissions() {
    return Collections.unmodifiableSet(defaults);
  }

  @Override
  public void addPermission(final String uuid, final String permission) {
    if (permissions.computeIfAbsent(uuid, u -> new HashSet<>()).add(permission)) {
      save();
    }
  }

  @Override
  public boolean hasPermission(final String uuid, final String permission) {
    return hasDefaultPermission(permission) || getPermissions(uuid).contains(permission);
  }

  @Override
  public void removePermission(final String uuid, final String permission) {
    final var set = permissions.get(uuid);
    if (set != null) {
      set.remove(permission);
      if (set.isEmpty()) {
        permissions.remove(uuid);
      }
      save();
    }
  }

  @Override
  public boolean isAdministrator(final String uuid) {
    return administration.getInfo(uuid).admin;
  }

  @Override
  public void setAdministrator(final String uuid, final boolean administrator) {
    administration.getInfo(uuid).admin = true;
  }

  @Override
  public Collection<String> getPermissions(final String uuid) {
    return permissions.containsKey(uuid) ? Collections.unmodifiableSet(permissions.get(uuid)) : Collections.emptySet();
  }

  private synchronized void save() {
    try (final var writer = new JsonWriter(new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8)))) {
      writer.setIndent("    ");
      writer.setHtmlSafe(false);

      writer.beginObject();

      writer.name("defaults");
      writer.beginArray();
      for (final var def : defaults) {
        writer.value(def);
      }
      writer.endArray();

      writer.name("permissions");
      writer.beginObject();
      for (final var entry : permissions.entrySet()) {
        writer.name(entry.getKey());
        writer.beginArray();
        for (final var permission : entry.getValue()) {
          writer.value(permission);
        }
        writer.endArray();
      }
      writer.endObject();

      writer.endObject();
    } catch (final IOException e) {
      Log.info("Failed to save the permission file.");
    }
  }
}
