# flagboard-android
**The Feature Flag Dashboard**

Flagboard has a small and familiar API that allows you to use "local" remote configurations (AKA Feature Flags) inside your app.
It could be loaded from services like Firebase Remote Config or Apptimize. Also, custom feature flags can be loaded on demand.
[![](https://jitpack.io/v/GrinGraz/flagboard-android.svg)](https://jitpack.io/#GrinGraz/flagboard-android)


## Usage
Add Flagboard to your project in the module level `build.gradle`

```groovy
dependencies {
    implementation 'com.github.GrinGraz:flagboard-android:Tag'
}
```

First, initialize Flagboard in your `Application` file.

```kotlin
Flagboard.init(applicationContext)
```

Then, load your feature flags into Flagboard. There is a `ConflicStrategy` to keep or replace when feature flags are loaded.
```kotlin
val mapOfFlags: Map<String, Any> = mapOf()
Flagboard.loadFlags(mapOfFlags, ConflictStrategy.Keep)
```

Afterward, the feature flags can be retrieved in your app with functions by value type.

```kotlin
val isFeatureEnabled: Boolean = Flagboard.getBoolean(stringKey)

if (isFeatureEnabled) {
    doSomethingGreat()
}
```

To display the Flagboards dashboard with the list of your loaded remote configuration call
```kotlin
Flagboard.open(context)
```

Then you will see this.

![image](https://github.com/GrinGraz/flagboard-android/assets/6061374/b12116f6-b714-493f-884e-492c19332476)

Here, you can modify the feature flags that will persist after a full restart of the app.

Also, Flagboard can be reset anywhere.

```kotlin
Flagboard.reset()
```

To retrieve the current status
```kotlin
Flagboard.getState()
```


