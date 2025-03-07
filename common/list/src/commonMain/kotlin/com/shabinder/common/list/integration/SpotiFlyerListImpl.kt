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

package com.shabinder.common.list.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.shabinder.common.di.Picture
import com.shabinder.common.list.SpotiFlyerList
import com.shabinder.common.list.SpotiFlyerList.Dependencies
import com.shabinder.common.list.SpotiFlyerList.State
import com.shabinder.common.list.store.SpotiFlyerListStore.Intent
import com.shabinder.common.list.store.SpotiFlyerListStoreProvider
import com.shabinder.common.list.store.getStore
import com.shabinder.common.models.TrackDetails
import kotlinx.coroutines.flow.Flow

internal class SpotiFlyerListImpl(
    componentContext: ComponentContext,
    dependencies: Dependencies
) : SpotiFlyerList, ComponentContext by componentContext, Dependencies by dependencies {

    private val store =
        instanceKeeper.getStore {
            SpotiFlyerListStoreProvider(
                dir = this.dir,
                storeFactory = storeFactory,
                fetchQuery = fetchQuery,
                downloadProgressFlow = downloadProgressFlow,
                link = link,
                showPopUpMessage = showPopUpMessage
            ).provide()
        }

    override val models: Flow<State> = store.states

    override fun onDownloadAllClicked(trackList: List<TrackDetails>) {
        store.accept(Intent.StartDownloadAll(trackList))
    }

    override fun onDownloadClicked(track: TrackDetails) {
        store.accept(Intent.StartDownload(track))
    }

    override fun onBackPressed() {
        listOutput.callback(SpotiFlyerList.Output.Finished)
    }

    override fun onRefreshTracksStatuses() {
        store.accept(Intent.RefreshTracksStatuses)
    }

    override suspend fun loadImage(url: String): Picture = dir.loadImage(url)
}
