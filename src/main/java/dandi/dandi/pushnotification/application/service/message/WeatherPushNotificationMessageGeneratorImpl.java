package dandi.dandi.pushnotification.application.service.message;

import dandi.dandi.weather.application.port.out.WeatherForecastResponse;
import org.springframework.stereotype.Component;

@Component
public class WeatherPushNotificationMessageGeneratorImpl implements WeatherPushNotificationMessageGenerator {

    private static final String MESSAGE_FORMAT = "오늘 날씨는 최저 %d / 최고 %d입니다. 단디에서 옷차림을 확인해보세요";

    @Override
    public String generateMessage(WeatherForecastResponse weatherForecastResponse) {
        return String.format(MESSAGE_FORMAT, weatherForecastResponse.getMinTemperature(),
                weatherForecastResponse.getMaxTemperature());
    }
}
