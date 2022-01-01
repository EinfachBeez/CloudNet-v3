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

package de.dytanic.cloudnet.common.log;

import java.util.ServiceLoader;
import lombok.NonNull;

public final class LogManager {

  private static final LoggerFactory LOGGER_FACTORY = loadLoggerFactory();

  private LogManager() {
    throw new UnsupportedOperationException();
  }

  public static @NonNull Logger rootLogger() {
    return LogManager.logger(LoggerFactory.ROOT_LOGGER_NAME);
  }

  public static @NonNull Logger logger(@NonNull Class<?> caller) {
    return LogManager.logger(caller.getName());
  }

  public static @NonNull Logger logger(@NonNull String name) {
    return LogManager.loggerFactory().logger(name);
  }

  public static @NonNull LoggerFactory loggerFactory() {
    return LOGGER_FACTORY;
  }

  private static @NonNull LoggerFactory loadLoggerFactory() {
    var factories = ServiceLoader.load(LoggerFactory.class).iterator();
    // check if a logger service is registered
    if (factories.hasNext()) {
      return factories.next();
    } else {
      return new FallbackLoggingFactory();
    }
  }
}