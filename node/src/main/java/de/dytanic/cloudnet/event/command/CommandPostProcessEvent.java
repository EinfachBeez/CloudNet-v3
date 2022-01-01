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

package de.dytanic.cloudnet.event.command;

import de.dytanic.cloudnet.command.source.CommandSource;
import de.dytanic.cloudnet.driver.event.Event;
import lombok.NonNull;

public class CommandPostProcessEvent extends Event {

  private final String commandLine;
  private final CommandSource commandSource;

  public CommandPostProcessEvent(@NonNull String commandLine, @NonNull CommandSource commandSource) {
    this.commandLine = commandLine;
    this.commandSource = commandSource;
  }

  /**
   * @return the command source that executed the given commandline.
   */
  @NonNull
  public CommandSource commandSource() {
    return this.commandSource;
  }

  /**
   * @return the command line that was executed.
   */
  @NonNull
  public String commandLine() {
    return this.commandLine;
  }
}