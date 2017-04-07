# Android Sync Template
---------
Author: Summers Pittman (supittma@redhat.com, secondsun@gmail.com)   
Level: Intermediate  
Technologies: Java, Android, RHMAP  
Summary: A demonstration of how to synchronize a single collection with RHMAP.  
Community Project : [Feed Henry](http://feedhenry.org)
Target Product: RHMAP  
Product Versions: RHMAP 3.8.0+   
Source: https://github.com/feedhenry-templates/sync-android-app  
Prerequisites: fh-android-sdk : 3.0.+, Android Studio : 1.4.0 or newer, Android SDK : 22+ or newer

## What is it?

This application manages items in a collection that is synchronized with a remote RHMAP cloud application.  The user can create, update, and delete collection items.  Refer to `app/src/main/assets/fhconfig.properties` and `app/src/main/java/com/feedhenry/sync/activities/ListOfItemsActivity.java` for the relevant pieces of code and configuraiton.

If you do not have access to a RHMAP instance, you can sign up for a free instance at [https://openshift.feedhenry.com/](https://openshift.feedhenry.com/).

## How do I run it?  

### RHMAP Studio

This application and its cloud services are available as a project template in RHMAP as part of the "Sync Framework Project" template.

### Local Clone (ideal for Open Source Development)
If you wish to contribute to this template, the following information may be helpful; otherwise, RHMAP and its build facilities are the preferred solution.

###  Prerequisites  
 * fh-android-sdk : 3.0.+
 * Android Studio : 1.4.0 or newer
 * Android SDK : 22+ or newer

## Build instructions
 * Edit `app/src/main/assets/fhconfig.properties` to include the relevant information from RHMAP.  
 * Attach running Android Device with API 16+ running  
 * ./gradlew installDebug  
 
## How does it work?

### Initialization

[ListOfItemsActivity#fireSync](https://github.com/feedhenry-templates/sync-android-app/blob/master/app/src/main/java/com/feedhenry/sync/activities/ListOfItemsActivity.java#L103) is the method which is responsible for configuring and setting up syncronization.  Further details are available in our [API documentation](http://docs.feedhenry.com/v3/api/api_sync.html).

### Creating and Editing Items

[ListOfItemsActivity#saveItem(item)](https://github.com/feedhenry-templates/sync-android-app/blob/master/app/src/main/java/com/feedhenry/sync/activities/ListOfItemsActivity.java#L252) is called by the UI when the item is created or updated.  This piece of code checks if the object is managed by the syncronization APIs and performs a create or update as appropriate.

### Removing Items

[ListOfItemsActivity#saveItem(item)](https://github.com/feedhenry-templates/sync-android-app/blob/master/app/src/main/java/com/feedhenry/sync/activities/ListOfItemsActivity.java#L272) is called by the UI when a user swipes to remove an item from the list.


