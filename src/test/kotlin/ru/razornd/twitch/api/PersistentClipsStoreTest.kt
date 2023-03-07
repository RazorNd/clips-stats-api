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

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant


private val models = listOf(
    ClipModel(
        "AffluentGrossWolfCoolStoryBro-wSMEUglI-VtbOGCR",
        26819117,
        198848559,
        1756245080,
        518203,
        "Налёт",
        571,
        Instant.parse("2023-03-05T12:50:15Z"),
        30.0,
        13383
    ),
    ClipModel(
        "NiceCarefulLadiesAliens-I9U3ILqy9oyP_oKJ",
        26819117,
        91499491,
        1756245080,
        394568,
        "РЕНДИ > Ф1 ГОНКА БАХРЕЙН",
        125,
        Instant.parse("2023-03-05T12:36:03Z"),
        28.0,
        13557
    ),
    ClipModel(
        "GloriousDignifiedSushiRitzMitz-8r3S2fohJNogJHKa",
        43899589,
        145561907,
        1755428285,
        490422,
        "Кекс про отдых в Тае",
        50,
        Instant.parse("2023-03-04T20:53:24Z"),
        4.9,
        21427
    ),
)


private val expected = listOf(
    Clip(
        "AffluentGrossWolfCoolStoryBro-wSMEUglI-VtbOGCR",
        1756245080,
        "Налёт",
        571,
        13383,
        30.0,
        Instant.parse("2023-03-05T12:50:15Z")
    ),
    Clip(
        "NiceCarefulLadiesAliens-I9U3ILqy9oyP_oKJ",
        1756245080,
        "РЕНДИ > Ф1 ГОНКА БАХРЕЙН",
        125,
        13557,
        28.0,
        Instant.parse("2023-03-05T12:36:03Z")
    ),
    Clip(
        "GloriousDignifiedSushiRitzMitz-8r3S2fohJNogJHKa",
        1755428285,
        "Кекс про отдых в Тае",
        50,
        21427,
        4.9,
        Instant.parse("2023-03-04T20:53:24Z")
    )
)

class PersistentClipsStoreTest {

    private val repository: ClipRepository = mockk()

    private val store = PersistentClipsStore(repository)

    @Test
    fun find() {
        val after = Instant.parse("2023-03-04T20:53:20Z")

        coEvery {
            repository.findTop50ByCreatedAtAfterAndViewCountGreaterThanEqualOrderByCreatedAt(
                after,
                50
            )
        } returns models.asFlow()

        val actual = runBlocking { store.find(after) }

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }
}
