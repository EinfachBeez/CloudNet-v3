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

package eu.cloudnetservice.cloudnet.node.template.install.execute;

import eu.cloudnetservice.cloudnet.node.template.install.InstallInformation;
import eu.cloudnetservice.cloudnet.node.template.install.execute.defaults.BuildStepExecutor;
import eu.cloudnetservice.cloudnet.node.template.install.execute.defaults.CopyFilterStepExecutor;
import eu.cloudnetservice.cloudnet.node.template.install.execute.defaults.DeployStepExecutor;
import eu.cloudnetservice.cloudnet.node.template.install.execute.defaults.DownloadStepExecutor;
import eu.cloudnetservice.cloudnet.node.template.install.execute.defaults.PaperApiVersionFetchStepExecutor;
import eu.cloudnetservice.cloudnet.node.template.install.execute.defaults.UnzipStepExecutor;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import lombok.NonNull;

public enum InstallStep {

  DOWNLOAD(new DownloadStepExecutor()),
  BUILD(new BuildStepExecutor()),
  UNZIP(new UnzipStepExecutor()),
  COPY_FILTER(new CopyFilterStepExecutor()),
  DEPLOY(new DeployStepExecutor()),
  PAPER_API(new PaperApiVersionFetchStepExecutor());

  private final InstallStepExecutor executor;

  InstallStep(InstallStepExecutor executor) {
    this.executor = executor;
  }

  public @NonNull Set<Path> execute(
    @NonNull InstallInformation installInformation,
    @NonNull Path workingDirectory,
    @NonNull Set<Path> inputPaths
  ) throws IOException {
    return this.executor.execute(installInformation, workingDirectory, inputPaths);
  }

  public void interrupt() {
    this.executor.interrupt();
  }
}