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

package eu.cloudnetservice.cloudnet.ext.syncproxy.platform.waterdog;

import de.dytanic.cloudnet.wrapper.Wrapper;
import dev.waterdog.waterdogpe.plugin.Plugin;
import eu.cloudnetservice.cloudnet.ext.syncproxy.platform.listener.SyncProxyCloudListener;

public final class WaterDogPESyncProxyPlugin extends Plugin {

  private WaterDogPESyncProxyManagement syncProxyManagement;

  @Override
  public void onEnable() {
    this.syncProxyManagement = new WaterDogPESyncProxyManagement(this.getProxy());
    // register the SyncProxyManagement in our service registry
    this.syncProxyManagement.registerService(Wrapper.instance().servicesRegistry());
    // register the event listener to handle service updates
    Wrapper.instance().eventManager().registerListener(new SyncProxyCloudListener<>(this.syncProxyManagement));
    // register the waterdog ping & join listener
    new WaterDogPESyncProxyListener(this.syncProxyManagement, this.getProxy());
  }

  @Override
  public void onDisable() {
    // unregister all listeners for cloudnet events
    Wrapper.instance().eventManager().unregisterListeners(this.getClass().getClassLoader());
    Wrapper.instance().unregisterPacketListenersByClassLoader(this.getClass().getClassLoader());
    // remove the service from the registry
    this.syncProxyManagement.unregisterService(Wrapper.instance().servicesRegistry());
  }
}