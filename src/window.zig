const std = @import("std");
const vec = @import("std").meta;

const rl = @import("raylib");

pub const Dimension = struct {
    width: u16 = undefined,
    height: u16 = undefined,

    pub fn update(self: *Dimension) void {
        self.width = @intCast(rl.getScreenWidth());
        self.height = @intCast(rl.getScreenHeight());
    }
};
pub var dimension: Dimension = .{};

pub const Transform = struct {
    /// Returns a Vector
    /// Reduces the parent's size by the buffer size
    pub fn absBuffer(parent: *const @Vector(4, u16), buffers: *const @Vector(4, u16)) @Vector(4, u16) {
        const px = parent[0];
        const py = parent[1];
        const pw = parent[2];
        const ph = parent[3];

        const bLeft = buffers[0];
        const bRight = buffers[1];
        const bTop = buffers[2];
        const bBottom = buffers[3];

        const xShift: u16 = px + bLeft;
        const yShift: u16 = py + bTop;
        const rWidth: u16 = pw - bLeft - bRight;
        const rHeight: u16 = ph - bTop - bBottom;
        return .{ xShift, yShift, rWidth, rHeight };
    }

    /// Returns a Vector
    /// Reduces the parent's size by the buffer size, relative to the parent's size
    /// The buffer is a percentage of the parent's size
    pub fn relBuffer(parent: *const @Vector(4, u16), buffers: *const @Vector(4, f32)) @Vector(4, u16) {
        const px = parent[0];
        const py = parent[1];
        const pw = parent[2];
        const ph = parent[3];

        const bLeft = buffers[0];
        const bRight = buffers[1];
        const bTop = buffers[2];
        const bBottom = buffers[3];

        const xShift: u16 = px + @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(pw)) * bLeft)));
        const yShift: u16 = py + @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(ph)) * bTop)));
        const rWidth: u16 = pw - @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(pw)) * bLeft))) - @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(pw)) * bRight)));
        const rHeight: u16 = ph - @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(ph)) * bTop))) - @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(ph)) * bBottom)));
        return .{ xShift, yShift, rWidth, rHeight };
    }
};
