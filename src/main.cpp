#include "config.hpp"
#include "window.hpp"
#include "scene.hpp"
#include "gui.hpp"

int main(void)
{
    // load config
    Config::instance();

    // create window
    Window::create();

    Window::queueContext(Scene::MainMenu);
    Window::updateContext();

    Window::destroy();

    return 0;
}