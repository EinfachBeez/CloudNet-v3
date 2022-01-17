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

package eu.cloudnetservice.modules.bridge.util;

import eu.cloudnetservice.cloudnet.driver.network.HostAndPort;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.unix.DomainSocketAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnixDomainSocketAddress;
import lombok.NonNull;

public final class BridgeHostAndPortUtil {

  private BridgeHostAndPortUtil() {
    throw new UnsupportedOperationException();
  }

  public static @NonNull HostAndPort fromSocketAddress(@NonNull SocketAddress address) {
    // default java.net addresses are supported by default
    if (address instanceof InetSocketAddress || address instanceof UnixDomainSocketAddress) {
      return HostAndPort.fromSocketAddress(address);
    }
    // netty unix domain socket version
    if (address instanceof DomainSocketAddress domain) {
      return new HostAndPort(domain.path(), HostAndPort.NO_PORT);
    }
    // netty local address
    if (address instanceof LocalAddress local) {
      return new HostAndPort(local.id(), HostAndPort.NO_PORT);
    }
    // unsupported address
    throw new IllegalArgumentException("Unsupported socket address type: " + address.getClass().getName());
  }
}
