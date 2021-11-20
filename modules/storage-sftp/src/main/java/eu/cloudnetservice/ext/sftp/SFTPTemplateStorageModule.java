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

package eu.cloudnetservice.ext.sftp;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.cluster.sync.DataSyncHandler;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.common.io.FileUtils;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import de.dytanic.cloudnet.driver.network.HostAndPort;
import de.dytanic.cloudnet.driver.template.TemplateStorage;
import eu.cloudnetservice.ext.sftp.config.SFTPTemplateStorageConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jetbrains.annotations.NotNull;

public final class SFTPTemplateStorageModule extends DriverModule {

  private SFTPTemplateStorage storage;
  private volatile SFTPTemplateStorageConfig config;

  @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.LOADED)
  public void convertConfig() {
    // the old config was located in a directory called '-ftp' rather than '-sftp'
    Path oldConfigPath = this.getModuleWrapper().getModuleProvider().getModuleDirectoryPath()
      .resolve("CloudNet-Storage-FTP")
      .resolve("config.json");
    if (Files.exists(oldConfigPath)) {
      JsonDocument config = JsonDocument.newDocument(oldConfigPath);
      // convert to the new config format
      this.writeConfig(new SFTPTemplateStorageConfig(
        config.get("address", HostAndPort.class),
        config.getString("storage"),
        config.getString("username"),
        config.getString("password"),
        config.getString("sshKeyPath") == null ? null : Paths.get(config.getString("sshKeyPath")),
        config.getString("sshKeyPassword") == null ? null : config.getString("sshKeyPassword"),
        null,
        config.getString("baseDirectory"),
        4));
      // remove the old directory
      FileUtils.delete(oldConfigPath.getParent());
    }
  }

  @ModuleTask(event = ModuleLifeCycle.LOADED)
  public void handleInit() {
    if (Files.exists(this.getConfigPath())) {
      // load the config
      this.config = JsonDocument.newDocument(this.getConfigPath()).toInstanceOf(SFTPTemplateStorageConfig.class);
      // init the storage
      this.storage = new SFTPTemplateStorage(this.config);
      this.getServiceRegistry().registerService(TemplateStorage.class, this.storage.getName(), this.storage);
      // register the cluster sync handler
      CloudNet.getInstance().getDataSyncRegistry().registerHandler(DataSyncHandler.<SFTPTemplateStorageConfig>builder()
        .key("bridge-config")
        .nameExtractor($ -> "Bridge Config")
        .convertObject(SFTPTemplateStorageConfig.class)
        .writer(this::writeConfig)
        .singletonCollector(() -> this.config)
        .currentGetter($ -> this.config)
        .build());
    } else {
      JsonDocument.newDocument(new SFTPTemplateStorageConfig()).write(this.getConfigPath());
    }
  }

  @ModuleTask(event = ModuleLifeCycle.STOPPED)
  public void handleStop() throws IOException {
    this.storage.close();
    this.getServiceRegistry().unregisterService(TemplateStorage.class, this.storage.getName());
  }

  public void writeConfig(@NotNull SFTPTemplateStorageConfig config) {
    this.config = config;
    JsonDocument.newDocument(config).write(this.getConfigPath());
  }
}