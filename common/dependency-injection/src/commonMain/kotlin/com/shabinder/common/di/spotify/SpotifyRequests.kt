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

package com.shabinder.common.di.spotify

import com.shabinder.common.di.gaana.corsApi
import com.shabinder.common.models.spotify.Album
import com.shabinder.common.models.spotify.PagingObjectPlaylistTrack
import com.shabinder.common.models.spotify.Playlist
import com.shabinder.common.models.spotify.Track
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private val BASE_URL get() = "${corsApi}https://api.spotify.com/v1"

interface SpotifyRequests {

    val httpClient: HttpClient

    suspend fun authenticateSpotifyClient(override: Boolean = false): HttpClient?

    suspend fun getPlaylist(playlistID: String): Playlist {
        return httpClient.get("$BASE_URL/playlists/$playlistID")
    }

    suspend fun getPlaylistTracks(
        playlistID: String?,
        offset: Int = 0,
        limit: Int = 100
    ): PagingObjectPlaylistTrack {
        return httpClient.get("$BASE_URL/playlists/$playlistID/tracks?offset=$offset&limit=$limit")
    }

    suspend fun getTrack(id: String?): Track {
        return httpClient.get("$BASE_URL/tracks/$id")
    }

    suspend fun getEpisode(id: String?): Track {
        return httpClient.get("$BASE_URL/episodes/$id")
    }

    suspend fun getShow(id: String?): Track {
        return httpClient.get("$BASE_URL/shows/$id")
    }

    suspend fun getAlbum(id: String): Album {
        return httpClient.get("$BASE_URL/albums/$id")
    }

    suspend fun getResponse(url: String): String {
        return httpClient.get(url)
    }
}
