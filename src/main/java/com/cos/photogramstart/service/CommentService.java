package com.cos.photogramstart.service;

import com.cos.photogramstart.domain.comment.Comment;
import com.cos.photogramstart.domain.comment.CommentRepository;
import com.cos.photogramstart.domain.image.Image;
import com.cos.photogramstart.domain.user.User;
import com.cos.photogramstart.domain.user.UserRepository;
import com.cos.photogramstart.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment writeComment(String content, int imageId, int userId) {

        // Tip (객체를 만들 때 id 값만 담아서 insert 가 가능함)
        // 대신 return 시에 image 객체와 user 객체는 id 값만 가지고 있는 빈 객체를 리턴받는다.
        Image image = new Image();
        image.setId(imageId);

        User userEntity = userRepository.findById(userId).orElseThrow(() -> {
            throw new CustomApiException("유저 아이디를 찾을 수 없습니다.");
        });

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(userEntity);
        comment.setImage(image);

        return commentRepository.save(comment);
    }

    @Transactional
    public void removeComment(int id) {

        try {
            commentRepository.deleteById(id);
        } catch (Exception e) {
            throw new CustomApiException(e.getMessage());
        }

    }
}
