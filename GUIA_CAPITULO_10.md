# Capítulo 10 · Palette Widgets

Abre Cap_10 en Android Studio, sincroniza Gradle y ejecuta app en un emulador o dispositivo con Android 8.1 o posterior. El botón Atrás y el botón Volver al índice regresan a la lista de prácticas.

## Antes de ejecutar

1. Abre activity_main.xml para ver el contenedor de las prácticas y demo_image.xml para ver el ImageView que se carga en la app.
2. En Design, abre la Palette y arrastra un ImageView a un layout de prueba con un contenedor. En Attributes, revisa src, scaleType y contentDescription.
3. Cambia a Split o Code. Revisa cómo esos atributos se representan en XML. Compara el resultado con demo_image.xml. En MainActivity.kt, busca la función imageDemo para ver cómo Kotlin carga el XML y añade la interacción.
4. En MainActivity.kt, abre show para ver qué función corresponde a cada botón del índice.

## Pruebas en orden

| Sección | Paso | Resultado esperado |
| --- | --- | --- |
| 1. View | Pulsa dos veces Mostrar u ocultar. | El rectángulo desaparece y vuelve. |
| 2. ImageView | Cambia la escala, toca la imagen y pulsa Cargar imagen de internet. | La imagen cambia de encuadre y aparece un aviso al tocarla. Si hay conexión, se muestra la imagen remota. |
| 3. WebView | Lee el HTML incluido y carga la página de Android Developers. | El contenido se muestra dentro de la app. Si la conexión falla, aparece un aviso. |
| 4. VideoView | Reproduce los dos clips y toca el video. | Aparecen los controles de reproducción. |
| 5. CalendarView | Elige una fecha y luego cambia de mes. | El texto muestra el día, el mes y el año seleccionados. |
| 6. TextClock | Compara Lima y UTC. | Ambos relojes se actualizan con sus respectivas zonas horarias. |
| 7. ProgressBar indeterminado | Pulsa Simular carga. | Aparece el indicador y se oculta tras dos segundos. |
| 8. ProgressBar horizontal | Pulsa Iniciar progreso. | La barra y el porcentaje avanzan hasta 100 %. |
| 9. SeekBar continuo | Arrastra el control y vuelve a abrir la sección. | El porcentaje simulado cambia y se conserva. |
| 10. SeekBar discreto | Recorre las cinco marcas. | El texto indica el nivel del 1 al 5. |
| 11. RatingBar | Selecciona estrellas enteras y medias estrellas. | La calificación cambia en pasos de 0,5 y se conserva. |
| 12. SearchView | Escribe kotlin, borra la consulta y toca un resultado. | La lista se filtra tras una pausa breve, se restaura y confirma la selección. |
| 13. TextureView y SurfaceView | Autoriza la cámara y pulsa Reproducir en SurfaceView. | TextureView muestra la cámara y SurfaceView reproduce un clip por internet. |
| 14. Separadores | Observa el encabezado y las columnas. | Un separador horizontal divide el encabezado y uno vertical divide las columnas. |
| 15. Práctica integradora | Repite las pruebas de imagen, progreso, volumen, estrellas, calendario y búsqueda. | Los seis ejemplos funcionan juntos en una pantalla desplazable. |

La página remota y los videos requieren internet. Para la vista previa usa un dispositivo con cámara o configura una cámara en el emulador. El volumen de la práctica representa un porcentaje guardado por la app.

