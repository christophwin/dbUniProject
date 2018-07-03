-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema university_project
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `university_project` ;

-- -----------------------------------------------------
-- Schema university_project
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `university_project` DEFAULT CHARACTER SET latin1 COLLATE latin1_german2_ci ;
USE `university_project` ;

-- -----------------------------------------------------
-- Table `university_project`.`subjectArea`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `university_project`.`subjectArea` ;

CREATE TABLE IF NOT EXISTS `university_project`.`subjectArea` (
  `idsubjectarea` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`idsubjectarea`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `university_project`.`professors`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `university_project`.`professors` ;

CREATE TABLE IF NOT EXISTS `university_project`.`professors` (
  `id` INT NOT NULL,
  `firstName` VARCHAR(45) NOT NULL,
  `lastName` VARCHAR(45) NOT NULL,
  `birthDate` DATE NOT NULL,
  `adress` VARCHAR(45) NOT NULL,
  `subjectAreaId` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_professoren_fachbereiche1_idx` (`subjectAreaId` ASC),
  CONSTRAINT `fk_professoren_fachbereiche1`
    FOREIGN KEY (`subjectAreaId`)
    REFERENCES `university_project`.`subjectArea` (`idsubjectarea`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `university_project`.`assistants`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `university_project`.`assistants` ;

CREATE TABLE IF NOT EXISTS `university_project`.`assistants` (
  `id` INT NOT NULL,
  `firstName` VARCHAR(45) NOT NULL,
  `lastName` VARCHAR(45) NOT NULL,
  `birthDate` VARCHAR(45) NOT NULL,
  `adress` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `university_project`.`lectures`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `university_project`.`lectures` ;

CREATE TABLE IF NOT EXISTS `university_project`.`lectures` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `professorId` INT NOT NULL,
  `assistantId` INT NOT NULL,
  `topic` VARCHAR(150) NOT NULL,
  `lectureTimes` VARCHAR(150) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_vorlesungen_professoren_idx` (`professorId` ASC),
  INDEX `fk_vorlesungen_assistenten1_idx` (`assistantId` ASC),
  CONSTRAINT `fk_vorlesungen_professoren`
    FOREIGN KEY (`professorId`)
    REFERENCES `university_project`.`professors` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_vorlesungen_assistenten1`
    FOREIGN KEY (`assistantId`)
    REFERENCES `university_project`.`assistants` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `university_project`.`students`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `university_project`.`students` ;

CREATE TABLE IF NOT EXISTS `university_project`.`students` (
  `id` INT NOT NULL,
  `firstName` VARCHAR(45) NOT NULL,
  `lastName` VARCHAR(45) NOT NULL,
  `birthDate` DATE NOT NULL,
  `adress` VARCHAR(45) NOT NULL,
  `subjectAreaId` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_studenten_fachbereiche1_idx` (`subjectAreaId` ASC),
  CONSTRAINT `fk_studenten_fachbereiche1`
    FOREIGN KEY (`subjectAreaId`)
    REFERENCES `university_project`.`subjectArea` (`idsubjectarea`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `university_project`.`administrativeEmployees`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `university_project`.`administrativeEmployees` ;

CREATE TABLE IF NOT EXISTS `university_project`.`administrativeEmployees` (
  `id` INT NOT NULL,
  `firstName` VARCHAR(45) NOT NULL,
  `lastName` VARCHAR(45) NOT NULL,
  `birthDate` VARCHAR(45) NOT NULL,
  `adress` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `university_project`.`listenTo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `university_project`.`listenTo` ;

CREATE TABLE IF NOT EXISTS `university_project`.`listenTo` (
  `idstudents` INT NOT NULL,
  `idlectures` INT NOT NULL,
  `start` DATE NOT NULL,
  `end` DATE NULL,
  PRIMARY KEY (`idstudents`, `idlectures`),
  INDEX `fk_studenten_has_vorlesungen_vorlesungen1_idx` (`idlectures` ASC),
  INDEX `fk_studenten_has_vorlesungen_studenten1_idx` (`idstudents` ASC),
  CONSTRAINT `fk_studenten_has_vorlesungen_studenten1`
    FOREIGN KEY (`idstudents`)
    REFERENCES `university_project`.`students` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_studenten_has_vorlesungen_vorlesungen1`
    FOREIGN KEY (`idlectures`)
    REFERENCES `university_project`.`lectures` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `university_project` ;

-- -----------------------------------------------------
-- Placeholder table for view `university_project`.`professorData`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `university_project`.`professorData` (`id` INT, `firstName` INT, `lastName` INT, `birthDate` INT, `adress` INT, `name` INT);

-- -----------------------------------------------------
-- Placeholder table for view `university_project`.`adminData`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `university_project`.`adminData` (`id` INT, `firstName` INT, `lastName` INT, `birthDate` INT, `adress` INT);

-- -----------------------------------------------------
-- Placeholder table for view `university_project`.`assistantData`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `university_project`.`assistantData` (`id` INT, `firstName` INT, `lastName` INT, `birthDate` INT, `adress` INT);

-- -----------------------------------------------------
-- Placeholder table for view `university_project`.`studentData`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `university_project`.`studentData` (`id` INT, `firstName` INT, `lastName` INT, `birthDate` INT, `adress` INT, `name` INT);

-- -----------------------------------------------------
-- Placeholder table for view `university_project`.`lecturesView`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `university_project`.`lecturesView` (`id` INT, `topic` INT, `lectureTimes` INT, `professorId` INT, `professor_firstName` INT, `professor_lastName` INT, `assistantId` INT, `assistant_firstName` INT, `assistant_lastName` INT);

-- -----------------------------------------------------
-- Placeholder table for view `university_project`.`lecturesOfStudent`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `university_project`.`lecturesOfStudent` (`topic` INT, `lectureTimes` INT, `start` INT, `end` INT, `professor_firstName` INT, `professor_lastName` INT, `assistent_firstName` INT, `assistent_lastName` INT, `id` INT, `id_2` INT);

-- -----------------------------------------------------
-- Placeholder table for view `university_project`.`lecturesOfProfessor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `university_project`.`lecturesOfProfessor` (`topic` INT, `lectureTimes` INT, `assistant_firstName` INT, `assistant_lastName` INT, `id` INT);

-- -----------------------------------------------------
-- Placeholder table for view `university_project`.`lecturesOfAssistant`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `university_project`.`lecturesOfAssistant` (`topic` INT, `lectureTimes` INT, `professor_firstName` INT, `professor_lastName` INT, `id` INT);

-- -----------------------------------------------------
-- View `university_project`.`professorData`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `university_project`.`professorData` ;
DROP TABLE IF EXISTS `university_project`.`professorData`;
USE `university_project`;
CREATE  OR REPLACE VIEW `professorData` AS SELECT pr.id, pr.firstName, pr.lastName, pr.birthDate, pr.adress, su.name FROM professors pr LEFT JOIN subjectarea su ON pr.subjectAreaId = su.idsubjectarea;

-- -----------------------------------------------------
-- View `university_project`.`adminData`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `university_project`.`adminData` ;
DROP TABLE IF EXISTS `university_project`.`adminData`;
USE `university_project`;
CREATE  OR REPLACE VIEW `adminData` AS SELECT * FROM administrativeEmployees;

-- -----------------------------------------------------
-- View `university_project`.`assistantData`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `university_project`.`assistantData` ;
DROP TABLE IF EXISTS `university_project`.`assistantData`;
USE `university_project`;
CREATE  OR REPLACE VIEW `assistantData` AS SELECT * FROM assistants;

-- -----------------------------------------------------
-- View `university_project`.`studentData`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `university_project`.`studentData` ;
DROP TABLE IF EXISTS `university_project`.`studentData`;
USE `university_project`;
CREATE  OR REPLACE VIEW `studentData` AS SELECT st.id, st.firstName, st.lastName, st.birthDate, st.adress, su.name FROM students st LEFT JOIN subjectarea su ON st.subjectAreaId = su.idsubjectarea;

-- -----------------------------------------------------
-- View `university_project`.`lecturesView`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `university_project`.`lecturesView` ;
DROP TABLE IF EXISTS `university_project`.`lecturesView`;
USE `university_project`;
CREATE  OR REPLACE VIEW `lecturesView` AS
SELECT l.id, l.topic, l.lectureTimes,l.professorId, p.firstName as professor_firstName, p.lastName as professor_lastName, l.assistantId, a.firstName as assistant_firstName, a.lastName as assistant_lastName
FROM lectures l, professors p, assistants a
WHERE l.professorId = p.id AND l.assistantId = a.id;

-- -----------------------------------------------------
-- View `university_project`.`lecturesOfStudent`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `university_project`.`lecturesOfStudent` ;
DROP TABLE IF EXISTS `university_project`.`lecturesOfStudent`;
USE `university_project`;
CREATE  OR REPLACE VIEW `lecturesOfStudent` AS
SELECT le.topic, le.lectureTimes, li.start, li.end, p.firstName as professor_firstName, p.lastName as professor_lastName, a.firstName as assistent_firstName, a.lastName as assistent_lastName, li.idstudents as id, le.id as id_2
FROM students s, listento li, lectures le, professors p, assistants a
WHERE s.id = li.idstudents AND li.idlectures = le.id AND a.id = le.assistantId AND p.id = le.professorId;

-- -----------------------------------------------------
-- View `university_project`.`lecturesOfProfessor`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `university_project`.`lecturesOfProfessor` ;
DROP TABLE IF EXISTS `university_project`.`lecturesOfProfessor`;
USE `university_project`;
CREATE  OR REPLACE VIEW `lecturesOfProfessor` AS SELECT topic, lectureTimes, assistant_firstName, assistant_lastName, professorId as id FROM lecturesview;

-- -----------------------------------------------------
-- View `university_project`.`lecturesOfAssistant`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `university_project`.`lecturesOfAssistant` ;
DROP TABLE IF EXISTS `university_project`.`lecturesOfAssistant`;
USE `university_project`;
CREATE  OR REPLACE VIEW `lecturesOfAssistant` AS SELECT topic, lectureTimes, professor_firstName, professor_lastName, assistantId as id FROM lecturesview;
SET SQL_MODE = '';
GRANT USAGE ON *.* TO 'assistant'@'localhost';
 DROP USER 'assistant'@'localhost';
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'assistant'@'localhost' IDENTIFIED BY 'assistant';

GRANT SELECT, UPDATE ON TABLE `university_project`.`assistantData` TO 'assistant'@'localhost';
GRANT SELECT ON TABLE `university_project`.`lecturesOfAssistant` TO 'assistant'@'localhost';
SET SQL_MODE = '';
GRANT USAGE ON *.* TO 'professor'@'localhost';
 DROP USER 'professor'@'localhost';
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'professor'@'localhost' IDENTIFIED BY 'professor';

GRANT SELECT ON TABLE `university_project`.`lecturesOfProfessor` TO 'professor'@'localhost';
GRANT UPDATE, SELECT ON TABLE `university_project`.`professorData` TO 'professor'@'localhost';
SET SQL_MODE = '';
GRANT USAGE ON *.* TO 'student'@'localhost';
 DROP USER 'student'@'localhost';
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'student'@'localhost' IDENTIFIED BY 'student';

GRANT SELECT ON TABLE `university_project`.`lecturesOfStudent` TO 'student'@'localhost';
GRANT UPDATE, SELECT ON TABLE `university_project`.`studentData` TO 'student'@'localhost';
SET SQL_MODE = '';
GRANT USAGE ON *.* TO 'adminEmployee'@'localhost';
 DROP USER 'adminEmployee'@'localhost';
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'adminEmployee'@'localhost' IDENTIFIED BY 'adminEmployee';

GRANT ALL ON TABLE `university_project`.`adminData` TO 'adminEmployee'@'localhost';
GRANT ALL ON TABLE `university_project`.`lecturesView` TO 'adminEmployee'@'localhost';
GRANT ALL ON TABLE `university_project`.`professorData` TO 'adminEmployee'@'localhost';
GRANT ALL ON TABLE `university_project`.`studentData` TO 'adminEmployee'@'localhost';
GRANT ALL ON TABLE `university_project`.`assistantData` TO 'adminEmployee'@'localhost';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `university_project`.`subjectArea`
-- -----------------------------------------------------
START TRANSACTION;
USE `university_project`;
INSERT INTO `university_project`.`subjectArea` (`idsubjectarea`, `name`) VALUES (1, 'Mathematik');
INSERT INTO `university_project`.`subjectArea` (`idsubjectarea`, `name`) VALUES (2, 'Biologie');
INSERT INTO `university_project`.`subjectArea` (`idsubjectarea`, `name`) VALUES (3, 'BWL');
INSERT INTO `university_project`.`subjectArea` (`idsubjectarea`, `name`) VALUES (4, 'VWL');
INSERT INTO `university_project`.`subjectArea` (`idsubjectarea`, `name`) VALUES (5, 'Chemie');
INSERT INTO `university_project`.`subjectArea` (`idsubjectarea`, `name`) VALUES (6, 'Psychologie');
INSERT INTO `university_project`.`subjectArea` (`idsubjectarea`, `name`) VALUES (7, 'Theologie');

COMMIT;


-- -----------------------------------------------------
-- Data for table `university_project`.`professors`
-- -----------------------------------------------------
START TRANSACTION;
USE `university_project`;
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (1111, 'Peter', 'Meier', '30061997', 'Stuttgarter Platz 24, 53111 Bonn', 1);
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (2222, 'Tim', 'Hausmeier', '20041953', 'Hauptstrasse 1, 53111 Bonn', 7);
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (3333, 'Hans', 'Mueller', '01061982', 'Rosenfelder Strasse 45, 53111 Bonn', 2);
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (4444, 'Sabine', 'Baum', '03101975', 'Baumplatz 24, 53604 Bad Honnef', 7);
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (5555, 'Michael', 'Ende', '25011966', 'Vierstraßen 5, 53111 Bonn', 6);
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (6666, 'Mike', 'Rosenberger', '15031960', 'Baumhauser Strasse 2, 53111 Bonn', 4);
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (7777, 'Annette', 'Schrüller', '22081956', 'Palmersheimer Strasse 34, 53111 Bonn', 3);
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (8888, 'Thomas', 'Schmitz', '10101980', 'Bonner Paltz 56, 53111 Bonn', 1);
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (9999, 'Thomas', 'Rauhausen', '12121971', 'Fruehfelder Weg 6, 53111 Bonn', 4);
INSERT INTO `university_project`.`professors` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0000, 'Johannes', 'Strauch', '24121968', 'Stadtring 7, 53111 Bonn', 5);

COMMIT;


-- -----------------------------------------------------
-- Data for table `university_project`.`assistants`
-- -----------------------------------------------------
START TRANSACTION;
USE `university_project`;
INSERT INTO `university_project`.`assistants` (`id`, `firstName`, `lastName`, `birthDate`, `adress`) VALUES (2341, 'Annika', 'Gerber', '20041997', 'Musterstraße 1, 43650 Musterdorf');
INSERT INTO `university_project`.`assistants` (`id`, `firstName`, `lastName`, `birthDate`, `adress`) VALUES (2342, 'Johanna', 'Bauer', '02051998', 'Strassenmuster 4, 45234 Daumenhausen');
INSERT INTO `university_project`.`assistants` (`id`, `firstName`, `lastName`, `birthDate`, `adress`) VALUES (2343, 'Niklas', 'Rohfeler', '01011992', 'Hausweg 5, 67234 Hausendorf');
INSERT INTO `university_project`.`assistants` (`id`, `firstName`, `lastName`, `birthDate`, `adress`) VALUES (2344, 'Simon', 'Daumenhaus', '08091993', 'Petersstraße 45, 53604 Bad Honnef');

COMMIT;


-- -----------------------------------------------------
-- Data for table `university_project`.`lectures`
-- -----------------------------------------------------
START TRANSACTION;
USE `university_project`;
INSERT INTO `university_project`.`lectures` (`id`, `professorId`, `assistantId`, `topic`, `lectureTimes`) VALUES (1, 1111, 2341, 'Mathematik Grundlagen', 'Mo: 10:00 Uhr');
INSERT INTO `university_project`.`lectures` (`id`, `professorId`, `assistantId`, `topic`, `lectureTimes`) VALUES (2, 4444, 2342, 'Theologie Grundlagen (katholisch)', 'Di: 15:00 Uhr &  Do: 11:00 Uhr ');
INSERT INTO `university_project`.`lectures` (`id`, `professorId`, `assistantId`, `topic`, `lectureTimes`) VALUES (3, 7777, 2343, 'BWL Grundlagen', 'Fr: 12:00 Uhr');
INSERT INTO `university_project`.`lectures` (`id`, `professorId`, `assistantId`, `topic`, `lectureTimes`) VALUES (4, 3333, 2343, 'Biologie Grundlagen', 'Mi: 13:00 Uhr');
INSERT INTO `university_project`.`lectures` (`id`, `professorId`, `assistantId`, `topic`, `lectureTimes`) VALUES (5, 1111, 2341, 'Stochhastik', 'Fr: 17:00 Uhr');

COMMIT;


-- -----------------------------------------------------
-- Data for table `university_project`.`students`
-- -----------------------------------------------------
START TRANSACTION;
USE `university_project`;
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0111, 'Ben', 'Bruns', '20061997', 'Musterstrasse 91, 43650 Musterdorf', 1);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0222, 'Ella', 'Mueller', '03011993', 'Eulenweg 60, 45897 Dingsdorf', 4);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0333, 'Johanna', 'Cordes', '20011991', 'Sonnenplatz 5, 53643 Dorfdorf', 6);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0444, 'Vincent', 'Harms', '02121998', 'Blumenweg 4, 23546 Blumenstadt', 7);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0555, 'Erik', 'Janssen', '25111997', 'Baumstrasse 45, 45234 Dransdorf', 3);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0666, 'Johann', 'Schuster', '06111989', 'Spielstrasse 8, 75234 Stadtdorf', 2);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0777, 'Tim', 'Lackmann', '08081990', 'Blauberg 34, 34542 Hansenei', 5);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0888, 'Carl', 'Schulz', '02091992', 'An der Schule 5, 45678 Dausendorf', 3);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0999, 'Grata', 'Braun', '31121990', 'Datenhausener Straße 56, 53604 Bad Honnef', 1);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0000, 'Mira', 'Smitt', '05051988', 'Blauberg 134, 34542 Hansenei', 6);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0234, 'Henry', 'Krause', '12081993', 'Schwarzberg 6, 34543 Baumhausen', 4);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0684, 'Felix', 'Laule', '10071995', 'Datenhausener Straße 56, 53604 Bad Honnef', 7);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0696, 'Finn', 'Schmidt', '05041996', 'Spielstrasse 98, 75234 Stadtdorf', 3);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0369, 'Linus', 'Werner', '02021994', 'Spielstrasse 18, 75234 Stadtdorf', 5);
INSERT INTO `university_project`.`students` (`id`, `firstName`, `lastName`, `birthDate`, `adress`, `subjectAreaId`) VALUES (0585, 'Sofia', 'Kaiser', '29101990', 'Blauberg 4, 34542 Hansenei', 4);

COMMIT;


-- -----------------------------------------------------
-- Data for table `university_project`.`administrativeEmployees`
-- -----------------------------------------------------
START TRANSACTION;
USE `university_project`;
INSERT INTO `university_project`.`administrativeEmployees` (`id`, `firstName`, `lastName`, `birthDate`, `adress`) VALUES (1231, 'Perter', 'Petermeier', '20051990', 'Hauptstrasse 300, 53111  Bonn');
INSERT INTO `university_project`.`administrativeEmployees` (`id`, `firstName`, `lastName`, `birthDate`, `adress`) VALUES (1232, 'Hugo', 'Rheinhausen', '10021976', 'An der Baumschule 3, 53639 Koenigswinter');
INSERT INTO `university_project`.`administrativeEmployees` (`id`, `firstName`, `lastName`, `birthDate`, `adress`) VALUES (1233, 'Saskia', 'Schwung', '20031989', 'Datenhausener Straße 6, 53604 Bad Honnef');
INSERT INTO `university_project`.`administrativeEmployees` (`id`, `firstName`, `lastName`, `birthDate`, `adress`) VALUES (1234, 'Franziska', 'Baumann', '30121975', 'Traumweg 8, 53111 Bonn');

COMMIT;


-- -----------------------------------------------------
-- Data for table `university_project`.`listenTo`
-- -----------------------------------------------------
START TRANSACTION;
USE `university_project`;
INSERT INTO `university_project`.`listenTo` (`idstudents`, `idlectures`, `start`, `end`) VALUES (0111, 1, '01042017', '28072017');
INSERT INTO `university_project`.`listenTo` (`idstudents`, `idlectures`, `start`, `end`) VALUES (0111, 4, '01042017', '28072017');
INSERT INTO `university_project`.`listenTo` (`idstudents`, `idlectures`, `start`, `end`) VALUES (0222, 3, '01042017', '28072017');
INSERT INTO `university_project`.`listenTo` (`idstudents`, `idlectures`, `start`, `end`) VALUES (0333, 2, '01042017', '28072017');
INSERT INTO `university_project`.`listenTo` (`idstudents`, `idlectures`, `start`, `end`) VALUES (0444, 5, '01042017', '28072017');
INSERT INTO `university_project`.`listenTo` (`idstudents`, `idlectures`, `start`, `end`) VALUES (0555, 1, '01042017', '28072017');
INSERT INTO `university_project`.`listenTo` (`idstudents`, `idlectures`, `start`, `end`) VALUES (0555, 5, '01042017', '28072017');
INSERT INTO `university_project`.`listenTo` (`idstudents`, `idlectures`, `start`, `end`) VALUES (0555, 3, '01042017', '28072017');

COMMIT;

