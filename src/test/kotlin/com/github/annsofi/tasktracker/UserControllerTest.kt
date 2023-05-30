import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.annsofi.tasktracker.TaskTrackerApplication
import com.github.annsofi.tasktracker.model.UserDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = [TaskTrackerApplication::class])
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    val objectMapper = jacksonObjectMapper()

    @Test
    fun `addUser should create a new user`() {
        val userDto = UserDto(
            name = "user1",
            email = "user1@test.com",
        )

        val userResponse = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)),
        )
            .andExpect(status().isOk)
            .andReturn()

        val createdUser = objectMapper.readValue(userResponse.response.contentAsString, UserDto::class.java)

        mockMvc.perform(
            get("/api/users/${createdUser.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(userDto.name))
            .andExpect(jsonPath("$.email").value(userDto.email))
    }
}
