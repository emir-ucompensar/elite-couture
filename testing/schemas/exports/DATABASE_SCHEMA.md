# Elite Couture - Diagrama de Base de Datos

Este documento fue generado automáticamente a partir de los datos en Supabase.

## Diagrama ERD

```mermaid
erDiagram

    CART_ITEMS {
        bigint id "NOT NULL"
        uuid user_uuid "NOT NULL"
        uuid product_uuid "NOT NULL"
        int quantity "NOT NULL"
        bigint added_at "NOT NULL"
    }

    FAVORITES {
        bigint id "NOT NULL"
        uuid user_uuid "NOT NULL"
        uuid product_uuid "NOT NULL"
        bigint created_at "NOT NULL"
    }

    PRODUCTS {
        bigint id "NOT NULL"
        uuid uuid "NOT NULL"
        string name "NOT NULL"
        string type
        string gender
        string description
        double precision price "NOT NULL"
        int stock "NOT NULL"
        array images "NOT NULL"
        array tags "NOT NULL"
        int is_visible_to_guest "NOT NULL"
        bigint created_at "NOT NULL"
    }

    STORES {
        int id "NOT NULL"
        character varying name "NOT NULL"
        character varying address "NOT NULL"
        character varying phone
        character varying hours
        double precision latitude "NOT NULL"
        double precision longitude "NOT NULL"
        timestamp with time zone created_at
        timestamp with time zone updated_at
    }

    USERS {
        bigint id "NOT NULL"
        uuid uuid "NOT NULL"
        string email "NOT NULL"
        string password
        string first_name "NOT NULL"
        string last_name
        string address
        int is_guest "NOT NULL"
        bigint created_at "NOT NULL"
    }

    PRODUCTS ||--o{ CART_ITEMS : "has"
    USERS ||--o{ CART_ITEMS : "has"
    PRODUCTS ||--o{ FAVORITES : "has"
    USERS ||--o{ FAVORITES : "has"
```

## Descripción de Tablas

### `cart_items`

Items en el carrito de compras de cada usuario con cantidad.

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | BIGINT | NOT NULL |
| `user_uuid` | UUID | NOT NULL |
| `product_uuid` | UUID | NOT NULL |
| `quantity` | INTEGER | NOT NULL |
| `added_at` | BIGINT | NOT NULL |

**Foreign Keys:**

- `user_uuid` → `users.uuid`
- `product_uuid` → `products.uuid`

### `favorites`

Productos marcados como favoritos por cada usuario.

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | BIGINT | NOT NULL |
| `user_uuid` | UUID | NOT NULL |
| `product_uuid` | UUID | NOT NULL |
| `created_at` | BIGINT | NOT NULL |

**Foreign Keys:**

- `user_uuid` → `users.uuid`
- `product_uuid` → `products.uuid`

### `products`

Catálogo de productos de la tienda con imágenes, precios y tags para filtrado.

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | BIGINT | NOT NULL |
| `uuid` | UUID | NOT NULL |
| `name` | TEXT | NOT NULL |
| `type` | TEXT | - |
| `gender` | TEXT | - |
| `description` | TEXT | - |
| `precision` | REAL | - |
| `stock` | INTEGER | NOT NULL |
| `images` | ARRAY | NOT NULL |
| `tags` | ARRAY | NOT NULL |
| `is_visible_to_guest` | INTEGER | NOT NULL |
| `created_at` | BIGINT | NOT NULL |

### `stores`



| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | INTEGER | NOT NULL |
| `varying` | CHARACTER | - |
| `varying` | CHARACTER | - |
| `varying` | CHARACTER | - |
| `varying` | CHARACTER | - |
| `precision` | REAL | - |
| `precision` | REAL | - |
| `with` | TIMESTAMP | - |
| `with` | TIMESTAMP | - |

### `users`

Almacena información de usuarios registrados y usuarios invitados.

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | BIGINT | NOT NULL |
| `uuid` | UUID | NOT NULL |
| `email` | TEXT | NOT NULL |
| `password` | TEXT | - |
| `first_name` | TEXT | NOT NULL |
| `last_name` | TEXT | - |
| `address` | TEXT | - |
| `is_guest` | INTEGER | NOT NULL |
| `created_at` | BIGINT | NOT NULL |

## Relaciones

- **users** ↔ **cart_items**: Un usuario puede tener múltiples items en su carrito
- **users** ↔ **favorites**: Un usuario puede tener múltiples productos favoritos
- **products** ↔ **cart_items**: Un producto puede estar en múltiples carritos
- **products** ↔ **favorites**: Un producto puede ser favorito de múltiples usuarios

## Notas

- Versión de base de datos: 7
- Las columnas `images` y `tags` usan pipe (`|`) como separador
- Las relaciones tienen `ON DELETE CASCADE` para integridad referencial

---
*Generado automáticamente con `generate_erd.py`*
