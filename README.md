[![](https://jitpack.io/v/brijoe/DH.svg)](https://jitpack.io/#brijoe/DH)
[![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://developer.android.com/guide/topics/manifest/uses-sdk-element.html#ApiLevels)
[![Gradle Version](https://img.shields.io/badge/gradle-2.10%2B-green.svg)](https://docs.gradle.org/current/release-notes)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

# DH 

[切换为中文](README-zh.md)

A development tools library for Android. 
The library provides you with a quick access portal, you can shake your phone  to show debugging dialog at any time .Even you can watch the app's network log directly.

**Notice:** 
You must use OKHttp library to perform network request,because the DH libary depends on OkHttp Interceptor.

<img src="images/Screenshot.png" width="750" />


## Getting started


Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

In your module build.gradle:

```
dependencies {
		 ...
	    implementation 'com.github.brijoe:DH:1.1.3'
	    ...
	}

```
**Notice:if your module has compiled okhttp interceptor library,you can
exclude the inbuilt one to avoid conflict.**

as following:

```
 implementation ('com.github.brijoe:DH:1.1.3'){
            exclude group: 'com.squareup.okhttp3'
        }

```
In your Application class:

```
public class MyApplication extends Application {

  @Override public void onCreate() {
    	DH.install(this);
  }
}
```
Want to watch the network log of your app ?add the `DHInterceptor` to the Interceptor chains.


```
 OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().clear();
        builder.writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                //your own interceptor
                .addInterceptor(busInterceptor)
                //add DHInterceptor
                .addInterceptor(new DHInterceptor())
                .readTimeout(30, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();

```


## Next
Now shake your phone，and the dialog will show up.

## Customize 

### decide whether to show dialog or not 

You are allowed to decide whether the dialog is displayed by
passing a value of boolean type to `install()`method，especially when your app is in release mode.

```
	DH.install(context,boolean);
```

### customize your dialog items
You can also add your menu item to the dialog if you are not satisfied with it.


```
 DH.addDebugger(new Debugger(0,"My Item", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyApplication.this,"you click my item",Toast.LENGTH_SHORT).show();
            }
        }));

```


## License
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```

