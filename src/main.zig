const rl = @import("raylib");

const window = @import("window.zig");
const menu = @import("Menus/menu.zig");

pub fn main() anyerror!void {
    window.dimension = .{
        .width = 800,
        .height = 450,
    };

    rl.initWindow(window.dimension.width, window.dimension.height, "raylib-zig [core] example - basic window");
    rl.setWindowState(rl.ConfigFlags{ .window_resizable = true });
    defer rl.closeWindow();

    menu.selectMenu(menu.menus.Main);
}
