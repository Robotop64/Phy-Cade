#include "window.hpp"
#include "menu.hpp"
#include "logging.hpp"

#include "raylib.h"

void Menu::GameMenu()
{
    Log::msg("Window", "Swap to Context: Game-Menu");

    bool close = false;
    while (!WindowShouldClose() && !close)
    {
        BeginDrawing();
        ClearBackground(RAYWHITE);
        DrawText("GAME MENU", 190, 200, 20, LIGHTGRAY);
        EndDrawing();

        if (IsKeyPressed(KEY_E))
        {
            close = true;
            Window::queueContext([]()
                                 { Menu::MainMenu(); });
        }
    }
}