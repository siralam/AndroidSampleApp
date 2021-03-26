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

## To be implemented
- Use Jetpack ViewModel and LiveData
- MVVM architecture
- Add Crashlytics
- Use Hilt to do DI
- Write Unit tests on Model class
- Use Room for local DB
- Write Unit tests on ViewModel class
- Use Android Compose for UI
- Write instrumental tests

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

Q: Why do you wrap another function over the Retrofit functions declared in `OpenWeatherService`, but seems to do nothing additional?  
A: This is because as the client side I cannot control how the API are designed, but I want the `OpenWeatherService` interface to be exactly the same as what the API is designed. Just like an API doc. And then, even if the API is badly designed, I can wrap it in a function that somehow "convert" the call into what the client side expects to do. By doing this, we won't pollute the core part of the client side code, for example passing a lot of nulls for parameters that I am not interested in.