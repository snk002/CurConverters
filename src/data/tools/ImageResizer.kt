package data.tools

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.lang.Double.min
import java.lang.Integer.max
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

/**
 * Static image resizing helper
 */
object ImageResizer {

    /**
     * Resizes an image to an absolute width and height
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param scaledWidth absolute width in pixels
     * @param scaledHeight absolute height in pixels
     * @throws IOException
     */
    @Throws(IOException::class)
    fun resize(
        inputImagePath: String?,
        outputImagePath: String,
        scaledWidth: Int,
        scaledHeight: Int
    ) {
        val inputFile = File(inputImagePath)
        val inputImage = ImageIO.read(inputFile)
        val outputImage = BufferedImage(
            scaledWidth,
            scaledHeight, inputImage.type
        )
        val g2d = outputImage.createGraphics()
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null)
        g2d.dispose()
        // extracts extension of output file
        val formatName = outputImagePath.substring(
            outputImagePath
                .lastIndexOf(".") + 1
        )
        try {
            Files.createDirectories(Paths.get(outputImagePath))
        } catch (_: FileAlreadyExistsException) {} //ignore if already exists
        ImageIO.write(outputImage, formatName, File(outputImagePath))
    }

    /**
     * Resizes an image to an absolute width or height (proportional).
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param maxSide Maximum side size.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun resize(
        inputImagePath: String?,
        outputImagePath: String,
        maxSide: Int
    ) {
        val inputFile = File(inputImagePath)
        val inputImage = ImageIO.read(inputFile)
        val maxCurrentSize = max(inputImage.width, inputImage.height)
        val scale: Double = min(1.0, maxSide / maxCurrentSize.toDouble())
        resize(inputImagePath, outputImagePath, scale)
    }

    /**
     * Resizes an image by a percentage of original size (proportional).
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param percent percentage of the output image over the input image.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun resize(
        inputImagePath: String?,
        outputImagePath: String,
        percent: Double
    ) {
        val inputFile = File(inputImagePath)
        val inputImage = ImageIO.read(inputFile)
        val scaledWidth = (inputImage.width * percent).toInt()
        val scaledHeight = (inputImage.height * percent).toInt()
        resize(inputImagePath, outputImagePath, scaledWidth, scaledHeight)
    }
}