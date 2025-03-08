#pragma once

#include "clayman.hpp"
#include "clay_renderer_raylib.h"

#include <functional>

namespace Gui
{
    struct Handle
    {
        ClayMan *clayMan;
        Font fonts[1];
        Clay_RenderCommandArray commands;
    };

    Handle init();

    void updateMouse(Handle &handle);

    void draw(Handle &handle);

    bool updatedInput();
    void clearInput();
};