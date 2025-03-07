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

package list

import com.shabinder.common.list.SpotiFlyerList
import com.shabinder.common.list.SpotiFlyerList.State
import extras.RenderableComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.css.flexGrow
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.html.id
import react.RBuilder
import styled.css
import styled.styledDiv
import styled.styledSection

class ListScreen(
    props: Props<SpotiFlyerList>,
) : RenderableComponent<SpotiFlyerList, State>(props,initialState = State()) {

    override val stateFlow: Flow<SpotiFlyerList.State> = model.models

    override fun RBuilder.render() {

        val result = state.data.queryResult

        styledSection {
            attrs {
                id = "list-screen"
            }

            if(result == null){
                LoadingAnim {  }
            }else{
                CoverImage {
                    coverImageURL = result.coverUrl
                    coverName = result.title
                }

                DownloadAllButton {
                    isActive = state.data.trackList.size > 1
                    downloadAll = {
                        model.onDownloadAllClicked(state.data.trackList)
                    }
                    link = state.data.link
                }

                styledDiv{
                    css {
                        display =Display.flex
                        flexGrow = 1.0
                        flexDirection = FlexDirection.column
                        color = Color.white
                    }
                    state.data.trackList.forEachIndexed{ _, trackDetails ->
                        TrackItem {
                            details = trackDetails
                            downloadTrack = model::onDownloadClicked
                        }
                    }
                }
            }

            css {
                classes = mutableListOf("list-screen")
                display = Display.flex
                padding(8.px)
                flexDirection = FlexDirection.column
                flexGrow = 1.0
            }
        }
    }
}