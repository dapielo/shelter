# SHELTER

Aplicacion web que funciona como punto de encuentro entre personas que quieren adoptar a un animal que buscar hogar, y voluntarios que acogen animales abandonados en sus casas hasta que encuentren una nueva familia.

---

## Estado del proyecto

El proyecto se encuentra en fase de desarrollo, y aunque se puede ejectuar en su estado actual, hay funcionalidades completas sin implementar con lo cual hay enlaces completos que no funcionan poruqe no están implementados aún. Hay funcionalidades o aspectos visuales que pueden cambiar en la version definitiva.

## Arquitectura

El proyecto esta construido sobre tes contenedores independientes conenctados a traves de docker compose:

- Frontend con Typescript y Angular sobre un contenedor de Nginx
- Backend con Java y Spring Boot sobre un contenedor de Temurin
- Base de datos con Mysql sobre un contenedor de la imagen oficial de MySQL

## Dependencias 

Para poder ejecutar el proeycto de forma local se necesita tener instalado previamente:

- git (no es necesarea cuenta de GitHub)
- docker engine
- docker compose

### Instalacion en Windows

Para docker-engine y compose hay dos opciones, instalarlo en Windows Subsistem for Linux (WSL) en una maquina de linux (ver más abajo), o a traves del instalador de Docker Desktop, instrucciones de instalacion en la web oficial de Docker Desktop:

[guia de instalacion en windows](https://docs.docker.com/desktop/setup/install/windows-install/)

Para git tenemos el instalador oficial de git, instrucciones en la web oficial de git:

[guia de instalacion de git en windows](https://git-scm.com/install/windows)

### Instalacion en Linux

Para docker-engine y docker compose, se puede usar el repositorio oficial de docker, guia de isntalacion a continuacion:

[guia de instalacion de docker en linux](https://docs.docker.com/engine/install/)

Muchas distribuciones populares tienen un paquete de docker en los repositorios oficiales, pongo dos ejemplos de los mas usados:

#### Debian

```bash
sudo apt install docker.io docker-compose
```

#### Ubuntu y derivadas

```bash
sudo apt install docker.io docker-compose-v2
```

## Ejecucion

### Windows

```powershell
git clone https://github.com/dapielo/shelter.git
cd shelter
docker compose up -d
```

Para tener la ejecucion, se puede hacer a mano desde la interfaz de docker desktop, o desde la misma terminal con 

```powershell
docker compose down 
```

### Linux

```bash
git clone https://github.com/dapielo/shelter.git
cd shelter
docker compose up -d
```

Para detener la ejecucion desde la misma terminal:

```bash
docker compose down
```

O desde cualquier otra terminal si no recuerdas la ruta com 

```bash
docker compose -p shelter down
```