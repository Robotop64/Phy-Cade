#include "config.hpp"
#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"

int main(void)
{
    Config::instance();

    Window::create();

    Window::queueContext(Scene::MainMenu);
    Window::updateContext();

    Gui::cleanup();

    Window::destroy();

    return 0;
}