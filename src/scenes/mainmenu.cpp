#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"
#include "logging.hpp"
#include "profiler.hpp"

struct State
{
    bool close = false;
    bool gui_lock = false;
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

        if (IsWindowResized()) //|| GetKeyPressed() != 0
        {
            Gui::updateMouse(handle);
            calcLayout(handle);
            Log::msg("MainMenu", "Recalculating Layout.");
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
}

void calcLayout(Gui::Handle &handle)
{
    ClayMan &clayMan = *handle.clayMan;
    clayMan.beginLayout();
    clayMan.element(
        {
            // .id = clayMan.hashID("Main-Container"),
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
                    // .id = clayMan.hashID("Box-A"),
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