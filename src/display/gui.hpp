#pragma once

#include "clayman.hpp"
#include "clay_renderer_raylib.h"

// #include <array>

namespace Gui
{
    struct Handle
    {
        ClayMan clayMan;
        Font fonts[1];
        // std::array<Font, 1> fonts;
    };

    Handle init();
};