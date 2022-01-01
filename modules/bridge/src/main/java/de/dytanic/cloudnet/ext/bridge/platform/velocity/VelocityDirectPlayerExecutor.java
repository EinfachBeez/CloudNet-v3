/*
 * Copyright 2019-2022 CloudNetService team & contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dytanic.cloudnet.ext.bridge.platform.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import de.dytanic.cloudnet.common.collection.Pair;
import de.dytanic.cloudnet.ext.bridge.platform.PlatformBridgeManagement;
import de.dytanic.cloudnet.ext.bridge.platform.PlatformPlayerExecutorAdapter;
import de.dytanic.cloudnet.ext.bridge.player.executor.ServerSelectorType;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.Nullable;

final class VelocityDirectPlayerExecutor extends PlatformPlayerExecutorAdapter {

  private final UUID uniqueId;
  private final ProxyServer proxyServer;
  private final PlatformBridgeManagement<Player, ?> management;
  private final Supplier<Collection<? extends Player>> playerSupplier;

  public VelocityDirectPlayerExecutor(
    @NonNull UUID uniqueId,
    @NonNull ProxyServer proxyServer,
    @NonNull PlatformBridgeManagement<Player, ?> management,
    @NonNull Supplier<Collection<? extends Player>> playerSupplier
  ) {
    this.uniqueId = uniqueId;
    this.proxyServer = proxyServer;
    this.management = management;
    this.playerSupplier = playerSupplier;
  }

  @Override
  public @NonNull UUID uniqueId() {
    return this.uniqueId;
  }

  @Override
  public void connect(@NonNull String serviceName) {
    this.proxyServer.getServer(serviceName).ifPresent(
      server -> this.playerSupplier.get().forEach(player -> player.createConnectionRequest(server).fireAndForget()));
  }

  @Override
  public void connectSelecting(@NonNull ServerSelectorType selectorType) {
    this.management.cachedServices().stream()
      .sorted(selectorType.comparator())
      .map(server -> this.proxyServer.getServer(server.name()))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst()
      .ifPresent(
        server -> this.playerSupplier.get().forEach(player -> player.createConnectionRequest(server).fireAndForget()));
  }

  @Override
  public void connectToFallback() {
    this.playerSupplier.get().stream()
      .map(player -> new Pair<>(player, this.management.fallback(player)))
      .filter(pair -> pair.second().isPresent())
      .map(pair -> new Pair<>(pair.first(), this.proxyServer.getServer(pair.second().get().name())))
      .filter(pair -> pair.second().isPresent())
      .forEach(pair -> pair.first().createConnectionRequest(pair.second().get()).fireAndForget());
  }

  @Override
  public void connectToGroup(@NonNull String group, @NonNull ServerSelectorType selectorType) {
    this.management.cachedServices().stream()
      .filter(service -> service.configuration().groups().contains(group))
      .sorted(selectorType.comparator())
      .map(service -> this.proxyServer.getServer(service.name()))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .forEach(
        server -> this.playerSupplier.get().forEach(player -> player.createConnectionRequest(server).fireAndForget()));
  }

  @Override
  public void connectToTask(@NonNull String task, @NonNull ServerSelectorType selectorType) {
    this.management.cachedServices().stream()
      .filter(service -> service.serviceId().taskName().equals(task))
      .sorted(selectorType.comparator())
      .map(service -> this.proxyServer.getServer(service.name()))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .forEach(
        server -> this.playerSupplier.get().forEach(player -> player.createConnectionRequest(server).fireAndForget()));
  }

  @Override
  public void kick(@NonNull Component message) {
    this.playerSupplier.get().forEach(player -> player.disconnect(message));
  }

  @Override
  public void sendTitle(@NonNull Title title) {
    this.playerSupplier.get().forEach(player -> player.showTitle(title));
  }

  @Override
  public void sendMessage(@NonNull Component message) {
    this.playerSupplier.get().forEach(player -> player.sendMessage(message));
  }

  @Override
  public void sendChatMessage(@NonNull Component message, @Nullable String permission) {
    this.playerSupplier.get().forEach(player -> {
      if (permission == null || player.hasPermission(permission)) {
        player.sendMessage(message);
      }
    });
  }

  @Override
  public void sendPluginMessage(@NonNull String tag, byte[] data) {
    this.playerSupplier.get().forEach(player -> player.sendPluginMessage(MinecraftChannelIdentifier.from(tag), data));
  }

  @Override
  public void dispatchProxyCommand(@NonNull String command) {
    this.playerSupplier.get().forEach(player -> player.spoofChatInput(command));
  }
}