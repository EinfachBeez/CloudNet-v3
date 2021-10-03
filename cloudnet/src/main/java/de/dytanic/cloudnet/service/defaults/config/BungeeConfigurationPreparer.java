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

package de.dytanic.cloudnet.service.defaults.config;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.service.ICloudService;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public class BungeeConfigurationPreparer extends AbstractServiceConfigurationPreparer {

  @Override
  public void configure(@NotNull CloudNet nodeInstance, @NotNull ICloudService cloudService) {
    // check if we should run now
    if (this.shouldRewriteIp(nodeInstance, cloudService)) {
      // copy the default file
      Path configFile = cloudService.getDirectory().resolve("config.yml");
      this.copyCompiledFile("files/bungee/config.yml", configFile);
      // rewrite the configuration file
      this.rewriteFile(configFile, line -> {
        if (line.trim().startsWith("host:")) {
          line = String.format(
            "    host: %s:%d",
            nodeInstance.getConfig().getHostAddress(),
            cloudService.getServiceConfiguration().getPort());
        }
        return line;
      });
    }
  }
}
