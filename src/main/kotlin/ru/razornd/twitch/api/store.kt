/*
 * Copyright 2023 Daniil Razorenov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ru.razornd.twitch.api

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

data class Clip(
    val id: String,
    val videoId: Long?,
    val title: String,
    val viewCount: Int,
    val offset: Int?,
    val duration: Double,
    val createdAt: Instant
)

interface ClipsStore {

    suspend fun find(after: Instant): List<Clip>

}

@Service
open class PersistentClipsStore(private val repository: ClipRepository) : ClipsStore {

    @Transactional(readOnly = true)
    override suspend fun find(after: Instant) =
        repository.findTop50ByCreatedAtAfterAndViewCountGreaterThanEqualOrderByCreatedAt(after, 50)
            .map { it.asDto() }
            .toList()

    private fun ClipModel.asDto() = Clip(id, videoId, title, viewCount, vodOffset, duration, createdAt)
}
