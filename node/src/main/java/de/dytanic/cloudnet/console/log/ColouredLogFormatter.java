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

package de.dytanic.cloudnet.console.log;

import de.dytanic.cloudnet.common.log.LoggingUtils;
import de.dytanic.cloudnet.console.ConsoleColor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import lombok.NonNull;

public final class ColouredLogFormatter extends Formatter {

  private final DateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm:ss.SSS");

  @Override
  public String format(LogRecord record) {
    var builder = new StringBuilder()
      .append(ConsoleColor.DARK_GRAY)
      .append('[')
      .append(ConsoleColor.WHITE)
      .append(this.dateFormat.format(record.getMillis()))
      .append(ConsoleColor.DARK_GRAY)
      .append("] ")
      .append(this.color(record.getLevel()))
      .append(ConsoleColor.DARK_GRAY)
      .append(": ")
      .append(ConsoleColor.DEFAULT)
      .append(super.formatMessage(record));
    LoggingUtils.printStackTraceInto(builder, record);

    return builder.toString();
  }

  private @NonNull String color(@NonNull Level level) {
    var color = ConsoleColor.DARK_GRAY;
    if (level == Level.INFO) {
      color = ConsoleColor.GREEN;
    } else if (level == Level.WARNING) {
      color = ConsoleColor.YELLOW;
    } else if (level == Level.SEVERE) {
      color = ConsoleColor.RED;
    } else if (level.intValue() >= Level.FINEST.intValue() && level.intValue() <= Level.FINE.intValue()) {
      color = ConsoleColor.BLUE;
    }

    return color + level.getLocalizedName();
  }
}