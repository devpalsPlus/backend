CREATE TABLE `devpals`.`PositionTag` (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         name VARCHAR(255) UNIQUE NOT NULL,
                                         createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE `devpals`.`ReportTag` (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         name VARCHAR(255) UNIQUE NOT NULL,
                                         createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `devpals`.`SkillTag` (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      name VARCHAR(255) UNIQUE NOT NULL,
                                      img TEXT,
                                      createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `devpals`.`MethodType` (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              name varchar(255) NOT NULL,
)


CREATE TABLE `devpals`.`User` (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  nickname VARCHAR(255) UNIQUE NOT NULL,
                                  email VARCHAR(255) UNIQUE NOT NULL,
                                  password VARCHAR(255) NOT NULL,
                                  bio TEXT,
                                  profileImg TEXT,
                                  userLevel ENUM('Beginner', 'Intermediate', 'Advanced') DEFAULT 'Beginner',
                                  github VARCHAR(255),
                                  career JSON DEFAULT NULL,
                                  skillIds TEXT,
                                  positionIds TEXT,
                                  refreshToken TEXT NOT NULL,
                                  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);



CREATE TABLE `devpals`.`Project` (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
                                     description TEXT NOT NULL,
                                     totalMember INT NOT NULL,
                                     startDate DATE NOT NULL,
                                     estimatedPeriod VARCHAR(50),
                                     methodTypeId BIGINT NOT NULL,
                                     userId BIGINT NOT NULL,
                                     views INT DEFAULT 0,
                                     isBeginner BOOLEAN DEFAULT FALSE,
                                     isDone BOOLEAN DEFAULT FALSE,
                                     recruitmentStartDate DATE NOT NULL,
                                     recruitmentEndDate DATE NOT NULL,
                                     positionTagIds text,
                                     skillTagIds text,
                                     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
                                     FOREIGN KEY (methodId) REFERENCES Method(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`Applicant` (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       userId BIGINT NOT NULL,
                                       projectId BIGINT NOT NULL,
                                       message TEXT,
                                       email VARCHAR(255),
                                       phoneNumber VARCHAR(15),
                                       career JSON,
                                       status ENUM('WAITING', 'ACCEPTED', 'REJECTED') DEFAULT 'WAITING',
                                       createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
                                       FOREIGN KEY (projectId) REFERENCES Project(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`Authenticode` (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          userEmail VARCHAR(255) NOT NULL,
                                          code VARCHAR(20) NOT NULL,
                                          expiresAt TIMESTAMP NOT NULL,
                                          isUsed BOOLEAN DEFAULT FALSE
);

CREATE TABLE `devpals`.`Comment` (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         projectId BIGINT NOT NULL,
                         userId BIGINT NOT NULL,
                         content VARCHAR(255) NOT NULL,
                         createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (projectId) REFERENCES Project(id) ON DELETE CASCADE,
                         FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`Recomment` (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           projectId BIGINT NOT NULL,
                           userId BIGINT NOT NULL,
                           commentId BIGINT NOT NULL,
                           content VARCHAR(255) NOT NULL,
                           createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (projectId) REFERENCES Project(id) ON DELETE CASCADE,
                           FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
                           FOREIGN KEY (commentId) REFERENCES Comment(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`Report` (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           reportTargetId BIGINT NOT NULL,
                           reporterId BIGINT NOT NULL,
                           reportFilter VARCHAR(50) NOT NULL,
                           reportTagIds TEXT,
                           detail TEXT NOT NULL,
                           createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

-- 메인 알람 테이블
CREATE TABLE `devpals`.`Alarm` (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           receiver_id BIGINT NOT NULL,
                           content VARCHAR(255),
                           enabled BOOLEAN NOT NULL DEFAULT FALSE,
                           routingId BIGINT,
                           createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           ALARM_FILTER VARCHAR(50),
                           FOREIGN KEY (receiver_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 지원자 관련 알람 테이블
CREATE TABLE `devpals`.`ApplicantAlarm` (
                           id BIGINT PRIMARY KEY,
                           project_id BIGINT NOT NULL,
                           applicant_id BIGINT NOT NULL,
                           FOREIGN KEY (id) REFERENCES Alarm(id) ON DELETE CASCADE,
                           FOREIGN KEY (project_id) REFERENCES Project(id) ON DELETE CASCADE,
                           FOREIGN KEY (applicant_id) REFERENCES Applicant(id) ON DELETE CASCADE
);

-- 댓글 관련 알람 테이블
CREATE TABLE `devpals`.`CommentAlarm` (
                           id BIGINT PRIMARY KEY,
                           project_id BIGINT NOT NULL,
                           comment_id BIGINT NOT NULL,
                           recomment_id BIGINT,
                           replier BOOLEAN,
                           FOREIGN KEY (id) REFERENCES Alarm(id) ON DELETE CASCADE,
                           FOREIGN KEY (project_id) REFERENCES Project(id) ON DELETE CASCADE,
                           FOREIGN KEY (comment_id) REFERENCES Comment(id) ON DELETE CASCADE,
                           FOREIGN KEY (recomment_id) REFERENCES Recomment(id) ON DELETE CASCADE
);

-- 프로젝트 관련 알람 테이블
CREATE TABLE `devpals`.`ProjectAlarm` (
                           id BIGINT PRIMARY KEY,
                           project_id BIGINT NOT NULL,
                           applicant_id BIGINT NOT NULL,
                           FOREIGN KEY (id) REFERENCES Alarm(id) ON DELETE CASCADE,
                           FOREIGN KEY (project_id) REFERENCES Project(id) ON DELETE CASCADE,
                           FOREIGN KEY (applicant_id) REFERENCES Applicant(id) ON DELETE CASCADE
);

-- 신고 관련 알람 테이블
CREATE TABLE `devpals`.`ReportAlarm` (
                           id BIGINT PRIMARY KEY,
                           report_id BIGINT NOT NULL,
                           FOREIGN KEY (id) REFERENCES Alarm(id) ON DELETE CASCADE,
                           FOREIGN KEY (report_id) REFERENCES Report(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`Inquiry` (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     userId BIGINT NOT NULL,
                                     title VARCHAR(255) NOT NULL,
                                     content TEXT NOT NULL,
                                     category VARCHAR(255) NOT NULL,
                                     warning INT DEFAULT 0,
                                     isAnswered BOOLEAN NOT NULL DEFAULT FALSE,
                                     createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`InquiryImages` (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           inquiryId BIGINT NOT NULL,
                                           imageUrl TEXT,
                                           createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           FOREIGN KEY (inquiryId) REFERENCES Inquiry(id) ON DELETE CASCADE
);

CREATE TABLE faq (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     category VARCHAR(50) NOT NULL,
                     title VARCHAR(255) NOT NULL,
                     content TEXT NOT NULL,
                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                     updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);