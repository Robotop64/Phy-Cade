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

struct Resources
{
    Texture2D QR;
};
static Resources resources;

void calcLayout(Gui::Handle &handle);
void processInput();

void Scene::MainMenu()
{
    Log::msg("Window", "Swap to Context: Main-Menu");

    state = State{};
    resources = Resources{.QR = LoadTexture("resources/GitQR-Inverted.png")};

    Gui::Handle handle = Gui::init();
    calcLayout(handle);

    Log::msg("MainMenu", "Starting Loop.");

    // Clay_SetDebugModeEnabled(true);

    while (!WindowShouldClose() && !state.close)
    {
        processInput();

        if (state.rebuild_layout)
        {
            Gui::updateMouse(handle);
            calcLayout(handle);
            state.rebuild_layout = false;
            // Log::msg("MainMenu", "Rebuilt Layout on Frame {}.", (GetTime() / GetFPS()));
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

    if (Gui::updatedInput())
    {
        state.rebuild_layout = true;
    }

    Gui::clearInput();
}

#pragma region Styling
const Clay_Color gray_0 = {35, 35, 35, 255};
const Clay_Color gray_1 = {70, 70, 70, 255};
const Clay_Color gray_2 = {105, 105, 105, 255};
const Clay_Color gray_3 = {140, 140, 140, 255};
const Clay_Color gray_4 = {175, 175, 175, 255};
const Clay_Color gray_5 = {210, 210, 210, 255};
const Clay_Color gray_6 = {245, 245, 245, 255};

const Clay_TextElementConfig infoText = {
    .textColor = {255, 255, 255, 255},
    .fontId = 0,
    .fontSize = 16,
    .letterSpacing = 2,
};
const Clay_TextElementConfig buttonText = {
    .textColor = {255, 255, 255, 255},
    .fontId = 0,
    .fontSize = 32,
    .letterSpacing = 2,
};
const Clay_TextElementConfig titleText = {
    .textColor = {255, 255, 255, 255},
    .fontId = 0,
    .fontSize = 48,
    .letterSpacing = 2,
};
#pragma endregion Styling

void MenuButton(std::string label, ClayMan &clayMan)
{
    clayMan.element(
        {
            // .id = CLAY_ID("Test"),
            .layout = {
                .sizing = clayMan.fixedSize(250, 50),
                .padding = {8, 8, 8, 8},
                .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
            },
            .backgroundColor = gray_2,
            .border = {.color = gray_3, .width = {5, 5, 5, 5, 5}},
        },
        [&]()
        {
            clayMan.textElement(label, buttonText);
        });
}

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
#pragma region LeftColumn
            clayMan.element(
                {
                    .layout = {
                        .sizing = clayMan.expandXY(),
                        .childGap = 8,
                        .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_BOTTOM},
                        .layoutDirection = CLAY_TOP_TO_BOTTOM,
                    },
                    // .backgroundColor = gray_1,
                },
                [&]()
                {
                    clayMan.textElement("Build Version: ", infoText);
                });
#pragma endregion LeftColumn
#pragma region CenterColumn
            clayMan.element(
                {
                    .layout = {
                        .sizing = {.width = CLAY_SIZING_PERCENT(0.4), .height = CLAY_SIZING_GROW()},
                        .childGap = 32,
                        .childAlignment = {.x = CLAY_ALIGN_X_CENTER},
                        .layoutDirection = CLAY_TOP_TO_BOTTOM,
                    },
                    // .backgroundColor = gray_1,
                },
                [&]()
                {
                    // Buffer
                    clayMan.element({.layout = {.sizing = {.width = CLAY_SIZING_GROW(), .height = CLAY_SIZING_PERCENT(0.05)}}},
                                    [&]() {});
                    // Title
                    clayMan.element(
                        {
                            .layout = {
                                .sizing = clayMan.expandXfixedY(50),
                                .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                            },
                            // .backgroundColor = gray_2,
                        },
                        [&]()
                        {
                            clayMan.textElement("PacMan: Phi-cade Edition", titleText);
                        });
                    // Buffer
                    clayMan.element({.layout = {.sizing = {.width = CLAY_SIZING_GROW(), .height = CLAY_SIZING_PERCENT(0.10)}}},
                                    [&]() {});
                    // Buttons
                    clayMan.element(
                        {
                            .layout = {
                                .sizing = CLAY_SIZING_FIT(),
                                .childGap = 32,
                                .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                                .layoutDirection = CLAY_TOP_TO_BOTTOM,
                            },
                            // .backgroundColor = gray_2,
                        },
                        [&]()
                        {
                            MenuButton("Play", clayMan);
                            MenuButton("Leaderboard", clayMan);
                            MenuButton("Settings", clayMan);
                            MenuButton("Credits", clayMan);
                        });
                });
#pragma endregion CenterColumn
#pragma region RightColumn
            clayMan.element(
                {
                    .layout = {
                        .sizing = clayMan.expandXY(),
                        .childGap = 8,
                        .childAlignment = {.x = CLAY_ALIGN_X_RIGHT, .y = CLAY_ALIGN_Y_BOTTOM},
                        .layoutDirection = CLAY_TOP_TO_BOTTOM,
                    },
                    // .backgroundColor = gray_1,
                },
                [&]()
                {
                    clayMan.element(
                        {
                            .layout = {
                                .sizing = clayMan.fixedSize(250, 30),
                                .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                            },
                            // .backgroundColor = gray_2,
                        },
                        [&]()
                        { clayMan.textElement("Problems & Suggestions to:", infoText); });
                    clayMan.element(
                        {
                            .layout = {
                                .sizing = clayMan.fixedSize(200, 200),
                            },
                            .image = {.imageData = &resources.QR, .sourceDimensions = {1000, 1000}},
                        },
                        [&]() {});
                });
#pragma endregion RightColumn
        });

    handle.commands = clayMan.endLayout();
}