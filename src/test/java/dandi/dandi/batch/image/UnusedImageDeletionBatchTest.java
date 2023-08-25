package dandi.dandi.batch.image;

import dandi.dandi.image.adapter.out.persistence.jpa.UnusedImageJpaEntity;
import dandi.dandi.image.adapter.out.persistence.jpa.UnusedImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@RunWith(SpringRunner.class)
@SpringBatchTest
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
@SpringBootTest
class UnusedImageDeletionBatchTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UnusedImageRepository unusedImageRepository;

    @DisplayName("배치 실행 시점 기준, 날짜가 1일 전에 생성된 미사용 이미지들을 삭제한다.")
    @Test
    void deleteUnusedImageJob_Yesterday() throws Exception {
        LocalDateTime now = LocalDateTime.now().plusDays(1);
        JobParameters jobParameters = new JobParameters(Map.of("dateTime", new JobParameter(now.toString())));
        for (int i = 1; i <= 20; i++) {
            String fileKey = String.valueOf(i);
            unusedImageRepository.save(new UnusedImageJpaEntity(fileKey));
        }

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertAll(
                () -> assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED),
                () -> assertThat(unusedImageRepository.findAll()).isEmpty()
        );
    }

    @DisplayName("배치 실행 시점 기준, 오늘 생성된 미사용 이미지들을 삭제하지 않는다.")
    @Test
    void deleteUnusedImageJob_Today() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        JobParameters jobParameters = new JobParameters(Map.of("dateTime", new JobParameter(now.toString())));
        for (int i = 1; i <= 20; i++) {
            String fileKey = String.valueOf(i);
            unusedImageRepository.save(new UnusedImageJpaEntity(fileKey));
        }

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertAll(
                () -> assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED),
                () -> assertThat(unusedImageRepository.findAll()).hasSize(20)
        );
    }
}