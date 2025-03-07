#include "gui.hpp"
#include "logging.hpp"

Gui::Handle subinit()
{
    Font fonts[1];
    fonts[0] = LoadFontEx("resources/fonts/Roboto-Regular.ttf", 48, 0, 400);
    SetTextureFilter(fonts[0].texture, TEXTURE_FILTER_BILINEAR);
    ClayMan clayMan = ClayMan(GetScreenWidth(), GetScreenHeight(), Raylib_MeasureText, fonts);
    Gui::Handle handle = {&clayMan, {fonts[0]}};

    return handle;
}

Gui::Handle Gui::init()
{
    Log::msg("Gui", "Initializing.");

    static Gui::Handle handle = subinit();

    Log::msg("Gui", "Initialized.");

    return handle;
}

void Gui::updateMouse(Handle &handle)
{
    ClayMan *clayMan = handle.clayMan;

    Vector2 mousePosition = GetMousePosition();
    Vector2 scrollDelta = GetMouseWheelMoveV();

    clayMan->updateClayState(
        GetScreenWidth(),
        GetScreenHeight(),
        mousePosition.x,
        mousePosition.y,
        scrollDelta.x,
        scrollDelta.y,
        GetFrameTime(),
        IsMouseButtonDown(MOUSE_BUTTON_LEFT));
}

void Gui::draw(Handle &handle)
{
    Clay_Raylib_Render(handle.commands, handle.fonts);
};
