cmake_minimum_required(VERSION 3.31.0)
project(PacPhi VERSION 0.1.0 LANGUAGES C CXX)

option(PRODUCTION_BUILD "Make this a production build" OFF)
set(OS "Windows")

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

file(GLOB_RECURSE MY_SOURCES CONFIGURE_DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/src")
file(GLOB HEADER_DIRS LIST_DIRECTORIES true "src/*")
foreach(HEADER_DIR ${HEADER_DIRS})
    if(IS_DIRECTORY ${HEADER_DIR})
        message(STATUS "Included subdirectory ${HEADER_DIR}")
        include_directories(${HEADER_DIR})
    endif()
endforeach()


# Libraries
#===============================================================================
set(DLLs "")
#-------------------------------------------------------------------------------
# RAYLIB
set(RAYLIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/raylib")
include_directories(${RAYLIB_DIR}) # include all headers in /lib/raylib
if (WIN32)
    set(RAYLIB_LIB "${RAYLIB_DIR}/raylib.dll")
elseif (UNIX)
    set(RAYLIB_LIB "${RAYLIB_DIR}/libraylib.so")
endif()
list(APPEND DLLs ${RAYLIB_LIB})
#-------------------------------------------------------------------------------
# Toml++
#-------------------------------------------------------------------------------
set(TOMLPP_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/tomlplusplus")
include_directories(${TOMLPP_DIR})
#--------------------------------------------------------------------------------
# Json
#-------------------------------------------------------------------------------
set(JSON_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/json")
include_directories(${JSON_DIR})
#-------------------------------------------------------------------------------
# Clay
#-------------------------------------------------------------------------------
set(CLAY_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/clay")
include_directories(${CLAY_DIR})
list(APPEND MY_SOURCES "${CLAY_DIR}/clayman.cpp")
list(APPEND MY_SOURCES "${CLAY_DIR}/clay_renderer_raylib.cpp")
#===============================================================================

# set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/out/bin)
add_executable("${CMAKE_PROJECT_NAME}" src/main.cpp ${MY_SOURCES})

# Linking
#===============================================================================
foreach(DLL ${DLLs})
    target_link_libraries(${CMAKE_PROJECT_NAME} ${DLL})
    message(STATUS "Linked ${DLL}")
endforeach()

# Find DLLs in /lib instead of /bin
if (WIN32)
    install(CODE "
        file(WRITE ${CMAKE_CURRENT_SOURCE_DIR}/out/start.bat
            \"SET PATH=%~dp0lib;%PATH%\n\"
            \"start %~dp0bin\\\\${CMAKE_PROJECT_NAME}.exe\n\"
        )
    ")
endif()
# TODO Linux
#===============================================================================

# Install
#===============================================================================
# install(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/resources
#     DESTINATION ${CMAKE_CURRENT_SOURCE_DIR}/out/)
install(FILES ${CMAKE_CURRENT_SOURCE_DIR}/build/${CMAKE_PROJECT_NAME}.exe
    DESTINATION ${CMAKE_CURRENT_SOURCE_DIR}/out/bin)
# Only install linked DLLs
foreach(DLL ${DLLs})
    install(FILES ${DLL}
        DESTINATION ${CMAKE_CURRENT_SOURCE_DIR}/out/lib)
endforeach()
# Create user data folder
# install(CODE "
#     file(MAKE_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/out/userdata)
# ")
#===============================================================================
