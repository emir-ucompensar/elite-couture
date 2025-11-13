-- Consulta SQL para exportar tablas y relaciones (claves foráneas) del esquema público
-- Ejecuta esto directamente en el SQL Editor de Supabase para obtener toda la información necesaria para un ERD


-- Tablas y columnas (una fila por columna)
SELECT 'COLUMN' AS type,
       c.table_name,
       c.column_name,
       c.data_type,
       c.is_nullable,
       c.column_default,
       NULL AS referenced_table,
       NULL AS referenced_column
FROM information_schema.columns c
WHERE c.table_schema = 'public'
ORDER BY c.table_name, c.ordinal_position;




SELECT 'FK' AS type,
       kcu.table_name,
       kcu.column_name,
       NULL AS data_type,
       NULL AS is_nullable,
       NULL AS column_default,
       ccu.table_name AS referenced_table,
       ccu.column_name AS referenced_column
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
  ON tc.constraint_name = kcu.constraint_name
  AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage ccu
  ON ccu.constraint_name = tc.constraint_name
  AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema = 'public'
ORDER BY kcu.table_name, kcu.column_name;

-- Puedes exportar el resultado como CSV y usarlo para construir el ERD en cualquier herramienta.