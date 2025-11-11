-- Habilitar Row Level Security (RLS)
ALTER TABLE stores ENABLE ROW LEVEL SECURITY;

-- Política: Permitir acceso completo solo a usuarios autenticados
CREATE POLICY "Authenticated users can access stores" ON stores
    FOR ALL
    USING (auth.role() = 'authenticated');
-- Supabase SQL schema para la tabla de tiendas físicas de Elite Couture
CREATE TABLE stores (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    phone VARCHAR(30),
    hours VARCHAR(100),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Índice para búsquedas geográficas
CREATE INDEX idx_stores_location ON stores (latitude, longitude);
