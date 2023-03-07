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

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant

@WebFluxTest
class ClipsApiTest {

    @MockkBean
    lateinit var store: ClipsStore

    // language=JSON
    private val body = """
        [
          {
            "id": "j0tM78y1HAQ",
            "videoId": 59,
            "title": "booty",
            "viewCount": 45996,
            "offset": 1157530526,
            "duration": 16.9,
            "createdAt": "2019-03-25T03:40:42Z"
          },
          {
            "id": "YR9KdH5j72O",
            "videoId": 278126720,
            "title": "rom",
            "viewCount": 548611,
            "offset": 94,
            "duration": 54.0,
            "createdAt": "2019-03-25T12:13:01Z"
          },
          {
            "id": "hgTbhUsD2SY",
            "videoId": 1448532184,
            "title": "local",
            "viewCount": 7879392,
            "offset": 39229529,
            "duration": 41.0,
            "createdAt": "2019-03-25T19:57:51Z"
          },
          {
            "id": "QE2xVfz3A6p",
            "videoId": 1845565898,
            "title": "choice",
            "viewCount": 87388,
            "offset": 9750,
            "duration": 23.0,
            "createdAt": "2019-03-25T14:11:59Z"
          }
        ]
    """.trimIndent()

    @Test
    fun `get clips after date`(@Autowired client: WebTestClient) {
        val after = "2020-11-12T12:17:54Z"

        coEvery { store.find(Instant.parse(after)) } returns listOf(
            Clip("j0tM78y1HAQ", 59, "booty", 45996, 1157530526, 16.9, Instant.parse("2019-03-25T03:40:42Z")),
            Clip("YR9KdH5j72O", 278126720, "rom", 548611, 94, 54.0, Instant.parse("2019-03-25T12:13:01Z")),
            Clip("hgTbhUsD2SY", 1448532184, "local", 7879392, 39229529, 41.0, Instant.parse("2019-03-25T19:57:51Z")),
            Clip("QE2xVfz3A6p", 1845565898, "choice", 87388, 9750, 23.0, Instant.parse("2019-03-25T14:11:59Z"))
        )

        client.get()
            .uri { it.path("/api/clips").queryParam("after", after).build() }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody().json(body)
    }

    @Test
    fun `error on empty after query param`(@Autowired client: WebTestClient) {
        client.get()
            .uri("/api/clips")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Bad Request")
    }
}
