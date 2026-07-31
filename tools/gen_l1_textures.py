"""Generate Phase L1 placeholder textures from nearest assets / procedural PNGs."""
from PIL import Image, ImageDraw
import os
import shutil

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TEX = os.path.join(ROOT, "src/main/resources/assets/arcana/textures")
BLOCK = os.path.join(TEX, "block")
ITEM = os.path.join(TEX, "item")
GUI = os.path.join(TEX, "gui")


def tint_copy(src, dst, rgb_mul, add=(0, 0, 0)):
    im = Image.open(src).convert("RGBA")
    px = im.load()
    w, h = im.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            nr = max(0, min(255, int(r * rgb_mul[0]) + add[0]))
            ng = max(0, min(255, int(g * rgb_mul[1]) + add[1]))
            nb = max(0, min(255, int(b * rgb_mul[2]) + add[2]))
            px[x, y] = (nr, ng, nb, a)
    im.save(dst)
    print("wrote", dst)


def make_cube(path, base_rgb, accent_rgb=None, pattern="grid"):
    im = Image.new("RGBA", (16, 16), (*base_rgb, 255))
    d = ImageDraw.Draw(im)
    if accent_rgb is None:
        accent_rgb = tuple(max(0, c - 30) for c in base_rgb)
    d.rectangle([0, 0, 15, 15], outline=accent_rgb)
    if pattern == "grid":
        for i in range(0, 16, 4):
            d.line([(i, 0), (i, 15)], fill=accent_rgb)
            d.line([(0, i), (15, i)], fill=accent_rgb)
    elif pattern == "ring":
        d.ellipse([3, 3, 12, 12], outline=accent_rgb)
        d.ellipse([6, 6, 9, 9], fill=accent_rgb)
    elif pattern == "parchment":
        for y in range(2, 14, 3):
            d.line([(2, y), (13, y)], fill=accent_rgb)
        d.rectangle([1, 1, 14, 14], outline=(80, 60, 40))
    elif pattern == "cross":
        d.line([(2, 8), (13, 8)], fill=accent_rgb)
        d.line([(8, 2), (8, 13)], fill=accent_rgb)
        d.rectangle([5, 5, 10, 10], outline=accent_rgb)
    elif pattern == "dots":
        for y in range(2, 15, 4):
            for x in range(2, 15, 4):
                d.point((x, y), fill=accent_rgb)
                d.point((x + 1, y), fill=accent_rgb)
    im.save(path)
    print("wrote", path)


def make_item(path, bg, mark):
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.rectangle([2, 1, 13, 14], fill=bg, outline=tuple(max(0, c - 40) for c in bg[:3]))
    d.line([(4, 4), (11, 4)], fill=mark)
    d.line([(4, 7), (11, 7)], fill=mark)
    d.line([(4, 10), (9, 10)], fill=mark)
    d.rectangle([5, 11, 10, 12], fill=mark)
    im.save(path)
    print("wrote", path)


def main():
    os.makedirs(BLOCK, exist_ok=True)
    os.makedirs(ITEM, exist_ok=True)
    os.makedirs(GUI, exist_ok=True)

    src_gui = os.path.join(GUI, "arcane_workbench.png")
    dst_gui = os.path.join(GUI, "research_table.png")
    shutil.copy2(src_gui, dst_gui)
    tint_copy(dst_gui, dst_gui, (1.05, 0.95, 0.75), add=(12, 8, 0))

    make_cube(os.path.join(BLOCK, "research_table.png"), (180, 150, 100), (90, 70, 40), "parchment")
    make_cube(os.path.join(BLOCK, "research_table_side.png"), (140, 110, 70), (70, 50, 30), "grid")
    make_cube(os.path.join(BLOCK, "infusion_matrix.png"), (120, 70, 150), (200, 140, 220), "ring")
    make_cube(os.path.join(BLOCK, "pedestal.png"), (110, 110, 120), (70, 70, 80), "cross")
    make_cube(os.path.join(BLOCK, "focal_manipulator.png"), (90, 70, 50), (200, 170, 80), "dots")

    tube = os.path.join(BLOCK, "essentia_tube.png")
    if os.path.exists(tube):
        tint_copy(tube, os.path.join(BLOCK, "essentia_filter_tube.png"), (0.7, 1.05, 1.15), add=(0, 10, 20))
    else:
        make_cube(os.path.join(BLOCK, "essentia_filter_tube.png"), (90, 140, 160), (40, 90, 110), "cross")

    make_item(os.path.join(ITEM, "crimson_rite.png"), (160, 40, 50), (220, 180, 100))

    focus3 = os.path.join(ITEM, "focus_3.png")
    if not os.path.exists(focus3):
        focus2 = os.path.join(ITEM, "focus_2.png")
        if os.path.exists(focus2):
            tint_copy(focus2, focus3, (1.1, 0.85, 1.2), add=(20, 0, 30))
        else:
            make_item(focus3, (80, 40, 120), (200, 160, 255))

    print("done textures")


if __name__ == "__main__":
    main()
