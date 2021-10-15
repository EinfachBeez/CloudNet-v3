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

package de.dytanic.cloudnet.common.concurrent;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@NotNull
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "3.6")
public interface IScheduledTask<V> extends ITask<V> {

  long getTaskId();

  boolean isRepeatable();

  long getDelayedTimeStamp();

  long getDelayMillis();

  IScheduledTask<V> setDelayMillis(long delayMillis);

  long getRepeatMillis();

  IScheduledTask<V> setRepeatMillis(long repeatMillis);

  IScheduledTask<V> cancel();

}