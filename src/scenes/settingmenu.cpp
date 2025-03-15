#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"
#include "logging.hpp"
#include "profiler.hpp"
#include "config.hpp"

#include <string>
#include <vector>

struct State
{
    bool close = false;
    bool gui_lock = false;
    bool rebuild_layout = false;
};
static State state;

struct Resources
{
};
static Resources resources;

namespace
{
    const std::string menu = "SettingMenu";
    void calcLayout();
    void processInput();
    void Button(std::string);
    void cleanup();
    std::vector<std::string> getMajorGroups();
    std::vector<std::string> getMinorGroups(std::string major);
    std::vector<std::string> getSettings(std::string major);
}

void Scene::SettingMenu()
{
    Log::msg("Window", "Swap to Context: {}", menu);

    state = State{};
    resources = Resources{};

    Gui::init();
    Gui::setContext(menu);

    calcLayout();

    Log::msg(menu, "Starting Loop.");

    Gui::EnableDebug(false);

    while (!WindowShouldClose() && !state.close)
    {
        processInput();

        if (state.rebuild_layout)
        {
            Gui::updateState();
            calcLayout();
            state.rebuild_layout = false;
            // Log::msg(menu, "Rebuilt Layout on Frame {}.", (GetTime() / GetFPS()));
        }

        BeginDrawing();
        ClearBackground(BLACK);

        Gui::draw();

        EndDrawing();
    }

    cleanup();

    Log::msg(menu, "Ending Loop.");

    Log::msg(menu, "Leaving Current Context.");
}

#pragma region local
    namespace
    {
        #pragma region Styling
        const Clay_Color gray_0 = {35, 35, 35, 255};
        const Clay_Color gray_1 = {70, 70, 70, 255};
        const Clay_Color gray_2 = {105, 105, 105, 255};
        const Clay_Color gray_3 = {140, 140, 140, 255};
        const Clay_Color gray_4 = {175, 175, 175, 255};
        const Clay_Color gray_5 = {210, 210, 210, 255};
        const Clay_Color gray_6 = {245, 245, 245, 255};

        Clay_TextElementConfig infoText = {
            .textColor = {255, 255, 255, 255},
            .fontId = 0,
            .fontSize = 16,
            .letterSpacing = 2,
            .hashStringContents = true,
        };
        Clay_TextElementConfig buttonText = {
            .textColor = {255, 255, 255, 255},
            .fontId = 0,
            .fontSize = 32,
            .letterSpacing = 2,
            .hashStringContents = true,
        };
        Clay_TextElementConfig titleText = {
            .textColor = {255, 255, 255, 255},
            .fontId = 0,
            .fontSize = 48,
            .letterSpacing = 2,
            .hashStringContents = true,
        };
        #pragma endregion Styling

        void processInput()
        {
            
            if (Gui::componentClicked("MainMenu-Button", MOUSE_BUTTON_LEFT))
            {
                state.close = true;
                Window::queueContext([](){ Scene::MainMenu(); });
            }
            
        
            if (Gui::isInputUpdated())
            {
                state.rebuild_layout = true;
            }
        
            Gui::clearInput();
        }

        void Button(std::string label)
        {
            Clay_ElementId button_id = CLAY_SID(Gui::ClayString(label+"-Button"));
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
                .backgroundColor = Clay_PointerOver(button_id) ? gray_4 : gray_2,
                .border = {.color = gray_3, .width = {5, 5, 5, 5, 5}},
            }){
                CLAY_TEXT(Gui::ClayString(label), &buttonText);
            };
        }

        void calcLayout()
        {
            Gui::BeginLayout();

            //empty Main container
            CLAY({
                .layout = {
                    .sizing = {
                        .width = CLAY_SIZING_GROW(),
                        .height = CLAY_SIZING_GROW(),
                    },
                    .padding = {16, 16, 16, 16},
                    .childGap = 16,
                },
                .backgroundColor = gray_0,
            }){
            #pragma region SideBar
                CLAY({
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_PERCENT(0.2),
                            .height = CLAY_SIZING_GROW(),
                        },
                        .childGap = 16,
                        // .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_BOTTOM},
                        .layoutDirection = CLAY_TOP_TO_BOTTOM,
                    },
                    .backgroundColor = gray_1,
                }){
                    CLAY({
                        .layout = {
                            .sizing = {
                                .width = CLAY_SIZING_GROW(),
                                .height = CLAY_SIZING_FIXED(70),
                            },
                            .padding = {8, 8, 8, 8},
                            .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                        },
                        .backgroundColor = gray_2,
                    }){
                        CLAY_TEXT(Gui::ClayString("Settings"), &titleText);
                    };
                };
            #pragma endregion SideBar

            #pragma region Content
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
                    .backgroundColor = gray_1,
                }){
                    
                };
            #pragma endregion Content
            };

            Clay_RenderCommandArray commands = Gui::EndLayout();
            Gui::updateRenderCommands(commands);
        };

        void cleanup()
        {
        }
    }
#pragma endregion local


