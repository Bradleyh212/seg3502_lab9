package seg3x02.tempconverterapi

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.Base64

@SpringBootTest
@AutoConfigureMockMvc
class TemperatureApiSecurityTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @ParameterizedTest
    @ValueSource(strings = [
        "/temperature-converter/celsius-fahrenheit/50",
        "/temperature-converter/fahrenheit-celsius/122",
        "/",
        "/v3/api-docs"
    ])
    fun `requests without credentials require Basic authentication`(path: String) {
        mockMvc.perform(get(path))
            .andExpect(status().isUnauthorized)
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Realm\""))
    }

    @ParameterizedTest
    @CsvSource(
        "user1, pass1, celsius-fahrenheit/50, 122.0",
        "user1, pass1, fahrenheit-celsius/122, 50.0",
        "user2, pass2, celsius-fahrenheit/50, 122.0",
        "user2, pass2, fahrenheit-celsius/122, 50.0"
    )
    fun `both users can access both conversions`(
        username: String, password: String, conversion: String, expected: String
    ) {
        mockMvc.perform(get("/temperature-converter/$conversion")
            .header(HttpHeaders.AUTHORIZATION, basic(username, password)))
            .andExpect(status().isOk)
            .andExpect(content().string(expected))
    }

    @ParameterizedTest
    @CsvSource("user1, wrong", "user2, wrong", "unknown, pass1")
    fun `invalid credentials are rejected`(username: String, password: String) {
        mockMvc.perform(get("/temperature-converter/celsius-fahrenheit/50")
            .header(HttpHeaders.AUTHORIZATION, basic(username, password)))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `successful authentication does not authenticate the next request`() {
        mockMvc.perform(get("/temperature-converter/celsius-fahrenheit/50")
            .header(HttpHeaders.AUTHORIZATION, basic("user1", "pass1")))
            .andExpect(status().isOk)
        mockMvc.perform(get("/temperature-converter/celsius-fahrenheit/50"))
            .andExpect(status().isUnauthorized)
    }

    private fun basic(username: String, password: String): String =
        "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray(Charsets.UTF_8))
}
