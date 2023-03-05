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

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant

private val expected = listOf(
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
    ClipModel(
        "TiredDifferentFlamingoYee-0yMSzsPygt-gQJmy",
        26819117,
        62809808,
        1755227025,
        518203,
        "...",
        55,
        Instant.parse("2023-03-04T15:46:40Z"),
        44.4,
        27497
    ),
).sortedBy { it.createdAt }

@Testcontainers(disabledWithoutDocker = true)
@DataR2dbcTest(
    properties = [
        "spring.r2dbc.url=r2dbc:tc:postgresql:///twitch?TC_IMAGE_TAG=14-alpine&timeZone=UTC",
        "spring.sql.init.mode=always",
        "logging.level.io.r2dbc.postgresql.QUERY=debug",
        "logging.level.io.r2dbc.postgresql.PARAM=debug"
    ]
)
class ClipRepositoryTest {

    @Autowired
    lateinit var repository: ClipRepository

    @Test
    fun `find after date`() {

        val models = runBlocking {
            repository.findTop50ByCreatedAtAfterAndViewCountGreaterThanEqualOrderByCreatedAt(Instant.parse("2023-03-04T15:46:39Z"), 50)
                .toList()
        }

        assertThat(models)
            .usingRecursiveComparison()
            .ignoringFields("createdAt")
            .isEqualTo(expected)
    }
}
