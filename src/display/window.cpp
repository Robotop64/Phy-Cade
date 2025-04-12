#include "raylib.h"

#include "window.hpp"
#include "config.hpp"
#include "logging.hpp"

void Window::create()
{
    SetTraceLogLevel(LOG_WARNING);
    if (Option::as<OptionExtended<bool>>(Config::instance().map().get("display.advanced-anti_aliasing").value())->value)
        SetConfigFlags(FLAG_MSAA_4X_HINT | FLAG_WINDOW_HIGHDPI); // significantly increases memory usage

    auto resolution_opt = Option::as<OptionChoice<std::string>>(Config::instance().map().get("display.general-resolution").value());
    auto resolution = [&]()
    {
        int delimiter = resolution_opt->value.find("x");
        int width = std::stoi(resolution_opt->value.substr(0, delimiter));
        int height = std::stoi(resolution_opt->value.substr(delimiter + 1));

        return std::array<int, 2>{width, height};
    }();

    InitWindow(resolution[0], resolution[1], "PacPhi");
    SetWindowState(FLAG_WINDOW_RESIZABLE);
    SetTargetFPS(Option::as<OptionExtended<int>>(Config::instance().map().get("display.general-fps").value())->value);

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