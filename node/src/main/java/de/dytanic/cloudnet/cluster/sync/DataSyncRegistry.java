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

package de.dytanic.cloudnet.cluster.sync;

import de.dytanic.cloudnet.driver.network.buffer.DataBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DataSyncRegistry {

  void registerHandler(@NotNull DataSyncHandler<?> handler);

  void unregisterHandler(@NotNull DataSyncHandler<?> handler);

  void unregisterHandler(@NotNull String handlerKey);

  void unregisterHandler(@NotNull ClassLoader loader);

  boolean hasHandler(@NotNull String handlerKey);

  @Nullable DataBuf handle(@NotNull DataBuf input, boolean force);

  @NotNull DataBuf.Mutable prepareClusterData(@NotNull DataBuf.Mutable to, boolean force);
}