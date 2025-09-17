# Frontend - Sistema de Gestión de Estaciones de Carga

Frontend React para el sistema de gestión de estaciones de carga de vehículos eléctricos.

## Características

- ✅ Lista completa de estaciones de carga
- ✅ Crear nuevas estaciones
- ✅ Editar estaciones existentes
- ✅ Eliminar estaciones
- ✅ Búsqueda de estaciones cercanas por coordenadas
- ✅ Estadísticas del sistema
- ✅ Interfaz responsive y moderna
- ✅ Validación de formularios
- ✅ Manejo de errores

## Tecnologías Utilizadas

- React 18
- Axios para consumo de APIs
- CSS3 con diseño responsive
- Geolocalización del navegador

## Instalación y Configuración

### Prerrequisitos

- Node.js 16+ 
- npm o yarn
- Backend Spring Boot ejecutándose en puerto 8080

### Pasos de Instalación

1. **Instalar dependencias:**
   ```bash
   cd frontend
   npm install
   ```

2. **Configurar proxy (ya configurado en package.json):**
   El frontend está configurado para hacer requests al backend en `http://localhost:8080`

3. **Ejecutar en modo desarrollo:**
   ```bash
   npm start
   ```

4. **Abrir en el navegador:**
   La aplicación estará disponible en `http://localhost:3000`

## Estructura del Proyecto

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── StationList.js      # Lista y gestión de estaciones
│   │   ├── StationForm.js      # Formulario crear/editar
│   │   ├── SearchStations.js   # Búsqueda por ubicación
│   │   └── Statistics.js       # Estadísticas del sistema
│   ├── App.js                  # Componente principal
│   ├── App.css                 # Estilos principales
│   └── index.js               # Punto de entrada
└── package.json
```

## Endpoints del Backend Utilizados

- `GET /api/charging-stations` - Obtener todas las estaciones
- `POST /api/charging-stations` - Crear nueva estación
- `PUT /api/charging-stations/{id}` - Actualizar estación
- `DELETE /api/charging-stations/{id}` - Eliminar estación
- `GET /api/charging-stations/search` - Buscar estaciones cercanas

## Funcionalidades

### Gestión de Estaciones
- Ver lista completa de estaciones
- Crear nuevas estaciones con validación
- Editar estaciones existentes
- Eliminar estaciones con confirmación

### Búsqueda Avanzada
- Buscar estaciones por coordenadas (latitud/longitud)
- Especificar radio de búsqueda (1-100 km)
- Usar geolocalización del navegador
- Resultados ordenados por distancia

### Estadísticas
- Total de estaciones
- Estaciones disponibles vs en uso
- Distribución por tipo de cargador
- Puntos de carga totales y promedio
- Gráficos visuales de distribución

## Uso

1. **Lista de Estaciones:** Pestaña principal para ver y gestionar todas las estaciones
2. **Nueva Estación:** Formulario para crear nuevas estaciones con validación
3. **Buscar Estaciones:** Buscar estaciones cercanas a una ubicación específica
4. **Estadísticas:** Ver métricas y análisis del sistema

## Configuración de Desarrollo

### Variables de Entorno
Crear archivo `.env` en la carpeta frontend:

```env
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_APP_NAME=Sistema de Estaciones de Carga
```

### Scripts Disponibles

- `npm start` - Ejecutar en modo desarrollo
- `npm build` - Construir para producción
- `npm test` - Ejecutar tests
- `npm eject` - Ejectar configuración (irreversible)

## Troubleshooting

### Error de CORS
Asegurarse que el backend tenga configurado CORS para permitir requests desde `http://localhost:3000`

### Backend no responde
Verificar que el backend Spring Boot esté ejecutándose en puerto 8080

### Problemas de Geolocalización
La funcionalidad de "Usar Mi Ubicación" requiere HTTPS en producción

## Contribución

1. Fork del proyecto
2. Crear rama feature (`git checkout -b feature/AmazingFeature`)
3. Commit de cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT.