package dandi.dandi.member.domain;

import static dandi.dandi.member.MemberTestFixture.INITIAL_PROFILE_IMAGE_URL;
import static dandi.dandi.member.MemberTestFixture.OAUTH_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    private static final String NICKNAME = "abcd123";

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("oAuthId를 가진 Member를 찾는다.")
    @Test
    void findByOAuthId() {
        memberRepository.save(Member.initial(OAUTH_ID, NICKNAME, INITIAL_PROFILE_IMAGE_URL));

        Optional<Member> member = memberRepository.findByOAuthId(OAUTH_ID);

        assertThat(member).isPresent();
    }

    @DisplayName("nickname을 가진 Member가 존재하는지 반환한다.")
    @ParameterizedTest
    @CsvSource({"abcd123, true", "jklq123, false"})
    void existsMemberByNicknameValue(String nickname, boolean expected) {
        memberRepository.save(Member.initial(OAUTH_ID, NICKNAME, INITIAL_PROFILE_IMAGE_URL));

        boolean actual = memberRepository.existsMemberByNicknameValue(nickname);

        assertThat(actual).isEqualTo(expected);
    }
}
