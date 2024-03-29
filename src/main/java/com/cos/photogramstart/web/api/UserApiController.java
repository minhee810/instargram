package com.cos.photogramstart.web.api;

import com.cos.photogramstart.config.auth.PrincipalDetails;
import com.cos.photogramstart.domain.user.User;
import com.cos.photogramstart.service.SubscribeService;
import com.cos.photogramstart.service.UserService;
import com.cos.photogramstart.web.dto.CMRespDto;
import com.cos.photogramstart.web.dto.subscribe.SubscribeDto;
import com.cos.photogramstart.web.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserApiController {

    private final UserService userService;

    private final SubscribeService subscribeService;


    @PutMapping("/api/user/{principalId}/profileImageUrl") // MultipartFile 변수명 form 태그의 name 속성과 정확하게 일치해야함.
    public ResponseEntity<?> profileImageUrlUpdate(@PathVariable int principalId, MultipartFile profileImageFile, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User userEntity = userService.profileImageUrlUpdate(principalId, profileImageFile);
        principalDetails.setUser(userEntity);  // 세션 변경
        return new ResponseEntity<>(new CMRespDto(1, "프로필 사진 변경 성공", null), HttpStatus.OK);

    }


    @GetMapping("/api/user/{pageUserId}/subscribe")
    public ResponseEntity<?> subscribeList(@PathVariable int pageUserId, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<SubscribeDto> subscribeDto = subscribeService.subscribeList(principalDetails.getUser().getId(), pageUserId);

        return new ResponseEntity<>(new CMRespDto<>(1, "구독자 정보 리스트 가져오기 성공", subscribeDto), HttpStatus.OK);
    }


    @PutMapping("/api/user/{id}")
    public CMRespDto<?> update(@PathVariable int id, @Valid UserUpdateDto userUpdateDto, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        User userEntity = userService.update(id, userUpdateDto);
        principalDetails.setUser(userEntity);

        return new CMRespDto<>(1, "회원수정 완료", userEntity); // 응답시에 userEntity 의 모든 getter 함수가 호출되고 JSON 으로 파싱하여 응답한다.
    }

}



