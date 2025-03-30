#pragma once

#include "clay.h"

namespace Style
{
    namespace Text
    {
        struct Font
        {
            uint16_t id;
            uint16_t size;
        };

        extern const Font SMALL;
        extern const Font MEDIUM;
        extern const Font LARGE;
        extern const Font XLARGE;

        extern Clay_TextElementConfig infoText;
        extern Clay_TextElementConfig buttonText;
        extern Clay_TextElementConfig titleText;
    }

    namespace Dark
    {
        extern const Clay_Color gray_0;
        extern const Clay_Color gray_1;
        extern const Clay_Color gray_2;
        extern const Clay_Color gray_3;
        extern const Clay_Color gray_4;
        extern const Clay_Color gray_5;
        extern const Clay_Color gray_6;

        extern const Clay_Color none;
    };
}