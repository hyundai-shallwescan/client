package com.ite.sws.common.util


import android.content.Context
import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.VideoView
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

object VideoUtil {

    fun compressVideo(
        context: Context,
        inputUri: Uri,
        outputFilePath: String,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        try {
            val extractor = MediaExtractor()
            extractor.setDataSource(context, inputUri, null)
            val trackIndex = selectTrack(extractor)
            if (trackIndex < 0) {
                throw IllegalArgumentException("No video track found in the file")
            }
            extractor.selectTrack(trackIndex)
            val inputFormat = extractor.getTrackFormat(trackIndex)

            // Configure encoder
            val outputFormat = MediaFormat.createVideoFormat(
                MediaFormat.MIMETYPE_VIDEO_AVC,
                inputFormat.getInteger(MediaFormat.KEY_WIDTH),
                inputFormat.getInteger(MediaFormat.KEY_HEIGHT)
            )
            outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1000000)
            outputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)

            val encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            encoder.start()

            val muxer = MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val muxerTrackIndex = muxer.addTrack(outputFormat)
            muxer.start()

            val buffer = ByteBuffer.allocate(1024 * 1024)
            val bufferInfo = MediaCodec.BufferInfo()

            var outputDone = false
            while (!outputDone) {
                bufferInfo.offset = 0
                bufferInfo.size = extractor.readSampleData(buffer, 0)
                if (bufferInfo.size < 0) {
                    outputDone = true
                    bufferInfo.size = 0
                } else {
                    bufferInfo.presentationTimeUs = extractor.sampleTime
                    muxer.writeSampleData(muxerTrackIndex, buffer, bufferInfo)
                    extractor.advance()
                }
            }

            encoder.stop()
            encoder.release()
            muxer.stop()
            muxer.release()
            extractor.release()

            onSuccess(outputFilePath)

        } catch (e: Exception) {
            Log.e("VideoUtil", "Error compressing video: ${e.message}")
            onFailure(e)
        }
    }


    private fun selectTrack(extractor: MediaExtractor): Int {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                return i
            }
        }
        return -1
    }

    fun captureThumbnail(context: Context, uri: Uri, prefix: String): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val bitmap = retriever.frameAtTime
        val file = File(context.cacheDir, "${prefix}_thumbnail.jpg")
        FileOutputStream(file).use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        retriever.release()
        return file.path
    }

    fun getVideoDimensions(context: Context, uri: Uri): Pair<Int, Int> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val width =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
        val height =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()
                ?: 0
        retriever.release()
        return Pair(width, height)
    }

    fun uriToFilePath(context: Context, uri: Uri): String {
        var filePath = ""
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                filePath = it.getString(it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
            }
        }
        return filePath
    }

    fun removeVideo(videoView: VideoView, callback: () -> Unit) {
        videoView.stopPlayback()
        callback()
    }
}
