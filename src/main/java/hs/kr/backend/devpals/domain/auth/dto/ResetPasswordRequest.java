package hs.kr.backend.devpals.domain.auth.dto;

import com.mysql.cj.x.protobuf.MysqlxSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ResetPasswordRequest extends EmailVertificationRequest {
    private String newPassword;

    public ResetPasswordRequest(String email, String code, String newPassword) {
        super(email, code);
        this.newPassword = newPassword;
    }
}
