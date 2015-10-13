/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package redhat.com.syncsample.item;

import java.io.Serializable;

/**
 * Created by summers on 10/13/15.
 */
public class ShoppingItem implements Serializable {
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
}
