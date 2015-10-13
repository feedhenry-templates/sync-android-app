/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package redhat.com.syncsample.item;


/**
 * Objects which implement this Handler receive touch select events from ShoppingItem touches
 */
public interface ShoppingItemSelectHandler {

    void shoppingItemSelected(ShoppingItem shoppingItem);
    void shoppingItemLongSelected(ShoppingItem shoppingItem);

}
