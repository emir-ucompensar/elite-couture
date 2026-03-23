# Elite Couture

An Android e-commerce app for fashion, built with Kotlin. Academic project implementing authentication, product catalog, shopping cart, favorites, and category filtering.

---

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| Build | Gradle 9.1.0 |
| SDK | Android 10+ (API 29–34) |
| IDE | Visual Studio Code |
| Database | SQLite |
| UI | Material Design 3 + Coil 2.5.0 |

---

## Installation

```bash
# Clone the repository
git clone https://github.com/emir-ucompensar/elite-couture.git
cd elite-couture

# Build APK
gradle assembleDebug

# Install on device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

> **Requirements:** Python 3.7+ and ADB installed.

---

## Features

- **Authentication:** Login, registration, and guest mode
- **User Profile:** Editable profile with validation and AES-256 encryption
- **Shopping Cart:** Add/remove items, adjust quantities
- **Favorites:** Swipe-to-delete with undo option
- **Catalog:** 25 products with images, filterable by category and tags
- **Tag System:** Visual labels for product classification
- **Side Menu:** Navigation by category (Men / Women)
- **Database:** SQLite with 5 related tables ([View ERD](scripting/schemas/exports/DATABASE_SCHEMA.md))
- **Location System:** Find nearby stores or set a delivery address
- **Camera Support:** Capture images directly from the device camera

---

## Architecture

```
app/
├── data/              # Data layer (DAOs, entities, repositories)
├── domain/            # Business logic (models, use cases)
├── ui/                # User interface (fragments, adapters)
│   ├── common/        # Shared components
│   └── feature/       # Feature modules
└── util/              # Utilities and helpers
```

**Patterns used:** Repository, Use Case, Service Locator, lightweight MVVM

### Database (v8)

- **users** — Authentication and profiles
- **products** — Catalog with tags and images
- **cart_items** — Shopping cart
- **favorites** — Saved products
- **store** — Physical store locations

[View full ERD diagram](scripting/schemas/exports/DATABASE_SCHEMA.md)

![ERD](scripting/schemas/exports/database_erd_from_md.png)

---

## Dev Scripts (scripting/)

| Script | Description |
|---|---|
| `export_erd_from_md.py` | Auto-generates the ERD diagram |
| `export_erd_to_png.py` | Exports ERD to PNG |
| `copy_product_images_v1.py` | Processes product images |
| `test_favorites.py` | Real-time log monitor |
| `check_favorites_db.py` | Database query tool |
| `helper.py` | Device info and useful commands |

[View full scripting docs](scripting/README.md)

---

## Design Decisions

**Why VS Code instead of Android Studio?**

To better understand Gradle's build system mechanics, develop through the command line without IDE dependency, and maintain full control over project configuration.

---

## License

MIT License — Academic project, free for educational use.
