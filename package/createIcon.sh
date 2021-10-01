# https://stackoverflow.com/questions/12306223/how-to-manually-create-icns-files-using-iconutil
mkdir Samurai.iconset
sips -z 16 16     samurai-mac.png --out Samurai.iconset/icon_16x16.png
sips -z 32 32     samurai-mac.png --out Samurai.iconset/icon_16x16@2x.png
sips -z 32 32     samurai-mac.png --out Samurai.iconset/icon_32x32.png
sips -z 64 64     samurai-mac.png --out Samurai.iconset/icon_32x32@2x.png
sips -z 128 128   samurai-mac.png --out Samurai.iconset/icon_128x128.png
sips -z 256 256   samurai-mac.png --out Samurai.iconset/icon_128x128@2x.png
sips -z 256 256   samurai-mac.png --out Samurai.iconset/icon_256x256.png
sips -z 512 512   samurai-mac.png --out Samurai.iconset/icon_256x256@2x.png
sips -z 512 512   samurai-mac.png --out Samurai.iconset/icon_512x512.png
cp samurai-mac.png Samurai.iconset/icon_512x512@2x.png
iconutil -c icns Samurai.iconset
rm -R Samurai.iconset
