# appbarlayout-spring-behavior
One library contains one behavior help appbarlayout to scrll spring. In the sample have add the realtimeblur test and add tablayout with TabScrimHelper(animate color with CollapsingToolbarLayout).Because of some methods in the Behavior of AppBarLayout not open to the out package. So I set the package name same to the support design.And add one fling_fix_behavior to fix the appbarlayout to scroll down fling. Also add one NestedFixFlingScrollView to fix NestedScrollView fling bug.

   * AppBarLayout Spring
   * AppBarLayout Fling fix
   * NestedScrollView Fling Fix

[Download Sample](https://github.com/ToDou/appbarlayout-spring-behavior/releases)

Screenshot
====
First is with spring. Second with blur and TabScrimHelper.Third with fling fix  

![](/screenshot/appbar_spring.gif) ![](/screenshot/appbar_spring_blur_tab.gif) ![](/screenshot/appbar_scrollview_fling_fix.gif)

Installation
====
```groovy
dependencies {
    compile 'com.github.todou:appbarspring:1.0.2'
}
```
Usages
====
####AppBarLayout-Spring
```xml
<android.support.design.widget.AppBarLayout
        ...
        app:layout_behavior="@string/appbar_spring_behavior"
        ...>
        ...
</android.support.design.widget.AppBarLayout>
```
if you want add blur with the spring.You can add this by the [RealtimeBlurView](https://github.com/mmin18/RealtimeBlurView):
```java
final RealtimeBlurView realtimeBlurView = (RealtimeBlurView) findViewById(R.id.real_time_blur_view);
AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
AppBarLayoutSpringBehavior springBehavior = (AppBarLayoutSpringBehavior) ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
springBehavior.setSpringOffsetCallback(new AppBarLayoutSpringBehavior.SpringOffsetCallback() {
      @Override
      public void springCallback(int offset) {
           int radius = 20 * (240 - offset > 0 ? 240 - offset : 0) / 240;
           realtimeBlurView.setBlurRadius(radius);
      }
});
```
With the tablayout you can add **TabScrimHelper** to let TabLayout animate color with the CollapsingToolbarLayout:
```java
TabScrimHelper tabScrimHelper = new TabScrimHelper(tabLayout, collapsingToolbarLayout);
appBarLayout.addOnOffsetChangedListener(tabScrimHelper);
```
####AppBarLayout-Fling Fix
Maybe you will find the appbar fling when scroll down is not smooth. You can try this appbar fling fix behavior:
```xml
<android.support.design.widget.AppBarLayout
        ...
        app:layout_behavior="@string/appbar_fling_fix_behavior"
        ...>
        ...
</android.support.design.widget.AppBarLayout>
```
####NestScrollView-Fling Fix
To let NestScrollView fling when scroll up.Just use the **NestedFixFlingScrollView**

License
====
<pre>
Copyright 2016 ToDou

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
