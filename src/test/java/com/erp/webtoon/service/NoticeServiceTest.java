package com.erp.webtoon.service;

import com.erp.webtoon.domain.Notice;
import com.erp.webtoon.domain.User;
import com.erp.webtoon.dto.notice.NoticeCardViewDto;
import com.erp.webtoon.dto.notice.NoticeListDto;
import com.erp.webtoon.dto.notice.NoticeRequestDto;
import com.erp.webtoon.dto.notice.NoticeResponseDto;
import com.erp.webtoon.repository.NoticeRepository;
import com.erp.webtoon.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        noticeRepository.deleteAll();
    }


    @Test
    @DisplayName("공지사항 등록")
    void test1() throws Exception {
        //given
        User user = User.builder()
                .employeeId("20232023")
                .build();

        userRepository.save(user);

        NoticeRequestDto noticeRequestDto = new NoticeRequestDto();
        noticeRequestDto.setEmployeeId("20232023");
        noticeRequestDto.setNoticeType("인사팀");
        noticeRequestDto.setTitle("제목입니다.");
        noticeRequestDto.setContent("내용입니다.");

        //when
        noticeService.save(noticeRequestDto);

        //then
        assertEquals(1L, noticeRepository.count());
        Notice notice = noticeRepository.findAll().get(0);
        assertEquals("인사팀", notice.getNoticeType());
        assertEquals("제목입니다.", notice.getTitle());
        assertEquals("내용입니다.", notice.getContent());
    }

    @Test
    @DisplayName("공지사항 개별 조회")
    void test2() throws Exception {
        //given
        User user = User.builder()
                .employeeId("20232023")
                .name("규규")
                .build();

        userRepository.save(user);

        Notice newNotice = Notice.builder()
                .noticeType("인사팀")
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        newNotice.setWriteUser(user);
        noticeRepository.save(newNotice);

        //when
        NoticeResponseDto response = noticeService.find(newNotice.getId());
        System.out.println(response);

        //then
        assertEquals(1L, noticeRepository.count());
        assertEquals("인사팀", response.getNoticeType());
        assertEquals("제목입니다.", response.getTitle());
        assertEquals("내용입니다.", response.getContent());
        assertEquals(2, response.getReadCount());
        assertEquals("규규", response.getName());
        assertEquals(LocalDate.now(), response.getNoticeDate());
    }

    @Test
    @DisplayName("공지사항 리스트 조회 기능")
    void test3() {
        //given
        User newUser = User.builder()
                .employeeId("20232023")
                .build();

        userRepository.save(newUser);

        List<Notice> notices = IntStream.range(1, 31)
                .mapToObj(i -> Notice.builder()
                        .noticeType("type" + i)
                        .title("title" + i)
                        .content("content" + i)
                        .build())
                .collect(Collectors.toList());

        for (Notice notice: notices) {
            notice.setWriteUser(newUser);
        }

        noticeRepository.saveAll(notices);

        //when
        List<NoticeListDto> dtoList = noticeService.findAllNotice();

        //then
        assertEquals(30, dtoList.size());
        assertEquals("title1", dtoList.get(29).getTitle());
        assertEquals("title30", dtoList.get(0).getTitle());

    }


    @Test
    @DisplayName("공지사항 카드뷰 조회 기능")
    void test6() {
        //given
        List<Notice> notices = IntStream.range(1, 31)
                .mapToObj(i -> Notice.builder()
                        .noticeType("type" + i)
                        .title("title" + i)
                        .content("content" + i)
                        .build())
                .collect(Collectors.toList());

        noticeRepository.saveAll(notices);

        //when
        List<NoticeCardViewDto> dtoList = noticeService.findCardNotice();

        //then
        assertEquals(6, dtoList.size());
    }
}