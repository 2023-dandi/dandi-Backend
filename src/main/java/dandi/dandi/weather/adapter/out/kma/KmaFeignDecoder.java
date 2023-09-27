package dandi.dandi.weather.adapter.out.kma;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import dandi.dandi.weather.application.port.out.WeatherRequestFatalException;
import dandi.dandi.weather.application.port.out.WeatherRequestRetryableException;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;

public class KmaFeignDecoder implements Decoder {

	private static final Logger logger = LoggerFactory.getLogger(KmaFeignDecoder.class);
	private static final String CONTENT_TYPE = "content-type";
	private static final String TEXT_XML = "text/xml";
	private static final String HTTP_ROUTING_ERROR = "HTTP ROUTING ERROR";
	private static final String TEXT_XML_EXCEPTION_MESSAGE_FORMAT = "공공 데이터 포털 에러 XML 응답\r\n%s";
	private static final String SERVICE_KEY_IS_NOT_REGISTERED_ERROR = "SERVICE_KEY_IS_NOT_REGISTERED_ERROR";

	private final Decoder delegate;

	public KmaFeignDecoder(ObjectFactory<HttpMessageConverters> messageConverters,
		ObjectProvider<HttpMessageConverterCustomizer> customizers) {
		delegate = new OptionalDecoder(new ResponseEntityDecoder(
			new SpringDecoder(messageConverters, customizers)));
	}

	@Override
	public Object decode(Response response, Type type) throws IOException, FeignException {
		if (hasTextXmlContentType(response)) {
			String xmlContent = decodeXmlResponseBody(response);
			handleXmlResponse(xmlContent);
		}
		return delegate.decode(response, type);
	}

	private void handleXmlResponse(String xmlContent) {
		String exceptionMessage = String.format(TEXT_XML_EXCEPTION_MESSAGE_FORMAT, xmlContent);
		if (xmlContent.contains(HTTP_ROUTING_ERROR) || xmlContent.contains(SERVICE_KEY_IS_NOT_REGISTERED_ERROR)) {
			throw new WeatherRequestRetryableException(exceptionMessage);
		}
		logger.info(exceptionMessage);
		throw new WeatherRequestFatalException(exceptionMessage);
	}

	private String decodeXmlResponseBody(Response response) throws IOException {
		Response.Body body = response.body();
		Reader bodyReader = body.asReader(StandardCharsets.UTF_8);
		return Util.toString(bodyReader);
	}

	private boolean hasTextXmlContentType(Response response) {
		Collection<String> contentTypes = response.headers().get(CONTENT_TYPE);
		return contentTypes.stream()
			.anyMatch(contentType -> contentType.contains(TEXT_XML));
	}
}
