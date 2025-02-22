CREATE TABLE `devpals`.`PositionTag` (
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


CREATE TABLE `devpals`.`User` (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  nickname VARCHAR(255) UNIQUE NOT NULL,
                                  email VARCHAR(255) UNIQUE NOT NULL,
                                  password VARCHAR(255) NOT NULL,
                                  bio TEXT,
                                  profileImg TEXT,
                                  userLevel ENUM('Beginner', 'Intermediate', 'Advanced') DEFAULT 'Beginner',
                                  github VARCHAR(255),
                                  career JSON,
                                  positionTagId INT,
                                  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  CONSTRAINT fk_User_positionTag FOREIGN KEY (positionTagId) REFERENCES PositionTag(id)
);

CREATE TABLE `devpals`.`UserSkillTag` (
                                          userId INT NOT NULL,
                                          skillTagId INT NOT NULL,
                                          createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          PRIMARY KEY (userId, skillTagId),
                                          FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
                                          FOREIGN KEY (skillTagId) REFERENCES SkillTag(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`Method` (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(50) UNIQUE NOT NULL,
                                    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `devpals`.`Project` (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
                                     description TEXT NOT NULL,
                                     totalMember INT NOT NULL,
                                     startDate DATE NOT NULL,
                                     estimatedPeriod VARCHAR(50),
                                     methodId INT NOT NULL,
                                     authorId INT NOT NULL,
                                     views INT DEFAULT 0,
                                     isBeginner BOOLEAN DEFAULT FALSE,
                                     isDone BOOLEAN DEFAULT FALSE,
                                     recruitmentStartDate DATE NOT NULL,
                                     recruitmentEndDate DATE NOT NULL,
                                     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     FOREIGN KEY (authorId) REFERENCES User(id) ON DELETE CASCADE,
                                     FOREIGN KEY (methodId) REFERENCES Method(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`Applicant` (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       userId INT NOT NULL,
                                       projectId INT NOT NULL,
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

CREATE TABLE `devpals`.`Notification` (
                                          id CHAR(36) PRIMARY KEY,
                                          userId INT NOT NULL,
                                          content TEXT NOT NULL,
                                          createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE
);



CREATE TABLE `devpals`.`ProjectSkillTag` (
                                             projectId INT NOT NULL,
                                             skillTagId INT NOT NULL,
                                             PRIMARY KEY (projectId, skillTagId),
                                             FOREIGN KEY (projectId) REFERENCES Project(id) ON DELETE CASCADE,
                                             FOREIGN KEY (skillTagId) REFERENCES SkillTag(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`ProjectPositionTag` (
                                                projectId INT NOT NULL,
                                                positionTagId INT NOT NULL,
                                                PRIMARY KEY (projectId, positionTagId),
                                                FOREIGN KEY (projectId) REFERENCES Project(id) ON DELETE CASCADE,
                                                FOREIGN KEY (positionTagId) REFERENCES PositionTag(id) ON DELETE CASCADE
);

CREATE TABLE `devpals`.`Authenticode` (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          userEmail VARCHAR(255) NOT NULL,
                                          code VARCHAR(20) NOT NULL,
                                          expiresAt TIMESTAMP NOT NULL,
                                          isUsed BOOLEAN DEFAULT FALSE
);

CREATE TABLE `devpals`.`Session` (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     userId INT UNIQUE NOT NULL,
                                     accessToken TEXT NOT NULL,
                                     refreshToken TEXT NOT NULL,
                                     expiresAt TIMESTAMP NOT NULL,
                                     FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE
);
