# Referencia para integrar Hobbify

El prototipo React/Vite concentra la carga y los cambios de datos en `AppDataContext` y hoy los persiste en `localStorage`. Cuando comience la integración, ese contexto puede encargarse de llamar a la API y adaptar sus respuestas a los campos que usan las pantallas. El [contrato OpenAPI](../openapi.yaml) describe los endpoints disponibles.

## Cuentas y perfil empresarial

- Usa la misma URL de host en ambos servicios durante el desarrollo. El origen permitido por defecto es `http://localhost:5173` y la API está en `http://localhost:8080/api/v1`.
- La cuenta de demostración y el modo empresa del prototipo se derivan de datos locales y de la URL. Al integrar, usa `GET /auth/me` para obtener la identidad y el tipo de cuenta. Las rutas privadas requieren sesión con cookie y las escrituras requieren CSRF. Envía `credentials: 'include'` y renueva el token después del login o logout.
- `GET /empresas/me` entrega el perfil propio. `PUT /empresas/me/presentacion` recibe los tres campos del formulario «Editar página» y conserva los datos de contacto. La respuesta contiene el perfil actualizado con el ID de la empresa.
- `GET /empresas/{id}` devuelve el perfil y sus publicaciones visibles. El prototipo usa el nombre como clave en `/organizadores/:name` y en los seguimientos. El futuro adaptador debe usar el ID empresarial para relacionar publicaciones, perfiles y seguimientos, y usar el nombre solo para mostrarlo.

| Campo del prototipo | Campo de presentación en la API |
| --- | --- |
| `profile.name` | `nombreComercial` |
| `profile.description` | `descripcion` |
| `profile.color` | `colorPrincipal` en hexadecimal, con `#522bb8` como valor de lectura cuando la empresa carece de color guardado |

## Próximos tickets según las pantallas del prototipo

| Pantalla o acción | Correspondencia para el backend |
| --- | --- |
| Publicar o editar un plan | El formulario usa `title`, `description`, `type`, `category`, `date`, `place`, `capacity` y `price`. Mapear a los datos comunes de la publicación y al detalle de su tipo. El servidor obtiene el organizador de la sesión. |
| Tipos y fechas | El backlog y V1 reconocen lugares, talleres y charlas. El prototipo también muestra `Evento` y `Experiencia`, y su formulario deja fuera `Lugar`. Acordar la clasificación antes de exponer esos valores. El formulario tiene una fecha de calendario y V1 requiere inicio con hora y zona para talleres y charlas. |
| Descubrir y filtrar | La ruta usa `q`, `cat`, `type` y `price`. El prototipo selecciona una categoría por plan. V1 admite varias etiquetas y el selector puede enviar una sola por ID mientras la pantalla siga con selección única. |
| Ficha y cupos | La ficha necesita el ID de la publicación, el ID de la empresa y el cupo disponible. Usar los conteos del servidor al integrar, para evitar sumar dos veces las inscripciones cuando el contexto cargue los datos reales. |
| Inscribirse y Mis entradas | El formulario de demostración pide nombre y correo. La inscripción real se asocia a la cuenta autenticada y el servidor comprueba apertura, duplicados y cupos. La pantalla puede mostrar el título, la fecha o el horario y el estado devuelto. |
| Inscritos y Excel | La tabla muestra nombre, correo y estado. La API debe agregar fecha de inscripción y aplicar el alcance empresarial previsto en el backlog. La descarga debe incluir todos los registros que correspondan al alcance elegido. |
| Favoritos, seguidos, intereses y métricas | El contexto usa IDs para favoritos, nombres de organizaciones para seguidos y etiquetas visibles para intereses. Usar IDs persistentes para las tres relaciones. El servidor calcula las métricas y la unicidad de las visitas. |
| Precios | El prototipo muestra precios en Bs y simula reservas. El flujo de cobro corresponde a la ampliación y se integrará con la pasarela de terceros. |
