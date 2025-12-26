# WishList

 

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-7f52ff?logo=kotlin&logoColor=white)

![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)

![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%7C%20Auth-FFCA28?logo=firebase&logoColor=black)

![Android](https://img.shields.io/badge/Android-API%2021+-3DDC84?logo=android&logoColor=white)

![License](https://img.shields.io/badge/License-Private-red)

 

Aplicacion Android nativa para la gestion colaborativa de listas de deseos. Permite a los usuarios crear, organizar y compartir productos deseados con sincronizacion en tiempo real.

 

## Funcionalidades

 

### Sistema de Autenticacion

- Login seguro con Firebase Authentication

- Sistema de roles (Usuario / Administrador)

- Persistencia de sesion

 

### Gestion de Productos

- **Crear** nuevos productos con nombre, descripcion, enlace y estado

- **Editar** productos existentes mediante dialogo modal

- **Eliminar** productos de la lista

- **Marcar favoritos** para acceso rapido

 

### Sistema de Estados

Los productos se organizan en tres estados con indicadores visuales:

 

| Estado | Descripcion |

|--------|-------------|

| Lo quiero | Productos prioritarios o deseados |

| En consideracion | Pendientes de decision |

| Otros | Items secundarios pero relevantes |

 

### Filtrado y Busqueda

- Busqueda por nombre o descripcion

- Filtrado por estado del producto

- Vista exclusiva de favoritos

- Ordenamiento por fecha (mas recientes primero)

 

### Sincronizacion en Tiempo Real

- Actualizacion automatica via Firestore SnapshotListener

- Notificaciones cuando se agregan nuevos productos

- Datos compartidos entre multiples usuarios

 

### Integracion Externa

- Enlaces clicables que abren el navegador

- Notificaciones por email al crear productos (servicio externo)

 

## Stack Tecnologico

 

### Frontend

- **Kotlin** - Lenguaje de programacion

- **Jetpack Compose** - Framework de UI declarativo

- **Material Design 3** - Sistema de diseno

- **Manrope Font** - Tipografia personalizada

 

### Backend

- **Firebase Authentication** - Autenticacion de usuarios

- **Cloud Firestore** - Base de datos NoSQL en tiempo real

 

### Networking

- **Retrofit 2.11** - Cliente HTTP para APIs REST

- **OkHttp3** - Interceptor para logging de requests

- **Gson** - Serializacion JSON

 

## Arquitectura

 

```

app/

├── model/

│   ├── Product.kt              # Modelo de datos

│   └── ProductStatusExt.kt     # Extensiones de estado

├── ui/

│   ├── components/

│   │   ├── ProductCard.kt      # Tarjeta reutilizable

│   │   └── StatusDropdown.kt   # Selector de estados

│   ├── screens/

│   │   ├── LoginScreen.kt      # Pantalla de autenticacion

│   │   ├── MainScreen.kt       # Vista de usuario

│   │   └── AdminScreen.kt      # Panel de administracion

│   └── theme/

│       ├── Theme.kt            # Temas claro/oscuro

│       └── Type.kt             # Tipografia

└── MainActivity.kt             # Punto de entrada y navegacion

```

 

### Patrones Implementados

- **Composable Pattern** - Componentes UI reutilizables

- **State Hoisting** - Elevacion de estado para manejo centralizado

- **Real-time Listeners** - Sincronizacion con Firestore

- **Extension Functions** - Funcionalidad extendida para modelos

 

## Modelo de Datos

 

```kotlin

data class Product(

    val id: String,

    val name: String,           // max 200 caracteres

    val link: String,           // max 1000 caracteres

    val description: String,    // max 1000 caracteres

    val status: ProductStatus,

    val favorite: Boolean,

    val dateAchieved: Timestamp

)

```

 

## Requisitos

 

- Android Studio Hedgehog o superior

- JDK 11+

- Android SDK API 21 (minimo) - API 35 (target)

- Cuenta de Firebase con proyecto configurado

 

## Configuracion

 

1. Clonar el repositorio

```bash

git clone https://github.com/iviapps/WishList_v1.git

```

 

2. Configurar Firebase

   - Crear proyecto en [Firebase Console](https://console.firebase.google.com)

   - Habilitar Authentication (Email/Password)

   - Crear base de datos Firestore

   - Descargar `google-services.json` y colocarlo en `app/`

 

3. Configurar servicio de email (opcional)

   - Crear directorio `app/src/main/java/com/iviapps/wishlistbyivi/services/`

   - Implementar las clases `EmailClient`, `EmailData`, `EmailUser`

 

4. Compilar y ejecutar

```bash

./gradlew assembleDebug

```

 

## Capturas de Pantalla

 

| Login | Lista de Productos | Panel Admin |

|-------|-------------------|-------------|

| Autenticacion segura | Vista con filtros | CRUD completo |

 

## Version Web

 

Disponible en: [wish-list-v1.vercel.app](https://wish-list-v1.vercel.app/)

 

## Roadmap

 

- [ ] Soporte para imagenes de productos

- [ ] Notificaciones push

- [ ] Compartir listas via link

- [ ] Modo offline con sincronizacion

- [ ] Tests unitarios e instrumentados

 

## Contacto

 

**Iveth Barrezueta**

 

[![LinkedIn](https://img.shields.io/badge/LinkedIn-ivethbarrezueta-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ivethbarrezueta)

[![Email](https://img.shields.io/badge/Email-iviapps.content%40gmail.com-EA4335?logo=gmail&logoColor=white)](mailto:iviapps.content@gmail.com)

 

---

 

Desarrollado con Kotlin y Jetpack Compose
