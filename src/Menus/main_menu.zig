const std = @import("std");
const rl = @import("raylib");
const win = @import("../window.zig");

const Layout = struct {
    title: @Vector(4, u16) = undefined,
    play: @Vector(4, u16) = undefined,
    ranking: @Vector(4, u16) = undefined,
    options: @Vector(4, u16) = undefined,
    credits: @Vector(4, u16) = undefined,
    updates: @Vector(4, u16) = undefined,
    quit: @Vector(4, u16) = undefined,
};
var layout: Layout = .{};

pub fn init() void {
    rl.setTargetFPS(60);

    calcLayout();

    while (!rl.windowShouldClose()) {
        if (rl.isWindowResized() and !rl.isWindowFullscreen()) {
            win.dimension.update();
            calcLayout();
        }

        rl.beginDrawing();
        defer rl.endDrawing();

        rl.clearBackground(rl.Color.white);

        draw();
    }
}

fn draw() void {
    rl.drawRectangle(layout.title[0], layout.title[1], layout.title[2], layout.title[3], rl.Color.gray);
}

fn calcLayout() void {
    const window: @Vector(4, u16) = .{ 0, 0, win.dimension.width, win.dimension.height };
    layout.title = win.Transform.relBuffer(&window, &.{ 0.25, 0.25, 0.1, 0.80 });
}
