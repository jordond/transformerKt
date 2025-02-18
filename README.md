## Project status

This project is no longer maintained, please see #261.

<p align="center"> 
   <img height="200" src="art/logo-text.png"/>
</p>
<p align="center">
    <img src="https://img.shields.io/maven-central/v/dev.jordond.transformerkt/transformerkt">
    <a href="https://github.com/jordond/transformerKt/actions/workflows/ci.yml"><img src="https://github.com/jordond/transformerKt/actions/workflows/ci.yml/badge.svg"></img></a>
    <a href="https://developer.android.com/jetpack/androidx/releases/media3#1.6.0-alpha03-alpha01"><img src="https://img.shields.io/badge/media3-1.6.0-alpha03-alpha01?color=brightgreen" /></a>
    <img src="https://img.shields.io/github/license/jordond/transformerkt" />   
</p>

TransformerKt is a Kotlin coroutine wrapper library
around [media3.transformer](https://developer.android.com/guide/topics/media/transformer):

> Transformer is an API for editing media, including converting between formats (transcoding),
> applying changes like trimming a clip from a longer video, cropping a portion of the video frame,
> applying custom effects, and other editing operations

You can view the TransformerKt KDocs at [docs.transformerkt.dev](https://docs.transformerkt.dev)

- Using `media3.transformer` [version `1.6.0-alpha03-alpha01`](https://github.com/androidx/media/releases)

## Table of Contents

- [Motivation](#motivation)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Applying Effects](#applying-effects)
- [Composition](#composition)
- [Demo App](#demo-app)
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

Add the dependency to your app level `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("dev.jordond.transformerkt:transformerkt:3.7.0-alpha01")
}
```

Or using Version Catalogs:

```toml
[versions]
transformerkt = "3.7.0-alpha01"

[libraries]
transformerkt = { group = "dev.jordond.transformerkt", name = "transformerkt", version.ref = "transformerkt" }

```

## Usage

First you should familiarize yourself with
the [Transformer Docs](https://developer.android.com/guide/topics/media/transformer).

### Inputs

Then you need an input video or image file. `TransformerKt` supports the following inputs:

- [MediaItem](https://developer.android.com/reference/androidx/media3/common/MediaItem).
- [EditedMediaItem](https://github.com/androidx/media/blob/0fce8f416b54124605c1ed8aa72af98c94602834/libraries/transformer/src/main/java/androidx/media3/transformer/EditedMediaItem.java).
- A `Uri` pointing to somewhere on the device.
- A `File` object pointing to a file in the _app's_ sand-boxed storage.
    - Warning: Getting a `File` object to a file outside of the app's storage will probably cause a
      permission error.

Now that you have your input sorted, there are two ways to consume this library.

### Extension functions

A few extension functions have been added to the `Transformer` instance.

- `suspend fun Transformer.start(): TransformerStatus.Finished`
- `fun Transformer.start(): Flow<TransformerStatus>`

There are overloads for each of the supported inputs. For example:

```kotlin
suspend fun transform(context: Context, input: Uri) {
    val output = File(context.filesDir, "output.mp4")
    val transformer = TransformerKt.build(context) {
        setVideoMimeType(MimeTypes.VIDEO_H264)
    }
    val result = transformer.start(input, output) { progress ->
        // Update UI progress
    }
    when (result) {
        is TransformerStatus.Failure -> TODO()
        is TransformerStatus.Success -> TODO()
    }
}
```

Or you can use the `Flow` version instead:

```kotlin
fun transform(context: Context, input: Uri) {
    val output = File(context.filesDir, "output.mp4")
    val transformer = Transformer.build(context) { setVideoMimeType(MimeTypes.VIDEO_H264) }
    transformer.start(input, output).collect { status ->
        when (status) {
            is TransformerStatus.Progress -> TODO()
            is TransformerStatus.Success -> TODO()
            is TransformerStatus.Failure -> TODO()
        }
    }
}
```

## Applying Effects

Starting with Media3 `1.1.0-alpha01` the `Transformer` library changed the way you apply effects.
Instead of applying the effects to the `Transformer.Builder` you now create a `EditedMediaItem` and
apply the affects there.

To make that API a bit easier, an extension function `.edited {}` has been added
to `MediaItem.Builder`:

```kotlin
val editedMediaItem = MediaItem.Builder()
    .setUri(Uri.parse("https://example.com/video.mp4"))
    .setMediaId("Foo")
    .edited {
        setRemoveAudio(true)
    }

val result = TransformerKt.build(context).start(editedMediaItem, File("output.mp4"))
```

Or directly from a [MediaItem] instance:

```kotlin
val editedMediaItem = MediaItem
    .fromUri(Uri.parse("https://example.com/video.mp4"))
    .edited {
        setRemoveAudio(true)
    }

val result = TransformerKt.build(context).start(editedMediaItem, File("output.mp4"))
```

## Composition

Transformer now supports `Composition` which allows you to combine multiple inputs into a single
output. You can apply effects to the whole composition or on a per input basis:

```kotlin
data class MyComplexItem(val tag: String, val uri: Uri, val startMs: Long, val endMs: Long)

val items: List<Uri>
val complexItems: List<MyComplexItem>
val endCredits: File
val audioOverlay: File
val composition = compositionOf {
    // Apply effects to the whole composition
    effects {
        resolution(1920, 1080, LayoutScale.Fit)
    }

    // Create a sequence of inputs
    sequenceOf {
        items(items) { uri ->
            effects {
                bitmapOverlay(context, R.drawable.watermark) {
                    setScale(.2f, .2f)
                    setOverlayFrameAnchor(.8f, .8f)
                }
            }
        }

        items(
            items = complexItems,
            selector = { it.uri },
            configure = { complexItem ->
                // Configure the MediaItem instance
                setTag(complexItem.tag)
                setClippingConfiguration(complexItem.startMs, complexItem.endMs)
            },
        ) { complexItem ->
            setRemoveAudio(true)

            effects {
                speed(2f)
                brightness(0.5f)
            }
        }

        item(endCredits)
    }

    sequenceOf(isLooping = true) {
        item(audioOverlay)
    }
}
```

Checkout
the [`TransformerRepo.kt`](demo/src/main/java/dev/transformerkt/demo/transformer/TransformerRepo.kt)
file for more examples.

## Included Effects

Currently only one custom effect is included in this library. It is the `AudioProcessor`
called `VolumeChangeProcessor`. It allows you to change the volume of the audio track in the video.

```kotlin
val volumeChange = volumeChangeEffect(inputChannels = 2, volume = 0.5f)
// ... Add to your EditedMediaItemSequence
```

If you provide a `VolumeChangeProcessor` you are able to customize the volume based on the elapsed
time in the output video:

```kotlin
val volumeChange = volumeChangeEffect(inputChannels = 2) { elapsedMs ->
    when {
        elapsedMs < 1000 -> 0f
        elapsedMs < 2000 -> 0.5f
        else -> 1f
    }
}
```

For convenience there is a `fadeAudioOutEffect` that will fade the audio out over a given duration:

```kotlin
val volumeChange = fadeAudioOutEffect(
    totalDurationUs = 10_000_000,
    inputChannels = 2,
    initialVolume = 0.8f,
    finalVolume = 0.3f,
    fadeDurationUs = 2_000_000,
)
```

This effect will fade the audio from `0.8f` to `0.3f` over the course of `2_000_000` microseconds
(2 seconds) from the end of the video.

## Demo App

A demo app is included in the `demo` module. It is a simple app that allows you to select a HDR
video and convert it do a SDR video.

To run the demo app you can follow these steps:

```shell
git clone git@github.com:jordond/transformerkt.git transformerkt
cd transformerkt
./gradlew assembleRelease
```

Then install the `demo/build/outputs/apk/release/demo-release.apk` file on your device.

## License

See [LICENSE](LICENSE)
