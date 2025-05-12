package hs.kr.backend.devpals.global.exception;

import lombok.Getter;

@Getter
public enum ErrorException {
    ACCESS_TOKEN_NOT_EXPIRED("액세스 토큰이 아직 만료되지 않았습니다."),
    AUTHOR_ONLY("해당 공고의 기획자만 조회 가능합니다."),
    ALARM_FILTER_NOT_FOUND("알람 필터값이 존재하지 않습니다."),
    CAN_NOT_DELETE_ALARM("해당 알람을 삭제할 수 없습니다."),
    ALARM_NOT_FOUND("알람이 존재하지 않습니다."),
    INVALID_PASSWORD("비밀번호가 올바르지 않습니다."),
    SERVER_ERROR("서버 오류가 발생했습니다."),
    UNAUTHORIZED("인증 권한이 없습니다."),
    TOKEN_EXPIRED("토큰이 만료되었습니다."),
    FORBIDDEN("접근이 금지되었습니다."),
    USER_ID_NOT_FOUND("해당 유저가 토큰에 존재하지 않습니다."),
    USER_NOT_FOUND("가입되지 않은 계정입니다."),
    DUPLICATE_EMAIL("중복된 이메일 입니다."),
    DUPLICATE_NICKNAME("중복된 닉네임 입니다."),
    EMAIL_SEND_FAILED("유효한 이메일을 입력해주세요."),
    EMAIL_VERTIFICATION("이메일 인증을 해주세요."),
    EMAIL_CODE_EXPIRED("이메일 코드가 만료되었습니다."),
    INVALID_CODE("이메일 인증코드를 다시 확인해주세요."),
    EMAIL_NOT_VERIFIED("이메일 인증코드를 입력해주세요."),
    FAIL_JSONPROCESSING("Json 형식을 변환하지 못했습니다."),
    POSITION_NOT_FOUND("포지션 태그를 찾을 수 없습니다."),
    REPORT_TAG_NOT_FOUND("신고사유(카테고리)를 찾을 수 없습니다."),
    FILE_EMPTY("파일이 비어있습니다."),
    FAIL_UPLOAD("파일 업로드에 실패했습니다."),
    FILE_NOT_SEARCH("파일을 찾을 수 없습니다."),
    INVALID_FILE_TYPE("파일 타입을 확인해주세요."),
    SKILL_NOT_FOUND("스킬 태그를 찾을 수 없습니다."),
    METHOD_TYPE_NOT_FOUND("방식 유형을 찾을 수 없습니다."),
    INVALID_METHOD_TYPE("잘못된 방식입니다. (온라인, 오프라인, 온/오프라인만 허용)"),
    PROJECT_NOT_FOUND("해당 프로젝트를 찾을 수 없습니다."),
    FAIL_PROJECT_UPDATE("작성자만 수정 가능합니다."),
    ALREADY_APPLIED("해당 프로젝트에 이미 지원하셨습니다."),
    INVALID_APPLICANT_PROJECT("해당 지원자를 프로젝트에서 찾을 수 없습니다."),
    PROJECT_DONE("마감한 공고는 지원자의 상태를 변경할 수 없습니다."),
    STATUS_NOT_FOUND("작성한 상태값이 존재하지 않습니다."),
    EQUAL_STATUS("상태가 이미 동일합니다."),
    COMMENT_NOT_FOUND("해당 프로젝트의 댓글을 찾을 수 없습니다."),
    RECOMMENT_NOT_FOUND("해당 프로젝트의 대댓글을 찾을 수 없습니다."),
    NOT_COMMENT_OWNER("작성자 및 프로젝트 작성자만 댓글을 삭제할 수 있습니다."),
    INVALID_PROJECT_COMMENT("프로젝트와 댓글이 매칭되지 않습니다."),
    INQUIRY_NOT_FOUND("문의내용을 찾을 수 없습니다."),
    NOT_INQUIRY_DELETE("문의글을 삭제할 권한이 없습니다."),
    FAQ_NOT_FOUND("FAQ글을 찾을 수 없습니다."),
    NO_PERMISSION("관리자 기능입니다."),

    INVALID_EVALUATION_SCORES("점수는 6개 항목으로 구성되어야 합니다."),
    EVALUATION_ALREADY_EXISTS("이미 평가를 완료한 사용자입니다."),
    APPLICANT_NOT_FOUND("해당 프로젝트에 참여자로 등록된 유저가 아닙니다."),
    INVALID_EVALUATION_TARGET("평가 대상은 프로젝트에 참여 중인 유저여야 합니다.");

    NOT_FOUND_NOTICE("공지사항을 찾을 수 없습니다.");

    private final String message;

    ErrorException(String message) {
        this.message = message;
    }
}
