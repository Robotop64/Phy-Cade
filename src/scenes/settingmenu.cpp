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
};
static Resources resources;

namespace
{
    void calcLayout(Gui::Handle &handle);
    void processInput(Gui::Handle &handle);
    void Button(std::string label, ClayMan &clayMan);
    void cleanupState();
}

void Scene::SettingMenu()
{
    Log::msg("Window", "Swap to Context: Setting-Menu");

    state = State{};

    Gui::Handle handle = Gui::init();
    calcLayout(handle);

    Log::msg("SettingMenu", "Starting Loop.");

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

    // Gui::cleanup();

    Log::msg("SettingMenu", "Ending Loop.");

    Log::msg("SettingMenu", "Leaving Current Context.");
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
            
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
            {
                state.close = true;
                Window::queueContext([](){ Scene::MainMenu(); });
            }    

            if (Gui::updatedInput())
            {
                state.rebuild_layout = true;
            }

            Gui::clearInput();
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
                .backgroundColor = gray_0
            },
            [&]()
            {

            });

            handle.commands = clayMan.endLayout();
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
        
        void cleanupState()
        {
            state = State{};
        }
    }
#pragma endregion local