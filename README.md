MelodySmart Module for Android
==============================


Overview
--------
This Android module is used to interface with the MelodySmart software library (which interfaces with the [BC118](https://www.bluecreation.com/product_info.php?products_id=64) BlueCreation BLE device).


Add Module to Project
---------------------
To include the module in another Android project, clone the repository at the root of the Android project to create a ```melodysmart-module-android``` directory. To include the module in the project, modify the ```settings.gradle``` file:

```
include ':app', ':melodysmart-module-android'
```

Then, add the following to the ```app/build.gradle``` file:

```
repositories {
    flatDir {
        dirs 'libs'
        dirs project(':melodysmart-module-android').file('libs')
    }
}
```

If you are using version control for the android project, you will also likely want to add the repository as a submodule to your Android project's repository. You can do this with ```git submodule add https://github.com/CMU-CREATE-Lab/melodysmart-module-android.git```, then add the following line to the ```.gitmodules``` file:

```
ignore = dirty
```

This document was last written for Android Studio version 2.2.0 using Gradle version 2.14.1.


Code Implementation
-------------------
The three main abstract classes to implement are DataListener, DeviceListener, and DeviceHandler. DeviceListener provides callbacks for the BLE device connect/disconnect. DataListener provides callbacks for the DataService connection as well as receiving messages (data) from the BLE device. DeviceHandler defines the initialization of the listeners and MessageQueue.

