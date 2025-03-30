#pragma once
#include <functional>

namespace Window
{
    using Context = std::function<void()>;
    static Context current = nullptr;

    void create();
    void destroy();

    void updateContext();
    void queueContext(Context context);
};