CREATE TABLE `mst_user`(
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`userName` VARCHAR(100) NOT NULL,
	`password` VARCHAR(500) NOT NULL,
	`is_active` tinyint(1) NOT NULL DEFAULT '1',
  	`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	`created_by` VARCHAR(20) DEFAULT 'system',
  	`modified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  	`modified_by` VARCHAR(20) DEFAULT 'system',
	PRIMARY KEY(`id`),
	UNIQUE KEY `UQ_user` (`userName`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8;

CREATE TABLE `mst_role`(
	`id` INT(2) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL,
	`is_active` tinyint(1) NOT NULL DEFAULT '1',
  	`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	`created_by` VARCHAR(20) DEFAULT 'system',
  	`modified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  	`modified_by` VARCHAR(20) DEFAULT 'system',
	PRIMARY KEY(`id`),
	UNIQUE KEY `UQ_role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `role_resource_assoc`(
	`role_id` INT(2) NOT NULL,
	`resource` VARCHAR(100) NOT NULL,
	`is_active` tinyint(1) NOT NULL DEFAULT '1',
  	`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	`created_by` VARCHAR(20) DEFAULT 'system',
  	`modified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  	`modified_by` VARCHAR(20) DEFAULT 'system',
	PRIMARY KEY(`role_id`, `resource`),
	CONSTRAINT `FK_PK_role_resource_assoc` FOREIGN KEY (`role_id`) REFERENCES `mst_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_role_assoc`(
	`user_id` INT(11) NOT NULL,
	`role_id` INT(2) NOT NULL,
	`is_active` tinyint(1) NOT NULL DEFAULT '1',
  	`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	`created_by` VARCHAR(20) DEFAULT 'system',
  	`modified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  	`modified_by` VARCHAR(20) DEFAULT 'system',
	PRIMARY KEY(`user_id`, `role_id`),
	CONSTRAINT `FK_PK_user_role_assoc_user` FOREIGN KEY (`user_id`) REFERENCES `mst_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
	CONSTRAINT `FK_PK_user_role_assoc_role` FOREIGN KEY (`role_id`) REFERENCES `mst_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `mst_user`(`userName`, `password`) VALUES('sarvesh', 'password');

INSERT INTO `mst_role`(`name`) VALUES('reviewer');

SELECT * FROM mst_user;

SELECT * FROM mst_role;

SELECT * FROM role_resource_assoc;

INSERT INTO `user_role_assoc`(`user_id`, `role_id`) VALUES(1000, 1);

UPDATE mst_user SET `password` = '$2a$10$Af7jVDceMUfyIXQxeMAdTO9OL233V6MkP6mK6nqnTZKFa.AF0DGzu' WHERE `id` = 1000;

INSERT INTO `role_resource_assoc`(`role_id`, `resource`) VALUES(1, '/api/org/team/getAll');
