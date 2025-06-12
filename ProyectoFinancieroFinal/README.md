#  TERMINAL FINANCIERO  
**DAM – Sergio Vargas Sánchez**

**Terminal Financiero** es una herramienta desarrollada para realizar consultas sobre una base de datos Oracle mediante una interfaz gráfica sencilla, eficaz e intuitiva.

La aplicación está diseñada para facilitar el trabajo tanto de usuarios individuales como de empresas, ofreciendo funcionalidades prácticas que mejoran la experiencia de uso respecto a las herramientas tradicionales.

---

##  Objetivo

Simplificar el acceso y la gestión de datos en bases de datos Oracle. Bajo mi experiencia, Oracle puede resultar una plataforma compleja y poco intuitiva para quienes no utilizan herramientas complementarias como Oracle SQL Developer. Por ello, este terminal proporciona una alternativa más accesible, que incluye funcionalidades adicionales que optimizan el trabajo diario.

---

##  Motivación

La motivación para desarrollar esta herramienta surgió durante mi estancia en **VIEWNEXT**, la empresa en la que realicé mis prácticas. A partir de una utilidad básica para consultas a Oracle, fui mejorando progresivamente la herramienta, incorporando nuevas funciones y mejorando la interfaz, con el objetivo de entregar una solución más completa y profesional.

---

##  Funcionalidades Principales

###  Gestión de usuarios

- Permite crear y gestionar diferentes usuarios (nombre, IP, contraseña, base de datos, puerto).  
- Validaciones de campos vacíos y formato de IP.  
- Guardado en `config.properties` (automático).  
- Posibilidad de cancelar o cambiar de usuario en cualquier momento.

###  Seguridad y encriptación

- Usuario y contraseña se guardan encriptados.  
- Se usa una clave interna (key) para cifrar/descifrar datos.

###  Carga automática del último usuario

- Se guarda el último usuario utilizado.  
- Se carga automáticamente al iniciar la aplicación.

###  Conexión eficiente a Oracle

- Se crea o reutiliza la conexión según disponibilidad.  
- Si está inactiva por 10 segundos, se cierra automáticamente.

###  Sistema de filtros para consultas

- Filtros combinables por Empresa, Centro, Terminal y Versión de aplicación.  
- Validaciones incluidas (tipo de dato, longitud, rangos de terminales).  
- Mensajes si no hay resultados con los filtros.

###  Múltiples pestañas de consulta

- Permite comparar varias consultas simultáneamente.  
- Cada pestaña conserva sus propios filtros y resultados.

### Indicador de resultados

- Muestra el número total de resultados al final de cada consulta.

###  Exportación de datos

- **Excel (.xlsx):** exporta datos con estructura.  
- **PDF:** exporta con fecha, hora y consulta realizada.  
- Ambas opciones se habilitan solo tras una consulta.

###  Registro de errores (logging)

- Errores registrados en `logger/`  
- Al cerrar, se genera un archivo con todos los errores.  
- Máximo 3 archivos (se elimina el más antiguo al superar el límite).

###  Salida controlada

- Botones dedicados para cerrar la app de forma segura.

### Validaciones y errores

- Verificación de la BBDD seleccionada.  
- Detección de errores de autenticación.  
- Comprobación de sintaxis de consultas.  
- Manejo de excepciones comunes con mensajes claros.

---

## Librerías Externas

- **Apache POI**: para leer/exportar Excel.  
- **Apache Commons**: utilidades IO y matemáticas.  
- **Apache PDFBox**: creación de documentos PDF.  
- **Oracle JDBC Driver**: conexión Java ↔ Oracle.  
- **SLF4J + Log4j**: sistema de logging.

---

## Instalación y Ejecución

1. Descarga o clona el proyecto.  
2. Accede a la carpeta del proyecto.  
3. Ejecuta el launcher (`.bat`, `.jar` o script).  
   Esto iniciará automáticamente la interfaz gráfica.

###  Requisitos Previos

- Java 1.8 o superior.  
- Base de datos Oracle 19c activa.  
- Asegurarse de que la tabla `T2223300` u otra equivalente exista.

---

## Guía de Uso

### 1. Configuración inicial

- Si es tu primera vez, pulsa “Nuevo Usuario”.  
- Rellena los campos:
  - Nombre del perfil.  
  - Usuario, contraseña, IP del servidor, puerto (ej. 1521), y nombre de la BBDD.  
- Se guarda en `config.properties` cifrado y se registra el último usuario usado.

### 2. Consulta de datos

La pantalla principal se divide en tres secciones:

####  Encabezado y navegación

- Muestra el usuario activo.  
- Menú superior:
  - Crear/modificar/cambiar usuario.  
  - Acceder a soporte/info.  
  - Salir.  
- Muestra el nombre de la sección actual.

####  Filtros y botones

- Filtrar por Empresa, Centro, Terminal o Versión.  
- Validaciones incluidas.  
- Botones disponibles:
  - **Consultar**  
  - **Exportar a Excel**  
  - **Exportar a PDF**  
  - **Nueva pestaña**  
  - **Salir**

####  Resultados

- Tabla inferior con resultados.  
- Encima: pestañas de consultas abiertas.  
- Debajo: número de resultados.

---

##  Otras funcionalidades

- Menú **"Ayuda"**:
  - **Soporte:** preguntas frecuentes + botón de regreso.  
  - **Acerca de:** versión y desarrolladores.

- Gestión de errores:
  - Carpeta `logger/` con hasta 3 archivos por sesión.  
  - Eliminación automática del más antiguo al superarse el límite.

---

##  Conclusión

**Terminal Financiero** es una solución funcional lista para entornos profesionales que trabajan con grandes volúmenes de datos en Oracle.

Aunque hay margen de mejora (eliminar usuarios, editar registros, más funcionalidades), ya ofrece:

- Consultas filtradas de forma sencilla.  
- Exportación de resultados en Excel y PDF.  
- Gestión clara de múltiples usuarios.

Gracias a su diseño intuitivo y funcionalidades prácticas, puede ser una herramienta clave para optimizar tareas repetitivas en departamentos técnicos, de soporte o administración.


