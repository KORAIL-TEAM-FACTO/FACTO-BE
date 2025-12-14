package team.java.facto_be;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import team.java.facto_be.domain.bookmark.repository.BookmarkRepository;
import team.java.facto_be.domain.chatbot.domain.repository.ChatMessageRepository;
import team.java.facto_be.domain.chatbot.domain.repository.ChatSessionRepository;
import team.java.facto_be.domain.recentview.repository.RecentViewRepository;
import team.java.facto_be.domain.user.repository.UserRepository;
import team.java.facto_be.domain.welfare.repository.WelfareServiceRepository;

<<<<<<< Updated upstream
@SpringBootTest
@Disabled("DB 연결이 필요한 통합 테스트")
=======
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
        "spring.data.jpa.repositories.enabled=false",
        "spring.data.redis.repositories.enabled=false",
        "spring.data.mongodb.repositories.enabled=false",
        "spring.autoconfigure.exclude[additional]=org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration",
        "spring.autoconfigure.exclude[ai]=org.springframework.ai.vectorstore.chroma.autoconfigure.ChromaVectorStoreAutoConfiguration"
})
@ActiveProfiles("test")
>>>>>>> Stashed changes
class FactoBeApplicationTests {

    @MockBean private BookmarkRepository bookmarkRepository;
    @MockBean private ChatMessageRepository chatMessageRepository;
    @MockBean private ChatSessionRepository chatSessionRepository;
    @MockBean private RecentViewRepository recentViewRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private WelfareServiceRepository welfareServiceRepository;
    @MockBean private team.java.facto_be.global.security.jwt.domain.repository.RefreshTokenRepository refreshTokenRepository;
    @MockBean private VectorStore vectorStore;
    @MockBean private ChatClient chatClient;

    @Test
    void contextLoads() {
    }

}
