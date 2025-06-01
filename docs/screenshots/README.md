## Screenshots

These screenshots are taken from a vanilla installation of Fedora.

To create GIFs, use the following command:

```bash
ffmpeg -i input.mp4 -vf "fps=10,palettegen" palette.png
ffmpeg -i input.mp4 -i palette.png -filter_complex "[0:v][1:v]paletteuse" output.gif
```