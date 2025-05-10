#include "config.hpp"
#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"
#include "profiler.hpp"

int main(void)
{
    Profiler::Setup();
    Profiler::Stamp("Start");

    Config::instance();

    Window::create();

    Window::queueContext(Scene::MainMenu);
    Window::updateContext();

    Gui::cleanup();

    Window::destroy();

    return 0;
}