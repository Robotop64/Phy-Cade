#include "window.hpp"
#include "menu.hpp"

#include "raylib.h"

#include <iostream>

void Menu::GameMenu()
{
    bool close = false;
    while (!WindowShouldClose() && !close)
    {
        BeginDrawing();
        ClearBackground(RAYWHITE);
        DrawText("GAME MENU", 190, 200, 20, LIGHTGRAY);
        EndDrawing();

        if (IsKeyPressed(KEY_E))
        {
            std::cout << "Game menu\n";
            close = true;
            Window::queueContext([]()
                                 { Menu::MainMenu(); });
        }
    }
}