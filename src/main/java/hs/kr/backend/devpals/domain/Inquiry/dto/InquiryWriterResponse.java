package hs.kr.backend.devpals.domain.Inquiry.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "문의 작성자 정보")
public class InquiryWriterResponse {

    @Schema(description = "작성자 ID", example = "5")
    Long id;

    @Schema(description = "작성자 닉네임", example = "홍길동")
    String nickname;

    @Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile.png")
    String img;

    public static InquiryWriterResponse fromEntity(UserEntity user) {
        return InquiryWriterResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .img(user.getProfileImg())
                .build();
    }
}