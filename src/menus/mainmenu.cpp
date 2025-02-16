#include "window.hpp"
#include "menu.hpp"

#include "raylib.h"

#include <iostream>

void Menu::MainMenu()
{
    bool close = false;
    while (!WindowShouldClose() && !close)
    {
        BeginDrawing();
        ClearBackground(RAYWHITE);
        DrawText("MAIN MENU", 190, 200, 20, LIGHTGRAY);
        EndDrawing();

        if (IsKeyPressed(KEY_E))
        {
            std::cout << "Exiting Main\n";
            close = true;
            Window::queueContext(nullptr);
        }
        if (IsKeyPressed(KEY_G))
        {
            std::cout << "Main menu\n";
            close = true;
            Window::queueContext([]()
                                 { Menu::GameMenu(); });
        }
    }
}