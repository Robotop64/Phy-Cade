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

namespace
{
    void calcLayout(Gui::Handle &handle);
    void processInput(Gui::Handle &handle);
    void Button(std::string label, ClayMan &clayMan);
    void cleanup();
}

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
        processInput(handle);

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

    cleanup();
    Gui::cleanup();

    Log::msg("MainMenu", "Ending Loop.");

    Log::msg("MainMenu", "Leaving Current Context.");
}

#pragma region local
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

    namespace
    {
        void processInput(Gui::Handle &handle)
        {
            ClayMan &clayMan = *handle.clayMan;
            
            if (Clay_PointerOver(CLAY_ID("Play-Button")) && IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
            {
                state.close = true;
                Window::queueContext([](){ Scene::Game(); });
            }
        
            // if (Clay_PointerOver(CLAY_ID("Leaderboard-Button")) && IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
            // {
            //     state.close = true;
            //     Window::queueContext([](){ Scene::Leaderboard(); });
            // }
        
            if (Clay_PointerOver(CLAY_ID("Settings-Button")) && IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
            {
                state.close = true;
                Window::queueContext([](){ Scene::SettingMenu(); });
            }
        
            if (Clay_PointerOver(CLAY_ID("Quit-Button")) && IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
            {
                state.close = true;
                Window::queueContext(nullptr);
                Log::msg("Window", "Swap to Context: None");
            }
            
        
            if (Gui::updatedInput())
            {
                state.rebuild_layout = true;
            }
        
            Gui::clearInput();
        }

        void Button(std::string label, ClayMan &clayMan)
        {
            clayMan.element(
                {
                    .id = clayMan.hashID(label+"-Button"),
                    .layout = {
                        .sizing = clayMan.fixedSize(250, 50),
                        .padding = {8, 8, 8, 8},
                        .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                    },
                    .backgroundColor = Clay_PointerOver(clayMan.hashID(label+"-Button")) ? gray_4 : gray_2,
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
                        },
                        [&]()
                        {
                            clayMan.textElement(std::string("Build Version: " + std::string("VERSION")), infoText);
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
                                },
                                [&]()
                                {
                                    Button("Play", clayMan);
                                    Button("Leaderboard", clayMan);
                                    Button("Settings", clayMan);
                                    Button("Quit", clayMan);
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
                        },
                        [&]()
                        {
                            clayMan.element(
                                {
                                    .layout = {
                                        .sizing = clayMan.fixedSize(250, 30),
                                        .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                                    },
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

        void cleanup()
        {
            UnloadTexture(resources.QR);
        }
    }
#pragma endregion local


