#pragma once

#include "clayman.hpp"
#include "clay_renderer_raylib.h"

#include <functional>

namespace Gui
{
    struct Handle
    {
        ClayMan* clayMan;
        Font fonts[1];
        // std::array<Font, 1> fonts;
    };

    Handle init();

    void updateMouse(Handle& handle);

    void draw(Handle& handle,std::function<void()> layout);
};