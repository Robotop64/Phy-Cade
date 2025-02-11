#include "raylib.h"

#include "window.hpp"
#include "config.hpp"

#include <iostream>
#include <tuple>


void Window::create()
{
    // std::cout << "Creating window\n";

    auto handle = Config::instance().get(Config::User, "Display.Basic.pref_resolution");
    int resolution[2] = {handle[0].value_or(0), handle[1].value_or(0)};

    InitWindow(resolution[0], resolution[1], "PacPhi"); 
    SetTargetFPS(Config::instance().get(Config::User, "Display.Basic.target_fps").value_or(60));
    
    std::cout << "Window created\n";
}

void Window::destroy()
{
    // CloseWindow();
    std::cout << "Window destroyed\n";
}