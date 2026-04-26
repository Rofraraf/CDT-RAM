# CDT-RAM

Prototipo Android para la digitalización del Test del Reloj mediante stylus, orientado al registro, análisis y visualización de métricas de ejecución como apoyo al personal sanitario.



## Descripción

CDT-RAM es una aplicación Android desarrollada como parte de un Trabajo de Fin de Grado en Ingeniería Informática. El objetivo del proyecto es explorar la digitalización del Clock Drawing Test mediante dispositivos compatibles con stylus, permitiendo registrar información asociada a la ejecución de la prueba.



El sistema permite capturar datos relacionados con la ejecución del test, como la cantidad de trazos realizados, la presión media ejercida con el stylus, la velocidad media del trazo y las pausas detectadas durante la realización de la prueba. Estos datos se plantean como métricas digitales de apoyo para su posterior análisis y visualización.



## Estado del proyecto

Proyecto en desarrollo. Actualmente se encuentra implementada una primera versión funcional del prototipo, centrada en la captura del dibujo mediante stylus y el cálculo inicial de métricas digitales asociadas a la ejecución del Test del Reloj.





## Métricas implementadas



Actualmente, el prototipo permite calcular y visualizar las siguientes métricas durante la ejecución del Test del Reloj:



\- \*\*Número de trazos:\*\* cantidad de segmentos independientes realizados por el usuario durante la prueba.

\- \*\*Presión media relativa:\*\* valor medio de presión registrado por el stylus durante el trazado.

\- \*\*Velocidad media de trazo:\*\* velocidad media del movimiento efectivo del stylus, expresada en mm/s.

\- \*\*Pausas durante la ejecución:\*\* número de interrupciones detectadas entre trazos durante la realización de la prueba.



La métrica de velocidad se calcula a partir del movimiento del stylus durante el trazado, diferenciándola del tiempo global de ejecución de la prueba. De esta forma, se obtiene una medida más representativa de la ejecución motora del dibujo.



La detección de pausas permite registrar interrupciones durante la ejecución del test, lo que puede aportar información adicional sobre el ritmo de realización de la prueba y los posibles momentos de planificación o detención entre trazos.



Estas métricas tienen una finalidad académica y exploratoria, y se plantean como apoyo para el análisis posterior de la prueba, no como herramienta diagnóstica.



## Tecnologías utilizadas

* Android Studio
* Kotlin
* Jetpack Compose
* MotionEvent API
* Git y GitHub



## Aviso

Este prototipo no realiza diagnósticos clínicos. Su finalidad es académica y experimental, orientada al apoyo en la captura y análisis de métricas digitales del Test del Reloj.

