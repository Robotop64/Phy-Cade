cmake_minimum_required(VERSION 3.31.0)
project(Test VERSION 0.1.0 LANGUAGES C CXX)

option(PRODUCTION_BUILD "Make this a production build" OFF)
set(OS "Windows")

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

file(GLOB_RECURSE MY_SOURCES CONFIGURE_DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp")
file(GLOB_RECURSE MY_HEADERS CONFIGURE_DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/src/*.h")

# Third-party libraries
#--------------------------------
# RAYLIB
set(RAYLIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/raylib")
include_directories(${RAYLIB_DIR})
if (OS STREQUAL "Windows")
    set(RAYLIB_DLL "${RAYLIB_DIR}/raylib.dll")
elseif (OS STREQUAL "Linux")
    set(RAYLIB_DLL "${RAYLIB_DIR}/libraylib.so")
endif()
#--------------------------------

# set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/out/bin)
add_executable("${CMAKE_PROJECT_NAME}" src/main.cpp ${MY_SOURCES})

target_link_libraries(${CMAKE_PROJECT_NAME} PRIVATE ${RAYLIB_DLL})

# Install
install(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/resources
        DESTINATION ${CMAKE_CURRENT_SOURCE_DIR}/out/)
install(FILES ${CMAKE_CURRENT_SOURCE_DIR}/build/${CMAKE_PROJECT_NAME}.exe
        DESTINATION ${CMAKE_CURRENT_SOURCE_DIR}/out/bin)
file(GLOB_RECURSE MY_DLLS CONFIGURE_DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/lib/*.dll")
install(FILES ${MY_DLLS}
        DESTINATION ${CMAKE_CURRENT_SOURCE_DIR}/out/lib)
