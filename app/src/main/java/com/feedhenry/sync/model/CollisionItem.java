/**
 * Copyright 2015 Red Hat, Inc., and individual contributors
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
package com.feedhenry.sync.model;

import com.feedhenry.sdk.sync.FHSyncUtils;

import org.json.fh.JSONObject;

import java.io.Serializable;

/**
 * The collision item is the model item that represents a sync collision item
 */
public class CollisionItem implements Serializable, Comparable<CollisionItem> {

    private String id;
    private String date;

    public CollisionItem() {
    }

    public CollisionItem(String id, String date) {
        this.id = id;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String toString() {
        return id + " : " + date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollisionItem that = (CollisionItem) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return !(date != null ? !date.equals(that.date) : that.date != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(CollisionItem another) {

        if (another == null) {
            return 1;
        }

        int compareResult = compareStrings(date, another.date);
        if (compareResult == 0) {
            compareResult = compareStrings(id, another.id);
        }

        return -1 * compareResult;

    }

    private int compareStrings(String first, String second) {
        if (first == null) {
            if (second == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return first.compareTo(second);
        }
    }

    public long getFHhash() {
        JSONObject create = new JSONObject();
        create.put("date", this.date);
        try {
            return FHSyncUtils.generateHash(create.toString()).hashCode();
        } catch (Exception e) {
            //TODO : Refactor the sdk code to expose the correct exception
            throw new RuntimeException(e);
        }
    }

}
