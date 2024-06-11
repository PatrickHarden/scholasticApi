/*
Update student_lexile_status table - add current_question & time_spent columns
*/
ALTER TABLE student_lexile_status
ADD COLUMN current_question INT(11) NOT NULL,
ADD COLUMN time_spent BIGINT NOT NULL DEFAULT 0;