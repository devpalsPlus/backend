package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.CommentDTO;
import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.CommentRepoisitory;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.events.Comment;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectCommentService {
    private final JwtTokenValidator jwtTokenValidator;
    private final CommentRepoisitory commentRepoisitory;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ResponseEntity<ApiResponse<String>> writeComment(String token, Long projectId, CommentDTO dto) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        CommentEntity comment = CommentEntity.from(dto, project, user);
        commentRepoisitory.save(comment);

        return ResponseEntity.ok(new ApiResponse<>(true, "댓글 작성 성공", null));
    }

    public ResponseEntity<ApiResponse<List<CommentDTO>>> getComment(Long projectId) {

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        List<CommentEntity> comments = commentRepoisitory.findAllByProjectId(projectId);

        List<CommentDTO> response = commentRepoisitory.findAllByProjectId(projectId).stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "댓글 가져오기 성공", response));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateComment(String token, Long projectId, Long commentId, String content) {
        Long userId = jwtTokenValidator.getUserId(token);

        CommentEntity comment = commentRepoisitory.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorException.COMMENT_NOT_FOUND));

        if (!comment.getProject().getId().equals(projectId)) {
            throw new CustomException(ErrorException.INVALID_PROJECT_COMMENT);
        }

        Long commentOwnerId = comment.getUser().getId();
        Long projectAuthorId = comment.getProject().getAuthorId();

        if (!Objects.equals(userId, commentOwnerId) && !Objects.equals(userId, projectAuthorId)) {
            throw new CustomException(ErrorException.NOT_COMMENT_OWNER);
        }

        comment.updateContent(content);
        return ResponseEntity.ok(new ApiResponse<>(true, "댓글 수정 성공", null));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteComment(String token, Long projectId, Long commentId) {
        Long userId = jwtTokenValidator.getUserId(token);

        CommentEntity comment = commentRepoisitory.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorException.COMMENT_NOT_FOUND));

        if (!comment.getProject().getId().equals(projectId)) {
            throw new CustomException(ErrorException.INVALID_PROJECT_COMMENT);
        }

        Long commentOwnerId = comment.getUser().getId();
        Long projectAuthorId = comment.getProject().getAuthorId();

        if (!Objects.equals(commentOwnerId, userId) && !Objects.equals(projectAuthorId, userId)) {
            throw new CustomException(ErrorException.NOT_COMMENT_OWNER);
        }

        commentRepoisitory.delete(comment);

        return ResponseEntity.ok(new ApiResponse<>(true, "댓글 삭제 성공", null));
    }
}
