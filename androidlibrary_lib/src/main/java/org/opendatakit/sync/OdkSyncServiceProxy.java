/*
 * Copyright (C) 2014 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opendatakit.sync;

import java.util.concurrent.atomic.AtomicBoolean;

import org.opendatakit.common.android.utilities.WebLogger;
import org.opendatakit.sync.service.OdkSyncServiceInterface;
import org.opendatakit.sync.service.SyncAttachmentState;
import org.opendatakit.sync.service.SyncProgressState;
import org.opendatakit.sync.service.SyncStatus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class OdkSyncServiceProxy implements ServiceConnection {

  private final static String LOGTAG = OdkSyncServiceProxy.class.getSimpleName();

  private OdkSyncServiceInterface syncSvcProxy;

  protected Context componentContext;
  protected final AtomicBoolean isBoundToService = new AtomicBoolean(false);

  public OdkSyncServiceProxy(Context context) {
    componentContext = context;
    Intent bind_intent = new Intent();
    bind_intent.setClassName(SyncConsts.SYNC_SERVICE_PACKAGE, SyncConsts.SYNC_SERVICE_CLASS);
    componentContext.bindService(bind_intent, this, Context.BIND_AUTO_CREATE);
  }

  public void shutdown() {
    Log.d(LOGTAG, "Application shutdown - unbinding from SyncService");
    if (isBoundToService.get()) {
      try {
        componentContext.unbindService(this);
        isBoundToService.set(false);
        Log.d(LOGTAG, "unbound to service");
      } catch (Exception ex) {
        Log.e(LOGTAG, "service shutdown threw exception");
        ex.printStackTrace();
      }
    }
  }

  @Override
  public void onServiceConnected(ComponentName className, IBinder service) {
    Log.d(LOGTAG, "Bound to service");
    syncSvcProxy = OdkSyncServiceInterface.Stub.asInterface(service);
    isBoundToService.set(true);
  }

  @Override
  public void onServiceDisconnected(ComponentName arg0) {
    Log.d(LOGTAG, "unbound to service");
    isBoundToService.set(false);
  }

  public SyncStatus getSyncStatus(String appName) throws RemoteException {
    if (appName == null)
      throw new IllegalArgumentException("App Name cannot be null");
    try {
      return syncSvcProxy.getSyncStatus(appName);
    } catch (RemoteException rex) {
      WebLogger.getLogger(appName).printStackTrace(rex);
      throw rex;
    }
  }

  public boolean resetServer(String appName, SyncAttachmentState attachmentState) throws RemoteException {
    if (appName == null)
      throw new IllegalArgumentException("App Name cannot be null");

    try {
      return syncSvcProxy.resetServer(appName, attachmentState);
    } catch (RemoteException rex) {
      WebLogger.getLogger(appName).printStackTrace(rex);
      throw rex;
    }
  }

  public boolean synchronizeWithServer(String appName, SyncAttachmentState attachmentState) throws
      RemoteException {
    if (appName == null)
      throw new IllegalArgumentException("App Name cannot be null");

    try {
      return syncSvcProxy.synchronizeWithServer(appName, attachmentState);
    } catch (RemoteException rex) {
      WebLogger.getLogger(appName).printStackTrace(rex);
      throw rex;
    }
  }

  public boolean isBoundToService() {
    return isBoundToService.get();
  }

  public SyncProgressState getSyncProgress(String appName) throws RemoteException {
    if (appName == null)
      throw new IllegalArgumentException("App Name cannot be null");

    try {
      return syncSvcProxy.getSyncProgress(appName);
    } catch (RemoteException rex) {
      WebLogger.getLogger(appName).printStackTrace(rex);
      throw rex;
    }
  }

  public String getSyncUpdateMessage(String appName) throws RemoteException {
    if (appName == null)
      throw new IllegalArgumentException("App Name cannot be null");

    try {
      return syncSvcProxy.getSyncUpdateMessage(appName);
    } catch (RemoteException rex) {
      WebLogger.getLogger(appName).printStackTrace(rex);
      throw rex;
    }
  }
}
