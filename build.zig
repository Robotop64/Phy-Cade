const std = @import("std");

pub fn build(b: *std.Build) void {
    const target = b.standardTargetOptions(.{});
    const optimize = b.standardOptimizeOption(.{});

    const exe = b.addExecutable(.{
        .name = "Phy-Cade",
        .root_source_file = b.path("src/main.zig"),
        .target = target,
        .optimize = optimize,
    });

    // Raylib ---------------------------------------------------------------
    const raylib = b.dependency("raylib-zig", .{
        .target = target,
        .optimize = optimize,
    });
    exe.linkLibrary(raylib.artifact("raylib"));
    exe.root_module.addImport("raylib", raylib.module("raylib"));
    exe.root_module.addImport("raygui", raylib.module("raygui"));
    //-----------------------------------------------------------------------
    // SQlite3
    const sqlite = b.dependency("sqlite", .{
        .target = target,
        .optimize = optimize,
    });
    exe.linkLibrary(sqlite.artifact("sqlite"));
    exe.root_module.addImport("sqlite", sqlite.module("sqlite"));
    //-----------------------------------------------------------------------

    b.installArtifact(exe);

    // Run after build
    const run_cmd = b.addRunArtifact(exe);

    run_cmd.step.dependOn(b.getInstallStep());

    if (b.args) |args| {
        run_cmd.addArgs(args);
    }

    const run_step = b.step("run", "Run the app");
    run_step.dependOn(&run_cmd.step);
}
