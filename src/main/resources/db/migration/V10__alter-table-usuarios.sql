ALTER TABLE usuarios ADD COLUMN a2f_tipo INT DEFAULT 0;

UPDATE usuarios SET a2f_tipo = 1 WHERE a2f_ativa = 1;
UPDATE usuarios SET a2f_tipo = 0 WHERE a2f_ativa = 0;

ALTER TABLE usuarios DROP COLUMN a2f_ativa;
