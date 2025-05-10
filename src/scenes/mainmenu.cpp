#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"
#include "logging.hpp"
#include "config.hpp"
#include "style.hpp"
#include "profiler.hpp"

#include <string>

namespace
{
    const std::string menu = "MainMenu";

    void calcLayout();
    void processInput();
    void cleanup();

    void Button(std::string);

    struct State
    {
        bool close = false;
        bool rebuild_layout = false;
    };
    State state;

    struct Resources
    {
        Texture2D QR;
    };
    Resources resources;
}

void Scene::MainMenu()
{
    Log::msg("Window", "Swap to Context: {}", menu);

    state = State{};
    resources = Resources{.QR = LoadTexture("resources/GitQR-Inverted.png")};

    Gui::init();
    Gui::setContext(menu);

    calcLayout();

    Log::msg(menu, "Starting Loop.");

    Gui::EnableDebug(false);

    while (!WindowShouldClose() && !state.close)
    {
        Profiler::StampS("MainMenu");

        processInput();

        if (state.rebuild_layout)
        {
            Gui::updateState();
            calcLayout();
            state.rebuild_layout = false;
        }

        BeginDrawing();
        ClearBackground(BLACK);

        Gui::draw();

        EndDrawing();
        Profiler::StampE("MainMenu");
        // Log::msg("Profiler", "Gui-StringArena State: {}", std::get<float>(Profiler::GetValue("Gui-StringArena")));
        // Log::msg("Profiler", "Frame Time: {}ms", Profiler::EvalScope("MainMenu").count() * 1000);
    }

    cleanup();

    Log::msg(menu, "Ending Loop.");

    Log::msg(menu, "Leaving Current Context.");
}

#pragma region local
namespace
{
    void processInput()
    {
        if (!Gui::isInputUpdated())
            return;

        state.rebuild_layout = true;

        // if (Gui::componentClicked("Play-Button", MOUSE_BUTTON_LEFT))
        // {
        //     state.close = true;
        //     Window::queueContext([](){ Scene::Game(); });
        // }

        // if (Clay_PointerOver(CLAY_ID("Leaderboard-Button")) && IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
        // {
        //     state.close = true;
        //     Window::queueContext([](){ Scene::Leaderboard(); });
        // }

        if (Gui::componentClicked("Settings-Button", MOUSE_BUTTON_LEFT))
        {
            state.close = true;
            Window::queueContext([]()
                                 { Scene::SettingMenu(); });
        }

        if (Gui::componentClicked("Quit-Button", MOUSE_BUTTON_LEFT))
        {
            state.close = true;
            Window::queueContext(nullptr);
            Log::msg("Window", "Swap to Context: None");
        }

        Gui::clearInput();
    }

    void calcLayout()
    {
        Gui::BeginLayout();

        // empty Main container
        CLAY({
            .layout = {
                .sizing = {
                    .width = CLAY_SIZING_GROW(),
                    .height = CLAY_SIZING_GROW(),
                },
                .padding = {16, 16, 16, 16},
                .childGap = 16,
            },
            .backgroundColor = Style::Dark::gray_0,
        })
        {
#pragma region LeftColumn
            CLAY({
                .layout = {
                    .sizing = {
                        .width = CLAY_SIZING_GROW(),
                        .height = CLAY_SIZING_GROW(),
                    },
                    .childGap = 8,
                    .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_BOTTOM},
                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                },
            })
            {
                CLAY_TEXT(Gui::ClayString("Build Version: " + std::string("VERSION")), &Style::Text::infoText);
            };
#pragma endregion LeftColumn

#pragma region CenterColumn
            CLAY({
                .layout = {
                    .sizing = {
                        .width = CLAY_SIZING_PERCENT(0.4),
                        .height = CLAY_SIZING_GROW(),
                    },
                    .childGap = 32,
                    .childAlignment = {.x = CLAY_ALIGN_X_CENTER},
                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                },
            })
            {
                // Buffer
                CLAY({
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_GROW(),
                            .height = CLAY_SIZING_PERCENT(0.05),
                        },
                    },
                }){};
                // Title
                CLAY({
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_GROW(),
                            .height = CLAY_SIZING_FIXED(50),
                        },
                        .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                    },
                })
                {
                    CLAY_TEXT(Gui::ClayString("PacMan: Phi-cade Edition"), &Style::Text::titleText);
                };
                // Buffer
                CLAY({
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_GROW(),
                            .height = CLAY_SIZING_PERCENT(0.10),
                        },
                    },
                }){};
                // Buttons
                CLAY({
                    .layout = {
                        .sizing = CLAY_SIZING_FIT(),
                        .childGap = 32,
                        .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                        .layoutDirection = CLAY_TOP_TO_BOTTOM,
                    },
                })
                {
                    Button("Play");
                    Button("Leaderboard");
                    Button("Settings");
                    Button("Quit");
                };
            };
#pragma endregion CenterColumn

#pragma region RightColumn
            CLAY({
                .layout = {
                    .sizing = {
                        .width = CLAY_SIZING_GROW(),
                        .height = CLAY_SIZING_GROW(),
                    },
                    .childGap = 8,
                    .childAlignment = {.x = CLAY_ALIGN_X_RIGHT, .y = CLAY_ALIGN_Y_BOTTOM},
                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                },
            })
            {
                CLAY({
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_FIXED(200),
                            .height = CLAY_SIZING_FIXED(30),
                        },
                        .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                    },
                })
                {
                    CLAY_TEXT(Gui::ClayString("Problems & Suggestions to:"), &Style::Text::infoText);
                };
                CLAY({
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_FIXED(200),
                            .height = CLAY_SIZING_FIXED(200),
                        },
                    },
                    .image = {.imageData = &resources.QR, .sourceDimensions = {1000, 1000}},
                }){};
            };
#pragma endregion RightColumn
        };

        Clay_RenderCommandArray commands = Gui::EndLayout();
        Gui::updateRenderCommands(commands);
    };

    void cleanup()
    {
        UnloadTexture(resources.QR);
    }

    void Button(std::string label)
    {
        Clay_ElementId button_id = CLAY_SID(Gui::ClayString(label + "-Button"));
        CLAY({
            .id = button_id,
            .layout = {
                .sizing = {
                    .width = CLAY_SIZING_FIXED(250),
                    .height = CLAY_SIZING_FIXED(50),
                },
                .padding = {8, 8, 8, 8},
                .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
            },
            .backgroundColor = Clay_PointerOver(button_id) ? Style::Dark::gray_4 : Style::Dark::gray_2,
            .cornerRadius = CLAY_CORNER_RADIUS(15),
        })
        {
            CLAY_TEXT(Gui::ClayString(label), &Style::Text::buttonText);
        };
    }
}
#pragma endregion local
