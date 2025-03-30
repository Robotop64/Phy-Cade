#include "raylib.h"

#include "window.hpp"
#include "config.hpp"
#include "logging.hpp"

Window::Context Window::current = nullptr;

void Window::create()
{
    auto handle = Config::instance().get(Config::User, "Display.Basic.pref_resolution");
    int resolution[2] = {handle[0].value_or(0), handle[1].value_or(0)};

    SetTraceLogLevel(LOG_WARNING);
    if (Config::instance().get(Config::User, "Display.Advanced.anti_aliasing").value<bool>())
        SetConfigFlags(FLAG_MSAA_4X_HINT | FLAG_WINDOW_HIGHDPI);
    InitWindow(resolution[0], resolution[1], "PacPhi");
    SetWindowState(FLAG_WINDOW_RESIZABLE);
    SetTargetFPS(Config::instance().get(Config::User, "Display.Basic.target_fps").value_or(60));

    Log::msg("Window", "Created.");
}

void Window::destroy()
{
    CloseWindow();
    Log::msg("Window", "Destroyed.");
}

void Window::updateContext()
{
    while (Window::current != nullptr && !WindowShouldClose())
    {
        Log::msg("Window", "Updating Context.");
        Window::current();
    }
}

void Window::queueContext(Context context)
{
    Window::current = context;
    Log::msg("Window", "Context queued.");
}