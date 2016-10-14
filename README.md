# appbarlayout-spring-extension
One library contains one behavior help appbarlayout to scrll spring. In the sample have add the realtimeblur test and add tablayout with TabScrimHelper(animate color with CollapsingToolbarLayout).Because of some methods in the Behavior of AppBarLayout not open to the out package. So I set the package name same to the support design.

Screenshot
====
First is with spring. Second with blur and TabScrimHelper  

![](/screenshot/appbar_spring.gif) ![](/screenshot/appbar_spring_blur_tab.gif)  

Installation
====
```groovy
dependencies {
    compile 'com.todou.com:appbarspring:1.0.1'
}
```
Usages
====
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
