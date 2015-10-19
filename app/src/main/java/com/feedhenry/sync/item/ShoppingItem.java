/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package com.feedhenry.sync.item;

import com.feedhenry.sdk.sync.FHSyncUtils;

import org.json.fh.JSONObject;

import java.io.Serializable;

/**
 * The shopping item is the model item that we are synchonizing.
 */
public class ShoppingItem implements Serializable, Comparable<ShoppingItem> {
    private String itemId;
    private String itemName;
    private String itemCreated;

    public ShoppingItem(String pId, String pName, String pCreated) {
        itemId = pId;
        itemName = pName;
        itemCreated = pCreated;
    }

    public String getId() {
        return itemId;
    }

    public String getName() {
        return itemName;
    }

    public String getCreated() {
        return itemCreated;
    }

    public String toString() {
        return itemName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoppingItem that = (ShoppingItem) o;

        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null) return false;
        if (itemName != null ? !itemName.equals(that.itemName) : that.itemName != null)
            return false;
        return !(itemCreated != null ? !itemCreated.equals(that.itemCreated) : that.itemCreated != null);

    }

    @Override
    public int hashCode() {
        int result = itemId != null ? itemId.hashCode() : 0;
        result = 31 * result + (itemName != null ? itemName.hashCode() : 0);
        result = 31 * result + (itemCreated != null ? itemCreated.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ShoppingItem another) {

        if (another == null) {
            return 1;
        }

        int compareResult = compareStrings(itemCreated, another.itemCreated);
        if (compareResult == 0) {
            compareResult = compareStrings(itemName, another.itemName);
            if (compareResult == 0) {
                compareResult = compareStrings(itemId, another.itemId);
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
        create.put("name", this.itemName);
        create.put("created", this.itemCreated);
        try {
            return FHSyncUtils.generateHash(create.toString()).hashCode();
        } catch (Exception e) {
            //TODO : Refactor the sdk code to expose the correct exception
            throw new RuntimeException(e);
        }
    }

}
