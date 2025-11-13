-- ======================================================================
-- Elite Couture - Supabase Database Schema
-- ======================================================================
-- Este script crea las tablas en Supabase PostgreSQL para migrar desde SQLite
-- Ejecutar en: Supabase Dashboard > SQL Editor

-- ======================================================================
-- 1. TABLA: users
-- ======================================================================
CREATE TABLE IF NOT EXISTS public.users (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    email TEXT NOT NULL UNIQUE,
    password TEXT, -- Nullable para usuarios OAuth en el futuro
    first_name TEXT NOT NULL,
    last_name TEXT,
    address TEXT,
    is_guest BOOLEAN NOT NULL DEFAULT false,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::BIGINT -- Timestamp en milisegundos
);

-- Índices para búsquedas comunes
CREATE INDEX IF NOT EXISTS idx_users_email ON public.users(email);
CREATE INDEX IF NOT EXISTS idx_users_uuid ON public.users(uuid);
CREATE INDEX IF NOT EXISTS idx_users_is_guest ON public.users(is_guest);

-- ======================================================================
-- 2. TABLA: products
-- ======================================================================
CREATE TABLE IF NOT EXISTS public.products (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    type TEXT, -- Tipo de producto (ej: "Vestido", "Camisa")
    gender TEXT, -- "Hombre", "Mujer", "Unisex"
    description TEXT,
    price DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    images TEXT[] NOT NULL DEFAULT '{}', -- Array de URLs de Supabase Storage
    tags TEXT[] NOT NULL DEFAULT '{}', -- Array de tags para filtrado
    is_visible_to_guest BOOLEAN NOT NULL DEFAULT true,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::BIGINT
);

-- Índices para búsquedas y filtros
CREATE INDEX IF NOT EXISTS idx_products_uuid ON public.products(uuid);
CREATE INDEX IF NOT EXISTS idx_products_name ON public.products(name);
CREATE INDEX IF NOT EXISTS idx_products_type ON public.products(type);
CREATE INDEX IF NOT EXISTS idx_products_gender ON public.products(gender);
CREATE INDEX IF NOT EXISTS idx_products_is_visible_to_guest ON public.products(is_visible_to_guest);
CREATE INDEX IF NOT EXISTS idx_products_tags ON public.products USING GIN(tags); -- GIN index para arrays

-- ======================================================================
-- 3. TABLA: cart_items
-- ======================================================================
CREATE TABLE IF NOT EXISTS public.cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_uuid UUID NOT NULL REFERENCES public.users(uuid) ON DELETE CASCADE,
    product_uuid UUID NOT NULL REFERENCES public.products(uuid) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    added_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::BIGINT,
    UNIQUE(user_uuid, product_uuid) -- Un producto por usuario en el carrito
);

-- Índices para consultas de carrito
CREATE INDEX IF NOT EXISTS idx_cart_items_user_uuid ON public.cart_items(user_uuid);
CREATE INDEX IF NOT EXISTS idx_cart_items_product_uuid ON public.cart_items(product_uuid);

-- ======================================================================
-- 4. TABLA: favorites
-- ======================================================================
CREATE TABLE IF NOT EXISTS public.favorites (
    id BIGSERIAL PRIMARY KEY,
    user_uuid UUID NOT NULL REFERENCES public.users(uuid) ON DELETE CASCADE,
    product_uuid UUID NOT NULL REFERENCES public.products(uuid) ON DELETE CASCADE,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::BIGINT,
    UNIQUE(user_uuid, product_uuid) -- Un producto favorito por usuario
);

-- Índices para consultas de favoritos
CREATE INDEX IF NOT EXISTS idx_favorites_user_uuid ON public.favorites(user_uuid);
CREATE INDEX IF NOT EXISTS idx_favorites_product_uuid ON public.favorites(product_uuid);

-- ======================================================================
-- 5. ROW LEVEL SECURITY (RLS) POLICIES
-- ======================================================================

-- Habilitar RLS en todas las tablas
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.products ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.cart_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.favorites ENABLE ROW LEVEL SECURITY;

-- ======================================================================
-- POLÍTICAS: users
-- ======================================================================

-- Permitir SELECT público (para login, validación de email)
CREATE POLICY "users_select_policy" ON public.users
    FOR SELECT
    USING (true);

-- Permitir INSERT público (para registro de usuarios)
CREATE POLICY "users_insert_policy" ON public.users
    FOR INSERT
    WITH CHECK (true);

-- Permitir UPDATE público (para editar perfil - en producción filtrar por auth.uid())
CREATE POLICY "users_update_policy" ON public.users
    FOR UPDATE
    USING (true);

-- Permitir DELETE público (para desarrollo - en producción filtrar por auth.uid())
CREATE POLICY "users_delete_policy" ON public.users
    FOR DELETE
    USING (true);

-- ======================================================================
-- POLÍTICAS: products
-- ======================================================================

-- Permitir SELECT público (todos pueden ver productos)
CREATE POLICY "products_select_policy" ON public.products
    FOR SELECT
    USING (true);

-- Permitir INSERT público (para desarrollo - en producción solo admins)
CREATE POLICY "products_insert_policy" ON public.products
    FOR INSERT
    WITH CHECK (true);

-- Permitir UPDATE público (para desarrollo - en producción solo admins)
CREATE POLICY "products_update_policy" ON public.products
    FOR UPDATE
    USING (true);

-- Permitir DELETE público (para desarrollo - en producción solo admins)
CREATE POLICY "products_delete_policy" ON public.products
    FOR DELETE
    USING (true);

-- ======================================================================
-- POLÍTICAS: cart_items
-- ======================================================================

-- Permitir SELECT público
CREATE POLICY "cart_items_select_policy" ON public.cart_items
    FOR SELECT
    USING (true);

-- Permitir INSERT público
CREATE POLICY "cart_items_insert_policy" ON public.cart_items
    FOR INSERT
    WITH CHECK (true);

-- Permitir UPDATE público
CREATE POLICY "cart_items_update_policy" ON public.cart_items
    FOR UPDATE
    USING (true);

-- Permitir DELETE público
CREATE POLICY "cart_items_delete_policy" ON public.cart_items
    FOR DELETE
    USING (true);

-- ======================================================================
-- POLÍTICAS: favorites
-- ======================================================================

-- Permitir SELECT público
CREATE POLICY "favorites_select_policy" ON public.favorites
    FOR SELECT
    USING (true);

-- Permitir INSERT público
CREATE POLICY "favorites_insert_policy" ON public.favorites
    FOR INSERT
    WITH CHECK (true);

-- Permitir UPDATE público
CREATE POLICY "favorites_update_policy" ON public.favorites
    FOR UPDATE
    USING (true);

-- Permitir DELETE público
CREATE POLICY "favorites_delete_policy" ON public.favorites
    FOR DELETE
    USING (true);

-- ======================================================================
-- 6. FUNCIONES HELPER
-- ======================================================================

-- Función para obtener timestamp en milisegundos
CREATE OR REPLACE FUNCTION get_timestamp_ms()
RETURNS BIGINT AS $$
BEGIN
    RETURN (extract(epoch from now()) * 1000)::BIGINT;
END;
$$ LANGUAGE plpgsql;

-- ======================================================================
-- 7. DATOS DE PRUEBA (OPCIONAL)
-- ======================================================================

-- Usuario de prueba
INSERT INTO public.users (uuid, email, password, first_name, last_name, is_guest, created_at)
VALUES (
    gen_random_uuid(),
    'test@elitecouture.com',
    'test123', -- En producción usar hash
    'Test',
    'User',
    false,
    (extract(epoch from now()) * 1000)::BIGINT
) ON CONFLICT (email) DO NOTHING;

-- Usuario invitado
INSERT INTO public.users (uuid, email, password, first_name, last_name, is_guest, created_at)
VALUES (
    gen_random_uuid(),
    'guest@elitecouture.com',
    NULL,
    'Guest',
    NULL,
    true,
    (extract(epoch from now()) * 1000)::BIGINT
) ON CONFLICT (email) DO NOTHING;

-- Producto de prueba
INSERT INTO public.products (uuid, name, type, gender, description, price, stock, images, tags, is_visible_to_guest, created_at)
VALUES (
    gen_random_uuid(),
    'Vestido Elegante',
    'Vestido',
    'Mujer',
    'Vestido elegante para ocasiones especiales',
    99.99,
    10,
    ARRAY['https://tjhhqwizpiywyrwjpgrg.supabase.co/storage/v1/object/public/product-images/product_01/17097806_76_D2.avif'],
    ARRAY['Vestido', 'Mujer', 'Elegante', 'Fiesta'],
    true,
    (extract(epoch from now()) * 1000)::BIGINT
) ON CONFLICT (uuid) DO NOTHING;

-- ======================================================================
-- ✅ SCHEMA CREADO EXITOSAMENTE
-- ======================================================================
-- Próximos pasos:
-- 1. Copiar y ejecutar este script en Supabase Dashboard > SQL Editor
-- 2. Verificar que las tablas se crearon correctamente
-- 3. Crear servicios de Kotlin para interactuar con Supabase
-- 4. Migrar datos existentes (si hay)
-- 5. Probar CRUD completo
-- 6. Remover código de SQLite/Room
-- ======================================================================
