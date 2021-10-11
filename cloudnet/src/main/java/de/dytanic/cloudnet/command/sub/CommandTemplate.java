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

package de.dytanic.cloudnet.command.sub;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.specifier.Quoted;
import cloud.commandframework.context.CommandContext;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.command.exception.SyntaxException;
import de.dytanic.cloudnet.command.source.CommandSource;
import de.dytanic.cloudnet.common.JavaVersion;
import de.dytanic.cloudnet.common.language.LanguageManager;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.driver.template.SpecificTemplateStorage;
import de.dytanic.cloudnet.driver.template.TemplateStorage;
import de.dytanic.cloudnet.template.TemplateStorageUtil;
import de.dytanic.cloudnet.template.install.ServiceVersion;
import de.dytanic.cloudnet.template.install.ServiceVersionType;
import de.dytanic.cloudnet.util.JavaVersionResolver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.zip.ZipInputStream;

@CommandDescription("Manages the templates and allows installation of .jar files")
public class CommandTemplate {

  @Parser
  public ServiceTemplate defaultServiceTemplateParser(CommandContext<CommandSource> sender, Queue<String> input) {
    ServiceTemplate template = ServiceTemplate.parse(input.remove());
    if (template == null || template.nullableStorage() == null) {
      throw new SyntaxException("");
    }
    return template;
  }

  @Parser
  public TemplateStorage defaultTemplateStorageParser(CommandContext<CommandSource> sender, Queue<String> input) {
    TemplateStorage templateStorage = CloudNet.getInstance().getTemplateStorage(input.remove());
    if (templateStorage == null) {
      throw new SyntaxException("");
    }
    return templateStorage;
  }

  @Parser
  public ServiceVersionType defaultVersionTypeParser(CommandContext<CommandSource> sender, Queue<String> input) {
    String versionTypeName = input.remove().toLowerCase();
    return CloudNet.getInstance().getServiceVersionProvider().getServiceVersionType(versionTypeName)
      .orElseThrow(() -> new SyntaxException(""));
  }

  @Parser
  public ServiceEnvironmentType defaultEnvironmentTypeParser(CommandContext<CommandSource> sender,
    Queue<String> input) {
    return ServiceEnvironmentType.valueOf(input.remove());
  }

  @CommandMethod("template|t list [storage]")
  public void displayTemplates(CommandSource source, @Argument("storage") TemplateStorage templateStorage) {
    TemplateStorage resultingStorage =
      templateStorage == null ? CloudNet.getInstance().getLocalTemplateStorage() : templateStorage;

    List<String> messages = new ArrayList<>();
    messages.add(LanguageManager.getMessage("command-template-list-templates")
      .replace("%storage%", resultingStorage.getName()));

    for (ServiceTemplate template : resultingStorage.getTemplates()) {
      messages.add("  " + template.toString());
    }

    source.sendMessage(messages);
  }

  @CommandMethod("template|t versions|v")
  public void displayTemplateVersions(CommandSource source) {
    List<String> versions = new ArrayList<>();

    for (ServiceVersionType versionType : CloudNet.getInstance().getServiceVersionProvider()
      .getServiceVersionTypes().values()) {
      List<String> messages = new ArrayList<>();

      messages.add("  " + versionType.getName() + ":");

      for (ServiceVersion version : versionType.getVersions()) {
        messages.add("    " + version.getName());
      }

      versions.add(String.join("\n", messages));
    }

    source.sendMessage(LanguageManager.getMessage("command-template-list-versions"));

    //todo source.sendMessage(ColumnTextFormatter.formatInColumns(versions, "\n", 4).split("\n"));
  }

  @CommandMethod("template|t install <template> <versionType> <version>")
  public void installTemplate(
    CommandSource source,
    @Argument("template") ServiceTemplate serviceTemplate,
    @Argument("versionType") ServiceVersionType versionType,
    @Argument("version") String version,
    @Flag("force") boolean forceInstall,
    @Flag("executable") @Quoted String executable
  ) {

    ServiceVersion serviceVersion = versionType.getVersion(version).orElse(null);
    if (serviceVersion == null) {
      source.sendMessage("Invalid version bla bla");
      return;
    }

    String resolvedExecutable = executable == null ? "java" : executable;
    JavaVersion javaVersion = JavaVersionResolver.resolveFromJavaExecutable(resolvedExecutable);
    if (javaVersion == null) {
      source.sendMessage("Java executable invalid");
      return;
    }

    if (!versionType.canInstall(serviceVersion, javaVersion)) {
      source.sendMessage(LanguageManager.getMessage("command-template-install-wrong-java")
        .replace("%version%", versionType.getName() + "-" + serviceVersion.getName())
        .replace("%java%", javaVersion.getName())
      );
      if (!forceInstall) {
        return;
      }
    }

    CloudNet.getInstance().getMainThread().runTask(() -> {
      source.sendMessage(LanguageManager.getMessage("command-template-install-try")
        .replace("%version%", versionType.getName() + "-" + serviceVersion.getName())
        .replace("%template%", serviceTemplate.toString())
      );

      if (CloudNet.getInstance().getServiceVersionProvider().installServiceVersion(
        resolvedExecutable.equals("java") ? null : resolvedExecutable, versionType, serviceVersion, serviceTemplate,
        forceInstall)) {
        source.sendMessage(LanguageManager.getMessage("command-template-install-success")
          .replace("%version%", versionType.getName() + "-" + serviceVersion.getName())
          .replace("%template%", serviceTemplate.toString())
        );
      } else {
        source.sendMessage(LanguageManager.getMessage("command-template-install-failed")
          .replace("%version%", versionType.getName() + "-" + serviceVersion.getName())
          .replace("%template%", serviceTemplate.toString())
        );
      }
    });

  }

  @CommandMethod("template|t delete|rm|del <template>")
  public void deleteTemplate(CommandSource source, @Argument("template") ServiceTemplate template) {
    SpecificTemplateStorage templateStorage = template.storage();
    if (!templateStorage.exists()) {
      source.sendMessage("Template not found");
      return;
    }

    templateStorage.delete();
    source.sendMessage("Deleted template");
  }

  @CommandMethod("template|t create <template> <environment>")
  public void createTemplate(
    CommandSource source,
    @Argument("template") ServiceTemplate template,
    @Argument("environment") ServiceEnvironmentType environmentType
  ) {
    SpecificTemplateStorage templateStorage = template.storage();
    if (templateStorage.exists()) {
      source.sendMessage("Already exists");
      return;
    }

    try {
      if (TemplateStorageUtil.createAndPrepareTemplate(template, environmentType)) {
        source.sendMessage(LanguageManager.getMessage("command-template-create-success")
          .replace("%template%", template.getFullName())
          .replace("%storage%", template.getStorage())
        );
      }
    } catch (IOException exception) {
      source.sendMessage(LanguageManager.getMessage("command-template-create-failed")
        .replace("%template%", template.getFullName())
        .replace("%storage%", template.getStorage())
      );
    }
  }

  @CommandMethod("template|t copy|cp <storage:prefix/name (sourceTemplate)> <storage:prefix/name (targetTemplate)>")
  public void copyTemplate(
    CommandSource source,
    @Argument("storage:prefix/name (sourceTemplate)") ServiceTemplate sourceTemplate,
    @Argument("storage:prefix/name (targetTemplate)") ServiceTemplate targetTemplate
  ) {
    if (sourceTemplate.equals(targetTemplate)) {
      source.sendMessage("Cannot copy template it self");
      return;
    }

    SpecificTemplateStorage sourceStorage = sourceTemplate.storage();
    SpecificTemplateStorage targetStorage = targetTemplate.storage();

    CloudNet.getInstance().getMainThread().runTask(() -> {
      source.sendMessage(LanguageManager.getMessage("command-template-copy")
        .replace("%sourceTemplate%", sourceTemplate.toString())
        .replace("%targetTemplate%", targetTemplate.toString())
      );

      targetStorage.delete();
      targetStorage.create();
      try (ZipInputStream stream = sourceStorage.asZipInputStream()) {
        if (stream == null) {
          source.sendMessage(LanguageManager.getMessage("command-template-copy-failed"));
        }

        targetStorage.deploy(stream);
        source.sendMessage(LanguageManager.getMessage("command-template-copy-success")
          .replace("%sourceTemplate%", sourceTemplate.toString())
          .replace("%targetTemplate%", targetTemplate.toString())
        );
      }
    });
  }
}
