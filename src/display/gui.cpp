#include "gui.hpp"
#include "logging.hpp"

#include "raylib.h"
#include "clay_renderer_raylib.c"

ClayMan subinit()
{
    Log::msg("Gui", "Initializing.");

    Font fonts[1];
    fonts[0] = LoadFontEx("resources/fonts/Array-Regular.otf", 48, 0, 400);
    return ClayMan(GetScreenWidth(), GetScreenHeight(), Raylib_MeasureText, fonts);
}

ClayMan Gui::init()
{
    static ClayMan clayMan = subinit();

    return clayMan;
}

// void HandleClayErrors(Clay_ErrorData errorData)
// {
//     printf("%s", errorData.errorText.chars);
// }