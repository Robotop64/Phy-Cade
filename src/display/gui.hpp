#pragma once

#include "clay.h"
#include "clay_renderer_raylib.h"

#include <string>
#include <map>

class Gui
{
public:
    static Font fonts[];
    static Clay_RenderCommandArray current_Commands;

    static void init();
    static void setContext(const std::string &name);

    static void updateState();
    static bool isInputUpdated();
    static void clearInput();
    static bool componentClicked(const std::string &id, const int button);

    static void BeginLayout();
    static Clay_RenderCommandArray EndLayout();

    static void draw();
    static void updateRenderCommands(const Clay_RenderCommandArray &commands);

    static void cleanup();

    static void EnableDebug(const bool enable)
    {
        Clay_SetDebugModeEnabled(enable);
    }
    static Clay_Context *CreateContext();
    static Clay_String ClayString(const std::string &text);

private:
    static Clay_Context *current_Context;
    static std::map<std::string, Clay_Context *> contexts;

    static char stringArena[];
    static size_t nextStringArenaIndex;

    static void handleErrors(Clay_ErrorData errorData)
    {
        printf("%s", errorData.errorText.chars);
    }

    static const char *insertStringIntoArena(const std::string &str);
    static void resetStringArenaIndex()
    {
        nextStringArenaIndex = 0;
    }
};