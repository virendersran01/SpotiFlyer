/*
 *  * Copyright (c)  2021  Shabinder Singh
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License
 *  *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.shabinder.common.di

import co.touchlab.kermit.Kermit
import com.shabinder.common.database.createDatabase
import com.shabinder.common.di.utils.removeIllegalChars
import com.shabinder.common.models.DownloadResult
import com.shabinder.common.models.TrackDetails
import com.shabinder.database.Database
import io.ktor.client.request.get
import io.ktor.client.statement.HttpStatement
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt

expect class Dir(
    logger: Kermit,
    database: Database? = createDatabase()
) {
    val db: Database?
    fun isPresent(path: String): Boolean
    fun fileSeparator(): String
    fun defaultDir(): String
    fun imageCacheDir(): String
    fun createDirectory(dirPath: String)
    suspend fun cacheImage(image: Any, path: String) // in Android = ImageBitmap, Desktop = BufferedImage
    suspend fun loadImage(url: String): Picture
    suspend fun clearCache()
    suspend fun saveFileWithMetadata(mp3ByteArray: ByteArray, trackDetails: TrackDetails)
    fun addToLibrary(path: String)
}

suspend fun downloadFile(url: String): Flow<DownloadResult> {
    return flow {
        val client = createHttpClient()
        val response = client.get<HttpStatement>(url).execute()
        val data = ByteArray(response.contentLength()!!.toInt())
        var offset = 0
        do {
            val currentRead = response.content.readAvailable(data, offset, data.size)
            offset += currentRead
            val progress = (offset * 100f / data.size).roundToInt()
            emit(DownloadResult.Progress(progress))
        } while (currentRead > 0)
        if (response.status.isSuccess()) {
            emit(DownloadResult.Success(data))
        } else {
            emit(DownloadResult.Error("File not downloaded"))
        }
        client.close()
    }
}
fun getNameURL(url: String): String {
    return url.substring(url.lastIndexOf('/', url.lastIndexOf('/') - 1) + 1, url.length).replace('/', '_')
}
/*
* Call this function at startup!
* */
fun Dir.createDirectories() {
    createDirectory(defaultDir())
    createDirectory(imageCacheDir())
    createDirectory(defaultDir() + "Tracks/")
    createDirectory(defaultDir() + "Albums/")
    createDirectory(defaultDir() + "Playlists/")
    createDirectory(defaultDir() + "YT_Downloads/")
}
fun Dir.finalOutputDir(itemName: String, type: String, subFolder: String, defaultDir: String, extension: String = ".mp3"): String =
    defaultDir + removeIllegalChars(type) + this.fileSeparator() +
        if (subFolder.isEmpty())"" else { removeIllegalChars(subFolder) + this.fileSeparator() } +
        removeIllegalChars(itemName) + extension
