#include "toml.hpp"
#include <iostream>

#define Config toml::parse_result

void saveConfig(Config config);

Config getConfig();