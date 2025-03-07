#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"
#include "logging.hpp"
#include "profiler.hpp"
#include "config.hpp"

#include <string>

struct State
{
    bool close = false;
    bool gui_lock = false;
    bool rebuild_layout = false;
};
static State state;

void calcLayout(Gui::Handle &handle);
void processInput();

void Scene::MainMenu()
{
    Log::msg("Window", "Swap to Context: Main-Menu");

    state = State{};

    Gui::Handle handle = Gui::init();
    calcLayout(handle);

    Log::msg("MainMenu", "Starting Loop.");

    while (!WindowShouldClose() && !state.close)
    {
        processInput();

        if (state.rebuild_layout)
        {
            Gui::updateMouse(handle);
            calcLayout(handle);
            state.rebuild_layout = false;
            // Log::msg("MainMenu", "Rebuilt Layout.");
        }

        BeginDrawing();
        ClearBackground(BLACK);

        Gui::draw(handle);

        EndDrawing();
    }

    Log::msg("MainMenu", "Ending Loop.");

    Log::msg("MainMenu", "Leaving Current Context.");
}

void processInput()
{
    if (IsKeyPressed(KEY_ESCAPE))
    {
        state.close = true;
        Window::queueContext(nullptr);
        Log::msg("Window", "Swap to Context: None");
    }
    if (IsKeyPressed(KEY_G))
    {
        state.close = true;
        Window::queueContext([]()
                             { Scene::Game(); });
    }

    if (IsWindowResized() || GetKeyPressed() != 0 || IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonPressed(MOUSE_BUTTON_RIGHT))
    {
        // Clear input buffer
        while (GetKeyPressed() != 0)
        {
        }
        state.rebuild_layout = true;
    }
}

const Clay_Color gray_0 = {35, 35, 35, 255};
const Clay_Color gray_1 = {70, 70, 70, 255};
const Clay_Color gray_2 = {105, 105, 105, 255};
const Clay_Color gray_3 = {140, 140, 140, 255};
const Clay_Color gray_4 = {175, 175, 175, 255};
const Clay_Color gray_5 = {210, 210, 210, 255};
const Clay_Color gray_6 = {245, 245, 245, 255};

const Clay_TextElementConfig infoText = {
    // Configure text
    .textColor = {255, 255, 255, 255},
    .fontId = 0,
    .fontSize = 16,
};

void calcLayout(Gui::Handle &handle)
{
    ClayMan &clayMan = *handle.clayMan;
    clayMan.beginLayout();

    // Main Container
    clayMan.element(
        {
            // .id = CLAY_ID("Main-Container"),
            .layout = {
                .sizing = clayMan.expandXY(),
                .padding = {16, 16, 16, 16},
                .childGap = 16,
            },
            .backgroundColor = gray_0,
        },
        [&]()
        {
            // Left Column
            clayMan.element(
                {
                    .layout = {
                        .sizing = clayMan.expandXY(),
                        .childGap = 8,
                        .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_BOTTOM},
                        .layoutDirection = CLAY_TOP_TO_BOTTOM,
                    },
                    .backgroundColor = gray_1,
                },
                [&]()
                {
                    for (int i = 0; i < 3; i++)
                    {
                        std::string text = "Button " + std::to_string(i);
                        clayMan.textElement(text, infoText);
                    }
                });
            // Center Column
            clayMan.element(
                {
                    .layout = {
                        .sizing = clayMan.expandYfixedX(GetScreenWidth() * 3 / 7),
                    },
                    .backgroundColor = gray_1,
                },
                [&]() {});
            // Right Column
            clayMan.element(
                {
                    .layout = {
                        .sizing = clayMan.expandXY(),
                    },
                    .backgroundColor = gray_1,
                },
                [&]() {});
        });

    handle.commands = clayMan.endLayout();
}