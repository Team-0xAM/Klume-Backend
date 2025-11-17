package com.oxam.klume.reservation.service;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.reservation.exception.RoomAlreadyBookedException;
import com.oxam.klume.reservation.repository.DailyReservationRepository;
import com.oxam.klume.reservation.repository.ReservationRepository;
import com.oxam.klume.room.repository.AvailableTimeRepository;
import com.oxam.klume.room.repository.DailyAvailableTimeRepository;
import com.oxam.klume.room.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationServiceImplTest {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AvailableTimeRepository availableTimeRepository;

    @Autowired
    private DailyAvailableTimeRepository dailyAvailableTimeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DailyReservationRepository dailyReservationRepository;

    @Test
//    @Transactional
    @DisplayName("10개의 스레드가 동시에 같은 시간대 예약 시도 - 1개만 성공해야 함")
    void testConcurrentReservation_OnlyOneSuccess() throws InterruptedException {
        final Member member = memberRepository.findById(7).get();
        final int organizationId = 1;
        final int roomId = 1;
        final int dailyAvailableTimeId = 3;

        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    reservationService.reserveRoom(member, organizationId, roomId, dailyAvailableTimeId);
                    successCount.incrementAndGet();
                } catch (RoomAlreadyBookedException e) {
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        System.out.println("\n=== 테스트 결과 ===");
        System.out.println("성공: " + successCount.get() + "건");
        System.out.println("실패: " + failCount.get() + "건");

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(9);

        // DB에도 1건만 저장되어 있는지 확인
        long reservationCount = dailyReservationRepository.count();
        System.out.println("reservationCount = " + reservationCount);
        assertThat(reservationCount).isEqualTo(1);
    }
}