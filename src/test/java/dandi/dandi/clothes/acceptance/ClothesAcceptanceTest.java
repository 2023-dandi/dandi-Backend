package dandi.dandi.clothes.acceptance;

import static dandi.dandi.common.HttpMethodFixture.httpPostWithAuthorizationAndImgFile;
import static dandi.dandi.common.RequestURI.CLOTHES_IMAGE_REGISTER_REQUEST_URI;
import static dandi.dandi.utils.image.TestImageUtils.generatetestImgFile;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dandi.dandi.clothes.application.port.in.ClothesImageRegisterResponse;
import dandi.dandi.common.AcceptanceTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ClothesAcceptanceTest extends AcceptanceTest {

    @DisplayName("옷 사진 등록 요청에 성공하면 201과 옷 사진 url을 응답한다.")
    @Test
    void registerClothesImage_Created() {
        String token = getToken();
        File file = generatetestImgFile();
        String multiPartControlName = "clothesImage";

        ExtractableResponse<Response> response = httpPostWithAuthorizationAndImgFile(
                CLOTHES_IMAGE_REGISTER_REQUEST_URI, token, file, multiPartControlName);

        ClothesImageRegisterResponse clothesImageRegisterResponse = response.jsonPath()
                .getObject(".", ClothesImageRegisterResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(clothesImageRegisterResponse.getClothesImageUrl()).startsWith(
                        "https://www.cloud-front.com/clothes/")
        );
    }

    @DisplayName("옷 사진 등록 요청에 성공하면 500을 응답한다.")
    @Test
    void registerClothesImage_InternalServerError() {
        String token = getToken();
        File file = generatetestImgFile();
        String multiPartControlName = "clothesImage";
        mockAmazonS3Exception();

        ExtractableResponse<Response> response = httpPostWithAuthorizationAndImgFile(
                CLOTHES_IMAGE_REGISTER_REQUEST_URI, token, file, multiPartControlName);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}