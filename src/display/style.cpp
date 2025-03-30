#include "style.hpp"

namespace Style
{
    namespace Text
    {
        const Font SMALL = {.id = 0, .size = 16};
        const Font MEDIUM = {.id = 1, .size = 32};
        const Font LARGE = {.id = 2, .size = 48};
        const Font XLARGE = {.id = 3, .size = 64};

        Clay_TextElementConfig infoText = {
            .textColor = {255, 255, 255, 255},
            .fontId = SMALL.id,
            .fontSize = SMALL.size,
            .hashStringContents = true,
        };
        Clay_TextElementConfig buttonText = {
            .textColor = {255, 255, 255, 255},
            .fontId = MEDIUM.id,
            .fontSize = MEDIUM.size,
            .hashStringContents = true,
        };
        Clay_TextElementConfig titleText = {
            .textColor = {255, 255, 255, 255},
            .fontId = LARGE.id,
            .fontSize = LARGE.size,
            .hashStringContents = true,
        };
    }

    namespace Dark
    {
        const Clay_Color gray_0 = {35, 35, 35, 255};
        const Clay_Color gray_1 = {70, 70, 70, 255};
        const Clay_Color gray_2 = {105, 105, 105, 255};
        const Clay_Color gray_3 = {140, 140, 140, 255};
        const Clay_Color gray_4 = {175, 175, 175, 255};
        const Clay_Color gray_5 = {210, 210, 210, 255};
        const Clay_Color gray_6 = {245, 245, 245, 255};

        const Clay_Color none = {0, 0, 0, 0};
    };
}