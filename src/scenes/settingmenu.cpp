#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"
#include "logging.hpp"
#include "config.hpp"
#include "style.hpp"

#include <string>

namespace
{
    const std::string menu = "SettingMenu";

    void calcLayout();
    void processInput();
    void cleanup();

    void ActionButton(std::string);
    void unfoldTree(NodePtr node, int depth = 0);
    void SettingButton(OptionPtr Option);

    enum Direction
    {
        Vertical,
        Horizontal
    };
    void ClaySpring(Direction direction);

    struct State
    {
        bool close = false;
        // bool gui_lock = false;
        bool rebuild_layout = false;

        bool open_selector = false;
        bool close_active_dropdown = false;
        bool show_info = false;

        std::optional<NodePtr> selected_group = std::nullopt;
        std::optional<OptionPtr> selected_setting = std::nullopt;
    };
    State state;

    struct Resources
    {
    };
    Resources resources;
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
    void processInput()
    {
        bool process_input = false;

        if (Gui::isInputUpdated())
        {
            state.rebuild_layout = true;
            process_input = true;
        }

        if (!process_input)
            return;

        if (Gui::componentClicked("X-Button", MOUSE_BUTTON_LEFT))
        {
            state.close = true;
            Window::queueContext([]()
                                 { Scene::MainMenu(); });
        }

        if (Gui::componentClicked("i-Button", MOUSE_BUTTON_LEFT))
        {
            state.show_info = !state.show_info;
            Log::msg(menu, "Show Info: {}", state.show_info ? "true" : "false");
        }

        if (Gui::componentClicked("S-Button", MOUSE_BUTTON_LEFT))
        {
            Config::instance().save();
            Log::msg(menu, "Saved Config.");
        }
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
#pragma region SideBar
            CLAY({
                .layout = {
                    .sizing = {
                        .width = CLAY_SIZING_PERCENT(0.125),
                        .height = CLAY_SIZING_GROW(),
                    },
                    .childGap = 16,
                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                },
            })
            {
                CLAY({
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_GROW(),
                            .height = CLAY_SIZING_FIXED(70),
                        },
                        .padding = {8, 8, 8, 8},
                        .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                    },
                    .backgroundColor = Style::Dark::gray_2,
                    .cornerRadius = CLAY_CORNER_RADIUS(15),
                })
                {
                    CLAY_TEXT(Gui::ClayString("Settings"), &Style::Text::titleText);
                };

                CLAY({
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_GROW(),
                            .height = CLAY_SIZING_GROW(),
                        },
                        .padding = {8, 8, 8, 8},
                        .childGap = 8,
                        .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_TOP},
                        .layoutDirection = CLAY_TOP_TO_BOTTOM,
                    },
                    .backgroundColor = Style::Dark::gray_1,
                    .cornerRadius = CLAY_CORNER_RADIUS(15),
                })
                {
                    unfoldTree(Config::instance().tree());
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
                    .padding = {16, 16, 16, 16},
                    .childGap = 16,
                    .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_CENTER},
                    .layoutDirection = CLAY_LEFT_TO_RIGHT,
                },
                .backgroundColor = Style::Dark::gray_1,
                .cornerRadius = CLAY_CORNER_RADIUS(15),
            })
            {
                CLAY({
                    .layout = {
                        .sizing = {
                            .width = state.show_info ? CLAY_SIZING_PERCENT(0.5) : CLAY_SIZING_GROW(),
                            .height = CLAY_SIZING_GROW(),
                        },
                        .childGap = 16,
                        .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_TOP},
                        .layoutDirection = CLAY_TOP_TO_BOTTOM,
                    },
                })
                {
                    if (state.selected_group)
                    {
                        // separate loop to fix selecting a later setting not updating a previously selected one
                        // border change would otherwise be delayed by 1 frame
                        for (const auto &child : state.selected_group.value()->getOptions())
                        {
                            Clay_ElementId setting_id = CLAY_SID(Gui::ClayString(child->name + "-Setting"));

                            if (Gui::componentClicked(setting_id, MOUSE_BUTTON_LEFT))
                            {
                                state.selected_setting = child;
                                Log::msg(menu, "Selected Setting: {}", child->name);
                            }
                        }

                        for (const auto &child : state.selected_group.value()->getOptions())
                        {
                            Clay_ElementId setting_id = CLAY_SID(Gui::ClayString(child->name + "-Setting"));

                            CLAY({
                                .id = setting_id,
                                .layout = {
                                    .sizing = {
                                        .width = CLAY_SIZING_GROW(),
                                        .height = CLAY_SIZING_FIXED(50),
                                    },
                                    .padding = {32, 0, 8, 8},
                                    .childGap = 8,
                                    .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_CENTER},
                                    .layoutDirection = CLAY_LEFT_TO_RIGHT,
                                },
                                .backgroundColor = Clay_PointerOver(setting_id) ? Style::Dark::gray_4 : Style::Dark::gray_2,
                                .cornerRadius = CLAY_CORNER_RADIUS(15),
                                .border = {.color = state.selected_setting == child ? Style::Dark::gray_4 : Style::Dark::none, .width = {2, 2, 2, 2, 0}},
                            })
                            {
                                CLAY_TEXT(Gui::ClayString(child->name), &Style::Text::buttonText);

                                ClaySpring(Horizontal);

                                SettingButton(child);
                            };
                        }
                    }
                };

                if (state.selected_group && state.show_info)
                {
                    CLAY({
                        .layout = {
                            .sizing = {
                                .width = CLAY_SIZING_GROW(),
                                .height = CLAY_SIZING_GROW(),
                            },
                            .childGap = 16,
                            .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_TOP},
                            .layoutDirection = CLAY_TOP_TO_BOTTOM,
                        },
                        .backgroundColor = Style::Dark::gray_2,
                        .cornerRadius = CLAY_CORNER_RADIUS(15),
                    }){
                        // display info about selected setting, if available
                    };
                }
            };

#pragma endregion Content

#pragma region Actions
            CLAY({
                .layout = {
                    .sizing = {
                        .width = CLAY_SIZING_PERCENT(0.025),
                        .height = CLAY_SIZING_GROW(),
                    },
                    .childGap = 8,
                    .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_TOP},
                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                },
            })
            {
                ActionButton("X");
                ActionButton("i");
                ClaySpring(Vertical);
                ActionButton("S");
            };
#pragma endregion Actions
        };

        Clay_RenderCommandArray commands = Gui::EndLayout();
        Gui::updateRenderCommands(commands);
    };

    void cleanup()
    {
    }

    void ActionButton(std::string label)
    {
        Clay_ElementId button_id = CLAY_SID(Gui::ClayString(label + "-Button"));
        CLAY({
            .id = button_id,
            .layout = {
                .sizing = {
                    .width = CLAY_SIZING_FIXED(50),
                    .height = CLAY_SIZING_FIXED(50),
                },
                .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
            },
            .backgroundColor = Clay_PointerOver(button_id) ? Style::Dark::gray_4 : Style::Dark::gray_2,
            .cornerRadius = CLAY_CORNER_RADIUS(15),
        })
        {
            CLAY_TEXT(Gui::ClayString(label), &Style::Text::buttonText);
        };
    }

    void unfoldTree(NodePtr node, int depth)
    {
        bool is_leaf = node->getType() == NodeType::Leaf ? true : false;

        auto mapChildren = [](NodePtr node, int depth) -> void
        {
            for (const auto &child : node->getChildren())
            {
                unfoldTree(child, depth + 1);
            }
        };

        if (node->getName() != "root")
        {
            std::string id = node->path() + "-SideButton";
            Clay_ElementId SettingGroup_id = CLAY_SID(Gui::ClayString(id));

            if (Gui::componentClicked(SettingGroup_id, MOUSE_BUTTON_LEFT) && is_leaf)
            {
                state.selected_group = node;
                Log::msg(menu, "Selected SettingGroup: {}", node->path());
            }

            CLAY({
                .id = SettingGroup_id,
                .layout = {
                    .sizing = {
                        .width = CLAY_SIZING_GROW(),
                        .height = CLAY_SIZING_FIT(),
                    },
                    .padding = {8, 8, 8, 8},
                    .childGap = 4,
                    .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_CENTER},
                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                },
                .backgroundColor = is_leaf ? (Clay_PointerOver(SettingGroup_id) ? Style::Dark::gray_4 : Style::Dark::gray_3) : Style::Dark::gray_2,
                .cornerRadius = CLAY_CORNER_RADIUS(5),
            })
            {
                CLAY_TEXT(Gui::ClayString(node->getName() + ":"), &Style::Text::buttonText);

                if (!is_leaf)
                    mapChildren(node, depth);
            };
        }
        else
        {
            mapChildren(node, depth);
        }
    }

    void SettingButton(OptionPtr option)
    {
        OptionType type = option->type;
        std::string id = option->name + "-SettingButton";
        Clay_ElementId setting_id = CLAY_SID_LOCAL(Gui::ClayString(id));

        CLAY({
            .layout = {
                .sizing = {
                    .width = state.show_info ? CLAY_SIZING_PERCENT(0.4) : CLAY_SIZING_PERCENT(0.2),
                    .height = CLAY_SIZING_GROW(),
                },
                .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                .layoutDirection = CLAY_LEFT_TO_RIGHT,
            },
            .backgroundColor = Style::Dark::none,
            .border = {.color = Style::Dark::gray_4, .width = {3, 0, 0, 0, 0}},
        })
        {
            switch (type)
            {
            case OptionType::BOOL:
            {
                auto setting = Option::as<OptionExtended<bool>>(option);
                bool value = setting->value;

                if (Gui::componentClicked(setting_id, MOUSE_BUTTON_LEFT))
                {
                    value = !value;
                    setting->value = value;
                    Log::msg("Config", "Set {} to {}", setting->name, value ? "true" : "false");
                }

                CLAY({
                    .id = setting_id,
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_FIXED(30),
                            .height = CLAY_SIZING_FIXED(30),
                        },
                    },
                    .backgroundColor = value ? Style::Dark::gray_5 : Style::Dark::none,
                    .cornerRadius = CLAY_CORNER_RADIUS(5),
                    .border = {.color = Style::Dark::gray_5, .width = {2, 2, 2, 2, 0}},
                }){};
            }
            break;
            case OptionType::CHOICE:
            {
                Clay_ElementId selector_bounds = CLAY_SID_LOCAL(Gui::ClayString(id + "-Selector"));
                Clay_ElementId selector = CLAY_SID_LOCAL(Gui::ClayString(id + "-Selector-Box"));

                if (Gui::componentClicked(setting_id, MOUSE_BUTTON_LEFT))
                {
                    state.open_selector = true;
                    state.close_active_dropdown = false;
                    Log::msg(menu, "Activated Choice selector: {}", option->name);
                }

                switch (option->innerType.value())
                {
                case OptionType::STRING:
                {
                    auto setting = Option::as<OptionChoice<std::string>>(option);
                    std::string value = setting->value;
                    std::vector<std::string> choices = setting->choices;

                    for (const auto &choice : choices)
                    {
                        Clay_ElementId choice_id = CLAY_SID(Gui::ClayString(choice + "-Choice"));

                        if (Clay_PointerOver(selector_bounds) && !Clay_PointerOver(selector) && IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && state.open_selector)
                        {
                            state.close_active_dropdown = true;
                            Log::msg(menu, "Canceled Selector");
                            break;
                        }

                        if (Gui::componentClicked(choice_id, MOUSE_BUTTON_LEFT))
                        {
                            setting->value = choice;
                            value = choice;
                            state.close_active_dropdown = true;
                            Log::msg(menu, "Set {} to {}", setting->name, value);
                        }
                    }

                    CLAY({
                        .id = setting_id,
                        .layout = {
                            .sizing = {
                                .width = CLAY_SIZING_GROW(),
                                .height = CLAY_SIZING_FIXED(30),
                            },
                            .padding = {5, 5, 0, 0},
                            .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                        },
                        .backgroundColor = Style::Dark::none,
                    })
                    {
                        CLAY_TEXT(Gui::ClayString(value), &Style::Text::buttonText);
                    };

                    if (state.open_selector && !state.close_active_dropdown && state.selected_setting == option)
                    {

                        CLAY({
                            .id = selector_bounds,
                            .layout = {
                                .sizing = {
                                    .width = CLAY_SIZING_GROW(),
                                    .height = CLAY_SIZING_GROW(),
                                },
                            },
                            .backgroundColor = Style::Dark::none,
                            .floating = {.attachTo = CLAY_ATTACH_TO_ROOT},
                        })
                        {
                            CLAY({
                                .id = selector,
                                .layout = {
                                    .sizing = {
                                        .width = CLAY_SIZING_FIT(),
                                        .height = CLAY_SIZING_FIT(),
                                    },
                                    .childGap = 8,
                                    .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_TOP},
                                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                                },
                                .backgroundColor = Style::Dark::gray_2,
                                .floating = {
                                    .parentId = setting_id.id,
                                    .attachPoints = {
                                        .element = CLAY_ATTACH_POINT_CENTER_TOP,
                                        .parent = CLAY_ATTACH_POINT_CENTER_BOTTOM,
                                    },
                                    .attachTo = CLAY_ATTACH_TO_ELEMENT_WITH_ID,
                                },
                            })
                            {
                                for (const auto &choice : choices)
                                {
                                    Clay_ElementId choice_id = CLAY_SID(Gui::ClayString(choice + "-Choice"));

                                    CLAY({
                                        .id = choice_id,
                                        .layout = {
                                            .sizing = {
                                                .width = CLAY_SIZING_GROW(),
                                                .height = CLAY_SIZING_FIXED(30),
                                            },
                                            .padding = {10, 10, 0, 0},
                                            .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                                        },
                                        .backgroundColor = Clay_Hovered() ? Style::Dark::gray_4 : Style::Dark::none,
                                    })
                                    {
                                        CLAY_TEXT(Gui::ClayString(choice), &Style::Text::buttonText);
                                    };
                                }
                            };
                        };
                    }
                }
                break;
                }
            }
            break;
            case OptionType::RANGE:
            {
                Clay_ElementId setting_inc = CLAY_SID_LOCAL(Gui::ClayString(id + "-Increase"));
                Clay_ElementId setting_dec = CLAY_SID_LOCAL(Gui::ClayString(id + "-Reduce"));

                int mode = 0;
                if (Gui::componentClicked(setting_inc, MOUSE_BUTTON_LEFT))
                {
                    mode = 1;
                }
                else if (Gui::componentClicked(setting_dec, MOUSE_BUTTON_LEFT))
                {
                    mode = -1;
                }

                Clay_ElementDeclaration change_button = {
                    .layout = {
                        .sizing = {
                            .width = CLAY_SIZING_FIXED(30),
                            .height = CLAY_SIZING_FIXED(30),
                        },
                        .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                    },
                    .backgroundColor = Style::Dark::none,
                    .cornerRadius = CLAY_CORNER_RADIUS(5),
                    .border = {.color = Style::Dark::gray_5, .width = {2, 2, 2, 2, 0}},
                };

                switch (option->innerType.value())
                {
                case OptionType::INT:
                {
                    auto setting = Option::as<OptionRange<int>>(option);
                    int value = setting->value;
                    const int min = setting->min;
                    const int max = setting->max;

                    int change = 1;
                    if (IsKeyDown(KEY_LEFT_SHIFT))
                        change = 10;
                    if (IsKeyDown(KEY_LEFT_CONTROL))
                        change = 100;
                    if (IsKeyDown(KEY_LEFT_SHIFT) && IsKeyDown(KEY_LEFT_CONTROL))
                        change = 1000;

                    if (!mode == 0 && (min <= value + change * mode && value + change * mode <= max))
                    {
                        value = value + change * mode;
                        setting->value = value;
                        Log::msg(menu, "Set {} to {}", setting->name, value);
                    }

                    change_button.id = setting_dec;

                    CLAY(change_button)
                    {
                        CLAY_TEXT(Gui::ClayString("<"), &Style::Text::buttonText);
                    };

                    CLAY({
                        .id = setting_id,
                        .layout = {
                            .sizing = {
                                .width = CLAY_SIZING_FIT(),
                                .height = CLAY_SIZING_FIXED(30),
                            },
                            .padding = {10, 10, 0, 0},
                            .childAlignment = {.x = CLAY_ALIGN_X_CENTER, .y = CLAY_ALIGN_Y_CENTER},
                        },
                        .backgroundColor = Style::Dark::none,
                    })
                    {
                        CLAY_TEXT(Gui::ClayString(std::to_string(value)), &Style::Text::buttonText);
                    };

                    change_button.id = setting_inc;

                    CLAY(change_button)
                    {
                        CLAY_TEXT(Gui::ClayString(">"), &Style::Text::buttonText);
                    };
                }
                break;
                }
            }
            break;
            };
        }
    }

    void ClaySpring(Direction direction)
    {
        CLAY({
            .layout = {
                .sizing = {
                    .width = direction == Vertical ? CLAY_SIZING_FIXED(0) : CLAY_SIZING_GROW(),
                    .height = direction == Vertical ? CLAY_SIZING_GROW() : CLAY_SIZING_FIXED(0),
                },
            },
        }){};
    }

#pragma endregion local
}