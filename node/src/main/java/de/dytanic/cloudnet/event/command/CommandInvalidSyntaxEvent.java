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

package de.dytanic.cloudnet.event.command;

import de.dytanic.cloudnet.command.source.CommandSource;
import de.dytanic.cloudnet.driver.event.Event;
import org.jetbrains.annotations.NotNull;

public class CommandInvalidSyntaxEvent extends Event {

  private final CommandSource source;
  private final String correctSyntax;
  private String response;

  public CommandInvalidSyntaxEvent(
    @NotNull CommandSource source,
    @NotNull String correctSyntax,
    @NotNull String response
  ) {
    this.source = source;
    this.correctSyntax = correctSyntax;
    this.response = response;
  }

  /**
   * @return the command source that executed the commandline.
   */
  @NotNull
  public CommandSource getCommandSource() {
    return this.source;
  }

  /**
   * @return the correct syntax for the executed command
   */
  @NotNull
  public String getCorrectSyntax() {
    return this.correctSyntax;
  }

  /**
   * @return the translated invalid syntax message that is the user will receive
   */
  @NotNull
  public String getResponse() {
    return this.response;
  }

  /**
   * Set the translated invalid syntax message that is the user will receive
   *
   * @param response the message that the user will receive
   */
  public void setResponse(@NotNull String response) {
    this.response = response;
  }
}