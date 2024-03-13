package com.cos.photogramstart.web.api;

import com.cos.photogramstart.config.auth.PrincipalDetails;
import com.cos.photogramstart.domain.user.User;
import com.cos.photogramstart.handler.ex.CustomValidationApiException;
import com.cos.photogramstart.handler.ex.CustomValidationException;
import com.cos.photogramstart.service.UserService;
import com.cos.photogramstart.web.dto.CMRespDto;
import com.cos.photogramstart.web.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserApiController {

    private final UserService userService;

    @PutMapping("/api/user/{id}")
    public CMRespDto<?> update(@PathVariable int id, @Valid UserUpdateDto userUpdateDto, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new CustomValidationApiException("유효성 검사 실패함", errorMap);
        } else {
            User userEntity = userService.update(id, userUpdateDto);
            principalDetails.setUser(userEntity);

            return new CMRespDto<>(1, "회원수정 완료", userEntity); // 응답시에 userEntity 의 모든 getter 함수가 호출되고 JSON 으로 파싱하여 응답한다.
        }


    }
}
