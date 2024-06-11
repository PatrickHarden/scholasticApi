CREATE TABLE admin_batch_assignment_srm (
  id int NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  benchmark varchar(45) NOT NULL,
  assigned_date datetime NOT NULL,
  end_date datetime DEFAULT NULL,
  school_id bigint NOT NULL,
  grade varchar(10) NOT NULL,
  created_date timestamp NOT NULL,
  modified_date timestamp NOT NULL,
  active tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (id),
  KEY idx_user_id_assigned_date_school_id (user_id,assigned_date,school_id)
);

CREATE TABLE student_lexile_status (
  id int NOT NULL AUTO_INCREMENT,
  student_id bigint NOT NULL,
  teacher_id bigint DEFAULT NULL,
  benchmark varchar(255) NOT NULL,
  assigned_date datetime NOT NULL,
  completed_date datetime DEFAULT NULL,
  lexile decimal(6,2) DEFAULT NULL,
  status varchar(255) DEFAULT NULL,
  assessment_test bigint DEFAULT NULL,
  end_date datetime DEFAULT NULL,
  admin_assignment_id int DEFAULT NULL,
  teacher_judgement varchar(255) DEFAULT NULL,
  cancelled_by_user_id bigint DEFAULT NULL,
  modified_date timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_student_id (student_id)
);