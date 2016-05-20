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
package org.opendatakit.sync.service;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.*;

/**
 * An object for measuring the results of a synchronization call. This is
 * especially intended to see how to display the results to the user. For
 * example, imagine you wanted to synchronize three tables. The object should
 * contain three TableLevelResult objects, mapping the dbTableName to the status
 * corresponding to outcome.
 * 
 * @author sudar.sam@gmail.com
 *
 */
public class SyncOverallResult implements Parcelable {

  private SyncOutcome appLevelSyncOutcome = SyncOutcome.WORKING;

  private TreeMap<String, TableLevelResult> mResults = new TreeMap<String, TableLevelResult>();

  public SyncOverallResult() {
  }

  protected SyncOverallResult(Parcel in) {
    appLevelSyncOutcome = in.readParcelable(SyncOutcome.class.getClassLoader());
    int count = in.readInt();
    for ( int i = 0 ; i < count ; ++i ) {
      String tableId = in.readString();
      TableLevelResult tableLevelResult = in.readParcelable(TableLevelResult.class.getClassLoader());
      mResults.put(tableId, tableLevelResult);
    }
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(appLevelSyncOutcome, flags);
    dest.writeInt(mResults.size());
    for ( String tableId : mResults.keySet() ) {
      TableLevelResult tableLevelResult = mResults.get(tableId);
      dest.writeString(tableId);
      dest.writeParcelable(tableLevelResult, flags);
    }
  }

  @Override public int describeContents() {
    return 0;
  }

  public static final Creator<SyncOverallResult> CREATOR = new Creator<SyncOverallResult>() {
    @Override public SyncOverallResult createFromParcel(Parcel in) {
      return new SyncOverallResult(in);
    }

    @Override public SyncOverallResult[] newArray(int size) {
      return new SyncOverallResult[size];
    }
  };

  /**
   * Get the app-level sync outcome.
   *
   * @return
   */
  public SyncOutcome getAppLevelSyncOutcome() {
    return appLevelSyncOutcome;
  }

  /**
   * Record the app-level sync outcome. This should be SUCCESS.
   * If it is anything else, then sync will typically be abandoned
   * without taking any table-level actions.
   *
   * @param syncOutcome
   */
  public void setAppLevelSyncOutcome(SyncOutcome syncOutcome) {
    this.appLevelSyncOutcome = syncOutcome;
  }

  /**
   * Get all the {@link TableLevelResult} objects.  This may not be
   * the full list of tables on the server if the sync interaction
   * aborts prematurely.
   * 
   * @return
   */
  public List<TableLevelResult> getTableLevelResults() {
    List<TableLevelResult> r = new ArrayList<TableLevelResult>();
    r.addAll(this.mResults.values());
    Collections.sort(r, new Comparator<TableLevelResult>() {
      @Override
      public int compare(TableLevelResult lhs, TableLevelResult rhs) {
        return lhs.getTableId().compareTo(rhs.getTableId());
      }
    });

    return r;
  }

  /**
   * Record a table result.
   *
   * @param tableId
   * @param tableLevelResult
   */
  public void setTableLevelResult(String tableId, TableLevelResult tableLevelResult) {
    mResults.put(tableId, tableLevelResult);
  }

  /**
   * Get the TableLevelResult for the indicated tableId.
   * This will create that object if it does not already exist.
   *
   * @param tableId
   * @return
   */
  public TableLevelResult fetchTableLevelResult(String tableId) {
    TableLevelResult r = mResults.get(tableId);
    if (r == null) {
      r = new TableLevelResult(tableId);
      mResults.put(tableId, r);
    }
    return r;
  }

}
