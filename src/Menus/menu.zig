const std = @import("std");
const main_menu = @import("main_menu.zig");

pub const menus = enum { Main, Game, Ranking, Settings, Credits, Updates, Quit };

pub fn selectMenu(m: menus) void {
    switch (m) {
        .Main => {
            std.debug.print("Entering Main Menu\n", .{});
            main_menu.init();
        },
        .Game => {},
        .Ranking => {},
        .Settings => {},
        .Credits => {},
        .Updates => {},
        .Quit => {
            std.debug.print("Exiting Game\n", .{});
        },
    }
}
