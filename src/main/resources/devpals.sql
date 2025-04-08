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
                                  CONSTRAINT fk_User_positionTag FOREIGN KEY (positionTagId) REFERENCES PositionTag(id)
);



CREATE TABLE `devpals`.`Project` (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
                                     description TEXT NOT NULL,
                                     totalMember INT NOT NULL,
                                     startDate DATE NOT NULL,
                                     estimatedPeriod VARCHAR(50),
                                     methodTypeId INT NOT NULL,
                                     authorId INT NOT NULL,
                                     views INT DEFAULT 0,
                                     isBeginner BOOLEAN DEFAULT FALSE,
                                     isDone BOOLEAN DEFAULT FALSE,
                                     recruitmentStartDate DATE NOT NULL,
                                     recruitmentEndDate DATE NOT NULL,
                                     positionTagIds text,
                                     skillTagIds text,
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

CREATE TABLE `devpals`.`Authenticode` (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          userEmail VARCHAR(255) NOT NULL,
                                          code VARCHAR(20) NOT NULL,
                                          expiresAt TIMESTAMP NOT NULL,
                                          isUsed BOOLEAN DEFAULT FALSE
);

