# Elite Couture - Diagrama de Base de Datos

Este documento fue generado automáticamente a partir de los datos en Supabase.

## Diagrama ERD

```mermaid
erDiagram

    CART_ITEMS {
        bigint id_NOT_NULL
        uuid user_uuid_NOT_NULL
        uuid product_uuid_NOT_NULL
        int quantity_NOT_NULL
        bigint added_at_NOT_NULL
    }

    FAVORITES {
        bigint id_NOT_NULL
        uuid user_uuid_NOT_NULL
        uuid product_uuid_NOT_NULL
        bigint created_at_NOT_NULL
    }

    PRODUCTS {
        bigint id_NOT_NULL
        uuid uuid_NOT_NULL
        string name_NOT_NULL
        string type_NULLABLE
        string gender_NULLABLE
        string description_NULLABLE
        double price_NOT_NULL
        int stock_NOT_NULL
        string[] images_NOT_NULL
        string[] tags_NOT_NULL
        int is_visible_to_guest_NOT_NULL
        bigint created_at_NOT_NULL
    }

    STORES {
        int id_NOT_NULL
        string name_NOT_NULL
        string address_NOT_NULL
        string phone_NULLABLE
        string hours_NULLABLE
        double latitude_NOT_NULL
        double longitude_NOT_NULL
        timestamp created_at_NULLABLE
        timestamp updated_at_NULLABLE
    }

    USERS {
        bigint id_NOT_NULL
        uuid uuid_NOT_NULL
        string email_NOT_NULL
        string password_NULLABLE
        string first_name_NOT_NULL
        string last_name_NULLABLE
        string address_NULLABLE
        int is_guest_NOT_NULL
        bigint created_at_NOT_NULL
    }

    CART_ITEMS ||--o| PRODUCTS : contains
    CART_ITEMS ||--o| USERS    : belongs_to
    FAVORITES  ||--o| PRODUCTS : marks
    FAVORITES  ||--o| USERS    : belongs_to

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
