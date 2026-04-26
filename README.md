# CDT-RAM

Prototipo Android para la digitalización del Test del Reloj mediante stylus, orientado al registro, análisis y visualización de métricas de ejecución como apoyo al personal sanitario.

## Descripción

CDT-RAM es una aplicación Android desarrollada como parte de un Trabajo de Fin de Grado en Ingeniería Informática. El objetivo del proyecto es explorar la digitalización del Clock Drawing Test mediante dispositivos compatibles con stylus, permitiendo registrar información asociada a la ejecución de la prueba.

El sistema permite capturar datos relacionados con el trazado realizado por el usuario, como la cantidad de trazos, la presión ejercida y los tiempos de ejecución. Estos datos se plantean como métricas digitales de apoyo para su posterior análisis y visualización.

## Estado del proyecto



\## Métricas implementadas



Actualmente, el prototipo permite calcular y visualizar las siguientes métricas durante la ejecución del Test del Reloj:



\- \*\*Número de trazos:\*\* cantidad de segmentos independientes realizados por el usuario durante la prueba.

\- \*\*Presión media relativa:\*\* valor medio de presión registrado por el stylus durante el trazado.

\- \*\*Velocidad media de trazo:\*\* velocidad media del movimiento efectivo del stylus, expresada en mm/s.



La métrica de velocidad se calcula a partir del movimiento del stylus durante el trazado, diferenciándola del tiempo global de ejecución de la prueba. De esta forma, se obtiene una medida más representativa de la ejecución motora del dibujo.



Estas métricas tienen una finalidad académica y exploratoria, y se plantean como apoyo para el análisis posterior de la prueba, no como herramienta diagnóstica.





## Tecnologías utilizadas

* Android Studio
* Kotlin
* Jetpack Compose
* MotionEvent API
* Git y GitHub

## Aviso

Este prototipo no realiza diagnósticos clínicos. Su finalidad es académica y experimental, orientada al apoyo en la captura y análisis de métricas digitales del Test del Reloj.

