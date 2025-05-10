#include "gui.hpp"
#include "logging.hpp"
#include "profiler.hpp"

Clay_RenderCommandArray Gui::current_Commands = Clay_RenderCommandArray{};
Clay_Context *Gui::current_Context = nullptr;
Font Gui::fonts[4] = {0, 0, 0, 0};
std::map<std::string, Clay_Context *> Gui::contexts = std::map<std::string, Clay_Context *>();
size_t Gui::nextStringArenaIndex = 0;
char Gui::stringArena[100000] = {0};

void Gui::init()
{
    static bool initialized = false;
    if (!initialized)
    {
        initialized = true;

        Gui::fonts[0] = LoadFontEx("resources/fonts/Roboto-Regular.ttf", 16, 0, 400);
        Gui::fonts[1] = LoadFontEx("resources/fonts/Roboto-Regular.ttf", 32, 0, 400);
        Gui::fonts[2] = LoadFontEx("resources/fonts/Roboto-Regular.ttf", 48, 0, 400);
        Gui::fonts[3] = LoadFontEx("resources/fonts/Roboto-Regular.ttf", 64, 0, 400);
        SetTextureFilter(Gui::fonts[0].texture, TEXTURE_FILTER_BILINEAR);
        SetTextureFilter(Gui::fonts[1].texture, TEXTURE_FILTER_BILINEAR);
        SetTextureFilter(Gui::fonts[2].texture, TEXTURE_FILTER_BILINEAR);
        SetTextureFilter(Gui::fonts[3].texture, TEXTURE_FILTER_BILINEAR);
    }
}

void Gui::setContext(const std::string &name)
{
    bool contextExists = Gui::contexts.find(name) != Gui::contexts.end();
    if (!contextExists)
    {
        Gui::contexts[name] = Gui::CreateContext();
        Log::msg("Gui", "Created Context: {}", name);
    }

    Clay_SetCurrentContext(Gui::contexts[name]);
    Gui::current_Context = contexts[name];
    Log::msg("Gui", "Set Current Context: {}", name);
}

Clay_Context *Gui::CreateContext()
{
    uint64_t clayRequiredMemory = Clay_MinMemorySize();
    Clay_Arena clayMemory = Clay_CreateArenaWithCapacityAndMemory(clayRequiredMemory, malloc(clayRequiredMemory));

    Clay_Context *new_context = Clay_Initialize(
        clayMemory,
        (Clay_Dimensions){
            .width = (float)GetScreenWidth(),
            .height = (float)GetScreenHeight()},
        (Clay_ErrorHandler)handleErrors);

    Clay_SetMeasureTextFunction(Raylib_MeasureText, Gui::fonts);

    return new_context;
}

void Gui::updateState()
{
    Clay_SetLayoutDimensions((Clay_Dimensions){
        .width = (float)GetScreenWidth(),
        .height = (float)GetScreenHeight()});

    Vector2 mousePosition = GetMousePosition();

    Clay_SetPointerState(
        (Clay_Vector2){mousePosition.x, mousePosition.y},
        IsMouseButtonDown(MOUSE_BUTTON_LEFT));

    Vector2 scrollDelta = GetMouseWheelMoveV();

    Clay_UpdateScrollContainers(
        true,
        (Clay_Vector2){scrollDelta.x, scrollDelta.y},
        GetFrameTime());
}

bool Gui::isInputUpdated()
{
    static Vector2 lastMousePosition = GetMousePosition();
    static Vector2 lastScrollDelta = GetMouseWheelMoveV();

    bool mouseUpdate = !(lastMousePosition == GetMousePosition());
    bool scrollUpdate = !(lastScrollDelta == GetMouseWheelMoveV());
    bool mouseButtonUpdate = (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonPressed(MOUSE_BUTTON_RIGHT));
    bool keyUpdate = (GetKeyPressed() != 0);
    bool windowUpdate = IsWindowResized();

    lastMousePosition = GetMousePosition();
    lastScrollDelta = GetMouseWheelMoveV();

    return mouseUpdate || scrollUpdate || mouseButtonUpdate || keyUpdate || windowUpdate;
}

void Gui::clearInput()
{
    while (GetKeyPressed() != 0)
    {
    }
}

bool Gui::componentClicked(const std::string &id, int button)
{
    bool hover = Clay_PointerOver(CLAY_SID(Gui::ClayString(id)));
    bool clicked = IsMouseButtonPressed(button);

    return hover && clicked;
}

bool Gui::componentClicked(const Clay_ElementId &id, int button)
{
    bool hover = Clay_PointerOver(id);
    bool clicked = IsMouseButtonPressed(button);

    return hover && clicked;
}

void Gui::BeginLayout()
{
    Gui::resetStringArenaIndex();
    Clay_BeginLayout();
}

Clay_RenderCommandArray Gui::EndLayout()
{
    return Clay_EndLayout();
}

void Gui::draw()
{
    Clay_Raylib_Render(Gui::current_Commands, Gui::fonts);
};

void Gui::updateRenderCommands(const Clay_RenderCommandArray &commands)
{
    Gui::current_Commands = commands;
}

void Gui::cleanup()
{
    Clay_Raylib_Close();
}

const char *Gui::insertStringIntoArena(const std::string &str)
{
    size_t strSize = str.size();
    if (nextStringArenaIndex + strSize + 1 > sizeof(stringArena))
    {
        Log::msg("Gui", "The string: {} caused an overflow in the string arena.", str);
        throw std::overflow_error("StringArena: Not enough space to insert the string.");
    }

    char *startPtr = &stringArena[nextStringArenaIndex];

    for (size_t i = 0; i < strSize; i++)
    {
        stringArena[nextStringArenaIndex++] = str[i];
    }
    stringArena[nextStringArenaIndex++] = ' ';

    Profiler::Stamp("Gui-StringArena", std::nullopt, float(nextStringArenaIndex) / sizeof(stringArena));

    return startPtr;
}

Clay_String Gui::ClayString(const std::string &text)
{
    int32_t length = (int32_t)text.size();
    const char *textchars = insertStringIntoArena(text);
    Clay_String cs = {.length = length, .chars = textchars};
    return cs;
}