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
    void unfoldTree(std::shared_ptr<Node> node, int depth = 0);
    void prep_configGroups(std::vector<std::shared_ptr<Node>> &groups);
    void SettingButton(std::shared_ptr<Node> setting);

    enum Direction
    {
        Vertical,
        Horizontal
    };
    void ClaySpring(Direction direction);

    struct State
    {
        bool close = false;
        bool gui_lock = false;
        bool rebuild_layout = false;

        std::optional<std::shared_ptr<Node>> selected_group = std::nullopt;
    };
    State state;

    struct Resources
    {
        std::shared_ptr<Node> configTree = Config::instance().parseTree();
        std::vector<std::shared_ptr<Node>> sidebar_groups = {};
    };
    Resources resources;
}

void Scene::SettingMenu()
{
    Log::msg("Window", "Swap to Context: {}", menu);

    state = State{};
    resources = Resources{};
    prep_configGroups(resources.sidebar_groups);

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

        Gui::clearInput();

        if (!process_input)
            return;

        if (Gui::componentClicked("X-Button", MOUSE_BUTTON_LEFT))
        {
            state.close = true;
            Window::queueContext([]()
                                 { Scene::MainMenu(); });
        }

        for (const auto &group : resources.sidebar_groups)
        {
            if (Gui::componentClicked(group->path() + "-SideButton", MOUSE_BUTTON_LEFT))
            {
                state.selected_group = group;
                Log::msg(menu, "Selected SettingGroup: {}", group->getName());
                // state.rebuild_layout = true;
            }
        }

        // if (state.selected_group)
        // {
        //     Node *group = state.selected_group.value();

        //     for (Node &child : group->children)
        //     {
        //         if (Gui::componentClicked(child.name + "-Setting", MOUSE_BUTTON_LEFT))
        //         {
        //             Log::msg(menu, "Selected Setting: {}", child.path());
        //         }
        //     }
        // }
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
                    unfoldTree(resources.configTree);
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
                    .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_TOP},
                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                },
                .backgroundColor = Style::Dark::gray_1,
                .cornerRadius = CLAY_CORNER_RADIUS(15),
            })
            {
                if (state.selected_group)
                {
                    for (const auto &child : state.selected_group.value()->getChildren())
                    {
                        Clay_ElementId setting_id = CLAY_SID(Gui::ClayString(child->getName() + "-Setting"));
                        CLAY({
                            .id = setting_id,
                            .layout = {
                                .sizing = {
                                    .width = CLAY_SIZING_GROW(),
                                    .height = CLAY_SIZING_FIXED(50),
                                },
                                .padding = {32, 32, 8, 8},
                                .childGap = 8,
                                .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_CENTER},
                                .layoutDirection = CLAY_LEFT_TO_RIGHT,
                            },
                            .backgroundColor = Clay_PointerOver(setting_id) ? Style::Dark::gray_4 : Style::Dark::gray_2,
                            .cornerRadius = CLAY_CORNER_RADIUS(15),
                        })
                        {
                            CLAY_TEXT(Gui::ClayString(child->getName()), &Style::Text::buttonText);

                            ClaySpring(Horizontal);

                            SettingButton(child);
                        };
                    }
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
                },
            })
            {
                ActionButton("X");
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

    void unfoldTree(std::shared_ptr<Node> node, int depth)
    {
        bool has_subgroups = false;
        for (const auto &child : node->getChildren())
        {
            if (child->getType() == Node::Group)
            {
                has_subgroups = true;
                break;
            }
        }

        if (node->getName() != "root" && node->getType() == Node::Group)
        {
            std::string id = node->path() + "-SideButton";
            Clay_ElementId SettingGroup_id = CLAY_SID(Gui::ClayString(id));

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
                .backgroundColor = !has_subgroups ? (Clay_PointerOver(SettingGroup_id) ? Style::Dark::gray_4 : Style::Dark::gray_3) : Style::Dark::gray_2,
                .cornerRadius = CLAY_CORNER_RADIUS(5),
            })
            {
                std::string label;

                label += node->getName();
                if (has_subgroups)
                    label += ":";

                CLAY_TEXT(Gui::ClayString(label), &Style::Text::buttonText);

                // iterate in reverse so Base / General is above Advanced
                for (const auto &child : node->getChildren())
                {
                    if (child->getType() == Node::Group)
                    {
                        unfoldTree(child, depth + 1);
                    }
                }
            };
        }
        else
        {
            for (const auto &child : node->getChildren())
            {
                if (child->getType() == Node::Group)
                {
                    unfoldTree(child, depth);
                }
            }
        }
    }

    void prep_configGroups(std::vector<std::shared_ptr<Node>> &groups)
    {
        std::vector<std::shared_ptr<Node>> stack = {};
        std::shared_ptr<Node> root = resources.configTree;
        stack.push_back(root); // add root as start point

        while (!stack.empty())
        {
            std::shared_ptr<Node> node = stack.back();
            stack.pop_back();

            // Log::msg(menu,
            //          "Node: {}, Is a {}, Has {} children.", node->name, node->node_type == Node::Group ? "Group" : "Leaf", node->children.size());

            bool has_subgroups = false;

            for (const auto &child : node->getChildren())
            {
                // Log::msg(menu, "->Checking Child: {}", child.name);
                if (child->getType() == Node::Group)
                {
                    has_subgroups = true;
                    // Log::msg(menu, "-->Child is Groups.");
                    break;
                }
            }

            if (!has_subgroups)
            {
                groups.push_back(node);
                // Log::msg(menu, "Added Group: {}", node->name);
            }

            for (const auto &child : node->getChildren())
            {
                if (child->getType() == Node::Group)
                {
                    stack.push_back(child);
                    // Log::msg(menu, "->Pushed Child: {}", child.name);
                }
            }
        }
    }

    void SettingButton(std::shared_ptr<Node> setting)
    {
        std::any value = setting->getValue();
        if (!value.has_value())
        {
            return;
        }
        else
        {
            value = setting->getValue().value();
        }

        if (value.type() == typeid(bool))
        {
            bool bool_value = std::any_cast<bool>(value);

            Clay_ElementId setting_id = CLAY_SID(Gui::ClayString(setting->path() + "-SettingButton"));

            CLAY({
                .id = setting_id,
                .layout = {
                    .sizing = {
                        .width = CLAY_SIZING_FIXED(30),
                        .height = CLAY_SIZING_FIXED(30),
                    },
                },
                .backgroundColor = bool_value ? Style::Dark::gray_5 : Style::Dark::none,
                .border = {.color = Style::Dark::gray_5, .width = {3, 3, 3, 3, 0}},
            }){};
            if (Gui::componentClicked(setting->path() + "-SettingButton", MOUSE_BUTTON_LEFT))
            {
                bool_value = !bool_value;

                setting->setValue(bool_value);
                Config::instance().set(Config::User, setting->path(), bool_value);

                Log::msg(menu, "Set {} to {}", setting->path(), bool_value ? "true" : "false");
            }
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