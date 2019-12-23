# MonthlyActivity

Github-style activity view for a month.

[![JitPack](https://jitpack.io/v/primaverahq/MonthlyActivity.svg)](https://jitpack.io/#primaverahq/MonthlyActivity) 
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/primaverahq/MonthlyActivity/blob/master/LICENSE.md) 

![Preview](https://raw.githubusercontent.com/primaverahq/MonthlyActivity/master/images/screenshot.png)

#### Features

- Setting a month and a year to be shown.
- Automatic selection of first day of the week based on locale.
- Customizable tile size and color.
- Days tiles click events.

#### Usage

Add the JitPack repository to your project level `build.gradle`:

```groovy
allprojects {
 repositories {
    google()
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```

Add this to your app `build.gradle`:

```groovy
dependencies {
	implementation 'com.github.primaverahq:MonthlyActivity:<latest-version>'
}
```

Add a view to your XML: 

```xml
<com.primaverahq.monthlyactivity.MonthlyActivityView
    android:id="@+id/activityView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```
Convert your data to `Map<Int, Int>` where keys are days of month and values are actual values. Note that following `java.util.Calendar` rules, days are counted from 1 and months are counted from 0. Set a [ColorEvaluator](https://github.com/primaverahq/MonthlyActivity/blob/master/library/com/primaverahq/monthlyactivity/ColorEvaluator.kt) and load your data:

```kotlin
val baseColor = Color.parseColor("#ee5454")
activityView.colorEvaluator = LinearAlphaEvaluator(baseColor)
activityView.onTileClickListener = listener
activityView.setData(2019, 11, data)
```

#### What is yet to come
- Test coverage.
- Animations.
- More in-built evaluators, if needed.
- Week and year views.

#### Contributing
Feel free to join us and make this small library better. Open an issue and/or submit a pull request. Contributions are very welcome.

#### License

MonthlyActivity is distributed under the MIT license. See [LICENSE](https://github.com/primaverahq/MonthlyActivity/blob/master/LICENSE.md) for details.