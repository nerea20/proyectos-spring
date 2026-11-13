USE ventas;
CREATE TABLE IF NOT EXISTS `cliente_has_comercial` (
  `cliente_id` INT UNSIGNED NOT NULL,
  `comercial_id` INT UNSIGNED NOT NULL,
  `fecha` DATE NOT NULL,
  `prioridad` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`cliente_id`, `comercial_id`),
  INDEX `fk_cliente_has_comercial_comercial1_idx` (`comercial_id` ASC) VISIBLE,
  INDEX `fk_cliente_has_comercial_cliente1_idx` (`cliente_id` ASC) VISIBLE,
  CONSTRAINT `fk_cliente_has_comercial_cliente1`
    FOREIGN KEY (`cliente_id`)
    REFERENCES `cliente` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cliente_has_comercial_comercial1`
    FOREIGN KEY (`comercial_id`)
    REFERENCES `comercial` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)

ENGINE = InnoDB;

SET @tiene_fecha = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cliente_has_comercial'
    AND COLUMN_NAME = 'fecha'
);
SET @sql_fecha = IF(@tiene_fecha = 0,
  'ALTER TABLE cliente_has_comercial ADD COLUMN fecha DATE NULL',
  'SELECT 1'
);
PREPARE sentencia_fecha FROM @sql_fecha;
EXECUTE sentencia_fecha;
DEALLOCATE PREPARE sentencia_fecha;

SET @tiene_prioridad = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cliente_has_comercial'
    AND COLUMN_NAME = 'prioridad'
);
SET @sql_prioridad = IF(@tiene_prioridad = 0,
  'ALTER TABLE cliente_has_comercial ADD COLUMN prioridad INT UNSIGNED NOT NULL DEFAULT 1',
  'SELECT 1'
);
PREPARE sentencia_prioridad FROM @sql_prioridad;
EXECUTE sentencia_prioridad;
DEALLOCATE PREPARE sentencia_prioridad;