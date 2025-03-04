#include "config.hpp"
#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"

int main(void)
{
    // init config
    Config::instance();

    Window::create();

    Window::queueContext(Scene::MainMenu);
    Window::updateContext();

    Window::destroy();

    return 0;
}