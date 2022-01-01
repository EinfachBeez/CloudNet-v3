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

package eu.cloudnetservice.modules.npc.node.listeners;

import de.dytanic.cloudnet.console.animation.setup.answer.Parsers;
import de.dytanic.cloudnet.console.animation.setup.answer.QuestionAnswerType;
import de.dytanic.cloudnet.console.animation.setup.answer.QuestionListEntry;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.event.setup.SetupCompleteEvent;
import de.dytanic.cloudnet.event.setup.SetupInitiateEvent;
import eu.cloudnetservice.modules.npc.configuration.NPCConfiguration;
import eu.cloudnetservice.modules.npc.configuration.NPCConfigurationEntry;
import eu.cloudnetservice.modules.npc.node.NodeNPCManagement;
import lombok.NonNull;

public final class NodeSetupListener {

  private static final QuestionListEntry<Boolean> CREATE_ENTRY_QUESTION_LIST = QuestionListEntry.<Boolean>builder()
    .key("generateDefaultNPCConfigurationEntry")
    .translatedQuestion("module-npcs-tasks-setup-generate-default-config")
    .answerType(QuestionAnswerType.<Boolean>builder()
      .parser(Parsers.bool())
      .recommendation("no")
      .possibleResults("yes", "no")
      .build())
    .build();

  private final NodeNPCManagement management;

  public NodeSetupListener(@NonNull NodeNPCManagement management) {
    this.management = management;
  }

  @EventListener
  public void handle(@NonNull SetupInitiateEvent event) {
    event.setup().entries().stream()
      .filter(entry -> entry.key().equals("taskEnvironment"))
      .findFirst()
      .ifPresent(entry -> entry.answerType().thenAccept(($, environment) -> {
        if (!event.setup().hasResult("generateDefaultNPCConfigurationEntry")
          && ServiceEnvironmentType.isMinecraftServer((ServiceEnvironmentType) environment)) {
          event.setup().addEntries(CREATE_ENTRY_QUESTION_LIST);
        }
      }));
  }

  @EventListener
  public void handle(@NonNull SetupCompleteEvent event) {
    if (event.setup().hasResult("generateDefaultNPCConfigurationEntry")) {
      String taskName = event.setup().result("taskName");
      Boolean generateNPCConfig = event.setup().result("generateDefaultNPCConfigurationEntry");

      if (taskName != null && generateNPCConfig) {
        var entries = this.management.npcConfiguration().entries();
        if (entries.stream().noneMatch(entry -> entry.targetGroup().equals(taskName))) {
          // add the new entry
          entries.add(NPCConfigurationEntry.builder().targetGroup(taskName).build());
          // update the config
          this.management.npcConfiguration(NPCConfiguration.builder()
            .entries(entries)
            .build());
        }
      }
    }
  }
}