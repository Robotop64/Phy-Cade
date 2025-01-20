# Installation

## Requisites

- Windows:  
  Using [MSYS2](https://www.msys2.org/), install

  - GCC with: `pacman -S mingw-w64-x86_64-gcc`
  - CMake with: `pacman -S mingw-w64-x86_64-cmake`
  - Gdb with: `pacman -S mingw-w64-x86_64-gdb`

  in the MSYS2 terminal.

  Adjust the Path environment variable to include the following paths:

  - `???\msys64\mingw64\bin`
  - `???\msys64\ucrt64\bin`

  Check using `??? --version` if `gcc`, `g++`, `cmake`, and `gdb` are available.

- VsCode:
  Install the following extensions:

  - C/C++
  - CMake Tools (1.20.32+)
  - CMake
  - Task Runner (Recommended)

- Source:  
  Clone the repository using `git clone ???`.

## Build

- Run once from the command palette:
  `CMake: Select Kit`.  
  Command may only be available in a Pre-Release version of the CMake Tools extension.
- Task Runner: Run
  1. `Configure`: To generate the build files
  2. `Install`: To build the project in the `out` directory

## Run

- Task Runner: Run
  1. `Install` if not already done
  2. `Launch`
