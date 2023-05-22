## TransformerKt

A Kotlin coroutine wrapper
around [media3.transformer](https://developer.android.com/guide/topics/media/transformer).

> Transformer is an API for editing media, including converting between formats (transcoding),
> applying changes like trimming a clip from a longer video, cropping a portion of the video frame,
> applying custom effects, and other editing operations

## Motivation

The `media3.transformer` API is Java based and therefore relies on callbacks to notify the caller of
the result of an operation. This library wraps the API in a Kotlin coroutine based API to make it
easier to use. It exposes the `Transformer` API as either a `suspend` function or a `Flow`.

This library also includes some helpful extension functions to make it easier to use the API.
See [Usage](#usage) for more information.

**Note:** Due to the way `Transformer` works, the coroutines must be launched on
the `Dispatchers.Main` thread, otherwise the API will throw an `IllegalStateException`. Since it
relies on the current thread to contain a `Looper`. While it is launched on the
main-thread, `Transformer` delegates all the heavy lifting off of the main thread.
See [the docs](https://developer.android.com/guide/topics/media/transformer/getting-started#threading)
for more information.

## Getting Started

First you need to add jitpack to either your root level `build.gradle.kts` or
your `settings.gradle.kts` file:

`build.gradle.kts`:

```kotlin
allprojects {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

`settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the dependency to your app level `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.github.jordond:transformerkt:1.0.0")
}
```

## Usage

TODO

## License

See [LICENSE](LICENSE)