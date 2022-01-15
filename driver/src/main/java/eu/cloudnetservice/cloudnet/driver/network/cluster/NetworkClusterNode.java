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

package eu.cloudnetservice.cloudnet.driver.network.cluster;

import eu.cloudnetservice.cloudnet.common.document.gson.JsonDocument;
import eu.cloudnetservice.cloudnet.common.document.property.JsonDocPropertyHolder;
import eu.cloudnetservice.cloudnet.driver.network.HostAndPort;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * Represents a general information holder about a node running in a cluster. Every node knows this information about
 * each node which runs in the cluster, even if the node is not connected.
 *
 * @since 4.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class NetworkClusterNode extends JsonDocPropertyHolder {

  private final String uniqueId;
  private final HostAndPort[] listeners;

  /**
   * Creates a new instance of a network cluster node.
   *
   * @param uniqueId  the unique id of the node.
   * @param listeners the listeners of the node which are always running.
   * @throws NullPointerException if either the id or listener array is null.
   */
  public NetworkClusterNode(@NonNull String uniqueId, @NonNull HostAndPort[] listeners) {
    this.uniqueId = uniqueId;
    this.listeners = listeners;
  }

  /**
   * Creates a new instance of a network cluster node.
   *
   * @param uniqueId   the unique id of the node.
   * @param listeners  the listeners of the node which are always running.
   * @param properties the properties which are set for this node, mainly for developers to store information.
   * @throws NullPointerException if either the id or listener array is null.
   */
  public NetworkClusterNode(
    @NonNull String uniqueId,
    @NonNull HostAndPort[] listeners,
    @NonNull JsonDocument properties
  ) {
    this.uniqueId = uniqueId;
    this.listeners = listeners;
    this.properties = properties;
  }

  /**
   * Get the unique id of this node.
   *
   * @return the unique id of this node.
   */
  public @NonNull String uniqueId() {
    return this.uniqueId;
  }

  /**
   * Get all listener of this node.
   *
   * @return all listener of this node.
   */
  public @NonNull HostAndPort[] listeners() {
    return this.listeners;
  }
}
