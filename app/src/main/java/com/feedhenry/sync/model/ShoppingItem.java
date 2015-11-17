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
 * The shopping item is the model item that we are synchonizing.
 */
public class ShoppingItem implements Serializable, Comparable<ShoppingItem> {

    private String id;
    private String name;
    private String created;

    public ShoppingItem() {
    }

    public ShoppingItem(String id, String name, String created) {
        this.id = id;
        this.name = name;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoppingItem that = (ShoppingItem) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        return !(created != null ? !created.equals(that.created) : that.created != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ShoppingItem another) {

        if (another == null) {
            return 1;
        }

        int compareResult = compareStrings(created, another.created);
        if (compareResult == 0) {
            compareResult = compareStrings(name, another.name);
            if (compareResult == 0) {
                compareResult = compareStrings(id, another.id);
            }
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
        create.put("name", this.name);
        create.put("created", this.created);
        try {
            return FHSyncUtils.generateHash(create.toString()).hashCode();
        } catch (Exception e) {
            //TODO : Refactor the sdk code to expose the correct exception
            throw new RuntimeException(e);
        }
    }

}
