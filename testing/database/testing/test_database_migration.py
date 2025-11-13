"""
Elite Couture - Complete Database Migration Test
================================================
Este script prueba TODAS las operaciones CRUD en las tablas de Supabase:
- Users (CREATE, READ, UPDATE, DELETE)
- Products (CREATE, READ, UPDATE, DELETE)
- Favorites (CREATE, READ, DELETE)
- Cart Items (CREATE, READ, UPDATE, DELETE)
"""

import os
import time
from supabase import create_client, Client
from dotenv import load_dotenv

# ======================================================================
# CONFIGURACIÓN
# ======================================================================

# Cargar .env desde la raíz de testing/
env_path = os.path.join(os.path.dirname(__file__), '../../', '.env')
load_dotenv(env_path)

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_ANON_KEY")

# Colores ANSI
class Colors:
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    CYAN = '\033[96m'
    MAGENTA = '\033[95m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

# Tracking de resultados
test_results = {}

# ======================================================================
# FUNCIONES DE UTILIDAD
# ======================================================================

def print_header(title: str):
    """Imprime un encabezado con formato"""
    print(f"\n{Colors.CYAN}{Colors.BOLD}{'=' * 70}{Colors.RESET}")
    print(f"{Colors.CYAN}{Colors.BOLD}  {title}{Colors.RESET}")
    print(f"{Colors.CYAN}{Colors.BOLD}{'=' * 70}{Colors.RESET}\n")

def print_success(message: str):
    """Imprime mensaje de éxito"""
    print(f"{Colors.GREEN}[PASSED]{Colors.RESET} {message}")

def print_error(message: str):
    """Imprime mensaje de error"""
    print(f"{Colors.RED}[FAILED]{Colors.RESET} {message}")

def print_info(message: str):
    """Imprime mensaje informativo"""
    print(f"{Colors.CYAN}[INFO]{Colors.RESET} {message}")

def print_warn(message: str):
    """Imprime mensaje de advertencia"""
    print(f"{Colors.YELLOW}[WARN]{Colors.RESET} {message}")

# ======================================================================
# TESTS: USERS
# ======================================================================

def test_users(supabase: Client):
    """Prueba operaciones CRUD de usuarios"""
    print_header("TESTING: USERS TABLE")
    
    # CREATE
    try:
        print_info("Creating test user...")
        user_data = {
            "email": f"test_{int(time.time())}@elitecouture.com",
            "password": "test123",
            "first_name": "Test",
            "last_name": "User",
            "address": "123 Test Street",
            "is_guest": False
        }
        
        result = supabase.table("users").insert(user_data).execute()
        created_user = result.data[0]
        user_uuid = created_user["uuid"]
        
        print_success(f"User created with UUID: {user_uuid}")
        test_results["users_create"] = True
    except Exception as e:
        print_error(f"Failed to create user: {str(e)}")
        test_results["users_create"] = False
        return None
    
    # READ
    try:
        print_info("Reading user by UUID...")
        result = supabase.table("users").select("*").eq("uuid", user_uuid).execute()
        
        if len(result.data) > 0:
            print_success(f"User found: {result.data[0]['email']}")
            test_results["users_read"] = True
        else:
            print_error("User not found")
            test_results["users_read"] = False
    except Exception as e:
        print_error(f"Failed to read user: {str(e)}")
        test_results["users_read"] = False
    
    # UPDATE
    try:
        print_info("Updating user address...")
        result = supabase.table("users").update({
            "address": "456 Updated Street"
        }).eq("uuid", user_uuid).execute()
        
        print_success("User address updated")
        test_results["users_update"] = True
    except Exception as e:
        print_error(f"Failed to update user: {str(e)}")
        test_results["users_update"] = False
    
    # DELETE
    try:
        print_info("Deleting test user...")
        supabase.table("users").delete().eq("uuid", user_uuid).execute()
        
        print_success("User deleted")
        test_results["users_delete"] = True
    except Exception as e:
        print_error(f"Failed to delete user: {str(e)}")
        test_results["users_delete"] = False
    
    return user_uuid

# ======================================================================
# TESTS: PRODUCTS
# ======================================================================

def test_products(supabase: Client):
    """Prueba operaciones CRUD de productos"""
    print_header("TESTING: PRODUCTS TABLE")
    
    # CREATE
    try:
        print_info("Creating test product...")
        product_data = {
            "name": "Test Product",
            "type": "Vestido",
            "gender": "Mujer",
            "description": "Product for testing",
            "price": 99.99,
            "stock": 10,
            "images": [
                "https://tjhhqwizpiywyrwjpgrg.supabase.co/storage/v1/object/public/product-images/product_01/17097806_76_D2.avif"
            ],
            "tags": ["Test", "Vestido", "Mujer"],
            "is_visible_to_guest": True
        }
        
        result = supabase.table("products").insert(product_data).execute()
        created_product = result.data[0]
        product_uuid = created_product["uuid"]
        
        print_success(f"Product created with UUID: {product_uuid}")
        print_info(f"  Price: ${created_product['price']}")
        print_info(f"  Stock: {created_product['stock']}")
        test_results["products_create"] = True
    except Exception as e:
        print_error(f"Failed to create product: {str(e)}")
        test_results["products_create"] = False
        return None
    
    # READ
    try:
        print_info("Reading product by UUID...")
        result = supabase.table("products").select("*").eq("uuid", product_uuid).execute()
        
        if len(result.data) > 0:
            product = result.data[0]
            print_success(f"Product found: {product['name']}")
            print_info(f"  Images count: {len(product['images'])}")
            print_info(f"  Tags count: {len(product['tags'])}")
            test_results["products_read"] = True
        else:
            print_error("Product not found")
            test_results["products_read"] = False
    except Exception as e:
        print_error(f"Failed to read product: {str(e)}")
        test_results["products_read"] = False
    
    # UPDATE
    try:
        print_info("Updating product stock...")
        result = supabase.table("products").update({
            "stock": 5,
            "price": 79.99
        }).eq("uuid", product_uuid).execute()
        
        print_success("Product stock and price updated")
        test_results["products_update"] = True
    except Exception as e:
        print_error(f"Failed to update product: {str(e)}")
        test_results["products_update"] = False
    
    # SEARCH
    try:
        print_info("Searching products by name...")
        result = supabase.table("products").select("*").ilike("name", "%Test%").execute()
        
        print_success(f"Found {len(result.data)} products matching 'Test'")
        test_results["products_search"] = True
    except Exception as e:
        print_error(f"Failed to search products: {str(e)}")
        test_results["products_search"] = False
    
    # DELETE
    try:
        print_info("Deleting test product...")
        supabase.table("products").delete().eq("uuid", product_uuid).execute()
        
        print_success("Product deleted")
        test_results["products_delete"] = True
    except Exception as e:
        print_error(f"Failed to delete product: {str(e)}")
        test_results["products_delete"] = False
    
    return product_uuid

# ======================================================================
# TESTS: FAVORITES
# ======================================================================

def test_favorites(supabase: Client, user_uuid: str, product_uuid: str):
    """Prueba operaciones CRUD de favoritos"""
    print_header("TESTING: FAVORITES TABLE")
    
    if not user_uuid or not product_uuid:
        print_warn("Skipping favorites tests (missing user or product)")
        test_results["favorites_create"] = False
        test_results["favorites_read"] = False
        test_results["favorites_delete"] = False
        return
    
    # CREATE
    try:
        print_info("Adding product to favorites...")
        favorite_data = {
            "user_uuid": user_uuid,
            "product_uuid": product_uuid
        }
        
        result = supabase.table("favorites").insert(favorite_data).execute()
        favorite_id = result.data[0]["id"]
        
        print_success(f"Favorite created with ID: {favorite_id}")
        test_results["favorites_create"] = True
    except Exception as e:
        print_error(f"Failed to create favorite: {str(e)}")
        test_results["favorites_create"] = False
        return
    
    # READ
    try:
        print_info("Reading user's favorites...")
        result = supabase.table("favorites").select("*").eq("user_uuid", user_uuid).execute()
        
        print_success(f"User has {len(result.data)} favorite(s)")
        test_results["favorites_read"] = True
    except Exception as e:
        print_error(f"Failed to read favorites: {str(e)}")
        test_results["favorites_read"] = False
    
    # DELETE
    try:
        print_info("Removing product from favorites...")
        supabase.table("favorites").delete().eq("user_uuid", user_uuid).eq("product_uuid", product_uuid).execute()
        
        print_success("Favorite removed")
        test_results["favorites_delete"] = True
    except Exception as e:
        print_error(f"Failed to delete favorite: {str(e)}")
        test_results["favorites_delete"] = False

# ======================================================================
# TESTS: CART ITEMS
# ======================================================================

def test_cart_items(supabase: Client, user_uuid: str, product_uuid: str):
    """Prueba operaciones CRUD de items del carrito"""
    print_header("TESTING: CART_ITEMS TABLE")
    
    if not user_uuid or not product_uuid:
        print_warn("Skipping cart items tests (missing user or product)")
        test_results["cart_create"] = False
        test_results["cart_read"] = False
        test_results["cart_update"] = False
        test_results["cart_delete"] = False
        return
    
    # CREATE
    try:
        print_info("Adding product to cart...")
        cart_data = {
            "user_uuid": user_uuid,
            "product_uuid": product_uuid,
            "quantity": 2
        }
        
        result = supabase.table("cart_items").insert(cart_data).execute()
        cart_item_id = result.data[0]["id"]
        
        print_success(f"Cart item created with ID: {cart_item_id}")
        print_info(f"  Quantity: {result.data[0]['quantity']}")
        test_results["cart_create"] = True
    except Exception as e:
        print_error(f"Failed to create cart item: {str(e)}")
        test_results["cart_create"] = False
        return
    
    # READ
    try:
        print_info("Reading user's cart...")
        result = supabase.table("cart_items").select("*").eq("user_uuid", user_uuid).execute()
        
        total_quantity = sum(item["quantity"] for item in result.data)
        print_success(f"User has {len(result.data)} item(s) in cart (total quantity: {total_quantity})")
        test_results["cart_read"] = True
    except Exception as e:
        print_error(f"Failed to read cart: {str(e)}")
        test_results["cart_read"] = False
    
    # UPDATE
    try:
        print_info("Updating cart item quantity...")
        result = supabase.table("cart_items").update({
            "quantity": 5
        }).eq("user_uuid", user_uuid).eq("product_uuid", product_uuid).execute()
        
        print_success("Cart item quantity updated to 5")
        test_results["cart_update"] = True
    except Exception as e:
        print_error(f"Failed to update cart item: {str(e)}")
        test_results["cart_update"] = False
    
    # DELETE
    try:
        print_info("Removing item from cart...")
        supabase.table("cart_items").delete().eq("user_uuid", user_uuid).eq("product_uuid", product_uuid).execute()
        
        print_success("Cart item removed")
        test_results["cart_delete"] = True
    except Exception as e:
        print_error(f"Failed to delete cart item: {str(e)}")
        test_results["cart_delete"] = False

# ======================================================================
# EJECUCIÓN PRINCIPAL
# ======================================================================

def main():
    print_header("ELITE COUTURE - DATABASE MIGRATION TEST")
    
    if not SUPABASE_URL or not SUPABASE_KEY:
        print_error("Missing SUPABASE_URL or SUPABASE_ANON_KEY in .env")
        return
    
    try:
        # Conectar
        print_info("Connecting to Supabase...")
        supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)
        print_success("Connection established")
        
        # Ejecutar tests
        user_uuid = test_users(supabase)
        product_uuid = test_products(supabase)
        
        # Re-crear usuario y producto para tests de relaciones
        print_info("\nRe-creating user and product for relationship tests...")
        user_data = {
            "email": f"test_{int(time.time())}@elitecouture.com",
            "password": "test123",
            "first_name": "Test",
            "last_name": "User",
            "is_guest": False
        }
        result = supabase.table("users").insert(user_data).execute()
        user_uuid = result.data[0]["uuid"]
        
        product_data = {
            "name": "Test Product",
            "price": 99.99,
            "stock": 10,
            "images": [],
            "tags": []
        }
        result = supabase.table("products").insert(product_data).execute()
        product_uuid = result.data[0]["uuid"]
        
        # Tests de relaciones
        test_favorites(supabase, user_uuid, product_uuid)
        test_cart_items(supabase, user_uuid, product_uuid)
        
        # Cleanup
        print_info("\nCleaning up test data...")
        supabase.table("users").delete().eq("uuid", user_uuid).execute()
        supabase.table("products").delete().eq("uuid", product_uuid).execute()
        print_success("Cleanup complete")
        
    except Exception as e:
        print_error(f"Connection error: {str(e)}")
        return
    
    # Resumen
    print_header("TEST SUMMARY")
    
    total_tests = len(test_results)
    passed_tests = sum(1 for result in test_results.values() if result)
    failed_tests = total_tests - passed_tests
    
    print(f"Tests ejecutados: {total_tests}")
    print(f"{Colors.GREEN}Exitosos: {passed_tests}{Colors.RESET}")
    print(f"{Colors.RED}Fallidos: {failed_tests}{Colors.RESET}\n")
    
    # Detalle por tabla
    print(f"{Colors.BOLD}USERS TABLE:{Colors.RESET}")
    for key in ["users_create", "users_read", "users_update", "users_delete"]:
        status = test_results.get(key, False)
        icon = f"{Colors.GREEN}[PASSED]{Colors.RESET}" if status else f"{Colors.RED}[FAILED]{Colors.RESET}"
        print(f"  {icon} {key.replace('users_', '').upper()}")
    
    print(f"\n{Colors.BOLD}PRODUCTS TABLE:{Colors.RESET}")
    for key in ["products_create", "products_read", "products_update", "products_search", "products_delete"]:
        status = test_results.get(key, False)
        icon = f"{Colors.GREEN}[PASSED]{Colors.RESET}" if status else f"{Colors.RED}[FAILED]{Colors.RESET}"
        print(f"  {icon} {key.replace('products_', '').upper()}")
    
    print(f"\n{Colors.BOLD}FAVORITES TABLE:{Colors.RESET}")
    for key in ["favorites_create", "favorites_read", "favorites_delete"]:
        status = test_results.get(key, False)
        icon = f"{Colors.GREEN}[PASSED]{Colors.RESET}" if status else f"{Colors.RED}[FAILED]{Colors.RESET}"
        print(f"  {icon} {key.replace('favorites_', '').upper()}")
    
    print(f"\n{Colors.BOLD}CART_ITEMS TABLE:{Colors.RESET}")
    for key in ["cart_create", "cart_read", "cart_update", "cart_delete"]:
        status = test_results.get(key, False)
        icon = f"{Colors.GREEN}[PASSED]{Colors.RESET}" if status else f"{Colors.RED}[FAILED]{Colors.RESET}"
        print(f"  {icon} {key.replace('cart_', '').upper()}")
    
    # Resultado final
    print("\n" + "=" * 70)
    if failed_tests == 0:
        print(f"{Colors.GREEN}{Colors.BOLD}RESULTADO: TODOS LOS TESTS PASARON{Colors.RESET}")
        print(f"{Colors.GREEN}La migración a Supabase está LISTA!{Colors.RESET}")
    else:
        print(f"{Colors.YELLOW}{Colors.BOLD}RESULTADO: {failed_tests} TEST(S) FALLARON{Colors.RESET}")
        print(f"{Colors.YELLOW}Revisa los errores arriba y ejecuta el SQL en Supabase Dashboard{Colors.RESET}")
    print("=" * 70)

if __name__ == "__main__":
    main()
