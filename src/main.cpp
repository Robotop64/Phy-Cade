#include "raylib.h"

#include "config.hpp"
#include "window.hpp"
#include "menu.hpp"
#include "gui.hpp"

#include <iostream>

int main(void)
{
    // load config
    Config config = Config::instance();

    // create window
    Window::create();

    Window::queueContext(Menu::MainMenu);
    Window::updateContext();

    Window::destroy();

    return 0;
}