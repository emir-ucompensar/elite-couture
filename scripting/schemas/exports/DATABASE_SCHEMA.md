# Elite Couture - Diagrama de Base de Datos

Este documento fue generado automáticamente a partir de los datos en Supabase.

## Diagrama ERD

```mermaid
erDiagram

    CART_ITEMS {
        bigint id
        uuid user_uuid
        uuid product_uuid
        int quantity
        bigint added_at
    }

    FAVORITES {
        bigint id
        uuid user_uuid
        uuid product_uuid
        bigint created_at
    }

    PRODUCTS {
        bigint id
        uuid uuid
        string name
        string type
        string gender
        string description
        double_precision price
        int stock
        array images
        array tags
        boolean is_visible_to_guest
        bigint created_at
    }

    STORES {
        int id
        character_varying name
        character_varying address
        character_varying phone
        character_varying hours
        double_precision latitude
        double_precision longitude
        timestamp_with_time_zone created_at
        timestamp_with_time_zone updated_at
    }

    USERS {
        bigint id
        uuid uuid
        string email
        string password
        string first_name
        string last_name
        string address
        boolean is_guest
        bigint created_at
    }

    CART_ITEMS ||--o| PRODUCTS : product_uuid -> uuid
    CART_ITEMS ||--o| USERS : user_uuid -> uuid
    FAVORITES ||--o| PRODUCTS : product_uuid -> uuid
    FAVORITES ||--o| USERS : user_uuid -> uuid
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
