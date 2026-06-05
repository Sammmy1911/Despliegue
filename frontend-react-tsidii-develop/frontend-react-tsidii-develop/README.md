# frontend-react-tsidii

## Setup local

1. Entra a la app:
   - `cd bu_app`
2. Instala dependencias:
   - `npm install`
3. Ejecuta en desarrollo:
   - `npm run dev`

## Calidad de código y hooks de Git

Este repositorio tiene configuración de ESLint + Husky para el taller:

- `pre-commit`: ejecuta `npm run lint`
- `pre-push`: ejecuta `npm run build`

La configuración de hooks se hace automáticamente con `prepare` cuando corres `npm install` en `bu_app`.

> Nota: cada integrante debe ejecutar `npm install` al clonar el repositorio para que su entorno local configure los hooks.
