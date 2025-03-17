#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"
#include "logging.hpp"
#include "profiler.hpp"
#include "config.hpp"

#include <string>

namespace
{
    const std::string menu = "SettingMenu";
    void calcLayout();
    void processInput();
    void ActionButton(std::string);
    void cleanup();
    void unfoldTree(Node node, int depth = 0);
    void prep_configGroups(std::vector<Node *> &groups);
}

struct State
{
    bool close = false;
    bool gui_lock = false;
    bool rebuild_layout = false;

    std::optional<Node *> selected_group = std::nullopt;
};
static State state;

struct Resources
{
    Node configTree = Config::instance().parseTree();
    std::vector<Node *> sidebar_groups = {};
};
static Resources resources;

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

        for (Node *group : resources.sidebar_groups)
        {
            if (Gui::componentClicked(group->path() + "-SideButton", MOUSE_BUTTON_LEFT))
            {
                state.selected_group = group;
                Log::msg(menu, "Selected SettingGroup: {}", group->name);
            }
        }
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
            .backgroundColor = Clay_PointerOver(button_id) ? gray_4 : gray_2,
            .cornerRadius = CLAY_CORNER_RADIUS(15),
            // .border = {.color = gray_3, .width = {5, 5, 5, 5, 5}},
        })
        {
            CLAY_TEXT(Gui::ClayString(label), &buttonText);
        };
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
            .backgroundColor = gray_0,
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
                    // .childAlignment = {.x = CLAY_ALIGN_X_LEFT, .y = CLAY_ALIGN_Y_BOTTOM},
                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                },
                // .backgroundColor = gray_1,
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
                    .backgroundColor = gray_2,
                    .cornerRadius = CLAY_CORNER_RADIUS(15),
                })
                {
                    CLAY_TEXT(Gui::ClayString("Settings  "), &titleText);
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
                    .backgroundColor = gray_1,
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
                    .childGap = 8,
                    .childAlignment = {.x = CLAY_ALIGN_X_RIGHT, .y = CLAY_ALIGN_Y_BOTTOM},
                    .layoutDirection = CLAY_TOP_TO_BOTTOM,
                },
                .backgroundColor = gray_1,
                .cornerRadius = CLAY_CORNER_RADIUS(15),
            }){

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
                // .backgroundColor = gray_2,
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

    void unfoldTree(Node node, int depth)
    {
        bool has_subgroups = false;
        for (auto child : node.children)
        {
            if (child.node_type == Node::Group)
            {
                has_subgroups = true;
                break;
            }
        }

        if (node.name != "root" && node.node_type == Node::Group)
        {
            std::string id = node.path() + "-SideButton";
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
                .backgroundColor = !has_subgroups ? (Clay_PointerOver(SettingGroup_id) ? gray_4 : gray_3) : gray_2,
                .cornerRadius = CLAY_CORNER_RADIUS(5),
            })
            {
                std::string label;
                // for (int i = 0; i < depth; i++)
                // {
                //     label += "|\t";
                // }
                label += node.name;
                if (has_subgroups)
                    label += ":";

                CLAY_TEXT(Gui::ClayString(label), &buttonText);

                // iterate in reverse so Base / General is above Advanced
                for (auto child = node.children.rbegin(); child != node.children.rend(); child++)
                {
                    if (child->node_type == Node::Group)
                    {
                        unfoldTree(*child, depth + 1);
                    }
                }
            };
        }
        else
        {
            for (auto child : node.children)
            {
                if (child.node_type == Node::Group)
                {
                    unfoldTree(child, depth);
                }
            }
        }
    }

    void prep_configGroups(std::vector<Node *> &groups)
    {
        std::vector<Node *> stack = {};
        Node *root = &resources.configTree;
        stack.push_back(root); // add root as start point

        while (!stack.empty())
        {
            Node *node = stack.back();
            stack.pop_back();

            // Log::msg(menu,
            //          "Node: {}, Is a {}, Has {} children.", node->name, node->node_type == Node::Group ? "Group" : "Leaf", node->children.size());

            bool has_subgroups = false;

            for (auto child : node->children)
            {
                // Log::msg(menu, "->Checking Child: {}", child.name);
                if (child.node_type == Node::Group)
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

            for (Node &child : node->children)
            {
                if (child.node_type == Node::Group)
                {
                    stack.push_back(&child);
                    // Log::msg(menu, "->Pushed Child: {}", child.name);
                }
            }
        }
    }

#pragma endregion local
}