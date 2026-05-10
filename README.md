# CDT-RAM



Prototipo Android para la digitalización del Test del Reloj mediante stylus, orientado al registro, análisis y visualización de métricas de ejecución como apoyo al personal sanitario.



## Descripción



CDT-RAM es una aplicación Android desarrollada como parte de un Trabajo de Fin de Grado en Ingeniería Informática. El objetivo del proyecto es explorar la digitalización del Clock Drawing Test mediante dispositivos compatibles con stylus, permitiendo registrar información asociada a la ejecución de la prueba.



El sistema permite capturar datos relacionados con la ejecución del test, como la cantidad de trazos realizados, la presión media ejercida con el stylus, la velocidad media del trazo, las pausas detectadas y determinados eventos de entrada producidos durante la realización de la prueba.



Además, el prototipo incorpora gestión de pacientes, almacenamiento local de sesiones, consulta de historial, revisión de pruebas realizadas y comparación entre sesiones. Estos datos se plantean como métricas digitales de apoyo para su posterior análisis y visualización.

## 

## Estado del proyecto





Proyecto en desarrollo. Actualmente se encuentra implementada una primera versión funcional del prototipo, centrada en la captura del dibujo mediante stylus, el cálculo de métricas digitales, la persistencia local mediante Room y la gestión básica de pacientes y sesiones.



El prototipo permite guardar la imagen final del dibujo, registrar eventos de entrada asociados a la sesión y consultar posteriormente la información almacenada desde las pantallas de historial y revisión.



## Funcionalidades implementadas



\- Captura del dibujo del Test del Reloj mediante stylus.

\- Registro de métricas digitales durante la ejecución de la prueba.

\- Cálculo de trazos, presión media, velocidad media y pausas.

\- Registro de eventos de entrada asociados al dibujo.

\- Captura de eventos hover durante la interacción con el stylus.

\- Guardado y visualización de la imagen final del reloj.

\- Gestión de pacientes.

\- Creación de nuevos pacientes.

\- Edición de datos de pacientes.

\- Archivado de pacientes.

\- Visualización de tarjeta resumen del paciente.

\- Selección de paciente desde la pantalla de test.

\- Almacenamiento local de información mediante Room.

\- Consulta del historial de sesiones asociadas a un paciente.

\- Revisión individual de sesiones guardadas.

\- Comparación visual entre sesiones.

\- Visualización de pacientes mediante tarjetas compactas.

\- Exportación del listado de pacientes en formato CSV.

\- Navegación inferior para facilitar el acceso entre las principales secciones de la aplicación.

\- Cabecera común para mantener una estructura visual coherente entre pantallas.

\- Organización del código en pantallas y componentes reutilizables.

## 

## Métricas implementadas



Actualmente, el prototipo permite calcular y visualizar las siguientes métricas durante la ejecución del Test del Reloj:



\- \*\*Número de trazos:\*\* cantidad de segmentos independientes realizados por el usuario durante la prueba.

\- \*\*Presión media relativa:\*\* valor medio de presión registrado por el stylus durante el trazado.

\- \*\*Velocidad media de trazo:\*\* velocidad media del movimiento efectivo del stylus, expresada en mm/s.

\- \*\*Pausas durante la ejecución:\*\* número de interrupciones detectadas entre trazos durante la realización de la prueba.

\- \*\*Eventos de entrada:\*\* registros asociados a la interacción del stylus con la superficie de dibujo.

\- \*\*Eventos hover:\*\* eventos producidos cuando el stylus se aproxima a la pantalla sin realizar contacto directo.



La métrica de velocidad se calcula a partir del movimiento del stylus durante el trazado, diferenciándola del tiempo global de ejecución de la prueba. De esta forma, se obtiene una medida más representativa de la ejecución motora del dibujo.



La detección de pausas permite registrar interrupciones durante la ejecución del test, lo que puede aportar información adicional sobre el ritmo de realización de la prueba y los posibles momentos de planificación o detención entre trazos.



El registro de eventos de entrada y eventos hover permite conservar información adicional sobre el proceso de ejecución, no solo sobre el resultado final del dibujo.



Estas métricas tienen una finalidad académica y exploratoria, y se plantean como apoyo para el análisis posterior de la prueba, no como herramienta diagnóstica.

## 

## Tecnologías utilizadas



* Android Studio
* Kotlin
* Jetpack Compose
* MotionEvent API
* Git y GitHub
* Room
* SQLite
* CSV



## Aviso



Este prototipo no realiza diagnósticos clínicos. Su finalidad es académica y experimental, orientada al apoyo en la captura y análisis de métricas digitales del Test del Reloj.

