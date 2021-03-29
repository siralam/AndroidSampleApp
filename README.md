# AndroidSampleApp

This is a sample Android app that allows you to query weather data.  
Objective of this app is to demonstrate my ideal app architecture in a simpliest way.  
Below shows its current status - what has been implemented, and what is in the planning but not yet implemented.

If you have any comment, such as if you think something isn't implemented in a correct way, or does not follow best practice, please feel free to open an issue and discuss together.

## Implemented

- API call with Retrofit and OKHttp
- Use Kotlin coroutine to do API calls on background thread
- Hide API key from Git
- Hide API key from APK de-packing
- Code minify and obfuscation
- Use Jetpack[ViewModel, LiveData, Navigation] + MVVM
- Add Crashlytics
- Use Hilt to do DI
- Write Unit tests on ViewModel class

## To be implemented
- Use Room for local DB
- Write Unit tests on Model class
- Use Android Compose for UI
- Write instrumental tests
- Robust Logging Tool
- Light and Dark themes
- Caching data in Repository

## Preparation before build

Of course I do not place my api key in version control, so before you download the project and try to build and run it, you have to place your api key inside first.

Make sure you installed `CMake` and `NDK` from SDK Manager -> SDK Tools.  
After that, create `app/src/main/cpp/libnative-lib.cpp` with the below content:

```C++
#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring
JNICALL
Java_com_asksira_androidsampleapp_network_ApiKeyStore_getWeatherApiKey(JNIEnv* env, jobject) {
    std::string api_key = "Put your own API key here";
    return env->NewStringUTF(api_key.c_str());
}
```

Remember to replace `Put your own API key here` with your real API key you get from registering https://openweathermap.org/.

## FAQ

Q: Why don't you use DataBinding library before writing UI in Compose?

A: Because the DataBinding library publisehd by Google SUCKS. It introduces a lot of difficulty in debugging due to generation of interdemiate classes and extremely insufficient error messages. Also, I think writing view logic in xml just to save a little bit more boilerplate code is a bad idea. Again, the reason is increased debugging difficulty.
<br/><br/>

Q: Why do you put `Double.kelvinToCelsius()` in ViewModel instead of Fragment(View)?

A: I stuggled on whether "How to display data" should be put in View or ViewModel. You know, it is sometimes very clumsy to put it in ViewModel. Consider this scenario: You have a `val user = LiveData<User>()` in your ViewModel. Server returns `user.points` to you in `Double`, and the value is `12345678.9999`, but you just want to display `12 million+` on your UI. If you do this conversion inside `View`, you can still enjoy simply using `val user = LiveData<User>()` in your ViewModel. However, if you put it in `ViewModel`, you have to break down `User` into a bunch of `LiveData` objects and one of them will be `val millionPoints = LiveData(Int)`. This adds a lot of biolerplate code to the VM, isn't it? However, imagine you want to write unit tests for your VM. How do you verify your result? Do your expected output to be `12` or `12345678.9999`? It should be `12` if the unit test makes sense. Therefore, in terms of best practice, I believe we should still put these "How to display" logic inside the VM.
<br/><br/>

Q: Why don't you use the `SingleLiveData` for showing the error dialog, instead calling vm to reset its value in observer?

A: First of all, I don't see the current approach has serious issues, according to what I know, it simply introduces several more lines of code, and the developer has to remember to call vm to reset the value inside the observation block (which is acceptable). Instead, if I go for `SingleLiveData`, there are new concerns: (1) This is not an officially approved and maintained approach by Google; (2) Assume some collaborator does not understand `SingleLiveData`, it takes time for him to understand what it is and why to use it; (3) It looks like officially Google is introducing Kotlin's `Flow` to solve this problem. May be instead of using `SingleLiveData`, let's look at how shall we apply that instead, in the future.
<br/><br/>

Q: Why do `SearchFragment` implements `OnRetryListener` instead of passing a lambda into `ErrorRetryDialogFragment`?

A: This is actually one of the most common mistakes I see in a lot of Android developers. You know, member variables in `Fragment` cannot survive configuration changes. Primitives can survive if you put them in arguments. But a callback (interface) cannot be put there. So, if the dialog appears, and the screen is rotated, those callbacks will never function anymore. 
<br/><br/>

Q: Why don't you use `Navigation` library when displaying error message dialog?

A: In order to make the retry function works, without polluting the scope outside the Fragment (e.g. Store something static), I have no choice but to let the dialog be able to execute a callback. If I use Navigation, I have no way to this. Come to think of it, showing an error message in a dialog is by nature not a navigation at all, so I think this is perfectly OK.
<br/><br/>

Q: Why don't you use `SavedStateHandle` to make your ViewModel survives process termination by the OS?

A: **I don't like over-engineering**. I value simplicity over complexity. To be exact, there should be a balance between them. Every time you increase your code's complexity, it should be because you think it introduce merits more than demerits. e.g. We use MVVM because we think it has a lot of advantages, such as making codes easier to read, to understand and to modify; not because it is a recommended design pattern. It should be the other way round. Therefore, if you don't really care about your user will need to start from the initial screen of the app when they come back after a long time, write codes to persist states through process termination not only increased development time, increased complexity for trivial benefit, but also increased time to test it.