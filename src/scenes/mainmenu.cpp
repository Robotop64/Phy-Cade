#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"
#include "logging.hpp"
#include "profiler.hpp"
#include "config.hpp"

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

void calcLayout(Gui::Handle &handle)
{
    ClayMan &clayMan = *handle.clayMan;
    clayMan.beginLayout();
    clayMan.element(
        {
            .id = CLAY_ID("Main-Container"),
            .layout = {
                .sizing = clayMan.expandXY(),
                .padding = {16, 16, 16, 16},
                .childGap = 16,
            },
        },
        [&]()
        {
            clayMan.element(
                {
                    .id = CLAY_ID("Box-A"),
                    .layout = {
                        .sizing = clayMan.expandYfixedX(200),
                    },
                    .backgroundColor = {255, 0, 0, 255},
                },
                [&]() {});
            clayMan.element(
                {
                    // .id = clayMan.hashID("Box-B"),
                    .layout = {
                        .sizing = clayMan.expandXY(),
                    },
                    .backgroundColor = {0, 255, 0, 255},
                },
                [&]() {});
            clayMan.element(
                {
                    // .id = clayMan.hashID("Box-C"),
                    .layout = {
                        .sizing = clayMan.expandYfixedX(200),
                    },
                    .backgroundColor = {0, 0, 255, 255},
                },
                [&]() {});
        });
    handle.commands = clayMan.endLayout();
}