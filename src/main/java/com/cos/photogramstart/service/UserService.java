package com.cos.photogramstart.service;

import com.cos.photogramstart.domain.subscribe.SubscribeRepository;
import com.cos.photogramstart.domain.user.User;
import com.cos.photogramstart.domain.user.UserRepository;
import com.cos.photogramstart.handler.ex.CustomException;
import com.cos.photogramstart.handler.ex.CustomValidationApiException;
import com.cos.photogramstart.web.dto.user.UserProfileDto;
import com.cos.photogramstart.web.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final SubscribeRepository subscribeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    /**
     * @param pageUserId 해당 프로필 페이지 사용자 아이디
     */
    @Transactional(readOnly = true)  // select 할때도 transaction 을 건다. readOnly 를 true 로 설정.
    public UserProfileDto memberProfile(int pageUserId, int principalId) {

        UserProfileDto dto = new UserProfileDto();

        // SELECT * FROM image WHERE userId =:userId;
        User userEntity = userRepository.findById(pageUserId).orElseThrow(() -> {
            throw new CustomException("해당 프로필 페이지는 없는 페이지입니다.");
        });

        dto.setUser(userEntity);
        dto.setPageOwnerState(pageUserId == principalId);
        dto.setImageCount(userEntity.getImages().size());

        int subscribeState = subscribeRepository.mSubscribeState(principalId, pageUserId);
        int subscribeCount = subscribeRepository.mSubscribeCount(pageUserId);

        dto.setSubscribeState(subscribeState == 1);
        dto.setSubscribeCount(subscribeCount);
        return dto;
    }

    @Transactional
    public User update(int id, UserUpdateDto userUpdateDto) {
        // 1. 영속화 - 사용자 찾기 또는 예외 발생
        User userEntity = userRepository.findById(id).orElseThrow(() -> new CustomValidationApiException("찾을 수 없는 id 입니다."));


        // 2. 정보 업데이트
        userEntity.setName(userUpdateDto.getName());
        userEntity.setPassword(bCryptPasswordEncoder.encode(userUpdateDto.getPassword()));
        userEntity.setPhone(userUpdateDto.getPhone());
        userEntity.setBio(userUpdateDto.getBio());
        userEntity.setWebsite(userUpdateDto.getWebsite());
        userEntity.setGender(userUpdateDto.getGender());

        // 3. 변경된 정보가 영속성 컨텍스트에 의해 자동으로 DB에 반영됨 (더티 체킹)

        return userEntity; // 예시로 엔티티 반환. 실제로는 DTO 사용 권장
    }

}
