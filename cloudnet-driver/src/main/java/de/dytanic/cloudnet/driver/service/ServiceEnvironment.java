/*
 * Copyright 2019-2021 CloudNetService team & contributors
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

package de.dytanic.cloudnet.driver.service;

import java.util.Arrays;

public enum ServiceEnvironment {

  //Minecraft Server
  MINECRAFT_SERVER_DEFAULT("minecraft"),
  MINECRAFT_SERVER_SPIGOT("spigot"),
  MINECRAFT_SERVER_PAPER_SPIGOT("paper"),
  MINECRAFT_SERVER_TUINITY_SPIGOT("tuinity"),
  MINECRAFT_SERVER_FORGE("forge"),
  MINECRAFT_SERVER_SPONGE_VANILLA("spongevanilla"),
  MINECRAFT_SERVER_AKARIN("akarin"),
  MINECRAFT_SERVER_TACO("taco"),
  //GlowStone
  GLOWSTONE_DEFAULT("glowstone"),
  //BungeeCord
  BUNGEECORD_DEFAULT("bungee"),
  BUNGEECORD_WATERFALL("waterfall"),
  BUNGEECORD_TRAVERTINE("travertine"),
  BUNGEECORD_HEXACORD("hexacord"),
  //Waterdog
  WATERDOG_PE("waterdog-pe"),
  //Nukkit
  NUKKIT_DEFAULT("nukkit"),
  //Velocity
  VELOCITY_DEFAULT("velocity");

  public static final ServiceEnvironment[] VALUES = ServiceEnvironment.values();

  private final String name;

  ServiceEnvironment(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public ServiceEnvironmentType getEnvironmentType() {
    return Arrays.stream(ServiceEnvironmentType.VALUES)
      .filter(serviceEnvironmentType -> Arrays.asList(serviceEnvironmentType.getEnvironments()).contains(this))
      .findFirst()
      .orElse(null);
  }
}
