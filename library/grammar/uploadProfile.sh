#!/bin/sh

rm -f a3profile.txt.xhtml

mm2xhtml a3profile.txt || exit 1

scp a3profile.txt.xhtml ksaito0717@shell.sourceforge.jp:/home/groups/a/ac/acerola3d/htdocs/docs/format/profile/index.html || exit 1

rm a3profile.txt.xhtml

echo DONE!!!!!!

