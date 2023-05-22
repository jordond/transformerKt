<p align="center"> 
   <img height="250" src="art/logo-text.png"/> 
</p>

# tKt

TransformerKt is a Kotlin coroutine wrapper
around [media3.transformer](https://developer.android.com/guide/topics/media/transformer).

> Transformer is an API for editing media, including converting between formats (transcoding),
> applying changes like trimming a clip from a longer video, cropping a portion of the video frame,
> applying custom effects, and other editing operations

## Table of Contents

- [Motivation](#motivation)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Transform Requests](#transform-requests)
- [Applying Effects](#applying-effects)
- [License](#license)

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

In `build.gradle.kts`:

```kotlin
allprojects {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Or `settings.gradle.kts`:

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

First you should familiarize yourself with
the [Transformer Docs](https://developer.android.com/guide/topics/media/transformer).

### Inputs

Then you need an input video or image file. TransformerKt supports the following inputs:

- [MediaItem](https://developer.android.com/reference/androidx/media3/common/MediaItem).
- [EditedMediaItem](https://github.com/androidx/media/blob/0fce8f416b54124605c1ed8aa72af98c94602834/libraries/transformer/src/main/java/androidx/media3/transformer/EditedMediaItem.java).
    - Note this class is new as of `media3` version `1.1.0-alpha01`. The library changed the way you
      apply effects and customizations to the MediaItem.
- A `Uri` pointing to somewhere on the device.
- A `File` object pointing to a file in the _app's_ sandboxed storage.
    - Warning: Getting a `File` object to a file outside of the app's storage will probably cause a
      permission error.

Now that you have your input sorted, there are two ways to consume this library.

### Extension functions

A few extension functions have been added to the `Transformer` instance.

- `suspend fun Transformer.start(): TransformerKt.Status.Finished`
- `fun Transformer.start(): Flow<TransformerKt.Status>`

There are overloads for each of the supported [TransformerKt.Input]s. For example:

```kotlin
suspend fun transform(context: Context, input: Uri) {
    val output = File(context.filesDir, "output.mp4")
    val transformer = Transformer.Builder(context).build()
    val result = transformer.start(input, output, TransformerKt.H264Request) { progress ->
        // Update UI progress
    }
    when (result) {
        is TransformerKt.Status.Failure -> TODO()
        is TransformerKt.Status.Success -> TODO()
    }
}
```

Or you can use the `Flow` version instead:

```kotlin
fun transform(context: Context, input: Uri) {
    val output = File(context.filesDir, "output.mp4")
    val transformer = Transformer.Builder(context).build()
    transformer.start(input, output, TransformerKt.H264Request).collect { status ->
        when (status) {
            is TransformerKt.Status.Progress -> TODO()
            is TransformerKt.Status.Success -> TODO()
            is TransformerKt.Status.Failure -> TODO()
        }
    }
}
```

### TransformerKt class

The `TransformerKt` class is a wrapper around the `Transformer` class. It exposes the same API as
the extension functions but you do not need to create a `Transformer` instance yourself.

You can create an instance like so:

```kotlin
val transformer: TransformerKt = TransformerKt.create(context)
```

Then like the extension functions you can can call `suspend fun TransformerKt.start()`:

```kotlin
// First you need to wrap the input in a TransformerKt.Input
val input = TransformerKt.Input.from(inputUri)
val result = transformer.start(input, output) { progress ->
    // Handle UI progress updates
}

// Handle result
```

Or as a `Flow`:

```kotlin
transformer.start(input, output).collect { status ->
    when (status) {
        is TransformerKt.Status.Progress -> TODO()
        is TransformerKt.Status.Success -> TODO()
        is TransformerKt.Status.Failure -> TODO()
    }
}
```

## Transform Requests

Now that you understand _how_ to use the library, you need to understand _what_ you can do with it.

First take a look at
the [Transformer Transformation Docs](https://developer.android.com/guide/topics/media/transformer/transformations)
so you can see what is possible.

**Note:** The documentation is currently not up to date with the latest version of `media3`. So some
things may be different.

By default, the library uses a default instance of `TransformationRequest` which most likely will
not do anything to your input file. Therefore you need to provide your own `TransformationRequest`
to the library, or use one of the predefined ones.

Currently `TransformerKt` ships with:

- `TransformerKt.H264Request`
    - Converts the input to an H264 encoded MP4 file.

### Example Requests

You can modify this request by using the provided `buildWith {}` extension function
for `TransformationRequest.Builder`.

Convert a video to an H264 encoded MP4 file, with AAC audio:

```kotlin
val request = TransformerKt.H264Request.buildWith {
    setAudioMimeType(MimeTypes.AUDIO_AAC)
}
```

Convert a HDR video to a SDR video:

```kotlin
val request = TransformerKt.H264Request.buildWith {
    setHdrMode(TransformationRequest.HDR_MODE_TONE_MAP_HDR_TO_SDR_USING_OPEN_GL)
}
```

## Applying Effects

Starting with version `1.1.0-alpha01`, the `Transformer` library changed the way you apply effects.
Instead of applying the effects to the `Transformer.Builder` you now create a `EditedMediaItem` and
apply the affects there.

To make that API a bit easier, an extension function has been added to `MediaItem.Builder`:

```kotlin
val editedMediaItem = MediaItem.Builder()
    .setUri(Uri.parse("https://example.com/video.mp4"))
    .setMediaId("Foo")
    .edited {
        setRemoveAudio(true)
    }

// Pass to Transformer.start()
```

Or directly from a [MediaItem] instance:

```kotlin
val editedMediaItem = MediaItem
    .fromUri(Uri.parse("https://example.com/video.mp4"))
    .edited {
        setRemoveAudio(true)
    }

// Pass to Transformer.start()
```

## License

See [LICENSE](LICENSE)