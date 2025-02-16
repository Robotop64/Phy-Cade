#include "window.hpp"
#include "menu.hpp"
#include "gui.hpp"
#include "logging.hpp"

#include "raylib.h"

#include <iostream>

void Menu::MainMenu()
{
    Log::msg("Window", "Swap to Context: Main-Menu");

    ClayMan handle = Gui::init();

    bool close = false;
    while (!WindowShouldClose() && !close)
    {
        BeginDrawing();
        ClearBackground(RAYWHITE);
        DrawText("MAIN MENU", 190, 200, 20, LIGHTGRAY);
        EndDrawing();

        if (IsKeyPressed(KEY_E))
        {
            close = true;
            Window::queueContext(nullptr);
            Log::msg("Window", "Swap to Context: None");
        }
        if (IsKeyPressed(KEY_G))
        {
            close = true;
            Window::queueContext([]()
                                 { Menu::GameMenu(); });
        }
    }
}