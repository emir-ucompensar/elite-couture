# 📸 Directorio de Imágenes de Productos - Elite Couture

## 📁 Estructura de Carpetas

```
product_images/
├── product_01/
│   ├── 1.jpg (o .png)
│   ├── 2.jpg
│   └── 3.jpg
├── product_02/
│   ├── 1.jpg
│   ├── 2.jpg
│   └── 3.jpg
├── product_03/
│   ├── 1.jpg
│   ├── 2.jpg
│   └── 3.jpg
├── product_04/
│   ├── 1.jpg
│   ├── 2.jpg
│   └── 3.jpg
├── product_05/
│   ├── 1.jpg
│   ├── 2.jpg
│   └── 3.jpg
├── product_06/
│   ├── 1.jpg
│   ├── 2.jpg
│   └── 3.jpg
└── product_07/
    ├── 1.jpg
    ├── 2.jpg
    └── 3.jpg
```

## 📋 Instrucciones

### 1. Copiar tus imágenes:
- Coloca las **3 fotos** de cada prenda en su carpeta correspondiente
- Nómbralas: `1.jpg`, `2.jpg`, `3.jpg` (o `.png`)
- **Importante:** Cada producto debe tener exactamente 3 imágenes

### 2. Formato recomendado:
- **Formato:** JPG o PNG
- **Resolución recomendada:** 1080x1440 px (ratio 3:4, estándar e-commerce)
- **Tamaño máximo por imagen:** 500 KB (para rendimiento)
- **Fondo:** Preferiblemente blanco o neutro

### 3. Orden de las fotos:
Para mejor experiencia en el carrusel:
- **1.jpg** - Vista frontal/principal
- **2.jpg** - Vista lateral o detalle
- **3.jpg** - Vista trasera o contexto/modelo

### 4. Tips para fotos de ropa:
✅ Buena iluminación (luz natural preferible)
✅ Fondo limpio y uniforme
✅ Producto centrado en el encuadre
✅ Mostrar la prenda completa
✅ Mismo estilo de foto para todas las prendas (consistencia)

## 🔄 Después de agregar las imágenes:

Una vez tengas las fotos en sus carpetas, necesitarás:

1. **Copiarlas a `res/drawable-nodpi/`** con este formato:
   ```
   product_01_img_1.jpg
   product_01_img_2.jpg
   product_01_img_3.jpg
   product_02_img_1.jpg
   ...
   ```

2. **Actualizar `products_seed.json`** con la información de cada producto:
   - Nombre de la prenda
   - Descripción
   - Precio
   - Categoría/Tipo
   - Stock disponible

## 📝 Template para indexar productos:

Cuando estés listo para indexar, usa esta plantilla:

```
PRODUCTO 01:
- Nombre: _______________
- Descripción: _______________
- Tipo: (Vestido/Blusa/Pantalón/Falda/etc)
- Precio: $_____________
- Stock: _______________

PRODUCTO 02:
...
```

## 🚀 Script de Copia Automática

Puedes usar este script PowerShell para copiar automáticamente las imágenes al proyecto:

```powershell
# Ejecutar desde: product_images/
for ($i=1; $i -le 7; $i++) {
    $productNum = "{0:D2}" -f $i
    for ($j=1; $j -le 3; $j++) {
        Copy-Item "product_$productNum\$j.jpg" `
                  "..\app\src\main\res\drawable-nodpi\product_${productNum}_img_$j.jpg"
    }
}
```

---

**Nota:** Este directorio (`product_images/`) está fuera del código de la app. Es solo para organización.
Las imágenes finales irán en `app/src/main/res/drawable-nodpi/`
