#include "raylib.h"

#include "config.hpp"
#include "window.hpp"

#include <iostream>

namespace win {
    #include "windows.h"
}


int main(void)
{
    // load config
    Config config = Config::instance();

    // create window
    Window::create();

    //sleep
    win::Sleep(2000);

    Window::destroy();

    return 0;
}