package dandi.dandi.member.application.port.in;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class LocationUpdateCommandTest {

    @DisplayName("null 값으로 LocationUpdateCommand를 생성하려 하면 예외를 발생시킨다.")
    @ParameterizedTest
    @MethodSource("provideNullLocation")
    void create_Null(Double latitude, Double longitude) {
        assertThatThrownBy(() -> new LocationUpdateCommand(latitude, longitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("위도, 경도 값이 잘못되었습니다");
    }

    private static Stream<Arguments> provideNullLocation() {
        return Stream.of(
                Arguments.of(null, 0.0),
                Arguments.of(0.0, null),
                Arguments.of(null, null)
        );
    }

    @DisplayName("위도 -90.0 ~ 90.0, 경도 -180.0 ~ 180.0 범위가 아닌 값으로 LocationUpdateCommand를 생성하려하면 예외를 발생시킨다.")
    @ParameterizedTest
    @CsvSource({"-90.1, 0", "90.1, 0", "0, -180.1", "0, 180.1", "90.1, -180.1"})
    void create_Exception(Double latitude, Double longitude) {
        assertThatThrownBy(() -> new LocationUpdateCommand(latitude, longitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("위도, 경도 값이 잘못되었습니다");
    }
}