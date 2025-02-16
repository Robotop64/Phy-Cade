#pragma once
#include <functional>

struct Window
{
    using Context = std::function<void()>;
    static Context current;

    static void create();
    static void destroy();

    static void updateContext();
    static void queueContext(Context context);
};