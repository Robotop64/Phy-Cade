#include "gui.hpp"
#include "logging.hpp"

Gui::Handle subinit()
{
    Font fonts[1];
    fonts[0] = LoadFontEx("resources/fonts/Array-Regular.otf", 48, 0, 400);
    SetTextureFilter(fonts[0].texture, TEXTURE_FILTER_BILINEAR);
    Gui::Handle handle = {ClayMan(GetScreenWidth(), GetScreenHeight(), Raylib_MeasureText, fonts), {fonts[0]}};

    return handle;
}

Gui::Handle Gui::init()
{
    Log::msg("Gui", "Initializing.");

    static Gui::Handle handle = subinit();

    Log::msg("Gui", "Initialized.");

    return handle;
}