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

package de.dytanic.cloudnet.driver.network.netty.http;

import de.dytanic.cloudnet.driver.network.http.HttpVersion;
import lombok.NonNull;

public class NettyHttpMessage {

  protected @NonNull HttpVersion versionFromNetty(@NonNull io.netty.handler.codec.http.HttpVersion httpVersion) {
    if (httpVersion == io.netty.handler.codec.http.HttpVersion.HTTP_1_1) {
      return HttpVersion.HTTP_1_1;
    } else {
      return HttpVersion.HTTP_1_0;
    }
  }

  protected @NonNull io.netty.handler.codec.http.HttpVersion versionToNetty(@NonNull HttpVersion httpVersion) {
    if (httpVersion == HttpVersion.HTTP_1_1) {
      return io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
    } else {
      return io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
    }
  }
}